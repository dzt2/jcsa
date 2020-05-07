package com.jcsa.jcmuta.mutant.error2mutation.infection;

import java.util.Map;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.StateInfection;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

/**
 * OAAN, OABN, OALN, OARN
 * OBAN, OBBN, OBLN, OBRN
 * OLAN, OLBN, OLLN, OLRN
 * ORAN, ORBN, ORLN, ORRN
 * @author yukimula
 *
 */
public abstract class OPRTInfection extends StateInfection {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_beg_statement(cir_tree, this.get_location(mutation));
	}
	
	/**
	 * get the symbolic expression as mutated description
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @return null if the type fails to match
	 * @throws Exception
	 */
	protected abstract SymExpression muta_expression(CirExpression expression, 
			CirExpression loperand, CirExpression roperand) throws Exception;
	
	/**
	 * perform partial evaluation
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @return false if the errors are undecidable
	 * @throws Exception
	 */
	protected abstract boolean partial_evaluate(CirExpression expression,
			CirExpression loperand, CirExpression roperand, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception;
	
	/**
	 * perform undecidable evaluation when no operand is constant
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @return
	 * @throws Exception
	 */
	protected abstract boolean symbolic_evaluate(CirExpression expression,
			CirExpression loperand, CirExpression roperand, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception;
	
	@Override
	protected void get_infections(CirTree cir_tree, AstMutation mutation, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		AstBinaryExpression location = (AstBinaryExpression) this.get_location(mutation);
		CirExpression expression = this.get_result_of(cir_tree, location);
		CirExpression loperand = this.get_result_of(cir_tree, 
				CTypeAnalyzer.get_expression_of(location.get_loperand()));
		CirExpression roperand = this.get_result_of(cir_tree, 
				CTypeAnalyzer.get_expression_of(location.get_roperand()));
		SymExpression muta_expression = this.muta_expression(expression, loperand, roperand);
		
		/** CASE-1. data type matching failed **/
		if(muta_expression == null) {
			output.put(graph.get_error_set().syntax_error(), StateEvaluation.get_conjunctions());
		}
		/** CASE-2. perform complete evaluation on mutation **/
		else if(this.complete_evaluate(expression, muta_expression, graph, output)) { return; }
		/** CASE-3. perform partial evaluation based on operands **/
		else if(this.partial_evaluate(expression, loperand, roperand, graph, output)) { return; }
		/** CASE-4. perform symbolic evaluation based on constraints **/
		else if(this.symbolic_evaluate(expression, loperand, roperand, graph, output)) return;
		/** DEFAULT. Impossible Case Occurs during translation **/
		else {
			throw new RuntimeException("Unable to solve the mutation: " + mutation.toString());
		}
	}

}
