package com.jcsa.jcmutest.mutant.ctx2mutant;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstAbstErrorState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstConditionState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstContextStates;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstCoverTimesState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstSeedMutantState;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolIdentifier;
import com.jcsa.jcparse.lang.symbol.SymbolNode;
import com.jcsa.jcparse.lang.symbol.eval.SymbolContext;

/**
 * 	It specifies a context-mutation at the original point using AstContextState.
 * 	
 * 	@author yukimula
 *
 */
public class ContextMutation {
	
	/* definitions */
	/** the source mutation **/
	private	Mutant mutant;
	/** the coverage state for reachability **/
	private	AstCoverTimesState	c_state;
	/** the state directly seeding mutation **/
	private AstSeedMutantState	s_state;
	/** the conditional state for infection **/
	private	List<AstConditionState>	i_states;
	/** abstract error state to propagation **/
	private	List<AstAbstErrorState>	p_states;
	
	/* getters */
	/**
	 * @return	the source syntactic mutant in the source code
	 */
	public	Mutant		get_mutant()	{ return this.mutant; }
	/**
	 * @return	the location where the mutation is directly seeded
	 */
	public	AstCirNode	get_location()	{ return this.s_state.get_location(); }
	/**
	 * @return 	the statement where the mutation is enclosed with
	 */
	public	AstCirNode	get_statement()	{ return this.c_state.get_location(); }
	/**
	 * @return	the condition for reaching the mutated location
	 */
	public	AstCoverTimesState	get_coverage_state()	{ return this.c_state; }
	/**
	 * @return	the state to localize the local position of defect
	 */
	public	AstSeedMutantState	get_mutation_state()	{ return this.s_state; }
	/**
	 * @return	the number of pairs of infection and initial error
	 */
	public	int	number_of_infection_pairs() { return this.i_states.size(); }
	/**
	 * @return 	the state infection condition
	 */
	public	AstConditionState	get_infection_state(int k) throws IndexOutOfBoundsException	{ return this.i_states.get(k); }
	/**
	 * @return	the initial error state for propagation
	 */
	public	AstAbstErrorState	get_ini_error_state(int k) throws IndexOutOfBoundsException	{ return this.p_states.get(k); }
	/**
	 * It appends a new infection-pair of condition and initial error state
	 * @param constraint
	 * @param init_error
	 * @throws Exception
	 */
	public 	void	put_infection_error(AstConditionState constraint, AstAbstErrorState init_error) throws Exception {
		if(constraint == null) {
			throw new IllegalArgumentException("Invalid constraint as null");
		}
		else if(init_error == null) {
			throw new IllegalArgumentException("Invalid init_error as null");
		}
		else { this.i_states.add(constraint); this.p_states.add(init_error); }
	}
	
	/* constructor */
	/**
	 * It creates a new ContextMutation for modeling
	 * @param mutant
	 * @param location
	 * @throws Exception
	 */
	private ContextMutation(Mutant mutant, AstCirNode location) throws Exception {
		if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant: null");
		}
		else if(location == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else {
			this.mutant = mutant; 
			this.c_state = AstContextStates.cov_time(location.statement_of(), 1, Integer.MAX_VALUE);
			this.s_state = AstContextStates.sed_muta(location, mutant);
			this.i_states = new ArrayList<AstConditionState>();
			this.p_states = new ArrayList<AstAbstErrorState>();
		}
	}
	
	/* definitions */
	/** {true, false} **/
	public static final SymbolExpression bool_value = SymbolFactory.variable(CBasicTypeImpl.bool_type, "@BoolValue");
	/** true **/
	public static final SymbolExpression true_value = SymbolFactory.variable(CBasicTypeImpl.bool_type, "@TrueValue");
	/** false **/
	public static final SymbolExpression fals_value = SymbolFactory.variable(CBasicTypeImpl.bool_type, "@FalsValue");
	/** integer or double **/
	public static final SymbolExpression numb_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NumbValue");
	/** { x | x > 0 } **/
	public static final SymbolExpression post_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@PostValue");
	/** { x | x < 0 } **/
	public static final SymbolExpression negt_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NegtValue");
	/** 0 **/
	public static final SymbolExpression zero_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@ZeroValue");
	/** { x | x <= 0 } **/
	public static final SymbolExpression npos_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NposValue");
	/** { x | x >= 0 } **/
	public static final SymbolExpression nneg_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NnegValue");
	/** { x | x != 0 } **/
	public static final SymbolExpression nzro_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NzroValue");
	/** address value in pointer **/
	public static final SymbolExpression addr_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@AddrValue");
	/** null **/
	public static final SymbolExpression null_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NullValue");
	/** {p | p != null} **/
	public static final SymbolExpression nnul_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NnulValue");
	/** abstract value of the exception **/
	public static final SymbolExpression trap_value = SymbolFactory.variable(CBasicTypeImpl.bool_type, "@Exception");	
	
	/* symbolic interfaces */
	/**
	 * @param node
	 * @return whether the node is a trap_value
	 */
	private	static	boolean is_trap_value(SymbolNode node) {
		if(node == null) { return false; }
		else if(node instanceof SymbolIdentifier) {
			return node.equals(trap_value);
		}
		else { return false; }
	}
	/**
	 * @param node
	 * @return whether the node is abstract domain expression
	 */
	private static	boolean	is_abst_value(SymbolNode node) {
		if(node == null) { return false; }
		else if(node instanceof SymbolIdentifier) {
			return 	node.equals(bool_value) || node.equals(true_value) || node.equals(fals_value) ||
					node.equals(numb_value) || node.equals(post_value) || node.equals(negt_value) ||
					node.equals(npos_value) || node.equals(nneg_value) || node.equals(zero_value) ||
					node.equals(nzro_value) || node.equals(addr_value) || node.equals(null_value) ||
					node.equals(nnul_value);
		}
		else { return false; }
	}
	/**
	 * @param source
	 * @return whether the source contains trap_value expression
	 */
	public	static	boolean	has_trap_value(SymbolExpression source) {
		if(source == null) {
			return false;
		}
		else {
			Queue<SymbolNode> queue = new LinkedList<SymbolNode>();
			queue.add(source);
			while(!queue.isEmpty()) {
				SymbolNode parent = queue.poll();
				if(is_trap_value(parent)) {
					return true;
				}
				else {
					for(SymbolNode child : parent.get_children()) {
						queue.add(child);
					}
				}
			}
			return false;
		}
	}
	/**
	 * @param source
	 * @return whether the source contains trap_value expression
	 */
	public	static	boolean	has_abst_value(SymbolExpression source) {
		if(source == null) {
			return false;
		}
		else {
			Queue<SymbolNode> queue = new LinkedList<SymbolNode>();
			queue.add(source);
			while(!queue.isEmpty()) {
				SymbolNode parent = queue.poll();
				if(is_abst_value(parent)) {
					return true;
				}
				else {
					for(SymbolNode child : parent.get_children()) {
						queue.add(child);
					}
				}
			}
			return false;
		}
	}
	/**
	 * @param expression	the symbolic expression to be evaluated based on state
	 * @param in_context	the input state from which the evaluation starts
	 * @param ou_context	the output state to preserve the result of evaluations
	 * @return				the expression evaluated from the state in arithmetic-safe way
	 * @throws Exception
	 */
	public	static	SymbolExpression evaluate(SymbolExpression expression, 
			SymbolContext in_context, SymbolContext ou_context) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else {
			try {
				return expression.evaluate(in_context, ou_context);
			}
			catch(ArithmeticException ex) { return trap_value; }
		}
	}
	
	/**
	 * It creates a context mutation object to the users
	 * @param mutant
	 * @param location
	 * @return
	 * @throws Exception
	 */
	public	static	ContextMutation	new_mutation(Mutant mutant, AstCirNode location) throws Exception {
		return new ContextMutation(mutant, location);
	}
	
}
