package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.Collection;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirAnnotation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymInstanceUtils;
import com.jcsa.jcmutest.mutant.sym2mutant.CirMutations;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymInstance;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateError;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.parse.symbol.SymbolStateContexts;

/**
 * It records the concrete value evaluated from a (abstract) symbolic instance, either constraint
 * or state error that is expected to be satisfied for the purpose of killing mutation.
 * 
 * @author yukimula
 *
 */
public class SymInstanceState {
	
	/* definitions */
	/** the abstract symbolic instance as source to be evaluated **/
	private SymInstance 				abstract_instance;
	/** the concrete instance evaluated from the source instance **/
	private SymInstance 				concrete_instance;
	/** boolean value to specify whether the abstract instance is satisfied or not **/
	private Boolean						evaluation_result;
	/** the collection of annotations that describe the symbolic semantics of concrete instance **/
	private Collection<CirAnnotation>	annotations;
	
	/* getters */
	/**
	 * @return the abstract symbolic instance as source to be evaluated
	 */
	public SymInstance 	get_abstract_instance() { return this.abstract_instance; }
	/**
	 * @return the concrete instance evaluated from the source instance
	 */
	public SymInstance 	get_concrete_instance() { return this.concrete_instance; }
	/**
	 * @return 	True if the concrete instance holds at the given context
	 * 			False if the concrete instance does not hold at contexts
	 * 			null if the satisfaction of concrete instance is unknown
	 */
	public Boolean		get_evaluation_result() { return this.evaluation_result; }
	/**
	 * @return the collection of annotations that describe the symbolic semantics of concrete instance
	 */
	public Iterable<CirAnnotation> get_annotations() { return this.annotations;  }
	/**
	 * @return execution point where the instance is evaluated
	 */
	public CirExecution get_execution() { return this.abstract_instance.get_execution(); }
	/**
	 * @return whether the instance is SymStateError
	 */
	public boolean is_state_error() { return this.abstract_instance instanceof SymStateError; }
	/**
	 * @return whether the instance is SymConstraint
	 */
	public boolean is_constraints() { return this.abstract_instance instanceof SymConstraint; }
	
	/* creator */
	private SymInstanceState() { }	/* avoid to create from outsider */
	/**
	 * @param instance
	 * @param cir_mutations used to generate concrete instance
	 * @param contexts
	 * @return the state that records the evaluation of input instance w.r.t. given contexts
	 * @throws Exception
	 */
	protected static SymInstanceState new_state(
			SymInstance instance, CirMutations cir_mutations, 
			SymbolStateContexts contexts) throws Exception {
		if(instance == null)
			throw new IllegalArgumentException("Invalid instance: null");
		else if(cir_mutations == null)
			throw new IllegalArgumentException("Invalid cir_mutations.");
		else {
			SymInstanceState state = new SymInstanceState();	/* construct */
			
			/* abstract + concrete instance from contexts */
			state.abstract_instance = instance;
			if(instance instanceof SymConstraint) {
				state.concrete_instance = cir_mutations.optimize((SymConstraint) instance, contexts);
			}
			else if(instance instanceof SymStateError) {
				state.concrete_instance = cir_mutations.optimize((SymStateError) instance, contexts);
			}
			else {
				throw new IllegalArgumentException("Unknown class: " + instance.getClass().getName());
			}
			
			/* evaluation + annotations from concrete instance */
			state.evaluation_result = state.concrete_instance.validate(null);
			state.annotations = SymInstanceUtils.annotations(state.concrete_instance);
			
			return state;										/* return it */
		}
	}
	
}
