package com.jcsa.jcparse.lang.symb;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerList;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArgumentList;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArrayExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCastExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCommaExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConditionalExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFunCallExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstParanthExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstSizeofExpression;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CFunctionType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.CUnionType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirConstExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirDefaultValue;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirInitializerBody;
import com.jcsa.jcparse.lang.irlang.expr.CirNameExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirStringLiteral;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.scope.CEnumeratorName;
import com.jcsa.jcparse.lang.scope.CInstanceName;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CParameterName;

/**
 * It provides interface to parse from AstNode or CirNode.
 * 
 * @author yukimula
 *
 */
public class SymParser {
	
	/* definitions */
	private CRunTemplate sizeof_template;
	private SymParser() { }
	private static final SymParser parser = new SymParser();
	
	/* parsed from AstNode */
	private SymExpression parse_ast(AstExpression source) throws Exception {
		SymExpression target;
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(source instanceof AstIdExpression)
			target = this.parse_id_expression((AstIdExpression) source);
		else if(source instanceof AstConstant)
			target = this.parse_constant((AstConstant) source);
		else if(source instanceof AstLiteral)
			target = this.parse_literal((AstLiteral) source);
		else if(source instanceof AstArrayExpression)
			target = this.parse_array_expression((AstArrayExpression) source);
		else if(source instanceof AstCastExpression)
			target = this.parse_cast_expression((AstCastExpression) source);
		else if(source instanceof AstCommaExpression)
			target = this.parse_comma_expression((AstCommaExpression) source);
		else if(source instanceof AstConstExpression)
			target = this.parse_const_expression((AstConstExpression) source);
		else if(source instanceof AstParanthExpression)
			target = this.parse_paranth_expression((AstParanthExpression) source);
		else if(source instanceof AstFunCallExpression)
			target = this.parse_call_expression((AstFunCallExpression) source);
		else if(source instanceof AstFieldExpression)
			target = this.parse_field_expression((AstFieldExpression) source);
		else if(source instanceof AstInitializerBody)
			target = this.parse_initializer_body((AstInitializerBody) source);
		else if(source instanceof AstSizeofExpression)
			target = this.parse_sizeof_expression((AstSizeofExpression) source);
		else if(source instanceof AstPostfixExpression)
			target = this.parse_postfix_expression((AstPostfixExpression) source);
		else if(source instanceof AstUnaryExpression)
			target = this.parse_unary_expression((AstUnaryExpression) source);
		else if(source instanceof AstBinaryExpression)
			target = this.parse_binary_expression((AstBinaryExpression) source);
		else if(source instanceof AstConditionalExpression)
			target = this.parse_conditional_expression((AstConditionalExpression) source);
		else 
			throw new IllegalArgumentException("Unsupport: " + source);
		target.set_source(source);
		return target;
	}
	private SymExpression parse_id_expression(AstIdExpression source) throws Exception {
		CName cname = source.get_cname();
		if(cname instanceof CInstanceName) {
			CType data_type = ((CInstanceName) cname).get_instance().get_type();
			String name = cname.get_name() + "#" + cname.get_scope().hashCode();
			return new SymIdentifier(data_type, name);
		}
		else if(cname instanceof CParameterName) {
			CType data_type = ((CParameterName) cname).get_parameter().get_type();
			String name = cname.get_name() + "#" + cname.get_scope().hashCode();
			return new SymIdentifier(data_type, name);
		}
		else if(cname instanceof CEnumeratorName) {
			int value = ((CEnumeratorName) cname).get_enumerator().get_value();
			CConstant constant = new CConstant(); constant.set_int(value);
			return new SymConstant(CBasicTypeImpl.int_type, constant);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + cname.get_name());
		}
	}
	private SymExpression parse_constant(AstConstant source) throws Exception {
		return new SymConstant(source.get_value_type(), source.get_constant());
	}
	private SymExpression parse_literal(AstLiteral source) throws Exception {
		return new SymLiteral(source.get_value_type(), source.get_literal());
	}
	private SymExpression parse_array_expression(AstArrayExpression source) throws Exception {
		SymExpression base = this.parse_ast(source.get_array_expression());
		SymExpression bias = this.parse_ast(source.get_dimension_expression());
		SymExpression addr = SymFactory.arith_add(base.get_data_type(), base, bias);
		return SymFactory.dereference(source.get_value_type(), addr);
	}
	private SymExpression parse_cast_expression(AstCastExpression source) throws Exception {
		SymExpression operand = this.parse_ast(source.get_expression());
		return SymFactory.type_cast(source.get_value_type(), operand);
	}
	private SymExpression parse_comma_expression(AstCommaExpression source) throws Exception {
		int index = source.number_of_arguments() - 1;
		return this.parse_ast(source.get_expression(index));
	}
	private SymExpression parse_const_expression(AstConstExpression source) throws Exception {
		return this.parse_ast(source.get_expression());
	}
	private SymExpression parse_paranth_expression(AstParanthExpression source) throws Exception {
		return this.parse_ast(source.get_sub_expression());
	}
	private SymExpression parse_call_expression(AstFunCallExpression source) throws Exception {
		SymExpression function = this.parse_ast(source.get_function());
		List<Object> arguments = new ArrayList<Object>();
		if(source.has_argument_list()) {
			AstArgumentList alist = source.get_argument_list();
			for(int k = 0; k < alist.number_of_arguments(); k++) {
				arguments.add(this.parse_ast(alist.get_argument(k)));
			}
		}
		return SymFactory.call_expression(source.get_value_type(), function, arguments);
	}
	private SymExpression parse_field_expression(AstFieldExpression source) throws Exception {
		SymExpression body = this.parse_ast(source.get_body());
		return SymFactory.field_expression(source.get_value_type(), body, source.get_field().get_name());
	}
	private SymExpression parse_initializer_body(AstInitializerBody source) throws Exception {
		List<Object> elements = new ArrayList<Object>();
		AstInitializerList list = source.get_initializer_list();
		for(int k = 0; k < list.number_of_initializer(); k++) {
			AstInitializer ik = list.get_initializer(k).get_initializer();
			if(ik.is_body())
				elements.add(this.parse_ast(ik.get_body()));
			else
				elements.add(this.parse_ast(ik.get_expression()));
		}
		return SymFactory.initializer_list(source.get_value_type(), elements);
	}
	private SymExpression parse_sizeof_expression(AstSizeofExpression source) throws Exception {
		CType data_type;
		if(source.is_expression())
			data_type = source.get_expression().get_value_type();
		else
			data_type = source.get_typename().get_type();
		data_type = CTypeAnalyzer.get_value_type(data_type);
		if(this.sizeof_template == null) {
			return SymFactory.new_identifier(source.
					get_value_type(), SymIdentifier.AnyPosInteger);
		}
		else {
			int value = this.sizeof_template.sizeof(data_type);
			return SymFactory.new_constant(Integer.valueOf(value));
		}
	}
	private SymExpression parse_postfix_expression(AstPostfixExpression source) throws Exception {
		switch(source.get_operator().get_operator()) {
		case increment:
		case decrement:	return this.parse_ast(source.get_operand());
		default: throw new IllegalArgumentException(source.generate_code());
		}
	}
	private SymExpression parse_unary_expression(AstUnaryExpression source) throws Exception {
		SymExpression operand = this.parse_ast(source.get_operand());
		switch(source.get_operator().get_operator()) {
		case positive:		return operand;
		case negative:		return SymFactory.arith_neg(source.get_value_type(), operand);
		case bit_not:		return SymFactory.bitws_rsv(source.get_value_type(), operand);
		case logic_not:		return SymFactory.logic_not(operand);
		case address_of:	return SymFactory.address_of(source.get_value_type(), operand);
		case dereference:	return SymFactory.dereference(source.get_value_type(), operand);
		case increment:		return SymFactory.arith_add(source.get_value_type(), operand, Integer.valueOf(1));
		case decrement:		return SymFactory.arith_sub(source.get_value_type(), operand, Integer.valueOf(1));
		default: throw new IllegalArgumentException(source.generate_code());
		}
	}
	private SymExpression parse_binary_expression(AstBinaryExpression source) throws Exception {
		SymExpression loperand = this.parse_ast(source.get_loperand());
		SymExpression roperand = this.parse_ast(source.get_roperand());
		COperator operator = source.get_operator().get_operator();
		
		switch(operator) {
		case arith_add_assign:
		case arith_sub_assign:
		case arith_mul_assign:
		case arith_div_assign:
		case arith_mod_assign:
		case bit_and_assign:
		case bit_or_assign:
		case bit_xor_assign:
		case left_shift_assign:
		case righ_shift_assign:
		{
			String code = operator.toString();
			code = code.substring(0, code.length() - 7);
			operator = COperator.valueOf(code.strip());
		}
		default:		break;
		}
		
		CType type = source.get_value_type();
		switch(operator) {
		case arith_add:		return SymFactory.arith_add(type, loperand, roperand);
		case arith_sub:		return SymFactory.arith_sub(type, loperand, roperand);
		case arith_mul:		return SymFactory.arith_mul(type, loperand, roperand);
		case arith_div:		return SymFactory.arith_div(type, loperand, roperand);
		case arith_mod:		return SymFactory.arith_mod(type, loperand, roperand);
		case bit_and:		return SymFactory.bitws_and(type, loperand, roperand);
		case bit_or:		return SymFactory.bitws_ior(type, loperand, roperand);
		case bit_xor:		return SymFactory.bitws_xor(type, loperand, roperand);
		case left_shift:	return SymFactory.bitws_lsh(type, loperand, roperand);
		case righ_shift:	return SymFactory.bitws_rsh(type, loperand, roperand);
		case logic_and:		return SymFactory.logic_and(loperand, roperand);
		case logic_or:		return SymFactory.logic_ior(loperand, roperand);
		case greater_tn:	return SymFactory.greater_tn(loperand, roperand);
		case greater_eq:	return SymFactory.greater_eq(loperand, roperand);
		case smaller_tn:	return SymFactory.smaller_tn(loperand, roperand);
		case smaller_eq:	return SymFactory.smaller_eq(loperand, roperand);
		case equal_with:	return SymFactory.equal_with(loperand, roperand);
		case not_equals:	return SymFactory.not_equals(loperand, roperand);
		case assign:		return roperand;
		default:	throw new IllegalArgumentException("Invalid operator");
		}
	}
	private SymExpression parse_conditional_expression(AstConditionalExpression source) throws Exception {
		SymExpression condition = this.parse_ast(source.get_condition());
		SymExpression toperand = this.parse_ast(source.get_true_branch());
		SymExpression foperand = this.parse_ast(source.get_false_branch());
		SymExpression ncondition = SymFactory.logic_not(condition);
		
		SymExpression loperand = SymFactory.
				arith_mul(source.get_value_type(), condition, toperand);
		SymExpression roperand = SymFactory.
				arith_mul(source.get_value_type(), ncondition, foperand);
		return SymFactory.arith_add(source.get_value_type(), loperand, roperand);
	}
	
	/* parsed from CirNode */
	private SymExpression parse_cir(CirExpression source) throws Exception {
		SymExpression target;
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(source instanceof CirNameExpression)
			target = this.parse_name_expression((CirNameExpression) source);
		else if(source instanceof CirConstExpression)
			target = this.parse_constant((CirConstExpression) source);
		else if(source instanceof CirStringLiteral)
			target = this.parse_string_literal((CirStringLiteral) source);
		else if(source instanceof CirFieldExpression)
			target = this.parse_field_expression((CirFieldExpression) source);
		else if(source instanceof CirDeferExpression)
			target = this.parse_defer_expression((CirDeferExpression) source);
		else if(source instanceof CirAddressExpression)
			target = this.parse_address_expression((CirAddressExpression) source);
		else if(source instanceof CirCastExpression)
			target = this.parse_cast_expression((CirCastExpression) source);
		else if(source instanceof CirComputeExpression)
			target = this.parse_compute_expression((CirComputeExpression) source);
		else if(source instanceof CirDefaultValue)
			target = this.parse_default_value((CirDefaultValue) source);
		else if(source instanceof CirInitializerBody)
			target = this.parse_initializer_body((CirInitializerBody) source);
		else if(source instanceof CirWaitExpression)
			target = this.parse_wait_expression((CirWaitExpression) source);
		else
			throw new IllegalArgumentException("Unsupport: " + source);
		target.set_source(source);
		return target;
	}
	private SymExpression parse_name_expression(CirNameExpression source) throws Exception {
		return SymFactory.new_identifier(source.get_data_type(), source.get_unique_name());
	}
	private SymExpression parse_constant(CirConstExpression source) throws Exception {
		return SymFactory.new_constant(source.get_constant());
	}
	private SymExpression parse_string_literal(CirStringLiteral source) throws Exception {
		return SymFactory.new_literal(source.get_data_type(), source.get_literal());
	}
	private SymExpression parse_field_expression(CirFieldExpression source) throws Exception {
		SymExpression body = this.parse_cir(source.get_body());
		return SymFactory.field_expression(source.
				get_data_type(), body, source.get_field().get_name());
	}
	private SymExpression parse_defer_expression(CirDeferExpression source) throws Exception {
		SymExpression operand = this.parse_cir(source.get_address());
		return SymFactory.dereference(source.get_data_type(), operand);
	}
	private SymExpression parse_address_expression(CirAddressExpression source) throws Exception {
		SymExpression operand = this.parse_cir(source.get_operand());
		return SymFactory.address_of(source.get_data_type(), operand);
	}
	private SymExpression parse_cast_expression(CirCastExpression source) throws Exception {
		SymExpression operand = this.parse_cir(source.get_operand());
		return SymFactory.type_cast(source.get_data_type(), operand);
	}
	private SymExpression parse_compute_expression(CirComputeExpression source) throws Exception {
		CType type = source.get_data_type();
		SymExpression operand = this.parse_cir(source.get_operand(0));
		SymExpression loperand = operand, roperand = null;
		if(source.number_of_operand() == 2) {
			roperand = this.parse_cir(source.get_operand(1));
		}
		
		switch(source.get_operator()) {
		case negative:		return SymFactory.arith_neg(type, operand);
		case bit_not:		return SymFactory.bitws_rsv(type, operand);
		case logic_not:		return SymFactory.logic_not(operand);
		case arith_add:		return SymFactory.arith_add(type, loperand, roperand);
		case arith_sub:		return SymFactory.arith_sub(type, loperand, roperand);
		case arith_mul:		return SymFactory.arith_mul(type, loperand, roperand);
		case arith_div:		return SymFactory.arith_div(type, loperand, roperand);
		case arith_mod:		return SymFactory.arith_mod(type, loperand, roperand);
		case bit_and:		return SymFactory.bitws_and(type, loperand, roperand);
		case bit_or:		return SymFactory.bitws_ior(type, loperand, roperand);
		case bit_xor:		return SymFactory.bitws_xor(type, loperand, roperand);
		case left_shift:	return SymFactory.bitws_lsh(type, loperand, roperand);
		case righ_shift:	return SymFactory.bitws_rsh(type, loperand, roperand);
		case logic_and:		return SymFactory.logic_and(loperand, roperand);
		case logic_or:		return SymFactory.logic_ior(loperand, roperand);
		case greater_tn:	return SymFactory.greater_tn(loperand, roperand);
		case greater_eq:	return SymFactory.greater_eq(loperand, roperand);
		case smaller_tn:	return SymFactory.smaller_tn(loperand, roperand);
		case smaller_eq:	return SymFactory.smaller_eq(loperand, roperand);
		case equal_with:	return SymFactory.equal_with(loperand, roperand);
		case not_equals:	return SymFactory.not_equals(loperand, roperand);
		default: throw new IllegalArgumentException(source.generate_code(true));
		}
	}
	private SymExpression parse_default_value(CirDefaultValue source) throws Exception {
		CType type = CTypeAnalyzer.get_value_type(source.get_data_type());
		if(type == null) {
			return SymFactory.new_identifier(
					CBasicTypeImpl.void_type, SymIdentifier.AnySequence);
		}
		else if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	return SymFactory.new_identifier(type, SymIdentifier.AnyBoolean);
			case c_char:	
			case c_uchar:	return SymFactory.new_identifier(type, SymIdentifier.AnyCharacter);
			case c_short:
			case c_int:
			case c_long:
			case c_llong:	return SymFactory.new_identifier(type, SymIdentifier.AnyInteger);
			case c_ushort:
			case c_uint:
			case c_ulong:
			case c_ullong:	return SymFactory.new_identifier(type, SymIdentifier.AnyPosInteger);
			case c_float:
			case c_double:
			case c_ldouble:	return SymFactory.new_identifier(type, SymIdentifier.AnyReal);
			default: 		return SymFactory.new_identifier(type, SymIdentifier.AnySequence);
			}
		}
		else if(type instanceof CArrayType
				|| type instanceof CPointerType
				|| type instanceof CFunctionType) {
			return SymFactory.new_identifier(type, SymIdentifier.AnyAddress);
		}
		else if(type instanceof CStructType
				|| type instanceof CUnionType) {
			return SymFactory.new_identifier(type, SymIdentifier.AnySequence);
		}
		else if(type instanceof CEnumType) {
			return SymFactory.new_identifier(type, SymIdentifier.AnyInteger);
		}
		else {
			throw new IllegalArgumentException(type.generate_code());
		}
	}
	private SymExpression parse_initializer_body(CirInitializerBody source) throws Exception {
		List<Object> elements = new ArrayList<Object>();
		for(int k = 0; k < source.number_of_elements(); k++) {
			elements.add(this.parse_cir(source.get_element(k)));
		}
		return SymFactory.initializer_list(source.get_data_type(), elements);
	}
	private SymExpression parse_wait_expression(CirWaitExpression source) throws Exception {
		CirStatement wait_statement = source.statement_of();
		CirExecution wait_execution = wait_statement.get_tree().get_function_call_graph().
				get_function(wait_statement).get_flow_graph().get_execution(wait_statement);
		CirExecution call_execution = wait_execution.get_graph().get_execution(wait_execution.get_id() - 1);
		CirCallStatement call_statement = (CirCallStatement) call_execution.get_statement();
		
		SymExpression function = this.parse_cir(call_statement.get_function());
		List<Object> arguments = new ArrayList<Object>();
		CirArgumentList list = call_statement.get_arguments();
		for(int k = 0; k < list.number_of_arguments(); k++) {
			arguments.add(this.parse_cir(list.get_argument(k)));
		}
		return SymFactory.call_expression(source.get_data_type(), function, arguments);
	}
	
	/* parsing methods */
	public static SymExpression parse(AstExpression source, CRunTemplate sizeof_template) throws Exception {
		parser.sizeof_template = sizeof_template;
		return parser.parse_ast(source);
	}
	public static SymExpression parse(CirExpression source) throws Exception {
		return parser.parse_cir(source);
	}
	
}
