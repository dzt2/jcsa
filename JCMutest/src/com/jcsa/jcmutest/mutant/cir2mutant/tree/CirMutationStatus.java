package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymStateError;

/**
 * It records the status of the constraint or state-error under analysis.
 * 
 * @author yukimula
 *
 */
public class CirMutationStatus {
	
	/* definitions */
	/** the times that the subject is executed **/
	private int execution_times;
	/** the times that the subject evaluated as true **/
	private int acception_times;
	/** the times that the subject evaluated as false **/
	private int rejection_times;
	/** the annotations to describe the subject for each of its execution **/
	private Set<CirAnnotation> annotations;
	/** the sequence of concrete values of subject recorded in the status **/
	private List<Object> concrete_values;
	
	/* definitions */
	/**
	 * create an empty record of the status for Cir-Subject
	 */
	protected CirMutationStatus() {
		this.execution_times = 0;
		this.acception_times = 0;
		this.rejection_times = 0;
		this.annotations = new HashSet<CirAnnotation>();
		this.concrete_values = new ArrayList<Object>();
	}
	
	/* getters */
	/**
	 * @return the times that the subject is executed
	 */
	public int get_execution_times() { return this.execution_times; }
	/**
	 * @return the times that the subject evaluated as true
	 */
	public int get_acception_times() { return this.acception_times; }
	/**
	 * @return the times that the subject evaluated as false
	 */
	public int get_rejection_times() { return this.rejection_times; }
	/**
	 * @return the annotations to describe the subject for each of its execution
	 */
	public Iterable<CirAnnotation> get_annotations() { return this.annotations; }
	/**
	 * @return either SymConstraint* or SymStateError*
	 */
	public Iterable<Object> get_concrete_values() { return this.concrete_values; }
	
	/* setters */
	/**
	 * clear the original records in the status
	 */
	protected void clear() {
		this.execution_times = 0;
		this.acception_times = 0;
		this.rejection_times = 0;
		this.annotations.clear();
	}
	/**
	 * @param constraint
	 * @return append the concrete constraint to the status
	 * @throws Exception
	 */
	protected Boolean append(SymConstraint constraint) throws Exception {
		this.annotations.addAll(CirAnnotations.annotations(constraint, null));
		this.execution_times++;
		Boolean result = constraint.validate(null); 
		if(result != null) {
			if(result.booleanValue()) {
				this.acception_times++;
			}
			else {
				this.rejection_times++;
			}
		}
		this.concrete_values.add(constraint);
		return result;
	}
	/**
	 * append the state for evaluating the state-error
	 * @param state_error
	 * @param contexts
	 * @throws Exception
	 */
	protected Boolean append(SymStateError state_error) throws Exception { 
		this.annotations.addAll(CirAnnotations.annotations(state_error, null));
		this.execution_times++;
		Boolean result = state_error.validate(null);
		if(result != null) {
			if(result.booleanValue()) {
				this.acception_times++;
			}
			else {
				this.rejection_times++;
			}
		}
		this.concrete_values.add(state_error);
		return result;
	}
	/**
	 * append the state without any subject being described
	 */
	protected Boolean append() {
		this.execution_times++;
		return Boolean.TRUE;
	}
	
	/* verifiers */
	/**
	 * @return whether the subject has been executed in analysis
	 */
	public boolean is_executed() { return this.execution_times > 0; }
	/**
	 * @return whether the subject was accepted at least once in testing
	 */
	public boolean is_accepted() { return this.acception_times > 0; }
	/**
	 * @return whether the subject can be accepted (accpetion_times > 0 or
	 *         rejection_times < execution_times)
	 */
	public boolean is_acceptable() { return this.execution_times > this.rejection_times; }
	
}
