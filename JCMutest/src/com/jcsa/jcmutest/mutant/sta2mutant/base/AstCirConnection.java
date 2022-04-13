package com.jcsa.jcmutest.mutant.sta2mutant.base;

import java.util.List;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.AstDeclaration;
import com.jcsa.jcparse.lang.astree.decl.AstTypeName;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstName;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator.DeclaratorProduction;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstBasicExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;
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
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.astree.unit.AstTranslationUnit;
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
import com.jcsa.jcparse.lang.irlang.expr.CirStringLiteral;
import com.jcsa.jcparse.lang.irlang.expr.CirType;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirDefaultStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirInitAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabelStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirReturnAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirSaveAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * 	It implements the connection from AstContextNode to CirNode using AstcontextData
 * 	@author yukimula
 *
 */
public final class AstCirConnection {
	
	/* definitions */
	/** node to being connected **/	private	AstContextNode	tree_node;
	private AstCirConnection() { this.tree_node = null; }
	static final AstCirConnection connector = new AstCirConnection();
	
	/* localization */
	/**
	 * It creates a new data state w.r.t. the given type in current node
	 * @param type
	 * @param store
	 * @param value
	 * @return
	 * @throws Exception
	 */
	private	AstContextData 	new_context_data(AstContextDataType type, 
			CirNode store, SymbolExpression value) throws Exception {
		if(this.tree_node == null) {
			throw new IllegalArgumentException("Invalid tree_node: null");
		}
		else if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(store == null) { return null; /* no valid data is */ }
		/*else if(value == null) {
			throw new IllegalArgumentException("Invalid value: null");
		}*/
		else { return this.tree_node.add_state(type, store, value); }
	}
	/**
	 * @param source
	 * @param cir_class
	 * @return it derives the list of CirNode(s) of the source with given class
	 * @throws Exception
	 */
	private	List<CirNode>	loc_cir_locations(AstNode source, Class<?> cir_class) throws Exception {
		if(this.tree_node == null) {
			throw new IllegalArgumentException("Invalid tree_node: null");
		}
		else if(source == null) {
			throw new IllegalArgumentException("Invlid source: null");
		}
		else {
			return this.tree_node.get_tree().get_cir_tree().get_cir_nodes(source, cir_class);
		}
	}
	/**
	 * @param source
	 * @param cir_class
	 * @param k
	 * @return null if no such nodes exist
	 * @throws Exception
	 */
	private	CirNode			loc_cir_location(AstNode source, Class<?> cir_class, int k) throws Exception {
		List<CirNode> locations = this.loc_cir_locations(source, cir_class);
		if(k < 0 || k >= locations.size()) { return null; }
		else { return locations.get(k); }
	}
	/**
	 * @param source
	 * @param cir_class
	 * @return it derives the first expression w.r.t. the source node and class
	 * @throws Exception
	 */
	private	CirExpression	loc_cir_expression(AstNode source, Class<?> cir_class) throws Exception {
		List<CirNode> locations = this.loc_cir_locations(source, cir_class);
		CirExpression expression = null;
		for(CirNode location : locations) {
			if(location instanceof CirExpression) {
				expression = (CirExpression) location;
				if(expression.execution_of() != null) {
					break;
				}
			}
		}
		return expression;
	}
	/**
	 * @param source
	 * @return the first statement of the source in the cir-tree.
	 * @throws Exception
	 */
	private	CirStatement	loc_cir_statement(AstNode source) throws Exception {
		if(this.tree_node == null) {
			throw new IllegalArgumentException("Invalid cir_tree: null");
		}
		else if(source == null) {
			throw new IllegalArgumentException("Invalid source as null");
		}
		else {
			AstCirPair range;
			CirTree cir_tree = this.tree_node.get_tree().get_cir_tree();
			while(!cir_tree.has_cir_range(source)) {
				source = source.get_parent();
			}
			range = cir_tree.get_cir_range(source);
			return range.get_beg_statement();
		}
	}
	
	/* connections */
	/**
	 * It connects each tree node to the CirNode location and store value.
	 * @param tree_node
	 * @throws Exception
	 */
	protected void	connect(AstContextNode tree_node) throws Exception {
		if(tree_node == null) {
			throw new IllegalArgumentException("Invalid tree_node: null");
		}
		else {
			this.tree_node = tree_node;
			this.parse(tree_node.get_ast_source());
		}
	}
	private	void	parse(AstNode source) throws Exception {
		if(this.tree_node == null) {
			throw new IllegalArgumentException("Invalid tree_node");
		}
		else if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstExpression) {
			this.parse_expression((AstExpression) source);
		}
		else if(source instanceof AstStatement) {
			this.parse_statement((AstStatement) source);
		}
		else { this.parse_elemental(source); }
	}
	/* expression */
	private	void	parse_basic_expression(AstBasicExpression source) throws Exception {
		if(source instanceof AstIdExpression) {
			this.parse_id_expression((AstIdExpression) source);
		}
		else if(source instanceof AstConstant) {
			this.parse_constant((AstConstant) source);
		}
		else if(source instanceof AstLiteral) {
			this.parse_literal((AstLiteral) source);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + source);
		}
	}
	private	void	parse_id_expression(AstIdExpression source) throws Exception {
		CirExpression store = this.loc_cir_expression(source, CirExpression.class);
		this.new_context_data(AstContextDataType.value, store, SymbolFactory.sym_expression(source));
	}
	private	void	parse_constant(AstConstant source) throws Exception {
		CirExpression store = this.loc_cir_expression(source, CirConstExpression.class);
		this.new_context_data(AstContextDataType.value, store, SymbolFactory.sym_expression(source));
	}
	private	void	parse_literal(AstLiteral source) throws Exception {
		CirExpression store = this.loc_cir_expression(source, CirStringLiteral.class);
		this.new_context_data(AstContextDataType.value, store, SymbolFactory.sym_expression(source));
	}
	private	void	parse_unary_expression(AstUnaryExpression source) throws Exception {
		CirNode store;
		switch(source.get_operator().get_operator()) {
		case positive:		
		{
			this.parse(source.get_operand()); break;
		}
		case negative:
		{
			store = this.loc_cir_expression(source, CirArithExpression.class);
			this.new_context_data(AstContextDataType.value, store, SymbolFactory.sym_expression(source));
			break;
		}
		case bit_not:
		{
			store = this.loc_cir_expression(source, CirBitwsExpression.class);
			this.new_context_data(AstContextDataType.value, store, SymbolFactory.sym_expression(source));
			break;
		}
		case logic_not:
		{
			store = this.loc_cir_expression(source, CirLogicExpression.class);
			this.new_context_data(AstContextDataType.value, store, SymbolFactory.sym_expression(source));
			break;
		}
		case address_of:
		{
			store = this.loc_cir_expression(source, CirAddressExpression.class);
			this.new_context_data(AstContextDataType.value, store, SymbolFactory.sym_expression(source));
			break;
		}
		case dereference:
		{
			store = this.loc_cir_expression(source, CirDeferExpression.class);
			this.new_context_data(AstContextDataType.value, store, SymbolFactory.sym_expression(source));
			break;
		}
		case increment:
		case decrement:
		{
			store = this.loc_cir_location(source, CirIncreAssignStatement.class, 0);
			this.new_context_data(AstContextDataType.assign, store, SymbolFactory.sym_expression(store));
			store = ((CirIncreAssignStatement) store).get_lvalue();
			this.new_context_data(AstContextDataType.value, store, SymbolFactory.sym_expression(source));
			break;
		}
		default:	throw new IllegalArgumentException("Invalid:"  + source.generate_code());
		}
	}
	private	void	parse_postfix_expression(AstPostfixExpression source) throws Exception {
		CirNode store;
		switch(source.get_operator().get_operator()) {
		case increment:
		case decrement:
		{
			store = this.loc_cir_location(source, CirIncreAssignStatement.class, 0);
			this.new_context_data(AstContextDataType.assign, store, SymbolFactory.sym_expression(store));
			store = ((CirIncreAssignStatement) store).get_lvalue();
			this.new_context_data(AstContextDataType.value, store, SymbolFactory.sym_expression(source));
			break;
		}
		default:	throw new IllegalArgumentException("Invalid:"  + source.generate_code());
		}
	}
	private	void	parse_array_expression(AstArrayExpression source) throws Exception {
		CirExpression store = this.loc_cir_expression(source, CirDeferExpression.class);
		this.new_context_data(AstContextDataType.value, store, SymbolFactory.sym_expression(source));
	}
	private	void	parse_cast_expression(AstCastExpression source) throws Exception {
		CirExpression store = this.loc_cir_expression(source, CirCastExpression.class);
		this.new_context_data(AstContextDataType.value, store, SymbolFactory.sym_expression(source));
	}
	private	void	parse_comma_expression(AstCommaExpression source) throws Exception { 
		int n = source.number_of_arguments() - 1;
		this.parse(source.get_expression(n));
	}
	private	void	parse_conditional_expression(AstConditionalExpression source) throws Exception {
		CirNode store;
		store = this.loc_cir_location(source, CirIfStatement.class, 0);
		this.new_context_data(AstContextDataType.select, store, SymbolFactory.sym_expression(store));
		store = this.loc_cir_location(source, CirImplicator.class, 2);
		this.new_context_data(AstContextDataType.value, store, SymbolFactory.sym_expression(source));
	}
	private	void	parse_const_expression(AstConstExpression source) throws Exception {
		this.parse(source.get_expression());
	}
	private	void	parse_paranth_expression(AstParanthExpression source) throws Exception {
		this.parse(source.get_sub_expression());
	}
	private	void	parse_field_expression(AstFieldExpression source) throws Exception {
		CirExpression store = this.loc_cir_expression(source, CirFieldExpression.class);
		this.new_context_data(AstContextDataType.value, store, SymbolFactory.sym_expression(source));
	}
	private	void	parse_fun_call_expression(AstFunCallExpression source) throws Exception {
		CirNode store;
		store = this.loc_cir_location(source, CirCallStatement.class, 0);
		this.new_context_data(AstContextDataType.invoke, store, SymbolFactory.sym_expression(store));
		store = this.loc_cir_expression(source, CirWaitExpression.class);
		this.new_context_data(AstContextDataType.value, store, SymbolFactory.sym_expression(source));
	}
	private	void	parse_initializer_body(AstInitializerBody source) throws Exception {
		CirExpression store = this.loc_cir_expression(source, CirInitializerBody.class);
		this.new_context_data(AstContextDataType.value, store, SymbolFactory.sym_expression(source));
	}
	private	void	parse_sizeof_expression(AstSizeofExpression source) throws Exception {
		CirExpression store = this.loc_cir_expression(source, CirConstExpression.class);
		this.new_context_data(AstContextDataType.value, store, SymbolFactory.sym_expression(source));
	}
	private	void	parse_binary_expression(AstBinaryExpression source) throws Exception {
		CirNode store;
		switch(source.get_operator().get_operator()) {
		case arith_add:
		case arith_sub:
		case arith_mul:
		case arith_div:
		case arith_mod:
		{
			store = this.loc_cir_expression(source, CirArithExpression.class);
			this.new_context_data(AstContextDataType.value, store, SymbolFactory.sym_expression(source));
			break;
		}
		case bit_and:
		case bit_or:
		case bit_xor:
		case left_shift:
		case righ_shift:
		{
			store = this.loc_cir_expression(source, CirBitwsExpression.class);
			this.new_context_data(AstContextDataType.value, store, SymbolFactory.sym_expression(source));
			break;
		}
		case greater_tn:
		case greater_eq:
		case smaller_tn:
		case smaller_eq:
		case not_equals:
		case equal_with:
		{
			store = this.loc_cir_expression(source, CirRelationExpression.class);
			this.new_context_data(AstContextDataType.value, store, SymbolFactory.sym_expression(source));
			break;
		}
		case logic_and:
		case logic_or:
		{
			store = this.loc_cir_location(source, CirIfStatement.class, 0);
			this.new_context_data(AstContextDataType.select, store, SymbolFactory.sym_expression(store));
			store = this.loc_cir_location(source, CirImplicator.class, 3);
			this.new_context_data(AstContextDataType.value, store, SymbolFactory.sym_expression(source));
			break;
		}
		case assign:
		{
			store = this.loc_cir_location(source, CirAssignStatement.class, 0);
			this.new_context_data(AstContextDataType.assign, store, SymbolFactory.sym_expression(store));
			break;
		}
		case arith_add_assign:
		case arith_sub_assign:
		case arith_mul_assign:
		case arith_div_assign:
		case arith_mod_assign:
		{
			store = this.loc_cir_location(source, CirAssignStatement.class, 0);
			this.new_context_data(AstContextDataType.assign, store, SymbolFactory.sym_expression(store));
			store = this.loc_cir_expression(source, CirArithExpression.class);
			this.new_context_data(AstContextDataType.value, store, SymbolFactory.sym_expression(source));
			break;
		}
		case bit_and_assign:
		case bit_or_assign:
		case bit_xor_assign:
		case left_shift_assign:
		case righ_shift_assign:
		{
			store = this.loc_cir_location(source, CirAssignStatement.class, 0);
			this.new_context_data(AstContextDataType.assign, store, SymbolFactory.sym_expression(store));
			store = this.loc_cir_expression(source, CirBitwsExpression.class);
			this.new_context_data(AstContextDataType.value, store, SymbolFactory.sym_expression(source));
			break;
		}
		default:	throw new IllegalArgumentException("Unsupport: " + source.generate_code());
		}
	}
	private	void	parse_expression(AstExpression source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstBasicExpression) {
			this.parse_basic_expression((AstBasicExpression) source);
		}
		else if(source instanceof AstBinaryExpression) {
			this.parse_binary_expression((AstBinaryExpression) source);
		}
		else if(source instanceof AstUnaryExpression) {
			this.parse_unary_expression((AstUnaryExpression) source);
		}
		else if(source instanceof AstPostfixExpression) {
			this.parse_postfix_expression((AstPostfixExpression) source);
		}
		else if(source instanceof AstArrayExpression) {
			this.parse_array_expression((AstArrayExpression) source);
		}
		else if(source instanceof AstCastExpression) {
			this.parse_cast_expression((AstCastExpression) source);
		}
		else if(source instanceof AstCommaExpression) {
			this.parse_comma_expression((AstCommaExpression) source);
		}
		else if(source instanceof AstConditionalExpression) {
			this.parse_conditional_expression((AstConditionalExpression) source);
		}
		else if(source instanceof AstConstExpression) {
			this.parse_const_expression((AstConstExpression) source);
		}
		else if(source instanceof AstParanthExpression) {
			this.parse_paranth_expression((AstParanthExpression) source);
		}
		else if(source instanceof AstFieldExpression) {
			this.parse_field_expression((AstFieldExpression) source);
		}
		else if(source instanceof AstFunCallExpression) {
			this.parse_fun_call_expression((AstFunCallExpression) source);
		}
		else if(source instanceof AstInitializerBody) {
			this.parse_initializer_body((AstInitializerBody) source);
		}
		else if(source instanceof AstSizeofExpression) {
			this.parse_sizeof_expression((AstSizeofExpression) source);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + source);
		}
	}
	/* statement */
	private	void	parse_declaration_statement(AstDeclarationStatement source) throws Exception {
		CirStatement store = this.loc_cir_statement(source);
		if(store != null)
			this.new_context_data(AstContextDataType.execute, store, SymbolFactory.sym_expression(store));
	}
	private	void	parse_expression_statement(AstExpressionStatement source) throws Exception {
		CirStatement store = this.loc_cir_statement(source);
		if(store != null)
			this.new_context_data(AstContextDataType.execute, store, SymbolFactory.sym_expression(store));
	}
	private	void	parse_compound_statement(AstCompoundStatement source) throws Exception {
		CirStatement store = this.loc_cir_statement(source);
		if(store != null)
			this.new_context_data(AstContextDataType.execute, store, SymbolFactory.sym_expression(store));
	}
	private	void	parse_break_statement(AstBreakStatement source) throws Exception {
		CirNode store = this.loc_cir_location(source, CirGotoStatement.class, 0);
		this.new_context_data(AstContextDataType.execute, store, SymbolFactory.sym_expression(store));
	}
	private	void	parse_continue_statement(AstContinueStatement source) throws Exception {
		CirNode store = this.loc_cir_location(source, CirGotoStatement.class, 0);
		this.new_context_data(AstContextDataType.execute, store, SymbolFactory.sym_expression(store));
	}
	private	void	parse_goto_statement(AstGotoStatement source) throws Exception {
		CirNode store = this.loc_cir_location(source, CirGotoStatement.class, 0);
		this.new_context_data(AstContextDataType.execute, store, SymbolFactory.sym_expression(store));
	}
	private	void	parse_return_statement(AstReturnStatement source) throws Exception {
		CirNode store;
		if(source.has_expression()) {
			store = this.loc_cir_location(source, CirReturnAssignStatement.class, 0);
			this.new_context_data(AstContextDataType.assign, store, SymbolFactory.sym_expression(source));
		}
		store = this.loc_cir_location(source, CirGotoStatement.class, 0);
		this.new_context_data(AstContextDataType.execute, store, SymbolFactory.sym_expression(store));
	}
	private	void	parse_if_statement(AstIfStatement source) throws Exception {
		CirNode store;
		store = this.loc_cir_location(source, CirIfStatement.class, 0);
		this.new_context_data(AstContextDataType.select, store, SymbolFactory.sym_expression(store));
	}
	private	void	parse_for_statement(AstForStatement source) throws Exception {
		CirNode store;
		store = this.loc_cir_location(source, CirIfStatement.class, 0);
		this.new_context_data(AstContextDataType.select, store, SymbolFactory.sym_expression(store));
	}
	private	void	parse_while_statement(AstWhileStatement source) throws Exception {
		CirNode store;
		store = this.loc_cir_location(source, CirIfStatement.class, 0);
		this.new_context_data(AstContextDataType.select, store, SymbolFactory.sym_expression(store));
	}
	private	void	parse_do_while_statement(AstDoWhileStatement source) throws Exception {
		CirNode store;
		store = this.loc_cir_location(source, CirIfStatement.class, 0);
		this.new_context_data(AstContextDataType.select, store, SymbolFactory.sym_expression(store));
	}
	private	void	parse_switch_statement(AstSwitchStatement source) throws Exception {
		CirNode store = this.loc_cir_location(source, CirSaveAssignStatement.class, 0);
		this.new_context_data(AstContextDataType.assign, store, SymbolFactory.sym_expression(store));
	}
	private	void	parse_case_statement(AstCaseStatement source) throws Exception {
		CirNode store;
		store = this.loc_cir_location(source, CirCaseStatement.class, 0);
		this.new_context_data(AstContextDataType.select, store, SymbolFactory.sym_expression(store));
	}
	private	void	parse_default_statement(AstDefaultStatement source) throws Exception {
		CirNode store = this.loc_cir_location(source, CirDefaultStatement.class, 0);
		this.new_context_data(AstContextDataType.execute, store, SymbolFactory.sym_expression(store));
	}
	private	void	parse_labeled_statement(AstLabeledStatement source) throws Exception {
		CirNode store = this.loc_cir_location(source, CirLabelStatement.class, 0);
		this.new_context_data(AstContextDataType.execute, store, SymbolFactory.sym_expression(store));
	}
	private	void	parse_statement(AstStatement source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstDeclarationStatement) {
			this.parse_declaration_statement((AstDeclarationStatement) source);
		}
		else if(source instanceof AstExpressionStatement) {
			this.parse_expression_statement((AstExpressionStatement) source);
		}
		else if(source instanceof AstCompoundStatement) {
			this.parse_compound_statement((AstCompoundStatement) source);
		}
		else if(source instanceof AstBreakStatement) {
			this.parse_break_statement((AstBreakStatement) source);
		}
		else if(source instanceof AstContinueStatement) {
			this.parse_continue_statement((AstContinueStatement) source);
		}
		else if(source instanceof AstGotoStatement) {
			this.parse_goto_statement((AstGotoStatement) source);
		}
		else if(source instanceof AstReturnStatement) {
			this.parse_return_statement((AstReturnStatement) source);
		}
		else if(source instanceof AstLabeledStatement) {
			this.parse_labeled_statement((AstLabeledStatement) source);
		}
		else if(source instanceof AstDefaultStatement) {
			this.parse_default_statement((AstDefaultStatement) source);
		}
		else if(source instanceof AstCaseStatement) {
			this.parse_case_statement((AstCaseStatement) source);
		}
		else if(source instanceof AstIfStatement) {
			this.parse_if_statement((AstIfStatement) source);
		}
		else if(source instanceof AstSwitchStatement) {
			this.parse_switch_statement((AstSwitchStatement) source);
		}
		else if(source instanceof AstForStatement) {
			this.parse_for_statement((AstForStatement) source);
		}
		else if(source instanceof AstWhileStatement) {
			this.parse_while_statement((AstWhileStatement) source);
		}
		else if(source instanceof AstDoWhileStatement) {
			this.parse_do_while_statement((AstDoWhileStatement) source);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + source);
		}
	}
	/* elemental */
	private	void	parse_declaration(AstDeclaration source) throws Exception {
		CirStatement store = this.loc_cir_statement(source);
		if(store != null)
			this.new_context_data(AstContextDataType.execute, store, SymbolFactory.sym_expression(store));
	}
	private	void	parse_init_declarator(AstInitDeclarator source) throws Exception {
		CirNode store = this.loc_cir_location(source, CirInitAssignStatement.class, 0);
		this.new_context_data(AstContextDataType.assign, store, SymbolFactory.sym_expression(source));
	}
	private	void	parse_declarator(AstDeclarator source) throws Exception {
		if(source.get_production() == DeclaratorProduction.identifier) {
			this.parse(source.get_identifier());
		}
		else {
			this.parse(source.get_declarator());
		}
	}
	private	void	parse_name(AstName source) throws Exception {
		/*
		CirExpression store = this.loc_cir_expression(source.get_parent(), CirExpression.class);
		this.new_context_data(AstContextDataType.value, store, SymbolFactory.sym_expression(source));
		*/
	}
	private	void	parse_typename(AstTypeName source) throws Exception {
		CirNode store = this.loc_cir_location(source, CirType.class, 0);
		this.new_context_data(AstContextDataType.type, store, null);
	}
	private	void	parse_field(AstField source) throws Exception {
		CirNode store = this.loc_cir_location(source, CirField.class, 0);
		this.new_context_data(AstContextDataType.field, store, null);
	}
	private	void	parse_function_definition(AstFunctionDefinition source) throws Exception {
		CirNode store = this.loc_cir_location(source, CirFunctionDefinition.class, 0);
		this.new_context_data(AstContextDataType.function, store, null);
	}
	private	void	parse_translation_unit(AstTranslationUnit source) throws Exception {
		CirNode store = this.tree_node.get_tree().get_cir_tree().get_root();
		this.new_context_data(AstContextDataType.transition, store, null);
	}
	private	void	parse_elemental(AstNode source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstDeclaration) {
			this.parse_declaration((AstDeclaration) source);
		}
		else if(source instanceof AstInitDeclarator) {
			this.parse_init_declarator((AstInitDeclarator) source);
		}
		else if(source instanceof AstDeclarator) {
			this.parse_declarator((AstDeclarator) source);
		}
		else if(source instanceof AstName) {
			this.parse_name((AstName) source);
		}
		else if(source instanceof AstTypeName) {
			this.parse_typename((AstTypeName) source);
		}
		else if(source instanceof AstField) {
			this.parse_field((AstField) source);
		}
		else if(source instanceof AstFunctionDefinition) {
			this.parse_function_definition((AstFunctionDefinition) source);
		}
		else if(source instanceof AstTranslationUnit) {
			this.parse_translation_unit((AstTranslationUnit) source);
		}
		else { /* do no connection for others */ }
	}
	
}
