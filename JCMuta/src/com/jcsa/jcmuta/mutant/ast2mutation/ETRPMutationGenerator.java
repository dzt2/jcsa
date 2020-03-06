package com.jcsa.jcmuta.mutant.ast2mutation;

import java.util.Collection;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConditionalExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFunCallExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

/**
 * trap_on_expression(expression)
 * where expression is:
 *	(1) assign_bin_expr
 *	(2) inc_unary_expr
 *	(3) conditional_expr's condition, true, false branch.
 *	(4) fun_call_expr
 * @author yukimula
 *
 */
public class ETRPMutationGenerator extends AstMutationGenerator {
	
	@Override
	protected void collect_locations(AstNode location, Collection<AstNode> locations) throws Exception {
		if(location instanceof AstExpression) {
			AstExpression expression = (AstExpression) location;
			expression = CTypeAnalyzer.get_expression_of(expression);
			
			if(expression instanceof AstIncreUnaryExpression
				|| expression instanceof AstIncrePostfixExpression
				|| expression instanceof AstAssignExpression
				|| expression instanceof AstArithAssignExpression
				|| expression instanceof AstBitwiseAssignExpression
				|| expression instanceof AstShiftAssignExpression
				|| expression instanceof AstFunCallExpression) {
				locations.add(expression);
			}
			else if(expression instanceof AstConditionalExpression) {
				locations.add(CTypeAnalyzer.get_expression_of(
						((AstConditionalExpression) expression).get_condition()));
				locations.add(CTypeAnalyzer.get_expression_of(
						((AstConditionalExpression) expression).get_true_branch()));
				locations.add(CTypeAnalyzer.get_expression_of(
						((AstConditionalExpression) expression).get_false_branch()));
			}
		}
	}

	@Override
	protected void generate_mutations(AstNode location, Collection<AstMutation> mutations) throws Exception {
		AstExpression expression = (AstExpression) location;
		mutations.add(AstMutation.ETRP(expression));
	}

}
