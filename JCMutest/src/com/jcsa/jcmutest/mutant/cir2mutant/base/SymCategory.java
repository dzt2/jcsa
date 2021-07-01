package com.jcsa.jcmutest.mutant.cir2mutant.base;

/**
 * 	Category of symbolic condition can be:<br>
 * 	<br>
 * 	<code>
 * 	evaluation	---	It defines a logical expression evaluated at some code location.<br>
 * 	data_error	---	It specifies a constraint for observing data state errors.		<br>
 * 	path_error	---	It specifies a constraint for observing path state errors.		<br>
 * 	kill_fault	---	It specifies a oracle to decide whether the fault is detected.	<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public enum SymCategory {
	evaluation,
	data_error,
	path_error,
	kill_fault,
}
