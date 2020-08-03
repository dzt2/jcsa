package com.jcsa.jcparse.lang.ir.unit;

import com.jcsa.jcparse.lang.ir.CirNode;

/**
 * field |-- name
 * @author yukimula
 *
 */
public interface CirField extends CirNode {
	
	/**
	 * @return the field name
	 */
	public String get_name();
	
}
