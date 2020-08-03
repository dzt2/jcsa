package com.jcsa.jcparse.lang.ir.unit;

/**
 * label	|--	{label: int}
 * 
 * @author yukimula
 *
 */
public interface CirLabel extends CirUnit {
	
	/**
	 * @return the integer index of the statement in the statement list
	 * 		   in which the label is defined.
	 */
	public int get_label();
	
}
