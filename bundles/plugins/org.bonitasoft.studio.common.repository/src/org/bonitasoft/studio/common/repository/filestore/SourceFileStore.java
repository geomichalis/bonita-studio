/**
 * Copyright (C) 2012-2014 Bonitasoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
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
package org.bonitasoft.studio.common.repository.filestore;

import java.io.InputStream;
import java.util.Collections;

import org.bonitasoft.studio.common.editor.BonitaJavaEditor;
import org.bonitasoft.studio.common.log.BonitaStudioLog;
import org.bonitasoft.studio.common.repository.CommonRepositoryPlugin;
import org.bonitasoft.studio.common.repository.Repository;
import org.bonitasoft.studio.common.repository.RepositoryManager;
import org.bonitasoft.studio.common.repository.model.IRepositoryStore;
import org.bonitasoft.studio.common.repository.store.SourceRepositoryStore;
import org.bonitasoft.studio.pics.Pics;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.jarpackager.IJarExportRunnable;
import org.eclipse.jdt.ui.jarpackager.JarPackageData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;


/**
 * @author Romain Bioteau
 *
 */
public class SourceFileStore extends AbstractFileStore {

    private String qualifiedClassName;
    private IEditorPart editorPart;

    public SourceFileStore(String qualifiedClassName, IRepositoryStore<?> parentStore) {
        super("", parentStore);
        this.qualifiedClassName = qualifiedClassName ;
    }

    @Override
    public String getName() {
        if(getResource() != null){
            return getResource().getName();
        }else{
            return qualifiedClassName;
        }

    }

    @Override
    public String getDisplayName() {
        return getName() ;
    }

    /* (non-Javadoc)
     * @see org.bonitasoft.studio.common.repository.model.IRepositoryFileStore#getIcon()
     */
    @Override
    public Image getIcon() {
        return Pics.getImage("java.gif", CommonRepositoryPlugin.getDefault());
    }

    /* (non-Javadoc)
     * @see org.bonitasoft.studio.common.repository.model.IRepositoryFileStore#getContent()
     */
    @Override
    public InputStream getContent() {
        try {
            return getResource().getContents() ;
        } catch (CoreException e) {
            BonitaStudioLog.error(e);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.bonitasoft.studio.common.repository.model.IRepositoryFileStore#getResource()
     */
    @Override
    public IFile getResource() {
        IJavaProject project = RepositoryManager.getInstance().getCurrentRepository().getJavaProject() ;
        IType type;
        try {
            type = project.findType(qualifiedClassName);
            if(type != null && type.getCompilationUnit() != null){
                return (IFile) type.getCompilationUnit().getResource() ;
            }
        } catch (JavaModelException e) {
            BonitaStudioLog.error(e) ;
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.bonitasoft.studio.common.repository.filestore.AbstractFileStore#doSave(java.lang.Object)
     */
    @Override
    protected void doSave(Object content) {


    }

    /* (non-Javadoc)
     * @see org.bonitasoft.studio.common.repository.filestore.AbstractFileStore#doOpen()
     */
    @Override
    protected IWorkbenchPart doOpen() {

        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                try {
                    editorPart = IDE.openEditor(page, new FileEditorInput(getResource()), BonitaJavaEditor.ID);
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().saveEditor(editorPart, false);
                } catch (PartInitException e) {
                }

            }
        }) ;
        return editorPart;
    }

    /* (non-Javadoc)
     * @see org.bonitasoft.studio.common.repository.filestore.AbstractFileStore#doClose()
     */
    @Override
    protected void doClose() {

    }

    public void exportAsJar(String absoluteTargetFilePath, boolean includeSources) {
        final JarPackageData jarPackakeData = createJarPackageData() ;
        IFile[] elements = Collections.singletonList(getResource()).toArray(new IFile[1]) ;
        jarPackakeData.setJarLocation(new Path(absoluteTargetFilePath)) ;
        jarPackakeData.setBuildIfNeeded(true);
        jarPackakeData.setElements(elements) ;
        jarPackakeData.setExportWarnings(true) ;
        jarPackakeData.setComment(SourceRepositoryStore.SIGNATURE_FILE_NAME) ;
        jarPackakeData.setExportClassFiles(true) ;
        jarPackakeData.setExportJavaFiles(includeSources) ;
        jarPackakeData.setGenerateManifest(true) ;
        jarPackakeData.setUsesManifest(true) ;
        jarPackakeData.setOverwrite(true) ;
        jarPackakeData.setUseSourceFolderHierarchy(includeSources) ;
        final IJarExportRunnable runnable = jarPackakeData.createJarExportRunnable(null) ;
        try {
            runnable.run(Repository.NULL_PROGRESS_MONITOR) ;
        } catch (Exception e){
            BonitaStudioLog.error(e) ;
        }
    }

	protected JarPackageData createJarPackageData() {
		return new JarPackageData();
	}

    @Override
    public void rename(String newQualifiedClassName) {
        IJavaProject project = RepositoryManager.getInstance().getCurrentRepository().getJavaProject() ;
        String packageName = "" ;
        String className = newQualifiedClassName ;

        if(newQualifiedClassName.indexOf(".") != -1){
            packageName = newQualifiedClassName.substring(0,newQualifiedClassName.lastIndexOf(".")) ;
            className = newQualifiedClassName.substring(newQualifiedClassName.lastIndexOf(".")+1,newQualifiedClassName.length()) ;
        }

        try {
            final IRepositoryStore<?> store = getParentStore() ;
            IPackageFragmentRoot root = project.findPackageFragmentRoot(store.getResource().getFullPath());
            root.createPackageFragment(packageName, true, Repository.NULL_PROGRESS_MONITOR) ;
            IPackageFragment targetContainer = project.findPackageFragment(store.getResource().getFullPath().append(packageName.replace(".","/"))) ;
            IType type = project.findType(qualifiedClassName) ;
            if(type != null){
                type.getCompilationUnit().move(targetContainer, null, className+".java", true, Repository.NULL_PROGRESS_MONITOR) ;
                qualifiedClassName = newQualifiedClassName ;
            }
        } catch (Exception e) {
            BonitaStudioLog.error(e) ;
        }
    }

    @Override
    protected void doDelete() {
    	IJavaProject project = RepositoryManager.getInstance().getCurrentRepository().getJavaProject() ;
    	try {
    		IType type = project.findType(qualifiedClassName);
    		if(type != null){
    			IPackageFragment packageFragment = type.getPackageFragment();
    			final ICompilationUnit compilationUnit = type.getCompilationUnit();
    			if(compilationUnit != null){
    				closeRelatedEditorIfOpened(compilationUnit);//the editor need to be closed here, otherwise the PackageFragment are not refreshed correctly
    				compilationUnit.delete(true, new NullProgressMonitor());
    				deleteRecursivelyEmptyPackages(project, packageFragment);
    			}
    		} else {
    			super.doDelete();
    		}
    	} catch (JavaModelException e1) {
    		BonitaStudioLog.error(e1);
    		super.doDelete();
    	} catch (CoreException e) {
    		BonitaStudioLog.error(e);
    	}
    }

	private void deleteRecursivelyEmptyPackages(IJavaProject project, IPackageFragment packageFragment) throws JavaModelException {
		if(packageFragment != null){
			while(!packageFragment.hasChildren()){
				//I don't find another way than passing through IResource, directly using IJavaElement seems not possible.
				final IPath pathOfParentPackageFragment = packageFragment.getResource().getParent().getFullPath();
				IPackageFragment parent = project.findPackageFragment(pathOfParentPackageFragment);
				packageFragment.delete(true, new NullProgressMonitor());
				if(parent instanceof IPackageFragment && !parent.isDefaultPackage()){
					packageFragment = (IPackageFragment) parent;
				} else {
					return;
				}
			}
		}
	}

	private void closeRelatedEditorIfOpened(final ICompilationUnit compilationUnit) throws PartInitException {
		final IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if(editorPart != null){
			if(PlatformUI.isWorkbenchRunning()){
				activePage.closeEditor(editorPart, false);
			}
		} else {
			if(PlatformUI.isWorkbenchRunning()){
				for(IEditorReference editorReference : activePage.getEditorReferences()){
					final IEditorInput editorInput = editorReference.getEditorInput();
					if(editorInput instanceof FileEditorInput){
						if(compilationUnit.getResource().equals(((FileEditorInput) editorInput).getFile())){
							activePage.closeEditors(new IEditorReference[]{editorReference}, false);
							break;
						}
					}
				}
			}
		}
	}
}
