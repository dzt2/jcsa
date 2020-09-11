package com.jcsa.jcmutest.mutant.sec2mutant.muta;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescriptions;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.AstCirPair;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

/**
 * It parses the mutation to generate SecInfection with information about the
 * infection condition that is required for killing this mutant.
 * 
 * @author yukimula
 *
 */
public abstract class SecInfectionParser {
	
	/* definition */
	/** C-intermediate code in which the infection is fetched **/
	private CirTree cir_tree;
	/** the syntactic mutation under analysis **/
	private AstMutation mutation;
	/** the statement where the mutation is seeded and executed **/
	private CirStatement location;
	/** the infection module being produced from this parser **/
	protected SecInfection infection;
	public SecInfectionParser() { }
	
	/* parsing interfaces */
	/**
	 * @param mutation
	 * @return find the statement where the mutation is injected and reached
	 * @throws Exception
	 */
	protected abstract CirStatement find_location(AstMutation mutation) throws Exception;
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
	public SecInfection parse(CirTree cir_tree, Mutant mutant) throws Exception {
		if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree: null");
		else if(mutant == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else {
			/* 1. initialize the attributes */
			this.cir_tree = cir_tree;
			this.mutation = mutant.get_mutation();
			this.infection = new SecInfection(mutant);
			
			/* 2. determine the seeded point */
			this.location = this.find_location(this.mutation);
			if(this.location == null) {
				return null;	/* unreachable mutation */
			}
			
			/* 3. generate the infection module */
			if(!this.generate_infections(this.location, this.mutation)) {
				throw new IllegalArgumentException("Failed at " + this.mutation);
			}
			
			/* 4. return final result */ return this.infection;
		}
	}
	
	/* supporting methods */
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
		return this.cir_tree.get_localizer().get_cir_value(location);
	}
	/**
	 * @param location
	 * @return the first statement in the range of CIR-code to which the location
	 * 		   corresponds.
	 * @throws Exception
	 */
	protected CirStatement get_beg_statement(AstNode location) throws Exception {
		return this.cir_tree.get_localizer().beg_statement(location);
	}
	/**
	 * @param location
	 * @return the final statement in the range of CIR-code to which the location
	 * 		   corresponds.
	 * @throws Exception
	 */
	protected CirStatement get_end_statement(AstNode location) throws Exception {
		return this.cir_tree.get_localizer().end_statement(location);
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
	protected void add_infection(SecDescription constraint, SecDescription init_error) throws Exception {
		this.infection.add_infection_pair(constraint, init_error);
	}
	protected CirExecution get_execution(CirStatement statement) throws Exception {
		return this.cir_tree.get_localizer().get_execution(statement);
	}
	protected SecDescriptions conjunct(Collection<SecDescription> descriptions) throws Exception {
		return SecFactory.conjunct(this.location, descriptions);
	}
	protected SecDescriptions disjunct(Collection<SecDescription> descriptions) throws Exception {
		return SecFactory.conjunct(this.location, descriptions);
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
	
	/* generation methods */
	/**
	 * @param condition
	 * @param value
	 * @return assert(this.statement, condition, value)
	 * @throws Exception
	 */
	protected SecConstraint get_constraint(CirStatement statement, Object condition, boolean value) throws Exception {
		return SecFactory.assert_constraint(statement, condition, value);
	}
	/**
	 * @param condition
	 * @param value
	 * @return assert(this.statement, condition, value)
	 * @throws Exception
	 */
	protected SecConstraint get_constraint(Object condition, boolean value) throws Exception {
		return SecFactory.assert_constraint(this.location, condition, value);
	}
	/**
	 * @param statement
	 * @param times
	 * @return execute(statement, int)
	 * @throws Exception
	 */
	protected SecConstraint exec_constraint(CirStatement statement, int times) throws Exception {
		return SecFactory.execute_constraint(statement, times);
	}
	/**
	 * @param statement
	 * @return trap_statement(statement)
	 * @throws Exception
	 */
	protected SecDescription trap_statement(CirStatement statement) throws Exception {
		return SecFactory.trap_statement(statement);
	}
	protected SecDescription add_expression(CirExpression expression, Object operand) throws Exception {
		return SecFactory.add_expression(location, expression, COperator.arith_add, operand);
	}
	protected SecDescription sub_expression(CirExpression expression, Object operand) throws Exception {
		return SecFactory.add_expression(location, expression, COperator.arith_sub, operand);
	}
	protected SecDescription mul_expression(CirExpression expression, Object operand) throws Exception {
		return SecFactory.add_expression(location, expression, COperator.arith_mul, operand);
	}
	protected SecDescription inc_reference(CirExpression expression) throws Exception {
		return SecFactory.uny_expression(location, expression, COperator.increment);
	}
	protected SecDescription dec_reference(CirExpression expression) throws Exception {
		return SecFactory.uny_expression(location, expression, COperator.decrement);
	}
	protected SecDescription neg_expression(CirExpression expression) throws Exception {
		return SecFactory.uny_expression(location, expression, COperator.negative);
	}
	protected SecDescription rsv_expression(CirExpression expression) throws Exception {
		return SecFactory.uny_expression(location, expression, COperator.bit_not);
	}
	protected SecDescription not_expression(CirExpression expression) throws Exception {
		return SecFactory.uny_expression(location, expression, COperator.logic_not);
	}
	protected SecDescription set_expression(CirExpression orig_expression, Object muta_expression) throws Exception {
		return SecFactory.set_expression(this.location, orig_expression, muta_expression);
	}
	
	/* symbolic generation */
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
	
}
