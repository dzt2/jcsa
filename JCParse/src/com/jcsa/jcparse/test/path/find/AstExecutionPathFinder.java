package com.jcsa.jcparse.test.path.find;

import java.nio.ByteBuffer;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.AstTypeName;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstDesignatorList;
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
import com.jcsa.jcparse.lang.astree.expr.othr.AstField;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFunCallExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstParanthExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstSizeofExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCompoundStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
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
import com.jcsa.jcparse.test.path.AstExecutionType;
import com.jcsa.jcparse.test.path.AstExecutionUnit;
import com.jcsa.jcparse.test.path.InstrumentLine;
import com.jcsa.jcparse.test.path.InstrumentList;
import com.jcsa.jcparse.test.path.InstrumentType;

/**
 * 	To build up the execution path of AST-sequence by parsing
 * 	the instrumental lines.
 * 	
 * 	@author yukimula
 *
 */
public class AstExecutionPathFinder {
	
	/* definition */
	private InstrumentListConsummer input;
	private AstExecutionPath path;
	private AstExecutionPathFinder(InstrumentList lines,
			AstExecutionPath path) throws Exception {
		if(lines == null)
			throw new IllegalArgumentException("Invalid lines: null");
		else if(path == null)
			throw new IllegalArgumentException("Invalid path: null");
		else {
			this.input = new InstrumentListConsummer(lines);
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
	private boolean is_instrumental_node(AstExpression node) throws Exception {
		return this.is_valid_context(node) && 
				this.is_valid_expression(node) && 
				this.is_valid_type(node.get_value_type());
	}
	/**
	 * match the execution point with the current line in instrumental list
	 * @param point
	 * @throws Exception is thrown when maching fails at that point.
	 */
	private void match(AstExecutionNode point) throws Exception {
		
		if(!this.input.match(point.get_unit())) {
			throw new RuntimeException("Unable to match: " + this.input.get());
		}
		else {
			point.get_unit().set_state(input.get().get_state());
			this.input.next();
		}
	}
	/**
	 * @param beg
	 * @return the solution initialized with the beginning node in path range
	 * @throws Exception
	 */
	private AstExecutionPathSolution new_solution(AstExecutionNode beg) throws Exception {
		AstExecutionPathSolution solution = new AstExecutionPathSolution();
		solution.append(AstExecutionFlowType.move_flow, beg);
		return solution;
	}
	private AstExecutionPathSolution new_solution() {
		return new AstExecutionPathSolution();
	}
	/**
	 * @param last_node
	 * @param location
	 * @return the execution point closest to the last-node w.r.t. the given location.
	 * @throws Exception
	 */
	private AstExecutionNode find_closest(AstExecutionNode last_node, 
			AstExecutionType type, AstNode location) throws Exception {
		AstExecutionNode node = last_node;
		while(node != null) {
			if(node.get_unit().get_type() == type && node.get_unit().get_location() == location) {
				return last_node;
			}
			else if(last_node.has_in_flow()) {
				last_node = last_node.get_in_flow().get_source();
			}
			else {
				last_node = null;
			}
		}
		return null;
	}
	/**
	 * @param unit
	 * @return create a new node in the execution path
	 * @throws Exception
	 */
	private AstExecutionNode new_node(AstExecutionUnit unit) throws Exception {
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
	private byte[] state_of(String literal) throws Exception {
		byte[] bytes = new byte[literal.length() + 1];
		for(int k = 0; k < literal.length(); k++) {
			bytes[k] = (byte) literal.charAt(k);
		}
		bytes[literal.length()] = 0;
		return bytes;
	}
	/**
	 * set the state of target by source
	 * @param source
	 * @param target
	 */
	private void set_state(AstExecutionNode source, AstExecutionNode target) {
		target.get_unit().set_state(source.get_unit().get_state());
	}
	/**
	 * @return whether there is more input in the current buffer.
	 */
	private boolean has_input() { return this.input.get() != null; }
	
	/* path finding algorithms */
	private AstExecutionPathSolution find(AstNode location) throws Exception {
		if(location == null)
			throw new IllegalArgumentException("Invalid location: null");
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
		else if(location instanceof AstConditionalExpression)
			return this.find_conditional_expression((AstConditionalExpression) location);
		else if(location instanceof AstConstExpression)
			return this.find_const_expression((AstConstExpression) location);
		else if(location instanceof AstParanthExpression)
			return this.find_paranth_expression((AstParanthExpression) location);
		else if(location instanceof AstSizeofExpression)
			return this.find_sizeof_expression((AstSizeofExpression) location);
		else if(location instanceof AstFieldExpression)
			return this.find_field_expression((AstFieldExpression) location);
		else if(location instanceof AstInitializer)
			return this.find_initializer((AstInitializer) location);
		else if(location instanceof AstInitializerBody)
			return this.find_initializer_body((AstInitializerBody) location);
		else if(location instanceof AstInitializerList)
			return this.find_initializer_list((AstInitializerList) location);
		else if(location instanceof AstFieldInitializer)
			return this.find_field_initializer((AstFieldInitializer) location);
		else if(location instanceof AstFunCallExpression)
			return this.find_fun_call_expression((AstFunCallExpression) location);
		else
			throw new IllegalArgumentException("Unsupport: " + location);
	}
	/* expression package */
	private AstExecutionPathSolution find_id_expression(AstIdExpression location) throws Exception {
		AstExecutionPathSolution solution;
		if(this.has_input()) {
			AstExecutionNode node = this.new_node(AstExecutionUnit.evaluate(location));
			if(this.is_instrumental_node(location)) { this.match(node); }
			solution = this.new_solution(node);
		}
		else {
			solution = this.new_solution();
		}
		return solution;
	}
	private AstExecutionPathSolution find_constant(AstConstant location) throws Exception {
		AstExecutionPathSolution solution;
		if(this.has_input()) {
			AstExecutionNode node = this.new_node(AstExecutionUnit.evaluate(location));
			node.get_unit().set_state(this.state_of(location.get_constant()));
			solution = this.new_solution(node);
		}
		else {
			solution = this.new_solution();
		}
		return solution;
	}
	private AstExecutionPathSolution find_literal(AstLiteral location) throws Exception {
		AstExecutionPathSolution solution;
		if(this.has_input()) {
			AstExecutionNode node = this.new_node(AstExecutionUnit.evaluate(location));
			node.get_unit().set_state(this.state_of(location.get_literal()));
			solution = this.new_solution(node);
		}
		else {
			solution = this.new_solution();
		}
		return solution;
	}
	private AstExecutionPathSolution find_unary_expression(AstUnaryExpression location) throws Exception {
		AstExecutionPathSolution solution;
		
		/* beg_expr(expression) */
		if(this.has_input()) {
			AstExecutionNode beg = this.new_node(AstExecutionUnit.beg_expr(location));
			solution = this.new_solution(beg);
		}
		else {
			solution = this.new_solution();
		}
		
		/* down_flow ==> find(expression.operand())*/
		if(this.has_input()) {
			solution.append(AstExecutionFlowType.down_flow, this.find(location.get_operand()));
		}
		
		/* upon_flow ==> end_expr(expression) */
		if(this.has_input()) {
			AstExecutionNode end = this.new_node(AstExecutionUnit.end_expr(location));
			if(this.is_instrumental_node(location)) { this.match(end); }
			solution.append(AstExecutionFlowType.upon_flow, end);
		}
		
		return solution;
	}
	private AstExecutionPathSolution find_postfix_expression(AstPostfixExpression location) throws Exception {
		AstExecutionPathSolution solution;
		
		/* beg_expr(expression) */
		if(this.has_input()) {
			AstExecutionNode beg = this.new_node(AstExecutionUnit.beg_expr(location));
			solution = this.new_solution(beg);
		}
		else {
			solution = this.new_solution();
		}
		
		/* down_flow ==> find(expression.operand())*/
		if(this.has_input()) {
			solution.append(AstExecutionFlowType.down_flow, this.find(location.get_operand()));
		}
		
		/* upon_flow ==> end_expr(expression) */
		if(this.has_input()) {
			AstExecutionNode end = this.new_node(AstExecutionUnit.end_expr(location));
			if(this.is_instrumental_node(location)) { this.match(end); }
			solution.append(AstExecutionFlowType.upon_flow, end);
		}
		
		return solution;
	}
	private AstExecutionPathSolution find_binary_expression(AstBinaryExpression location) throws Exception {
		AstExecutionPathSolution solution;
		
		/* beg_expr(expression) */
		if(this.has_input()) {
			AstExecutionNode beg = this.new_node(AstExecutionUnit.beg_expr(location));
			solution = this.new_solution(beg);
		}
		else {
			solution = this.new_solution();
		}
		
		/* down_flow ==> find(expression.left_operand) */
		if(this.has_input()) {
			solution.append(AstExecutionFlowType.down_flow, this.find(location.get_loperand()));
		}
		
		/* move_flow ==> find(expression.right_operand) */
		if(this.has_input()) {
			solution.append(AstExecutionFlowType.move_flow, this.find(location.get_roperand()));
		}
		
		/* upon_flow ==> end_expr(expression) */
		if(this.has_input()) {
			AstExecutionNode end = this.new_node(AstExecutionUnit.end_expr(location));
			if(this.is_instrumental_node(location)) { this.match(end); }
			solution.append(AstExecutionFlowType.upon_flow, end);
		}
		
		return solution;
	}
	private AstExecutionPathSolution find_array_expression(AstArrayExpression location) throws Exception {
		AstExecutionPathSolution solution;
		
		/* beg_expr(array_expression) */
		if(this.has_input()) {
			AstExecutionNode beg = this.new_node(AstExecutionUnit.beg_expr(location));
			solution = this.new_solution(beg);
		}
		else {
			solution = this.new_solution();
		}
		
		/* down_flow ==> find{array} */
		if(this.has_input()) {
			solution.append(AstExecutionFlowType.down_flow, this.find(location.get_array_expression()));
		}
		
		/* move_flow ==> find{dimension} */
		if(this.has_input()) {
			solution.append(AstExecutionFlowType.move_flow, this.find(location.get_dimension_expression()));
		}
		
		/* upon_flow ==> end_expr(expression) */
		if(this.has_input()) {
			AstExecutionNode end = this.new_node(AstExecutionUnit.end_expr(location));
			if(this.is_instrumental_node(location)) {
				this.match(end);
			}
			solution.append(AstExecutionFlowType.upon_flow, end);
		}
		
		return solution;
	}
	private AstExecutionPathSolution find_cast_expression(AstCastExpression location) throws Exception {
		AstExecutionPathSolution solution;
		
		/* beg_expr(expression) */
		if(this.has_input()) {
			AstExecutionNode beg = this.new_node(AstExecutionUnit.beg_expr(location));
			solution = this.new_solution(beg);
		}
		else {
			solution = this.new_solution();
		}
		
		/* down_flow ==> find(expression.operand())*/
		if(this.has_input()) {
			solution.append(AstExecutionFlowType.down_flow, this.find(location.get_expression()));
		}
		
		/* upon_flow ==> end_expr(expression) */
		if(this.has_input()) {
			AstExecutionNode end = this.new_node(AstExecutionUnit.end_expr(location));
			if(this.is_instrumental_node(location)) { this.match(end); }
			solution.append(AstExecutionFlowType.upon_flow, end);
		}
		
		return solution;
	}
	private AstExecutionPathSolution find_comma_expression(AstCommaExpression location) throws Exception {
		AstExecutionPathSolution solution;
		
		/* beg_expr(expression) */
		if(this.has_input()) {
			AstExecutionNode beg = this.new_node(AstExecutionUnit.beg_expr(location));
			solution = this.new_solution(beg);
		}
		else {
			solution = this.new_solution();
		}
		
		/* down_flow ==> operands[0] and move_flow ==> operands[k] */
		for(int k = 0; k < location.number_of_arguments(); k++) {
			if(this.has_input()) {
				if(k == 0) {
					solution.append(AstExecutionFlowType.down_flow, this.find(location.get_expression(k)));
				}
				else {
					solution.append(AstExecutionFlowType.move_flow, this.find(location.get_expression(k)));
				}
			}
		}
		
		/* upon_flow ==> end_expr(expression) */
		if(this.has_input()) {
			AstExecutionNode end = this.new_node(AstExecutionUnit.end_expr(location));
			if(this.is_instrumental_node(location)) { this.match(end); }
			solution.append(AstExecutionFlowType.upon_flow, end);
		}
		
		return solution;
	}
	private AstExecutionPathSolution find_conditional_expression(AstConditionalExpression location) throws Exception {
		AstExecutionPathSolution solution;
		
		/* beg_expr(expression) */
		if(this.has_input()) {
			AstExecutionNode beg = this.new_node(AstExecutionUnit.beg_expr(location));
			solution = this.new_solution(beg);
		}
		else {
			solution = this.new_solution();
		}
		
		/* down_flow ==> find(expression.condition) */
		if(this.has_input()) {
			solution.append(AstExecutionFlowType.down_flow, this.find(location.get_condition()));
		}
		
		/* move_flow ==> find(expression.true_or_false_branch) */
		if(this.has_input()) {
			// find the node with the value of the condition
			AstExecutionNode prev_condition;
			prev_condition = this.find_closest(solution.end, AstExecutionType.evaluate, 
							CTypeAnalyzer.get_expression_of(location.get_condition()));
			if(prev_condition == null) {
				prev_condition = this.find_closest(solution.end, AstExecutionType.end_expr, 
							CTypeAnalyzer.get_expression_of(location.get_condition()));
			}
			
			// decide the next-branch-statement
			AstExpression selected_branch;
			if(prev_condition != null) {
				if(prev_condition.get_unit().get_bool_state().booleanValue()) {
					selected_branch = location.get_true_branch();
				}
				else {
					selected_branch = location.get_false_branch();
				}
				// move_flow ==> find(selected_branch)
				solution.append(AstExecutionFlowType.move_flow, this.find(selected_branch));
			}
		}
		
		/* upon_flow ==> end_expr(expression) */
		if(this.has_input()) {
			AstExecutionNode end = this.new_node(AstExecutionUnit.end_expr(location));
			if(this.is_instrumental_node(location)) { this.match(end); }
			solution.append(AstExecutionFlowType.upon_flow, end);
		}
		
		return solution;
	}
	private AstExecutionPathSolution find_const_expression(AstConstExpression location) throws Exception {
		return this.find(location.get_expression());
	}
	private AstExecutionPathSolution find_paranth_expression(AstParanthExpression location) throws Exception {
		return this.find(location.get_sub_expression());
	}
	private AstExecutionPathSolution find_sizeof_expression(AstSizeofExpression location) throws Exception {
		AstExecutionPathSolution solution;
		if(this.has_input()) {
			AstExecutionNode node = this.new_node(AstExecutionUnit.evaluate(location));
			if(this.is_instrumental_node(location)) { this.match(node); }
			solution = this.new_solution(node);
		}
		else {
			solution = this.new_solution();
		}
		return solution;
	}
	private AstExecutionPathSolution find_field_expression(AstFieldExpression location) throws Exception {
		AstExecutionPathSolution solution;
		
		/* beg_expr(expression) */
		if(this.has_input()) {
			AstExecutionNode beg = this.new_node(AstExecutionUnit.beg_expr(location));
			solution = this.new_solution(beg);
		}
		else {
			solution = this.new_solution();
		}
		
		/* down_flow ==> find(expression.body) */
		if(this.has_input()) {
			solution.append(AstExecutionFlowType.down_flow, this.find(location.get_body()));
		}
		
		/* upon_flow ==> end_expr(expression) */
		if(this.has_input()) {
			AstExecutionNode end = this.new_node(AstExecutionUnit.end_expr(location));
			if(this.is_instrumental_node(location)) { this.match(end); }
			solution.append(AstExecutionFlowType.upon_flow, end);
		}
		
		return solution;
	}
	private AstExecutionPathSolution find_initializer(AstInitializer location) throws Exception {
		if(location.is_body())
			return this.find(location.get_body());
		else
			return this.find(location.get_expression());
	}
	private AstExecutionPathSolution find_initializer_body(AstInitializerBody location) throws Exception {
		AstExecutionPathSolution solution;
		
		/* beg_expr(initializer_body) */
		if(this.has_input()) {
			solution = this.new_solution(this.new_node(AstExecutionUnit.beg_expr(location)));
		}
		else {
			solution = this.new_solution();
		}
		
		/* down_flow ==> initializer_body.list */
		if(this.has_input()) {
			solution.append(AstExecutionFlowType.down_flow, this.find(location.get_initializer_list()));
		}
		
		/* upon_flow ==> end_expr(expression) */
		if(this.has_input()) {
			AstExecutionNode end = this.new_node(AstExecutionUnit.end_expr(location));
			solution.append(AstExecutionFlowType.upon_flow, end);
		}
		
		return solution;
	}
	private AstExecutionPathSolution find_initializer_list(AstInitializerList location) throws Exception {
		AstExecutionPathSolution solution;
		
		/* beg_expr(expression) */
		if(this.has_input()) {
			solution = this.new_solution(this.new_node(AstExecutionUnit.beg_expr(location)));
		}
		else {
			solution = this.new_solution();
		}
		
		/* down_flow ==> list[0] and move_flow ==> list[k] */
		for(int k = 0; k < location.number_of_initializer(); k++) {
			if(this.has_input()) {
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
		
		/* end_expr(expression) */
		if(this.has_input()) {
			solution.append(AstExecutionFlowType.upon_flow, this.new_node(AstExecutionUnit.end_expr(location)));
		}
		
		return solution;
	}
	private AstExecutionPathSolution find_field_initializer(AstFieldInitializer location) throws Exception {
		return this.find(location.get_initializer());
	}
	private AstExecutionPathSolution find_fun_call_expression(AstFunCallExpression location) throws Exception {
		AstExecutionPathSolution solution;
		
		/* beg-expr(expression) */
		if(this.has_input()) {
			solution = this.new_solution(this.new_node(AstExecutionUnit.beg_expr(location)));
		}
		else {
			solution = this.new_solution();
		}
		
		/* down_flow ==> find(expression.function) */
		if(this.has_input()) {
			solution.append(AstExecutionFlowType.down_flow, this.find(location.get_function()));
		}
		
		/* move_flow ==> find(expression.argument_list) */
		if(this.has_input()) {
			if(location.has_argument_list()) {
				solution.append(AstExecutionFlowType.move_flow, 
						this.find(location.get_argument_list()));
			}
		}
		
		/* call_flow ==> other function definition */
		AstExecutionFlowType return_flow_type;
		if(this.has_input()) {
			InstrumentLine next_line = this.input.get();
			if(next_line.get_location() instanceof AstCompoundStatement) {
				AstFunctionDefinition callee = 
						(AstFunctionDefinition) next_line.get_location().get_parent();
				solution.append(AstExecutionFlowType.call_flow, 
						this.new_node(AstExecutionUnit.beg_func(callee)));
				return_flow_type = AstExecutionFlowType.retr_flow;
			}
			else {
				return_flow_type = AstExecutionFlowType.upon_flow;
			}
		}
		else {
			return_flow_type = null;
		}
		
		/* retr_flow or upon_flow ==> end_expr(expression) */
		if(this.has_input()) {
			AstExecutionNode end = this.new_node(AstExecutionUnit.end_expr(location));
			if(this.is_instrumental_node(location)) { this.match(end); }
			solution.append(return_flow_type, end);
		}
		
		return solution;
	}
	/* statement package */
	
	
	
}
