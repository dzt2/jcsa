package com.jcsa.jcparse.lang.astree.impl;

import com.jcsa.jcparse.lang.CSyntaxElmImpl;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.code.AstCodeGenerator;
import com.jcsa.jcparse.lang.code.AstNodeNormalizer;
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
	public String get_code() {
		return this.get_code(false);
	}
	
	@Override
	public String get_code(boolean heading) {
		CText source_code = this.tree.get_source_code();
		StringBuilder buffer = new StringBuilder();
		
		try {
			if(heading) {
				String class_name = this.getClass().getSimpleName();
				class_name = class_name.substring(3, class_name.length() - 4);
				int line = source_code.line_of(this.location.get_bias());
				buffer.append(class_name + "[" + line + "]:\n");
			}
			buffer.append(source_code.substring(this.location.get_bias(), 
					this.location.get_bias() + this.location.get_length()));
		}
		catch(Exception ex) {
			ex.printStackTrace();
			buffer.append("##Error Occurs##");
		}
		
		return buffer.toString();
	}
	
	@Override
	public String generate_code() {
		return this.generate_code(false);
	}
	
	@Override
	public String generate_code(boolean normalized) {
		try {
			if(normalized) {
				return AstNodeNormalizer.normalize(this);
			}
			else {
				return AstCodeGenerator.generate_code(this);
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public void set_tree(AstTree tree) throws Exception {
		if(tree != null) this.tree = tree;
		else throw new IllegalArgumentException("invalid tree as null");
	}
}
