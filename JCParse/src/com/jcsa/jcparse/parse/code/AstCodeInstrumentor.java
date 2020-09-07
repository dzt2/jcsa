package com.jcsa.jcparse.parse.code;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.astree.decl.AstDeclaration;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstIdentifierList;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclaratorList;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstFieldInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerList;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstDeclarationSpecifiers;
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
import com.jcsa.jcparse.lang.astree.stmt.AstStatementList;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.astree.unit.AstDeclarationList;
import com.jcsa.jcparse.lang.astree.unit.AstExternalUnit;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.astree.unit.AstTranslationUnit;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CFunctionType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CQualifierType;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CUnionType;

/**
 * It generates the instrumental code that can monitor the state of program under
 * test during it being executed by following source code format:<br>
 * <code>
 * 	#include "jcinst.h"
 * 	...
 * 	...
 * 	int f(int x, int y) {
 * 		...
 * 		y = jcm_add(17, jcm_add(15, x) + jcm_add(16, y));
 * 		...
 * 		return jcm_add(39, y);
 * 	}
 * 	...
 * 	...
 * 	double g(double x, double y, double z) {
 * 		...
 * 		result1 = jcm_add(196, f(jcm_add(194, x), jcm_add(195, y)));
 * 		result2 = jcm_add(199, f(jcm_add(197, y), jcm_add(198, z)));
 * 		...
 * 	}
 * 	...
 * 	...
 * 	int main(int argc, char *argv[]) {	jcm_sta($RESULT_PATH);	// starting-method
 *		...
 *		...
 *		x = jcm_add(1004, atoi(jcm_add(1003, jcm_add(1001, argv)[jcm_add(1002, 1)])));
 *		y = jcm_add(1008, atoi(jcm_add(1007, jcm_add(1005, argv)[jcm_add(1006, 2)])));
 *		answer = jcm_add(1012, g(jcm_add(1009, x), jcm_add(1010, y), jcm_add(1011, 16.5)));
 *		...
 *		...
 *		return jcm_add(1024, 0); 		
 * 	}
 * 	...
 * 	...
 * </code>
 * <br>
 * In source code instrumentation, the code can be classified in three categories:<br>
 * 	1. non-instrumented method: the function is not instrumented without any jcm_add;<br>
 * 	2. instrumented method: the function is instrumented with all the expressions being 
 * 	   inserted with a calling as jcm_add(id, expr), where id is the AstNode.get_key() 
 * 	   of that abstract syntactic node of the expr.<br>
 * 	3. starting-method: the function refers to the main function, where the jcm_sta()
 * 	   is inserted at the head of the method, in which the result file path needs to 
 * 	   be specified by external users who want to instrument the file.<br>
 *  <br>
 * @author yukimula
 *
 */
public class AstCodeInstrumentor {
	
	/* code parameters */
	private static final String jcm_add_head = "jcm_add(%d, ";
	private static final String jcm_add_tail = ")";
	private static final String jcm_prev_template = "jcm_prev(%d)";
	private static final String jcm_post_template = "jcm_post(%d)";
	private static final String jcm_open_template = "jcm_sta(\"%s\");";
	private static final String jcm_include_code = "\n#include \"jcinst.h\"\t// instrumental library\n";
	
	/* attributes */
	/** the number of tabs at the beginning of the new line **/
	private int tabs;
	/** to preserve the code that has been generated by now **/
	private StringBuilder buffer;
	
	/* input parameters */
	/** the function where the jcm_sta() is applied to write result. **/
	private AstFunctionDefinition start_function;
	/** the file that is used to preserve the instrumentation result. **/
	private File output_file;
	/** the functions that are instrumented with jcm_add, jcm_prev and jcm_post **/
	private Set<AstFunctionDefinition> ifunctions;
	
	/* constructor & singleton */
	/**
	 * private constructor for singleton mode
	 */
	private AstCodeInstrumentor() {
		this.tabs = 0;
		this.buffer = new StringBuilder();
		
		this.start_function = null;
		this.output_file = null;
		this.ifunctions = new HashSet<AstFunctionDefinition>();
	}
	/** the singleton to generate the code that describes the structure of AST node **/
	private static final AstCodeInstrumentor instrumentor = new AstCodeInstrumentor();
	
	/* utility methods */
	/**
	 * initialize the code generator
	 */
	private void input(AstFunctionDefinition start_function,
			Iterable<AstFunctionDefinition> ifunctions,
			File output_file) throws Exception {
		if(ifunctions == null)
			throw new IllegalArgumentException("Invalid ifunctions set");
		else if(output_file == null)
			throw new IllegalArgumentException("Invalid output_file");
		else {
			this.tabs = 0;
			this.buffer.setLength(0);
			this.start_function = start_function;
			this.ifunctions.clear();
			if(start_function != null)
				this.ifunctions.add(start_function);
			for(AstFunctionDefinition ifunction : ifunctions) {
				if(ifunction != null)
					this.ifunctions.add(ifunction);
			}
			this.output_file = output_file;
		}
	}
	/**
	 * \n\t\t\t...\t
	 */
	private void new_line() {
		buffer.append("\n");
		for(int k = 0; k < this.tabs; k++) {
			buffer.append("\t");
		}
	}
	/**
	 * @param node
	 * @throws Exception
	 * @return generate the original code that the AST-node describes
	 */
	private void gen(AstNode node) throws Exception {
		this.buffer.append(AstCodeGenerator.generate(node));
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
	 * insert the "jcm_add(expr_id, " into buffer
	 * @param node
	 * @throws Exception
	 */
	private void ins_jcm_add_head(AstExpression node) throws Exception {
		if(this.is_valid_context(node)
				&& this.is_valid_type(node.get_value_type())
				&& this.is_valid_expression(node)) {
			this.buffer.append(String.format(jcm_add_head, node.get_key()));
		}
	}
	/**
	 * insert ")" into buffer
	 * @param node
	 * @throws Exception
	 */
	private void ins_jcm_add_tail(AstExpression node) throws Exception {
		if(this.is_valid_context(node)
				&& this.is_valid_type(node.get_value_type())
				&& this.is_valid_expression(node)) {
			this.buffer.append(jcm_add_tail);
		}
	}
	/**
	 * jcm_prev(statement.id)
	 * @param node
	 * @throws Exception
	 */
	private void ins_jcm_prev(AstStatement node) throws Exception {
		this.buffer.append(String.format(jcm_prev_template, node.get_key()));
	}
	/**
	 * jcm_post(statement.id)
	 * @param node
	 * @throws Exception
	 */
	private void ins_jcm_post(AstStatement node) throws Exception {
		this.buffer.append(String.format(jcm_post_template, node.get_key()));
	}
	/**
	 * jcm_prev(statement.id);
	 * @param node
	 * @throws Exception
	 */
	private void ins_jcm_prev_statement(AstStatement node) throws Exception {
		this.buffer.append(String.format(jcm_prev_template, node.get_key()));
		this.buffer.append("; ");
	}
	/**
	 * jcm_post(statement.id);
	 * @param node
	 * @throws Exception
	 */
	private void ins_jcm_post_statement(AstStatement node) throws Exception {
		this.buffer.append(" ");
		this.buffer.append(String.format(jcm_post_template, node.get_key()));
		this.buffer.append(";");
	}
	/**
	 * @param statement
	 * @return whether the statement is empty
	 */
	private boolean is_empty_statement(AstStatement statement) {
		if(statement instanceof AstExpressionStatement) {
			return !((AstExpressionStatement) statement).has_expression();
		}
		else {
			return false;
		}
	}
	/**
	 * jcm_sta(output_file_path);
	 */
	private void ins_jcm_open() {
		this.buffer.append(String.format(jcm_open_template, this.output_file.getAbsolutePath()));
	}
	
	/* instrumental methods */
	/**
	 * generate the instrumental code with respect to the node as given.
	 * @param node
	 * @throws Exception
	 */
	private void ins(AstNode node) throws Exception {
		if(node == null)
			throw new IllegalArgumentException("Invalid node: null");
		else if(node instanceof AstDeclaration)
			this.ins_declaration((AstDeclaration) node);
		else if(node instanceof AstInitDeclaratorList) 
			this.ins_init_declarator_list((AstInitDeclaratorList) node);
		else if(node instanceof AstInitDeclarator)
			this.ins_init_declarator((AstInitDeclarator) node);
		else if(node instanceof AstInitializer)
			this.ins_initializer((AstInitializer) node);
		else if(node instanceof AstInitializerBody)
			this.ins_initializer_body((AstInitializerBody) node);
		else if(node instanceof AstInitializerList)
			this.ins_initializer_list((AstInitializerList) node);
		else if(node instanceof AstFieldInitializer)
			this.ins_field_initializer((AstFieldInitializer) node);
		else if(node instanceof AstIdExpression)
			this.ins_id_expression((AstIdExpression) node);
		else if(node instanceof AstConstant)
			this.ins_constant((AstConstant) node);
		else if(node instanceof AstLiteral)
			this.ins_literal((AstLiteral) node);
		else if(node instanceof AstUnaryExpression)
			this.ins_unary_expression((AstUnaryExpression) node);
		else if(node instanceof AstPostfixExpression)
			this.ins_postfix_expression((AstPostfixExpression) node);
		else if(node instanceof AstBinaryExpression)
			this.ins_binary_expression((AstBinaryExpression) node);
		else if(node instanceof AstArrayExpression)
			this.ins_array_expression((AstArrayExpression) node);
		else if(node instanceof AstCastExpression)
			this.ins_cast_expression((AstCastExpression) node);
		else if(node instanceof AstCommaExpression)
			this.ins_comma_expression((AstCommaExpression) node);
		else if(node instanceof AstConditionalExpression)
			this.ins_conditional_expression((AstConditionalExpression) node);
		else if(node instanceof AstFieldExpression)
			this.ins_field_expression((AstFieldExpression) node);
		else if(node instanceof AstFunCallExpression)
			this.ins_fun_call_expression((AstFunCallExpression) node);
		else if(node instanceof AstArgumentList)
			this.ins_argument_list((AstArgumentList) node);
		else if(node instanceof AstSizeofExpression)
			this.ins_sizeof_expression((AstSizeofExpression) node);
		else if(node instanceof AstParanthExpression)
			this.ins_paranth_expression((AstParanthExpression) node);
		else if(node instanceof AstConstExpression)
			this.ins_const_expression((AstConstExpression) node);
		else if(node instanceof AstBreakStatement)
			this.ins_break_statement((AstBreakStatement) node);
		else if(node instanceof AstContinueStatement)
			this.ins_continue_statement((AstContinueStatement) node);
		else if(node instanceof AstGotoStatement)
			this.ins_goto_statement((AstGotoStatement) node);
		else if(node instanceof AstReturnStatement)
			this.ins_return_statement((AstReturnStatement) node);
		else if(node instanceof AstLabeledStatement)
			this.ins_labeled_statement((AstLabeledStatement) node);
		else if(node instanceof AstCaseStatement)
			this.ins_case_statement((AstCaseStatement) node);
		else if(node instanceof AstDefaultStatement)
			this.ins_default_statement((AstDefaultStatement) node);
		else if(node instanceof AstExpressionStatement)
			this.ins_expression_statement((AstExpressionStatement) node);
		else if(node instanceof AstDeclarationStatement)
			this.ins_declaration_statement((AstDeclarationStatement) node);
		else if(node instanceof AstCompoundStatement)
			this.ins_compound_statement((AstCompoundStatement) node);
		else if(node instanceof AstStatementList)
			this.ins_statement_list((AstStatementList) node);
		else if(node instanceof AstIfStatement)
			this.ins_if_statement((AstIfStatement) node);
		else if(node instanceof AstSwitchStatement)
			this.ins_switch_statement((AstSwitchStatement) node);
		else if(node instanceof AstForStatement)
			this.ins_for_statement((AstForStatement) node);
		else if(node instanceof AstWhileStatement)
			this.ins_while_statement((AstWhileStatement) node);
		else if(node instanceof AstDoWhileStatement)
			this.ins_do_while_statement((AstDoWhileStatement) node);
		else if(node instanceof AstFunctionDefinition)
			this.ins_function_definition((AstFunctionDefinition) node);
		else if(node instanceof AstTranslationUnit)
			this.ins_translation_unit((AstTranslationUnit) node);
		else
			throw new IllegalArgumentException("Unsupport: " + node);
	}
	/* declaration package */
	private void ins_declaration(AstDeclaration node) throws Exception {
		this.gen(node.get_specifiers());
		if(node.has_declarator_list()) {
			this.buffer.append(" ");
			this.ins(node.get_declarator_list());
		}
	}
	private void ins_init_declarator_list(AstInitDeclaratorList node) throws Exception {
		for(int k = 0; k < node.number_of_init_declarators(); k++) {
			this.ins(node.get_init_declarator(k));
			if(k < node.number_of_init_declarators() - 1) {
				this.buffer.append(", ");
			}
		}
	}
	private void ins_init_declarator(AstInitDeclarator node) throws Exception {
		this.gen(node.get_declarator());
		if(node.has_initializer()) {
			this.buffer.append(" = ");
			this.ins(node.get_initializer());
		}
	}
	private void ins_initializer(AstInitializer node) throws Exception {
		if(node.is_body()) {
			this.ins(node.get_body());
		}
		else {
			this.ins(node.get_expression());
		}
	}
	private void ins_initializer_body(AstInitializerBody node) throws Exception {
		/*
		this.buffer.append("{ ");
		this.ins(node.get_initializer_list());
		if(node.has_tail_comma()) {
			this.buffer.append(", ");
		}
		this.buffer.append("}");
		*/
		/* TODO to avoid instrument in initializer elements */
		this.gen(node);
	}
	private void ins_initializer_list(AstInitializerList node) throws Exception {
		for(int k = 0; k < node.number_of_initializer(); k++) {
			this.ins(node.get_initializer(k));
			if(k < node.number_of_initializer() - 1) {
				this.buffer.append(", ");
			}
		}
	}
	private void ins_field_initializer(AstFieldInitializer node) throws Exception {
		if(node.has_designator_list()) {
			this.gen(node.get_designator_list());
			this.buffer.append(" = ");
		}
		this.ins(node.get_initializer());
	}
	/* expression package */
	private void ins_id_expression(AstIdExpression node) throws Exception {
		this.ins_jcm_add_head(node);
		this.gen(node);
		this.ins_jcm_add_tail(node);
	}
	private void ins_constant(AstConstant node) throws Exception {
		this.ins_jcm_add_head(node);
		this.gen(node);
		this.ins_jcm_add_tail(node);
	}
	private void ins_literal(AstLiteral node) throws Exception {
		this.gen(node);
	}
	private void ins_unary_expression(AstUnaryExpression node) throws Exception {
		this.ins_jcm_add_head(node);
		this.gen(node.get_operator());
		this.ins(node.get_operand());
		this.ins_jcm_add_tail(node);
	}
	private void ins_postfix_expression(AstPostfixExpression node) throws Exception {
		this.ins_jcm_add_head(node);
		this.ins(node.get_operand());
		this.gen(node.get_operator());
		this.ins_jcm_add_tail(node);
	}
	private void ins_binary_expression(AstBinaryExpression node) throws Exception {
		this.ins_jcm_add_head(node);
		
		this.ins(node.get_loperand());
		
		this.buffer.append(" ");
		this.gen(node.get_operator());
		this.buffer.append(" ");
		
		this.ins(node.get_roperand());
		
		this.ins_jcm_add_tail(node);
	}
	private void ins_array_expression(AstArrayExpression node) throws Exception {
		this.ins_jcm_add_head(node);
		this.ins(node.get_array_expression());
		this.buffer.append("[");
		this.ins(node.get_dimension_expression());
		this.buffer.append("]");
		this.ins_jcm_add_tail(node);
	}
	private void ins_cast_expression(AstCastExpression node) throws Exception {
		this.ins_jcm_add_head(node);
		
		this.buffer.append("(");
		this.gen(node.get_typename());
		this.buffer.append(") ");
		this.ins(node.get_expression());
		
		this.ins_jcm_add_tail(node);
	}
	private void ins_comma_expression(AstCommaExpression node) throws Exception {
		this.ins_jcm_add_head(node);
		
		this.buffer.append("(");
		for(int k = 0; k < node.number_of_arguments(); k++) {
			this.ins(node.get_expression(k));
			if(k < node.number_of_arguments() - 1) {
				this.buffer.append(", ");
			}
		}
		this.buffer.append(")");
		
		this.ins_jcm_add_tail(node);
	}
	private void ins_conditional_expression(AstConditionalExpression node) throws Exception {
		this.ins_jcm_add_head(node);
		
		this.buffer.append("(");
		this.ins(node.get_condition());
		this.buffer.append(" ? ");
		this.ins(node.get_true_branch());
		this.buffer.append(" : ");
		this.ins(node.get_false_branch());
		this.buffer.append(")");
		
		this.ins_jcm_add_tail(node);
	}
	private void ins_field_expression(AstFieldExpression node) throws Exception {
		this.ins_jcm_add_head(node);
		
		this.ins(node.get_body());
		this.gen(node.get_operator());
		this.gen(node.get_field());
		
		this.ins_jcm_add_tail(node);
	}
	private void ins_fun_call_expression(AstFunCallExpression node) throws Exception {
		this.ins_jcm_add_head(node);
		
		this.ins(node.get_function());
		this.buffer.append("(");
		if(node.has_argument_list()) {
			this.ins(node.get_argument_list());
		}
		this.buffer.append(")");
		
		this.ins_jcm_add_tail(node);
	}
	private void ins_argument_list(AstArgumentList node) throws Exception {
		for(int k = 0; k < node.number_of_arguments(); k++) {
			this.ins(node.get_argument(k));
			if(k < node.number_of_arguments() - 1) {
				this.buffer.append(", ");
			}
		}
	}
	private void ins_sizeof_expression(AstSizeofExpression node) throws Exception {
		this.ins_jcm_add_head(node);
		
		this.buffer.append("sizeof(");
		if(node.is_expression()) {
			this.gen(node.get_expression());
		}
		else {
			this.gen(node.get_typename());
		}
		this.buffer.append(")");
		
		this.ins_jcm_add_tail(node);
	}
	private void ins_paranth_expression(AstParanthExpression node) throws Exception {
		this.buffer.append("(");
		this.ins(node.get_sub_expression());
		this.buffer.append(")");
	}
	private void ins_const_expression(AstConstExpression node) throws Exception {
		this.ins(node.get_expression());
	}
	/* statement package */
	private void ins_break_statement(AstBreakStatement node) throws Exception {
		this.ins_jcm_prev_statement(node);
		this.gen(node);
	}
	private void ins_continue_statement(AstContinueStatement node) throws Exception {
		this.ins_jcm_prev_statement(node);
		this.gen(node);
	}
	private void ins_goto_statement(AstGotoStatement node) throws Exception {
		this.ins_jcm_prev_statement(node);
		this.gen(node);
	}
	private void ins_return_statement(AstReturnStatement node) throws Exception {
		this.ins_jcm_prev_statement(node);
		this.buffer.append("return");
		if(node.has_expression()) {
			this.buffer.append(" ");
			this.ins(node.get_expression());
		}
		this.buffer.append(";");
	}
	private void ins_labeled_statement(AstLabeledStatement node) throws Exception {
		this.gen(node);
		this.ins_jcm_post_statement(node);
	}
	private void ins_case_statement(AstCaseStatement node) throws Exception {
		this.gen(node);
		this.ins_jcm_post_statement(node);
	}
	private void ins_default_statement(AstDefaultStatement node) throws Exception {
		this.gen(node);
		this.ins_jcm_post_statement(node);
	}
	private void ins_expression_statement(AstExpressionStatement node) throws Exception {
		/** jcm_prev(stmt.id); expression; jcm_post(stmt.id); 
		 * 	or jcm_prev(stmt.id); jcm_post(stmt.id); **/
		this.ins_jcm_prev_statement(node);
		if(node.has_expression()) {
			this.ins(node.get_expression());
			this.buffer.append(";");
		}
		this.ins_jcm_post_statement(node);
	}
	private void ins_declaration_statement(AstDeclarationStatement node) throws Exception {
		this.ins_jcm_prev_statement(node);
		this.ins(node.get_declaration());
		this.buffer.append(";");
		this.ins_jcm_post_statement(node);
	}
	private void ins_compound_statement(AstCompoundStatement node) throws Exception {
		this.buffer.append("{");
		
		if(node.get_parent() == this.start_function) {
			this.tabs++;
			this.new_line();
			this.ins_jcm_open();
			this.tabs--;
		}
		
		this.tabs++;
		this.new_line();
		this.ins_jcm_prev_statement(node);
		this.tabs--;
		
		if(node.has_statement_list()) {
			this.tabs++;
			this.ins(node.get_statement_list());
			this.tabs--;
		}
		
		this.tabs++;
		this.new_line();
		this.ins_jcm_post_statement(node);
		this.tabs--;
		
		this.new_line();
		this.buffer.append("}");
	}
	private void ins_statement_list(AstStatementList node) throws Exception {
		for(int k = 0; k < node.number_of_statements(); k++) {
			AstStatement statement = node.get_statement(k);
			if(statement instanceof AstLabeledStatement
				|| statement instanceof AstCaseStatement
				|| statement instanceof AstDefaultStatement) {
				this.tabs--;
				this.new_line();
				this.ins(statement);
				this.tabs++;
			}
			else if(!this.is_empty_statement(statement)) {
				this.new_line();
				this.ins(statement);
			}
		}
	}
	private void ins_block(AstStatement statement) throws Exception {
		if(statement instanceof AstCompoundStatement) {
			this.new_line();
			this.ins(statement);
		}
		else {
			this.new_line();
			this.buffer.append("{");
			
			if(statement instanceof AstLabeledStatement
				|| statement instanceof AstCaseStatement
				|| statement instanceof AstDefaultStatement) {
				this.new_line();
				this.ins(statement);
			}
			else {
				this.tabs++;
				this.new_line();
				this.ins(statement);
				this.tabs--;
			}
			
			this.new_line();
			this.buffer.append("}");
		}
	}
	private void ins_if_statement(AstIfStatement node) throws Exception {
		this.buffer.append("if(");
		this.ins(node.get_condition());
		this.buffer.append(")");
		
		this.ins_block(node.get_true_branch());
		
		if(node.has_else()) {
			this.new_line();
			this.buffer.append("else");
			this.ins_block(node.get_false_branch());
		}
	}
	private void ins_switch_statement(AstSwitchStatement node) throws Exception {
		this.buffer.append("switch(");
		this.ins(node.get_condition());
		this.buffer.append(")");
		this.ins_block(node.get_body());
	}
	private void ins_while_statement(AstWhileStatement node) throws Exception {
		this.buffer.append("while(");
		this.ins(node.get_condition());
		this.buffer.append(")");
		this.ins_block(node.get_body());
	}
	private void ins_do_while_statement(AstDoWhileStatement node) throws Exception {
		this.buffer.append("do");
		this.ins_block(node.get_body());
		this.new_line();
		this.buffer.append("while(");
		this.ins(node.get_condition());
		this.buffer.append(");");
	}
	private void ins_for_statement(AstForStatement node) throws Exception {
		this.buffer.append("for( ");
		
		AstStatement initializer = node.get_initializer();
		if(initializer instanceof AstDeclarationStatement) {
			AstDeclarationStatement statement = (AstDeclarationStatement) initializer;
			this.ins(statement.get_declaration()); this.buffer.append("; ");
		}
		else {
			AstExpressionStatement statement = (AstExpressionStatement) initializer;
			if(statement.has_expression()) {
				this.ins_jcm_prev(statement);
				this.buffer.append(", ");
				this.ins(statement.get_expression());
				this.buffer.append("; ");
			}
			else {
				this.ins_jcm_prev(statement);
				this.buffer.append(", ");
				this.ins_jcm_post(statement);
				this.buffer.append("; ");
			}
		}
		
		AstExpressionStatement condition = node.get_condition();
		if(condition.has_expression()) {
			this.ins_jcm_prev(condition);
			this.buffer.append(", ");
			this.ins(condition.get_expression());
			this.buffer.append("; ");
		}
		else {
			this.ins_jcm_prev(condition);
			this.buffer.append(", ");
			this.ins_jcm_post(condition);
			this.buffer.append("; ");
		}
		
		if(node.has_increment()) {
			this.ins(node.get_increment());
		}
		
		this.buffer.append(")");
		
		this.ins_block(node.get_body());
		
	}
	/* external-unit package */
	private String get_declarator_name(AstDeclarator declarator) throws Exception {
		while(declarator != null) {
			switch(declarator.get_production()) {
			case identifier: return declarator.get_identifier().get_name();
			default: declarator = declarator.get_declarator(); break;
			}
		}
		return null;
	}
	private void gen_parameter_declaration(String name, AstDeclarationList dlist) throws Exception {
		for(int i = 0; i < dlist.number_of_declarations(); i++) {
			AstDeclaration declaration = dlist.get_declaration(i).get_declaration();
			AstDeclarationSpecifiers specifiers = declaration.get_specifiers();
			AstInitDeclaratorList list = declaration.get_declarator_list();
			for(int j = 0; j < list.number_of_init_declarators(); j++) {
				String dname = this.get_declarator_name(list.get_init_declarator(j).get_declarator());
				if(name.equals(dname)) {
					this.gen(specifiers);
					this.buffer.append(" ");
					this.gen(list.get_init_declarator(j));
					break;
				}
			}
		}
	}
	private void gen_function_declarator(AstDeclarator declarator, AstDeclarationList dlist) throws Exception {
		switch(declarator.get_production()) {
		case pointer_declarator:
		{
			this.gen(declarator.get_pointer());
			this.buffer.append(" ");
			this.gen_function_declarator(declarator.get_declarator(), dlist);
		}
		break;
		case declarator_dimension:
		{
			this.gen_function_declarator(declarator.get_declarator(), dlist);
			this.gen(declarator.get_dimension());
		}
		break;
		case declarator_parambody:
		{
			this.gen_function_declarator(declarator.get_declarator(), dlist);
			
			AstIdentifierList ilist = declarator.get_parameter_body().get_identifier_list();
			this.buffer.append("(");
			for(int k = 0; k < ilist.number_of_identifiers(); k++) {
				String name = ilist.get_identifier(k).get_name();
				this.gen_parameter_declaration(name, dlist);
				if(k < ilist.number_of_identifiers() - 1) {
					this.buffer.append(", ");
				}
			}
			this.buffer.append(")");
		}
		break;
		case lp_declarator_rp:
		{
			this.buffer.append("(");
			this.gen_function_declarator(declarator.get_declarator(), dlist);
			this.buffer.append(")");
		}
		break;
		case identifier:
		{
			this.gen(declarator.get_identifier());
		}
		break;
		default: throw new IllegalArgumentException("Invalid production: " + declarator.get_production());
		}
	}
	private void ins_function_definition(AstFunctionDefinition node) throws Exception {
		this.gen(node.get_specifiers());
		this.buffer.append(" ");
		if(node.has_declaration_list()) {
			this.gen_function_declarator(node.get_declarator(), node.get_declaration_list());
		}
		else {
			this.gen(node.get_declarator());
		}
		this.ins_block(node.get_body());
	}
	private void ins_translation_unit(AstTranslationUnit node) throws Exception {
		for(int k = 0; k < node.number_of_units(); k++) {
			AstExternalUnit unit = node.get_unit(k);
			if(unit instanceof AstDeclarationStatement) {
				this.gen(unit);
				this.new_line();
			}
			else if(unit instanceof AstFunctionDefinition) {}
			else {
				this.buffer.append(unit.get_code());
				this.new_line();
			}
		}
		
		this.buffer.append(jcm_include_code);
		
		for(int k = 0; k < node.number_of_units(); k++) {
			AstExternalUnit unit = node.get_unit(k);
			if(unit instanceof AstFunctionDefinition) {
				if(this.ifunctions.contains(unit)) {
					this.new_line();
					this.ins(unit);
					this.new_line();
				}
				else {
					this.new_line();
					this.gen(unit);
					this.new_line();
				}
			}
		}
	}
	
	/* instrument API */
	/**
	 * @param tree the program being instrumented.
	 * @param start_function the function where jcm_open is injected.
	 * @param instrument_functions the set of functions being instrumented.
	 * @param output_file the path where the instrumental code is written.
	 * @return generate the source code with instrumented 
	 * @throws Exception
	 */
	protected static String instrument(AstTree tree, AstFunctionDefinition start_function,
			Iterable<AstFunctionDefinition> instrument_functions, File output_file) throws Exception {
		instrumentor.input(start_function, instrument_functions, output_file);
		instrumentor.ins(tree.get_ast_root());
		return instrumentor.buffer.toString();
	}
	
}