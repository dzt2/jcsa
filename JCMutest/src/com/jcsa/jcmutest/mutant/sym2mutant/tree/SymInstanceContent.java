package com.jcsa.jcmutest.mutant.sym2mutant.tree;

import java.util.Collection;
import java.util.List;

import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymInstance;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateError;
import com.jcsa.jcmutest.mutant.sym2mutant.cond.SymInstanceStatus;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * It preserves the status for evaluating a symbolic instance.
 *
 * @author yukimula
 *
 */
public abstract class SymInstanceContent {

	/* definitions */
	/** the symbolic instance being represented **/
	private SymInstance instance;
	/** the abstract status is not used for accumulation **/
	private SymInstanceStatus status;
	/**
	 * create a content node of symbolic instance
	 * @param instance
	 * @throws Exception
	 */
	protected SymInstanceContent(SymInstance instance) throws Exception {
		if(instance == null)
			throw new IllegalArgumentException("Invalid instance: null");
		else {
			this.instance = instance;
			this.status = new SymInstanceStatus(instance);
		}
	}

	/* getters */
	/**
	 * @return the symbolic instance that the content evaluates
	 */
	public SymInstance get_instance() { return this.instance; }
	/**
	 * @return
	 */
	public CirExecution get_execution() { return this.instance.get_execution(); }
	/**
	 * @return whether the instance it represents is a constraint
	 */
	public boolean is_constraint() { return this.instance instanceof SymConstraint; }
	/**
	 * @return whether the instance it represents is a state error
	 */
	public boolean is_state_error() { return this.instance instanceof SymStateError; }
	/**
	 * @return the abstract status is not used for accumulation
	 */
	public SymInstanceStatus get_status() { return this.status; }

	/* setters */
	/**
	 * clear the accumulate status (concrete) in the content
	 */
	protected void clc_status() { this.status.clear_results(); }
	/**
	 * update the accumulate status (concrete) in the content
	 * @param contexts
	 * @throws Exception
	 */
	protected Boolean add_status(SymbolProcess contexts) throws Exception {
		return this.status.evaluate(contexts);
	}

	/* inference */
	/**
	 * @return the sequence of edges from root until this node
	 */
	public abstract List<SymInstanceTreeEdge> get_prev_path();
	/**
	 * @return the sequence of edges from this node until all the leafs
	 */
	public abstract Collection<List<SymInstanceTreeEdge>> get_post_paths();
}
