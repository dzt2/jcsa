package com.jcsa.jcmutest.mutant.uni2mutant.base;

/**
 * The class of state-location to formalize the CirAbstractStore.
 * 
 * @author yukimula
 *
 */
public enum CirAbstractStoreClass {
	cond,
	expr,
	refr,
	vdef,
	assg,
	call,
	wait,
	retr,
	ifte,
	loop,
	skip,
	brac,
	conv,
	labs,
	args,
	// TODO implement more class of CirAbstractStore
}
