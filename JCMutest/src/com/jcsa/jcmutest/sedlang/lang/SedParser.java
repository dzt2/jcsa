package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcmutest.sedlang.SedNode;
import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
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
import com.jcsa.jcparse.lang.ctype.CEnumerator;
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
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabel;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.lexical.CPunctuator;
import com.jcsa.jcparse.lang.scope.CEnumeratorName;
import com.jcsa.jcparse.lang.scope.CInstanceName;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CParameterName;

/**
 * It parses the Sed language code from CIR language.
 * 
 * @author yukimula
 *
 */
public class SedParser {
	
	/* singleton mode */
	private CRunTemplate sizeof_template;
	public SedParser(CRunTemplate sizeof_template) throws Exception {
		if(sizeof_template == null)
			throw new IllegalArgumentException("Invalid template: null");
		else 
			this.sizeof_template = sizeof_template;
	}
	private CirExecution find_execution(CirStatement statement) throws Exception {
		return statement.get_tree().get_function_call_graph().get_function
					(statement).get_flow_graph().get_execution(statement);
	}
	
	/* parsing methods from CirNode */
	/**
	 * @param source
	 * @return parse the SedNode from CirNode
	 * @throws Exception
	 */
	public SedNode parse(CirNode source) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(source instanceof CirNameExpression)
			return this.parse_name_expression((CirNameExpression) source);
		else if(source instanceof CirConstExpression)
			return this.parse_constant((CirConstExpression) source);
		else if(source instanceof CirStringLiteral)
			return this.parse_literal((CirStringLiteral) source);
		else if(source instanceof CirDeferExpression)
			return this.parse_defer_expression((CirDeferExpression) source);
		else if(source instanceof CirFieldExpression)
			return this.parse_field_expression((CirFieldExpression) source);
		else if(source instanceof CirField)
			return this.parse_field((CirField) source);
		else if(source instanceof CirAddressExpression)
			return this.parse_address_expression((CirAddressExpression) source);
		else if(source instanceof CirCastExpression)
			return this.parse_cast_expression((CirCastExpression) source);
		else if(source instanceof CirInitializerBody)
			return this.parse_initializer_body((CirInitializerBody) source);
		else if(source instanceof CirDefaultValue)
			return this.parse_default_value((CirDefaultValue) source);
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
		else if(source instanceof CirIfStatement)
			return this.parse_if_statement((CirIfStatement) source);
		else if(source instanceof CirCaseStatement)
			return this.parse_case_statement((CirCaseStatement) source);
		else if(source instanceof CirGotoStatement)
			return this.parse_goto_statement((CirGotoStatement) source);
		else if(source instanceof CirCallStatement)
			return this.parse_call_statement((CirCallStatement) source);
		else if(source instanceof CirTagStatement)
			return this.parse_tag_statement((CirTagStatement) source);
		else
			throw new IllegalArgumentException("Unsupport: " + source);
	}
	private SedNode parse_name_expression(CirNameExpression source) throws Exception {
		return new SedIdentifier(source, source.get_data_type(), source.get_unique_name());
	}
	private SedNode parse_constant(CirConstExpression source) throws Exception {
		return new SedConstant(source, source.get_constant());
	}
	private SedNode parse_literal(CirStringLiteral source) throws Exception {
		return new SedLiteral(source, source.get_data_type(), source.get_literal());
	}
	private SedNode parse_defer_expression(CirDeferExpression source) throws Exception {
		SedNode operand = this.parse(source.get_address());
		SedUnaryExpression expression = new 
				SedUnaryExpression(source, source.get_data_type());
		expression.add_child(SedFactory.new_operator(COperator.dereference));
		expression.add_child(operand);
		return expression;
	}
	private SedNode parse_field_expression(CirFieldExpression source) throws Exception {
		SedNode body = this.parse(source.get_body());
		SedNode field = this.parse(source.get_field());
		SedFieldExpression expression = new 
				SedFieldExpression(source, source.get_data_type());
		expression.add_child(body);
		expression.add_child(field);
		return expression;
	}
	private SedNode parse_field(CirField source) throws Exception {
		return new SedField(source, source.get_name());
	}
	private SedNode parse_address_expression(CirAddressExpression source) throws Exception {
		SedNode operand = this.parse(source.get_operand());
		SedUnaryExpression expression = new 
				SedUnaryExpression(source, source.get_data_type());
		expression.add_child(SedFactory.new_operator(COperator.address_of));
		expression.add_child(operand);
		return expression;
	}
	private SedNode parse_cast_expression(CirCastExpression source) throws Exception {
		SedNode operand = this.parse(source.get_operand());
		SedUnaryExpression expression = new 
				SedUnaryExpression(source, source.get_data_type());
		expression.add_child(SedFactory.new_operator(COperator.assign));
		expression.add_child(operand);
		return expression;
	}
	private SedNode parse_default_value(CirDefaultValue source) throws Exception {
		return new SedDefaultValue(source, source.get_data_type());
	}
	private SedNode parse_initializer_body(CirInitializerBody source) throws Exception {
		SedInitializerList list = new SedInitializerList(source, source.get_data_type());
		for(int k = 0; k < source.number_of_elements(); k++) {
			list.add_child(this.parse(source.get_element(k)));
		}
		return list;
	}
	private SedNode parse_compute_expression(CirComputeExpression source) throws Exception {
		SedNode expression;
		switch(source.get_operator()) {
		case negative:
		case positive:
		case bit_not:
		case logic_not:
		{
			expression = new SedUnaryExpression(source, source.get_data_type());
			expression.add_child(SedFactory.new_operator(source.get_operator()));
			expression.add_child(this.parse(source.get_operand(0)));
			return expression;
		}
		case arith_add:
		case arith_mul:
		case bit_and:
		case bit_or:
		case bit_xor:
		case logic_and:
		case logic_or:
		{
			expression = new SedMultiExpression(source, source.get_data_type());
			expression.add_child(SedFactory.new_operator(source.get_operator()));
			for(int k = 0; k < source.number_of_operand(); k++) {
				expression.add_child(this.parse(source.get_operand(k)));
			}
			return expression;
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
			expression = new SedBinaryExpression(source, source.get_data_type());
			expression.add_child(SedFactory.new_operator(source.get_operator()));
			expression.add_child(this.parse(source.get_operand(0)));
			expression.add_child(this.parse(source.get_operand(1)));
			return expression;
		}
		default: throw new IllegalArgumentException("Invalid source: " + source);
		}
	}
	private CirCallStatement find_call_statement(CirWaitAssignStatement source) throws Exception {
		CirExecution wait_execution = this.find_execution(source);
		CirExecution call_execution = wait_execution.
				get_graph().get_execution(wait_execution.get_id() - 1);
		return (CirCallStatement) call_execution.get_statement();
	}
	private SedNode parse_wait_expression(CirWaitExpression source) throws Exception {
		CirCallStatement call_statement = this.find_call_statement(
					(CirWaitAssignStatement) source.get_parent());
		SedNode function = this.parse(call_statement.get_function());
		SedNode argument_list = this.parse(call_statement.get_arguments());
		SedCallExpression expression = 
					new SedCallExpression(source, source.get_data_type());
		expression.add_child(function); expression.add_child(argument_list);
		return expression;
	}
	private SedNode parse_argument_list(CirArgumentList source) throws Exception {
		SedArgumentList arguments = new SedArgumentList(source);
		for(int k = 0; k < source.number_of_arguments(); k++) {
			arguments.add_child(this.parse(source.get_argument(k)));
		}
		return arguments;
	}
	private SedNode parse_label(CirLabel source) throws Exception {
		CirStatement statement = (CirStatement) source.
				get_tree().get_node(source.get_target_node_id());
		return new SedLabel(statement, this.find_execution(statement));
	}
	private SedNode parse_assign_statement(CirAssignStatement source) throws Exception {
		SedAssignStatement statement = new SedAssignStatement(source);
		statement.add_child(this.parse(source.get_lvalue()));
		statement.add_child(this.parse(source.get_rvalue()));
		return statement;
	}
	private SedNode parse_goto_statement(CirGotoStatement source) throws Exception {
		SedGotoStatement statement = new SedGotoStatement(source);
		statement.add_child(this.parse(source.get_label()));
		return statement;
	}
	private SedNode parse_call_statement(CirCallStatement source) throws Exception {
		SedCallStatement statement = new SedCallStatement(source);
		statement.add_child(this.parse(source.get_function()));
		statement.add_child(this.parse(source.get_arguments()));
		CirExecution call_execution = this.find_execution(source);
		CirExecution next_execution = call_execution.get_ou_flow(0).get_target();
		statement.add_child(new SedLabel(null, next_execution));
		return statement;
	}
	private SedNode parse_if_statement(CirIfStatement source) throws Exception {
		SedIfStatement statement = new SedIfStatement(source);
		statement.add_child(this.parse(source.get_condition()));
		statement.add_child(this.parse(source.get_true_label()));
		statement.add_child(this.parse(source.get_false_label()));
		return statement;
	}
	private SedNode parse_case_statement(CirCaseStatement source) throws Exception {
		SedIfStatement statement = new SedIfStatement(source);
		statement.add_child(this.parse(source.get_condition()));
		CirExecution case_execution = this.find_execution(source);
		CirExecution next_execution = null;
		for(CirExecutionFlow flow : case_execution.get_ou_flows()) {
			if(flow.get_type() == CirExecutionFlowType.true_flow) {
				next_execution = flow.get_target();
				break;
			}
		}
		statement.add_child(new SedLabel(null, next_execution));
		statement.add_child(this.parse(source.get_false_label()));
		return statement;
	}
	private SedNode parse_tag_statement(CirTagStatement source) throws Exception {
		SedLabelStatement statement = new SedLabelStatement(source);
		statement.add_child(new SedLabel(null, this.find_execution(source)));
		return statement;
	}
	
	/* parsing method from AstNode */
	/**
	 * @param source
	 * @return the SedNode parsed from AstNode
	 * @throws Exception
	 */
	public SedNode parse(AstNode source) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(source instanceof AstIdExpression)
			return this.parse_id_expression((AstIdExpression) source);
		else if(source instanceof AstConstant)
			return this.parse_constant((AstConstant) source);
		else if(source instanceof AstLiteral)
			return this.parse_literal((AstLiteral) source);
		else if(source instanceof AstBinaryExpression)
			return this.parse_binary_expression((AstBinaryExpression) source);
		else if(source instanceof AstUnaryExpression)
			return this.parse_unary_expression((AstUnaryExpression) source);
		else if(source instanceof AstPostfixExpression)
			return this.parse_postfix_expression((AstPostfixExpression) source);
		else if(source instanceof AstSizeofExpression)
			return this.parse_sizeof_expression((AstSizeofExpression) source);
		else if(source instanceof AstArrayExpression)
			return this.parse_array_expression((AstArrayExpression) source);
		else if(source instanceof AstCastExpression)
			return this.parse_cast_expression((AstCastExpression) source);
		else if(source instanceof AstCommaExpression)
			return this.parse_comma_expression((AstCommaExpression) source);
		else if(source instanceof AstField)
			return this.parse_field((AstField) source);
		else if(source instanceof AstFieldExpression)
			return this.parse_field_expression((AstFieldExpression) source);
		else if(source instanceof AstInitializerBody)
			return this.parse_initializer_body((AstInitializerBody) source);
		else if(source instanceof AstInitializer)
			return this.parse_initializer((AstInitializer) source);
		else if(source instanceof AstParanthExpression)
			return this.parse_paranth_expression((AstParanthExpression) source);
		else if(source instanceof AstConstExpression)
			return this.parse_const_expression((AstConstExpression) source);
		else if(source instanceof AstFunCallExpression)
			return this.parse_fun_call_expression((AstFunCallExpression) source);
		else if(source instanceof AstArgumentList)
			return this.parse_argument_list((AstArgumentList) source);
		else if(source instanceof AstConditionalExpression)
			return this.parse_conditional_expression((AstConditionalExpression) source);
		else
			throw new IllegalArgumentException("Unsupport: " + source);
	}
	private SedNode parse_id_expression(AstIdExpression source) throws Exception {
		CName cname = source.get_cname();
		if(cname instanceof CInstanceName || cname instanceof CParameterName) {
			return new SedIdentifier(null, source.get_value_type(), source.get_name());
		}
		else if(cname instanceof CEnumeratorName) {
			CEnumerator enumerator = ((CEnumeratorName) cname).get_enumerator();
			CConstant constant = new CConstant();
			constant.set_int(enumerator.get_value());
			return new SedConstant(null, constant);
		}
		else {
			throw new IllegalArgumentException("Invalid cname: " + cname.getClass());
		}
	}
	private SedNode parse_constant(AstConstant source) throws Exception {
		return new SedConstant(null, source.get_constant());
	}
	private SedNode parse_literal(AstLiteral source) throws Exception {
		return new SedLiteral(null, source.get_value_type(), source.get_literal());
	}
	private SedNode parse_sizeof_expression(AstSizeofExpression source) throws Exception {
		int length;
		if(source.is_expression()) {
			length = this.sizeof_template.sizeof(source.get_expression());
		}
		else {
			length = this.sizeof_template.sizeof(source.get_typename().get_type());
		}
		CConstant constant = new CConstant();
		constant.set_int(length);
		return new SedConstant(null, constant);
	}
	private SedNode parse_unary_expression(AstUnaryExpression source) throws Exception {
		SedExpression operand = (SedExpression) this.parse(source.get_operand());
		switch(source.get_operator().get_operator()) {
		case positive:	return operand;
		case negative:
		case bit_not:
		case logic_not:
		case address_of:
		case dereference:
		{
			SedExpression expression = new SedUnaryExpression(null, source.get_value_type());
			expression.add_child(new SedOperator(null, source.get_operator().get_operator()));
			expression.add_child(operand);
			return expression;
		}
		case increment:
		{
			SedExpression expression = new SedMultiExpression(null, source.get_value_type());
			expression.add_child(new SedOperator(null, COperator.arith_add));
			expression.add_child(operand);
			CConstant constant = new CConstant();
			constant.set_int(1);
			expression.add_child(new SedConstant(null, constant));
			return expression;
		}
		case decrement:
		{
			SedExpression expression = new SedBinaryExpression(null, source.get_value_type());
			expression.add_child(new SedOperator(null, COperator.arith_sub));
			expression.add_child(operand);
			CConstant constant = new CConstant();
			constant.set_int(1);
			expression.add_child(new SedConstant(null, constant));
			return expression;
		}
		default: throw new IllegalArgumentException("Invalid: " + source);
		}
	}
	private SedNode parse_postfix_expression(AstPostfixExpression source) throws Exception {
		return this.parse(source.get_operand());
	}
	private SedNode parse_binary_expression(AstBinaryExpression source) throws Exception {
		SedExpression loperand = (SedExpression) this.parse(source.get_loperand());
		SedExpression roperand = (SedExpression) this.parse(source.get_roperand());
		switch(source.get_operator().get_operator()) {
		case assign:		return roperand;
		case arith_add:
		case arith_mul:
		case bit_and:
		case bit_or:
		case bit_xor:
		case logic_and:
		case logic_or:
		{
			SedMultiExpression expression = new SedMultiExpression(null, source.get_value_type());
			expression.add_child(new SedOperator(null, source.get_operator().get_operator()));
			expression.add_child(loperand); 
			expression.add_child(roperand);
			return expression;
		}
		case arith_sub:
		case arith_div:
		case arith_mod:
		case left_shift:
		case righ_shift: 
		case greater_tn:
		case smaller_tn:
		case greater_eq:
		case smaller_eq:
		case equal_with:
		case not_equals:
		{
			SedBinaryExpression expression = new SedBinaryExpression(null, source.get_value_type());
			expression.add_child(new SedOperator(null, source.get_operator().get_operator()));
			expression.add_child(loperand); 
			expression.add_child(roperand);
			return expression;
		}
		case arith_add_assign:
		case arith_mul_assign:
		case bit_and_assign:
		case bit_or_assign:
		case bit_xor_assign:
		{
			String opname = source.get_operator().get_operator().toString();
			COperator operator = COperator.valueOf(opname.substring(0, opname.length() - 7));
			SedMultiExpression expression = new SedMultiExpression(null, source.get_value_type());
			expression.add_child(new SedOperator(null, operator));
			expression.add_child(loperand); 
			expression.add_child(roperand);
			return expression;
		}
		case arith_sub_assign:
		case arith_div_assign:
		case arith_mod_assign:
		case left_shift_assign:
		case righ_shift_assign:
		{
			String opname = source.get_operator().get_operator().toString();
			COperator operator = COperator.valueOf(opname.substring(0, opname.length() - 7));
			SedBinaryExpression expression = new SedBinaryExpression(null, source.get_value_type());
			expression.add_child(new SedOperator(null, operator));
			expression.add_child(loperand); 
			expression.add_child(roperand);
			return expression;
		}
		default: throw new IllegalArgumentException("Unsupport: " + source.get_operator());
		}
	}
	private SedNode parse_array_expression(AstArrayExpression source) throws Exception {
		SedExpression loperand = (SedExpression) this.parse(source.get_array_expression());
		SedExpression roperand = (SedExpression) this.parse(source.get_dimension_expression());
		
		SedExpression address = new SedMultiExpression(null, loperand.get_data_type());
		address.add_child(new SedOperator(null, COperator.arith_add));
		address.add_child(loperand);
		address.add_child(roperand);
		
		SedExpression expression = new SedUnaryExpression(null, source.get_value_type());
		expression.add_child(new SedOperator(null, COperator.dereference));
		expression.add_child(address);
		return expression;
	}
	private SedNode parse_cast_expression(AstCastExpression source) throws Exception {
		SedExpression expression = new SedUnaryExpression(null, source.get_value_type());
		expression.add_child(new SedOperator(null, COperator.assign));
		expression.add_child(this.parse(source.get_expression()));
		return expression;
	}
	private SedNode parse_comma_expression(AstCommaExpression source) throws Exception {
		int index = source.number_of_arguments() - 1;
		return this.parse(source.get_expression(index));
	}
	private SedNode parse_field(AstField source) throws Exception {
		return new SedField(null, source.get_name());
	}
	private CType get_pointed_type(CType type) throws Exception {
		type = CTypeAnalyzer.get_value_type(type);
		if(type instanceof CPointerType) {
			return ((CPointerType) type).get_pointed_type();
		}
		else if(type instanceof CArrayType) {
			return ((CArrayType) type).get_element_type();
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + type);
		}
	}
	private SedNode parse_field_expression(AstFieldExpression source) throws Exception {
		SedExpression body = (SedExpression) this.parse(source.get_body());
		SedField field = (SedField) this.parse(source.get_field());
		
		if(source.get_operator().get_punctuator() == CPunctuator.arrow) {
			SedExpression new_body = new SedUnaryExpression(null, 
					this.get_pointed_type(body.get_data_type()));
			new_body.add_child(new SedOperator(null, COperator.dereference));
			new_body.add_child(body);
			body = new_body;
		}
		
		SedExpression expression = new SedFieldExpression(null, source.get_value_type());
		expression.add_child(body);
		expression.add_child(field);
		return expression;
	}
	private SedNode parse_initializer_body(AstInitializerBody source) throws Exception {
		SedInitializerList list = new SedInitializerList(null, source.get_value_type());
		for(int k = 0; k < source.get_initializer_list().number_of_initializer(); k++) {
			SedExpression element = (SedExpression) this.parse(
					source.get_initializer_list().get_initializer(k).get_initializer());
			list.add_child(element);
		}
		return list;
	}
	private SedNode parse_initializer(AstInitializer source) throws Exception {
		if(source.is_body())
			return this.parse(source.get_body());
		else
			return this.parse(source.get_expression());
	}
	private SedNode parse_paranth_expression(AstParanthExpression source) throws Exception {
		return this.parse(source.get_sub_expression());
	}
	private SedNode parse_const_expression(AstConstExpression source) throws Exception {
		return this.parse(source.get_expression());
	}
	private SedNode parse_fun_call_expression(AstFunCallExpression source) throws Exception {
		SedExpression function = (SedExpression) this.parse(source.get_function());
		SedArgumentList arguments;
		if(source.has_argument_list()) {
			arguments = (SedArgumentList) this.parse(source.get_argument_list());
		}
		else {
			arguments = new SedArgumentList(null);
		}
		SedCallExpression expression = new SedCallExpression(null, source.get_value_type());
		expression.add_child(function);
		expression.add_child(arguments);
		return expression;
	}
	private SedNode parse_argument_list(AstArgumentList source) throws Exception {
		SedArgumentList list = new SedArgumentList(null);
		for(int k = 0; k < source.number_of_arguments(); k++) {
			list.add_child(this.parse(source.get_argument(k)));
		}
		return list;
	}
	private SedNode parse_conditional_expression(AstConditionalExpression source) throws Exception {
		SedExpression condition = (SedExpression) this.parse(source.get_condition());
		SedExpression toperand = (SedExpression) this.parse(source.get_true_branch());
		SedExpression foperand = (SedExpression) this.parse(source.get_false_branch());
		
		SedExpression ncondition = new SedUnaryExpression(null, CBasicTypeImpl.bool_type);
		ncondition.add_child(new SedOperator(null, COperator.logic_not));
		ncondition.add_child(condition.clone());
		
		SedExpression loperand = new SedMultiExpression(null, toperand.get_data_type());
		loperand.add_child(new SedOperator(null, COperator.arith_mul));
		loperand.add_child(condition); loperand.add_child(toperand);
		
		SedExpression roperand = new SedMultiExpression(null, foperand.get_data_type());
		roperand.add_child(new SedOperator(null, COperator.arith_mul));
		roperand.add_child(ncondition); roperand.add_child(foperand);
		
		SedExpression expression = new SedMultiExpression(null, source.get_value_type());
		expression.add_child(new SedOperator(null, COperator.arith_add));
		expression.add_child(loperand); expression.add_child(roperand);
		
		return expression;
	}
	
}
