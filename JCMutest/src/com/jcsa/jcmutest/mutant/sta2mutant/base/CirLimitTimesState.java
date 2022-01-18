package com.jcsa.jcmutest.mutant.sta2mutant.base;

import java.util.HashSet;
import java.util.Set;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * [stmt:statement] <== cov_time(true, int_times)
 * 
 * @author yukimula
 *
 */
public class CirLimitTimesState extends CirConditionState {

	protected CirLimitTimesState(CirExecution point, int int_times) throws Exception {
		super(point, CirStateValue.cov_time(true, int_times));
	}
	
	/**
	 * @return the maximal times that the statement should be executed
	 */
	public int get_maximal_times() {
		return ((SymbolConstant) this.get_roperand()).get_int();
	}

	@Override
	public CirConditionState normalize(SymbolProcess context) throws Exception {
		CirExecutionPath path = this.get_previous_path();
		int maximal_times = this.get_maximal_times();
		return CirAbstractState.lim_time(path.get_source(), maximal_times);
	}

	@Override
	public Boolean validate(SymbolProcess context) throws Exception {
		if(context == null) {
			return null;
		}
		else {
			CirExecutionPath path = this.get_previous_path();
			Set<Integer> exec_times = new HashSet<Integer>();
			for(CirExecutionEdge edge : path.get_edges()) {
				CirExecution execution = edge.get_source();
				SymbolExpression res = context.get_data_stack().load(execution);
				if(res != null) {
					exec_times.add(((SymbolConstant) res).get_int());
				}
			}
			
			int max_times = this.get_maximal_times();
			for(Integer act_times : exec_times) {
				if(act_times > max_times) {
					return Boolean.FALSE;
				}
			}
			return Boolean.TRUE;
		}
	}
	
}
