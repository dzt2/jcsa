package com.jcsa.jcparse.lopt.analysis.flow;

import java.util.HashSet;
import java.util.Set;

import com.jcsa.jcparse.lang.base.BitSequence;
import com.jcsa.jcparse.lang.base.BitSet;
import com.jcsa.jcparse.lang.base.BitSetBase;
import com.jcsa.jcparse.lopt.CirInstance;
import com.jcsa.jcparse.lopt.analysis.AbsInterpreter;
import com.jcsa.jcparse.lopt.analysis.AbsOperator;
import com.jcsa.jcparse.lopt.analysis.AbsValue;
import com.jcsa.jcparse.lopt.analysis.BitSetValue;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceEdge;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceGraph;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceNode;

/**
 * Used to build up the dominance graph in C program.
 * 
 * @author yukimula
 *
 */
class CDominanceBuilder {
	
	/* abstract operator to compute the dominance set */
	/**
	 * The abstract operator used to compute dominance set based on following equation:<br>
	 * Y = X1 & X2 & X3 & ... & Xn + {Si}
	 * 
	 * @author yukimula
	 *
	 */
	protected static class CDominanceOperator implements AbsOperator {
		
		/* constructor */
		protected BitSetBase bitset_base;
		protected CDominanceOperator(CirInstanceGraph graph) throws Exception {
			if(graph == null)
				throw new IllegalArgumentException("Invalid graph: null");
			else {
				Set<Object> data_set = new HashSet<Object>();
				for(Object context : graph.get_contexts()) {
					for(CirInstance instance : graph.get_instances(context)) {
						data_set.add(instance);
						if(instance instanceof CirInstanceNode) {
							for(CirInstanceEdge edge : ((CirInstanceNode) instance).get_ou_edges()) {
								data_set.add(edge);
							}
						}
					}
				}
				this.bitset_base = BitSetBase.base(data_set);
			}
		}
		
		@Override
		public AbsValue initial_value(CirInstanceNode exec_instance) throws Exception {
			BitSet bitset = this.bitset_base.new_set();
			BitSequence bitstring = bitset.get_set();
			bitstring.set(bitstring.not());
			return new BitSetValue(bitset);
		}
		
		@Override
		public AbsValue initial_value(CirInstanceEdge flow_instance) throws Exception {
			BitSet bitset = this.bitset_base.new_set();
			BitSequence bitstring = bitset.get_set();
			bitstring.set(bitstring.not());
			return new BitSetValue(bitset);
		}
		
		@Override
		public AbsValue update_value(CirInstanceNode exec_instance, boolean forward) throws Exception {
			/* 1. declarations */
			boolean first = true;
			BitSet result = this.bitset_base.new_set();
			BitSequence result_bits = result.get_set();
			
			/* 2. determine the edges being converged */
			Iterable<CirInstanceEdge> edges;
			if(forward)
				 edges = exec_instance.get_in_edges();
			else edges = exec_instance.get_ou_edges();
			
			/* 3. Y = X1 & X2 & X3 & ... & Xn */
			for(CirInstanceEdge edge : edges) {
				BitSetValue state = (BitSetValue) edge.get_state();
				BitSequence bitstring = state.get().get_set();
				
				if(first) {
					first = false;
					result_bits.set(bitstring);
				}
				else {
					result_bits.set(result_bits.and(bitstring));
				}
			}
			
			/* 4. Z = X1 & X2 & X3 & ... & Xn + {Si} */
			result.add(exec_instance);
			// System.out.println("\t\t--> " + result.get_set().degree());
			
			/* 5. return the bit-set as new result */
			return new BitSetValue(result);
		}
		
		@Override
		public AbsValue update_value(CirInstanceEdge flow_instance, boolean forward) throws Exception {
			/* 1. get X */
			BitSetValue x;
			if(forward)
				 x = (BitSetValue) flow_instance.get_source().get_state();
			else x = (BitSetValue) flow_instance.get_target().get_state();
			BitSet x_set = x.get(); BitSequence x_string =x_set.get_set();
			
			/* 2. X + {flow} */
			BitSet result = this.bitset_base.new_set();
			result.get_set().set(x_string);
			result.add(flow_instance);
			
			return new BitSetValue(result);
		}
		
	}
	
	/* attributes */
	/** the abstract operator used to compute the abstract state hold by program element **/
	private CDominanceOperator operator;
	/** the program flow graph that needs to be analyzed to generate dominance relations **/
	private CirInstanceGraph input;
	/** the dominance graph describing the dominance relations between program elements **/
	private CDominanceGraph output;
	
	/* constructor */
	/** the singleton of the builder to build up the program flow graph **/
	protected static final CDominanceBuilder builder = new CDominanceBuilder();
	/**
	 * create a builder for building dominance graph
	 */
	private CDominanceBuilder() {
		this.operator = null;
		this.input = null;
		this.output = null;
	}
	
	/* processing methods */
	/**
	 * Set the input program and initialize the operator for abstract interpretation.
	 * @param input
	 * @throws Exception
	 */
	private void set_inputs(CirInstanceGraph input) throws Exception {
		if(input == null)
			throw new IllegalArgumentException("Invalid input: null");
		else {
			this.operator = new CDominanceOperator(input);
			this.input = input;
		}
	}
	/**
	 * Perform data flow analysis to solve the dominance set of the program elements
	 * @param forward
	 * @throws Exception
	 */
	private void analysis(boolean forward) throws Exception {
		AbsInterpreter interpreter;
		if(forward)
			interpreter = AbsInterpreter.forward_interpreter(operator);
		else interpreter = AbsInterpreter.backward_interpreter(operator);
		interpreter.interpret(input);
	}
	/**
	 * construct the dominance graph with respect to the dominance set
	 * computed during the program analysis.
	 * @param output
	 * @throws Exception
	 */
	private void get_output(CDominanceGraph output) throws Exception {
		if(output == null)
			throw new IllegalArgumentException("Invalid output as null");
		else {
			this.output = output;
			this.create_nodes();
			this.create_edges();
		}
	}
	/**
	 * Construct the dominance graph with respect to the input program under the given direction
	 * @param input
	 * @param forward true when the graph describes forward dominance relationships
	 * @param output
	 * @throws Exception
	 */
	protected void build(CirInstanceGraph input, boolean forward, CDominanceGraph output) throws Exception {
		this.set_inputs(input);
		this.analysis(forward);
		this.get_output(output);
	}
	
	/* analysis methods */
	private void create_nodes() throws Exception {
		for(Object context : this.input.get_contexts()) {
			for(CirInstance instance : input.get_instances(context)) {
				output.new_node(instance);
				if(instance instanceof CirInstanceNode) {
					for(CirInstanceEdge edge : ((CirInstanceNode) instance).get_ou_edges()) {
						output.new_node(edge);
					}
				}
			}
		}
	}
	private BitSet get_dominance_set(CirInstance instance) throws Exception {
		BitSetValue value = (BitSetValue) instance.get_state();
		return value.get();
	}
	private CirInstance get_instance_of(int k) throws Exception {
		return (CirInstance) this.operator.bitset_base.get(k);
	}
	private int get_index_of_instance(CirInstance instance) throws Exception {
		BitSetValue value = (BitSetValue) instance.get_state();
		return value.get().get_base().index_of(instance);
	}
	private void create_edge(CDominanceNode node) throws Exception {
		BitSet xset = this.get_dominance_set(node.get_instance());
		BitSequence xstring = xset.get_set();
		int index = this.get_index_of_instance(node.get_instance());
		
		for(int k = 0; k < xstring.length(); k++) {
			if(index != k && xstring.get(k)) {
				CirInstance instance = this.get_instance_of(k);
				BitSequence ystring = this.get_dominance_set(instance).get_set();
				
				xstring = xstring.and(ystring.not()); 
				xstring.set(k, BitSequence.BIT1);
			}
		}
		
		CirInstance target = node.get_instance();
		for(int k = 0; k < xstring.length(); k++) {
			if(index != k && xstring.get(k)) {
				CirInstance source = this.get_instance_of(k);
				if(source != target) {
					CDominanceNode source_node = this.output.get_node(source);
					CDominanceNode target_node = this.output.get_node(target);
					source_node.dominate(target_node);
				}
			}
		}
	}
	private void create_edges() throws Exception {
		for(CDominanceNode node : this.output.get_nodes()) {
			this.create_edge(node);
		}
	}
	
}
