package com.jcsa.jcparse.lang.ctoken;

/**
 * token for header following #include
 * 
 * @author yukimula
 */
public interface CHeaderToken extends CToken {
	/**
	 * is it pointing to a system header
	 * 
	 * @return
	 */
	public boolean is_system_header();

	/**
	 * is it pointing to a user-defined header
	 * 
	 * @return
	 */
	public boolean is_user_header();

	/**
	 * get the path of the header
	 * 
	 * @return
	 */
	public String get_path();
}
