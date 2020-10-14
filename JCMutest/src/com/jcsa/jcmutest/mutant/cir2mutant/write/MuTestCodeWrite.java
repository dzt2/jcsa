package com.jcsa.jcmutest.mutant.cir2mutant.write;

import java.io.File;
import java.io.FileWriter;

import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.astree.base.AstIdentifier;
import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.AstTypeName;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstOperator;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirConstExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirField;
import com.jcsa.jcparse.lang.irlang.expr.CirNameExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirType;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCall;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCallGraph;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabel;

/**
 * Used to write information of source code, AstTree, CirTree and CirFunctionCallGraph
 * on file for python scripts to read and translation.
 * 
 * @author yukimula
 *
 */
public class MuTestCodeWrite {
	
	/**
	 * encode the parameter into standard form and write to the file stream
	 * @param parameter
	 * @param writer
	 * @throws Exception
	 */
	private static void write_parameter(Object parameter, FileWriter writer) throws Exception {
		writer.write(MuTestWritingUtils.encode(parameter));
	}
	
	/**
	 * write the code in ifile to tfile for reading
	 * @param ifile
	 * @param tfile
	 * @throws Exception
	 */
	protected static void write_source_code(File ifile, File tfile) throws Exception {
		if(ifile == null || !ifile.exists())
			throw new IllegalArgumentException("Invalid ifile: null");
		else if(tfile == null)
			throw new IllegalArgumentException("Invalid tfile: null");
		else {
			FileOperations.copy(ifile, tfile);
		}
	}
	
	/**
	 * id type beg_index end_index type token [children]
	 * @param ast_node
	 * @param writer
	 * @throws Exception
	 */
	private static void write_ast_node(AstNode ast_node, FileWriter writer) throws Exception {
		/* id */
		writer.write(ast_node.get_key() + "\t");
		
		/* type of AST */
		String ast_type = ast_node.getClass().getSimpleName();
		ast_type = ast_type.substring(3, ast_type.length() - 4);
		writer.write(ast_type.strip() + "\t");
		
		/* beg_index end_index */
		int beg_index = ast_node.get_location().get_bias();
		int end_index = beg_index + ast_node.get_location().get_length();
		writer.write(beg_index + "\t" + end_index + "\t");
		
		/* data type */
		CType data_type;
		if(ast_node instanceof AstExpression) {
			data_type = ((AstExpression) ast_node).get_value_type();
		}
		else if(ast_node instanceof AstTypeName) {
			data_type = ((AstTypeName) ast_node).get_type();
		}
		else {
			data_type = null;
		}
		write_parameter(data_type, writer); writer.write("\t");
		
		/* token */
		Object token;
		if(ast_node instanceof AstIdentifier) {
			token = ((AstIdentifier) ast_node).get_name();
		}
		else if(ast_node instanceof AstConstant) {
			token = ((AstConstant) ast_node).get_constant();
		}
		else if(ast_node instanceof AstKeyword) {
			token = ((AstKeyword) ast_node).get_keyword();
		}
		else if(ast_node instanceof AstPunctuator) {
			token = ((AstPunctuator) ast_node).get_punctuator();
		}
		else if(ast_node instanceof AstOperator) {
			token = ((AstOperator) ast_node).get_operator();
		}
		else {
			token = null;
		}
		write_parameter(token, writer); writer.write("\t");
		
		/* children */
		writer.write("[");
		for(int k = 0; k < ast_node.number_of_children(); k++) {
			writer.write(" " + ast_node.get_child(k).get_key());
		}
		writer.write(" ]");
	}
	
	/**
	 * write the abstract syntax tree to the tfile
	 * @param ast_tree
	 * @param tdir
	 * @throws Exception
	 */
	protected static void write_ast_tree(AstTree ast_tree, File tfile) throws Exception {
		if(ast_tree == null)
			throw new IllegalArgumentException("Invalid ast_tree: null");
		else if(tfile == null)
			throw new IllegalArgumentException("Invalid tfile: null");
		else {
			FileWriter writer = new FileWriter(tfile);
			for(int k = 0; k < ast_tree.number_of_nodes(); k++) {
				AstNode ast_node = ast_tree.get_node(k);
				write_ast_node(ast_node, writer);
				writer.write("\n");
			}
			writer.close();
		}
	}
	
	/**
	 * id type ast_source type token children
	 * @param cir_node
	 * @param writer
	 * @throws Exception
	 */
	private static void write_cir_node(CirNode cir_node, FileWriter writer) throws Exception {
		/* id */
		writer.write(cir_node.get_node_id() + "\t");
		
		/* cir class */
		String cir_type = cir_node.getClass().getSimpleName();
		cir_type = cir_type.substring(3, cir_type.length() - 4);
		writer.write(cir_type + "\t");
		
		/* ast_source */
		write_parameter(cir_node.get_ast_source(), writer);
		writer.write("\t");
		
		/* data type */
		CType data_type;
		if(cir_node instanceof CirExpression) {
			data_type = ((CirExpression) cir_node).get_data_type();
		}
		else if(cir_node instanceof CirType) {
			data_type = ((CirType) cir_node).get_typename();
		}
		else {
			data_type = null;
		}
		write_parameter(data_type, writer); writer.write("\t");
		
		/* token */
		Object token;
		if(cir_node instanceof CirField) {
			token = ((CirField) cir_node).get_name();
		}
		else if(cir_node instanceof CirLabel) {
			token = ((CirLabel) cir_node).get_target_node_id();
		}
		else if(cir_node instanceof CirNameExpression) {
			token = ((CirNameExpression) cir_node).get_name();
		}
		else if(cir_node instanceof CirConstExpression) {
			token = ((CirConstExpression) cir_node).get_constant();
		}
		else if(cir_node instanceof CirComputeExpression) {
			token = ((CirComputeExpression) cir_node).get_operator();
		}
		else {
			token = null;
		}
		write_parameter(token, writer); writer.write("\t");
		
		/* children */
		writer.write("[");
		for(int k = 0; k < cir_node.number_of_children(); k++) {
			writer.write(" " + cir_node.get_child(k).get_node_id());
		}
		writer.write(" ]");
	}
	
	/**
	 * write the C-intermediate representation to target file
	 * @param cir_tree
	 * @param tfile
	 * @throws Exception
	 */
	protected static void write_cir_tree(CirTree cir_tree, File tfile) throws Exception {
		if(cir_tree == null)
			throw new IllegalArgumentException("Invalid ast_tree: null");
		else if(tfile == null)
			throw new IllegalArgumentException("Invalid tfile: null");
		else {
			FileWriter writer = new FileWriter(tfile);
			for(int k = 0; k < cir_tree.size(); k++) {
				CirNode cir_node = cir_tree.get_node(k);
				write_cir_node(cir_node, writer);
				writer.write("\n");
			}
			writer.close();
		}
	}
	
	/**
	 * [ type source target ]
	 * @param flow
	 * @param writer
	 * @throws Exception
	 */
	private static void write_execution_flow(CirExecutionFlow flow, FileWriter writer) throws Exception {
		writer.write("[");
		writer.write(" " + flow.get_type());
		writer.write(" " + flow.get_source());
		writer.write(" " + flow.get_target());
		writer.write(" ]");
	}
	
	/**
	 * id cir_stmt
	 * @param execution
	 * @param writer
	 * @throws Exception
	 */
	private static void write_execution_node(CirExecution execution, FileWriter writer) throws Exception {
		writer.write(execution.toString() + "\t");
		writer.write(execution.get_statement().get_node_id());
	}
	
	/**
	 * [call_execution, wait_execution]
	 * @param call
	 * @param writer
	 * @throws Exception
	 */
	private static void write_function_call(CirFunctionCall call, FileWriter writer) throws Exception {
		writer.write("[");
		writer.write(" " + call.get_call_execution());
		writer.write(" " + call.get_wait_execution());
		writer.write(" ]");
	}
	
	/**
	 * #Func name
	 * {#Exec exe_id cir_statement}+
	 * {#Flow [type source target]}+
	 * {#Call [call_exe, wait_exe]}+
	 * #EndFunc
	 * @param function
	 * @param writer
	 * @throws Exception
	 */
	private static void write_function(CirFunction function, FileWriter writer) throws Exception {
		writer.write("#Func\t" + function.get_name() + "\n");
		
		for(CirExecution execution : function.get_flow_graph().get_executions()) {
			writer.write("#Exec\t");
			write_execution_node(execution, writer);
			writer.write("\n");
		}
		
		for(CirExecution execution : function.get_flow_graph().get_executions()) {
			for(CirExecutionFlow flow : execution.get_ou_flows()) {
				writer.write("#Flow\t");
				write_execution_flow(flow, writer);
				writer.write("\n");
			}
		}
		
		for(CirFunctionCall call : function.get_ou_calls()) {
			writer.write("#Call\t");
			write_function_call(call, writer);
			writer.write("\n");
		}
		
		writer.write("#EndFunc\n\n");
	}
	
	/**
	 * write the function call graph of program into tfile
	 * @param graph
	 * @param tfile
	 * @throws Exception
	 */
	protected static void write_function_call_graph(CirFunctionCallGraph graph, File tfile) throws Exception {
		if(graph == null)
			throw new IllegalArgumentException("Invalid graph: null");
		else if(tfile == null)
			throw new IllegalArgumentException("Invalid tfile: null");
		else {
			FileWriter writer = new FileWriter(tfile);
			for(CirFunction function : graph.get_functions()) {
				write_function(function, writer);
			}
			writer.close();
		}
	}
	
	/**
	 * write source code, abstract syntax tree, C-intermediate representation
	 * and the function call graph (as well as control flow graph) to the files
	 * in the specified directory
	 * @param code_file
	 * @param tdir
	 * @throws Exception
	 */
	public static void write(MuTestProjectCodeFile code_file, File tdir) throws Exception {
		if(code_file == null)
			throw new IllegalArgumentException("Invalid code_file: null");
		else if(tdir == null || !tdir.isDirectory())
			throw new IllegalArgumentException("Invalid tdir as null");
		else {
			String name = MuTestWritingUtils.
					basename_without_postfix(code_file.get_cfile());
			write_source_code(code_file.get_ifile(), 
					new File(tdir.getAbsolutePath() + "/" + name + ".c"));
			write_ast_tree(code_file.get_ast_tree(),
					new File(tdir.getAbsolutePath() + "/" + name + ".ast"));
			write_cir_tree(code_file.get_cir_tree(),
					new File(tdir.getAbsolutePath() + "/" + name + ".cir"));
			write_function_call_graph(code_file.get_cir_tree().get_function_call_graph(),
					new File(tdir.getAbsolutePath() + "/" + name + ".flw"));
		}
	}
	
}
