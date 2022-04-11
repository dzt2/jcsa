package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolLiteral;

/**
 * 	sed_muta(statement; mutant_ID, class_operator)
 * 	
 * 	@author yukimula
 *
 */
public class UniSeedMutantState extends UniConditionState {

	protected UniSeedMutantState(CirStatement location, Mutant mutant) throws Exception {
		super(UniAbstractClass.sed_muta, location, 
				SymbolFactory.sym_constant(Integer.valueOf(mutant.get_id())), 
				SymbolFactory.literal(mutant.get_mutation().get_operator().toString()));
	}
	
	/**
	 * @return the integer ID of the mutant
	 */
	public int get_mutant_ID() { return ((SymbolConstant) this.get_loperand()).get_int(); }
	
	/**
	 * @return the operator of the mutation
	 */
	public String get_mutant_operator() { return ((SymbolLiteral) this.get_roperand()).get_literal(); }

}
