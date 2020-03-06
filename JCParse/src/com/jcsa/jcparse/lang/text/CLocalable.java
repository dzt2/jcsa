package com.jcsa.jcparse.lang.text;

/**
 * Element that can achieve to original source text
 * @author yukimula
 */
public interface CLocalable {
	/**
	 * get the location of this element
	 * @return
	 */
	public CLocation get_location();
	/**
	 * set the location of this element
	 * @param loc
	 * @throws Exception
	 */
	public void set_location(CLocation loc) throws Exception;
}
