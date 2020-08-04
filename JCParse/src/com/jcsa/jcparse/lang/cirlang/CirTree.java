package com.jcsa.jcparse.lang.cirlang;

import com.jcsa.jcparse.lang.cirlang.unit.CirTranslationUnit;

/**
 * The syntax tree of C-intermediate representation language.
 * @author yukimula
 *
 */
public interface CirTree {
	
	/**
	 * @return the translation unit as the root of the tree
	 */
	public CirTranslationUnit get_cir_root();
	
	/**
	 * @return the set of cir-nodes in the tree
	 */
	public Iterable<CirNode> get_nodes();
	
	/**
	 * @return the number of cir-nodes under the tree
	 */
	public int number_of_nodes();
	
	/**
	 * @param k
	 * @return the cir-node w.r.t. the specified index
	 * @throws IndexOutOfBoundsException
	 */
	public CirNode get_node(int k) throws IndexOutOfBoundsException;
	
	/* factory methods */
	
}
