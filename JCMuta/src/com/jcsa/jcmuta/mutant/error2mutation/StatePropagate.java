package com.jcsa.jcmuta.mutant.error2mutation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

/**
 * Used to propagate the state errors in program under test.
 * @author yukimula
 *
 */
public abstract class StatePropagate {
	
	/** whether to optimize the constraint on propagation edge **/
	private boolean opt_constraint;
	
	/** constructor **/
	public StatePropagate() { this.opt_constraint = false; }
	
	/* public APIs */
	/**
	 * open the optimization of symbolic constraint
	 */
	public void open_optimize_constraint() { this.opt_constraint = true; }
	/**
	 * close the optimization of symbolic constraint
	 */
	public void close_optimize_constraint() { this.opt_constraint = false; }
	/**
	 * Propagate from the source to the target w.r.t. the given location
	 * @param source
	 * @param cir_target
	 * @return the set of propagation edges from the source
	 * @throws Exception
	 */
	public Collection<StateErrorEdge> propagate(StateErrorNode source, CirNode cir_target) throws Exception {
		if(source != null && source.get_location() != null) {
			Map<StateError, StateConstraints> output = new HashMap<StateError, StateConstraints>();
			Collection<StateError> errors = this.get_representative_errors(source.get_errors());
			for(StateError error : errors) {
				this.propagate_state_error(error, cir_target, source.get_graph(), output);
			}
			
			List<StateErrorEdge> propagation_edges = new ArrayList<StateErrorEdge>();
			for(StateError new_error : output.keySet()) {
				StateConstraints constraints = output.get(new_error);
				StateErrorNode target = source.get_graph().new_node(new_error);
				StateErrorEdge edge = source.propagate(target, constraints);
				propagation_edges.add(edge);
			}
			
			return propagation_edges;
		}
		else {
			return null;
		}
	}
	
	/* toolkit methods */
	/**
	 * 
	 * @param errors
	 * @return
	 * @throws Exception
	 */
	protected Collection<StateError> get_representative_errors(Iterable<StateError> errors) throws Exception {
		int error_level = -1;
		for(StateError error : errors) {
			if(error.get_error_level() >= error_level) {
				error_level = error.get_error_level();
			}
		}
		
		List<StateError> results = new ArrayList<StateError>();
		for(StateError error : errors) {
			if(error.get_error_level() == error_level) {
				results.add(error);
			}
		}
		return results;
	}
	/**
	 * add the constraint to the tail of the constraints sequence
	 * @param constraints
	 * @param constraint
	 * @throws Exception
	 */
	protected void add_constraint(StateConstraints constraints, 
			CirStatement statement, SymExpression constraint) throws Exception {
		StateEvaluation.add_constraint(constraints, statement, constraint, this.opt_constraint);
	}
	/**
	 * generate the state error from which it is generated and the constraint required
	 * @param error
	 * @param cir_target
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	protected void propagate_state_error(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		switch(error.get_type()) {
		case execute:		this.propagate_execute(error, cir_target, graph, output); 		break;
		case not_execute:	this.propagate_not_execute(error, cir_target, graph, output);	break;
		case execute_for:	this.propagate_execute_for(error, cir_target, graph, output);	break;
		case set_bool:		this.propagate_set_bool(error, cir_target, graph, output);		break;
		case chg_bool:		this.propagate_chg_bool(error, cir_target, graph, output); 		break;
		case set_numb:		this.propagate_set_numb(error, cir_target, graph, output); 		break;
		case neg_numb:		this.propagate_neg_numb(error, cir_target, graph, output); 		break;
		case xor_numb:		this.propagate_xor_numb(error, cir_target, graph, output); 		break;
		case rsv_numb:		this.propagate_rsv_numb(error, cir_target, graph, output); 		break;
		case dif_numb:		this.propagate_dif_numb(error, cir_target, graph, output); 		break;
		case inc_numb:		this.propagate_inc_numb(error, cir_target, graph, output); 		break;
		case dec_numb:		this.propagate_dec_numb(error, cir_target, graph, output); 		break;
		case chg_numb:		this.propagate_chg_numb(error, cir_target, graph, output); 		break;
		case dif_addr:		this.propagate_dif_addr(error, cir_target, graph, output); 		break;
		case set_addr:		this.propagate_set_addr(error, cir_target, graph, output); 		break;
		case chg_addr:		this.propagate_chg_addr(error, cir_target, graph, output); 		break;
		case mut_expr:		this.propagate_mut_expr(error, cir_target, graph, output); 		break;
		case mut_refer:		this.propagate_mut_refer(error, cir_target, graph, output); 	break;
		default: 			break;
		}	
	}
	
	/* implementation method */
	protected abstract void propagate_execute(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	protected abstract void propagate_not_execute(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	protected abstract void propagate_execute_for(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	protected abstract void propagate_set_bool(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	protected abstract void propagate_chg_bool(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	protected abstract void propagate_set_numb(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	protected abstract void propagate_neg_numb(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	protected abstract void propagate_xor_numb(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	protected abstract void propagate_rsv_numb(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	protected abstract void propagate_dif_numb(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	protected abstract void propagate_inc_numb(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	protected abstract void propagate_dec_numb(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	protected abstract void propagate_chg_numb(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	protected abstract void propagate_dif_addr(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	protected abstract void propagate_set_addr(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	protected abstract void propagate_chg_addr(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	protected abstract void propagate_mut_expr(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	protected abstract void propagate_mut_refer(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	
}
