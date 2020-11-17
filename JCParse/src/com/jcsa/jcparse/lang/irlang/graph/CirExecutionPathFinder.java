package com.jcsa.jcparse.lang.irlang.graph;

/**
 * 	It implements the path finding algorithms and provide following methods for users to apply:<br>
 * 	<code>
 * 	+===========================================================================================+	<br>
 * 	|	Decidable Path Extension																|	<br>
 * 	+-------------------------------------------------------------------------------------------+	<br>
 * 	|	int df_extend(CirExecutionPath)															|	<br>
 * 	|	---	Using decidable path extension to extend the prefix path forward until the point of	|	<br>
 * 	|		which output flows are undecidable.													|	<br>
 * 	+-------------------------------------------------------------------------------------------+	<br>
 * 	|	int db_extend(CirExecutionPath)															|	<br>
 * 	|	---	Using decidable path extension to extend the following path backward to the point 	|	<br>
 *  |		from which the input flows to the path.source become undecidable.					|	<br>
 * 	+-------------------------------------------------------------------------------------------+	<br>
 * 	|	boolean df_extend(CirExecutionPath, CirExecution)										|	<br>
 * 	|	---	Using decidable path extension to extend the prefix path forward until the target or|	<br>
 * 	|		the point of which output flows become undecidable.									|	<br>
 * 	+-------------------------------------------------------------------------------------------+	<br>
 * 	|	boolean db_extend(CirExecutionPath, CirExecution)										|	<br>
 * 	|	---	Using decidable path extension to extend the following path backward to the source	|	<br>
 * 	|		or the point from which the input flows become undecidable.							|	<br>
 * 	+-------------------------------------------------------------------------------------------+	<br>
 * 	|	boolean df_extend(CirExecutionPath, CirExecutionFlow)									|	<br>
 * 	|	---	Using decidable path extension to extend the prefix path forward until the flow	be	|	<br>
 * 	|		reached or the point of which output flows become undecidable.						|	<br>
 * 	+-------------------------------------------------------------------------------------------+	<br>
 * 	|	boolean db_extend(CirExecutionPath, CirExecutionFlow)									|	<br>
 * 	|	---	Using decidable path extension to extend the following path backward to the flow be	|	<br>
 * 	|		reached or the point of which input flows become undecidable.						|	<br>
 * 	+-------------------------------------------------------------------------------------------+	<br>
 * 	|	CirExecutionPath df_extend(CirExecution)												|	<br>
 * 	|	CirExecutionPath db_extend(CirExecution)												|	<br>
 * 	+===========================================================================================+	<br>
 * 	+===========================================================================================+	<br>
 * 	+===========================================================================================+	<br>
 * 	+===========================================================================================+	<br>
 * 	+===========================================================================================+	<br>
 * 	</code>
 * 	@author yukimula
 *
 */
public class CirExecutionPathFinder {

}
