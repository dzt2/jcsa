package com.jcsa.jcparse.lang.sym;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * It provides interfaces to parse and generate symbolic expressions from other instance.
 * 
 * @author yukimula
 *
 */
public class SymFactory {
	
	/* configuration items */
	/** whether to optimize default-value in CIR program **/
	private static boolean cir_optimization = false;
	/** the template to support the sizeof operations in **/
	private static CRunTemplate run_template = null;
	/**
	 * set the configuration items
	 * @param run_template
	 * @param cir_optimization
	 */
	public static void config(CRunTemplate run_template, boolean cir_optimization) {
		SymFactory.run_template = run_template;
		SymFactory.cir_optimization = cir_optimization;
	}
	
	/* parsing methods */
	/**
	 * @param source {bool, char, short, int, long, float, double, CConstant | AstExpression, CirExpression, SymExpression | CirStatement, CirExecution}
	 * @return
	 * @throws Exception
	 */
	public static SymExpression sym_expression(Object source) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source as null");
		else if(source instanceof Boolean || source instanceof Character || source instanceof Short
				|| source instanceof Integer || source instanceof Long || source instanceof Float
				|| source instanceof Double || source instanceof CConstant) 
			return SymParser.parser.parse_constant(source);
		else if(source instanceof AstExpression || source instanceof AstInitializer)
			return (SymExpression) SymParser.parser.parse_ast((AstNode) source, run_template);
		else if(source instanceof CirExpression)
			return (SymExpression) SymParser.parser.parse_cir((CirNode) source, cir_optimization);
		else if(source instanceof CirStatement) {
			CirStatement statement = (CirStatement) source;
			return SymParser.parser.parse_statement(statement.get_tree().get_localizer().get_execution(statement));
		}
		else if(source instanceof CirExecution)
			return SymParser.parser.parse_statement((CirExecution) source);
		else if(source instanceof SymExpression)
			return (SymExpression) source;
		else
			throw new IllegalArgumentException(source.getClass().getSimpleName());
	}
	/**
	 * @param source {bool, char, short, int, long, float, double, CConstant}
	 * @return
	 * @throws Exception
	 */
	public static SymConstant sym_constant(Object source) throws Exception {
		return SymParser.parser.parse_constant(source);
	}
	/**
	 * [bool]	--> expression 		{true}
	 * 			-->	!expression		{false}
	 * [number]	--> expression != 0	{true}
	 * 			--> expression == 0	{false}
	 * @param expression
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static SymExpression sym_condition(Object expression, boolean value) throws Exception {
		return SymParser.parser.sym_condition(sym_expression(expression), value);
	}
	
	/* constructors */
	/**
	 * @param data_type
	 * @return the data type of the expression
	 * @throws Exception
	 */
	private static CType get_type(CType data_type) throws Exception {
		if(data_type == null)
			return CBasicTypeImpl.void_type;
		else
			return CTypeAnalyzer.get_value_type(data_type);
	}
	public static SymExpression identifier(CType data_type, String name) throws Exception {
		return SymIdentifier.create(get_type(data_type), name);
	}
	public static SymExpression string_literal(CType data_type, String literal) throws Exception {
		return SymLiteral.create(get_type(data_type), literal);
	}
	public static SymExpression arith_neg(CType data_type, Object operand) throws Exception {
		return SymUnaryExpression.create(get_type(data_type), SymOperator.create(COperator.negative), sym_expression(operand));
	}
	public static SymExpression bitws_rsv(CType data_type, Object operand) throws Exception {
		return SymUnaryExpression.create(get_type(data_type), SymOperator.create(COperator.bit_not), sym_expression(operand));
	}
	public static SymExpression logic_not(Object operand) throws Exception {
		return SymUnaryExpression.create(CBasicTypeImpl.bool_type, SymOperator.create(COperator.logic_not), sym_expression(operand));
	}
	public static SymExpression address_of(CType data_type, Object operand) throws Exception {
		return SymUnaryExpression.create(get_type(data_type), SymOperator.create(COperator.address_of), sym_expression(operand));
	}
	public static SymExpression dereference(CType data_type, Object operand) throws Exception {
		return SymUnaryExpression.create(get_type(data_type), SymOperator.create(COperator.dereference), sym_expression(operand));
	}
	public static SymExpression type_casting(CType data_type, Object operand) throws Exception {
		return SymUnaryExpression.create(get_type(data_type), SymOperator.create(COperator.assign), sym_expression(operand));
	}
	public static SymExpression arith_add(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymBinaryExpression.create(get_type(data_type), SymOperator.create(
				COperator.arith_add), sym_expression(loperand), sym_expression(roperand));
	}
	public static SymExpression arith_sub(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymBinaryExpression.create(get_type(data_type), SymOperator.create(
				COperator.arith_sub), sym_expression(loperand), sym_expression(roperand));
	}
	public static SymExpression arith_mul(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymBinaryExpression.create(get_type(data_type), SymOperator.create(
				COperator.arith_mul), sym_expression(loperand), sym_expression(roperand));
	}
	public static SymExpression arith_div(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymBinaryExpression.create(get_type(data_type), SymOperator.create(
				COperator.arith_div), sym_expression(loperand), sym_expression(roperand));
	}
	public static SymExpression arith_mod(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymBinaryExpression.create(get_type(data_type), SymOperator.create(
				COperator.arith_mod), sym_expression(loperand), sym_expression(roperand));
	}
	public static SymExpression bitws_and(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymBinaryExpression.create(get_type(data_type), SymOperator.create(
				COperator.bit_and), sym_expression(loperand), sym_expression(roperand));
	}
	public static SymExpression bitws_ior(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymBinaryExpression.create(get_type(data_type), SymOperator.create(
				COperator.bit_or), sym_expression(loperand), sym_expression(roperand));
	}
	public static SymExpression bitws_xor(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymBinaryExpression.create(get_type(data_type), SymOperator.create(
				COperator.bit_xor), sym_expression(loperand), sym_expression(roperand));
	}
	public static SymExpression bitws_lsh(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymBinaryExpression.create(get_type(data_type), SymOperator.create(
				COperator.left_shift), sym_expression(loperand), sym_expression(roperand));
	}
	public static SymExpression bitws_rsh(CType data_type, Object loperand, Object roperand) throws Exception {
		return SymBinaryExpression.create(get_type(data_type), SymOperator.create(
				COperator.righ_shift), sym_expression(loperand), sym_expression(roperand));
	}
	public static SymExpression logic_and(Object loperand, Object roperand) throws Exception {
		return SymBinaryExpression.create(CBasicTypeImpl.bool_type, SymOperator.create(
				COperator.logic_and), sym_condition(loperand, true), sym_condition(roperand, true));
	}
	public static SymExpression logic_ior(Object loperand, Object roperand) throws Exception {
		return SymBinaryExpression.create(CBasicTypeImpl.bool_type, SymOperator.create(
				COperator.logic_or), sym_condition(loperand, true), sym_condition(roperand, true));
	}
	public static SymExpression greater_tn(Object loperand, Object roperand) throws Exception {
		return SymBinaryExpression.create(CBasicTypeImpl.bool_type, SymOperator.create(
				COperator.greater_tn), sym_expression(loperand), sym_expression(roperand));
	}
	public static SymExpression greater_eq(Object loperand, Object roperand) throws Exception {
		return SymBinaryExpression.create(CBasicTypeImpl.bool_type, SymOperator.create(
				COperator.greater_eq), sym_expression(loperand), sym_expression(roperand));
	}
	public static SymExpression smaller_tn(Object loperand, Object roperand) throws Exception {
		return SymBinaryExpression.create(CBasicTypeImpl.bool_type, SymOperator.create(
				COperator.smaller_tn), sym_expression(loperand), sym_expression(roperand));
	}
	public static SymExpression smaller_eq(Object loperand, Object roperand) throws Exception {
		return SymBinaryExpression.create(CBasicTypeImpl.bool_type, SymOperator.create(
				COperator.smaller_eq), sym_expression(loperand), sym_expression(roperand));
	}
	public static SymExpression equal_with(Object loperand, Object roperand) throws Exception {
		return SymBinaryExpression.create(CBasicTypeImpl.bool_type, SymOperator.create(
				COperator.equal_with), sym_expression(loperand), sym_expression(roperand));
	}
	public static SymExpression not_equals(Object loperand, Object roperand) throws Exception {
		return SymBinaryExpression.create(CBasicTypeImpl.bool_type, SymOperator.create(
				COperator.not_equals), sym_expression(loperand), sym_expression(roperand));
	}
	public static SymExpression field_expression(CType data_type, Object body, String field) throws Exception {
		return SymFieldExpression.create(get_type(data_type), sym_expression(body), SymField.create(field));
	}
	public static SymExpression call_expression(CType data_type, Object function, Iterable<Object> arguments) throws Exception {
		List<SymExpression> expressions = new ArrayList<SymExpression>();
		for(Object argument : arguments) expressions.add(sym_expression(argument));
		return SymCallExpression.create(get_type(data_type), sym_expression(function), SymArgumentList.create(expressions));
	}
	public static SymExpression initializer_list(CType data_type, Iterable<Object> elements) throws Exception {
		List<SymExpression> expressions = new ArrayList<SymExpression>();
		for(Object element : elements) expressions.add(sym_expression(element));
		return SymInitializerList.create(get_type(data_type), expressions);
	}
	
}
