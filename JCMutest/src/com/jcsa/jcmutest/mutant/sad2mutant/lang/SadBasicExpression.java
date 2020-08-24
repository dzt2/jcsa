package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * <code>
 * 	|--	SadBasicExpression												<br>
 * 	|--	|--	SadIdExpression				{identifier: String}			<br>
 * 	|--	|--	SadConstant					{constant: CConstant}			<br>
 * 	|--	|--	SadLiteral					{literal: String}				<br>
 * 	|--	|--	SadDefaultValue												<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SadBasicExpression extends SadExpression {
	
	protected SadBasicExpression(CirNode source, CType data_type) {
		super(source, data_type);
	}
	
}
