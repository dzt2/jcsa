package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.CSizeofBase;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator.DeclaratorProduction;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstFieldInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerList;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPointUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftBinaryExpression;
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
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.ctype.impl.CTypeFactory;
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
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.lexical.CPunctuator;
import com.jcsa.jcparse.lang.scope.CInstanceName;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CParameterName;

/**
 * To parse SymExpression from AstExpression or CirExpression.
 * @author yukimula
 *
 */
public class SymParser {
	
	/** singleton of the parser **/
	private static final SymParser parser = new SymParser();
	/** to create pointer type **/
	private CTypeFactory tfactory = new CTypeFactory();
	/** symbolic expression parser **/
	private SymParser() { }
	
	/* ast-parse */
	private SymNode parse(AstNode source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstIdExpression) {
			return this.parse_id_expression((AstIdExpression) source);
		}
		else if(source instanceof AstConstant) {
			return this.parse_constant((AstConstant) source);
		}
		else if(source instanceof AstLiteral) {
			return this.parse_literal((AstLiteral) source);
		}
		else if(source instanceof AstArrayExpression) {
			return this.parse_array_expression((AstArrayExpression) source);
		}
		else if(source instanceof AstCastExpression) {
			return this.parse_cast_expression((AstCastExpression) source);
		}
		else if(source instanceof AstCommaExpression) {
			return this.parse_comma_expression((AstCommaExpression) source);
		}
		else if(source instanceof AstConditionalExpression) {
			return this.parse_conditional_expression((AstConditionalExpression) source);
		}
		else if(source instanceof AstFieldExpression) {
			return this.parse_field_expression((AstFieldExpression) source);
		}
		else if(source instanceof AstField) {
			return this.parse_field((AstField) source);
		}
		else if(source instanceof AstFunCallExpression) {
			return this.parse_fun_call_expression((AstFunCallExpression) source);
		}
		else if(source instanceof AstArgumentList) {
			return this.parse_argument_list((AstArgumentList) source);
		}
		else if(source instanceof AstInitializer) {
			return this.parse_initializer((AstInitializer) source);
		}
		else if(source instanceof AstInitializerBody) {
			return this.parse_initializer_body((AstInitializerBody) source);
		}
		else if(source instanceof AstInitializerList) {
			return this.parse_initializer_list((AstInitializerList) source);
		}
		else if(source instanceof AstFieldInitializer) {
			return this.parse_field_initializer((AstFieldInitializer) source);
		}
		else if(source instanceof AstSizeofExpression) {
			return this.parse_sizeof_expression((AstSizeofExpression) source);
		}
		else if(source instanceof AstConstExpression) {
			return this.parse_const_expression((AstConstExpression) source);
		}
		else if(source instanceof AstParanthExpression) {
			return this.parse_paranth_expression((AstParanthExpression) source);
		}
		else if(source instanceof AstArithUnaryExpression) {
			return this.parse_arith_unary_expression((AstArithUnaryExpression) source);
		}
		else if(source instanceof AstBitwiseUnaryExpression) {
			return this.parse_bitws_unary_expression((AstBitwiseUnaryExpression) source);
		}
		else if(source instanceof AstLogicUnaryExpression) {
			return this.parse_logic_unary_expression((AstLogicUnaryExpression) source);
		}
		else if(source instanceof AstPointUnaryExpression) {
			return this.parse_point_unary_expression((AstPointUnaryExpression) source);
		}
		else if(source instanceof AstIncreUnaryExpression) {
			return this.parse_incre_unary_expression((AstIncreUnaryExpression) source);
		}
		else if(source instanceof AstIncrePostfixExpression) {
			return this.parse_incre_postfix_expression((AstIncrePostfixExpression) source);
		}
		else if(source instanceof AstArithBinaryExpression) {
			return this.parse_arith_binary_expression((AstArithBinaryExpression) source);
		}
		else if(source instanceof AstArithAssignExpression) {
			return this.parse_arith_assign_expression((AstArithAssignExpression) source);
		}
		else if(source instanceof AstBitwiseBinaryExpression) {
			return this.parse_bitws_binary_expression((AstBinaryExpression) source);
		}
		else if(source instanceof AstBitwiseAssignExpression) {
			return this.parse_bitws_assign_expression((AstBinaryExpression) source);
		}
		else if(source instanceof AstLogicBinaryExpression) {
			return this.parse_logic_binary_expression((AstLogicBinaryExpression) source);
		}
		else if(source instanceof AstRelationExpression) {
			return this.parse_relation_expression((AstRelationExpression) source);
		}
		else if(source instanceof AstShiftBinaryExpression) {
			return this.parse_bitws_binary_expression((AstBinaryExpression) source);
		}
		else if(source instanceof AstShiftAssignExpression) {
			return this.parse_bitws_assign_expression((AstBinaryExpression) source);
		}
		else if(source instanceof AstAssignExpression) {
			return this.parse_assign_expression((AstAssignExpression) source);
		}
		else if(source instanceof AstInitDeclarator) {
			return this.parse_init_declarator((AstInitDeclarator) source);
		}
		else if(source instanceof AstDeclarator) {
			return this.parse_declarator((AstDeclarator) source);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + source);
		}
	}
	private SymNode parse_id_expression(AstIdExpression source) throws Exception {
		return new SymIdentifier(source.get_value_type(), source.get_name());
	}
	private SymNode parse_constant(AstConstant source) throws Exception {
		return new SymConstant(source.get_constant());
	}
	private SymNode parse_literal(AstLiteral source) throws Exception {
		return new SymLiteral(source.get_value_type(), source.get_literal());
	}
	private SymNode parse_array_expression(AstArrayExpression source) throws Exception {
		SymNode loperand = this.parse(source.get_array_expression());
		SymNode roperand = this.parse(source.get_dimension_expression());
		SymNode address = new SymMultiExpression(
				this.tfactory.get_pointer_type(source.get_value_type()), 
				COperator.arith_add);
		address.add_child(loperand); address.add_child(roperand);
		SymNode reference = new SymUnaryExpression(source.get_value_type(), COperator.dereference);
		reference.add_child(address); return reference;
	}
	private SymNode parse_cast_expression(AstCastExpression source) throws Exception {
		SymNode operand = this.parse(source.get_expression());
		SymNode result = new SymUnaryExpression(source.get_value_type(), COperator.assign);
		result.add_child(operand); return result;
	}
	private SymNode parse_comma_expression(AstCommaExpression source) throws Exception {
		int n = source.number_of_arguments();
		return this.parse(source.get_expression(n - 1));
	}
	private SymNode parse_conditional_expression(AstConditionalExpression source) throws Exception {
		SymNode condition = this.parse(source.get_condition());
		SymNode toperand = this.parse(source.get_true_branch());
		SymNode foperand = this.parse(source.get_false_branch());
		
		SymNode tcondition = condition.clone();
		SymNode loperand = new SymMultiExpression(
				source.get_true_branch().get_value_type(), COperator.arith_mul);
		loperand.add_child(tcondition); loperand.add_child(toperand);
		
		SymNode ncondition = new SymUnaryExpression(CBasicTypeImpl.bool_type, COperator.logic_not);
		ncondition.add_child(condition.clone());
		SymNode roperand = new SymMultiExpression(
				source.get_false_branch().get_value_type(), COperator.arith_mul);
		roperand.add_child(ncondition); roperand.add_child(foperand);
		
		SymNode result = new SymMultiExpression(source.get_value_type(), COperator.arith_add);
		result.add_child(loperand); result.add_child(roperand); return result;
	}
	private CType get_pointed_type(CType data_type) throws Exception {
		data_type = CTypeAnalyzer.get_value_type(data_type);
		if(data_type instanceof CArrayType) {
			return ((CArrayType) data_type).get_element_type();
		}
		else if(data_type instanceof CPointerType) {
			return ((CPointerType) data_type).get_pointed_type();
		}
		else {
			throw new IllegalArgumentException("Invalid: " + data_type);
		}
	}
	private SymNode parse_field_expression(AstFieldExpression source) throws Exception {
		SymNode body = this.parse(source.get_body());
		SymNode field = this.parse(source.get_field());
		if(source.get_operator().get_punctuator() == CPunctuator.arrow) {
			SymNode address = body;
			body = new SymUnaryExpression(
					this.get_pointed_type(source.get_body().get_value_type()), 
					COperator.dereference);
			body.add_child(address);
		}
		SymNode result = new SymFieldExpression(source.get_value_type());
		result.add_child(body); result.add_child(field); return result;
	}
	private SymNode parse_field(AstField source) throws Exception {
		return new SymField(source.get_name());
	}
	private SymNode parse_fun_call_expression(AstFunCallExpression source) throws Exception {
		SymNode callee = this.parse(source.get_function()), arguments;
		if(source.has_argument_list()) {
			arguments = this.parse(source.get_argument_list());
		}
		else {
			arguments = new SymArgumentList();
		}
		SymNode result = new SymFunCallExpression(source.get_value_type());
		result.add_child(callee); result.add_child(arguments); return result;
	}
	private SymNode parse_argument_list(AstArgumentList source) throws Exception {
		SymNode arguments = new SymArgumentList();
		for(int k = 0; k < source.number_of_arguments(); k++) {
			SymNode argument = this.parse(source.get_argument(k));
			arguments.add_child(argument);
		}
		return arguments;
	}
	private SymNode parse_initializer_body(AstInitializerBody source) throws Exception {
		return this.parse(source.get_initializer_list());
	}
	private SymNode parse_initializer_list(AstInitializerList source) throws Exception {
		SymNode result = new SymInitializerList();
		for(int k = 0; k < source.number_of_initializer(); k++) {
			result.add_child(this.parse(source.get_initializer(k)));
		}
		return result;
	}
	private SymNode parse_field_initializer(AstFieldInitializer source) throws Exception {
		return this.parse(source.get_initializer());
	}
	private SymNode parse_initializer(AstInitializer source) throws Exception {
		if(source.is_body()) {
			return this.parse(source.get_body());
		}
		else {
			return this.parse(source.get_expression());
		}
	}
	private SymNode parse_sizeof_expression(AstSizeofExpression source) throws Exception {
		CType data_type = CTypeAnalyzer.get_value_type(source.get_value_type());
		int size = CSizeofBase.Sizeof(data_type);
		CConstant constant = new CConstant();
		constant.set_int(size);
		return new SymConstant(constant);
	}
	private SymNode parse_const_expression(AstConstExpression source) throws Exception {
		return this.parse(source.get_expression());
	}
	private SymNode parse_paranth_expression(AstParanthExpression source) throws Exception {
		return this.parse(source.get_sub_expression());
	}
	private SymNode parse_arith_unary_expression(AstArithUnaryExpression source) throws Exception {
		COperator operator = source.get_operator().get_operator();
		switch(operator) {
		case positive:		return this.parse(source.get_operand());
		case negative:
		{
			SymNode operand = this.parse(source.get_operand());
			SymNode result = new SymUnaryExpression(source.get_value_type(), COperator.negative);
			result.add_child(operand); return result;
		}
		default: throw new IllegalArgumentException("Invalid: " + operator);
		}
	}
	private SymNode parse_bitws_unary_expression(AstBitwiseUnaryExpression source) throws Exception {
		COperator operator = source.get_operator().get_operator();
		switch(operator) {
		case bit_not:
		{
			SymNode operand = this.parse(source.get_operand());
			SymNode result = new SymUnaryExpression(source.get_value_type(), COperator.bit_not);
			result.add_child(operand); return result;
		}
		default: throw new IllegalArgumentException("Invalid: " + operator);
		}
	}
	private SymNode parse_logic_unary_expression(AstLogicUnaryExpression source) throws Exception {
		COperator operator = source.get_operator().get_operator();
		switch(operator) {
		case logic_not:
		{
			SymNode operand = this.parse(source.get_operand());
			SymNode result = new SymUnaryExpression(source.get_value_type(), COperator.logic_not);
			result.add_child(operand); return result;
		}
		default: throw new IllegalArgumentException("Invalid: " + operator);
		}
	}
	private SymNode parse_point_unary_expression(AstPointUnaryExpression source) throws Exception {
		COperator operator = source.get_operator().get_operator();
		switch(operator) {
		case address_of:
		{
			SymNode operand = this.parse(source.get_operand());
			SymNode result = new SymUnaryExpression(source.get_value_type(), COperator.address_of);
			result.add_child(operand); return result;
		}
		case dereference:
		{
			SymNode operand = this.parse(source.get_operand());
			SymNode result = new SymUnaryExpression(source.get_value_type(), COperator.dereference);
			result.add_child(operand); return result;
		}
		default: throw new IllegalArgumentException("Invalid: " + operator);
		}
	}
	private SymNode parse_incre_unary_expression(AstIncreUnaryExpression source) throws Exception {
		return this.parse(source.get_operand());
	}
	private SymNode parse_incre_postfix_expression(AstIncrePostfixExpression source) throws Exception {
		SymNode loperand = this.parse(source.get_operand());
		CConstant constant = new CConstant();
		switch(source.get_operator().get_operator()) {
		case increment:
		{
			constant.set_int(1);
		}
		break;
		case decrement:
		{
			constant.set_int(-1);
		}
		break;
		default: throw new IllegalArgumentException("Invalid: " + source.get_operator().get_operator());
		}
		SymNode roperand = new SymConstant(constant);
		SymNode result = new SymMultiExpression(source.get_value_type(), COperator.arith_add);
		result.add_child(loperand); result.add_child(roperand); return result;
	}
	private SymNode parse_arith_binary_expression(AstArithBinaryExpression source) throws Exception {
		COperator operator = source.get_operator().get_operator();
		SymNode loperand = this.parse(source.get_loperand());
		SymNode roperand = this.parse(source.get_roperand());
		
		SymNode result;
		switch(operator) {
		case arith_add:	result = new SymMultiExpression(source.get_value_type(), operator); 	break;
		case arith_sub:	result = new SymBinaryExpression(source.get_value_type(), operator); 	break;
		case arith_mul:	result = new SymMultiExpression(source.get_value_type(), operator); 	break;
		case arith_div:	result = new SymBinaryExpression(source.get_value_type(), operator); 	break;
		case arith_mod:	result = new SymBinaryExpression(source.get_value_type(), operator); 	break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		
		result.add_child(loperand); result.add_child(roperand); return result;
	}
	private SymNode parse_arith_assign_expression(AstArithAssignExpression source) throws Exception {
		COperator operator = source.get_operator().get_operator();
		SymNode loperand = this.parse(source.get_loperand());
		SymNode roperand = this.parse(source.get_roperand());
		
		SymNode result;
		switch(operator) {
		case arith_add_assign:	result = new SymMultiExpression(source.get_value_type(), COperator.arith_add); 	break;
		case arith_sub_assign:	result = new SymBinaryExpression(source.get_value_type(), COperator.arith_sub); 	break;
		case arith_mul_assign:	result = new SymMultiExpression(source.get_value_type(), COperator.arith_mul); 	break;
		case arith_div_assign:	result = new SymBinaryExpression(source.get_value_type(), COperator.arith_div); 	break;
		case arith_mod_assign:	result = new SymBinaryExpression(source.get_value_type(), COperator.arith_mod); 	break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		
		result.add_child(loperand); result.add_child(roperand); return result;
	}
	private SymNode parse_bitws_binary_expression(AstBinaryExpression source) throws Exception {
		COperator operator = source.get_operator().get_operator();
		SymNode loperand = this.parse(source.get_loperand());
		SymNode roperand = this.parse(source.get_roperand());
		
		SymNode result;
		switch(operator) {
		case bit_and:		result = new SymMultiExpression(source.get_value_type(), COperator.bit_and);	break;
		case bit_or:		result = new SymMultiExpression(source.get_value_type(), COperator.bit_or);	break;
		case bit_xor:		result = new SymMultiExpression(source.get_value_type(), COperator.bit_xor);	break;
		case left_shift:	result = new SymBinaryExpression(source.get_value_type(), COperator.left_shift);	break;
		case righ_shift:	result = new SymBinaryExpression(source.get_value_type(), COperator.righ_shift);	break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		
		result.add_child(loperand); result.add_child(roperand); return result;
	}
	private SymNode parse_bitws_assign_expression(AstBinaryExpression source) throws Exception {
		COperator operator = source.get_operator().get_operator();
		SymNode loperand = this.parse(source.get_loperand());
		SymNode roperand = this.parse(source.get_roperand());
		
		SymNode result;
		switch(operator) {
		case bit_and_assign:	result = new SymMultiExpression(source.get_value_type(), COperator.bit_and);	break;
		case bit_or_assign:		result = new SymMultiExpression(source.get_value_type(), COperator.bit_or);	break;
		case bit_xor_assign:	result = new SymMultiExpression(source.get_value_type(), COperator.bit_xor);	break;
		case left_shift_assign:	result = new SymBinaryExpression(source.get_value_type(), COperator.left_shift);	break;
		case righ_shift_assign:	result = new SymBinaryExpression(source.get_value_type(), COperator.righ_shift);	break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		
		result.add_child(loperand); result.add_child(roperand); return result;
	}
	private SymNode parse_logic_binary_expression(AstLogicBinaryExpression source) throws Exception {
		COperator operator = source.get_operator().get_operator();
		SymNode loperand = this.parse(source.get_loperand());
		SymNode roperand = this.parse(source.get_roperand());
		
		SymNode result;
		switch(operator) {
		case logic_and:	result = new SymMultiExpression(source.get_value_type(), COperator.logic_and); break;
		case logic_or:	result = new SymMultiExpression(source.get_value_type(), COperator.logic_or);  break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		
		result.add_child(loperand); result.add_child(roperand); return result;
	}
	private SymNode parse_relation_expression(AstRelationExpression source) throws Exception {
		COperator operator = source.get_operator().get_operator();
		SymNode loperand = this.parse(source.get_loperand());
		SymNode roperand = this.parse(source.get_roperand());
		
		SymNode result;
		switch(operator) {
		case greater_tn:
		case greater_eq:
		case smaller_tn:
		case smaller_eq:
		case equal_with:
		case not_equals: result = new SymBinaryExpression(CBasicTypeImpl.bool_type, operator); break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		
		result.add_child(loperand); result.add_child(roperand); return result;
	}
	private SymNode parse_assign_expression(AstAssignExpression source) throws Exception {
		return this.parse(source.get_roperand());
	}
	private SymNode parse_init_declarator(AstInitDeclarator source) throws Exception {
		if(source.has_initializer()) {
			return this.parse(source.get_initializer());
		}
		else {
			return this.parse(source.get_declarator());
		}
	}
	private SymNode parse_declarator(AstDeclarator source) throws Exception {
		while(source.get_production() != DeclaratorProduction.identifier) {
			source = source.get_declarator();
		}
		
		CName cname = source.get_identifier().get_cname();
		CType data_type;
		if(cname instanceof CInstanceName) {
			data_type = ((CInstanceName) cname).get_instance().get_type();
		}
		else if(cname instanceof CParameterName) {
			data_type = ((CParameterName) cname).get_parameter().get_type();
		}
		else {
			throw new IllegalArgumentException("Invalid: " + cname.getClass().getSimpleName());
		}
		
		return new SymIdentifier(data_type, cname.get_name());
	}
	/**
	 * @param source
	 * @return symbolic representation of the AST source
	 * @throws Exception
	 */
	public static SymNode parse_ast(AstNode source) throws Exception {
		return SymParser.parser.parse(source);
	}
	
	/* cir-parse */
	private SymNode parse(CirNode source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof CirArgumentList) {
			return this.parse_argument_list((CirArgumentList) source);
		}
		else if(source instanceof CirField) {
			return this.parse_field((CirField) source);
		}
		else if(source instanceof CirNameExpression) {
			return this.parse_name_expression((CirNameExpression) source);
		}
		else if(source instanceof CirDeferExpression) {
			return this.parse_defer_expression((CirDeferExpression) source);
		}
		else if(source instanceof CirFieldExpression) {
			return this.parse_field_expression((CirFieldExpression) source);
		}
		else if(source instanceof CirAddressExpression) {
			return this.parse_address_expression((CirAddressExpression) source);
		}
		else if(source instanceof CirCastExpression) {
			return this.parse_cast_expression((CirCastExpression) source);
		}
		else if(source instanceof CirComputeExpression) {
			return this.parse_compute_expression((CirComputeExpression) source);
		}
		else if(source instanceof CirConstExpression) {
			return this.parse_const_expression((CirConstExpression) source);
		}
		else if(source instanceof CirDefaultValue) {
			return this.parse_default_value((CirDefaultValue) source);
		}
		else if(source instanceof CirInitializerBody) {
			return this.parse_initializer_body((CirInitializerBody) source);
		}
		else if(source instanceof CirStringLiteral) {
			return this.parse_literal((CirStringLiteral) source);
		}
		else if(source instanceof CirWaitExpression) {
			return this.parse_wait_expression((CirWaitExpression) source);
		}
		else if(source instanceof CirCallStatement) {
			return this.parse_call_statement((CirCallStatement) source);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + source);
		}
	}
	private SymNode parse_argument_list(CirArgumentList source) throws Exception {
		SymNode arguments = new SymArgumentList();
		for(int k = 0; k < source.number_of_arguments(); k++) {
			arguments.add_child(this.parse(source.get_argument(k)));
		}
		return arguments;
	}
	private SymNode parse_field(CirField source) throws Exception {
		return new SymField(source.get_name());
	}
	private SymNode parse_name_expression(CirNameExpression source) throws Exception {
		return new SymIdentifier(source.get_data_type(), source.get_unique_name());
	}
	private SymNode parse_field_expression(CirFieldExpression source) throws Exception {
		SymNode body = this.parse(source.get_body());
		SymNode field = this.parse(source.get_field());
		SymNode result = new SymFieldExpression(source.get_data_type());
		result.add_child(body); result.add_child(field); return result;
	}
	private SymNode parse_defer_expression(CirDeferExpression source) throws Exception {
		SymNode operand = this.parse(source.get_address());
		SymNode result = new SymUnaryExpression(source.get_data_type(), COperator.dereference);
		result.add_child(operand); return result;
	}
	private SymNode parse_address_expression(CirAddressExpression source) throws Exception {
		SymNode operand = this.parse(source.get_operand());
		SymNode result = new SymUnaryExpression(source.get_data_type(), COperator.address_of);
		result.add_child(operand); return result;
	}
	private SymNode parse_cast_expression(CirCastExpression source) throws Exception {
		SymNode operand = this.parse(source.get_operand());
		SymNode result = new SymUnaryExpression(source.get_data_type(), COperator.assign);
		result.add_child(operand); return result;
	}
	private SymNode parse_const_expression(CirConstExpression source) throws Exception {
		return new SymConstant(source.get_constant());
	}
	private SymNode parse_default_value(CirDefaultValue source) throws Exception {
		CType data_type = CTypeAnalyzer.get_value_type(source.get_data_type());
		if(data_type instanceof CBasicType) {
			CConstant constant = new CConstant();
			switch(((CBasicType) data_type).get_tag()) {
			case c_bool:	constant.set_bool(false); break;
			case c_char:
			case c_uchar:	constant.set_char('\0'); break;
			case c_short:
			case c_ushort:
			case c_int:
			case c_uint:	constant.set_int(0); break;
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:	constant.set_long(0L); break;
			case c_float:	constant.set_float(0.0f); break;
			case c_double:
			case c_ldouble:	constant.set_double(0.0); break;
			default: return new SymReference(source);
			}
			return new SymConstant(constant);
		}
		else if(data_type instanceof CArrayType || data_type instanceof CPointerType) {
			CConstant constant = new CConstant();
			constant.set_long(0L);
			return new SymConstant(constant);
		}
		else {
			return new SymReference(source);
		}
	}
	private SymNode parse_initializer_body(CirInitializerBody source) throws Exception {
		SymNode result = new SymInitializerList();
		for(int k = 0; k < source.number_of_elements(); k++) {
			result.add_child(this.parse(source.get_child(k)));
		}
		return result;
	}
	private SymNode parse_literal(CirStringLiteral source) throws Exception {
		return new SymLiteral(source.get_data_type(), source.get_literal());
	}
	private SymNode parse_wait_expression(CirWaitExpression source) throws Exception {
		CirFunction function = source.get_tree().get_function_call_graph().get_function(source);
		CirExecution wait_execution = function.get_flow_graph().get_execution(source.statement_of());
		CirExecution call_execution = function.get_flow_graph().get_execution(wait_execution.get_id() - 1);
		CirCallStatement call_statement = (CirCallStatement) call_execution.get_statement();
		
		SymNode callee = this.parse(call_statement.get_function());
		SymNode arguments = this.parse(call_statement.get_arguments());
		SymNode result = new SymFunCallExpression(source.get_data_type());
		result.add_child(callee); result.add_child(arguments); return result;
	}
	private SymNode parse_call_statement(CirCallStatement source) throws Exception {
		CirFunction function = source.get_tree().get_function_call_graph().get_function(source);
		CirExecution call_execution = function.get_flow_graph().get_execution(source);
		CirExecution wait_execution = function.get_flow_graph().get_execution(call_execution.get_id() + 1);
		CirWaitAssignStatement wait_statement = (CirWaitAssignStatement) wait_execution.get_statement();
		CirWaitExpression wait_expression = (CirWaitExpression) wait_statement.get_rvalue();
		
		SymNode callee = this.parse(source.get_function());
		SymNode arguments = this.parse(source.get_arguments());
		SymNode result = new SymFunCallExpression(wait_expression.get_data_type());
		result.add_child(callee); result.add_child(arguments); return result;
	}
	private SymNode parse_compute_expression(CirComputeExpression source) throws Exception {
		COperator operator = source.get_operator();
		switch(operator) {
		case positive:
		case negative:
		case bit_not:
		case logic_not:
		{
			SymNode operand = this.parse(source.get_operand(0));
			SymNode result = new SymUnaryExpression(source.get_data_type(), operator);
			result.add_child(operand); return result;
		}
		case arith_sub:
		case arith_div:
		case arith_mod:
		case left_shift:
		case righ_shift:
		case greater_tn:
		case greater_eq:
		case smaller_tn:
		case smaller_eq:
		case equal_with:
		case not_equals:
		{
			SymNode loperand = this.parse(source.get_operand(0));
			SymNode roperand = this.parse(source.get_operand(1));
			SymNode result = new SymBinaryExpression(source.get_data_type(), operator);
			result.add_child(loperand); result.add_child(roperand); return result;
		}
		case arith_add:
		case arith_mul:
		case bit_and:
		case bit_or:
		case bit_xor:
		case logic_and:
		case logic_or:
		{
			SymNode loperand = this.parse(source.get_operand(0));
			SymNode roperand = this.parse(source.get_operand(1));
			SymNode result = new SymMultiExpression(source.get_data_type(), operator);
			result.add_child(loperand); result.add_child(roperand); return result;
		}
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
	}
	/**
	 * @param source
	 * @return symbolic description of the CIR source node
	 * @throws Exception
	 */
	public static SymNode parse_cir(CirNode source) throws Exception {
		return SymParser.parser.parse(source);
	}
	
}
