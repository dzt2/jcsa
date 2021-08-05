package com.jcsa.jcparse.lang.scope;

/**
 * name to represent macro in program
 *
 * @author yukimula
 */
public interface CMacroName extends CName {
	/**
	 * get the macro this name refers to
	 *
	 * @return
	 */
	public CMacro get_macro();

	/**
	 * set the macro for this name
	 *
	 * @param macro
	 */
	public void set_macro(CMacro macro);
}
