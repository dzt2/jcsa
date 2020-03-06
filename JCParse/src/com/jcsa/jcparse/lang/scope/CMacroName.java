package com.jcsa.jcparse.lang.scope;

import com.jcsa.jcparse.lang.centity.CMacro;

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
