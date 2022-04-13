package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.decl.AstDeclaration;
import com.jcsa.jcparse.lang.astree.decl.AstTypeName;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclaratorList;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstName;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator.DeclaratorProduction;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstFieldInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerList;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstBasicExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstOperator;
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
import com.jcsa.jcparse.lang.astree.unit.AstExternalUnit;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.astree.unit.AstTranslationUnit;

/**
 * 	It implements the parsing from AstTree & CirTree to AstContextTree.
 * 	
 * 	@author yukimula
 *
 */
public final class AstContextParser {
	
	/* definitions */
	/** the tree for being constructed and parsed from **/
	private	AstContextTree	tree;
	/** private constructor for the singleton mode **/
	private	AstContextParser() { this.tree = null; }
	static final AstContextParser parser = new AstContextParser();
	
	/* CONSTRUCTION */
	/**
	 * @param tree
	 * @return the root node of the context tree parsed from AstTree
	 * @throws Exception
	 */
	protected AstContextNode parse(AstContextTree tree) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree as null");
		}
		else {
			this.tree = tree;
			return this.parse_ast(tree.get_ast_tree().get_ast_root());
		}
	}
	/**
	 * It recursively parses the structural syntactic nodes of source to the context tree within.
	 * @param source
	 * @return the abstract syntactic node corresponding tree node in the tree
	 * @throws Exception
	 */
	private AstContextNode	parse_ast(AstNode source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstExpression) {
			return this.parse_ast_expression((AstExpression) source);
		}
		else if(source instanceof AstStatement) {
			return this.parse_ast_statement((AstStatement) source);
		}
		else {
			return this.parse_ast_element(source);
		}
	}
	/**
	 * @param source
	 * @return it creates a context node from current tree using the source
	 * @throws Exception
	 */
	private	AstContextNode	new_context_node(AstNode source) throws Exception {
		if(this.tree == null) {
			throw new IllegalArgumentException("Invalid tree: null");
		}
		else if(source == null) {
			throw new IllegalArgumentException("Invalid source; null");
		}
		else {
			return this.tree.new_node(source);
		}
	}
	/* statement */
	private	AstContextNode	parse_ast_expression_statement(AstExpressionStatement source) throws Exception {
		AstContextNode parent = this.new_context_node(source);
		if(source.has_expression()) {
			parent.add_child(AstContextNodeLink.evaluate, this.parse_ast(source.get_expression()));
		}
		return parent;
	}
	private	AstContextNode	parse_ast_declaration_statement(AstDeclarationStatement source) throws Exception {
		AstContextNode parent = this.new_context_node(source);
		parent.add_child(AstContextNodeLink.declare, this.parse_ast(source.get_declaration()));
		return parent;
	}
	private	AstContextNode	parse_ast_compound_statement(AstCompoundStatement source) throws Exception {
		AstContextNode parent = this.new_context_node(source);
		if(source.has_statement_list()) {
			AstStatementList list = source.get_statement_list();
			for(int k = 0; k < list.number_of_statements(); k++) {
				parent.add_child(AstContextNodeLink.execute, 
						this.parse_ast(list.get_statement(k)));
			}
		}
		return parent;
	}
	private	AstContextNode	parse_ast_break_statement(AstBreakStatement source) throws Exception {
		return this.new_context_node(source);
	}
	private	AstContextNode	parse_ast_continue_statement(AstContinueStatement source) throws Exception {
		return this.new_context_node(source);
	}
	private	AstContextNode	parse_ast_goto_statement(AstGotoStatement source) throws Exception {
		return this.new_context_node(source);
	}
	private	AstContextNode	parse_ast_return_statement(AstReturnStatement source) throws Exception {
		AstContextNode parent = this.new_context_node(source);
		if(source.has_expression()) {
			parent.add_child(AstContextNodeLink.lvalue, this.parse_ast(source.get_return()));
			parent.add_child(AstContextNodeLink.rvalue, this.parse_ast(source.get_expression()));
		}
		return parent;
	}
	private AstContextNode	parse_ast_labeled_statement(AstLabeledStatement source) throws Exception {
		return this.new_context_node(source);
	}
	private	AstContextNode	parse_ast_default_statement(AstDefaultStatement source) throws Exception {
		return this.new_context_node(source);
	}
	private	AstContextNode	parse_ast_if_statement(AstIfStatement source) throws Exception {
		AstContextNode parent = this.new_context_node(source);
		parent.add_child(AstContextNodeLink.condition, this.parse_ast(source.get_condition()));
		parent.add_child(AstContextNodeLink.true_body, this.parse_ast(source.get_true_branch()));
		if(source.has_else()) {
			parent.add_child(AstContextNodeLink.false_body, this.parse_ast(source.get_false_branch()));
		}
		return parent;
	}
	private	AstContextNode	parse_ast_switch_statement(AstSwitchStatement source) throws Exception {
		AstContextNode parent = this.new_context_node(source);
		parent.add_child(AstContextNodeLink.n_condition, this.parse_ast(source.get_condition()));
		parent.add_child(AstContextNodeLink.body, this.parse_ast(source.get_body()));
		return parent;
	}
	private	AstContextNode	parse_ast_case_statement(AstCaseStatement source) throws Exception {
		AstContextNode parent = this.new_context_node(source);
		parent.add_child(AstContextNodeLink.n_condition, this.parse_ast(source.get_expression()));
		return parent;
	}
	private	AstContextNode	parse_ast_while_statement(AstWhileStatement source) throws Exception {
		AstContextNode parent = this.new_context_node(source);
		parent.add_child(AstContextNodeLink.condition, this.parse_ast(source.get_condition()));
		parent.add_child(AstContextNodeLink.true_body, this.parse_ast(source.get_body()));
		return parent;
	}
	private	AstContextNode	parse_ast_do_while_statement(AstDoWhileStatement source) throws Exception {
		AstContextNode parent = this.new_context_node(source);
		parent.add_child(AstContextNodeLink.true_body, this.parse_ast(source.get_body()));
		parent.add_child(AstContextNodeLink.condition, this.parse_ast(source.get_condition()));
		return parent;
	}
	private	AstContextNode	parse_ast_for_statement(AstForStatement source) throws Exception {
		AstContextNode parent = this.new_context_node(source);
		parent.add_child(AstContextNodeLink.initial, this.parse_ast(source.get_initializer()));
		parent.add_child(AstContextNodeLink.condition, this.parse_ast(source.get_condition()));
		parent.add_child(AstContextNodeLink.true_body, this.parse_ast(source.get_body()));
		if(source.has_increment()) {
			parent.add_child(AstContextNodeLink.iterate, this.parse_ast(source.get_increment()));
		}
		return parent;
	}
	private AstContextNode	parse_ast_statement(AstStatement source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstExpressionStatement) {
			return this.parse_ast_expression_statement((AstExpressionStatement) source);
		}
		else if(source instanceof AstDeclarationStatement) {
			return this.parse_ast_declaration_statement((AstDeclarationStatement) source);
		}
		else if(source instanceof AstCompoundStatement) {
			return this.parse_ast_compound_statement((AstCompoundStatement) source);
		}
		else if(source instanceof AstBreakStatement) {
			return this.parse_ast_break_statement((AstBreakStatement) source);
		}
		else if(source instanceof AstContinueStatement) {
			return this.parse_ast_continue_statement((AstContinueStatement) source);
		}
		else if(source instanceof AstGotoStatement) {
			return this.parse_ast_goto_statement((AstGotoStatement) source);
		}
		else if(source instanceof AstReturnStatement) {
			return this.parse_ast_return_statement((AstReturnStatement) source);
		}
		else if(source instanceof AstLabeledStatement) {
			return this.parse_ast_labeled_statement((AstLabeledStatement) source);
		}
		else if(source instanceof AstDefaultStatement) {
			return this.parse_ast_default_statement((AstDefaultStatement) source);
		}
		else if(source instanceof AstIfStatement) {
			return this.parse_ast_if_statement((AstIfStatement) source);
		}
		else if(source instanceof AstSwitchStatement) {
			return this.parse_ast_switch_statement((AstSwitchStatement) source);
		}
		else if(source instanceof AstCaseStatement) {
			return this.parse_ast_case_statement((AstCaseStatement) source);
		}
		else if(source instanceof AstForStatement) {
			return this.parse_ast_for_statement((AstForStatement) source);
		}
		else if(source instanceof AstWhileStatement) {
			return this.parse_ast_while_statement((AstWhileStatement) source);
		}
		else if(source instanceof AstDoWhileStatement) {
			return this.parse_ast_do_while_statement((AstDoWhileStatement) source);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + source);
		}
	}
	/* expression */
	private AstContextNode	parse_ast_basic_expression(AstBasicExpression source) throws Exception {
		return this.new_context_node(source);
	}
	private AstContextNode	parse_ast_unary_expression(AstUnaryExpression source) throws Exception {
		AstContextNode parent = this.new_context_node(source);
		parent.add_child(AstContextNodeLink.operator, this.parse_ast(source.get_operator()));
		switch(source.get_operator().get_operator()) {
		case positive:		parent.add_child(AstContextNodeLink.uoperand, this.parse_ast(source.get_operand())); break;
		case negative:		parent.add_child(AstContextNodeLink.uoperand, this.parse_ast(source.get_operand())); break;
		case bit_not:		parent.add_child(AstContextNodeLink.uoperand, this.parse_ast(source.get_operand())); break;
		case logic_not:		parent.add_child(AstContextNodeLink.condition, this.parse_ast(source.get_operand())); break;
		case address_of:	parent.add_child(AstContextNodeLink.reference, this.parse_ast(source.get_operand())); break;
		case dereference:	parent.add_child(AstContextNodeLink.address, this.parse_ast(source.get_operand())); break;
		case increment:		parent.add_child(AstContextNodeLink.ivalue, this.parse_ast(source.get_operand())); break;
		case decrement:		parent.add_child(AstContextNodeLink.ivalue, this.parse_ast(source.get_operand())); break;
		default:			throw new IllegalArgumentException("Invalid: " + source);
		}
		return parent;
	}
	private	AstContextNode	parse_ast_postfix_expression(AstPostfixExpression source) throws Exception {
		AstContextNode parent = this.new_context_node(source);
		switch(source.get_operator().get_operator()) {
		case increment:	parent.add_child(AstContextNodeLink.ivalue, this.parse_ast(source.get_operand())); break;
		case decrement:	parent.add_child(AstContextNodeLink.ivalue, this.parse_ast(source.get_operand())); break;
		default:		throw new IllegalArgumentException("Invalid: " + source);
		}
		parent.add_child(AstContextNodeLink.operator, this.parse_ast(source.get_operator()));
		return parent;
	}
	private	AstContextNode	parse_ast_array_expression(AstArrayExpression source) throws Exception {
		AstContextNode parent = this.new_context_node(source);
		parent.add_child(AstContextNodeLink.array, this.parse_ast(source.get_array_expression()));
		parent.add_child(AstContextNodeLink.index, this.parse_ast(source.get_dimension_expression()));
		return parent;
	}
	private	AstContextNode	parse_ast_cast_expression(AstCastExpression source) throws Exception {
		AstContextNode parent = this.new_context_node(source);
		parent.add_child(AstContextNodeLink.cast_type, this.parse_ast(source.get_typename()));
		parent.add_child(AstContextNodeLink.uoperand, this.parse_ast(source.get_expression()));
		return parent;
	}
	private	AstContextNode	parse_ast_conditional_expression(AstConditionalExpression source) throws Exception {
		AstContextNode parent = this.new_context_node(source);
		parent.add_child(AstContextNodeLink.condition, this.parse_ast(source.get_condition()));
		parent.add_child(AstContextNodeLink.toperand, this.parse_ast(source.get_true_branch()));
		parent.add_child(AstContextNodeLink.foperand, this.parse_ast(source.get_false_branch()));
		return parent;
	}
	private	AstContextNode	parse_ast_field_expression(AstFieldExpression source) throws Exception {
		AstContextNode parent = this.new_context_node(source);
		parent.add_child(AstContextNodeLink.fbody, this.parse_ast(source.get_body()));
		parent.add_child(AstContextNodeLink.field, this.parse_ast(source.get_field()));
		return parent;
	}
	private	AstContextNode	parse_ast_fun_call_expression(AstFunCallExpression source) throws Exception {
		AstContextNode parent = this.new_context_node(source);
		parent.add_child(AstContextNodeLink.callee, this.parse_ast(source.get_function()));
		if(source.has_argument_list()) {
			AstArgumentList list = source.get_argument_list();
			for(int k = 0; k < list.number_of_arguments(); k++) {
				parent.add_child(AstContextNodeLink.argument, this.parse_ast(list.get_argument(k)));
			}
		}
		return parent;
	}
	private	AstContextNode	parse_ast_comma_expression(AstCommaExpression source) throws Exception {
		AstContextNode parent = this.new_context_node(source);
		for(int k = 0; k < source.number_of_arguments(); k++) {
			parent.add_child(AstContextNodeLink.element, this.parse_ast(source.get_expression(k)));
		}
		return parent;
	}
	private	AstContextNode	parse_ast_initializer_body(AstInitializerBody source) throws Exception {
		AstContextNode parent = this.new_context_node(source);
		AstInitializerList list = source.get_initializer_list();
		for(int k = 0; k < list.number_of_initializer(); k++) {
			parent.add_child(AstContextNodeLink.element, this.parse_ast(list.get_initializer(k)));
		}
		return parent;
	}
	private	AstContextNode	parse_ast_sizeof_expression(AstSizeofExpression source) throws Exception {
		return this.new_context_node(source);
	}
	private	AstContextNode	parse_ast_const_expression(AstConstExpression source) throws Exception {
		return this.parse_ast(source.get_expression());
	}
	private	AstContextNode	parse_ast_paranth_expression(AstParanthExpression source) throws Exception {
		return this.parse_ast(source.get_sub_expression());
	}
	private	AstContextNode	parse_ast_binary_expression(AstBinaryExpression source) throws Exception {
		AstContextNode parent = this.new_context_node(source);
		switch(source.get_operator().get_operator()) {
		case arith_add:
		case arith_sub:
		case arith_mul:
		case arith_div:
		case arith_mod:
		case bit_and:
		case bit_or:
		case bit_xor:
		case left_shift:
		case righ_shift:
		case greater_tn:
		case greater_eq:
		case smaller_tn:
		case smaller_eq:
		case equal_with:
		case not_equals:
		{
			parent.add_child(AstContextNodeLink.loperand, this.parse_ast(source.get_loperand()));
			parent.add_child(AstContextNodeLink.operator, this.parse_ast(source.get_operator()));
			parent.add_child(AstContextNodeLink.roperand, this.parse_ast(source.get_roperand()));
			break;
		}
		case logic_and:
		case logic_or:
		{
			parent.add_child(AstContextNodeLink.condition, this.parse_ast(source.get_loperand()));
			parent.add_child(AstContextNodeLink.operator, this.parse_ast(source.get_operator()));
			parent.add_child(AstContextNodeLink.condition, this.parse_ast(source.get_roperand()));
			break;
		}
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
			parent.add_child(AstContextNodeLink.lvalue, this.parse_ast(source.get_loperand()));
			parent.add_child(AstContextNodeLink.operator, this.parse_ast(source.get_operator()));
			parent.add_child(AstContextNodeLink.rvalue, this.parse_ast(source.get_roperand()));
			break;
		}
		default:	throw new IllegalArgumentException("Unsupported: " + source.generate_code());
		}
		return parent;
	}
	private AstContextNode	parse_ast_expression(AstExpression source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstBasicExpression) {
			return this.parse_ast_basic_expression((AstBasicExpression) source);
		}
		else if(source instanceof AstBinaryExpression) {
			return this.parse_ast_binary_expression((AstBinaryExpression) source);
		}
		else if(source instanceof AstUnaryExpression) {
			return this.parse_ast_unary_expression((AstUnaryExpression) source);
		}
		else if(source instanceof AstPostfixExpression) {
			return this.parse_ast_postfix_expression((AstPostfixExpression) source);
		}
		else if(source instanceof AstArrayExpression) {
			return this.parse_ast_array_expression((AstArrayExpression) source);
		}
		else if(source instanceof AstCastExpression) {
			return this.parse_ast_cast_expression((AstCastExpression) source);
		}
		else if(source instanceof AstCommaExpression) {
			return this.parse_ast_comma_expression((AstCommaExpression) source);
		}
		else if(source instanceof AstConditionalExpression) {
			return this.parse_ast_conditional_expression((AstConditionalExpression) source);
		}
		else if(source instanceof AstConstExpression) {
			return this.parse_ast_const_expression((AstConstExpression) source);
		}
		else if(source instanceof AstParanthExpression) {
			return this.parse_ast_paranth_expression((AstParanthExpression) source);
		}
		else if(source instanceof AstFieldExpression) {
			return this.parse_ast_field_expression((AstFieldExpression) source);
		}
		else if(source instanceof AstFunCallExpression) {
			return this.parse_ast_fun_call_expression((AstFunCallExpression) source);
		}
		else if(source instanceof AstInitializerBody) {
			return this.parse_ast_initializer_body((AstInitializerBody) source);
		}
		else if(source instanceof AstSizeofExpression) {
			return this.parse_ast_sizeof_expression((AstSizeofExpression) source);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + source);
		}
	}
	/* elemental */
	private	AstContextNode	parse_ast_declaration(AstDeclaration source) throws Exception {
		AstContextNode parent = this.new_context_node(source);
		if(source.has_declarator_list()) {
			AstInitDeclaratorList list = source.get_declarator_list();
			for(int k = 0; k < list.number_of_init_declarators(); k++) {
				parent.add_child(AstContextNodeLink.evaluate, this.parse_ast(list.get_init_declarator(k)));
			}
		}
		return parent;
	}
	private	AstContextNode	parse_ast_init_declarator(AstInitDeclarator source) throws Exception {
		AstContextNode parent = this.new_context_node(source);
		parent.add_child(AstContextNodeLink.lvalue, this.parse_ast(source.get_declarator()));
		if(source.has_initializer()) {
			parent.add_child(AstContextNodeLink.rvalue, this.parse_ast(source.get_initializer()));
		}
		return parent;
	}
	private	AstContextNode	parse_ast_declarator(AstDeclarator source) throws Exception {
		if(source.get_production() == DeclaratorProduction.identifier) {
			return this.parse_ast(source.get_identifier());
		}
		else {
			return this.parse_ast(source.get_declarator());
		}
	}
	private	AstContextNode	parse_ast_name(AstName source) throws Exception {
		return this.new_context_node(source);
	}
	private	AstContextNode	parse_ast_initializer(AstInitializer source) throws Exception {
		if(source.is_body()) {
			return this.parse_ast(source.get_body());
		}
		else {
			return this.parse_ast(source.get_expression());
		}
	}
	private	AstContextNode	parse_ast_field_initializer(AstFieldInitializer source) throws Exception {
		return this.parse_ast(source.get_initializer());
	}
	private	AstContextNode	parse_ast_typename(AstTypeName source) throws Exception {
		return this.new_context_node(source);
	}
	private	AstContextNode	parse_ast_keyword(AstKeyword source) throws Exception {
		return this.new_context_node(source);
	}
	private	AstContextNode	parse_ast_operator(AstOperator source) throws Exception {
		return this.new_context_node(source);
	}
	private	AstContextNode	parse_ast_field(AstField source) throws Exception {
		return this.new_context_node(source);
	}
	private	AstContextNode	parse_ast_function_definition(AstFunctionDefinition source) throws Exception {
		AstContextNode parent = this.new_context_node(source);
		parent.add_child(AstContextNodeLink.declare, this.parse_ast(source.get_declarator()));
		parent.add_child(AstContextNodeLink.body, this.parse_ast(source.get_body()));
		return parent;
	}
	private	AstContextNode	parse_ast_translation_unit(AstTranslationUnit source) throws Exception {
		AstContextNode parent = this.new_context_node(source);
		for(int k = 0; k < source.number_of_units(); k++) {
			AstExternalUnit unit = source.get_unit(k);
			if(unit instanceof AstFunctionDefinition) {
				parent.add_child(AstContextNodeLink.function, this.parse_ast(unit));
			}
			else {
				parent.add_child(AstContextNodeLink.execute, this.parse_ast(unit));
			}
		}
		return parent;
	}
	private AstContextNode	parse_ast_element(AstNode source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstDeclaration) {
			return this.parse_ast_declaration((AstDeclaration) source);
		}
		else if(source instanceof AstInitDeclarator) {
			return this.parse_ast_init_declarator((AstInitDeclarator) source);
		}
		else if(source instanceof AstDeclarator) {
			return this.parse_ast_declarator((AstDeclarator) source);
		}
		else if(source instanceof AstName) {
			return this.parse_ast_name((AstName) source);
		}
		else if(source instanceof AstInitializer) {
			return this.parse_ast_initializer((AstInitializer) source);
		}
		else if(source instanceof AstFieldInitializer) {
			return this.parse_ast_field_initializer((AstFieldInitializer) source);
		}
		else if(source instanceof AstField) {
			return this.parse_ast_field((AstField) source);
		}
		else if(source instanceof AstTypeName) {
			return this.parse_ast_typename((AstTypeName) source);
		}
		else if(source instanceof AstOperator) {
			return this.parse_ast_operator((AstOperator) source);
		}
		else if(source instanceof AstKeyword) {
			return this.parse_ast_keyword((AstKeyword) source);
		}
		else if(source instanceof AstFunctionDefinition) {
			return this.parse_ast_function_definition((AstFunctionDefinition) source);
		}
		else if(source instanceof AstTranslationUnit) {
			return this.parse_ast_translation_unit((AstTranslationUnit) source);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + source);
		}
	}
	
}
