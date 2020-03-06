package com.jcsa.jcparse.lang.irlang.expr;

import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * field --> identifier
 * @author yukimula
 *
 */
public interface CirField extends CirNode {
	public String get_name();
	public void set_name(String name) throws IllegalArgumentException;
}
