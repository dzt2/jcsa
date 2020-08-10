package com.jcsa.jcparse.test.inst;

import java.io.File;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
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
 * It provides the interface to build up the instrumental path from the file.
 * @author yukimula
 *
 */
public class InstrumentalBuilder {
	
	/* definition */
	private InstrumentalReader reader;
	private InstrumentalUnit t_unit;
	private InstrumentalBuilder(AstTree tree, File file) throws Exception {
		this.reader = new InstrumentalReader(tree, file);
		this.t_unit = this.reader.next();
	}
	
	/* instrumental reader access */
	/**
	 * @return whether there is more instrumental unit from file
	 */
	private boolean has_unit() { return this.t_unit != null; }
	/**
	 * @return the current instrumental unit to be consumed in parsing
	 */
	private InstrumentalUnit get_unit() { return this.t_unit; }
	/**
	 * update the instrumental unit for being consumed for parsing path
	 */
	private void next_unit() { 
		this.t_unit = this.reader.next();
	}
	/**
	 * @return the location of the current token
	 */
	private AstNode get_unit_location() {
		if(this.t_unit == null)
			return null;
		else
			return this.t_unit.get_location();
	}
	
	/* instrumental node determination */
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
	
	/* unit matching and consumption method */
	/**
	 * @param node
	 * @param exception
	 * @return match the current unit from file with the specified node
	 * @throws Exception
	 */
	private boolean match_unit(InstrumentalNode node, boolean exception) throws Exception {
		AstNode location = node.get_unit().get_location();
		if(this.is_instrumental_node(location)) {
			if(this.has_unit()) {
				if(node.get_unit().match(this.get_unit())) {
					node.get_unit().set_value(this.get_unit().get_value());
					this.next_unit();	/* update the unit cursor */
					return true;		/* matching successfully */
				}
				else if(exception) {	/* matching fails with exception */
					throw new RuntimeException("Unable to match the token of "
							+ this.get_unit().toString() + " with the node of"
							+ " " + node.get_unit().toString() + "\n");
				}
				else {	/* unable to match with current unit */
					return false;
				}
			}
			else {	/* ignore match when no more unit */
				return false;
			}
		}
		else {	/* ignore non-instrumental location */
			return false;
		}
	}
	/**
	 * @param node
	 * @return match with the current token without throwing exception
	 * @throws Exception
	 */
	private boolean safe_match(InstrumentalNode node) throws Exception {
		return this.match_unit(node, false);
	}
	/**
	 * @param node
	 * @return match with the current token with throwing exceptions
	 * @throws Exception
	 */
	private boolean force_match(InstrumentalNode node) throws Exception {
		return this.match_unit(node, true);
	}
	
	/* path access methods */
	private InstrumentalPath new_path() { return new InstrumentalPath(); }
	private void append_path(InstrumentalPath path, InstrumentalNode node) throws Exception {
		path.append(InstrumentalLink.goto_flow, node);
	}
	private void append_path(InstrumentalPath path, InstrumentalLink link, InstrumentalNode node) throws Exception {
		path.append(link, node);
	}
	private boolean condition_value(InstrumentalPath path, AstExpression condition) throws Exception {
		InstrumentalNode node = path.get_target().lfind(
				CTypeAnalyzer.get_expression_of(condition));
		if(node == null) {
			throw new IllegalArgumentException("Unabel to find: " + condition);
		}
		else {
			byte[] value = node.get_unit().get_value();
			for(byte element : value) {
				if(element != 0) {
					return true;
				}
			}
			return false;
		}
	}
	
	/* parsing method */
	/**
	 * @param location
	 * @return the executional path constructed from parsing the instrumental file
	 * @throws Exception
	 */
	private InstrumentalPath find(AstNode location) throws Exception {
		/*if(location != null) {
			System.out.println("\t\t==> Find path on " + 
					location.getClass().getSimpleName() + "[" + 
					location.get_key() + "] at unit of " + 
					this.get_unit());
		}*/
		if(location == null)
			throw new IllegalArgumentException("Invalid location: null");
		else if(!this.has_unit()) 
			return this.new_path(); 
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
		InstrumentalPath path = this.new_path();
		if(location.has_declarator_list()) {
			this.append_path(path, InstrumentalNode.beg(location));
			path.append(InstrumentalLink.down_flow,
					this.find(location.get_declarator_list()));
			this.append_path(path, InstrumentalLink.
					upon_flow, InstrumentalNode.end(location));
		}
		else {
			this.append_path(path, InstrumentalNode.pas(location));
		}
		return path;
	}
	private InstrumentalPath find_init_declarator_list(AstInitDeclaratorList location) throws Exception {
		InstrumentalPath path = this.new_path();
		this.append_path(path, InstrumentalNode.beg(location));
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
		this.append_path(path, InstrumentalLink.upon_flow, 
							InstrumentalNode.end(location));
		return path;
	}
	private InstrumentalPath find_init_declarator(AstInitDeclarator location) throws Exception {
		InstrumentalPath path = this.new_path();
		if(location.has_initializer()) {
			this.append_path(path, InstrumentalNode.beg(location));
			path.append(InstrumentalLink.down_flow, this.find(location.get_initializer()));
			this.append_path(path, InstrumentalLink.upon_flow, InstrumentalNode.end(location));
		}
		else {
			this.append_path(path, InstrumentalNode.pas(location));
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
		InstrumentalPath path = this.new_path();
		this.append_path(path, InstrumentalNode.beg(location));
		path.append(InstrumentalLink.down_flow, this.find(location.get_initializer_list()));
		this.append_path(path, InstrumentalLink.upon_flow, InstrumentalNode.end(location));
		return path;
	}
	private InstrumentalPath find_initializer_list(AstInitializerList location) throws Exception {
		InstrumentalPath path = this.new_path();
		this.append_path(path, InstrumentalNode.beg(location));
		for(int k = 0; k < location.number_of_initializer(); k++) {
			if(k == 0) {
				path.append(InstrumentalLink.down_flow, this.find(location.get_initializer(k)));
			}
			else {
				path.append(InstrumentalLink.move_flow, this.find(location.get_initializer(k)));
			}
		}
		this.append_path(path, InstrumentalLink.upon_flow, InstrumentalNode.end(location));
		return path;
	}
	private InstrumentalPath find_field_initializer(AstFieldInitializer location) throws Exception {
		return this.find(location.get_initializer());
	}
	/* expression package */
	private InstrumentalPath find_id_expression(AstIdExpression location) throws Exception {
		InstrumentalPath path = this.new_path();
		InstrumentalNode node = InstrumentalNode.pas(location);
		this.force_match(node);
		this.append_path(path, node);
		return path;
	}
	private InstrumentalPath find_constant(AstConstant location) throws Exception {
		InstrumentalPath path = this.new_path();
		InstrumentalNode node = InstrumentalNode.pas(location);
		this.force_match(node);
		this.append_path(path, node);
		return path;
	}
	private InstrumentalPath find_literal(AstLiteral location) throws Exception {
		String literal = location.get_literal();
		byte[] value = new byte[literal.length() + 1];
		for(int k = 0; k < literal.length(); k++) {
			value[k] = (byte) literal.charAt(k);
		}
		value[literal.length()] = 0;
		
		InstrumentalPath path = this.new_path();
		InstrumentalNode node = InstrumentalNode.pas(location);
		node.get_unit().set_value(value);
		this.append_path(path, node);
		return path;
	}
	private InstrumentalPath find_unary_expression(AstUnaryExpression location) throws Exception {
		InstrumentalPath path = this.new_path();
		this.append_path(path, InstrumentalNode.beg(location));
		path.append(InstrumentalLink.down_flow, this.find(location.get_operand()));
		InstrumentalNode end = InstrumentalNode.end(location);
		this.force_match(end);
		this.append_path(path, InstrumentalLink.upon_flow, end);
		return path;
	}
	private InstrumentalPath find_postfix_expression(AstPostfixExpression location) throws Exception {
		InstrumentalPath path = this.new_path();
		this.append_path(path, InstrumentalNode.beg(location));
		path.append(InstrumentalLink.down_flow, this.find(location.get_operand()));
		InstrumentalNode end = InstrumentalNode.end(location);
		this.force_match(end);
		this.append_path(path, InstrumentalLink.upon_flow, end);
		return path;
	}
	private InstrumentalPath find_binary_expression(AstBinaryExpression location) throws Exception {
		/* determine the sequence of executing operands */
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
			AstExpression temp = loperand;
			loperand = roperand;
			roperand = temp;
		}
		default:	break;
		}
		
		/* parsing path */
		InstrumentalPath path = this.new_path();
		this.append_path(path, InstrumentalNode.beg(location));
		path.append(InstrumentalLink.down_flow, this.find(loperand));
		path.append(InstrumentalLink.move_flow, this.find(roperand));
		InstrumentalNode end = InstrumentalNode.end(location);
		this.force_match(end);
		this.append_path(path, InstrumentalLink.upon_flow, end);
		return path;
	}
	private InstrumentalPath find_array_expression(AstArrayExpression location) throws Exception {
		InstrumentalPath path = this.new_path();
		this.append_path(path, InstrumentalNode.beg(location));
		path.append(InstrumentalLink.down_flow, this.find(location.get_array_expression()));
		path.append(InstrumentalLink.move_flow, this.find(location.get_dimension_expression()));
		InstrumentalNode end = InstrumentalNode.end(location);
		this.force_match(end);
		this.append_path(path, InstrumentalLink.upon_flow, end);
		return path;
	}
	private InstrumentalPath find_cast_expression(AstCastExpression location) throws Exception {
		InstrumentalPath path = this.new_path();
		this.append_path(path, InstrumentalNode.beg(location));
		path.append(InstrumentalLink.down_flow, this.find(location.get_expression()));
		InstrumentalNode end = InstrumentalNode.end(location);
		this.force_match(end);
		this.append_path(path, InstrumentalLink.upon_flow, end);
		return path;
	}
	private InstrumentalPath find_comma_expression(AstCommaExpression location) throws Exception {
		InstrumentalPath path = this.new_path();
		this.append_path(path, InstrumentalNode.beg(location));
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
		InstrumentalNode end = InstrumentalNode.end(location);
		this.force_match(end);
		this.append_path(path, InstrumentalLink.upon_flow, end);
		return path;
	}
	private InstrumentalPath find_const_expression(AstConstExpression location) throws Exception {
		return this.find(location.get_expression());
	}
	private InstrumentalPath find_paranth_expression(AstParanthExpression location) throws Exception {
		return this.find(location.get_sub_expression());
	}
	private InstrumentalPath find_field_expression(AstFieldExpression location) throws Exception {
		InstrumentalPath path = this.new_path();
		this.append_path(path, InstrumentalNode.beg(location));
		path.append(InstrumentalLink.down_flow, this.find(location.get_body()));
		InstrumentalNode end = InstrumentalNode.end(location);
		this.force_match(end);
		this.append_path(path, InstrumentalLink.upon_flow, end);
		return path;
	}
	private InstrumentalPath find_sizeof_expression(AstSizeofExpression location) throws Exception {
		InstrumentalPath path = this.new_path();
		InstrumentalNode node = InstrumentalNode.pas(location);
		this.force_match(node);
		this.append_path(path, node);
		return path;
	}
	private InstrumentalPath find_conditional_expression(AstConditionalExpression location) throws Exception {
		InstrumentalPath path = this.new_path();
		this.append_path(path, InstrumentalNode.beg(location));
		path.append(InstrumentalLink.down_flow, this.find(location.get_condition()));
		if(this.condition_value(path, location.get_condition())) {
			path.append(InstrumentalLink.move_flow, 
					this.find(location.get_true_branch()));
		}
		else {
			path.append(InstrumentalLink.move_flow, 
					this.find(location.get_false_branch()));
		}
		InstrumentalNode end = InstrumentalNode.end(location);
		this.force_match(end);
		this.append_path(path, InstrumentalLink.upon_flow, end);
		return path;
	}
	private InstrumentalPath find_fun_call_expression(AstFunCallExpression location) throws Exception {
		/* before calling function */
		InstrumentalPath path = this.new_path();
		this.append_path(path, InstrumentalNode.beg(location));
		path.append(InstrumentalLink.down_flow, this.find(location.get_function()));
		if(location.has_argument_list()) {
			path.append(InstrumentalLink.move_flow, find(location.get_argument_list()));
		}
		this.append_path(path, InstrumentalLink.upon_flow, InstrumentalNode.pas(location));
		
		/* calling the other function(s) */
		while(this.has_unit()) {
			AstNode next_location = this.get_unit_location();
			if(next_location instanceof AstCompoundStatement) {
				AstFunctionDefinition callee = 
						(AstFunctionDefinition) next_location.get_parent();
				path.append(InstrumentalLink.call_flow, this.find(callee));
				this.append_path(path, InstrumentalLink.retr_flow, 
										   InstrumentalNode.pas(location));
			}
			else if(next_location == location || !this.is_instrumental_node(location)) {
				break;
			}
			else {
				throw new RuntimeException("Invalid unit of " + 
						this.get_unit().toString() + " with " + 
						location.getClass().getSimpleName() + "[" + 
						location.get_key() + "] at\n" + location.generate_code());
			}
		}
		
		/* update the return-value of call-expression */
		InstrumentalNode end = path.get_target();
		end.get_unit().set_tag(InstrumentalTag.end);
		this.force_match(end);
		return path;
	}
	private InstrumentalPath find_argument_list(AstArgumentList location) throws Exception {
		InstrumentalPath path = this.new_path();
		this.append_path(path, InstrumentalNode.beg(location));
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
		this.append_path(path, InstrumentalLink.upon_flow, InstrumentalNode.end(location));
		return path;
	}
	/* statement package */
	private InstrumentalPath find_expression_statement(AstExpressionStatement location) throws Exception {
		InstrumentalPath path = this.new_path();
		
		/* beg(statement) #match */
		InstrumentalNode beg = InstrumentalNode.beg(location);
		this.force_match(beg);
		this.append_path(path, beg);
		
		/* down_flow::find(expression) */
		InstrumentalLink return_link;
		if(location.has_expression()) {
			path.append(InstrumentalLink.down_flow, 
					this.find(location.get_expression()));
			return_link = InstrumentalLink.upon_flow;
		}
		else {
			return_link = InstrumentalLink.move_flow;
		}
		
		/* whether the statement ends at the point */
		boolean ignore_statement = false;
		AstNode parent = location.get_parent();
		if(parent instanceof AstForStatement) {
			if(((AstForStatement) parent).get_initializer() == location) {
				ignore_statement = location.has_expression();
			}
			else if(((AstForStatement) parent).get_condition() == location) {
				ignore_statement = location.has_expression();
			}
		}
		
		/* upon_flow::end(statement) */
		InstrumentalNode end = InstrumentalNode.end(location);
		if(!ignore_statement) { this.force_match(end); }
		this.append_path(path, return_link, end);
		return path;
	}
	private InstrumentalPath find_declaration_statement(AstDeclarationStatement location) throws Exception {
		InstrumentalPath path = this.new_path();
		
		InstrumentalNode beg = InstrumentalNode.beg(location);
		this.force_match(beg);
		this.append_path(path, beg);
		path.append(InstrumentalLink.down_flow, 
				this.find(location.get_declaration()));
		
		boolean ignore_end = false;
		if(location.get_parent() instanceof AstForStatement) {
			AstForStatement parent = (AstForStatement) location.get_parent();
			if(parent.get_initializer() == location) {
				ignore_end = true;
			}
		}
		
		InstrumentalNode end = InstrumentalNode.end(location);
		if(!ignore_end) { this.force_match(end); }
		this.append_path(path, InstrumentalLink.upon_flow, end);
		
		return path;
	}
	private InstrumentalPath find_compound_statement(AstCompoundStatement location) throws Exception {
		InstrumentalPath path = this.new_path();
		
		/* try-to-match beg(statement) */
		InstrumentalNode beg = InstrumentalNode.beg(location);
		if(this.safe_match(beg)) {
			this.append_path(path, beg);
			return path;
		}
		
		/* try-to-match end(statement) */
		InstrumentalNode end = InstrumentalNode.end(location);
		if(this.safe_match(end)) {
			this.append_path(path, end);
			return path;
		}
		
		/* unable to match the current node */
		throw new RuntimeException("Unable to match the node of "
				+ location.getClass().getSimpleName() + "[" 
				+ location.get_key() + "] with " + this.get_unit());
	}
	private InstrumentalPath find_goto_statement(AstGotoStatement location) throws Exception {
		InstrumentalPath path = this.new_path();
		InstrumentalNode node = InstrumentalNode.pas(location);
		this.force_match(node);
		this.append_path(path, node);
		return path;
	}
	private InstrumentalPath find_break_statement(AstBreakStatement location) throws Exception {
		InstrumentalPath path = this.new_path();
		InstrumentalNode node = InstrumentalNode.pas(location);
		this.force_match(node);
		this.append_path(path, node);
		return path;
	}
	private InstrumentalPath find_continue_statement(AstContinueStatement location) throws Exception {
		InstrumentalPath path = this.new_path();
		InstrumentalNode node = InstrumentalNode.pas(location);
		this.force_match(node);
		this.append_path(path, node);
		return path;
	}
	private InstrumentalPath find_return_statement(AstReturnStatement location) throws Exception {
		InstrumentalPath path = this.new_path();
		if(location.has_expression()) {
			InstrumentalNode beg = InstrumentalNode.beg(location);
			this.force_match(beg);
			this.append_path(path, beg);
			path.append(InstrumentalLink.down_flow, this.find(location.get_expression()));
			this.append_path(path, InstrumentalLink.upon_flow, InstrumentalNode.end(location));
		}
		else {
			InstrumentalNode beg = InstrumentalNode.pas(location);
			this.force_match(beg);
			this.append_path(path, beg);
		}
		return path;
	}
	private InstrumentalPath find_labeled_statement(AstLabeledStatement location) throws Exception {
		InstrumentalPath path = this.new_path();
		InstrumentalNode node = InstrumentalNode.pas(location);
		this.force_match(node);
		this.append_path(path, node);
		return path;
	}
	private InstrumentalPath find_case_statement(AstCaseStatement location) throws Exception {
		InstrumentalPath path = this.new_path();
		InstrumentalNode node = InstrumentalNode.pas(location);
		this.force_match(node);
		this.append_path(path, node);
		return path;
	}
	private InstrumentalPath find_default_statement(AstDefaultStatement location) throws Exception {
		InstrumentalPath path = this.new_path();
		InstrumentalNode node = InstrumentalNode.pas(location);
		this.force_match(node);
		this.append_path(path, node);
		return path;
	}
	/* block-statement package */
	private InstrumentalPath find_if_statement(AstIfStatement location) throws Exception {
		InstrumentalPath path = this.new_path();
		this.append_path(path, InstrumentalNode.beg(location));
		path.append(InstrumentalLink.down_flow, this.find(location.get_condition()));
		this.append_path(path, InstrumentalLink.upon_flow, InstrumentalNode.end(location));
		return path;
	}
	private InstrumentalPath find_switch_statement(AstSwitchStatement location) throws Exception {
		InstrumentalPath path = this.new_path();
		this.append_path(path, InstrumentalNode.beg(location));
		path.append(InstrumentalLink.down_flow, this.find(location.get_condition()));
		this.append_path(path, InstrumentalLink.upon_flow, InstrumentalNode.end(location));
		return path;
	}
	private InstrumentalPath find_while_statement(AstWhileStatement location) throws Exception {
		InstrumentalPath path = this.new_path();
		this.append_path(path, InstrumentalNode.beg(location));
		path.append(InstrumentalLink.down_flow, this.find(location.get_condition()));
		this.append_path(path, InstrumentalLink.upon_flow, InstrumentalNode.end(location));
		return path;
	}
	private InstrumentalPath find_do_while_statement(AstDoWhileStatement location) throws Exception {
		InstrumentalPath path = this.new_path();
		this.append_path(path, InstrumentalNode.beg(location));
		path.append(InstrumentalLink.down_flow, this.find(location.get_condition()));
		this.append_path(path, InstrumentalLink.upon_flow, InstrumentalNode.end(location));
		return path;
	}
	private InstrumentalPath find_for_statement(AstForStatement location) throws Exception {
		return this.find(location.get_increment());
	}
	/* function package */
	/**
	 * @param location
	 * @return [statement, direct_child]
	 * @throws Exception
	 */
	private AstNode[] statement_context(AstNode location) throws Exception {
		AstNode parent = location.get_parent();
		AstNode direct_child = location;
		while(parent != null) {
			if(parent instanceof AstStatement) {
				break;
			}
			else {
				direct_child = parent;
				parent = parent.get_parent();
			}
		}
		return new AstNode[] { parent, direct_child };
	}
	/**
	 * @return the statement to be executed at the next part
	 * @throws Exception
	 */
	private AstStatement get_next_statement() throws Exception {
		AstNode next_location = this.get_unit_location();
		AstStatement statement; AstNode direct_child;
		if(next_location instanceof AstStatement) {
			statement = (AstStatement) next_location;
		}
		else if(next_location instanceof AstExpression) {
			AstNode[] context = this.statement_context(next_location);
			statement = (AstStatement) context[0];
			direct_child = context[1];
			if(statement instanceof AstIfStatement) {
				if(((AstIfStatement) statement).get_condition() != direct_child) {
					throw new IllegalArgumentException("Invalid location: " + next_location);
				}
			}
			else if(statement instanceof AstSwitchStatement) {
				if(((AstSwitchStatement) statement).get_condition() != direct_child) {
					throw new IllegalArgumentException("Invalid location: " + next_location);
				}
			}
			else if(statement instanceof AstWhileStatement) {
				if(((AstWhileStatement) statement).get_condition() != direct_child) {
					throw new IllegalArgumentException("Invalid location: " + next_location);
				}
			}
			else if(statement instanceof AstDoWhileStatement) {
				if(((AstDoWhileStatement) statement).get_condition() != direct_child) {
					throw new IllegalArgumentException("Invalid location: " + next_location);
				}
			}
			else if(statement instanceof AstForStatement) {
				if(((AstForStatement) statement).get_increment() != direct_child) {
					throw new IllegalArgumentException("Invalid location: " + next_location);
				}
			}
			else {
				throw new IllegalArgumentException("Invalid statement: " + statement);
			}
		}
		else {
			throw new RuntimeException("Invalid location: " + next_location);
		}
		return statement;
	}
	private InstrumentalPath traverse_function_body(AstFunctionDefinition function) throws Exception {
		InstrumentalPath path = this.new_path();
		while(this.has_unit()) {
			/* determine the statement where the parsing starts */
			InstrumentalUnit unit = this.get_unit();
			AstStatement statement = this.get_next_statement();
			
			/* building path based on the next statement point */
			path.append(InstrumentalLink.goto_flow, this.find(statement));
			
			/* determine whether to exit from the function */
			if(statement instanceof AstReturnStatement) {
				path.append(InstrumentalLink.goto_flow, 
						InstrumentalNode.end(function.get_body()));
				break;
			}
			else if(statement instanceof AstCompoundStatement
					&& statement.get_parent() == function &&
					unit.get_tag() == InstrumentalTag.end) {
				break;
			}
		}
		return path;
	}
	private InstrumentalPath find_function_definition(AstFunctionDefinition location) throws Exception {
		InstrumentalPath path = this.new_path();
		this.append_path(path, InstrumentalNode.beg(location));
		path.append(InstrumentalLink.down_flow, this.traverse_function_body(location));
		this.append_path(path, InstrumentalLink.upon_flow, InstrumentalNode.end(location));
		return path;
	}
	
	/* path construction */
	/**
	 * @param tree abstract syntax tree to interpret the instrumental file
	 * @param file the instrumental file from which the path is created
	 * @return find the executional path by parsing the instrumental file
	 * @throws Exception
	 */
	public static InstrumentalPath find(AstTree tree, File file) throws Exception {
		InstrumentalBuilder builder = new InstrumentalBuilder(tree, file);
		if(builder.has_unit()) {
			AstFunctionDefinition main_fun = (AstFunctionDefinition) 
					builder.get_unit().get_location().get_parent();
			return builder.find(main_fun);
		}
		else {
			return builder.new_path();	// empty executional path
		}
	}
	/**
	 * @param tree abstract syntax tree to interpret the instrumental file
	 * @param file the instrumental file from which the path is created
	 * @return read the simple-path without completing the begin and end of the unit
	 * 		   between which the nodes are connected.
	 * @throws Exception
	 */
	public static InstrumentalPath read(AstTree tree, File file) throws Exception {
		InstrumentalPath path = new InstrumentalPath();
		InstrumentalReader reader = new InstrumentalReader(tree, file);
		InstrumentalUnit unit;
		while((unit = reader.next()) != null) {
			InstrumentalNode node = new InstrumentalNode(unit);
			path.append(InstrumentalLink.goto_flow, node);
		}
		return path;
	}
	
}
