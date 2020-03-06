package __backup__;

import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * The node in semantic analysis graph represents a semantic property that (is expected to) hold
 * at a specific program location or point in the code under analysis, which can be described as:<br>
 * <code>(word, location, parameter)</code><br>
 * such that <code>word</code> defines the type of the property, <code>location</code> specifies
 * the subject that the <code>word</code> is performed, while the <code>parameter</code> refines
 * the details of the semantic property under analysis.<br>
 * 
 * @author yukimula
 *
 */
public class CirSemanticNode {
	
	/* attributes */
	/** the word describing the type of the semantic property **/
	private CirSemanticWord word;
	/** the location in C-like intermediate representation code
	 *  where the semantic property (is expected to) hold at. **/
	private CirNode location;
	/** the parameter provides more details about the property **/
	private Object parameter;
	/** the set of links in semantic graph that can cause the 
	 * 	property of this node to occur. **/
	protected List<CirSemanticLink> in;
	/** the set of links in semantic graph of which target nodes
	 * 	can be caused by the semantic property hold in this node ***/
	protected List<CirSemanticLink> ou;
	
	/* constructor */
	/**
	 * create a node describing the semantic property hold at the specified point in 
	 * C-like intermediate representation program, within the specified graph.
	 * @param graph where this node is included
	 * @param word which describes the type of the semantic property
	 * @param location where the semantic property needs to hold at
	 * @param parameter provides more details about the semantic property
	 * @throws Exception
	 */
	protected CirSemanticNode(CirSemanticWord word, CirNode location, Object parameter) throws Exception {
		if(word == null)
			throw new IllegalArgumentException("Invalid word: null");
		else if(location == null)
			throw new IllegalArgumentException("Invalid location: null");
		else {
			this.word = word;
			this.location = location;
			this.parameter = parameter;
			this.in = new LinkedList<CirSemanticLink>();
			this.ou = new LinkedList<CirSemanticLink>();
		}
	}
	
	/* getters */
	/**
	 * get the word describing the type of the semantic property in this graph
	 * @return
	 */
	public CirSemanticWord get_word() { return this.word; }
	/**
	 * get the location in C-like intermediate representation program that the
	 * semantic property is expected to be hold.
	 * @return
	 */
	public CirNode get_location() { return this.location; }
	/**
	 * get the parameter providing the details about the semantic property hold
	 * @return
	 */
	public Object get_parameter() {return this.parameter; }
	/**
	 * get the set of links to this node which represents the possible relation
	 * that causes the property in this node to hold or occur after a group of
	 * the conditions be true. 
	 * @return
	 */
	public Iterable<CirSemanticLink> get_in_links() { return in; }
	/**
	 * get the set of links from this node which represents the possible relation
	 * that causes the other set of the nodes in semantic graph to hold or occur
	 * after the semantic property in this node is proven to be true.
	 * @return
	 */
	public Iterable<CirSemanticLink> get_ou_links() { return ou; }
	/**
	 * get the number of links to this node
	 * @return
	 */
	public int get_in_degree() { return this.in.size(); }
	/**
	 * get the number of links from this node
	 * @return
	 */
	public int get_ou_degree() { return this.ou.size(); }
	/**
	 * get the kth link to this node in which this node is an effect
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public CirSemanticLink get_in_link(int k) throws IndexOutOfBoundsException { return in.get(k); }
	/**
	 * get the kth link from this node in which this node is a cause
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public CirSemanticLink get_ou_link(int k) throws IndexOutOfBoundsException { return ou.get(k); }
	@Override
	public String toString() {
		try {
			if(this.parameter == null) {
				return this.word + "(" + location.generate_code() + ")";
			}
			else {
				return this.word + "(" + location.generate_code() + ", " + parameter + ")";
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof CirSemanticNode) {
			CirSemanticNode node = (CirSemanticNode) obj;
			if(this.parameter == null)
				return (node.word == this.word) && (node.location == location) && (node.parameter == null);
			else return (node.word == this.word) && (node.location == location) && (this.parameter.equals(node.parameter));
		}
		else return obj == this;
	}
	
}
