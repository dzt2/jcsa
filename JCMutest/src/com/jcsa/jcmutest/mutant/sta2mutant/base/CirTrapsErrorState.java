package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * [stmt:statement] <== set_trap(execution, exception)
 * @author yukimula
 *
 */
public class CirTrapsErrorState extends CirPathErrorState {

	protected CirTrapsErrorState(CirExecution point) throws Exception {
		super(point, CirStateValue.set_trap(point));
	}

	@Override
	public CirAbstErrorState normalize(SymbolProcess context) throws Exception {
		CirExecution execution = this.get_execution();
		CirFunction main_function = execution.get_graph().get_function().get_graph().get_main_function();
		if(main_function != null) {
			execution = main_function.get_flow_graph().get_exit();
		}
		else {
			execution = execution.get_graph().get_exit();
		}
		return CirAbstractState.set_trap(execution);
	}

	@Override
	public Boolean validate(SymbolProcess context) throws Exception {
		return Boolean.TRUE;
	}

}
