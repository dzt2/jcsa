package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcmutest.sedlang.SedNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * <code>
 * 	+--------------------------------------------------------------------------+<br>
 * 	|-- <i>SedExpression</i>							{data_type: CType}		<br>
 * 	|--	|--	<i>SedBasicExpression</i>											<br>
 * 	|--	|--	|-- SedIdentifier							{name: String}			<br>
 * 	|--	|--	|--	SedConstant								{constant: CConstant}	<br>
 * 	|--	|--	|--	SedLiteral								{literal: String}		<br>
 * 	|--	|--	|--	SedDefaultValue													<br>
 * 	|--	|--	SedUnaryExpression				{operator: +, -, ~, !, *, &, cast}	<br>
 * 	|--	|--	SedBinaryExpression				{operator: -, /, %, <<, >>, ...}	<br>
 * 	|--	|--	SedMultiExpression				{operator: +, *, &, |, ^, &&, ||}	<br>
 * 	|--	|--	SedCallExpression													<br>
 * 	|--	|--	SedFieldExpression													<br>
 * 	|--	|--	SedInitializerList													<br>
 * 	+--------------------------------------------------------------------------+<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SedExpression extends SedNode {
	
	/** the data type of the value hold by the expression **/
	private CType data_type;
	protected SedExpression(CirNode source, CType data_type) {
		super(source);
		this.data_type = data_type;
	}
	
	/* getter */
	/**
	 * @return the data type of the value of the expression
	 */
	public CType get_data_type() { return this.data_type; }
	
}
