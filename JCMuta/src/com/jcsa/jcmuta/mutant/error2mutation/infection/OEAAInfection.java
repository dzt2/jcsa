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

public class OEAAInfection extends StateInfection {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_beg_statement(cir_tree, this.get_location(mutation));
	}
	
	/**
	 * loperand != 0
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_add_assign(CirExpression loperand, CirExpression roperand, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression condition = this.get_sym_condition(loperand, false);
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
	private void arith_sub_assign(CirExpression loperand, CirExpression roperand, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression condition = this.get_sym_condition(loperand, false);
		condition = this.derive_sym_constraint(condition);
		StateConstraints constraints = new StateConstraints(true);
		constraints.add_constraint(loperand.statement_of(), condition);
		output.put(graph.get_error_set().chg_numb(roperand), constraints);
	}
	/**
	 * loperand != 1 and roperand != 0
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_mul_assign(CirExpression loperand, CirExpression roperand, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lcondition = SymFactory.parse(loperand);
		lcondition = SymFactory.new_binary_expression(
				CBasicTypeImpl.bool_type, COperator.not_equals, 
				lcondition, SymFactory.new_constant(1L));
		lcondition = this.derive_sym_constraint(lcondition);
		
		SymExpression rcondition = SymFactory.parse(roperand);
		rcondition = SymFactory.new_binary_expression(
				CBasicTypeImpl.bool_type, COperator.not_equals, 
				rcondition, SymFactory.new_constant(0L));
		rcondition = this.derive_sym_constraint(rcondition);
		
		StateConstraints constraints = new StateConstraints(true);
		constraints.add_constraint(loperand.statement_of(), lcondition);
		constraints.add_constraint(roperand.statement_of(), rcondition);
		
		output.put(graph.get_error_set().chg_numb(roperand), constraints);
	}
	
	/**
	 * roperand = 0 --> failure
	 * roperand !=0 --> chg_numb
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_div_assign(CirExpression loperand, CirExpression roperand, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		StateConstraints constraints; SymExpression condition;
		
		/* roperand = 0 --> failure */
		constraints = new StateConstraints(true); condition = SymFactory.parse(roperand);
		condition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.equal_with, condition, SymFactory.new_constant(0L));
		condition = this.derive_sym_constraint(condition);
		constraints.add_constraint(roperand.statement_of(), condition);
		output.put(graph.get_error_set().failure(), constraints);
		
		/* roperand != 0 --> data error */
		constraints = new StateConstraints(true); condition = SymFactory.parse(roperand);
		condition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.not_equals, condition, SymFactory.new_constant(0L));
		condition = this.derive_sym_constraint(condition);
		constraints.add_constraint(roperand.statement_of(), condition);
		output.put(graph.get_error_set().chg_numb(roperand), constraints);
	}
	
	/**
	 * roperand = 0 --> failure
	 * roperand !=0 --> chg_numb
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_mod_assign(CirExpression loperand, CirExpression roperand, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		StateConstraints constraints; SymExpression condition;
		
		/* roperand = 0 --> failure */
		constraints = new StateConstraints(true); condition = SymFactory.parse(roperand);
		condition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.equal_with, condition, SymFactory.new_constant(0L));
		condition = this.derive_sym_constraint(condition);
		constraints.add_constraint(roperand.statement_of(), condition);
		output.put(graph.get_error_set().failure(), constraints);
		
		/* roperand != 0 --> data error */
		constraints = new StateConstraints(true); condition = SymFactory.parse(roperand);
		condition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.not_equals, condition, SymFactory.new_constant(0L));
		condition = this.derive_sym_constraint(condition);
		constraints.add_constraint(roperand.statement_of(), condition);
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
		case assign_to_arith_add_assign: this.arith_add_assign(loperand, roperand, graph, output); break;
		case assign_to_arith_sub_assign: this.arith_sub_assign(loperand, roperand, graph, output); break;
		case assign_to_arith_mul_assign: this.arith_mul_assign(loperand, roperand, graph, output); break;
		case assign_to_arith_div_assign: this.arith_div_assign(loperand, roperand, graph, output); break;
		case assign_to_arith_mod_assign: this.arith_mod_assign(loperand, roperand, graph, output); break;
		default: throw new IllegalArgumentException(mutation.get_mutation_operator().toString());
		}
	}

}
