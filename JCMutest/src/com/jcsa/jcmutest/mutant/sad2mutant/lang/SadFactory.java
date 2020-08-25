package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.ctype.impl.CTypeFactory;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * It provides the interface to create SadNode.
 * 
 * @author yukimula
 *
 */
public class SadFactory {
	
	private static final CTypeFactory tfactory = new CTypeFactory();
	
	public static SadIdExpression id_expression(CType data_type, String name) {
		return new SadIdExpression(null, data_type, name);
	}
	public static SadConstant constant(CConstant constant) {
		return new SadConstant(null, constant.get_type(), constant);
	}
	public static SadConstant constant(boolean value) {
		CConstant constant = new CConstant();
		constant.set_bool(value);
		return new SadConstant(null, constant.get_type(), constant);
	}
	public static SadConstant constant(char value) {
		CConstant constant = new CConstant();
		constant.set_char(value);
		return new SadConstant(null, constant.get_type(), constant);
	}
	public static SadConstant constant(int value) {
		CConstant constant = new CConstant();
		constant.set_int(value);
		return new SadConstant(null, constant.get_type(), constant);
	}
	public static SadConstant constant(long value) {
		CConstant constant = new CConstant();
		constant.set_long(value);
		return new SadConstant(null, constant.get_type(), constant);
	}
	public static SadConstant constant(float value) {
		CConstant constant = new CConstant();
		constant.set_float(value);
		return new SadConstant(null, constant.get_type(), constant);
	}
	public static SadConstant constant(double value) {
		CConstant constant = new CConstant();
		constant.set_double(value);
		return new SadConstant(null, constant.get_type(), constant);
	}
	public static SadLiteral literal(String literal) throws Exception {
		return new SadLiteral(null, tfactory.get_array_type(CBasicTypeImpl.char_type, literal.length() + 1), literal);
	}
	public static SadDefaultValue default_value(CType data_type) throws Exception {
		return new SadDefaultValue(null, data_type);
	}
	public static SadUnaryExpression arith_neg(CType data_type, SadExpression operand) throws Exception {
		SadUnaryExpression expression = new SadUnaryExpression(null, data_type, COperator.negative);
		expression.add_child(operand);
		return expression;
	}
	public static SadUnaryExpression bitws_rsv(CType data_type, SadExpression operand) throws Exception {
		SadUnaryExpression expression = new SadUnaryExpression(null, data_type, COperator.bit_not);
		expression.add_child(operand);
		return expression;
	}
	public static SadUnaryExpression logic_not(CType data_type, SadExpression operand) throws Exception {
		SadUnaryExpression expression = new SadUnaryExpression(null, data_type, COperator.logic_not);
		expression.add_child(operand);
		return expression;
	}
	public static SadUnaryExpression address_of(CType data_type, SadExpression operand) throws Exception {
		SadUnaryExpression expression = new SadUnaryExpression(null, data_type, COperator.address_of);
		expression.add_child(operand);
		return expression;
	}
	public static SadUnaryExpression dereference(CType data_type, SadExpression operand) throws Exception {
		SadUnaryExpression expression = new SadUnaryExpression(null, data_type, COperator.dereference);
		expression.add_child(operand);
		return expression;
	}
	public static SadUnaryExpression type_cast(CType data_type, SadExpression operand) throws Exception {
		SadUnaryExpression expression = new SadUnaryExpression(null, data_type, COperator.assign);
		expression.add_child(operand);
		return expression;
	}
	public static SadBinaryExpression arith_sub(CType data_type, SadExpression loperand, SadExpression roperand) throws Exception {
		SadBinaryExpression expression = new SadBinaryExpression(null, data_type, COperator.arith_sub);
		expression.add_child(loperand);
		expression.add_child(roperand);
		return expression;
	}
	public static SadBinaryExpression arith_div(CType data_type, SadExpression loperand, SadExpression roperand) throws Exception {
		SadBinaryExpression expression = new SadBinaryExpression(null, data_type, COperator.arith_div);
		expression.add_child(loperand);
		expression.add_child(roperand);
		return expression;
	}
	public static SadBinaryExpression arith_mod(CType data_type, SadExpression loperand, SadExpression roperand) throws Exception {
		SadBinaryExpression expression = new SadBinaryExpression(null, data_type, COperator.arith_mod);
		expression.add_child(loperand);
		expression.add_child(roperand);
		return expression;
	}
	public static SadBinaryExpression bitws_lsh(CType data_type, SadExpression loperand, SadExpression roperand) throws Exception {
		SadBinaryExpression expression = new SadBinaryExpression(null, data_type, COperator.left_shift);
		expression.add_child(loperand);
		expression.add_child(roperand);
		return expression;
	}
	public static SadBinaryExpression bitws_rsh(CType data_type, SadExpression loperand, SadExpression roperand) throws Exception {
		SadBinaryExpression expression = new SadBinaryExpression(null, data_type, COperator.righ_shift);
		expression.add_child(loperand);
		expression.add_child(roperand);
		return expression;
	}
	public static SadBinaryExpression greater_tn(CType data_type, SadExpression loperand, SadExpression roperand) throws Exception {
		SadBinaryExpression expression = new SadBinaryExpression(null, data_type, COperator.greater_tn);
		expression.add_child(loperand);
		expression.add_child(roperand);
		return expression;
	}
	public static SadBinaryExpression greater_eq(CType data_type, SadExpression loperand, SadExpression roperand) throws Exception {
		SadBinaryExpression expression = new SadBinaryExpression(null, data_type, COperator.greater_eq);
		expression.add_child(loperand);
		expression.add_child(roperand);
		return expression;
	}
	public static SadBinaryExpression smaller_tn(CType data_type, SadExpression loperand, SadExpression roperand) throws Exception {
		SadBinaryExpression expression = new SadBinaryExpression(null, data_type, COperator.smaller_tn);
		expression.add_child(loperand);
		expression.add_child(roperand);
		return expression;
	}
	public static SadBinaryExpression smaller_eq(CType data_type, SadExpression loperand, SadExpression roperand) throws Exception {
		SadBinaryExpression expression = new SadBinaryExpression(null, data_type, COperator.smaller_eq);
		expression.add_child(loperand);
		expression.add_child(roperand);
		return expression;
	}
	public static SadBinaryExpression equal_with(CType data_type, SadExpression loperand, SadExpression roperand) throws Exception {
		SadBinaryExpression expression = new SadBinaryExpression(null, data_type, COperator.equal_with);
		expression.add_child(loperand);
		expression.add_child(roperand);
		return expression;
	}
	public static SadBinaryExpression not_equals(CType data_type, SadExpression loperand, SadExpression roperand) throws Exception {
		SadBinaryExpression expression = new SadBinaryExpression(null, data_type, COperator.not_equals);
		expression.add_child(loperand);
		expression.add_child(roperand);
		return expression;
	}
	public static SadMultiExpression arith_add(CType data_type, Iterable<SadExpression> operands) throws Exception {
		SadMultiExpression expression = new SadMultiExpression(null, data_type, COperator.arith_add);
		for(SadExpression operand : operands) { expression.add_child(operand); } 
		return expression;
	}
	public static SadMultiExpression arith_mul(CType data_type, Iterable<SadExpression> operands) throws Exception {
		SadMultiExpression expression = new SadMultiExpression(null, data_type, COperator.arith_mul);
		for(SadExpression operand : operands) { expression.add_child(operand); } 
		return expression;
	}
	public static SadMultiExpression bitws_and(CType data_type, Iterable<SadExpression> operands) throws Exception {
		SadMultiExpression expression = new SadMultiExpression(null, data_type, COperator.bit_and);
		for(SadExpression operand : operands) { expression.add_child(operand); } 
		return expression;
	}
	public static SadMultiExpression bitws_ior(CType data_type, Iterable<SadExpression> operands) throws Exception {
		SadMultiExpression expression = new SadMultiExpression(null, data_type, COperator.bit_or);
		for(SadExpression operand : operands) { expression.add_child(operand); } 
		return expression;
	}
	public static SadMultiExpression bitws_xor(CType data_type, Iterable<SadExpression> operands) throws Exception {
		SadMultiExpression expression = new SadMultiExpression(null, data_type, COperator.bit_xor);
		for(SadExpression operand : operands) { expression.add_child(operand); } 
		return expression;
	}
	public static SadMultiExpression logic_and(CType data_type, Iterable<SadExpression> operands) throws Exception {
		SadMultiExpression expression = new SadMultiExpression(null, data_type, COperator.logic_and);
		for(SadExpression operand : operands) { expression.add_child(operand); } 
		return expression;
	}
	public static SadMultiExpression logic_ior(CType data_type, Iterable<SadExpression> operands) throws Exception {
		SadMultiExpression expression = new SadMultiExpression(null, data_type, COperator.logic_or);
		for(SadExpression operand : operands) { expression.add_child(operand); } 
		return expression;
	}
	public static SadFieldExpression field_expression(CType data_type, SadExpression body, String field) throws Exception {
		SadFieldExpression expression = new SadFieldExpression(null, data_type);
		expression.add_child(body);
		expression.add_child(new SadField(null, field));
		return expression;
	}
	public static SadInitializerList initializer_list(CType data_type, Iterable<SadExpression> operands) throws Exception {
		SadInitializerList list = new SadInitializerList(null, data_type);
		for(SadExpression operand : operands) { list.add_child(operand); }
		return list;
	}
	public static SadCallExpression call_expression(CType data_type, SadExpression function, Iterable<SadExpression> arguments) throws Exception {
		SadCallExpression expression = new SadCallExpression(null, data_type);
		expression.add_child(function);
		expression.add_child(new SadArgumentList(null));
		for(SadExpression argument : arguments) {
			expression.get_argument_list().add_child(argument);
		}
		return expression;
	}
	public static SadAssignStatement assign_statement(CirExecution execution, SadExpression lvalue, SadExpression rvalue) throws Exception {
		SadAssignStatement statement = new SadAssignStatement(execution.get_statement());
		statement.add_child(new SadLabel(null, execution));
		statement.add_child(lvalue);
		statement.add_child(rvalue);
		return statement;
	}
	public static SadGotoStatement goto_statement(CirExecution source, CirExecution target) throws Exception {
		SadGotoStatement statement = new SadGotoStatement(source.get_statement());
		statement.add_child(new SadLabel(null, source));
		statement.add_child(new SadLabel(null, target));
		return statement;
	}
	public static SadIfStatement if_statement(CirExecution execution, SadExpression 
			condition, CirExecution tbranch, CirExecution fbranch) throws Exception {
		SadIfStatement statement = new SadIfStatement(execution.get_statement());
		statement.add_child(new SadLabel(null, execution));
		statement.add_child(condition);
		statement.add_child(new SadLabel(null, tbranch));
		statement.add_child(new SadLabel(null, fbranch));
		return statement;
	}
	public static SadCallStatement call_statement(CirExecution execution, SadExpression function, Iterable<SadExpression> arguments) throws Exception {
		SadCallStatement statement = new SadCallStatement(execution.get_statement());
		statement.add_child(new SadLabel(null, execution));
		statement.add_child(function);
		statement.add_child(new SadArgumentList(null));
		for(SadExpression argument : arguments) {
			statement.get_argument_list().add_child(argument);
		}
		return statement;
	}
	public static SadLabelStatement label_statement(CirExecution execution) throws Exception {
		SadLabelStatement statement = new SadLabelStatement(execution.get_statement());
		statement.add_child(new SadLabel(null, execution));
		return statement;
	}
	
	public static SadExecuteOnAssertion assert_execute_on(SadStatement statement, int times) throws Exception {
		SadExecuteOnAssertion assertion = new SadExecuteOnAssertion(null);
		assertion.add_child(statement);
		assertion.add_child(constant(times));
		return assertion;
	}
	public static SadConditionAssertion assert_condition(SadStatement 
				statement, SadExpression condition) throws Exception {
		SadConditionAssertion assertion = new SadConditionAssertion(null);
		assertion.add_child(statement);
		assertion.add_child(condition);
		return assertion;
	}
	public static SadSetLabelAssertion set_label_mutation(SadStatement source, SadStatement target) throws Exception {
		SadSetLabelAssertion assertion = new SadSetLabelAssertion(null);
		assertion.add_child(source);
		assertion.add_child(target);
		return assertion;
	}
	public static SadSetExpressionAssertion set_expression(SadStatement 
			statement, SadExpression source, SadExpression target) throws Exception {
		SadSetExpressionAssertion assertion = new SadSetExpressionAssertion(null);
		assertion.add_child(statement);
		assertion.add_child(source);
		assertion.add_child(target);
		return assertion;
	}
	public static SadInsOperatorAssertion ins_operator(SadStatement statement,
			SadExpression expression, COperator operator) throws Exception {
		SadInsOperatorAssertion assertion = new SadInsOperatorAssertion(null);
		assertion.add_child(statement);
		assertion.add_child(expression);
		assertion.add_child(new SadOperator(operator));
		return assertion;
	}
	public static SadAddOperandAssertion add_operand(SadStatement statement,
			SadExpression expression, COperator operator, SadExpression operand) throws Exception {
		SadAddOperandAssertion assertion = new SadAddOperandAssertion(null);
		assertion.add_child(statement);
		assertion.add_child(expression);
		assertion.add_child(new SadOperator(operator));
		assertion.add_child(operand);
		return assertion;
	}
	public static SadInsOperandAssertion ins_operand(SadStatement statement,
			SadExpression expression, COperator operator, SadExpression operand) throws Exception {
		SadInsOperandAssertion assertion = new SadInsOperandAssertion(null);
		assertion.add_child(statement);
		assertion.add_child(expression);
		assertion.add_child(new SadOperator(operator));
		assertion.add_child(operand);
		return assertion;
	}
	public static SadConjunctAssertion conjunct(Iterable<SadAssertion> assertions) throws Exception {
		SadConjunctAssertion set = new SadConjunctAssertion(null);
		for(SadAssertion assertion : assertions) {
			set.add_child(assertion);
		}
		return set;
	}
	public static SadDisjunctAssertion disjunct(Iterable<SadAssertion> assertions) throws Exception {
		SadDisjunctAssertion set = new SadDisjunctAssertion(null);
		for(SadAssertion assertion : assertions) {
			set.add_child(assertion);
		}
		return set;
	}
	
}
