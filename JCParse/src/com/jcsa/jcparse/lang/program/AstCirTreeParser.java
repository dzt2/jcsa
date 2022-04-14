package com.jcsa.jcparse.lang.program;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.base.AstKeyword;
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
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.astree.unit.AstTranslationUnit;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.program.types.AstCirParChild;
import com.jcsa.jcparse.lang.scope.CEnumeratorName;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CTypeName;

/**
 * It implements the construction of AstCirTree from the AstTree.
 * 
 * @author yukimula
 *
 */
final class AstCirTreeParser {
	
	/* definition */
	/** the tree to be constructed as input **/	private	AstCirTree	tree;
	/** singleton mode **/ private AstCirTreeParser() { this.tree = null; }
	private static final AstCirTreeParser parser = new AstCirTreeParser();
	
	/* construct methods */
	/**
	 * @param tree an empty tree
	 * @return it completes the tree structure based on AstTree (Node + Link)
	 * @throws Exception
	 */
	protected static AstCirNode parse(AstCirTree tree) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree: null");
		}
		else {
			parser.tree = tree;
			return parser.parse_ast(tree.get_ast_tree().get_ast_root());
		}
	}
	/**
	 * @param source
	 * @return it creates a new tree node w.r.t. the source in the tree
	 * @throws Exception
	 */
	private	AstCirNode	new_tree_node(AstNode source, Object token) throws Exception {
		if(this.tree == null) {
			throw new IllegalArgumentException("Invalid tree: null");
		}
		else { return this.tree.new_tree_node(source, token); }
	}
	/**
	 * It recursively constructs the nodes from given source node
	 * @param source
	 * @return the AstCirNode w.r.t. the source as representation
	 * @throws Exception
	 */
	private	AstCirNode	parse_ast(AstNode source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstExpression) {
			return this.parse_ast_expression((AstExpression) source);
		}
		else if(source instanceof AstStatement) {
			return this.parse_ast_statement((AstStatement) source);
		}
		else { return this.parse_ast_elemental(source); }
	}
	/* EXPRESSION */
	private	AstCirNode	parse_ast_id_expression(AstIdExpression source) throws Exception {
		CName cname = source.get_cname();
		if(cname instanceof CEnumeratorName) {
			int value = ((CEnumeratorName) cname).get_enumerator().get_value();
			CConstant constant = new CConstant(); constant.set_int(value);
			return this.new_tree_node(source, constant);
		}
		else if(cname instanceof CTypeName) {
			return this.new_tree_node(source, ((CTypeName) cname).get_type());
		}
		else {
			return this.new_tree_node(source, source.get_name());
		}
	}
	private	AstCirNode	parse_ast_constant(AstConstant source) throws Exception {
		return this.new_tree_node(source, source.get_constant());
	}
	private	AstCirNode	parse_ast_literal(AstLiteral source) throws Exception {
		return this.new_tree_node(source, source.get_literal());
	}
	private	AstCirNode	parse_ast_basic_expression(AstBasicExpression source) throws Exception {
		if(source instanceof AstIdExpression) {
			return this.parse_ast_id_expression((AstIdExpression) source);
		}
		else if(source instanceof AstConstant) {
			return this.parse_ast_constant((AstConstant) source);
		}
		else {
			return this.parse_ast_literal((AstLiteral) source);
		}
	}
	private	AstCirNode	parse_ast_unary_expression(AstUnaryExpression source) throws Exception {
		AstCirNode parent = this.new_tree_node(source, source.get_operator().get_operator());
		switch(source.get_operator().get_operator()) {
		case positive:	
		case negative:	
		case bit_not:
		case address_of:
		{
			parent.add_child(AstCirParChild.uoperand, this.parse_ast(source.get_operand()));
			break;
		}
		case logic_not:
		{
			parent.add_child(AstCirParChild.condition, this.parse_ast(source.get_operand()));
			break;
		}
		case dereference:
		{
			parent.add_child(AstCirParChild.address, this.parse_ast(source.get_operand()));
			break;
		}
		case increment:
		case decrement:
		{
			parent.add_child(AstCirParChild.ivalue, this.parse_ast(source.get_operand()));
			break;
		}
		default:		throw new IllegalArgumentException("Invalid: " + source);
		}
		return parent;
	}
	private	AstCirNode	parse_ast_postfix_expression(AstPostfixExpression source) throws Exception {
		AstCirNode parent = this.new_tree_node(source, source.get_operator().get_operator());
		switch(source.get_operator().get_operator()) {
		case increment:
		case decrement:
		{
			parent.add_child(AstCirParChild.ivalue, this.parse_ast(source.get_operand()));
			break;
		}
		default:		throw new IllegalArgumentException("Invalid: " + source);
		}
		return parent;
	}
	private	AstCirNode	parse_ast_binary_expression(AstBinaryExpression source) throws Exception {
		AstCirNode parent = this.new_tree_node(source, source.get_operator().get_operator());
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
		case not_equals:
		case equal_with:
		{
			parent.add_child(AstCirParChild.loperand, this.parse_ast(source.get_loperand()));
			parent.add_child(AstCirParChild.roperand, this.parse_ast(source.get_roperand()));
			break;
		}
		case logic_and:
		case logic_or:
		{
			parent.add_child(AstCirParChild.condition, this.parse_ast(source.get_loperand()));
			parent.add_child(AstCirParChild.condition, this.parse_ast(source.get_roperand()));
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
			parent.add_child(AstCirParChild.lvalue, this.parse_ast(source.get_loperand()));
			parent.add_child(AstCirParChild.rvalue, this.parse_ast(source.get_roperand()));
			break;
		}
		default:	throw new IllegalArgumentException(source.generate_code());
		}
		return parent;
	}
	private	AstCirNode	parse_ast_array_expression(AstArrayExpression source) throws Exception {
		AstCirNode parent = this.new_tree_node(source, null);
		parent.add_child(AstCirParChild.address, this.parse_ast(source.get_array_expression()));
		parent.add_child(AstCirParChild.index, this.parse_ast(source.get_dimension_expression()));
		return parent;
	}
	private	AstCirNode	parse_ast_cast_expression(AstCastExpression source) throws Exception {
		AstCirNode parent = this.new_tree_node(source, source.get_typename().get_type());
		parent.add_child(AstCirParChild.uoperand, this.parse_ast(source.get_expression()));
		return parent;
	}
	private	AstCirNode	parse_ast_comma_expression(AstCommaExpression source) throws Exception {
		AstCirNode parent = this.new_tree_node(source, null);
		for(int k = 0; k < source.number_of_arguments(); k++) {
			AstCirNode element = this.parse_ast(source.get_expression(k));
			if(k == source.number_of_arguments() - 1) {
				parent.add_child(AstCirParChild.uoperand, element);
			}
			else {
				parent.add_child(AstCirParChild.evaluate, element);
			}
		}
		return parent;
	}
	private	AstCirNode	parse_ast_conditional_expression(AstConditionalExpression source) throws Exception {
		AstCirNode parent = this.new_tree_node(source, null);
		parent.add_child(AstCirParChild.condition, this.parse_ast(source.get_condition()));
		parent.add_child(AstCirParChild.loperand, this.parse_ast(source.get_true_branch()));
		parent.add_child(AstCirParChild.roperand, this.parse_ast(source.get_false_branch()));
		return parent;
	}
	private	AstCirNode	parse_ast_initializer_body(AstInitializerBody source) throws Exception {
		AstCirNode parent = this.new_tree_node(source, null);
		AstInitializerList list = source.get_initializer_list();
		for(int k = 0; k < list.number_of_initializer(); k++) {
			AstCirNode child = this.parse_ast(list.get_initializer(k));
			parent.add_child(AstCirParChild.element, child);
		}
		return parent;
	}
	private	AstCirNode	parse_ast_paranth_expression(AstParanthExpression source) throws Exception {
		return this.parse_ast(source.get_sub_expression());
	}
	private	AstCirNode	parse_ast_const_expression(AstConstExpression source) throws Exception {
		return this.parse_ast(source.get_expression());
	}
	private	AstCirNode	parse_ast_fun_call_expression(AstFunCallExpression source) throws Exception {
		AstCirNode parent = this.new_tree_node(source, null);
		parent.add_child(AstCirParChild.callee, this.parse_ast(source.get_function()));
		if(source.has_argument_list()) {
			AstArgumentList list = source.get_argument_list();
			for(int k = 0; k < list.number_of_arguments(); k++) {
				AstCirNode child = this.parse_ast(list.get_argument(k));
				parent.add_child(AstCirParChild.argument, child);
			}
		}
		return parent;
	}
	private	AstCirNode	parse_ast_sizeof_expression(AstSizeofExpression source) throws Exception {
		CType type;
		if(source.is_expression()) {
			type = source.get_expression().get_value_type();
		}
		else {
			type = source.get_typename().get_type();
		}
		int size = this.tree.get_template().sizeof(type);
		CConstant constant = new CConstant();
		constant.set_int(size);
		return this.new_tree_node(source, constant);
	}
	private	AstCirNode	parse_ast_field_expression(AstFieldExpression source) throws Exception {
		AstCirNode parent = this.new_tree_node(source, source.get_operator().get_punctuator());
		parent.add_child(AstCirParChild.fbody, this.parse_ast(source.get_body()));
		parent.add_child(AstCirParChild.field, this.parse_ast(source.get_field()));
		return parent;
	}
	private	AstCirNode	parse_ast_expression(AstExpression source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstBasicExpression) {
			return this.parse_ast_basic_expression((AstBasicExpression) source);
		}
		else if(source instanceof AstUnaryExpression) {
			return this.parse_ast_unary_expression((AstUnaryExpression) source);
		}
		else if(source instanceof AstPostfixExpression) {
			return this.parse_ast_postfix_expression((AstPostfixExpression) source);
		}
		else if(source instanceof AstBinaryExpression) {
			return this.parse_ast_binary_expression((AstBinaryExpression) source);
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
		else if(source instanceof AstInitializerBody) {
			return this.parse_ast_initializer_body((AstInitializerBody) source);
		}
		else if(source instanceof AstParanthExpression) {
			return this.parse_ast_paranth_expression((AstParanthExpression) source);
		}
		else if(source instanceof AstConstExpression) {
			return this.parse_ast_const_expression((AstConstExpression) source);
		}
		else if(source instanceof AstFieldExpression) {
			return this.parse_ast_field_expression((AstFieldExpression) source);
		}
		else if(source instanceof AstSizeofExpression) {
			return this.parse_ast_sizeof_expression((AstSizeofExpression) source);
		}
		else if(source instanceof AstFunCallExpression) {
			return this.parse_ast_fun_call_expression((AstFunCallExpression) source);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + source);
		}
	}
	/* STATEMENTS */
	private	AstCirNode	parse_ast_declaration_statement(AstDeclarationStatement source) throws Exception {
		AstCirNode parent = this.new_tree_node(source, null);
		if(source.get_declaration().has_declarator_list()) {
			AstInitDeclaratorList list = source.get_declaration().get_declarator_list();
			for(int k = 0; k < list.number_of_init_declarators(); k++) {
				AstCirNode init_assign = this.parse_ast(list.get_init_declarator(k));
				parent.add_child(AstCirParChild.evaluate, init_assign);
			}
		}
		return parent;
	}
	private	AstCirNode	parse_ast_expression_statement(AstExpressionStatement source) throws Exception {
		AstCirNode parent = this.new_tree_node(source, null);
		if(source.has_expression()) {
			parent.add_child(AstCirParChild.evaluate, this.parse_ast(source.get_expression()));
		}
		return parent;
	}
	private	AstCirNode	parse_ast_compound_statement(AstCompoundStatement source) throws Exception {
		AstCirNode parent = this.new_tree_node(source, null);
		if(source.has_statement_list()) {
			AstStatementList list = source.get_statement_list();
			for(int k = 0; k < list.number_of_statements(); k++) {
				AstCirNode child = this.parse_ast(list.get_statement(k));
				parent.add_child(AstCirParChild.execute, child);
			}
		}
		return parent;
	}
	private	AstCirNode	parse_ast_break_statement(AstBreakStatement source) throws Exception {
		return this.new_tree_node(source, source.get_break().get_keyword());
	}
	private	AstCirNode	parse_ast_continue_statement(AstContinueStatement source) throws Exception {
		return this.new_tree_node(source, source.get_continue().get_keyword());
	}
	private	AstCirNode	parse_ast_goto_statement(AstGotoStatement source) throws Exception {
		return this.new_tree_node(source, source.get_goto().get_keyword());
	}
	private	AstCirNode	parse_ast_return_statement(AstReturnStatement source) throws Exception {
		AstCirNode parent = this.new_tree_node(source, source.get_return().get_keyword());
		parent.add_child(AstCirParChild.lvalue, this.parse_ast(source.get_return()));
		if(source.has_expression()) {
			parent.add_child(AstCirParChild.rvalue, this.parse_ast(source.get_expression()));
		}
		return parent;
	}
	private	AstCirNode	parse_ast_if_statement(AstIfStatement source) throws Exception {
		AstCirNode parent = this.new_tree_node(source, source.get_if().get_keyword());
		parent.add_child(AstCirParChild.condition, this.parse_ast(source.get_condition()));
		parent.add_child(AstCirParChild.tbranch, this.parse_ast(source.get_true_branch()));
		if(source.has_else()) {
			parent.add_child(AstCirParChild.fbranch, this.parse_ast(source.get_false_branch()));
		}
		return parent;
	}
	private	AstCirNode	parse_ast_switch_statement(AstSwitchStatement source) throws Exception {
		AstCirNode parent = this.new_tree_node(source, source.get_switch().get_keyword());
		parent.add_child(AstCirParChild.n_condition, this.parse_ast(source.get_condition()));
		parent.add_child(AstCirParChild.execute, this.parse_ast(source.get_body()));
		return parent;
	}
	private	AstCirNode	parse_ast_case_statement(AstCaseStatement source) throws Exception {
		AstCirNode parent = this.new_tree_node(source, source.get_case().get_keyword());
		parent.add_child(AstCirParChild.n_condition, this.parse_ast(source.get_expression()));
		return parent;
	}
	private	AstCirNode	parse_ast_labeled_statement(AstLabeledStatement source) throws Exception {
		return this.new_tree_node(source, source.get_label().get_name());
	}
	private	AstCirNode	parse_ast_default_statement(AstDefaultStatement source) throws Exception {
		return this.new_tree_node(source, source.get_default().get_keyword());
	}
	private	AstCirNode	parse_ast_while_statement(AstWhileStatement source) throws Exception {
		AstCirNode parent = this.new_tree_node(source, source.get_while().get_keyword());
		parent.add_child(AstCirParChild.condition, this.parse_ast(source.get_condition()));
		parent.add_child(AstCirParChild.tbranch, this.parse_ast(source.get_body()));
		return parent;
	}
	private	AstCirNode	parse_ast_do_while_statement(AstDoWhileStatement source) throws Exception {
		AstCirNode parent = this.new_tree_node(source, source.get_do().get_keyword());
		parent.add_child(AstCirParChild.tbranch, this.parse_ast(source.get_body()));
		parent.add_child(AstCirParChild.condition, this.parse_ast(source.get_condition()));
		return parent;
	}
	private	AstCirNode	parse_ast_for_statement(AstForStatement source) throws Exception {
		AstCirNode parent = this.new_tree_node(source, source.get_for().get_keyword());
		parent.add_child(AstCirParChild.execute, this.parse_ast(source.get_initializer()));
		parent.add_child(AstCirParChild.condition, this.parse_ast(source.get_condition()));
		parent.add_child(AstCirParChild.tbranch, this.parse_ast(source.get_body()));
		return parent;
	}
	private	AstCirNode	parse_ast_statement(AstStatement source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstDeclarationStatement) {
			return this.parse_ast_declaration_statement((AstDeclarationStatement) source);
		}
		else if(source instanceof AstExpressionStatement) {
			return this.parse_ast_expression_statement((AstExpressionStatement) source);
		}
		else if(source instanceof AstCompoundStatement)  {
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
		else if(source instanceof AstIfStatement) {
			return this.parse_ast_if_statement((AstIfStatement) source);
		}
		else if(source instanceof AstSwitchStatement) {
			return this.parse_ast_switch_statement((AstSwitchStatement) source);
		}
		else if(source instanceof AstWhileStatement) {
			return this.parse_ast_while_statement((AstWhileStatement) source);
		}
		else if(source instanceof AstDoWhileStatement) {
			return this.parse_ast_do_while_statement((AstDoWhileStatement) source);
		}
		else if(source instanceof AstForStatement) {
			return this.parse_ast_for_statement((AstForStatement) source);
		}
		else if(source instanceof AstLabeledStatement) {
			return this.parse_ast_labeled_statement((AstLabeledStatement) source);
		}
		else if(source instanceof AstDefaultStatement) {
			return this.parse_ast_default_statement((AstDefaultStatement) source);
		}
		else if(source instanceof AstCaseStatement) {
			return this.parse_ast_case_statement((AstCaseStatement) source);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + source);
		}
	}
	/* ELEMENTAL */
	private	AstCirNode	parse_ast_init_declarator(AstInitDeclarator source) throws Exception {
		AstCirNode parent = this.new_tree_node(source, null);
		parent.add_child(AstCirParChild.lvalue, this.parse_ast(source.get_declarator()));
		if(source.has_initializer()) {
			parent.add_child(AstCirParChild.rvalue, this.parse_ast(source.get_initializer()));
		}
		return parent;
	}
	private	AstCirNode	parse_ast_initializer(AstInitializer source) throws Exception {
		if(source.is_body()) {
			return this.parse_ast(source.get_body());
		}
		else {
			return this.parse_ast(source.get_expression());
		}
	} 
	private	AstCirNode	parse_ast_field_initializer(AstFieldInitializer source) throws Exception {
		return this.parse_ast(source.get_initializer());
	}
	private	AstCirNode	parse_ast_declarator(AstDeclarator source) throws Exception {
		if(source.get_production() == DeclaratorProduction.identifier) {
			return this.parse_ast(source.get_identifier());
		}
		else {
			return this.parse_ast(source.get_declarator());
		}
	}
	private	AstCirNode	parse_ast_name(AstName source) throws Exception {
		CName cname = source.get_cname();
		if(cname instanceof CEnumeratorName) {
			int value = ((CEnumeratorName) cname).get_enumerator().get_value();
			CConstant constant = new CConstant(); constant.set_int(value);
			return this.new_tree_node(source, constant);
		}
		else if(cname instanceof CTypeName) {
			return this.new_tree_node(source, ((CTypeName) cname).get_type());
		}
		else {
			return this.new_tree_node(source, source.get_name());
		}
	}
	private	AstCirNode	parse_ast_keyword(AstKeyword source) throws Exception {
		return this.new_tree_node(source, source.get_keyword());
	}
	private	AstCirNode	parse_ast_field(AstField source) throws Exception {
		return this.new_tree_node(source, source.get_name());
	}
	private	AstCirNode	parse_ast_function_definition(AstFunctionDefinition source) throws Exception {
		AstCirNode parent = this.new_tree_node(source, null);
		parent.add_child(AstCirParChild.name, this.parse_ast(source.get_declarator()));
		parent.add_child(AstCirParChild.body, this.parse_ast(source.get_body()));
		return parent;
	}
	private	AstCirNode	parse_ast_translation_unit(AstTranslationUnit source) throws Exception {
		AstCirNode parent = this.new_tree_node(source, null);
		for(int k = 0; k < source.number_of_units(); k++) {
			AstCirNode child = this.parse_ast(source.get_unit(k));
			parent.add_child(AstCirParChild.define, child);
		}
		return parent;
	}
	private	AstCirNode	parse_ast_elemental(AstNode source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstInitDeclarator) {
			return this.parse_ast_init_declarator((AstInitDeclarator) source);
		}
		else if(source instanceof AstInitializer) {
			return this.parse_ast_initializer((AstInitializer) source);
		}
		else if(source instanceof AstFieldInitializer) {
			return this.parse_ast_field_initializer((AstFieldInitializer) source);
		}
		else if(source instanceof AstDeclarator) {
			return this.parse_ast_declarator((AstDeclarator) source);
		}
		else if(source instanceof AstName) {
			return this.parse_ast_name((AstName) source);
		}
		else if(source instanceof AstField) {
			return this.parse_ast_field((AstField) source);
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
