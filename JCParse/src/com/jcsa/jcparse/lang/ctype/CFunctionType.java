package com.jcsa.jcparse.lang.ctype;

/**
 * <code>(func, type, (type,type,...))</code>
 * 
 * @author yukimula
 */
public interface CFunctionType extends CType {
	/**
	 * get the type of return value from function of this type
	 * 
	 * @return
	 */
	public CType get_return_type();

	/**
	 * get the parameter type list defined in function of this type
	 * 
	 * @return
	 */
	public CParameterTypeList get_parameter_types();
}
