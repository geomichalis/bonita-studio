/**
 * Copyright (C) 2009-2013 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.studio.application;

import org.bonitasoft.studio.common.perspectives.AutomaticSwitchPerspectivePartListener;
import org.bonitasoft.studio.common.perspectives.BonitaPerspectivesUtils;
import org.bonitasoft.studio.common.perspectives.PerspectiveIDRegistry;
import org.bonitasoft.studio.common.platform.tools.PlatformUtil;
import org.bonitasoft.studio.profiles.manager.BonitaProfilesManager;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.WorkbenchPage;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.internal.ide.IDEInternalPreferences;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.util.PrefUtil;

public class BonitaStudioWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	private IWorkbenchWindow window;

	public BonitaStudioWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
		configurer.setShowProgressIndicator(true);
		window = configurer.getWindow();
	}

	@Override
	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
		return new ActionBarAdvisor(configurer){
			@Override
			protected void makeActions(IWorkbenchWindow window) {
				super.makeActions(window);
				register(ActionFactory.UNDO.create(window));
				register(ActionFactory.REDO.create(window));
				register(ActionFactory.PREFERENCES.create(window));
				register(ActionFactory.ABOUT.create(window));
			}
		};
	}


	@Override
	public void preWindowOpen() {
		BonitaProfilesManager.getInstance().setActiveProfile(BonitaProfilesManager.getInstance().getActiveProfile(),false) ;
	}

	@Override
	public void postWindowCreate() {
		final MWindow model = ((WorkbenchWindow)window).getModel();
		EModelService modelService =  model.getContext().get(EModelService.class);
		MUIElement statusBar = modelService.find("org.eclipse.ui.trim.status", model);
		if(statusBar != null){
			statusBar.setVisible(false);
		}
		MUIElement leftTrimbar = modelService.find("org.eclipse.ui.trim.vertical1", model);
		if(leftTrimbar != null){
			leftTrimbar.setVisible(false);
		}
		MUIElement rightTrimbar = modelService.find("org.eclipse.ui.trim.vertical2", model);
		if(rightTrimbar != null){
			rightTrimbar.setVisible(false);
		}
	}

	@SuppressWarnings("restriction")
	@Override
	public void openIntro() {
		PlatformUtil.closeIntro();
		PrefUtil.getAPIPreferenceStore().setValue(IWorkbenchPreferenceConstants.SHOW_INTRO, true);
		PrefUtil.saveAPIPrefs();
		BonitaPerspectivesUtils.switchToPerspective(PerspectiveIDRegistry.PROCESS_PERSPECTIVE_ID);
		if(window.getActivePage().getPerspective() != null) {
			super.openIntro();
			PlatformUtil.openIntro();
		}
	}


	/**
	 * Register to selection service to update button enablement
	 * Register the Automatic Perspective switch part listener
	 */
	@Override
	public void postWindowOpen() {
		if(window.getShell().getShells().length > 0){
			Shell shell = window.getShell().getShells()[0];
			shell.removeListener(SWT.Activate, shell.getListeners(SWT.Activate)[0]);
			shell.removeListener(SWT.Close,shell.getListeners(SWT.Close)[0]);
			shell.removeListener(SWT.Deactivate,shell.getListeners(SWT.Deactivate)[0]);
			shell.removeListener(SWT.Iconify,shell.getListeners(SWT.Iconify)[0]);
			shell.removeListener(SWT.Deiconify,shell.getListeners(SWT.Deiconify)[0]);
		}
		final MWindow model = ((WorkbenchPage)window.getActivePage()).getWindowModel();
		model.getContext().get(EPartService.class).addPartListener(new AutomaticSwitchPerspectivePartListener());
	}


	@Override
	public boolean preWindowShellClose() {
		if (PlatformUI.getWorkbench().getWorkbenchWindowCount() > 1) {
			return true;
		}
		// the user has asked to close the last window, while will cause the
		// workbench to close in due course - prompt the user for confirmation
		if( promptOnExit(getWindowConfigurer().getWindow().getShell())){
			if(PlatformUI.isWorkbenchRunning() && window != null && window.getActivePage() != null){
				window.getActivePage().closeAllEditors(true) ;
			}
			return true;

		}
		return false;
	}

	private boolean promptOnExit(Shell parentShell) {
		IPreferenceStore store = IDEWorkbenchPlugin.getDefault()
				.getPreferenceStore();
		boolean promptOnExit = store
				.getBoolean(IDEInternalPreferences.EXIT_PROMPT_ON_CLOSE_LAST_WINDOW);

		if (promptOnExit) {
			if (parentShell == null) {
				IWorkbenchWindow workbenchWindow = window;
				if (workbenchWindow != null) {
					parentShell = workbenchWindow.getShell();
				}
			}
			if (parentShell != null) {
				parentShell.setMinimized(false);
				parentShell.forceActive();
			}

			String message;

			String productName = null;
			IProduct product = Platform.getProduct();
			if (product != null) {
				productName = product.getName();
			}
			if (productName == null) {
				message = IDEWorkbenchMessages.PromptOnExitDialog_message0;
			} else {
				message = NLS.bind(
						IDEWorkbenchMessages.PromptOnExitDialog_message1,
						productName);
			}

			MessageDialogWithToggle dlg = MessageDialogWithToggle
					.openOkCancelConfirm(parentShell,
							IDEWorkbenchMessages.PromptOnExitDialog_shellTitle,
							message,
							IDEWorkbenchMessages.PromptOnExitDialog_choice,
							false, null, null);
			if (dlg.getReturnCode() != IDialogConstants.OK_ID) {
				return false;
			}
			if (dlg.getToggleState()) {
				store
				.setValue(
						IDEInternalPreferences.EXIT_PROMPT_ON_CLOSE_LAST_WINDOW,
						false);
				IDEWorkbenchPlugin.getDefault().savePluginPreferences();
			}
		}

		return true;
	}
}