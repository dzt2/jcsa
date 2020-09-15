package com.jcsa.jcmutest.mutant.sec2mutant.util.apis;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectPair;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfection;
import com.jcsa.jcmutest.mutant.sec2mutant.util.SecStateEdgeType;
import com.jcsa.jcmutest.mutant.sec2mutant.util.SecStateGraph;
import com.jcsa.jcmutest.mutant.sec2mutant.util.SecStateNode;

/**
 * It is used to initialize the state-graph using the sec-infection module,
 * including the reaching statement as well as the initial state errors.
 * 
 * @author yukimula
 *
 */
public class SecInfectionBuild {
	
	/** singleton of the builder **/
	private static final SecInfectionBuild builder = new SecInfectionBuild(); 
	
	/** private constructor **/
	private SecInfectionBuild() { }
	
	/**
	 * build up the infection structure from reaching point to the initial
	 * state errors linked with the constraints as required for infection.
	 * @param graph
	 * @throws Exception
	 */
	private void build_infection(SecStateGraph graph) throws Exception {
		try {
			SecInfection infection = SecInfection.parse(
					graph.get_cir_tree(), graph.get_mutant());
			if(infection.has_statement()) {
				SecStateNode reach_node = graph.new_node(SecFactory.
						execution_constraint(infection.get_statement(), 1));
				for(SecInfectPair pair : infection.get_infection_pairs()) {
					reach_node.link_to(SecStateEdgeType.infect, 
							graph.new_node(pair.get_init_error()), 
							pair.get_constraint());
				}
			}
		}
		catch(UnsupportedOperationException ex) {
			return;
		}
	}
	
	/**
	 * build up the infection structure from reaching point to the initial
	 * state errors linked with the constraints as required for infection.
	 * @param graph
	 * @throws Exception
	 */
	public static void build(SecStateGraph graph) throws Exception {
		builder.build_infection(graph);
	}
	
}
