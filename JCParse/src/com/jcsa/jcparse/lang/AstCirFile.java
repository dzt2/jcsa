package com.jcsa.jcparse.lang;

import java.io.File;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCallGraph;
import com.jcsa.jcparse.lang.parse.CTranslate;
import com.jcsa.jcparse.lang.sym.SymNode;
import com.jcsa.jcparse.lang.sym.SymParser;
import com.jcsa.jcparse.lang.text.CText;

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
	/** used to parse to generate symbolic expression **/
	private SymParser sym_parser;
	
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
			this.sym_parser = new SymParser(this.template);
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
	 * @return C-intermediate representation
	 */
	public CirTree get_cir_tree() {
		return this.cir_tree;
	}
	/**
	 * @return the function calling graph
	 */
	public CirFunctionCallGraph get_function_call_graph() {
		return this.cir_tree.get_function_call_graph();
	}
	/**
	 * @param source
	 * @return parse from the AST node
	 * @throws Exception
	 */
	public SymNode sym_parse(AstNode source) throws Exception {
		return this.sym_parser.parse(source);
	}
	/**
	 * @param source
	 * @return parse from the CIR node
	 * @throws Exception
	 */
	public SymNode sym_parse(CirNode source) throws Exception {
		return this.sym_parser.parse(source);
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
