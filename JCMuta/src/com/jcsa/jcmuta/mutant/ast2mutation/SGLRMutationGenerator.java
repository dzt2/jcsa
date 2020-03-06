package com.jcsa.jcmuta.mutant.ast2mutation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstGotoStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;

public class SGLRMutationGenerator extends AstMutationGenerator {
	
	private Queue<AstNode> ast_queue = new LinkedList<AstNode>();
	private List<AstLabeledStatement> labeled_statements;
	
	protected SGLRMutationGenerator() {
		labeled_statements = new ArrayList<AstLabeledStatement>();
	}
	
	private AstFunctionDefinition function_of(AstNode location) throws Exception {
		while(location != null) {
			if(location instanceof AstFunctionDefinition)
				return (AstFunctionDefinition) location;
			else location = location.get_parent();
		}
		return null;
	}
	
	private void collect_labeled_statement(AstFunctionDefinition definition) throws Exception {
		this.ast_queue.clear(); this.ast_queue.add(definition);
		
		this.labeled_statements.clear();
		while(!this.ast_queue.isEmpty()) {
			AstNode ast_node = this.ast_queue.poll();
			for(int k = 0; k < ast_node.number_of_children(); k++) {
				AstNode child = ast_node.get_child(k);
				if(child != null) this.ast_queue.add(child);
			}
			
			if(ast_node instanceof AstLabeledStatement) {
				this.labeled_statements.add((AstLabeledStatement) ast_node);
			}
		}
	}
	
	@Override
	protected void collect_locations(AstNode location, Collection<AstNode> locations) throws Exception {
		if(location instanceof AstGotoStatement) {
			locations.add(location);
		}
	}
	
	@Override
	protected void generate_mutations(AstNode location, Collection<AstMutation> mutations) throws Exception {
		AstGotoStatement statement = (AstGotoStatement) location;
		this.collect_labeled_statement(this.function_of(location));
		
		for(AstLabeledStatement labeled_statement : this.labeled_statements) {
			String source = statement.get_label().get_name();
			String target = labeled_statement.get_label().get_name();
			if(!source.equals(target))
				mutations.add(AstMutation.SGLR(statement, labeled_statement));
		}
	}

}
