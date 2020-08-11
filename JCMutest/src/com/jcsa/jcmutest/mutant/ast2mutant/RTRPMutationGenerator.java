package com.jcsa.jcmutest.mutant.ast2mutant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;

public class RTRPMutationGenerator extends MutationGenerator {
	
	private Map<String, AstReturnStatement> returns = new HashMap<String, AstReturnStatement>();
	
	@Override
	protected void initialize(AstFunctionDefinition function, Iterable<AstNode> locations) throws Exception {
		this.returns.clear();
		for(AstNode location : locations) {
			if(location instanceof AstReturnStatement) {
				AstReturnStatement statement = (AstReturnStatement) location;
				if(statement.has_expression()) {
					returns.put(statement.get_expression().generate_code(), statement);
				}
			}
		}
	}

	@Override
	protected boolean available(AstNode location) throws Exception {
		if(location instanceof AstReturnStatement) {
			return ((AstReturnStatement) location).has_expression();
		}
		else {
			return false;
		}
	}

	@Override
	protected void generate(AstNode location, List<AstMutation> mutations) throws Exception {
		AstReturnStatement source = (AstReturnStatement) location;
		String skey = source.get_expression().generate_code();
		for(String tkey : this.returns.keySet()) {
			if(!tkey.equals(skey)) {
				mutations.add(AstMutations.RTRP(source, this.returns.get(tkey)));
			}
		}
	}

}
