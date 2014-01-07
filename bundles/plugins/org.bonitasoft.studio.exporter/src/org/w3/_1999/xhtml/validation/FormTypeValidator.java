/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.w3._1999.xhtml.validation;

import java.util.List;

import org.w3._1999.xhtml.DirType;
import org.w3._1999.xhtml.MethodType;

/**
 * A sample validator interface for {@link org.w3._1999.xhtml.FormType}.
 * This doesn't really do anything, and it's not a real EMF artifact.
 * It was generated by the org.eclipse.emf.examples.generator.validator plug-in to illustrate how EMF's code generator can be extended.
 * This can be disabled with -vmargs -Dorg.eclipse.emf.examples.generator.validator=false.
 */
public interface FormTypeValidator {
	boolean validate();

	boolean validateAccept(String value);
	boolean validateAcceptCharset(String value);
	boolean validateAction(String value);
	boolean validateClass(List<String> value);
	boolean validateDir(DirType value);
	boolean validateEnctype(String value);
	boolean validateId(String value);
	boolean validateLang(String value);
	boolean validateLang1(String value);
	boolean validateMethod(MethodType value);
	boolean validateOnclick(String value);
	boolean validateOndblclick(String value);
	boolean validateOnkeydown(String value);
	boolean validateOnkeypress(String value);
	boolean validateOnkeyup(String value);
	boolean validateOnmousedown(String value);
	boolean validateOnmousemove(String value);
	boolean validateOnmouseout(String value);
	boolean validateOnmouseover(String value);
	boolean validateOnmouseup(String value);
	boolean validateOnreset(String value);
	boolean validateOnsubmit(String value);
	boolean validateStyle(String value);
	boolean validateTitle(String value);
}
