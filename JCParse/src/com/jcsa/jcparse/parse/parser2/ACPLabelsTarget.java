package com.jcsa.jcparse.parse.parser2;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.irlang.stmt.CirLabel;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * The labels-target is used to maintain the CirLabel(s) and the target statement they refer to.
 * Each labels-target instance is defined based on a String key in the module.
 * @author yukimula
 *
 */
class ACPLabelsTarget {
	
	/* definitions and constructor */
	private ACPScope scope;
	private String label_name;
	private List<CirLabel> labels;
	private CirStatement target;
	protected ACPLabelsTarget(ACPScope scope, String label_name) throws IllegalArgumentException {
		if(label_name == null || label_name.isBlank())
			throw new IllegalArgumentException("invalid label_name as null");
		else if(scope == null)
			throw new IllegalArgumentException("undefined scope as null");
		else {
			this.scope = scope;
			this.label_name = label_name;
			this.labels = new ArrayList<CirLabel>();
			this.target = null;
		}
	}
	
	/* getters */
	/**
	 * get the scope where the label-target instance is created
	 * @return
	 */
	public ACPScope get_scope() { return this.scope; }
	/**
	 * get the name of the labels in the instance
	 * @return
	 */
	public String get_label_name() { return this.label_name; }
	/**
	 * whether the instance contains any target for labels to refer
	 * @return
	 */
	public boolean has_target() { return this.target != null; }
	/**
	 * add a new label into the list and update its target ID if target has been set
	 * @param label
	 * @throws IllegalArgumentException
	 */
	public void add_label(CirLabel label) throws IllegalArgumentException {
		if(label == null)
			throw new IllegalArgumentException("invalid label: null");
		else {
			this.labels.add(label);
			if(this.target != null) 
				label.set_target_node_id(target.get_node_id());
		}
	}
	/**
	 * set the target statement for the labels in the list and update their target ID
	 * @param target
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public void set_target(CirStatement target) throws IllegalArgumentException, IllegalAccessException {
		if(target == null)
			throw new IllegalArgumentException("invalid target as null");
		else if(this.target != null)
			throw new IllegalAccessException("invalid access for target");
		else {
			this.target = target;
			for(CirLabel label : this.labels) {
				label.set_target_node_id(target.get_node_id());
			}
		}
	}
	/**
	 * reset the labels and target statement in the instance
	 */
	public void init() { this.labels.clear(); this.target = null; }
	/**
	 * get the target statement for solving the labels
	 * @return
	 */
	public CirStatement get_target() { return target; }
	
}
