package com.jcsa.jcparse.lang.ir.unit;

import com.jcsa.jcparse.lang.ir.CirNode;

/**
 * statement_label is the integer index of the statement in the list.
 * <br>
 * 	{statement_index}
 * @author yukimula
 *
 */
public interface CirLabel extends CirNode {
	
	/**
	 * @return the index of the statement in the function body.
	 */
	public int get_statement_index();
	
}
