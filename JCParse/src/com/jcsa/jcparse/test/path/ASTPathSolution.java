package com.jcsa.jcparse.test.path;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

class ASTPathSolution {
	
	protected AstExecutionNode beg;
	protected AstExecutionNode end;
	protected Map<AstNode, AstExecutionUnit> state_map;
	
	protected ASTPathSolution(AstExecutionNode node) throws IllegalArgumentException {
		if(node == null)
			throw new IllegalArgumentException("Invalid node: null");
		else {
			this.beg = node;
			this.end = node;
			this.state_map = new HashMap<AstNode, AstExecutionUnit>();
			this.add_state(node.get_unit());
		}
	}
	private void add_state(AstExecutionUnit unit) {
		switch(unit.get_type()) {
		case evaluate:
		case end_expr:
						this.state_map.put(unit.get_location(), unit);
		default:		break;
		}
	}
	
	protected void append(AstExecutionFlowType flow_type, AstExecutionNode node) throws IllegalArgumentException {
		this.end.connect(flow_type, node);
		this.add_state(node.get_unit());
		this.end = node;
	}
	protected void append(AstExecutionFlowType flow_type, ASTPathSolution solution) throws IllegalArgumentException {
		this.end.connect(flow_type, solution.beg);
		for(AstExecutionUnit unit : solution.state_map.values())
			this.add_state(unit);
		this.end = solution.end;
	}
	
	protected int match(List<InstrumentLine> lines, int index) throws Exception {
		if(index < lines.size()) {
			while(index < lines.size()) {
				InstrumentLine line = lines.get(index);
				if(line.get_location() instanceof AstExpression) {
					if(this.state_map.containsKey(line.get_location())) {
						this.state_map.get(line.get_location()).set_state(line.get_state());
						index++;
					}
					else {
						break;
					}
				}
				else {
					index++;
				}
			}
			this.state_map.clear();	/* clear states that cannot be solved */
		}
		return index;
	}
	
}
