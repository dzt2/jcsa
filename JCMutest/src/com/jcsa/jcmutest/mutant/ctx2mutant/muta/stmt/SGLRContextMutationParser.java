package com.jcsa.jcmutest.mutant.ctx2mutant.muta.stmt;

import java.util.LinkedList;
import java.util.Queue;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.ContextMutationParser;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstLabel;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.program.AstCirNode;

public class SGLRContextMutationParser extends ContextMutationParser {
	
	private	AstLabeledStatement find_statement_of(AstLabel label) throws Exception {
		AstFunctionDefinition function = label.get_function_of();
		Queue<AstNode> queue = new LinkedList<AstNode>();
		queue.add(function); String name = label.get_name();
		while(!queue.isEmpty()) {
			AstNode parent = queue.poll();
			if(parent instanceof AstLabeledStatement) {
				if(((AstLabeledStatement) parent).get_label().get_name().equals(name)) {
					return (AstLabeledStatement) parent;
				}
			}
			else {
				for(int k = 0; k < parent.number_of_children(); k++) {
					queue.add(parent.get_child(k));
				}
			}
		}
		throw new IllegalArgumentException("Unable to localize: " + name);
	}

	@Override
	protected AstCirNode find_reach_location(AstMutation mutation) throws Exception {
		AstNode goto_statement = mutation.get_location().get_parent();
		return this.get_location(goto_statement);
	}

	@Override
	protected void parse_infection_set(AstCirNode location, AstMutation mutation) throws Exception {
		AstCirNode orig_next = this.get_location(this.find_statement_of((AstLabel) mutation.get_location()));
		AstCirNode muta_next = this.get_location(this.find_statement_of((AstLabel) mutation.get_parameter()));
		this.put_infection(this.cov_time(1, Integer.MAX_VALUE), this.set_flow(orig_next, muta_next));
	}

}
