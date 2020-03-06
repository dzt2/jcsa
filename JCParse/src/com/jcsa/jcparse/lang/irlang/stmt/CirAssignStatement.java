package com.jcsa.jcparse.lang.irlang.stmt;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;

/**
 * CirAssignStatement	|-- CirBinAssignStatement 
 * 						|-- CirIncreAssignStatement
 * 						|-- CirSaveAssignStatement
 * 						|-- CirInitAssignStatement
 * 						|-- CirReturnAssignStatement 
 * 						|-- CirWaitAssignStatement
 * @author yukimula
 *
 */
public interface CirAssignStatement extends CirStatement {
	public CirReferExpression get_lvalue();
	public CirExpression get_rvalue();
	public void set_lvalue(CirReferExpression lvalue) throws IllegalArgumentException;
	public void set_rvalue(CirExpression rvalue) throws IllegalArgumentException;
}
