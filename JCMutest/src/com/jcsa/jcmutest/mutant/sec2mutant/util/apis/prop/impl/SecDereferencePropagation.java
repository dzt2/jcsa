package com.jcsa.jcmutest.mutant.sec2mutant.util.apis.prop.impl;

import java.util.Collection;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecStateError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.cons.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecAddExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecInsExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecSetExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecUnyExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectPair;
import com.jcsa.jcmutest.mutant.sec2mutant.util.apis.prop.SecUnaryPropagation;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class SecDereferencePropagation extends SecUnaryPropagation {
	
	@Override
	protected void process_set_expression(CirStatement statement, CirExpression expression, SecSetExpressionError error,
			Collection<SecInfectPair> propagations) throws Exception {
		CType type = expression.get_data_type();
		SymExpression muta_operand = error.get_muta_expression().get_expression();
		SymExpression muta_expression = SymFactory.dereference(type, muta_operand);
		SecConstraint constraint = SecFactory.condition_constraint(statement, Boolean.TRUE, true);
		SecStateError target_error = SecFactory.set_expression(statement, expression, muta_expression);
		propagations.add(new SecInfectPair(constraint, target_error));
	}
	
	@Override
	protected void process_add_expression(CirStatement statement, CirExpression expression, SecAddExpressionError error,
			Collection<SecInfectPair> propagations) throws Exception {
		CType type = expression.get_data_type(); SymExpression muta_expression;
		COperator operator = error.get_operator().get_operator();
		SymExpression loperand = error.get_orig_expression().get_expression();
		SymExpression roperand = error.get_operand().get_expression();
		
		SecConstraint constraint = SecFactory.condition_constraint(statement, Boolean.TRUE, true);
		switch(operator) {
		case arith_add:	muta_expression = SymFactory.arith_add(loperand.get_data_type(), loperand, roperand); break;	
		case arith_sub:	muta_expression = SymFactory.arith_sub(loperand.get_data_type(), loperand, roperand); break;
		default: 		muta_expression = null; break;
		}
		
		if(muta_expression != null) {
			muta_expression = SymFactory.dereference(type, muta_expression);
			propagations.add(new SecInfectPair(constraint, SecFactory.set_expression(statement, expression, muta_expression)));
		}
		else {
			propagations.add(new SecInfectPair(constraint, SecFactory.trap_error(statement)));
		}
	}
	
	@Override
	protected void process_ins_expression(CirStatement statement, CirExpression expression, SecInsExpressionError error,
			Collection<SecInfectPair> propagations) throws Exception {
		CType type = expression.get_data_type(); SymExpression muta_expression;
		COperator operator = error.get_operator().get_operator();
		SymExpression loperand = error.get_orig_expression().get_expression();
		SymExpression roperand = error.get_operand().get_expression();
		
		SecConstraint constraint = SecFactory.condition_constraint(statement, Boolean.TRUE, true);
		switch(operator) {
		case arith_sub:	muta_expression = SymFactory.arith_sub(loperand.get_data_type(), roperand, loperand); break;
		default: 		muta_expression = null; break;
		}
		
		if(muta_expression != null) {
			muta_expression = SymFactory.dereference(type, muta_expression);
			propagations.add(new SecInfectPair(constraint, SecFactory.set_expression(statement, expression, muta_expression)));
		}
		else {
			propagations.add(new SecInfectPair(constraint, SecFactory.trap_error(statement)));
		}
	}
	
	@Override
	protected void process_uny_expression(CirStatement statement, CirExpression expression, SecUnyExpressionError error,
			Collection<SecInfectPair> propagations) throws Exception {
		propagations.add(new SecInfectPair(
				SecFactory.condition_constraint(statement, Boolean.TRUE, true),
				SecFactory.trap_error(statement)));
	}
	
}
