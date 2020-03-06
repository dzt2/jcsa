package com.jcsa.jcmuta.mutant.sem2mutation.error;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertions;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutationParser;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lopt.analysis.flow.CInfluenceEdge;
import com.jcsa.jcparse.lopt.analysis.flow.CInfluenceGraph;
import com.jcsa.jcparse.lopt.analysis.flow.CInfluenceNode;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceGraph;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceNode;

/**
 * Used to extend the constraint set.
 * @author yukimula
 *
 */
public class ConstraintExtension {
	
	/* constructor and singleton */
	private CirTree cir_tree;
	private CirInstanceGraph instance_graph;
	private CInfluenceGraph influence_graph;
	private Set<SemanticAssertion> extension_set;
	private static final ConstraintExtension 
		extension = new ConstraintExtension();
	private ConstraintExtension() { 
		this.extension_set = new HashSet<SemanticAssertion>();
	}
	
	/* extension methods */
	private void extend(SemanticAssertion assertion) throws Exception {
		if(!this.extension_set.contains(assertion)) {
			this.extension_set.add(assertion);
			switch(assertion.get_constraint_function()) {
			case cover:		this.extend_cover(assertion); break;
			case cover_for:	this.extend_cover_for(assertion); break;
			default: this.extend_other(assertion); break;	/* not support the extension */
			}
		}
	}
	private ConstraintSet extend(Iterable<SemanticAssertion> assertions) throws Exception {
		this.extension_set.clear();
		for(SemanticAssertion assertion : assertions) {
			if(assertion.is_constraint()) {
				this.extend(assertion);
			}
		}
		return new ConstraintSet(this.extension_set);
	}
	/**
	 * generate the set of assertions with the test constraint
	 * @param cir_tree
	 * @param graph
	 * @param assertions
	 * @return
	 * @throws Exception
	 */
	public static ConstraintSet get_constraint(CirTree cir_tree, CirInstanceGraph instance_graph, 
			CInfluenceGraph influence_graph, Iterable<SemanticAssertion> assertions) throws Exception {
		if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree as null");
		else if(influence_graph == null)
			throw new IllegalArgumentException("Invalid influence graph");
		else if(instance_graph == null)
			throw new IllegalArgumentException("Invalid influence graph");
		else if(assertions == null)
			throw new IllegalArgumentException("Invalid assertions: null");
		else {
			extension.cir_tree = cir_tree;
			extension.instance_graph = instance_graph;
			extension.influence_graph = influence_graph;
			return extension.extend(assertions);
		}
	}
	public static ConstraintSet get_constraint(CirTree cir_tree, CirInstanceGraph instance_graph, 
			CInfluenceGraph influence_graph, SemanticAssertion assertion) throws Exception {
		if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree as null");
		else if(influence_graph == null)
			throw new IllegalArgumentException("Invalid influence graph");
		else if(instance_graph == null)
			throw new IllegalArgumentException("Invalid influence graph");
		else if(assertion == null)
			throw new IllegalArgumentException("Invalid assertions: null");
		else {
			extension.cir_tree = cir_tree;
			extension.instance_graph = instance_graph;
			extension.influence_graph = influence_graph;
			List<SemanticAssertion> assertions = new ArrayList<SemanticAssertion>();
			assertions.add(assertion);
			return extension.extend(assertions);
		}
	}
	
	/* implementation methods */
	private SemanticAssertion get_predicate(SemanticAssertions assertions, 
			CirExpression expression, boolean value) throws Exception {
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		if(CTypeAnalyzer.is_boolean(data_type)) {
			return assertions.equal_with(expression, Boolean.valueOf(value));
		}
		else if(CTypeAnalyzer.is_number(data_type)) {
			if(value)
				return assertions.not_equals(expression, 0);
			else return assertions.equal_with(expression, 0);
		}
		else if(CTypeAnalyzer.is_pointer(data_type)) {
			if(value)
				return assertions.not_equals(expression, SemanticMutationParser.Nullptr);
			else return assertions.equal_with(expression, SemanticMutationParser.Nullptr);
		}
		else {
			throw new IllegalArgumentException("Invalid data type: " + data_type);
		}
	}
	private void extend_cover(SemanticAssertion assertion) throws Exception {
		CirStatement statement = (CirStatement) assertion.get_operand(0);
		CirExecution execution = cir_tree.get_function_call_graph().
				get_function(statement).get_flow_graph().get_execution(statement);
		
		for(Object context : this.instance_graph.get_contexts()) {
			if(this.instance_graph.has_instance(context, execution)) {
				CirInstanceNode instance = this.instance_graph.get_instance(context, execution);
				if(this.influence_graph.has_node(instance, statement)) {
					CInfluenceNode target = this.influence_graph.get_node(instance, statement);
					for(CInfluenceEdge edge : target.get_in_edges()) {
						CInfluenceNode source = edge.get_source();
						CirExpression condition; boolean value;
						
						switch(edge.get_type()) {
						case execute_when_true:
							condition = (CirExpression) source.get_cir_source();
							value = true; break;
						case execute_when_false:
							condition = (CirExpression) source.get_cir_source();
							value = false; break;
						default: 
							condition = null; value = false;
							break;
						}
						
						if(condition != null) {
							this.extend(this.get_predicate(
									assertion.get_assertions(), condition, value));
						}
					}
				}
			}
		}
	}
	private void extend_cover_for(SemanticAssertion assertion) throws Exception {
		CirExpression condition = (CirExpression) assertion.get_operand(0);
		SemanticAssertion predicate = this.
				get_predicate(assertion.get_assertions(), condition, true);
		this.extend(predicate);
	}
	private void extend_other(SemanticAssertion assertion) throws Exception {
		CirNode location = assertion.get_location();
		
		CirStatement statement;
		if(location instanceof CirStatement) {
			statement = (CirStatement) location;
		}
		else if(location instanceof CirExpression) {
			statement = ((CirExpression) location).statement_of();
		}
		else {
			statement = null;
		}
		
		if(statement != null) {
			this.extend(assertion.get_assertions().cover(statement));
		}
	}
	
}
