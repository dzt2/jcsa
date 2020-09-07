package com.jcsa.jcmutest.mutant.sel2mutant.lang;

/**
 * <code>
 * 	+----------------------------------------------------------------------+<br>
 * 	SelToken																<br>
 * 	|--	SelKeyword						{keyword: SelKeywords}				<br>
 * 	|--	SelDataType						{ctype: CType; vtype: SelDataType}	<br>
 * 	|--	SelOperator						{operator: COperator}				<br>
 * 	|--	SelExpression					{expression: SymExpression}			<br>
 * 	|--	SelStatement					{execution; statement: CirStatement}<br>
 * 	+----------------------------------------------------------------------+<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SelToken extends SelNode { }
