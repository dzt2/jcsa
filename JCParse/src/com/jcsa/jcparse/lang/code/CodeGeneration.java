package com.jcsa.jcparse.lang.code;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * It provides basic interfaces to generate code for both AST and CIR nodes.
 * 
 * @author yukimula
 *
 */
public class CodeGeneration {
	
	/**
	 * @param code
	 * @return remove the \n \r and \t to just space
	 */
	public static String strip_code(String code) {
		StringBuilder buffer = new StringBuilder();
		
		for(int k = 0; k < code.length(); k++) {
			char ch = code.charAt(k);
			if(ch == '\t' || ch == '\n') {
				ch = ' ';
			}
			buffer.append(ch);
		}
		
		return buffer.toString();
	}
	
	/**
	 * @param ast_node
	 * @return code generated to describe the abstract syntactic node
	 * @throws Exception
	 */
	public static String generate_code(AstNode ast_node) throws Exception {
		return AstCodeGenerator.generate(ast_node);
	}
	
	/**
	 * @param simplified
	 * @param cir_node
	 * @return code generated to describe the C-intermediate representation
	 * @throws Exception
	 */
	public static String generate_code(boolean simplified, CirNode cir_node) throws Exception {
		return CirCodeGenerator.generate(simplified, cir_node);
	}
	
	/**
	 * @param data_type
	 * @return code that describes the data type being printed.
	 * @throws Exception
	 */
	public static String generate_code(CType data_type) throws Exception {
		return TypeCodeGenerator.generate(data_type);
	}
	
}
