package com.jcsa.jcparse.lopt.analysis.flow;

/**
 * Each node in relational graph represents a program element in testing, including:<br>
 * 	(1)	<code>Statement</code>: a statement being executed in C-like intermediate representation, except
 * 		the useless goto and tag statement.<br>
 * 	(2)	<code>Reference</code>: a reference expression used or defined in C-like intermediate representation.<br>
 * 	(3)	<code>Expression</code>: a non-reference expression used in C-like intermediate representation.<br> 
 * @author yukimula
 *
 */
public enum CRelationNodeType {
	/** statement **/					Statement,
	/** reference-expression **/		Reference,
	/** non-reference expression **/	Expression,
}
