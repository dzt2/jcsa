package com.jcsa.jcmutest.selang.util;

import com.jcsa.jcmutest.selang.lang.SedNode;
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
import com.jcsa.jcmutest.selang.lang.tokn.SedArgumentList;
import com.jcsa.jcmutest.selang.lang.tokn.SedField;
import com.jcsa.jcmutest.selang.lang.tokn.SedStatement;
import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstFieldInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerList;
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
import com.jcsa.jcparse.lang.astree.expr.othr.AstField;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFunCallExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstParanthExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstSizeofExpression;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirConstExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirDefaultValue;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirField;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirInitializerBody;
import com.jcsa.jcparse.lang.irlang.expr.CirNameExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirStringLiteral;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabel;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.lexical.CPunctuator;
import com.jcsa.jcparse.lang.scope.CEnumeratorName;
import com.jcsa.jcparse.lang.scope.CInstanceName;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CParameterName;

/**
 * It provides interfaces to parse the SedNode from other objects.
 * 
 * @author yukimula
 *
 */
class SedParser {
	
	/* definitions */
	private CRunTemplate sizeof_template;
	private SedParser() { this.sizeof_template = null; }
	private static final SedParser parser = new SedParser();
	
	/* basic methods */
	private CType get_pointed_type(CType type) throws Exception {
		type = CTypeAnalyzer.get_value_type(type);
		if(type instanceof CPointerType) {
			return ((CPointerType) type).get_pointed_type();
		}
		else if(type instanceof CArrayType) {
			return ((CArrayType) type).get_element_type();
		}
		else {
			throw new IllegalArgumentException("Not a pointer type");
		}
	}
	private CirExecution get_execution(CirStatement location) throws Exception {
		return location.get_tree().get_function_call_graph().get_function(location).get_flow_graph().get_execution(location);
	}
	
	/* AstNode parse */
	private SedNode parse_ast(AstNode source) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(source instanceof AstIdExpression)
			return this.parse_ast_id_expression((AstIdExpression) source);
		else if(source instanceof AstConstant)
			return this.parse_ast_constant((AstConstant) source);
		else if(source instanceof AstLiteral)
			return this.parse_ast_literal((AstLiteral) source);
		else if(source instanceof AstUnaryExpression)
			return this.parse_ast_unary_expression((AstUnaryExpression) source);
		else if(source instanceof AstBinaryExpression)
			return this.parse_ast_binary_expression((AstBinaryExpression) source);
		else if(source instanceof AstPostfixExpression)
			return this.parse_ast_postfix_expression((AstPostfixExpression) source);
		else if(source instanceof AstArrayExpression)
			return this.parse_ast_array_expression((AstArrayExpression) source);
		else if(source instanceof AstCastExpression)
			return this.parse_ast_cast_expression((AstCastExpression) source);
		else if(source instanceof AstCommaExpression)
			return this.parse_ast_comma_expression((AstCommaExpression) source);
		else if(source instanceof AstConstExpression)
			return this.parse_ast_const_expression((AstConstExpression) source);
		else if(source instanceof AstParanthExpression)
			return this.parse_ast_paranth_expression((AstParanthExpression) source);
		else if(source instanceof AstField)
			return this.parse_ast_field((AstField) source);
		else if(source instanceof AstFieldExpression)
			return this.parse_ast_field_expression((AstFieldExpression) source);
		else if(source instanceof AstSizeofExpression)
			return this.parse_ast_sizeof_expression((AstSizeofExpression) source);
		else if(source instanceof AstConditionalExpression)
			return this.parse_ast_conditional_expression((AstConditionalExpression) source);
		else if(source instanceof AstFunCallExpression)
			return this.parse_ast_fun_call_expression((AstFunCallExpression) source);
		else if(source instanceof AstArgumentList)
			return this.parse_ast_argument_list((AstArgumentList) source);
		else if(source instanceof AstInitializer)
			return this.parse_ast_initializer((AstInitializer) source);
		else if(source instanceof AstInitializerBody)
			return this.parse_ast_initializer_body((AstInitializerBody) source);
		else if(source instanceof AstInitializerList)
			return this.parse_ast_initializer_list((AstInitializerList) source);
		else if(source instanceof AstFieldInitializer)
			return this.parse_ast_field_initializer((AstFieldInitializer) source);
		else
			throw new IllegalArgumentException("Unsupport: " + source);
	}
	private SedNode parse_ast_id_expression(AstIdExpression source) throws Exception {
		CName cname = source.get_cname();
		if(cname instanceof CInstanceName || cname instanceof CParameterName) {
			return new SedIdExpression(null, source.get_value_type(),
					cname.get_name() + "#" + cname.get_scope().hashCode());
		}
		else if(cname instanceof CEnumeratorName) {
			CConstant constant = new CConstant();
			constant.set_int(((CEnumeratorName) cname).get_enumerator().get_value());
			return new SedConstant(null, source.get_value_type(), constant);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + cname.getClass());
		}
 	}
	private SedNode parse_ast_constant(AstConstant source) throws Exception {
		return new SedConstant(null, source.get_value_type(), source.get_constant());
	}
	private SedNode parse_ast_literal(AstLiteral source) throws Exception {
		return new SedLiteral(null, source.get_value_type(), source.get_literal());
	}
	private SedNode parse_ast_unary_expression(AstUnaryExpression source) throws Exception {
		SedNode operand = this.parse_ast(source.get_operand());
		switch(source.get_operator().get_operator()) {
		case positive:		return operand;
		case negative:
		case bit_not:
		case logic_not:
		case address_of:
		case dereference:
		{
			SedUnaryExpression expr = new SedUnaryExpression(null, source.
					get_value_type(), source.get_operator().get_operator());
			expr.add_child(operand);
			return expr;
		}
		case increment:
		{
			SedBinaryExpression expr = new SedBinaryExpression(null, 
					source.get_value_type(), COperator.arith_add);
			expr.add_child(operand);
			CConstant one = new CConstant();
			one.set_int(1);
			expr.add_child(new SedConstant(null, CBasicTypeImpl.int_type, one));
			return expr;
		}
		case decrement:
		{
			SedBinaryExpression expr = new SedBinaryExpression(null, 
					source.get_value_type(), COperator.arith_sub);
			expr.add_child(operand);
			CConstant one = new CConstant();
			one.set_int(1);
			expr.add_child(new SedConstant(null, CBasicTypeImpl.int_type, one));
			return expr;
		}
		default: throw new IllegalArgumentException("Invalid: " + source.generate_code());
		}
	}
	private SedNode parse_ast_binary_expression(AstBinaryExpression source) throws Exception {
		SedNode loperand = this.parse_ast(source.get_loperand());
		SedNode roperand = this.parse_ast(source.get_roperand());
		switch(source.get_operator().get_operator()) {
		case assign:	return roperand;
		case arith_add:
		case arith_sub:
		case arith_mul:
		case arith_div:
		case arith_mod:
		case bit_and:
		case bit_or:
		case bit_xor:
		case left_shift:
		case righ_shift:
		case logic_and:
		case logic_or:
		case greater_tn:
		case greater_eq:
		case smaller_tn:
		case smaller_eq:
		case equal_with:
		case not_equals:
		{
			SedBinaryExpression expr = new SedBinaryExpression(null, source.
					get_value_type(), source.get_operator().get_operator());
			expr.add_child(loperand); expr.add_child(roperand); return expr;
		}
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
			COperator operator = source.get_operator().get_operator();
			String opcode = operator.toString();
			opcode = opcode.substring(0, opcode.length() - 7);
			operator = COperator.valueOf(opcode);
			SedBinaryExpression expr = new SedBinaryExpression(
						null, source.get_value_type(), operator);
			expr.add_child(loperand); expr.add_child(roperand); 
			return expr;
		}
		default: throw new IllegalArgumentException("Invalid: " + source.generate_code());
		}
	}
	private SedNode parse_ast_array_expression(AstArrayExpression source) throws Exception {
		SedExpression loperand = (SedExpression) this.parse_ast(source.get_array_expression());
		SedExpression roperand = (SedExpression) this.parse_ast(source.get_dimension_expression());
		SedExpression address = new SedBinaryExpression(null, loperand.get_data_type(), COperator.arith_add);
		address.add_child(loperand); address.add_child(roperand);
		SedExpression expression = new SedUnaryExpression(null, source.get_value_type(), COperator.dereference);
		expression.add_child(address); return expression;
	}
	private SedNode parse_ast_cast_expression(AstCastExpression source) throws Exception {
		SedNode expression = new SedUnaryExpression(
				null, source.get_value_type(), COperator.assign);
		expression.add_child(this.parse_ast(source.get_expression()));
		return expression;
	}
	private SedNode parse_ast_comma_expression(AstCommaExpression source) throws Exception {
		int index = source.number_of_arguments() - 1;
		return this.parse_ast(source.get_expression(index));
	}
	private SedNode parse_ast_const_expression(AstConstExpression source) throws Exception {
		return this.parse_ast(source.get_expression());
	}
	private SedNode parse_ast_paranth_expression(AstParanthExpression source) throws Exception {
		return this.parse_ast(source.get_sub_expression());
	}
	private SedNode parse_ast_field(AstField source) throws Exception {
		return new SedField(source.get_name());
	}
	private SedNode parse_ast_field_expression(AstFieldExpression source) throws Exception {
		SedNode body = this.parse_ast(source.get_body());
		SedNode field = this.parse_ast(source.get_field());
		
		if(source.get_operator().get_punctuator() == CPunctuator.arrow) {
			SedNode new_body = new SedUnaryExpression(null, 
					this.get_pointed_type(source.get_body().get_value_type()),
					COperator.dereference);
			new_body.add_child(body);
			body = new_body;
		}
		
		SedFieldExpression expression = 
				new SedFieldExpression(null, source.get_value_type());
		expression.add_child(body); expression.add_child(field);
		return expression;
	}
	private SedNode parse_ast_initializer_body(AstInitializerBody source) throws Exception {
		return this.parse_ast(source.get_initializer_list());
	}
	private SedNode parse_ast_initializer_list(AstInitializerList source) throws Exception {
		SedInitializerList list = new SedInitializerList(null, CBasicTypeImpl.void_type);
		for(int k = 0; k < source.number_of_initializer(); k++) {
			list.add_child(this.parse_ast(source.get_initializer(k)));
		}
		return list;
	}
	private SedNode parse_ast_field_initializer(AstFieldInitializer source) throws Exception {
		return this.parse_ast(source.get_initializer());
	}
	private SedNode parse_ast_initializer(AstInitializer source) throws Exception {
		if(source.is_body()) {
			return this.parse_ast(source.get_body());
		}
		else {
			return this.parse_ast(source.get_expression());
		}
	}
	private SedNode parse_ast_sizeof_expression(AstSizeofExpression source) throws Exception {
		if(this.sizeof_template != null) {
			CType type;
			if(source.is_expression()) {
				type = source.get_expression().get_value_type();
			}
			else {
				type = source.get_typename().get_type();
			}
			CConstant constant = new CConstant();
			constant.set_int(this.sizeof_template.sizeof(type));
			return new SedConstant(null, source.get_value_type(), constant);
		}
		else {
			return new SedDefaultValue(null, source.get_value_type(), SedDefaultValue.AnyPosInteger);
		}
	}
	private SedNode parse_ast_conditional_expression(AstConditionalExpression source) throws Exception {
		SedNode condition = this.parse_ast(source.get_condition());
		SedNode toperand = this.parse_ast(source.get_true_branch());
		SedNode foperand = this.parse_ast(source.get_false_branch());
		SedNode ncondition = new SedUnaryExpression(null, 
				CBasicTypeImpl.bool_type, COperator.logic_not);
		ncondition.add_child(condition);
		
		SedNode loperand = new SedBinaryExpression(
				null, source.get_value_type(), COperator.arith_mul);
		loperand.add_child(condition); loperand.add_child(toperand);
		
		SedNode roperand = new SedBinaryExpression(
				null, source.get_value_type(), COperator.arith_mul);
		loperand.add_child(ncondition); loperand.add_child(foperand);
		
		SedNode expression = new SedBinaryExpression(null, 
				source.get_value_type(), COperator.arith_add);
		expression.add_child(loperand); expression.add_child(roperand);
		return expression;
	}
	private SedNode parse_ast_argument_list(AstArgumentList source) throws Exception {
		SedArgumentList list = new SedArgumentList();
		for(int k = 0; k < source.number_of_arguments(); k++) {
			list.add_child(this.parse_ast(source.get_argument(k)));
		}
		return list;
	}
	private SedNode parse_ast_fun_call_expression(AstFunCallExpression source) throws Exception {
		SedNode function = this.parse_ast(source.get_function());
		SedNode argument_list;
		if(source.has_argument_list()) {
			argument_list = this.parse_ast(source.get_argument_list());
		}
		else {
			argument_list = new SedArgumentList();
		}
		SedNode expression = new SedCallExpression(null, source.get_value_type());
		expression.add_child(function); expression.add_child(argument_list);
		return expression;
	}
	private SedNode parse_ast_postfix_expression(AstPostfixExpression source) throws Exception {
		return this.parse_ast(source.get_operand());
	}
	protected static SedNode parse(AstNode source, CRunTemplate sizeof_template) throws Exception {
		parser.sizeof_template = sizeof_template;
		return parser.parse_ast(source);
	}
	
	/* CirNode parse */
	private SedNode parse_cir(CirNode source) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(source instanceof CirNameExpression)
			return this.parse_name_expression((CirNameExpression) source);
		else if(source instanceof CirConstExpression)
			return this.parse_constant((CirConstExpression) source);
		else if(source instanceof CirStringLiteral)
			return this.parse_literal((CirStringLiteral) source);
		else if(source instanceof CirDefaultValue)
			return this.parse_default_value((CirDefaultValue) source);
		else if(source instanceof CirDeferExpression)
			return this.parse_defer_expression((CirDeferExpression) source);
		else if(source instanceof CirField)
			return this.parse_field((CirField) source);
		else if(source instanceof CirFieldExpression)
			return this.parse_field_expression((CirFieldExpression) source);
		else if(source instanceof CirAddressExpression)
			return this.parse_address_expression((CirAddressExpression) source);
		else if(source instanceof CirCastExpression)
			return this.parse_cast_expression((CirCastExpression) source);
		else if(source instanceof CirInitializerBody)
			return this.parse_initializer_body((CirInitializerBody) source);
		else if(source instanceof CirComputeExpression)
			return this.parse_compute_expression((CirComputeExpression) source);
		else if(source instanceof CirWaitExpression)
			return this.parse_wait_expression((CirWaitExpression) source);
		else if(source instanceof CirArgumentList)
			return this.parse_argument_list((CirArgumentList) source);
		else if(source instanceof CirLabel)
			return this.parse_label((CirLabel) source);
		else if(source instanceof CirAssignStatement)
			return this.parse_assign_statement((CirAssignStatement) source);
		else if(source instanceof CirGotoStatement)
			return this.parse_goto_statement((CirGotoStatement) source);
		else if(source instanceof CirIfStatement)
			return this.parse_if_statement((CirIfStatement) source);
		else if(source instanceof CirCaseStatement)
			return this.parse_case_statement((CirCaseStatement) source);
		else if(source instanceof CirCallStatement)
			return this.parse_call_statement((CirCallStatement) source);
		else if(source instanceof CirTagStatement)
			return this.parse_tag_statement((CirTagStatement) source);
		else
			throw new IllegalArgumentException("Unsupport: " + source);
	}
	private SedNode parse_name_expression(CirNameExpression source) throws Exception {
		return new SedIdExpression(source, source.get_data_type(), source.get_unique_name());
	}
	private SedNode parse_constant(CirConstExpression source) throws Exception {
		return new SedConstant(source, source.get_data_type(), source.get_constant());
	}
	private SedNode parse_literal(CirStringLiteral source) throws Exception {
		return new SedLiteral(source, source.get_data_type(), source.get_literal());
	}
	private SedNode parse_default_value(CirDefaultValue source) throws Exception {
		String name;
		if(source.get_data_type() == null) {
			name = SedDefaultValue.AnySequence;
		}
		else {
			CType type = CTypeAnalyzer.get_value_type(source.get_data_type());
			if(CTypeAnalyzer.is_boolean(type)) 
				name = SedDefaultValue.AnyBoolean;
			else if(CTypeAnalyzer.is_character(type)) 
				name = SedDefaultValue.AnyCharacter;
			else if(CTypeAnalyzer.is_integer(type)) 
				name = SedDefaultValue.AnyInteger;
			else if(CTypeAnalyzer.is_real(type))
				name = SedDefaultValue.AnyDouble;
			else if(CTypeAnalyzer.is_pointer(type))
				name = SedDefaultValue.AnyAddress;
			else
				name = SedDefaultValue.AnySequence;
		}
		return new SedDefaultValue(source, source.get_data_type(), name);
	}
	private SedNode parse_defer_expression(CirDeferExpression source) throws Exception {
		SedUnaryExpression expr = new SedUnaryExpression(source, 
				source.get_data_type(), COperator.dereference);
		expr.add_child(this.parse_cir(source.get_address()));
		return expr;
	}
	private SedNode parse_field(CirField source) throws Exception {
		return new SedField(source.get_name());
	}
	private SedNode parse_field_expression(CirFieldExpression source) throws Exception {
		SedFieldExpression expr = new 
				SedFieldExpression(source, source.get_data_type());
		expr.add_child(this.parse_cir(source.get_body()));
		expr.add_child(this.parse_cir(source.get_field()));
		return expr;
	}
	private SedNode parse_address_expression(CirAddressExpression source) throws Exception {
		SedUnaryExpression expr = new SedUnaryExpression(source, 
				source.get_data_type(), COperator.address_of);
		expr.add_child(this.parse_cir(source.get_operand()));
		return expr;
	}
	private SedNode parse_cast_expression(CirCastExpression source) throws Exception {
		SedUnaryExpression expr = new SedUnaryExpression(source, 
				source.get_data_type(), COperator.assign);
		expr.add_child(this.parse_cir(source.get_operand()));
		return expr;
	}
	private SedNode parse_initializer_body(CirInitializerBody source) throws Exception {
		SedNode list = new SedInitializerList(source, CBasicTypeImpl.void_type);
		for(int k = 0; k < source.number_of_elements(); k++) {
			list.add_child(this.parse_cir(source.get_element(k)));
		}
		return list;
	}
	private SedNode parse_compute_expression(CirComputeExpression source) throws Exception {
		switch(source.get_operator()) {
		case negative:
		case bit_not:
		case logic_not:
		{
			SedUnaryExpression expr = new SedUnaryExpression(source, 
					source.get_data_type(), source.get_operator());
			expr.add_child(this.parse_cir(source.get_operand(0)));
			return expr;
		}
		case arith_add:
		case arith_sub:
		case arith_mul:
		case arith_div:
		case arith_mod:
		case bit_and:
		case bit_or:
		case bit_xor:
		case left_shift:
		case righ_shift:
		case logic_and:
		case logic_or:
		case greater_tn:
		case greater_eq:
		case smaller_tn:
		case smaller_eq:
		case equal_with:
		case not_equals:
		{
			SedBinaryExpression expr = new SedBinaryExpression(source,
					source.get_data_type(), source.get_operator());
			expr.add_child(this.parse_cir(source.get_operand(0)));
			expr.add_child(this.parse_cir(source.get_operand(1)));
			return expr;
		}
		default: throw new IllegalArgumentException("Invalid: " + source.generate_code(true));
		}
	}
	private SedNode parse_wait_expression(CirWaitExpression source) throws Exception {
		CirExecution wait_execution = this.get_execution(source.statement_of());
		CirExecution call_execution = wait_execution.get_graph().get_execution(wait_execution.get_id() - 1);
		CirCallStatement call_statement = (CirCallStatement) call_execution.get_statement();
		SedNode expression = new SedCallExpression(source, source.get_data_type());
		expression.add_child(this.parse_cir(call_statement.get_function()));
		expression.add_child(this.parse_cir(call_statement.get_arguments()));
		return expression;
	}
	private SedNode parse_argument_list(CirArgumentList source) throws Exception {
		SedArgumentList list = new SedArgumentList();
		for(int k = 0; k < source.number_of_arguments(); k++) {
			list.add_child(this.parse_cir(source.get_argument(k)));
		}
		return list;
	}
	private SedNode parse_label(CirLabel source) throws Exception {
		CirStatement statement = (CirStatement) source.
				get_tree().get_node(source.get_target_node_id());
		return new SedStatement(statement);
	}
	private SedNode parse_assign_statement(CirAssignStatement source) throws Exception {
		return new SedStatement(source);
	}
	private SedNode parse_goto_statement(CirGotoStatement source) throws Exception {
		return new SedStatement(source);
	}
	private SedNode parse_if_statement(CirIfStatement source) throws Exception {
		return new SedStatement(source);
	}
	private SedNode parse_case_statement(CirCaseStatement source) throws Exception {
		return new SedStatement(source);
	}
	private SedNode parse_call_statement(CirCallStatement source) throws Exception {
		return new SedStatement(source);
	}
	private SedNode parse_tag_statement(CirTagStatement source) throws Exception {
		return new SedStatement(source);
	}
	public static SedNode parse(CirNode source) throws Exception {
		return parser.parse_cir(source);
	}
	
}
