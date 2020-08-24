package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
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
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
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
 * It generates the symbolic assertion node parsed from AstNode as well as CirNode.
 * 
 * @author yukimula
 *
 */
public class SadParser {
	
	/* definition */
	private CRunTemplate sizeof_template;
	public SadParser(CRunTemplate sizeof_template) throws Exception {
		if(sizeof_template == null)
			throw new IllegalArgumentException("No template");
		this.sizeof_template = sizeof_template;
	}
	private CirExecution find_execution(CirNode location) throws Exception {
		while(location != null) {
			if(location instanceof CirStatement) {
				CirStatement statement = (CirStatement) location;
				return statement.get_tree().get_function_call_graph().get_function
							(statement).get_flow_graph().get_execution(statement);
			}
			else {
				location = location.get_parent();
			}
		}
		throw new IllegalArgumentException("Invalid location: null");
	}
	
	/* parse from CirNode */
	/**
	 * @param source
	 * @return the symbolic assertion node parsed from CirNode
	 * @throws Exception
	 */
	public SadNode parse(CirNode source) throws Exception {
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
		else if(source instanceof CirFieldExpression)
			return this.parse_address_expression((CirAddressExpression) source);
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
		else if(source instanceof CirTagStatement)
			return this.parse_tag_statement((CirTagStatement) source);
		else
			throw new IllegalArgumentException("Unsupport: " + source);
	}
	private SadNode parse_name_expression(CirNameExpression source) throws Exception {
		return new SadIdExpression(source, source.get_data_type(), source.get_unique_name());
	}
	private SadNode parse_constant(CirConstExpression source) throws Exception {
		return new SadConstant(source, source.get_data_type(), source.get_constant());
	}
	private SadNode parse_literal(CirStringLiteral source) throws Exception {
		return new SadLiteral(source, source.get_data_type(), source.get_literal());
	}
	private SadNode parse_default_value(CirDefaultValue source) throws Exception {
		return new SadDefaultValue(source, source.get_data_type());
	}
	private SadNode parse_defer_expression(CirDeferExpression source) throws Exception {
		SadUnaryExpression expression = new SadUnaryExpression(source, 
						source.get_data_type(), COperator.dereference);
		expression.add_child(this.parse(source.get_address()));
		return expression;
	}
	private SadNode parse_field_expression(CirFieldExpression source) throws Exception {
		SadFieldExpression expression = new SadFieldExpression(
								source, source.get_data_type());
		expression.add_child(this.parse(source.get_body()));
		expression.add_child(this.parse(source.get_field()));
		return expression;
	}
	private SadNode parse_field(CirField source) throws Exception {
		return new SadField(source, source.get_name());
	}
	private SadNode parse_address_expression(CirAddressExpression source) throws Exception {
		SadUnaryExpression expression = new SadUnaryExpression(source, 
						source.get_data_type(), COperator.address_of);
		expression.add_child(this.parse(source.get_operand()));
		return expression;
	}
	private SadNode parse_cast_expression(CirCastExpression source) throws Exception {
		SadUnaryExpression expression = new SadUnaryExpression(source,
				source.get_data_type(), COperator.assign);
		expression.add_child(this.parse(source.get_operand()));
		return expression;
	}
	private SadNode parse_initializer_body(CirInitializerBody source) throws Exception {
		SadInitializerList list = new SadInitializerList(source, source.get_data_type());
		for(int k = 0; k < source.number_of_elements(); k++) {
			list.add_child(this.parse(source.get_element(k)));
		}
		return list;
	}
	private SadNode parse_compute_expression(CirComputeExpression source) throws Exception {
		switch(source.get_operator()) {
		case negative:
		case positive:
		case bit_not:
		case logic_not:
		{
			SadUnaryExpression expression = new SadUnaryExpression(
					source, source.get_data_type(), source.get_operator());
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
			SadMultiExpression expression = new SadMultiExpression(source, 
							source.get_data_type(), source.get_operator());
			expression.add_child(this.parse(source.get_operand(0)));
			expression.add_child(this.parse(source.get_operand(1)));
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
			SadBinaryExpression expression = new SadBinaryExpression(source, 
							source.get_data_type(), source.get_operator());
			for(int k = 0; k < source.number_of_operand(); k++) {
				expression.add_child(this.parse(source.get_operand(k)));
			}
			return expression;
		}
		default: throw new IllegalArgumentException("Invalid: " + source);
		}
	}
	private SadNode parse_argument_list(CirArgumentList source) throws Exception {
		SadArgumentList list = new SadArgumentList(source);
		for(int k = 0; k < source.number_of_arguments(); k++) {
			list.add_child(this.parse(source.get_argument(k)));
		}
		return list;
	}
	private SadNode parse_label(CirLabel source) throws Exception {
		CirStatement statement = (CirStatement) source.
				get_tree().get_node(source.get_target_node_id());
		CirExecution execution = this.find_execution(statement);
		return new SadLabel(source, execution);
	}
	private SadNode parse_assign_statement(CirAssignStatement source) throws Exception {
		SadAssignStatement statement = new SadAssignStatement(source);
		statement.add_child(new SadLabel(null, this.find_execution(source)));
		statement.add_child(this.parse(source.get_lvalue()));
		statement.add_child(this.parse(source.get_rvalue()));
		return statement;
	}
	private SadNode parse_goto_statement(CirGotoStatement source) throws Exception {
		SadGotoStatement statement = new SadGotoStatement(source);
		statement.add_child(new SadLabel(null, this.find_execution(source)));
		statement.add_child(this.parse_label(source.get_label()));
		return statement;
	}
	private SadNode parse_if_statement(CirIfStatement source) throws Exception {
		SadIfStatement statement = new SadIfStatement(source);
		statement.add_child(new SadLabel(null, this.find_execution(source)));
		statement.add_child(this.parse(source.get_condition()));
		statement.add_child(this.parse(source.get_true_label()));
		statement.add_child(this.parse(source.get_false_label()));
		return statement;
	}
	private SadNode parse_case_statement(CirCaseStatement source) throws Exception {
		SadIfStatement statement = new SadIfStatement(source);
		statement.add_child(new SadLabel(null, this.find_execution(source)));
		statement.add_child(this.parse(source.get_condition()));
		CirExecution execution = this.find_execution(source);
		for(CirExecutionFlow flow : execution.get_ou_flows()) {
			if(flow.get_type() == CirExecutionFlowType.true_flow) {
				statement.add_child(new SadLabel(null, flow.get_target()));
			}
		}
		statement.add_child(this.parse(source.get_false_label()));
		return statement;
	}
	private SadNode parse_tag_statement(CirTagStatement source) throws Exception {
		SadLabelStatement statement = new SadLabelStatement(source);
		statement.add_child(new SadLabel(null, this.find_execution(source)));
		return statement;
	}
	
	/* parse from AstNode */
	/**
	 * @param source
	 * @return the symbolic assertion node parsed from AstNode
	 * @throws Exception
	 */
	public SadNode parse(AstNode source) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source null");
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
		else if(source instanceof AstInitializer)
			return this.parse_initializer((AstInitializer) source);
		else if(source instanceof AstInitializerBody)
			return this.parse_initializer_body((AstInitializerBody) source);
		else if(source instanceof AstParanthExpression)
			return this.parse_paranth_expression((AstParanthExpression) source);
		else if(source instanceof AstConstExpression)
			return this.parse_const_expression((AstConstExpression) source);
		else if(source instanceof AstSizeofExpression)
			return this.parse_sizeof_expression((AstSizeofExpression) source);
		else if(source instanceof AstArrayExpression)
			return this.parse_array_expression((AstArrayExpression) source);
		else if(source instanceof AstCastExpression)
			return this.parse_cast_expression((AstCastExpression) source);
		else if(source instanceof AstField)
			return this.parse_field((AstField) source);
		else if(source instanceof AstFieldExpression)
			return this.parse_field_expression((AstFieldExpression) source);
		else if(source instanceof AstCommaExpression)
			return this.parse_comma_expression((AstCommaExpression) source);
		else if(source instanceof AstConditionalExpression)
			return this.parse_conditional_expression((AstConditionalExpression) source);
		else if(source instanceof AstFunCallExpression)
			return this.parse_fun_call_expression((AstFunCallExpression) source);
		else if(source instanceof AstArgumentList)
			return this.parse_argument_list((AstArgumentList) source);
		else
			throw new IllegalArgumentException("Unsupport " + source);
	}
	private SadNode parse_id_expression(AstIdExpression source) throws Exception {
		CName cname = source.get_cname();
		if(cname instanceof CInstanceName || 
				cname instanceof CParameterName) {
			String name = cname.get_name() + "#" + cname.get_scope().hashCode();
			return new SadIdExpression(null, source.get_value_type(), name);
		}
		else if(cname instanceof CEnumeratorName) {
			CEnumerator enumerator = ((CEnumeratorName) cname).get_enumerator();
			CConstant constant = new CConstant();
			constant.set_int(enumerator.get_value());
			return new SadConstant(null, constant.get_type(), constant);
		}
		else {
			throw new IllegalArgumentException("Invalid cname: " + cname.getClass());
		}
	}
	private SadNode parse_constant(AstConstant source) throws Exception {
		return new SadConstant(null, source.get_value_type(), source.get_constant());
	}
	private SadNode parse_literal(AstLiteral source) throws Exception {
		return new SadLiteral(null, source.get_value_type(), source.get_literal());
	}
	private SadNode parse_binary_expression(AstBinaryExpression source) throws Exception {
		switch(source.get_operator().get_operator()) {
		case assign:	
		{
			return this.parse(source.get_roperand());
		}
		case arith_add:
		case arith_mul:
		case bit_and:
		case bit_or:
		case bit_xor:
		case logic_and:
		case logic_or:
		{
			SadMultiExpression expression = new SadMultiExpression(null, source.
						get_value_type(), source.get_operator().get_operator());
			expression.add_child(this.parse(source.get_loperand()));
			expression.add_child(this.parse(source.get_roperand()));
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
			SadBinaryExpression expression = new SadBinaryExpression(null, source.
					get_value_type(), source.get_operator().get_operator());
			expression.add_child(this.parse(source.get_loperand()));
			expression.add_child(this.parse(source.get_roperand()));
			return expression;
		}
		case arith_add_assign:
		case arith_mul_assign:
		case bit_and_assign:
		case bit_or_assign:
		case bit_xor_assign:
		{
			String opcode = source.get_operator().get_operator().toString();
			COperator operator = COperator.valueOf(opcode.substring(0, opcode.length() - 7));
			SadMultiExpression expression = new SadMultiExpression(null, source.get_value_type(), operator);
			expression.add_child(this.parse(source.get_loperand()));
			expression.add_child(this.parse(source.get_roperand()));
			return expression;
		}
		case arith_sub_assign:
		case arith_div_assign:
		case arith_mod_assign:
		case left_shift_assign:
		case righ_shift_assign:
		{
			String opcode = source.get_operator().get_operator().toString();
			COperator operator = COperator.valueOf(opcode.substring(0, opcode.length() - 7));
			SadBinaryExpression expression = new SadBinaryExpression(null, source.get_value_type(), operator);
			expression.add_child(this.parse(source.get_loperand()));
			expression.add_child(this.parse(source.get_roperand()));
			return expression;
		}
		default: throw new IllegalArgumentException("Invalid: " + source.get_operator());
		}
	}
	private SadNode parse_unary_expression(AstUnaryExpression source) throws Exception {
		switch(source.get_operator().get_operator()) {
		case negative:
		case bit_not:
		case logic_not:
		case address_of:
		case dereference:
		{
			SadUnaryExpression expression = new SadUnaryExpression(null, source.
						get_value_type(), source.get_operator().get_operator());
			expression.add_child(this.parse(source.get_operand()));
			return expression;
		}
		case positive:
		{
			return this.parse(source.get_operand());
		}
		case increment:
		{
			SadMultiExpression expression = new SadMultiExpression(null, 
						source.get_value_type(), COperator.arith_add);
			expression.add_child(this.parse(source.get_operand()));
			expression.add_child(SadFactory.constant(1));
			return expression;
		}
		case decrement:
		{
			SadBinaryExpression expression = new SadBinaryExpression(null, 
					source.get_value_type(), COperator.arith_sub);
			expression.add_child(this.parse(source.get_operand()));
			expression.add_child(SadFactory.constant(1));
			return expression;
		}
		default: throw new IllegalArgumentException("Invalid: " + source.generate_code());
		}
	}
	private SadNode parse_postfix_expression(AstPostfixExpression source) throws Exception {
		return this.parse(source.get_operand());
	}
	private SadNode parse_const_expression(AstConstExpression source) throws Exception {
		return this.parse(source.get_expression());
	}
	private SadNode parse_paranth_expression(AstParanthExpression source) throws Exception {
		return this.parse(source.get_sub_expression());
	}
	private SadNode parse_sizeof_expression(AstSizeofExpression source) throws Exception {
		CType type;
		if(source.is_expression())
			type = source.get_expression().get_value_type();
		else
			type = source.get_typename().get_type();
		type = CTypeAnalyzer.get_value_type(type);
		int length = this.sizeof_template.sizeof(type);
		return SadFactory.constant(length);
	}
	private SadNode parse_array_expression(AstArrayExpression source) throws Exception {
		SadExpression base = (SadExpression) this.parse(source.get_array_expression());
		SadExpression bias = (SadExpression) this.parse(source.get_dimension_expression());
		SadExpression addr = new SadMultiExpression(null, base.get_data_type(), COperator.arith_add);
		addr.add_child(base);
		addr.add_child(bias);
		SadExpression expression = new SadUnaryExpression(null, source.get_value_type(), COperator.dereference);
		expression.add_child(addr);
		return expression;
	}
	private SadNode parse_cast_expression(AstCastExpression source) throws Exception {
		SadUnaryExpression expression = new SadUnaryExpression(null, 
						source.get_value_type(), COperator.assign);
		expression.add_child(this.parse(source.get_expression()));
		return expression;
	}
	private SadNode parse_comma_expression(AstCommaExpression source) throws Exception {
		int index = source.number_of_arguments() - 1;
		return this.parse(source.get_expression(index));
	}
	private SadNode parse_field(AstField source) throws Exception {
		return new SadField(null, source.get_name());
	}
	private CType element_type(CType type) throws Exception {
		type = CTypeAnalyzer.get_value_type(type);
		if(type instanceof CArrayType) {
			return ((CArrayType) type).get_element_type();
		}
		else if(type instanceof CPointerType) {
			return ((CPointerType) type).get_pointed_type();
		}
		else {
			throw new IllegalArgumentException("Invalid: " + type);
		}
	}
	private SadNode parse_field_expression(AstFieldExpression source) throws Exception {
		SadExpression body = (SadExpression) this.parse(source.get_body());
		SadField field = (SadField) this.parse(source.get_field());
		if(source.get_operator().get_punctuator() == CPunctuator.arrow) {
			SadExpression new_body = new SadUnaryExpression(null, this.
					element_type(body.get_data_type()), COperator.dereference);
			new_body.add_child(body);
			body = new_body;
		}
		SadFieldExpression expression = new 
				SadFieldExpression(null, source.get_value_type());
		expression.add_child(body);
		expression.add_child(field);
		return expression;
	}
	private SadNode parse_initializer_body(AstInitializerBody source) throws Exception {
		SadInitializerList list = new SadInitializerList(null, source.get_value_type());
		for(int k = 0; k < source.get_initializer_list().number_of_initializer(); k++) {
			SadExpression operand = (SadExpression) this.parse(
					source.get_initializer_list().get_initializer(k).get_initializer());
			list.add_child(operand);
		}
		return list;
	}
	private SadNode parse_initializer(AstInitializer source) throws Exception {
		if(source.is_body())
			return this.parse(source.get_body());
		else
			return this.parse(source.get_expression());
	}
	private SadNode parse_conditional_expression(AstConditionalExpression source) throws Exception {
		SadNode condition = this.parse(source.get_condition());
		SadNode toperand = this.parse(source.get_true_branch());
		SadNode foperand = this.parse(source.get_false_branch());
		SadNode ncondition = SadFactory.logic_not(CBasicTypeImpl.
					bool_type, (SadExpression) condition.clone());
		
		SadNode loperand = new SadMultiExpression(null, 
						source.get_value_type(), COperator.arith_mul);
		loperand.add_child(condition); loperand.add_child(toperand);
		
		SadNode roperand = new SadMultiExpression(null, 
						source.get_value_type(), COperator.arith_mul);
		roperand.add_child(ncondition); roperand.add_child(foperand);
		
		SadNode expression = new SadMultiExpression(null, 
						source.get_value_type(), COperator.arith_add);
		expression.add_child(loperand); expression.add_child(roperand);
		
		return expression;
	}
	private SadNode parse_fun_call_expression(AstFunCallExpression source) throws Exception {
		SadNode function = this.parse(source.get_function());
		SadNode arguments;
		if(source.has_argument_list()) {
			arguments = this.parse(source.get_argument_list());
		}
		else {
			arguments = new SadArgumentList(null);
		}
		SadNode expression = new SadCallExpression(null, source.get_value_type());
		expression.add_child(function); expression.add_child(arguments);
		return expression;
	}
	private SadNode parse_argument_list(AstArgumentList source) throws Exception {
		SadNode list = new SadArgumentList(null);
		for(int k = 0; k < source.number_of_arguments(); k++) {
			list.add_child(this.parse(source.get_argument(k)));
		}
		return list;
	}
	
}
