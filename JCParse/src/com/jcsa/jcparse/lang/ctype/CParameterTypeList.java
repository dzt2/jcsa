package com.jcsa.jcparse.lang.ctype;

/**
 * A list of type
 * 
 * @author yukimula
 */
public interface CParameterTypeList {
	/**
	 * get the number of parameters in list
	 * 
	 * @return
	 */
	public int size();

	/**
	 * get the ith parameter's type
	 * 
	 * @param i
	 * @return
	 * @throws Exception
	 */
	public CType get_parameter_type(int i) throws Exception;

	/**
	 * add the ith parameter's type
	 * 
	 * @param type
	 * @throws Exception
	 */
	public void add_parameter_type(CType type) throws Exception;

	/**
	 * whether the list is variable
	 * 
	 * @return
	 */
	public boolean is_ellipsis();
}
