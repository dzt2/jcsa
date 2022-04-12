package com.jcsa.jcmutest.mutant.sta2mutant.abst;

import java.util.List;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstFieldInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerList;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArgumentList;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstParanthExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstBreakStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstContinueStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDeclarationStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstGotoStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.irlang.AstCirPair;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirReturnAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;

final class AstContextParser {
	
	/* definitions */
	/** context-tree **/	private	AstContextTree tree;
	/** constructor **/		private	AstContextParser() { }
	/**
	 * It recursively parses the source to the context node.
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private	AstContextNode	parse(AstNode source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		// TODO implement more
		else {
			throw new IllegalArgumentException(source.generate_code());
		}
	}
	
	/* basic methods */
	/**
	 * @param source
	 * @return it finds the real expression of the source represents
	 * @throws Exception
	 */
	private	AstExpression 	get_ast_expression(AstNode source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstParanthExpression) {
			return this.get_ast_expression(((AstParanthExpression) source).get_sub_expression());
		}
		else if(source instanceof AstConstExpression) {
			return this.get_ast_expression(((AstConstExpression) source).get_expression());
		}
		else if(source instanceof AstFieldInitializer) {
			return this.get_ast_expression(((AstFieldInitializer) source).get_initializer());
		}
		else if(source instanceof AstInitializer) {
			if(((AstInitializer) source).is_body()) {
				return this.get_ast_expression(((AstInitializer) source).get_body());
			}
			else {
				return this.get_ast_expression(((AstInitializer) source).get_expression());
			}
		}
		else if(source instanceof AstInitializerList || source instanceof AstArgumentList) {
			return this.get_ast_expression(source.get_parent());
		}
		else if(source instanceof AstExpression) {
			return (AstExpression) source;
		}
		else {
			return null;
		}
	}
	/**
	 * @param source
	 * @param cir_class
	 * @return the list of CirNode(s) corresponds to the ast-source.
	 */
	private List<CirNode>	get_cir_locations(AstNode source, Class<?> cir_class) throws Exception {
		if(this.tree == null) {
			throw new IllegalArgumentException("Invalid cir_tree: null");
		}
		else if(source == null) {
			throw new IllegalArgumentException("Invalid source as null");
		}
		else if(cir_class == null) {
			return this.tree.get_cir_tree().get_cir_nodes(source);
		}
		else {
			return this.tree.get_cir_tree().get_cir_nodes(source, cir_class);
		}
	}
	/**
	 * @param source
	 * @return the first statement enclosed in the source
	 * @throws Exception
	 */
	private	CirStatement	get_beg_statement(AstNode source) throws Exception {
		if(this.tree == null) {
			throw new IllegalArgumentException("Invalid cir_tree: null");
		}
		else if(source == null) {
			throw new IllegalArgumentException("Invalid source as null");
		}
		else {
			AstCirPair range;
			while(!this.tree.get_cir_tree().has_cir_range(source)) {
				source = source.get_parent();
			}
			range = this.tree.get_cir_tree().get_cir_range(source);
			return range.get_beg_statement();
		}
	}
	/**
	 * @param source
	 * @return the final statement enclosed in the source
	 * @throws Exception
	 */
	private	CirStatement	get_end_statement(AstNode source) throws Exception {
		if(this.tree == null) {
			throw new IllegalArgumentException("Invalid cir_tree: null");
		}
		else if(source == null) {
			throw new IllegalArgumentException("Invalid source as null");
		}
		else {
			AstCirPair range;
			while(!this.tree.get_cir_tree().has_cir_range(source)) {
				source = source.get_parent();
			}
			range = this.tree.get_cir_tree().get_cir_range(source);
			return range.get_end_statement();
		}
	}
	/**
	 * @param source
	 * @param cir_class
	 * @param k
	 * @return the kth location w.r.t. the source in given class or null if out-of-range
	 * @throws Exception
	 */
	private	CirNode			get_cir_location(AstNode source, Class<?> cir_class, int k) throws Exception {
		List<CirNode> cir_locations = this.get_cir_locations(source, cir_class);
		if(k < 0 || k >= cir_locations.size()) {
			return null;
		}
		else {
			return cir_locations.get(k);
		}
	}
	/**
	 * @param source
	 * @param cir_class
	 * @return it finds the expression w.r.t. the source of given class
	 * @throws Exception
	 */
	private	CirExpression	get_cir_expression(AstNode source, Class<?> cir_class) throws Exception {
		List<CirNode> cir_locations = this.get_cir_locations(source, cir_class);
		CirExpression expression = null;
		for(CirNode cir_location : cir_locations) {
			if(cir_location instanceof CirExpression) {
				expression = (CirExpression) cir_location;
				if(expression.execution_of() != null) break;
			}
		}
		return expression;
	}
	
	
	/* statements */
	private	AstContextNode	parse_statement(AstStatement source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else {
			throw new IllegalArgumentException(source.generate_code());
		}
	}
	private	AstContextNode	parse_break_statement(AstBreakStatement source) throws Exception {
		CirNode target = this.get_cir_location(source, CirGotoStatement.class, 0);
		return this.tree.new_node(source, target);
	}
	private	AstContextNode	parse_continue_statement(AstContinueStatement source) throws Exception {
		CirNode target = this.get_cir_location(source, CirGotoStatement.class, 0);
		return this.tree.new_node(source, target);
	}
	private	AstContextNode	parse_goto_statement(AstGotoStatement source) throws Exception {
		CirNode target = this.get_cir_location(source, CirGotoStatement.class, 0);
		return this.tree.new_node(source, target);
	}
	private	AstContextNode	parse_return_statement(AstReturnStatement source) throws Exception {
		CirNode target; AstContextNode parent;
		if(source.has_expression()) {
			AstContextNode lvalue = this.parse(source.get_return());
			AstContextNode rvalue = this.parse(source.get_expression());
			target = this.get_cir_location(source, CirReturnAssignStatement.class, 0);
			parent = this.tree.new_node(source, target);
			parent.add_child(AstContextLink.cdef, lvalue);
			parent.add_child(AstContextLink.expr, rvalue);
		}
		else {
			target = this.get_cir_location(source, CirGotoStatement.class, 0);
			parent = this.tree.new_node(source, target);
		}
		return parent;
	}
	private	AstContextNode	parse_decl_statement(AstDeclarationStatement source) throws Exception {
		return this.parse(source.get_declaration());
	}
	private	AstContextNode	parse_expr_statement(AstExpressionStatement source) throws Exception {
		if(source.has_expression()) {
			return this.parse(source.get_expression());
		}
		else {
			CirNode target = this.get_beg_statement(source);
			return this.tree.new_node(source, target);
		}
	}
	
	
	
	
	
	
	
	
	
	
}
