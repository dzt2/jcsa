package com.jcsa.jcparse.lang.irlang.graph;

/**
 * The flow between executions of statements in the program flow graph, which can be:<br>
 * <code>
 * 	1. base --> *			[next_flow]								<br>
 * 	2. call	--> {beg_stmt}	[call_flow]	{user_defined}				<br>
 * 	3. {end_stmt} --> wait	[wait_flow]	{user_defined}				<br>
 * 	4. call	--> wait		[skip_flow]	{external + pointer}		<br>
 * 	5. brch --> *			[true_flow|fals_flow]					<br>
 * 	6. none --> *			[next_flow]								<br>
 * 	7. {beg} --> {end}		[skip_flow]	{external-body}				<br>
 * </code>
 * @author yukimula
 *
 */
public class CirExecutionFlow {
	
	/* definitions and constructor */
	private CirExecutionFlowType type;
	private CirExecution source, target;
	protected CirExecutionFlow(CirExecutionFlowType type, CirExecution 
			source, CirExecution target) throws IllegalArgumentException {
		if(type == null)
			throw new IllegalArgumentException("invalid type");
		else if(source == null)
			throw new IllegalArgumentException("invalid source");
		else if(target == null)
			throw new IllegalArgumentException("invalid target");
		else {
			this.type = type; this.source = source; this.target = target;
		}
	}
	
	/* getters */
	/**
	 * get the type of the flow in execution flow graph
	 * @return
	 */
	public CirExecutionFlowType get_type() { return this.type; }
	/**
	 * get the statement executed before the flow is performed
	 * @return
	 */
	public CirExecution get_source() { return this.source; }
	/**
	 * get the statement executed after the flow is performed
	 * @return
	 */
	public CirExecution get_target() { return this.target; }
	/**
	 * whether the flow can be reached from the entry of the function
	 * where it is defined.<br>
	 * <br>
	 * Note: the reach-ability here is only based on the structure of
	 * the execution flow graph rather than the program semantics. For
	 * example, a flow based on the predicate in branch statement can
	 * not be reached because the predicate is not-satisfiable, which
	 * is not considered in our case here.
	 * @return
	 */
	public boolean is_reachable() { 
		return this.source.is_reachable() && this.target.is_reachable(); 
	}
	/**
	 * @return A flow is valid if it belongs to the output flow of its source
	 *  	   as well as an input flow of its target.
	 */
	public boolean is_valid_flow() {
		for(CirExecutionFlow ou_flow : this.source.get_ou_flows()) {
			if(ou_flow == this) {
				return true;
			}
		}
		return false;
	}
	
	/* virtual constructor */
	/**
	 * @param type
	 * @param source
	 * @param target
	 * @return create an invalid flow from source to target w.r.t. the type
	 * 		   which was NOT linked to the entity of the execution nodes.
	 * @throws Exception
	 */
	public static CirExecutionFlow invalid_flow(CirExecutionFlowType type,
			CirExecution source, CirExecution target) throws Exception {
		return new CirExecutionFlow(type, source, target);
	}
	
}
