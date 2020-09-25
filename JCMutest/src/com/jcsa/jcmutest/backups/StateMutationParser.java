package com.jcsa.jcmutest.backups;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.AstCirPair;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

/**
 * It translates the mutant to state-mutation in which the state errors
 * are caused with some constraint being satisfied.
 * 
 * @author yukimula
 *
 */
public abstract class StateMutationParser {
	
	/* definition */
	/** C-intermediate code in which the infection is fetched **/
	private CirTree cir_tree;
	/** the syntactic mutation under analysis **/
	private AstMutation mutation;
	/** the statement where the mutation is seeded and executed **/
	private CirStatement beg_statement;
	/** the statement where the mutation is checked for detected **/
	private CirStatement end_statement;
	/** the state mutation generated from the infection parser **/
	protected StateMutation state_mutation;
	public StateMutationParser() { }
	
	/* parsing interfaces */
	/**
	 * @param mutation
	 * @return the statement where the mutation will be reached
	 * @throws Exception
	 */
	protected abstract CirStatement find_beg_statement(AstMutation mutation) throws Exception;
	/**
	 * @param mutation
	 * @return the statement where the mutation will be checked
	 * @throws Exception
	 */
	protected abstract CirStatement find_end_statement(AstMutation mutation) throws Exception;
	/**
	 * generate the infection pairs in infection moduls for killing the mutant
	 * @param mutation
	 * @param statement
	 * @return whether the generation succeeds (for example, syntax incorrectly
	 * 		   mutant must return false to report the users that it is invalid.
	 * @throws Exception
	 */
	protected abstract boolean generate_infections(CirStatement 
				statement, AstMutation mutation) throws Exception;
	/**
	 * @param cir_tree
	 * @param mutant
	 * @return the infection module that is needed for killing the input mutant
	 * @throws Exception
	 */
	public StateMutation parse(CirTree cir_tree, Mutant mutant) throws Exception {
		if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree: null");
		else if(mutant == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else {
			/* 1. initialize the attributes */
			this.cir_tree = cir_tree;
			this.mutation = mutant.get_mutation();
			this.state_mutation = new StateMutation(mutant);
			
			/* 2. determine the seeded point */
			this.beg_statement = this.find_beg_statement(mutation);
			this.end_statement = this.find_end_statement(mutation);
			
			/* 3. generate the infection pairs */
			if(this.beg_statement != null) {
				this.state_mutation.set_beg_execution(this.get_execution(this.beg_statement));
				this.state_mutation.set_end_execution(this.get_execution(this.end_statement));
				this.generate_infections(this.beg_statement, mutation);
				return this.state_mutation;
			}
			else {
				throw new UnsupportedOperationException("Unable to reach");
			}
		}
	}
	
	/* cir-code processing methods */
	/**
	 * @param location
	 * @return the range to which the location corresponds in CIR code
	 * @throws Exception
	 */
	protected AstCirPair get_cir_range(AstNode location) throws Exception {
		return this.cir_tree.get_localizer().get_cir_range(location);
	}
	/**
	 * @param location
	 * @return the expression as the direct usage point of the expression as provided
	 * @throws Exception
	 */
	protected CirExpression get_cir_expression(AstNode location) throws Exception {
		if(location instanceof AstExpression) {
			location = CTypeAnalyzer.get_expression_of((AstExpression) location);
		}
		return this.cir_tree.get_localizer().get_cir_value(location);
	}
	/**
	 * @param location
	 * @return the first statement in the range of CIR-code to which the location
	 * 		   corresponds.
	 * @throws Exception
	 */
	protected CirStatement get_beg_statement(AstNode location) throws Exception {
		CirStatement statement = this.cir_tree.get_localizer().beg_statement(location);
		if(statement == null) {
			try {
				CirExpression expression = this.get_cir_expression(location);
				if(expression != null) {
					return expression.statement_of();
				}
				else {
					return this.get_cir_range(location).get_beg_statement();
				}
			}
			catch(Exception ex) {
				return this.get_cir_range(location).get_beg_statement();
			}
		}
		else {
			return statement;
		}
	}
	/**
	 * @param location
	 * @return the final statement in the range of CIR-code to which the location
	 * 		   corresponds.
	 * @throws Exception
	 */
	protected CirStatement get_end_statement(AstNode location) throws Exception {
		CirStatement statement = this.cir_tree.get_localizer().end_statement(location);
		if(statement == null) {
			try {
				CirExpression expression = this.get_cir_expression(location);
				if(expression != null) {
					return expression.statement_of();
				}
				else {
					return this.get_cir_range(location).get_end_statement();
				}
			}
			catch(Exception ex) {
				return this.get_cir_range(location).get_end_statement();
			}
		}
		else {
			return statement;
		}
	}
	/**
	 * @param location
	 * @param type
	 * @return cir-nodes to which the source location corresponds with specified class
	 * @throws Exception
	 */
	protected List<CirNode> get_cir_nodes(AstNode location, Class<?> cir_class) throws Exception {
		return this.cir_tree.get_cir_nodes(location, cir_class);
	}
	/**
	 * @param location
	 * @param cir_class
	 * @param index
	 * @return the kth cir-node to which the location corresponds with specified
	 * 		   class in the specified location in sorted list.
	 * @throws Exception
	 */
	protected CirNode get_cir_node(AstNode location, Class<?> cir_class, int index) throws Exception {
		return this.get_cir_nodes(location, cir_class).get(index);
	}
	/**
	 * @param location
	 * @param cir_class
	 * @return the first cir-node to which the location corresponds with specified
	 * 		   class in the specified location in sorted list.
	 * @throws Exception
	 */
	protected CirNode get_cir_node(AstNode location, Class<?> cir_class) throws Exception {
		return this.get_cir_node(location, cir_class, 0);
	}
	/**
	 * add the infection pair of [constraint, state_error] in the infection module
	 * @param constraint
	 * @param init_error
	 * @throws Exception
	 */
	protected boolean add_infection(SecConstraint constraint, SecStateError init_error) throws Exception {
		this.state_mutation.add_state_pair(constraint, init_error);
		return true;
	}
	/**
	 * @param statement
	 * @return the executional node w.r.t. the statement in CIR-code
	 * @throws Exception
	 */
	protected CirExecution get_execution(CirStatement statement) throws Exception {
		return this.cir_tree.get_localizer().get_execution(statement);
	}
	private void get_statements_in(AstNode location, Set<CirStatement> statements) throws Exception {
		AstCirPair range = this.get_cir_range(location);
		if(range != null && range.executional()) {
			statements.add(range.get_beg_statement());
			statements.add(range.get_end_statement());
		}
		for(int k = 0; k < location.number_of_children(); k++) {
			this.get_statements_in(location.get_child(k), statements);
		}
	}
	/**
	 * @param location
	 * @return the statements within the lcoation
	 * @throws Exception
	 */
	protected Set<CirStatement> get_statements_in(AstNode location) throws Exception {
		Set<CirStatement> statements = new HashSet<CirStatement>();
		this.get_statements_in(location, statements);
		return statements;
	}
	
	/* symbolic generation */
	/**
	 * @param operator {&&, ||, <, <=, >, >=, ==, !=}
	 * @param loperand
	 * @param roperand
	 * @return create a symbolic condition using binary operator
	 * @throws Exception
	 */
	protected SymExpression sym_condition(COperator operator, Object loperand, Object roperand) throws Exception {
		switch(operator) {
		case logic_and:		return SymFactory.logic_and(loperand, roperand);
		case logic_or:		return SymFactory.logic_ior(loperand, roperand);
		case greater_tn:	return SymFactory.greater_tn(loperand, roperand);
		case greater_eq:	return SymFactory.greater_eq(loperand, roperand);
		case smaller_tn:	return SymFactory.smaller_tn(loperand, roperand);
		case smaller_eq:	return SymFactory.smaller_eq(loperand, roperand);
		case equal_with:	return SymFactory.equal_with(loperand, roperand);
		case not_equals:	return SymFactory.not_equals(loperand, roperand);
		default: throw new IllegalArgumentException("invalid: " + operator);
		}
	}
	/**
	 * @param type
	 * @param operator	{+, -, *, /, %, &, |, ^, <<, >>}
	 * @param loperand
	 * @param roperand
	 * @return the symbolic expression using binary operator
	 * @throws Exception
	 */
	protected SymExpression sym_expression(CType type, COperator operator, Object loperand, Object roperand) throws Exception {
		switch(operator) {
		case arith_add:		return SymFactory.arith_add(type, loperand, roperand);
		case arith_sub:		return SymFactory.arith_sub(type, loperand, roperand);
		case arith_mul:		return SymFactory.arith_mul(type, loperand, roperand);
		case arith_div:		return SymFactory.arith_div(type, loperand, roperand);
		case arith_mod:		return SymFactory.arith_mod(type, loperand, roperand);
		case bit_and:		return SymFactory.bitws_and(type, loperand, roperand);
		case bit_or:		return SymFactory.bitws_ior(type, loperand, roperand);
		case bit_xor:		return SymFactory.bitws_xor(type, loperand, roperand);
		case left_shift:	return SymFactory.bitws_lsh(type, loperand, roperand);
		case righ_shift:	return SymFactory.bitws_rsh(type, loperand, roperand);
		default: throw new IllegalArgumentException("Invalid: " + operator);
		}
	}
	
	/* constraint generation */
	/**
	 * @param condition
	 * @param value
	 * @return assert_on(this.location, condition, value)
	 * @throws Exception
	 */
	protected SecConstraint get_constraint(Object condition, boolean value) throws Exception {
		return SecFactory.condition_constraint(this.beg_statement, condition, value);
	}
	/**
	 * @param statement
	 * @param times
	 * @return execute(statement, int)
	 * @throws Exception
	 */
	protected SecConstraint exe_constraint(CirStatement statement, int times) throws Exception {
		return SecFactory.execution_constraint(statement, times);
	}
	/**
	 * @param descriptions
	 * @return the conjunction of the constraints
	 * @throws Exception
	 */
	protected SecConstraint conjunct(Collection<SecConstraint> constraints) throws Exception {
		return SecFactory.conjunct_constraints(this.beg_statement, constraints);
	}
	/**
	 * @param descriptions
	 * @return the disjunction of the constraints
	 * @throws Exception
	 */
	protected SecConstraint disjunct(Collection<SecConstraint> constraints) throws Exception {
		return SecFactory.disjunct_constraints(this.beg_statement, constraints);
	}
	
	/* statement error generation */
	/**
	 * @param statements
	 * @return add_stmt(statement)*
	 * @throws Exception
	 */
	protected SecStatementError add_statement(CirStatement statement) throws Exception {
		if(!(statement instanceof CirTagStatement))
			return SecFactory.add_statement(statement);
		else
			throw new IllegalArgumentException(statement.generate_code(true));
	}
	/**
	 * @param statements
	 * @return add_stmt(statement)*
	 * @throws Exception
	 */
	protected SecStatementError del_statement(CirStatement statement) throws Exception {
		if(!(statement instanceof CirTagStatement))
			return SecFactory.del_statement(statement);
		else
			throw new IllegalArgumentException(statement.generate_code(true));
	}
	/**
	 * @param source
	 * @param target
	 * @return set_stmt(source, target)
	 * @throws Exception
	 */
	protected SecStatementError set_statement(CirStatement source, CirStatement target) throws Exception {
		return SecFactory.set_statement(source, target);
	}
	
	/* unique error generation */
	/**
	 * @param statement
	 * @return trap_statement(statement)
	 * @throws Exception
	 */
	protected SecUniqueError trap_statement(CirStatement statement) throws Exception {
		return SecFactory.trap_error(statement);
	}
	
	/* expression error generation */
	/**
	 * @param orig_expression
	 * @param muta_expression
	 * @return set_expr(orig_expr, muta_expr)
	 * @throws Exception
	 */
	protected SecExpressionError set_expression(CirExpression orig_expression, Object muta_expression) throws Exception {
		return SecFactory.set_expression(this.beg_statement, orig_expression, muta_expression);
	}
	/**
	 * @param expression
	 * @return neg_expr(expr)
	 * @throws Exception
	 */
	protected SecExpressionError neg_expression(CirExpression expression) throws Exception {
		return SecFactory.uny_expression(this.beg_statement, expression, COperator.negative);
	}
	/**
	 * @param expression
	 * @return rsv_expr(expr)
	 * @throws Exception
	 */
	protected SecExpressionError rsv_expression(CirExpression expression) throws Exception {
		return SecFactory.uny_expression(beg_statement, expression, COperator.bit_not);
	}
	/**
	 * @param expression
	 * @return not_expr(expr)
	 * @throws Exception
	 */
	protected SecExpressionError not_expression(CirExpression expression) throws Exception {
		return SecFactory.uny_expression(beg_statement, expression, COperator.logic_not);
	}
	/**
	 * @param expression
	 * @param operand
	 * @return add_expr(orig_expr, +, operand)
	 * @throws Exception
	 */
	protected SecExpressionError add_expression(CirExpression expression, Object operand) throws Exception {
		return SecFactory.add_expression(beg_statement, expression, COperator.arith_add, operand);
	}
	/**
	 * @param expression
	 * @param operand
	 * @return add_expr(orig_expr, -, operand)
	 * @throws Exception
	 */
	protected SecExpressionError sub_expression(CirExpression expression, Object operand) throws Exception {
		return SecFactory.add_expression(beg_statement, expression, COperator.arith_sub, operand);
	}
	/**
	 * @param expression
	 * @param operand
	 * @return add_expr(orig_expr, *, operand)
	 * @throws Exception
	 */
	protected SecExpressionError mul_expression(CirExpression expression, Object operand) throws Exception {
		return SecFactory.add_expression(beg_statement, expression, COperator.arith_mul, operand);
	}
	
	/* reference error generation */
	/**
	 * @param expression
	 * @param operand
	 * @return add_refr(old_refr, +, operand)
	 * @throws Exception
	 */
	protected SecReferenceError add_reference(CirExpression expression, Object operand) throws Exception {
		return SecFactory.add_reference(this.beg_statement, expression, COperator.arith_add, operand);
	}
	/**
	 * @param expression
	 * @param operand
	 * @return add_refr(old_refr, -, operand)
	 * @throws Exception
	 */
	protected SecReferenceError sub_reference(CirExpression expression, Object operand) throws Exception {
		return SecFactory.add_reference(this.beg_statement, expression, COperator.arith_sub, operand);
	}
	
}
