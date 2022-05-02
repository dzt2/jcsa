package com.jcsa.jcparse.lang.program;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.base.AstIdentifier;
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
import com.jcsa.jcparse.lang.irlang.AstCirPair;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirArithExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirBitwsExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirConstExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirField;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirImplicator;
import com.jcsa.jcparse.lang.irlang.expr.CirInitializerBody;
import com.jcsa.jcparse.lang.irlang.expr.CirLogicExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirRelationExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirReturnPoint;
import com.jcsa.jcparse.lang.irlang.expr.CirStringLiteral;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirDefaultStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirInitAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabelStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirReturnAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;
import com.jcsa.jcparse.lang.irlang.unit.CirTransitionUnit;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.CKeyword;
import com.jcsa.jcparse.lang.program.types.AstCirEdgeType;
import com.jcsa.jcparse.lang.program.types.AstCirLinkType;
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
	/** the current node to be link to CIR **/	private	AstCirNode	cur_node;
	private AstCirTreeParser() { this.tree = null; this.cur_node = null; }
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
		AstCirNode function = this.parse_ast(source.get_function());
		String func_name = null;
		if(function.get_ast_source() instanceof AstIdentifier) {
			func_name = function.get_token().toString();
		}
		
		AstCirNode parent = this.new_tree_node(source, func_name);
		parent.add_child(AstCirParChild.callee, function);
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
		int size = this.tree.get_sizeof_template().sizeof(type);
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
		AstExpressionStatement condition = source.get_condition();
		if(condition.has_expression()) {
			parent.add_child(AstCirParChild.condition, this.parse_ast(condition.get_expression()));
		}
		else {
			parent.add_child(AstCirParChild.condition, this.parse_ast(condition));
		}
		if(source.has_increment()) {
			parent.add_child(AstCirParChild.execute, this.parse_ast(source.get_increment()));
		}
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
	
	/* LINK-METHODS */
	/**
	 * It constructs the linking from AstNode to CirNode
	 * @param tree
	 * @throws Exception
	 */
	protected static void 	link(AstCirTree tree) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree: null");
		}
		else {
			parser.tree = tree;
			for(AstCirNode tree_node : tree.get_tree_nodes()) {
				parser.cur_node = tree_node;
				parser.link_ast(tree_node.get_ast_source());
				parser.cond_ast(tree_node.get_ast_source());
			}
		}
	}
	/**
	 * @param type
	 * @param target
	 * @return it creates a link from AstNode to CirNode in C-intermediate code
	 * @throws Exception
	 */
	private	AstCirLink		new_ast_link(AstCirLinkType type, CirNode target) throws Exception {
		if(this.cur_node == null) {
			throw new IllegalArgumentException("Invalid curr_node: null");
		}
		else if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(target == null) { return null; /* none of linking */ }
		else { return this.cur_node.add_link(type, target); }
	}
	/**
	 * It links the node to CirNode location in the current tree-node
	 * @param source
	 * @throws Exception
	 */
	private	void			link_ast(AstNode source) throws Exception {
		if(this.cur_node == null) {
			throw new IllegalArgumentException("Invalid cur_node: null");
		}
		else if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstExpression) {
			this.link_ast_expression((AstExpression) source);
		}
		else if(source instanceof AstStatement) {
			this.link_ast_statement((AstStatement) source);
		}
		else { this.link_ast_element(source); }
	}
	/* LOCALIZATION */
	/**
	 * @param source
	 * @param cir_class
	 * @return the list of CirNode(s) as locations to be assess from AST source
	 * @throws Exception
	 */
	private	List<CirNode>	loc_cir_locations(AstNode source, Class<?> cir_class) throws Exception {
		if(this.cur_node == null) {
			throw new IllegalArgumentException("Invalid cur_node: null");
		}
		else if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(cir_class == null) {
			throw new IllegalArgumentException("Invalid cir_class: null");
		}
		else { 
			return this.cur_node.get_tree().get_cir_tree().get_cir_nodes(source, cir_class);
		}
	}
	/**
	 * @param source
	 * @param cir_class
	 * @param k
	 * @return the kth C-intermediate location w.r.t. the type of the source
	 * @throws Exception
	 */
	private	CirNode			loc_cir_location(AstNode source, Class<?> cir_class, int k) throws Exception {
		List<CirNode> cir_nodes = this.loc_cir_locations(source, cir_class);
		if(k < 0 || k >= cir_nodes.size()) { return null; }
		else { return cir_nodes.get(k); }
	}
	/**
	 * @param source
	 * @param cir_class
	 * @return the expression being localized w.r.t. the source and class
	 * @throws Exception
	 */
	private	CirExpression	loc_cir_expression(AstNode source, Class<?> cir_class) throws Exception {
		List<CirNode> cir_nodes = this.loc_cir_locations(source, cir_class);
		CirExpression expression = null;
		for(CirNode cir_node : cir_nodes) {
			if(cir_node instanceof CirExpression) {
				expression = (CirExpression) cir_node;
				if(expression.execution_of() == null) {
					break;
				}
			}
		}
		return expression;
	}
	/**
	 * @param source
	 * @return the statement w.r.t. the source and given class
	 * @throws Exception
	 */
	private	CirStatement	loc_cir_statement(AstNode source) throws Exception {
		if(this.cur_node == null) {
			throw new IllegalArgumentException("Invalid cir_tree: null");
		}
		else if(source == null) {
			throw new IllegalArgumentException("Invalid source as null");
		}
		else {
			AstCirPair range;
			CirTree cir_tree = this.cur_node.get_tree().get_cir_tree();
			while(!cir_tree.has_cir_range(source)) {
				source = source.get_parent();
			}
			range = cir_tree.get_cir_range(source);
			return range.get_beg_statement();
		}
	}
	/* EXPRESSION */
	private	void			link_ast_expression(AstExpression source) throws Exception {
		if(source instanceof AstBasicExpression) {
			this.link_ast_basic_expression((AstBasicExpression) source);
		}
		else if(source instanceof AstUnaryExpression) {
			this.link_ast_unary_expression((AstUnaryExpression) source);
		}
		else if(source instanceof AstPostfixExpression) {
			this.link_ast_postfix_expression((AstPostfixExpression) source);
		}
		else if(source instanceof AstBinaryExpression) {
			this.link_ast_binary_expression((AstBinaryExpression) source);
		}
		else if(source instanceof AstArrayExpression) {
			this.link_ast_array_expression((AstArrayExpression) source);
		}
		else if(source instanceof AstCastExpression) {
			this.link_ast_cast_expression((AstCastExpression) source);
		}
		else if(source instanceof AstCommaExpression) {
			this.link_ast_comma_expression((AstCommaExpression) source);
		}
		else if(source instanceof AstConstExpression) {
			this.link_ast_const_expression((AstConstExpression) source);
		}
		else if(source instanceof AstParanthExpression) {
			this.link_ast_paranth_expression((AstParanthExpression) source);
		}
		else if(source instanceof AstConditionalExpression) {
			this.link_ast_conditional_expression((AstConditionalExpression) source);
		}
		else if(source instanceof AstFieldExpression) {
			this.link_ast_field_expression((AstFieldExpression) source);
		}
		else if(source instanceof AstFunCallExpression) {
			this.link_ast_fun_call_expression((AstFunCallExpression) source);
		}
		else if(source instanceof AstInitializerBody) {
			this.link_ast_initializer_body((AstInitializerBody) source);
		}
		else if(source instanceof AstSizeofExpression) {
			this.link_ast_sizeof_expression((AstSizeofExpression) source);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + source.generate_code());
		}
	}
	private	void			link_ast_id_expression(AstIdExpression source) throws Exception {
		CirExpression target = this.loc_cir_expression(source, CirExpression.class);
		this.new_ast_link(AstCirLinkType.used_expr, target);
	}
	private	void			link_ast_constant(AstConstant source) throws Exception {
		CirExpression target = this.
				loc_cir_expression(source, CirConstExpression.class);
		this.new_ast_link(AstCirLinkType.used_expr, target);
	}
	private	void			link_ast_literal(AstLiteral source) throws Exception {
		CirExpression target = this.
				loc_cir_expression(source, CirStringLiteral.class);
		this.new_ast_link(AstCirLinkType.used_expr, target);
	}
	private	void			link_ast_basic_expression(AstBasicExpression source) throws Exception {
		if(source instanceof AstIdExpression) {
			this.link_ast_id_expression((AstIdExpression) source);
		}
		else if(source instanceof AstConstant) {
			this.link_ast_constant((AstConstant) source);
		}
		else {
			this.link_ast_literal((AstLiteral) source);
		}
	}
	private	void			link_ast_unary_expression(AstUnaryExpression source) throws Exception {
		switch(source.get_operator().get_operator()) {
		case positive:		
		{
			this.link_ast(source.get_operand()); break;
		}
		case negative:
		{
			this.new_ast_link(AstCirLinkType.used_expr, this.
					loc_cir_expression(source, CirArithExpression.class));
			break;
		}
		case bit_not:
		{
			this.new_ast_link(AstCirLinkType.used_expr, this.
					loc_cir_expression(source, CirBitwsExpression.class));
			break;
		}
		case logic_not:
		{
			this.new_ast_link(AstCirLinkType.used_expr, this.
					loc_cir_expression(source, CirLogicExpression.class));
			break;
		}
		case address_of:
		{
			this.new_ast_link(AstCirLinkType.used_expr, this.
					loc_cir_expression(source, CirAddressExpression.class));
			break;
		}
		case dereference:	
		{
			this.new_ast_link(AstCirLinkType.used_expr, this.
					loc_cir_expression(source, CirDeferExpression.class));
			break;
		}
		case increment:
		case decrement:
		{
			CirIncreAssignStatement statement = (CirIncreAssignStatement) this.
					loc_cir_location(source, CirIncreAssignStatement.class, 0);
			this.new_ast_link(AstCirLinkType.assg_stmt, statement);
			this.new_ast_link(AstCirLinkType.used_expr, statement.get_rvalue());
			break;
		}
		default:
		{
			throw new IllegalArgumentException("Invalid: " + source.generate_code());
		}
		}
	}
	private	void			link_ast_postfix_expression(AstPostfixExpression source) throws Exception {
		switch(source.get_operator().get_operator()) {
		case increment:
		case decrement:
		{
			CirIncreAssignStatement statement = (CirIncreAssignStatement) this.
					loc_cir_location(source, CirIncreAssignStatement.class, 0);
			this.new_ast_link(AstCirLinkType.assg_stmt, statement);
			this.new_ast_link(AstCirLinkType.used_expr, this.
					loc_cir_location(source, CirImplicator.class, 1));
			break;
		}
		default:
		{
			throw new IllegalArgumentException("Invalid: " + source.generate_code());
		}
		}
	}
	private	void			link_ast_binary_expression(AstBinaryExpression source) throws Exception {
		switch(source.get_operator().get_operator()) {
		case arith_add:
		case arith_sub:
		case arith_mul:
		case arith_div:
		case arith_mod:
		{
			this.new_ast_link(AstCirLinkType.used_expr, this.
					loc_cir_expression(source, CirArithExpression.class));
			break;
		}
		case bit_and:
		case bit_or:
		case bit_xor:
		case left_shift:
		case righ_shift:
		{
			this.new_ast_link(AstCirLinkType.used_expr, this.
					loc_cir_expression(source, CirBitwsExpression.class));
			break;
		}
		case greater_tn:
		case greater_eq:
		case smaller_tn:
		case smaller_eq:
		case equal_with:
		case not_equals:
		{
			this.new_ast_link(AstCirLinkType.used_expr, this.
					loc_cir_expression(source, CirRelationExpression.class));
			break;
		}
		case logic_and:
		case logic_or:
		{
			this.new_ast_link(AstCirLinkType.ifte_stmt, this.
					loc_cir_location(source, CirIfStatement.class, 0));
			this.new_ast_link(AstCirLinkType.used_expr, this.
					loc_cir_location(source, CirImplicator.class, 3));
			break;
		}
		case assign:
		{
			this.new_ast_link(AstCirLinkType.assg_stmt, this.
					loc_cir_location(source, CirAssignStatement.class, 0));
			break;
		}
		case arith_add_assign:
		case arith_sub_assign:
		case arith_mul_assign:
		case arith_div_assign:
		case arith_mod_assign:
		{
			this.new_ast_link(AstCirLinkType.used_expr, this.
					loc_cir_expression(source, CirArithExpression.class));
			this.new_ast_link(AstCirLinkType.assg_stmt, this.
					loc_cir_location(source, CirAssignStatement.class, 0));
			break;
		}
		case bit_and_assign:
		case bit_or_assign:
		case bit_xor_assign:
		case left_shift_assign:
		case righ_shift_assign:
		{
			this.new_ast_link(AstCirLinkType.used_expr, this.
					loc_cir_expression(source, CirBitwsExpression.class));
			this.new_ast_link(AstCirLinkType.assg_stmt, this.
					loc_cir_location(source, CirAssignStatement.class, 0));
			break;
		}
		default:	throw new IllegalArgumentException(source.generate_code());
		}
	}
	private	void			link_ast_array_expression(AstArrayExpression source) throws Exception {
		this.new_ast_link(AstCirLinkType.used_expr, 
				this.loc_cir_expression(source, CirDeferExpression.class));
	}
	private	void			link_ast_cast_expression(AstCastExpression source) throws Exception {
		this.new_ast_link(AstCirLinkType.used_expr, 
				this.loc_cir_expression(source, CirCastExpression.class));
	}
	private	void			link_ast_comma_expression(AstCommaExpression source) throws Exception {
		int index = source.number_of_arguments() - 1;
		this.link_ast(source.get_expression(index));
	}
	private	void			link_ast_conditional_expression(AstConditionalExpression source) throws Exception {
		this.new_ast_link(AstCirLinkType.ifte_stmt, this.
				loc_cir_location(source, AstIfStatement.class, 0));
		this.new_ast_link(AstCirLinkType.used_expr, this.
				loc_cir_location(source, CirImplicator.class, 2));
	}
	private	void			link_ast_paranth_expression(AstParanthExpression source) throws Exception {
		this.link_ast(source.get_sub_expression());
	}
	private	void			link_ast_const_expression(AstConstExpression source) throws Exception {
		this.link_ast(source.get_expression());
	}
	private	void			link_ast_field_expression(AstFieldExpression source) throws Exception {
		this.new_ast_link(AstCirLinkType.used_expr, 
				this.loc_cir_expression(source, CirFieldExpression.class));
	}
	private	void			link_ast_sizeof_expression(AstSizeofExpression source) throws Exception {
		this.new_ast_link(AstCirLinkType.used_expr, this.
				loc_cir_expression(source, CirConstExpression.class));
	}
	private	void			link_ast_initializer_body(AstInitializerBody source) throws Exception {
		this.new_ast_link(AstCirLinkType.used_expr, this.
				loc_cir_expression(source, CirInitializerBody.class));
	}
	private	void			link_ast_fun_call_expression(AstFunCallExpression source) throws Exception {
		this.new_ast_link(AstCirLinkType.call_stmt, this.
				loc_cir_location(source, CirCallStatement.class, 0));
		this.new_ast_link(AstCirLinkType.assg_stmt, this.
				loc_cir_location(source, CirWaitAssignStatement.class, 0));
		this.new_ast_link(AstCirLinkType.used_expr, this.
				loc_cir_location(source, CirWaitExpression.class, 0));
	}
	/* STATEMENTS */
	private	void			link_ast_statement(AstStatement source) throws Exception {
		if(source instanceof AstDeclarationStatement) {
			this.link_ast_declaration_statement((AstDeclarationStatement) source);
		}
		else if(source instanceof AstExpressionStatement) {
			this.link_ast_expression_statement((AstExpressionStatement) source);
		}
		else if(source instanceof AstCompoundStatement) {
			this.link_ast_compound_statement((AstCompoundStatement) source);
		}
		else if(source instanceof AstBreakStatement) {
			this.link_ast_break_statement((AstBreakStatement) source);
		}
		else if(source instanceof AstContinueStatement) {
			this.link_ast_continue_statement((AstContinueStatement) source);
		}
		else if(source instanceof AstGotoStatement) {
			this.link_ast_goto_statement((AstGotoStatement) source);
		}
		else if(source instanceof AstReturnStatement) {
			this.link_ast_return_statement((AstReturnStatement) source);
		}
		else if(source instanceof AstIfStatement) {
			this.link_ast_if_statement((AstIfStatement) source);
		}
		else if(source instanceof AstSwitchStatement) {
			this.link_ast_switch_statement((AstSwitchStatement) source);
		}
		else if(source instanceof AstCaseStatement) {
			this.link_ast_case_statement((AstCaseStatement) source);
		}
		else if(source instanceof AstLabeledStatement) {
			this.link_ast_labeled_statement((AstLabeledStatement) source);
		}
		else if(source instanceof AstDefaultStatement) {
			this.link_ast_default_statement((AstDefaultStatement) source);
		}
		else if(source instanceof AstForStatement) {
			this.link_ast_for_statement((AstForStatement) source);
		}
		else if(source instanceof AstWhileStatement) {
			this.link_ast_while_statement((AstWhileStatement) source);
		}
		else if(source instanceof AstDoWhileStatement) {
			this.link_ast_do_while_statement((AstDoWhileStatement) source);
		}
		else {
			throw new IllegalArgumentException(source.generate_code());
		}
	}
	private	void			link_ast_declaration_statement(AstDeclarationStatement source) throws Exception {
		this.new_ast_link(AstCirLinkType.loct_stmt, this.loc_cir_statement(source));
	}
	private	void			link_ast_expression_statement(AstExpressionStatement source) throws Exception {
		if(source.get_parent() instanceof AstForStatement) {
			AstForStatement statement = (AstForStatement) source.get_parent();
			if(source == statement.get_condition()) {
				CirIfStatement if_stmt = (CirIfStatement) this.
						loc_cir_location(statement, CirIfStatement.class, 0);
				this.new_ast_link(AstCirLinkType.used_expr, if_stmt.get_condition());
			}
			else {
				this.new_ast_link(AstCirLinkType.loct_stmt, this.loc_cir_statement(source));
			}
		}
		else {
			this.new_ast_link(AstCirLinkType.loct_stmt, this.loc_cir_statement(source));
		}
	}
	private	void			link_ast_compound_statement(AstCompoundStatement source) throws Exception { }
	private	void			link_ast_break_statement(AstBreakStatement source) throws Exception {
		this.new_ast_link(AstCirLinkType.skip_stmt, this.loc_cir_location(source, CirGotoStatement.class, 0));
	}
	private	void			link_ast_continue_statement(AstContinueStatement source) throws Exception {
		this.new_ast_link(AstCirLinkType.skip_stmt, this.loc_cir_location(source, CirGotoStatement.class, 0));
	}
	private	void			link_ast_goto_statement(AstGotoStatement source) throws Exception {
		this.new_ast_link(AstCirLinkType.skip_stmt, this.loc_cir_location(source, CirGotoStatement.class, 0));
	}
	private	void			link_ast_return_statement(AstReturnStatement source) throws Exception {
		if(source.has_expression()) {
			this.new_ast_link(
					AstCirLinkType.assg_stmt, this.loc_cir_location(source, CirReturnAssignStatement.class, 0));
		}
		this.new_ast_link(AstCirLinkType.skip_stmt, this.loc_cir_location(source, CirGotoStatement.class, 0));
	}
	private	void			link_ast_labeled_statement(AstLabeledStatement source) throws Exception {
		this.new_ast_link(AstCirLinkType.labl_stmt, this.loc_cir_location(source, CirLabelStatement.class, 0));
	}
	private	void			link_ast_default_statement(AstDefaultStatement source) throws Exception {
		this.new_ast_link(AstCirLinkType.labl_stmt, this.loc_cir_location(source, CirDefaultStatement.class, 0));
	}
	private	void			link_ast_if_statement(AstIfStatement source) throws Exception {
		this.new_ast_link(AstCirLinkType.ifte_stmt, this.loc_cir_location(source, CirIfStatement.class, 0));
		this.new_ast_link(AstCirLinkType.labl_stmt, this.loc_cir_location(source, CirIfEndStatement.class, 0));
	}
	private	void			link_ast_case_statement(AstCaseStatement source) throws Exception {
		this.new_ast_link(AstCirLinkType.ifte_stmt, this.loc_cir_location(source, CirCaseStatement.class, 0));
	}
	private	void			link_ast_switch_statement(AstSwitchStatement source) throws Exception {
		this.new_ast_link(AstCirLinkType.loct_stmt, this.loc_cir_statement(source));
	}
	private	void			link_ast_for_statement(AstForStatement source) throws Exception {
		this.new_ast_link(AstCirLinkType.ifte_stmt, this.loc_cir_location(source, CirIfStatement.class, 0));
		this.new_ast_link(AstCirLinkType.labl_stmt, this.loc_cir_location(source, CirIfEndStatement.class, 0));
	}
	private	void			link_ast_while_statement(AstWhileStatement source) throws Exception {
		this.new_ast_link(AstCirLinkType.ifte_stmt, this.loc_cir_location(source, CirIfStatement.class, 0));
		this.new_ast_link(AstCirLinkType.labl_stmt, this.loc_cir_location(source, CirIfEndStatement.class, 0));
	}
	private	void			link_ast_do_while_statement(AstDoWhileStatement source) throws Exception {
		this.new_ast_link(AstCirLinkType.ifte_stmt, this.loc_cir_location(source, CirIfStatement.class, 0));
		this.new_ast_link(AstCirLinkType.labl_stmt, this.loc_cir_location(source, CirIfEndStatement.class, 0));
	}
	/* ELEMENTAL */
	private	void			link_ast_init_declarator(AstInitDeclarator source) throws Exception {
		this.new_ast_link(AstCirLinkType.assg_stmt, this.
				loc_cir_location(source, CirInitAssignStatement.class, 0));
	}
	private	void			link_ast_name(AstName source) throws Exception {
		this.new_ast_link(AstCirLinkType.used_expr, this.
				loc_cir_expression(source.get_parent(), CirExpression.class));
	}
	private	void			link_ast_keyword(AstKeyword source) throws Exception {
		if(source.get_keyword() == CKeyword.c89_return) {
			this.new_ast_link(AstCirLinkType.used_expr, this.loc_cir_expression(source, CirReturnPoint.class));
		}
	}
	private	void			link_ast_function_definition(AstFunctionDefinition source) throws Exception {
		this.new_ast_link(AstCirLinkType.func_defs, this.
				loc_cir_location(source, CirFunctionDefinition.class, 0));
	}
	private	void			link_ast_translation_unit(AstTranslationUnit source) throws Exception {
		this.new_ast_link(AstCirLinkType.func_defs, this.
				loc_cir_location(source, CirFunctionDefinition.class, 0));
		this.new_ast_link(AstCirLinkType.tran_unit, this.
				loc_cir_location(source, CirTransitionUnit.class, 0));
	}
	private	void			link_ast_field(AstField source) throws Exception {
		this.new_ast_link(AstCirLinkType.used_expr, this.loc_cir_location(source, CirField.class, 0));
	}
	private	void			link_ast_element(AstNode source) throws Exception {
		if(source instanceof AstInitDeclarator) {
			this.link_ast_init_declarator((AstInitDeclarator) source);
		}
		else if(source instanceof AstName) {
			this.link_ast_name((AstName) source);
		}
		else if(source instanceof AstField) {
			this.link_ast_field((AstField) source);
		}
		else if(source instanceof AstKeyword) {
			this.link_ast_keyword((AstKeyword) source);
		}
		else if(source instanceof AstFunctionDefinition) {
			this.link_ast_function_definition((AstFunctionDefinition) source);
		}
		else if(source instanceof AstTranslationUnit) {
			this.link_ast_translation_unit((AstTranslationUnit) source);
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	
	/* COND-METHODS */
	/**
	 * It builds the dependence relationships between AstCirNode.
	 * @param source
	 * @throws Exception
	 */
	private	void			cond_ast(AstNode source) throws Exception {
		if(this.cur_node == null) {
			throw new IllegalArgumentException("Invalid cur_node: null");
		}
		else if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstStatement) {
			this.cond_ast_statement((AstStatement) source);
		}
		/* TODO implement syntax-directed algorithm here */
		else { /* simply do nothing to unsupported nodes */ }
	}
	private	void			cond_ast_statement(AstStatement source) throws Exception {
		if(source instanceof AstBreakStatement) {
			this.cond_ast_break_statement((AstBreakStatement) source);
		}
		else if(source instanceof AstContinueStatement) {
			this.cond_ast_continue_statement((AstContinueStatement) source);
		}
		else if(source instanceof AstReturnStatement) {
			this.cond_ast_return_statement((AstReturnStatement) source);
		}
		else if(source instanceof AstGotoStatement) {
			this.cond_ast_goto_statement((AstGotoStatement) source);
		}
		else {
			this.cond_ast_otherwise_statement(source);
		}
	}
	private	void			cond_ast_break_statement(AstBreakStatement source) throws Exception {
		AstNode parent = source.get_parent();
		while(parent != null) {
			if(parent instanceof AstSwitchStatement
				|| parent instanceof AstForStatement
				|| parent instanceof AstWhileStatement
				|| parent instanceof AstDoWhileStatement) {
				AstCirNode target = this.cur_node.get_tree().get_tree_node(parent);
				this.cur_node.add_edge(AstCirEdgeType.skip_depend, target);
				return;
			}
			else { parent = parent.get_parent(); }
		}
		throw new IllegalArgumentException("No target found");
	}
	private	void			cond_ast_continue_statement(AstContinueStatement source) throws Exception {
		AstNode parent = source.get_parent();
		while(parent != null) {
			if(parent instanceof AstForStatement
				|| parent instanceof AstWhileStatement
				|| parent instanceof AstDoWhileStatement) {
				AstCirNode target = this.cur_node.get_tree().get_tree_node(parent);
				this.cur_node.add_edge(AstCirEdgeType.skip_depend, target); return;
			}
			else { parent = parent.get_parent(); }
		}
		throw new IllegalArgumentException("No target found");
	}
	private	void			cond_ast_return_statement(AstReturnStatement source) throws Exception {
		AstFunctionDefinition function = source.get_function_of();
		AstCirNode target = this.cur_node.get_tree().get_tree_node(function);
		this.cur_node.add_edge(AstCirEdgeType.retr_depend, target);
	}
	private	void			cond_ast_goto_statement(AstGotoStatement source) throws Exception {
		AstFunctionDefinition function = source.get_function_of();
		Queue<AstNode> queue = new LinkedList<AstNode>();
		queue.add(function); String name = source.get_label().get_name();
		while(!queue.isEmpty()) {
			AstNode parent = queue.poll();
			if(parent instanceof AstLabeledStatement) {
				if(((AstLabeledStatement) parent).get_label().get_name().equals(name)) {
					AstCirNode target = this.cur_node.get_tree().get_tree_node(parent);
					this.cur_node.add_edge(AstCirEdgeType.skip_depend, target); return;
				}
			}
			for(int k = 0; k < parent.number_of_children(); k++) {
				queue.add(parent.get_child(k));
			}
		}
	}
	private	void			cond_ast_otherwise_statement(AstStatement source) throws Exception {
		AstNode child = source, parent; AstCirNode target;
		do {
			parent = child.get_parent();
			if(parent instanceof AstFunctionDefinition) {
				target = this.cur_node.get_tree().get_tree_node(parent);
				target.add_edge(AstCirEdgeType.func_depend, this.cur_node);
				break;
			}
			else if(parent instanceof AstIfStatement) {
				target = this.cur_node.get_tree().get_tree_node(parent);
				if(((AstIfStatement) parent).has_else() && ((AstIfStatement) parent).get_false_branch() == child) {
					target.add_edge(AstCirEdgeType.fals_depend, this.cur_node);
				}
				else {
					target.add_edge(AstCirEdgeType.true_depend, this.cur_node);
				}
				break;
			}
			else if(parent instanceof AstSwitchStatement) {
				target = this.cur_node.get_tree().get_tree_node(parent);
				target.add_edge(AstCirEdgeType.case_depend, this.cur_node);
				break;
			}
			else if(parent instanceof AstWhileStatement || 
					parent instanceof AstForStatement || 
					parent instanceof AstDoWhileStatement) {
				target = this.cur_node.get_tree().get_tree_node(parent);
				target.add_edge(AstCirEdgeType.true_depend, this.cur_node);
				break;
			}
			else { child = parent; }
		}
		while(parent != null);
	}
	
}
