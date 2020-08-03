package com.jcsa.jcparse.lang.ir.unit;

import com.jcsa.jcparse.lang.ir.CirNode;

/**
 * The unit is non-typed expression and non-statement node in C-intermediate
 * representation language, including:<br>
 * <code>
 * 	+------------------------------------------------------------------+<br>
 * 	<i>unit</i>															<br>
 * 	|--	field							{name: String}					<br>
 * 	|--	label							{label: int}					<br>
 * 	|--	type							{type: CType}					<br>
 * 	|--	argument_list													<br>
 * 	|--	statement_list													<br>
 * 	|--	function_definition												<br>
 * 	|--	translation_unit												<br>
 * 	+------------------------------------------------------------------+<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public interface CirUnit extends CirNode {
}
