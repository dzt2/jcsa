package com.jcsa.jcmutest.mutant.uni2mutant.muta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.uni2mutant.UniMutation;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstErrorState;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniConditionState;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * 	It implements the parsing from AstMutation to UniMutation.
 * 	
 * 	@author yukimula
 *
 */
public abstract class UniMutationParser {
	
	/* construct */
	/** the C-intermediate representation base **/
	private	CirTree					cir_tree;
	/** the statement in which fault is seeded **/
	private	CirStatement			rec_stmt;
	/** the list of state infection conditions **/
	private	List<UniConditionState>	i_states;
	/** the list of initial of infected states **/
	private	List<UniAbstErrorState>	p_states;
	/**
	 * It creates an empty parser to transform
	 */
	public UniMutationParser() {
		this.cir_tree = null; 	this.rec_stmt = null;
		this.i_states = new ArrayList<UniConditionState>();
		this.p_states = new ArrayList<UniAbstErrorState>();
	}
	
	/* implements */
	/**
	 * @param mutation
	 * @return	the statement where the mutation is injected as the reach-point
	 * @throws Exception
	 */
	protected abstract CirStatement	get_reach_node(AstMutation mutation) throws Exception;
	/**
	 * It generates the map from infection conditions to initial error states
	 * @param mutation
	 * @throws Exception
	 */
	protected abstract void generate_infection_map(AstMutation mutation) throws Exception;
	/**
	 * @param cir_tree	the C-intermediate representative root where the mutant is derived
	 * @param mutation	the syntactic mutation, from which RIP-based mutations are created
	 * @return			the set of RIP-based state mutations, from which mutant is created
	 * @throws Exception
	 */
	public Collection<UniMutation> parse(CirTree cir_tree, AstMutation mutation) throws Exception {
		if(cir_tree == null) {
			throw new IllegalArgumentException("Invalid cir_tree: null");
		}
		else if(mutation == null) {
			throw new IllegalArgumentException("Invalid mutation: null");
		}
		else {
			/* initialization */
			this.cir_tree = cir_tree;
			this.rec_stmt = null;
			this.i_states.clear();
			this.p_states.clear();
			
			/* reachability */
			this.rec_stmt = this.get_reach_node(mutation);
			if(this.rec_stmt != null) {
				this.generate_infection_map(mutation);
			}
			
			/* construction */
			List<UniMutation> mutations = new ArrayList<UniMutation>();
			for(int k = 0; k < this.i_states.size(); k++) {
				UniConditionState i_state = this.i_states.get(k);
				UniAbstErrorState p_state = this.p_states.get(k);
				mutations.add(new UniMutation(this.rec_stmt, i_state, p_state));
			}
			return mutations;
		}
	}
	
	/* base methods */
	/**
	 * @param source
	 * @param cir_class
	 * @return	the list of CirNode(s) that the source corresponds to
	 */
	protected List<CirNode> loc_cir_locations(AstNode source, Class<?> cir_class) {
		return this.cir_tree.get_cir_nodes(source, cir_class);
	}
	/**
	 * @param source
	 * @param cir_class
	 * @return the kth CirNode w.r.t. the source
	 */
	protected CirNode		loc_cir_location(AstNode source, Class<?> cir_class, int k) {
		List<CirNode> cir_nodes = this.loc_cir_locations(source, cir_class);
		if(k < 0 || k >= cir_nodes.size()) {
			return null;
		}
		else {
			return cir_nodes.get(k);
		}
	}
	/**
	 * @param source
	 * @param cir_class
	 * @return the first-point where the source corresponds to
	 */ 
	protected CirNode		loc_cir_location(AstNode source, Class<?> cir_class) {
		List<CirNode> cir_nodes = this.loc_cir_locations(source, cir_class);
		for(CirNode cir_node : cir_nodes) {
			if(cir_node.execution_of() != null) {
				return cir_node;
			}
		}
		return null;
	}
	/**
	 * @param source
	 * @return	the first statement which the source corresponds to
	 * @throws Exception
	 */
	protected CirStatement	loc_beg_statement(AstNode source) throws Exception {
		return this.cir_tree.get_localizer().beg_statement(source);
	}
	/**
	 * @param source
	 * @return	the final statement which the source corresponds to
	 * @throws Exception
	 */
	protected CirStatement	loc_end_statement(AstNode source) throws Exception {
		return this.cir_tree.get_localizer().end_statement(source);
	}
	/**
	 * @param source
	 * @param cir_class
	 * @return the expression corresponds to the source
	 */
	protected CirExpression	loc_use_expression(AstNode source, Class<?> cir_class) {
		List<CirNode> cir_nodes = this.loc_cir_locations(source, cir_class);
		CirExpression expression = null;
		for(CirNode cir_node : cir_nodes) {
			if(cir_node instanceof CirExpression) {
				expression = (CirExpression) cir_node;
				if(expression.execution_of() != null) {
					break;
				}
			}
		}
		return expression;
	}
	
}
