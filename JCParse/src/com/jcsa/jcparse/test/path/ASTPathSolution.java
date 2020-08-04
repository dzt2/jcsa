package com.jcsa.jcparse.test.path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

/**
 * It defines the data needed to describe the solution of path building.
 * @author yukimula
 *
 */
class ASTPathSolution {
	
	/* constructor and attributes */
	/** the first node in the range of traversal **/
	private AstExecutionNode beg;
	/** the final node in the range of traversal **/
	private AstExecutionNode end;
	/** all the nodes that occur during traverse **/
	private List<AstExecutionNode> path;
	/**
	 * initialize the solution w.r.t a given node as head
	 * @param beg
	 * @throws IllegalArgumentException
	 */
	protected ASTPathSolution(AstExecutionNode beg) throws IllegalArgumentException {
		if(beg == null)
			throw new IllegalArgumentException("Invalid beg: null");
		else {
			this.beg = beg;
			this.end = beg;
			this.path = new ArrayList<AstExecutionNode>();
		}
	}
	
	/* getters */
	public AstExecutionNode get_beg() { return this.beg; }
	public AstExecutionNode get_end() { return this.end; }
	public List<AstExecutionNode> get_path() { return this.path; }
	
	/* setter */
	protected void append(AstExecutionFlowType flow_type, 
			AstExecutionNode node) throws IllegalArgumentException {
		if(node == null)
			throw new IllegalArgumentException("Invalid node: null");
		else {
			this.end.connect(flow_type, node);
			this.end = node;
			this.path.add(node);
		}
	}
	protected void append(AstExecutionFlowType flow_type, 
			ASTPathSolution solution) throws IllegalArgumentException {
		if(solution == null)
			throw new IllegalArgumentException("Invalid solution: null");
		else {
			this.end.connect(flow_type, solution.beg);
			this.end = solution.end;
			for(AstExecutionNode node : solution.path) {
				this.path.add(node);
			}
		}
	}
	protected int match_solution(List<InstrumentLine> lines, int index) throws Exception {
		if(lines == null)
			throw new IllegalArgumentException("Invalid lines: null");
		else {
			if(index < lines.size()) {
				Map<AstNode, AstExecutionUnit> solutions = new HashMap<AstNode, AstExecutionUnit>();
				for(AstExecutionNode node : this.path) {
					switch(node.get_unit().get_type()) {
					case end_expr:
					case evaluate:
									solutions.put(node.get_unit().get_location(), node.get_unit());
					default: 		break;
					}
				}
				while(index < lines.size()) {
					InstrumentLine line = lines.get(index);
					if(line.get_location() instanceof AstExpression) {
						if(solutions.containsKey(line.get_location())) {
							solutions.get(line.get_location()).set_state(line.get_state());
							index++;
						}
						else {
							break;
						}
					}
					else index++;
				}
			}
			return index;
		}
	}
	
}
