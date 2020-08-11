package com.jcsa.jcmutest.mutant.rip2mutant;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;

/**
 * It provides interfaces to extend the mutations as coverage,
 * weak and strong mutation (standard version) such to compress
 * the number of mutants being used.
 * 
 * @author yukimula
 *
 */
public abstract class MutationExtender {
	
	/* extension methods */
	/**
	 * @param source
	 * @return the mutation that is killed once the statement where it 
	 * 			is seeded is executed
	 * @throws Exception
	 */
	protected abstract AstMutation coverage_mutation(AstMutation source) throws Exception;
	/**
	 * @param source
	 * @return the mutation that is killed once the state error is
	 * 			caused during the mutant is tested.
	 * @throws Exception
	 */
	protected abstract AstMutation weak_mutation(AstMutation source) throws Exception;
	/**
	 * @param source
	 * @return the mutation that is killed iff. the original mutant is killed
	 * @throws Exception
	 */
	protected abstract AstMutation strong_mutation(AstMutation source) throws Exception;
	
	/* TODO utility methods */
	
	
}
