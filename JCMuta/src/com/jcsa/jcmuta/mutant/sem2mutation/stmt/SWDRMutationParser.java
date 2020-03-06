package com.jcsa.jcmuta.mutant.sem2mutation.stmt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmuta.MutaOperator;
import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutationParser;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.AstCirPair;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;

public class SWDRMutationParser extends SemanticMutationParser {
	
	/**
	 * find the looping statement where the mutation is performed
	 * @param ast_mutation
	 * @return
	 * @throws Exception
	 */
	private AstStatement get_loop_statement(AstMutation ast_mutation) throws Exception {
		AstNode location = ast_mutation.get_location();
		while(location != null) {
			if(location instanceof AstForStatement
				|| location instanceof AstWhileStatement
				|| location instanceof AstDoWhileStatement) {
				return (AstStatement) location;
			}
			else { location = location.get_parent(); }
		}
		throw new IllegalArgumentException("Invalid location");
	}
	
	/**
	 * get the conditional statement of the looping
	 * @param ast_mutation
	 * @return
	 * @throws Exception
	 */
	private CirIfStatement get_condition_statement(CirTree cir_tree, AstMutation ast_mutation) throws Exception {
		AstStatement ast_statement = this.get_loop_statement(ast_mutation);
		return (CirIfStatement) this.get_cir_node(ast_statement, CirIfStatement.class);
	}
	
	/**
	 * equal_with(condition, false|0|Nullptr)
	 * @param expression
	 * @param sem_mutation
	 * @return
	 * @throws Exception
	 */
	private SemanticAssertion get_constraint(CirExpression expression, SemanticMutation sem_mutation) throws Exception {
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		if(CTypeAnalyzer.is_boolean(data_type)) {
			return sem_mutation.get_assertions().equal_with(expression, Boolean.FALSE);
		}
		else if(CTypeAnalyzer.is_number(data_type)) {
			return sem_mutation.get_assertions().equal_with(expression, Long.valueOf(0));
		}
		else if(CTypeAnalyzer.is_pointer(data_type)) {
			return sem_mutation.get_assertions().equal_with(expression, Nullptr);
		}
		else throw new IllegalArgumentException("Invalid data_type");
	}
	
	/**
	 * get the statements within the looping location
	 * @param cir_tree
	 * @param loop_statement
	 * @return
	 * @throws Exception
	 */
	private Set<CirStatement> collect_statements_in(CirTree cir_tree, AstMutation ast_mutation) throws Exception {
		AstNode location = this.get_loop_statement(ast_mutation);
		Set<CirStatement> cir_statements = new HashSet<CirStatement>();
		Queue<AstNode> ast_queue = new LinkedList<AstNode>();
		
		ast_queue.add(location);
		while(!ast_queue.isEmpty()) {
			AstNode ast_node = ast_queue.poll();
			for(int k = 0; k < ast_node.number_of_children(); k++) {
				AstNode ast_child = ast_node.get_child(k);
				if(ast_child != null) ast_queue.add(ast_child);
			}
			
			AstCirPair range = this.get_cir_range(ast_node);
			if(range != null && range.executional()) {
				cir_statements.add(range.get_beg_statement());
				cir_statements.add(range.get_end_statement());
			}
		}
		
		return cir_statements;
	}
	
	@Override
	protected CirStatement get_statement(AstMutation ast_mutation) throws Exception {
		return this.get_condition_statement(cir_tree, ast_mutation);
	}
	
	@Override
	protected void generate_infections(AstMutation ast_mutation) throws Exception {
		/** 1. declarations **/
		CirIfStatement if_statement = this.get_condition_statement(cir_tree, ast_mutation);
		SemanticAssertion constraint = this.get_constraint(if_statement.get_condition(), sem_mutation);
		Set<CirStatement> loop_body = this.collect_statements_in(cir_tree, ast_mutation);
		
		/** 2. operator based mutation **/
		List<SemanticAssertion> state_errors = new ArrayList<SemanticAssertion>();
		if(ast_mutation.get_mutation_operator() == MutaOperator.while_to_do) {
			for(CirStatement statement : loop_body) {
				if(!(statement instanceof CirTagStatement)) {
					state_errors.add(sem_mutation.get_assertions().active(statement));
				}
			}
		}
		else if(ast_mutation.get_mutation_operator() == MutaOperator.do_to_while) {
			for(CirStatement statement : loop_body) {
				if(!(statement instanceof CirTagStatement)) {
					state_errors.add(sem_mutation.get_assertions().disactive(statement));
				}
			}
		}
		else {
			throw new IllegalArgumentException("Invalid operator: null");
		}
		
		/** 3. construct the semantic mutation **/
		if(!state_errors.isEmpty()) {
			this.infect(constraint, state_errors);
		}
	}	
	
}
