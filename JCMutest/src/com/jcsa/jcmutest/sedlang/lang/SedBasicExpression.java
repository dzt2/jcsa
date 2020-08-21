package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * <code>
 * 	|--	|--	<i>SedBasicExpression</i>											<br>
 * 	|--	|--	|-- SedIdentifier							{name: String}			<br>
 * 	|--	|--	|--	SedConstant								{constant: CConstant}	<br>
 * 	|--	|--	|--	SedLiteral								{literal: String}		<br>
 * 	|--	|--	|--	SedDefaultValue													<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SedBasicExpression extends SedExpression {

	protected SedBasicExpression(CirNode source, CType data_type) {
		super(source, data_type);
	}

}
