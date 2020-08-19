package com.jcsa.jcmutest.mutant.cir2mutation;

import java.util.Collection;
import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPointUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftBinaryExpression;
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
import com.jcsa.jcparse.lang.astree.stmt.AstCompoundStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstContinueStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDeclarationStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDefaultStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
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
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
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

/**
 * It is used to parse the cir-mutation from ast-mutation.
 * 
 * @author yukimula
 *
 */
public abstract class CirMutationParser {
	
	/**
	 * parse from the ast-mutation to the cir-mutation(s) that describes it
	 * or none which implies the source mutation is an equivalent mutation.
	 * @param source
	 * @param targets
	 * @throws Exception
	 */
	public abstract void parse(CirTree cir_tree, AstMutation source, Collection<CirMutation> targets) throws Exception;
	
	/* utility methods */
	/**
	 * @param cir_tree
	 * @param location
	 * @return the range of cir-code to which the ast-location corresponds
	 * @throws Exception
	 */
	protected AstCirPair get_cir_range(CirTree cir_tree, AstNode location) throws Exception {
		if(cir_tree.has_cir_range(location))
			return cir_tree.get_cir_range(location);
		else
			return null;
	}
	/**
	 * @param cir_tree
	 * @param location
	 * @param cir_class
	 * @return the set of cir-locations to which the ast-location corresponds.
	 * @throws Exception
	 */
	protected List<CirNode> get_cir_nodes(CirTree cir_tree, AstNode location, Class<?> cir_class) throws Exception {
		return cir_tree.get_cir_nodes(location, cir_class);
	}
	/**
	 * @param cir_tree
	 * @param location
	 * @param cir_class
	 * @param index
	 * @return the cir-node to which the location corresponds w.r.t. the given
	 * 		   index and class tag.
	 * @throws Exception
	 */
	protected CirNode get_cir_node(CirTree cir_tree, AstNode location, Class<?> cir_class, int index) throws Exception {
		List<CirNode> cir_nodes = cir_tree.
				get_cir_nodes(location, cir_class);
		if(index >= cir_nodes.size()) return null;
		else return cir_nodes.get(index);
	}
	/**
	 * @param source
	 * @return the expression in intermediate representation that can represent
	 * 		   the source expression in abstract syntactic structure.
	 * @throws Exception
	 */
	protected CirExpression get_use_point(CirTree cir_tree, AstExpression source) throws Exception {
		AstCirPair range = this.get_cir_range(cir_tree, source);
		if(range == null) {
			return null;
		}
		else if(source instanceof AstIdExpression) {
			return range.get_result();
		}
		else if(source instanceof AstConstant) {
			return range.get_result();
		}
		else if(source instanceof AstLiteral) {
			return range.get_result();
		}
		else if(source instanceof AstArithUnaryExpression) {
			return range.get_result();
		}
		else if(source instanceof AstBitwiseUnaryExpression) {
			return range.get_result();
		}
		else if(source instanceof AstLogicUnaryExpression) {
			return range.get_result();
		}
		else if(source instanceof AstPointUnaryExpression) {
			return range.get_result();
		}
		else if(source instanceof AstIncreUnaryExpression) {
			CirAssignStatement statement = (CirAssignStatement) this.get_cir_node(
							cir_tree, source, CirIncreAssignStatement.class, 0);
			return statement.get_rvalue();
		}
		else if(source instanceof AstIncrePostfixExpression) {
			CirAssignStatement statement = (CirAssignStatement) this.get_cir_node(
								cir_tree, source, CirSaveAssignStatement.class, 0);
			return statement.get_rvalue();
		}
		else if(source instanceof AstArithBinaryExpression
				|| source instanceof AstBitwiseBinaryExpression
				|| source instanceof AstShiftBinaryExpression
				|| source instanceof AstRelationExpression) {
			return range.get_result();
		}
		else if(source instanceof AstLogicBinaryExpression) {
			return range.get_result();
		}
		else if(source instanceof AstAssignExpression
				|| source instanceof AstArithAssignExpression
				|| source instanceof AstBitwiseAssignExpression
				|| source instanceof AstShiftAssignExpression) {
			CirAssignStatement statement = (CirAssignStatement) this.get_cir_node(
								cir_tree, source, CirBinAssignStatement.class, 0);
			return statement.get_rvalue();
		}
		else if(source instanceof AstArrayExpression
				|| source instanceof AstCastExpression
				|| source instanceof AstFieldExpression
				|| source instanceof AstSizeofExpression
				|| source instanceof AstCommaExpression) {
			return range.get_result();
		}
		else if(source instanceof AstParanthExpression) {
			return this.get_use_point(cir_tree, ((AstParanthExpression) source).get_sub_expression());
		}
		else if(source instanceof AstConstExpression) {
			return this.get_use_point(cir_tree, ((AstConstExpression) source).get_expression());
		}
		else if(source instanceof AstConditionalExpression) {
			return range.get_result();
		}
		else if(source instanceof AstFunCallExpression) {
			CirAssignStatement statement = (CirAssignStatement) this.get_cir_node(
								cir_tree, source, CirWaitAssignStatement.class, 0);
			return statement.get_rvalue();
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + source);
		}
	}
	/**
	 * @param statement
	 * @return the executional node in flow graph that the statement represents.
	 * @throws Excception
	 */
	protected CirExecution get_cir_execution(CirStatement statement) throws Exception {
		return statement.get_tree().get_function_call_graph().get_function(
				statement).get_flow_graph().get_execution(statement);
	}
	/**
	 * @param parent
	 * @param child
	 * @return the index of the child under the parent
	 * @throws Exception
	 */
	private int index_of_ast_child(AstNode parent, AstNode child) throws Exception {
		for(int k = 0; k < parent.number_of_children(); k++) {
			if(parent.get_child(k) == child) return k;
		}
		throw new IllegalArgumentException("Invalid: " + child);
	}
	/**
	 * @param location
	 * @return the statement in cir which is executed iff. the location is 
	 * 		   reached during testing or null if no statement refers to it.
	 * @throws Exception
	 */
	protected CirStatement get_beg_statement(CirTree cir_tree, AstNode location) throws Exception {
		AstCirPair range = this.get_cir_range(cir_tree, location);
		if(range == null || !range.executional()) {
			AstNode child = location, parent = location.get_parent();
			while(parent != null) {
				int index = this.index_of_ast_child(parent, child);
				if(index > 0) {
					return this.get_end_statement(cir_tree, parent.get_child(index - 1));
				}
				else {
					child = parent; parent = parent.get_parent();
				}
			}
			return null;
		}
		else if(location instanceof AstFunctionDefinition) {
			CirFunctionDefinition def = (CirFunctionDefinition) this.get_cir_node(
							cir_tree, location, CirFunctionDefinition.class, 0);
			CirFunction function = cir_tree.get_function_call_graph().get_function(def);
			return function.get_flow_graph().get_entry().get_statement();
		}
		else if(location instanceof AstStatementList) {
			for(int k = 0; k < location.number_of_children(); k++) {
				AstStatement stmt = ((AstStatementList) location).get_statement(k);
				CirStatement beg_stmt = this.get_beg_statement(cir_tree, stmt);
				if(beg_stmt != null) return beg_stmt;
			}
			return this.get_beg_statement(cir_tree, location.get_parent());
		}
		else if(location instanceof AstExpression) {
			if(location instanceof AstIdExpression
				|| location instanceof AstConstant
				|| location instanceof AstLiteral
				|| location instanceof AstInitializerBody) {
				return range.get_beg_statement();
			}
			else if(location instanceof AstArithUnaryExpression
					|| location instanceof AstBitwiseUnaryExpression
					|| location instanceof AstLogicUnaryExpression
					|| location instanceof AstPointUnaryExpression) {
				return range.get_beg_statement();
			}
			else if(location instanceof AstIncreUnaryExpression
					|| location instanceof AstIncrePostfixExpression) {
				return (CirStatement) this.get_cir_node(
						cir_tree, location, CirIncreAssignStatement.class, 0);
			}
			else if(location instanceof AstArithBinaryExpression
					|| location instanceof AstBitwiseBinaryExpression
					|| location instanceof AstShiftBinaryExpression
					|| location instanceof AstRelationExpression) {
				return range.get_beg_statement();
			}
			else if(location instanceof AstAssignExpression
					|| location instanceof AstArithAssignExpression
					|| location instanceof AstBitwiseAssignExpression
					|| location instanceof AstShiftAssignExpression) {
				return (CirStatement) this.get_cir_node(cir_tree, 
						location, CirBinAssignStatement.class, 0);
			}
			else if(location instanceof AstLogicBinaryExpression
					|| location instanceof AstConditionalExpression) {
				return (CirStatement) this.get_cir_node(cir_tree, location, CirIfStatement.class, 0);
			}
			else if(location instanceof AstParanthExpression) {
				return this.get_beg_statement(cir_tree, 
						((AstParanthExpression) location).get_sub_expression());
			}
			else if(location instanceof AstConstExpression) {
				return this.get_beg_statement(cir_tree, 
						((AstConstExpression) location).get_expression());
			}
			else if(location instanceof AstArrayExpression
					|| location instanceof AstCastExpression
					|| location instanceof AstCommaExpression
					|| location instanceof AstFieldExpression
					|| location instanceof AstSizeofExpression) {
				return range.get_beg_statement();
			}
			else if(location instanceof AstFunCallExpression) {
				return (CirStatement) this.get_cir_node(
						cir_tree, location, CirCallStatement.class, 0);
			}
			else {
				throw new IllegalArgumentException("Unsupport: " + location);
			}
		}
		else if(location instanceof AstStatement) {
			if(location instanceof AstBreakStatement
				|| location instanceof AstContinueStatement
				|| location instanceof AstGotoStatement) {
				return (CirStatement) this.get_cir_node(
						cir_tree, location, CirGotoStatement.class, 0);
			}
			else if(location instanceof AstReturnStatement) {
				if(((AstReturnStatement) location).has_expression()) {
					return (CirStatement) this.get_cir_node(cir_tree, 
							location, CirReturnAssignStatement.class, 0);
				}
				else {
					return (CirStatement) this.get_cir_node(
							cir_tree, location, CirGotoStatement.class, 0);
				}
			}
			else if(location instanceof AstLabeledStatement) {
				return (CirStatement) this.get_cir_node(
						cir_tree, location, CirLabelStatement.class, 0);
			}
			else if(location instanceof AstCaseStatement) {
				return (CirStatement) this.get_cir_node(
						cir_tree, location, CirCaseStatement.class, 0);
			}
			else if(location instanceof AstDefaultStatement) {
				return (CirStatement) this.get_cir_node(
						cir_tree, location, CirDefaultStatement.class, 0);
			}
			else if(location instanceof AstIfStatement) {
				return (CirStatement) this.get_cir_node(
						cir_tree, location, CirIfStatement.class, 0);
			}
			else if(location instanceof AstSwitchStatement) {
				return (CirStatement) this.get_cir_node(
						cir_tree, location, CirSaveAssignStatement.class, 0);
			}
			else if(location instanceof AstWhileStatement) {
				return (CirStatement) this.get_cir_node(
						cir_tree, location, CirIfStatement.class, 0);
			}
			else if(location instanceof AstDoWhileStatement) {
				CirStatement body_beg = this.get_beg_statement(cir_tree, 
							((AstDoWhileStatement) location).get_body());
				if(body_beg == null) {
					body_beg = (CirStatement) this.get_cir_node(
							cir_tree, location, CirIfStatement.class, 0);
				}
				return body_beg;
			}
			else if(location instanceof AstForStatement) {
				return (CirStatement) this.get_cir_node(
						cir_tree, location, CirIfStatement.class, 0);
			}
			else if(location instanceof AstDeclarationStatement
					|| location instanceof AstExpressionStatement) {
				return range.get_beg_statement();
			}
			else if(location instanceof AstCompoundStatement) {
				return range.get_beg_statement();
			}
			else {
				throw new IllegalArgumentException("Unsupport: " + location);
			}
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + location);
		}
	}
	/**
	 * @param cir_tree
	 * @param location
	 * @return the statement in cir which is executed iff. the location is
	 * 		   reached during testing or null if no statement refers to it
	 * @throws Exception
	 */
	protected CirStatement get_end_statement(CirTree cir_tree, AstNode location) throws Exception {
		AstCirPair range = this.get_cir_range(cir_tree, location);
		if(range == null || !range.executional()) {
			AstNode child = location, parent = location.get_parent();
			while(parent != null) {
				int index = this.index_of_ast_child(parent, child);
				if(index > 0) {
					return this.get_beg_statement(cir_tree, parent.get_child(index + 1));
				}
				else {
					child = parent; parent = parent.get_parent();
				}
			}
			return null;
		}
		else if(location instanceof AstFunctionDefinition) {
			CirFunctionDefinition def = (CirFunctionDefinition) this.get_cir_node(
							cir_tree, location, CirFunctionDefinition.class, 0);
			CirFunction function = cir_tree.get_function_call_graph().get_function(def);
			return function.get_flow_graph().get_exit().get_statement();
		}
		else if(location instanceof AstStatementList) {
			CirStatement end_statement = null;
			for(int k = 0; k < location.number_of_children(); k++) {
				AstStatement stmt = ((AstStatementList) location).get_statement(k);
				end_statement = this.get_end_statement(cir_tree, stmt);
			}
			if(end_statement == null) 
				end_statement = this.get_end_statement(cir_tree, location.get_parent());
			return end_statement;
		}
		else if(location instanceof AstExpression) {
			if(location instanceof AstIdExpression
				|| location instanceof AstConstant
				|| location instanceof AstLiteral
				|| location instanceof AstInitializerBody) {
				return range.get_end_statement();
			}
			else if(location instanceof AstArithUnaryExpression
					|| location instanceof AstBitwiseUnaryExpression
					|| location instanceof AstLogicUnaryExpression
					|| location instanceof AstPointUnaryExpression) {
				return range.get_end_statement();
			}
			else if(location instanceof AstIncreUnaryExpression
					|| location instanceof AstIncrePostfixExpression) {
				return (CirStatement) this.get_cir_node(
						cir_tree, location, CirIncreAssignStatement.class, 0);
			}
			else if(location instanceof AstArithBinaryExpression
					|| location instanceof AstBitwiseBinaryExpression
					|| location instanceof AstShiftBinaryExpression
					|| location instanceof AstRelationExpression) {
				return range.get_end_statement();
			}
			else if(location instanceof AstAssignExpression
					|| location instanceof AstArithAssignExpression
					|| location instanceof AstBitwiseAssignExpression
					|| location instanceof AstShiftAssignExpression) {
				return (CirStatement) this.get_cir_node(cir_tree, 
						location, CirBinAssignStatement.class, 0);
			}
			else if(location instanceof AstLogicBinaryExpression
					|| location instanceof AstConditionalExpression) {
				return (CirStatement) this.get_cir_node(cir_tree, 
							location, CirIfEndStatement.class, 0);
			}
			else if(location instanceof AstParanthExpression) {
				return this.get_end_statement(cir_tree, 
						((AstParanthExpression) location).get_sub_expression());
			}
			else if(location instanceof AstConstExpression) {
				return this.get_end_statement(cir_tree, 
						((AstConstExpression) location).get_expression());
			}
			else if(location instanceof AstArrayExpression
					|| location instanceof AstCastExpression
					|| location instanceof AstCommaExpression
					|| location instanceof AstFieldExpression
					|| location instanceof AstSizeofExpression) {
				return range.get_end_statement();
			}
			else if(location instanceof AstFunCallExpression) {
				return (CirStatement) this.get_cir_node(
						cir_tree, location, CirWaitAssignStatement.class, 0);
			}
			else {
				throw new IllegalArgumentException("Unsupport: " + location);
			}
		}
		else if(location instanceof AstStatement) {
			if(location instanceof AstBreakStatement
				|| location instanceof AstContinueStatement
				|| location instanceof AstGotoStatement
				|| location instanceof AstReturnStatement) {
				return (CirStatement) this.get_cir_node(
						cir_tree, location, CirGotoStatement.class, 0);
			}
			else if(location instanceof AstLabeledStatement) {
				return (CirStatement) this.get_cir_node(
						cir_tree, location, CirLabelStatement.class, 0);
			}
			else if(location instanceof AstCaseStatement) {
				return (CirStatement) this.get_cir_node(
						cir_tree, location, CirCaseStatement.class, 0);
			}
			else if(location instanceof AstDefaultStatement) {
				return (CirStatement) this.get_cir_node(
						cir_tree, location, CirDefaultStatement.class, 0);
			}
			else if(location instanceof AstIfStatement) {
				return (CirStatement) this.get_cir_node(
						cir_tree, location, CirIfEndStatement.class, 0);
			}
			else if(location instanceof AstSwitchStatement) {
				return (CirStatement) this.get_cir_node(
						cir_tree, location, CirCaseEndStatement.class, 0);
			}
			else if(location instanceof AstWhileStatement
					|| location instanceof AstDoWhileStatement
					|| location instanceof AstForStatement) {
				return (CirStatement) this.get_cir_node(
						cir_tree, location, CirIfEndStatement.class, 0);
			}
			else if(location instanceof AstDeclarationStatement
					|| location instanceof AstExpressionStatement) {
				return range.get_end_statement();
			}
			else if(location instanceof AstCompoundStatement) {
				return range.get_end_statement();
			}
			else {
				throw new IllegalArgumentException("Unsupport: " + location);
			}
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + location);
		}
	}
	
}
