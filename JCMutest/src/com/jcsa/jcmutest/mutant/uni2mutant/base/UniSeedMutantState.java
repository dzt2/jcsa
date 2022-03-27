package com.jcsa.jcmutest.mutant.uni2mutant.base;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolLiteral;

/**
 * 	sed_muta(statement; mutant_ID, clas_oprt)
 * 	
 * 	@author yukimula
 *
 */
public class UniSeedMutantState extends UniConditionState {
	
	/**
	 * It creates a seed-mutation state
	 * @param _store	the statement location
	 * @param mutant	the mutant being injected in the statement location
	 * @throws Exception
	 */
	protected UniSeedMutantState(UniAbstractStore _store, Mutant mutant) throws Exception {
		super(UniAbstractClass.sed_muta, _store, 
				SymbolFactory.sym_constant(Integer.valueOf(mutant.get_id())),
				SymbolFactory.literal(mutant.get_mutation().get_operator().toString()));
	}
	
	/**
	 * @return the integer ID of the mutant being injected
	 */
	public int get_mutant_ID() {
		return ((SymbolConstant) this.get_loperand()).get_int();
	}
	
	/**
	 * @return the mutation operator to model the mutation
	 */
	public String get_mutant_operator() {
		return ((SymbolLiteral) this.get_roperand()).get_literal();
	}
	
}
