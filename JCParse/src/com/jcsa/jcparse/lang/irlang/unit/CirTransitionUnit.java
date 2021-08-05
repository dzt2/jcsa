package com.jcsa.jcparse.lang.irlang.unit;

import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * transition_unit |--> (external_unit)*
 * @author yukimula
 *
 */
public interface CirTransitionUnit extends CirNode {

	/**
	 * get the number of external units in the program
	 * @return
	 */
	public int number_of_units();
	/**
	 * get the kth external unit in the program as either init_assign_statement
	 * or the function definition
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public CirExternalUnit get_unit(int k) throws IndexOutOfBoundsException;
	/**
	 * add a new external unit under the program root node
	 * @param unit
	 * @throws IllegalArgumentException
	 */
	public void add_unit(CirExternalUnit unit) throws IllegalArgumentException;

}
