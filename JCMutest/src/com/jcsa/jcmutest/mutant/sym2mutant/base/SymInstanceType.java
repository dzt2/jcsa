package com.jcsa.jcmutest.mutant.sym2mutant.base;

/**
 * 	Type of symbolic instance in C-intermediate representation code:<br>
 * 	<code>
 * 	SymInstance							{execution}										<br>
 * 	|--	SymConstraint					(execution, condition)							<br>
 * 	|--	SymStateError					(execution, location)							<br>
 * 	|--	|--	SymPathError				(execution, statement)							<br>
 * 	|--	|--	|--	SymTrapError			trap_error(execution, statement)				<br>
 * 	|--	|--	|--	SymFlowError			flow_error(execution, orig_flow, muta_flow)		<br>
 * 	|--	|--	SymValueError				(execution, expression, orig_value, muta_value)	<br>
 * 	|--	|--	|--	SymExpressionError		expr_error(expr, orig_val, muta_val)			<br>
 * 	|--	|--	|--	SymReferenceError		refr_error(expr, orig_val, muta_val)			<br>
 * 	|--	|--	|--	SymStateValueError		stat_error(expr, orig_val, muta_val)			<br>
 * 	</code>
 * 	@author yukimula
 *
 */
public enum SymInstanceType {
	constraint,
	trap_error,
	flow_error,
	expr_error,
	refr_error,
	stat_error,
}
