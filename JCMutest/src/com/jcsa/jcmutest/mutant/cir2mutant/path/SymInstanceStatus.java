package com.jcsa.jcmutest.mutant.cir2mutant.path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirAnnotation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymInstance;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymInstanceUtils;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymStateError;
import com.jcsa.jcparse.flwa.symbol.CStateContexts;

/**
 * It records the status of symbolic instance being evaluated during testing.
 * 
 * @author yukimula
 *
 */
public class SymInstanceStatus {
	
	/* definitions */
	/** the original instance hold by the status **/
	private SymInstance instance;
	/** the times that the instance was evaluated **/
	private int execution_times;
	/** the times that the instance evaluated as false **/
	private int rejection_times;
	/** the times that the instance evaluated as true **/
	private int acception_times;
	/** the concrete instances evaluated from the source **/
	private List<SymInstance> concrete_instances;
	/** the annotations with corresponds to the instance as given **/
	private Collection<CirAnnotation> cir_annotations;
	
	/* constructor */
	/**
	 * create an empty status for describing the evaluation of target instance
	 * @param instance
	 * @throws Exception
	 */
	protected SymInstanceStatus(SymInstance instance) throws Exception {
		if(instance == null)
			throw new IllegalArgumentException("Invalid instance: null");
		else {
			this.instance = instance;
			this.execution_times = 0;
			this.rejection_times = 0;
			this.acception_times = 0;
			this.concrete_instances = new ArrayList<SymInstance>();
			this.cir_annotations = new HashSet<CirAnnotation>();
		}
	}
	
	/* getters */
	/**
	 * @return the original instance hold by the status
	 */
	public SymInstance get_instance() { return this.instance; }
	/**
	 * @return the times that the instance was evaluated
	 */
	public int get_execution_times() { return this.execution_times; }
	/**
	 * @return the times that the instance evaluated as false
	 */
	public int get_rejection_times() { return this.rejection_times; }
	/**
	 * @return the times that the instance evaluated as true
	 */
	public int get_acception_times() { return this.acception_times; }
	/**
	 * @return the concrete instances evaluated from the source
	 */
	public Iterable<SymInstance> get_concrete_instances() { return this.concrete_instances; }
	/**
	 * @return the annotations connected with the concrete instance evaluated within
	 */
	public Iterable<CirAnnotation> get_cir_annotations() { return this.cir_annotations; }
	
	/* setters */
	/**
	 * reset the status of the instance
	 */
	protected void reset() {
		this.execution_times = 0;
		this.rejection_times = 0;
		this.acception_times = 0;
		this.concrete_instances.clear();
		this.cir_annotations.clear();
	}
	/**
	 * @param cir_mutations
	 * @param contexts
	 * @return execute the instance in the given context and append its 
	 * @throws Exception
	 */
	protected Boolean evaluate(CirMutations cir_mutations, CStateContexts contexts) throws Exception {
		Boolean result;
		if(this.instance instanceof SymConstraint) {
			SymConstraint constraint = (SymConstraint) this.instance;
			constraint = cir_mutations.optimize(constraint, contexts);
			this.concrete_instances.add(constraint);
			this.cir_annotations.addAll(SymInstanceUtils.annotations(constraint));
			result = constraint.validate(null);
		}
		else {
			SymStateError state_error = (SymStateError) this.instance;
			state_error = cir_mutations.optimize(state_error, contexts);
			this.concrete_instances.add(state_error);
			this.cir_annotations.addAll(SymInstanceUtils.annotations(state_error));
			result = state_error.validate(null);
		}
		this.execution_times++;
		if(result != null) {
			if(result.booleanValue()) {
				this.acception_times++;
			}
			else {
				this.rejection_times++;
			}
		}
		return result;
	}
	
}
