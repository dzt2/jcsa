package com.jcsa.jcmutest.mutant.cir2mutant;

import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
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
import com.jcsa.jcparse.lang.astree.stmt.AstStatementList;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.irlang.AstCirPair;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirBinAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirReturnAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirSaveAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;

/**
 * It parses the ast-mutation to generate cir-mutation
 * 
 * @author yukimula
 *
 */
public abstract class CirMutationParser {
	
	/**
	 * @param tree the syntax tree of C-intermediate representation
	 * @param source the ast-mutation from which the cir-mutations are produced
	 * @param targets the set of cir-mutations parsed from the ast-mutation
	 * @throws Exception
	 */
	protected abstract void parse(CirTree tree, AstMutation 
			source, List<CirMutation> targets) throws Exception;
	
	/* utility methods */
	/**
	 * @param tree syntactic tree for intermediate representation language
	 * @param location the location in abstract syntax tree from C program
	 * @return the range of cir-code to which the ast-location corresponds
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
	 * @return the cir-nodes to which the location corresponds w.r.t. the specified type
	 * @throws Exception
	 */
	protected List<CirNode> get_cir_nodes(CirTree tree, AstNode location, Class<?> type) throws Exception {
		return tree.get_cir_nodes(location, type);
	}
	/**
	 * @param tree
	 * @param location
	 * @return the expression to which the location corresponds
	 * @throws Exception
	 */
	protected CirExpression get_use_point(CirTree tree, AstNode location) throws Exception {
		if(location instanceof AstParanthExpression) {
			location = ((AstParanthExpression) location).get_sub_expression();
		}
		else if(location instanceof AstConstExpression) {
			location = ((AstConstExpression) location).get_expression();
		}
		else if(location instanceof AstInitializer) {
			if(((AstInitializer) location).is_body())
				location = ((AstInitializer) location).get_body();
			else
				location = ((AstInitializer) location).get_expression();
		}
		AstCirPair range = this.get_cir_range(tree, location);
		if(range == null || !range.computational()) {
			throw new IllegalArgumentException("Not computable: " + location.generate_code());
		}
		else if(location instanceof AstIdExpression
				|| location instanceof AstConstant
				|| location instanceof AstLiteral
				|| location instanceof AstSizeofExpression
				|| location instanceof AstArrayExpression
				|| location instanceof AstCastExpression
				|| location instanceof AstFieldExpression
				|| location instanceof AstArithBinaryExpression
				|| location instanceof AstBitwiseBinaryExpression
				|| location instanceof AstShiftBinaryExpression
				|| location instanceof AstRelationExpression
				|| location instanceof AstArithUnaryExpression
				|| location instanceof AstBitwiseUnaryExpression
				|| location instanceof AstLogicUnaryExpression
				|| location instanceof AstPointUnaryExpression
				|| location instanceof AstLogicBinaryExpression
				|| location instanceof AstConditionalExpression
				|| location instanceof AstCommaExpression) {
			return range.get_result();
		}
		else if(location instanceof AstConstExpression) {
			return this.get_use_point(tree, 
					((AstConstExpression) location).get_expression());
		}
		else if(location instanceof AstParanthExpression) {
			return this.get_use_point(tree, ((AstParanthExpression) 
									location).get_sub_expression());
		}
		else if(location instanceof AstAssignExpression
				|| location instanceof AstArithAssignExpression
				|| location instanceof AstBitwiseAssignExpression
				|| location instanceof AstShiftAssignExpression) {
			CirAssignStatement statement = (CirAssignStatement) get_cir_nodes(
							tree, location, CirBinAssignStatement.class).get(0);
			return statement.get_rvalue();
		}
		else if(location instanceof AstFunCallExpression) {
			CirAssignStatement statement = (CirAssignStatement) get_cir_nodes(
						tree, location, CirWaitAssignStatement.class).get(0);
			return statement.get_rvalue();
		}
		else if(location instanceof AstIncreUnaryExpression) {
			CirAssignStatement statement = (CirAssignStatement) get_cir_nodes(
						tree, location, CirIncreAssignStatement.class).get(0);
			return statement.get_rvalue();
		}
		else if(location instanceof AstIncrePostfixExpression) {
			CirAssignStatement statement = (CirAssignStatement) get_cir_nodes(
						tree, location, CirSaveAssignStatement.class).get(0);
			return statement.get_rvalue();
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + location);
		}
	}
	/**
	 * @param tree
	 * @param location
	 * @return the first statement to execute the location
	 * @throws Exception
	 */
	protected CirStatement beg_statement(CirTree tree, AstNode location) throws Exception {
		AstCirPair range = this.get_cir_range(tree, location);
		if(range == null || !range.executional()) {
			return this.beg_statement(tree, location.get_parent());
		}
		else if(location instanceof AstAssignExpression
				|| location instanceof AstArithAssignExpression
				|| location instanceof AstBitwiseAssignExpression
				|| location instanceof AstShiftAssignExpression) {
			return (CirStatement) this.get_cir_nodes(tree, 
					location, CirBinAssignStatement.class).get(0);
		}
		else if(location instanceof AstFunCallExpression) {
			return (CirStatement) this.get_cir_nodes(tree, 
					location, CirCallStatement.class).get(0);
		}
		else if(location instanceof AstIncreUnaryExpression) {
			return (CirAssignStatement) this.get_cir_nodes(tree, 
					location, CirIncreAssignStatement.class).get(0);
		}
		else if(location instanceof AstIncrePostfixExpression) {
			return (CirAssignStatement) this.get_cir_nodes(tree, 
					location, CirSaveAssignStatement.class).get(0);
		}
		else if(location instanceof AstConditionalExpression
				|| location instanceof AstLogicBinaryExpression) {
			return (CirStatement) this.get_cir_nodes(tree, 
					location, CirIfStatement.class).get(0);
		}
		else if(location instanceof AstExpression) {
			return range.get_beg_statement();
		}
		else if(location instanceof AstGotoStatement
				|| location instanceof AstBreakStatement
				|| location instanceof AstContinueStatement
				|| location instanceof AstSwitchStatement) {
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
		else if(location instanceof AstLabeledStatement
				|| location instanceof AstCaseStatement
				|| location instanceof AstDefaultStatement) {
			return range.get_beg_statement();
		}
		else if(location instanceof AstStatementList) {
			CirStatement beg_statement = null;
			for(int k = 0; k < location.number_of_children(); k++) {
				beg_statement = this.beg_statement(tree, 
						((AstStatementList) location).get_statement(k));
				if(beg_statement != null) {
					return beg_statement;
				}
			}
			return this.beg_statement(tree, location.get_parent());
		}
		else if(location instanceof AstExpressionStatement
				|| location instanceof AstDeclarationStatement
				|| location instanceof AstCompoundStatement) {
			return range.get_beg_statement();
		}
		else if(location instanceof AstIfStatement
				|| location instanceof AstForStatement
				|| location instanceof AstWhileStatement) {
			return (CirStatement) this.get_cir_nodes(tree, 
					location, CirIfStatement.class).get(0);
		}
		else if(location instanceof AstDoWhileStatement) {
			return range.get_beg_statement();
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + location);
		}
	}
	/**
	 * @param statement
	 * @return the executional node to which the statement corresponds
	 * @throws Exception
	 */
	protected CirExecution get_execution(CirStatement statement) throws Exception {
		return statement.get_tree().get_function_call_graph().get_function(
					statement).get_flow_graph().get_execution(statement);
	}
	
}
