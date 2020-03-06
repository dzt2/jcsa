package com.jcsa.jcparse.lang.astree.decl.initializer;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;

/**
 * <code>(designator_list =)? initializer</code>
 * 
 * @author yukimula
 */
public interface AstFieldInitializer extends AstNode {
	public boolean has_designator_list();

	public AstDesignatorList get_designator_list();

	public AstPunctuator get_assign();

	public AstInitializer get_initializer();
}
