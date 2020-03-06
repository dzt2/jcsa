package __backup__;

import java.util.LinkedList;
import java.util.List;

/**
 * The link between semantic node in C-like intermediate representation describes the possible
 * cause-effect relationships between the semantic properties hold at each program point, which
 * can be one of the following type:<br>
 * <code>forall(x1, x2, ..., xn) ==> {y1, y2, ..., ym}</code><br>
 * where the effects <code>{yj}</code> hold when all the causes <code>{xi}</code> holds.
 * 
 * @author yukimula
 *
 */
public class CirSemanticLink {
	
	/* attributes */
	/** the set of nodes as the causes lead to the occurrence of the effects **/
	protected List<CirSemanticNode> in;
	/** the set of nodes as the effects lead by the occurrence of the causes **/
	protected List<CirSemanticNode> ou;
	
	/* constructor */
	/**
	 * create the cause-effect semantic link in the graph
	 * @param graph
	 * @param word
	 * @throws Exception
	 */
	protected CirSemanticLink() {
		this.in = new LinkedList<CirSemanticNode>();
		this.ou = new LinkedList<CirSemanticNode>();
	}
	
	/* getters */
	/**
	 * get the set of nodes as causes of this link
	 * @return
	 */
	public Iterable<CirSemanticNode> get_in_nodes() { return this.in; }
	/**
	 * get the set of nodes as effects of the link
	 * @return
	 */
	public Iterable<CirSemanticNode> get_ou_nodes() { return this.ou; }
	/**
	 * get the number of nodes as causes to this link
	 * @return
	 */
	public int get_in_degree() { return this.in.size(); }
	/**
	 * get the number of nodes as effects this link to
	 * @return
	 */
	public int get_ou_degree() { return this.ou.size(); }
	/**
	 * get the kth node to this link as the cause
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public CirSemanticNode get_in_node(int k) throws IndexOutOfBoundsException { return in.get(k); }
	/**
	 * get the kth node as the effect caused by this link
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public CirSemanticNode get_ou_node(int k) throws IndexOutOfBoundsException { return ou.get(k); }
	
}
