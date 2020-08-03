package com.jcsa.jcparse.lang.ir.unit;

/**
 * translation_unit |-- function_definition*
 * @author yukimula
 *
 */
public interface CirTranslationUnit extends CirUnit {
	
	/**
	 * @return the number of function definitions in the program
	 */
	public int number_of_function_definitions();
	
	/**
	 * @param k
	 * @return the kth function definition in the program
	 * @throws IndexOutOfBoundsException
	 */
	public CirFunctionDefinition get_function_definition(int k) throws IndexOutOfBoundsException;
	
}
