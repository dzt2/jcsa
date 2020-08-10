package com.jcsa.jcparse.test.inst;

import com.jcsa.jcparse.lang.astree.AstNode;

/**
 * The node in the instrumental path describes a time-point of some unit occurs during
 * the testing process in a model of linked-list.
 * 
 * @author yukimula
 *
 */
public class InstrumentalNode {
	
	/* definitions */
	/** the unit that defines the semantic of the node **/
	private InstrumentalUnit unit;
	/** the flow directly points to this node from another **/
	private InstrumentalFlow in_flow;
	/** the flow directly points from this node to another **/
	private InstrumentalFlow ou_flow;
	/**
	 * create an isolated node w.r.t. the unit that defines its semantics
	 * @param unit
	 * @throws Exception
	 */
	protected InstrumentalNode(InstrumentalUnit unit) throws Exception {
		if(unit == null)
			throw new IllegalArgumentException("Invalid unit: null");
		else {
			this.unit = unit;
			this.in_flow = null;
			this.ou_flow = null;
		}
	}
	
	/* getters */
	/**
	 * @return the unit that defines its semantics
	 */
	public InstrumentalUnit get_unit() { return this.unit; }
	/**
	 * @return whether there is flow pointing to this node from another
	 */
	public boolean has_in_flow() { return this.in_flow != null; }
	/**
	 * @return whether there is flow pointing from this node to another
	 */
	public boolean has_ou_flow() { return this.ou_flow != null; }
	/**
	 * @return the flow directly points to this node from another
	 */
	public InstrumentalFlow get_in_flow() { return this.in_flow; }
	/**
	 * @return the flow directly points from this node to another
	 */
	public InstrumentalFlow get_ou_flow() { return this.ou_flow; }
	@Override
	public String toString() { return this.unit.toString(); }
	/**
	 * @param link
	 * @param target
	 * @return the flow that connects this node with the target node
	 * @throws Exception
	 */
	protected InstrumentalFlow connect(InstrumentalLink link, InstrumentalNode target) throws Exception {
		if(target == null || target.has_in_flow())
			throw new IllegalArgumentException("Invalid target: " + target);
		else if(this.has_ou_flow())
			throw new IllegalArgumentException("Invalid source: " + this);
		else {
			InstrumentalFlow flow = new InstrumentalFlow(link, this, target);
			this.ou_flow = flow;
			target.in_flow = flow;
			return flow;
		}
	}
	
	/* factory */
	/**
	 * @param location
	 * @return beg(location)
	 * @throws Exception
	 */
	protected static InstrumentalNode beg(AstNode location) throws Exception {
		return new InstrumentalNode(new 
				InstrumentalUnit(InstrumentalTag.beg, location));
	}
	/**
	 * @param location
	 * @return end(location)
	 * @throws Exception
	 */
	protected static InstrumentalNode end(AstNode location) throws Exception {
		return new InstrumentalNode(new 
				InstrumentalUnit(InstrumentalTag.end, location));
	}
	/**
	 * @param location
	 * @return pas(location)
	 * @throws Exception
	 */
	protected static InstrumentalNode pas(AstNode location) throws Exception {
		return new InstrumentalNode(new 
				InstrumentalUnit(InstrumentalTag.pas, location));
	}
	
	/* implication */
	/**
	 * @return the node directly points to this node from another
	 */
	public InstrumentalNode get_in_node() {
		if(this.has_in_flow())
			return this.in_flow.get_source();
		else
			return null;
	}
	/**
	 * @return the node direct points from this node to another
	 */
	public InstrumentalNode get_ou_node() {
		if(this.has_ou_flow())
			return this.ou_flow.get_target();
		else
			return null;
	}
	/**
	 * @param tag
	 * @param location
	 * @return whether the node matches with the (tag, location)
	 */
	private boolean match(InstrumentalTag tag, AstNode location) {
		return this.unit.get_tag() == tag && this.unit.get_location() == location;
	}
	/**
	 * @param location
	 * @return whether the node matches with the location
	 */
	private boolean match(AstNode location) {
		return this.unit.get_location() == location;
	}
	/**
	 * @param prior
	 * @param tag
	 * @param location
	 * @return the node that matches with tag(location) closest to this node
	 * @throws Exception
	 */
	private InstrumentalNode find(boolean prior, InstrumentalTag tag, AstNode location) {
		InstrumentalNode node = this;
		while(node != null) {
			if(node.match(tag, location)) {
				break;
			}
			else if(prior) {
				node = node.get_in_node();
			}
			else {
				node = node.get_ou_node();
			}
		}
		return node;
	}
	/**
	 * @param prior
	 * @param location
	 * @return the node that matches with tag(location) closest to this node
	 * @throws Exception
	 */
	private InstrumentalNode find(boolean prior, AstNode location) {
		InstrumentalNode node = this;
		while(node != null) {
			if(node.match(location)) {
				break;
			}
			else if(prior) {
				node = node.get_in_node();
			}
			else {
				node = node.get_ou_node();
			}
		}
		return node;
	}
	/**
	 * @param tag
	 * @param location
	 * @return the node prior to this one that matches with tag(location)
	 */
	public InstrumentalNode lfind(InstrumentalTag tag, AstNode location) {
		return this.find(true, tag, location);
	}
	/**
	 * @param tag
	 * @param location
	 * @return the node prior to this one that matches with any_tag(location)
	 */
	public InstrumentalNode lfind(AstNode location) {
		return this.find(true, location);
	}
	/**
	 * @param tag
	 * @param location
	 * @return the node next to this one that matches with tag(location)
	 */
	public InstrumentalNode rfind(InstrumentalTag tag, AstNode location) {
		return this.find(false, tag, location);
	}
	/**
	 * @param tag
	 * @param location
	 * @return the node next to this one that matches with any_tag(location)
	 */
	public InstrumentalNode rfind(AstNode location) {
		return this.find(false, location);
	}
	
}
