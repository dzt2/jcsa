package com.jcsa.jcparse.lang.irlang.expr;

import com.jcsa.jcparse.lang.scope.CName;

/**
 * as identifier in declarator of initialization
 *
 * @author yukimula
 *
 */
public interface CirDeclarator extends CirNameExpression {
	public CName get_cname();
	public void set_cname(CName cname) throws IllegalArgumentException;
}
