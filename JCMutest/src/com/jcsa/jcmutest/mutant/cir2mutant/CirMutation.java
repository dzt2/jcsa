package com.jcsa.jcmutest.mutant.cir2mutant;

import com.jcsa.jcmutest.mutant.cir2mutant.base.SymCategory;
import com.jcsa.jcmutest.mutant.cir2mutant.base.SymCondition;


/**
 * This instance represents a mutation being performed in C-intermediate code rather than C source code.
 * 
 * @author yukimula
 *
 */
public class CirMutation {
	
	/* definitions */
	/** state infection constraint **/
	private SymCondition constraint;
	/** start infected state error **/
	private SymCondition init_error;
	/**
	 * @param constraint
	 * @param init_error
	 * @throws Exception
	 */
	public CirMutation(SymCondition constraint, SymCondition init_error) throws Exception {
		if(constraint == null || constraint.get_category() != SymCategory.evaluation) {
			throw new IllegalArgumentException("Invalid constraint as: " + constraint);
		}
		else if(init_error == null || (init_error.get_category() != SymCategory.data_error 
				&& init_error.get_category() != SymCategory.path_error)) {
			throw new IllegalArgumentException("Invalid init_error as: " + init_error);
		}
		else {
			this.constraint = constraint;
			this.init_error = init_error;
		}
	}
	
	/* getters */
	/**
	 * @return state infection constraint
	 */
	public SymCondition get_constraint() { return this.constraint; }
	/**
	 * @return start infected state error
	 */
	public SymCondition get_init_error() { return this.init_error; }
	@Override
	public String toString() { 
		return this.constraint.toString() + " ==> " + this.init_error.toString();
	}
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		else if(obj instanceof CirMutation)
			return obj.toString().equals(this.toString());
		else
			return false;
	}
	
}
