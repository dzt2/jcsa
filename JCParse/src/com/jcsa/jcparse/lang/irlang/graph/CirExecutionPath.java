package com.jcsa.jcparse.lang.irlang.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Stack;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirBegStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.parse.symbol.SymbolEvaluator;


/**
 * It denotes a series of execution (edges) in the CFG to represent its path.
 *
 * @author yukimula
 *
 */
public class CirExecutionPath {

	/* attributes */
	/** the source node of the path **/
	private CirExecution source;
	/** the target node of the path **/
	private CirExecution target;
	/** the sequence of edges linking the source to the target **/
	private LinkedList<CirExecutionEdge> edges;

	/* constructor */
	/**
	 * create an empty path with one single node without any edges
	 * @param source
	 * @throws IllegalArgumentException
	 */
	public CirExecutionPath(CirExecution source) throws IllegalArgumentException {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else {
			this.source = source; this.target = source;
			this.edges = new LinkedList<>();
		}
	}

	/* getters */
	/**
	 * @return the source node of the path
	 */
	public CirExecution get_source() { return this.source; }
	/**
	 * @return the target node of the path
	 */
	public CirExecution get_target() { return this.target; }
	/**
	 * @return the length of the edges in the path
	 */
	public int get_length() { return this.edges.size(); }
	/**
	 * @return whether the path is empty without any edeges
	 */
	public boolean is_empty() { return this.edges.isEmpty(); }
	/**
	 * @return the sequence of execution flows in the path
	 */
	public Iterable<CirExecutionEdge> get_edges() { return this.edges; }
	/**
	 * @param k [0, length - 1]
	 * @return the kth edge in the path
	 * @throws IndexOutOfBoundsException
	 */
	public CirExecutionEdge get_edge(int k) throws IndexOutOfBoundsException {
		return this.edges.get(k);
	}
	/**
	 * @param k
	 * @return the flow of the kth edge in the path
	 * @throws IndexOutOfBoundsException
	 */
	public CirExecutionFlow get_flow(int k) throws IndexOutOfBoundsException {
		return this.get_edge(k).get_flow();
	}
	/**
	 * @param k [0, length]
	 * @return the kth node in the path
	 * @throws IndexOutOfBoundsException
	 */
	public CirExecution get_node(int k) throws IndexOutOfBoundsException {
		if(k < 0) {
			throw new IndexOutOfBoundsException("Invalid index: " + k);
		}
		else if(k < this.edges.size()) {
			return this.edges.get(k).get_source();
		}
		else if(k == this.edges.size()) {
			return this.target;
		}
		else {
			throw new IndexOutOfBoundsException("Invalid index: " + k);
		}
	}
	/**
	 * @param reverse whether to visit the edges in the path reversively
	 * @return
	 */
	public Iterator<CirExecutionEdge> get_iterator(boolean reverse) {
		if(reverse) {
			return this.edges.descendingIterator();
		}
		else {
			return this.edges.iterator();
		}
	}
	/**
	 * @return the iterator to access edge in sequence of the path
	 */
	public Iterator<CirExecutionEdge> get_iterator() { return this.edges.iterator(); }
	/**
	 * @return the first edge in the path
	 */
	public CirExecutionEdge get_first_edge()  {
		if(this.edges.isEmpty()) {
			return null;
		}
		else {
			return this.edges.getFirst();
		}
	}
	/**
	 * @return the final edge in the path
	 * @throws NoSuchElementException
	 */
	public CirExecutionEdge get_final_edge()  {
		if(this.edges.isEmpty()) {
			return null;
		}
		else {
			return this.edges.getLast();
		}
	}
	/**
	 * @return the flow of the first edge in the path
	 */
	public CirExecutionFlow get_first_flow() {
		if(this.edges.isEmpty()) {
			return null;
		}
		else {
			return this.edges.getFirst().get_flow();
		}
	}
	/**
	 * @return the flow of the final edge in the path
	 */
	public CirExecutionFlow get_final_flow() {
		if(this.edges.isEmpty()) {
			return null;
		}
		else {
			return this.edges.getLast().get_flow();
		}
	}

	/* universals */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		for(CirExecutionEdge edge : this.edges) {
			buffer.append(edge.get_flow().get_source().toString());
			buffer.append(" <" + edge.get_flow().get_type() + "> ");
		}
		buffer.append(this.target.toString());
		return buffer.toString();
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		else if(obj instanceof CirExecutionPath) {
			CirExecutionPath path = (CirExecutionPath) obj;
			if(path.source == this.source && path.target == this.target
							&& path.get_length() == this.get_length()) {
				for(int k = 0; k < this.edges.size(); k++) {
					if(!path.edges.get(k).get_flow().equals(this.edges.get(k).get_flow()))
						return false;
				}
				return true;
			}
			else
				return false;
		}
		else
			return false;
	}
	@Override
	public CirExecutionPath clone() {
		CirExecutionPath path = new CirExecutionPath(this.source);
		for(CirExecutionEdge edge : this.edges) {
			path.append(edge.get_flow());
		}
		path.target = this.target;
		return path;
	}

	/* setters */
	/**
	 * append the flow in the tail of the execution path and return the new edge
	 * @param flow
	 * @return the edge w.r.t. the flow appended from the tail of execution path
	 * @throws IllegalArgumentException
	 */
	public CirExecutionEdge append(CirExecutionFlow flow) throws IllegalArgumentException {
		if(flow == null) {
			throw new IllegalArgumentException("Invalid flow: null");
		}
		else if(this.target == flow.get_source()) {
			CirExecutionEdge edge = new CirExecutionEdge(this, flow);
			this.edges.add(edge); this.target = flow.get_target();
			return edge;
		}
		else {
			throw new IllegalArgumentException("Invalid flow: " + flow);
		}
	}
	/**
	 * insert the flow in the head of the execution path and return the new edge
	 * @param flow
	 * @return the edge w.r.t. the flow inserted into the head of execution path
	 * @throws IllegalArgumentException
	 */
	public CirExecutionEdge insert(CirExecutionFlow flow) throws IllegalArgumentException {
		if(flow == null) {
			throw new IllegalArgumentException("Invalid flow: null");
		}
		else if(this.source == flow.get_target()) {
			CirExecutionEdge edge = new CirExecutionEdge(this, flow);
			this.edges.addFirst(edge); this.source = flow.get_source();
			return edge;
		}
		else {
			throw new IllegalArgumentException("Invalid flow: " + flow);
		}
	}
	/**
	 * connect the path on the left and extend this path
	 * @param path
	 * @throws Exception
	 */
	public void l_connect(CirExecutionPath path) throws Exception {
		if(path == null) {
			throw new IllegalArgumentException("Invalid path: null");
		}
		else if(path.target == this.source) {
			Iterator<CirExecutionEdge> iterator = path.get_iterator(true);
			while(iterator.hasNext()) {
				CirExecutionEdge edge = iterator.next();
				this.insert(edge.get_flow());
			}
		}
		else {
			throw new IllegalArgumentException("Invalid: " + path);
		}
	}
	/**
	 * connect the path on the right and extend the path
	 * @param path
	 * @throws Exception
	 */
	public void r_connect(CirExecutionPath path) throws Exception {
		if(path == null) {
			throw new IllegalArgumentException("Invalid path: null");
		}
		else if(path.source == this.target) {
			Iterator<CirExecutionEdge> iterator = path.get_iterator(false);
			while(iterator.hasNext()) {
				CirExecutionEdge edge = iterator.next();
				this.append(edge.get_flow());
			}
		}
		else {
			throw new IllegalArgumentException("Invalid: " + path);
		}
	}

	/* inference */
	/**
	 * @return the flow that calls the entry of the execution path
	 */
	public CirExecutionFlow get_call_flow_of_source() throws Exception {
		/* 1. declarations */
		Stack<CirExecutionFlow> stack = new Stack<>();
		Iterator<CirExecutionEdge> iterator = this.get_iterator(false);

		/* 2. infer the call-point based on first return-flow */
		while(iterator.hasNext()) {
			CirExecutionEdge edge = iterator.next();
			if(edge.get_type() == CirExecutionFlowType.call_flow) {
				stack.push(edge.get_flow());
			}
			else if(edge.get_type() == CirExecutionFlowType.retr_flow) {
				if(stack.isEmpty()) {	/* infer based on the first return */
					CirExecutionFlow retr_flow = edge.get_flow();
					CirExecution wait_execution = retr_flow.get_target();
					CirExecution call_execution = wait_execution.get_graph().
									get_execution(wait_execution.get_id() - 1);
					return call_execution.get_ou_flow(0);
				}
				else {
					CirExecutionFlow call_flow = stack.pop();
					CirExecutionFlow retr_flow = edge.get_flow();
					if(!CirExecutionFlow.match_call_retr_flow(call_flow, retr_flow)) {
						throw new RuntimeException(call_flow + " --> " + retr_flow);
					}
				}
			}
		}

		/* 3. infer the call-point based on local entry */
		CirExecution source = this.source.get_graph().get_entry();
		if(source.get_in_degree() == 1) {
			return source.get_in_flow(0);
		}
		else {
			return null;
		}
	}
	/**
	 * @return the flow that returns from the target of this path
	 * @throws Exception
	 */
	public CirExecutionFlow get_retr_flow_of_target() throws Exception {
		/* 1. declarations */
		Stack<CirExecutionFlow> stack = new Stack<>();
		Iterator<CirExecutionEdge> iterator = this.get_iterator(true);

		/* 2. infer the retr-flow based on last call-point in path */
		while(iterator.hasNext()) {
			CirExecutionEdge edge = iterator.next();
			if(edge.get_type() == CirExecutionFlowType.retr_flow) {
				stack.push(edge.get_flow());
			}
			else if(edge.get_type() == CirExecutionFlowType.call_flow) {
				if(stack.isEmpty()) {
					CirExecutionFlow call_flow = edge.get_flow();
					CirExecution call_execution = call_flow.get_source();
					CirExecution wait_execution = call_execution.get_graph().
									get_execution(call_execution.get_id() + 1);
					return wait_execution.get_in_flow(0);
				}
				else {
					CirExecutionFlow call_flow = edge.get_flow();
					CirExecutionFlow retr_flow = stack.pop();
					if(!CirExecutionFlow.match_call_retr_flow(call_flow, retr_flow)) {
						throw new RuntimeException(call_flow + " --> " + retr_flow);
					}
				}
			}
		}

		/* 3. infer the retr-flow based on exit of the target function */
		CirExecution target = this.target.get_graph().get_exit();
		if(target.get_ou_degree() == 1) {
			return target.get_ou_flow(0);
		}
		else {
			return null;
		}
	}
	private SymbolExpression condition_of(CirExpression expression) throws Exception {
		return SymbolEvaluator.evaluate_on(SymbolFactory.sym_condition(expression, true));
	}
	/**
	 * @param cross_function whether to collect input flows based on function-cross analysis
	 * @return the set of possible flows reaching the source of the path
	 * @throws Exception
	 */
	public Collection<CirExecutionFlow> get_in_flows_of_source(boolean cross_function) throws Exception {
		Collection<CirExecutionFlow> flows = new ArrayList<>();
		if(this.source.get_in_degree() == 0) { /* no available flow */ }
		else if(this.source.get_in_degree() == 1) {	/* unique flows */
			flows.add(this.source.get_in_flow(0));
		}
		else {									/* context-sensitive decide */
			CirStatement statement = this.source.get_statement();
			if(statement instanceof CirBegStatement) {
				CirExecutionFlow call_flow = this.get_call_flow_of_source();
				if(call_flow != null) {
					flows.add(call_flow);
				}
				else {
					if(cross_function) {
						for(CirExecutionFlow flow : this.source.
								get_in_flows(CirExecutionFlowType.call_flow)) {
							flows.add(flow);
						}
					}
				}
			}
			else {
				for(CirExecutionFlow flow : this.source.get_in_flows()) {
					flows.add(flow);
				}
			}
		}
		return flows;
	}
	/**
	 * @param cross_function whether to collect input flows based on function-cross analysis
	 * @return the set of possible flows reaching from the target of the path
	 * @throws Exception
	 */
	public Collection<CirExecutionFlow> get_ou_flows_of_target(boolean cross_function) throws Exception {
		Collection<CirExecutionFlow> flows = new ArrayList<>();
		if(this.target.get_ou_degree() == 0) { /* none output flows */ }
		else if(this.target.get_ou_degree() == 1) {	/* unique flow */
			flows.add(this.target.get_ou_flow(0));
		}
		else {
			CirStatement statement = this.target.get_statement();
			if(statement instanceof CirIfStatement) {
				SymbolExpression condition = this.condition_of(
						((CirIfStatement) statement).get_condition());
				Iterable<CirExecutionFlow> ou_flows;
				if(condition instanceof SymbolConstant) {
					if(((SymbolConstant) condition).get_bool()) {
						ou_flows = this.target.get_ou_flows(CirExecutionFlowType.true_flow);
					}
					else {
						ou_flows = this.target.get_ou_flows(CirExecutionFlowType.fals_flow);
					}
				}
				else {
					ou_flows = this.target.get_ou_flows();
				}
				for(CirExecutionFlow flow : ou_flows) flows.add(flow);
			}
			else if(statement instanceof CirCaseStatement) {
				SymbolExpression condition = this.condition_of(
						((CirCaseStatement) statement).get_condition());
				Iterable<CirExecutionFlow> ou_flows;
				if(condition instanceof SymbolConstant) {
					if(((SymbolConstant) condition).get_bool()) {
						ou_flows = this.target.get_ou_flows(CirExecutionFlowType.true_flow);
					}
					else {
						ou_flows = this.target.get_ou_flows(CirExecutionFlowType.fals_flow);
					}
				}
				else {
					ou_flows = this.target.get_ou_flows();
				}
				for(CirExecutionFlow flow : ou_flows) flows.add(flow);
			}
			else if(statement instanceof CirEndStatement) {
				CirExecutionFlow next_flow = this.get_retr_flow_of_target();
				if(next_flow != null) {
					flows.add(next_flow);
				}
				else {
					if(cross_function) {
						for(CirExecutionFlow flow : this.target.
								get_ou_flows(CirExecutionFlowType.retr_flow)) {
							flows.add(flow);
						}
					}
				}
			}
			else {
				for(CirExecutionFlow flow : this.target.get_ou_flows()) {
					flows.add(flow);
				}
			}
		}
		return flows;
	}
	/**
	 * @param beg_index
	 * @param end_index
	 * @return sub-sequence of path[beg_index: end_index] not including end_index
	 * @throws Exception
	 */
	public CirExecutionPath get_path(int beg_index, int end_index) throws Exception {
		if(beg_index < 0 || beg_index >= this.edges.size()) {
			throw new IllegalArgumentException("Invalid beg_index: " + beg_index);
		}
		else if(end_index > this.edges.size()) {
			throw new IllegalArgumentException("Invalid end_index: " + end_index);
		}
		else {
			CirExecutionPath path = new CirExecutionPath(this.get_node(beg_index));
			for(int index = beg_index; index < end_index; index++) {
				path.append(this.edges.get(index).get_flow());
			}
			return path;
		}
	}

}
