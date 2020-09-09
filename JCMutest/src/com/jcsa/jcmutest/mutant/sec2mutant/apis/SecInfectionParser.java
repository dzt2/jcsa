package com.jcsa.jcmutest.mutant.sec2mutant.apis;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcparse.lang.irlang.CirTree;
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
	protected CirTree cir_tree;
	protected SecInfection infection;
	protected SecInfectionParser(CirTree cir_tree) throws Exception {
		if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree");
		else {
			this.mutation = null;
			this.cir_tree = cir_tree;
		}
	}
	
	/**
	 * @return the statement where the mutation is injected
	 * @throws Exception
	 */
	protected abstract CirStatement get_location() throws Exception;
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
			this.cir_tree = cir_tree;
			this.mutation = mutant.get_mutation();
			this.infection = infection;
			
			CirStatement statement = this.get_location();
			if(statement != null) {
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
	
}
