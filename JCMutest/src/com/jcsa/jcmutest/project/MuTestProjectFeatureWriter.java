package com.jcsa.jcmutest.project;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.cond.CirAnnotation;
import com.jcsa.jcmutest.mutant.cir2mutant.trees.CirInfectionEdge;
import com.jcsa.jcmutest.mutant.cir2mutant.trees.CirInfectionNode;
import com.jcsa.jcmutest.mutant.cir2mutant.trees.CirInfectionTree;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcparse.base.Complex;
import com.jcsa.jcparse.flwa.CirInstance;
import com.jcsa.jcparse.flwa.context.CirCallContextInstanceGraph;
import com.jcsa.jcparse.flwa.context.CirFunctionCallPathType;
import com.jcsa.jcparse.flwa.depend.CDependEdge;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.flwa.depend.CDependNode;
import com.jcsa.jcparse.flwa.depend.CDependPredicate;
import com.jcsa.jcparse.flwa.depend.CDependReference;
import com.jcsa.jcparse.flwa.graph.CirInstanceEdge;
import com.jcsa.jcparse.flwa.graph.CirInstanceGraph;
import com.jcsa.jcparse.flwa.graph.CirInstanceNode;
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
import com.jcsa.jcparse.lang.irlang.stmt.CirLabel;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionBody;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;
import com.jcsa.jcparse.lang.irlang.unit.CirTransitionUnit;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.CKeyword;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.lexical.CPunctuator;
import com.jcsa.jcparse.lang.symbol.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolField;
import com.jcsa.jcparse.lang.symbol.SymbolFieldExpression;
import com.jcsa.jcparse.lang.symbol.SymbolIdentifier;
import com.jcsa.jcparse.lang.symbol.SymbolLiteral;
import com.jcsa.jcparse.lang.symbol.SymbolNode;
import com.jcsa.jcparse.lang.symbol.SymbolOperator;
import com.jcsa.jcparse.lang.symbol.SymbolUnaryExpression;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;
import com.jcsa.jcparse.test.file.TestInput;
import com.jcsa.jcparse.test.state.CStateNode;
import com.jcsa.jcparse.test.state.CStatePath;

/**
 * It generates the features for describing infected execution state along with
 * the corresponding constraint in a concrete or abstract mutation testing.<br>
 * <br>
 * <code>
 * 	+-------------------------------------------------------------------+	<br>
 * 	|	Source Code Features											|	<br>
 * 	+-------------------------------------------------------------------+	<br>
 * 	|	xxx.cpp	|	source code file									|	<br>
 * 	|	xxx.ast	|	abstract syntactic tree and its nodes				|	<br>
 * 	|	xxx.cir	|	C-intermediate representation tree and nodes		|	<br>
 * 	|	xxx.flw	|	program control flow graph							|	<br>
 * 	+-------------------------------------------------------------------+	<br>
 * 	|	Static Analysis Features										|	<br>
 * 	+-------------------------------------------------------------------+	<br>
 * 	|	xxx.ins	|	program instance graph using call-relations			|	<br>
 * 	|	xxx.dep	|	program dependence graph for analysis				|	<br>
 * 	+-------------------------------------------------------------------+	<br>
 * 	|	Dynamic Testing Features										|	<br>
 * 	+-------------------------------------------------------------------+	<br>
 * 	|	xxx.mut	|	Mutant and their relationships generated			|	<br>
 * 	|	xxx.tst	|	Test input and its parameters in command			|	<br>
 * 	|	xxx.res	|	Test result of each mutant and test case			|	<br>
 * 	+-------------------------------------------------------------------+	<br>
 * 	|	Symbolic Analysis Features										|	<br>
 * 	+-------------------------------------------------------------------+	<br>
 * 	|	xxx.sym	|	Symbolic nodes generated in evaluation				|	<br>
 * 	|	xxx.stn	|	CirInfectionNode(s) from static|dynamic analysis	|	<br>
 * 	|	xxx.stp	|	CirInfectionEdge(s) by leaf-root path evaluated.	|	<br>
 * 	+-------------------------------------------------------------------+	<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public class MuTestProjectFeatureWriter {
	
	/* definitions */
	/** the input source code file for creating features **/
	private MuTestProjectCodeFile 	inputs;
	/** the directory where feature files were generated **/
	private File 					output_directory;
	/** it is used to output string text to current file **/
	private FileWriter 				file_writer;
	/** set to preserve symbolic nodes used for features **/
	private Set<SymbolNode> 		symbol_nodes;
	/** the maximal times of evaluation on state infection **/
	private int						max_infected_times;
	
	/* singleton pattern */
	/** private constructor for singleton design pattern **/
	private MuTestProjectFeatureWriter() { this.symbol_nodes = new HashSet<>(); }
	/** the single instance for generating symbolic features of input source code file **/
	private static final MuTestProjectFeatureWriter writer = new MuTestProjectFeatureWriter();
	
	/* input-output methods */
	/**
	 * close writer
	 * @throws Exception
	 */
	private void close() throws Exception {
		if(this.file_writer != null) {
			this.file_writer.close();
			this.file_writer = null;
		}
	}
	/**
	 * open to new writer
	 * @param postfix
	 * @throws Exception
	 */
	private void open(String postfix) throws Exception {
		/* get the output file for writing */
		String file_name; int index; File output_file;
		file_name = inputs.get_name(); index = file_name.indexOf('.');
		file_name = file_name.substring(0, index).trim();
		output_file = new File(this.output_directory.getAbsolutePath() + "/" + file_name + postfix);
		System.out.println("\t==> Start to Write \"" + output_file.getAbsolutePath() + "\"...");

		/* open a new writer */
		this.close(); 
		this.file_writer = new FileWriter(output_file);
	}
	/**
	 * set the inputs and output directory for generating feature files
	 * @param inputs
	 * @param output_directory
	 * @throws Exception
	 */
	private void set_IO(MuTestProjectCodeFile inputs, File 
			output_directory, int max_evaluation_times) throws Exception {
		if(inputs == null) {
			throw new IllegalArgumentException("Invalid input: null");
		}
		else if(output_directory == null) {
			throw new IllegalArgumentException("Invalid output: null");
		}
		else if(output_directory.exists() && !output_directory.isDirectory()) {
			throw new IllegalArgumentException("Invalid: " + output_directory.getAbsolutePath());
		}
		else if(max_evaluation_times <= 0) {
			throw new IllegalArgumentException("Invalid: " + max_evaluation_times);
		}
		else {
			if(!output_directory.exists()) { FileOperations.mkdir(output_directory); }
			this.inputs = inputs;
			this.output_directory = output_directory;
			this.close();
			this.max_infected_times = max_evaluation_times;
		}
	}
	
	/* basic supporting methods */
	/**
	 * translate special characters to specified format
	 * @param text
	 * @return {@ --> \a; space --> \s; $ --> \p;}
	 * @throws Exception
	 */
	private String normalize_string(String text) throws Exception {
		StringBuilder buffer = new StringBuilder();
		for(int k = 0; k < text.length(); k++) {
			char ch = text.charAt(k);
			if(Character.isWhitespace(ch))
				buffer.append("\\s");
			else if(ch == '@')
				buffer.append("\\a");
			else if(ch == '$')
				buffer.append("\\p");
			else
				buffer.append(ch);
		}
		return buffer.toString();
	}
	/**
	 * @param token
	 * @return 	BASIC: 	n@null, b@bool, c@char, i@int, f@real, x@r@i, s@txt;
	 * 			ENUMS:	key@keyword, opr@operator, pun@punctuator, typ@type;
	 * 			OBJEC:	ast@int, cir@int, exe@txt@int, ins@txt@int@int;
	 * 			FEATU:	sym@txt@int, mut@int, tst@int;
	 * @throws Exception
	 */
	private String encode_token(Object token) throws Exception {
		if(token == null) {
			return "n@null";
		}
		else if(token instanceof Boolean) {
			return "b@" + token.toString();
		}
		else if(token instanceof Character) {
			int value = ((Character) token).charValue();
			return "c@" + value;
		}
		else if(token instanceof Short || token instanceof Integer || token instanceof Long) {
			return "i@" + token.toString();
		}
		else if(token instanceof Float || token instanceof Double) {
			return "f@" + token.toString();
		}
		else if(token instanceof Complex) {
			return "x@" + ((Complex) token).real() + "@" + ((Complex) token).imag();
		}
		else if(token instanceof String) {
			return "s@" + this.normalize_string(token.toString());
		}
		else if(token instanceof CConstant) {
			return this.encode_token(((CConstant) token).get_object());
		}
		else if(token instanceof CKeyword) {
			return "key@" + token.toString();
		}
		else if(token instanceof COperator) {
			return "opr@" + token.toString();
		}
		else if(token instanceof CPunctuator) {
			return "pun@" + token.toString();
		}
		else if(token instanceof CType) {
			return "typ@" + this.normalize_string(((CType) token).generate_code());
		}
		else if(token instanceof AstNode) {
			return "ast@" + ((AstNode) token).get_key();
		}
		else if(token instanceof CirNode) {
			return "cir@" + ((CirNode) token).get_node_id();
		}
		else if(token instanceof CirExecution) {
			String name = ((CirExecution) token).get_graph().get_function().get_name();
			int key = ((CirExecution) token).get_id();
			return "exe@" + name + "@" + key;
		}
		else if(token instanceof CirInstanceNode) {
			CirExecution execution = ((CirInstanceNode) token).get_execution();
			int context = 0;
			if(((CirInstanceNode) token).get_context() != null) {
				context = ((CirInstanceNode) token).get_context().hashCode();
			}
			String name = execution.get_graph().get_function().get_name();
			int exec_id = execution.get_id();
			return "ins@" + name + "@" + exec_id + "@" + context;
		}
		else if(token instanceof SymbolNode) {
			return "sym@" + token.getClass().getSimpleName().substring(6).trim() + "@" + token.hashCode();
		}
		else if(token instanceof Mutant) {
			return "mut@" + ((Mutant) token).get_id();
		}
		else if(token instanceof TestInput) {
			return "tst@" + ((TestInput) token).get_id();
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + token.getClass().getSimpleName());
		}
	}
	/**
	 * @param test_cases
	 * @return arrange each test case in set to the corresponding mutants it needs be executed on
	 * @throws Exception
	 */
	private Map<TestInput, Collection<Mutant>> initial_tests_mutants(Collection<TestInput> test_cases) throws Exception {
		/* perform executions on instrumental version */
		MuTestProjectTestSpace tspace = inputs.get_code_space().get_project().get_test_space();
		Map<TestInput, Collection<Mutant>> maps = new HashMap<TestInput, Collection<Mutant>>();
		this.inputs.get_code_space().get_project().execute_instrumental(test_cases);
		
		/* mapping each test to corresponding mutants */
		for(TestInput test_case : test_cases) {
			Collection<Mutant> mutants = new ArrayList<Mutant>();
			for(Mutant mutant : this.inputs.get_mutant_space().get_mutants()) {
				MuTestProjectTestResult s_result = tspace.get_test_result(mutant);
				MuTestProjectTestResult w_result = tspace.get_test_result(mutant.get_weak_mutant());
				MuTestProjectTestResult c_result = tspace.get_test_result(mutant.get_coverage_mutant());
				if(s_result != null && s_result.get_kill_set().get(test_case.get_id())) {
					mutants.add(mutant);
				}
				else if(w_result != null && w_result.get_kill_set().get(test_case.get_id())) {
					mutants.add(mutant);
				}
				else if(c_result != null && c_result.get_kill_set().get(test_case.get_id())) {
					mutants.add(mutant);
				}
			}
			if(!mutants.isEmpty()) { maps.put(test_case, mutants); }
		}
		return maps;
	}
	/**
	 * node_type$attr_type$execution$location$parameter
	 * @param node
	 * @throws Exception
	 */
	private void write_cir_attribute(CirInfectionNode node) throws Exception {
		String category = node.get_type().toString();
		String operator = node.get_attribute().get_type().toString();
		CirExecution execution = node.get_attribute().get_execution();
		CirNode location = node.get_attribute().get_location();
		SymbolNode parameter = node.get_attribute().get_parameter();
		
		this.file_writer.write("\t" + category.toString());
		this.file_writer.write("$" + operator.toString());
		this.file_writer.write("$" + this.encode_token(execution));
		this.file_writer.write("$" + this.encode_token(location));
		this.file_writer.write("$" + this.encode_token(parameter));
		
		if(parameter != null) { this.symbol_nodes.add(parameter); }
	}
	/**
	 * class$operator$execution$location$parameter
	 * @param annotation
	 * @throws Exception
	 */
	private void write_cir_annotation(CirAnnotation annotation) throws Exception {
		String category = annotation.get_category().toString();
		String operator = annotation.get_operator().toString();
		CirExecution execution = annotation.get_execution();
		CirNode location = annotation.get_location();
		SymbolNode parameter = annotation.get_parameter();
		
		this.file_writer.write("\t" + category.toString());
		this.file_writer.write("$" + operator.toString());
		this.file_writer.write("$" + this.encode_token(execution));
		this.file_writer.write("$" + this.encode_token(location));
		this.file_writer.write("$" + this.encode_token(parameter));
		
		if(parameter != null) { this.symbol_nodes.add(parameter); }
	}
	/**
	 * @param tree
	 * @return whether the tree can be printed on output stream
	 * @throws Exception
	 */
	private boolean is_tree_available(CirInfectionTree tree) throws Exception {
		MuTestProjectTestSpace tspace = inputs.get_code_space().get_project().get_test_space();
		if(tree == null) {
			return false;
		}
		else if(tspace.get_test_result(tree.get_mutant()) == null) {
			return false;
		}
		else {
			return tree.has_cir_infections();
		}
	}
	
	/* static code information */
	/**
	 * xxx.c
	 * @throws Exception
	 */
	private void write_cpp() throws Exception {
		this.open(".c");
		FileReader reader = new FileReader(this.inputs.get_ifile());
		char[] buffer = new char[1024 * 1024 * 8]; int length;
		while((length = reader.read(buffer)) >= 0)
			this.file_writer.write(buffer, 0, length);
		reader.close();
		this.close();
	}
	/**
	 * ast@key class_name beg_index end_index data_type content [ {ast@key}* ]
	 * @param node
	 * @throws Exception
	 */
	private void write_ast(AstNode node) throws Exception {
		this.file_writer.write(this.encode_token(node));

		String class_name = node.getClass().getSimpleName();
		class_name = class_name.substring(3, class_name.length() - 4).trim();
		int beg_index = node.get_location().get_bias();
		int end_index = beg_index + node.get_location().get_length();
		this.file_writer.write("\t" + class_name + "\t" + beg_index + "\t" + end_index);

		CType data_type;
		if(node instanceof AstExpression) {
			data_type = ((AstExpression) node).get_value_type();
		}
		else if(node instanceof AstTypeName) {
			data_type = ((AstTypeName) node).get_type();
		}
		else {
			data_type = null;
		}
		this.file_writer.write("\t" + this.encode_token(data_type));

		Object content;
		if(node instanceof AstIdentifier) {
			content = ((AstIdentifier) node).get_name();
		}
		else if(node instanceof AstConstant) {
			content = ((AstConstant) node).get_constant();
		}
		else if(node instanceof AstKeyword) {
			content = ((AstKeyword) node).get_keyword();
		}
		else if(node instanceof AstPunctuator) {
			content = ((AstPunctuator) node).get_punctuator();
		}
		else if(node instanceof AstOperator) {
			content = ((AstOperator) node).get_operator();
		}
		else {
			content = null;
		}
		this.file_writer.write("\t" + this.encode_token(content));

		this.file_writer.write("\t[");
		for(int k = 0; k < node.number_of_children(); k++) {
			this.file_writer.write(" " + this.encode_token(node.get_child(k)));
		}
		this.file_writer.write(" ]");

		this.file_writer.write("\n");
	}
	/**
	 * ast@key class_name beg_index end_index data_type content [ {ast@key}* ] \n
	 * @throws Exception
	 */
	private void write_ast() throws Exception {
		this.open(".ast");
		AstTree ast_tree = this.inputs.get_ast_tree();
		for(int k = 0; k < ast_tree.number_of_nodes(); k++) {
			this.write_ast(ast_tree.get_node(k));
		}
		this.close();
	}
	/**
	 * cir@key class_name ast_source data_type content [ {cir@key}* ] code
	 * @param node
	 * @throws Exception
	 */
	private void write_cir(CirNode node) throws Exception {
		this.file_writer.write(this.encode_token(node));

		String class_name = node.getClass().getSimpleName();
		class_name = class_name.substring(3, class_name.length() - 4).trim();
		this.file_writer.write("\t" + class_name + "\t" + this.encode_token(node.get_ast_source()));

		CType data_type;
		if(node instanceof CirExpression) {
			data_type = ((CirExpression) node).get_data_type();
		}
		else if(node instanceof CirType) {
			data_type = ((CirType) node).get_typename();
		}
		else {
			data_type = null;
		}
		this.file_writer.write("\t" + this.encode_token(data_type));

		Object content;
		if(node instanceof CirNameExpression) {
			content = ((CirNameExpression) node).get_unique_name();
		}
		else if(node instanceof CirDeferExpression) {
			content = COperator.dereference;
		}
		else if(node instanceof CirFieldExpression) {
			content = CPunctuator.dot;
		}
		else if(node instanceof CirConstExpression) {
			content = ((CirConstExpression) node).get_constant();
		}
		else if(node instanceof CirCastExpression) {
			content = COperator.assign;
		}
		else if(node instanceof CirAddressExpression) {
			content = COperator.address_of;
		}
		else if(node instanceof CirComputeExpression) {
			content = ((CirComputeExpression) node).get_operator();
		}
		else if(node instanceof CirField) {
			content = ((CirField) node).get_name();
		}
		else if(node instanceof CirLabel) {
			CirNode target = node.get_tree().get_node(((CirLabel) node).get_target_node_id());
			content = target;
		}
		else {
			content = null;
		}
		this.file_writer.write("\t" + this.encode_token(content));

		this.file_writer.write("\t[");
		for(CirNode child : node.get_children()) {
			this.file_writer.write(" " + this.encode_token(child));
		}
		this.file_writer.write(" ]");

		String code = null;
		if(!(node instanceof CirFunctionDefinition
			|| node instanceof CirTransitionUnit
			|| node instanceof CirFunctionBody)) {
			code = node.generate_code(true);
		}
		this.file_writer.write("\t" + this.encode_token(code));

		this.file_writer.write("\n");
	}
	/**
	 * cir@key class_name ast_source data_type content [ {cir@key}* ] code \n
	 * @throws Exception
	 */
	private void write_cir() throws Exception {
		this.open(".cir");
		for(CirNode node : this.inputs.get_cir_tree().get_nodes()) {
			this.write_cir(node);
		}
		this.close();
	}
	/**
	 * [edge] type source target
	 * @param flow
	 * @throws Exception
	 */
	private void write_execution_flow(CirExecutionFlow flow) throws Exception {
		this.file_writer.write("\t" + "[edge]");
		this.file_writer.write("\t" + flow.get_type());
		this.file_writer.write("\t" + this.encode_token(flow.get_source()));
		this.file_writer.write("\t" + this.encode_token(flow.get_target()));
		this.file_writer.write("\n");
	}
	/**
	 * [call] call_exec wait_exec
	 * @param call
	 * @throws Exception
	 */
	private void write_execution_call(CirFunctionCall call) throws Exception {
		this.file_writer.write("\t" + "[call]");
		this.file_writer.write("\t" + this.encode_token(call.get_call_execution()));
		this.file_writer.write("\t" + this.encode_token(call.get_wait_execution()));
		this.file_writer.write("\n");
	}
	/**
	 * [node] ID cir_statement
	 * @param execution
	 * @throws Exception
	 */
	private void write_execution_node(CirExecution execution) throws Exception {
		this.file_writer.write("\t" + "[node]");
		this.file_writer.write("\t" + this.encode_token(execution));
		this.file_writer.write("\t" + this.encode_token(execution.get_statement()));
		this.file_writer.write("\n");
	}
	/**
	 * 	[node] ID cir_statement
	 * 	[edge] type source target
	 * 	[call] call_exec wait_exec
	 * @param function
	 * @throws Exception
	 */
	private void write_cir_function(CirFunction function) throws Exception {
		this.file_writer.write("[beg]\t" + function.get_name() + "\n");
		for(CirExecution execution : function.get_flow_graph().get_executions()) {
			this.write_execution_node(execution);
			for(CirExecutionFlow flow : execution.get_ou_flows()) {
				this.write_execution_flow(flow);
			}
		}
		for(CirFunctionCall call : function.get_ou_calls()) {
			this.write_execution_call(call);
		}
		this.file_writer.write("[end]\t" + function.get_name() + "\n");
	}
	/**
	 * 	[beg] name
	 * 		[node] EXEC_ID CIR_ID
	 * 		[edge] TYPE SOURCE TARGET
	 * 		[call] CALL_EXE WAIT_EXE
	 * 	[end] name
	 * 	@throws Exception
	 */
	private void write_flw() throws Exception {
		this.open(".flw");
		for(CirFunction function : this.inputs.get_cir_tree().get_function_call_graph().get_functions()) {
			this.write_cir_function(function);
			this.file_writer.write("\n");
		}
		this.close();
	}
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
	
	/* dependence analysis */
	/**
	 * [edge] type source target
	 * @param edge
	 * @throws Exception
	 */
	private void write_instance_edge(CirInstanceEdge edge) throws Exception {
		this.file_writer.write("[edge]\t" + edge.get_type());
		this.file_writer.write("\t" + this.encode_token(edge.get_source()));
		this.file_writer.write("\t" + this.encode_token(edge.get_target()));
		this.file_writer.write("\n");
	}
	/**
	 * [node] ins@txt@int@int execution context
	 * @param node
	 * @throws Exception
	 */
	private void write_instance_node(CirInstanceNode node) throws Exception {
		this.file_writer.write("[node]");
		this.file_writer.write("\t" + this.encode_token(node));
		this.file_writer.write("\t" + this.encode_token(node.get_execution()));
		int context = 0;
		if(node.get_context() != null) context = node.get_context().hashCode();
		this.file_writer.write("\t" + context);
		this.file_writer.write("\n");
	}
	/**
	 * [node] ins@txt@int@int 	execution 	context
	 * [edge] type 				source 		target
	 * @throws Exception
	 */
	private void write_instance_graph(CirInstanceGraph graph) throws Exception {
		this.open(".ins");
		for(Object context : graph.get_contexts()) {
			for(CirInstance node : graph.get_instances(context)) {
				if(node instanceof CirInstanceNode) {
					this.write_instance_node((CirInstanceNode) node);
					for(CirInstanceEdge edge : ((CirInstanceNode) node).get_ou_edges()) {
						this.write_instance_edge(edge);
					}
				}
			}
		}
		this.close();
	}
	/**
	 * [edge]	type	source	target	expression|null boolean|expression|null
	 * @param edge
	 * @throws Exception
	 */
	private void write_dependence_edge(CDependEdge edge) throws Exception {
		this.file_writer.write("[edge]");
		this.file_writer.write("\t" + edge.get_type());
		this.file_writer.write("\t" + this.encode_token(edge.get_source().get_instance()));
		this.file_writer.write("\t" + this.encode_token(edge.get_target().get_instance()));

		Object element = edge.get_element();
		if(element instanceof CDependPredicate) {
			CDependPredicate content = (CDependPredicate) element;
			this.file_writer.write("\t" + this.encode_token(content.get_condition()));
			this.file_writer.write("\t" + this.encode_token(content.get_predicate_value()));
		}
		else if(element instanceof CDependReference) {
			CDependReference content = (CDependReference) element;
			this.file_writer.write("\t" + this.encode_token(content.get_def()));
			this.file_writer.write("\t" + this.encode_token(content.get_use()));
		}
		else {
			this.file_writer.write("\t" + this.encode_token(null));
			this.file_writer.write("\t" + this.encode_token(null));
		}

		this.file_writer.write("\n");
	}
	/**
	 * [node] instance
	 * @param node
	 * @throws Exception
	 */
	private void write_dependence_node(CDependNode node) throws Exception {
		this.file_writer.write("[node]\t" + this.encode_token(node.get_instance()) + "\n");
	}
	/**
	 * [node] 	instance
	 * [edge]	type	source	target	expression boolean|expression
	 * @param graph
	 * @throws Exception
	 */
	private void write_dependence_graph(CDependGraph graph) throws Exception {
		this.open(".dep");
		for(CDependNode node : graph.get_nodes()) {
			this.write_dependence_node(node);
			for(CDependEdge edge : node.get_ou_edges()) {
				this.write_dependence_edge(edge);
			}
		}
		this.close();
	}
	/**
	 * xxx.ins, xxx.dep
	 * @throws Exception
	 */
	private void write_flow_features(CDependGraph graph) throws Exception {
		this.write_instance_graph(graph.get_program_graph());
		this.write_dependence_graph(graph);
	}
	
	/* testing related data features */
	/**
	 * ID parameter
	 * @param test
	 * @throws Exception
	 */
	private void write_tst(TestInput test) throws Exception {
		this.file_writer.write(this.encode_token(test) + "\t" + this.encode_token(test.get_parameter()) + "\n");
	}
	/**
	 * ID tst@parameter
	 * @throws Exception
	 */
	private void write_tst() throws Exception {
		this.open(".tst");
		MuTestProjectTestSpace tspace = this.inputs.get_code_space().get_project().get_test_space();
		for(TestInput test : tspace.get_test_space().get_inputs()) {
			this.write_tst(test);
		}
		this.close();
	}
	/**
	 * ID class operator location parameter coverage weak strong
	 * @param mutant
	 * @throws Exception
	 */
	private void write_mut(Mutant mutant) throws Exception {
		this.file_writer.write("" + this.encode_token(mutant));
		this.file_writer.write("\t" + mutant.get_mutation().get_class());
		this.file_writer.write("\t" + mutant.get_mutation().get_operator());
		this.file_writer.write("\t" + this.encode_token(mutant.get_mutation().get_location()));
		this.file_writer.write("\t" + this.encode_token(mutant.get_mutation().get_parameter()));

		this.file_writer.write("\t" + this.encode_token(mutant.get_coverage_mutant()));
		this.file_writer.write("\t" + this.encode_token(mutant.get_weak_mutant()));
		this.file_writer.write("\t" + this.encode_token(mutant.get_strong_mutant()));

		this.file_writer.write("\n");
	}
	/**
	 * ID class operator location parameter coverage weak strong
	 * @throws Exception
	 */
	private void write_mut() throws Exception {
		this.open(".mut");
		for(Mutant mutant : this.inputs.get_mutant_space().get_mutants()) {
			this.write_mut(mutant);
		}
		this.close();
	}
	/**
	 * MID bit_string
	 * @param result
	 * @throws Exception
	 */
	private void write_res(MuTestProjectTestResult result) throws Exception {
		this.file_writer.write(this.encode_token(result.get_mutant()) + "\t" + result.get_kill_set().toString() + "\n");
	}
	/**
	 * MID bit_string
	 * @throws Exception
	 */
	private void write_res() throws Exception {
		this.open(".res");
		MuTestProjectTestSpace tspace = this.inputs.get_code_space().get_project().get_test_space();
		for(Mutant mutant : this.inputs.get_mutant_space().get_mutants()) {
			MuTestProjectTestResult result = tspace.get_test_result(mutant);
			if(result != null)
				this.write_res(result);
		}
		this.close();
	}
	/**
	 * xxx.mut, xxx.tst, xxx.res
	 * @throws Exception
	 */
	private void write_test_features() throws Exception {
		this.write_tst(); this.write_mut(); this.write_res();
	}
	
	/* symbolic expression trees */
	/**
	 * ID class source{Ast|Cir|Exe|Null|Const} data_type content code [child*]
	 * @param node
	 * @throws Exception
	 */
	private void write_sym_node(SymbolNode node, Set<String> records) throws Exception {
		String node_key = this.encode_token(node);

		if(!records.contains(node_key)) {
			this.file_writer.write(this.encode_token(node));

			String class_name = node.getClass().getSimpleName();
			this.file_writer.write("\t" + class_name.substring(6));
			this.file_writer.write("\t" + this.encode_token(node.get_source()));

			CType data_type;
			if(node instanceof SymbolExpression) {
				data_type = ((SymbolExpression) node).get_data_type();
			}
			else {
				data_type = null;
			}
			this.file_writer.write("\t" + this.encode_token(data_type));

			Object content;
			if(node instanceof SymbolField) {
				content = ((SymbolField) node).get_name();
			}
			else if(node instanceof SymbolOperator) {
				content = ((SymbolOperator) node).get_operator();
			}
			else if(node instanceof SymbolIdentifier) {
				content = ((SymbolIdentifier) node).get_name();
			}
			else if(node instanceof SymbolConstant) {
				content = ((SymbolConstant) node).get_constant();
			}
			else if(node instanceof SymbolLiteral) {
				content = ((SymbolLiteral) node).get_literal();
			}
			else if(node instanceof SymbolBinaryExpression) {
				content = ((SymbolBinaryExpression) node).get_operator().get_operator();
			}
			else if(node instanceof SymbolUnaryExpression) {
				content = ((SymbolUnaryExpression) node).get_operator().get_operator();
			}
			else if(node instanceof SymbolFieldExpression) {
				content = CPunctuator.dot;
			}
			else {
				content = null;
			}
			this.file_writer.write("\t" + this.encode_token(content));

			this.file_writer.write("\t" + this.encode_token(node.generate_code(true)));

			this.file_writer.write("\t[");
			for(SymbolNode child : node.get_children()) {
				this.file_writer.write(" " + this.encode_token(child));
			}
			this.file_writer.write(" ]");

			this.file_writer.write("\n");
		}
		records.add(node_key);

		for(SymbolNode child : node.get_children()) {
			this.write_sym_node(child, records);
		}
	}
	/**
	 * write the symbolic expression nodes in this.sym_nodes to writer and output the number
	 * of symbolic expression nodes being printed to output file.
	 * @return
	 * @throws Exception
	 */
	private int write_sym_nodes() throws Exception {
		Set<String> records = new HashSet<>();
		for(SymbolNode node : this.symbol_nodes) {
			this.write_sym_node(node, records);
		}
		return records.size();
	}
	/**
	 * write the symbolic nodes into xxx.sym
	 * @return
	 * @throws Exception
	 */
	private int write_sym() throws Exception {
		this.open(".sym");
		int number = this.write_sym_nodes();
		this.close();
		return number;
	}
	/**
	 * (\tattribute) (\tannotation)+ (\t;)
	 * @param node
	 * @return whether the node is successfully printed on line
	 * @throws Exception
	 */
	private boolean write_cir_infection_node(CirInfectionNode node) throws Exception {
		if(node.get_data().is_executed()) {
			/* I. capture the annotations to be printed in the node */
			Collection<CirAnnotation> annotations = new HashSet<CirAnnotation>();
			for(CirAnnotation annotation : node.get_data().get_symbolic_annotations()) {
				annotations.add(annotation);
			}
			for(CirAnnotation annotation : node.get_data().get_abstract_annotations()) {
				annotations.add(annotation);
			}
			
			/* II. output: (\tattribute) (\tannotation)+ (\t;) */
			this.write_cir_attribute(node);
			for(CirAnnotation annotation : annotations) {
				this.write_cir_annotation(annotation);
			}
			this.file_writer.write("\t;");
			
			return true;
		}
		else {
			return false;
		}
	}
	/**
	 * mid tid {(\t attribute) (\t annotation)+ (\t ;)}+ \n
	 * @param mutant
	 * @param test_case
	 * @param node_list
	 * @return the number of nodes being printed on the line
	 * @throws Exception
	 */
	private int write_cir_infection_line(Mutant mutant, TestInput test_case, Iterable<CirInfectionNode> node_list) throws Exception {
		/* mid tid */
		String mid = this.encode_token(mutant);
		String tid = this.encode_token(test_case);
		this.file_writer.write(mid + "\t" + tid);
		
		/* { (\t attribute) (\t annotation)* (\t ;) }* */
		int number_of_nodes = 0;
		for(CirInfectionNode node : node_list) {
			if(this.write_cir_infection_node(node))
				number_of_nodes++;
		}
		
		/* new_line */	this.file_writer.write("\n");
		
		return number_of_nodes;	/* number of nodes being printed on line */
	}
	/**
	 * Write all the nodes in BFS traversal sequence in the tree to xxx.stn
	 * @param tree
	 * @param test_case
	 * @return the number of tree nodes being printed into the line
	 * @throws Exception
	 */
	private int write_cir_infection_nodes(CirInfectionTree tree, TestInput test_case) throws Exception {
		/* 1. collect the sequence of nodes for being printed */
		List<CirInfectionNode> node_list = new ArrayList<CirInfectionNode>();
		Iterator<CirInfectionNode> node_iterator = tree.get_nodes();
		while(node_iterator.hasNext()) { node_list.add(node_iterator.next()); }
		
		/* 2. print the nodes in BFS traversal from the tree to xxx.stn */
		return this.write_cir_infection_line(tree.get_mutant(), test_case, node_list);
	}
	/**
	 * Write every root-leaf path in the tree to multiple lines of the xxx.stp
	 * @param tree
	 * @param test_case
	 * @return number_of_paths
	 * @throws Exception
	 */
	private int write_cir_infection_paths(CirInfectionTree tree, TestInput test_case) throws Exception {
		/* every root-leaf path refers to one single line in xxx.stp */
		int number_of_paths = 0;
		for(CirInfectionNode leaf : tree.get_leafs()) {
			/* collect the nodes in sequence of each root-leaf path */
			Iterable<CirInfectionEdge> path = leaf.get_root_path();
			List<CirInfectionNode> node_list = new ArrayList<CirInfectionNode>();
			for(CirInfectionEdge edge : path) { node_list.add(edge.get_source()); } 
			node_list.add(leaf);
			
			/* print one line for every root-leaf path into xxx.stp */
			this.write_cir_infection_line(tree.get_mutant(), test_case, node_list);
			number_of_paths++;
		}
		return number_of_paths;
	}
	/**
	 * write xxx.stn by printing nodes in each tree by BFS traversal in each line
	 * @param dependence_graph
	 * @param test_cases
	 * @throws Exception
	 */
	private void write_stn(CDependGraph dependence_graph, Collection<TestInput> test_cases) throws Exception {
		/* 1. initialize the counter of evaluation */
		int number_of_nodes = 0, number_of_trees = 0;
		Set<Mutant> mutants = new HashSet<Mutant>();
		this.open(".stn");
		
		/* 2. static evaluation */
		if(test_cases == null || test_cases.isEmpty()) {
			for(Mutant mutant : this.inputs.get_mutant_space().get_mutants()) {
				CirInfectionTree tree = CirInfectionTree.new_tree(mutant, dependence_graph);
				if(this.is_tree_available(tree)) {
					tree.add_states(null, this.max_infected_times, null);
					tree.sum_states();
					number_of_nodes += this.write_cir_infection_nodes(tree, null);
					number_of_trees++;
					mutants.add(mutant);
				}
			}
		}
		/* 3. dynamic evaluation */
		else {
			/* I. perform instrumental execution to capture state info */
			MuTestProjectTestSpace tspace = inputs.get_code_space().get_project().get_test_space();
			Map<TestInput, Collection<Mutant>> test_mutants = this.initial_tests_mutants(test_cases);
			System.out.println("\t==> Select " + test_mutants.size() + " test cases to evaluate.");
			
			/* II. print symbolic execution for each mutant-test pair */
			int counter = 0;
			for(TestInput test_case : test_mutants.keySet()) {
				/* A. download the concrete execution path from testing */
				CStatePath state_path = tspace.load_instrumental_path(
						this.inputs.get_sizeof_template(), 
						this.inputs.get_ast_tree(), this.inputs.get_cir_tree(), test_case);
				counter++;
				if(state_path == null) { continue; /* avoid useless test case */ }
				else { 
					System.out.println("\t\tProceed [" + counter + "/" + test_mutants.size() + "]");
				}
				
				/* B. construct mutation trees for reachable mutants */
				Collection<CirInfectionTree> trees = new ArrayList<CirInfectionTree>();
				for(Mutant mutant : test_mutants.get(test_case)) {
					CirInfectionTree tree = CirInfectionTree.new_tree(mutant, state_path);
					if(this.is_tree_available(tree)) { trees.add(tree); }
				}
				
				/* C. perform dynamic context-sensitive evaluations */
				SymbolProcess context = new SymbolProcess(this.inputs.
						get_ast_tree(), this.inputs.get_cir_tree());
				for(CStateNode state_node : state_path.get_nodes()) {
					CirExecution execution = state_node.get_execution();
					context.accumulate(state_node);
					for(CirInfectionTree tree : trees) {
						tree.add_states(execution, this.max_infected_times, context);
					}
				}
				
				/* D. output the symbolic features to output file */
				for(CirInfectionTree tree : trees) {
					tree.sum_states();
					number_of_nodes += this.write_cir_infection_nodes(tree, test_case);
					number_of_trees++;
					mutants.add(tree.get_mutant());
				}
			}
		}
		
		/* 4. print the number information to users */
		this.close();
		System.out.println(String.format("\t==> Print %d nodes, %d trees of %d/%d mutants with %d expressions.", 
				number_of_nodes, number_of_trees, mutants.size(), this.inputs.get_mutant_space().size(), this.symbol_nodes.size()));
	}
	/**
	 * write xxx.stp by printing each root-leaf path onto each line in the file
	 * @param dependence_graph
	 * @param test_cases
	 * @throws Exception
	 */
	private void write_stp(CDependGraph dependence_graph, Collection<TestInput> test_cases) throws Exception {
		/* 1. initialize the counter of evaluation */
		int number_of_paths = 0, number_of_trees = 0;
		Set<Mutant> mutants = new HashSet<Mutant>();
		this.open(".stp");
		
		/* 2. static evaluation */
		if(test_cases == null || test_cases.isEmpty()) {
			for(Mutant mutant : this.inputs.get_mutant_space().get_mutants()) {
				CirInfectionTree tree = CirInfectionTree.new_tree(mutant, dependence_graph);
				if(this.is_tree_available(tree)) {
					tree.add_states(null, this.max_infected_times, null);
					tree.sum_states();
					number_of_paths += this.write_cir_infection_paths(tree, null);
					number_of_trees++;
					mutants.add(mutant);
				}
			}
		}
		/* 3. dynamic evaluation */
		else {
			/* I. perform instrumental execution to capture state info */
			MuTestProjectTestSpace tspace = inputs.get_code_space().get_project().get_test_space();
			Map<TestInput, Collection<Mutant>> test_mutants = this.initial_tests_mutants(test_cases);
			System.out.println("\t==> Select " + test_mutants.size() + " test cases to evaluate.");
			
			/* II. print symbolic execution for each mutant-test pair */
			int counter = 0;
			for(TestInput test_case : test_mutants.keySet()) {
				/* A. download the concrete execution path from testing */
				CStatePath state_path = tspace.load_instrumental_path(
						this.inputs.get_sizeof_template(), 
						this.inputs.get_ast_tree(), this.inputs.get_cir_tree(), test_case);
				counter++;
				if(state_path == null) { continue; /* avoid useless test case */ }
				else { 
					System.out.println("\t\tProceed [" + counter + "/" + test_mutants.size() + "]");
				}
				
				/* B. construct mutation trees for reachable mutants */
				Collection<CirInfectionTree> trees = new ArrayList<CirInfectionTree>();
				for(Mutant mutant : test_mutants.get(test_case)) {
					CirInfectionTree tree = CirInfectionTree.new_tree(mutant, state_path);
					if(this.is_tree_available(tree)) { trees.add(tree); }
				}
				
				/* C. perform dynamic context-sensitive evaluations */
				SymbolProcess context = new SymbolProcess(this.inputs.
						get_ast_tree(), this.inputs.get_cir_tree());
				for(CStateNode state_node : state_path.get_nodes()) {
					CirExecution execution = state_node.get_execution();
					context.accumulate(state_node);
					for(CirInfectionTree tree : trees) {
						tree.add_states(execution, this.max_infected_times, context);
					}
				}
				
				/* D. output the symbolic features to output file */
				for(CirInfectionTree tree : trees) {
					tree.sum_states();
					number_of_paths += this.write_cir_infection_paths(tree, test_case);
					number_of_trees++;
					mutants.add(tree.get_mutant());
				}
			}
		}
		
		/* 4. print the number information to users */
		this.close();
		System.out.println(String.format("\t==> Print %d paths, %d trees of %d/%d mutants with %d expressions.", 
				number_of_paths, number_of_trees, mutants.size(), this.inputs.get_mutant_space().size(), this.symbol_nodes.size()));
	}
	/**
	 * xxx.stn + xxx.sym
	 * @param dependence_graph
	 * @param test_cases
	 * @throws Exception
	 */
	private void write_symb_features(CDependGraph dependence_graph, Collection<TestInput> test_cases) throws Exception {
		this.symbol_nodes.clear();
		this.write_stn(dependence_graph, test_cases);
		this.write_stp(dependence_graph, test_cases);
		this.write_sym();
		this.symbol_nodes.clear();
	}
	
	/* public interfaces for writing */
	/**
	 * write all the features {code, flow, test, symbolic}
	 * @param input
	 * @param output_directory
	 * @param test_cases
	 * @throws Exception
	 */
	private void write_all(MuTestProjectCodeFile input, 
			File output_directory, Collection<TestInput> test_cases,
			int max_infected_times) throws Exception {
		/* 1. initialize the writer */	
		this.set_IO(input, output_directory, max_infected_times);
		
		/* 2. dependence analysis */
		CirFunction root_function =
				this.inputs.get_cir_tree().get_function_call_graph().get_main_function();
		CDependGraph dependence_graph = CDependGraph.graph(CirCallContextInstanceGraph.
						graph(root_function, CirFunctionCallPathType.unique_path, -1));
		
		/* 3. write program features */
		this.write_code_features();
		this.write_test_features();
		this.write_flow_features(dependence_graph);
		this.write_symb_features(dependence_graph, test_cases);
	}
	public static void write_features(MuTestProjectCodeFile input, File output_directory, int max_infected_times) throws Exception {
		writer.write_all(input, output_directory, null, max_infected_times);
	}
	public static void write_features(MuTestProjectCodeFile input, 
			File output_directory, Collection<TestInput> test_cases, int max_infected_times) throws Exception {
		writer.write_all(input, output_directory, test_cases, max_infected_times);
	}
	
}
