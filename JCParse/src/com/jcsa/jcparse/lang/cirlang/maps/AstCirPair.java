package com.jcsa.jcparse.lang.cirlang.maps;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.cirlang.CirNode;

/**
 * The pair of AstNode and CirNode.
 * 
 * @author yukimula
 *
 */
public interface AstCirPair {
	
	public AstCirPairType get_type();
	
	public AstNode get_ast_node();
	
	public CirNode get_cir_node();
	
}
