package com.jcsa.jcparse.lang.ir.unit;

import com.jcsa.jcparse.lang.ir.CirNode;

/**
 * translation_unit |-- function_definition*
 * 
 * @author yukimula
 *
 */
public interface CirTranslationUnit extends CirNode {
	
	/**
	 * @return the number of the function definitions in the translation unit
	 */
	public int number_of_function_definitions();
	
	/**
	 * @param k
	 * @return the kth function definition in the translation unit
	 * @throws IndexOutOfBoundsException
	 */
	public CirFunctionDefinition get_function_definition(int k) throws IndexOutOfBoundsException;
	
}
