package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * <code>
 * 	+------------------------------------------------------------------+<br>
 * 	SadExpression						{data_type: CType}				<br>
 * 	|--	SadBasicExpression												<br>
 * 	|--	|--	SadIdExpression				{identifier: String}			<br>
 * 	|--	|--	SadConstant					{constant: CConstant}			<br>
 * 	|--	|--	SadLiteral					{literal: String}				<br>
 * 	|--	|--	SadDefaultValue												<br>
 * 	|--	SadUnaryExpression				{+,-,~,!,*,&,cast}				<br>
 * 	|-- SadBinaryExpression				{-,/,%,<<,>>,<,<=,>,>=,==,!=}	<br>
 * 	|-- SadMultiExpression				{+,*,&,|,^,&&,||}				<br>
 * 	|--	SadFieldExpression												<br>
 * 	|--	SadInitializerList												<br>
 * 	|--	SadCallExpression												<br>
 * 	+------------------------------------------------------------------+<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public abstract class SadExpression extends SadNode {
	
	/** the data type of the value of the expression **/
	private CType data_type;
	protected SadExpression(CirNode source, CType data_type) {
		super(source);
		this.data_type = data_type;
	}
	
	/**
	 * @return the data type of the value of the expression
	 */
	public CType get_data_type() {
		return this.data_type;
	}
	
}
