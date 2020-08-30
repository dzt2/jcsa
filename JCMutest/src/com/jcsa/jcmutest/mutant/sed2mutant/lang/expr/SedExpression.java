package com.jcsa.jcmutest.mutant.sed2mutant.lang.expr;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * <code>
 * 	+------------------------------------------------------------------+<br>
 * 	SedExpression					{data_type: CType}					<br>
 * 	|--	SedBasicExpression												<br>
 * 	|--	|--	SedIdExpression			{name: String}						<br>
 * 	|--	|--	SedConstant				{bool|char|int|long|float|double}	<br>
 * 	|--	|--	SedLiteral				{literal: String}					<br>
 * 	|--	|--	SedDefaultValue												<br>
 * 	|--	SedUnaryExpression			{-, ~, !, &, *, cast}				<br>
 * 	|--	SedBinaryExpression			[+, -, *, /, ..., ==, !=]			<br>
 * 	|--	SedFieldExpression												<br>
 * 	|--	SedInitializerList												<br>
 * 	|--	SedCallExpression												<br>
 * 	+------------------------------------------------------------------+<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SedExpression extends SedNode {
	
	private CType data_type;
	public SedExpression(CirNode cir_source, CType data_type) {
		super(cir_source);
		this.data_type = data_type;
	}
	
	
	/**
	 * @return the data type of the value of the expression
	 */
	public CType get_data_type() { return this.data_type; }
	
}
