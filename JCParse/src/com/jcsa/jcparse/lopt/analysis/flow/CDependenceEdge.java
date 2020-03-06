package com.jcsa.jcparse.lopt.analysis.flow;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceNode;

/**
 * The relationship between the statement as either the data or control dependence.
 * @author yukimula
 *
 */
public abstract class CDependenceEdge {
	
	/* attributes and constructor */
	/** the node that dependes on another **/
	private CDependenceNode source;
	/** the node that is depended by another **/
	private CDependenceNode target;
	/**
	 * create the dependence that source depends on the target.
	 * @param source
	 * @param target
	 * @throws Exception
	 */
	protected CDependenceEdge(CDependenceNode source, CDependenceNode target) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target: null");
		else { this.source = source; this.target = target; }
	}
	
	/* getters */
	/**
	 * get the source node that directly depends on another
	 * @return
	 */
	public CDependenceNode get_source() { return this.source; }
	/**
	 * get the target node that is directly depended by another
	 * @return
	 */
	public CDependenceNode get_target() { return this.target; }
	/**
	 * get the instance of the statement of the source that depends on another
	 * @return
	 */
	public CirInstanceNode get_source_instance() { return source.get_instance(); }
	/**
	 * get the instance of the statement of the target depended by another
	 * @return
	 */
	public CirInstanceNode get_target_instance() { return target.get_instance(); }
	/**
	 * get the execution of the statement of the source that depends on another
	 * @return
	 */
	public CirExecution get_source_execution() { return source.get_instance().get_execution(); }
	/**
	 * get the execution of the statement of the target that is depended by another
	 * @return
	 */
	public CirExecution get_target_execution() { return target.get_instance().get_execution(); }
	/**
	 * get the statement of the source that depends on another
	 * @return
	 */
	public CirStatement get_source_statement() { return source.get_instance().get_execution().get_statement(); }
	/**
	 * get the statement of the target that is depended by another
	 * @return
	 */
	public CirStatement get_target_statement() { return target.get_instance().get_execution().get_statement(); }
	
}
