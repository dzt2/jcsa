package com.jcsa.jcparse.lang.irlang.stmt;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;

/**
 * if expression then label else label
 * @author yukimula
 *
 */
public interface CirIfStatement extends CirStatement {
	public CirExpression get_condition();
	public CirLabel get_true_label();
	public CirLabel get_false_label();
	public void set_condition(CirExpression condition) throws IllegalArgumentException;
	public void set_true_branch(CirLabel true_label) throws IllegalArgumentException;
	public void set_false_branch(CirLabel false_label) throws IllegalArgumentException;
}
