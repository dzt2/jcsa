package com.jcsa.jcmutest.mutant.ast2mutant;

import java.util.List;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class VTRPMutationGenerator extends MutationGenerator {

	@Override
	protected void initialize(AstFunctionDefinition function) throws Exception { }

	@Override
	protected boolean available(AstNode location) throws Exception {
		if(this.is_numeric_expression(location) 
				&& !this.is_left_reference(location)) {
			AstExpression expression = (AstExpression) location;
			AstNode[] statement_child = this.statement_context(location);
			if(statement_child[0] instanceof AstCaseStatement) {
				return false;
			}
			else if(expression instanceof AstConstant) {
				return false;
			}
			else {
				return true;
			}
		}
		else {
			return false;
		}
	}

	@Override
	protected void generate(AstNode location, List<AstMutation> mutations) throws Exception {
		AstExpression expression = (AstExpression) location;
		expression = CTypeAnalyzer.get_expression_of(expression);
		CType data_type = CTypeAnalyzer.
					get_value_type(expression.get_value_type());
		mutations.add(AstMutations.trap_on_pos(expression));
		mutations.add(AstMutations.trap_on_zro(expression));
		if(CTypeAnalyzer.is_signed(data_type)) {
			mutations.add(AstMutations.trap_on_neg(expression));
		}
	}

}
