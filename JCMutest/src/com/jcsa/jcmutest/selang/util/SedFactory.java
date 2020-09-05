package com.jcsa.jcmutest.selang.util;

import com.jcsa.jcmutest.selang.lang.SedNode;
import com.jcsa.jcmutest.selang.lang.abst.SedAbstractValueError;
import com.jcsa.jcmutest.selang.lang.abst.SedAppExpressionError;
import com.jcsa.jcmutest.selang.lang.abst.SedInsExpressionError;
import com.jcsa.jcmutest.selang.lang.abst.SedMutExpressionError;
import com.jcsa.jcmutest.selang.lang.abst.SedNevExpressionError;
import com.jcsa.jcmutest.selang.lang.conc.SedAddExpressionError;
import com.jcsa.jcmutest.selang.lang.conc.SedAndExpressionError;
import com.jcsa.jcmutest.selang.lang.conc.SedChgExpressionError;
import com.jcsa.jcmutest.selang.lang.conc.SedConcreteValueError;
import com.jcsa.jcmutest.selang.lang.conc.SedDecExpressionError;
import com.jcsa.jcmutest.selang.lang.conc.SedExtExpressionError;
import com.jcsa.jcmutest.selang.lang.conc.SedIncExpressionError;
import com.jcsa.jcmutest.selang.lang.conc.SedIorExpressionError;
import com.jcsa.jcmutest.selang.lang.conc.SedMulExpressionError;
import com.jcsa.jcmutest.selang.lang.conc.SedNegExpressionError;
import com.jcsa.jcmutest.selang.lang.conc.SedRsvExpressionError;
import com.jcsa.jcmutest.selang.lang.conc.SedSetExpressionError;
import com.jcsa.jcmutest.selang.lang.conc.SedShkExpressionError;
import com.jcsa.jcmutest.selang.lang.conc.SedXorExpressionError;
import com.jcsa.jcmutest.selang.lang.cons.SedConditionConstraint;
import com.jcsa.jcmutest.selang.lang.cons.SedConstraint;
import com.jcsa.jcmutest.selang.lang.cons.SedExecutionConstraint;
import com.jcsa.jcmutest.selang.lang.desc.SedConjunctDescriptions;
import com.jcsa.jcmutest.selang.lang.desc.SedDescription;
import com.jcsa.jcmutest.selang.lang.desc.SedDisjunctDescriptions;
import com.jcsa.jcmutest.selang.lang.expr.SedBinaryExpression;
import com.jcsa.jcmutest.selang.lang.expr.SedCallExpression;
import com.jcsa.jcmutest.selang.lang.expr.SedConstant;
import com.jcsa.jcmutest.selang.lang.expr.SedDefaultValue;
import com.jcsa.jcmutest.selang.lang.expr.SedExpression;
import com.jcsa.jcmutest.selang.lang.expr.SedFieldExpression;
import com.jcsa.jcmutest.selang.lang.expr.SedIdExpression;
import com.jcsa.jcmutest.selang.lang.expr.SedInitializerList;
import com.jcsa.jcmutest.selang.lang.expr.SedLiteral;
import com.jcsa.jcmutest.selang.lang.expr.SedUnaryExpression;
import com.jcsa.jcmutest.selang.lang.serr.SedAddStatementError;
import com.jcsa.jcmutest.selang.lang.serr.SedDelStatementError;
import com.jcsa.jcmutest.selang.lang.serr.SedMutStatementError;
import com.jcsa.jcmutest.selang.lang.serr.SedSetStatementError;
import com.jcsa.jcmutest.selang.lang.serr.SedStatementError;
import com.jcsa.jcmutest.selang.lang.tokn.SedField;
import com.jcsa.jcmutest.selang.lang.tokn.SedStatement;
import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * It provides interface to create SedNode, from which the user obtains
 * the initial instance of SedNode and use clone() to create more ones.
 * @author yukimula
 *
 */
public class SedFactory {
	
	/* parsing from AstNode or CirNode or normal instance */
	/**
	 * @param source
	 * @param sizeof_template
	 * @return initial SedNode created from AstNode
	 * @throws Exception
	 */
	public static SedNode parse(AstNode source, 
			CRunTemplate sizeof_template) throws Exception {
		return SedParser.parse(source, sizeof_template);
	}
	/**
	 * @param source
	 * @return initial SedNode created from CirNode
	 * @throws Exception
	 */
	public static SedNode parse(CirNode source) throws Exception {
		return SedParser.parse(source);
	}
	/**
	 * @param source [bool|char|short|int|long|float|double|AstNode|CirNode|SedNode]
	 * @return initial SedNode parsed from normal instance
	 * @throws Exception
	 */
	public static SedNode fetch(Object source) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(source instanceof Boolean) {
			CConstant constant = new CConstant();
			constant.set_bool(((Boolean) source).booleanValue());
			return new SedConstant(null, CBasicTypeImpl.bool_type, constant);
		}
		else if(source instanceof Character) {
			CConstant constant = new CConstant();
			constant.set_char(((Character) source).charValue());
			return new SedConstant(null, CBasicTypeImpl.char_type, constant);
		}
		else if(source instanceof Short) {
			CConstant constant = new CConstant();
			constant.set_int(((Short) source).shortValue());
			return new SedConstant(null, CBasicTypeImpl.short_type, constant);
		}
		else if(source instanceof Integer) {
			CConstant constant = new CConstant();
			constant.set_int(((Integer) source).intValue());
			return new SedConstant(null, CBasicTypeImpl.int_type, constant);
		}
		else if(source instanceof Long) {
			CConstant constant = new CConstant();
			constant.set_long(((Long) source).longValue());
			return new SedConstant(null, CBasicTypeImpl.long_type, constant);
		}
		else if(source instanceof Float) {
			CConstant constant = new CConstant();
			constant.set_float(((Float) source).floatValue());
			return new SedConstant(null, CBasicTypeImpl.float_type, constant);
		}
		else if(source instanceof Double) {
			CConstant constant = new CConstant();
			constant.set_double(((Double) source).doubleValue());
			return new SedConstant(null, CBasicTypeImpl.double_type, constant);
		}
		else if(source instanceof AstNode) {
			return parse((AstNode) source, null);
		}
		else if(source instanceof CirNode) {
			return parse((CirNode) source);
		}
		else if(source instanceof SedNode) {
			return (SedNode) source;
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	/**
	 * @param source
	 * @param value
	 * @return the condition to be asserted, parsed from the source
	 * @throws Exception
	 */
	public static SedExpression get_condition(SedExpression source, boolean value) throws Exception {
		CType data_type = CTypeAnalyzer.get_value_type(source.get_data_type());
		if(CTypeAnalyzer.is_boolean(data_type)) {
			if(value) {
				return source;
			}
			else {
				return SedFactory.logic_not(source);
			}
		}
		else if(CTypeAnalyzer.is_number(data_type)
				|| CTypeAnalyzer.is_pointer(data_type)) {
			SedNode target = SedFactory.fetch(Integer.valueOf(0));
			if(value) {
				return SedFactory.not_equals(source, target);
			}
			else {
				return SedFactory.equal_with(source, target);
			}
		}
		else {
			throw new IllegalArgumentException(source.generate_code());
		}
	}
	
	/* cir-source & non-cir-source creation for SedNode */
	public static SedStatement statement(CirStatement statement) throws Exception {
		return new SedStatement(statement);
	}
	public static SedIdExpression id_expression(CType type, String name) throws Exception {
		return new SedIdExpression(null, type, name);
	}
	public static SedConstant constant(CType type, CConstant constant) throws Exception {
		return new SedConstant(null, type, constant);
	}
	public static SedLiteral literal(CType type, String literal) throws Exception {
		return new SedLiteral(null, type, literal);
	}
	public static SedDefaultValue default_value(CType type, String name) throws Exception {
		return new SedDefaultValue(null, type, name);
	}
	public static SedCallExpression call_expression(CType data_type,
			Object function, Iterable<Object> arguments) throws Exception {
		SedCallExpression expression = new SedCallExpression(null, data_type);
		expression.add_child(fetch(function));
		for(Object argument : arguments) expression.add_child(fetch(argument));
		return expression;
	}
	public static SedInitializerList initializer_list(CType 
			data_type, Iterable<Object> elements) throws Exception {
		SedInitializerList list = new SedInitializerList(null,
				data_type == null ? CBasicTypeImpl.void_type : data_type);
		for(Object element : elements) list.add_child(fetch(element));
		return list;
	}
	public static SedFieldExpression field_expression(CType 
			type, Object body, String name) throws Exception {
		SedFieldExpression expression = new SedFieldExpression(null, type);
		expression.add_child(fetch(body)); 
		expression.add_child(new SedField(name));
		return expression;
	}
	public static SedUnaryExpression arith_neg(CType data_type,
			Object operand) throws Exception {
		SedUnaryExpression expression = new SedUnaryExpression(
				null, data_type, COperator.negative);
		expression.add_child(fetch(operand)); return expression;
	}
	public static SedUnaryExpression bitws_rsv(CType data_type,
			Object operand) throws Exception {
		SedUnaryExpression expression = new SedUnaryExpression(
				null, data_type, COperator.bit_not);
		expression.add_child(fetch(operand)); return expression;
	}
	public static SedUnaryExpression logic_not(Object operand) throws Exception {
		SedUnaryExpression expression = new SedUnaryExpression(
				null, CBasicTypeImpl.bool_type, COperator.logic_not);
		expression.add_child(fetch(operand)); return expression;
	}
	public static SedUnaryExpression address_of(CType data_type,
			Object operand) throws Exception {
		SedUnaryExpression expression = new SedUnaryExpression(
				null, data_type, COperator.address_of);
		expression.add_child(fetch(operand)); return expression;
	}
	public static SedUnaryExpression dereference(CType data_type,
			Object operand) throws Exception {
		SedUnaryExpression expression = new SedUnaryExpression(
				null, data_type, COperator.dereference);
		expression.add_child(fetch(operand)); return expression;
	}
	public static SedUnaryExpression type_cast(CType data_type,
			Object operand) throws Exception {
		SedUnaryExpression expression = new SedUnaryExpression(
				null, data_type, COperator.assign);
		expression.add_child(fetch(operand)); return expression;
	}
	public static SedBinaryExpression arith_add(CType data_type,
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, data_type, COperator.arith_add);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression arith_sub(CType data_type,
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, data_type, COperator.arith_sub);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression arith_mul(CType data_type,
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, data_type, COperator.arith_mul);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression arith_div(CType data_type,
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, data_type, COperator.arith_div);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression arith_mod(CType data_type,
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, data_type, COperator.arith_mod);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression bitws_and(CType data_type,
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, data_type, COperator.bit_and);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression bitws_ior(CType data_type,
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, data_type, COperator.bit_or);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression bitws_xor(CType data_type,
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, data_type, COperator.bit_xor);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression bitws_lsh(CType data_type,
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, data_type, COperator.left_shift);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression bitws_rsh(CType data_type,
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, data_type, COperator.righ_shift);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression logic_and(
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, CBasicTypeImpl.bool_type, COperator.logic_and);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression logic_ior(
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, CBasicTypeImpl.bool_type, COperator.logic_or);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression greater_tn(
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, CBasicTypeImpl.bool_type, COperator.greater_tn);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression greater_eq(
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, CBasicTypeImpl.bool_type, COperator.greater_eq);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression smaller_tn(
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, CBasicTypeImpl.bool_type, COperator.smaller_tn);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression smaller_eq(
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, CBasicTypeImpl.bool_type, COperator.smaller_eq);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression equal_with(
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, CBasicTypeImpl.bool_type, COperator.equal_with);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	public static SedBinaryExpression not_equals(
			Object loperand, Object roperand) throws Exception {
		SedBinaryExpression expression = new SedBinaryExpression(
				null, CBasicTypeImpl.bool_type, COperator.not_equals);
		expression.add_child(fetch(loperand));
		expression.add_child(fetch(roperand));
		return expression;
	}
	
	/* description creators */
	public static SedConstraint execution_constraint(
			CirStatement statement, int loop_times) throws Exception {
		SedExpression times = (SedExpression) 
				fetch(Integer.valueOf(loop_times));
		return new SedExecutionConstraint(statement, times);
	}
	public static SedConstraint condition_constraint(CirStatement statement, 
			SedExpression condition, boolean assert_value) throws Exception {
		condition = SedFactory.get_condition(condition, assert_value);
		return new SedConditionConstraint(statement, condition);
	}
	public static SedStatementError add_statement(CirStatement statement) throws Exception {
		return new SedAddStatementError(statement, statement);
	}
	public static SedStatementError del_statement(CirStatement statement) throws Exception {
		return new SedDelStatementError(statement, statement);
	}
	public static SedStatementError set_statement(
			CirStatement orig_statement, 
			CirStatement muta_statement) throws Exception {
		return new SedSetStatementError(
				orig_statement, orig_statement, muta_statement);
	}
	public static SedStatementError mut_statement(
			CirStatement orig_statement, 
			CirStatement muta_statement) throws Exception {
		return new SedMutStatementError(
				orig_statement, orig_statement, muta_statement);
	}
	public static SedAbstractValueError nev_expression(CirStatement statement,
			CirExpression expression, COperator operator) throws Exception {
		return new SedNevExpressionError(statement, expression, operator);
	}
	public static SedAbstractValueError app_expression(
			CirStatement statement, CirExpression expression, 
			COperator operator, SedExpression operand) throws Exception {
		return new SedAppExpressionError(
				statement, expression, operator, operand);
	}
	public static SedAbstractValueError ins_expression(
			CirStatement statement, CirExpression expression, 
			COperator operator, SedExpression operand) throws Exception {
		return new SedInsExpressionError(
				statement, expression, operator, operand);
	}
	public static SedAbstractValueError mut_expression(CirStatement statement,
			CirExpression orig_expression, SedExpression muta_expression) throws Exception {
		return new SedMutExpressionError(statement,
				orig_expression, muta_expression);
	}
	public static SedConcreteValueError chg_expression(CirStatement statement,
			CirExpression expression) throws Exception {
		return new SedChgExpressionError(statement, expression);
	}
	public static SedConcreteValueError neg_expression(CirStatement statement,
			CirExpression expression) throws Exception {
		return new SedNegExpressionError(statement, expression);
	}
	public static SedConcreteValueError rsv_expression(CirStatement statement,
			CirExpression expression) throws Exception {
		return new SedRsvExpressionError(statement, expression);
	}
	public static SedConcreteValueError inc_expression(CirStatement statement,
			CirExpression expression) throws Exception {
		return new SedIncExpressionError(statement, expression);
	}
	public static SedConcreteValueError dec_expression(CirStatement statement,
			CirExpression expression) throws Exception {
		return new SedDecExpressionError(statement, expression);
	}
	public static SedConcreteValueError ext_expression(CirStatement statement,
			CirExpression expression) throws Exception {
		return new SedExtExpressionError(statement, expression);
	}
	public static SedConcreteValueError shk_expression(CirStatement statement,
			CirExpression expression) throws Exception {
		return new SedShkExpressionError(statement, expression);
	}
	public static SedConcreteValueError set_expression(CirStatement statement,
			CirExpression orig_expression, SedExpression muta_expression) throws Exception {
		return new SedSetExpressionError(statement, 
				orig_expression, muta_expression);
	}
	public static SedConcreteValueError add_expression(CirStatement statement,
			CirExpression orig_expression, SedExpression muta_expression) throws Exception {
		return new SedAddExpressionError(statement, 
				orig_expression, muta_expression);
	}
	public static SedConcreteValueError mul_expression(CirStatement statement,
			CirExpression orig_expression, SedExpression muta_expression) throws Exception {
		return new SedMulExpressionError(statement, 
				orig_expression, muta_expression);
	}
	public static SedConcreteValueError and_expression(CirStatement statement,
			CirExpression orig_expression, SedExpression muta_expression) throws Exception {
		return new SedAndExpressionError(statement, 
				orig_expression, muta_expression);
	}
	public static SedConcreteValueError ior_expression(CirStatement statement,
			CirExpression orig_expression, SedExpression muta_expression) throws Exception {
		return new SedIorExpressionError(statement, 
				orig_expression, muta_expression);
	}
	public static SedConcreteValueError xor_expression(CirStatement statement,
			CirExpression orig_expression, SedExpression muta_expression) throws Exception {
		return new SedXorExpressionError(statement, 
				orig_expression, muta_expression);
	}
	public static SedDescription conjunct(CirStatement statement, 
			Iterable<SedDescription> descriptions) throws Exception {
		SedDescription result = new SedConjunctDescriptions(statement);
		for(SedDescription description : descriptions) {
			result.add_child(description);
		}
		return result;
	}
	public static SedDescription disjunct(CirStatement statement, 
			Iterable<SedDescription> descriptions) throws Exception {
		SedDescription result = new SedDisjunctDescriptions(statement);
		for(SedDescription description : descriptions) {
			result.add_child(description);
		}
		return result;
	}
	
}
