package com.jcsa.jcmutest.mutant.sec2mutant.muta;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.AstCirPair;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * It provides the interface to generate the reachability and infection module
 * of a mutation on the C-intermediate representation code.
 * 
 * @author yukimula
 *
 */
public abstract class SecInfectionParser {
	
	protected AstMutation mutation;
	protected AstNode location;
	protected CirTree cir_tree;
	protected CirStatement statement;
	protected SecInfection infection;
	public SecInfectionParser() { }
	
	/**
	 * @return the statement where the mutation is injected
	 * @throws Exception
	 */
	protected abstract CirStatement get_statement() throws Exception;
	/**
	 * generate the infection pairs within this.infection module
	 * @throws Exception
	 */
	protected abstract void generate_infections() throws Exception;
	
	/**
	 * @param cir_tree
	 * @param mutant
	 * @return the infection module parsed from mutant
	 * @throws Exception
	 */
	public SecInfection parse(CirTree cir_tree, Mutant mutant) throws Exception {
		if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree");
		else {
			/* initialization */
			SecInfection infection = new SecInfection(mutant);
			this.location = mutant.get_mutation().get_location();
			this.cir_tree = cir_tree;
			this.mutation = mutant.get_mutation();
			this.infection = infection;
			
			this.statement = this.get_statement();
			if(statement != null) {
				this.infection.statement = statement;
				this.generate_infections();
			}
			return infection;
		}
	}
	
	/* basic methods */
	/**
	 * add the infection-pair in the list of this.infection module
	 * @param constraint
	 * @param init_error
	 * @throws Exception
	 */
	protected void add_infection(SecDescription constraint, 
			SecDescription init_error) throws Exception {
		this.infection.add_infection_pair(constraint, init_error);;
	}
	/**
	 * @param location
	 * @return the expression where the location is used (with valid statement)
	 * @throws Exception
	 */
	protected CirExpression get_cir_value(AstNode location) throws Exception {
		return this.cir_tree.get_localizer().get_cir_value(location);
	}
	/**
	 * @param location
	 * @return the first statement to which the location covers
	 * @throws Exception
	 */
	protected CirStatement get_beg_statement(AstNode location) throws Exception {
		return this.cir_tree.get_localizer().beg_statement(location);
	}
	/**
	 * @param location
	 * @return the final statement to which the location covers
	 * @throws Exception
	 */
	protected CirStatement get_end_statement(AstNode location) throws Exception {
		return this.cir_tree.get_localizer().end_statement(location);
	}
	/**
	 * @param location
	 * @param type
	 * @return
	 * @throws Exception
	 */
	protected List<CirNode> get_cir_nodes(AstNode location, Class<?> type) throws Exception {
		return this.cir_tree.get_localizer().get_cir_nodes(location, type);
	}
	/**
	 * collect the statements within the location
	 * @param location
	 * @param statements
	 * @throws Exception
	 */
	private void collect_statements_in(AstNode location, Set<CirStatement> statements) throws Exception {
		AstCirPair range = this.cir_tree.
				get_localizer().get_cir_range(location);
		if(range != null && range.executional()) {
			statements.add(range.get_beg_statement());
			statements.add(range.get_end_statement());
		}
		for(int k = 0; k < location.number_of_children(); k++) {
			this.collect_statements_in(location.get_child(k), statements);
		}
	}
	protected Set<CirStatement> get_statements_in(AstNode location) throws Exception {
		Set<CirStatement> statements = new HashSet<CirStatement>();
		collect_statements_in(location, statements); return statements;
	}
	
}
