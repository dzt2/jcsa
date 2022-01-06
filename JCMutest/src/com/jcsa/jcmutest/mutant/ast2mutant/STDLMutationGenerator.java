package com.jcsa.jcmutest.mutant.ast2mutant;

import java.util.List;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstCompoundStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDeclarationStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;

public class STDLMutationGenerator extends MutationGenerator {

	@Override
	protected void initialize(AstFunctionDefinition function, Iterable<AstNode> locations) throws Exception { }

	@Override
	protected boolean available(AstNode location) throws Exception {
		if(location instanceof AstStatement) {
			if(location instanceof AstLabeledStatement
				|| location instanceof AstReturnStatement
				|| location instanceof AstDeclarationStatement) {
				return false;
			}
			else if(location instanceof AstExpressionStatement) {
				return ((AstExpressionStatement) location).has_expression();
			}
			else if(location instanceof AstCompoundStatement) {
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
		AstStatement statement = (AstStatement) location;
		mutations.add(AstMutations.delete_statement(statement));
	}

}
