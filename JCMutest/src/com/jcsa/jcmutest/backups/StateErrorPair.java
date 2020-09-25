package com.jcsa.jcmutest.backups;

import com.jcsa.jcparse.lang.sym.SymConstant;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.test.state.CStateContexts;

/**
 * constraint-error pair.
 * 
 * @author yukimula
 *
 */
public class StateErrorPair {
	
	/* definitions */
	/** constraint for causing state errors **/
	private SecConstraint constraint;
	/** error caused when constraint is met **/
	private SecStateError state_error;
	/**
	 * @param constraint 	constraint for causing state errors
	 * @param state_error	error caused when constraint is met
	 * @throws IllegalArgumentException
	 */
	protected StateErrorPair(SecConstraint constraint, 
			SecStateError state_error) throws IllegalArgumentException {
		if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint: null");
		else if(state_error == null)
			throw new IllegalArgumentException("Invalid state_error: null");
		else {
			this.constraint = constraint;
			this.state_error = state_error;
		}
	}
	
	/* getters */
	/**
	 * @return constraint for causing state errors
	 */
	public SecConstraint get_constraint() { return this.constraint; }
	/**
	 * @return error caused when constraint is met
	 */
	public SecStateError get_state_error() { return this.state_error; }
	@Override
	public String toString() {
		try {
			return constraint.generate_code() + " |==> " + state_error.generate_code();
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	/**
	 * @param contexts
	 * @return optimize the state-error pair under the contexts
	 * @throws Exception
	 */
	public StateErrorPair optimize(CStateContexts contexts) throws Exception {
		SecConstraint constraint = this.constraint.optimize(contexts);
		SecStateError state_error = this.state_error.optimize(contexts);
		
		SymExpression condition = constraint.get_sym_condition();
		if(condition instanceof SymConstant) {
			if(((SymConstant) condition).get_bool()) {
				state_error = SecFactory.none_error(state_error.get_statement().get_statement());
			}
		}
		
		return new StateErrorPair(constraint, state_error);
	}
	
}
