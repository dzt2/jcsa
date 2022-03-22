package com.jcsa.jcmutest.mutant.uni2mutant.base.impl;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractClass;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractStore;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolLiteral;

/**
 * 	sed_muta(statement; mutant_ID, class_oprt)
 * 	
 * 	@author yukimula
 *
 */
public class UniSeedMutantState extends UniConditionState {
	
	protected UniSeedMutantState(UniAbstractStore state_store, Mutant mutant) throws Exception {
		super(UniAbstractClass.sed_muta, state_store, 
				SymbolFactory.sym_constant(Integer.valueOf(mutant.get_id())),
				SymbolFactory.literal(mutant.get_mutation().get_operator().toString()));
	}
	
	/**
	 * @return the integer ID of the mutant being injected
	 */
	public int get_mutant_id() { return ((SymbolConstant) this.get_lvalue()).get_int(); }
	
	/**
	 * @return the mutation operator name
	 */
	public String get_class_operator() { return ((SymbolLiteral) this.get_rvalue()).get_literal(); }
	
}
