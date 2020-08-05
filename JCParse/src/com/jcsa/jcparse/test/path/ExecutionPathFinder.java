package com.jcsa.jcparse.test.path;

import java.util.List;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArrayExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCastExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCommaExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConditionalExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class ExecutionPathFinder {
	
	private AstExecutionPath path;
	private int cursor;
	private List<InstrumentLine> lines;
	private ExecutionPathFinder(AstExecutionPath path) {
		this.path = path;
	}
	
	/* traversal algorithms */
	private ASTPathSolution traverse(AstNode node) throws Exception {
		if(node == null)
			throw new IllegalArgumentException("Invalid node: null");
		// TODO implement the traversal algorithm methods
		else
			throw new IllegalArgumentException("Unsupport: " + node);
	}
	private ASTPathSolution traverse_id_expression(AstIdExpression node) throws Exception {
		return new ASTPathSolution(this.path.new_node(AstExecutionUnit.evaluate(node)));
	}
	private ASTPathSolution traverse_constant(AstConstant node) throws Exception {
		return new ASTPathSolution(this.path.new_node(AstExecutionUnit.evaluate(node)));
	}
	private ASTPathSolution traverse_literal(AstLiteral node) throws Exception {
		return new ASTPathSolution(this.path.new_node(AstExecutionUnit.evaluate(node)));
	}
	private ASTPathSolution traverse_array_expression(AstArrayExpression node) throws Exception {
		ASTPathSolution solution = new ASTPathSolution(this.path.new_node(AstExecutionUnit.beg_expr(node)));
		solution.append(AstExecutionFlowType.down_flow, this.traverse(node.get_array_expression()));
		solution.append(AstExecutionFlowType.move_flow, this.traverse(node.get_dimension_expression()));
		solution.append(AstExecutionFlowType.upon_flow, this.path.new_node(AstExecutionUnit.end_expr(node)));
		return solution;
	}
	private ASTPathSolution traverse_cast_expression(AstCastExpression node) throws Exception {
		ASTPathSolution solution = new ASTPathSolution(this.path.new_node(AstExecutionUnit.beg_expr(node)));
		solution.append(AstExecutionFlowType.down_flow, this.traverse(node.get_typename()));
		solution.append(AstExecutionFlowType.move_flow, this.traverse(node.get_expression()));
		solution.append(AstExecutionFlowType.upon_flow, this.path.new_node(AstExecutionUnit.end_expr(node)));
		return solution;
	}
	private ASTPathSolution traverse_comma_expression(AstCommaExpression node) throws Exception {
		ASTPathSolution solution = new ASTPathSolution(this.path.new_node(AstExecutionUnit.beg_expr(node)));
		solution.append(AstExecutionFlowType.down_flow, this.traverse(node.get_expression(0)));
		for(int k = 1; k < node.number_of_arguments(); k++) {
			solution.append(AstExecutionFlowType.move_flow, this.traverse(node.get_expression(k)));
		}
		solution.append(AstExecutionFlowType.upon_flow, this.path.new_node(AstExecutionUnit.end_expr(node)));
		return solution;
	}
	private ASTPathSolution traverse_conditional_expression(AstConditionalExpression node) throws Exception {
		ASTPathSolution solution = new ASTPathSolution(
				this.path.new_node(AstExecutionUnit.beg_expr(node)));
		
		/* 1. traverse the condition part */
		ASTPathSolution csolution = this.traverse(node.get_condition());
		AstExecutionUnit condition_unit = csolution.state_map.get(
				CTypeAnalyzer.get_expression_of(node.get_condition()));
		solution.append(AstExecutionFlowType.down_flow, csolution);
		this.cursor = solution.match(this.lines, this.cursor);
		
		/* 2. traverse the true and false parts */
		ASTPathSolution tsolution = this.traverse(node.get_true_branch());
		ASTPathSolution fsolution = this.traverse(node.get_false_branch());
		
	}
	
	
	
}
