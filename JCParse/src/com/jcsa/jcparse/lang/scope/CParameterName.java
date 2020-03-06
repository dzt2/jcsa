package com.jcsa.jcparse.lang.scope;

import com.jcsa.jcparse.lang.centity.CInstance;

/**
 * name to represent parameter in function
 * 
 * @author yukimula
 */
public interface CParameterName extends CName {
	/**
	 * get the parameter of this name
	 * 
	 * @return
	 */
	public CInstance get_parameter();

	/**
	 * set the parameter for this name
	 * 
	 * @param param
	 */
	public void set_parameter(CInstance param);
}
