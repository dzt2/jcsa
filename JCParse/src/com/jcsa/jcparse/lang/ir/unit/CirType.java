package com.jcsa.jcparse.lang.ir.unit;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * type |-- {data_type: CType}
 * @author yukimula
 *
 */
public interface CirType extends CirUnit {
	
	/**
	 * @return the data type of the node
	 */
	public CType get_data_type();
	
}
