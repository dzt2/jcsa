package __backup__;

import java.util.LinkedList;
import java.util.Queue;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceGraph;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceNode;
import com.jcsa.jcparse.lopt.models.relation.CRelationEdge;
import com.jcsa.jcparse.lopt.models.relation.CRelationGraph;
import com.jcsa.jcparse.lopt.models.relation.CRelationNode;

/**
 * Used to generate the influence graph of C-like intermediate representation.
 * 
 * @author yukimula
 *
 */
public class CirInfluenceBuilder {
	
	/* constructor */
	/** the program flow graph used as input to build up influence **/
	private CirInstanceGraph input;
	/** the relational graph used to construct the program influence **/
	private CRelationGraph relations;
	/** the influence graph to be constructed from the flow graph **/
	private CirInfluenceGraph output;
	/** singleton constructor **/
	private CirInfluenceBuilder() { }
	/** singleton **/
	private static final CirInfluenceBuilder builder = new CirInfluenceBuilder();
	
	/* building methods */
	/**
	 * open the builder by setting its input and output
	 * @param input
	 * @param output
	 * @throws Exception
	 */
	private void open(CirInstanceGraph input, CirInfluenceGraph output) throws Exception {
		if(input == null)
			throw new IllegalArgumentException("Invalid input: null");
		else if(output == null)
			throw new IllegalArgumentException("Invalid output: null");
		else { 
			this.input = input; this.output = output; 
		}
	}
	/**
	 * build up the influence graph from C-like intermediate representation
	 * @throws Exception
	 */
	private void build() throws Exception {
		this.relations = CRelationGraph.graph(input);
		this.create_nodes(); this.create_edges();
	}
	/**
	 * close the builder by removing its input and output
	 */
	private void close() { this.input = null; this.output = null; this.relations = null; }
	/**
	 * build up the influence graph based on the program in C-like intermediate representation
	 * @param input
	 * @param output
	 * @throws Exception
	 */
	protected static void build(CirInstanceGraph input, CirInfluenceGraph output) throws Exception {
		builder.open(input, output);
		builder.build();
		builder.close();
	}
	
	/* parsing methods */
	private Queue<CirNode> cir_queue = new LinkedList<CirNode>();
	/**
	 * create all the nodes for the program elements in input code.
	 * @throws Exception
	 */
	private void create_nodes() throws Exception {
		for(CirInstanceNode instance : this.relations.get_instances()) {
			CirStatement statement = instance.get_execution().get_statement();
			cir_queue.add(statement);
			
			while(!cir_queue.isEmpty()) {
				CirNode cir_source = cir_queue.poll();
				
				if(cir_source instanceof CirStatement
					|| cir_source instanceof CirExpression) {
					this.output.new_node(instance, cir_source);
				}
				
				for(CirNode child : cir_source.get_children()) {
					cir_queue.add(child);
				}
			}
		}
	}
	/**
	 * translate the relation to the respect influence between nodes
	 * @param relation_edge
	 * @throws Exception
	 */
	private void create_edges(CRelationEdge relation_edge) throws Exception {
		CRelationNode source_node = relation_edge.get_source();
		CRelationNode target_node = relation_edge.get_target();
		CirInfluenceNode source = this.output.get_node(source_node.get_instance(), source_node.get_cir_source());
		CirInfluenceNode target = this.output.get_node(target_node.get_instance(), target_node.get_cir_source());
		
		switch(relation_edge.get_type()) {
		/** condition[stmt-->expr] |--> exec_c **/
		case condition: 
			this.output.connect(CirInfluenceEdgeType.exec_c, source, target); break;
		
		/** lvalue[stmt-->refer] |--> none **/
		case left_value: break;
		
		/** rvalue[stmt-->expr] |--> exec_v **/
		case right_value: 
			this.output.connect(CirInfluenceEdgeType.exec_e, source, target); break;
		
		/**
		 * 1. call_stmt --> function |--> exec_a
		 * 2. wait_expr <-- function |--> genv_f
		 * **/
		case function:
		{
			CirNode cir_source = source.get_cir_source();
			if(cir_source instanceof CirCallStatement) {
				this.output.connect(CirInfluenceEdgeType.exec_a, source, target);
			}
			else if(cir_source instanceof CirWaitExpression) {
				this.output.connect(CirInfluenceEdgeType.gen_fw, target, source);
			}
			else throw new IllegalArgumentException(cir_source.getClass().getSimpleName());
		}
		break;
		
		/**
		 * 1. call_stmt.function --> call_stmt.argument |--> exec_a[call_stmt.fun, call_stmt.arg]
		 * 2. wait_expr.function <-- call_stmt.argument |--> genv_a 
		 * **/
		case argument:
		{
			CirNode cir_source = source.get_cir_source();
			if(cir_source.get_parent() instanceof CirCallStatement) {
				this.output.connect(CirInfluenceEdgeType.exec_p, source, target);
			}
			else if(cir_source.get_parent() instanceof CirWaitExpression) { 
				this.output.connect(CirInfluenceEdgeType.gen_af, target, source);
			}
			else throw new IllegalArgumentException(cir_source.get_parent().generate_code());
		}
		break;
		
		/** refer_include[refr<--expr] |--> genv_r **/
		case refer_include: 
		{
			CirNode child = target.get_cir_source();
			CirNode parent = source.get_cir_source();
			while(child != parent) {
				CirInfluenceNode x = this.output.get_node(target.get_instance(), child);
				CirInfluenceNode y = this.output.get_node(target.get_instance(), child.get_parent());
				this.output.connect(CirInfluenceEdgeType.gen_cp, x, y);
				child = child.get_parent();
			}
		}
		break;
		
		/** value_include[expr<--refr] |--> genv_v **/
		case value_include:
		{
			CirNode child = target.get_cir_source();
			CirNode parent = source.get_cir_source();
			while(child != parent) {
				CirInfluenceNode x = this.output.get_node(target.get_instance(), child);
				CirInfluenceNode y = this.output.get_node(target.get_instance(), child.get_parent());
				this.output.connect(CirInfluenceEdgeType.gen_cp, x, y);
				child = child.get_parent();
			}
		}
		break;
		
		/** pass_point --> ignored **/
		case pass_point: /*this.output.connect(CInfluenceEdgeType.exec_p, source, target);*/ break;
		case wait_point: /*this.output.connect(CInfluenceEdgeType.exec_w, source, target);*/ break;
		case retr_point: /*this.output.connect(CInfluenceEdgeType.exec_r, source, target);*/ break;
		
		case define_use: this.output.connect(CirInfluenceEdgeType.pas_du, source, target); break;
		case use_define: this.output.connect(CirInfluenceEdgeType.pas_ud, source, target); break;
		case pass_in:	 this.output.connect(CirInfluenceEdgeType.pas_ap, source, target); break;
		case pass_ou:	 this.output.connect(CirInfluenceEdgeType.pas_rw, source, target); break;
		
		case transit_true:	this.output.connect(CirInfluenceEdgeType.exec_t, source, target); break;
		case transit_false:	this.output.connect(CirInfluenceEdgeType.exec_f, source, target); break;
		
		/** invalid case **/
		default: throw new IllegalArgumentException("Unable to translate: " + relation_edge.get_type());
		}
	}
	/**
	 * translate the relations in relational graph to the influence in influence graph
	 * @throws Exception
	 */
	private void create_edges() throws Exception {
		for(CirInstanceNode instance : this.relations.get_instances()) {
			for(CRelationNode relation_node : this.relations.get_nodes(instance)) {
				for(CRelationEdge relation_edge : relation_node.get_ou_edges()) {
					this.create_edges(relation_edge);
				}
			}
		}
	}
	
	
}
