package com.jcsa.jcparse.lang.irlang.impl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstBasicExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArrayExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCastExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCommaExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConditionalExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFunCallExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstParanthExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstSizeofExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstBreakStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstContinueStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDefaultStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstGotoStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatementList;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.irlang.AstCirPair;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirBinAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirDefaultStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabelStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirReturnAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirSaveAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * It provides the interface to determine the locations in cir-code
 * w.r.t. a given AstNode.
 * 
 * @author yukimula
 *
 */
public class CirLocalizer {
	
	/* definition */
	private CirTree cir_tree;
	protected CirLocalizer(CirTree cir_tree) {
		this.cir_tree = cir_tree;
	}
	
	/* localization methods */
	/**
	 * @param parent
	 * @param child
	 * @return the index of the child in the parent
	 * @throws Exception
	 */
	public int find_child_index(AstNode parent, AstNode child) throws Exception {
		for(int k = 0; k < parent.number_of_children(); k++) {
			if(parent.get_child(k) == child) {
				return k;
			}
		}
		throw new IllegalArgumentException("Not a child of parent");
	}
	/**
	 * @param location
	 * @return the instance of cir-code range w.r.t. the location in form of AST.
	 */
	public AstCirPair get_cir_range(AstNode location) {
		if(this.cir_tree.has_cir_range(location)) {
			while(true) {
				try {
					return this.cir_tree.get_cir_range(location);
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		else {
			return null;
		}
	}
	/**
	 * @param location
	 * @param cir_class
	 * @return the list of cir-nodes w.r.t. the location in form of AST or empty
	 */
	public List<CirNode> get_cir_nodes(AstNode location, Class<?> cir_class) {
		List<CirNode> cir_nodes = this.cir_tree.get_cir_nodes(location);
		LinkedList<CirNode> cir_filter_nodes = new LinkedList<CirNode>();
		for(CirNode cir_node : cir_nodes) {
			if(cir_class.isInstance(cir_node)) {
				cir_filter_nodes.add(cir_node);
			}
		}
		return cir_filter_nodes;
	}
	/**
	 * @param location
	 * @return the cir-expression that represents the location or null if it is not used.
	 * @throws Exception
	 */
	public CirExpression get_cir_value(AstNode location) throws Exception {
		if(location == null)
			throw new IllegalArgumentException("Invalid location: null");
		else if(location instanceof AstExpression) {
			AstCirPair range = this.get_cir_range(location);
			if(range == null || !range.computational()) {
				return null;
			}
			else if(location instanceof AstBasicExpression
					|| location instanceof AstArrayExpression
					|| location instanceof AstCastExpression
					|| location instanceof AstFieldExpression
					|| location instanceof AstSizeofExpression
					|| location instanceof AstCommaExpression
					|| location instanceof AstInitializerBody
					|| location instanceof AstConditionalExpression) {
				return range.get_result();
			}
			else if(location instanceof AstConstExpression) {
				return this.get_cir_value(((AstConstExpression) 
									location).get_expression());
			}
			else if(location instanceof AstParanthExpression) {
				return this.get_cir_value(((AstParanthExpression) 
								location).get_sub_expression());
			}
			else if(location instanceof AstFunCallExpression) {
				CirAssignStatement statement = (CirAssignStatement) this.
						get_cir_nodes(location, CirWaitAssignStatement.class).get(0);
				return statement.get_rvalue();
			}
			else if(location instanceof AstUnaryExpression) {
				if(location instanceof AstIncreUnaryExpression) {
					CirAssignStatement statement = (CirAssignStatement) this.
							get_cir_nodes(location, CirIncreAssignStatement.class).get(0);
					return statement.get_rvalue();
				}
				else {
					return range.get_result();
				}
			}
			else if(location instanceof AstPostfixExpression) {
				CirAssignStatement statement = (CirAssignStatement) this.
						get_cir_nodes(location, CirSaveAssignStatement.class).get(0);
				return statement.get_rvalue();
			}
			else if(location instanceof AstBinaryExpression) {
				COperator operator = ((AstBinaryExpression) location).get_operator().get_operator();
				switch(operator) {
				case assign:
				case arith_add_assign:
				case arith_sub_assign:
				case arith_mul_assign:
				case arith_div_assign:
				case arith_mod_assign:
				case bit_and_assign:
				case bit_or_assign:
				case bit_xor_assign:
				case left_shift_assign:
				case righ_shift_assign:
				{
					CirAssignStatement statement = (CirAssignStatement) this.
							get_cir_nodes(location, CirBinAssignStatement.class).get(0);
					return statement.get_rvalue();
				}
				default:
				{
					return range.get_result();
				}
				}
			}
			else {
				return range.get_result();
			}
		}
		else
			throw new IllegalArgumentException("Unsupport: " + location);
	}
	/**
	 * @param location
	 * @return the location is started iff. the statement in cir-code is executed.
	 * @throws Exception
	 */
	private CirStatement get_beg_statement(AstNode location) throws Exception {
		AstCirPair range = this.get_cir_range(location);
		if(range == null) {
			return null;
		}
		else if(location instanceof AstExpression) {
			if(location instanceof AstBasicExpression
				|| location instanceof AstArrayExpression
				|| location instanceof AstCastExpression
				|| location instanceof AstCommaExpression
				|| location instanceof AstFieldExpression
				|| location instanceof AstInitializerBody
				|| location instanceof AstSizeofExpression) {
				return range.get_end_statement();
			}
			else if(location instanceof AstConstExpression) {
				return this.get_beg_statement(
						((AstConstExpression) location).get_expression());
			}
			else if(location instanceof AstParanthExpression) {
				return this.get_beg_statement(
						((AstParanthExpression) location).get_sub_expression());
			}
			else if(location instanceof AstConditionalExpression) {
				return (CirStatement) this.get_cir_nodes(
						location, CirIfStatement.class).get(0);
			}
			else if(location instanceof AstFunCallExpression) {
				return (CirStatement) this.get_cir_nodes(
						location, CirCallStatement.class).get(0);
			}
			else if(location instanceof AstUnaryExpression) {
				if(location instanceof AstIncreUnaryExpression) {
					return (CirStatement) this.get_cir_nodes(
							location, CirIncreAssignStatement.class).get(0);
				}
				else {
					return range.get_end_statement();
				}
			}
			else if(location instanceof AstPostfixExpression) {
				return (CirStatement) this.get_cir_nodes(
						location, CirSaveAssignStatement.class).get(0);
			}
			else if(location instanceof AstBinaryExpression) {
				if(location instanceof AstLogicBinaryExpression) {
					return (CirStatement) this.get_cir_nodes(
							location, CirSaveAssignStatement.class).get(0);
				}
				else if(location instanceof AstAssignExpression
						|| location instanceof AstArithAssignExpression
						|| location instanceof AstBitwiseAssignExpression
						|| location instanceof AstShiftAssignExpression) {
					return (CirStatement) this.get_cir_nodes(
							location, CirBinAssignStatement.class).get(0);
				}
				else {
					return range.get_end_statement();
				}
			}
			else {
				return range.get_end_statement();
			}
		}
		else if(location instanceof AstStatement) {
			if(location instanceof AstBreakStatement
				|| location instanceof AstContinueStatement
				|| location instanceof AstGotoStatement) {
				return (CirStatement) this.get_cir_nodes(
						location, CirGotoStatement.class).get(0);
			}
			else if(location instanceof AstReturnStatement) {
				if(((AstReturnStatement) location).has_expression()) {
					return (CirStatement) this.get_cir_nodes(location, 
							CirReturnAssignStatement.class).get(0);
				}
				else {
					return (CirStatement) this.get_cir_nodes(
							location, CirGotoStatement.class).get(0);
				}
			}
			else if(location instanceof AstLabeledStatement) {
				return (CirStatement) this.get_cir_nodes(
						location, CirLabelStatement.class).get(0);
			}
			else if(location instanceof AstDefaultStatement) {
				return (CirStatement) this.get_cir_nodes(
						location, CirDefaultStatement.class).get(0);
			}
			else if(location instanceof AstCaseStatement) {
				return (CirStatement) this.get_cir_nodes(
						location, CirCaseStatement.class).get(0);
			}
			else if(location instanceof AstIfStatement
					|| location instanceof AstWhileStatement) {
				return (CirStatement) this.get_cir_nodes(
						location, CirIfStatement.class).get(0);
			}
			else if(location instanceof AstSwitchStatement) {
				return (CirStatement) this.get_cir_nodes(location, 
						CirSaveAssignStatement.class).get(0);
			}
			else if(location instanceof AstDoWhileStatement) {
				CirStatement statement;
				statement = this.get_beg_statement(((AstDoWhileStatement) location).get_body());
				if(statement == null) {
					statement = (CirStatement) this.get_cir_nodes(
							location, CirIfStatement.class).get(0);
				}
				return statement;
			}
			else if(location instanceof AstForStatement) {
				CirStatement statement = null;
				if(statement == null)
					statement = this.get_beg_statement(((AstForStatement) location).get_initializer());
				if(statement == null)
					statement = this.get_beg_statement(((AstForStatement) location).get_condition());
				if(statement == null)
					statement = (CirStatement) this.get_cir_nodes(location, CirIfStatement.class).get(0);
				return statement;
			}
			else {
				return range.get_beg_statement();
			}
		}
		else if(location instanceof AstStatementList) {
			AstStatementList list = (AstStatementList) location;
			for(int k = 0; k < list.number_of_statements(); k++) {
				CirStatement statement = this.get_beg_statement(list.get_statement(k));
				if(statement != null)
					return statement;
			}
			return null;
		}
		else if(location instanceof AstFunctionDefinition) {
			CirFunctionDefinition definition = (CirFunctionDefinition) this.
					get_cir_nodes(location, CirFunctionDefinition.class).get(0);
			return this.cir_tree.get_function_call_graph().get_function(
					definition).get_flow_graph().get_entry().get_statement();
		}
		else {
			return range.get_beg_statement();
		}
	}
	/**
	 * @param location
	 * @return the first statement that is executed iff. the location is reached
	 * @throws Exception
	 */
	public CirStatement beg_statement(AstNode location) throws Exception {
		CirStatement statement = this.get_beg_statement(location);
		if(statement == null) {
			AstNode parent = location.get_parent();
			int index = this.find_child_index(parent, location);
			
			for(int k = index - 1; k >= 0; k--) {
				statement = this.get_end_statement(parent.get_child(k));
				if(statement != null) return statement;
			}
			
			return this.beg_statement(parent);
		}
		else {
			return statement;
		}
	}
	/**
	 * @param location
	 * @return the final statement in which the location defines
	 * @throws Exception
	 */
	private CirStatement get_end_statement(AstNode location) throws Exception {
		AstCirPair range = this.get_cir_range(location);
		if(range == null) {
			return null;
		}
		else if(location instanceof AstExpression) {
			if(location instanceof AstArrayExpression
				|| location instanceof AstBasicExpression
				|| location instanceof AstCastExpression
				|| location instanceof AstCommaExpression
				|| location instanceof AstFieldExpression
				|| location instanceof AstInitializerBody
				|| location instanceof AstSizeofExpression) {
				return range.get_end_statement();
			}
			else if(location instanceof AstConstExpression) {
				return this.get_end_statement(
						((AstConstExpression) location).get_expression());
			}
			else if(location instanceof AstParanthExpression) {
				return this.get_end_statement(
						((AstParanthExpression) location).get_sub_expression());
			}
			else if(location instanceof AstFunCallExpression) {
				return (CirStatement) this.get_cir_nodes(location, 
							CirWaitAssignStatement.class).get(0);
			}
			else if(location instanceof AstConditionalExpression) {
				return (CirStatement) this.get_cir_nodes(
						location, CirIfEndStatement.class).get(0);
			}
			else if(location instanceof AstUnaryExpression) {
				if(location instanceof AstIncreUnaryExpression) {
					return (CirStatement) this.get_cir_nodes(location, 
							CirIncreAssignStatement.class).get(0);
				}
				else {
					return range.get_end_statement();
				}
			}
			else if(location instanceof AstPostfixExpression) {
				return (CirStatement) this.get_cir_nodes(location, 
							CirIncreAssignStatement.class).get(0);
			}
			else if(location instanceof AstBinaryExpression) {
				if(location instanceof AstLogicBinaryExpression) {
					return (CirStatement) this.get_cir_nodes(
							location, CirIfEndStatement.class).get(0);
				}
				else if(location instanceof AstAssignExpression
						|| location instanceof AstArithAssignExpression
						|| location instanceof AstBitwiseAssignExpression
						|| location instanceof AstShiftAssignExpression) {
					return (CirStatement) this.get_cir_nodes(
							location, CirAssignStatement.class).get(0);
				}
				else {
					return range.get_end_statement();
				}
			}
			else {
				return range.get_end_statement();
			}
		}
		else if(location instanceof AstStatement) {
			if(location instanceof AstBreakStatement
				|| location instanceof AstContinueStatement
				|| location instanceof AstGotoStatement
				|| location instanceof AstReturnStatement) {
				return (CirStatement) this.get_cir_nodes(
						location, CirGotoStatement.class).get(0);
			}
			else if(location instanceof AstLabeledStatement) {
				return (CirStatement) this.get_cir_nodes(
						location, CirLabelStatement.class).get(0);
			}
			else if(location instanceof AstDefaultStatement) {
				return (CirStatement) this.get_cir_nodes(
						location, CirDefaultStatement.class).get(0);
			}
			else if(location instanceof AstCaseStatement) {
				return (CirStatement) this.get_cir_nodes(
						location, CirCaseStatement.class).get(0);
			}
			else if(location instanceof AstIfStatement
					|| location instanceof AstWhileStatement
					|| location instanceof AstForStatement
					|| location instanceof AstDoWhileStatement) {
				return (CirStatement) this.get_cir_nodes(
						location, CirIfEndStatement.class).get(0);
			}
			else if(location instanceof AstSwitchStatement) {
				return (CirStatement) this.get_cir_nodes(
						location, CirCaseEndStatement.class).get(0);
			}
			else {
				return range.get_end_statement();
			}
		}
		else if(location instanceof AstStatementList) {
			AstStatementList list = (AstStatementList) location;
			CirStatement statement = null, new_statement;
			for(int k = 0; k < list.number_of_statements(); k++) {
				new_statement = this.get_end_statement(list.get_statement(k));
				if(new_statement != null) {
					statement = new_statement;
				}
			}
			return statement;
		}
		else if(location instanceof AstFunctionDefinition) {
			CirFunctionDefinition definition = (CirFunctionDefinition) this.
					get_cir_nodes(location, CirFunctionDefinition.class).get(0);
			return this.cir_tree.get_function_call_graph().get_function(
					definition).get_flow_graph().get_exit().get_statement();
		}
		else {
			return range.get_end_statement();
		}
	}
	public CirStatement end_statement(AstNode location) throws Exception {
		CirStatement statement = this.get_end_statement(location);
		if(statement == null) {
			AstNode parent = location.get_parent();
			int index = this.find_child_index(parent, location);
			
			for(int k = index + 1; k < parent.number_of_children(); k++) {
				statement = this.get_beg_statement(parent.get_child(k));
				if(statement != null) { return statement; }
			}
			
			return this.end_statement(parent);
		}
		else {
			return statement;
		}
	}
	/**
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	public CirExecution get_execution(CirStatement statement) throws Exception {
		return this.cir_tree.get_function_call_graph().get_function(
				statement).get_flow_graph().get_execution(statement);
	}
	/**
	 * @param statement
	 * @return the wait-assign-statement correspond to the call-statement
	 * @throws Exception
	 */
	public CirExecution get_return_point(CirCallStatement statement) throws Exception {
		CirExecution call_execution = this.get_execution(statement);
		return call_execution.get_graph().get_execution(call_execution.get_id() + 1);
	}
	/**
	 * collect all the expressions under the location
	 * @param location
	 * @param expressions
	 */
	private static void collect_expressions_in(CirNode location, Set<CirExpression> expressions) {
		for(CirNode child : location.get_children()) {
			collect_expressions_in(child, expressions);
		}
		if(location instanceof CirExpression) {
			expressions.add((CirExpression) location);
		}
	}
	/**
	 * @param location
	 * @return get the expressions in the location
	 * @throws Exception
	 */
	public static Set<CirExpression> expressions_in(CirNode location) throws Exception {
		if(location == null)
			throw new IllegalArgumentException("Invalid location: null");
		else {
			Set<CirExpression> expressions = new HashSet<CirExpression>();
			collect_expressions_in(location, expressions);
			return expressions;
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is taken as a left-reference
	 */
	public static boolean is_left_reference(CirExpression expression) {
		CirNode parent = expression.get_parent();
		if(parent instanceof CirAssignStatement) {
			return ((CirAssignStatement) parent).get_lvalue() == expression;
		}
		else if(parent instanceof CirAddressExpression) {
			return ((CirAddressExpression) parent).get_operand() == expression;
		}
		else if(parent instanceof CirFieldExpression) {
			return ((CirFieldExpression) parent).get_body() == expression;
		}
		else {
			return false;
		}
	}
	
}
