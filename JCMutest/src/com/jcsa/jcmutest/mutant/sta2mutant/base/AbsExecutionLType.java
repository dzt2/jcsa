package com.jcsa.jcmutest.mutant.sta2mutant.base;

/**
 * 	The type of the location to preserve execution state.
 * 	
 * 	@author yukimula
 *
 */
public enum AbsExecutionLType {
	
	/** the condition of IF, CASE or LOOP statement **/	bool,
	/** the expression taken as argument of calling **/	argv,
	/** the usual used expression in the statements **/	expr,
	/** the reference taken as the definition point **/	refr,
	
	/** the assigment statement to assign reference **/	assg,
	/** IF-statement, CASE-statement Loop-statement **/	ifte,
	/** the statement to call function by arguments **/	call,
	/** the statement where the return-value is get **/	wait,
	
	/** goto-statement for reaching an end or other **/	gots,
	/** the beg-statement and end-statement of func **/	bend,
	/** the labeled statement for goto that reaches **/	labl,
	/** other statement as the tag of CFG-statement **/	tags,
	
}
