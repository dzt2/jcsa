package com.jcsa.jcparse.lang.irlang.expr;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * type |-- {data_type}
 * @author yukimula
 *
 */
public interface CirType extends CirNode {
	public CType get_typename();
	public void set_typename(CType type) throws IllegalArgumentException;
}
