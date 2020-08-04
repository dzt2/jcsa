package com.jcsa.jcparse.test.path;

import java.io.File;
import java.util.List;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArrayExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCastExpression;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;

public class AstExecutionPathFinder {
	
	/* constructor and attributes */
	private AstTree ast_tree;
	private AstExecutionPath path;
	private int line_cursor;
	private List<InstrumentLine> lines;
	protected AstExecutionPathFinder(
					AstTree ast_tree, File file) throws Exception {
		if(ast_tree == null)
			throw new IllegalArgumentException("Invalid ast_tree");
		else if(file == null || !file.exists())
			throw new IllegalArgumentException("Invalid file: null");
		else {
			this.ast_tree = ast_tree;
			this.lines = InstrumentLine.parse(ast_tree, file);
			this.line_cursor = 0;
			this.path = new AstExecutionPath();
		}
	}
	
	/* building methods */
	private AstExecutionNode[] traverse(AstNode node) throws Exception {
		if(node == null)
			throw new IllegalArgumentException("Invalid node: null");
		/* TODO implement more syntax-directed traversal algorithm */
		else
			throw new IllegalArgumentException("Unsupport: " + node);
	}
	private AstExecutionNode[] traverse_id_expression(AstIdExpression node) throws Exception {
		AstExecutionNode enode = this.
				path.new_node(AstExecutionUnit.evaluate(node));
		return new AstExecutionNode[] { enode, enode };
	}
	private AstExecutionNode[] traverse_constant(AstConstant node) throws Exception {
		AstExecutionNode enode = this.
				path.new_node(AstExecutionUnit.evaluate(node));
		return new AstExecutionNode[] { enode, enode };
	}
	private AstExecutionNode[] traverse_string_literal(AstLiteral node) throws Exception {
		AstExecutionNode enode = this.
				path.new_node(AstExecutionUnit.evaluate(node));
		return new AstExecutionNode[] { enode, enode };
	}
	private AstExecutionNode[] traverse_array_expression(AstArrayExpression node) throws Exception {
		AstExecutionNode beg = this.path.new_node(AstExecutionUnit.beg_expr(node));
		AstExecutionNode[] array_range = this.traverse(node.get_array_expression());
		AstExecutionNode[] index_range = this.traverse(node.get_dimension_expression());
		AstExecutionNode end = this.path.new_node(AstExecutionUnit.end_expr(node));
		beg.connect(AstExecutionFlowType.down_flow, array_range[0]);
		array_range[1].connect(AstExecutionFlowType.move_flow, index_range[0]);
		index_range[1].connect(AstExecutionFlowType.upon_flow, end);
		return new AstExecutionNode[] { beg, end };
	}
	private AstExecutionNode[] traverse_cast_expression(AstCastExpression node) throws Exception {
		AstExecutionNode beg = this.path.new_node(AstExecutionUnit.beg_expr(node));
		AstExecutionNode[] type_range = this.traverse(node.get_typename());
		AstExecutionNode[] expr_range = this.traverse(node.get_expression());
		AstExecutionNode end = this.path.new_node(AstExecutionUnit.end_expr(node));
		
		beg.connect(AstExecutionFlowType.down_flow, type_range[0]);
		type_range[1].connect(AstExecutionFlowType.move_flow, expr_range[0]);
		expr_range[1].connect(AstExecutionFlowType, target)
	}
	
	
	
}
