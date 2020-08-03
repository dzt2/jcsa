package com.jcsa.jcparse.lang.ir.unit;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ir.CirNode;

public interface CirType extends CirNode {
	
	/**
	 * @return type that the node describes
	 */
	public CType get_type();
	
}
