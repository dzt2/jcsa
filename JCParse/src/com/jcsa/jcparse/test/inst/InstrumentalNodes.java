package com.jcsa.jcparse.test.inst;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclarator;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstBasicExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArgumentList;
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
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstGotoStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirArithExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirLogicExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirBinAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirDefaultStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirInitAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabelStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirReturnAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirSaveAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * It is used to generate instrumental nodes sequence for analysis on CIR code.
 *
 * @author yukimula
 *
 */
public class InstrumentalNodes {

	/* definitions */
	/** C-intermediate representation program **/
	private CirTree cir_tree;
	/** mapping from expression to its value **/
	private Map<CirExpression, Object> buffer;
	/** the sequence of instrumental nodes being generated **/
	private List<InstrumentalNode> nodes;

	/* singleton constructor */
	private InstrumentalNodes() { }
	private static final InstrumentalNodes parser = new InstrumentalNodes();

	/* cir-location algorithms */
	/**
	 * @param location
	 * @return the executional node of the statement where the location belongs to
	 * @throws Exception
	 */
	private CirExecution get_cir_execution(CirNode location) throws Exception {
		while(location != null) {
			if(location instanceof CirStatement) {
				CirStatement statement = (CirStatement) location;
				return statement.get_tree().get_localizer().get_execution(statement);
			}
			else {
				location = location.get_parent();
			}
		}
		return null;
	}
	/**
	 * @param location
	 * @param type
	 * @return the set of cir-nodes w.r.t. the location with specified types
	 * @throws Exception
	 */
	private List<CirNode> get_cir_nodes(AstNode location, Class<?> type) throws Exception {
		return this.cir_tree.get_cir_nodes(location, type);
	}
	/**
	 * @param expression
	 * @return whether the expression is a left-reference
	 */
	private boolean is_left_reference(CirExpression expression) {
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
	/**
	 * collect all the expressions under the location
	 * @param location
	 * @param expressions
	 */
	private void collect_expressions_in(CirNode location, Set<CirExpression> expressions) {
		for(CirNode child : location.get_children()) {
			this.collect_expressions_in(child, expressions);
		}
		if(location instanceof CirExpression) {
			CirExpression expression = (CirExpression) location;
			if(!this.is_left_reference(expression)) {
				expressions.add(expression);
			}
		}
	}

	/* buffer operation methods */
	/**
	 * record the expression and its value in the buffer
	 * @param expression
	 * @param value
	 */
	private void put_expression(CirExpression expression, Object value) {
		if(expression == null || expression.statement_of() == null)
			return;
		else if(!this.is_left_reference(expression))	/* ignore lvalue */
			this.buffer.put(expression, value);
	}
	/**
	 * record the expressions w.r.t. the location and their values in buffer
	 * @param location
	 * @param value
	 * @throws Exception
	 */
	private void put_expressions(AstNode location, Object value) throws Exception {
		List<CirNode> expressions = this.get_cir_nodes(location, CirExpression.class);
		for(CirNode expression : expressions) {
			this.put_expression((CirExpression) expression, value);
		}
	}
	/**
	 * create a new node w.r.t. the statement, of which expressions are
	 * constructed with values and initialized into the node created.
	 * (1) create a node w.r.t. the statement and add it to the list;
	 * (2) collect all the expressions under the statement;
	 * (3) set the value of these expressions using buffer;
	 * (4) remove expression-value pairs used from buffer;
	 * @param statement
	 * @throws Exception
	 */
	private void append(CirStatement statement) throws Exception {
		CirExecution execution = this.get_cir_execution(statement);
		InstrumentalNode node = new InstrumentalNode(execution);

		Set<CirExpression> expressions = new HashSet<>();
		this.collect_expressions_in(statement, expressions);

		for(CirExpression expression : expressions) {
			if(this.buffer.containsKey(expression)) {
				Object value = this.buffer.get(expression);
				if(value != null) {
					node.set_unit(expression, value);
				}
				this.buffer.remove(expression);
			}
		}

		/* link the path between it and last one */
		if(!this.nodes.isEmpty()) {
			InstrumentalNode source = this.nodes.get(nodes.size() - 1);
			InstrumentalNode target = node;
			List<CirExecution> path = this.complete_path_between(source, target);
			for(CirExecution curr_execution : path) {
				InstrumentalNode curr = new InstrumentalNode(curr_execution);
				this.nodes.add(curr);
			}
		}
		this.nodes.add(node);
	}

	/* context-sensitive translation */
	private void parse_follow(AstExpression location) throws Exception {
		AstNode child = location;
		AstNode parent = location.get_parent();
		while(parent != null) {
			if(parent instanceof AstParanthExpression
				|| parent instanceof AstConstExpression
				|| parent instanceof AstInitializer) {
				child = parent;
				parent = parent.get_parent();
			}
			else {
				break;
			}
		}

		if(parent instanceof AstFunCallExpression) {
			if(!((AstFunCallExpression) parent).has_argument_list()) {
				if(((AstFunCallExpression) parent).get_function() == child) {
					CirStatement statement = (CirStatement) this.
							get_cir_nodes(parent, CirCallStatement.class).get(0);
					this.append(statement);
				}
			}
		}
		else if(parent instanceof AstLogicBinaryExpression) {
			if(((AstLogicBinaryExpression) parent).get_loperand() == child) {
				CirAssignStatement statement1 = (CirAssignStatement) this.
						get_cir_nodes(parent, CirSaveAssignStatement.class).get(0);
				Object condition_value = this.buffer.get(statement1.get_rvalue());
				this.append(statement1);

				CirIfStatement statement2 = (CirIfStatement) this.
						get_cir_nodes(location, CirIfStatement.class).get(0);
				if(((AstLogicBinaryExpression) parent).get_operator().get_operator() == COperator.logic_and) {
					this.put_expression(statement2.get_condition(), condition_value);
				}
				else {
					CirLogicExpression condition = (CirLogicExpression) statement2.get_condition();
					this.put_expression(condition.get_operand(0), condition_value);
					if(condition_value != null) {
						if(condition_value instanceof Boolean)
							condition_value = Boolean.valueOf(!((Boolean) condition_value).booleanValue());
						else if(condition_value instanceof Character)
							condition_value = Boolean.valueOf(((Character) condition_value).charValue() == 0);
						else if(condition_value instanceof Short)
							condition_value = Boolean.valueOf(((Short) condition_value).shortValue() == 0);
						else if(condition_value instanceof Integer)
							condition_value = Boolean.valueOf(((Integer) condition_value).intValue() == 0);
						else if(condition_value instanceof Long)
							condition_value = Boolean.valueOf(((Long) condition_value).longValue() == 0);
						else if(condition_value instanceof Float)
							condition_value = Boolean.valueOf(((Float) condition_value).floatValue() == 0);
						else if(condition_value instanceof Double)
							condition_value = Boolean.valueOf(((Double) condition_value).doubleValue() == 0);
						else
							throw new IllegalArgumentException(condition_value.getClass().getSimpleName());
					}
					this.put_expression(condition, condition_value);
				}
				this.append(statement2);
			}
			else if(((AstLogicBinaryExpression) parent).get_roperand() == child) {
				CirAssignStatement statement = (CirAssignStatement) this.
						get_cir_nodes(location, CirSaveAssignStatement.class).get(1);
				this.append(statement);
			}
		}
		else if(parent instanceof AstConditionalExpression) {
			if(((AstConditionalExpression) parent).get_condition() == child) {
				CirStatement statement = (CirStatement) this.
						get_cir_nodes(location, CirIfStatement.class).get(0);
				this.append(statement);
			}
			else if(((AstConditionalExpression) parent).get_true_branch() == child) {
				CirStatement statement = (CirStatement) this.
						get_cir_nodes(location, CirSaveAssignStatement.class).get(0);
				this.append(statement);
			}
			else if(((AstConditionalExpression) parent).get_false_branch() == child) {
				CirStatement statement = (CirStatement) this.
						get_cir_nodes(location, CirSaveAssignStatement.class).get(1);
				this.append(statement);
			}
		}
		else if(parent instanceof AstIfStatement) {
			if(((AstIfStatement) parent).get_condition() == child) {
				CirStatement statement = (CirStatement) this.
						get_cir_nodes(parent, CirIfStatement.class).get(0);
				this.append(statement);
			}
		}
		else if(parent instanceof AstSwitchStatement) {
			if(((AstSwitchStatement) parent).get_condition() == child) {
				CirStatement statement = (CirStatement) this.
						get_cir_nodes(parent, CirSaveAssignStatement.class).get(0);
				this.append(statement);
			}
		}
		else if(parent instanceof AstWhileStatement) {
			if(((AstWhileStatement) parent).get_condition() == child) {
				CirStatement statement = (CirStatement) this.
						get_cir_nodes(parent, CirIfStatement.class).get(0);
				this.append(statement);
			}
		}
		else if(parent instanceof AstDoWhileStatement) {
			if(((AstDoWhileStatement) parent).get_condition() == child) {
				CirStatement statement = (CirStatement) this.
						get_cir_nodes(parent, CirIfStatement.class).get(0);
				this.append(statement);
			}
		}
	}

	/* basic expression */
	private void parse_id_expression(InstrumentalLine line, AstIdExpression location) throws Exception {
		if(line.is_end()) {
			this.put_expressions(location, line.get_value());
		}
	}
	private void parse_constant(InstrumentalLine line, AstConstant location) throws Exception {
		if(line.is_end()) {
			Object value = line.get_value();
			if(value == null) {
				value = location.get_constant().get_object();
			}
			this.put_expressions(location, value);
		}
	}
	private void parse_literal(InstrumentalLine line, AstLiteral location) throws Exception {
		if(line.is_end()) {
			this.put_expressions(location, location.get_literal());
		}
	}
	private void parse_basic_expression(InstrumentalLine line, AstBasicExpression location) throws Exception {
		if(location instanceof AstIdExpression)
			this.parse_id_expression(line, (AstIdExpression) location);
		else if(location instanceof AstConstant)
			this.parse_constant(line, (AstConstant) location);
		else if(location instanceof AstLiteral)
			this.parse_literal(line, (AstLiteral) location);
		else
			throw new IllegalArgumentException(location.toString());
	}
	private void parse_unary_expression(InstrumentalLine line, AstUnaryExpression location) throws Exception {
		COperator operator = location.get_operator().get_operator();
		if(line.is_end()) {
			switch(operator) {
			case increment:
			case decrement:
			{
				CirAssignStatement statement = (CirAssignStatement) this.
						get_cir_nodes(location, CirIncreAssignStatement.class).get(0);
				CirArithExpression expression = (CirArithExpression) statement.get_rvalue();
				CirExpression roperand = expression.get_operand(1);
				this.put_expression(expression, line.get_value());
				this.put_expression(roperand, Integer.valueOf(1));

				CirExpression loperand = expression.get_operand(0);
				Object value = line.get_value();
				int increment = (expression.get_operator() == COperator.arith_add) ? -1 : 1;
				if(value != null) {
					if(value instanceof Character) {
						value = Long.valueOf(((Character) value).charValue() + increment);
					}
					else if(value instanceof Short) {
						value = Long.valueOf(((Short) value).shortValue() + increment);
					}
					else if(value instanceof Integer) {
						value = Long.valueOf(((Integer) value).intValue() + increment);
					}
					else if(value instanceof Long) {
						value = Long.valueOf(((Long) value).longValue() + increment);
					}
					else if(value instanceof Float) {
						value = Double.valueOf(((Float) value).floatValue() + increment);
					}
					else if(value instanceof Double) {
						value = Double.valueOf(((Double) value).doubleValue() + increment);
					}
					else {
						throw new IllegalArgumentException(value.getClass().getSimpleName());
					}
				}
				this.put_expression(loperand, value);
				this.append(statement);
			}
			default: this.put_expressions(location, line.get_value()); break;
			}
		}
	}
	private void parse_postfix_expression(InstrumentalLine line, AstPostfixExpression location) throws Exception {
		if(line.is_end()) {
			this.put_expressions(location, line.get_value());

			CirAssignStatement sav_statement = (CirAssignStatement) this.
					get_cir_nodes(location, CirSaveAssignStatement.class).get(0);
			this.put_expression(sav_statement.get_rvalue(), line.get_value());
			this.append(sav_statement);

			CirAssignStatement inc_statement = (CirAssignStatement) this.
					get_cir_nodes(location, CirIncreAssignStatement.class).get(0);
			CirArithExpression expression = (CirArithExpression) inc_statement.get_rvalue();
			CirExpression loperand = expression.get_operand(0);
			CirExpression roperand = expression.get_operand(1);
			this.put_expression(loperand, line.get_value());
			this.put_expression(roperand, Integer.valueOf(1));

			Object value = line.get_value();
			int increment = (expression.get_operator() == COperator.arith_add) ? 1 : -1;
			if(value != null) {
				if(value instanceof Character) {
					value = Long.valueOf(((Character) value).charValue() + increment);
				}
				else if(value instanceof Short) {
					value = Long.valueOf(((Short) value).shortValue() + increment);
				}
				else if(value instanceof Integer) {
					value = Long.valueOf(((Integer) value).intValue() + increment);
				}
				else if(value instanceof Long) {
					value = Long.valueOf(((Long) value).longValue() + increment);
				}
				else if(value instanceof Float) {
					value = Double.valueOf(((Float) value).floatValue() + increment);
				}
				else if(value instanceof Double) {
					value = Double.valueOf(((Double) value).doubleValue() + increment);
				}
				else {
					throw new IllegalArgumentException(value.getClass().getSimpleName());
				}
			}
			this.put_expression(expression, value);
			this.append(inc_statement);
		}
	}
	private boolean is_assign_expression(AstBinaryExpression location) throws Exception {
		return location instanceof AstAssignExpression
				|| location instanceof AstArithAssignExpression
				|| location instanceof AstBitwiseAssignExpression
				|| location instanceof AstShiftAssignExpression;
	}
	private void parse_binary_expression(InstrumentalLine line, AstBinaryExpression location) throws Exception {
		if(line.is_end()) {
			if(this.is_assign_expression(location)) {
				CirStatement statement = (CirStatement) this.
						get_cir_nodes(location, CirBinAssignStatement.class).get(0);
				this.append(statement);
			}
			else if(location instanceof AstLogicBinaryExpression) {
				this.put_expressions(location, line.get_value());
				CirStatement statement = (CirStatement) this.
						get_cir_nodes(location, CirIfEndStatement.class).get(0);
				this.append(statement);
			}
			else {
				this.put_expressions(location, line.get_value());
			}
		}
	}

	/* special expression */
	private void parse_array_expression(InstrumentalLine line, AstArrayExpression location) throws Exception {
		if(line.is_end()) {
			this.put_expressions(location, line.get_value());
		}
	}
	private void parse_cast_expression(InstrumentalLine line, AstCastExpression location) throws Exception {
		if(line.is_end()) {
			this.put_expressions(location, line.get_value());
		}
	}
	private void parse_comma_expression(InstrumentalLine line, AstCommaExpression location) throws Exception {
		if(line.is_end()) {
			this.put_expressions(location, line.get_value());
		}
	}
	private void parse_field_expression(InstrumentalLine line, AstFieldExpression location) throws Exception {
		if(line.is_end()) {
			this.put_expressions(location, line.get_value());
		}
	}
	private void parse_sizeof_expression(InstrumentalLine line, AstSizeofExpression location) throws Exception {
		if(line.is_end()) {
			this.put_expressions(location, line.get_value());
		}
	}
	private void parse_initializer_body(InstrumentalLine line, AstInitializerBody location) throws Exception {}
	private void parse_conditional_expression(InstrumentalLine line, AstConditionalExpression location) throws Exception {
		if(line.is_end()) {
			this.put_expressions(location, line.get_value());
			CirStatement statement = (CirStatement) this.
					get_cir_nodes(location, CirIfEndStatement.class).get(0);
			this.append(statement);
		}
	}
	private void parse_fun_call_expression(InstrumentalLine line, AstFunCallExpression location) throws Exception {
		if(line.is_end()) {
			this.put_expressions(location, line.get_value());
			CirStatement statement = (CirStatement) this.get_cir_nodes(
						location, CirWaitAssignStatement.class).get(0);
			this.append(statement);
		}
	}

	/* expression */
	private void parse_expression(InstrumentalLine line, AstExpression location) throws Exception {
		if(location instanceof AstBasicExpression)
			this.parse_basic_expression(line, (AstBasicExpression) location);
		else if(location instanceof AstUnaryExpression)
			this.parse_unary_expression(line, (AstUnaryExpression) location);
		else if(location instanceof AstPostfixExpression)
			this.parse_postfix_expression(line, (AstPostfixExpression) location);
		else if(location instanceof AstBinaryExpression)
			this.parse_binary_expression(line, (AstBinaryExpression) location);
		else if(location instanceof AstArrayExpression)
			this.parse_array_expression(line, (AstArrayExpression) location);
		else if(location instanceof AstCastExpression)
			this.parse_cast_expression(line, (AstCastExpression) location);
		else if(location instanceof AstCommaExpression)
			this.parse_comma_expression(line, (AstCommaExpression) location);
		else if(location instanceof AstFieldExpression)
			this.parse_field_expression(line, (AstFieldExpression) location);
		else if(location instanceof AstSizeofExpression)
			this.parse_sizeof_expression(line, (AstSizeofExpression) location);
		else if(location instanceof AstInitializerBody)
			this.parse_initializer_body(line, (AstInitializerBody) location);
		else if(location instanceof AstConditionalExpression)
			this.parse_conditional_expression(line, (AstConditionalExpression) location);
		else if(location instanceof AstFunCallExpression)
			this.parse_fun_call_expression(line, (AstFunCallExpression) location);
		else
			throw new IllegalArgumentException("Unsupport: " + location);

		if(line.is_end())
			this.parse_follow(location);
	}
	private void parse_argument_list(InstrumentalLine line, AstArgumentList location) throws Exception {
		if(line.is_end()) {
			CirStatement statement = (CirStatement) this.get_cir_nodes(
					location.get_parent(), CirCallStatement.class).get(0);
			this.append(statement);
		}
	}

	/* declaration part */
	private void parse_declarator(InstrumentalLine line, AstDeclarator location) throws Exception {}
	private void parse_init_declarator(InstrumentalLine line, AstInitDeclarator location) throws Exception {
		if(line.is_end()) {
			CirStatement statement = (CirStatement) this.get_cir_nodes(
						location, CirInitAssignStatement.class).get(0);
			this.append(statement);
		}
	}

	/* statement part */
	private void parse_expression_statement(InstrumentalLine line, AstExpressionStatement location) throws Exception {
		if(line.is_end()) {
			if(location.get_parent() instanceof AstForStatement) {
				AstForStatement parent = (AstForStatement) location.get_parent();
				if(parent.get_condition() == location) {
					CirStatement statement = (CirStatement) this.
							get_cir_nodes(parent, CirIfStatement.class).get(0);
					this.append(statement);
				}
			}
		}
	}
	private void parse_goto_statement(InstrumentalLine line, AstGotoStatement location) throws Exception {
		if(line.is_beg()) {
			CirStatement statement = (CirStatement) this.get_cir_nodes(
					location, CirGotoStatement.class).get(0);
			this.append(statement);
		}
	}
	private void parse_break_statement(InstrumentalLine line,
				AstBreakStatement location) throws Exception {
		if(line.is_beg()) {
			CirStatement statement = (CirStatement) this.get_cir_nodes(
					location, CirGotoStatement.class).get(0);
			this.append(statement);
		}
	}
	private void parse_continue_statement(InstrumentalLine line,
			AstContinueStatement location) throws Exception {
		if(line.is_beg()) {
			CirStatement statement = (CirStatement) this.get_cir_nodes(
					location, CirGotoStatement.class).get(0);
			this.append(statement);
		}
	}
	private void parse_return_statement(InstrumentalLine line,
			AstReturnStatement location) throws Exception {
		if(line.is_end()) {
			CirStatement statement;
			if(location.has_expression()) {
				statement = (CirStatement) this.get_cir_nodes(location,
						CirReturnAssignStatement.class).get(0);
				this.append(statement);
			}
			statement = (CirStatement) this.get_cir_nodes(
					location, CirGotoStatement.class).get(0);
			this.append(statement);
		}
	}
	private void parse_labeled_statement(InstrumentalLine line,
			AstLabeledStatement location) throws Exception {
		if(line.is_end()) {
			CirStatement statement = (CirStatement) this.get_cir_nodes(
					location, CirLabelStatement.class).get(0);
			this.append(statement);
		}
	}
	private void parse_case_statement(InstrumentalLine line,
			AstCaseStatement location) throws Exception {
		if(line.is_end()) {
			CirStatement statement = (CirStatement) this.get_cir_nodes(
					location, CirCaseStatement.class).get(0);
			this.append(statement);
		}
	}
	private void parse_default_statement(InstrumentalLine line,
			AstDefaultStatement location) throws Exception {
		if(line.is_end()) {
			CirStatement statement = (CirStatement) this.
					get_cir_nodes(location, CirDefaultStatement.class).get(0);
			this.append(statement);
		}
	}
	private void parse_statement(InstrumentalLine line, AstStatement location) throws Exception {
		if(location instanceof AstExpressionStatement)
			this.parse_expression_statement(line, (AstExpressionStatement) location);
		else if(location instanceof AstGotoStatement)
			this.parse_goto_statement(line, (AstGotoStatement) location);
		else if(location instanceof AstBreakStatement)
			this.parse_break_statement(line, (AstBreakStatement) location);
		else if(location instanceof AstContinueStatement)
			this.parse_continue_statement(line, (AstContinueStatement) location);
		else if(location instanceof AstReturnStatement)
			this.parse_return_statement(line, (AstReturnStatement) location);
		else if(location instanceof AstLabeledStatement)
			this.parse_labeled_statement(line, (AstLabeledStatement) location);
		else if(location instanceof AstCaseStatement)
			this.parse_case_statement(line, (AstCaseStatement) location);
		else if(location instanceof AstDefaultStatement)
			this.parse_default_statement(line, (AstDefaultStatement) location);
	}

	/* function part */
	private void parse_function_definition(InstrumentalLine line,
			AstFunctionDefinition location) throws Exception {
		CirFunctionDefinition def = (CirFunctionDefinition) this.
				get_cir_nodes(location, CirFunctionDefinition.class).get(0);
		CirFunction function = this.cir_tree.get_function_call_graph().get_function(def);
		if(line.is_beg()) {
			this.append(function.get_flow_graph().get_entry().get_statement());
		}
		else {
			this.append(function.get_flow_graph().get_exit().get_statement());
		}
	}

	/* parsing methods */
	private void parse(InstrumentalLine line) throws Exception {
		AstNode location = line.get_location();
		if(location instanceof AstExpression)
			this.parse_expression(line, (AstExpression) location);
		else if(location instanceof AstStatement)
			this.parse_statement(line, (AstStatement) location);
		else if(location instanceof AstArgumentList)
			this.parse_argument_list(line, (AstArgumentList) location);
		else if(location instanceof AstDeclarator)
			this.parse_declarator(line, (AstDeclarator) location);
		else if(location instanceof AstInitDeclarator)
			this.parse_init_declarator(line, (AstInitDeclarator) location);
		else if(location instanceof AstFunctionDefinition)
			this.parse_function_definition(line, (AstFunctionDefinition) location);
		else
			return;
	}
	public static List<InstrumentalNode> get_nodes(CRunTemplate template,
			AstTree ast_tree, CirTree cir_tree, File instrumental_file) throws Exception {
		if(template == null)
			throw new IllegalArgumentException("Invalid template: null");
		else if(ast_tree == null)
			throw new IllegalArgumentException("Invalid ast_tree: null");
		else if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree: null");
		else if(instrumental_file == null || !instrumental_file.exists())
			throw new IllegalArgumentException("Invalid instrumental file");
		else {
			List<InstrumentalLine> lines = InstrumentalLines.
					complete_lines(template, ast_tree, instrumental_file);

			parser.cir_tree = cir_tree;
			parser.buffer = new HashMap<>();
			parser.nodes = new ArrayList<>();

			for(InstrumentalLine line : lines) {
				parser.parse(line);
			}

			parser.buffer.clear();
			return parser.nodes;
		}
	}

	/* complete nodes in path */
	private List<CirExecution> complete_path_between(
			InstrumentalNode source_node,
			InstrumentalNode target_node) throws Exception {
		List<CirExecution> path = new ArrayList<>();
		CirExecution source_execution = source_node.get_execution();
		CirExecution target_execution = target_node.get_execution();
		CirExecution curr_execution = source_execution;
		CirStatement target_statement = target_execution.get_statement();

		while(curr_execution != target_execution) {
			if(curr_execution != source_execution && curr_execution != target_execution)
				path.add(curr_execution);

			/* determine the next node being executed from curr_execution */
			CirStatement statement = curr_execution.get_statement();
			if(statement instanceof CirAssignStatement || statement instanceof CirGotoStatement) {
				curr_execution = curr_execution.get_ou_flow(0).get_target();
			}
			else if(statement instanceof CirCallStatement) {
				if(curr_execution.get_ou_flow(0).get_type() == CirExecutionFlowType.call_flow) {
					curr_execution = curr_execution.get_ou_flow(0).get_target();
				}
				else {
					curr_execution = target_execution;	/* annex to the target */
				}
			}
			else if(statement instanceof CirIfStatement) {
				InstrumentalUnit condition = source_node.get_unit(((CirIfStatement) statement).get_condition());
				if(condition != null) {
					if(condition.get_bool()) {
						for(CirExecutionFlow flow : curr_execution.get_ou_flows()) {
							if(flow.get_type() == CirExecutionFlowType.true_flow) {
								curr_execution = flow.get_target();
								break;
							}
						}
					}
					else {
						for(CirExecutionFlow flow : curr_execution.get_ou_flows()) {
							if(flow.get_type() == CirExecutionFlowType.fals_flow) {
								curr_execution = flow.get_target();
								break;
							}
						}
					}
				}
				else {
					curr_execution = target_execution;	/* annex to the target */
				}
			}
			else if(statement instanceof CirCaseStatement) {
				if(statement == target_statement) {
					curr_execution = target_execution;	/* annex to the target */
				}
				else {
					for(CirExecutionFlow flow : curr_execution.get_ou_flows()) {
						if(flow.get_type() == CirExecutionFlowType.fals_flow) {
							curr_execution = flow.get_target();
							break;
						}
					}
				}
			}
			else if(statement instanceof CirEndStatement) {
				curr_execution = target_execution;	/* annex to the target */
			}
			else {
				curr_execution = curr_execution.get_ou_flow(0).get_target();
			}
		}

		return path;
	}

}
