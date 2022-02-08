package com.jcsa.jcmutest.mutant.cir2mutant.muta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirConditionState;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.AstCirPair;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;


/**
 * It implements the parsing from Mutant to CirMutation.
 * 
 * @author yukimula
 *
 */
public abstract class CirMutationParser {
	
	/* constructor */
	private	CirTree 					cir_tree;
	private CirExecution 				execution;
	protected List<CirConditionState>	i_states;
	protected List<CirAbstErrorState>	p_states;
	public CirMutationParser() {
		this.cir_tree = null;
		this.execution = null;
		this.i_states = new ArrayList<CirConditionState>();
		this.p_states = new ArrayList<CirAbstErrorState>();
	}
	
	/* ast-cir localize */
	/**
	 * @param location
	 * @return the range of cir-code to which the AST-location refers (or null)
	 * @throws Exception
	 */
	protected AstCirPair	get_cir_range(AstNode location) throws Exception {
		if(this.cir_tree == null) {
			throw new IllegalArgumentException("No cir_tree is established");
		}
		else if(this.cir_tree.has_cir_range(location)) {
			return this.cir_tree.get_cir_range(location);
		}
		else {
			return null;	/* return null if no cir code corresponds to */
		}
	}
	/**
	 * @param location
	 * @param cir_type
	 * @return nodes in CIR program to which the location corresponds with specified type
	 * @throws Exception
	 */
	protected List<CirNode>	get_cir_nodes(AstNode location, Class<?> cir_type) throws Exception {
		if(this.cir_tree == null) {
			throw new IllegalArgumentException("No cir_tree is established");
		}
		else {
			return this.cir_tree.get_cir_nodes(location, cir_type);
		}
	}
	/**
	 * @param location
	 * @param cir_type
	 * @return the first cir-code node with correspond to the AST location or null
	 * @throws Exception
	 */
	protected CirNode		get_cir_node(AstNode location, Class<?> cir_type) throws Exception {
		List<CirNode> cir_nodes = this.get_cir_nodes(location, cir_type);
		if(cir_nodes.isEmpty()) {
			return null;
		}
		else {
			return cir_nodes.get(0);
		}
	}
	/**
	 * @param location
	 * @param cir_type
	 * @return the last node w.r.t. the AST location in cir-code range
	 * @throws Exception
	 */
	protected CirNode		get_last_node(AstNode location, Class<?> cir_type) throws Exception {
		List<CirNode> cir_nodes = this.get_cir_nodes(location, cir_type);
		if(cir_nodes.isEmpty()) {
			return null;
		}
		else {
			return cir_nodes.get(cir_nodes.size() - 1);
		}
	}
	
	/* cir-node getters */
	/**
	 * @param location
	 * @return the expression in cir-code range to which the location corresponds
	 * @throws Exception
	 */
	protected CirExpression	get_cir_expression(AstNode location) throws Exception {
		AstCirPair cir_range = this.get_cir_range(location);
		CirExpression expression;
		if(cir_range != null) {
			expression = cir_range.get_result();
			if(expression != null && expression.statement_of() != null) {
				return expression;
			}
		}
		
		List<CirNode> cir_locations = this.get_cir_nodes(location, CirExpression.class);
		for(int k = cir_locations.size() - 1; k >= 0; k--) {
			expression = (CirExpression) cir_locations.get(k);
			if(expression.statement_of() != null) {
				return expression;
			}
		}
		
		return null;
	}
	/**
	 * @param location
	 * @return the first statement before the AST-location code range contains
	 * @throws Exception
	 */
	protected CirStatement get_beg_statement(AstNode location) throws Exception {
		if(this.cir_tree == null) {
			throw new IllegalArgumentException("Invalid cir_tree: null");		/* invalid context */
		}
		else if(location == null) {
			return null;														/* invalid inputs */
		}
		else {
			/* cir-localizer based search */
			CirStatement statement = this.cir_tree.get_localizer().beg_statement(location);
			if(statement != null) { return statement; }
			
			/* ast-cir-range based search */
			AstCirPair cir_range = this.get_cir_range(location);
			if(cir_range != null) { statement = cir_range.get_beg_statement(); }
			if(statement != null) { return statement; }
			
			/* expression-based search */
			CirExpression expression = this.get_cir_expression(location);
			if(expression != null) { statement = expression.statement_of(); }
			if(statement != null) { return statement; }
			
			/* cannot find any position */	return null;
		}
	}
	/**
	 * @param location
	 * @return the statement being executed after the location is out from
	 * @throws Exception
	 */
	protected CirStatement get_end_statement(AstNode location) throws Exception {
		if(this.cir_tree == null) {
			throw new IllegalArgumentException("Invalid cir_tree: null");		/* invalid context */
		}
		else if(location == null) {
			return null;														/* no input beings */
		}
		else {
			/* localizer based search */
			CirStatement statement = this.cir_tree.get_localizer().end_statement(location);
			if(statement != null) { return statement; }
			
			/* ast-cir-range based search */
			AstCirPair cir_range = this.get_cir_range(location);
			if(cir_range != null) { statement = cir_range.get_end_statement(); }
			if(statement != null) { return statement; }
			
			/* result-directed cir search */
			CirExpression expression = this.get_cir_expression(location);
			if(expression != null) { statement = expression.statement_of(); }
			if(statement != null) { return statement; }
			
			/* cannot find any position */	return null;
		}
	}
	
	/* data operations */
	/**
	 * @return the execution point for reaching the mutated point
	 */
	protected CirExecution get_r_execution() { return this.execution; }
	/**
	 * It puts the constraint-error infection pairs into the maps
	 * @param constraint
	 * @param init_error
	 * @throws Exception
	 */
	protected void put_infection_pair(CirConditionState constraint, 
			CirAbstErrorState init_error) throws Exception {
		if(constraint == null) {
			throw new IllegalArgumentException("Invalid constraint: null");
		}
		else if(init_error == null) {
			throw new IllegalArgumentException("Invalid init_error: null");
		}
		else { this.i_states.add(constraint); this.p_states.add(init_error); }
	}
	
	/* implementation */
	/**
	 * @param mutation
	 * @return the statement point where the mutation needs be executed before
	 * @throws Exception
	 */
	protected abstract CirStatement find_reach_point(AstMutation mutation) throws Exception;
	/**
	 * It generates the constraint-error pairs into infection maps
	 * @param mutation
	 * @throws Exception
	 */
	protected abstract void generate_infections(AstMutation mutation) throws Exception;
	
	/* parsing interface methods */
	/**
	 * It initializes the parse space.
	 * @param cir_tree
	 */
	private void init_parse(CirTree cir_tree) throws Exception {
		if(cir_tree == null)
			throw new RuntimeException("Invalid cir_tree: null");
		this.cir_tree = cir_tree;
		this.execution = null;
		this.i_states.clear();
		this.p_states.clear();
	}
	/**
	 * @param mutation
	 * @return whether the execution is successfully found
	 * @throws Exception
	 */
	private boolean set_execution(AstMutation mutation) throws Exception {
		CirStatement statement = this.find_reach_point(mutation);
		if(statement != null) {
			this.execution = statement.execution_of();
			return true;
		}
		else {
			this.execution = null;
			return false;
		}
	}
	/**
	 * @return the set of CirMutation(s) generated from the buffer
	 * @throws Exception
	 */
	private Collection<CirMutation> get_outputs(Mutant mutant) throws Exception {
		List<CirMutation> cir_mutations = new ArrayList<CirMutation>();
		for(int k = 0; k < this.i_states.size(); k++) {
			CirConditionState i_state = this.i_states.get(k);
			CirAbstErrorState p_state = this.p_states.get(k);
			cir_mutations.add(CirMutations.new_mutation(mutant, this.execution, i_state, p_state));
		}
		return cir_mutations;
	}
	/**
	 * @param mutant
	 * @return it parses the mutant to CirMutation objects
	 * @throws Exception
	 */
	public Collection<CirMutation> parse(Mutant mutant) throws Exception {
		if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant: null");
		}
		else {
			CirTree cir_tree = mutant.get_space().get_cir_tree();
			this.init_parse(cir_tree);
			if(this.set_execution(mutant.get_mutation())) {
				this.generate_infections(mutant.get_mutation());
			}
			return this.get_outputs(mutant);
		}
	}
	
}
