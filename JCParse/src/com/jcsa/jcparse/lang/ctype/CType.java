package com.jcsa.jcparse.lang.ctype;

/**
 * data type for objects defined or declared in C <br>
 * 1. basic type: int, long, long long, double, float <br>
 * 2. array type: array(type, length) <br>
 * 3. point type: point(type) <br>
 * 4. function: F(type, type_list) <br>
 * 5. struct: struct(attr_list) <br>
 * 6. union: union(attr_list) <br>
 * 7. enum: enum(enumerator_list) <br>
 * 8. qualifier: <i>qualifier</i>(type), where <i>qualifier</i> = <b>const</b> |
 * <b>volatile</b> <br>
 * 9. storage: <i>storage</i>(type) <br>
 *
 * @author yukimula
 */
public interface CType {
	/**
	 * whether the type is complete and defined
	 *
	 * @return
	 */
	public boolean is_defined();

	/**
	 * @return code of the data type
	 */
	public String generate_code();
}
