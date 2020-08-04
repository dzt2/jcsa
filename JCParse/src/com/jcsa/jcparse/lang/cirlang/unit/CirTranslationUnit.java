package com.jcsa.jcparse.lang.cirlang.unit;

/**
 * translation_unit |-- (function_definition)+
 * 
 * @author yukimula
 *
 */
public interface CirTranslationUnit extends CirUnit {
	
	/** the name of the function that initializes the global variables **/
	public static final String init_function_name = "#init";
	
	/**
	 * @return the number of function definitions in the program
	 */
	public int number_of_function_definitions();
	
	/**
	 * @param k
	 * @return the kth function definition in the translation unit
	 * @throws IndexOutOfBoundsException
	 */
	public CirFunctionDefinition get_function_definition(int k) throws IndexOutOfBoundsException;
	
}
