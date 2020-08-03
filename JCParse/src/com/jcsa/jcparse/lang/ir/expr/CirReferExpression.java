package com.jcsa.jcparse.lang.ir.expr;

/**
 * 
 * 	|--	<i>reference_expression</i>									<br>
 * 	|--	|--	<i>name_expression</i>									<br>
 * 	|--	|--	|--	declarator_expression								<br>
 * 	|--	|--	|--	identifier_expression								<br>
 * 	|--	|--	|--	implicator_expression								<br>
 * 	|--	|--	|--	return_ref_expression								<br>
 * 	|--	|--	deference_expression	[unary_expression]				<br>
 * 	|--	|--	field_expression										<br>
 * 
 * @author yukimula
 *
 */
public interface CirReferExpression extends CirExpression {
}
