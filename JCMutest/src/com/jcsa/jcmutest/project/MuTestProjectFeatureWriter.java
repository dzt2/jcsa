package com.jcsa.jcmutest.project;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collection;
import java.util.List;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirAnnotation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymExpressionError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymFlowError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymInstance;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymReferenceError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymStateValueError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymTrapError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymValueError;
import com.jcsa.jcmutest.mutant.cir2mutant.path.SymInstanceEdge;
import com.jcsa.jcmutest.mutant.cir2mutant.path.SymInstanceGraph;
import com.jcsa.jcmutest.mutant.cir2mutant.path.SymInstanceNode;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcparse.base.Complex;
import com.jcsa.jcparse.flwa.context.CirCallContextInstanceGraph;
import com.jcsa.jcparse.flwa.context.CirFunctionCallPathType;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.astree.base.AstIdentifier;
import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.AstTypeName;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstOperator;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CFunctionType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CQualifierType;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CUnionType;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirConstExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirField;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirNameExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirType;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCall;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCallGraph;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabel;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.CKeyword;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.lexical.CPunctuator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.test.file.TestInput;
import com.jcsa.jcparse.test.state.CStatePath;

/**
 * It implements the feature writing on code and mutation information, including:<br>
 * 	1) xxx.cpp, xxx.ast, xxx.cir, xxx.flw
 * 	2) xxx.mut, xxx.tst, xxx.res, xxx.sym
 * 
 * @author yukimula
 *
 */
public class MuTestProjectFeatureWriter {
	
	/* definitions */
	/** the code file in project to be written **/
	private MuTestProjectCodeFile code_file;
	/** the simple name of code file without extension and prefix path **/
	private String file_name;
	/** the directory where xxx.cpp, xxx.ast, xxx.cir, .... xxx.sym are written **/
	private File output_directory;
	/** the current file to be written as either cpp, ast, cir, flw, ..., sym **/
	private File output_file;
	/** the writer to write the output stream to the specified file **/
	private FileWriter writer;
	
	/* constructor */
	/**
	 * create a feature writer for writing information in code file on specified output directory
	 * @param code_file
	 * @param output_directory
	 * @throws Exception
	 */
	private MuTestProjectFeatureWriter(MuTestProjectCodeFile code_file, File output_directory) throws Exception {
		if(code_file == null)
			throw new IllegalArgumentException("Invalid code_file: null");
		else if(output_directory == null || !output_directory.isDirectory())
			throw new IllegalArgumentException("Invalid output_directory");
		else {
			this.code_file = code_file;
			
			this.file_name = code_file.get_name();
			int index = this.file_name.lastIndexOf('.');
			if(index > 0) {
				this.file_name = this.file_name.substring(0, index).strip();
			}
			
			this.output_directory = output_directory;
			this.output_file = null;
			this.writer = null;
		}
	}
	
	/* stream methods */
	/**
	 * open the writer to write feature information to xxx.postfix in output directory
	 * @param postfix
	 * @throws Exception
	 */
	private void open_writer(String postfix) throws Exception {
		this.close_writer();
		this.output_file = new File(this.output_directory.getAbsolutePath() + "/" + this.file_name + "." + postfix);
		this.writer = new FileWriter(this.output_file);
		System.out.println("\t==> Write " + this.output_file.getAbsolutePath());
	}
	/**
	 * close the writer to write feature information
	 * @throws Exception
	 */
	private void close_writer() throws Exception {
		if(this.writer != null) {
			this.writer.close();
			this.writer = null;
			this.output_file = null;
		}
	}
	
	/* string method */
	/**
	 * @param data_type
	 * @return the code of data type
	 * @throws Exception
	 */
	private String type_string(CType data_type) throws Exception {
		if(data_type instanceof CBasicType) {
			switch(((CBasicType) data_type).get_tag()) {
			case c_void:				return "void";
			case c_bool:				return "bool";
			case c_char:				return "char";
			case c_uchar:				return "uchar";
			case c_short:				return "short";
			case c_ushort:				return "ushort";
			case c_int:					return "int";
			case c_uint:				return "uint";
			case c_long:				return "long";
			case c_ulong:				return "ulong";
			case c_llong:				return "llong";
			case c_ullong:				return "ullong";
			case c_float:				return "float";
			case c_double:				return "double";
			case c_ldouble:				return "ldouble";
			case c_float_complex:		return "float_x";
			case c_double_complex:		return "double_x";
			case c_ldouble_complex:		return "ldouble_x";
			case c_float_imaginary:		return "float_i";
			case c_double_imaginary:	return "double_i";
			case c_ldouble_imaginary:	return "ldouble_i";
			case gnu_va_list:			return "va_list";
			default: throw new IllegalArgumentException(data_type.generate_code());
			}
		}
		else if(data_type instanceof CArrayType) {
			if(((CArrayType) data_type).length() > 0)
				return this.type_string(((CArrayType) data_type).get_element_type()) + ":[" + ((CArrayType) data_type).length() + "]";
			else
				return this.type_string(((CArrayType) data_type).get_element_type()) + ":*";
		}
		else if(data_type instanceof CPointerType) {
			return this.type_string(((CPointerType) data_type).get_pointed_type()) + ":*";
		}
		else if(data_type instanceof CFunctionType) {
			return this.type_string(((CFunctionType) data_type).get_return_type()) + ":()";
		}
		else if(data_type instanceof CStructType) {
			String name = ((CStructType) data_type).get_name();
			if(name.isBlank())
				name = "struct #" + data_type.hashCode();
			return name;
		}
		else if(data_type instanceof CUnionType) {
			String name = ((CStructType) data_type).get_name();
			if(name.isBlank())
				name = "union #" + data_type.hashCode();
			return name;
		}
		else if(data_type instanceof CEnumType) {
			return "int";
		}
		else if(data_type instanceof CQualifierType) {
			return this.type_string(((CQualifierType) data_type).get_reference());
		}
		else {
			throw new IllegalArgumentException(data_type.generate_code());
		}
	}
	/**
	 * n@null, b@bool, c@char, i@intg, f@real, x@real@imag, s@string, 
	 * k@keyword, p@punctuate, o@operator, ast@id, cir@id, exe@id, 
	 * typ@type, mut@id
	 * @param token
	 * @return
	 * @throws Exception
	 */
	private String token_string(Object token) throws Exception {
		if(token == null)
			return "n@null";
		else if(token instanceof Boolean)
			return "b@" + token.toString();
		else if(token instanceof Character)
			return "c@" + ((int) ((Character) token).charValue());
		else if(token instanceof Short || token instanceof Integer || token instanceof Long)
			return "i@" + token.toString();
		else if(token instanceof Float || token instanceof Double)
			return "f@" + token.toString();
		else if(token instanceof Complex)
			return "x@" + ((Complex) token).real() + "@" + ((Complex) token).imag();
		else if(token instanceof String) {
			String text = token.toString();
			StringBuilder buffer = new StringBuilder();
			buffer.append("s@");
			for(int k = 0; k < text.length(); k++) {
				char ch = text.charAt(k);
				if(Character.isWhitespace(ch)) {
					buffer.append("\\s");
				}
				else if(ch == '@') {
					buffer.append("\\a");
				}
				else {
					buffer.append(ch);
				}
			}
			return buffer.toString();
		}
		else if(token instanceof CConstant) {
			return this.token_string(((CConstant) token).get_object());
		}
		else if(token instanceof CKeyword) {
			return "k@" + token.toString();
		}
		else if(token instanceof CPunctuator) {
			return "p@" + token.toString();
		}
		else if(token instanceof COperator) {
			return "o@" + token.toString();
		}
		else if(token instanceof AstNode) {
			return "ast@" + ((AstNode) token).get_key();
		}
		else if(token instanceof CirNode) {
			return "cir@" + ((CirNode) token).get_node_id();
		}
		else if(token instanceof CirExecution) {
			return "exe@" + token.toString();
		}
		else if(token instanceof CType) {
			return "typ@" + type_string((CType) token);
		}
		else if(token instanceof Mutant) {
			return "mut@" + ((Mutant) token).get_id();
		}
		else 
			throw new IllegalArgumentException(token.getClass().getSimpleName());
	}
	
	/* STATIC FEATURE WRITING */
	/**
	 * xxx.cpp, xxx.ast, xxx.cir, xxx.flw
	 * @throws Exception
	 */
	private void write_code_features() throws Exception {
		this.write_cpp();
		this.write_ast();
		this.write_cir();
		this.write_flw();
	}
	
	/* CPP information */
	/**
	 * write the source code of intermediate file to xxx.c in output_directory
	 * @throws Exception
	 */
	private void write_cpp() throws Exception {
		this.open_writer("c");		/* output_directory/name.cpp */
		
		/* copy the characters in ifile to cpp file */
		File ifile = this.code_file.get_ifile();
		FileReader reader = new FileReader(ifile);
		char[] buffer = new char[1024 * 1024 * 8];
		int length;
		while((length = reader.read(buffer)) >= 0) {
			this.writer.write(buffer, 0, length);
		}
		reader.close();
		
		this.close_writer();		/* write the end of file (EOF) */
	}
	
	/* AST information */
	/**
	 * id class beg_index end_index type token [ child child child ... child ]
	 * @throws Exception
	 */
	private void write_ast() throws Exception {
		this.open_writer("ast");
		AstTree ast_tree = this.code_file.get_ast_tree();
		for(int k = 0; k < ast_tree.number_of_nodes(); k++) {
			this.write_ast(ast_tree.get_node(k));
		}
		this.close_writer();
	}
	/**
	 * id class beg_index end_index type token [ child child child ... child ]
	 * @throws Exception
	 */
	private void write_ast(AstNode ast_node) throws Exception {
		this.writer.write(this.token_string(ast_node));
		
		String class_name = ast_node.getClass().getSimpleName();
		class_name = class_name.substring(3, class_name.length() - 4).strip();
		writer.write("\t" + class_name);
		
		int beg_index = ast_node.get_location().get_bias();
		int end_index = beg_index + ast_node.get_location().get_length();
		writer.write("\t" + beg_index + "\t" + end_index);		
		
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
		this.writer.write("\t" + this.token_string(data_type));
		
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
		this.writer.write("\t" + this.token_string(token));
		
		this.writer.write("\t[");
		for(int k = 0; k < ast_node.number_of_children(); k++) {
			this.writer.write(" " + this.token_string(ast_node.get_child(k)));
		}
		this.writer.write(" ]");
		
		this.writer.write("\n");
	}
	
	/* CIR information */
	/**
	 * id class ast_id type token [ child child ... child ]
	 * @throws Exception
	 */
	private void write_cir() throws Exception {
		this.open_writer("cir");
		CirTree cir_tree = this.code_file.get_cir_tree();
		for(CirNode cir_node : cir_tree.get_nodes()) {
			this.write_cir(cir_node);
		}
		this.close_writer();
	}
	/**
	 * id class ast_id type token [ child child ... child ]
	 * @param cir_node
	 * @throws Exception
	 */
	private void write_cir(CirNode cir_node) throws Exception {
		this.writer.write(this.token_string(cir_node));
		
		String class_name = cir_node.getClass().getSimpleName();
		class_name = class_name.substring(3, class_name.length() - 4).strip();
		writer.write("\t" + class_name);
		
		this.writer.write("\t" + this.token_string(cir_node.get_ast_source()));
		
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
		this.writer.write("\t" + this.token_string(data_type));
		
		Object token;
		if(cir_node instanceof CirNameExpression) {
			token = ((CirNameExpression) cir_node).get_unique_name();
		}
		else if(cir_node instanceof CirDeferExpression) {
			token = COperator.dereference;
		}
		else if(cir_node instanceof CirFieldExpression) {
			token = CPunctuator.dot;
		}
		else if(cir_node instanceof CirConstExpression) {
			token = ((CirConstExpression) cir_node).get_constant();
		}
		else if(cir_node instanceof CirCastExpression) {
			token = COperator.assign;
		}
		else if(cir_node instanceof CirAddressExpression) {
			token = COperator.address_of;
		}
		else if(cir_node instanceof CirComputeExpression) {
			token = ((CirComputeExpression) cir_node).get_operator();
		}
		else if(cir_node instanceof CirField) {
			token = ((CirField) cir_node).get_name();
		}
		else if(cir_node instanceof CirLabel) {
			CirNode target = cir_node.get_tree().get_node(((CirLabel) cir_node).get_target_node_id());
			token = target;
		}
		else {
			token = null;
		}
		this.writer.write("\t" + this.token_string(token));
		
		this.writer.write("\t[");
		for(CirNode child : cir_node.get_children()) {
			this.writer.write(" " + this.token_string(child));
		}
		this.writer.write(" ]");
		
		this.writer.write("\n");
	}
	
	/* FLW information */
	/**
	 * write functions, nodes and edges in program flow graph
	 * @throws Exception
	 */
	private void write_flw() throws Exception {
		this.open_writer("flw");
		CirFunctionCallGraph graph = this.code_file.get_cir_tree().get_function_call_graph();
		for(CirFunction function : graph.get_functions()) {
			this.write_cir_function(function);
		}
		this.close_writer();
	}
	/**
	 * [edge] type source target
	 * @param flow
	 * @throws Exception
	 */
	private void write_execution_flow(CirExecutionFlow flow) throws Exception {
		this.writer.write("\t" + "[edge]");
		this.writer.write("\t" + flow.get_type());
		this.writer.write("\t" + this.token_string(flow.get_source()));
		this.writer.write("\t" + this.token_string(flow.get_target()));
		this.writer.write("\n");
	}
	/**
	 * [node] exec_id function int_id cir_id 
	 * @param execution
	 * @throws Exception
	 */
	private void write_execution_node(CirExecution execution) throws Exception {
		this.writer.write("\t" + "[node]");
		this.writer.write("\t" + this.token_string(execution));
		this.writer.write("\t" + execution.get_graph().get_function().get_name());
		this.writer.write("\t" + execution.get_id());
		this.writer.write("\t" + this.token_string(execution.get_statement()));
		this.writer.write("\n");
	}
	/**
	 * [call] call_exec wait_exec
	 * @param call
	 * @throws Exception
	 */
	private void write_function_call(CirFunctionCall call) throws Exception {
		writer.write("\t[call]");
		writer.write("\t" + this.token_string(call.get_call_execution()));
		writer.write("\t" + this.token_string(call.get_wait_execution()));
		writer.write("\n");
	}
	/**
	 * [func] name
	 * 		[node] exec_id function int_id cir_id
	 * 		[edge] type source_exec_id target_exec_id
	 * 		[call] call_exec_id wait_exec_id
	 * [end_func]
	 * @param function
	 * @throws Exception
	 */
	private void write_cir_function(CirFunction function) throws Exception {
		this.writer.write("[func]\t" + function.get_name() + "\n");
		for(CirExecution execution : function.get_flow_graph().get_executions()) {
			this.write_execution_node(execution);
			for(CirExecutionFlow flow : execution.get_ou_flows()) {
				this.write_execution_flow(flow);
			}
		}
		for(CirFunctionCall call : function.get_ou_calls()) {
			this.write_function_call(call);
		}
		this.writer.write("[end_func]\n");
	}
	
	/* DYNAMIC FEATURE WRITINGS */
	/**
	 * xxx.tst, xxx.res, xxx.mut, xxx.sym
	 * @throws Exception
	 */
	private void write_muta_features() throws Exception {
		this.write_tst();
		this.write_mut();
		this.write_res();
	}
	
	/* TST information */
	/**
	 * tid parameter
	 * @throws Exception
	 */
	private void write_tst() throws Exception {
		this.open_writer("tst");
		MuTestProjectTestSpace tspace = this.code_file.get_code_space().get_project().get_test_space();
		for(TestInput test_case : tspace.get_test_inputs()) {
			this.writer.write(test_case.get_id() + "\t");
			this.writer.write(this.token_string(test_case.get_parameter()));
			this.writer.write("\n");
		}
		this.close_writer();
	}
	/**
	 * mid bit_string
	 * @throws Exception
	 */
	private void write_res() throws Exception {
		this.open_writer("res");
		MuTestProjectTestSpace tspace = this.code_file.get_code_space().get_project().get_test_space();
		for(Mutant mutant : this.code_file.get_mutant_space().get_mutants()) {
			MuTestProjectTestResult result = tspace.get_test_result(mutant);
			if(result != null) {
				this.writer.write(mutant.get_id() + "\t");
				this.writer.write(result.get_kill_set().toString());
				this.writer.write("\n");
			}
		}
		this.close_writer();
	}
	
	/* MUT information */
	/**
	 * id class operator location parameter [ coverage weak strong ]
	 * @param mutant
	 * @throws Exception
	 */
	private void write_mutant(Mutant mutant) throws Exception {
		this.writer.write(this.token_string(mutant));
		AstMutation mutation = mutant.get_mutation();
		this.writer.write("\t" + mutation.get_class());
		this.writer.write("\t" + mutation.get_operator());
		this.writer.write("\t" + this.token_string(mutation.get_location()));
		this.writer.write("\t" + this.token_string(mutation.get_parameter()));
		
		this.writer.write("\t[");
		this.writer.write(" " + this.token_string(mutant.get_coverage_mutant()));
		this.writer.write(" " + this.token_string(mutant.get_weak_mutant()));
		this.writer.write(" " + this.token_string(mutant.get_strong_mutant()));
		this.writer.write(" ]");
		
		this.writer.write("\n");
	}
	/**
	 * id class operator location parameter [ coverage weak strong ]
	 * @throws Exception
	 */
	private void write_mut() throws Exception {
		this.open_writer("mut");
		for(Mutant mutant : this.code_file.get_mutant_space().get_mutants()) {
			this.write_mutant(mutant);
		}
		this.close_writer();
	}
	
	/* FET information */
	/**
	 * append the code generated from expression to output stream
	 * @param expression
	 * @throws Exception
	 */
	private void write_sym_expression(SymExpression expression) throws Exception {
		String code = expression.generate_code();
		for(int k = 0; k < code.length(); k++) {
			char ch = code.charAt(k);
			if(!Character.isWhitespace(ch)) {
				this.writer.write(ch);
			}
		}
	}
	/**
	 * 	cons@execution@condition
	 * 	trap@execution
	 * 	flow@execution@orig_target@muta_target
	 * 	expr@location@muta_value
	 * 	refr@location@muta_value
	 * 	stat@location@muta_value
	 * 	
	 * 	@param instance
	 * 	@throws Exception
	 */
	private void write_sym_instance(SymInstance instance) throws Exception {
		if(instance instanceof SymConstraint) {
			writer.write("cons@" + instance.get_execution() + "@");
			this.write_sym_expression(((SymConstraint) instance).get_condition());
		}
		else if(instance instanceof SymFlowError) {
			writer.write("flow@" + instance.get_execution() + "@");
			writer.write(((SymFlowError) instance).get_original_flow().get_target() + 
					"@" + ((SymFlowError) instance).get_mutation_flow().get_target());
		}
		else if(instance instanceof SymTrapError) {
			writer.write("trap@" + instance.get_execution());
		}
		else if(instance instanceof SymExpressionError) {
			writer.write("expr@" + ((SymValueError) instance).get_expression().get_node_id() + "@");
			this.write_sym_expression(((SymValueError) instance).get_mutation_value());
		}
		else if(instance instanceof SymReferenceError) {
			writer.write("refr@" + ((SymValueError) instance).get_expression().get_node_id() + "@");
			this.write_sym_expression(((SymValueError) instance).get_mutation_value());
		}
		else if(instance instanceof SymStateValueError) {
			writer.write("stat@" + ((SymValueError) instance).get_expression().get_node_id() + "@");
			this.write_sym_expression(((SymValueError) instance).get_mutation_value());
		}
		else {
			throw new IllegalArgumentException("Invalid: " + instance);
		}
	}
	/**
	 * type@execution@parameter
	 * @param annotation
	 * @throws Exception
	 */
	private void write_cir_annotation(CirAnnotation annotation) throws Exception {
		writer.write(annotation.get_type() + "@" + annotation.get_execution());
		if(annotation.get_parameter() != null) {
			SymExpression parameter = (SymExpression) annotation.get_parameter();
			writer.write("@");
			this.write_sym_expression(parameter);
		}
	}
	/**
	 * [ constraint* ]
	 * @param edge
	 * @throws Exception
	 */
	private void write_sym_instance_edge(SymInstanceEdge edge) throws Exception {
		CirMutations cir_mutations = edge.get_source().get_graph().get_cir_mutations();
		SymConstraint constraint = edge.get_constraint(); 
		constraint = cir_mutations.optimize(constraint, null);
		Collection<SymConstraint> constraints = cir_mutations.improve_constraints(constraint);
		
		writer.write("[");
		writer.write(" ");
		this.write_sym_instance(edge.get_constraint());
		for(SymConstraint improved_constraint : constraints) {
			writer.write(" ");
			this.write_sym_instance(improved_constraint);
		}
		writer.write(" ]");
	}
	/**
	 * ( error annotation+ )
	 * @param node
	 * @throws Exception
	 */
	private void write_sym_instance_node(SymInstanceNode node) throws Exception {
		writer.write("(");
		if(node.has_state_error()) {
			writer.write(" ");
			this.write_sym_instance(node.get_state_error());
		}
		for(CirAnnotation annotation : node.get_status().get_cir_annotations()) {
			writer.write(" ");
			this.write_cir_annotation(annotation);
		}
		
		writer.write(" )");
	}
	/**
	 * mid tid res node edge node edge ... edge node
	 * @param graph
	 * @param state_path
	 * @throws Exception
	 */
	private void write_sym_instance_graph(SymInstanceGraph graph, TestInput test_case, int test_result) throws Exception {
		Collection<List<SymInstanceEdge>> paths = graph.select_reachable_paths();
		for(List<SymInstanceEdge> path : paths) {
			int tid = -1;
			if(test_case != null) tid = test_case.get_id();
			writer.write(graph.get_mutant().get_id() + "\t" + tid + "\t" + test_result);
			for(SymInstanceEdge edge : path) {
				writer.write("\t");
				this.write_sym_instance_edge(edge);
				writer.write("\t");
				this.write_sym_instance_node(edge.get_target());
			}
			writer.write("\n");
		}
	}
	/**
	 * mid tid res node edge node edge ... edge node*
	 * @param test_case
	 * @throws Exception
	 */
	private void write_sym_instance_graphs(TestInput test_case, CDependGraph dependence_graph, int max_distance) throws Exception {
		MuTestProjectTestSpace tspace = this.code_file.get_code_space().get_project().get_test_space();
		CStatePath state_path = tspace.load_instrumental_path(this.code_file.get_sizeof_template(), 
							this.code_file.get_ast_tree(), this.code_file.get_cir_tree(), test_case);
		if(state_path != null) {
			this.open_writer(test_case.get_id() + ".sym");
			for(Mutant mutant : this.code_file.get_mutant_space().get_mutants()) {
				MuTestProjectTestResult result = tspace.get_test_result(mutant); int test_result;
				if(result == null) {
					continue;
				}
				
				SymInstanceGraph graph = SymInstanceGraph.new_graph(dependence_graph, mutant, max_distance);
				graph.evaluate(state_path);
				if(result.get_kill_set().get(test_case.get_id())) {
					test_result = 3;
				}
				else {
					boolean covered = false, infected = false;
					for(SymInstanceNode muta_node : graph.get_mutated_nodes()) {
						if(muta_node.get_status().is_executed()) {
							covered = true;
						}
						for(SymInstanceEdge muta_edge : muta_node.get_ou_edges()) {
							if(muta_edge.get_status().is_acceptable()) {
								infected = true;
							}
						}
					}
					if(!covered) {
						test_result = 0;
					}
					else if(!infected) {
						test_result = 1;
					}
					else {
						test_result = 2;
					}
				}
				
				this.write_sym_instance_graph(graph, test_case, test_result);
			}
			this.close_writer();
		}
	}
	/**
	 * 
	 * @param dependence_graph
	 * @param max_distance
	 * @throws Exception
	 */
	private void write_sym_instance_graphs(CDependGraph dependence_graph, int max_distance) throws Exception {
		MuTestProjectTestSpace tspace = this.code_file.get_code_space().get_project().get_test_space();
		this.open_writer("sym");
		for(Mutant mutant : this.code_file.get_mutant_space().get_mutants()) {
			MuTestProjectTestResult result = tspace.get_test_result(mutant); int test_result;
			if(result == null) {
				continue;
			}
			
			SymInstanceGraph graph = SymInstanceGraph.new_graph(dependence_graph, mutant, max_distance);
			graph.evaluate();
			if(result.get_kill_set().degree() > 0) {
				test_result = 3;
			}
			else {
				boolean covered = false, infected = false;
				for(SymInstanceNode muta_node : graph.get_mutated_nodes()) {
					if(muta_node.get_status().is_executed()) {
						covered = true;
					}
					for(SymInstanceEdge muta_edge : muta_node.get_ou_edges()) {
						if(muta_edge.get_status().is_acceptable()) {
							infected = true;
						}
					}
				}
				if(!covered) {
					test_result = 0;
				}
				else if(!infected) {
					test_result = 1;
				}
				else {
					test_result = 2;
				}
			}
			
			this.write_sym_instance_graph(graph, null, test_result);
		}
		this.close_writer();
	}
	/**
	 * xxx.sym, xxx.0.sym, ..., xxx.n.sym
	 * @param max_distance
	 * @throws Exception
	 */
	private void write_symb_features(int max_distance) throws Exception {
		CirFunction root_function = code_file.get_cir_tree().get_function_call_graph().get_main_function();
		CDependGraph dependence_graph = CDependGraph.graph(CirCallContextInstanceGraph.graph(root_function, 
				CirFunctionCallPathType.unique_path, -1));
		this.write_sym_instance_graphs(dependence_graph, max_distance);
		MuTestProjectTestSpace tspace = this.code_file.get_code_space().get_project().get_test_space();
		for(TestInput test_case : tspace.get_test_inputs()) {
			this.write_sym_instance_graphs(test_case, dependence_graph, max_distance);
		}
	}
	
	/* static generation */
	/**
	 * write the features in code_file to specified output directory
	 * @param code_file
	 * @param output_directory
	 * @param max_distance
	 * @throws Exception
	 */
	public static void write_features(MuTestProjectCodeFile code_file, File output_directory, int max_distance) throws Exception {
		if(code_file == null)
			throw new IllegalArgumentException("Invalid code_file: null");
		else if(output_directory == null)
			throw new IllegalArgumentException("Invalid output_directory");
		else {
			if(!output_directory.exists()) FileOperations.mkdir(output_directory);
			MuTestProjectFeatureWriter writer = new MuTestProjectFeatureWriter(code_file, output_directory);
			writer.write_code_features();
			writer.write_muta_features();
			writer.write_symb_features(max_distance);
		}
	}
	
}
