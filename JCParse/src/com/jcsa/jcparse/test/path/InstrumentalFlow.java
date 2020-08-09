package com.jcsa.jcparse.test.path;

/**
 * The flow between nodes in the instrumental path.
 * 
 * @author yukimula
 *
 */
public class InstrumentalFlow {
	
	/* definitions */
	/** the type of the flow in the instrumental path **/
	private InstrumentalLink type;
	/** the node from which the flow points to another **/
	private InstrumentalNode source;
	/** the node to which the flow points from another **/
	private InstrumentalNode target;
	/**
	 * create a flow from source to the target with the specified type
	 * @param type
	 * @param source
	 * @param target
	 * @throws Exception
	 */
	protected InstrumentalFlow(InstrumentalLink type, InstrumentalNode 
					source, InstrumentalNode target) throws Exception {
		if(type == null)
			throw new IllegalArgumentException("Invalid type: null");
		else if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target: null");
		else {
			this.type = type;
			this.source = source;
			this.target = target;
		}
	}
	
	/* getters */
	/**
	 * @return the type of the flow in the instrumental path
	 */
	public InstrumentalLink get_type() { return this.type; }
	/**
	 * @return the node from which the flow points to another
	 */
	public InstrumentalNode get_source() { return this.source; }
	/**
	 * @return the node to which the flow points from another
	 */
	public InstrumentalNode get_target() { return this.target; }
	
}
