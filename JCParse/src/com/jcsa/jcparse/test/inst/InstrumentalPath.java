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
	
	/* partial path */
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
			if(unit.get_type() == InstrumentalType.evaluate) {
				next_execution = this.get_execution(unit);
				if(prev_execution != next_execution) {
					if(!buffer.isEmpty()) {
						nodes.add(new InstrumentalNode(this, nodes.size(), prev_execution, buffer));
					}
					prev_execution = next_execution;
					buffer.clear();
				}
				buffer.add(unit);
			}
		}
		if(!buffer.isEmpty()) {
			nodes.add(new InstrumentalNode(this, nodes.size(), prev_execution, buffer));
		}
	}
	
	/* complete path */
	private List<CirExecution> complete_sub_path(CirExecution source, 
			CirExecution target, List<CirExecution> sub_path) throws Exception {
		sub_path.clear(); boolean first = true;
		InstrumentalNode last_node = this.nodes.get(nodes.size() - 1);
		
		while(source != target) {
			if(!first)
				sub_path.add(source);
			first = false;
			
			CirStatement statement = source.get_statement();
			if(statement instanceof CirAssignStatement
				|| statement instanceof CirGotoStatement) {
				source = source.get_ou_flow(0).get_target();
			}
			else if(statement instanceof CirCallStatement) {
				if(source.get_ou_flow(0).get_type() == CirExecutionFlowType.call_flow) {
					source = source.get_ou_flow(0).get_target();
				}
				else {
					if(target.get_graph().get_function() != source.get_graph().get_function()) {
						break;
					}
					else {
						source = source.get_ou_flow(0).get_target();
					}
				}
			}
			else if(statement instanceof CirEndStatement) {
				break;
			}
			else if(statement instanceof CirTagStatement) {
				source = source.get_ou_flow(0).get_target();
			}
			else if(statement instanceof CirCaseStatement) {
				break;
			}
			else if(statement instanceof CirIfStatement) {
				if(source == last_node.get_execution()) {
					CirExpression condition = ((CirIfStatement) statement).get_condition();
					boolean found = false, decision = false;
					for(InstrumentalUnit unit : last_node.get_units()) {
						if(unit.get_location() == condition) {
							byte[] bytes = unit.get_bytes();
							for(byte element : bytes) {
								if(element != 0) {
									decision = true;
								}
							}
							found = true;
							break;
						}
					}
					if(found) {
						if(decision) {
							for(CirExecutionFlow flow : source.get_ou_flows()) {
								if(flow.get_type() == CirExecutionFlowType.true_flow) {
									source = flow.get_target();
									break;
								}
							}
						}
						else {
							for(CirExecutionFlow flow : source.get_ou_flows()) {
								if(flow.get_type() == CirExecutionFlowType.fals_flow) {
									source = flow.get_target();
									break;
								}
							}
						}
					}
					else {
						break;
					}
				}
				else {
					break;
				}
			}
			else {
				throw new IllegalArgumentException(statement.generate_code(true));
			}
		}
		
		while(sub_path.contains(source))
			sub_path.remove(source);
		sub_path.remove(target);
		return sub_path;
	}
	private void generate_complete_path(List<InstrumentalUnit> units) throws Exception {
		/* declarations */
		CirExecution next_execution, prev_execution = null; nodes.clear();
		List<InstrumentalUnit> buffer = new ArrayList<InstrumentalUnit>();
		ArrayList<CirExecution> sub_path = new ArrayList<CirExecution>();
		for(InstrumentalUnit unit : units) {
			if(unit.get_type() == InstrumentalType.evaluate) {
				next_execution = this.get_execution(unit);
				if(prev_execution != next_execution) {
					if(!buffer.isEmpty()) {
						nodes.add(new InstrumentalNode(this, nodes.size(), prev_execution, buffer));
						/* complete the sub-path between them */
						this.complete_sub_path(prev_execution, next_execution, sub_path);
						for(CirExecution execution : sub_path) {
							nodes.add(new InstrumentalNode(this, nodes.size(), execution, null));
						}
					}
					prev_execution = next_execution;
					buffer.clear();
				}
				buffer.add(unit);
			}
		}
		if(!buffer.isEmpty()) {
			nodes.add(new InstrumentalNode(this, nodes.size(), prev_execution, buffer));
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
