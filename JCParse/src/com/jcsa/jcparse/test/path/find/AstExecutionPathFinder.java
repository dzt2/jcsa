package com.jcsa.jcparse.test.path.find;

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
import com.jcsa.jcparse.lang.astree.stmt.AstCompoundStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstContinueStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDeclarationStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDefaultStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstGotoStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
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
import com.jcsa.jcparse.test.path.AstExecutionFlowType;
import com.jcsa.jcparse.test.path.AstExecutionNode;
import com.jcsa.jcparse.test.path.AstExecutionPath;
import com.jcsa.jcparse.test.path.AstExecutionUnit;
import com.jcsa.jcparse.test.path.InstrumentLine;
import com.jcsa.jcparse.test.path.InstrumentList;
import com.jcsa.jcparse.test.path.InstrumentType;

/**
 * find(prev_range, cur_line) |-- next_range
 * @author yukimula
 *
 */
public class AstExecutionPathFinder {
	
	/* definitions */
	private InstrumentListConsumer consumer;
	private AstExecutionPath execution_path;
	private AstExecutionPathFinder(InstrumentList list,
			AstExecutionPath execution_path) throws Exception {
		if(list == null)
			throw new IllegalArgumentException("No instrumental data provided");
		else if(execution_path == null)
			throw new IllegalArgumentException("No execution path being parsed");
		else {
			this.consumer = new InstrumentListConsumer(list);
			this.execution_path = execution_path;
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
		else {
			return true;
		}
	}
	/**
	 * @param node
	 * @return whether the expression is a valid point for instrumentation
	 * @throws Exception
	 */
	private boolean is_instrumental_expression(AstExpression node) throws Exception {
		return this.is_valid_context(node) 
				&& this.is_valid_expression(node)
				&& this.is_valid_type(node.get_value_type());
	}
	/**
	 * @param constant
	 * @return the byte-string representation of numeric constant.
	 * @throws Exception
	 */
	/*
	private byte[] get_state(CConstant constant) throws Exception {
		switch(constant.get_type().get_tag()) {
		case c_bool:	
		{
			if(constant.get_bool()) {
				return ByteBuffer.allocate(1).putChar((char) 1).array();
			}
			else {
				return ByteBuffer.allocate(1).putChar((char) 0).array();
			}
		}
		case c_char:
		case c_uchar:
		{
			return ByteBuffer.allocate(1).putChar(constant.get_char()).array();
		}
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:
		{
			return ByteBuffer.allocate(4).putInt(constant.get_integer()).array();
		}
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:
		{
			return ByteBuffer.allocate(8).putLong(constant.get_long()).array();
		}
		case c_float:
		{
			return ByteBuffer.allocate(4).putFloat(constant.get_float()).array();
		}
		case c_double:
		case c_ldouble:
		{
			return ByteBuffer.allocate(8).putDouble(constant.get_double()).array();
		}
		default: throw new IllegalArgumentException("Invalid type: " + constant);
		}
	}
	*/
	/**
	 * @param literal
	 * @return byte-string representation of string literal
	 * @throws Exception
	 */
	private byte[] get_state(String literal) throws Exception {
		byte[] state = new byte[literal.length() + 1];
		for(int k = 0; k < literal.length(); k++) {
			state[k] = (byte) literal.charAt(k);
		}
		state[literal.length()] = 0;
		return state;
	}
	/**
	 * @return whether there is instrumental line to consume
	 */
	private boolean has_line() {
		return this.consumer.has();
	}
	/**
	 * @return the instrumental line being consumed or null if path ends somewhere
	 */
	private InstrumentLine get_line() {
		return this.consumer.get();
	}
	/**
	 * @return create an empty execution range
	 */
	private AstExecutionRange new_range(AstNode location) {
		return new AstExecutionRange(location);
	}
	/**
	 * @param unit
	 * @return the execution node w.r.t. the unit to be performed in testing
	 * @throws Exception
	 */
	private AstExecutionNode new_execution(AstExecutionUnit unit) throws Exception {
		return this.execution_path.new_node(unit);
	}
	/**
	 * @param execution the final execution to be matched with current instrumental line
	 * @param exception whether to throw exception if the match failed.
	 * @return true if successfully matched and move the index or false
	 * @throws Exception
	 */
	private boolean match(AstExecutionNode execution, boolean exception) throws Exception {
		AstNode location = execution.get_unit().get_location();
		if(location instanceof AstExpression) {
			if(!this.is_instrumental_expression((AstExpression) location)) {
				return false;	/* to avoid non-instrumental location */
			}
		}
		
		if(this.consumer.match(execution)) {
			execution.get_unit().set_state(this.consumer.get().get_state());
			this.consumer.next();
			return true;
		}
		else if(exception) {
			throw new RuntimeException("Failed to match line of " + 
					this.consumer.get() + " with " + execution.get_unit());
		}
		else {
			return false;
		}
	}
	/**
	 * @param range
	 * @param condition
	 * @return the boolean value hold by the given condition evaluated in the range
	 * @throws Exception
	 */
	private boolean condition_value(AstExecutionRange range, AstExpression condition) throws Exception {
		condition = CTypeAnalyzer.get_expression_of(condition);
		AstExecutionNode execution = range.find(condition);
		if(execution != null) {
			return execution.get_unit().get_bool_state().booleanValue();
		}
		else {
			throw new IllegalArgumentException("No value is found for " + condition.
					get_code() + " at line " + condition.get_location().line_of());
		}
	}
	/**
	 * [statement, direct_child]
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private AstNode[] context_expression(AstExpression expression) throws Exception {
		AstNode child = expression, parent = expression.get_parent();
		while(!(parent instanceof AstStatement)) {
			child = parent;
			parent = parent.get_parent();
		}
		return new AstNode[] { parent, child };
	}
	
	/* closed-path-traversal-algorithm */
	/**
	 * @param location
	 * @return automatic path finding algorithm 
	 * @throws Exception
	 */
	private AstExecutionRange find(AstNode location) throws Exception {
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
		else if(location instanceof AstInitializerBody)
			return this.find_initializer_body((AstInitializerBody) location);
		else if(location instanceof AstInitializerList)
			return this.find_initializer_list((AstInitializerList) location);
		else if(location instanceof AstFieldInitializer)
			return this.find_field_initializer((AstFieldInitializer) location);
		else if(location instanceof AstIdExpression)
			return this.find_id_expression((AstIdExpression) location);
		else if(location instanceof AstConstant)
			return this.find_constant((AstConstant) location);
		else if(location instanceof AstLiteral)
			return this.find_literal((AstLiteral) location);
		else if(location instanceof AstUnaryExpression)
			return this.find_unary_expression((AstUnaryExpression) location);
		else if(location instanceof AstPostfixExpression)
			return this.find_postfix_expression((AstPostfixExpression) location);
		else if(location instanceof AstBinaryExpression)
			return this.find_binary_expression((AstBinaryExpression) location);
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
		else if(location instanceof AstExpressionStatement)
			return this.find_expression_statement((AstExpressionStatement) location);
		else if(location instanceof AstDeclarationStatement)
			return this.find_declaration_statement((AstDeclarationStatement) location);
		else if(location instanceof AstGotoStatement)
			return this.find_goto_statement((AstGotoStatement) location);
		else if(location instanceof AstReturnStatement)
			return this.find_return_statement((AstReturnStatement) location);
		else if(location instanceof AstBreakStatement)
			return this.find_break_statement((AstBreakStatement) location);
		else if(location instanceof AstContinueStatement)
			return this.find_continue_statement((AstContinueStatement) location);
		else if(location instanceof AstCaseStatement)
			return this.find_case_statement((AstCaseStatement) location);
		else if(location instanceof AstDefaultStatement)
			return this.find_default_statement((AstDefaultStatement) location);
		else if(location instanceof AstLabeledStatement)
			return this.find_labeled_statement((AstLabeledStatement) location);
		else if(location instanceof AstIfStatement)
			return this.find_if_statement((AstIfStatement) location);
		else if(location instanceof AstSwitchStatement)
			return this.find_switch_statement((AstSwitchStatement) location);
		else if(location instanceof AstWhileStatement)
			return this.find_while_statement((AstWhileStatement) location);
		else if(location instanceof AstDoWhileStatement)
			return this.find_do_while_statement((AstDoWhileStatement) location);
		else if(location instanceof AstCompoundStatement)
			return this.find_compound_statement((AstCompoundStatement) location);
		else if(location instanceof AstFunctionDefinition)
			return this.find_function_definition((AstFunctionDefinition) location);
		else
			throw new IllegalArgumentException("Unsupport: " + location);
	}
	
	/* declaration package */
	/**
	 * 	beg_stmt(declaration)
	 * 		==(down_flow)==>
	 * 		find(declaration.init_declarator_list)
	 * 		==(upon_flow)==>
	 * 	end_stmt(declaration)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_declaration(AstDeclaration location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		if(this.has_line()) {
			/**
			 * 	step-1. beg_stmt(declaration)					
			 * 	step-2. down_flow::find(init_declarator_list)
			 * 	step-3. upon_flow::end_stmt(declaration)		
			 **/
			if(location.has_declarator_list()) {
				/* beg_stmt(declaration) */
				if(this.has_line()) {
					range.append(this.new_execution(AstExecutionUnit.beg_stmt(location)));
				}
				
				/* down_flow::find(init_declarator_list) */
				if(this.has_line()) {
					range.append(AstExecutionFlowType.down_flow, 
							this.find(location.get_declarator_list()));
				}
				
				/* upon_flow::end_stmt(declaration) */
				if(this.has_line()) {
					range.append(AstExecutionFlowType.upon_flow, 
							this.new_execution(AstExecutionUnit.end_stmt(location)));
				}
			}
			/**
			 * 	step-1. execute(declaration)
			 **/
			else {
				/* execute(declaration) */
				if(this.has_line()) {
					range.append(this.new_execution(AstExecutionUnit.execute(location)));
				}
			}
		}
		return range;
	}
	/**
	 * 	beg_stmt(declarator_list)
	 * 		down_flow::find(declarators[0])
	 * 		move_flow::find(declarators[1])
	 * 		...
	 * 		move_flow::find(declarators[n - 1])
	 * 	upon_flow::end_stmt(declarator_list)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_init_declarator_list(AstInitDeclaratorList location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		
		/* beg_stmt(declarator_list) */
		if(this.has_line()) {
			range.append(this.new_execution(AstExecutionUnit.beg_stmt(location)));
		}
		
		/* down_flow|move_flow::find(init_declarator) */
		for(int k = 0; k < location.number_of_init_declarators(); k++) {
			if(this.has_line()) {
				if(k == 0) {
					range.append(AstExecutionFlowType.down_flow, 
							this.find(location.get_init_declarator(k)));
				}
				else {
					range.append(AstExecutionFlowType.move_flow, 
							this.find(location.get_init_declarator(k)));
				}
			}
		}
		
		/* upon_flow::end_stmt(declarator-list) */
		if(this.has_line()) {
			range.append(AstExecutionFlowType.upon_flow, this.
					new_execution(AstExecutionUnit.end_stmt(location)));
		}
		
		return range;
	}
	/**
	 * 	beg_stmt(init_declarator)
	 * 		down_flow::find(declarator)
	 * 		move_flow::find(initializer)
	 * 	upon_flow::end_stmt(init_declarator)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_init_declarator(AstInitDeclarator location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		
		/* beg_stmt(init_declarator) */
		if(this.has_line()) {
			range.append(this.new_execution(AstExecutionUnit.beg_stmt(location)));
		}
		
		/* down_flow::find(declarator) */
		if(this.has_line()) {
			range.append(AstExecutionFlowType.down_flow, 
					this.find(location.get_declarator()));
		}
		
		/* move_flow::find(initializer) */
		if(this.has_line()) {
			if(location.has_initializer()) {
				range.append(AstExecutionFlowType.move_flow, 
						this.find(location.get_initializer()));
			}
		}
		
		/* upon_flow::end_stmt(init_declarator) */
		if(this.has_line()) {
			range.append(AstExecutionFlowType.upon_flow, this.
					new_execution(AstExecutionUnit.end_stmt(location)));
		}
		
		return range;
	}
	/**
	 * 	execute(declarator)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_declarator(AstDeclarator location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		if(this.has_line()) {
			range.append(this.new_execution(AstExecutionUnit.execute(location)));
		}
		return range;
	}
	/* initializer package */
	/**
	 * recursive finding
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_initializer(AstInitializer location) throws Exception {
		if(location.is_body())
			return this.find(location.get_body());
		else
			return this.find(location.get_expression());
	}
	/**
	 * 	beg_expr(initializer_body)
	 * 	down_flow::find(initializer_list)
	 * 	upon_flow::end_expr(initializer_body)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_initializer_body(AstInitializerBody location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		
		/* beg_expr(initializer_body) */
		if(this.has_line()) {
			range.append(this.new_execution(AstExecutionUnit.beg_expr(location)));
		}
		
		/* down_flow::find(initializer_list) */
		if(this.has_line()) {
			range.append(AstExecutionFlowType.down_flow, 
					this.find(location.get_initializer_list()));
		}
		
		/* upon_flow::end_expr(initializer_body) */
		if(this.has_line()) {
			range.append(AstExecutionFlowType.upon_flow, this.
					new_execution(AstExecutionUnit.end_expr(location)));
		}
		
		return range;
	}
	/**
	 * 	beg_expr(initializer_list)
	 * 	down_flow::find(field_initializer[0])
	 * 	move_flow::find(field_initializer[1])
	 * 	...
	 * 	move_flow::find(field_initializer[n - 1])
	 * 	upon_flow::end_expr(initializer_list)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_initializer_list(AstInitializerList location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		
		/* beg_expr(initializer_list) */
		if(this.has_line()) {
			range.append(this.new_execution(AstExecutionUnit.beg_expr(location)));
		}
		
		/* down_flow|move_flow::find(field_initializers[k]) */
		for(int k = 0; k < location.number_of_initializer(); k++) {
			if(this.has_line()) {
				if(k == 0) {
					range.append(AstExecutionFlowType.down_flow, 
							this.find(location.get_initializer(k)));
				}
				else {
					range.append(AstExecutionFlowType.move_flow, 
							this.find(location.get_initializer(k)));
				}
			}
		}
		
		/* upon_flow::end_expr(initializer_list) */
		if(this.has_line()) {
			range.append(AstExecutionFlowType.upon_flow, this.
					new_execution(AstExecutionUnit.end_expr(location)));
		}
		
		return range;
	}
	/**
	 * 	beg_expr(field_initializer)
	 * 	down_flow::find(initializer)
	 * 	upon_flow::end_expr(field_initializer)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_field_initializer(AstFieldInitializer location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		
		/* beg_expr(field_initializer) */
		if(this.has_line()) {
			range.append(this.new_execution(AstExecutionUnit.beg_expr(location)));
		}
		
		/* down_flow::find(initializer) */
		if(this.has_line()) {
			range.append(AstExecutionFlowType.down_flow, 
					this.find(location.get_initializer()));
		}
		
		/* upon_flow::end_expr(field_initializer) */
		if(this.has_line()) {
			range.append(AstExecutionFlowType.upon_flow, this.
					new_execution(AstExecutionUnit.end_expr(location)));
		}
		
		return range;
	}
	/* expression package */
	/**
	 * evaluate(id_expression)	#match
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_id_expression(AstIdExpression location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		if(this.has_line()) {
			AstExecutionNode execution = this.new_execution(
						AstExecutionUnit.evaluate(location));
			this.match(execution, true); 
			range.append(execution);
		}
		return range;
	}
	/**
	 * evaluate(constant)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_constant(AstConstant location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		if(this.has_line()) {
			AstExecutionNode execution = 
					this.new_execution(AstExecutionUnit.evaluate(location));
			this.match(execution, true);
			range.append(execution);
		}
		return range;
	}
	/**
	 * evaluate(literal)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_literal(AstLiteral location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		if(this.has_line()) {
			AstExecutionNode execution = 
					this.new_execution(AstExecutionUnit.evaluate(location));
			execution.get_unit().set_state(get_state(location.get_literal()));
			range.append(execution);
		}
		return range;
	}
	/**
	 * 	beg_expr(expression)
	 * 	down_flow::find(expression.operand)
	 * 	upon_flow::end_expr(expression)		#match
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_unary_expression(AstUnaryExpression location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		
		/* beg_expr(expression) */
		if(this.has_line()) {
			range.append(this.new_execution(AstExecutionUnit.beg_expr(location)));
		}
		
		/* down_flow::find(operand) */
		if(this.has_line()) {
			range.append(AstExecutionFlowType.down_flow, find(location.get_operand()));
		}
		
		/* upon_flow::end_expr(expression) */
		if(this.has_line()) {
			AstExecutionNode end_execution = this.
					new_execution(AstExecutionUnit.end_expr(location));
			this.match(end_execution, true);
			range.append(AstExecutionFlowType.upon_flow, end_execution);
		}
		
		return range;
	}
	/**
	 * 	beg_expr(expression)
	 * 	down_flow::find(expression.operand)
	 * 	upon_flow::end_expr(expression)		#match
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_postfix_expression(AstPostfixExpression location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		
		/* beg_expr(expression) */
		if(this.has_line()) {
			range.append(this.new_execution(AstExecutionUnit.beg_expr(location)));
		}
		
		/* down_flow::find(operand) */
		if(this.has_line()) {
			range.append(AstExecutionFlowType.down_flow, find(location.get_operand()));
		}
		
		/* upon_flow::end_expr(expression) */
		if(this.has_line()) {
			AstExecutionNode end_execution = this.
					new_execution(AstExecutionUnit.end_expr(location));
			this.match(end_execution, true);
			range.append(AstExecutionFlowType.upon_flow, end_execution);
		}
		
		return range;
	}
	/**
	 * 	beg_expr(expression)
	 * 	down_flow::find(expression.loperand)
	 * 	move_flow::find(expression.roperand)
	 * 	upon_flow::end_expr(expression)			#match
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_binary_expression(AstBinaryExpression location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		
		/* beg_expr(expression) */
		if(this.has_line()) {
			range.append(this.new_execution(AstExecutionUnit.beg_expr(location)));
		}
		
		/* down_flow::find(expression.loperand) */
		if(this.has_line()) {
			range.append(AstExecutionFlowType.down_flow, find(location.get_loperand()));
		}
		
		/* move_flow::find(expression.roperand) */
		if(this.has_line()) {
			range.append(AstExecutionFlowType.move_flow, find(location.get_roperand()));
		}
		
		/* upon_flow::end_expr(expression)	#match */
		if(this.has_line()) {
			AstExecutionNode end_execution = this.
					new_execution(AstExecutionUnit.end_expr(location));
			this.match(end_execution, true);
			range.append(AstExecutionFlowType.upon_flow, end_execution);
		}
		
		return range;
	}
	/**
	 * 	beg_expr(expression)
	 * 	down_flow::find(array)
	 * 	move_flow::find(index)
	 * 	upon_flow::end_expr(expression)		#match
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_array_expression(AstArrayExpression location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		
		/* beg_expr(expression) */
		if(this.has_line()) {
			range.append(this.new_execution(AstExecutionUnit.beg_expr(location)));
		}
		
		/* down_flow::find(array) */
		if(this.has_line()) {
			range.append(AstExecutionFlowType.down_flow, 
					this.find(location.get_array_expression()));
		}
		
		/* move_flow::find(index) */
		if(this.has_line()) {
			range.append(AstExecutionFlowType.move_flow, 
					this.find(location.get_dimension_expression()));
		}
		
		/* upon_flow::end_expr(expression)  #match */
		if(this.has_line()) {
			AstExecutionNode end_execution = this.
					new_execution(AstExecutionUnit.end_expr(location));
			this.match(end_execution, true);
			range.append(AstExecutionFlowType.move_flow, end_execution);
		}
		
		return range;
	}
	/**
	 * 	beg_expr(expression)
	 * 	down_flow::find(expression.operand)
	 * 	upon_flow::end_expr(expression)		#match
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_cast_expression(AstCastExpression location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		
		/* beg_expr(expression) */
		if(this.has_line()) {
			range.append(this.new_execution(AstExecutionUnit.beg_expr(location)));
		}
		
		/* down_flow::find(operand) */
		if(this.has_line()) {
			range.append(AstExecutionFlowType.down_flow, 
					this.find(location.get_expression()));
		}
		
		/* upon_flow::end_expr(expression)	#match */
		if(this.has_line()) {
			AstExecutionNode end_execution = this.
					new_execution(AstExecutionUnit.end_expr(location));
			this.match(end_execution, true);
			range.append(AstExecutionFlowType.upon_flow, end_execution);
		}
		
		return range;
	}
	/**
	 * 	beg_expr(expression)
	 * 	down_flow::find(operands[0])
	 * 	move_flow::find(operands[1])
	 * 	...
	 * 	move_flow::find(operands[n - 1])
	 * 	upon_flow::end_expr(expression)		#match
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_comma_expression(AstCommaExpression location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		
		/* beg_expr(expression) */
		if(this.has_line()) {
			range.append(this.new_execution(AstExecutionUnit.beg_expr(location)));
		}
		
		/* down_flow::find(operands[0]) */
		for(int k = 0; k < location.number_of_arguments(); k++) {
			if(this.has_line()) {
				if(k == 0) {
					range.append(AstExecutionFlowType.down_flow,
							this.find(location.get_expression(k)));
				}
				else {
					range.append(AstExecutionFlowType.move_flow,
							this.find(location.get_expression(k)));
				}
			}
		}
		
		/* upon_flow::end_expr(expression)	#match */
		if(this.has_line()) {
			AstExecutionNode end_execution = this.
					new_execution(AstExecutionUnit.end_expr(location));
			this.match(end_execution, true);
			range.append(AstExecutionFlowType.upon_flow, end_execution);
		}
		
		return range;
	}
	/**
	 * recursive method
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_const_expression(AstConstExpression location) throws Exception {
		return this.find(location.get_expression());
	}
	/**
	 * recursive method
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_paranth_expression(AstParanthExpression location) throws Exception {
		return this.find(location.get_sub_expression());
	}
	/**
	 * 	beg_expr(expression)
	 * 	down_flow::find(body)
	 * 	upon_flow::end_expr(expression)		#match
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_field_expression(AstFieldExpression location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		
		/* beg_expr(expression) */
		if(this.has_line()) {
			range.append(this.new_execution(AstExecutionUnit.beg_expr(location)));
		}
		
		/* down_flow::find(body) */
		if(this.has_line()) {
			range.append(AstExecutionFlowType.down_flow, this.find(location.get_body()));
		}
		
		/* upon_flow::end_expr(expression)	#match */
		if(this.has_line()) {
			AstExecutionNode end_execution = this.
					new_execution(AstExecutionUnit.end_expr(location));
			this.match(end_execution, true);
			range.append(AstExecutionFlowType.upon_flow, end_execution);
		}
		
		return range;
	}
	/**
	 * 	evaluate(expression)		#match
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_sizeof_expression(AstSizeofExpression location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		if(this.has_line()) {
			AstExecutionNode beg_execution = this.
					new_execution(AstExecutionUnit.evaluate(location));
			this.match(beg_execution, true);
			range.append(beg_execution);
		}
		return range;
	}
	/**
	 * 	beg_expr(expression)
	 * 	down_flow::find(condition)
	 * 	goto_flow::find(true_or_false_branch)
	 * 	upon_flow::end_expr(expression)			#match
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_conditional_expression(AstConditionalExpression location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		
		/* beg_expr(expression) */
		if(this.has_line()) {
			range.append(this.new_execution(AstExecutionUnit.beg_expr(location)));
		}
		
		/* down_flow::find(condition) */
		if(this.has_line()) {
			range.append(AstExecutionFlowType.down_flow, find(location.get_condition()));
		}
		
		/* decide the branch being selected */
		if(this.has_line()) {
			if(this.condition_value(range, location.get_condition())) {
				range.append(AstExecutionFlowType.move_flow, find(location.get_true_branch()));
			}
			else {
				range.append(AstExecutionFlowType.move_flow, find(location.get_false_branch()));
			}
		}
		
		/* upon_flow::end_expr(expression)		#match */
		if(this.has_line()) {
			AstExecutionNode end_execution = this.
					new_execution(AstExecutionUnit.end_expr(location));
			this.match(end_execution, true);
			range.append(AstExecutionFlowType.upon_flow, end_execution);
		}
		
		return range;
	}
	/**
	 * 	beg_expr(expression)
	 * 	down_flow::find(function)
	 * 	move_flow::find(argument_list)
	 * 	[call_flow::find(fun_definition)]
	 * 	retr_flow::end_expr(expression)				#match
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_fun_call_expression(AstFunCallExpression location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		
		/* beg_expr(expression) */
		if(this.has_line()) {
			range.append(this.new_execution(AstExecutionUnit.beg_expr(location)));
		}
		
		/* down_flow::find(function) */
		if(this.has_line()) {
			range.append(AstExecutionFlowType.down_flow, find(location.get_function()));
		}
		
		/* move_flow::find(argument_list) */
		if(this.has_line()) {
			if(location.has_argument_list()) {
				range.append(AstExecutionFlowType.move_flow, find(location.get_argument_list()));
			}
		}
		
		/* call_flow::find(fun_definition) */
		AstExecutionFlowType return_flow_type;
		if(this.has_line()) {
			AstNode next_location = this.get_line().get_location();
			if(this.get_line().get_type() == InstrumentType.beg_stmt && 
					next_location instanceof AstCompoundStatement &&
					next_location.get_parent() instanceof AstFunctionDefinition) {
				range.append(AstExecutionFlowType.call_flow, this.find(next_location.get_parent()));
				return_flow_type = AstExecutionFlowType.retr_flow;
			}
			else {
				return_flow_type = AstExecutionFlowType.upon_flow;
			}
		}
		else {
			return_flow_type = null;
		}
		
		/* retr_flow|upon_flow::end_expr(expression) */
		if(this.has_line()) {
			AstExecutionNode end_execution = this.
					new_execution(AstExecutionUnit.end_expr(location));
			this.match(end_execution, true);
			range.append(return_flow_type, end_execution);
		}
		
		return range;
	}
	/* statement package */
	/**
	 * 	beg_stmt(statement)						#match
	 * 	down_flow::find(expression)
	 * 	upon_flow::end_stmt(statement)			#match
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_expression_statement(AstExpressionStatement location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		
		/* beg_stmt(statement)		#match */
		if(this.has_line()) {
			AstExecutionNode beg_execution = this.
					new_execution(AstExecutionUnit.beg_stmt(location));
			this.match(beg_execution, true);
			range.append(beg_execution);
		}
		
		/* down_flow::find(expression) */
		AstExecutionFlowType return_flow_type;
		if(this.has_line()) {
			if(location.has_expression()) {
				range.append(AstExecutionFlowType.down_flow, find(location.get_expression()));
				return_flow_type = AstExecutionFlowType.upon_flow;
			}
			else {
				return_flow_type = AstExecutionFlowType.move_flow;
			}
		}
		else {
			return_flow_type = null;
		}
		
		/* return_flow::end_stmt(statement) 	#<match> */
		if(this.has_line()) {
			AstExecutionNode end_execution = this.
					new_execution(AstExecutionUnit.end_stmt(location));
			this.match(end_execution, false);
			range.append(return_flow_type, end_execution);
		}
		
		return range;
	}
	/**
	 * 	beg_stmt(statement)						#match
	 * 	down_flow::find(declaration)
	 * 	upon_flow::end_stmt(statement)			#match!
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_declaration_statement(AstDeclarationStatement location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		
		/* beg_stmt(statement) */
		if(this.has_line()) {
			AstExecutionNode beg_execution = this.
					new_execution(AstExecutionUnit.beg_stmt(location));
			this.match(beg_execution, false);
			range.append(beg_execution);
		}
		
		/* down_flow::find(declaration) */
		if(this.has_line()) {
			range.append(AstExecutionFlowType.down_flow, find(location.get_declaration()));
		}
		
		/* upon_flow::end_stmt(statement) */
		if(this.has_line()) {
			AstExecutionNode end_execution = this.
					new_execution(AstExecutionUnit.end_stmt(location));
			this.match(end_execution, false);
			range.append(AstExecutionFlowType.upon_flow, end_execution);
		}
		
		return range;
	}
	/**
	 * 	execute(statement)			#match
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_goto_statement(AstGotoStatement location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		if(this.has_line()) {
			AstExecutionNode beg_execution = this.
					new_execution(AstExecutionUnit.execute(location));
			this.match(beg_execution, true);
			range.append(beg_execution);
		}
		return range;
	}
	/**
	 * 	execute(statement)			#match
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_break_statement(AstBreakStatement location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		if(this.has_line()) {
			AstExecutionNode beg_execution = this.
					new_execution(AstExecutionUnit.execute(location));
			this.match(beg_execution, true);
			range.append(beg_execution);
		}
		return range;
	}
	/**
	 * 	execute(statement)			#match
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_continue_statement(AstContinueStatement location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		if(this.has_line()) {
			AstExecutionNode beg_execution = this.
					new_execution(AstExecutionUnit.execute(location));
			this.match(beg_execution, true);
			range.append(beg_execution);
		}
		return range;
	}
	/**
	 * 	execute(statement)			#match
	 * 	----------------------------------
	 * 	beg_stmt(statement)			#match
	 * 	down_flow::find(expression)
	 * 	upon_flow::end_stmt(statement)
	 * 	----------------------------------
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_return_statement(AstReturnStatement location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		/**
		 * 	beg_stmt(statement)			#match
		 * 	down_flow::find(expression)
		 * 	upon_flow::end_stmt(statement)
		 */
		if(location.has_expression()) {
			/* beg_stmt(statement)   #match */
			if(this.has_line()) {
				AstExecutionNode beg_execution = this.
						new_execution(AstExecutionUnit.beg_stmt(location));
				this.match(beg_execution, true);
				range.append(beg_execution);
			}
			/* down_flow::find(expression) */
			if(this.has_line()) {
				range.append(AstExecutionFlowType.down_flow, find(location.get_expression()));
			}
			/* upon_flow::end_stmt(statement) */
			if(this.has_line()) {
				range.append(AstExecutionFlowType.upon_flow, 
						this.new_execution(AstExecutionUnit.end_stmt(location)));
			}
		}
		/**
		 * 	execute(statement)			#match
		 */
		else {
			if(this.has_line()) {
				AstExecutionNode beg_execution = this.
						new_execution(AstExecutionUnit.execute(location));
				this.match(beg_execution, true);
				range.append(beg_execution);
			}
		}
		return range;
	}
	/**
	 * execute(statement)		#match
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_labeled_statement(AstLabeledStatement location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		if(this.has_line()) {
			AstExecutionNode beg_execution = this.
					new_execution(AstExecutionUnit.execute(location));
			this.match(beg_execution, true);
			range.append(beg_execution);
		}
		return range;
	}
	/**
	 * execute(statement)		#match
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_default_statement(AstDefaultStatement location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		if(this.has_line()) {
			AstExecutionNode beg_execution = this.
					new_execution(AstExecutionUnit.execute(location));
			this.match(beg_execution, true);
			range.append(beg_execution);
		}
		return range;
	}
	/**
	 * 	beg_stmt(statement)
	 * 	down_flow::find(expression)
	 * 	upon_flow::end_stmt(statement)				#match
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_case_statement(AstCaseStatement location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		
		/* beg_stmt(statement) */
		if(this.has_line()) {
			range.append(this.new_execution(AstExecutionUnit.beg_stmt(location)));
		}
		
		/* down_flow::find(expression) */
		if(this.has_line()) {
			range.append(AstExecutionFlowType.down_flow, find(location.get_expression()));
		}
		
		/* upon_flow::end_stmt(statement) */
		if(this.has_line()) {
			AstExecutionNode end_execution = this.
					new_execution(AstExecutionUnit.end_stmt(location));
			this.match(end_execution, true);
			range.append(AstExecutionFlowType.upon_flow, end_execution);
		}
		
		return range;
	}
	/**
	 * 	beg_stmt(statement)
	 * 	down_flow::find(condition)
	 * 	upon_flow::end_stmt(statement)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_if_statement(AstIfStatement location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		
		/* beg_stmt(statement) */
		if(this.has_line()) {
			range.append(this.new_execution(AstExecutionUnit.beg_stmt(location)));
		}
		
		/* down_flow::find(condition) */
		if(this.has_line()) {
			range.append(AstExecutionFlowType.down_flow, find(location.get_condition()));
		}
		
		/* upon_flow::end_stmt(statement) */
		if(this.has_line()) {
			range.append(AstExecutionFlowType.upon_flow, 
					this.new_execution(AstExecutionUnit.end_stmt(location)));
		}
		
		return range;
	}
	/**
	 * 	beg_stmt(statement)
	 * 	down_flow::find(condition)
	 * 	upon_flow::end_stmt(statement)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_switch_statement(AstSwitchStatement location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		
		/* beg_stmt(statement) */
		if(this.has_line()) {
			range.append(this.new_execution(AstExecutionUnit.beg_stmt(location)));
		}
		
		/* down_flow::find(condition) */
		if(this.has_line()) {
			range.append(AstExecutionFlowType.down_flow, this.find(location.get_condition()));
		}
		
		/* upon_flow::end_stmt(statement) */
		if(this.has_line()) {
			range.append(AstExecutionFlowType.upon_flow, 
					this.new_execution(AstExecutionUnit.end_stmt(location)));
		}
		
		return range;
	}
	/**
	 * 	beg_stmt(statement)
	 * 	down_flow::find(condition)
	 * 	upon_flow::end_stmt(statement)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_while_statement(AstWhileStatement location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		
		/* beg_stmt(statement) */
		if(this.has_line()) {
			range.append(this.new_execution(AstExecutionUnit.beg_stmt(location)));
		}
		
		/* down_flow::find(condition) */
		if(this.has_line()) {
			range.append(AstExecutionFlowType.down_flow, find(location.get_condition()));
		}
		
		/* upon_flow::end_stmt(statement) */
		if(this.has_line()) {
			range.append(AstExecutionFlowType.upon_flow, 
					this.new_execution(AstExecutionUnit.end_stmt(location)));
		}
		
		return range;
	}
	/**
	 * 	beg_stmt(statement)
	 * 	down_flow::find(condition)
	 * 	upon_flow::end_stmt(statement)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_do_while_statement(AstDoWhileStatement location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		
		/* beg_stmt(statement) */
		if(this.has_line()) {
			range.append(this.new_execution(AstExecutionUnit.beg_stmt(location)));
		}
		
		/* down_flow::find(condition) */
		if(this.has_line()) {
			range.append(AstExecutionFlowType.down_flow, find(location.get_condition()));
		}
		
		/* upon_flow::end_stmt(statement) */
		if(this.has_line()) {
			range.append(AstExecutionFlowType.upon_flow, 
					this.new_execution(AstExecutionUnit.end_stmt(location)));
		}
		
		return range;
	}
	/***
	 * beg_stmt|end_stmt(statement)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_compound_statement(AstCompoundStatement location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		if(this.has_line()) {
			switch(this.get_line().get_type()) {
			case beg_stmt:	
				range.append(this.new_execution(AstExecutionUnit.beg_stmt(location))); break;
			case end_stmt:	
				range.append(this.new_execution(AstExecutionUnit.end_stmt(location))); break;
			default: throw new IllegalArgumentException("Invalid line: " + this.get_line());
			}
		}
		return range;
	}
	/**
	 * 	beg_func(definition)
	 * 	traverse_function(definition)
	 * 	end_func(definition)
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange find_function_definition(AstFunctionDefinition location) throws Exception {
		AstExecutionRange range = this.new_range(location);
		
		/* beg_func(definition) */
		if(this.has_line()) {
			range.append(this.new_execution(AstExecutionUnit.beg_func(location)));
			range.append(AstExecutionFlowType.down_flow, traverse_on(location));
			range.append(AstExecutionFlowType.upon_flow, 
					this.new_execution(AstExecutionUnit.end_func(location)));
		}
		
		return range;
	}
	/**
	 * perform the linear traversal on the function definition
	 * @param definition
	 * @return
	 * @throws Exception
	 */
	private AstExecutionRange traverse_on(AstFunctionDefinition definition) throws Exception {
		AstExecutionRange range = this.new_range(definition.get_body());
		/* perform instrumental-driven algorithm */
		while(this.has_line()) {
			AstNode next_location = this.get_line().get_location();
			
			/* decide the range of the next right location */
			AstExecutionRange local_range;
			if(next_location instanceof AstStatement) {
				local_range = this.find(next_location);
			}
			else if(next_location instanceof AstExpression) {
				AstNode[] statement_child = this.
						context_expression((AstExpression) next_location);
				AstStatement statement = (AstStatement) statement_child[0];
				AstNode direct_child = statement_child[1];
				
				/* if_statement.condition */
				if(statement instanceof AstIfStatement) {
					if(((AstIfStatement) statement).get_condition() == direct_child) {
						local_range = this.find(statement);
					}
					else {
						throw new RuntimeException("Invalid pair: " + direct_child);
					}
				}
				/* switch_statement.condition */
				else if(statement instanceof AstSwitchStatement) {
					if(((AstSwitchStatement) statement).get_condition() == direct_child) {
						local_range = this.find(statement);
					}
					else {
						throw new RuntimeException("Invalid pair: " + direct_child);
					}
				}
				/* while_statement.condition */
				else if(statement instanceof AstWhileStatement) {
					if(((AstWhileStatement) statement).get_condition() == direct_child) {
						local_range = this.find(statement);
					}
					else {
						throw new RuntimeException("Invalid pair: " + direct_child);
					}
				}
				/* do_while_statement.condition */
				else if(statement instanceof AstDoWhileStatement) {
					if(((AstDoWhileStatement) statement).get_condition() == direct_child) {
						local_range = this.find(statement);
					}
					else {
						throw new RuntimeException("Invalid pair: " + direct_child);
					}
				}
				/* for_statement.increment */
				else if(statement instanceof AstForStatement) {
					if(((AstForStatement) statement).get_increment() == direct_child) {
						local_range = this.find(((AstForStatement) statement).get_increment());
					}
					else {
						throw new RuntimeException("Invalid pair: " + direct_child);
					}
				}
				else {
					throw new RuntimeException("Unable to interpret at " + statement);
				}
			}
			else {
				throw new RuntimeException("Unable to interpret: " + this.get_line());
			}
			
			/* append on the tail of sequence */
			range.append(AstExecutionFlowType.goto_flow, local_range);
			
			/* the function returns at this point */
			if(next_location instanceof AstReturnStatement) { break; }
		}		
		return range;
	}
	
}
