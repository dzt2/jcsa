package com.jcsa.jcmutest.mutant.sad2mutant.muta;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadAssertion;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadExpression;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadFactory;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadParser;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPostfixExpression;
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
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.AstCirPair;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
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
 * It generates the infection part in the symbolic graph, including:<br>
 * 	1. execute_on(location, 1) as requirement to reach the faulty statement;<br>
 * 	2. relation with constraint as the infection condition to initial error;<br>
 * 	3. targets from the above relations as the initial state errors being created.<br>
 * 
 * @author yukimula
 *
 */
public abstract class SadInfection {
	
	/** maximal number of bytes being shifted **/
	protected static final int max_shift_size = 16;
	
	/* location identification */
	/**
	 * @param tree
	 * @param location
	 * @return the cir-code range to which the location refers or null
	 * @throws Exception
	 */
	protected AstCirPair get_cir_range(CirTree tree, AstNode location) throws Exception {
		if(tree.has_cir_range(location)) 
			return tree.get_cir_range(location);
		else return null;
	}
	/**
	 * @param tree
	 * @param location
	 * @param type
	 * @return the cir-nodes w.r.t. the location with specified type
	 * @throws Exception
	 */
	protected List<CirNode> get_cir_nodes(CirTree tree, AstNode location, Class<?> type) throws Exception {
		return tree.get_cir_nodes(location, type);
	}
	/**
	 * @param tree
	 * @param location
	 * @return the use point of the result of the expression at location.
	 * @throws Exception
	 */
	protected CirExpression find_result(CirTree tree, AstNode location) throws Exception {
		AstCirPair range = this.get_cir_range(tree, location);
		if(range == null || !range.computational()) {
			return null;	/* not available to represent value */
		}
		else if(location instanceof AstExpression) {
			if(location instanceof AstIdExpression
				|| location instanceof AstConstant
				|| location instanceof AstLiteral
				|| location instanceof AstArrayExpression
				|| location instanceof AstCastExpression
				|| location instanceof AstFieldExpression
				|| location instanceof AstSizeofExpression
				|| location instanceof AstInitializerBody
				|| location instanceof AstConditionalExpression) {
				return range.get_result();
			}
			else if(location instanceof AstConstExpression) {
				return this.find_result(tree, 
						((AstConstExpression) location).get_expression());
			}
			else if(location instanceof AstParanthExpression) {
				return this.find_result(tree, 
						((AstParanthExpression) location).get_sub_expression());
			}
			else if(location instanceof AstCommaExpression) {
				int index = ((AstCommaExpression) location).number_of_arguments();
				return this.find_result(tree, ((AstCommaExpression) location).get_expression(index - 1));
			}
			else if(location instanceof AstUnaryExpression) {
				switch(((AstUnaryExpression) location).get_operator().get_operator()) {
				case increment:
				case decrement:
				{
					CirAssignStatement statement = (CirAssignStatement) get_cir_nodes(
								tree, location, CirIncreAssignStatement.class).get(0);
					return statement.get_rvalue();
				}
				default:	return range.get_result();
				}
			}
			else if(location instanceof AstPostfixExpression) {
				switch(((AstPostfixExpression) location).get_operator().get_operator()) {
				case increment:
				case decrement:
				{
					CirAssignStatement statement = (CirAssignStatement) get_cir_nodes(
								tree, location, CirSaveAssignStatement.class).get(0);
					return statement.get_rvalue();
				}
				default: 	return range.get_result();
				}
			}
			else if(location instanceof AstBinaryExpression) {
				switch(((AstBinaryExpression) location).get_operator().get_operator()) {
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
					CirAssignStatement statement = (CirAssignStatement) get_cir_nodes(
									tree, location, CirAssignStatement.class).get(0);
					return statement.get_rvalue();
				}
				default:	return range.get_result();
				}
			}
			else if(location instanceof AstFunCallExpression) {
				CirWaitAssignStatement statement = (CirWaitAssignStatement) get_cir_nodes(
									tree, location, CirWaitAssignStatement.class).get(0);
				return statement.get_rvalue();
			}
			else {
				throw new RuntimeException("Unsupport: " + location);
			}
		}
		else {
			return range.get_result();
		}
	}
	/**
	 * @param tree
	 * @param location
	 * @return the first statement being executed for the location to be started
	 * @throws Exception
	 */
	private CirStatement get_beg_stmt(CirTree tree, AstNode location) throws Exception {
		AstCirPair range = this.get_cir_range(tree, location);
		if(range == null || !range.executional()) {
			return null;	/* not available to locate the first statement */
		}
		else if(location instanceof AstExpression) {
			if(location instanceof AstIdExpression
				|| location instanceof AstConstant
				|| location instanceof AstLiteral
				|| location instanceof AstArrayExpression
				|| location instanceof AstCastExpression
				|| location instanceof AstFieldExpression
				|| location instanceof AstInitializerBody
				|| location instanceof AstSizeofExpression) {
				return range.get_result().statement_of();
			}
			else if(location instanceof AstUnaryExpression) {
				switch(((AstUnaryExpression) location).get_operator().get_operator()) {
				case increment:
				case decrement:
							return (CirStatement) this.get_cir_nodes(tree, 
									location, CirIncreAssignStatement.class).get(0);
				default: 	return range.get_result().statement_of();
				}
			}
			else if(location instanceof AstPostfixExpression) {
				switch(((AstUnaryExpression) location).get_operator().get_operator()) {
				case increment:
				case decrement:
							return (CirStatement) this.get_cir_nodes(tree, 
									location, CirSaveAssignStatement.class).get(0);
				default: 	return range.get_result().statement_of();
				}
			}
			else if(location instanceof AstBinaryExpression) {
				switch(((AstBinaryExpression) location).get_operator().get_operator()) {
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
					return 	(CirStatement) this.get_cir_nodes(tree, 
							location, CirBinAssignStatement.class).get(0);
				default:	return range.get_result().statement_of();
				}
			}
			else if(location instanceof AstConditionalExpression) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirIfStatement.class).get(0);
			}
			else if(location instanceof AstFunCallExpression) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirCallStatement.class).get(0);
			}
			else {
				return range.get_beg_statement();
			}
		}
		else if(location instanceof AstStatement) {
			if(location instanceof AstBreakStatement
				|| location instanceof AstGotoStatement
				|| location instanceof AstContinueStatement) {
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
						location, CirLabelStatement.class).get(0);
			}
			else if(location instanceof AstCaseStatement) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirCaseStatement.class).get(0);
			}
			else if(location instanceof AstDefaultStatement) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirDefaultStatement.class).get(0);
			}
			else if(location instanceof AstIfStatement) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirIfStatement.class).get(0);
			}
			else if(location instanceof AstSwitchStatement) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirSaveAssignStatement.class).get(0);
			}
			else if(location instanceof AstWhileStatement
					|| location instanceof AstForStatement) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirIfStatement.class).get(0);
			}
			else {
				return range.get_beg_statement();
			}
		}
		else if(location instanceof AstStatementList) {
			CirStatement statement = null;
			for(int k = 0; k < location.number_of_children(); k++) {
				statement = this.get_beg_stmt(tree, 
						((AstStatementList) location).get_statement(k));
				if(statement != null) {
					return statement;
				}
			}
			return null;
		}
		else if(location instanceof AstFunctionDefinition) {
			CirFunctionDefinition def = (CirFunctionDefinition) this.
					get_cir_nodes(tree, location, CirFunctionDefinition.class).get(0);
			CirFunction function = tree.get_function_call_graph().get_function(def);
			return function.get_flow_graph().get_entry().get_statement();
		}
		else {
			return range.get_beg_statement();
		}
	}
	/**
	 * @param tree
	 * @param location
	 * @return the final statement being executed for the location to be started
	 * @throws Exception
	 */
	private CirStatement get_end_stmt(CirTree tree, AstNode location) throws Exception {
		AstCirPair range = this.get_cir_range(tree, location);
		if(range == null || !range.executional()) {
			return null;	/* not available to locate the final statement */
		}
		else if(location instanceof AstExpression) {
			if(location instanceof AstIdExpression
				|| location instanceof AstConstant
				|| location instanceof AstLiteral
				|| location instanceof AstArrayExpression
				|| location instanceof AstCastExpression
				|| location instanceof AstFieldExpression
				|| location instanceof AstInitializerBody
				|| location instanceof AstSizeofExpression) {
				return range.get_result().statement_of();
			}
			else if(location instanceof AstUnaryExpression) {
				switch(((AstUnaryExpression) location).get_operator().get_operator()) {
				case increment:
				case decrement:
							return (CirStatement) this.get_cir_nodes(tree, 
									location, CirIncreAssignStatement.class).get(0);
				default: 	return range.get_result().statement_of();
				}
			}
			else if(location instanceof AstPostfixExpression) {
				switch(((AstUnaryExpression) location).get_operator().get_operator()) {
				case increment:
				case decrement:
							return (CirStatement) this.get_cir_nodes(tree, 
									location, CirIncreAssignStatement.class).get(0);
				default: 	return range.get_result().statement_of();
				}
			}
			else if(location instanceof AstBinaryExpression) {
				switch(((AstBinaryExpression) location).get_operator().get_operator()) {
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
					return 	(CirStatement) this.get_cir_nodes(tree, 
							location, CirBinAssignStatement.class).get(0);
				default:	return range.get_result().statement_of();
				}
			}
			else if(location instanceof AstConditionalExpression) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirIfEndStatement.class).get(0);
			}
			else if(location instanceof AstFunCallExpression) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirWaitAssignStatement.class).get(0);
			}
			else {
				return range.get_end_statement();
			}
		}
		else if(location instanceof AstStatement) {
			if(location instanceof AstBreakStatement
				|| location instanceof AstGotoStatement
				|| location instanceof AstContinueStatement
				|| location instanceof AstReturnStatement) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirGotoStatement.class).get(0);
			}
			else if(location instanceof AstLabeledStatement) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirLabelStatement.class).get(0);
			}
			else if(location instanceof AstCaseStatement) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirCaseStatement.class).get(0);
			}
			else if(location instanceof AstDefaultStatement) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirDefaultStatement.class).get(0);
			}
			else if(location instanceof AstIfStatement) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirIfEndStatement.class).get(0);
			}
			else if(location instanceof AstSwitchStatement) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirCaseEndStatement.class).get(0);
			}
			else if(location instanceof AstWhileStatement
					|| location instanceof AstForStatement
					|| location instanceof AstDoWhileStatement) {
				return (CirStatement) this.get_cir_nodes(tree, 
						location, CirIfEndStatement.class).get(0);
			}
			else {
				return range.get_end_statement();
			}
		}
		else if(location instanceof AstStatementList) {
			CirStatement statement = null, cur_statement;
			for(int k = 0; k < location.number_of_children(); k++) {
				cur_statement = this.get_end_stmt(tree, 
						((AstStatementList) location).get_statement(k));
				if(cur_statement != null) statement = cur_statement;
			}
			return statement;
		}
		else if(location instanceof AstFunctionDefinition) {
			CirFunctionDefinition def = (CirFunctionDefinition) this.
					get_cir_nodes(tree, location, CirFunctionDefinition.class).get(0);
			CirFunction function = tree.get_function_call_graph().get_function(def);
			return function.get_flow_graph().get_exit().get_statement();
		}
		else {
			return range.get_end_statement();
		}
	}
	/**
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstNode get_prev_location(AstNode location) throws Exception {
		AstNode parent = location.get_parent();
		if(parent == null) {
			return null;
		}
		else {
			for(int k = 0; k < parent.number_of_children(); k++) {
				if(parent.get_child(k) == location && k > 0) {
					return parent.get_child(k - 1);
				}
			}
			return null;
		}
	}
	/**
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstNode get_succ_location(AstNode location) throws Exception {
		AstNode parent = location.get_parent();
		if(parent == null) {
			return null;
		}
		else {
			for(int k = 0; k < parent.number_of_children(); k++) {
				if(parent.get_child(k) == location && k < parent.number_of_children() - 1) {
					return parent.get_child(k + 1);
				}
			}
			return null;
		}
	}
	/**
	 * @param tree
	 * @param location
	 * @return the statement just before the location is reached
	 * @throws Exception
	 */
	protected CirStatement find_beg_stmt(CirTree tree, AstNode location) throws Exception {
		CirStatement statement = null; AstNode cursor;
		while(location != null && statement == null) {
			cursor = location;
			do {
				statement = this.get_beg_stmt(tree, cursor);
				cursor = this.get_succ_location(cursor);
			} while(statement == null && cursor != null);
			
			if(statement == null) {
				cursor = location;
				do {
					statement = this.get_end_stmt(tree, cursor);
					cursor = this.get_prev_location(cursor);
				} while(statement == null && cursor != null);
			}
			
			if(statement == null) {
				location = location.get_parent();
			}
			else {
				break;
			}
		}
		return statement;
	}
	/**
	 * @param tree
	 * @param location
	 * @return
	 * @throws Exception
	 */
	protected CirStatement find_end_statement(CirTree tree, AstNode location) throws Exception {
		CirStatement statement = null; AstNode cursor;
		while(location != null && statement == null) {
			cursor = location;
			do {
				statement = this.get_end_stmt(tree, cursor);
				cursor = this.get_prev_location(cursor);
			} while(statement == null && cursor != null);
			
			if(statement == null) {
				cursor = location;
				do {
					statement = this.get_beg_stmt(tree, cursor);
					cursor = this.get_succ_location(cursor);
				} while(statement == null && cursor != null);
			}
			
			if(statement == null) {
				location = location.get_parent();
			}
			else {
				break;
			}
		}
		return statement;
	}
	
	/* implementation methods */
	/**
	 * @param tree
	 * @param mutation
	 * @param graph
	 * @return execute_on(faulty_statement, 1)
	 * @throws Exception
	 */
	protected SadVertex get_reach(CirTree tree, AstMutation mutation, SadGraph graph) throws Exception {
		CirStatement statement = this.find_beg_stmt(tree, mutation.get_location());
		if(statement != null) {
			return graph.get_vertex(SadFactory.assert_execution(statement, 1));
		}
		else {
			return null;
		}
	}
	/**
	 * @param tree cir-code from which the mutation is located
	 * @param mutation the source mutation being used to create infections
	 * @param reach_node the node for reaching the faulty statement.
	 * @throws Exception
	 */
	protected abstract void get_infect(CirTree tree, AstMutation mutation, SadVertex reach_node) throws Exception;
	/**
	 * @param tree
	 * @param mutation
	 * @return generate the infection structure w.r.t. the mutation in the cir-tree
	 * @throws Exception
	 */
	protected SadGraph infect(CirTree tree, AstMutation mutation) throws Exception {
		SadGraph graph = new SadGraph(tree);
		SadVertex reach_node = this.get_reach(tree, mutation, graph);
		if(reach_node != null) this.get_infect(tree, mutation, reach_node);
		return graph;
	}
	
	/* construction methods */
	/**
	 * @param expression
	 * @return standard condition translated from the expression
	 * @throws Exception
	 */
	protected SadExpression condition_of(CirExpression expression, boolean value) throws Exception {
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		SadExpression condition = (SadExpression) SadParser.cir_parse(expression);
		if(CTypeAnalyzer.is_boolean(type)) {
			if(value) {
				return condition;
			}
			else {
				return SadFactory.logic_not(CBasicTypeImpl.bool_type, condition);
			}
		}
		else if(CTypeAnalyzer.is_number(type) || CTypeAnalyzer.is_pointer(type)) {
			if(value) {
				return SadFactory.not_equals(CBasicTypeImpl.bool_type, condition, SadFactory.constant(0));
			}
			else {
				return SadFactory.equal_with(CBasicTypeImpl.bool_type, condition, SadFactory.constant(0));
			}
		}
		else {
			throw new IllegalArgumentException("Invalid type: " + type.generate_code());
		}
	}
	/**
	 * @param source
	 * @param state_error
	 * @param constraint
	 * @return
	 * @throws Exception
	 */
	protected SadRelation connect(SadVertex source, SadAssertion state_error, SadAssertion constraint) throws Exception {
		return source.link(constraint, source.get_graph().get_vertex(state_error));
	}
	/**
	 * @param source
	 * @param state_error
	 * @return 
	 * @throws Exception
	 */
	protected SadRelation connect(SadVertex source, SadAssertion state_error) throws Exception {
		return source.link(
				SadFactory.assert_condition(source.get_location(), SadFactory.constant(true)), 
				source.get_graph().get_vertex(state_error));
	}
	
	/* simplification methods */
	/**
	 * @param expression
	 * @return bool | char | int | long | float | double | CirExpression
	 * @throws Excecption
	 */
	protected SadExpression sad_expression(Object expression) throws Exception {
		if(expression instanceof Boolean) {
			return SadFactory.constant(((Boolean) expression).booleanValue());
		}
		else if(expression instanceof Character) {
			return SadFactory.constant(((Character) expression).charValue());
		}
		else if(expression instanceof Integer) {
			return SadFactory.constant(((Integer) expression).intValue());
		}
		else if(expression instanceof Long) {
			return SadFactory.constant(((Long) expression).longValue());
		}
		else if(expression instanceof Float) {
			return SadFactory.constant(((Float) expression).floatValue());
		}
		else if(expression instanceof Double) {
			return SadFactory.constant(((Double) expression).doubleValue());
		}
		else if(expression instanceof CirExpression) {
			return (SadExpression) SadParser.cir_parse((CirExpression) expression);
		}
		else if(expression instanceof SadExpression) {
			return (SadExpression) ((SadExpression) expression).clone();
		}
		else {
			throw new IllegalArgumentException("Invalid type: " + expression.getClass().getSimpleName());
		}
	}
	protected SadExpression arith_sub(CType type, Object loperand, Object roperand) throws Exception {
		return SadFactory.arith_sub(type, this.sad_expression(loperand), this.sad_expression(roperand));
	}
	protected SadExpression arith_div(CType type, Object loperand, Object roperand) throws Exception {
		return SadFactory.arith_div(type, this.sad_expression(loperand), this.sad_expression(roperand));
	}
	protected SadExpression arith_mod(CType type, Object loperand, Object roperand) throws Exception {
		return SadFactory.arith_mod(type, this.sad_expression(loperand), this.sad_expression(roperand));
	}
	protected SadExpression bitws_lsh(CType type, Object loperand, Object roperand) throws Exception {
		return SadFactory.bitws_lsh(type, this.sad_expression(loperand), this.sad_expression(roperand));
	}
	protected SadExpression bitws_rsh(CType type, Object loperand, Object roperand) throws Exception {
		return SadFactory.bitws_rsh(type, this.sad_expression(loperand), this.sad_expression(roperand));
	}
	protected SadExpression greater_tn(Object loperand, Object roperand) throws Exception {
		return SadFactory.greater_tn(CBasicTypeImpl.bool_type, this.sad_expression(loperand), this.sad_expression(roperand));
	}
	protected SadExpression greater_eq(Object loperand, Object roperand) throws Exception {
		return SadFactory.greater_eq(CBasicTypeImpl.bool_type, this.sad_expression(loperand), this.sad_expression(roperand));
	}
	protected SadExpression smaller_tn(Object loperand, Object roperand) throws Exception {
		return SadFactory.smaller_tn(CBasicTypeImpl.bool_type, this.sad_expression(loperand), this.sad_expression(roperand));
	}
	protected SadExpression smaller_eq(Object loperand, Object roperand) throws Exception {
		return SadFactory.smaller_eq(CBasicTypeImpl.bool_type, this.sad_expression(loperand), this.sad_expression(roperand));
	}
	protected SadExpression equal_with(Object loperand, Object roperand) throws Exception {
		return SadFactory.equal_with(CBasicTypeImpl.bool_type, this.sad_expression(loperand), this.sad_expression(roperand));
	}
	protected SadExpression not_equals(Object loperand, Object roperand) throws Exception {
		return SadFactory.not_equals(CBasicTypeImpl.bool_type, this.sad_expression(loperand), this.sad_expression(roperand));
	}
	protected SadExpression arith_add(CType type, Object loperand, Object roperand) throws Exception {
		List<SadExpression> operands = new ArrayList<SadExpression>();
		operands.add(this.sad_expression(loperand));
		operands.add(this.sad_expression(roperand));
		return SadFactory.arith_add(type, operands);
	}
	protected SadExpression arith_mul(CType type, Object loperand, Object roperand) throws Exception {
		List<SadExpression> operands = new ArrayList<SadExpression>();
		operands.add(this.sad_expression(loperand));
		operands.add(this.sad_expression(roperand));
		return SadFactory.arith_mul(type, operands);
	}
	protected SadExpression bitws_and(CType type, Object loperand, Object roperand) throws Exception {
		List<SadExpression> operands = new ArrayList<SadExpression>();
		operands.add(this.sad_expression(loperand));
		operands.add(this.sad_expression(roperand));
		return SadFactory.bitws_and(type, operands);
	}
	protected SadExpression bitws_ior(CType type, Object loperand, Object roperand) throws Exception {
		List<SadExpression> operands = new ArrayList<SadExpression>();
		operands.add(this.sad_expression(loperand));
		operands.add(this.sad_expression(roperand));
		return SadFactory.bitws_ior(type, operands);
	}
	protected SadExpression bitws_xor(CType type, Object loperand, Object roperand) throws Exception {
		List<SadExpression> operands = new ArrayList<SadExpression>();
		operands.add(this.sad_expression(loperand));
		operands.add(this.sad_expression(roperand));
		return SadFactory.bitws_xor(type, operands);
	}
	protected SadExpression logic_and(Object loperand, Object roperand) throws Exception {
		List<SadExpression> operands = new ArrayList<SadExpression>();
		operands.add(this.sad_expression(loperand));
		operands.add(this.sad_expression(roperand));
		return SadFactory.logic_and(CBasicTypeImpl.bool_type, operands);
	}
	protected SadExpression logic_ior(Object loperand, Object roperand) throws Exception {
		List<SadExpression> operands = new ArrayList<SadExpression>();
		operands.add(this.sad_expression(loperand));
		operands.add(this.sad_expression(roperand));
		return SadFactory.logic_ior(CBasicTypeImpl.bool_type, operands);
	}
	protected SadExpression any_value(CType data_type) throws Exception {
		return SadFactory.id_expression(data_type, "$ANY");
	}
	protected SadExpression any_pos_value(CType data_type) throws Exception {
		return SadFactory.id_expression(data_type, "$POS");
	}
	protected SadExpression any_neg_value(CType data_type) throws Exception {
		return SadFactory.id_expression(data_type, "$NEG");
	}
	
}
