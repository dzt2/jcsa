package com.jcsa.jcparse.lang.irlang.expr;

import com.jcsa.jcparse.lang.scope.CName;

/**
 * explicitly declared expression in assignment
 * 
 * @author yukimula
 *
 */
public interface CirIdentifier extends CirNameExpression {
	public CName get_cname();
	public void set_cname(CName cname) throws IllegalArgumentException;
}
