package com.jcsa.jcparse.lang.cirlang.unit;

import com.jcsa.jcparse.lang.cirlang.CirNode;

/**
 * <code>
 * 	<i>unit</i>															<br>
 * 	|--	translation_unit												<br>
 * 	|--	function_definition												<br>
 * 	|-- statement_list													<br>
 * 	|-- field									{name: String}			<br>
 * 	|-- label									{label: int}			<br>
 * 	|--	type									{data_type: CType}		<br>
 * 	|--	argument_list													<br>
 * 	<br>
 * </code>
 * @author yukimula
 *
 */
public interface CirUnit extends CirNode {
}
