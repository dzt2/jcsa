package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

public class CirKillTarget extends CirAttribute {

	protected CirKillTarget(CirMutation mutation, SymbolExpression parameter)
			throws IllegalArgumentException {
		super(CirAttributeType.kill_muta, mutation.get_execution(), 
				mutation.get_execution().get_statement(), parameter);
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
