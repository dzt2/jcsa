package com.jcsa.jcmutest.mutant.sta2mutant.base;

/**
 * 	The category of abstract execution state defined in mutation testing.		<br>
 * 	<br>
 * 	<code>
 * 		covt(statement,  min_times): to cover the statement for minimal-times;	<br>
 * 		rect(statement,  max_times): to limit the times for running statement;	<br>
 * 		eval(statement,  condition): to evaluate whether the condition is met;	<br>
 * 		vald(statement;  condition): to see whether this condition always met;	<br>
 * 		exec(statement;  bool_exec): whether to execute this statement or not;	<br>
 * 		trap(statement;  exception): the statement will trap and exit for all;	<br>
 * 		expr(expression; sym_value): the expression must hold symbolic values;	<br>
 * 		refr(reference;  sym_value): the reference to save the symbolic value;	<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public enum AbsExecutionClass {
	covt,
	rect,
	eval,
	vald,
	exec,
	trap,
	expr,
	refr,
}
