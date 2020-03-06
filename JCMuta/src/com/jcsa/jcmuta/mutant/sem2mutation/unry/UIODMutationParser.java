package com.jcsa.jcmuta.mutant.sem2mutation.unry;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutationParser;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class UIODMutationParser extends SemanticMutationParser {

	/**
	 * get the increment assignment of the mutation location
	 * @param cir_tree
	 * @param ast_mutation
	 * @return
	 * @throws Exception
	 */
	private CirAssignStatement get_assignment(AstMutation ast_mutation) throws Exception {
		return (CirAssignStatement) this.get_cir_node(
				ast_mutation.get_location(), CirIncreAssignStatement.class);
	}
	
	/**
	 * get the result of the expression used in program
	 * @param cir_tree
	 * @param ast_mutation
	 * @return
	 * @throws Exception
	 */
	private CirExpression get_usage_result(AstMutation ast_mutation) throws Exception {
		return this.get_result(ast_mutation.get_location());
	}
	
	/**
	 * get the expression used to assign the definition
	 * @param cir_tree
	 * @param ast_mutation
	 * @return
	 * @throws Exception
	 */
	private CirExpression get_define_expr(AstMutation ast_mutation) throws Exception {
		return this.get_assignment(ast_mutation).get_rvalue();
	}
	
	@Override
	protected CirStatement get_statement(AstMutation ast_mutation) throws Exception {
		return this.get_assignment(ast_mutation);
	}

	@Override
	protected void generate_infections(AstMutation ast_mutation) throws Exception {
		CirExpression result = this.get_usage_result(ast_mutation);
		CirExpression define = this.get_define_expr(ast_mutation);
		List<SemanticAssertion> state_errors = new ArrayList<SemanticAssertion>();
		
		switch(ast_mutation.get_mutation_operator()) {
		case delete_prev_inc:
		{
			if(result != null) {
				state_errors.add(sem_mutation.get_assertions().diff_value(result, -1));
				// state_errors.add(sem_mutation.get_assertions().mut_value(result));
			}
			state_errors.add(sem_mutation.get_assertions().diff_value(define, -1));
			// state_errors.add(sem_mutation.get_assertions().mut_value(define));
		}
		break;
		case delete_prev_dec:
		{
			if(result != null) {
				state_errors.add(sem_mutation.get_assertions().diff_value(result, 1));
				// state_errors.add(sem_mutation.get_assertions().mut_value(result));
			}
			state_errors.add(sem_mutation.get_assertions().diff_value(define, 1));
			// state_errors.add(sem_mutation.get_assertions().mut_value(define));
		}
		break;
		case delete_post_inc:
		{
			state_errors.add(sem_mutation.get_assertions().diff_value(define, -1));
			// state_errors.add(sem_mutation.get_assertions().mut_value(define));
		}
		break;
		case delete_post_dec:
		{
			state_errors.add(sem_mutation.get_assertions().diff_value(define, 1));
			// state_errors.add(sem_mutation.get_assertions().mut_value(define));
		}
		break;
		default: throw new IllegalArgumentException("Invalid operator as null");
		}
		
		if(!state_errors.isEmpty()) { this.infect(state_errors); }
	}

}
