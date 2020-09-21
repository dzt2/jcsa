package com.jcsa.jcparse.test.inst;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.astree.decl.AstDeclaration;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclaratorList;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstFieldInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerList;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstBasicExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPointUnaryExpression;
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
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstGotoStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CFunctionType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CQualifierType;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CUnionType;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * It parses the instrumental line to construct the instrumental tree
 * under the abstract syntactic tree.
 * 
 * @author yukimula
 *
 */
public class InstrumentalParse {
	
	/* definitions */
	/** the template to decode bytes **/
	private CRunTemplate template;
	/** abstract syntax tree of code being instrumented **/
	private AstTree ast_tree;
	/** the input stream to read instrumental lines from **/
	private InputStream instream;
	/** the instrumental line read from the stream or null when EOF reached **/
	private InstrumentalLine curr_line;
	/** the tree to construct the instrumental line **/
	private InstrumentalTree inst_tree;
	/** the tree node being used currently as the parent in the parsing way **/
	private InstrumentalNode curr_node;
	
	/* constructor */
	private InstrumentalParse() { }
	private static final InstrumentalParse parser = new InstrumentalParse();
	
	/* stream IO methods */
	/**
	 * set the program data with sizeof-template and AST
	 * @param template
	 * @param ast_tree
	 * @throws IllegalArgumentException
	 */
	private void set_program(CRunTemplate template, AstTree ast_tree) throws IllegalArgumentException {
		if(template == null)
			throw new IllegalArgumentException("Invalid template: null");
		else if(ast_tree == null)
			throw new IllegalArgumentException("Invalid ast_tree: null");
		else { 
			this.template = template; 
			this.ast_tree = ast_tree;
			this.inst_tree = new InstrumentalTree(template, ast_tree);
		}
	}
	/**
	 * open the input stream to read instrumental lines from file
	 * @param instrumental_file
	 * @throws Exception
	 */
	private void open_stream(File instrumental_file) throws Exception {
		if(instrumental_file == null || !instrumental_file.exists())
			throw new IllegalArgumentException("Undefined: " + instrumental_file);
		else {
			this.close_stream();
			this.instream = new FileInputStream(instrumental_file);
			this.move_to_next_line();
		}
	}
	/**
	 * @return whether the input stream reaches the EOF.
	 */
	private boolean has_line() { return this.curr_line != null; }
	/**
	 * read the stream and update the next line from stream
	 * @throws Exception
	 */
	private void move_to_next_line() throws Exception {
		if(this.template == null || this.ast_tree == null)
			throw new IllegalArgumentException("No program used");
		else if(this.instream == null)
			throw new IllegalArgumentException("No instrumental file");
		else {
			this.curr_line = InstrumentalLine.
					read(this.template, this.ast_tree, this.instream);
		}
	}
	/**
	 * close the input stream for reading the instrumental lines
	 * @throws Exception
	 */
	private void close_stream() throws Exception {
		if(this.instream != null) {
			this.instream.close();
			this.instream = null;
		}
	}
	
	/* line matching algorithms */
	/**
	 * 1. match the node with current line, expected as beg::location
	 * 2. if successfully matched, update the value in unit using value of line;
	 * 3. if successfully matched, update the input stream to get the next line.
	 * @param node
	 * @return
	 * @throws Exception
	 */
	private boolean match_beg_location(InstrumentalNode node) throws Exception {
		if(this.curr_line != null) {
			if(this.curr_line.is_beg() && this.curr_line.
					get_location() == node.get_unit().get_location()) {
				node.get_unit().set_value(this.curr_line.get_value());
				this.move_to_next_line();
				return true;	/* set state, stream, return true */
			}
			else {
				return false;	/* unable to match the location */
			}
		}
		else {
			return false;		/* no more line exists after EOF */
		}
	}
	/**
	 * 1. match the node with current line, expected as end::location
	 * 2. if successfully matched, update the value in unit using value of line;
	 * 3. if successfully matched, update the input stream to get the next line.
	 * @param node
	 * @return
	 * @throws Exception
	 */
	private boolean match_end_location(InstrumentalNode node) throws Exception {
		if(this.curr_line != null) {
			if(this.curr_line.is_end() && this.curr_line.
					get_location() == node.get_unit().get_location()) {
				node.get_unit().set_value(this.curr_line.get_value());
				this.move_to_next_line();
				return true;	/* set state, stream, return true */
			}
			else {
				return false;	/* unable to match the location */
			}
		}
		else {
			return false;		/* no more line exists after EOF */
		}
	}
	/**
	 * @param location
	 * @param match_result
	 * @return whether it matches with the current line
	 * @throws Exception is thrown when it fails to match & more lines matched
	 */
	private boolean check_match_result(AstNode location, boolean match_result) throws Exception {
		if(match_result) {
			return true;
		}
		else if(this.has_line() && this.is_instrumented_location(location)) {
			throw new RuntimeException("Unable to match line: " + this.
					curr_line + " at location " + location.generate_code());
		}
		else {
			return false;	/* ignore the exception when no line exists */
		}
	}
	/**
	 * @param forcelly whether to throw exception to the users
	 * @return match the current tree node with the current line
	 * @throws Exception
	 */
	private boolean match_beg(boolean forcelly) throws Exception {
		boolean match_result = this.match_beg_location(this.curr_node);
		if(forcelly) {
			match_result = this.check_match_result(this.
					curr_node.get_unit().get_location(), match_result);
		}
		return match_result;
	}
	/**
	 * @param forcelly whether to throw exception to the users
	 * @return match the current tree node with the current line
	 * @throws Exception
	 */
	private boolean match_end(boolean forcelly) throws Exception {
		boolean match_result = this.match_end_location(this.curr_node);
		if(forcelly) {
			match_result = this.check_match_result(this.
					curr_node.get_unit().get_location(), match_result);
		}
		return match_result;
	}
	
	/* location verifiers */
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
		AstNode parent = context.get_parent();
		while(parent != null) {
			if(parent instanceof AstInitializerBody) {
				return false;
			}
			else if(parent instanceof AstCaseStatement) {
				return false;
			}
			else {
				parent = parent.get_parent();
			}
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
	 * @param location
	 * @return 
	 * @throws Exception
	 */
	private boolean is_instrumented_location(AstNode location) throws Exception {
		if(location instanceof AstExpression) {
			AstExpression expression = (AstExpression) location;
			return this.is_valid_type(expression.get_value_type())
					&& this.is_valid_context(expression) 
					&& this.is_valid_expression(expression);
		}
		else if(location instanceof AstStatement) {
			return true;
		}
		else {
			return false;
		}
	}
	/**
	 * @param location
	 * @return whether the expression is an assignment
	 * @throws Exception
	 */
	private boolean is_assign_expression(AstBinaryExpression location) throws Exception {
		COperator operator = location.get_operator().get_operator();
		switch(operator) {
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
		case righ_shift_assign:	return true;
		default:				return false;
		}
	}
	
	/* tree node management */
	/**
	 * @param location
	 * @return create the instrumental node in the tree
	 * @throws Exception
	 */
	private InstrumentalNode new_tree_node(AstNode location) throws Exception {
		if(this.inst_tree == null)
			throw new IllegalArgumentException("Invalid access: no tree");
		else
			return this.inst_tree.new_node(location);
	}
	/**
	 * generate a new node w.r.t. the location and add it to the current node
	 * as its child, and finally set the current node as the child itself.
	 * 
	 * @param location
	 * @throws Exception
	 */
	private void push_tree(AstNode location) throws Exception {
		InstrumentalNode child = this.new_tree_node(location);
		if(this.curr_node != null) {
			this.curr_node.add_child(child);
		}
	}
	/**
	 * remove the current node and set to its parent
	 * @throws Exception
	 */
	private void pop_tree() throws Exception {
		this.curr_node = this.curr_node.get_parent();
	}
	
	/* parsing methods */
	private void parse(AstNode location) throws Exception {
		if(location == null)
			throw new IllegalArgumentException("Invalid location: null");
		/* TODO to implement the syntax-directed translation algorithm */
		else
			throw new IllegalArgumentException("Unsupport: " + location);
	}
	
	/* expression part */
	private void parse_id_expression(AstIdExpression location) throws Exception {
		this.push_tree(location);
		this.match_end(true);
		this.pop_tree();
	}
	private void parse_constant(AstConstant location) throws Exception {
		this.push_tree(location);
		this.match_end(true);
		this.pop_tree();
	}
	private void parse_literal(AstLiteral location) throws Exception {
		this.push_tree(location);
		this.curr_node.get_unit().set_value(location.get_literal());
		this.pop_tree();
	}
	private void parse_unary_expression(AstUnaryExpression location) throws Exception {
		this.push_tree(location);
		this.parse(location.get_operand());
		this.match_end(true);
		this.pop_tree();
	}
	private void parse_postfix_expression(AstPostfixExpression location) throws Exception {
		this.push_tree(location);
		this.parse(location.get_operand());
		this.match_end(true);
		this.pop_tree();
	}
	private void parse_binary_expression(AstBinaryExpression location) throws Exception {
		this.push_tree(location);
		if(this.is_assign_expression(location)) {
			this.parse(location.get_roperand());
			this.parse(location.get_loperand());
		}
		else {
			this.parse(location.get_loperand());
			this.parse(location.get_roperand());
		}
		this.match_end(true);
		this.pop_tree();
	}
	private void parse_array_expression(AstArrayExpression location) throws Exception {
		this.push_tree(location);
		this.parse(location.get_array_expression());
		this.parse(location.get_dimension_expression());
		this.match_end(true);
		this.pop_tree();
	}
	private void parse_cast_expression(AstCastExpression location) throws Exception {
		this.push_tree(location);
		this.parse(location.get_expression());
		this.match_end(true);
		this.pop_tree();
	}
	private void parse_comma_expression(AstCommaExpression location) throws Exception {
		this.push_tree(location);
		for(int k = 0; k < location.number_of_arguments(); k++) {
			this.parse(location.get_expression(k));
		}
		this.match_end(true);
		this.pop_tree();
	}
	private void parse_const_expression(AstConstExpression location) throws Exception {
		this.parse(location.get_expression());
	}
	private void parse_paranth_expression(AstParanthExpression location) throws Exception {
		this.parse(location.get_sub_expression());
	}
	private void parse_sizeof_expression(AstSizeofExpression location) throws Exception {
		this.push_tree(location);
		this.match_end(true);
		this.pop_tree();
	}
	private void parse_field_expression(AstFieldExpression location) throws Exception {
		this.push_tree(location);
		this.parse(location.get_body());
		this.pop_tree();
	}
	
	
	
}
