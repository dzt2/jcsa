package com.jcsa.jcparse.lang.cirlang.unit;

/**
 * 	label |-- {label: int}
 * @author yukimula
 *
 */
public interface CirLabel extends CirUnit {
	
	/**
	 * @return the label of the statement that the label defines
	 */
	public int get_label();
	
	/**
	 * @param label set the label of statement that it defines
	 */
	public void set_label(int label);
	
}
