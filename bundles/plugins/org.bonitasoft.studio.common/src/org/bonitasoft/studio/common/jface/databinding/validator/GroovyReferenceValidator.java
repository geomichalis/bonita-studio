/**
 * Copyright (C) 2012 BonitaSoft S.A.
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
package org.bonitasoft.studio.common.jface.databinding.validator;

import java.util.Arrays;

import org.bonitasoft.studio.common.BonitaConstants;
import org.bonitasoft.studio.common.Messages;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;

/**
 * @author Romain Bioteau
 *
 */
public class GroovyReferenceValidator implements IValidator {

	public static final String[] KEYWORDS = new String[] { BonitaConstants.API_ACCESSOR, BonitaConstants.ENGINE_EXECUTION_CONTEXT, BonitaConstants.ACTIVITY_INSTANCE_ID,
		BonitaConstants.PROCESS_DEFINITION_ID, BonitaConstants.ROOT_PROCESS_INSTANCE_ID, BonitaConstants.PARENT_PROCESS_INSTANCE_ID };
	private String fieldName;
	private boolean checkEmptyField;

	public GroovyReferenceValidator(String fieldName,boolean checkEmptyField){
		this.fieldName = fieldName;
		this.checkEmptyField = checkEmptyField;
	}
	
	public GroovyReferenceValidator(String fieldName){
		this(fieldName,true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.databinding.validation.IValidator#validate(java.lang.Object)
	 */
	@Override
	public IStatus validate(Object value) {
		if(checkEmptyField){
			final IStatus s = new EmptyInputValidator(fieldName).validate(value);
			if(!s.isOK()){
				return s;
			}
		}else{
			if(value == null || value.toString().isEmpty()){
				return ValidationStatus.ok();
			}
		}
		if (!value.toString().isEmpty()) {
			if (Character.isUpperCase(value.toString().charAt(0))) {
				return ValidationStatus.error(Messages.nameMustStartWithLowerCase);
			}
		}

		if(value.toString() != null && !value.toString().isEmpty() && Arrays.asList(KEYWORDS).contains(value.toString())){
			return ValidationStatus.error(Messages.reservedKeyword);
		}

		final IStatus javaConventionNameStatus = JavaConventions.validateFieldName(value.toString(), JavaCore.VERSION_1_6, JavaCore.VERSION_1_6);
		if(!javaConventionNameStatus.isOK()){
			return ValidationStatus.error(javaConventionNameStatus.getMessage());
		}
		if(!javaConventionNameStatus.isOK()){
			return javaConventionNameStatus;
		}
		return ValidationStatus.ok();
	}

}
