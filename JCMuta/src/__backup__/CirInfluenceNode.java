package __backup__;

import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceNode;

/**
 * The node in influence graph can be a statement, expression or variable that is used or defined
 * at some point of the program execution.
 * 
 * @author yukimula
 *
 */
public class CirInfluenceNode {
	
	/* attributes */
	/** the graph where this node is created **/
	private CirInfluenceGraph graph;
	/** the instance of statement where the syntax source node that this node refers to
	 *  in the C-like intermediate representation **/
	private CirInstanceNode instance;
	/** the C-like intermediate representation syntax node that this node represents **/
	private CirNode cir_source;
	/** the edges pointing to this node from the others **/
	protected List<CirInfluenceEdge> in;
	/** the edges pointing from this node to the others **/
	protected List<CirInfluenceEdge> ou;
	
	/* constructor */
	/**
	 * create an isolated node in the influence graph with respect to the CIR source
	 * node in the specified instance of statement being executed.
	 * @param graph
	 * @param instance
	 * @param cir_source
	 * @throws Exception
	 */
	protected CirInfluenceNode(CirInfluenceGraph graph, 
			CirInstanceNode instance, CirNode cir_source) throws Exception {
		if(graph == null)
			throw new IllegalArgumentException("Invalid graph: null");
		else if(instance == null)
			throw new IllegalArgumentException("Invalid instance: null");
		else if(cir_source == null)
			throw new IllegalArgumentException("Invalid cir_source: null");
		else {
			this.graph = graph;
			this.instance = instance;
			this.cir_source = cir_source;
			this.in = new LinkedList<CirInfluenceEdge>();
			this.ou = new LinkedList<CirInfluenceEdge>();
		}
	}
	
	/* getters */
	/**
	 * get the graph where this node is created
	 * @return
	 */
	public CirInfluenceGraph get_graph() { return graph; }
	/**
	 * get the instance of the statement where the syntactic source that this node refers to
	 * is defined in.
	 * @return
	 */
	public CirInstanceNode get_instance() { return instance; }
	/**
	 * get the execution of the statement where the syntactic source that this node refers to
	 * is defined in.
	 * @return
	 */
	public CirExecution get_execution() { return instance.get_execution(); }
	/**
	 * get the statement where the syntactic source that this node refers to is defined in.
	 * @return
	 */
	public CirStatement get_statement() { return instance.get_execution().get_statement(); }
	/**
	 * get the syntactic source that this node represents
	 * @return
	 */
	public CirNode get_cir_source() { return this.cir_source; }
	/**
	 * get the edges to this node from the others, which influence on this one.
	 * @return
	 */
	public Iterable<CirInfluenceEdge> get_in_edges() { return in; }
	/**
	 * get the edges from this node to the others, which are influenced by this.
	 * @return
	 */
	public Iterable<CirInfluenceEdge> get_ou_edges() { return ou; }
	/**
	 * get the number of edges to this node from the others, which influence on this one.
	 * @return
	 */
	public int get_in_degree() { return in.size(); }
	/**
	 * get the number of edges from this node to the others, which are influenced by this.
	 * @return
	 */
	public int get_ou_degree() { return ou.size(); }
	/**
	 * get the kth edge to this node from the others, which influence on this one.
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public CirInfluenceEdge get_in_edge(int k) throws IndexOutOfBoundsException { return in.get(k); }
	/**
	 * get the kth edge from this node to the others, which are influenced by this.
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public CirInfluenceEdge get_ou_edge(int k) throws IndexOutOfBoundsException { return ou.get(k); }
	@Override
	public String toString() {
		try {
			if(cir_source instanceof CirStatement)
				return "{" + cir_source.generate_trim_code() + "}";
			else {
				return "[" + cir_source.generate_trim_code() + "] in {"
						+ this.get_statement().generate_trim_code() + "}";
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	/* setter */
	/**
	 * link this node to the other using an edge with specified type
	 * @param type
	 * @param target
	 * @return
	 */
	protected CirInfluenceEdge link_to(CirInfluenceEdgeType type, CirInfluenceNode target) {
		CirInfluenceEdge edge = new CirInfluenceEdge(type, this, target);
		this.ou.add(edge); target.in.add(edge); return edge;
	}
	
	
}
