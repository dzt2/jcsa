package com.jcsa.jcparse.lang.irlang.graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;

/**
 * The execution of a statement in program flow graph can be:<br>
 * <code>
 * 	assign_stmt				<br>
 * 	==>	bin_assign	[base]	<br>
 * 	==> inc_assign	[base]	<br>
 * 	==>	ini_assign	[base]	<br>
 * 	==> ret_assign	[base]	<br>
 * 	==> sav_assign	[base]	<br>
 * 	==> wat_assign	[wait]	<br>
 * 	goto_stmt		[none]	<br>
 * 	if_stmt			[brch]	<br>
 * 	case_stmt		[brch]	<br>
 * 	tag_stmt		[none]	<br>
 * 	call_stmt		[call]	<br>
 * </code>
 * @author yukimula
 *
 */
public class CirExecution {
	
	/* attributes */
	private CirExecutionFlowGraph graph;
	private int id;
	private CirExecutionType type;
	private CirStatement statement;
	private boolean reachable;
	private List<CirExecutionFlow> in, ou;
	
	/* constructor */
	/**
	 * create an execution in program flow graph with respect to the given statement
	 * @param graph
	 * @param id
	 * @param statement
	 * @throws Exception
	 */
	protected CirExecution(CirExecutionFlowGraph graph, int id, CirStatement statement) throws Exception {
		if(graph == null)
			throw new IllegalArgumentException("invalid graph");
		else if(statement == null)
			throw new IllegalArgumentException("invalid statement");
		else {
			this.graph = graph; this.id = id;
			this.type = this.type_of(statement);
			this.reachable = false;
			this.statement = statement;
			this.in = new LinkedList<CirExecutionFlow>();
			this.ou = new LinkedList<CirExecutionFlow>();
		}
	}
	/**
	 * determine the type of execution according to the type of statement
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	private CirExecutionType type_of(CirStatement statement) throws Exception {
		if(statement instanceof CirAssignStatement) {
			if(statement instanceof CirWaitAssignStatement) {
				return CirExecutionType.wait;
			}
			else {
				return CirExecutionType.base;
			}
		}
		else if(statement instanceof CirGotoStatement) {
			return CirExecutionType.none;
		}
		else if(statement instanceof CirIfStatement) {
			return CirExecutionType.brch;
		}
		else if(statement instanceof CirCaseStatement) {
			return CirExecutionType.brch;
		}
		else if(statement instanceof CirTagStatement) {
			return CirExecutionType.none;
		}
		else if(statement instanceof CirCallStatement) {
			return CirExecutionType.call;
		}
		else throw new IllegalArgumentException("unsupport: " + statement);
	}
	
	/* getters */
	/**
	 * get the flow graph where the execution of statement is defined.
	 * @return
	 */
	public CirExecutionFlowGraph get_graph() { return this.graph; }
	/**
	 * get the integer ID of the statement execution node in the graph.
	 * @return
	 */
	public int get_id() { return this.id; }
	/**
	 * get the type of the statement execution
	 * @return
	 */
	public CirExecutionType get_type() { return this.type; }
	/**
	 * get the statement to be executed
	 * @return
	 */
	public CirStatement get_statement() { return this.statement; }
	/**
	 * get the flows pointing to this execution node in the graph
	 * @return
	 */
	public Iterable<CirExecutionFlow> get_in_flows() { return this.in; }
	/**
	 * get the flows pointing from this execution node in graph.
	 * @return
	 */
	public Iterable<CirExecutionFlow> get_ou_flows() { return this.ou; }
	/**
	 * get the number of flows pointing to this node
	 * @return
	 */
	public int get_in_degree() { return this.in.size(); }
	/**
	 * get the number of flows pointing from this one
	 * @return
	 */
	public int get_ou_degree() { return this.ou.size(); }
	/**
	 * get the kth flow pointing to this node
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public CirExecutionFlow get_in_flow(int k) throws IndexOutOfBoundsException { return this.in.get(k); }
	/**
	 * get the kth flow pointing from this node 
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public CirExecutionFlow get_ou_flow(int k) throws IndexOutOfBoundsException { return this.ou.get(k); }
	/**
	 * whether the execution of the statement can be reached from the entry
	 * of the function where the statement belongs to. <br>
	 * <br>
	 * Note: the reach-ability here is based on the structure of the flow graph
	 * rather than program semantics, such as to determine the reach-ability of
	 * a flow that calling the exit() method will never return to the point of 
	 * the waiting-statement, which is NOT THE CASE we consider here.<br>
	 * 
	 * @return
	 */
	public boolean is_reachable() { return this.reachable; }
	@Override
	public String toString() {
		return this.graph.get_function().get_name() + "[" + this.id + "]";
	}
	/**
	 * @param type
	 * @return the output flows w.r.t. the given type
	 */
	public Iterable<CirExecutionFlow> get_ou_flows(CirExecutionFlowType type) {
		List<CirExecutionFlow> flows = new ArrayList<CirExecutionFlow>();
		for(CirExecutionFlow flow : this.ou) {
			if(flow.get_type() == type) {
				flows.add(flow);
			}
		}
		return flows;
	}
	/**
	 * @param type
	 * @return the input flows w.r.t. the given type
	 */
	public Iterable<CirExecutionFlow> get_in_flows(CirExecutionFlowType type) {
		List<CirExecutionFlow> flows = new ArrayList<CirExecutionFlow>();
		for(CirExecutionFlow flow : this.in) {
			if(flow.get_type() == type) {
				flows.add(flow);
			}
		}
		return flows;
	}
	
	/* setter */
	/**
	 * link this execution node to the next node with respect to the given type
	 * @param next
	 * @param type
	 * @return
	 * @throws Exception
	 */
	protected CirExecutionFlow link_to(CirExecution next, CirExecutionFlowType type) throws Exception {
		CirExecutionFlow flow = new CirExecutionFlow(type, this, next);
		this.ou.add(flow); next.in.add(flow); return flow;
	}
	/**
	 * set the execution of the statement as reachable or not.
	 * @param reachable
	 */
	protected void set_reachable(boolean reachable) { this.reachable = reachable; }
	
}
