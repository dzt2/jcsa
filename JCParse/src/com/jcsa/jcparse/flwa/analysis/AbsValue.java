package com.jcsa.jcparse.flwa.analysis;

/**
 * Abstract value hold by each statement or variable during the abstract execution.
 * @author yukimula
 *
 */
public interface AbsValue {

	/**
	 * set the value as specified value and report whether the value
	 * is different with respect to the value before it was updated.
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public boolean set(AbsValue value) throws Exception;

}
