package com.jcsa.jcparse.lang;

import java.io.File;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
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

/**
 * It manages the data model of source code in C programming language.
 *
 * @author yukimula
 *
 */
public class AstCirFile {

	/* attributes */
	/** the source code file **/
	private File source_code_file;
	/** template for executing C programs **/
	private CRunTemplate template;
	/** abstract syntax tree **/
	private AstTree ast_tree;
	/** C-intermediate representation **/
	private CirTree cir_tree;
	/** C-function calling graph **/
	private CirFunctionCallGraph call_graph;

	/* constructor */
	/**
	 * create AST-CIR file w.r.t. source code file under the template
	 * @param source_file
	 * @param template_file
	 * @throws Exception
	 */
	private AstCirFile(File source_file, File template_file, ClangStandard standard) throws Exception {
		if(source_file == null || !source_file.exists())
			throw new IllegalArgumentException("Invalid source");
		else if(template_file == null)
			throw new IllegalArgumentException("Invalid template");
		else {
			this.source_code_file = source_file;
			this.template = new CRunTemplate(template_file);
			this.ast_tree = CTranslate.parse(source_file, standard, template);
			this.cir_tree = CTranslate.parse(this.ast_tree, template);
			this.call_graph = this.cir_tree.get_function_call_graph();
		}
	}

	/* getters */
	/**
	 * @return source code file
	 */
	public File get_source_file() {
		return this.source_code_file;
	}
	/**
	 * @return template to build up execution environment
	 */
	public CRunTemplate get_run_template() {
		return this.template;
	}
	/**
	 * @return the source code of the C file
	 */
	public CText get_source_code() {
		return this.ast_tree.get_source_code();
	}
	/**
	 * @return abstract syntax tree
	 */
	public AstTree get_ast_tree() {
		return this.ast_tree;
	}
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
	/**
	 * @return C-intermediate representation
	 */
	public CirTree get_cir_tree() {
		return this.cir_tree;
	}
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
	/**
	 * @return the function calling graph
	 */
	public CirFunctionCallGraph get_function_call_graph() {
		return this.call_graph;
	}
	/**
	 * @param source
	 * @return the function in calling graph where the source belongs to
	 *   		or null if the source does not belong to any definition.
	 */
	public CirFunction get_cir_function(CirNode source) {
		return this.call_graph.get_function(source);
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

	/* factory method */
	/**
	 * @param source_file
	 * @param template_file
	 * @param standard
	 * @return
	 * @throws Exception
	 */
	public static AstCirFile parse(File source_file, File template_file, ClangStandard standard) throws Exception {
		return new AstCirFile(source_file, template_file, standard);
	}

}
