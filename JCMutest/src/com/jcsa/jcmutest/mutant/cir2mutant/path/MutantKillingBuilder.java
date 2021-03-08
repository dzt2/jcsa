package com.jcsa.jcmutest.mutant.cir2mutant.path;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirAnnotation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymConstraint;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.flwa.graph.CirInstanceGraph;
import com.jcsa.jcparse.flwa.graph.CirInstanceNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPathFinder;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It is used to build up the mutant-killing paths.
 * 
 * @author yukimula
 *
 */
class MutantKillingBuilder {
	
	/* singleton */
	protected CirMutations cir_mutations;
	private MutantKillingBuilder() { }
	protected static final MutantKillingBuilder builder = new MutantKillingBuilder();
	
	/* previous reaching path generator */
	/**
	 * @param dependence_graph
	 * @param target
	 * @return the set of edges in execution paths that pass through mutation where edge refers to the
	 * 		   edge immediately before the execution of faulty statement(s).
	 * @throws Exception
	 */
	private Collection<CirExecutionEdge> muta_edges(CDependGraph 
			dependence_graph, CirExecution target) throws Exception {
		Collection<CirExecutionPath> paths = new LinkedList<CirExecutionPath>();
		CirInstanceGraph instance_graph = dependence_graph.get_program_graph();
		
		/* dependence-based extensions */
		if(dependence_graph != null && instance_graph.has_instances_of(target)) {
			for(CirInstanceNode instance : instance_graph.get_instances_of(target)) {
				if(dependence_graph.has_node(instance)) {
					CirExecutionPath path = CirExecutionPathFinder.
							finder.dependence_path(dependence_graph, instance);
					paths.add(path);
				}
			}
		}
		/* simple path extensions in local */
		else {
			CirExecution source = target.get_graph().get_entry();
			CirExecutionPath dependence_path = new CirExecutionPath(source);
			CirExecutionPathFinder.finder.vf_extend(dependence_path, target);
			paths.add(dependence_path);
		}
		
		/* decidable propagation path extension */
		Collection<CirExecutionEdge> edges = new LinkedList<CirExecutionEdge>();
		for(CirExecutionPath path : paths) {
			int length = (path.is_empty()) ? 0 : path.length() - 1;
			CirExecution exits = target.get_graph().get_exit();
			CirExecutionPathFinder.finder.vf_extend(path, exits);
			edges.add(path.get_edge(length));
		}
		return edges;
	}
	/**
	 * generate the constraints for executing the testing on specified path
	 * @param path
	 * @throws Exception
	 */
	private void annotate_path_conditions(MutantKillingPath path) throws Exception {
		for(CirExecutionEdge edge : path.get_execution_edges()) {
			SymConstraint constraint; SymbolExpression condition;
			
			switch(edge.get_type()) {
			case true_flow:
			{
				CirStatement cond_statement = edge.get_source().get_statement();
				if(cond_statement instanceof CirIfStatement) {
					CirExpression if_condition = ((CirIfStatement) cond_statement).get_condition();
					condition = SymbolFactory.sym_condition(if_condition, true);
				}
				else {
					CirExpression if_condition = ((CirCaseStatement) cond_statement).get_condition();
					condition = SymbolFactory.sym_condition(if_condition, true);
				}
				constraint = cir_mutations.expression_constraint(
						edge.get_source().get_statement(), condition, true);
				break;
			}
			case fals_flow:
			{
				CirStatement cond_statement = edge.get_source().get_statement();
				if(cond_statement instanceof CirIfStatement) {
					CirExpression if_condition = ((CirIfStatement) cond_statement).get_condition();
					condition = SymbolFactory.sym_condition(if_condition, false);
				}
				else {
					CirExpression if_condition = ((CirCaseStatement) cond_statement).get_condition();
					condition = SymbolFactory.sym_condition(if_condition, false);
				}
				constraint = cir_mutations.expression_constraint(
						edge.get_source().get_statement(), condition, true);
				break;
			}
			case call_flow:
			{
				constraint = cir_mutations.expression_constraint(
						edge.get_source().get_statement(), Boolean.TRUE, true);
				break;
			}
			case retr_flow:
			{
				constraint = cir_mutations.expression_constraint(
						edge.get_source().get_statement(), Boolean.TRUE, true);
				break;
			}
			default: 
			{
				constraint = null;
				break;
			}
			}
			
			MutantKillingState state = (MutantKillingState) edge.get_annotation();
			state.add(constraint);
		}
	}
	/**
	 * generate the constraints or state errors annotated in mutation killing path
	 * @param sym_path
	 * @param muta_edge
	 * @throws Exception
	 */
	private void annotate_muta_conditions(List<SymInstanceEdge> sym_path, MutantKillingPath path) throws Exception {
		SymInstanceNode target = null, source; int k = 0;
		
		for(SymInstanceEdge sym_edge : sym_path) {
			source = sym_edge.get_source();
			
			while(k < path.length()) {
				if(source.get_execution() == path.get_execution_path().get_edge(k).get_source()) {
					MutantKillingState state = path.get_state(k);
					
					state.add(source.get_state_error());
					for(CirAnnotation annotation : source.get_status().get_cir_annotations()) {
						state.add(annotation);
					}
					
					state.add(sym_edge.get_constraint());
					for(CirAnnotation annotation : sym_edge.get_status().get_cir_annotations()) {
						state.add(annotation);
					}
					
					break;
				}
				k++;
			}
			
			target = sym_edge.get_target();
		}
		
		if(target != null) {
			while(k < path.length()) {
				if(target.get_execution() == path.get_execution_path().get_edge(k).get_source()) {
					MutantKillingState state = path.get_state(k);
					state.add(target.get_state_error());
					for(CirAnnotation annotation : target.get_status().get_cir_annotations()) {
						state.add(annotation);
					}
					break;
				}
				k++;
			}
		}
	}
	/**
	 * @param mutant
	 * @param sym_path
	 * @param dependence_graph
	 * @return set of mutation killing paths for killing the target mutant in specified pattern of symbolic path
	 * @throws Exception
	 */
	protected static Collection<MutantKillingPath> muta_paths(Mutant mutant, CirExecution location,
			List<SymInstanceEdge> sym_path, CDependGraph dependence_graph) throws Exception {
		Collection<CirExecutionEdge> muta_edges = builder.muta_edges(dependence_graph, location);
		Collection<MutantKillingPath> muta_paths = new LinkedList<MutantKillingPath>();
		
		for(CirExecutionEdge muta_edge : muta_edges) {
			MutantKillingPath muta_path = new MutantKillingPath(muta_edge.get_path(), mutant);
			muta_paths.add(muta_path);
			
			builder.annotate_path_conditions(muta_path);
			builder.annotate_muta_conditions(sym_path, muta_path);
		}
		
		return muta_paths;
	}
	
}
