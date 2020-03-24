package com.jcsa.jcparse.lopt.models.influence;

/**
 * The type of influence node relies on the type of CirNode.
 * <code>
 * 	1. CirStatement		--> statement
 * 	2. CirExpression	--> expression
 * 	3. CirLabel			--> label
 * 	4. CirField			--> field
 * </code>
 * @author yukimula
 *
 */
public enum CInfluenceNodeType {
	statement,
	expression,
	field,
	label,
}
