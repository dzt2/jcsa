package com.jcsa.jcmutest.mutant.cir2mutant;

import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConditionalExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFunCallExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstParanthExpression;
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
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
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

public abstract class CirMutationParser {
	
	/* implementation methods */
	/**
	 * @param tree the intermediate representation to interpret
	 * @param source ast-mutation from which cir-mutation is parsed
	 * @param targets the set of cir-mutations parsed from source
	 * @throws Exception
	 */
	protected abstract void parse(CirTree tree, AstMutation 
			source, List<CirMutation> targets) throws Exception;
	
	/* utility methods */
	/**
	 * @param tree
	 * @param location
	 * @return the range of cir-code to which the location corresponds
	 * @throws Exception
	 */
	protected AstCirPair get_cir_range(CirTree tree, AstNode location) throws Exception {
		if(tree.has_cir_range(location)) {
			return tree.get_cir_range(location);
		}
		else {
			return null;
		}
	}
	/**
	 * @param tree
	 * @param location
	 * @return the set of cir-nodes w.r.t. the location as specified
	 * @throws Exception
	 */
	protected List<CirNode> get_cir_nodes(CirTree tree, AstNode 
				location, Class<?> cir_type) throws Exception {
		return tree.get_cir_nodes(location, cir_type);
	}
	/**
	 * @param tree
	 * @param location
	 * @return the expression that represents the usage of the location
	 * @throws Exception
	 */
	protected CirExpression get_use_point(CirTree tree, AstNode location) throws Exception {
		AstCirPair range = this.get_cir_range(tree, location);
		if(range == null || !range.computational()) {
			return null;
		}
		else if(location instanceof AstExpression) {
			if(location instanceof AstIncreUnaryExpression) {
				CirIncreAssignStatement statement = (CirIncreAssignStatement) this.
						get_cir_nodes(tree, location, CirIncreAssignStatement.class).get(0);
				return statement.get_rvalue();
			}
			else if(location instanceof AstIncrePostfixExpression) {
				CirSaveAssignStatement statement = (CirSaveAssignStatement) this.
						get_cir_nodes(tree, location, CirSaveAssignStatement.class).get(0);
				return statement.get_rvalue();
			}
			else if(location instanceof AstAssignExpression
					|| location instanceof AstArithAssignExpression
					|| location instanceof AstBitwiseAssignExpression
					|| location instanceof AstShiftAssignExpression) {
				CirAssignStatement statement = (CirAssignStatement) this.
						get_cir_nodes(tree, location, CirAssignStatement.class).get(0);
				return statement.get_rvalue();
			}
			else {
				return range.get_result();
			}
		}
		else {
			throw new IllegalArgumentException("Invalid: " + location);
		}
	}
	/**
	 * @param location
	 * @return index of the location in its parent.
	 * @throws Exception
	 */
	private int child_index(AstNode location) throws Exception {
		AstNode parent = location.get_parent();
		if(parent != null) {
			for(int k = 0; k < parent.number_of_children(); k++) {
				if(parent.get_child(k) == location) {
					return k;
				}
			}
		}
		throw new IllegalArgumentException("Unable to decide index");
	}
	/**
	 * @param tree
	 * @param location
	 * @return the statement that represents the beginning of the location
	 * @throws Exception
	 */
	protected CirStatement get_beg_statement(CirTree tree, AstNode location) throws Exception {
		AstCirPair range = this.get_cir_range(tree, location);
		if(range == null || !range.executional()) {
			while(location != null) {
				int index = this.child_index(location);
				AstNode parent = location.get_parent();
				CirStatement beg_stmt = null;
				if(beg_stmt == null && index > 0) {
					beg_stmt = this.get_end_statement(tree, parent.get_child(index - 1));
				}
				if(beg_stmt == null && index < parent.number_of_children() - 1) {
					beg_stmt = this.get_beg_statement(tree, parent.get_child(index + 1));
				}
				location = parent;
			}
			return null;
		}
		else if(location instanceof AstFunctionDefinition) {
			CirFunctionDefinition def = (CirFunctionDefinition) get_cir_nodes(
					tree, location, CirFunctionDefinition.class).get(0);
			return tree.get_function_call_graph().get_function(def).
						get_flow_graph().get_entry().get_statement();
		}
		else if(location instanceof AstStatement) {
			if(location instanceof AstBreakStatement
				|| location instanceof AstContinueStatement
				|| location instanceof AstGotoStatement
				|| location instanceof AstReturnStatement) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirGotoStatement.class).get(0);
			}
			else if(location instanceof AstLabeledStatement) {
				return (CirStatement) this.get_cir_nodes(tree, 
							location, CirLabelStatement.class);
			}
			else if(location instanceof AstCaseStatement) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirCaseStatement.class).get(0);
			}
			else if(location instanceof AstDefaultStatement) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirDefaultStatement.class).get(0);
			}
			else if(location instanceof AstIfStatement
					|| location instanceof AstWhileStatement
					|| location instanceof AstForStatement
					|| location instanceof AstDoWhileStatement) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirIfEndStatement.class).get(0);
			}
			else if(location instanceof AstSwitchStatement) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirCaseEndStatement.class).get(0);
			}
			else {
				return range.get_end_statement();
			}
		}
		else if(location instanceof AstExpression) {
			if(location instanceof AstIncreUnaryExpression
				|| location instanceof AstIncrePostfixExpression) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirIncreAssignStatement.class).get(0);
			}
			else if(location instanceof AstLogicBinaryExpression
					|| location instanceof AstConditionalExpression) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirIfEndStatement.class).get(0);
			}
			else if(location instanceof AstConstExpression) {
				return this.get_end_statement(tree, 
						((AstConstExpression) location).get_expression());
			}
			else if(location instanceof AstParanthExpression) {
				return this.get_end_statement(tree, 
						((AstParanthExpression) location).get_sub_expression());
			}
			else if(location instanceof AstFunCallExpression) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirWaitAssignStatement.class).get(0);
			}
			else {
				return range.get_end_statement();
			}
		}
		else if(location instanceof AstStatementList) {
			CirStatement end_stmt = null;
			for(int k = location.number_of_children() - 1; k >= 0; k--) {
				end_stmt = this.get_end_statement(tree, 
						((AstStatementList) location).get_statement(k));
				if(end_stmt != null) return end_stmt;
			}
			return range.get_end_statement();
		}
		else {
			throw new IllegalArgumentException("Invalid: " + location);
		}
	}
	protected CirStatement get_end_statement(CirTree tree, AstNode location) throws Exception {
		AstCirPair range = this.get_cir_range(tree, location);
		if(range == null || !range.executional()) {
			while(location != null) {
				int index = this.child_index(location);
				AstNode parent = location.get_parent();
				CirStatement beg_stmt = null;
				if(beg_stmt == null && index > 0) {
					beg_stmt = this.get_end_statement(tree, parent.get_child(index - 1));
				}
				if(beg_stmt == null && index < parent.number_of_children() - 1) {
					beg_stmt = this.get_beg_statement(tree, parent.get_child(index + 1));
				}
				location = parent;
			}
			return null;
		}
		else if(location instanceof AstFunctionDefinition) {
			CirFunctionDefinition def = (CirFunctionDefinition) get_cir_nodes(
							tree, location, CirFunctionDefinition.class).get(0);
			return tree.get_function_call_graph().get_function(def).
						get_flow_graph().get_exit().get_statement();
		}
		else if(location instanceof AstStatement) {
			if(location instanceof AstBreakStatement
				|| location instanceof AstContinueStatement
				|| location instanceof AstGotoStatement) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirGotoStatement.class).get(0);
			}
			else if(location instanceof AstReturnStatement) {
				if(((AstReturnStatement) location).has_expression()) {
					return (CirStatement) this.get_cir_nodes(tree, 
							location, CirReturnAssignStatement.class).get(0);
				}
				else {
					return (CirStatement) this.get_cir_nodes(tree, 
							location, CirGotoStatement.class).get(0);
				}
			}
			else if(location instanceof AstLabeledStatement) {
				return (CirStatement) this.get_cir_nodes(tree, 
							location, CirLabelStatement.class);
			}
			else if(location instanceof AstCaseStatement) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirCaseStatement.class).get(0);
			}
			else if(location instanceof AstDefaultStatement) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirDefaultStatement.class).get(0);
			}
			else if(location instanceof AstIfStatement
					|| location instanceof AstWhileStatement
					|| location instanceof AstForStatement) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirIfStatement.class).get(0);
			}
			else if(location instanceof AstDoWhileStatement) {
				CirStatement beg_stmt = this.get_beg_statement(tree, 
						((AstDoWhileStatement) location).get_body());
				if(beg_stmt != null) {
					beg_stmt = (CirStatement) this.get_cir_nodes(tree, 
							location, CirIfStatement.class).get(0);
				}
				return beg_stmt;
			}
			else if(location instanceof AstSwitchStatement) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirGotoStatement.class).get(0);
			}
			else {
				return range.get_beg_statement();
			}
		}
		else if(location instanceof AstExpression) {
			if(location instanceof AstIncreUnaryExpression
				|| location instanceof AstIncrePostfixExpression
				|| location instanceof AstLogicBinaryExpression) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirAssignStatement.class).get(0);
			}
			else if(location instanceof AstConditionalExpression) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirIfStatement.class).get(0);
			}
			else if(location instanceof AstConstExpression) {
				return this.get_beg_statement(tree, 
						((AstConstExpression) location).get_expression());
			}
			else if(location instanceof AstParanthExpression) {
				return this.get_beg_statement(tree, 
						((AstParanthExpression) location).get_sub_expression());
			}
			else if(location instanceof AstFunCallExpression) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirCallStatement.class).get(0);
			}
			else {
				return range.get_beg_statement();
			}
		}
		else if(location instanceof AstStatementList) {
			CirStatement beg_stmt = null;
			for(int k = 0; k < location.number_of_children(); k++) {
				beg_stmt = this.get_beg_statement(tree, 
						((AstStatementList) location).get_statement(k));
				if(beg_stmt != null) return beg_stmt;
			}
			return range.get_beg_statement();
		}
		else {
			throw new IllegalArgumentException("Invalid: " + location);
		}
	}
	/**
	 * @param statement
	 * @return the executional node to which the statement corresponds
	 * @throws Exception
	 */
	protected CirExecution get_cir_execution(CirStatement statement) throws Exception {
		return statement.get_tree().get_function_call_graph().get_function(
				statement).get_flow_graph().get_execution(statement);
	}
	
	
	
	
	
}
