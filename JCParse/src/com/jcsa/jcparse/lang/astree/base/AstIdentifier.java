package com.jcsa.jcparse.lang.astree.base;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.scope.CName;

/**
 * <code>identifier |--> IdExpression, Field, Label, Name, Macro</code>
 * 
 * @author yukimula
 */
public interface AstIdentifier extends AstNode {
	/**
	 * get the name of this identifier
	 * 
	 * @return
	 */
	public String get_name();

	/**
	 * get the cname for this node
	 * 
	 * @return
	 */
	public CName get_cname();

	/**
	 * set the cname for this identifier
	 * 
	 * @param cname
	 */
	public void set_cname(CName cname);
}
