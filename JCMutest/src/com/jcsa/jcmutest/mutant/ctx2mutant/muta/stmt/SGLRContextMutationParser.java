package com.jcsa.jcmutest.mutant.ctx2mutant.muta.stmt;

import java.util.LinkedList;
import java.util.Queue;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.ContextMutationParser;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstLabel;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.program.AstCirNode;

public class SGLRContextMutationParser extends ContextMutationParser {
	
	/**
	 * The statement that the label corresponds to
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private	AstStatement find_labeled_statement(AstLabel source) throws Exception {
		Queue<AstNode> queue = new LinkedList<AstNode>();
		queue.add(source.get_function_of());
		String source_name = source.get_name();
		while(!queue.isEmpty()) {
			AstNode parent = queue.poll();
			if(parent instanceof AstLabeledStatement) {
				String target_name = ((AstLabeledStatement) parent).get_label().get_name();
				if(target_name.equals(source_name)) { return (AstStatement) parent; }
			}
			for(int k = 0; k < parent.number_of_children(); k++) {
				queue.add(parent.get_child(k));
			}
		}
		throw new IllegalArgumentException("Undefined label: " + source.get_name());
	}
	
	@Override
	protected AstCirNode localize(AstMutation mutation) throws Exception {
		return this.find_ast_location(mutation.get_location().get_parent());
	}

	@Override
	protected void generate(AstCirNode location, AstMutation mutation) throws Exception {
		AstLabel source_label = (AstLabel) mutation.get_location();
		AstLabel target_label = (AstLabel) mutation.get_parameter();
		AstStatement source = this.find_labeled_statement(source_label);
		AstStatement target = this.find_labeled_statement(target_label);
		this.put_infection(this.eva_cond(Boolean.TRUE), this.set_flow(
				this.find_ast_location(source), this.find_ast_location(target)));
	}

}
