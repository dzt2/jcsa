package com.jcsa.jcmuta.mutant.error2mutation.process;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmuta.mutant.error2mutation.PathConditions;
import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.StateProcess;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

/**
 * definition --> usage w.r.t. path constraint
 * @author yukimula
 *
 */
public class DEFUSEProcess extends StateProcess {

	@Override
	protected void propagate_execute(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
	}

	@Override
	protected void propagate_not_execute(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
	}

	@Override
	protected void propagate_execute_for(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
	}
	
	/**
	 * get the path condition from definition point to the usage point 
	 * @param def
	 * @param use
	 * @return
	 * @throws Exception
	 */
	private StateConstraints get_path_conditions(CirExpression def, CirExpression use) throws Exception {
		Collection<List<CirExecutionFlow>> paths = PathConditions.
				paths_of(def.statement_of(), use.statement_of());
		Set<CirExecutionFlow> flows = PathConditions.must_be_path(paths);
		
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		for(CirExecutionFlow flow : flows) {
			CirStatement source = flow.get_source().get_statement();
			CirExpression condition = null; boolean parameter = true;
			
			switch(flow.get_type()) {
			case true_flow:
			{
				if(source instanceof CirIfStatement) {
					condition = ((CirIfStatement) source).get_condition();
				}
				else if(source instanceof CirCaseStatement) {
					condition = ((CirCaseStatement) source).get_condition();
				}
				parameter = true;
			}
			break;
			case fals_flow:
			{
				if(source instanceof CirIfStatement) {
					condition = ((CirIfStatement) source).get_condition();
				}
				else if(source instanceof CirCaseStatement) {
					condition = ((CirCaseStatement) source).get_condition();
				}
				parameter = false;
			}
			break;
			default: break;
			}
			
			if(condition != null) {
				SymExpression constraint = StateEvaluation.new_condition(condition, parameter);
				this.add_constraint(constraints, source, constraint);
			}
		}
		
		return constraints;
	}
	
	@Override
	protected void propagate_set_bool(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression lvalue = (CirExpression) cir_target;
		CirExpression rvalue = (CirExpression) error.get_operand(0);
		Boolean parameter = (Boolean) error.get_operand(1);
		output.put(graph.get_error_set().set_bool(lvalue, parameter.booleanValue()), 
				this.get_path_conditions(rvalue, lvalue));
	}

	@Override
	protected void propagate_chg_bool(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression lvalue = (CirExpression) cir_target;
		CirExpression rvalue = (CirExpression) error.get_operand(0);
		output.put(graph.get_error_set().chg_bool(lvalue), this.get_path_conditions(rvalue, lvalue));
	}

	@Override
	protected void propagate_set_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression lvalue = (CirExpression) cir_target;
		CirExpression rvalue = (CirExpression) error.get_operand(0);
		Object parameter = error.get_operand(1);
		
		if(parameter instanceof Long) {
			output.put(graph.get_error_set().set_numb(lvalue, ((Long) parameter).longValue()), 
					this.get_path_conditions(rvalue, lvalue));
		}
		else {
			output.put(graph.get_error_set().set_numb(lvalue, ((Double) parameter).doubleValue()), 
					this.get_path_conditions(rvalue, lvalue));
		}
	}

	@Override
	protected void propagate_neg_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression lvalue = (CirExpression) cir_target;
		CirExpression rvalue = (CirExpression) error.get_operand(0);
		output.put(graph.get_error_set().neg_numb(lvalue), this.get_path_conditions(rvalue, lvalue));
	}

	@Override
	protected void propagate_xor_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression lvalue = (CirExpression) cir_target;
		Long parameter = (Long) error.get_operand(1);
		CirExpression rvalue = (CirExpression) error.get_operand(0);
		output.put(graph.get_error_set().xor_numb(lvalue, parameter.longValue()), 
				this.get_path_conditions(rvalue, lvalue));
	}

	@Override
	protected void propagate_rsv_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression lvalue = (CirExpression) cir_target;
		CirExpression rvalue = (CirExpression) error.get_operand(0);
		output.put(graph.get_error_set().rsv_numb(lvalue), this.get_path_conditions(rvalue, lvalue));
	}

	@Override
	protected void propagate_dif_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression lvalue = (CirExpression) cir_target;
		CirExpression rvalue = (CirExpression) error.get_operand(0);
		Object parameter = error.get_operand(1);
		
		if(parameter instanceof Long) {
			output.put(graph.get_error_set().dif_numb(lvalue, ((Long) parameter).longValue()), 
					this.get_path_conditions(rvalue, lvalue));
		}
		else {
			output.put(graph.get_error_set().dif_numb(lvalue, ((Double) parameter).doubleValue()), 
					this.get_path_conditions(rvalue, lvalue));
		}
	}

	@Override
	protected void propagate_inc_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression lvalue = (CirExpression) cir_target;
		CirExpression rvalue = (CirExpression) error.get_operand(0);
		output.put(graph.get_error_set().inc_numb(lvalue), this.get_path_conditions(rvalue, lvalue));
	}

	@Override
	protected void propagate_dec_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression lvalue = (CirExpression) cir_target;
		CirExpression rvalue = (CirExpression) error.get_operand(0);
		output.put(graph.get_error_set().dec_numb(lvalue), this.get_path_conditions(rvalue, lvalue));
	}

	@Override
	protected void propagate_chg_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression lvalue = (CirExpression) cir_target;
		CirExpression rvalue = (CirExpression) error.get_operand(0);
		output.put(graph.get_error_set().chg_numb(lvalue), this.get_path_conditions(rvalue, lvalue));
	}

	@Override
	protected void propagate_dif_addr(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression lvalue = (CirExpression) cir_target;
		CirExpression rvalue = (CirExpression) error.get_operand(0);
		Long parameter = (Long) error.get_operand(1);
		output.put(graph.get_error_set().dif_addr(lvalue, ((Long) parameter).longValue()), 
				this.get_path_conditions(rvalue, lvalue));
	}

	@Override
	protected void propagate_set_addr(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression lvalue = (CirExpression) cir_target;
		CirExpression rvalue = (CirExpression) error.get_operand(0);
		String parameter = (String) error.get_operand(1);
		output.put(graph.get_error_set().set_addr(lvalue, parameter), 
				this.get_path_conditions(rvalue, lvalue));
	}

	@Override
	protected void propagate_chg_addr(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression lvalue = (CirExpression) cir_target;
		CirExpression rvalue = (CirExpression) error.get_operand(0);
		output.put(graph.get_error_set().chg_addr(lvalue), this.get_path_conditions(rvalue, lvalue));
	}

	@Override
	protected void propagate_mut_expr(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression lvalue = (CirExpression) cir_target;
		CirExpression rvalue = (CirExpression) error.get_operand(0);
		output.put(graph.get_error_set().mut_expr(lvalue), this.get_path_conditions(rvalue, lvalue));
	}

	@Override
	protected void propagate_mut_refer(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression lvalue = (CirExpression) cir_target;
		CirExpression rvalue = (CirExpression) error.get_operand(0);
		output.put(graph.get_error_set().mut_expr(lvalue), this.get_path_conditions(rvalue, lvalue));
	}

}
