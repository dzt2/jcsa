package com.jcsa.jcmutest.mutant.ctx2mutant.base;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolLiteral;

public class SedMutaMutation extends ContextMutation {

	protected SedMutaMutation(AstCirNode location, Mutant mutant) throws Exception {
		super(ContextMutaClass.sed_muta, location, 
				SymbolFactory.sym_constant(mutant.get_id()), 
				SymbolFactory.literal(mutant.get_mutation().get_operator().toString()));
	}
	
	/**
	 * @return the ID of the mutation being injected at this location
	 */
	public	int get_mutant_ID() { return ((SymbolConstant) this.get_loperand()).get_int(); }
	
	/**
	 * @return the mutation operator to be applied at that location
	 */
	public	String get_operator()	{ return ((SymbolLiteral) this.get_roperand()).get_literal(); }
	
}
