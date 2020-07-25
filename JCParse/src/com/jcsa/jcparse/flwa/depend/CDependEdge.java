package com.jcsa.jcparse.flwa.depend;

/**
 * The dependence relationship between nodes of statement in program
 * @author yukimula
 *
 */
public class CDependEdge {
	
	private CDependType type;
	private CDependNode source;
	private CDependNode target;
	private Object element;
	
	protected CDependEdge(CDependType type, CDependNode source, 
			CDependNode target, Object element) throws Exception {
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
			this.element = element;
		}
	}
	
	/**
	 * get the type of dependence 
	 * @return
	 */
	public CDependType get_type() { return this.type; }
	/**
	 * get the node of statement depends on another
	 * @return
	 */
	public CDependNode get_source() { return this.source; }
	/**
	 * get the node of statement depended by another
	 * @return
	 */
	public CDependNode get_target() { return this.target; }
	/**
	 * get the element, either predicate, reference or null
	 * @return
	 */
	public Object get_element() { return this.element; }
	
}
