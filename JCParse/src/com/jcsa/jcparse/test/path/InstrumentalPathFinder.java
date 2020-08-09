package com.jcsa.jcparse.test.path;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.AstDeclaration;
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

/**
 * To construct the complete path from instrumental data.
 * 
 * @author yukimula
 *
 */
public class InstrumentalPathFinder {
	
	/* definition */
	private InstrumentalLine ins_line;
	private InstrumentalReader reader;
	private InstrumentalPathFinder(InstrumentalReader reader) throws Exception {
		if(reader == null)
			throw new IllegalArgumentException("Invalid reader: null");
		else {
			this.reader = reader;
			this.ins_line = this.reader.next_line();
		}
	}
	
	/* basic methods */
	/**
	 * @return whether there is more line in the instrumental reader
	 */
	private boolean has_line() { return this.ins_line != null; }
	/**
	 * @return the instrumental line being consumed in current state
	 */
	private InstrumentalLine get_line() { return this.ins_line; }
	/**
	 * update the instrumental line to the next from the reader.
	 */
	private void next_line() throws Exception { 
		this.ins_line = this.reader.next_line(); 
	}
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
	 * @return whether the node is instrumental
	 * @throws Exception
	 */
	private boolean is_instrumental_node(AstNode node) throws Exception {
		if(node instanceof AstExpression) {
			return this.is_valid_context((AstExpression) node) && 
					this.is_valid_expression((AstExpression) node)
					&& this.is_valid_type(((AstExpression) node).get_value_type());
		}
		else if(node instanceof AstStatement) {
			if(node instanceof AstIfStatement
				|| node instanceof AstSwitchStatement
				|| node instanceof AstForStatement
				|| node instanceof AstWhileStatement
				|| node instanceof AstDoWhileStatement) {
				return false;
			}
			else {
				return true;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * @param path
	 * @return whether the execution node matches with the instrumental
	 * 		   line that is going to be consumed
	 */
	private boolean match(InstrumentalNode node, boolean exception) throws Exception {
		AstNode location = node.get_line().get_location();
		if(this.is_instrumental_node(location)) {
			if(this.has_line()) {
				InstrumentalLine line = this.get_line();
				if(node.get_line().match(line)) {
					node.set_value(line.get_value());	/* update the value */
					this.next_line();					/* update the token */
					return true;						/* matching successfully */
				}
				else if(exception) {
					throw new RuntimeException("Unable to match "
							+ node.get_line() + " with " + line);
				}
				else {
					return false;	/* safety return false */
				}
			}
			else {
				return false;	/* no more line to be matched */
			}
		}
		else {
			return false;	/* avoid non-instrumental node */
		}
	}
	/**
	 * @param node
	 * @return whether the execution node matches with the instrumental
	 * 		   line that is going to be consumed without throwing error
	 * @throws Exception
	 */
	private boolean safe_match(InstrumentalNode node) throws Exception {
		return this.match(node, false);
	}
	/**
	 * @param node
	 * @return whether the execution node matches with the instrumental
	 * 		   line that is going to be consumed with throwing exception
	 * @throws Exception
	 */
	private boolean force_match(InstrumentalNode node) throws Exception {
		return this.match(node, true);
	}
	/**
	 * @param location
	 * @return create an empty path that describes the execution sequence
	 * 		   during performing the location under given.
	 * @throws Exception
	 */
	private InstrumentalPath new_path(AstNode location) {
		return new InstrumentalPath(location);
	}
	/**
	 * @param path
	 * @param node goto_flow::node
	 * @throws Exception
	 */
	private void append(InstrumentalPath path, InstrumentalNode node) throws Exception {
		path.append(InstrumentalLink.goto_flow, node);
	}
	/**
	 * @param path
	 * @param type
	 * @param node type::node
	 * @throws Exception
	 */
	private void append(InstrumentalPath path, InstrumentalLink 
				type, InstrumentalNode node) throws Exception {
		path.append(type, node);
	}
	/**
	 * @param path
	 * @param condition
	 * @return the boolean value hold by the condition in the last part of the path
	 * @throws Exception
	 */
	private boolean condition_value(InstrumentalPath path, AstExpression condition) throws Exception {
		InstrumentalNode last_node = path.rfind(
				CTypeAnalyzer.get_expression_of(condition));
		if(last_node == null) {
			throw new IllegalArgumentException("Unabel to find: " + condition.generate_code());
		}
		else {
			byte[] value = last_node.get_line().get_value();
			for(byte item : value) {
				if(item != 0)
					return true;
			}
			return false;
		}
	}
	
	/* path finding algorithm */
	/**
	 * @param location
	 * @return automatic path finding to construct the closed-path w.r.t.
	 * 		   the specified AST-node that matching with current line.
	 * @throws Exception
	 */
	private InstrumentalPath find(AstNode location) throws Exception {
		if(location == null)
			throw new IllegalArgumentException("Invalid location: null");
		else if(!this.has_line()) { return this.new_path(location); }
		else if(location instanceof AstDeclaration)
			return this.find_declaration((AstDeclaration) location);
		else if(location instanceof AstInitDeclaratorList)
			return this.find_init_declarator_list((AstInitDeclaratorList) location);
		else if(location instanceof AstInitDeclarator)
			return this.find_init_declarator((AstInitDeclarator) location);
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
		else if(location instanceof AstArgumentList)
			return this.find_argument_list((AstArgumentList) location);
		else if(location instanceof AstExpressionStatement)
			return this.find_expression_statement((AstExpressionStatement) location);
		else if(location instanceof AstDeclarationStatement)
			return this.find_declaration_statement((AstDeclarationStatement) location);
		else if(location instanceof AstCompoundStatement)
			return this.find_compound_statement((AstCompoundStatement) location);
		else if(location instanceof AstCaseStatement)
			return this.find_case_statement((AstCaseStatement) location);
		else if(location instanceof AstDefaultStatement)
			return this.find_default_statement((AstDefaultStatement) location);
		else if(location instanceof AstLabeledStatement)
			return this.find_labeled_statement((AstLabeledStatement) location);
		else if(location instanceof AstGotoStatement)
			return this.find_goto_statement((AstGotoStatement) location);
		else if(location instanceof AstBreakStatement)
			return this.find_break_statement((AstBreakStatement) location);
		else if(location instanceof AstContinueStatement)
			return this.find_continue_statement((AstContinueStatement) location);
		else if(location instanceof AstReturnStatement)
			return this.find_return_statement((AstReturnStatement) location);
		else if(location instanceof AstIfStatement)
			return this.find_if_statement((AstIfStatement) location);
		else if(location instanceof AstSwitchStatement)
			return this.find_switch_statement((AstSwitchStatement) location);
		else if(location instanceof AstWhileStatement)
			return this.find_while_statement((AstWhileStatement) location);
		else if(location instanceof AstDoWhileStatement)
			return this.find_do_while_statement((AstDoWhileStatement) location);
		else if(location instanceof AstForStatement)
			return this.find_for_statement((AstForStatement) location);
		else if(location instanceof AstFunctionDefinition)
			return this.find_function_definition((AstFunctionDefinition) location);
		else 
			throw new IllegalArgumentException("Unsupport: " + location);
	}
	
	/* declaration package */
	private InstrumentalPath find_declaration(AstDeclaration location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		this.append(path, InstrumentalNode.beg_node(location));
		if(location.has_declarator_list()) {
			path.append(InstrumentalLink.down_flow, 
					this.find(location.get_declarator_list()));
			this.append(path, InstrumentalLink.upon_flow, 
					InstrumentalNode.end_node(location));
		}
		else {
			this.append(path, InstrumentalLink.move_flow, 
					InstrumentalNode.end_node(location));
		}
		return path;
	}
	private InstrumentalPath find_init_declarator_list(AstInitDeclaratorList location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		this.append(path, InstrumentalNode.beg_node(location));
		for(int k = 0; k < location.number_of_init_declarators(); k++) {
			if(k == 0) {
				path.append(InstrumentalLink.down_flow, this.
						find(location.get_init_declarator(k)));
			}
			else {
				path.append(InstrumentalLink.move_flow, this.
						find(location.get_init_declarator(k)));
			}
		}
		this.append(path, InstrumentalLink.upon_flow, 
				InstrumentalNode.end_node(location));
		return path;
	}
	private InstrumentalPath find_init_declarator(AstInitDeclarator location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		this.append(path, InstrumentalNode.beg_node(location));
		if(location.has_initializer()) {
			path.append(InstrumentalLink.down_flow, this.
						find(location.get_initializer()));
			this.append(path, InstrumentalLink.upon_flow, 
					InstrumentalNode.end_node(location));
		}
		else {
			this.append(path, InstrumentalLink.move_flow, 
					InstrumentalNode.end_node(location));
		}
		return path;
	}
	/* initializer package */
	private InstrumentalPath find_initializer(AstInitializer location) throws Exception {
		if(location.is_body())
			return this.find(location.get_body());
		else
			return this.find(location.get_expression());
	}
	private InstrumentalPath find_initializer_body(AstInitializerBody location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		this.append(path, InstrumentalNode.beg_expr(location));
		path.append(InstrumentalLink.down_flow, this.
				find(location.get_initializer_list()));
		this.append(path, InstrumentalLink.upon_flow, 
				InstrumentalNode.end_expr(location));
		return path;
	}
	private InstrumentalPath find_initializer_list(AstInitializerList location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		this.append(path, InstrumentalNode.beg_node(location));
		for(int k = 0; k < location.number_of_initializer(); k++) {
			if(k == 0) {
				path.append(InstrumentalLink.down_flow, this.
							find(location.get_initializer(k)));
			}
			else {
				path.append(InstrumentalLink.move_flow, this.
						find(location.get_initializer(k)));
			}
		}
		this.append(path, InstrumentalLink.upon_flow, 
				InstrumentalNode.end_node(location));
		return path;
	}
	private InstrumentalPath find_field_initializer(AstFieldInitializer location) throws Exception {
		return this.find(location.get_initializer());
	}
	/* expression package */
	private InstrumentalPath find_id_expression(AstIdExpression location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		InstrumentalNode node = InstrumentalNode.evaluate(location);
		this.force_match(node);
		this.append(path, node);
		return path;
	}
	private InstrumentalPath find_constant(AstConstant location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		InstrumentalNode node = InstrumentalNode.evaluate(location);
		this.force_match(node);
		this.append(path, node);
		return path;
	}
	private InstrumentalPath find_literal(AstLiteral location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		InstrumentalNode node = InstrumentalNode.evaluate(location);
		
		String literal = location.get_literal();
		byte[] value = new byte[literal.length() + 1];
		for(int k = 0; k < literal.length(); k++) {
			value[k] = (byte) literal.charAt(k);
		}
		value[literal.length()] = 0;
		node.set_value(value);
		
		this.append(path, node);
		return path;
	}
	private InstrumentalPath find_unary_expression(AstUnaryExpression location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		
		this.append(path, InstrumentalNode.beg_expr(location));
		path.append(InstrumentalLink.down_flow, this.find(location.get_operand()));
		
		InstrumentalNode end = InstrumentalNode.end_expr(location);
		this.force_match(end);
		this.append(path, InstrumentalLink.upon_flow, end);
		
		return path;
	}
	private InstrumentalPath find_postfix_expression(AstPostfixExpression location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		
		this.append(path, InstrumentalNode.beg_expr(location));
		path.append(InstrumentalLink.down_flow, this.find(location.get_operand()));
		
		InstrumentalNode end = InstrumentalNode.end_expr(location);
		this.force_match(end);
		this.append(path, InstrumentalLink.upon_flow, end);
		
		return path;
	}
	private InstrumentalPath find_binary_expression(AstBinaryExpression location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		AstExpression loperand = location.get_loperand();
		AstExpression roperand = location.get_roperand();
		
		switch(location.get_operator().get_operator()) {
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
		case righ_shift_assign:
		{
			AstExpression temp;
			temp = loperand;
			loperand = roperand;
			roperand = temp;
		}
		default:	break;
		}
		
		this.append(path, InstrumentalNode.beg_node(location));
		path.append(InstrumentalLink.down_flow, this.find(loperand));
		path.append(InstrumentalLink.move_flow, this.find(roperand));
		
		InstrumentalNode end = InstrumentalNode.end_expr(location);
		this.force_match(end);
		this.append(path, InstrumentalLink.upon_flow, end);
		
		return path;
	}
	private InstrumentalPath find_array_expression(AstArrayExpression location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		
		this.append(path, InstrumentalNode.beg_expr(location));
		path.append(InstrumentalLink.down_flow, this.find(location.get_array_expression()));
		path.append(InstrumentalLink.move_flow, this.find(location.get_dimension_expression()));
		InstrumentalNode end = InstrumentalNode.end_expr(location);
		this.force_match(end);
		this.append(path, InstrumentalLink.upon_flow, end);
		
		return path;
	}
	private InstrumentalPath find_cast_expression(AstCastExpression location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		
		this.append(path, InstrumentalNode.beg_expr(location));
		path.append(InstrumentalLink.down_flow, this.find(location.get_expression()));
		InstrumentalNode end = InstrumentalNode.end_expr(location);
		this.force_match(end);
		this.append(path, InstrumentalLink.upon_flow, end);
		
		return path;
	}
	private InstrumentalPath find_comma_expression(AstCommaExpression location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		
		this.append(path, InstrumentalNode.beg_expr(location));
		for(int k = 0; k < location.number_of_arguments(); k++) {
			if(k == 0) {
				path.append(InstrumentalLink.down_flow, 
						this.find(location.get_expression(k)));
			}
			else {
				path.append(InstrumentalLink.move_flow, 
						this.find(location.get_expression(k)));
			}
		}
		InstrumentalNode end = InstrumentalNode.end_expr(location);
		this.force_match(end);
		this.append(path, InstrumentalLink.upon_flow, end);
		
		return path;
	}
	private InstrumentalPath find_const_expression(AstConstExpression location) throws Exception {
		return this.find(location.get_expression());
	}
	private InstrumentalPath find_paranth_expression(AstParanthExpression location) throws Exception {
		return this.find(location.get_sub_expression());
	}
	private InstrumentalPath find_field_expression(AstFieldExpression location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		
		this.append(path, InstrumentalNode.beg_expr(location));
		path.append(InstrumentalLink.down_flow, this.find(location.get_body()));
		InstrumentalNode end = InstrumentalNode.end_expr(location);
		this.force_match(end);
		this.append(path, InstrumentalLink.upon_flow, end);
		
		return path;
	}
	private InstrumentalPath find_sizeof_expression(AstSizeofExpression location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		InstrumentalNode node = InstrumentalNode.evaluate(location);
		this.force_match(node);
		this.append(path, node);
		return path;
	}
	private InstrumentalPath find_conditional_expression(AstConditionalExpression location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		
		this.append(path, InstrumentalNode.beg_expr(location));
		path.append(InstrumentalLink.down_flow, find(location.get_condition()));
		
		if(this.condition_value(path, location.get_condition())) {
			path.append(InstrumentalLink.move_flow, 
					this.find(location.get_true_branch()));
		}
		else {
			path.append(InstrumentalLink.move_flow, 
					this.find(location.get_false_branch()));
		}
		
		InstrumentalNode end = InstrumentalNode.end_expr(location);
		this.force_match(end);
		this.append(path, InstrumentalLink.upon_flow, end);
		
		return path;
	}
	private InstrumentalPath find_fun_call_expression(AstFunCallExpression location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		
		this.append(path, InstrumentalNode.beg_expr(location));
		path.append(InstrumentalLink.down_flow, this.find(location.get_function()));
		if(location.has_argument_list()) {
			path.append(InstrumentalLink.move_flow, find(location.get_argument_list()));
		}
		
		/* when it gets to another function */
		while(this.has_line()) {
			AstNode next_location = this.get_line().get_location();
			if(next_location.get_parent() instanceof AstFunctionDefinition) {
				path.append(InstrumentalLink.call_flow, this.find(next_location.get_parent()));
			}
			else if(next_location == location) {
				InstrumentalNode end = InstrumentalNode.end_expr(location);
				this.force_match(end);
				this.append(path, InstrumentalLink.retr_flow, end);
				break;
			}
			else if(!this.is_instrumental_node(location)) {
				break;
			}
			else {
				throw new RuntimeException("Invalid line: " + next_location.getClass().getSimpleName() + 
						"[" + next_location.get_key() + "] at line of " + this.get_line().toString());
			}
		}
		
		return path;
	}
	private InstrumentalPath find_argument_list(AstArgumentList location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		
		this.append(path, InstrumentalNode.beg_node(location));
		for(int k = 0; k < location.number_of_arguments(); k++) {
			if(k == 0) {
				path.append(InstrumentalLink.down_flow, 
						this.find(location.get_argument(k)));
			}
			else {
				path.append(InstrumentalLink.move_flow, 
						this.find(location.get_argument(k)));
			}
		}
		this.append(path, InstrumentalLink.upon_flow, 
				InstrumentalNode.end_node(location));
		
		return path;
	}
	/* statement package */
	private InstrumentalPath find_expression_statement(AstExpressionStatement location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		
		InstrumentalNode beg = InstrumentalNode.beg_stmt(location);
		this.force_match(beg);
		this.append(path, beg);
		
		InstrumentalLink return_link;
		if(location.has_expression()) {
			path.append(InstrumentalLink.down_flow, 
					this.find(location.get_expression()));
			return_link = InstrumentalLink.upon_flow;
		}
		else {
			return_link = InstrumentalLink.move_flow;
		}
		
		boolean ignore_end = false;
		if(location.get_parent() instanceof AstForStatement) {
			AstForStatement parent = (AstForStatement) location.get_parent();
			if(parent.get_initializer() == location && location.has_expression()) {
				ignore_end = true;
			}
			else if(parent.get_condition() == location && location.has_expression()) {
				ignore_end = true;
			}
		}
		
		InstrumentalNode end = InstrumentalNode.end_stmt(location);
		if(!ignore_end) this.force_match(end);
		this.append(path, return_link, end);
		
		return path;
	}
	private InstrumentalPath find_declaration_statement(AstDeclarationStatement location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		
		InstrumentalNode beg = InstrumentalNode.beg_stmt(location);
		this.force_match(beg);
		this.append(path, beg);
		
		path.append(InstrumentalLink.down_flow, this.find(location.get_declaration()));
		
		boolean ignore_end = false;
		if(location.get_parent() instanceof AstForStatement) {
			AstForStatement parent = (AstForStatement) location.get_parent();
			if(parent.get_initializer() == location || parent.get_condition() == location) {
				ignore_end = true;
			}
		}
		
		InstrumentalNode end = InstrumentalNode.end_stmt(location);
		if(!ignore_end) this.force_match(end);
		this.append(path, InstrumentalLink.upon_flow, end);
		
		return path;
	}
	private InstrumentalPath find_compound_statement(AstCompoundStatement location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		
		/* try-to-match beg_stmt(location) */
		InstrumentalNode beg = InstrumentalNode.beg_stmt(location);
		if(this.safe_match(beg)) {
			this.append(path, beg);
			return path;
		}
		
		/* try-to-match end_stmt(location) */
		InstrumentalNode end = InstrumentalNode.end_stmt(location);
		if(this.safe_match(end)) {
			this.append(path, end);
			return path;
		}
		
		/* unable-to-match the compound statement tag */
		throw new RuntimeException("Unable to match " + 
				location + " with " + this.get_line());
	}
	private InstrumentalPath find_goto_statement(AstGotoStatement location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		
		InstrumentalNode node = InstrumentalNode.execute(location);
		this.force_match(node);
		this.append(path, node);
		
		return path;
	}
	private InstrumentalPath find_break_statement(AstBreakStatement location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		
		InstrumentalNode node = InstrumentalNode.execute(location);
		this.force_match(node);
		this.append(path, node);
		
		return path;
	}
	private InstrumentalPath find_continue_statement(AstContinueStatement location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		
		InstrumentalNode node = InstrumentalNode.execute(location);
		this.force_match(node);
		this.append(path, node);
		
		return path;
	}
	private InstrumentalPath find_return_statement(AstReturnStatement location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		if(location.has_expression()) {
			InstrumentalNode beg = InstrumentalNode.beg_stmt(location);
			this.force_match(beg);
			this.append(path, beg);
			
			path.append(InstrumentalLink.down_flow, 
					this.find(location.get_expression()));
			
			InstrumentalNode end = InstrumentalNode.end_stmt(location);
			this.append(path, InstrumentalLink.upon_flow, end);
		}
		else {
			InstrumentalNode node = InstrumentalNode.execute(location);
			this.force_match(node);
			this.append(path, node);
		}
		return path;
	}
	private InstrumentalPath find_labeled_statement(AstLabeledStatement location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		
		InstrumentalNode node = InstrumentalNode.execute(location);
		this.force_match(node);
		this.append(path, node);
		
		return path;
	}
	private InstrumentalPath find_default_statement(AstDefaultStatement location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		
		InstrumentalNode node = InstrumentalNode.execute(location);
		this.force_match(node);
		this.append(path, node);
		
		return path;
	}
	private InstrumentalPath find_case_statement(AstCaseStatement location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		
		InstrumentalNode node = InstrumentalNode.execute(location);
		this.force_match(node);
		this.append(path, node);
		
		return path;
	}
	private InstrumentalPath find_if_statement(AstIfStatement location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		this.append(path, InstrumentalNode.beg_stmt(location));
		path.append(InstrumentalLink.down_flow, this.find(location.get_condition()));
		this.append(path, InstrumentalLink.upon_flow, InstrumentalNode.end_stmt(location));
		return path;
	}
	private InstrumentalPath find_switch_statement(AstSwitchStatement location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		this.append(path, InstrumentalNode.beg_stmt(location));
		path.append(InstrumentalLink.down_flow, this.find(location.get_condition()));
		this.append(path, InstrumentalLink.upon_flow, InstrumentalNode.end_stmt(location));
		return path;
	}
	private InstrumentalPath find_while_statement(AstWhileStatement location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		this.append(path, InstrumentalNode.beg_stmt(location));
		path.append(InstrumentalLink.down_flow, this.find(location.get_condition()));
		this.append(path, InstrumentalLink.upon_flow, InstrumentalNode.end_stmt(location));
		return path;
	}
	private InstrumentalPath find_do_while_statement(AstDoWhileStatement location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		this.append(path, InstrumentalNode.beg_stmt(location));
		path.append(InstrumentalLink.down_flow, this.find(location.get_condition()));
		this.append(path, InstrumentalLink.upon_flow, InstrumentalNode.end_stmt(location));
		return path;
	}
	private InstrumentalPath find_for_statement(AstForStatement location) throws Exception {
		return this.find(location.get_increment());
	}
	/* function body traversal */
	/**
	 * @param location
	 * @return [statement, direct_child]
	 * @throws Exception
	 */
	private AstNode[] statement_context(AstNode location) throws Exception {
		AstNode child = location;
		AstNode parent = location.get_parent();
		while(parent != null) {
			if(parent instanceof AstStatement) {
				return new AstNode[] { parent, child };
			}
			else {
				child = parent;
				parent = parent.get_parent();
			}
		}
		throw new RuntimeException("Not in statement: " + location);
	}
	/**
	 * @param location
	 * @return call_fun --> exit_fun
	 * @throws Exception
	 */
	private InstrumentalPath find_function_definition(AstFunctionDefinition location) throws Exception {
		InstrumentalPath path = this.new_path(location);
		
		this.append(path, InstrumentalNode.call_fun(location));
		
		boolean first_statement = true;
		while(this.has_line()) {
			InstrumentalLine line = this.get_line();
			AstNode next_location = line.get_location();
			AstStatement statement; AstNode direct_child;
			
			if(next_location instanceof AstStatement) {
				statement = (AstStatement) next_location;
				if(first_statement) {
					first_statement = false;
					path.append(InstrumentalLink.down_flow, this.find(statement));
				}
				else {
					path.append(InstrumentalLink.goto_flow, this.find(statement));
				}
			}
			else {
				AstNode[] context = this.statement_context(next_location);
				statement = (AstStatement) context[0];
				direct_child = context[1];
				
				if(statement instanceof AstIfStatement) {
					if(((AstIfStatement) statement).get_condition() != direct_child) {
						throw new RuntimeException("Invalid child: " + direct_child);
					}
				}
				else if(statement instanceof AstSwitchStatement) {
					if(((AstSwitchStatement) statement).get_condition() != direct_child) {
						throw new RuntimeException("Invalid child: " + direct_child);
					}
				}
				else if(statement instanceof AstWhileStatement) {
					if(((AstWhileStatement) statement).get_condition() != direct_child) {
						throw new RuntimeException("Invalid child: " + direct_child);
					}
				}
				else if(statement instanceof AstDoWhileStatement) {
					if(((AstDoWhileStatement) statement).get_condition() != direct_child) {
						throw new RuntimeException("Invalid child: " + direct_child);
					}
				}
				else if(statement instanceof AstForStatement) {
					if(((AstForStatement) statement).get_increment() != direct_child) {
						throw new RuntimeException("Invalid child: " + direct_child);
					}
				}
				else {
					throw new RuntimeException("Unable to find path for " + statement);
				}
				
				if(first_statement) {
					first_statement = false;
					path.append(InstrumentalLink.down_flow, this.find(statement));
				}
				else {
					path.append(InstrumentalLink.goto_flow, this.find(statement));
				}
			}
			
			if(statement instanceof AstReturnStatement) {
				this.append(path, InstrumentalLink.goto_flow, InstrumentalNode.end_stmt(location.get_body()));
				break;		// return from the return-statement and get out from body
			}
			else if(statement == location.get_body() && line.get_tag() == InstrumentalTag.end_stmt) {
				break;		// reach the end of function without return statement
			}
		}
		
		this.append(path, InstrumentalLink.upon_flow, 
				InstrumentalNode.exit_fun(location));
		
		return path;
	}
	
	/* path finder interface */
	/**
	 * @param reader
	 * @return the complete instrumental path
	 * @throws Exception
	 */
	public static InstrumentalPath find(InstrumentalReader reader) throws Exception {
		InstrumentalPathFinder finder = new InstrumentalPathFinder(reader);
		if(finder.ins_line != null) {
			AstFunctionDefinition main_fun = (AstFunctionDefinition) 
						finder.ins_line.get_location().get_parent();
			return finder.find(main_fun);
		}
		else {
			return finder.new_path(null);
		}
	}
	
}
