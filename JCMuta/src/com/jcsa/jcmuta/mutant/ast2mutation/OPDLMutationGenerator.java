package com.jcsa.jcmuta.mutant.ast2mutation;

import java.util.Collection;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCommaExpression;

/**
 * delete_expression(expression.operand)
 * where expression is non-assignment binary expression (arith|bitws|logic|relation)
 * or the comma expression
 * @author yukimula
 *
 */
public class OPDLMutationGenerator extends AstMutationGenerator {

	@Override
	protected void collect_locations(AstNode location, Collection<AstNode> locations) throws Exception {
		if(location instanceof AstArithBinaryExpression
			|| location instanceof AstBitwiseBinaryExpression
			|| location instanceof AstLogicBinaryExpression
			|| location instanceof AstRelationExpression
			|| location instanceof AstShiftBinaryExpression) {
			locations.add(location);
		}
		else if(location instanceof AstCommaExpression) {
			locations.add(location);
		}
	}

	@Override
	protected void generate_mutations(AstNode location, Collection<AstMutation> mutations) throws Exception {
		AstExpression expression = (AstExpression) location;
		
		if(expression instanceof AstBinaryExpression) {
			mutations.add(AstMutation.OPDL(expression, 0));
			mutations.add(AstMutation.OPDL(expression, 1));
		}
		else {
			AstCommaExpression comma_expr = (AstCommaExpression) location;
			for(int k = 0; k < comma_expr.number_of_arguments(); k++) {
				mutations.add(AstMutation.OPDL(comma_expr, k));
			}
		}
	}

}
