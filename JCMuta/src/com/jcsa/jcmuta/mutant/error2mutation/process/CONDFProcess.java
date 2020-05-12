package com.jcsa.jcmuta.mutant.error2mutation.process;

import java.util.Map;

import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.StateProcess;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

/**
 * From condition expression to the statement in its false branch
 * @author yukimula
 *
 */
public class CONDFProcess extends StateProcess {

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

	@Override
	protected void propagate_set_bool(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		Object parameter = this.get_number(error.get_operand(1));
		
		/** condition == false **/
		if(StateEvaluation.is_zero_number(parameter)) 
			this.propagate_set_false(error, cir_target, graph, output);
		/** condition == true **/
		else { this.propagate_set_true(error, cir_target, graph, output); }
	}

	@Override
	protected void propagate_chg_bool(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirStatement target_statement = (CirStatement) cir_target;
		StateConstraints constraints; SymExpression constraint; 
		CirExpression condition = (CirExpression) error.get_operand(0);
		
		/** condition: false --> true --> execute(stmt) **/
		constraint = StateEvaluation.new_condition(condition, false);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, condition.statement_of(), constraint);
		output.put(graph.get_error_set().execute(target_statement), constraints);
		
		/** condition: false --> true --> execute(stmt) **/
		constraint = StateEvaluation.new_condition(condition, true);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, condition.statement_of(), constraint);
		output.put(graph.get_error_set().not_execute(target_statement), constraints);
	}

	@Override
	protected void propagate_set_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		this.propagate_set_bool(error, cir_target, graph, output);
	}
	
	private void propagate_set_true(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirStatement target_statement = (CirStatement) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		output.put(graph.get_error_set().not_execute(target_statement), constraints);
	}
	
	private void propagate_set_false(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirStatement target_statement = (CirStatement) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		output.put(graph.get_error_set().execute(target_statement), constraints);
	}
	
	@Override
	protected void propagate_neg_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		/** equivalent mutant **/
	}

	@Override
	protected void propagate_xor_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		this.propagate_chg_bool(error, cir_target, graph, output);
	}

	@Override
	protected void propagate_rsv_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		this.propagate_set_true(error, cir_target, graph, output);
	}

	@Override
	protected void propagate_dif_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		Object parameter = this.get_number(error.get_operand(1));
		if(StateEvaluation.is_positive_number(parameter)) {
			this.propagate_inc_numb(error, cir_target, graph, output);
		}
		else if(StateEvaluation.is_negative_number(parameter)) {
			this.propagate_dec_numb(error, cir_target, graph, output);
		}
	}

	@Override
	protected void propagate_inc_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		this.propagate_set_true(error, cir_target, graph, output);
	}

	@Override
	protected void propagate_dec_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		this.propagate_chg_bool(error, cir_target, graph, output);
	}

	@Override
	protected void propagate_chg_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		this.propagate_chg_bool(error, cir_target, graph, output);
	}

	@Override
	protected void propagate_dif_addr(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		this.propagate_dif_numb(error, cir_target, graph, output);
	}

	@Override
	protected void propagate_set_addr(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		this.propagate_set_bool(error, cir_target, graph, output);
	}

	@Override
	protected void propagate_chg_addr(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		this.propagate_chg_bool(error, cir_target, graph, output);
	}

	@Override
	protected void propagate_mut_expr(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		this.propagate_chg_bool(error, cir_target, graph, output);
	}

	@Override
	protected void propagate_mut_refer(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		this.propagate_mut_expr(error, cir_target, graph, output);
	} 

}
