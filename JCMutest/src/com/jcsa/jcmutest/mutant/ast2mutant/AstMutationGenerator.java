package com.jcsa.jcmutest.mutant.ast2mutant;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.astree.AstNode;

/**
 * 	It is used to generate AstMutation(s) from source code in C programming language.
 * 	
 * 	@author yukimula
 *
 */
public abstract class AstMutationGenerator {
	
	/**
	 * @param location the abstract syntax node where mutation is seeded
	 * @return whether the location is available for seeding the mutation
	 */
	protected abstract boolean is_seeded_location(AstNode location) throws Exception;
	
	/**
	 * @param location
	 * @return the set of mutations seeded in the location
	 */
	protected abstract Iterable<AstMutation> seed_mutations(AstNode location) throws Exception;
	
	/**
	 * @param locations the set of locations that are seeded for generating mutations
	 * @return the mutations generated which are seeded in the program
	 * @throws Exception
	 */
	public Iterable<AstMutation> seed(Iterable<AstNode> locations) throws Exception {
		List<AstMutation> mutations = new ArrayList<AstMutation>();
		for(AstNode location : locations) {
			if(this.is_seeded_location(location)) {
				Iterable<AstMutation> mset = this.seed_mutations(location);
				for(AstMutation mutation : mset) mutations.add(mutation);
			}
		}
		return mutations;
	}
	
}
