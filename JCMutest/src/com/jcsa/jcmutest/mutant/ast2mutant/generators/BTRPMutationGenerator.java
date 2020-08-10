package com.jcsa.jcmutest.mutant.ast2mutant.generators;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConditionalExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

/**
 * logic_expression | relational_expression | conditional_expression.condition |
 * while_statement.condition | do_while_statement.condition | for_statement.condition |
 * if_statement.condition
 * @author yukimula
 *
 */
public class BTRPMutationGenerator extends AstMutationGenerator {

	@Override
	protected boolean is_seeded_location(AstNode location) throws Exception {
		if(location instanceof AstExpression) {
			AstNode parent = location.get_parent();
			if(location instanceof AstLogicBinaryExpression
				|| location instanceof AstLogicUnaryExpression
				|| location instanceof AstRelationExpression) {
				return true;
			}
			else if(parent instanceof AstConditionalExpression) {
				return ((AstConditionalExpression) parent).get_condition() == location;
			}
			else if(parent instanceof AstIfStatement) {
				return ((AstIfStatement) parent).get_condition() == location;
			}
			else if(parent instanceof AstWhileStatement) {
				return ((AstWhileStatement) parent).get_condition() == location;
			}
			else if(parent instanceof AstDoWhileStatement) {
				return ((AstDoWhileStatement) parent).get_condition() == location;
			}
			else if(parent instanceof AstExpressionStatement) {
				AstNode parent_parent = parent.get_parent();
				if(parent_parent instanceof AstForStatement) {
					return ((AstForStatement) parent_parent).get_condition() == parent;
				}
				else {
					return false;
				}
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}

	@Override
	protected Iterable<AstMutation> seed_mutations(AstNode location) throws Exception {
		AstExpression condition = (AstExpression) location;
		condition = CTypeAnalyzer.get_expression_of(condition);
		List<AstMutation> mutations = new ArrayList<AstMutation>();
		mutations.add(AstMutations.trap_on_true(condition));
		mutations.add(AstMutations.trap_on_false(condition));
		return mutations;
	}

}
