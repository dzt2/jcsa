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
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstTrapsErrorState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstValueErrorState;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.program.AstCirTree;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * 	It implements the parsing from AstMutation to ContextMutation.
 * 	
 * 	@author yukimula
 *
 */
public abstract class ContextMutationParser {
	
	/* attributes */
	private	AstCirTree		program;
	private	ContextMutation	outputs;
	public	ContextMutationParser() { }
	
	/* getters */
	/**
	 * @return the location referring to the AstNode source
	 * @throws Exception
	 */
	protected 	AstCirNode	get_location(AstNode source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(this.program == null) {
			throw new IllegalArgumentException("Invalid program: null");
		}
		else if(this.program.has_tree_node(source)) {
			return this.program.get_tree_node(source);
		}
		else {
			throw new IllegalArgumentException("Undefined: " + source);
		}
	}
	/**
	 * It puts a condition-initial error pair of state infection to the outputs
	 * @param constraint
	 * @param init_error
	 * @throws Exception
	 */
	protected 	void		put_infection(AstConditionState 
			constraint, AstAbstErrorState init_error) throws Exception {
		if(constraint == null) {
			throw new IllegalArgumentException("Invalid constraint: null");
		}
		else if(init_error == null) {
			throw new IllegalArgumentException("Invalid init_error: null");
		}
		else if(this.outputs == null) {
			throw new IllegalArgumentException("Invalid outputs as: null");
		}
		else { this.outputs.put_infection_error(constraint, init_error); }
	}
	/**
	 * @param min_times
	 * @return cov_time(statement, min_times, MAX_INT)
	 * @throws Exception
	 */
	protected	AstCoverTimesState	cov_time(int min_times) throws Exception {
		if(this.outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else if(min_times < 0) {
			throw new IllegalArgumentException("Invalid min_times: " + min_times);
		}
		else {
			return AstContextState.cov_time(this.outputs.get_location().
						statement_of(), min_times, Integer.MAX_VALUE);
		}
	}
	/**
	 * @param condition
	 * @return eva_cond(statement, condition, false)
	 * @throws Exception
	 */
	protected	AstConstraintState	eva_cond(Object condition) throws Exception {
		if(this.outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			return AstContextState.eva_cond(this.outputs.
					get_location().statement_of(), condition, false);
		}
	}
	/**
	 * @param condition
	 * @return eva_cond(statement, condition, true)
	 * @throws Exception
	 */
	protected	AstConstraintState	mus_cond(Object condition) throws Exception {
		if(this.outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			return AstContextState.eva_cond(this.outputs.
					get_location().statement_of(), condition, true);
		}
	}
	/**
	 * @return
	 * @throws Exception
	 */
	protected	AstTrapsErrorState	mut_trap() throws Exception {
		if(this.outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else {
			return AstContextState.mut_trap(this.outputs.get_location());
		}
	}
	/**
	 * @param muta_exec
	 * @return
	 * @throws Exception
	 */
	protected	AstBlockErrorState	mut_stmt(boolean muta_exec) throws Exception {
		if(this.outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else {
			return AstContextState.mut_stmt(this.
					outputs.get_location().statement_of(), muta_exec);
		}
	}
	/**
	 * @param orig_value
	 * @param muta_value
	 * @return set_expr(expression, orig_value, muta_value)
	 * @throws Exception
	 */
	protected	AstValueErrorState	set_expr(SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		if(this.outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else if(orig_value == null) {
			throw new IllegalArgumentException("Invalid orig_value: null");
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else {
			return AstContextState.set_expr(this.outputs.get_location(), orig_value, muta_value);
		}
	}
	/**
	 * @param orig_next
	 * @param muta_next
	 * @return
	 * @throws Exception
	 */
	protected	AstFlowsErrorState	mut_flow(AstCirNode orig_next, AstCirNode muta_next) throws Exception {
		if(this.outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else {
			return AstContextState.mut_flow(this.outputs.get_location().statement_of(), orig_next, muta_next);
		}
	}
	
	/* parsing */
	/**
	 * @param mutation
	 * @return it localizes to the reach-ability location
	 * @throws Exception
	 */
	protected abstract AstCirNode 	find_reach_location(AstMutation mutation) throws Exception;
	/**
	 * @param mutation
	 * @return it generates infection-error state changes
	 * @throws Exception
	 */
	protected abstract void			parse_infection_set(AstCirNode location, AstMutation mutation) throws Exception;
	/**
	 * It parses the ast-mutation to context-mutation as output
	 * @param program
	 * @param mutant
	 * @return
	 * @throws Exception
	 */
	public	ContextMutation			parse(AstCirTree program, Mutant mutant) throws Exception {
		if(program == null) {
			throw new IllegalArgumentException("Invalid program: null");
		}
		else if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant: null");
		}
		else {
			this.program = program; 
			AstCirNode location = this.find_reach_location(mutant.get_mutation());
			if(location != null) {
				this.outputs = new ContextMutation(mutant, location);
				this.parse_infection_set(location, mutant.get_mutation());
			}
			else {
				this.outputs = null;
			}
			this.program = null; return this.outputs;
		}
	}
	
}
