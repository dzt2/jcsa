package com.jcsa.jcmutest.backups;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;
import com.jcsa.jcparse.test.state.CStateContexts;

/**
 * It performs the static error propagations through expressions within
 * a statement.
 * 
 * @author yukimula
 *
 */
public class SecExpressionPropagators {
	
	/* expression error propagators */
	private SecExpressionPropagator arith_add_lpropagator = new SecArithAddLPropagator();
	private SecExpressionPropagator arith_add_rpropagator = new SecArithAddRPropagator();
	private SecExpressionPropagator arith_sub_lpropagator = new SecArithSubLPropagator();
	private SecExpressionPropagator arith_sub_rpropagator = new SecArithSubRPropagator();
	private SecExpressionPropagator arith_mul_lpropagator = new SecArithMulLPropagator();
	private SecExpressionPropagator arith_mul_rpropagator = new SecArithMulRPropagator();
	private SecExpressionPropagator arith_div_lpropagator = new SecArithDivLPropagator();
	private SecExpressionPropagator arith_div_rpropagator = new SecArithDivRPropagator();
	private SecExpressionPropagator arith_mod_lpropagator = new SecArithModLPropagator();
	private SecExpressionPropagator arith_mod_rpropagator = new SecArithModRPropagator();
	private SecExpressionPropagator bitws_and_lpropagator = new SecBitwsAndLPropagator();
	private SecExpressionPropagator bitws_and_rpropagator = new SecBitwsAndRPropagator();
	private SecExpressionPropagator bitws_ior_lpropagator = new SecBitwsIorLPropagator();
	private SecExpressionPropagator bitws_ior_rpropagator = new SecBitwsIorRPropagator();
	private SecExpressionPropagator bitws_xor_lpropagator = new SecBitwsXorLPropagator();
	private SecExpressionPropagator bitws_xor_rpropagator = new SecBitwsXorRPropagator();
	private SecExpressionPropagator bitws_lsh_lpropagator = new SecBitwsLshLPropagator();
	private SecExpressionPropagator bitws_lsh_rpropagator = new SecBitwsLshRPropagator();
	private SecExpressionPropagator bitws_rsh_lpropagator = new SecBitwsRshLPropagator();
	private SecExpressionPropagator bitws_rsh_rpropagator = new SecBitwsRshRPropagator();
	private SecExpressionPropagator greater_tn_lpropagator= new SecGreaterTnLPropagator();
	private SecExpressionPropagator greater_tn_rpropagator= new SecGreaterTnRPropagator();
	private SecExpressionPropagator greater_eq_lpropagator= new SecGreaterEqLPropagator();
	private SecExpressionPropagator greater_eq_rpropagator= new SecGreaterEqRPropagator();
	private SecExpressionPropagator smaller_tn_lpropagator= new SecSmallerTnLPropagator();
	private SecExpressionPropagator smaller_tn_rpropagator= new SecSmallerTnRPropagator();
	private SecExpressionPropagator smaller_eq_lpropagator= new SecSmallerEqLPropagator();
	private SecExpressionPropagator smaller_eq_rpropagator= new SecSmallerEqRPropagator();
	private SecExpressionPropagator equal_with_lpropagator= new SecEqualWithLPropagator();
	private SecExpressionPropagator equal_with_rpropagator= new SecEqualWithRPropagator();
	private SecExpressionPropagator not_equals_lpropagator= new SecNotEqualsLPropagator();
	private SecExpressionPropagator not_equals_rpropagator= new SecNotEqualsRPropagator();
	private SecExpressionPropagator arith_neg_propagator = new SecArithNegPropagator();
	private SecExpressionPropagator bitws_rsv_propagator = new SecBitwsRsvPropagator();
	private SecExpressionPropagator logic_not_propagator = new SecLogicNotPropagator();
	private SecExpressionPropagator type_cast_propagator = new SecTypeCastPropagator();
	private SecExpressionPropagator address_of_propagator = new SecAddressOfPropagator();
	private SecExpressionPropagator dereference_propagator = new SecDereferencePropagator();
	private SecExpressionPropagator assignment_rpropagator = new SecAssignRLPropagator();
	private SecExpressionPropagator argument_in_propagator = new SecArgumentsPropagator();
	
	/* constructor */
	private SecExpressionPropagators() { }
	private static final SecExpressionPropagators propagators = new SecExpressionPropagators();
	
	/**
	 * @param error
	 * @param contexts
	 * @return whether the error is a valid expression error w.r.t. the given contexts
	 * @throws Exception
	 */
	private boolean verify_propagation(SecStateError error, CStateContexts contexts) throws Exception {
		if(error instanceof SecExpressionError) {
			for(SecStateError sub_error : error.extend(contexts)) {
				if(sub_error instanceof SecUniqueError) {
					return false;
				}
			}
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * @param source
	 * @return generate the propagation edges from the source using syntax-directed algorithms
	 * @throws Exception
	 */
	private Iterable<SecStateEdge> propagate_on(SecStateNode source, CStateContexts contexts) throws Exception {
		if(source == null || !source.is_state_error())
			throw new IllegalArgumentException("Invalid source as null");
		else if(this.verify_propagation(source.get_state_error(), contexts)){
			SecExpressionError source_error = (SecExpressionError) source.get_state_error();
			CirExpression orig_expression = 
						source_error.get_orig_expression().get_expression().get_cir_source();
			CirNode cir_parent = orig_expression.get_parent();
			CirStatement statement = orig_expression.statement_of();
			
			Map<SecStateError, SecConstraint> results; SecStateEdgeType edge_type;
			if(cir_parent instanceof CirDeferExpression) {
				results = this.dereference_propagator.propagate(statement, cir_parent, source_error);
				edge_type = SecStateEdgeType.op_expr;
			}
			else if(cir_parent instanceof CirAddressExpression) {
				results = this.address_of_propagator.propagate(statement, cir_parent, source_error);
				edge_type = SecStateEdgeType.op_expr;
			}
			else if(cir_parent instanceof CirCastExpression) {
				results = this.type_cast_propagator.propagate(statement, cir_parent, source_error);
				edge_type = SecStateEdgeType.op_expr;
			}
			else if(cir_parent instanceof CirComputeExpression) {
				boolean loperand = (((CirComputeExpression) cir_parent).get_operand(0) == orig_expression);
				edge_type = SecStateEdgeType.op_expr;
				switch(((CirComputeExpression) cir_parent).get_operator()) {
				case negative:
					results = this.arith_neg_propagator.propagate(statement, cir_parent, source_error);
					break;
				case bit_not:
					results = this.bitws_rsv_propagator.propagate(statement, cir_parent, source_error);
					break;
				case logic_not:
					results = this.logic_not_propagator.propagate(statement, cir_parent, source_error);
					break;
				case arith_add:
					if(loperand) 
						results = this.arith_add_lpropagator.propagate(statement, cir_parent, source_error);
					else 
						results = this.arith_add_rpropagator.propagate(statement, cir_parent, source_error);
					break;
				case arith_sub:
					if(loperand) 
						results = this.arith_sub_lpropagator.propagate(statement, cir_parent, source_error);
					else 
						results = this.arith_sub_rpropagator.propagate(statement, cir_parent, source_error);
					break;
				case arith_mul:
					if(loperand) 
						results = this.arith_mul_lpropagator.propagate(statement, cir_parent, source_error);
					else 
						results = this.arith_mul_rpropagator.propagate(statement, cir_parent, source_error);
					break;
				case arith_div:
					if(loperand) 
						results = this.arith_div_lpropagator.propagate(statement, cir_parent, source_error);
					else 
						results = this.arith_div_rpropagator.propagate(statement, cir_parent, source_error);
					break;
				case arith_mod:
					if(loperand) 
						results = this.arith_mod_lpropagator.propagate(statement, cir_parent, source_error);
					else 
						results = this.arith_mod_rpropagator.propagate(statement, cir_parent, source_error);
					break;
				case bit_and:
					if(loperand) 
						results = this.bitws_and_lpropagator.propagate(statement, cir_parent, source_error);
					else 
						results = this.bitws_and_rpropagator.propagate(statement, cir_parent, source_error);
					break;
				case bit_or:
					if(loperand) 
						results = this.bitws_ior_lpropagator.propagate(statement, cir_parent, source_error);
					else 
						results = this.bitws_ior_rpropagator.propagate(statement, cir_parent, source_error);
					break;
				case bit_xor:
					if(loperand) 
						results = this.bitws_xor_lpropagator.propagate(statement, cir_parent, source_error);
					else 
						results = this.bitws_xor_rpropagator.propagate(statement, cir_parent, source_error);
					break;
				case left_shift:
					if(loperand) 
						results = this.bitws_lsh_lpropagator.propagate(statement, cir_parent, source_error);
					else 
						results = this.bitws_lsh_rpropagator.propagate(statement, cir_parent, source_error);
					break;
				case righ_shift:
					if(loperand) 
						results = this.bitws_rsh_lpropagator.propagate(statement, cir_parent, source_error);
					else 
						results = this.bitws_rsh_rpropagator.propagate(statement, cir_parent, source_error);
					break;
				case greater_tn:
					if(loperand) 
						results = this.greater_tn_lpropagator.propagate(statement, cir_parent, source_error);
					else 
						results = this.greater_tn_rpropagator.propagate(statement, cir_parent, source_error);
					break;
				case greater_eq:
					if(loperand) 
						results = this.greater_eq_lpropagator.propagate(statement, cir_parent, source_error);
					else 
						results = this.greater_eq_rpropagator.propagate(statement, cir_parent, source_error);
					break;
				case smaller_tn:
					if(loperand) 
						results = this.smaller_tn_lpropagator.propagate(statement, cir_parent, source_error);
					else 
						results = this.smaller_tn_rpropagator.propagate(statement, cir_parent, source_error);
					break;
				case smaller_eq:
					if(loperand) 
						results = this.smaller_eq_lpropagator.propagate(statement, cir_parent, source_error);
					else 
						results = this.smaller_eq_rpropagator.propagate(statement, cir_parent, source_error);
					break;
				case equal_with:
					if(loperand) 
						results = this.equal_with_lpropagator.propagate(statement, cir_parent, source_error);
					else 
						results = this.equal_with_rpropagator.propagate(statement, cir_parent, source_error);
					break;
				case not_equals:
					if(loperand) 
						results = this.not_equals_lpropagator.propagate(statement, cir_parent, source_error);
					else 
						results = this.not_equals_rpropagator.propagate(statement, cir_parent, source_error);
					break;
				default: throw new IllegalArgumentException(cir_parent.generate_code(true));
				}
			}
			else if(cir_parent instanceof CirArgumentList) {
				CirCallStatement call_statement = (CirCallStatement) cir_parent.get_parent();
				CirExecution call_execution = call_statement.get_tree().get_localizer().get_execution(call_statement);
				CirExecution wait_execution = call_execution.get_graph().get_execution(call_execution.get_id() + 1);
				CirWaitAssignStatement wait_statement = (CirWaitAssignStatement) wait_execution.get_statement();
				CirExpression wait_expression = wait_statement.get_rvalue();
				results = this.argument_in_propagator.propagate(wait_statement, wait_expression, source_error);
				edge_type = SecStateEdgeType.ag_call;
			}
			else if(cir_parent instanceof CirAssignStatement) {
				if(((CirAssignStatement) cir_parent).get_rvalue() == orig_expression) {
					results = this.assignment_rpropagator.propagate(statement, 
							((CirAssignStatement) cir_parent).get_lvalue(), source_error);
					edge_type = SecStateEdgeType.assign;
				}
				else {
					results = null; 
					edge_type = SecStateEdgeType.assign;
				} 
			}
			else {
				results = null; edge_type = null;
			}
			
			if(results != null) {
				for(SecStateError target_error : results.keySet()) {
					SecConstraint constraint = results.get(target_error);
					SecStateNode target = source.get_graph().new_node(target_error);
					source.link_to(edge_type, target, constraint);
				}
			}
		}
		return source.get_ou_edges();
	}
	
	/**
	 * @param source
	 * @param contexts
	 * @return the leafs generated from the source in error propagation at the top of the statement brinks
	 * @throws Exception
	 */
	private Collection<SecStateNode> propagete_from(SecStateNode source, CStateContexts contexts) throws Exception {
		Queue<SecStateNode> queue = new LinkedList<SecStateNode>();
		List<SecStateNode> leafs = new ArrayList<SecStateNode>();
		queue.add(source);
		while(!queue.isEmpty()) {
			source = queue.poll();
			Iterable<SecStateEdge> edges = this.propagate_on(source, contexts);
			if(edges.iterator().hasNext()) {
				for(SecStateEdge edge : edges) 
					queue.add(edge.get_target());
			}
			else {
				leafs.add(source);
			}
		}
		return leafs;
	}
	
	/**
	 * @param source
	 * @param contexts
	 * @return the leafs generated from the source in error propagation at the top of the statement brinks
	 * @throws Exception
	 */
	public static Collection<SecStateNode> propagate(SecStateNode source, CStateContexts contexts) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source");
		else 
			return propagators.propagete_from(source, contexts);
	}
	
}
