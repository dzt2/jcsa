package com.jcsa.jcparse.flwa.context;

/**
 * The type of the path in function calling tree defines the constrain on each
 * path from the root function to any of its leafs in that tree shall satisfy.
 *
 * @author yukimula
 *
 */
public enum CirFunctionCallPathType {

	/** The path shall contains unique node such that none of duplicated nodes referring
	 *  to the same function in the program occur in any path from the root to its leaf. **/
	unique_path,
	/** The path shall contains unique edge such that none of duplicated edges referring
	 *  to the same calling context occur in any path from the root to any of its leafs. **/
	simple_path,
	/** None of constrains are forced to any path in the function calling tree. **/
	anyway_path,

}
