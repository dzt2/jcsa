package com.jcsa.jcmutest.backups;

/**
 * <code>
 * 	+----------------------------------------------------------------------+<br>
 * 	SecToken																<br>
 * 	|--	SecKeyword						{keyword: SecKeywords}				<br>
 * 	|--	SecType							{vtype: SecValueTypes}				<br>
 * 	|--	SecOperator						{operator: COperator}				<br>
 * 	|--	SecExpression					{expression: SymExpression}			<br>
 * 	|--	SecStatement					{statement: CirStatement}			<br>
 * 	+----------------------------------------------------------------------+<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SecToken extends SecNode { }
