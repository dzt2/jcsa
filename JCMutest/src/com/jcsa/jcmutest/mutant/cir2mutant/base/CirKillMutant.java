package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * kill_muta(execution, location, mutation_literal)
 * 
 * @author yukimula
 *
 */
public class CirKillMutant extends CirAttribute {

	protected CirKillMutant(CirMutation cir_mutation) throws Exception {
		super(CirAttributeType.kill_muta, cir_mutation.get_execution(), 
				cir_mutation.get_init_error().get_location(), 
				SymbolFactory.literal(cir_mutation.toString()));
	}

	@Override
	public CirAttribute optimize(SymbolProcess context) throws Exception {
		return this;
	}

	@Override
	public Boolean evaluate(SymbolProcess context) throws Exception {
		return true;
	}

}
