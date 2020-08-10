package com.jcsa.jcparse.test.inst;

/**
 * The path that describes the execution path fecthed from instrumental file.
 * @author yukimula
 *
 */
public class InstrumentalPath {
	
	/* definitions */
	/** the first node in the path **/
	private InstrumentalNode source;
	/** the final node in the path **/
	private InstrumentalNode target;
	/**
	 * create an empty path of instrumental file
	 */
	protected InstrumentalPath() {
		this.source = null;
		this.target = null;
	}
	
	/* getters */
	/**
	 * @return whether the path is empty
	 */
	public boolean is_empty() { return this.source == null; }
	/**
	 * @return the first node in the path 
	 */
	public InstrumentalNode get_source() { return this.source; }
	/**
	 * @return the final node in the path
	 */
	public InstrumentalNode get_target() { return this.target; }
	
	/* setters */
	/**
	 * append the node at the tail of the path
	 * @param link
	 * @param node
	 * @throws Exception
	 */
	protected void append(InstrumentalLink link, InstrumentalNode node) throws Exception {
		if(node == null)
			throw new IllegalArgumentException("Invalid node: null");
		else if(this.target == null) {
			this.source = node;
			this.target = node;
		}
		else {
			this.target.connect(link, node);
			this.target = node;
		}
	}
	/**
	 * append the path on the tail of this path
	 * @param link
	 * @param path
	 * @throws Exception
	 */
	protected void append(InstrumentalLink link, InstrumentalPath path) throws Exception {
		if(path == null)
			throw new IllegalArgumentException("Invalid path: null");
		else if(path.is_empty()) {
			return;		/* ignore the path when it is empty sequence */
		}
		else if(this.source == null) {	/* set the path as the given */
			this.source = path.source;
			this.target = path.target;
		}
		else {			/* connect this path with the specified path */
			this.target.connect(link, path.source);
			this.target = path.target;
		}
	}
	
}
