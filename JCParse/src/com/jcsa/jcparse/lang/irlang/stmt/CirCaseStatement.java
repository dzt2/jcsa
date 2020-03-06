package com.jcsa.jcparse.lang.irlang.stmt;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;

/**
 * CirCaseStatement	|-- case condition then next else L
 * @author yukimula
 *
 */
public interface CirCaseStatement extends CirStatement {
	public CirExpression get_condition();
	public CirLabel get_false_label();
	public void set_condition(CirExpression condition) throws IllegalArgumentException;
	public void set_false_branch(CirLabel false_label) throws IllegalArgumentException;
}
