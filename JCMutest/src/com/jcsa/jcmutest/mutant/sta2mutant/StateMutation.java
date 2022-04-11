package com.jcsa.jcmutest.mutant.sta2mutant;

import com.jcsa.jcmutest.mutant.sta2mutant.base.UniAbstErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.UniConditionState;

/**
 * 	It specifies a set of state-transformations in a given syntactic context. 	<br>
 * 	
 * 	@author yukimula
 *
 */
public class StateMutation {
	
	/* definitions */
	/** the context enclosing the state mutation **/
	private	StateContext		context;
	/** the pre-condition for causing the mutation **/
	private	UniConditionState	constraint;
	/** the initial error state for being infected **/
	private	UniAbstErrorState	init_error;
	/**
	 * @param context		the context enclosing the state mutation
	 * @param constraint	the pred-condition for infecting state
	 * @param init_error	the initial error state being created
	 * @throws Exception
	 */
	protected StateMutation(StateContext context, 
			UniConditionState constraint, 
			UniAbstErrorState init_error) throws Exception {
		if(context == null) {
			throw new IllegalArgumentException("Invalid context: null");
		}
		else if(constraint == null) {
			throw new IllegalArgumentException("Invalid constraint: null");
		}
		else if(init_error == null) {
			throw new IllegalArgumentException("Invalid init_error: null");
		}
		else {
			this.context = context;
			this.constraint = constraint; 
			this.init_error = init_error;
		}
	}
	
	/* getters */
	/**
	 * @return 	the context enclosing the state mutation
	 */
	public StateContext get_context() { return this.context; }
	/**
	 * @return	the pre-condition for causing the mutation
	 */
	public UniConditionState get_constraint() { return this.constraint; }
	/**
	 * @return	the initial error state for being infected
	 */
	public UniAbstErrorState get_init_error() { return this.init_error; }
	
}
