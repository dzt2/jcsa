package com.jcsa.jcparse.lang.ptoken;

/**
 * token as header in preprocessing (followng #include)
 * 
 * @author yukimula
 *
 */
public interface PHeaderToken extends PToken {
	/**
	 * is this a system header: <header>
	 * 
	 * @return
	 */
	public boolean is_system();

	/**
	 * is this a user-define header: "path"
	 * 
	 * @return
	 */
	public boolean is_usedef();

	/**
	 * get the path of this header
	 * 
	 * @return
	 */
	public String get_path();
}
