package com.jcsa.jcparse.flwa.defuse;

import com.jcsa.jcparse.flwa.graph.CirInstanceNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;

public class CDefineUseEdge {
	
	/** the node from which this edge points **/
	private CDefineUseNode source;
	/** the node to which this edge points **/
	private CDefineUseNode target;
	/** the assign statement that the edge represents **/
	private CirInstanceNode instance;
	
	/**
	 * create an edge from the define|use node to the use|define node
	 * @param source
	 * @param target
	 * @param statement
	 * @throws Exception
	 */
	protected CDefineUseEdge(CDefineUseNode source, 
			CDefineUseNode target, CirInstanceNode instance) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target: null");
		else if(instance == null)
			throw new IllegalArgumentException("Invalid statement: null");
		else if(!(instance.get_execution().get_statement() instanceof CirAssignStatement))
			throw new IllegalArgumentException("Invalid statement: not an assignment");
		else {
			this.source = source;
			this.target = target;
			this.instance = instance;
		}
	}
	
	/* getters */
	/**
	 * get the source node from which the edge points
	 * @return
	 */
	public CDefineUseNode get_source() { return this.source; }
	/**
	 * get the target node to which this edge points
	 * @return
	 */
	public CDefineUseNode get_target() { return this.target; }
	/**
	 * get the instance of the assign statement being executed that this edge refers to
	 * @return
	 */
	public CirInstanceNode get_instance() { return instance; }
	/**
	 * get the execution of assign statement
	 * @return
	 */
	public CirExecution get_execution() { return this.instance.get_execution(); }
	/**
	 * get the assign statement that the definition node of the edge represents
	 * @return
	 */
	public CirAssignStatement get_statement() { 
		return (CirAssignStatement) this.instance.
				get_execution().get_statement(); 
	}
	/**
	 * Is this edge links from a definition node to a usage node, which
	 * represents the defined node is further used in the usage node.
	 * @return
	 */
	public boolean is_def_use() {  
		return this.source.is_define() && this.target.is_usage();
	}
	/**
	 * Is this edge links from a usage node to a definition node, which
	 * represents the usage node is used in assigning the defined node.
	 * @return
	 */
	public boolean is_use_def() {
		return this.source.is_usage() && this.target.is_define();
	}
	
}
