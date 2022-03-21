package com.jcsa.jcmutest.mutant.uni2mutant.base;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.uni2mutant.UniMutations;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It creates an abstract symbolic state used to describe mutation testing process.
 * 
 * @author yukimula
 *
 */
public class UniAbstractState {
	
	/* attributes */
	/** the class of this abstract symbolic state **/
	private	UniAbstractClass	state_class;
	/** the store-location to evaluate this state **/
	private	UniAbstractStore	state_store;
	/** the left-operand used to define the state **/
	private	SymbolExpression	loperand;
	/** the righ-operand used to define the state **/
	private	SymbolExpression	roperand;
	/**
	 * It creates an abstract symbolic state used in mutation testing.
	 * @param state_class	the class of this abstract symbolic state
	 * @param state_store	the store-location to evaluate this state
	 * @param loperand		the left-operand used to define the state
	 * @param roperand		the righ-operand used to define the state
	 * @throws Exception
	 */
	private	UniAbstractState(UniAbstractClass state_class,
			UniAbstractStore state_store,
			SymbolExpression loperand,
			SymbolExpression roperand) throws Exception {
		if(state_class == null) {
			throw new IllegalArgumentException("Invalid state_store: null");
		}
		else if(state_store == null) {
			throw new IllegalArgumentException("Invalid state_store: null");
		}
		else if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand as: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand as: null");
		}
		else {
			this.state_class = state_class;
			this.state_store = state_store;
			this.loperand = UniMutations.evaluate(loperand);
			this.roperand = UniMutations.evaluate(roperand);
		}
	}
	
	/* getters */
	/**
	 * @return the class of this abstract symbolic state
	 */
	public 	UniAbstractClass	get_class()  { return this.state_class; }
	/**
	 * @return the store-location to evaluate this state
	 */
	public 	UniAbstractStore	get_store()	 { return this.state_store; }
	/**
	 * @return the left-operand used to define the state
	 */
	public 	SymbolExpression	get_lvalue() { return this.loperand; }
	/**
	 * @return the righ-operand used to define the state
	 */
	public 	SymbolExpression	get_rvalue() { return this.roperand; }
	
	/* general */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(this.state_class.toString());
		buffer.append(":");
		buffer.append(this.state_store.toString());
		buffer.append(":");
		buffer.append(this.loperand.toString());
		buffer.append(":");
		buffer.append(this.roperand.toString());
		return buffer.toString();
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof UniAbstractState) {
			return obj.toString().equals(this.toString());
		}
		else {
			return false;
		}
	}
	
	/* factory */
	/**
	 * @param store		the location where this state will be evaluated
	 * @param min_times	the minimal times to execute the target statement
	 * @param max_times	the maximal times to execute the target statement
	 * @return			cov_times(store_location; min_times, max_times)
	 * @throws Exception
	 */
	public static UniAbstractState	cov_time(UniAbstractStore store, int min_times, int max_times) throws Exception {
		if(store == null) {
			throw new IllegalArgumentException("Invalid store: null");
		}
		else if(min_times > max_times || max_times < 0) {
			throw new IllegalArgumentException("Invalid: " + min_times + " --> " + max_times);
		}
		else {
			if(store.is_statement() || store.is_gotolabel()) {
				return new UniAbstractState(UniAbstractClass.cov_time, store,
						SymbolFactory.sym_constant(Integer.valueOf(min_times)),
						SymbolFactory.sym_constant(Integer.valueOf(max_times)));
			}
			else {
				throw new IllegalArgumentException("Unsupport: " + store.toString());
			}
		}
	}
	/**
	 * @param store		the location where this state will be evaluated
	 * @param condition	the condition to be evaluated at the given node
	 * @param must_need	True (always satisfied); False (met at least once)
	 * @return			eva_bool(store_location; condition, must_need)
	 * @throws Exception
	 */
	public static UniAbstractState	eva_bool(UniAbstractStore store, Object condition, boolean must_need) throws Exception {
		if(store == null) {
			throw new IllegalArgumentException("Invalid store: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			if(store.is_statement() || store.is_gotolabel()) {
				return new UniAbstractState(UniAbstractClass.eva_bool, store,
						SymbolFactory.sym_condition(condition, true),
						SymbolFactory.sym_constant(Boolean.valueOf(must_need)));
			}
			else {
				throw new IllegalArgumentException("Unsupport: " + store.toString());
			}
		}
	}
	/**
	 * @param store		the location where this state will be evaluated
	 * @param mutant	the syntactic mutation to be injected here.....
	 * @return			sed_muta(store_location; mutant_ID, clas_oprt)
	 * @throws Exception
	 */
	public static UniAbstractState	sed_muta(UniAbstractStore store, Mutant mutant) throws Exception {
		if(store == null) {
			throw new IllegalArgumentException("Invalid store: null");
		}
		else if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant: null");
		}
		else {
			if(store.is_statement() || store.is_gotolabel()) {
				String literal = mutant.get_mutation().get_operator().toString();
				return new UniAbstractState(UniAbstractClass.sed_muta, store,
						SymbolFactory.sym_constant(Integer.valueOf(mutant.get_id())),
						SymbolFactory.sym_expression(literal));
			}
			else {
				throw new IllegalArgumentException("Unsupport: " + store.toString());
			}
		}
	}
	// TODO append more classes factory here...
	
	
}
