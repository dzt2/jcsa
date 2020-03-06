package com.jcsa.jcparse.lang.scope;

import com.jcsa.jcparse.lang.centity.CInstance;

/**
 * name to represent instance: variable, function. Different from other name,
 * instance name can occurs for more than one times. That is: there might be
 * several declarations and one definition for the same variable or function in
 * one file program.
 * 
 * @author yukimula
 */
public interface CInstanceName extends CName {
	/**
	 * get the instance this name presents
	 * 
	 * @return
	 */
	public CInstance get_instance();

	/**
	 * set the instance this name represents
	 * 
	 * @param instance
	 */
	public void set_instance(CInstance instance);

	/**
	 * get the next name of this instance declaration
	 * 
	 * @return : null when this is the final name in current scope
	 */
	public CInstanceName get_next_name();

	/**
	 * set the next name for this name declaration
	 * 
	 * @param name
	 */
	public void set_next_name(CInstanceName name);
}
