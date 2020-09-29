package com.jcsa.jcmutest.mutant.cir2mutant.struct;

public enum CirMutationFlow {
	
	/** from operand to the expression where it is used **/		operand_link,
	/** from right-value to left-value in the assignment **/	assign_link,
	/** from definition point to its usage point of other **/	use_def_link,
	/** from condition in if-statement to its control flow **/	condition_link,
	
}
