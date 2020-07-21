package com.jcsa.jcparse.lang.astree.impl;

import com.jcsa.jcparse.lang.CSyntaxElmImpl;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.code.CodeGeneration;
import com.jcsa.jcparse.lang.text.CText;

/**
 * abstract node for all AstNode subclasses
 * 
 * @author yukimula
 *
 */
public abstract class AstNodeImpl extends CSyntaxElmImpl implements AstNode {
	
	private AstTree tree;
	protected AstNode parent;
	protected int key;

	protected AstNodeImpl() {
		this.tree = null;
		this.parent = null; 
		this.key = AstNode.UNDEFINED_KEY;
	}
	
	@Override
	public AstTree get_tree() { return tree; }
	@Override
	public int get_key() { return key; }
	@Override
	public void set_key(int key) {
		this.key = key;
	}
	
	@Override
	public AstNode get_parent() {
		return parent;
	}

	/**
	 * set parent according to this method, fails in one of following cases:<br>
	 * 1. <i>this.parent</i> has been set; <br>
	 * 2. <i>parent</i> is <b>null</b> itself. <br>
	 * 
	 * @param parent
	 */
	protected void set_parent(AstNode parent) throws Exception {
		if (parent == null)
			throw new IllegalArgumentException("Invalid parent: null");
		else if (this.parent != null)
			throw new IllegalArgumentException("Invalid access: duplicated parent");
		else
			this.parent = parent;
	}
	
	@Override
	public String get_code() throws Exception {
		CText source_code = this.tree.get_source_code();
		return source_code.substring(this.location.get_bias(), 
					this.location.get_bias() + this.location.get_length());
	}
	
	@Override
	public String generate_code() throws Exception {
		return CodeGeneration.generate_code(this);
	}
	
	public void set_tree(AstTree tree) throws Exception {
		if(tree != null) this.tree = tree;
		else throw new IllegalArgumentException("invalid tree as null");
	}
}
