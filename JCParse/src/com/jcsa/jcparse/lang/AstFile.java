package com.jcsa.jcparse.lang;

import java.io.File;
import java.io.FileWriter;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.astree.code.AstNodeNormalizer;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.astree.unit.AstTranslationUnit;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.parse.CTranslate;
import com.jcsa.jcparse.lang.scope.CScope;
import com.jcsa.jcparse.lang.text.CText;

/**
 * Interfaces to access AST nodes among source file
 * @author yukimula
 */
public class AstFile {
	
	/** source file **/
	private AstTree ast_tree;
	private CirTree cir_tree;
	
	/* constructor */
	/**
	 * constructor
	 */
	private AstFile() { }
	
	/* getters */
	/**
	 * get the abstract syntax tree
	 * @return
	 */
	public AstTree get_ast_tree() { return this.ast_tree; }
	/**
	 * get the C-like intermediate representation tree
	 * @return
	 */
	public CirTree get_cir_tree() { return this.cir_tree; }
	/**
	 * get the source file
	 * @return
	 */
	public File get_source() {return ast_tree.get_source_file();}
	/**
	 * get the code text
	 * @return
	 */
	public CText get_code() {return ast_tree.get_source_code();}
	/**
	 * get the AST root
	 * @return
	 */
	public AstTranslationUnit get_ast_root() {return ast_tree.get_ast_root();}
	/**
	 * get the scope of source file
	 * @return
	 */
	public CScope get_file_scope() {return ast_tree.get_ast_root().get_scope();}
	
	/* function and ASTNode locator */
	/**
	 * get the function where node is located 
	 * @param node
	 * @return
	 */
	public AstFunctionDefinition function_of(AstNode node) {
		while((node != null) && !(node 
				instanceof AstFunctionDefinition)) {
			node = node.get_parent();
		}
		return (AstFunctionDefinition) node;
	}
	/**
	 * get the AST-node based on its unique key
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public AstNode get_ast_node(int key) throws Exception {
		return ast_tree.get_node(key);
	}
	
	/* translation method */
	/**
	 * write the normalized code of the AstFile onto the specified target C file. 
	 * @param target_cfile
	 * @throws Exception
	 */
	public void write(File target_cfile) throws Exception {
		if(target_cfile == null)
			throw new IllegalArgumentException("No file path specified");
		else {
			FileWriter writer = new FileWriter(target_cfile);
			writer.write(AstNodeNormalizer.normalizer.normalize(this.get_ast_root()));
			writer.close();
		}
	}
	
	/* factory method */
	/**
	 * parse the source code in specified file to AST
	 * @param source
	 * @param std
	 * @param csize
	 * @return
	 * @throws Exception
	 */
	public static AstFile parse(File source, ClangStandard std) throws Exception {
		AstFile astfile = new AstFile(); 							// constructor 
		astfile.ast_tree = CTranslate.parse(source, std);
		astfile.cir_tree = CTranslate.parse(astfile.ast_tree);
		return astfile;
		/*
		astfile.source = source;										// source file 
		astfile.code = CTranslate.get_source_text(source);				// source code
		astfile.astroot = CTranslate.get_ast_root(source, std);			// AST roots
		CTranslate.derive_types_for(
				astfile.astroot, CSizeofBase.sizeof_base);				// derive types 
		astfile.graph = AstFunctionGraph.parse(astfile);				// derive function-graph
		astfile.cfg = CFGraph.control_flow_graph(astfile.graph);		// derive control-flow-graph
		astfile.update_index(); return astfile;							// update the index and return
		*/
	}
	
}
