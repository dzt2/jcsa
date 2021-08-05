package com.jcsa.jcparse.lang.astree.decl.initializer;

import com.jcsa.jcparse.lang.astree.AstNode;

/**
 * <code>(designator)+</code>
 *
 * @author yukimula
 *
 */
public interface AstDesignatorList extends AstNode {
	public int number_of_designators();

	public AstDesignator get_designator(int k);

	public void append_designator(AstDesignator d) throws Exception;
}
