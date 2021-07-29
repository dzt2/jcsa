package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It implements factory and evaluation methods.
 * 
 * @author yukimula
 *
 */
public class CirAttributes {
	
	/* factory methods */
	/**
	 * @param execution
	 * @param expression
	 * @param value
	 * @return {condition; execution; statement; expression;}
	 * @throws Exception
	 */
	public static CirConstraint new_constraint(CirExecution execution, 
				Object expression, boolean value) throws Exception {
		return new CirConstraint(execution, SymbolFactory.sym_condition(expression, value));
	}
	/**
	 * @param execution
	 * @param times
	 * @return
	 * @throws Exception
	 */
	public static CirCoverCount new_coverage_count(CirExecution execution, int times) throws Exception {
		return new CirCoverCount(execution, SymbolFactory.sym_constant(Integer.valueOf(times)));
	}
	/**
	 * @param execution
	 * @return {trp_error; execution; statement; true;}
	 * @throws Exception
	 */
	public static CirTrapsError new_trap_error(CirExecution execution) throws Exception {
		return new CirTrapsError(execution, SymbolFactory.sym_constant(Boolean.TRUE));
	}
	/**
	 * @param execution
	 * @param execute
	 * @return {blk_error; execution; statement; true|false;}
	 * @throws Exception
	 */
	public static CirBlockError new_block_error(CirExecution execution, boolean execute) throws Exception {
		return new CirBlockError(execution, SymbolFactory.sym_constant(Boolean.valueOf(execute)));
	}
	/**
	 * @param orig_flow
	 * @param muta_flow
	 * @return {flw_error; if_exec; orig_target; muta_target;}
	 * @throws Exception
	 */
	public static CirFlowsError new_flow_error(CirExecutionFlow orig_flow, CirExecutionFlow muta_flow) throws Exception {
		return new CirFlowsError(orig_flow.get_source(), 
				orig_flow.get_target().get_statement(),
				SymbolFactory.sym_expression(muta_flow.get_target()));
	}
	/**
	 * @param orig_expression
	 * @param muta_expression
	 * @return {val_error; execution; expression; value;}
	 * @throws Exception
	 */
	public static CirValueError new_value_error(CirExpression orig_expression, Object muta_expression) throws Exception {
		return new CirValueError(orig_expression.get_tree().get_localizer().
				get_execution(orig_expression.statement_of()),
				orig_expression, SymbolFactory.sym_expression(muta_expression));
	}
	/**
	 * @param orig_expression
	 * @param muta_expression
	 * @return {ref_error; execution; expression; value;}
	 * @throws Exception
	 */
	public static CirReferError new_refer_error(CirExpression orig_expression, Object muta_expression) throws Exception {
		return new CirReferError(orig_expression.get_tree().get_localizer().
				get_execution(orig_expression.statement_of()),
				orig_expression, SymbolFactory.sym_expression(muta_expression));
	}
	/**
	 * @param orig_expression
	 * @param muta_expression
	 * @return {val_error; execution; expression; value;}
	 * @throws Exception
	 */
	public static CirStateError new_state_error(CirExpression orig_expression, Object muta_expression) throws Exception {
		return new CirStateError(orig_expression.get_tree().get_localizer().
				get_execution(orig_expression.statement_of()),
				orig_expression, SymbolFactory.sym_expression(muta_expression));
	}
	/**
	 * @param constraint
	 * @param init_error
	 * @return {constraint : init_error}
	 * @throws Exception
	 */
	public static CirMutation new_cir_mutation(CirAttribute constraint, CirAttribute init_error) throws Exception {
		return new CirMutation(constraint, init_error);
	}
	
}
