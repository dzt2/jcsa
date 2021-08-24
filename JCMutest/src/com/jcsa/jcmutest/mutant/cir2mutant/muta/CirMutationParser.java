package com.jcsa.jcmutest.mutant.cir2mutant.muta;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.CirInfection;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.AstCirPair;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public abstract class CirMutationParser {

	/* constructor */
	/** construct a parser for parsing mutation into cir-versions **/
	public CirMutationParser() { }

	/* parsing methods */
	/**
	 * @param mutation
	 * @return the statement where the mutation is reached
	 * @throws Exception
	 */
	protected abstract CirStatement get_location(
			CirTree cir_tree, AstMutation mutation) throws Exception;
	/**
	 * generate the infection-pair of constraint and state-error
	 * @param location
	 * @param mutation
	 * @param infections
	 * @throws Exception
	 */
	protected abstract void generate_infections(
			CirTree cir_tree, CirStatement statement, AstMutation mutation,
			Map<CirAttribute, CirAttribute> infections) throws Exception;
	/**
	 * @param mutations
	 * @param mutant
	 * @return It parses the mutation in AST into CIR versions
	 * @throws Exception
	 */
	protected Iterable<CirInfection> parse(CirTree cir_tree, AstMutation mutation) throws Exception {
		if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree: null");
		else if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else {
			Set<CirInfection> cir_mutations = new HashSet<>();
			CirStatement statement = this.get_location(cir_tree, mutation);
			if(statement != null) {
				Map<CirAttribute, CirAttribute> infections =
						new HashMap<>();
				this.generate_infections(cir_tree, statement, mutation, infections);
				for(CirAttribute init_error : infections.keySet()) {
					CirAttribute constraint = infections.get(init_error);
					cir_mutations.add(CirAttribute.new_cir_mutation(constraint, init_error));
				}
			}
			return cir_mutations;
		}
	}

	/* location methods */
	/**
	 * @param cir_tree
	 * @param location
	 * @return the range of cir-code to which the AST-location corresponds
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
	 * @return the expression in cir-code where the location represents
	 * @throws Exception
	 */
	protected CirExpression get_cir_expression(CirTree cir_tree, AstNode location) throws Exception {
		AstCirPair range = this.get_cir_range(cir_tree, location);
		if(range != null && range.computational())
			return range.get_result();
		else {
			List<CirNode> expressions = cir_tree.get_localizer().get_cir_nodes(location, CirExpression.class);
			if(expressions.isEmpty()) {
				return null;
			}
			else {
				return (CirExpression) expressions.get(0);	/* default location */
			}
		}
	}
	/**
	 * @param cir_tree
	 * @param location
	 * @return the statement being executed for reaching the location
	 * @throws Exception
	 */
	protected CirStatement get_beg_statement(CirTree cir_tree, AstNode location) throws Exception {
		CirStatement statement = cir_tree.get_localizer().beg_statement(location);
		if(statement == null) {
			try {
				CirExpression expression = this.get_cir_expression(cir_tree, location);
				if(expression != null) {
					return expression.statement_of();
				}
				else {
					return this.get_cir_range(cir_tree, location).get_beg_statement();
				}
			}
			catch(Exception ex) {
				return this.get_cir_range(cir_tree, location).get_beg_statement();
			}
		}
		else {
			return statement;
		}
	}
	/**
	 * @param cir_tree
	 * @param location
	 * @return the statement being executed after out from the location
	 * @throws Exception
	 */
	protected CirStatement get_end_statement(CirTree cir_tree, AstNode location) throws Exception {
		CirStatement statement = cir_tree.get_localizer().end_statement(location);
		if(statement == null) {
			try {
				CirExpression expression = this.get_cir_expression(cir_tree, location);
				if(expression != null) {
					return expression.statement_of();
				}
				else {
					return this.get_cir_range(cir_tree, location).get_end_statement();
				}
			}
			catch(Exception ex) {
				return this.get_cir_range(cir_tree, location).get_end_statement();
			}
		}
		else {
			return statement;
		}
	}
	/**
	 * @param cir_tree
	 * @param location
	 * @param cir_type
	 * @return nodes in CIR program to which the location corresponds with specified type
	 * @throws Exception
	 */
	protected List<CirNode> get_cir_nodes(CirTree cir_tree, AstNode location, Class<?> cir_type) throws Exception {
		return cir_tree.get_cir_nodes(location, cir_type);
	}
	/**
	 * @param cir_tree
	 * @param location
	 * @param cir_type
	 * @return the first node that matches the location with specified type
	 * @throws Exception
	 */
	protected CirNode get_cir_node(CirTree cir_tree, AstNode location, Class<?> cir_type) throws Exception {
		return cir_tree.get_cir_nodes(location, cir_type).get(0);
	}
	/**
	 * @param cir_tree
	 * @param location
	 * @param cir_type
	 * @return the last node that matches the location with specified type
	 * @throws Exception
	 */
	protected CirNode get_last_cir_node(CirTree cir_tree, AstNode location, Class<?> cir_type) throws Exception {
		List<CirNode> cir_nodes = this.
				get_cir_nodes(cir_tree, location, cir_type);
		return cir_nodes.get(cir_nodes.size() - 1);
	}

}
