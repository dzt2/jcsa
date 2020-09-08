package com.jcsa.jcparse.test.inst;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

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
	
}
