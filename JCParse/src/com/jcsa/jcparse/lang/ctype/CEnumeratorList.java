package com.jcsa.jcparse.lang.ctype;

/**
 * list of enumerators
 *
 * @author yukimula
 *
 */
public interface CEnumeratorList {
	/**
	 * get the number of enumerators in list
	 *
	 * @return
	 */
	public int size();

	/**
	 * get the kth enumerator from list
	 *
	 * @param k
	 * @return
	 * @throws Exception
	 *             : out of index
	 */
	public CEnumerator get_enumerator(int k) throws Exception;

	/**
	 * whether there is enumerator for this name
	 *
	 * @param name
	 * @return
	 */
	public boolean has_enumerator(String name);

	/**
	 * get the enumerator corresponding to the name
	 *
	 * @param name
	 * @return
	 * @throws Exception
	 *             : undefined name
	 */
	public CEnumerator get_enumerator(String name) throws Exception;

	/**
	 * add a new enumerator in the tail of the list
	 *
	 * @param enumerator
	 * @throws Exception
	 *             : duplicated name
	 */
	public void add_enumerator(CEnumerator enumerator) throws Exception;
}
