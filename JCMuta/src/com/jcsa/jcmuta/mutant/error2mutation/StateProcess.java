package com.jcsa.jcmuta.mutant.error2mutation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

/**
 * The state processor is used to translate from state error to new state error(s).
 * 
 * @author yukimula
 *
 */
public abstract class StateProcess {
	
	/** whether to optimize the constraint on propagation edge **/
	private boolean opt_constraint;
	/** random machine to generate random number for address **/
	private static final Random random_machine = new Random(System.currentTimeMillis());
	
	/** constructor **/
	public StateProcess() {  }
	
	/* public APIs */
	/**
	 * open the optimization of symbolic constraint
	 */
	public void open_optimize_constraint() { this.opt_constraint = true; }
	/**
	 * close the optimization of symbolic constraint
	 */
	public void close_optimize_constraint() { this.opt_constraint = false; }
	
	/* process methods */
	/**
	 * generate the propagation from the source node to the errors in the target
	 * @param source
	 * @param cir_target
	 * @return
	 * @throws Exception
	 */
	public Collection<StateErrorEdge> process(StateErrorNode source, CirNode cir_target) throws Exception {
		List<StateErrorEdge> propagation_edges = new ArrayList<StateErrorEdge>();
		
		if(source != null && source.get_location() != null) {
			/* get the representative set of state errors within the source node */
			Map<StateError, StateConstraints> output = new HashMap<StateError, StateConstraints>();
			Collection<StateError> errors = this.get_representative_errors(source.get_errors());
			
			/* generate the new state errors in the target node with constraints */
			for(StateError error : errors) {
				this.propagate_state_error(error, cir_target, source.get_graph(), output);
			}
			
			/* create new state error nodes w.r.t. the error created in a graph */
			for(StateError new_error : output.keySet()) {
				StateConstraints constraints = output.get(new_error);
				StateErrorNode target = source.get_graph().new_node(new_error);
				StateErrorEdge edge = source.propagate(target, constraints);
				propagation_edges.add(edge);
			}
		}
		
		return propagation_edges;
	}
	/**
	 * get the representative set of errors in the node
	 * @param errors
	 * @return
	 * @throws Exception
	 */
	private Collection<StateError> get_representative_errors(Iterable<StateError> errors) throws Exception {
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
	 * generate the state error from which it is generated and the constraint required
	 * @param error
	 * @param cir_target
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void propagate_state_error(StateError error, CirNode cir_target, 
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
	/**
	 * execute(stmt)
	 * @param error
	 * @param cir_target
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	protected abstract void propagate_execute(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	/**
	 * not_execute(stmt)
	 * @param error
	 * @param cir_target
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	protected abstract void propagate_not_execute(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	/**
	 * execute_for(stmt, loop_times)
	 * @param error
	 * @param cir_target
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	protected abstract void propagate_execute_for(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	/**
	 * set_bool(expression, boolean)
	 * @param error
	 * @param cir_target
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	protected abstract void propagate_set_bool(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	/**
	 * chg_bool(expression)
	 * @param error
	 * @param cir_target
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	protected abstract void propagate_chg_bool(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	/**
	 * set_numb(expression, int|double)
	 * @param error
	 * @param cir_target
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	protected abstract void propagate_set_numb(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	/**
	 * neg_numb(expression)
	 * @param error
	 * @param cir_target
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	protected abstract void propagate_neg_numb(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	/**
	 * xor_numb(expression, int);
	 * @param error
	 * @param cir_target
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	protected abstract void propagate_xor_numb(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	/**
	 * rsv_numb(expression)
	 * @param error
	 * @param cir_target
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	protected abstract void propagate_rsv_numb(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	/**
	 * dif_numb(expression, int|double)
	 * @param error
	 * @param cir_target
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	protected abstract void propagate_dif_numb(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	/**
	 * inc_numb(expression)
	 * @param error
	 * @param cir_target
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	protected abstract void propagate_inc_numb(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	/**
	 * dec_numb(expression)
	 * @param error
	 * @param cir_target
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	protected abstract void propagate_dec_numb(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	/**
	 * chg_numb(expression)
	 * @param error
	 * @param cir_target
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	protected abstract void propagate_chg_numb(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	/**
	 * dif_addr(expression, long)
	 * @param error
	 * @param cir_target
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	protected abstract void propagate_dif_addr(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	/**
	 * set_addr(expression, String)
	 * @param error
	 * @param cir_target
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	protected abstract void propagate_set_addr(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	/**
	 * chg_addr(expression)
	 * @param error
	 * @param cir_target
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	protected abstract void propagate_chg_addr(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	/**
	 * mut_expr(expression)
	 * @param error
	 * @param cir_target
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	protected abstract void propagate_mut_expr(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	/**
	 * mut_refer(expression)
	 * @param error
	 * @param cir_target
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	protected abstract void propagate_mut_refer(StateError error, CirNode cir_target, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	
	/* toolkit methods */
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
	 * 	boolean 	--> long
	 * 	long		--> long
	 * 	double		--> double
	 * 	string		--> long
	 * @param operand
	 * @return
	 * @throws Exception
	 */
	protected Object get_number(Object operand) throws Exception {
		if(operand instanceof Boolean) {
			if(((Boolean) operand).booleanValue()) 
				return Long.valueOf(1L);
			else
				return Long.valueOf(0L);
		}
		else if(operand instanceof Long) {
			return operand;
		}
		else if(operand instanceof Double) {
			return operand;
		}
		else if(operand instanceof String) {
			if(operand.equals(StateError.NullPointer)) {
				return Long.valueOf(0L);
			}
			else {
				return Long.valueOf(Math.abs(random_machine.nextLong()) % (1024 * 16) + 1);
			}
		}
		else {
			throw new IllegalArgumentException("Unknown type: " + operand);
		}
	}
	protected Object arith_add(Object loperand, Object roperand) throws Exception {
		Object lvalue = this.get_number(loperand);
		Object rvalue = this.get_number(roperand);
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x + y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x + y;
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x + y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x + y;
			}
		}
	}
	protected Object arith_sub(Object loperand, Object roperand) throws Exception {
		Object lvalue = this.get_number(loperand);
		Object rvalue = this.get_number(roperand);
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x - y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x - y;
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x - y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x - y;
			}
		}
	}
	protected Object arith_mul(Object loperand, Object roperand) throws Exception {
		Object lvalue = this.get_number(loperand);
		Object rvalue = this.get_number(roperand);
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x * y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x * y;
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x * y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x * y;
			}
		}
	}
	protected Object arith_div(Object loperand, Object roperand) throws Exception {
		Object lvalue = this.get_number(loperand);
		Object rvalue = this.get_number(roperand);
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x / y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x / y;
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x / y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x / y;
			}
		}
	}
	protected Object arith_mod(Object loperand, Object roperand) throws Exception {
		Object lvalue = this.get_number(loperand);
		Object rvalue = this.get_number(roperand);
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x % y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x % y;
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x % y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x % y;
			}
		}
	}
	protected Object bitws_and(Object loperand, Object roperand) throws Exception {
		Object lvalue = this.get_number(loperand);
		Object rvalue = this.get_number(roperand);
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x & y;
			}
			else {
				throw new IllegalArgumentException("Unsupport: " + lvalue + " and " + rvalue);
			}
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + lvalue + " and " + rvalue);
		}
	}
	protected Object bitws_ior(Object loperand, Object roperand) throws Exception {
		Object lvalue = this.get_number(loperand);
		Object rvalue = this.get_number(roperand);
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x | y;
			}
			else {
				throw new IllegalArgumentException("Unsupport: " + lvalue + " and " + rvalue);
			}
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + lvalue + " and " + rvalue);
		}
	}
	protected Object bitws_xor(Object loperand, Object roperand) throws Exception {
		Object lvalue = this.get_number(loperand);
		Object rvalue = this.get_number(roperand);
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x ^ y;
			}
			else {
				throw new IllegalArgumentException("Unsupport: " + lvalue + " and " + rvalue);
			}
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + lvalue + " and " + rvalue);
		}
	}
	protected Object bitws_lsh(Object loperand, Object roperand) throws Exception {
		Object lvalue = this.get_number(loperand);
		Object rvalue = this.get_number(roperand);
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x << y;
			}
			else {
				throw new IllegalArgumentException("Unsupport: " + lvalue + " and " + rvalue);
			}
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + lvalue + " and " + rvalue);
		}
	}
	protected Object bitws_rsh(Object loperand, Object roperand) throws Exception {
		Object lvalue = this.get_number(loperand);
		Object rvalue = this.get_number(roperand);
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x >> y;
			}
			else {
				throw new IllegalArgumentException("Unsupport: " + lvalue + " and " + rvalue);
			}
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + lvalue + " and " + rvalue);
		}
	}
	protected Object logic_and(Object loperand, Object roperand) throws Exception {
		Object lvalue = this.get_number(loperand);
		Object rvalue = this.get_number(roperand);
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return (x != 0) && (y != 0);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return (x != 0) && (y != 0);
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return (x != 0) && (y != 0);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return (x != 0) && (y != 0);
			}
		}
	}
	protected Object logic_ior(Object loperand, Object roperand) throws Exception {
		Object lvalue = this.get_number(loperand);
		Object rvalue = this.get_number(roperand);
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return (x != 0) || (y != 0);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return (x != 0) || (y != 0);
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return (x != 0) || (y != 0);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return (x != 0) || (y != 0);
			}
		}
	}
	protected Object greater_tn(Object loperand, Object roperand) throws Exception {
		Object lvalue = this.get_number(loperand);
		Object rvalue = this.get_number(roperand);
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x > y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x > y;
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x > y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x > y;
			}
		}
	}
	protected Object greater_eq(Object loperand, Object roperand) throws Exception {
		Object lvalue = this.get_number(loperand);
		Object rvalue = this.get_number(roperand);
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x >= y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x >= y;
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x >= y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x >= y;
			}
		}
	}
	protected Object smaller_tn(Object loperand, Object roperand) throws Exception {
		Object lvalue = this.get_number(loperand);
		Object rvalue = this.get_number(roperand);
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x < y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x < y;
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x < y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x < y;
			}
		}
	}
	protected Object smaller_eq(Object loperand, Object roperand) throws Exception {
		Object lvalue = this.get_number(loperand);
		Object rvalue = this.get_number(roperand);
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x <= y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x <= y;
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x <= y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x <= y;
			}
		}
	}
	protected Object equal_with(Object loperand, Object roperand) throws Exception {
		Object lvalue = this.get_number(loperand);
		Object rvalue = this.get_number(roperand);
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x == y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x == y;
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x == y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x == y;
			}
		}
	}
	protected Object not_equals(Object loperand, Object roperand) throws Exception {
		Object lvalue = this.get_number(loperand);
		Object rvalue = this.get_number(roperand);
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x != y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x != y;
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				return x != y;
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				return x != y;
			}
		}
	}
	
}
