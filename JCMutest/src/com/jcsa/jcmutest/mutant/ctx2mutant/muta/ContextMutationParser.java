package com.jcsa.jcmutest.mutant.ctx2mutant.muta;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.ctx2mutant.ContextMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstAbstErrorState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstBlockErrorState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstConditionState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstConstraintState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstContextState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstCoverTimesState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstFlowsErrorState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstValueErrorState;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * It implements the parsing from syntactic mutation (AstMutation) to the 
 * context-based mutation description (ContextMutation & AstContextState).	<br>
 * 	
 * @author yukimula
 *
 */
public abstract class ContextMutationParser {
	
	/* attributes */
	/** the syntactic mutation to be parsed to context-based forms **/
	private	Mutant			inputs;
	/** the context-based mutation description generated as output  **/
	private	ContextMutation	output;
	/** it creates an empty mutation parser for context-based form **/
	public ContextMutationParser() {}
	
	/* basic methods */
	/**
	 * @param source
	 * @return it finds the location in AstCirTree referring to the input source
	 * @throws Exception
	 */
	protected	AstCirNode	find_ast_location(AstNode source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(this.inputs == null) {
			throw new IllegalArgumentException("Invalid inputs: null");
		}
		else if(this.inputs.get_space().get_program().has_tree_node(source)) {
			return this.inputs.get_space().get_program().get_tree_node(source);
		}
		else {
			throw new IllegalArgumentException("Undefined as " + source);
		}
	}
	/**
	 * @param min_times
	 * @return cov_time(output.statement; min_times, MAX_INT)
	 * @throws Exception
	 */
	protected	AstCoverTimesState	cov_time(int min_times) throws Exception {
		if(min_times <= 0) {
			throw new IllegalArgumentException("Invalid: " + min_times);
		}
		else if(this.output == null) {
			throw new IllegalArgumentException("Invalid outputs");
		}
		else {
			return AstContextState.cov_time(this.output.get_statement(), min_times, Integer.MAX_VALUE);
		}
	}
	/**
	 * @param condition
	 * @return eva_cond(output.statement; condition, false)
	 * @throws Exception
	 */
	protected	AstConstraintState	eva_cond(Object condition) throws Exception {
		if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else if(this.output == null) {
			throw new IllegalArgumentException("Invalid output as: null");
		}
		else {
			return AstContextState.eva_cond(this.output.get_statement(), condition, false);
		}
	}
	/**
	 * @param condition
	 * @return eva_cond(output.statement; condition, true)
	 * @throws Exception
	 */
	protected	AstConstraintState	mus_cond(Object condition) throws Exception {
		if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else if(this.output == null) {
			throw new IllegalArgumentException("Invalid output as: null");
		}
		else {
			return AstContextState.eva_cond(this.output.get_statement(), condition, true);
		}
	}
	/**
	 * @param muta_exec
	 * @return set_stmt(output.statement, !muta_exec, muta_exec)
	 * @throws Exception
	 */
	protected	AstBlockErrorState	set_stmt(boolean muta_exec) throws Exception {
		if(this.output == null) {
			throw new IllegalArgumentException("Invalid output as: null");
		}
		else {
			return AstContextState.set_stmt(this.output.get_statement(), muta_exec);
		}
	}
	/**
	 * @return set_stmt(output.statement, true, trap_value)
	 * @throws Exception
	 */
	protected	AstBlockErrorState	trp_stmt()	throws Exception {
		if(this.output == null) {
			throw new IllegalArgumentException("Invalid output as: null");
		}
		else {
			return AstContextState.trp_stmt(this.output.get_location());
		}
	}
	/**
	 * @param orig_next
	 * @param muta_next
	 * @return
	 * @throws Exception
	 */
	protected	AstFlowsErrorState	set_flow(AstCirNode orig_next, AstCirNode muta_next) throws Exception {
		if(this.output == null) {
			throw new IllegalArgumentException("Invalid output as: null");
		}
		else if(orig_next == null) {
			throw new IllegalArgumentException("Invalid orig_next: null");
		}
		else if(muta_next == null) {
			throw new IllegalArgumentException("Invalid muta_next: null");
		}
		else {
			return AstContextState.set_flow(this.output.get_statement(), orig_next, muta_next);
		}
	}
	/**
	 * @param orig_value
	 * @param muta_value
	 * @return set_expr(expression; orig_value, muta_value)
	 * @throws Exception
	 */
	protected	AstValueErrorState	set_expr(SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		if(this.output == null) {
			throw new IllegalArgumentException("Invalid output as: null");
		}
		else if(orig_value == null) {
			throw new IllegalArgumentException("Invalid orig_value: null");
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else {
			return AstContextState.set_expr(this.output.get_location(), orig_value, muta_value);
		}
	}
	/**
	 * It puts the constraint-error pairs to the infection maps
	 * @param constraint
	 * @param init_error
	 * @throws Exception
	 */
	protected	void put_infection(AstConditionState constraint, AstAbstErrorState init_error) throws Exception {
		if(constraint == null) {
			throw new IllegalArgumentException("Invalid constraint: null");
		}
		else if(init_error == null) {
			throw new IllegalArgumentException("Invalid init_error: null");
		}
		else if(this.output == null) {
			throw new IllegalArgumentException("Invalid output as null");
		}
		else {
			this.output.put_infection_error(constraint, init_error);
		}
	}
	
	/* parse methods */
	/**
	 * @param mutation
	 * @return it localizes the location where the mutant is seeded.
	 * @throws Exception
	 */
	protected	abstract	AstCirNode	localize(AstMutation mutation) throws Exception;
	/**
	 * @param location
	 * @param mutation
	 * @throws Exception
	 */
	protected	abstract	void		generate(AstCirNode location, AstMutation mutation) throws Exception;
	/**
	 * It parses the syntactic mutation to context-based form
	 * @param mutant
	 * @return
	 * @throws Exception
	 */
	public	ContextMutation	parse(Mutant mutant) throws Exception {
		if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant: null");
		}
		else {
			this.inputs = mutant; this.output = null;
			AstCirNode location = this.localize(mutant.get_mutation());
			if(location != null) {
				this.output = new ContextMutation(mutant, location);
				this.generate(location, mutant.get_mutation());
			}
			return this.output;
		}
	}
	
}
