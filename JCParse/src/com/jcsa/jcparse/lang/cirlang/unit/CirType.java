package com.jcsa.jcparse.lang.cirlang.unit;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * type |-- {data_type: CType}
 * @author yukimula
 *
 */
public interface CirType extends CirUnit {
	
	/**
	 * @return the data type that the node describes
	 */
	public CType get_data_type();
	
}
