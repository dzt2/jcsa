package com.jcsa.jcmuta.mutant.ast2mutation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;

public class SRTRMutationGenerator extends AstMutationGenerator {
	
	private Queue<AstNode> ast_queue = new LinkedList<AstNode>();
	private List<AstReturnStatement> return_statements;
	
	protected SRTRMutationGenerator() {
		return_statements = new ArrayList<AstReturnStatement>();
	}
	
	private AstFunctionDefinition function_of(AstNode location) throws Exception {
		while(location != null) {
			if(location instanceof AstFunctionDefinition)
				return (AstFunctionDefinition) location;
			else location = location.get_parent();
		}
		return null;
	}
	
	private void collect_return_statements(AstFunctionDefinition definition) throws Exception {
		this.ast_queue.clear(); this.ast_queue.add(definition);
		
		this.return_statements.clear();
		while(!this.ast_queue.isEmpty()) {
			AstNode ast_node = this.ast_queue.poll();
			for(int k = 0; k < ast_node.number_of_children(); k++) {
				AstNode child = ast_node.get_child(k);
				if(child != null) this.ast_queue.add(child);
			}
			
			if(ast_node instanceof AstReturnStatement) {
				this.return_statements.add((AstReturnStatement) ast_node);
			}
		}
	}
	

	@Override
	protected void collect_locations(AstNode location, Collection<AstNode> locations) throws Exception {
		if(location instanceof AstReturnStatement) {
			if(((AstReturnStatement) location).has_expression()) {
				locations.add(location);
			}
		}
	}

	@Override
	protected void generate_mutations(AstNode location, Collection<AstMutation> mutations) throws Exception {
		AstReturnStatement statement = (AstReturnStatement) location;
		
		this.collect_return_statements(this.function_of(location));
		
		for(AstReturnStatement return_statement : this.return_statements) {
			if(statement != return_statement) {
				String source = statement.get_expression().get_location().read().strip();
				String target = return_statement.get_expression().get_location().read().strip();
				if(!source.equals(target)) 
					mutations.add(AstMutation.SRTR(statement, return_statement));
			}
		}
	}

}
