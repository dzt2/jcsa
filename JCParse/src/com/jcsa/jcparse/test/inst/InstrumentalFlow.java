package com.jcsa.jcparse.test.inst;

/**
 * The flow that connects two nodes in the instrumental path.
 * 
 * @author yukimula
 *
 */
public class InstrumentalFlow {
	
	/* definitions */
	/** the link that connects the source to the target **/
	private InstrumentalLink link;
	/** the source from which the flow points to target **/
	private InstrumentalNode source;
	/** the target to which the flow points from source **/
	private InstrumentalNode target;
	/**
	 * @param link the link that connects the source to the target
	 * @param source the source from which the flow points to target
	 * @param target the target to which the flow points from source
	 * @throws Exception
	 */
	protected InstrumentalFlow(InstrumentalLink link, InstrumentalNode 
			source, InstrumentalNode target) throws Exception {
		if(link == null)
			throw new IllegalArgumentException("Invalid link: null");
		else if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target: null");
		else {
			this.link = link;
			this.source = source;
			this.target = target;
		}
	}
	
	/* getters */
	/**
	 * @return the link that connects the source to the target
	 */
	public InstrumentalLink get_link() { return this.link; }
	/**
	 * @return the source from which the flow points to target
	 */
	public InstrumentalNode get_source() { return source; }
	/**
	 * @return the target to which the flow points from source
	 */
	public InstrumentalNode get_target() { return target; }
	
}
