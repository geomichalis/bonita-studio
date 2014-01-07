/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.w3._1999.xhtml.validation;

import java.util.List;

import org.w3._1999.xhtml.DirType;

/**
 * A sample validator interface for {@link org.w3._1999.xhtml.LinkType}.
 * This doesn't really do anything, and it's not a real EMF artifact.
 * It was generated by the org.eclipse.emf.examples.generator.validator plug-in to illustrate how EMF's code generator can be extended.
 * This can be disabled with -vmargs -Dorg.eclipse.emf.examples.generator.validator=false.
 */
public interface LinkTypeValidator {
	boolean validate();

	boolean validateCharset(String value);
	boolean validateClass(List<String> value);
	boolean validateDir(DirType value);
	boolean validateHref(String value);
	boolean validateHreflang(String value);
	boolean validateId(String value);
	boolean validateLang(String value);
	boolean validateLang1(String value);
	boolean validateMedia(String value);
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
	boolean validateRel(String value);

	boolean validateRel(List<String> value);
	boolean validateRev(List<String> value);
	boolean validateStyle(String value);
	boolean validateTitle(String value);
	boolean validateType(String value);
}
