package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * <code>
 * 	SymBasicExpression													<br>
 * 	|--	SymIdExpression					{name: String}					<br>
 * 	|--	SymConstant						{constant: CConstant}			<br>
 * 	|--	SymLiteral						{literal: String}				<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SymBasicExpression extends SymExpression {

	protected SymBasicExpression(CType data_type) throws IllegalArgumentException {
		super(data_type);
	}
	
}
