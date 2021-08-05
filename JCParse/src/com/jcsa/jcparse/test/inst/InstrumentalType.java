package com.jcsa.jcparse.test.inst;

/**
 * The type of location being instrumented.
 *
 * @author yukimula
 *
 */
public enum InstrumentalType {
	function,
	statement,
	expression,
	assignment,
	reference,
	condition,
	sequence,
}
