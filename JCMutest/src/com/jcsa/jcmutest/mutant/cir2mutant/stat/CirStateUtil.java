package com.jcsa.jcmutest.mutant.cir2mutant.stat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirBlockError;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirCoverCount;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirDiferError;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirFlowsError;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirReferError;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirTrapsError;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirValueError;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirInitializerBody;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPathFinder;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolNode;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;
import com.jcsa.jcparse.test.state.CStateNode;
import com.jcsa.jcparse.test.state.CStatePath;

/**
 * It implements the construction of state tree.
 * 
 * @author yukimula
 *
 */
public class CirStateUtil {
	
	/* singleton */	/** constructor **/	private CirStateUtil() {}
	private static final CirStateUtil utils = new CirStateUtil();
	private SymbolExpression evaluate(SymbolExpression expression, SymbolProcess context) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else {
			try {
				return expression.evaluate(context);
			}
			catch(ArithmeticException ex) {
				return CirValueScope.except_value;
			}
		}
	}
	
	/* condition reconstruction methods */
	/**
	 * It recursively collects the sub_conditions in the expression when taken
	 * it as the logical-and conjunction(s).
	 * @param expression
	 * @param conditions
	 * @throws Exception
	 */
	private void div_conditions_in_conjunct(SymbolExpression expression, 
			Collection<SymbolExpression> conditions) throws Exception {
		if(expression instanceof SymbolConstant) {
			if(((SymbolConstant) expression).get_bool()) {/* ignore true */}
			else {
				conditions.add(SymbolFactory.sym_constant(Boolean.FALSE));
			}
		}
		else if(expression instanceof SymbolBinaryExpression) {
			COperator op = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			if(op == COperator.logic_and) {
				this.div_conditions_in_conjunct(loperand, conditions);
				this.div_conditions_in_conjunct(roperand, conditions);
			}
			else {
				conditions.add(SymbolFactory.sym_condition(expression, true));
			}
		}
		else {
			conditions.add(SymbolFactory.sym_condition(expression, true));
		}
	}
	/**
	 * It recursively divides the sub_conditions in the expression when taken 
	 * it as the disjunctive connections.
	 * @param expression
	 * @param conditions
	 * @throws Exception
	 */
	private void div_conditions_in_disjunct(SymbolExpression expression,
			Collection<SymbolExpression> conditions) throws Exception {
		if(expression instanceof SymbolConstant) {
			if(((SymbolConstant) expression).get_bool()) {
				conditions.add(SymbolFactory.sym_constant(Boolean.TRUE));
			}
			else { /* ignore the false operand in disjunctive */ }
		}
		else if(expression instanceof SymbolBinaryExpression) {
			COperator op = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			if(op == COperator.logic_or) {
				this.div_conditions_in_disjunct(loperand, conditions);
				this.div_conditions_in_disjunct(roperand, conditions);
			}
			else {
				conditions.add(SymbolFactory.sym_condition(expression, true));
			}
		}
		else {
			conditions.add(SymbolFactory.sym_condition(expression, true));
		}
	}
	/**
	 * It generates the subsumed conditions from the input condition statically.
	 * @param condition
	 * @param subsumed_conditions
	 * @throws Exception
	 */
	private void gen_conditions_in_subsumed(SymbolExpression condition,
			Collection<SymbolExpression> subsumed_conditions) throws Exception {
		if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else if(condition instanceof SymbolConstant) {
			if(((SymbolConstant) condition).get_bool()) { /* none subsumed */ }
			else {
				subsumed_conditions.add(SymbolFactory.sym_constant(Boolean.FALSE));
			}
		}
		else if(condition instanceof SymbolBinaryExpression) {
			SymbolExpression loperand = ((SymbolBinaryExpression) condition).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) condition).get_roperand();
			COperator op = ((SymbolBinaryExpression) condition).get_operator().get_operator();
			
			if(loperand.get_source() instanceof CirExecution
					|| roperand.get_source() instanceof CirExecution) {
				subsumed_conditions.add(condition); 
			}
			else {
				switch(op) {
				case greater_tn:	/* x > y ==> (x > y; x >= y; x != y) */
					subsumed_conditions.add(condition);
					subsumed_conditions.add(SymbolFactory.greater_eq(loperand, roperand));
					subsumed_conditions.add(SymbolFactory.not_equals(loperand, roperand));
					break;
				case smaller_tn:	/* x < y ==> (x < y; x <= y; x != y) */
					subsumed_conditions.add(condition);
					subsumed_conditions.add(SymbolFactory.smaller_eq(loperand, roperand));
					subsumed_conditions.add(SymbolFactory.not_equals(loperand, roperand));
					break;
				case equal_with:	/* x == y --> (x == y; x <= y; x >= y) */
					subsumed_conditions.add(condition);
					subsumed_conditions.add(SymbolFactory.smaller_eq(loperand, roperand));
					subsumed_conditions.add(SymbolFactory.greater_eq(loperand, roperand));
				default:
					subsumed_conditions.add(condition); 
					break;
				}
			}
		}
		else {
			subsumed_conditions.add(condition);
		}
	}
	/**
	 * It divides the expression into sub_conditions by conjunctive, and then generate
	 * its subsumed conditions to incorporate all the implied conditions from them.
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private Collection<SymbolExpression> get_conditions_in(SymbolExpression expression) throws Exception {
		/* 1. divide the expression into the conjunctive */
		expression = this.evaluate(expression, null);
		Set<SymbolExpression> sub_conditions = new HashSet<SymbolExpression>();
		this.div_conditions_in_conjunct(expression, sub_conditions);
		
		/* 2. generate the subsumed conditions from prior */
		Set<SymbolExpression> conditions = new HashSet<SymbolExpression>();
		for(SymbolExpression sub_condition : sub_conditions) {
			this.gen_conditions_in_subsumed(sub_condition, conditions);
		}
		return conditions;
	}
	/**
	 * It divides the expression into sub_conditions by disjunctive
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private Collection<SymbolExpression> div_conditions_in(SymbolExpression expression) throws Exception {
		expression = this.evaluate(expression, null);
		Set<SymbolExpression> conditions = new HashSet<SymbolExpression>();
		this.div_conditions_in_disjunct(expression, conditions);
		return conditions;
	}
	
	/* flow-based checkpoint path find */
	/**
	 * recursively collect the symbolic references under the node
	 * @param node
	 * @param references to preserve the output references being collected
	 */
	private void get_symbol_references_in(SymbolNode node, Collection<SymbolExpression> references) {
		if(node.is_reference()) references.add((SymbolExpression) node);
		for(SymbolNode child : node.get_children()) {
			this.get_symbol_references_in(child, references);
		}
	}
	/**
	 * @param node
	 * @param references
	 * @return whether there is reference used in the node
	 */
	private boolean has_symbol_references_in(SymbolNode node, Collection<SymbolExpression> references) {
		if(references.isEmpty()) {
			return false;
		}
		else if(references.contains(node)) {
			return true;
		}
		else {
			for(SymbolNode child : node.get_children()) {
				if(this.has_symbol_references_in(child, references)) {
					return true;
				}
			}
			return false;
		}
	}
	/**
	 * @param execution
	 * @param references
	 * @return whether any references is limited in the execution (IF|CASE|ASSIGN)
	 */
	private boolean has_symbol_references_in(CirExecution execution,  Collection<SymbolExpression> references) throws Exception {
		if(references.isEmpty()) {
			return false;
		}
		else {
			CirStatement statement = execution.get_statement();
			if(statement instanceof CirAssignStatement) {
				return references.contains(SymbolFactory.
						sym_expression(((CirAssignStatement) statement).get_lvalue()));
			}
			else if(statement instanceof CirIfStatement) {
				return this.has_symbol_references_in(SymbolFactory.sym_expression(
						((CirIfStatement) statement).get_condition()), references);
			}
			else if(statement instanceof CirCaseStatement) {
				return this.has_symbol_references_in(SymbolFactory.sym_expression(
						((CirCaseStatement) statement).get_condition()), references);
			}
			else {
				return false;
			}
		}
	}
	/**
	 * @param execution
	 * @param expression
	 * @return find the previous check-point where the expression should be evaluated
	 * @throws Exception
	 */
	private CirExecution find_prior_checkpoint(CirExecution execution, SymbolExpression expression) throws Exception {
		if(expression instanceof SymbolConstant) {
			if(((SymbolConstant) expression).get_bool()) {
				CirExecutionPath prev_path = CirExecutionPathFinder.finder.db_extend(execution);
				Iterator<CirExecutionEdge> iterator = prev_path.get_iterator(true);
				while(iterator.hasNext()) {
					CirExecutionEdge edge = iterator.next();
					switch(edge.get_type()) {
					case true_flow:	return edge.get_target();
					case fals_flow:	return edge.get_target();
					default:		break;
					}
				}
				return prev_path.get_source();
			}
			else {
				return execution.get_graph().get_entry();
			}
		}
		else {
			CirExecutionPath prev_path = CirExecutionPathFinder.finder.db_extend(execution);
			Iterator<CirExecutionEdge> iterator = prev_path.get_iterator(true);
			Collection<SymbolExpression> references = new HashSet<SymbolExpression>();
			this.get_symbol_references_in(expression, references);
			while(iterator.hasNext()) {
				CirExecutionEdge edge = iterator.next();
				if(this.has_symbol_references_in(edge.get_source(), references)) {
					return edge.get_target();
				}
			}
			return prev_path.get_source();
		}
	}
	
	/* data store-value pair generation */
	/**
	 * collect the expressions under the location
	 * @param location
	 * @param expressions
	 */
	private void collect_expressions_in(CirNode location, Collection<CirExpression> expressions) {
		if(location instanceof CirExpression) {
			expressions.add((CirExpression) location);
		}
		for(CirNode child : location.get_children()) {
			this.collect_expressions_in(child, expressions);
		}
	}
	/**
	 * It generates the data value state in the node for every expression and following statement.
	 * @param node
	 * @param next_flow
	 * @throws Exception
	 */
	private void construct_data_state(CirStateNode node) throws Exception {
		/* 1. capture the expressions to be evaluated and preserved in the statement */
		CirStatement statement = node.get_execution().get_statement();
		Set<CirExpression> expressions = new HashSet<CirExpression>();
		if(statement instanceof CirAssignStatement) {
			this.collect_expressions_in(((CirAssignStatement) statement).get_rvalue(), expressions);
			expressions.add(((CirAssignStatement) statement).get_lvalue());
		}
		else if(statement instanceof CirIfStatement) {
			this.collect_expressions_in(((CirIfStatement) statement).get_condition(), expressions);
		}
		else if(statement instanceof CirCaseStatement) {
			this.collect_expressions_in(((CirCaseStatement) statement).get_condition(), expressions);
		}
		else if(statement instanceof CirCallStatement) {
			this.collect_expressions_in(((CirCallStatement) statement).get_arguments(), expressions);
			expressions.add(((CirCallStatement) statement).get_function());
		}
		
		/* 2. generate the original data store and its value iteratively */
		for(CirExpression expression : expressions) {
			if(CirMutations.is_assigned(expression)) {
				CirAssignStatement assign = (CirAssignStatement) expression.get_parent();
				node.get_data().add_store_value(CirStoreValue.new_expr(expression, assign.get_rvalue()));
			}
			else {
				node.get_data().add_store_value(CirStoreValue.new_expr(expression, expression));
			}
		}
	}
	/**
	 * create the next statement pointers in the given state
	 * @param node
	 * @param next_flow
	 * @throws Exception
	 */
	private void construct_stmt_state(CirStateNode node, CirExecutionFlow next_flow) throws Exception {
		if(next_flow != null) {
			CirExecutionPath path = new CirExecutionPath(next_flow.get_target());
			CirExecutionPathFinder.finder.df_extend(path);
			for(CirExecutionEdge edge : path.get_edges()) {
				node.get_data().add_store_value(CirStoreValue.new_stmt(edge.get_target(), true));
			}
			node.get_data().add_store_value(CirStoreValue.new_stmt(next_flow.get_target(), true));
		}
	}
	/**
	 * @param source
	 * @param target
	 * @return { cir_expression, muta_value } or null
	 * @throws Exception
	 */
	private Object[] get_next_muta_value(CirExpression source, SymbolExpression target) throws Exception {
		CirNode parent = source.get_parent();
		
		if(parent == null) {
			return null;
		}
		else if(parent instanceof CirDeferExpression) {
			return new Object[] { parent, SymbolFactory.dereference(target) };
		}
		else if(parent instanceof CirFieldExpression) {
			String name = ((CirFieldExpression) parent).get_field().get_name();
			return new Object[] { parent, SymbolFactory.field_expression(target, name) };
		}
		else if(parent instanceof CirAddressExpression) {
			return new Object[] { parent, SymbolFactory.address_of(target) };
		}
		else if(parent instanceof CirCastExpression) {
			CType data_type = ((CirCastExpression) parent).get_type().get_typename();
			return new Object[] { parent, SymbolFactory.cast_expression(data_type, target) };
		}
		else if(parent instanceof CirInitializerBody) {
			List<Object> elements = new ArrayList<Object>();
			CirInitializerBody body = (CirInitializerBody) parent;
			for(int k = 0; k < body.number_of_elements(); k++) {
				if(body.get_element(k) == source) {
					elements.add(target);
				}
				else {
					elements.add(body.get_element(k));
				}
			}
			return new Object[] { body, SymbolFactory.initializer_list(elements) };
		}
		else if(parent instanceof CirWaitExpression) {
			CirExecution wait_execution = parent.execution_of();
			CirExecution call_execution = wait_execution.get_graph().get_execution(wait_execution.get_id() - 1);
			CirCallStatement call_statement = (CirCallStatement) call_execution.get_statement();
			
			List<Object> arguments = new ArrayList<Object>();
			for(int k = 0; k < call_statement.get_arguments().number_of_arguments(); k++) {
				arguments.add(call_statement.get_arguments().get_argument(k));
			}
			return new Object[] { parent, SymbolFactory.call_expression(target, arguments) };
		}
		else if(parent instanceof CirArgumentList) {
			CirExecution call_execution = parent.execution_of();
			CirCallStatement call_statement = (CirCallStatement) parent.get_parent();
			CirExecution wait_execution = call_execution.get_graph().get_execution(call_execution.get_id() + 1);
			CirWaitAssignStatement wait_statement = (CirWaitAssignStatement) wait_execution.get_statement();
			
			List<Object> arguments = new ArrayList<Object>();
			for(int k = 0; k < call_statement.get_arguments().number_of_arguments(); k++) {
				if(call_statement.get_arguments().get_argument(k) == source) {
					arguments.add(target);
				}
				else {
					arguments.add(call_statement.get_arguments().get_argument(k));
				}
			}
			
			return new Object[] { wait_statement.get_rvalue(), 
					SymbolFactory.call_expression(call_statement.get_function(), arguments) };
		}
		else if(parent instanceof CirComputeExpression) {
			CirComputeExpression expression = (CirComputeExpression) parent;
			CType data_type = expression.get_data_type();
			switch(expression.get_operator()) {
			case negative:		target = SymbolFactory.arith_neg(target); break;
			case bit_not:		target = SymbolFactory.bitws_rsv(target); break;
			case logic_not:		target = SymbolFactory.logic_not(target); break;
			case arith_add:		
			{
				if(expression.get_operand(0) == source) {
					target = SymbolFactory.arith_add(data_type, target, expression.get_operand(1));
				}
				else {
					target = SymbolFactory.arith_add(data_type, expression.get_operand(0), target);
				}
				break;
			}
			case arith_sub:
			{
				if(expression.get_operand(0) == source) {
					target = SymbolFactory.arith_sub(data_type, target, expression.get_operand(1));
				}
				else {
					target = SymbolFactory.arith_sub(data_type, expression.get_operand(0), target);
				}
				break;
			}
			case arith_mul:
			{
				if(expression.get_operand(0) == source) {
					target = SymbolFactory.arith_mul(data_type, target, expression.get_operand(1));
				}
				else {
					target = SymbolFactory.arith_mul(data_type, expression.get_operand(0), target);
				}
				break;
			}
			case arith_div:
			{
				if(expression.get_operand(0) == source) {
					target = SymbolFactory.arith_div(data_type, target, expression.get_operand(1));
				}
				else {
					target = SymbolFactory.arith_div(data_type, expression.get_operand(0), target);
				}
				break;
			}
			case arith_mod:
			{
				if(expression.get_operand(0) == source) {
					target = SymbolFactory.arith_mod(data_type, target, expression.get_operand(1));
				}
				else {
					target = SymbolFactory.arith_mod(data_type, expression.get_operand(0), target);
				}
				break;
			}
			case bit_and:
			{
				if(expression.get_operand(0) == source) {
					target = SymbolFactory.bitws_and(data_type, target, expression.get_operand(1));
				}
				else {
					target = SymbolFactory.bitws_and(data_type, expression.get_operand(0), target);
				}
				break;
			}
			case bit_or:
			{
				if(expression.get_operand(0) == source) {
					target = SymbolFactory.bitws_ior(data_type, target, expression.get_operand(1));
				}
				else {
					target = SymbolFactory.bitws_ior(data_type, expression.get_operand(0), target);
				}
				break;
			}
			case bit_xor:
			{
				if(expression.get_operand(0) == source) {
					target = SymbolFactory.bitws_xor(data_type, target, expression.get_operand(1));
				}
				else {
					target = SymbolFactory.bitws_xor(data_type, expression.get_operand(0), target);
				}
				break;
			}
			case left_shift:
			{
				if(expression.get_operand(0) == source) {
					target = SymbolFactory.bitws_lsh(data_type, target, expression.get_operand(1));
				}
				else {
					target = SymbolFactory.bitws_lsh(data_type, expression.get_operand(0), target);
				}
				break;
			}
			case righ_shift:
			{
				if(expression.get_operand(0) == source) {
					target = SymbolFactory.bitws_rsh(data_type, target, expression.get_operand(1));
				}
				else {
					target = SymbolFactory.bitws_rsh(data_type, expression.get_operand(0), target);
				}
				break;
			}
			case greater_tn:
			{
				if(expression.get_operand(0) == source) {
					target = SymbolFactory.greater_tn(target, expression.get_operand(1));
				}
				else {
					target = SymbolFactory.greater_tn(expression.get_operand(0), target);
				}
				break;
			}
			case greater_eq:
			{
				if(expression.get_operand(0) == source) {
					target = SymbolFactory.greater_eq(target, expression.get_operand(1));
				}
				else {
					target = SymbolFactory.greater_eq(expression.get_operand(0), target);
				}
				break;
			}
			case smaller_tn:
			{
				if(expression.get_operand(0) == source) {
					target = SymbolFactory.smaller_tn(target, expression.get_operand(1));
				}
				else {
					target = SymbolFactory.smaller_tn(expression.get_operand(0), target);
				}
				break;
			}
			case smaller_eq:
			{
				if(expression.get_operand(0) == source) {
					target = SymbolFactory.smaller_eq(target, expression.get_operand(1));
				}
				else {
					target = SymbolFactory.smaller_eq(expression.get_operand(0), target);
				}
				break;
			}
			case equal_with:
			{
				if(expression.get_operand(0) == source) {
					target = SymbolFactory.equal_with(target, expression.get_operand(1));
				}
				else {
					target = SymbolFactory.equal_with(expression.get_operand(0), target);
				}
				break;
			}
			case not_equals:
			{
				if(expression.get_operand(0) == source) {
					target = SymbolFactory.not_equals(target, expression.get_operand(1));
				}
				else {
					target = SymbolFactory.not_equals(expression.get_operand(0), target);
				}
				break;
			}
			default: 
			{
				throw new IllegalArgumentException("Invalid: " + expression);
			}
			}
			
			return new Object[] { parent, target };
		}
		else if(parent instanceof CirAssignStatement) {
			if(((CirAssignStatement) parent).get_rvalue() == source) {
				return new Object[] { ((CirAssignStatement) parent).get_lvalue(), target };
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
	}
	/**
	 * @param source
	 * @param target
	 * @return mapping from original expression to mutated value
	 * @throws Exception
	 */
	private Map<CirNode, SymbolExpression> generate_muta_values(CirNode source, SymbolExpression target) throws Exception {
		Map<CirNode, SymbolExpression> results = new HashMap<CirNode, SymbolExpression>();
		Object[] source_target;
		
		while(source != null && target != null) {
			results.put(source, target);
			source_target = this.get_next_muta_value((CirExpression) source, target);
			if(source_target == null) {
				break;
			}
			else {
				source = (CirNode) source_target[0];
				target = (SymbolExpression) source_target[1];
			}
		}
		
		return results;
	}
	private void construct_muta_state_in_block_error(CirStateNode node, CirBlockError init_error) throws Exception {
		if(init_error.is_executed()) {
			node.get_data().add_store_value(CirStoreValue.new_stmt(init_error.get_execution(), true));
		}
		else {
			node.get_data().add_store_value(CirStoreValue.new_stmt(init_error.get_execution(), false));
		}
	}
	private void construct_muta_state_in_flows_error(CirStateNode node, CirFlowsError init_error) throws Exception {
		this.construct_stmt_state(node, init_error.get_mutation_flow());
	}
	private void construct_muta_state_in_traps_error(CirStateNode node, CirTrapsError init_error) throws Exception {
		node.get_data().add_store_value(CirStoreValue.new_trap(init_error.get_execution()));
	}
	private void construct_muta_state_in_expressions(CirStateNode node, CirExpression expression, SymbolExpression muta_value) throws Exception {
		Map<CirNode, SymbolExpression> results = this.generate_muta_values(expression, muta_value);
		for(CirNode location : results.keySet()) {
			SymbolExpression value = results.get(location);
			node.get_data().add_store_value(CirStoreValue.new_expr((CirExpression) location, value));
		}
	}
	private void construct_muta_state_in_difer_error(CirStateNode node, CirDiferError init_error) throws Exception {
		node.get_data().add_store_value(CirStoreValue.new_expr(init_error.get_orig_expression(), init_error.get_muta_expression()));
		node.get_data().add_store_value(CirStoreValue.new_trap(init_error.get_execution()));
	}
	private void construct_muta_state_in_value_error(CirStateNode node, CirValueError init_error) throws Exception {
		this.construct_muta_state_in_expressions(node, init_error.get_orig_expression(), init_error.get_muta_expression());
	}
	private void construct_muta_state_in_refer_error(CirStateNode node, CirReferError init_error) throws Exception {
		this.construct_muta_state_in_expressions(node, init_error.get_orig_expression(), init_error.get_muta_expression());
	}
	private void construct_muta_state_in_state_error(CirStateNode node, CirStateError init_error) throws Exception {
		node.get_data().add_store_value(CirStoreValue.new_expr(init_error.get_orig_expression(), init_error.get_muta_expression()));
	}
	/**
	 * It generates the mutated state in the node using given constraint-error infection pair
	 * @param node
	 * @param constraint
	 * @param init_error
	 * @throws Exception
	 */
	private void construct_muta_state(CirStateNode node, SymbolExpression constraint, CirAttribute init_error) throws Exception {
		/* state infection constraint */
		CirExecution execution = node.get_execution();
		Collection<SymbolExpression> sub_conditions = this.get_conditions_in(constraint);
		for(SymbolExpression sub_condition : sub_conditions) {
			CirExecution check_point = this.find_prior_checkpoint(execution, sub_condition);
			node.get_data().add_store_value(CirStoreValue.new_cond(check_point, sub_condition, true));
		}
		
		/* state error generation part */
		if(init_error instanceof CirBlockError) {
			this.construct_muta_state_in_block_error(node, (CirBlockError) init_error);
		}
		else if(init_error instanceof CirFlowsError) {
			this.construct_muta_state_in_flows_error(node, (CirFlowsError) init_error);
		}
		else if(init_error instanceof CirTrapsError) {
			this.construct_muta_state_in_traps_error(node, (CirTrapsError) init_error);
		}
		else if(init_error instanceof CirDiferError) {
			this.construct_muta_state_in_difer_error(node, (CirDiferError) init_error);
		}
		else if(init_error instanceof CirValueError) {
			this.construct_muta_state_in_value_error(node, (CirValueError) init_error);
		}
		else if(init_error instanceof CirReferError) {
			this.construct_muta_state_in_refer_error(node, (CirReferError) init_error);
		}
		else if(init_error instanceof CirStateError) {
			this.construct_muta_state_in_state_error(node, (CirStateError) init_error);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + init_error);
		}
		
	}
	
	/* generation of path reachability  */
	/**
	 * @param flow
	 * @return the attribute representing the coverage of the specified flow
	 * @throws Exception
	 */
	private CirAttribute get_flow_attribute(CirExecutionFlow flow) throws Exception {
		if(flow == null) {
			throw new IllegalArgumentException("Invalid flow as null");
		}
		else if(flow.get_type() == CirExecutionFlowType.true_flow) {
			CirStatement statement = flow.get_source().get_statement();
			CirExpression expression;
			if(statement instanceof CirIfStatement) {
				expression = ((CirIfStatement) statement).get_condition();
			}
			else {
				expression = ((CirCaseStatement) statement).get_condition();
			}
			return CirAttribute.new_constraint(flow.get_source(), expression, true);
		}
		else if(flow.get_type() == CirExecutionFlowType.fals_flow) {
			CirStatement statement = flow.get_source().get_statement();
			CirExpression expression;
			if(statement instanceof CirIfStatement) {
				expression = ((CirIfStatement) statement).get_condition();
			}
			else {
				expression = ((CirCaseStatement) statement).get_condition();
			}
			return CirAttribute.new_constraint(flow.get_source(), expression, false);
		}
		else if(flow.get_type() == CirExecutionFlowType.call_flow) {
			return CirAttribute.new_cover_count(flow.get_source(), 1);
		}
		else if(flow.get_type() == CirExecutionFlowType.retr_flow) {
			return CirAttribute.new_cover_count(flow.get_target(), 1);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + flow.get_type());
		}
	}
	/**
	 * collect the execution flows in the path to the given sequences 
	 * @param path
	 * @param flows
	 */
	private void collect_execution_flows_in(CirExecutionPath path, List<CirExecutionFlow> flows) {
		for(CirExecutionEdge edge : path.get_edges()) {
			/* capture execution flow in edge */
			CirExecutionFlow flow;
			switch(edge.get_type()) {
			case true_flow:
			case fals_flow:
			case call_flow:
			case retr_flow:	flow = edge.get_flow();	break;
			default:		flow = null;			break;
			}
			
			/* append the flow into simple path */
			if(flow != null && !flows.contains(flow)) {
				flows.add(flow);
			}
		}
	}
	/**
	 * generate the execution flows in sequence from function entry to the target using decidable path analysis
	 * @param target
	 * @param flows
	 * @throws Exception
	 */
	private void generate_execution_flows(CirExecution target, List<CirExecutionFlow> flows) throws Exception {
		if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else if(flows == null) {
			throw new IllegalArgumentException("Invalid flows: null");
		}
		else {
			CirExecution source = target.get_graph().get_entry();
			CirExecutionPath path = new CirExecutionPath(source);
			CirExecutionPathFinder.finder.vf_extend(path, target);
			this.collect_execution_flows_in(path, flows); return;
		}
	}
	/**
	 * generate the execution flows in sequence from program entry to the target using dependence path analysis
	 * @param target
	 * @param dependence_graph
	 * @param flows
	 * @throws Exception
	 */
	private void generate_execution_flows(CirExecution target, 
			CDependGraph dependence_graph, List<CirExecutionFlow> flows) throws Exception {
		if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else if(dependence_graph == null) {
			this.generate_execution_flows(target, flows);
		}
		else {
			CirExecutionPath path = CirExecutionPathFinder.
					finder.dependence_path(dependence_graph, target);
			this.collect_execution_flows_in(path, flows);
		}
	}
	/**
	 * generate the execution flows in sequence from program entry to the target using dynamic state analysis
	 * @param target
	 * @param state_path
	 * @param flows
	 * @throws Exception
	 */
	private void generate_execution_flows(CirExecution target, 
			CStatePath state_path, List<CirExecutionFlow> flows) throws Exception {
		if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else if(state_path == null || state_path.size() == 0) {
			this.generate_execution_flows(target, flows);
		}
		else {
			CirExecution source = state_path.get_node(0).get_execution();
			CirExecutionPath path = new CirExecutionPath(source);
			for(CStateNode state_node : state_path.get_nodes()) {
				CirExecutionPathFinder.finder.vf_extend(path, state_node.get_execution());
				this.collect_execution_flows_in(path, flows);
				if(state_node.get_execution() == target) {
					break;
				}
				else {
					path = new CirExecutionPath(state_node.get_execution());
				}
			}
			CirExecutionPathFinder.finder.vf_extend(path, target);
			this.collect_execution_flows_in(path, flows);
		}
	}
	/**
	 * @param node
	 * @param flow_attribute	either CirConstraint or CirCoverCount
	 * @throws Exception
	 */
	private void generate_path_conditions_in(CirStateNode node, CirAttribute flow_attribute) throws Exception {
		if(node == null) {
			throw new IllegalArgumentException("Invalid node as null");
		}
		else if(flow_attribute == null) {
			throw new IllegalArgumentException("Invalid flow_attribute");
		}
		else if(flow_attribute instanceof CirCoverCount) {	/* statement coverage */
			CirExecution execution = flow_attribute.get_execution();
			int execution_times = ((CirCoverCount) flow_attribute).get_coverage_count();
			Set<Integer> times = new HashSet<Integer>();
			
			for(int k = 1; k <= execution_times; k = k * 2) {
				times.add(Integer.valueOf(k));
			}
			times.add(Integer.valueOf(execution_times));
			
			for(Integer time : times) {
				node.get_data().add_store_value(CirStoreValue.new_cond(execution, time.intValue()));
			}
		}
		else if(flow_attribute instanceof CirConstraint) {	/* condition asserted */
			CirExecution execution = flow_attribute.get_execution(), check_point;
			SymbolExpression condition = ((CirConstraint) flow_attribute).get_condition();
			Collection<SymbolExpression> sub_conditions = this.get_conditions_in(condition);
			
			for(SymbolExpression sub_condition : sub_conditions) {
				check_point = this.find_prior_checkpoint(execution, sub_condition);
				node.get_data().add_store_value(CirStoreValue.new_cond(check_point, sub_condition, true));
			}
			
			if(sub_conditions.isEmpty()) {
				SymbolExpression sub_condition = SymbolFactory.sym_constant(Boolean.TRUE);
				check_point = this.find_prior_checkpoint(execution, sub_condition);
				node.get_data().add_store_value(CirStoreValue.new_cond(check_point, 1));
			}
		}
		else {
			throw new IllegalArgumentException("Invalid flow_attribute");
		}
	}
	/**
	 * extend the 
	 * @param parent
	 * @param flow
	 * @return
	 * @throws Exception
	 */
	private CirStateNode pre_construct_from(CirStateNode parent, CirExecutionFlow flow) throws Exception {
		CirStateNode child = parent.new_child(CirStateType.pre, flow.get_target());
		this.generate_path_conditions_in(child, this.get_flow_attribute(flow));
		return child;
	}
	/**
	 * @param tree
	 * @param target
	 * @param context	null, CDependGraph or CStatePath
	 * @return it constructs the pred_path from root to the target execution using path created under context
	 * @throws Exception
	 */
	private CirStateNode construct_reachability(CirStateTree tree, CirExecution target, Object context) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree: null");
		}
		else if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else {
			/* 1. generate the execution path from entry to target */
			List<CirExecutionFlow> flows = new ArrayList<CirExecutionFlow>();
			if(context instanceof CDependGraph) {
				this.generate_execution_flows(target, (CDependGraph) context, flows);
			}
			else if(context instanceof CStatePath) {
				this.generate_execution_flows(target, (CStatePath) context, flows);
			}
			else {
				this.generate_execution_flows(target, flows);
			}
			
			/* 2. generate the pred_state_nodes for reaching target */
			CirStateNode node = tree.get_root();
			for(CirExecutionFlow flow : flows) {
				node = this.pre_construct_from(node, flow);
			}
			
			/* 3. generate the mid node for reaching the target */
			node = node.new_child(CirStateType.mid, target);
			this.construct_data_state(node);	return node;
		}
	}
	
	/* generation of state infection */
	private void construct_state_infections_in(CirStateNode parent, CirMutation cir_mutation) throws Exception {
		/* 1. divide the infection constraint into the disjunctive sub_conditions */
		Collection<SymbolExpression> conditions = 
				this.div_conditions_in(cir_mutation.get_constraint().get_parameter());
		
		/* 2. invalid extension on that mutation */
		if(conditions.isEmpty()) {
			conditions.add(SymbolFactory.sym_constant(Boolean.FALSE));
		}
		
		/* 3. generate the mutated children from node using infection pair */
		for(SymbolExpression condition : conditions) {
			CirStateNode child = parent.new_child(CirStateType.pos, parent.get_execution());
			this.construct_muta_state(child, condition, cir_mutation.get_init_error());
		}
	}
	/**
	 * @param tree
	 * @throws Exception
	 */
	protected static void construct(CirStateTree tree, Object context) throws Exception {
		if(tree.has_cir_mutations()) {
			Map<CirExecution, Collection<CirMutation>> maps = new HashMap<CirExecution, Collection<CirMutation>>();
			for(CirMutation cir_mutation : tree.get_cir_mutations()) {
				CirExecution execution = cir_mutation.get_execution();
				if(!maps.containsKey(execution)) {
					maps.put(execution, new ArrayList<CirMutation>());
				}
				maps.get(execution).add(cir_mutation);
			}
			
			for(CirExecution execution : maps.keySet()) {
				CirStateNode reach_node = utils.construct_reachability(tree, execution, context);
				for(CirMutation cir_mutation : maps.get(execution)) {
					utils.construct_state_infections_in(reach_node, cir_mutation);
				}
			}
			tree.update_mid_state_nodes();
		}
	}
	
}
