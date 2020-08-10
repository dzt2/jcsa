package com.jcsa.jcmutest.mutant.ast2mutant.generate.refs;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.AstMutationGenerator;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class RTRPMutationGenerator extends AstMutationGenerator {

	@Override
	protected boolean is_available(AstNode location) throws Exception {
		if(location instanceof AstReturnStatement) {
			return ((AstReturnStatement) location).has_expression();
		}
		else {
			return false;
		}
	}
	
	private Iterable<AstReturnStatement> returns_in(AstNode location) throws Exception {
		AstFunctionDefinition function = location.get_tree().function_of(location);
		List<AstReturnStatement> returns = new ArrayList<AstReturnStatement>();
		
		Queue<AstNode> queue = new LinkedList<AstNode>(); 
		queue.add(function.get_body());
		while(!queue.isEmpty()) {
			AstNode node = queue.poll();
			if(node instanceof AstReturnStatement) {
				if(((AstReturnStatement) node).has_expression()) {
					returns.add((AstReturnStatement) node);
				}
			}
			else {
				for(int k = 0; k < node.number_of_children(); k++) {
					queue.add(node.get_child(k));
				}
			}
		}
		
		return returns;
	}
	
	@Override
	protected void generate_mutations(AstNode location, List<AstMutation> mutations) throws Exception {
		AstReturnStatement source = (AstReturnStatement) location;
		Iterable<AstReturnStatement> targets = this.returns_in(location);
		String key = CTypeAnalyzer.get_expression_of(source.get_expression()).generate_code();
		for(AstReturnStatement target : targets) {
			if(!CTypeAnalyzer.get_expression_of(target.get_expression()).generate_code().equals(key)) {
				mutations.add(AstMutations.RTRP(source, target));
			}
		}
	}

}
