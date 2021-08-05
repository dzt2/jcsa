package com.jcsa.jcparse.parse.code;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.astree.unit.AstExternalUnit;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.astree.unit.AstTranslationUnit;
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

	/**
	 * @param tree the program being instrumented.
	 * @param start_function the function where jcm_open is injected.
	 * @param instrument_functions the set of functions being instrumented.
	 * @param output_file the path where the instrumental result is written.
	 * @return generate the source code with instrumented
	 * @throws Exception
	 */
	public static String instrument_code(AstTree tree, AstFunctionDefinition start_function,
			Iterable<AstFunctionDefinition> instrument_functions, File output_file) throws Exception {
		return AstCodeInstrumentor.instrument(tree, start_function, instrument_functions, output_file);
	}

	/**
	 * @param tree tree the program being instrumented.
	 * @param instrument_functions the set of functions being instrumented.
	 * @param output_file the path where the instrumental result is written.
	 * @return generate the instrumental code of the program of which start_function is main.
	 * @throws Exception
	 */
	public static String instrument_code(AstTree tree,
			Iterable<AstFunctionDefinition> instrument_functions, File output_file) throws Exception {
		return AstCodeInstrumentor.instrument(tree, tree.get_main_function(), instrument_functions, output_file);
	}

	/**
	 * @param tree tree tree the program being instrumented.
	 * @param output_file the path where the instrumental result is written.
	 * @return start_function is main and all the functions are instrumented.
	 * @throws Exception
	 */
	public static String instrument_code(AstTree tree, File output_file) throws Exception {
		Collection<AstFunctionDefinition> ifunctions = new ArrayList<>();
		AstTranslationUnit ast_root = tree.get_ast_root();
		for(int k = 0; k < ast_root.number_of_units(); k++) {
			AstExternalUnit unit = ast_root.get_unit(k);
			if(unit instanceof AstFunctionDefinition) {
				ifunctions.add((AstFunctionDefinition) unit);
			}
		}
		return AstCodeInstrumentor.instrument(tree, tree.get_main_function(), ifunctions, output_file);
	}

}
