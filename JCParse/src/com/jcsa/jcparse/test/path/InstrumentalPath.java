package com.jcsa.jcparse.test.path;

import com.jcsa.jcparse.lang.astree.AstNode;

public class InstrumentalPath {
	
	/* definition */
	/** the source node in AST where the path refers to **/
	private AstNode ast_source;
	/** the first node being executed within the path **/
	private InstrumentalNode source;
	/** the final node being executed within the path **/
	private InstrumentalNode target;
	/**
	 * create an empty path that refers to the range of the ast-source
	 * during execution process.
	 * @param ast_source
	 * @throws Exception
	 */
	protected InstrumentalPath(AstNode ast_source) {
		this.ast_source = ast_source;
		this.source = null;
		this.target = null;
	}
	
	/* getters */
	/**
	 * @return the AST source node to which the path describes
	 */
	public AstNode get_ast_source() { return this.ast_source; }
	/**
	 * @return whether the path is empty.
	 */
	public boolean is_empty() { return this.source == null; }
	/**
	 * @param type
	 * @param next_node
	 * @return the flow that connects the last node in the path with the next
	 * @throws Exception
	 */
	public InstrumentalFlow append(InstrumentalLink type, 
			InstrumentalNode next_node) throws Exception {
		if(type == null)
			throw new IllegalArgumentException("Invalid type: null");
		else if(next_node == null)
			throw new IllegalArgumentException("Invalid next_node");
		else if(this.target == null) {
			this.source = next_node;
			this.target = next_node;
			return null;
		}
		else {
			InstrumentalFlow flow = this.target.connect(type, next_node);
			this.target = next_node;
			return flow;
		}
	}
	/**
	 * @param type
	 * @param path
	 * @return append the tail of this path to the head of the given path
	 * @throws Exception
	 */
	public InstrumentalFlow append(InstrumentalLink type, 
				InstrumentalPath path) throws Exception {
		if(type == null)
			throw new IllegalArgumentException("Invalid type: null");
		else if(path == null)
			throw new IllegalArgumentException("Invalid next_node");
		else if(this.source == null) {
			this.source = path.source;
			this.target = path.target;
			return null;
		}
		else if(path.is_empty()) {
			return null;	// no nodes are appended
		}
		else {
			InstrumentalFlow flow = this.target.connect(type, path.source);
			this.target = path.target;
			return flow;
		}
	}
	/**
	 * @param tag
	 * @param location
	 * @return the first node from the path's source to the node 
	 * 		   that matches with the tag and location.
	 */
	public InstrumentalNode lfind(InstrumentalTag tag, AstNode location) {
		if(this.source == null)
			return null;
		else
			return this.source.lfind(tag, location);
	}
	/**
	 * @param location
	 * @return the first node from the path's source to the node 
	 * 		   that matches with the location.
	 */
	public InstrumentalNode lfind(AstNode location) {
		if(this.source == null)
			return null;
		else
			return this.source.lfind(location);
	}
	/**
	 * @param tag
	 * @param location
	 * @return the final node from the path's target to the node 
	 * 		   that matches with the tag and location.
	 */
	public InstrumentalNode rfind(InstrumentalTag tag, AstNode location) {
		if(this.target == null)
			return null;
		else
			return this.target.rfind(tag, location);
	}
	/**
	 * @param location
	 * @return the final node from the path's target to the node 
	 * 		   that matches with the location.
	 */
	public InstrumentalNode rfind(AstNode location) {
		if(this.target == null)
			return null;
		else
			return this.target.rfind(location);
	}
	public InstrumentalNode get_source() { return this.source; }
	public InstrumentalNode get_target() { return this.target; }
	
}
