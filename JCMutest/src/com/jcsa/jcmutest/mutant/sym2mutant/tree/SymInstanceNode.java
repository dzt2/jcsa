package com.jcsa.jcmutest.mutant.sym2mutant.tree;

import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymInstance;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateError;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;

/**
 * Either node or edge in symbolic instance tree.
 * 
 * @author yukimula
 *
 */
public abstract class SymInstanceNode {
	
	/* definitions */
	/** instance that the node represents **/
	private SymInstance instance;
	/** the abstract state using null contexts **/
	private SymInstanceState abstract_state;
	/** the accumulated status during the dynamic evaluations **/
	private SymInstanceStatus concrete_status;
	/**
	 * create a node containing symbolic instance being evaluated
	 * @param instance
	 * @throws Exception
	 */
	protected SymInstanceNode(SymInstance instance) throws Exception {
		if(instance == null) {
			this.instance = null;
			this.abstract_state = null;
			this.concrete_status = null;
		}
		else {
			this.instance = instance;
			this.abstract_state = new SymInstanceState(instance, null);
			this.concrete_status = new SymInstanceStatus(instance);
		}
	}
	
	/* getters */
	/**
	 * @return whether the node contains any symbolic instance for analysis
	 */
	public boolean has_instance() { return this.instance != null; }
	/**
	 * @return symbolic instance that the node represents
	 */
	public SymInstance get_instance() { return this.instance; }
	/**
	 * @return the execution point of the instance
	 */
	public CirExecution get_execution() { return this.instance.get_execution(); }
	/**
	 * @return whether the abstract instance is a constraint
	 */
	public boolean is_constraint() { return this.instance instanceof SymConstraint; }
	/**
	 * @return whether the abstract instance is a state error
	 */
	public boolean is_state_error() { return this.instance instanceof SymStateError; }
	
	/* state */
	/**
	 * @return the abstract state using null contexts of this single instance
	 */
	public SymInstanceState get_abstract_state() { return this.abstract_state; }
	/**
	 * @return the accumulated status with concrete evaluation results from the input instance
	 */
	public SymInstanceStatus get_concrete_status() { return this.concrete_status; }
	
}
