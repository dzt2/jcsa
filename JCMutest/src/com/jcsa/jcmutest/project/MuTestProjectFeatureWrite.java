package com.jcsa.jcmutest.project;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.sym2mutant.cond.SymCondition;
import com.jcsa.jcmutest.mutant.sym2mutant.tree.SymInstanceContent;
import com.jcsa.jcmutest.mutant.sym2mutant.tree.SymInstanceTree;
import com.jcsa.jcmutest.mutant.sym2mutant.tree.SymInstanceTreeEdge;
import com.jcsa.jcmutest.mutant.sym2mutant.tree.SymInstanceTreeNode;
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
import com.jcsa.jcparse.test.file.TestInput;
import com.jcsa.jcparse.test.state.CStatePath;

/**
 * 	It generates the feature files for describing program, test, mutation and result files in specified output directory.
 * 	<br>
 * 	<code>
 * 	+-------------------------------------------------------------------------------------------------------------------+	<br>
 * 	| xxx.c		|	The source code directly copied from the xxx.i being preprocessed for compilation.					|	<br>
 * 	| xxx.ast	|	Abstract-Syntactic-Tree 	{key, class, beg_index, end_index, data_type, content, [child]*}		|	<br>
 * 	| xxx.cir	|	C-Intermediate Represent 	{key, class, ast_source, data_type, content, [child]*}					|	<br>
 * 	| xxx.flw	|	Control Flow Graph			{[beg:name] {[node]|[edge]|[call]}+ [end:name]}+						|	<br>
 * 	+-------------------------------------------------------------------------------------------------------------------+	<br>
 * 	| xxx.ins	|	Parameterized Flow Graph	{[node:inst] [edge:inst]}+												|	<br>
 * 	| xxx.dep	|	
 * 	+-------------------------------------------------------------------------------------------------------------------+	<br>
 * 	</code>
 * 	<br>
 * 	@author yukimula
 *
 */
public class MuTestProjectFeatureWrite {
	
	/* definitions */
	/** the input source code file for creating features **/
	private MuTestProjectCodeFile inputs;
	/** the directory where feature files were generated **/
	private File output_directory;
	/** it is used to output string text to current file **/
	private FileWriter writer;
	/** set to preserve symbolic nodes used for features **/
	private Set<SymbolNode> symbol_nodes;
	
	/* singleton pattern */
	/** private constructor for singleton design pattern **/
	private MuTestProjectFeatureWrite() { this.symbol_nodes = new HashSet<SymbolNode>(); }
	/** the single instance for generating symbolic features of input source code file **/
	private static final MuTestProjectFeatureWrite util = new MuTestProjectFeatureWrite();
	
	/* input-output methods */
	/**
	 * close writer
	 * @throws Exception
	 */
	private void close() throws Exception {
		if(this.writer != null) {
			this.writer.close();
			this.writer = null;
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
		file_name = file_name.substring(0, index).strip();
		output_file = new File(this.output_directory.getAbsolutePath() + "/" + file_name + postfix);
		System.out.println("\t==> Start to Write \"" + output_file.getAbsolutePath() + "\"...");
		
		/* open a new writer */
		this.close(); this.writer = new FileWriter(output_file);
	}
	/**
	 * set the inputs and output directory for generating feature files
	 * @param inputs
	 * @param output_directory
	 * @throws Exception
	 */
	private void set_IO(MuTestProjectCodeFile inputs, File output_directory) throws Exception {
		if(inputs == null) {
			throw new IllegalArgumentException("Invalid input: null");
		}
		else if(output_directory == null) {
			throw new IllegalArgumentException("Invalid output: null");
		}
		else if(output_directory.exists() && !output_directory.isDirectory()) {
			throw new IllegalArgumentException("Invalid: " + output_directory.getAbsolutePath());
		}
		else {
			if(!output_directory.exists()) { FileOperations.mkdir(output_directory); }
			this.inputs = inputs;
			this.output_directory = output_directory;
			this.close();
		}
	}
	
	/* basic encoding methods */
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
			String name = token.getClass().getSimpleName();
			return "sym@" + name + "@" + token.hashCode();
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
			this.writer.write(buffer, 0, length);
		reader.close();
		this.close();
	}
	/**
	 * ast@key class_name beg_index end_index data_type content [ {ast@key}* ] 
	 * @param node
	 * @throws Exception
	 */
	private void write_ast(AstNode node) throws Exception {
		this.writer.write(this.encode_token(node));
		
		String class_name = node.getClass().getSimpleName();
		class_name = class_name.substring(3, class_name.length() - 4).strip();
		int beg_index = node.get_location().get_bias();
		int end_index = beg_index + node.get_location().get_length();
		this.writer.write("\t" + class_name + "\t" + beg_index + "\t" + end_index);
		
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
		this.writer.write("\t" + this.encode_token(data_type));
		
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
		this.writer.write("\t" + this.encode_token(content));
		
		this.writer.write("\t[");
		for(int k = 0; k < node.number_of_children(); k++) {
			this.writer.write(" " + this.encode_token(node.get_child(k)));
		}
		this.writer.write(" ]");
		
		this.writer.write("\n");
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
		this.writer.write(this.encode_token(node));
		
		String class_name = node.getClass().getSimpleName();
		class_name = class_name.substring(3, class_name.length() - 4).strip();
		this.writer.write("\t" + class_name + "\t" + this.encode_token(node.get_ast_source()));
		
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
		this.writer.write("\t" + this.encode_token(data_type));
		
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
		this.writer.write("\t" + this.encode_token(content));
		
		this.writer.write("\t[");
		for(CirNode child : node.get_children()) {
			this.writer.write(" " + this.encode_token(child));
		}
		this.writer.write(" ]");
		
		String code = null;
		if(!(node instanceof CirFunctionDefinition
			|| node instanceof CirTransitionUnit
			|| node instanceof CirFunctionBody)) {
			code = node.generate_code(true);
		}
		this.writer.write("\t" + this.encode_token(code));
		
		this.writer.write("\n");
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
		this.writer.write("\t" + "[edge]");
		this.writer.write("\t" + flow.get_type());
		this.writer.write("\t" + this.encode_token(flow.get_source()));
		this.writer.write("\t" + this.encode_token(flow.get_target()));
		this.writer.write("\n"); 
	}
	/**
	 * [call] call_exec wait_exec
	 * @param call
	 * @throws Exception
	 */
	private void write_execution_call(CirFunctionCall call) throws Exception {
		this.writer.write("\t" + "[call]");
		this.writer.write("\t" + this.encode_token(call.get_call_execution()));
		this.writer.write("\t" + this.encode_token(call.get_wait_execution()));
		this.writer.write("\n");
	}
	/**
	 * [node] ID cir_statement
	 * @param execution
	 * @throws Exception
	 */
	private void write_execution_node(CirExecution execution) throws Exception {
		this.writer.write("\t" + "[node]");
		this.writer.write("\t" + this.encode_token(execution));
		this.writer.write("\t" + this.encode_token(execution.get_statement()));
		this.writer.write("\n");
	}
	/**
	 * 	[node] ID cir_statement
	 * 	[edge] type source target
	 * 	[call] call_exec wait_exec
	 * @param function
	 * @throws Exception
	 */
	private void write_cir_function(CirFunction function) throws Exception {
		this.writer.write("[beg]\t" + function.get_name() + "\n");
		for(CirExecution execution : function.get_flow_graph().get_executions()) {
			this.write_execution_node(execution);
			for(CirExecutionFlow flow : execution.get_ou_flows()) {
				this.write_execution_flow(flow);
			}
		}
		for(CirFunctionCall call : function.get_ou_calls()) {
			this.write_execution_call(call);
		}
		this.writer.write("[end]\t" + function.get_name() + "\n");
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
			this.writer.write("\n");
		}
		this.close();
	}
	
	/* dependence analysis */
	/**
	 * [edge] type source target
	 * @param edge
	 * @throws Exception
	 */
	private void write_instance_edge(CirInstanceEdge edge) throws Exception {
		this.writer.write("[edge]\t" + edge.get_type());
		this.writer.write("\t" + this.encode_token(edge.get_source()));
		this.writer.write("\t" + this.encode_token(edge.get_target()));
		this.writer.write("\n");
	}
	/**
	 * [node] ins@txt@int@int execution context
	 * @param node
	 * @throws Exception
	 */
	private void write_instance_node(CirInstanceNode node) throws Exception {
		this.writer.write("[node]");
		this.writer.write("\t" + this.encode_token(node));
		this.writer.write("\t" + this.encode_token(node.get_execution()));
		int context = 0;
		if(node.get_context() != null) context = node.get_context().hashCode();
		this.writer.write("\t" + context);
		this.writer.write("\n");
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
	 * [edge]	type	source	target	expression boolean|expression
	 * @param edge
	 * @throws Exception
	 */
	private void write_dependence_edge(CDependEdge edge) throws Exception {
		this.writer.write("[edge]");
		this.writer.write("\t" + edge.get_type());
		this.writer.write("\t" + this.encode_token(edge.get_source().get_instance()));
		this.writer.write("\t" + this.encode_token(edge.get_target().get_instance()));
		
		Object element = edge.get_element();
		if(element instanceof CDependPredicate) {
			CDependPredicate content = (CDependPredicate) element;
			this.writer.write("\t" + this.encode_token(content.get_condition()));
			this.writer.write("\t" + this.encode_token(content.get_predicate_value()));
		}
		else {
			CDependReference content = (CDependReference) element;
			this.writer.write("\t" + this.encode_token(content.get_def()));
			this.writer.write("\t" + this.encode_token(content.get_use()));
		}
		
		this.writer.write("\n");
	}
	/**
	 * [node] instance
	 * @param node
	 * @throws Exception
	 */
	private void write_dependence_node(CDependNode node) throws Exception {
		this.writer.write("[node]\t" + this.encode_token(node.get_instance()) + "\n");
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
	
	/* testing related data features */
	/**
	 * ID parameter
	 * @param test
	 * @throws Exception
	 */
	private void write_tst(TestInput test) throws Exception {
		this.writer.write(this.encode_token(test) + "\t" + this.encode_token(test.get_parameter()) + "\n");
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
		this.writer.write("" + this.encode_token(mutant));
		this.writer.write("\t" + mutant.get_mutation().get_class());
		this.writer.write("\t" + mutant.get_mutation().get_operator());
		this.writer.write("\t" + this.encode_token(mutant.get_mutation().get_location()));
		this.writer.write("\t" + this.encode_token(mutant.get_mutation().get_parameter()));
		
		this.writer.write("\t" + this.encode_token(mutant.get_coverage_mutant()));
		this.writer.write("\t" + this.encode_token(mutant.get_weak_mutant()));
		this.writer.write("\t" + this.encode_token(mutant.get_strong_mutant()));
		
		this.writer.write("\n");
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
		this.writer.write(this.encode_token(result.get_mutant()) + "\t" + result.get_kill_set().toString() + "\n");
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
	
	/* symbolic evaluation */
	/**
	 * @param mutant
	 * @return whether the mutant is available for being printed
	 * @throws Exception
	 */
	private boolean is_available_mutant(Mutant mutant) throws Exception {
		if(mutant == null) {
			return false;
		}
		else if(!mutant.has_cir_mutations()) {
			return false;
		}
		else if(this.inputs.get_code_space().get_project().get_test_space().get_test_result(mutant) == null) {
			return false;
		}
		else {
			return true;
		}
	}
	/**
	 * @param test_cases
	 * @return True if test cases have been selected or False to evaluate under static analysis
	 */
	private boolean is_selected(Collection<TestInput> test_cases) {
		return (test_cases != null) && !(test_cases.isEmpty());
	}
	/**
	 * generate instrumental files for given test cases in the source project by dynamic testing
	 * @param test_cases
	 * @throws Exception
	 */
	private void generate_instrument_files(Collection<TestInput> test_cases) throws Exception {
		if(this.is_selected(test_cases))
			this.inputs.get_code_space().get_project().execute_instrumental(test_cases);
	}
	/**
	 * perform static or dynamic evaluation on the specified test cases.
	 * @param trees the collection of symbolic instance trees to be evaluated
	 * @param test_cases
	 * @throws Exception
	 */
	private void evaluate_sym_instance_trees(Collection<SymInstanceTree> trees, 
			Collection<TestInput> test_cases) throws Exception {
		MuTestProjectTestSpace tspace = this.inputs.get_code_space().get_project().get_test_space();
		if(this.is_selected(test_cases)) {				/* CASE-I. DYNAMIC EVALUATION USED */
			for(TestInput test_case : test_cases) {
				CStatePath state_path = tspace.load_instrumental_path(this.inputs.get_sizeof_template(), 
									this.inputs.get_ast_tree(), this.inputs.get_cir_tree(), test_case);
				if(state_path != null) { for(SymInstanceTree tree : trees) tree.evaluate(state_path); }
			}
		}
		else {											/* CASE-II. STATIS EVALUATION USED */
			for(SymInstanceTree tree : trees) { tree.evaluate(); }
		}
	}
	/**
	 * Id of the selected test cases as context under asssumption for each line.
	 * @param test_cases
	 * @throws Exception
	 */
	private void write_stc(Collection<TestInput> test_cases) throws Exception {
		this.open(".stc");
		if(this.is_selected(test_cases)) {
			for(TestInput test_case : test_cases) {
				this.writer.write(test_case.get_id() + "\n");
			}
		}
		this.close();
	}
	
	/* symbolic writers */
	/**
	 * category$operator$execution$location$parameter
	 * @param condition
	 * @throws Exception
	 */
	private void write_sym_condition(SymCondition condition) throws Exception {
		/* declarations */
		String category = condition.get_category().toString();
		String operator = condition.get_operator().toString();
		CirExecution execution = condition.get_execution();
		CirNode location = condition.get_location();
		SymbolNode parameter = condition.get_parameter();
		
		this.writer.write(category.toString());
		this.writer.write("$" + operator.toString());
		this.writer.write("$" + this.encode_token(execution));
		this.writer.write("$" + this.encode_token(location));
		this.writer.write("$" + this.encode_token(parameter));
		
		if(parameter != null) { this.symbol_nodes.add(parameter); }
	}
	/**
	 * exec$accp$rejc [condition]+ ;
	 * @param content
	 * @throws Exception
	 */
	private void write_sym_content(SymInstanceContent content) throws Exception {
		/* exec$accp$rejc */
		int executions = content.get_status().number_of_executions();
		int acceptions = content.get_status().number_of_acceptions();
		int rejections = content.get_status().number_of_rejections();
		this.writer.write(executions + "$" + acceptions + "$" + rejections);
		
		/* [\t category@operator@execution@location@parameter]+ */
		for(SymCondition condition : content.get_status().get_conditions()) {
			this.writer.write("\t");
			this.write_sym_condition(condition);
		}
		
		/* \t; */	this.writer.write("\t;");
	}
	/**
	 * mid {head {condition}+ ;}*		[only executions > 0 be printed]
	 * @param mutant
	 * @param contents
	 * @throws Exception
	 */
	private void write_sym_contents(Mutant mutant, Iterable<SymInstanceContent> contents) throws Exception {
		this.writer.write("" + mutant.get_id());
		for(SymInstanceContent content : contents) {
			if(content.get_status().is_executed()) {
				this.writer.write("\t");
				this.write_sym_content(content);
			}
		}
		this.writer.write("\n");
		this.writer.flush();	/* update the feature line to file */
	}
	/**
	 * mid {all nodes and edges}
	 * @param tree
	 * @throws Exception
	 */
	private int write_sym_instance_tree(SymInstanceTree tree) throws Exception {
		List<SymInstanceContent> contents = new ArrayList<SymInstanceContent>();
		Queue<SymInstanceTreeNode> queue = new LinkedList<SymInstanceTreeNode>();
		
		queue.add(tree.get_root());
		while(!queue.isEmpty()) {
			SymInstanceTreeNode tree_node = queue.poll();
			contents.add(tree_node);
			for(SymInstanceTreeEdge edge : tree_node.get_ou_edges()) {
				contents.add(edge); queue.add(edge.get_target());
			}
		}
		
		this.write_sym_contents(tree.get_mutant(), contents);
		return 1;
	}
	/**
	 * @param trees
	 * @return write the nodes and edges in symbolic trees to each line of the output file
	 * @throws Exception
	 */
	private int write_sym_instance_trees(Collection<SymInstanceTree> trees) throws Exception {
		for(SymInstanceTree tree : trees) {
			this.write_sym_instance_tree(tree);
		}
		return trees.size();
	}
	/**
	 * mid {nodes and edges in one path from root to a leaf}
	 * @param tree
	 * @throws Exception
	 */
	private int write_sym_instance_path(SymInstanceTree tree) throws Exception {
		Collection<SymInstanceTreeNode> leafs = tree.get_leafs();
		int counters = 0;
		for(SymInstanceTreeNode leaf : leafs) {
			List<SymInstanceTreeEdge> path = leaf.get_prev_path();
			List<SymInstanceContent> contents = new ArrayList<SymInstanceContent>();
			contents.add(tree.get_root());
			for(SymInstanceTreeEdge edge : path) {
				contents.add(edge);
				contents.add(edge.get_target());
			}
			if(!contents.isEmpty()) {
				counters++;
				this.write_sym_contents(tree.get_mutant(), contents);
			}
		}
		return counters;
	}
	/**
	 * @param trees
	 * @return write all the paths of each symbolic trees to each line of the output file
	 * @throws Exeption
	 */
	private int write_sym_instance_paths(Collection<SymInstanceTree> trees) throws Exception {
		int counter = 0;
		for(SymInstanceTree tree : trees) {
			counter += this.write_sym_instance_path(tree);
		}
		return counter;
	}
	/**
	 * ID class source{Ast|Cir|Exe|Null|Const} data_type content code [child*]
	 * @param node
	 * @throws Exception
	 */
	private void write_sym_node(SymbolNode node, Set<String> records) throws Exception {
		String node_key = this.encode_token(node);
		
		if(!records.contains(node_key)) {
			this.writer.write(this.encode_token(node));
			
			String class_name = node.getClass().getSimpleName();
			this.writer.write("\t" + class_name.substring(6));
			this.writer.write("\t" + this.encode_token(node.get_source()));
			
			CType data_type;
			if(node instanceof SymbolExpression) {
				data_type = ((SymbolExpression) node).get_data_type();
			}
			else {
				data_type = null;
			}
			this.writer.write("\t" + this.encode_token(data_type));
			
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
			this.writer.write("\t" + this.encode_token(content));
			
			this.writer.write("\t" + this.encode_token(node.generate_code(true)));
			
			this.writer.write("\t[");
			for(SymbolNode child : node.get_children()) {
				this.writer.write(" " + this.encode_token(child));
			}
			this.writer.write(" ]");
			
			this.writer.write("\n");
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
		Set<String> records = new HashSet<String>();
		for(SymbolNode node : this.symbol_nodes) {
			this.write_sym_node(node, records);
		}
		return records.size();
	}
	/**
	 * xxx.stc, xxx.sit, xxx.sip, xxx.sym
	 * @param dependence_graph
	 * @param distance
	 * @param test_cases
	 * @throws Exception
	 */
	private void write_sym_features(CDependGraph dependence_graph, int distance, Collection<TestInput> test_cases) throws Exception {
		/* 1. declarations and initialization */
		this.symbol_nodes.clear();
		Collection<SymInstanceTree> trees = new ArrayList<SymInstanceTree>(); 
		int number_of_trees = 0, number_of_paths = 0, number_of_nodes = 0, number_of_mutants;
		
		/* 2. generate symbolic instance trees for each available mutant */
		number_of_mutants = this.inputs.get_mutant_space().size();
		for(Mutant mutant : this.inputs.get_mutant_space().get_mutants()) {
			/* NOTE only output features for mutant that has cir-mutation and tested */
			if(this.is_available_mutant(mutant)) {
				trees.add(SymInstanceTree.new_tree(mutant, distance, dependence_graph));
			}
		}
		
		/* 3. perform evaluation and record the selected tests for dynamic evaluation */ 
		this.evaluate_sym_instance_trees(trees, test_cases);	this.write_stc(test_cases);
		
		/* 4. xxx.sit */	
		this.open(".sit"); number_of_trees = this.write_sym_instance_trees(trees); this.close();
		
		/* 5. xxx.sip */
		this.open(".sip"); number_of_paths = this.write_sym_instance_paths(trees); this.close();
		
		/* 6. xxx.sym */
		this.open(".sym"); number_of_nodes = this.write_sym_nodes(); this.close();
		
		/* 7. output summary information */
		System.out.println("\t\t\t--> Print Features for " + number_of_mutants + " mutants using:"
				+ "\t" + number_of_trees + " trees\t" + number_of_paths + " paths\t" + number_of_nodes + " expressions.");
		return;
	}
	
	/* public interfaces */
	/**
	 * write all the feature information to specified output directory for input code file
	 * @param input
	 * @param output_directory
	 * @param test_cases
	 * @param max_distance
	 * @throws Exception
	 */
	private void write_all(MuTestProjectCodeFile input, File output_directory, 
			Collection<TestInput> test_cases, int max_distance) throws Exception {
		/* 1. initialize the writer */	this.set_IO(input, output_directory);
		
		/* 2. static informations */
		this.write_cpp(); this.write_ast(); this.write_cir(); this.write_flw();
		
		/* 3. testing information */
		this.write_tst(); this.write_mut(); this.write_res();
		
		/* 4. symbolic information */
		this.generate_instrument_files(test_cases);
		CirFunction root_function = 
				this.inputs.get_cir_tree().get_function_call_graph().get_main_function();
		CDependGraph dependence_graph = CDependGraph.graph(CirCallContextInstanceGraph.
						graph(root_function, CirFunctionCallPathType.unique_path, -1));
		this.write_instance_graph(dependence_graph.get_program_graph());
		this.write_dependence_graph(dependence_graph);
		this.write_sym_features(dependence_graph, max_distance, test_cases);
	}
	/**
	 * write feature information of code file to output directory with
	 * @param input			the code file from which features created
	 * @param output_directory	where the feature files are generated
	 * @param test_cases	the set of tests used for dynamic evaluation
	 * @param max_distance	the maximal distance for propagating errors
	 * @throws Exception
	 */
	public static void write(MuTestProjectCodeFile input, File output_directory, 
			Collection<TestInput> test_cases, int max_distance) throws Exception {
		util.write_all(input, output_directory, test_cases, max_distance);
	}
	
}
