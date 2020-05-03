package com.jcsa.jcmuta.mutant.error2mutation.infection;

import java.util.List;
import java.util.Map;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateInfection;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;
import com.jcsa.jcparse.lang.symb.SymFactory;

public class OEBAInfection extends StateInfection {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_beg_statement(cir_tree, this.get_location(mutation));
	}
	
	/**
	 * roperand != 0
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void bitws_and_assign(CirExpression loperand, CirExpression roperand, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression condition = SymFactory.parse(roperand);
		condition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.not_equals, condition, SymFactory.new_constant(0L));
		condition = this.derive_sym_constraint(condition);
		
		StateConstraints constraints = new StateConstraints(true);
		constraints.add_constraint(roperand.statement_of(), condition);
		
		output.put(graph.get_error_set().chg_numb(roperand), constraints);
	}
	
	/**
	 * loperand != 0 and roperand != ~0
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void bitws_ior_assign(CirExpression loperand, CirExpression roperand, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {	
		SymExpression condition1 = SymFactory.parse(roperand);
		condition1 = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.not_equals, condition1, SymFactory.new_constant(~0L));
		condition1 = this.derive_sym_constraint(condition1);
		
		SymExpression condition2 = SymFactory.parse(loperand);
		condition2 = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.not_equals, condition2, SymFactory.new_constant(0L));
		condition2 = this.derive_sym_constraint(condition2);
		
		StateConstraints constraints = new StateConstraints(true);
		constraints.add_constraint(roperand.statement_of(), condition1);
		constraints.add_constraint(roperand.statement_of(), condition2);
		
		output.put(graph.get_error_set().chg_numb(roperand), constraints);
	}
	
	/**
	 * loperand != 0
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void bitws_xor_assign(CirExpression loperand, CirExpression roperand, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {	
		SymExpression condition = SymFactory.parse(loperand);
		condition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.not_equals, condition, SymFactory.new_constant(0L));
		condition = this.derive_sym_constraint(condition);
		
		StateConstraints constraints = new StateConstraints(true);
		constraints.add_constraint(loperand.statement_of(), condition);
		
		output.put(graph.get_error_set().chg_numb(roperand), constraints);
	}
	
	/**
	 * loperand != 0
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void bitws_lsh_assign(CirExpression loperand, CirExpression roperand, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {	
		SymExpression condition = SymFactory.parse(loperand);
		condition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.not_equals, condition, SymFactory.new_constant(0L));
		condition = this.derive_sym_constraint(condition);
		
		StateConstraints constraints = new StateConstraints(true);
		constraints.add_constraint(loperand.statement_of(), condition);
		
		output.put(graph.get_error_set().chg_numb(roperand), constraints);
	}
	
	/**
	 * loperand != 0
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void bitws_rsh_assign(CirExpression loperand, CirExpression roperand, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {	
		SymExpression condition = SymFactory.parse(loperand);
		condition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.not_equals, condition, SymFactory.new_constant(0L));
		condition = this.derive_sym_constraint(condition);
		
		StateConstraints constraints = new StateConstraints(true);
		constraints.add_constraint(loperand.statement_of(), condition);
		
		output.put(graph.get_error_set().chg_numb(roperand), constraints);
	}
	
	@Override
	protected void get_infections(CirTree cir_tree, AstMutation mutation, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		List<CirNode> statements = cir_tree.get_cir_nodes(
				this.get_location(mutation), CirAssignStatement.class);
		CirAssignStatement statement = (CirAssignStatement) statements.get(statements.size() - 1);
		CirExpression loperand = statement.get_lvalue(), roperand = statement.get_rvalue();
		
		switch(mutation.get_mutation_operator()) {
		case assign_to_bitws_and_assign: this.bitws_and_assign(loperand, roperand, graph, output); break;
		case assign_to_bitws_ior_assign: this.bitws_ior_assign(loperand, roperand, graph, output); break;
		case assign_to_bitws_xor_assign: this.bitws_xor_assign(loperand, roperand, graph, output); break;
		case assign_to_bitws_lsh_assign: this.bitws_lsh_assign(loperand, roperand, graph, output); break;
		case assign_to_bitws_rsh_assign: this.bitws_rsh_assign(loperand, roperand, graph, output); break;
		default: throw new IllegalArgumentException(mutation.get_mutation_operator().toString());
		}
	}

}
