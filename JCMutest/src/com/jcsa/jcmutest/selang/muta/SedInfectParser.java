package com.jcsa.jcmutest.selang.muta;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public abstract class SedInfectParser {
	
	/**
	 * @param cir_tree
	 * @param mutation
	 * @return find the faulty statement where the mutation is seeded
	 * @throws Exception
	 */
	protected abstract CirStatement faulty_location(
			CirTree cir_tree, AstMutation mutation) throws Exception;
	
	/**
	 * generate the infection pairs in output for mutation
	 * @param cir_tree
	 * @param mutation
	 * @param infection
	 * @throws Exception
	 */
	protected abstract void add_infections(CirTree cir_tree, 
			CirStatement statement, AstMutation mutation, 
			SedInfection infection) throws Exception;
	
	/**
	 * @param cir_tree
	 * @param mutant
	 * @return the infection pairs required for killing the mutation.
	 * @throws Exception
	 */
	protected SedInfection parse(CirTree cir_tree, Mutant mutant) throws Exception {
		SedInfection infection = new SedInfection(mutant);
		AstMutation mutation = mutant.get_mutation();
		CirStatement statement = this.faulty_location(cir_tree, mutation);
		if(statement != null) {
			infection.set_statement(statement);
			this.add_infections(cir_tree, statement, mutation, infection);
		}
		return infection;
	}
}
