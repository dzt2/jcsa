package com.jcsa.jcparse.lang.ctype;

/**
 * set of field in struct | union type
 *
 * @author yukimula
 */
public interface CFieldBody {
	/**
	 * get the number of fields in body
	 *
	 * @return
	 */
	public int size();

	/**
	 * get the ith field
	 *
	 * @param k
	 * @return
	 * @throws Exception
	 */
	public CField get_field(int k) throws Exception;

	/**
	 * whether there is field corresponding to the name
	 *
	 * @param name
	 * @return
	 */
	public boolean has_field(String name);

	/**
	 * get the field by its name
	 *
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public CField get_field(String name) throws Exception;

	/**
	 * add a new field in the body
	 *
	 * @param field
	 * @throws Exception
	 *             : duplicated name
	 */
	public void add_field(CField field) throws Exception;
}
