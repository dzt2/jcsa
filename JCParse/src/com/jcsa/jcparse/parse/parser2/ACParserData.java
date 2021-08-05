package com.jcsa.jcparse.parse.parser2;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.unit.AstTranslationUnit;
import com.jcsa.jcparse.lang.irlang.impl.CirTreeImpl;

/**
 * It contains all the output informations generated from the abstract syntax tree to C-like intermediate representation parsing, including:<br>
 * 	(1) ACPModule for each function definition.<br>
 * 	(2) ACPModule for declarations in transition unit.<br>
 * 	(3) The solutions with range and result information for each AstNode to build their AstCirPair.<br>
 * 	(4) The sequence of statements being created for building up the the flow graph and function body.<br>
 *
 * @author yukimula
 *
 */
public class ACParserData {

	/* definitions & constructor */
	private AstTranslationUnit ast_root;
	private CirTreeImpl cir_tree;
	private Map<AstNode, ACPModule> modules;
	protected ACParserData(AstTranslationUnit ast_root) throws IllegalArgumentException {
		if(ast_root == null)
			throw new IllegalArgumentException("invalid ast-root as null");
		else {
			this.cir_tree = new CirTreeImpl(ast_root);
			this.modules = new HashMap<>();
		}
	}

	/* getters */
	/**
	 * get the root of the syntactic tree for being parsed
	 * @return
	 */
	public AstTranslationUnit get_ast_root() { return this.ast_root; }
	/**
	 * get the syntax tree in C-like intermediate representation for being built up.
	 * @return
	 */
	public CirTreeImpl get_cir_tree() { return this.cir_tree; }
	/**
	 * get the module for solving the nodes within the range of the AST source node.
	 * @param ast_source
	 * @return
	 * @throws IllegalArgumentException
	 */
	public ACPModule get_parsing_module(AstNode ast_source) throws IllegalArgumentException {
		if(ast_source == null)
			throw new IllegalArgumentException("invalid ast_source: null");
		else {
			if(!this.modules.containsKey(ast_source))
				this.modules.put(ast_source, new ACPModule(ast_source));
			return this.modules.get(ast_source);
		}
	}

}
