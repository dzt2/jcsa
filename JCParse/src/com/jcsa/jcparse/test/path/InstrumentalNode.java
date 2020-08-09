package com.jcsa.jcparse.test.path;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;

/**
 * 	The node in the executional path during testing, as a tuple:<br>
 * 	1. line: instrumental line that defines semantics of this node;<br>
 * 	2. in_flow: the executional flow pointing to this node from other;<br>
 * 	3. ou_flow: the executional flow pointing from this node to other;<br>
 * 
 * @author yukimula
 *
 */
public class InstrumentalNode {
	
	/* definitions */
	/** instrumental line that defines semantics of this node **/
	private InstrumentalLine line;
	/** executional flow pointing to this node from other **/
	private InstrumentalFlow in_flow;
	/** executional flow pointing from this node to other **/
	private InstrumentalFlow ou_flow;
	/**
	 * create an executional node in the path w.r.t. the line
	 * @param line
	 * @throws Exception
	 */
	private InstrumentalNode(InstrumentalLine line) throws Exception {
		if(line == null)
			throw new IllegalArgumentException("Invalid line: null");
		else {
			this.line = line;
			this.in_flow = null;
			this.ou_flow = null;
		}
	}
	
	/* getters */
	/**
	 * @return instrumental line that defines semantics of this node
	 */
	public InstrumentalLine get_line() { return this.line; }
	/**
	 * @return whether there is a flow points to this node from other
	 */
	public boolean has_in_flow() { return this.in_flow != null; }
	/**
	 * @return whether there is a flow points from this node to other
	 */
	public boolean has_ou_flow() { return this.ou_flow != null; }
	/**
	 * @return executional flow pointing to this node from other
	 */
	public InstrumentalFlow get_in_flow() { return this.in_flow; }
	/**
	 * @return executional flow pointing from this node to other
	 */
	public InstrumentalFlow get_ou_flow() { return this.ou_flow; }
	/**
	 * @return the executional node that directly points to this node
	 */
	public InstrumentalNode get_prev_node() {
		if(this.in_flow == null)
			return null;
		else
			return this.in_flow.get_source();
	}
	/**
	 * @return the executional node that directly points from this node
	 */
	public InstrumentalNode get_next_node() {
		if(this.ou_flow == null)
			return null;
		else
			return this.ou_flow.get_target();
	}
	/**
	 * @param tag
	 * @param location
	 * @return whether the node matches with the tag and location
	 */
	public boolean match(InstrumentalTag tag, AstNode location) {
		return this.line.get_tag() == tag && 
				this.line.get_location() == location;
	}
	/**
	 * @param location
	 * @return whether the node matches with the location
	 */
	public boolean match(AstNode location) {
		return this.line.get_location() == location;
	}
	/**
	 * @param link
	 * @param target
	 * @return the flow that connects this node to the target
	 * @throws Exception
	 */
	protected InstrumentalFlow connect(InstrumentalLink link, 
					InstrumentalNode target) throws Exception {
		if(link == null)
			throw new IllegalArgumentException("Invalid link: null");
		else if(target == null || target.in_flow != null)
			throw new IllegalArgumentException("Invalid target: " + target);
		else if(this.ou_flow != null)
			throw new IllegalArgumentException("Invalid source: " + this);
		else {
			InstrumentalFlow flow = new InstrumentalFlow(link, this, target);
			this.ou_flow = flow;
			target.in_flow = flow;
			return flow;
		}
	}
	/**
	 * @param tag
	 * @param location
	 * @return the first executional node after this node that matches
	 * 		   the specified type and location.
	 */
	public InstrumentalNode lfind(InstrumentalTag tag, AstNode location) {
		InstrumentalNode node = this;
		while(node != null) {
			if(node.match(tag, location)) {
				break;
			}
			else {
				node = node.get_next_node();
			}
		}
		return node;
	}
	/**
	 * @param location
	 * @return the first executional node after this node that matches
	 * 		   the specified type and location.
	 */
	public InstrumentalNode lfind(AstNode location) {
		InstrumentalNode node = this;
		while(node != null) {
			if(node.match(location)) {
				break;
			}
			else {
				node = node.get_next_node();
			}
		}
		return node;
	}
	/**
	 * @param tag
	 * @param location
	 * @return the first executional node before this node that matches
	 * 		   with the specified tag and location
	 */
	public InstrumentalNode rfind(InstrumentalTag tag, AstNode location) {
		InstrumentalNode node = this;
		while(node != null) {
			if(node.match(tag, location)) {
				break;
			}
			else {
				node = node.get_prev_node();
			}
		}
		return node;
	}
	/**
	 * @param location
	 * @return the first executional node before this node that matches
	 * 		   with the specified location
	 */
	public InstrumentalNode rfind(AstNode location) {
		InstrumentalNode node = this;
		while(node != null) {
			if(node.match(location)) {
				break;
			}
			else {
				node = node.get_prev_node();
			}
		}
		return node;
	}
	protected void set_value(byte[] value) { this.line.value = value; }
	
	/* creators */
	public static InstrumentalNode call_fun(AstFunctionDefinition location) throws Exception {
		return new InstrumentalNode(new InstrumentalLine(
				InstrumentalTag.call_fun, location, null));
	}
	public static InstrumentalNode exit_fun(AstFunctionDefinition location) throws Exception {
		return new InstrumentalNode(new InstrumentalLine(
				InstrumentalTag.exit_fun, location, null));
	}
	public static InstrumentalNode beg_stmt(AstStatement location) throws Exception {
		return new InstrumentalNode(new InstrumentalLine(
				InstrumentalTag.beg_stmt, location, null));
	}
	public static InstrumentalNode end_stmt(AstStatement location) throws Exception {
		return new InstrumentalNode(new InstrumentalLine(
				InstrumentalTag.end_stmt, location, null));
	}
	public static InstrumentalNode execute(AstStatement location) throws Exception {
		return new InstrumentalNode(new InstrumentalLine(
				InstrumentalTag.execute, location, null));
	}
	public static InstrumentalNode beg_expr(AstExpression location) throws Exception {
		return new InstrumentalNode(new InstrumentalLine(
				InstrumentalTag.beg_expr, location, null));
	}
	public static InstrumentalNode end_expr(AstExpression location) throws Exception {
		return new InstrumentalNode(new InstrumentalLine(
				InstrumentalTag.end_expr, location, null));
	}
	public static InstrumentalNode evaluate(AstExpression location) throws Exception {
		return new InstrumentalNode(new InstrumentalLine(
				InstrumentalTag.evaluate, location, null));
	}
	public static InstrumentalNode beg_node(AstNode location) throws Exception {
		return new InstrumentalNode(new InstrumentalLine(
				InstrumentalTag.beg_node, location, null));
	}
	public static InstrumentalNode end_node(AstNode location) throws Exception {
		return new InstrumentalNode(new InstrumentalLine(
				InstrumentalTag.end_node, location, null));
	}
	
}
