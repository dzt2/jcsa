package com.jcsa.jcparse.test.inst;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
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
import com.jcsa.jcparse.lang.ctype.CUnionType;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * It is used to generate the complete sequence of instrumental lines.
 * 
 * @author yukimula
 *
 */
public class InstrumentalLines {
	
	/* definitions */
	/** the template to decode the bytes into Java-Object value **/
	private CRunTemplate template;
	/** the abstract syntax tree on which the instrument seeded **/
	private AstTree ast_tree;
	/** the input stream that read bytes from instrumental file **/
	private InputStream stream;
	/** the current line read from the instrumental file stream **/
	private InstrumentalLine curr_line;
	/** the complete sequence of instrumental lines being parsed **/
	private List<InstrumentalLine> lines;
	
	/* singleton */
	private InstrumentalLines() { }
	private static final InstrumentalLines parser = new InstrumentalLines();
	
	/* stream methods */
	/**
	 * @return whether there is more line from the stream
	 */
	private boolean has_line() { return this.curr_line != null; }
	/**
	 * read the next line from the instrumental file stream
	 * @throws Exception
	 */
	private void next_line() throws Exception {
		if(this.stream != null) {
			this.curr_line = InstrumentalLine.read(template, ast_tree, stream);
		}
		else {
			throw new IllegalArgumentException("Invalid access");
		}
	}
	/**
	 * close the stream for reading instrumental file
	 * @throws Exception
	 */
	private void close() throws Exception {
		if(this.stream != null) {
			this.stream.close();
			this.stream = null;
			this.template = null;
			this.ast_tree = null;
			this.curr_line = null;
		}
	}
	/**
	 * start the stream for reading instrumental file
	 * @param template
	 * @param ast_tree
	 * @param instrumental_file
	 * @throws Exception
	 */
	private void start(CRunTemplate template, AstTree ast_tree, 
					File instrumental_file) throws Exception {
		if(template == null)
			throw new IllegalArgumentException("Invalid template: null");
		else if(ast_tree == null)
			throw new IllegalArgumentException("Invalid ast_tree: null");
		else if(instrumental_file == null || !instrumental_file.exists())
			throw new IllegalArgumentException("Invalid instrumental file");
		else {
			this.close();
			this.template = template;
			this.ast_tree = ast_tree;
			this.lines = new ArrayList<InstrumentalLine>();
			this.stream = new FileInputStream(instrumental_file);
			this.next_line();
		}
	}
	
	/* matching methods */
	/**
	 * @param location
	 * @return create a non-value line w.r.t. the location as given
	 * 		   and append it to the tail of the sequence
	 * @throws Exception
	 */
	private InstrumentalLine new_beg_line(AstNode location) throws Exception {
		InstrumentalLine line = new InstrumentalLine(false, location, null);
		this.lines.add(line);
		return line;
	}
	/**
	 * @param location
	 * @return create a non-value line w.r.t. the location as given
	 * 		   and append it to the tail of the sequence
	 * @throws Exception
	 */
	private InstrumentalLine new_end_line(AstNode location) throws Exception {
		InstrumentalLine line = new InstrumentalLine(true, location, null);
		this.lines.add(line);
		return line;
	}
	/**
	 * 1. if no line exists from stream, return false and do nothing;
	 * 2. if successfully matched, move the stream to the next line &
	 * 	  set the value of the input line from the stream buffer;
	 * 3. if failed to match when line is present, throw exception if
	 * 	  forcely is set as true, or return false otherwise.
	 * @param line
	 * @param forcely
	 * @return match the line with current line
	 * @throws Exception
	 */
	private boolean match_line(InstrumentalLine line, boolean forcely) throws Exception {
		if(this.has_line()) {
			// System.out.println("\t--> Match " + this.curr_line + " with " + line);
			if(line.is_beg() == this.curr_line.is_beg()
				&& line.get_location() == this.curr_line.get_location()) {
				line.set_value(this.curr_line.get_value());
				this.next_line();
				return true;
			}
			else if(forcely && this.is_instrumented(line.get_location())) {
				throw new RuntimeException("Unable to match:\n"
						+ "\tcurr_line: " + this.curr_line.toString() + 
						"\t==> " + this.curr_line.get_location().generate_code()
						+ "\n\tnew_line: " + line.toString() + "\t==> " + 
						line.get_location().generate_code() + "\n");
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	/* lines access */
	/**
	 * @param value
	 * @return translate the object-value into boolean
	 * @throws Exception
	 */
	private boolean get_bool_value(Object value) throws Exception {
		if(value == null)
			throw new IllegalArgumentException("Invalid value: null");
		else if(value instanceof Boolean)
			return ((Boolean) value).booleanValue();
		else if(value instanceof Character)
			return ((Character) value).charValue() != 0;
		else if(value instanceof Short)
			return ((Short) value).shortValue() != 0;
		else if(value instanceof Integer)
			return ((Integer) value).intValue() != 0;
		else if(value instanceof Long)
			return ((Long) value).longValue() != 0L;
		else if(value instanceof Float)
			return ((Float) value).floatValue() != 0;
		else if(value instanceof Double)
			return ((Double) value).doubleValue() != 0;
		else
			throw new IllegalArgumentException("Unsupport: " + value);
	}
	/**
	 * @return the last evaluated line in the path
	 * @throws IndexOutOfBoundsException
	 */
	private InstrumentalLine last_line() throws IndexOutOfBoundsException {
		return this.lines.get(this.lines.size() - 1);
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
	 * @return whether the location can be instrumented
	 * @throws Exception
	 */
	private boolean is_instrumented(AstNode location) throws Exception {
		if(location instanceof AstStatement) {
			return true;
		}
		else if(location instanceof AstExpression) {
			AstExpression expression = (AstExpression) location;
			return this.is_valid_type(expression.get_value_type())
					&& this.is_valid_context(expression)
					&& this.is_valid_expression(expression);
		}
		else {
			return false;
		}
	}
	/**
	 * @param location
	 * @return whether this is an assignment expression
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
	/**
	 * @return whether the next line refers to the beginning of a function being called
	 * @throws Exception
	 */
	private boolean is_next_calling_point() throws Exception {
		if(this.has_line()) {
			if(this.curr_line.is_beg()) {
				AstNode location = this.curr_line.get_location();
				if(location instanceof AstCompoundStatement &&
					location.get_parent() instanceof AstFunctionDefinition) {
					return true;
				}
				else {
					return false;
				}
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	/* construction methods */
	private void parse(AstNode location) throws Exception {
		if(location == null)
			throw new IllegalArgumentException("Invalid location: null");
		else if(location instanceof AstExpression)
			this.parse_expression((AstExpression) location);
		else if(location instanceof AstInitializer)
			this.parse_initializer((AstInitializer) location);
		else if(location instanceof AstInitializerList)
			this.parse_initializer_list((AstInitializerList) location);
		else if(location instanceof AstFieldInitializer)
			this.parse_field_initializer((AstFieldInitializer) location);
		else if(location instanceof AstArgumentList)
			this.parse_argument_list((AstArgumentList) location);
		else if(location instanceof AstDeclaration)
			this.parse_declaration((AstDeclaration) location);
		else if(location instanceof AstInitDeclaratorList)
			this.parse_init_declarator_list((AstInitDeclaratorList) location);
		else if(location instanceof AstInitDeclarator)
			this.parse_init_declarator((AstInitDeclarator) location);
		else if(location instanceof AstDeclarator)
			this.parse_declarator((AstDeclarator) location);
		else if(location instanceof AstDeclarationStatement)
			this.parse_declaration_statement((AstDeclarationStatement) location);
		else if(location instanceof AstExpressionStatement)
			this.parse_expression_statement((AstExpressionStatement) location);
		else if(location instanceof AstGotoStatement)
			this.parse_goto_statement((AstGotoStatement) location);
		else if(location instanceof AstBreakStatement)
			this.parse_break_statement((AstBreakStatement) location);
		else if(location instanceof AstContinueStatement)
			this.parse_continue_statement((AstContinueStatement) location);
		else if(location instanceof AstReturnStatement)
			this.parse_return_statement((AstReturnStatement) location);
		else if(location instanceof AstLabeledStatement)
			this.parse_labeled_statement((AstLabeledStatement) location);
		else if(location instanceof AstCaseStatement)
			this.parse_case_statement((AstCaseStatement) location);
		else if(location instanceof AstDefaultStatement)
			this.parse_default_statement((AstDefaultStatement) location);
		else if(location instanceof AstIfStatement)
			this.parse_if_statement((AstIfStatement) location);
		else if(location instanceof AstSwitchStatement)
			this.parse_switch_statement((AstSwitchStatement) location);
		else if(location instanceof AstForStatement)
			this.parse_for_statement((AstForStatement) location);
		else if(location instanceof AstWhileStatement)
			this.parse_while_statement((AstWhileStatement) location);
		else if(location instanceof AstDoWhileStatement)
			this.parse_do_while_statement((AstDoWhileStatement) location);
		else if(location instanceof AstCompoundStatement)
			this.parse_compound_statement((AstCompoundStatement) location);
		else if(location instanceof AstFunctionDefinition)
			this.parse_function_definition((AstFunctionDefinition) location);
		else
			throw new IllegalArgumentException("Unsupport: " + location);
	}
	
	/* general expression */
	private void parse_id_expression(AstIdExpression location) throws Exception {
		InstrumentalLine line = this.new_end_line(location);
		this.match_line(line, true);
	}
	private void parse_constant(AstConstant location) throws Exception {
		InstrumentalLine line = this.new_end_line(location);
		this.match_line(line, true);
		if(!line.has_value())
			line.set_value(location.get_constant().get_object());
	}
	private void parse_literal(AstLiteral location) throws Exception {
		InstrumentalLine line = this.new_end_line(location);
		this.match_line(line, false);
		line.set_value(location.get_literal());
	}
	private void parse_unary_expression(AstUnaryExpression location) throws Exception {
		this.new_beg_line(location);
		this.parse(location.get_operand());
		this.match_line(this.new_end_line(location), true);
	}
	private void parse_postfix_expression(AstPostfixExpression location) throws Exception {
		this.new_beg_line(location);
		this.parse(location.get_operand());
		this.match_line(this.new_end_line(location), true);
	}
	private void parse_binary_expression(AstBinaryExpression location) throws Exception {
		this.new_beg_line(location);
		if(this.is_assign_expression(location)) {
			this.parse(location.get_roperand());
			this.parse(location.get_loperand());
		}
		else if(location instanceof AstLogicBinaryExpression) {
			this.parse(location.get_loperand());
			if(this.get_bool_value(this.last_line().get_value())) {
				if(location.get_operator().get_operator() == COperator.logic_and) {
					this.parse(location.get_roperand());
				}
			}
			else {
				if(location.get_operator().get_operator() == COperator.logic_or) {
					this.parse(location.get_roperand());
				}
			}
		}
		else {
			this.parse(location.get_loperand());
			this.parse(location.get_roperand());
		}
		this.match_line(this.new_end_line(location), true);
	}
	
	/* special expression */
	private void parse_array_expression(AstArrayExpression location) throws Exception {
		this.new_beg_line(location);
		this.parse(location.get_array_expression());
		this.parse(location.get_dimension_expression());
		this.match_line(this.new_end_line(location), true);
	}
	private void parse_cast_expression(AstCastExpression location) throws Exception {
		this.new_beg_line(location);
		this.parse(location.get_expression());
		this.match_line(this.new_end_line(location), true);
	}
	private void parse_comma_expression(AstCommaExpression location) throws Exception {
		this.new_beg_line(location);
		for(int k = 0; k < location.number_of_arguments(); k++) 
			this.parse(location.get_expression(k));
		this.match_line(this.new_end_line(location), true);
	}
	private void parse_sizeof_expression(AstSizeofExpression location) throws Exception {
		InstrumentalLine line = this.new_end_line(location);
		this.match_line(line, true);
	}
	private void parse_paranth_expression(AstParanthExpression location) throws Exception {
		this.parse(location.get_sub_expression());
	}
	private void parse_const_expression(AstConstExpression location) throws Exception {
		this.parse(location.get_expression());
	}
	private void parse_field_expression(AstFieldExpression location) throws Exception {
		this.new_beg_line(location);
		this.parse(location.get_body());
		this.match_line(this.new_end_line(location), true);
	}
	
	/* initializer expression */
	private void parse_initializer(AstInitializer location) throws Exception {
		if(location.is_body())
			this.parse(location.get_body());
		else
			this.parse(location.get_expression());
	}
	private void parse_initializer_body(AstInitializerBody location) throws Exception {
		this.new_beg_line(location);
		this.parse(location.get_initializer_list());
		this.match_line(this.new_end_line(location), true);
	}
	private void parse_initializer_list(AstInitializerList location) throws Exception {
		for(int k = 0; k < location.number_of_initializer(); k++) {
			this.parse(location.get_initializer(k));
		}
	}
	private void parse_field_initializer(AstFieldInitializer location) throws Exception {
		this.parse(location.get_initializer());
	}
	
	/* transition expressions */
	private void parse_conditional_expression(AstConditionalExpression location) throws Exception {
		this.new_beg_line(location);
		this.parse(location.get_condition());
		if(this.get_bool_value(this.last_line().get_value())) {
			this.parse(location.get_true_branch());
		}
		else {
			this.parse(location.get_false_branch());
		}
		this.match_line(this.new_end_line(location), true);
	}
	private void parse_argument_list(AstArgumentList location) throws Exception {
		this.new_beg_line(location);
		for(int k = 0; k < location.number_of_arguments(); k++) {
			this.parse(location.get_argument(k));
		}
		this.new_end_line(location);
	}
	private void parse_fun_call_expression(AstFunCallExpression location) throws Exception {
		this.new_beg_line(location);
		this.parse(location.get_function());
		if(location.has_argument_list()) {
			this.parse(location.get_argument_list());
		}
		
		/* function calling part */
		while(this.is_next_calling_point()) {
			AstFunctionDefinition callee = (AstFunctionDefinition) 
					this.curr_line.get_location().get_parent();
			this.parse(callee);
		}
		
		this.match_line(this.new_end_line(location), true);
	}
	private void parse_expression(AstExpression location) throws Exception {
		if(location instanceof AstIdExpression)
			this.parse_id_expression((AstIdExpression) location);
		else if(location instanceof AstConstant)
			this.parse_constant((AstConstant) location);
		else if(location instanceof AstLiteral)
			this.parse_literal((AstLiteral) location);
		else if(location instanceof AstUnaryExpression)
			this.parse_unary_expression((AstUnaryExpression) location);
		else if(location instanceof AstPostfixExpression)
			this.parse_postfix_expression((AstPostfixExpression) location);
		else if(location instanceof AstBinaryExpression)
			this.parse_binary_expression((AstBinaryExpression) location);
		else if(location instanceof AstArrayExpression)
			this.parse_array_expression((AstArrayExpression) location);
		else if(location instanceof AstCastExpression)
			this.parse_cast_expression((AstCastExpression) location);
		else if(location instanceof AstConditionalExpression)
			this.parse_conditional_expression((AstConditionalExpression) location);
		else if(location instanceof AstConstExpression)
			this.parse_const_expression((AstConstExpression) location);
		else if(location instanceof AstFieldExpression)
			this.parse_field_expression((AstFieldExpression) location);
		else if(location instanceof AstFunCallExpression)
			this.parse_fun_call_expression((AstFunCallExpression) location);
		else if(location instanceof AstInitializerBody)
			this.parse_initializer_body((AstInitializerBody) location);
		else if(location instanceof AstSizeofExpression)
			this.parse_sizeof_expression((AstSizeofExpression) location);
		else if(location instanceof AstParanthExpression)
			this.parse_paranth_expression((AstParanthExpression) location);
		else if(location instanceof AstCommaExpression)
			this.parse_comma_expression((AstCommaExpression) location);
		else
			throw new IllegalArgumentException(location.toString());
	}
	
	/* declaration package */
	private void parse_declaration(AstDeclaration location) throws Exception {
		if(location.has_declarator_list()) {
			this.parse(location.get_declarator_list());
		}
	}
	private void parse_init_declarator_list(AstInitDeclaratorList location) throws Exception {
		for(int k = 0; k < location.number_of_init_declarators(); k++) {
			this.parse(location.get_init_declarator(k));
		}
	}
	private void parse_init_declarator(AstInitDeclarator location) throws Exception {
		this.new_beg_line(location);
		this.parse(location.get_declarator());
		if(location.has_initializer())
			this.parse(location.get_initializer());
		this.new_end_line(location);
	}
	private void parse_declarator(AstDeclarator location) throws Exception {
		this.new_end_line(location);
	}
	
	/* basic statements */
	private void parse_declaration_statement(AstDeclarationStatement location) throws Exception {
		this.match_line(this.new_beg_line(location), false);
		this.parse(location.get_declaration());
		this.match_line(this.new_end_line(location), false);
	}
	private void parse_expression_statement(AstExpressionStatement location) throws Exception {
		this.match_line(this.new_beg_line(location), true);
		if(location.has_expression()) {
			this.parse(location.get_expression());
		}
		this.match_line(this.new_end_line(location), false);
	}
	private void parse_goto_statement(AstGotoStatement location) throws Exception {
		this.match_line(this.new_beg_line(location), true);
		this.match_line(this.new_end_line(location), false);
	}
	private void parse_break_statement(AstBreakStatement location) throws Exception {
		this.match_line(this.new_beg_line(location), true);
		this.match_line(this.new_end_line(location), false);
	}
	private void parse_continue_statement(AstContinueStatement location) throws Exception {
		this.match_line(this.new_beg_line(location), true);
		this.match_line(this.new_end_line(location), false);
	}
	private void parse_return_statement(AstReturnStatement location) throws Exception {
		this.match_line(this.new_beg_line(location), true);
		if(location.has_expression()) {
			this.parse(location.get_expression());
		}
		this.match_line(this.new_end_line(location), false);
	}
	private void parse_labeled_statement(AstLabeledStatement location) throws Exception {
		this.match_line(this.new_beg_line(location), false);
		this.match_line(this.new_end_line(location), true);
	}
	private void parse_default_statement(AstDefaultStatement location) throws Exception {
		this.match_line(this.new_beg_line(location), false);
		this.match_line(this.new_end_line(location), true);
	}
	private void parse_case_statement(AstCaseStatement location) throws Exception {
		this.match_line(this.new_beg_line(location), false);
		this.parse(location.get_expression());
		this.match_line(this.new_end_line(location), true);
	}
	
	/* structure statement */
	private void parse_if_statement(AstIfStatement location) throws Exception {
		if(this.has_line() && this.curr_line.get_location() == location) {
			if(this.curr_line.is_beg()) {
				this.match_line(this.new_beg_line(location), true);
				this.parse(location.get_condition());
			}
			else {
				this.match_line(this.new_end_line(location), true);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid match location");
		}
	}
	private void parse_switch_statement(AstSwitchStatement location) throws Exception {
		if(this.has_line() && this.curr_line.get_location() == location) {
			if(this.curr_line.is_beg()) {
				this.match_line(this.new_beg_line(location), true);
				this.parse(location.get_condition());
			}
			else {
				this.match_line(this.new_end_line(location), true);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid match location");
		}
	}
	private void parse_while_statement(AstWhileStatement location) throws Exception {
		if(this.has_line() && this.curr_line.get_location() == location) {
			if(this.curr_line.is_beg()) {
				this.match_line(this.new_beg_line(location), true);
			}
			else {
				this.match_line(this.new_end_line(location), true);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid match location");
		}
	}
	private void parse_do_while_statement(AstDoWhileStatement location) throws Exception {
		if(this.has_line() && this.curr_line.get_location() == location) {
			if(this.curr_line.is_beg()) {
				this.match_line(this.new_beg_line(location), true);
			}
			else {
				this.match_line(this.new_end_line(location), true);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid match location");
		}
	}
	private void parse_for_statement(AstForStatement location) throws Exception {
		if(this.has_line() && this.curr_line.get_location() == location) {
			if(this.curr_line.is_beg()) {
				this.match_line(this.new_beg_line(location), true);
			}
			else {
				this.match_line(this.new_end_line(location), true);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid match location");
		}
	}
	private void parse_compound_statement(AstCompoundStatement location) throws Exception {
		if(this.has_line() && this.curr_line.get_location() == location) {
			if(location.get_parent() instanceof AstFunctionDefinition) {
				if(this.curr_line.is_beg()) {
					this.match_line(this.new_beg_line(location), true);
				}
				else {
					this.match_line(this.new_end_line(location), true);
				}
			}
			else {
				this.next_line();	/* ignore local compound statement */
			}
		}
		else {
			throw new IllegalArgumentException("Invalid match location");
		}
	}
	
	/* linear sequence directions */
	/**
	 * @param location
	 * @return reach the expression under the top statement
	 * @throws Exception
	 */
	private AstNode get_top_location(AstNode location) throws Exception {
		if(location instanceof AstExpression) {
			AstNode child = location;
			AstNode parent = child.get_parent();
			while(!(parent instanceof AstStatement)) {
				child = parent;
				parent = parent.get_parent();
			}
			return child;
		}
		else {
			return location;
		}
	}
	/**
	 * @param line
	 * @return whether the current line reaches the end of the function
	 * @throws Exception
	 */
	private boolean is_end_of_function(InstrumentalLine line) throws Exception {
		AstNode location = line.get_location();
		if(location instanceof AstCompoundStatement && location.
				get_parent() instanceof AstFunctionDefinition) {
			return line.is_end();
		}
		else if(location instanceof AstReturnStatement) {
			return line.is_end();
		}
		else {
			return false;
		}
	}
	private void parse_function_definition(AstFunctionDefinition location) throws Exception {
		this.new_beg_line(location);
		while(this.has_line()) {
			/*
			System.out.println("\t\t--> " + 
					this.curr_line.get_location().getClass().getSimpleName());
			*/
			AstNode next_location = this.get_top_location(this.curr_line.get_location());
			this.parse(next_location);
			InstrumentalLine last_line = this.last_line();
			if(this.is_end_of_function(last_line)) break;
		}
		this.new_end_line(location);
	}
	
	/* public interface */
	public static List<InstrumentalLine> simple_lines(CRunTemplate template,
			AstTree ast_tree, File instrumental_file) throws Exception {
		if(template == null)
			throw new IllegalArgumentException("Invalid template: null");
		else if(ast_tree == null)
			throw new IllegalArgumentException("Invalid ast_tree: null");
		else if(instrumental_file == null || !instrumental_file.exists())
			throw new IllegalArgumentException("Invalid instrumental file");
		else {
			List<InstrumentalLine> lines = new ArrayList<InstrumentalLine>();
			InputStream stream = new FileInputStream(instrumental_file);
			InstrumentalLine line;
			while((line = InstrumentalLine.read(template, ast_tree, stream)) != null) {
				lines.add(line);
			}
			stream.close();
			return lines;
		}
	}
	public static List<InstrumentalLine> complete_lines(CRunTemplate template,
			AstTree ast_tree, File instrumental_file) throws Exception {
		if(template == null)
			throw new IllegalArgumentException("Invalid template: null");
		else if(ast_tree == null)
			throw new IllegalArgumentException("Invalid ast_tree: null");
		else if(instrumental_file == null || !instrumental_file.exists())
			throw new IllegalArgumentException("Invalid instrumental file");
		else {
			parser.start(template, ast_tree, instrumental_file);
			if(parser.has_line()) {
				AstFunctionDefinition main = (AstFunctionDefinition) 
						parser.curr_line.get_location().get_parent();
				parser.parse(main);
			}
			parser.close();
			return parser.lines;
		}
	}
	
}
