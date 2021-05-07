package com.jcsa.jcmutest.mutant.sym2mutant.tree;

import java.util.Collection;

import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymInstance;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateError;
import com.jcsa.jcmutest.mutant.sym2mutant.util.SymConditionUtils;
import com.jcsa.jcmutest.mutant.sym2mutant.util.SymInstanceUtils;
import com.jcsa.jcparse.parse.symbol.SymbolStateContexts;

/**
 * It preserves one single evaluation on a particular symbolic instance in the program.
 * 
 * @author yukimula
 *
 */
public class SymInstanceState {
	
	/* definition */
	/** abstract symbolic instance being evaluated **/
	private SymInstance abstract_instance;
	/** concrete symbolic instance created from the abstract one **/
	private SymInstance concrete_instance;
	/** evaluation result of the concrete instance where null means unknown **/
	private Boolean		evaluation_result;
	/** the set of symbolic conditions generated from concrete instance **/
	private Collection<SymCondition> conditions;
	/**
	 * @param instance
	 * @param contexts
	 * @throws Exception
	 */
	protected SymInstanceState(SymInstance instance, SymbolStateContexts contexts) throws Exception {
		if(instance == null)
			throw new IllegalArgumentException("Invalid instance: null");
		else {
			this.abstract_instance = instance;
			this.concrete_instance = SymInstanceUtils.optimize(instance, contexts);
			this.evaluation_result = this.concrete_instance.validate(null);
			this.conditions = SymConditionUtils.sym_conditions(this.concrete_instance);
		}
	}
	
	/* getters */
	/**
	 * @return whether the abstract instance is a constraint
	 */
	public boolean is_constraint() { return this.abstract_instance instanceof SymConstraint; }
	/**
	 * @return whether the abstract instance is a state error
	 */
	public boolean is_state_error() { return this.abstract_instance instanceof SymStateError; }
	/**
	 * @return abstract symbolic instance being evaluated
	 */
	public SymInstance 	get_abstract_instance()	{ return this.abstract_instance; }
	/**
	 * @return concrete symbolic instance created from the abstract one
	 */
	public SymInstance 	get_concrete_instance()	{ return this.concrete_instance; }
	/**
	 * @return evaluation result of the concrete instance where null means unknown
	 */
	public Boolean		get_evaluation_result()	{ return this.evaluation_result; }
	/**
	 * @return the set of symbolic conditions generated from concrete instance
	 */
	public Iterable<SymCondition> get_conditions() { return this.conditions; }
	
}
