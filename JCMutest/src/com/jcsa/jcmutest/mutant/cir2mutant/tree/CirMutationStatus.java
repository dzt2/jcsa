package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.HashSet;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirStateError;
import com.jcsa.jcparse.flwa.symbol.CStateContexts;

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
	
	/* definitions */
	/**
	 * create an empty record of the status for Cir-Subject
	 */
	protected CirMutationStatus() {
		this.execution_times = 0;
		this.acception_times = 0;
		this.rejection_times = 0;
		this.annotations = new HashSet<CirAnnotation>();
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
	 * append the state for evaluating the constraint
	 * @param constraint
	 * @param contexts
	 * @throws Exception
	 */
	protected void append(CirConstraint constraint, CStateContexts contexts) throws Exception {
		this.annotations.addAll(CirAnnotations.annotations(constraint, contexts));
		this.execution_times++;
		Boolean result = constraint.validate(contexts);
		if(result != null) {
			if(result.booleanValue()) {
				this.acception_times++;
			}
			else {
				this.rejection_times++;
			}
		}
	}
	/**
	 * append the state for evaluating the state-error
	 * @param state_error
	 * @param contexts
	 * @throws Exception
	 */
	protected void append(CirStateError state_error, CStateContexts contexts) throws Exception { 
		this.annotations.addAll(CirAnnotations.annotations(state_error, contexts));
		this.execution_times++;
		Boolean result = state_error.validate(contexts);
		if(result != null) {
			if(result.booleanValue()) {
				this.acception_times++;
			}
			else {
				this.rejection_times++;
			}
		}
	}
	/**
	 * append the state without any subject being described
	 */
	protected void append() {
		this.execution_times++;
	}
	
}
