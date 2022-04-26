package com.jcsa.jcparse.lang.program;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.astree.decl.AstDeclaration;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator.DeclaratorProduction;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclaratorList;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstFieldInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerList;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArgumentList;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstParanthExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstStatementList;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.astree.unit.AstTranslationUnit;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCallGraph;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;
import com.jcsa.jcparse.lang.irlang.unit.CirTransitionUnit;
import com.jcsa.jcparse.lang.text.CText;
import com.jcsa.jcparse.parse.CTranslate;

public class AstCirTree {
	
	/* attributes */
	private	CRunTemplate				template;
	private	AstTree						ast_tree;
	private	CirTree						cir_tree;
	private	List<AstCirNode>			nodes;
	private	Map<AstNode, AstCirNode>	index;
	
	/* constructor */
	private	AstCirTree(CRunTemplate template, AstTree ast_tree, CirTree cir_tree) throws Exception {
		if(template == null) {
			throw new IllegalArgumentException("Invalid template: null");
		}
		else if(ast_tree == null) {
			throw new IllegalArgumentException("Invalid ast_tree: null");
		}
		else if(cir_tree == null) {
			throw new IllegalArgumentException("Invalid cir_tree: null");
		}
		else {
			this.template = template;
			this.ast_tree = ast_tree;
			this.cir_tree = cir_tree;
			this.nodes = new ArrayList<AstCirNode>();
			this.index = new HashMap<AstNode, AstCirNode>();
			// TODO implement the parsing
			AstCirTreeParser.parse(this);
			AstCirTreeParser.link(this);
		}
	}
	public static AstCirTree parse(File cfile, File template_file, ClangStandard standard) throws Exception {
		CRunTemplate template = new CRunTemplate(template_file);
		AstTree ast_tree = CTranslate.parse(cfile, standard, template);
		CirTree cir_tree = CTranslate.parse(ast_tree, template);
		return new AstCirTree(template, ast_tree, cir_tree);
	}
	public static AstCirTree load(AstTree ast_tree, CirTree cir_tree, CRunTemplate template) throws Exception {
		return new AstCirTree(template, ast_tree, cir_tree);
	}
	
	/* source code */
	/**
	 * @return	the source file from which the syntax tree is parsed.
	 */
	public	File					get_source_file()		{ return this.ast_tree.get_source_file(); }
	/**
	 * @return	the syntactic index for localize the source code file
	 */
	public	CText					get_source_code()		{ return this.ast_tree.get_source_code(); }
	/**
	 * @return	the template to support sizeof computation
	 */
	public	CRunTemplate			get_sizeof_template()	{ return this.template; }
	
	/* syntax tree */
	/**
	 * @return	the ast_tree
	 */
	public	AstTree					get_ast_tree()			{ return this.ast_tree; }
	/**
	 * @return the root node of abstract syntax tree
	 */
	public AstTranslationUnit get_ast_root() {
		return this.ast_tree.get_ast_root();
	}
	/**
	 * @return number of nodes in abstract syntax tree
	 */
	public int number_of_ast_nodes() {
		return this.ast_tree.number_of_nodes();
	}
	/**
	 * @param id
	 * @return get the abstract syntax node w.r.t. specified id
	 * @throws IndexOutOfBoundsException
	 */
	public AstNode get_ast_node(int id) throws IndexOutOfBoundsException {
		return this.ast_tree.get_node(id);
	}
	/**
	 * @param source
	 * @return the function definition in AST where the source belongs to
	 * 		   or null if the source is one of the external unit.
	 */
	public AstFunctionDefinition get_ast_function_definition(AstNode source) {
		while(source != null) {
			if(source instanceof AstFunctionDefinition)
				return (AstFunctionDefinition) source;
			else
				source = source.get_parent();
		}
		return null;
	}
	/**
	 * @return the function definition of the main() in AST
	 * @throws Exception
	 */
	public AstFunctionDefinition get_main_ast_function() throws Exception {
		return this.ast_tree.get_main_function();
	}
	
	/* intermediate */
	/**
	 * @return 	C-intermediate code tree
	 */
	public	CirTree					get_cir_tree()			{ return this.cir_tree; }
	/**
	 * @return the root of C-intermediate representation
	 */
	public CirTransitionUnit get_cir_root() {
		return this.cir_tree.get_root();
	}
	/**
	 * @return the number of nodes in C-intermediate representation.
	 */
	public int number_of_cir_nodes() {
		return this.cir_tree.size();
	}
	/**
	 * @param id
	 * @return the node in C-intermediate representation w.r.t. specified id
	 * @throws IndexOutOfBoundsException
	 */
	public CirNode get_cir_node(int id) throws IndexOutOfBoundsException {
		return this.cir_tree.get_node(id);
	}
	/**
	 * @param source
	 * @return the definition of function in C-intermediate representation
	 * 		   where the source node is created
	 */
	public CirFunctionDefinition get_cir_function_definition(CirNode source) {
		while(source != null) {
			if(source instanceof CirFunctionDefinition)
				return (CirFunctionDefinition) source;
			else
				source = source.get_parent();
		}
		return null;
	}
	/**
	 * @param source
	 * @return the statement where the source is defined or null
	 *  		if it does not belong to any statements.
	 */
	public CirStatement get_cir_statement(CirNode source) {
		while(source != null) {
			if(source instanceof CirStatement) {
				return (CirStatement) source;
			}
			else {
				source = source.get_parent();
			}
		}
		return null;
	}
	
	/* control flow */
	/**
	 * @return	the function calling graph for structural flow graph
	 */
	public	CirFunctionCallGraph	get_function_graph()	{ return this.cir_tree.get_function_call_graph(); }
	/**
	 * @param source
	 * @return the function in calling graph where the source belongs to
	 *   		or null if the source does not belong to any definition.
	 */
	public CirFunction get_cir_function(CirNode source) {
		return this.get_function_graph().get_function(source);
	}
	/**
	 * @param source
	 * @return the execution node in graph where the source belongs to
	 */
	public CirExecution get_cir_execution(CirNode source) {
		CirStatement statement = this.get_cir_statement(source);
		if(statement != null) {
			CirFunction function = this.get_cir_function(statement);
			return function.get_flow_graph().get_execution(statement);
		}
		else {
			return null;
		}
	}
	/**
	 * @return the cir-function of main() or null
	 * @throws Exception
	 */
	public CirFunction get_cir_main_function() throws Exception {
		return this.cir_tree.get_function_call_graph().get_main_function();
	}
	
	/* tree getters */
	/**
	 * @return	the number of nodes created under this tree
	 */
	public	int						number_of_tree_nodes()	{ return this.nodes.size(); }
	/**	
	 * @return 	the set of tree nodes
	 */
	public	Iterable<AstCirNode>	get_tree_nodes()		{ return this.nodes; }
	/**
	 * @param k
	 * @return 	the tree node w.r.t. the input ID
	 * @throws IndexOutOfBoundsException
	 */
	public	AstCirNode				get_tree_node(int k) throws IndexOutOfBoundsException { return this.nodes.get(k); }
	/**
	 * @param source
	 * @return it finds the real source of the input
	 */
	private	AstNode					find_ast_source(AstNode source) {
		while(source != null) {
			if(source instanceof AstParanthExpression) {
				source = ((AstParanthExpression) source).get_sub_expression();
			}
			else if(source instanceof AstConstExpression) {
				source = ((AstConstExpression) source).get_expression();
			}
			else if(source instanceof AstDeclarator) {
				if(((AstDeclarator) source).get_production() == DeclaratorProduction.identifier) {
					source = ((AstDeclarator) source).get_identifier();
				}
				else {
					source = ((AstDeclarator) source).get_declarator();
				}
			}
			else if(source instanceof AstInitDeclaratorList) { source = source.get_parent(); }
			else if(source instanceof AstDeclaration) 	{ source = source.get_parent(); }
			else if(source instanceof AstArgumentList) 	{ source = source.get_parent(); }
			else if(source instanceof AstStatementList)	{ source = source.get_parent(); }
			else if(source instanceof AstInitializer) {
				if(((AstInitializer) source).is_body()) {
					source = ((AstInitializer) source).get_body();
				}
				else {
					source = ((AstInitializer) source).get_expression();
				}
			}
			else if(source instanceof AstFieldInitializer) {
				source = ((AstFieldInitializer) source).get_initializer();
			}
			else if(source instanceof AstInitializerList) { source = source.get_parent(); }
			else {
				break;
			}
		}
		return source;
	}
	/**
	 * @param source
	 * @return whether there exists node w.r.t. the input source
	 */
	public	boolean					has_tree_node(AstNode source) {
		source = this.find_ast_source(source);
		if(source == null) {
			return false;
		}
		else {
			return this.index.containsKey(source);
		}
	}
	/**
	 * @param source
	 * @return	the tree node w.r.t. the AST-source
	 * @throws IllegalArgumentException	if undefined
	 */
	public	AstCirNode				get_tree_node(AstNode source) throws IllegalArgumentException {
		source = this.find_ast_source(source);
		if(source == null || !this.index.containsKey(source)) {
			throw new IllegalArgumentException("Undefined: " + source);
		}
		else {
			return this.index.get(source);
		}
	}
	/**
	 * @param source
	 * @return it creates a new node w.r.t. the source
	 * @throws IllegalArgumentException
	 */
	protected	AstCirNode			new_tree_node(AstNode source, Object token) throws IllegalArgumentException {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(this.index.containsKey(source)) {
			throw new IllegalArgumentException("Duplication arises");
		}
		else {
			AstCirNode node = new AstCirNode(this, this.nodes.size(), source, token);
			this.nodes.add(node); this.index.put(source, node); return node;
		}
	}
	
}
