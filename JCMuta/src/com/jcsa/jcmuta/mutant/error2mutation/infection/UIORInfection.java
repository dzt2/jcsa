package com.jcsa.jcmuta.mutant.error2mutation.infection;

import java.util.Map;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateInfection;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symb.StateConstraints;

public class UIORInfection extends StateInfection {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_beg_statement(cir_tree, this.get_location(mutation));
	}
	
	@Override
	protected void get_infections(CirTree cir_tree, AstMutation mutation, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		AstExpression expression = (AstExpression) this.get_location(mutation);
		if(expression instanceof AstIncreUnaryExpression) {
			/* determine the usage and definition point */
			CirIncreAssignStatement inc_statement = (CirIncreAssignStatement) 
					cir_tree.get_cir_nodes(expression, CirIncreAssignStatement.class).get(0);
			CirExpression use_expression = this.get_result_of(cir_tree, expression);
			CirExpression def_expression = inc_statement.get_rvalue();
			StateConstraints constraints = new StateConstraints(true);
			
			switch(mutation.get_mutation_operator()) {
			/* (++x, --x): def - 2; use - 2 */
			case prev_inc_to_prev_dec:
			{
				if(def_expression != null) 
					output.put(graph.get_error_set().dif_numb(def_expression, -2L), constraints);
				if(use_expression != null)
					output.put(graph.get_error_set().dif_numb(use_expression, -2L), constraints);
			}
			break;
			/* (++x, x--): def - 2, use - 1 */
			case prev_inc_to_post_dec:
			{
				if(def_expression != null) 
					output.put(graph.get_error_set().dif_numb(def_expression, -2L), constraints);
				if(use_expression != null)
					output.put(graph.get_error_set().dif_numb(use_expression, -1L), constraints);
			}
			break;
			/* (++x, x++): use - 1 */
			case prev_inc_to_post_inc:
			{
				if(use_expression != null)
					output.put(graph.get_error_set().dif_numb(use_expression, -1L), constraints);
			}
			break;
			/* (--x, ++x): def + 2, use + 2 */
			case prev_dec_to_prev_inc:
			{
				if(def_expression != null) 
					output.put(graph.get_error_set().dif_numb(def_expression, 2L), constraints);
				if(use_expression != null)
					output.put(graph.get_error_set().dif_numb(use_expression, 2L), constraints);
			}
			break;
			/* (--x, x++): def + 2, use + 1 */
			case prev_dec_to_post_inc:
			{
				if(def_expression != null) 
					output.put(graph.get_error_set().dif_numb(def_expression, 2L), constraints);
				if(use_expression != null)
					output.put(graph.get_error_set().dif_numb(use_expression, 1L), constraints);
			}
			break;
			/* (--x, x--): use + 1 */
			case prev_dec_to_post_dec:
			{
				if(use_expression != null)
					output.put(graph.get_error_set().dif_numb(use_expression, 1L), constraints);
			}
			break;
			default: throw new IllegalArgumentException(mutation.get_mutation_operator().toString());
			}
		}
		else {
			/* determine the usage and definition point */
			CirIncreAssignStatement inc_statement = (CirIncreAssignStatement) 
					cir_tree.get_cir_nodes(expression, CirIncreAssignStatement.class).get(0);
			CirExpression use_expression = this.get_result_of(cir_tree, expression);
			CirExpression def_expression = inc_statement.get_rvalue();
			StateConstraints constraints = new StateConstraints(true);
			
			switch(mutation.get_mutation_operator()) {
			/* (x++, ++x): use + 1 */
			case post_inc_to_prev_inc:
			{
				if(use_expression != null)
					output.put(graph.get_error_set().dif_numb(use_expression, 1L), constraints);
			}
			break;
			/* (x++, --x): def - 2, use - 1 */
			case post_inc_to_prev_dec:
			{
				if(def_expression != null)
					output.put(graph.get_error_set().dif_numb(def_expression, -2L), constraints);
				if(use_expression != null)
					output.put(graph.get_error_set().dif_numb(use_expression, -1L), constraints);
			}
			break;
			/* (x++, x--): def - 2; */
			case post_inc_to_post_dec:
			{
				if(def_expression != null)
					output.put(graph.get_error_set().dif_numb(def_expression, -2L), constraints);
			}
			break;
			/* (x--, --x): use - 1 */
			case post_dec_to_prev_dec:
			{
				if(use_expression != null)
					output.put(graph.get_error_set().dif_numb(use_expression, -1L), constraints);
			}
			break;
			/* (x--, ++x): def + 2, use + 1 */
			case post_dec_to_prev_inc:
			{
				if(def_expression != null)
					output.put(graph.get_error_set().dif_numb(def_expression, 2L), constraints);
				if(use_expression != null)
					output.put(graph.get_error_set().dif_numb(use_expression, 1L), constraints);
			}
			break;
			/* (x--, x++): def + 2 */
			case post_dec_to_post_inc:
			{
				if(def_expression != null)
					output.put(graph.get_error_set().dif_numb(def_expression, 2L), constraints);
			}
			break;
			default: throw new IllegalArgumentException(mutation.get_mutation_operator().toString());
			}
		}
	}

}
