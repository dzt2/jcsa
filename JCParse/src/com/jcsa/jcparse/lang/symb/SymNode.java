package com.jcsa.jcparse.lang.symb;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * The structural description of symbolic node.<br>
 * 	|-- Expression	(for structural and description)<br>
 * 	|--	ArgumentList or Field<br>
 * @author yukimula
 *
 */
public abstract class SymNode {
	
	/* attributes */
	/** the parent to which the node is defined **/
	private SymNode parent;
	/** the index of this node as the child of its parent or -1 if parent is null **/
	private int index;
	/** the children under the node **/
	private List<SymNode> children;
	
	/* constructor */
	/**
	 * create an isolated symbolic node in graph
	 */
	protected SymNode() {
		this.parent = null;	/* undefined parent */
		this.index = -1;	/* undefined child */
		this.children = new LinkedList<SymNode>();
	}
	
	/* getters */
	/**
	 * whether the node is root without any parent
	 * @return
	 */
	public boolean is_root() { return this.parent != null; }
	/**
	 * whether the node is leaf without any children
	 * @return
	 */
	public boolean is_leaf() { return this.children.isEmpty(); }
	/**
	 * get the parent of this node
	 * @return
	 */
	public SymNode get_parent() { return this.parent; }
	/**
	 * get the index of the node as child of its parent or -1 if it does not have a parent
	 * @return
	 */
	public int get_child_index() { return this.index; }
	/**
	 * get the number of children under this node
	 * @return
	 */
	public int number_of_children() { return this.children.size(); }
	/**
	 * get the children of this node
	 * @return
	 */
	public Iterable<SymNode> get_children() { return this.children; }
	/**
	 * get the kth child under the node
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public SymNode get_child(int k) throws IndexOutOfBoundsException { return this.children.get(k); }
	
	/* setters */
	/**
	 * add an isolated child under this parent
	 * @param child
	 * @throws IllegalArgumentException
	 */
	protected void add_child(SymNode child) throws IllegalArgumentException {
		if(child == null)
			throw new IllegalArgumentException("Unable to add: " + child);
		else if(child.parent != null)
			throw new IllegalArgumentException("Duplicated parent: " + child);
		else { 
			child.parent = this; 
			child.index = this.children.size(); 
			this.children.add(child);
		}
	}
	/**
	 * get the copy of this node
	 * @return
	 */
	public SymNode copy() {
		if(this instanceof SymAddress) {
			SymAddress node = (SymAddress) this;
			return new SymAddress(node.get_data_type(), node.get_address());
		}
		else if(this instanceof SymConstant) {
			SymConstant node = (SymConstant) this;
			return new SymConstant(node.get_constant());
		}
		else if(this instanceof SymLiteral) {
			SymLiteral node = (SymLiteral) this;
			return new SymLiteral(node.get_data_type(), node.get_literal());
		}
		else if(this instanceof SymDefaultValue) {
			SymDefaultValue node = (SymDefaultValue) this;
			return new SymDefaultValue(node.get_data_type());
		}
		else if(this instanceof SymUnaryExpression) {
			SymUnaryExpression node = (SymUnaryExpression) this;
			return new SymUnaryExpression(
					node.get_data_type(), 
					node.get_operator(), 
					(SymExpression) node.get_operand().copy());
		}
		else if(this instanceof SymBinaryExpression) {
			SymBinaryExpression node = (SymBinaryExpression) this;
			return new SymBinaryExpression(
					node.get_data_type(),
					node.get_operator(),
					(SymExpression) node.get_loperand().copy(),
					(SymExpression) node.get_roperand().copy());
		}
		else if(this instanceof SymMultiExpression) {
			SymMultiExpression node = (SymMultiExpression) this;
			SymMultiExpression copy = new SymMultiExpression(node.get_data_type(), node.get_operator());
			for(int k = 0; k < node.number_of_operands(); k++) {
				copy.add_operand((SymExpression) node.get_operand(k).copy());
			}
			return copy;
		}
		else if(this instanceof SymFieldExpression) {
			SymFieldExpression node = (SymFieldExpression) this;
			return new SymFieldExpression(
					node.get_data_type(),
					(SymExpression) node.get_body().copy(),
					node.get_field().get_name()
					);
		}
		else if(this instanceof SymInvocateExpression) {
			SymInvocateExpression node = (SymInvocateExpression) this;
			SymInvocateExpression copy = new SymInvocateExpression(
					node.get_data_type(), 
					(SymExpression) node.get_function().copy());
			SymArgumentList old_list = node.get_argument_list();
			SymArgumentList new_list = copy.get_argument_list();
			for(int k = 0; k < old_list.number_of_arguments(); k++)
				new_list.add_argument((SymExpression) old_list.get_argument(k).copy());
			return copy;
		}
		else if(this instanceof SymSequenceExpression) {
			SymSequenceExpression node = (SymSequenceExpression) this;
			SymSequenceExpression copy = new SymSequenceExpression();
			for(int k = 0; k < node.number_of_elements(); k++) {
				copy.add_element((SymExpression) node.get_element(k).copy());
			}
			return copy;
		}
		/*
		else if(this instanceof SymField) {
			SymField node = (SymField) this;
			return new SymField(node.get_name());
		}
		else if(this instanceof SymArgumentList) {
			
		}
		*/
		else {
			throw new IllegalArgumentException("Unsupport: " + this.getClass().getSimpleName());
		}
	}
	/**
	 * set the kth child in the node
	 * @param k
	 * @param child
	 * @throws IllegalArgumentException
	 */
	protected void set_child(int k, SymNode child) throws IllegalArgumentException {
		if(k < 0 || k >= this.children.size())
			throw new IllegalArgumentException("Invalid index: " + k);
		else if(child == null || child.parent != null)
			throw new IllegalArgumentException("Invalid child: null");
		else {
			SymNode old_child = this.children.get(k);
			old_child.parent = null; 
			old_child.index = -1;
			child.parent = this;
			child.index = k;
			this.children.set(k, child);
		}
	}
	/**
	 * remove all the children under the tree
	 */
	protected void clear_children() {
		for(SymNode child : this.children) {
			child.parent = null; 
			child.index = -1;
		}
		this.children.clear();
	}
	
	/* generate */
	/**
	 * get the root of tree in which this node is defined
	 * @return
	 */
	public SymNode get_root() {
		SymNode root = this;
		while(root.parent != null)
			root = root.parent;
		return root;
	}
	/**
	 * whether the node is a reference
	 * @return
	 */
	public boolean is_reference() {
		if(this instanceof SymUnaryExpression) {
			SymUnaryExpression expr = (SymUnaryExpression) this;
			return expr.get_operator() == COperator.dereference;
		}
		else if(this instanceof SymFieldExpression) return true;
		else { return false; }
	}
	/**
	 * get the reference expressions in this subtree
	 * @return
	 */
	public Iterable<SymExpression> get_references() {
		List<SymExpression> references = new ArrayList<SymExpression>();
		
		/* collect all the reference in the subtree of symbolic structure */
		Queue<SymNode> queue = new LinkedList<SymNode>(); queue.add(this);
		while(!queue.isEmpty()) { 
			SymNode node = queue.poll();
			if(node.is_reference()) references.add((SymExpression) node);
			for(SymNode child : node.children) { queue.add(child); }
		}
		
		return references;
	}
	
}
