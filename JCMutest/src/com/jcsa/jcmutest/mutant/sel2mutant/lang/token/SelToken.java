package com.jcsa.jcmutest.mutant.sel2mutant.lang.token;

import com.jcsa.jcmutest.mutant.sel2mutant.lang.SelNode;

/**
 * <code>
 * 	+----------------------------------------------------------------------+<br>
 * 	SelToken																<br>
 * 	|--	SelDataType					{data_type, value_type}					<br>
 * 	|--	SelKeyword					{keyword: SelKeywords}					<br>
 * 	|--	SelOperator					{operator: COperator}					<br>
 * 	|--	SelExpression				{expression: SymExpression}				<br>
 * 	|--	SelStatement				{statement; execution}					<br>
 * 	+----------------------------------------------------------------------+<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SelToken extends SelNode { }
