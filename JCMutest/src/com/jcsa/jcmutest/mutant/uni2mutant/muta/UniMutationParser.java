package com.jcsa.jcmutest.mutant.uni2mutant.muta;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.uni2mutant.UniMutation;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstErrorState;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractStates;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractStore;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniConditionState;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniConstraintState;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniCoverTimesState;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniTrapsErrorState;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniValueErrorState;
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
	protected abstract void generate_infection_map(CirStatement statement, AstMutation mutation) throws Exception;
	/**
	 * @param cir_tree	the C-intermediate representative root where the mutant is derived
	 * @param mutation	the syntactic mutation, from which RIP-based mutations are created
	 * @return			the set of RIP-based state mutations, from which mutant is created
	 * @throws Exception
	 */
	public List<UniMutation> parse(CirTree cir_tree, Mutant mutant) throws Exception {
		if(cir_tree == null) {
			throw new IllegalArgumentException("Invalid cir_tree: null");
		}
		else if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutation: null");
		}
		else {
			/* initialization */
			this.cir_tree = cir_tree;
			this.rec_stmt = null;
			this.i_states.clear();
			this.p_states.clear();
			
			/* transformation */
			this.rec_stmt = this.get_reach_node(mutant.get_mutation());
			if(this.rec_stmt != null) {
				this.generate_infection_map(this.rec_stmt, mutant.get_mutation());
			}
			
			/* construction */
			List<UniMutation> mutations = new ArrayList<UniMutation>();
			for(int k = 0; k < this.i_states.size(); k++) {
				UniConditionState i_state = this.i_states.get(k);
				UniAbstErrorState p_state = this.p_states.get(k);
				mutations.add(new UniMutation(mutant, this.rec_stmt, i_state, p_state));
			}
			return mutations;
		}
	}
	/**
	 * It puts the infection-propagation pairs to the table
	 * @param i_state
	 * @param p_state
	 * @throws Exception
	 */
	protected void put_infection_pair(UniConditionState i_state, UniAbstErrorState p_state) throws Exception {
		if(i_state == null) {
			throw new IllegalArgumentException("Invalid i_state: null");
		}
		else if(p_state != null) {
			throw new IllegalArgumentException("Invalid p_state: null");
		}
		else {
			this.i_states.add(i_state); this.p_states.add(p_state);
		}
	}
	
	/* localize methods */
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
	
	/* generate methods */ 
	/**
	 * @param statement
	 * @return	cov_stmt(statement; 1, 2^32)
	 * @throws Exception
	 */
	protected UniCoverTimesState	cov_time(CirStatement statement) throws Exception {
		UniAbstractStore store = UniAbstractStore.new_node(statement);
		return UniAbstractStates.cov_time(store, 1, Integer.MAX_VALUE);
	}
	/**
	 * @param statement
	 * @param max_time
	 * @return cov_stmt(statement; 1, max_time)
	 * @throws Exception
	 */
	protected UniCoverTimesState 	max_time(CirStatement statement, int max_time) throws Exception {
		UniAbstractStore store = UniAbstractStore.new_node(statement);
		return UniAbstractStates.cov_time(store, 1, max_time);
	}
	/**
	 * @param statement
	 * @param condition
	 * @return	eva_cond(statement; condition, false)
	 * @throws Exception
	 */
	protected UniConstraintState	eva_need(CirStatement statement, Object condition) throws Exception {
		UniAbstractStore store = UniAbstractStore.new_node(statement);
		return UniAbstractStates.eva_cond(store, condition, false);
	}
	/**
	 * @param statement
	 * @param condition
	 * @return	eva_cond(statement; condition, true)
	 * @throws Exception
	 */
	protected UniConstraintState	eva_must(CirStatement statement, Object condition) throws Exception {
		UniAbstractStore store = UniAbstractStore.new_node(statement);
		return UniAbstractStates.eva_cond(store, condition, true);
	}
	/**
	 * @param store	the store of the expression where the data error is seeded
	 * @param value	the mutated value to replace with the original expression
	 * @return
	 * @throws Exception
	 */
	protected UniValueErrorState	set_expr(CirExpression expression, Object value) throws Exception {
		return UniAbstractStates.set_expr(UniAbstractStore.new_node(expression), value);
	}
	/**
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	protected UniTrapsErrorState	trp_stmt(CirStatement statement) throws Exception {
		return UniAbstractStates.trp_stmt(UniAbstractStore.new_node(statement));
	}
	// TODO generate the methods
	
}
