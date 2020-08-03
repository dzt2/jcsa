package com.jcsa.jcparse.lang.ir.expr.refer;

import com.jcsa.jcparse.lang.ir.expr.CirExpression;

/**
 * <code>
 * 	|--	<i>refer_expression</i>											<br>
 * 	|--	|-- <i>name_expression</i>										<br>
 * 	|--	|--	|-- declarator_reference									<br>
 * 	|--	|--	|-- identifier_reference									<br>
 * 	|--	|--	|-- temporary_reference										<br>
 * 	|--	|--	|--	return_reference										<br>
 * 	|--	|-- field_expression											<br>
 * 	|--	|--	de_reference_expression		{unary_expression}				<br>
 * </code>
 * @author yukimula
 *
 */
public interface CirReferExpression extends CirExpression {
}
