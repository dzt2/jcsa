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
 * [stmt:statement] <== cov_time(false, int_times)
 * 
 * @author yukimula
 *
 */
public class CirReachTimesState extends CirConditionState {

	protected CirReachTimesState(CirExecution point, int int_times) throws Exception {
		super(point, CirStateValue.cov_time(false, int_times));
	}
	
	/**
	 * @return the minimal times that the statement should be executed
	 */
	public int get_minimal_times() {
		return ((SymbolConstant) this.get_roperand()).get_int();
	}
	
	@Override
	public CirConditionState normalize(SymbolProcess context) throws Exception {
		CirExecutionPath path = this.get_previous_path();
		int minimal_times = this.get_minimal_times();
		return CirAbstractState.cov_time(path.get_source(), minimal_times);
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
			
			int min_times = this.get_minimal_times();
			for(Integer act_times : exec_times) {
				if(act_times >= min_times) {
					return Boolean.TRUE;
				}
			}
			return Boolean.FALSE;
		}
	}
	
}
