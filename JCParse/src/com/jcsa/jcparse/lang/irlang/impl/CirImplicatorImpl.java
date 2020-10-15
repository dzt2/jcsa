package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirImplicator;

/**
 * Implicitly declared variable is defined as #{AST-Source-Node-ID}
 * @author yukimula
 *
 */
public class CirImplicatorImpl extends CirExpressionImpl implements CirImplicator {
	
	private String name;
	protected CirImplicatorImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id, true);
		this.name = null;
	}
	protected CirImplicatorImpl(CirTree tree, int node_id, String name) throws IllegalArgumentException {
		super(tree, node_id, true);
		this.name = name;
	}
	
	@Override
	public String get_name() { 
		if(name == null)
			return "#" + this.get_ast_source().get_key(); 
		else return name;
	}
	@Override
	public String get_unique_name() { return "#" + this.get_ast_source().get_key(); }
	
}
