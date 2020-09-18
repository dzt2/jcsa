package com.jcsa.jcparse.test.inst;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;

public class InstrumentalPath {
	
	/* definitions */
	private CRunTemplate template;
	private AstTree ast_tree;
	private CirTree cir_tree;
	private List<InstrumentalNode> nodes;
	public InstrumentalPath(CRunTemplate template, 
			AstTree ast_tree, CirTree cir_tree) throws Exception { 
		if(template == null)
			throw new IllegalArgumentException("Invalid template.");
		else if(ast_tree == null)
			throw new IllegalArgumentException("Invalid ast_tree.");
		else if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree.");
		else {
			this.template = template;
			this.ast_tree = ast_tree;
			this.cir_tree = cir_tree;
			this.nodes = new ArrayList<InstrumentalNode>();
		}
	}
	
	/* getters */
	/**
	 * @return the sequence of instrumental nodes in the path
	 * @throws Exception
	 */
	public Iterable<InstrumentalNode> get_nodes() throws Exception {
		return this.nodes;
	}
	
	/* basic methods */
	private List<CirExecution> between_path = new ArrayList<CirExecution>();
	/**
	 * @param unit
	 * @return the execution node where the unit is defined
	 * @throws Exception
	 */
	private CirExecution get_execution(InstrumentalUnit unit) throws Exception {
		CirStatement statement;
		switch(unit.get_type()) {
		case evaluate:
		{
			statement = ((CirExpression) unit.get_location()).statement_of();
			break;
		}
		case beg_stmt:
		case end_stmt:
		{
			statement = (CirStatement) unit.get_location();
			break;
		}
		default: throw new IllegalArgumentException(unit.toString());
		}
		return statement.get_tree().get_localizer().get_execution(statement);
	}
	/**
	 * add a new node w.r.t. the units in executional node in tail of the path
	 * @param units
	 * @throws Exception
	 */
	private void add_node(CirExecution execution, List<InstrumentalUnit> units) throws Exception {
		/* add the node w.r.t. the execution with specified units among it to the path */
		this.nodes.add(new InstrumentalNode(this, this.nodes.size(), execution, units));
	}
	
	/* partial path */
	/**
	 * generate the partial path (incomplete with all the nodes skiped) fetched
	 * from the instrumental nodes
	 * @param instrumental_file
	 * @throws Exception
	 */
	public void set_partial_path(File instrumental_file) throws Exception {
		if(instrumental_file == null || !instrumental_file.exists())
			throw new IllegalArgumentException("Undefined: " + instrumental_file);
		else {
			List<InstrumentalLine> lines = InstrumentalLine.read(instrumental_file, ast_tree, template);
			this.generate_partial_path(InstrumentalUnitParser.parse(template, cir_tree, lines));
		}
	}
	private void generate_partial_path(List<InstrumentalUnit> units) throws Exception {
		/* declarations */
		CirExecution next_execution, prev_execution = null; nodes.clear();
		List<InstrumentalUnit> buffer = new ArrayList<InstrumentalUnit>();
		for(InstrumentalUnit unit : units) {
			next_execution = this.get_execution(unit);
			if(prev_execution != next_execution) {
				if(!buffer.isEmpty()) {
					this.add_node(prev_execution, buffer);
				}
				prev_execution = next_execution;
				buffer.clear();
			}
			buffer.add(unit);
		}
		if(!buffer.isEmpty()) {
			this.add_node(prev_execution, buffer);
		}
	}
	
	/* complete path */
	/**
	 * @param source
	 * @param target
	 * @return generate the path from source to target based on current instrumental path
	 * @throws Exception
	 */
	private List<CirExecution> find_path_between(CirExecution source, CirExecution target) throws Exception {
		this.between_path.clear();
		
		CirExecution current = source;
		while(current != target) {
			if(current != source && current != target) 
				this.between_path.add(current);
			CirStatement statement = current.get_statement();
			if(statement instanceof CirEndStatement) {
				current = target;	/* undecidable in non-state analysis ==> may cause incomplete path */
			}
			else if(statement instanceof CirAssignStatement
				|| statement instanceof CirGotoStatement
				|| statement instanceof CirTagStatement) {
				if(current.get_ou_degree() == 0) {
					current = target;
				}
				else {
					current = current.get_ou_flow(0).get_target();
				}
			}
			else if(statement instanceof CirCallStatement) {
				CirExecutionFlow flow = current.get_ou_flow(0);
				if(flow.get_type() == CirExecutionFlowType.call_flow) {
					current = flow.get_target();
				}
				else if(current.get_graph().get_function() == target.get_graph().get_function()) {
					current = flow.get_target();
				}
				else {
					current = target;	/* undecidable for side-effect made by function pointers */
				}
			}
			else if(statement instanceof CirCaseStatement
					|| statement instanceof CirIfStatement) {
				current = target;	/* undecidable in non-state analysis */
			}
			else {
				throw new IllegalArgumentException(statement.generate_code(true));
			}
		}
		
		return this.between_path;
	}
	/**
	 * generate the complete execution path from the sequence of instrumental units
	 * @param units
	 * @throws Exception
	 */
	private void generate_complete_path(List<InstrumentalUnit> units) throws Exception {
		/* declarations */
		CirExecution next_execution, prev_execution = null; nodes.clear();
		List<InstrumentalUnit> buffer = new ArrayList<InstrumentalUnit>();
		for(InstrumentalUnit unit : units) {
			next_execution = this.get_execution(unit);
			if(prev_execution != next_execution) {
				if(!buffer.isEmpty()) {
					this.add_node(prev_execution, buffer);
					/* complete the path from prev-execution to the next-execution */
					List<CirExecution> path = find_path_between(prev_execution, next_execution);
					for(CirExecution execution : path) {
						this.add_node(execution, null);
					}
				}
				prev_execution = next_execution;
				buffer.clear();
			}
			buffer.add(unit);
		}
		if(!buffer.isEmpty()) {
			this.add_node(prev_execution, buffer);
		}
	}
	/**
	 * generate the partial path (incomplete with all the nodes skiped) fetched
	 * from the instrumental nodes
	 * @param instrumental_file
	 * @throws Exception
	 */
	public void set_complete_path(File instrumental_file) throws Exception {
		if(instrumental_file == null || !instrumental_file.exists())
			throw new IllegalArgumentException("Undefined: " + instrumental_file);
		else {
			List<InstrumentalLine> lines = InstrumentalLine.read(instrumental_file, ast_tree, template);
			this.generate_complete_path(InstrumentalUnitParser.parse(template, cir_tree, lines));
		}
	}
	
}
