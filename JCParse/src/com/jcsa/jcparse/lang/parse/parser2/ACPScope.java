package com.jcsa.jcparse.lang.parse.parser2;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcparse.lang.astree.AstNode;

/**
 * The scope in Abstract syntactic tree to C-like intermediate representation Parsing is a hierarchical structure
 * in which the labels_target are referred.
 * @author yukimula
 *
 */
class ACPScope {
	
	/* common defined label name */
	protected static final String BREAK_LABEL 		= "break";
	protected static final String CONTINUE_LABEL 	= "continue";
	protected static final String CASE_LABEL		= "case";
	protected static final String RETURN_LABEL	 	= "return";
	
	/* definitions and constructor */
	private ACPScope parent;
	private AstNode ast_key;
	private Map<String, ACPLabelsTarget> labels;
	protected ACPScope(AstNode ast_key) throws IllegalArgumentException {
		if(ast_key == null)
			throw new IllegalArgumentException("invalid ast_key as null");
		else {
			this.parent = null; this.ast_key = ast_key;
			this.labels = new HashMap<String, ACPLabelsTarget>();
		}
	}
	private ACPScope(ACPScope parent, AstNode ast_key) throws IllegalArgumentException {
		if(parent == null)
			throw new IllegalArgumentException("invalid parent: null");
		else if(ast_key == null)
			throw new IllegalArgumentException("invalid ast_key as null");
		else {
			this.parent = parent; this.ast_key = ast_key;
			this.labels = new HashMap<String, ACPLabelsTarget>();
		}
	}
	
	/* getters */
	/**
	 * get the AST node as the key referring to this scope
	 * @return
	 */
	public AstNode get_ast_key() { return this.ast_key; }
	/**
	 * whether there is a label-target instance in the scope or its path to the root.
	 * @param label_name
	 * @return
	 */
	public boolean has_labels_target(String label_name) {
		ACPScope scope = this;
		while(scope != null) {
			if(scope.labels.containsKey(label_name))
				return true;
			else scope = scope.parent;
		}
		return false;
	}
	/**
	 * get the label-target instance from the current scope or its path to the root.
	 * @param label_name
	 * @return
	 * @throws IllegalAccessException
	 */
	public ACPLabelsTarget get_labels_target(String label_name) throws IllegalAccessException {
		ACPScope scope = this;
		while(scope != null) {
			if(scope.labels.containsKey(label_name))
				return scope.labels.get(label_name);
			else scope = scope.parent;
		}
		throw new IllegalAccessException("Undefined label name: " + label_name);
	}
	/**
	 * create a label-target instance in current scope table
	 * @param label_name
	 * @return
	 * @throws IllegalArgumentException
	 */
	public ACPLabelsTarget new_labels_target(String label_name) throws IllegalArgumentException, IllegalAccessException {
		if(!this.labels.containsKey(label_name)) {
			this.labels.put(label_name, new ACPLabelsTarget(this, label_name));
		}
		return this.labels.get(label_name);
	}
	/**
	 * create a scope defined based on the range of the current scope
	 * @param ast_key
	 * @return
	 * @throws IllegalArgumentException
	 */
	protected ACPScope new_child_scope(AstNode ast_key) throws IllegalArgumentException {
		return new ACPScope(this, ast_key);
	}
	/**
	 * delete a labels-target instance from the current scope
	 * @param label_name
	 * @throws IllegalArgumentException when no instance corresponding to the given name
	 */
	public void del_labels_target(String label_name) throws IllegalArgumentException {
		if(this.labels.containsKey(label_name)) this.labels.remove(label_name);
		else throw new IllegalArgumentException("undefined: " + label_name);
	}
	
}
