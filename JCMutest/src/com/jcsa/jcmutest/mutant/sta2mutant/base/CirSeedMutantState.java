package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolLiteral;

public class CirSeedMutantState extends CirConditionState {
	
	protected CirSeedMutantState(CirAbstractStore location, Mutant mutant) throws Exception {
		super(CirAbstractClass.sed_muta, location, 
				SymbolFactory.sym_constant(Integer.valueOf(mutant.get_id())), 
				SymbolFactory.literal(mutant.get_mutation().get_operator().toString()));
	}
	
	/**
	 * @return the integer ID of the mutant being injected there
	 */
	public int get_mutant_ID() { return ((SymbolConstant) this.get_loperand()).get_int(); }
	/**
	 * @return the code of the mutation operator being injected
	 */
	public String get_mutation_operator() { return ((SymbolLiteral) this.get_roperand()).get_literal(); }
	
}
