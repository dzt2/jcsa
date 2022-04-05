package com.jcsa.jcmutest.mutant.sta2mutant.base;

/**
 * 	The category of abstract execution state defined in mutation testing.		<br>
 * 	<br>
 * 	<code>
 * 		covt(statement,  min_times): to cover the statement for minimal-times;	<br>
 * 		eval(statement,  condition): to evaluate whether the condition is met;	<br>
 * 		exec(statement;  bool_exec): whether to execute this statement or not;	<br>
 * 		expr(expression; sym_value): the expression must hold symbolic values;	<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public enum AbsExecutionClass {
	covt,
	eval,
	exec,
	expr,
}
