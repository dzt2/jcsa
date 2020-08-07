package com.jcsa.jcparse.test.path.find;

import java.nio.ByteBuffer;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.AstDeclaration;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclaratorList;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstFieldInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerList;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
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
import com.jcsa.jcparse.lang.astree.stmt.AstBreakStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstContinueStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDeclarationStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDefaultStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstGotoStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CFunctionType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CQualifierType;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.CUnionType;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.test.path.AstExecutionFlowType;
import com.jcsa.jcparse.test.path.AstExecutionNode;
import com.jcsa.jcparse.test.path.AstExecutionPath;
import com.jcsa.jcparse.test.path.AstExecutionUnit;
import com.jcsa.jcparse.test.path.InstrumentLine;
import com.jcsa.jcparse.test.path.InstrumentList;

/**
 * The interface to fetch the execution path of the AST-node(s).
 * 
 * @author yukimula
 *
 */
public class InstrumentalPathFinder {
	
	/* definitions */
	/** the consumer to fetch instrumental lines **/
	private InstrumentListConsumer input;
	/** the execution path in form of AST-sequence **/
	private AstExecutionPath path;
	/**
	 * @param instrument_list it provides the sequence of the instrumental lines
	 * @param path the execution path being constructed
	 * @throws IllegalArgumentException
	 */
	private InstrumentalPathFinder(InstrumentList instrument_list,
			AstExecutionPath path) throws IllegalArgumentException {
		if(instrument_list == null)
			throw new IllegalArgumentException("Invalid list");
		else if(path == null)
			throw new IllegalArgumentException("Invalid path");
		else {
			this.input = new InstrumentListConsumer(instrument_list);
			this.path = path;
		}
	}
	
	/* utility methods */
	/**
	 * @param data_type
	 * @return whether the expression with respect to the data type can be monitored
	 * 			and inserted with jcm_add for evaluating its value.
	 * @throws Exception
	 */
	private boolean is_valid_type(CType data_type) throws Exception {
		if(data_type == null) {
			return false;
		}
		else if(data_type instanceof CBasicType) {
			switch(((CBasicType) data_type).get_tag()) {
			case c_void:
			case gnu_va_list:	return false;
			default:			return true;
			}
		}
		else if(data_type instanceof CArrayType) {
			return false;
		}
		else if(data_type instanceof CPointerType) {
			return true;
		}
		else if(data_type instanceof CFunctionType) {
			return false;
		}
		else if(data_type instanceof CStructType || 
				data_type instanceof CUnionType || 
				data_type instanceof CEnumType) {
			return true;
		}
		else if(data_type instanceof CQualifierType) {
			return this.is_valid_type(((CQualifierType) data_type).get_reference());
		}
		else {
			throw new IllegalArgumentException("Invalid data_type: " + data_type);
		}
	}
	/**
	 * The following context is invalid for evaluating the value of the expression when:
	 * 	1.	expr {=, +=, -=, *=, /=, %=, &=, |=, ^=, <<=, >>=} other_expression;<br>
	 * 	2.	&expr, expr.field, expr++, expr--, ++expr, --expr;<br>
	 * 	3. 	case expr:<br>
	 * @param node
	 * @return
	 * @throws Exception
	 */
	private boolean is_valid_context(AstExpression node) throws Exception {
		/** 1. reach the direct-context where the node belongs to **/
		AstNode context = node.get_parent(), child = node;
		while(context instanceof AstParanthExpression
				|| context instanceof AstConstExpression
				|| context instanceof AstInitializer) {
			child = context;
			context = context.get_parent();
		}
		
		/** 2. invalid case determination **/
		if(context instanceof AstAssignExpression
			|| context instanceof AstArithAssignExpression
			|| context instanceof AstBitwiseAssignExpression
			|| context instanceof AstShiftAssignExpression) {
			return ((AstBinaryExpression) context).get_loperand() != child;
		}
		else if(context instanceof AstUnaryExpression) {
			switch(((AstUnaryExpression) context).get_operator().get_operator()) {
			case address_of:
			case increment:
			case decrement:
			{
				return ((AstUnaryExpression) context).get_operand() != child;
			}
			default: return true;
			}
		}
		else if(context instanceof AstPostfixExpression) {
			switch(((AstPostfixExpression) context).get_operator().get_operator()) {
			case increment:
			case decrement:
			{
				return ((AstPostfixExpression) context).get_operand() != child;
			}
			default: return true;
			}
		}
		else if(context instanceof AstFieldExpression) {
			switch(((AstFieldExpression) context).get_operator().get_punctuator()) {
			case dot:	
			{
				return ((AstFieldExpression) context).get_body() != child;
			}
			default:	return true;
			}
		}
		else if(context instanceof AstCaseStatement) {
			return ((AstCaseStatement) context).get_expression() != child;
		}
		else {
			return true;
		}
	}
	/**
	 * the expression is invalid for analysis when it is assignment.
	 * @param node
	 * @return
	 * @throws Exception
	 */
	private boolean is_valid_expression(AstExpression node) throws Exception {
		if(node instanceof AstBinaryExpression) {
			switch(((AstBinaryExpression) node).get_operator().get_operator()) {
			case assign:
			case arith_add_assign:
			case arith_sub_assign:
			case arith_mul_assign:
			case arith_div_assign:
			case arith_mod_assign:
			case bit_and_assign:
			case bit_or_assign:
			case bit_xor_assign:
			case left_shift_assign:
			case righ_shift_assign:	return false;
			default: return true;
			}
		}
		else if(node instanceof AstConstExpression
				|| node instanceof AstParanthExpression
				|| node instanceof AstConstant
				|| node instanceof AstLiteral) {
			return false;
		}
		else {
			return true;
		}
	}
	/**
	 * @param node
	 * @return whether the expression can be instrumented
	 * @throws Exception
	 */
	private boolean is_instrumental_expression(AstExpression node) throws Exception {
		return this.is_valid_context(node) && 
				this.is_valid_expression(node) && 
				this.is_valid_type(node.get_value_type());
	}
	/**
	 * @param location
	 * @return the solution to record the path range of the given location.
	 * @throws Exception
	 */
	private InstrumentPathSolution new_solution(AstNode location) throws Exception {
		return new InstrumentPathSolution(location);
	}
	/**
	 * @param unit
	 * @return the execution node in instrumental path w.r.t. the unit
	 * @throws Exception
	 */
	private AstExecutionNode new_execution(AstExecutionUnit unit) throws Exception {
		return this.path.new_node(unit);
	}
	/**
	 * @param constant
	 * @return the byte-sequence of the state of the constant
	 * @throws Exception
	 */
	private byte[] state_of(CConstant constant) throws Exception {
		switch(constant.get_type().get_tag()) {
		case c_bool:
		{
			if(constant.get_bool().booleanValue()) {
				return new byte[] { 1 };
			}
			else {
				return new byte[] { 0 };
			}
		}
		case c_char:
		case c_uchar:
		{
			return new byte[] { (byte) constant.get_char().charValue() };
		}
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:
		{
			return ByteBuffer.allocate(4).putInt(constant.get_integer().intValue()).array();
		}
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:
		{
			return ByteBuffer.allocate(8).putLong(constant.get_long().longValue()).array();
		}
		case c_float:
		{
			return ByteBuffer.allocate(4).putFloat(constant.get_float().floatValue()).array();
		}
		case c_double:
		case c_ldouble:
		{
			return ByteBuffer.allocate(8).putDouble(constant.get_double().doubleValue()).array();
		}
		default: throw new IllegalArgumentException("Unknown type");
		}
	}
	/**
	 * @param literal
	 * @return the byte sequence of the string literal
	 * @throws Exception
	 */
	private byte[] state_of(String literal) throws Exception {
		byte[] bytes = new byte[literal.length() + 1];
		for(int k = 0; k < literal.length(); k++) {
			bytes[k] = (byte) literal.charAt(k);
		}
		bytes[literal.length()] = 0;
		return bytes;
	}
	/**
	 * @return whether there is more line in the instrumental list 
	 */
	private boolean has_line() { return this.input.get() != null; }
	/**
	 * @param prev_solution
	 * @param condition_node
	 * @return the boolean value hold by the condition-node in previous solution path range
	 * @throws Exception
	 */
	private boolean get_condition_value(InstrumentPathSolution prev_solution,
			AstExpression condition_node) throws Exception {
		AstExecutionNode node = prev_solution.last_node(
				CTypeAnalyzer.get_expression_of(condition_node));
		return node.get_unit().get_bool_state().booleanValue();
	}
	/**
	 * @return the next instrumental line in the list
	 */
	private InstrumentLine get_line() { return this.input.get(); }
	/**
	 * To match with the current token in the insturmental list
	 * @param execution
	 * @throws Exception
	 */
	private void match(AstExecutionNode execution) throws Exception {
		/* To avoid the invalid instrumental expression point */
		AstNode location = execution.get_unit().get_location();
		if(location instanceof AstExpression) {
			if(!this.is_instrumental_expression((AstExpression) location)) {
				return;	// ignore the expression if it is not instrumental point
			}
		}
		
		/* when matching the current token successfully */
		if(this.input.match(execution.get_unit())) {
			this.input.next();
		}
		else {
			throw new RuntimeException("Failed to match " + this.
					input.get() + "\nat\n" + execution.get_unit());
		}
	}
	
	/* path-finding algorithms */
	/**
	 * Perform the syntax-directed algorithm for path-traversal
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find(AstNode location) throws Exception {
		if(location == null)
			throw new IllegalArgumentException("Invalid location: null");
		else if(location instanceof AstDeclaration)
			return this.find_declaration((AstDeclaration) location);
		else if(location instanceof AstInitDeclaratorList)
			return this.find_init_declarator_list((AstInitDeclaratorList) location);
		else if(location instanceof AstInitDeclarator)
			return this.find_init_declarator((AstInitDeclarator) location);
		else if(location instanceof AstDeclarator)
			return this.find_declarator((AstDeclarator) location);
		else if(location instanceof AstInitializer)
			return this.find_initializer((AstInitializer) location);
		else if(location instanceof AstFieldInitializer)
			return this.find_field_initializer((AstFieldInitializer) location);
		else if(location instanceof AstInitializerBody)
			return this.find_initializer_body((AstInitializerBody) location);
		else if(location instanceof AstInitializerList)
			return this.find_initializer_list((AstInitializerList) location);
		else if(location instanceof AstIdExpression)
			return this.find_id_expression((AstIdExpression) location);
		else if(location instanceof AstConstant)
			return this.find_constant((AstConstant) location);
		else if(location instanceof AstLiteral)
			return this.find_literal((AstLiteral) location);
		else if(location instanceof AstBinaryExpression)
			return this.find_binary_expression((AstBinaryExpression) location);
		else if(location instanceof AstPostfixExpression)
			return this.find_postfix_expression((AstPostfixExpression) location);
		else if(location instanceof AstUnaryExpression)
			return this.find_unary_expression((AstUnaryExpression) location);
		else if(location instanceof AstArrayExpression)
			return this.find_array_expression((AstArrayExpression) location);
		else if(location instanceof AstCastExpression)
			return this.find_cast_expression((AstCastExpression) location);
		else if(location instanceof AstCommaExpression)
			return this.find_comma_expression((AstCommaExpression) location);
		else if(location instanceof AstConstExpression)
			return this.find_const_expression((AstConstExpression) location);
		else if(location instanceof AstParanthExpression)
			return this.find_paranth_expression((AstParanthExpression) location);
		else if(location instanceof AstFieldExpression)
			return this.find_field_expression((AstFieldExpression) location);
		else if(location instanceof AstSizeofExpression)
			return this.find_sizeof_expression((AstSizeofExpression) location);
		else if(location instanceof AstConditionalExpression)
			return this.find_conditional_expression((AstConditionalExpression) location);
		else if(location instanceof AstFunCallExpression)
			return this.find_fun_call_expression((AstFunCallExpression) location);
		else if(location instanceof AstGotoStatement)
			return this.find_goto_statement((AstGotoStatement) location);
		else if(location instanceof AstReturnStatement)
			return this.find_return_statement((AstReturnStatement) location);
		else if(location instanceof AstBreakStatement)
			return this.find_break_statement((AstBreakStatement) location);
		else if(location instanceof AstContinueStatement)
			return this.find_continue_statement((AstContinueStatement) location);
		else if(location instanceof AstLabeledStatement)
			return this.find_labeled_statement((AstLabeledStatement) location);
		else if(location instanceof AstCaseStatement)
			return this.find_case_statement((AstCaseStatement) location);
		else if(location instanceof AstDefaultStatement)
			return this.find_default_statement((AstDefaultStatement) location);
		else if(location instanceof AstDeclarationStatement)
			return this.find_declaration_statement((AstDeclarationStatement) location);
		else if(location instanceof AstExpressionStatement)
			return this.find_expression_statement((AstExpressionStatement) location);
		else if(location instanceof AstArgumentList)
			return this.find_argument_list((AstArgumentList) location);
		// TODO implement the syntax-directed path-traversal algorithms
		else
			throw new IllegalArgumentException("Unsupport: " + location);
	}
	/* declaration package */
	/**
	 * 	beg_stmt(declaration) 
	 * 		=={down_flow}==> 
	 * 	find{declaration.declarator_list}
	 * 		=={upon_flow}==>
	 * 	end_stmt(declaration)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_declaration(AstDeclaration location) throws Exception {
		InstrumentPathSolution solution = this.new_solution(location);
		
		/* beg_stmt[declaration] */
		if(this.has_line()) {
			solution.append(this.new_execution(AstExecutionUnit.beg_stmt(location)));
		}
		
		/* down_flow ==> find{init_declarator_list} */
		AstExecutionFlowType return_type;
		if(this.has_line()) {
			if(location.has_declarator_list()) {
				solution.append(AstExecutionFlowType.down_flow, 
						this.find(location.get_declarator_list()));
				return_type = AstExecutionFlowType.upon_flow;
			}
			else {
				return_type = AstExecutionFlowType.move_flow;
			}
		}
		else {
			return_type = null;
		}
		
		/* upon_flow | move_flow ==> end_stmt(declaration) */
		if(this.has_line()) {
			solution.append(return_type, this.new_execution(
					AstExecutionUnit.end_stmt(location)));
		}
		
		/* end without next node */	return solution;
	}
	/**
	 * 	beg_stmt(init_declarator_list)
	 * 		=={down_flow}==>
	 * 		find{init_declarators[0]}
	 * 		=={move_flow}==>
	 * 		find{init_declarators[1]}
	 * 		=={move_flow}==>
	 * 		......
	 * 		=={move_flow}==>
	 * 		find{init_declarators[n - 1]}
	 * 		=={upon_flow}==>
	 * 	end_stmt(init_declarator_list)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_init_declarator_list(AstInitDeclaratorList location) throws Exception {
		InstrumentPathSolution solution = this.new_solution(location);
		
		/* beg_stmt(init_declarator_list) */
		if(this.has_line()) {
			solution.append(this.new_execution(AstExecutionUnit.beg_stmt(location)));
		}
		
		/* down_flow ==> init_declarators[0], move_flow ==> init_declarators[k] */
		for(int k = 0; k < location.number_of_init_declarators(); k++) {
			if(this.has_line()) {
				if(k == 0) {
					solution.append(AstExecutionFlowType.down_flow,
							this.find(location.get_init_declarator(k)));
				}
				else {
					solution.append(AstExecutionFlowType.move_flow,
							this.find(location.get_init_declarator(k)));
				}
			}
		}
		
		/* upon_flow ==> end_stmt(init_declarator_list) */
		if(this.has_line()) {
			solution.append(this.new_execution(AstExecutionUnit.end_stmt(location)));
		}
		
		/* end without next node */	return solution;
	}
	/**
	 * 	beg_stmt(init_declarator)
	 * 		=={down_flow}==>
	 * 		find{init_declarator.declarator}
	 * 		=={move_flow}==>						?
	 * 		find{init_declarator.initializer}		?
	 * 		=={upon_flow}==>
	 * 	end_stmt(init_declarator)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_init_declarator(AstInitDeclarator location) throws Exception {
		InstrumentPathSolution solution = this.new_solution(location);
		
		/* beg_stmt(init_declarator) */
		if(this.has_line()) {
			solution.append(this.new_execution(
					AstExecutionUnit.beg_stmt(location)));
		}
		
		/* down_flow ==> find{init_declarator.declarator} */
		if(this.has_line()) {
			solution.append(AstExecutionFlowType.down_flow, 
					this.find(location.get_declarator()));
		}
		
		/* move_flow ==> find{init_declarator.initializer} */
		if(this.has_line()) {
			if(location.has_initializer())
				solution.append(AstExecutionFlowType.move_flow, 
						this.find(location.get_initializer()));
		}
		
		/* upon_flow ==> end_stmt(init_declarator) */
		if(this.has_line()) {
			solution.append(AstExecutionFlowType.upon_flow, 
					this.new_execution(AstExecutionUnit.end_stmt(location)));
		}
		
		/* end without next node */	return solution;
	}
	/**
	 * evaluate(declarator)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_declarator(AstDeclarator location) throws Exception {
		InstrumentPathSolution solution = this.new_solution(location);
		solution.append(this.new_execution(AstExecutionUnit.evaluate(location)));
		/* end without next node */	return solution;
	}
	/* initializer package */
	/**
	 * recursive method
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_initializer(AstInitializer location) throws Exception {
		if(location.is_body())
			return this.find(location.get_body());
		else
			return this.find(location.get_expression());
	}
	/**
	 * 	beg_expr(initializer_body)
	 * 		=={down_flow}==>
	 * 		find(initializer_body.list)
	 * 		=={upon_flow}
	 * 	end_expr(initializer_body)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_initializer_body(AstInitializerBody location) throws Exception {
		InstrumentPathSolution solution = this.new_solution(location);
		
		/* beg_expr(initializer_body) */
		if(this.has_line()) {
			solution.append(this.new_execution(AstExecutionUnit.beg_expr(location)));
		}
		
		/* down_flow ==> find(initializer_list) */
		if(this.has_line()) {
			solution.append(AstExecutionFlowType.down_flow, 
					this.find(location.get_initializer_list()));
		}
		
		/* upon_flow ==> end_expr(initializer_body) */
		if(this.has_line()) {
			solution.append(AstExecutionFlowType.upon_flow, this.
					new_execution(AstExecutionUnit.end_expr(location)));
		}
		
		/* end without next node */	return solution;
	}
	/**
	 * 	beg_expr(initializer_list)
	 * 		=={down_flow}==>
	 * 		find(initializer_list[0])
	 * 		=={move_flow}==>
	 * 		find(initializer_list[1])
	 * 		=={move_flow}==>
	 * 		......
	 * 		=={move_flow}==>
	 * 		find(initializer_list[n - 1])
	 * 		=={upon_flow}==>
	 * 	end_expr(initializer_list)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_initializer_list(AstInitializerList location) throws Exception {
		InstrumentPathSolution solution = this.new_solution(location);
		
		/* beg_expr(initializer_list) */
		if(this.has_line()) {
			solution.append(this.new_execution(AstExecutionUnit.beg_expr(location)));
		}
		
		/* down_flow | move_flow ==> initializer_list[k] */
		for(int k = 0; k < location.number_of_initializer(); k++) {
			if(this.has_line()) {
				if(k == 0) {
					solution.append(AstExecutionFlowType.down_flow,
							this.find(location.get_initializer(k)));
				}
				else {
					solution.append(AstExecutionFlowType.move_flow,
							this.find(location.get_initializer(k)));
				}
			}
		}
		
		/* upon_flow ==> end_expr(initializer_list) */
		if(this.has_line()) {
			solution.append(AstExecutionFlowType.upon_flow, this.
					new_execution(AstExecutionUnit.end_expr(location)));
		}
		
		/* end without next node */	return solution;
	}
	/**
	 * 	beg_expr{field_initializer}
	 * 		=={down_flow}==>
	 * 		find(initializer)
	 * 		=={move_flow}==>
	 * 	end_expr(field_initializer)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_field_initializer(AstFieldInitializer location) throws Exception {
		InstrumentPathSolution solution = this.new_solution(location);
		
		/* beg_expr(field_initializer) */
		if(this.has_line()) {
			solution.append(this.new_execution(AstExecutionUnit.beg_expr(location)));
		}
		
		/* down_flow ==> find(initializer) */
		if(this.has_line()) {
			solution.append(AstExecutionFlowType.down_flow, 
					this.find(location.get_initializer()));
		}
		
		/* upon_flow ==> end_expr(field_initializer) */
		if(this.has_line()) {
			solution.append(AstExecutionFlowType.upon_flow, this.
					new_execution(AstExecutionUnit.end_expr(location)));
		}
		
		/* end without next node */	return solution;
	}
	/* expression package */
	/**
	 * evaluate(id_expression)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_id_expression(AstIdExpression location) throws Exception {
		InstrumentPathSolution solution = this.new_solution(location);
		if(this.has_line()) {
			AstExecutionNode node = this.new_execution(
					AstExecutionUnit.evaluate(location));
			this.match(node); solution.append(node);
		}
		return solution;
	}
	/**
	 * evaluate(constant)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_constant(AstConstant location) throws Exception {
		InstrumentPathSolution solution = this.new_solution(location);
		if(this.has_line()) {
			AstExecutionNode node = this.new_execution(
					AstExecutionUnit.evaluate(location));
			node.get_unit().set_state(this.state_of(location.get_constant()));
			solution.append(node);
		}
		return solution;
	}
	/**
	 * evaluate(literal)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_literal(AstLiteral location) throws Exception {
		InstrumentPathSolution solution = this.new_solution(location);
		if(this.has_line()) {
			AstExecutionNode node = this.new_execution(
					AstExecutionUnit.evaluate(location));
			node.get_unit().set_state(this.state_of(location.get_literal()));
			solution.append(node);
		}
		return solution;
	}
	/**
	 * 	beg_expr(unary_expression)
	 * 		=={down_flow}==>
	 * 		find(unary_expression.operand)
	 * 		=={upon_flow}==>
	 * 	end_expr(unary_expression)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_unary_expression(AstUnaryExpression location) throws Exception {
		InstrumentPathSolution solution = this.new_solution(location);
		
		/* beg_expr(unary_expression) */
		if(this.has_line()) {
			solution.append(this.new_execution(AstExecutionUnit.beg_expr(location)));
		}
		
		/* down_flow ==> find(unary_expression.operand) */
		if(this.has_line()) {
			solution.append(AstExecutionFlowType.down_flow,
					this.find(location.get_operand()));
		}
		
		/* upon_flow ==> end_expr(unary_expression) */
		if(this.has_line()) {
			AstExecutionNode execution = this.new_execution(
						AstExecutionUnit.end_expr(location));
			this.match(execution);
			solution.append(AstExecutionFlowType.upon_flow, execution);
		}
		
		return solution;
	}
	/**
	 * 	beg_expr(unary_expression)
	 * 		=={down_flow}==>
	 * 		find(unary_expression.operand)
	 * 		=={upon_flow}==>
	 * 	end_expr(unary_expression)
	 * 	@param location
	 * 	@return
	 * 	@throws Exception
	 */
	private InstrumentPathSolution find_postfix_expression(AstPostfixExpression location) throws Exception {
		InstrumentPathSolution solution = this.new_solution(location);
		
		/* beg_expr(unary_expression) */
		if(this.has_line()) {
			solution.append(this.new_execution(AstExecutionUnit.beg_expr(location)));
		}
		
		/* down_flow ==> find(unary_expression.operand) */
		if(this.has_line()) {
			solution.append(AstExecutionFlowType.down_flow,
					this.find(location.get_operand()));
		}
		
		/* upon_flow ==> end_expr(unary_expression) */
		if(this.has_line()) {
			AstExecutionNode execution = this.new_execution(
						AstExecutionUnit.end_expr(location));
			this.match(execution);
			solution.append(AstExecutionFlowType.upon_flow, execution);
		}
		
		return solution;
	}
	/**
	 * 	beg_expr(binary_expression)
	 * 		=={down_flow}==>
	 * 		find(binary_expression.loperand)
	 * 		=={move_flow}==>
	 * 		find(binary_expression.roperand)
	 * 		=={upon_flow}==>
	 * 	end_expr(binary_expression)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_binary_expression(AstBinaryExpression location) throws Exception {
		InstrumentPathSolution solution = this.new_solution(location);
		
		/* beg_expr(expression) */
		if(this.has_line()) {
			solution.append(this.new_execution(AstExecutionUnit.beg_expr(location)));
		}
		
		/* down_flow ==> find(expression.loperand) */
		if(this.has_line()) {
			solution.append(AstExecutionFlowType.down_flow, 
					this.find(location.get_loperand()));
		}
		
		/* move_flow ==> find(expression.roperand) */
		if(this.has_line()) {
			solution.append(AstExecutionFlowType.move_flow,
					this.find(location.get_roperand()));
		}
		
		/* upon_flow ==> end_expr(expression) */
		if(this.has_line()) {
			AstExecutionNode execution = this.
					new_execution(AstExecutionUnit.end_expr(location));
			this.match(execution);
			solution.append(AstExecutionFlowType.upon_flow, execution);
		}
		
		return solution;
	}
	/**
	 * 	beg_expr(expression)
	 * 		=={down_flow}==>
	 * 		find(expression.array)
	 * 		=={move_flow}==>
	 * 		find(expression.dimension)
	 * 		=={upon_flow}==>
	 * 	end_expr(expression)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_array_expression(AstArrayExpression location) throws Exception {
		InstrumentPathSolution solution = this.new_solution(location);
		
		/* beg_expr(expression) */
		if(this.has_line()) {
			solution.append(this.new_execution(AstExecutionUnit.beg_expr(location)));
		}
		
		/* down_flow ==> find(expression.array) */
		if(this.has_line()) {
			solution.append(AstExecutionFlowType.down_flow, 
					this.find(location.get_array_expression()));
		}
		
		/* move_flow ==> find(expression.dimension) */
		if(this.has_line()) {
			solution.append(AstExecutionFlowType.move_flow,
					this.find(location.get_dimension_expression()));
		}
		
		/* upon_flow ==> end_expr(expression) */
		if(this.has_line()) {
			AstExecutionNode execution = this.
					new_execution(AstExecutionUnit.end_expr(location));
			this.match(execution);
			solution.append(AstExecutionFlowType.upon_flow, execution);
		}
		
		return solution;
	}
	/**
	 * 	beg_expr(expression)
	 * 		=={down_flow}==>
	 * 		find(expression.operand)
	 * 		=={upon_flow}==>
	 * 	end_expr(expression)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_cast_expression(AstCastExpression location) throws Exception {
		InstrumentPathSolution solution = this.new_solution(location);
		
		/* beg_expr(expression) */
		if(this.has_line()) {
			solution.append(this.new_execution(AstExecutionUnit.beg_expr(location)));
		}
		
		/* down_flow ==> find(expression.operand) */
		if(this.has_line()) {
			solution.append(AstExecutionFlowType.down_flow,
					this.find(location.get_expression()));
		}
		
		/* upon_flow ==> end_expr(expression) */
		if(this.has_line()) {
			AstExecutionNode execution = this.new_execution(
						AstExecutionUnit.end_expr(location));
			this.match(execution);
			solution.append(AstExecutionFlowType.upon_flow, execution);
		}
		
		return solution;
	}
	/**
	 * 	beg_expr(expression)
	 * 		=={down_flow}==>
	 * 		find(expressions[0])
	 * 		=={move_flow}==>
	 * 		find(expressions[1])
	 * 		=={move_flow}==>
	 * 		......
	 * 		=={move_flow}==>
	 * 		find(expressions[n - 1])
	 * 		=={upon_flow}==>
	 * 	end_expr(expression)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_comma_expression(AstCommaExpression location) throws Exception {
		InstrumentPathSolution solution = this.new_solution(location);
		
		if(this.has_line()) {
			solution.append(this.new_execution(AstExecutionUnit.beg_expr(location)));
		}
		
		for(int k = 0; k < location.number_of_arguments(); k++) {
			if(this.has_line()) {
				if(k == 0) {
					solution.append(AstExecutionFlowType.down_flow,
							this.find(location.get_expression(k)));
				}
				else {
					solution.append(AstExecutionFlowType.move_flow,
							this.find(location.get_expression(k)));
				}
			}
		}
		
		if(this.has_line()) {
			AstExecutionNode execution = this.new_execution(
						AstExecutionUnit.end_expr(location));
			this.match(execution);
			solution.append(AstExecutionFlowType.upon_flow, execution);
		}
		
		return solution;
	}
	/**
	 * recursive method
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_const_expression(AstConstExpression location) throws Exception {
		return this.find(location.get_expression());
	}
	/**
	 * recursive method
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_paranth_expression(AstParanthExpression location) throws Exception {
		return this.find(location.get_sub_expression());
	}
	/**
	 * 	beg_expr(expression)
	 * 		=={down_flow}==>
	 * 		find(expression.body)
	 * 		=={upon_flow}==>
	 * 	end_expr(expression)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_field_expression(AstFieldExpression location) throws Exception {
		InstrumentPathSolution solution = this.new_solution(location);
		
		if(this.has_line()) {
			solution.append(this.new_execution(AstExecutionUnit.beg_expr(location)));
		}
		
		if(this.has_line()) {
			solution.append(AstExecutionFlowType.down_flow, 
					this.find(location.get_body()));
		}
		
		if(this.has_line()) {
			AstExecutionNode execution = this.new_execution(
						AstExecutionUnit.end_expr(location));
			this.match(execution);
			solution.append(AstExecutionFlowType.upon_flow, execution);
		}
		
		return solution;
	}
	/**
	 * 	evaluate(expression)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_sizeof_expression(AstSizeofExpression location) throws Exception {
		InstrumentPathSolution solution = this.new_solution(location);
		
		if(this.has_line()) {
			AstExecutionNode execution = this.new_execution(
						AstExecutionUnit.evaluate(location));
			this.match(execution);
			solution.append(execution);
		}
		
		return solution;
	}
	/**
	 * 	beg_expr(expression)
	 * 		=={down_flow}==>
	 * 		find(expression.condition)
	 * 		=={goto_flow}==>
	 * 		find(expression.true|false_branch)
	 * 		=={upon_flow}==>
	 * 	end_expr(expression)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_conditional_expression(AstConditionalExpression location) throws Exception {
		InstrumentPathSolution solution = this.new_solution(location);
		
		if(this.has_line()) {
			solution.append(this.new_execution(AstExecutionUnit.beg_expr(location)));
		}
		
		if(this.has_line()) {
			solution.append(AstExecutionFlowType.down_flow, this.find(location.get_condition()));
		}
		
		if(this.has_line()) {
			if(this.get_condition_value(solution, location.get_condition())) {
				solution.append(AstExecutionFlowType.goto_flow, 
						this.find(location.get_true_branch()));
			}
			else {
				solution.append(AstExecutionFlowType.goto_flow, 
						this.find(location.get_false_branch()));
			}
		}
		
		if(this.has_line()) {
			AstExecutionNode execution = this.new_execution(
						AstExecutionUnit.end_expr(location));
			this.match(execution);
			solution.append(AstExecutionFlowType.upon_flow, execution);
		}
		
		return solution;
	}
	/**
	 * 	beg_expr(expression)
	 * 		=={down_flow}==>
	 * 		find(expression.function)
	 * 		=={move_flow}==>
	 * 		find(expression.argument_list)
	 * 		=={call_flow}==>
	 * 		find(next_callee_definition)
	 * 		=={retr_flow|upon_flow}==>
	 * 	end_expr(expression)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_fun_call_expression(AstFunCallExpression location) throws Exception {
		InstrumentPathSolution solution = this.new_solution(location);
		
		if(this.has_line()) {
			solution.append(this.new_execution(AstExecutionUnit.beg_expr(location)));
		}
		
		if(this.has_line()) {
			solution.append(AstExecutionFlowType.down_flow,
					this.find(location.get_function()));
		}
		
		if(this.has_line()) {
			if(location.has_argument_list()) {
				solution.append(AstExecutionFlowType.move_flow,
						this.find(location.get_argument_list()));
			}
		}
		
		/* decide whether it is calling a definition or return */
		if(this.has_line()) {
			/* case-1. directly return to the call-expression */
			if(this.get_line().get_location() == location) {
				AstExecutionNode execution = this.new_execution(
								AstExecutionUnit.end_expr(location));
				this.match(execution);
				solution.append(AstExecutionFlowType.upon_flow, execution);
			}
			/* case-2. calling another function definition */
			else {
				AstFunctionDefinition callee = (AstFunctionDefinition) 
							this.get_line().get_location().get_parent();
				solution.append(AstExecutionFlowType.call_flow, find(callee));
				
				AstExecutionNode execution = this.new_execution(
							AstExecutionUnit.end_expr(location));
				this.match(execution);
				solution.append(AstExecutionFlowType.retr_flow, execution);
			}
		}
		
		return solution;
	}
	/**
	 * 	beg_stmt(argument_list)
	 * 		=={down_flow}==>
	 * 		find(arguments[0])
	 * 		=={move_flow}==>
	 * 		find(arguments[1])
	 * 		=={move_flow}==>
	 * 		...
	 * 		=={move_flow}==>
	 * 		find(arguments[n - 1])
	 * 		=={upon_flow}==>
	 * 	end_stmt(argument_list)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_argument_list(AstArgumentList location) throws Exception {
		InstrumentPathSolution solution = this.new_solution(location);
		
		if(this.has_line()) {
			solution.append(this.new_execution(
					AstExecutionUnit.beg_stmt(location)));
		}
		
		for(int k = 0; k < location.number_of_arguments(); k++) {
			if(this.has_line()) {
				if(k == 0) {
					solution.append(AstExecutionFlowType.down_flow,
							this.find(location.get_argument(k)));
				}
				else {
					solution.append(AstExecutionFlowType.move_flow,
							this.find(location.get_argument(k)));
				}
			}
		}
		
		if(this.has_line()) {
			solution.append(this.new_execution(
					AstExecutionUnit.end_stmt(location)));
		}
		
		return solution;
	}
	/* statement package */
	/**
	 * execute(statement)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_break_statement(AstBreakStatement location) throws Exception {
		InstrumentPathSolution solution = this.new_solution(location);
		
		if(this.has_line()) {
			AstExecutionNode execution = this.new_execution(
						AstExecutionUnit.execute(location));
			this.match(execution);
			solution.append(execution);
		}
		
		if(this.has_line()) {
			solution.set_next_node(this.get_line().get_location());
		}
		
		return solution;
	}
	/**
	 * execute(statement)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_continue_statement(AstContinueStatement location) throws Exception {
		InstrumentPathSolution solution = this.new_solution(location);
		
		if(this.has_line()) {
			AstExecutionNode execution = this.new_execution(
						AstExecutionUnit.execute(location));
			this.match(execution);
			solution.append(execution);
		}
		
		if(this.has_line()) {
			solution.set_next_node(this.get_line().get_location());
		}
		
		return solution;
	}
	/**
	 * execute(statement)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_goto_statement(AstGotoStatement location) throws Exception {
		InstrumentPathSolution solution = this.new_solution(location);
		
		if(this.has_line()) {
			AstExecutionNode execution = this.new_execution(
						AstExecutionUnit.execute(location));
			this.match(execution);
			solution.append(execution);
		}
		
		if(this.has_line()) {
			solution.set_next_node(this.get_line().get_location());
		}
		
		return solution;
	}
	/**
	 * 	beg_statement(statement)
	 * 		=={down_flow}==>
	 * 		find(statement.expression)
	 * 		=={upon_flow}==>
	 * 	end_statement(statement)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_return_statement(AstReturnStatement location) throws Exception {
		InstrumentPathSolution solution = this.new_solution(location);
		
		if(this.has_line()) {
			if(location.has_expression()) {
				AstExecutionNode beg = this.new_execution(
						AstExecutionUnit.beg_expr(location));
				this.match(beg); solution.append(beg);
				
				solution.append(AstExecutionFlowType.down_flow, 
						this.find(location.get_expression()));
				
				solution.append(AstExecutionFlowType.upon_flow, this.
						new_execution(AstExecutionUnit.end_expr(location)));
			}
			else {
				AstExecutionNode execution = this.new_execution(
							AstExecutionUnit.execute(location));
				this.match(execution); solution.append(execution);
			}
		}
		
		return solution;
	}
	/**
	 * execute(statement)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_labeled_statement(AstLabeledStatement location) throws Exception {
		InstrumentPathSolution solution = this.new_solution(location);
		if(this.has_line()) {
			AstExecutionNode execution = this.new_execution(
						AstExecutionUnit.execute(location));
			this.match(execution);
			solution.append(execution);
		}
		return solution;
	}
	/**
	 * execute(statement)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_default_statement(AstDefaultStatement location) throws Exception {
		InstrumentPathSolution solution = this.new_solution(location);
		if(this.has_line()) {
			AstExecutionNode execution = this.new_execution(
						AstExecutionUnit.execute(location));
			this.match(execution);
			solution.append(execution);
		}
		return solution;
	}
	/**
	 * 	beg_stmt(statement)
	 * 		=={down_flow}==>
	 * 		find(statement.expression)
	 * 		=={upon_flow}==>
	 * 	end_stmt(statement)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_case_statement(AstCaseStatement location) throws Exception {
		InstrumentPathSolution solution = this.new_solution(location);
		
		if(this.has_line()) {
			solution.append(this.new_execution(AstExecutionUnit.beg_stmt(location)));
		}
		
		if(this.has_line()) {
			solution.append(AstExecutionFlowType.down_flow, 
					this.find(location.get_expression()));
		}
		
		if(this.has_line()) {
			AstExecutionNode execution = this.new_execution(
						AstExecutionUnit.end_stmt(location));
			this.match(execution); 
			solution.append(AstExecutionFlowType.upon_flow, execution);
		}
		
		return solution;
	}
	/**
	 * 	beg_stmt(statement)
	 * 		=={down_flow}==>
	 * 		find(statement.declaration)
	 * 		=={upon_flow}==>
	 * 	end_stmt(statement)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_declaration_statement(AstDeclarationStatement location) throws Exception {
		InstrumentPathSolution solution = this.new_solution(location);
		
		if(this.has_line()) {
			AstExecutionNode beg_execution = this.new_execution(
							AstExecutionUnit.beg_stmt(location));
			this.match(beg_execution);
			solution.append(beg_execution);
		}
		
		if(this.has_line()) {
			solution.append(AstExecutionFlowType.down_flow,
					this.find(location.get_declaration()));
		}
		
		if(this.has_line()) {
			AstExecutionNode end_execution = this.new_execution(
							AstExecutionUnit.end_stmt(location));
			this.match(end_execution);
			solution.append(AstExecutionFlowType.upon_flow, end_execution);
		}
		
		return solution;
	}
	/**
	 * 	beg_stmt(statement)
	 * 		=={down_flow}==>
	 * 		find(statement.expression)
	 * 		=={upon_flow}==>
	 * 	end_stmt(statement)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private InstrumentPathSolution find_expression_statement(AstExpressionStatement location) throws Exception {
		InstrumentPathSolution solution = this.new_solution(location);
		
		if(this.has_line()) {
			AstExecutionNode beg_execution = this.new_execution(
							AstExecutionUnit.beg_stmt(location));
			this.match(beg_execution);
			solution.append(beg_execution);
		}
		
		AstExecutionFlowType return_type;
		if(this.has_line()) {
			if(location.has_expression()) {
				solution.append(AstExecutionFlowType.down_flow,
						this.find(location.get_expression()));
				return_type = AstExecutionFlowType.upon_flow;
			}
			else {
				return_type = AstExecutionFlowType.move_flow;
			}
		}
		else {
			return_type = null;
		}
		
		if(this.has_line()) {
			AstExecutionNode end_execution = this.new_execution(
							AstExecutionUnit.end_stmt(location));
			this.match(end_execution);
			solution.append(return_type, end_execution);
		}
		
		return solution;
	}
	
	
}
