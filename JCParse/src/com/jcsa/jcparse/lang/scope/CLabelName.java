package com.jcsa.jcparse.lang.scope;

/**
 * name to represent label in function statement
 *
 * @author yukimula
 */
public interface CLabelName extends CName {
	/**
	 * get the label of this name
	 *
	 * @return
	 */
	public CLabel get_label();

	/**
	 * set the label for this name
	 *
	 * @param label
	 */
	public void set_label(CLabel label);
}
