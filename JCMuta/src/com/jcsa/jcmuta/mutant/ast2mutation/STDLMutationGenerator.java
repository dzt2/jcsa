package com.jcsa.jcmuta.mutant.ast2mutation;

import java.util.Collection;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstCompoundStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDeclarationStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;

/**
 * delete_statement(statement) where statement is:
 * (1) NOT empty statement or block;
 * (2) 
 * @author yukimula
 *
 */
public class STDLMutationGenerator extends AstMutationGenerator {
	
	/**
	 * declaration, labeled statement and return statement
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	private boolean is_syntax_incorrect(AstStatement statement) throws Exception {
		return statement instanceof AstDeclarationStatement
				|| statement instanceof AstLabeledStatement
				|| statement instanceof AstReturnStatement;
	}
	private boolean is_empty_statement(AstStatement statement) throws Exception {
		if(statement instanceof AstExpressionStatement) {
			return !((AstExpressionStatement) statement).has_expression();
		}
		else if(statement instanceof AstCompoundStatement) {
			return !((AstCompoundStatement) statement).has_statement_list();
		}
		else {
			return false;
		}
	}
	
	@Override
	protected void collect_locations(AstNode location, Collection<AstNode> locations) throws Exception {
		if(location instanceof AstStatement) {
			AstStatement statement = (AstStatement) location;
			if(!this.is_syntax_incorrect(statement)) {
				if(!this.is_empty_statement(statement)) {
					locations.add(statement);
				}
			}
		}
	}

	@Override
	protected void generate_mutations(AstNode location, Collection<AstMutation> mutations) throws Exception {
		AstStatement statement = (AstStatement) location;
		mutations.add(AstMutation.STDL(statement));
	}

}
