package com.jcsa.jcparse.lang.symbol;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CTypeFactory;
import com.jcsa.jcparse.lang.irlang.expr.CirDefaultValue;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;
import com.jcsa.jcparse.lang.scope.CName;


public class SymbolFactory {
	
	/* parameter getter & setter */
	/** singleton for creating **/
	public static final SymbolNodeFactory factory = new SymbolNodeFactory();
	/**
	 * @return used to generate type for implied expression
	 */
	public static CTypeFactory get_type_factory() { return factory.get_type_factory(); }
	/**
	 * @return used to implement the computation for sizeof
	 */
	public static CRunTemplate get_ast_template() { return factory.get_ast_template(); }
	/**
	 * @return true to parse CirDefaultValue into constant
	 */
	public static boolean      get_cir_optimize() { return factory.get_cir_optimize(); }
	/**
	 * set the configuration parameters in the factory
	 * @param ast_template use to implement the computation on "sizeof"
	 * @param cir_optimize true to parse CirDefaultValue into constant
	 */
	public static void config(CRunTemplate ast_template, boolean cir_optimize) {
		factory.config(ast_template, cir_optimize);
	}
	
	/* parsing methods (static and object) */
	/**
	 * @param source
	 * @return	parse the Java Object to SymbolicExpression based on following rules:								<br>
	 * 			(1)	{bool|char|short|int|long|float|double}						==>	SymbolConstant		{source}	<br>
	 * 			(2)	{String}													==>	SymbolLiteral		{source}	<br>
	 * 			(3)	{CConstant}													==>	SymbolConstant		{value}		<br>
	 * 			(4)	{AstNode}[as expression]									==> SymbolExpression	{ast}		<br>
	 * 			(5)	{CirStatement|CirExecution}									==>	SymbolIdentifier	{do#exec}	<br>
	 * 			(6) {CirNode}[as expression]									==>	SymbolExpression	{cir}		<br>
	 * 			(7)	{SymbolExpression}											==>	return source					<br>
	 * 			(8)	{null|otherwise}											==> throw Exception 				<br>
	 * @throws Exception
	 */
	public static SymbolExpression sym_expression(Object source) throws Exception {
		return factory.obj2expression(source);
	}
	public static SymbolExpression sym_condition(Object source, boolean value) throws Exception {
		return factory.obj2condition(source, value);
	}
	public static SymbolConstant sym_constant(Object source) throws Exception {
		return (SymbolConstant) factory.obj2constant(source);
	}
	
	/* non-source based constructors */
	/**
	 * @param cname
	 * @return name#scope
	 * @throws Exception
	 */
	public static SymbolExpression identifier(CName cname) throws Exception {
		return factory.new_identifier(cname);
	}
	/**
	 * @param ast_reference
	 * @return #ast.key
	 * @throws Exception
	 */
	public static SymbolExpression identifier(AstExpression ast_reference) throws Exception {
		return factory.new_identifier(ast_reference);
	}
	/**
	 * @param default_value
	 * @return default#cir_node.id
	 * @throws Exception
	 */
	public static SymbolExpression identifier(CirDefaultValue default_value) throws Exception {
		return factory.new_identifier(default_value);
	}
	/**
	 * @param data_type
	 * @param def
	 * @return return#function.id
	 * @throws Exception
	 */
	public static SymbolExpression identifier(CType data_type, CirFunctionDefinition def) throws Exception {
		return factory.new_identifier(data_type, def);
	}
	/**
	 * @param data_type
	 * @param def
	 * @return special name
	 * @throws Exception
	 */
	public static SymbolExpression identifier(CType data_type, String identifier) throws Exception {
		return factory.new_identifier(data_type, identifier);
	}
	/**
	 * @param value 
	 * @return
	 * @throws Exception
	 */
	public static SymbolConstant constant(Object source) throws Exception {
		if(source instanceof Boolean) {
			return factory.new_constant(((Boolean) source).booleanValue());
		}
		else if(source instanceof Character) {
			return factory.new_constant(((Character) source).charValue());
		}
		else if(source instanceof Short) {
			return factory.new_constant(((Short) source).shortValue());
		}
		else if(source instanceof Integer) {
			return factory.new_constant(((Integer) source).intValue());
		}
		else if(source instanceof Long) {
			return factory.new_constant(((Long) source).longValue());
		}
		else if(source instanceof Float) {
			return factory.new_constant(((Float) source).floatValue());
		}
		else if(source instanceof Double) {
			return factory.new_constant(((Double) source).doubleValue());
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	/**
	 * @param literal
	 * @return
	 * @throws Exception
	 */
	public static SymbolExpression literal(String literal) throws Exception {
		return factory.new_literal(literal);
	}
	/**
	 * @param elements
	 * @return
	 * @throws Exception
	 */
	public static SymbolExpression initializer_list(Iterable<Object> elements) throws Exception {
		return factory.new_initializer_list(elements);
	}
	public static SymbolExpression field_expression(Object body, String field) throws Exception {
		return factory.new_field_expression(body, field);
	}
	public static SymbolExpression call_expression(Object function, Iterable<Object> arguments) throws Exception {
		return factory.new_call_expression(function, arguments);
	}
	public static SymbolExpression arith_neg(Object operand) throws Exception {
		return factory.new_arith_neg(operand);
	}
	public static SymbolExpression bitws_rsv(Object operand) throws Exception {
		return factory.new_bitws_rsv(operand);
	}
	public static SymbolExpression logic_not(Object operand) throws Exception {
		return factory.new_logic_not(operand);
	}
	public static SymbolExpression address_of(Object operand) throws Exception {
		return factory.new_address_of(operand);
	}
	public static SymbolExpression dereference(Object operand) throws Exception {
		return factory.new_dereference(operand);
	}
	public static SymbolExpression cast_expression(CType data_type, Object operand) throws Exception {
		return factory.new_type_casting(data_type, operand);
	}
	public static SymbolExpression arith_add(CType data_type, Object loperand, Object roperand) throws Exception {
		return factory.new_arith_add(data_type, loperand, roperand);
	}
	public static SymbolExpression arith_sub(CType data_type, Object loperand, Object roperand) throws Exception {
		return factory.new_arith_sub(data_type, loperand, roperand);
	}
	public static SymbolExpression arith_mul(CType data_type, Object loperand, Object roperand) throws Exception {
		return factory.new_arith_mul(data_type, loperand, roperand);
	}
	public static SymbolExpression arith_div(CType data_type, Object loperand, Object roperand) throws Exception {
		return factory.new_arith_div(data_type, loperand, roperand);
	}
	public static SymbolExpression arith_mod(CType data_type, Object loperand, Object roperand) throws Exception {
		return factory.new_arith_mod(data_type, loperand, roperand);
	}
	public static SymbolExpression bitws_and(CType data_type, Object loperand, Object roperand) throws Exception {
		return factory.new_bitws_and(data_type, loperand, roperand);
	}
	public static SymbolExpression bitws_ior(CType data_type, Object loperand, Object roperand) throws Exception {
		return factory.new_bitws_ior(data_type, loperand, roperand);
	}
	public static SymbolExpression bitws_xor(CType data_type, Object loperand, Object roperand) throws Exception {
		return factory.new_bitws_xor(data_type, loperand, roperand);
	}
	public static SymbolExpression bitws_lsh(CType data_type, Object loperand, Object roperand) throws Exception {
		return factory.new_bitws_lsh(data_type, loperand, roperand);
	}
	public static SymbolExpression bitws_rsh(CType data_type, Object loperand, Object roperand) throws Exception {
		return factory.new_bitws_rsh(data_type, loperand, roperand);
	}
	public static SymbolExpression logic_and(Object loperand, Object roperand) throws Exception {
		return factory.new_logic_and(loperand, roperand);
	}
	public static SymbolExpression logic_ior(Object loperand, Object roperand) throws Exception {
		return factory.new_logic_ior(loperand, roperand);
	}
	public static SymbolExpression greater_tn(Object loperand, Object roperand) throws Exception {
		return factory.new_greater_tn(loperand, roperand);
	}
	public static SymbolExpression greater_eq(Object loperand, Object roperand) throws Exception {
		return factory.new_greater_eq(loperand, roperand);
	}
	public static SymbolExpression smaller_tn(Object loperand, Object roperand) throws Exception {
		return factory.new_smaller_tn(loperand, roperand);
	}
	public static SymbolExpression smaller_eq(Object loperand, Object roperand) throws Exception {
		return factory.new_smaller_eq(loperand, roperand);
	}
	public static SymbolExpression equal_with(Object loperand, Object roperand) throws Exception {
		return factory.new_equal_with(loperand, roperand);
	}
	public static SymbolExpression not_equals(Object loperand, Object roperand) throws Exception {
		return factory.new_not_equals(loperand, roperand);
	}
	
}
