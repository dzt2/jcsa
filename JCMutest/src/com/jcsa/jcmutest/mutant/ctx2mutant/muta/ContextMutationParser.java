package com.jcsa.jcmutest.mutant.ctx2mutant.muta;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstContextMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.ContextMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.CovTimeMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.EvaCondMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.TrpStmtMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.program.AstCirTree;

/**
 * 	It parses the AstMutation to ContextMutation for analysis.
 * 	
 * 	@author yukimula
 *
 */
public abstract class ContextMutationParser {
	
	/* constructor */
	private	AstCirTree			program;
	private	AstContextMutation 	outputs;
	public ContextMutationParser() { }
	
	/* getters */
	/**
	 * @return the program from which the mutant is localized
	 */
	protected	AstCirTree	get_program() { return this.program; }
	/**
	 * it sets the infection condition-error pair to the set
	 * @param condition
	 * @param init_error
	 * @throws Exception
	 */
	protected	void		put_infection(ContextMutation condition, ContextMutation init_error) throws Exception {
		if(this.outputs == null) {
			throw new IllegalArgumentException("Undefined outputs: null");
		}
		else { this.outputs.put_infection(condition, init_error); }
	}
	/**
	 * @param source
	 * @return null if undefined
	 */
	protected	AstCirNode	get_location(AstNode source) {
		if(this.program == null) {
			return null;
		}
		else if(this.program.has_tree_node(source)) {
			return this.program.get_tree_node(source);
		}
		else {
			return null;
		}
	}
	
	/* implement */
	/**
	 * @param mutation
	 * @return it localizes to the reachability location
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
	public	AstContextMutation	parse(AstCirTree program, Mutant mutant) throws Exception {
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
				this.outputs = ContextMutation.ast_mutation(mutant, location);
				this.parse_infection_set(location, mutant.get_mutation());
			}
			else {
				this.outputs = null;
			}
			return this.outputs;
		}
	}
	
	/* generator */
	/**
	 * @param min_times
	 * @param max_times
	 * @return
	 * @throws Exception
	 */
	protected	CovTimeMutation	cov_time(int min_times, int max_times) throws Exception {
		if(this.outputs == null) {
			throw new IllegalArgumentException("No output specified");
		}
		else {
			AstCirNode statement = this.outputs.get_ast_location().statement_of();
			if(statement == null) {
				throw new IllegalArgumentException("Unable to localize: " + this.outputs);
			}
			else {
				return ContextMutation.cov_time(statement, min_times, max_times);
			}
		}
	}
	/**
	 * @param condition
	 * @param must_need
	 * @return
	 * @throws Exception
	 */
	protected 	EvaCondMutation	eva_cond(Object condition, boolean must_need) throws Exception {
		if(this.outputs == null) {
			throw new IllegalArgumentException("No output specified");
		}
		else {
			AstCirNode statement = this.outputs.get_ast_location().statement_of();
			if(statement == null) {
				throw new IllegalArgumentException("Unable to localize: " + this.outputs);
			}
			else {
				return ContextMutation.eva_cond(statement, condition, must_need);
			}
		}
	}
	/**
	 * @param location
	 * @return
	 * @throws Exception
	 */
	protected	TrpStmtMutation	trp_stmt() throws Exception {
		if(this.outputs == null) {
			throw new IllegalArgumentException("No output specified");
		}
		else {
			AstCirNode module = this.outputs.get_ast_location().module_of();
			if(module == null) {
				throw new IllegalArgumentException("Unable to locate: " + this.outputs);
			}
			else {
				return ContextMutation.trp_stmt(module);
			}
		}
	}
	
	
	
}
