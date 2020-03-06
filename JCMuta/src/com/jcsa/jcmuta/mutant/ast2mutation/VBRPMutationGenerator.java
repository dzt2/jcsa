package com.jcsa.jcmuta.mutant.ast2mutation;

import java.util.Collection;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class VBRPMutationGenerator extends AstMutationGenerator {

	@Override
	protected void collect_locations(AstNode location, Collection<AstNode> locations) throws Exception {
		AstExpression condition;
		if(location instanceof AstIfStatement) {
			condition = ((AstIfStatement) location).get_condition();
		}
		else if(location instanceof AstWhileStatement) {
			condition = ((AstWhileStatement) location).get_condition();
		}
		else if(location instanceof AstDoWhileStatement) {
			condition = ((AstDoWhileStatement) location).get_condition();
		}
		else if(location instanceof AstForStatement) {
			AstExpressionStatement cond_stmt = ((AstForStatement) location).get_condition();
			if(cond_stmt.has_expression()) condition = cond_stmt.get_expression();
			else condition = null;
		}
		else if(location instanceof AstLogicBinaryExpression
				|| location instanceof AstLogicUnaryExpression
				|| location instanceof AstRelationExpression) {
			condition = (AstExpression) location;
		}
		else condition = null;
		
		if(condition != null) {
			condition = CTypeAnalyzer.get_expression_of(condition);
			locations.add(condition);
		}
	}

	@Override
	protected void generate_mutations(AstNode location, Collection<AstMutation> mutations) throws Exception {
		AstExpression expression = (AstExpression) location;
		mutations.add(AstMutation.VBRP(expression, true));
		mutations.add(AstMutation.VBRP(expression, false));
	}

}
