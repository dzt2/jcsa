package com.jcsa.jcparse.lang.cirlang.expr.refer;

import com.jcsa.jcparse.lang.cirlang.expr.CirExpression;

/**
 * 	<code>
 * 	|--	<i>refer_expression</i>											<br>
 * 	|--	|--	<i>name_expression</i>				{get_name(boolean)}		<br>
 * 	|--	|--	|-- identifier_expression									<br>
 * 	|--	|--	|--	declarator_expression									<br>
 * 	|--	|--	|--	temporary_expression									<br>
 * 	|--	|--	|--	return_refer_expression									<br>
 * 	|--	|--	defer_expression					[unary_expression]		<br>
 * 	|--	|--	field_expression											<br>
 * 	</code>
 * 	@author yukimula
 *
 */
public interface CirReferExpression extends CirExpression { }
