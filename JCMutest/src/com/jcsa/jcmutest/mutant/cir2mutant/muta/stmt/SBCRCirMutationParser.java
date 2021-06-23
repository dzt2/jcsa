package com.jcsa.jcmutest.mutant.cir2mutant.muta.stmt;

import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.base.SymCondition;
import com.jcsa.jcmutest.mutant.cir2mutant.base.SymConditions;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SBCRCirMutationParser extends CirMutationParser {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_beg_statement(cir_tree, mutation.get_location());
	}
	
	private AstStatement find_loop_statement(AstNode location) throws Exception {
		while(location != null) {
			if(location instanceof AstDoWhileStatement
				|| location instanceof AstWhileStatement
				|| location instanceof AstForStatement) {
				return (AstStatement) location;
			}
			else {
				location = location.get_parent();
			}
		}
		throw new IllegalArgumentException("Not in loop-structure");
	}
	
	@Override
	protected void generate_infections(CirTree cir_tree, CirStatement statement, AstMutation mutation,
			Map<SymCondition, SymCondition> infections) throws Exception {
		CirExecution source = cir_tree.get_localizer().get_execution(statement);
		CirExecutionFlow orig_flow = source.get_ou_flow(0);
		AstStatement loop_statement = this.find_loop_statement(mutation.get_location());
		
		CirStatement next_statement;
		switch(mutation.get_operator()) {
		case break_to_continue:
		{
			next_statement = (CirStatement) this.get_cir_node(cir_tree, loop_statement, CirIfStatement.class);
			break;
		}
		case continue_to_break:
		{
			next_statement = (CirStatement) this.get_cir_node(cir_tree, loop_statement, CirIfEndStatement.class);
			break;
		}
		default: throw new IllegalArgumentException("Invalid operator: " + mutation.get_operator());
		}
		CirExecution target = cir_tree.get_localizer().get_execution(next_statement);
		
		CirExecutionFlow muta_flow = CirExecutionFlow.virtual_flow(source, target);
		infections.put(SymConditions.mut_flow(orig_flow, muta_flow),
				SymConditions.cov_stmt(SymConditions.execution_of(statement), 1));
	}

}
