package com.jcsa.jcmuta.mutant.error2mutation;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

/**
 * Error propagation relationship
 * @author yukimula
 *
 */
public class StateErrorEdge {
	
	/* attributes */
	/** error that causes another **/
	private StateErrorNode source;
	/** error that caused by another **/
	private StateErrorNode target;
	/** constraints required for causing propagation **/
	private List<StateConstraint> constraints;
	
	/* constructor */
	/**
	 * create a propagation from source to target
	 * @param source
	 * @param target
	 * @throws IllegalArgumentException
	 */
	protected StateErrorEdge(StateErrorNode source, StateErrorNode target) throws IllegalArgumentException {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target: null");
		else {
			this.source = source; this.target = target;
			this.constraints = new ArrayList<StateConstraint>();
		}
	}
	
	/* getters */
	/**
	 * get the error that causes another
	 * @return
	 */
	public StateErrorNode get_source() { return this.source; }
	/**
	 * get the errors caused by another
	 * @return
	 */
	public StateErrorNode get_target() { return this.target; }
	/**
	 * get the constraints for causing error propagation
	 * @return
	 */
	public Iterable<StateConstraint> get_constraints() { return this.constraints; }
	
	/* setters */
	/**
	 * whether the constraint is boolean condition
	 * @param constraint
	 * @return
	 * @throws IllegalArgumentException
	 */
	private boolean is_condition(StateConstraint constraint) throws Exception {
		CType data_type = CTypeAnalyzer.get_value_type(
				constraint.get_sym_condition().get_data_type());
		return CTypeAnalyzer.is_boolean(data_type);
	}
	/**
	 * add a boolean constraint as condition in the error propagation
	 * @param constraint
	 * @throws Exception
	 */
	protected void add_constraint(StateConstraint constraint) throws Exception {
		if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint: null");
		else if(this.is_condition(constraint))
			this.constraints.add(constraint);
		else throw new IllegalArgumentException(constraint.toString());
	}
	
}
