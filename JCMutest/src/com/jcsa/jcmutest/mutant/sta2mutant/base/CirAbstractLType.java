package com.jcsa.jcmutest.mutant.sta2mutant.base;

/**
 * 	It specifies the category of storage location to preserve value of state.	<br>
 * 	
 * 	@author yukimula
 *
 */
public enum CirAbstractLType {
	
	bool,
	argv,
	expr,
	refr,
	
	assg,
	ifte,
	call,
	wait,
	
	bend,
	gotw,
	labl,
	conv,
	
}
