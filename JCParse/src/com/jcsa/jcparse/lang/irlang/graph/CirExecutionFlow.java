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
	
	/* virtual constructor */
	/**
	 * @param type
	 * @param source
	 * @param target
	 * @return create an invalid flow from source to target w.r.t. the type
	 * 		   which was NOT linked to the entity of the execution nodes.
	 * @throws Exception
	 */
	public static CirExecutionFlow virtual_flow(CirExecutionFlowType type,
			CirExecution source, CirExecution target) throws Exception {
		return new CirExecutionFlow(type, source, target);
	}
	/**
	 * @return the flow is virtual if it is not equivalent with any flow connecting from its source to its target
	 * 		   with respect to the same flow type.
	 */
	public boolean is_virtual() {
		for(CirExecutionFlow flow : this.source.get_ou_flows()) {
			if(this.equals(flow)) {
				return false;
			}
		}
		return true;
	}
	/**
	 * @return the flow is actual if it is equivalent with some flow that connects from its source to its target
	 * 		   with respect to the same flow type. 
	 */
	public boolean is_actual() {
		for(CirExecutionFlow flow : this.source.get_ou_flows()) {
			if(this.equals(flow)) {
				return true;
			}
		}
		return false;
	}
	
	/* common interfaces */
	@Override
	public String toString() {
		return this.type + "(" + this.source + ", " + this.target + ")";
	}
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		else if(obj instanceof CirExecutionFlow) {
			CirExecutionFlow flow = (CirExecutionFlow) obj;
			return flow.type == this.type && flow.source == this.source && flow.target == this.target;
		}
		else
			return false;
	}
	
}
