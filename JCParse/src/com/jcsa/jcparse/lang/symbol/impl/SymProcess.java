package com.jcsa.jcparse.lang.symbol.impl;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymAddress;
import com.jcsa.jcparse.lang.symbol.SymCastExpression;
import com.jcsa.jcparse.lang.symbol.SymComputeExpression;
import com.jcsa.jcparse.lang.symbol.SymConstant;
import com.jcsa.jcparse.lang.symbol.SymDeferExpression;
import com.jcsa.jcparse.lang.symbol.SymExpression;
import com.jcsa.jcparse.lang.symbol.SymFieldExpression;
import com.jcsa.jcparse.lang.symbol.SymSequence;
import com.jcsa.jcparse.lang.symbol.SymStatement;
import com.jcsa.jcparse.lang.symbol.SymStringLiteral;

/**
 * 
 * @author yukimula
 *
 */
public class SymProcess {
	
	/* factory methods */
	public static SymAddress new_address(CType data_type, String identifier) throws Exception {
		return new SymAddressImpl(data_type, identifier);
	}
	public static SymConstant new_constant(CConstant constant) throws Exception {
		return new SymConstantImpl(constant.get_type(), constant);
	}
	public static SymStringLiteral new_literal(CType data_type, String literal) throws Exception {
		return new SymStringLiteralImpl(data_type, literal);
	}
	public static SymSequence new_sequence() throws Exception {
		return new SymSequenceImpl(null);
	}
	public static SymFieldExpression new_field_expression(CType data_type, 
			SymExpression body, String field) throws Exception {
		return new SymFieldExpressionImpl(data_type, body, field);
	}
	public static SymDeferExpression new_defer_expression(CType data_type, SymExpression operand) throws Exception {
		return new SymDeferExpressionImpl(data_type, operand);
	}
	public static SymCastExpression new_cast_expression(CType data_type, SymExpression operand) throws Exception {
		return new SymCastExpressionImpl(data_type, operand);
	}
	public static SymComputeExpression new_unary_expression(CType data_type, 
			COperator operator, SymExpression operand) throws Exception {
		SymComputeExpression expr = new SymComputeExpressionImpl(data_type, operator);
		expr.add_operand(operand); return expr;
	}
	public static SymComputeExpression new_binary_expression(CType data_type, 
			COperator operator, SymExpression loperand, SymExpression roperand) throws Exception {
		SymComputeExpression expr = new SymComputeExpressionImpl(data_type, operator);
		expr.add_operand(loperand); expr.add_operand(roperand); return expr;
	}
	public static SymStatement new_statement(CirExecution execution) throws Exception {
		SymStatement stmt = new SymStatementImpl();
		stmt.set_execution(execution); return stmt;
	}
	
	/*  */
	
	
}
