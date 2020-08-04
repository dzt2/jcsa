package com.jcsa.jcparse.lang.astree;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator.DeclaratorProduction;
import com.jcsa.jcparse.lang.astree.impl.AstNodeImpl;
import com.jcsa.jcparse.lang.astree.unit.AstExternalUnit;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.astree.unit.AstTranslationUnit;
import com.jcsa.jcparse.lang.text.CText;
import com.jcsa.jcparse.parse.code.CodeGeneration;

/**
 * The abstract syntax tree.
 * 
 * @author yukimula
 *
 */
public class AstTree {
	
	private File source_file;
	private CText source_code;
	private List<AstNode> nodes;
	
	public AstTree(File source_file, CText source_code, AstTranslationUnit ast_root) throws Exception {
		if(source_file == null || !source_file.exists())
			throw new IllegalArgumentException("Undefined source_file as null");
		else if(source_code == null)
			throw new IllegalArgumentException("Undefined source_code as null");
		else if(ast_root == null)
			throw new IllegalArgumentException("Undefined ast_root as null");
		else {
			this.source_file = source_file;
			this.source_code = source_code;
			this.set_ast_root(ast_root);
		}
	}
	private void set_ast_root(AstTranslationUnit ast_root) throws Exception {
		if(ast_root == null)
			throw new IllegalArgumentException("invalid ast_root");
		else {
			this.nodes = new ArrayList<AstNode>();
			Queue<AstNode> queue = new LinkedList<AstNode>();
			
			queue.add(ast_root);
			while(!queue.isEmpty()) {
				AstNode node = queue.poll();
				((AstNodeImpl) node).set_tree(this);;
				
				node.set_key(this.nodes.size());
				this.nodes.add(node);
				
				for(int k = 0; k < node.number_of_children(); k++) {
					AstNode child = node.get_child(k);
					if(child != null) {
						queue.add(child);
					}
				}
			}
		}
	}
	
	/* getters */
	/**
	 * get the source code file used to generate the abstract syntax
	 * @return
	 */
	public File get_source_file() { return this.source_file; }
	/**
	 * get the source code text.
	 * @return
	 */
	public CText get_source_code() { return this.source_code; }
	/**
	 * get the root of the abstract syntax tree
	 * @return
	 */
	public AstTranslationUnit get_ast_root() { return (AstTranslationUnit) this.nodes.get(0); }
	/**
	 * get the number of nodes in the tree
	 * @return
	 */
	public int number_of_nodes() { return this.nodes.size(); }
	/**
	 * get the node in the tree with respect to its integer ID in it.
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public AstNode get_node(int k) throws IndexOutOfBoundsException { return nodes.get(k); }
	/**
	 * @param node
	 * @return the function definition of the node
	 */
	public AstFunctionDefinition function_of(AstNode node) {
		while(node != null && 
				!(node instanceof AstFunctionDefinition)) {
			node = node.get_parent();
		}
		return (AstFunctionDefinition) node;
	}
	/**
	 * @return the function definition node of main(); or null if not defined
	 */
	public AstFunctionDefinition get_main_function() throws Exception {
		return this.get_function("main");
	}
	/**
	 * @param name
	 * @return the function with respect to the specified name or null;
	 * @throws Exception
	 */
	public AstFunctionDefinition get_function(String name) throws Exception {
		AstTranslationUnit root = this.get_ast_root();
		for(int k = 0; k < root.number_of_units(); k++) {
			AstExternalUnit unit = root.get_unit(k);
			if(unit instanceof AstFunctionDefinition) {
				AstDeclarator declarator = ((AstFunctionDefinition) unit).get_declarator();
				
				while(declarator.get_production() != DeclaratorProduction.identifier) {
					declarator = declarator.get_declarator();
				}
				
				if(declarator.get_identifier().get_name().strip().equals(name.strip())) {
					return (AstFunctionDefinition) unit;
				}
			}
		}
		return null;
	}
	
	/* code generator */
	/**
	 * generate the (normalized code) of this code file based on its AST structure
	 * @param normalized
	 * @param cfile
	 * @throws Exception
	 */
	public void generate(boolean normalized, File cfile) throws Exception {
		String code;
		if(normalized) {
			throw new RuntimeException("Nor supported");
		}
		else {
			code = CodeGeneration.generate_code(this.get_ast_root());
		}
		
		FileWriter writer = new FileWriter(cfile);
		writer.write(code);
		writer.close();
	}
	
}
