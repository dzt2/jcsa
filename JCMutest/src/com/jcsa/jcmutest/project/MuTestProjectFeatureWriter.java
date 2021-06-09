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
 * 	This implements the feature encoding for data prepared in each MuTestProject, and the output is organized as:
 * 	<br>
 * 	<code>
 * 	[feature_directory]																										<br>
 * 	|--	xxx.c		Source code file with code after being pre-processed by compiler {xxx.i}								<br>
 * 	|--	xxx.ast		ID class beg_index end_index data_type token [ child child ... child ]									<br>
 * 	|--	xxx.cir		ID class ast_id data_type token [ child child ... child ] code											<br>
 * 	|--	xxx.flw		([beg] name) ([node] exe_id cir_id|[edge] type exe_id exe_id|[call] exe_id exe_id)+ ([end] name)		<br>
 * 	|--	xxx.tst		ID parameter																							<br>
 * 	|--	xxx.mut		ID class operator location parameter coverage weak strong												<br>
 * 	|--	xxx.res		ID bit_string																							<br>
 * 	|--	xxx.stc		test_ID	{selected test cases' ID for dynamic evaluation or None for static evaluation as a result}		<br>
 * 	|--	xxx.sit		the information to preserve status of each symbolic instance among tree.								<br>
 * 	|--	xxx.sip		the information to preserve status of each symbolic instance in tree path								<br>
 * 	|--	xxx.sym		ID class source{Ast|Cir|Exe|Null|Const} data_type content code [child*]									<br>
 * 	</code>
 * 
 * @author yukimula
 *
 */
public class MuTestProjectFeatureWriter {
	
	/* Project-Level */
	/** it provides the data source for feature **/
	private MuTestProjectCodeFile source_file;
	/** xxx as the name of output files' prefix **/
	private String file_name;
	/** directory where the output files are generated **/
	private File output_directory;
	/** it preserves the set of symbolic nodes used to define parameters in symbolic features **/
	private Set<SymbolNode> symbolic_nodes;
	
	/* File-Level */
	/** the file being written by now **/
	private File output_file;
	/** output stream to write feature data to file **/
	private FileWriter writer;
	
	/* singleton mode */
	/**
	 * private constructor for singleton mode
	 */
	private MuTestProjectFeatureWriter() {
		this.source_file = null;
		this.file_name = null;
		this.output_directory = null;
		this.symbolic_nodes = new HashSet<SymbolNode>();
		this.output_file = null;
		this.writer = null;
	}
	/** singleton for feature writing **/
	public static final MuTestProjectFeatureWriter fwriter = new MuTestProjectFeatureWriter();
	
	/* IO operations */
	/**
	 * close the writer and file outputting
	 * @throws Exception
	 */
	private void close() throws Exception {
		if(this.writer != null) {
			this.writer.close();
			this.output_file = null;
			this.writer = null;
		}
	}
	/**
	 * start openning write another in output_directory/xxx.postfix
	 * @param postfix
	 * @throws Exception
	 */
	private void open(String postfix) throws Exception {
		this.close();
		this.output_file = new File(this.output_directory.getAbsolutePath() + "/" + this.file_name + postfix);
		this.writer = new FileWriter(this.output_file);
		System.out.println("\t==> Write to file: " + this.output_file.getAbsolutePath()); /* WARN inform the users */
	}
	/**
	 * set the input and output
	 * @param input
	 * @param output
	 * @throws Exception
	 */
	private void set_IO(MuTestProjectCodeFile input, File output) throws Exception {
		if(input == null) {
			throw new IllegalArgumentException("Invalid input: null");
		}
		else if(output == null) {
			throw new IllegalArgumentException("Invalid output: null");
		}
		else if(output.exists() && !output.isDirectory()) {
			throw new IllegalArgumentException("Invalid: " + output.getAbsolutePath());
		}
		else {
			if(!output.exists()) { FileOperations.mkdir(output); }
			this.source_file = input;
			this.file_name = input.get_name();
			int index = this.file_name.lastIndexOf('.');
			this.file_name = this.file_name.substring(0, index).strip();
			this.output_directory = output;
			this.close();
		}
	}
	
	/* basic encoding methods */
	/**
	 * @ --> \a; space --> \s; $ --> \p;
	 * @param text
	 * @return
	 */
	private String normalize_string(String text) {
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
	 * n@null, b@bool, c@char, i@int, f@real, x@r@i, 
	 * key@str, pun@str, opr@str, 
	 * ast@int, cir@int, mut@int, exe@str@int, tst@int,
	 * s@str [identifier, literal, cir_code]
	 * typ@str, sym@type@hashCode
	 * @param token
	 * @return
	 */
	private String token_string(Object token) throws Exception {
		if(token == null) {
			return "n@null";
		}
		else if(token instanceof Boolean) {
			return "b@" + token.toString();
		}
		else if(token instanceof Character) {
			return "c@" + ((int) ((Character) token).charValue());
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
			return this.token_string(((CConstant) token).get_object());
		}
		else if(token instanceof CKeyword) {
			return "key@" + token.toString();
		}
		else if(token instanceof CPunctuator) {
			return "pun@" + token.toString();
		}
		else if(token instanceof COperator) {
			return "opr@" + token.toString();
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
			return "exe@" + ((CirExecution) token).get_graph().get_function().get_name() + "@" + ((CirExecution) token).get_id();
		}
		else if(token instanceof Mutant) {
			return "mut@" + ((Mutant) token).get_id();
		}
		else if(token instanceof TestInput) {
			return "tst@" + ((TestInput) token).get_id();
		}
		else if(token instanceof SymbolNode) {
			return "sym@" + token.getClass().getSimpleName().substring(6).strip() + "@" + token.hashCode();
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + token.getClass().getSimpleName());
		}
	}
	
	/* static code information writing **/
	/**
	 * copy the code from ifile to xxx.cpp
	 * @throws Exception
	 */
	private void write_cpp() throws Exception {
		this.open(".c");
		FileReader reader = new FileReader(this.source_file.get_ifile());
		char[] buffer = new char[1024 * 1024 * 8]; int length;
		while((length = reader.read(buffer)) >= 0) 
			this.writer.write(buffer, 0, length);
		reader.close();
		this.close();
	}
	/**
	 * ID class beg_index end_index data_type token [ child child ... child ]
	 * @param node
	 * @throws Exception
	 */
	private void write_ast_node(AstNode node) throws Exception {
		String ast_id = this.token_string(node);
		String class_name = node.getClass().getSimpleName().strip();
		class_name = class_name.substring(3, class_name.length() - 4).strip();
		int beg_index = node.get_location().get_bias();
		int end_index = node.get_location().get_bias() + node.get_location().get_length();
		this.writer.write(ast_id + "\t" + class_name + "\t" + beg_index + "\t" + end_index);
		
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
		this.writer.write("\t" + this.token_string(data_type));
		
		Object token;
		if(node instanceof AstIdentifier) {
			token = ((AstIdentifier) node).get_name();
		}
		else if(node instanceof AstConstant) {
			token = ((AstConstant) node).get_constant();
		}
		else if(node instanceof AstKeyword) {
			token = ((AstKeyword) node).get_keyword();
		}
		else if(node instanceof AstPunctuator) {
			token = ((AstPunctuator) node).get_punctuator();
		}
		else if(node instanceof AstOperator) {
			token = ((AstOperator) node).get_operator();
		}
		else {
			token = null;
		}
		this.writer.write("\t" + this.token_string(token));
		
		this.writer.write("\t[");
		for(int k = 0; k < node.number_of_children(); k++) {
			this.writer.write(" " + this.token_string(node.get_child(k)));
		}
		this.writer.write(" ]");
		
		this.writer.write("\n");
	}
	/**
	 * ID class beg_index end_index data_type token [ child child ... child ]
	 * @throws Exception
	 */
	private void write_ast() throws Exception {
		this.open(".ast");
		AstTree ast_tree = this.source_file.get_ast_tree();
		for(int k = 0; k < ast_tree.number_of_nodes(); k++) {
			AstNode node = ast_tree.get_node(k);
			this.write_ast_node(node);
		}
		this.close();
	}
	/**
	 * ID class ast_id data_type token [ child child ... child ] code
	 * @param node
	 * @throws Exception
	 */
	private void write_cir_node(CirNode node) throws Exception {
		String cir_id = this.token_string(node);
		String class_name = node.getClass().getSimpleName().strip();
		class_name = class_name.substring(3, class_name.length() - 4).strip();
		String ast_id = this.token_string(node.get_ast_source());
		this.writer.write(cir_id + "\t" + class_name + "\t" + ast_id);
		
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
		this.writer.write("\t" + this.token_string(data_type));
		
		Object token;
		if(node instanceof CirNameExpression) {
			token = ((CirNameExpression) node).get_unique_name();
		}
		else if(node instanceof CirDeferExpression) {
			token = COperator.dereference;
		}
		else if(node instanceof CirFieldExpression) {
			token = CPunctuator.dot;
		}
		else if(node instanceof CirConstExpression) {
			token = ((CirConstExpression) node).get_constant();
		}
		else if(node instanceof CirCastExpression) {
			token = COperator.assign;
		}
		else if(node instanceof CirAddressExpression) {
			token = COperator.address_of;
		}
		else if(node instanceof CirComputeExpression) {
			token = ((CirComputeExpression) node).get_operator();
		}
		else if(node instanceof CirField) {
			token = ((CirField) node).get_name();
		}
		else if(node instanceof CirLabel) {
			CirNode target = node.get_tree().get_node(((CirLabel) node).get_target_node_id());
			token = target;
		}
		else {
			token = null;
		}
		this.writer.write("\t" + this.token_string(token));
		
		this.writer.write("\t[");
		for(CirNode child : node.get_children()) {
			this.writer.write(" " + this.token_string(child));
		}
		this.writer.write(" ]");
		
		String code = null;
		if(!(node instanceof CirFunctionDefinition
			|| node instanceof CirTransitionUnit
			|| node instanceof CirFunctionBody)) {
			code = node.generate_code(true);
		}
		this.writer.write("\t" + this.token_string(code));
		
		this.writer.write("\n");
	}
	/**
	 * ID class ast_id data_type token [ child child ... child ] code
	 * @throws Exception
	 */
	private void write_cir() throws Exception {
		this.open(".cir");
		for(CirNode node : this.source_file.get_cir_tree().get_nodes()) {
			this.write_cir_node(node);
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
		this.writer.write("\t" + this.token_string(flow.get_source()));
		this.writer.write("\t" + this.token_string(flow.get_target()));
		this.writer.write("\n"); 
	}
	/**
	 * [call] call_exec wait_exec
	 * @param call
	 * @throws Exception
	 */
	private void write_execution_call(CirFunctionCall call) throws Exception {
		this.writer.write("\t" + "[call]");
		this.writer.write("\t" + this.token_string(call.get_call_execution()));
		this.writer.write("\t" + this.token_string(call.get_wait_execution()));
		this.writer.write("\n");
	}
	/**
	 * [node] ID cir_statement
	 * @param execution
	 * @throws Exception
	 */
	private void write_execution_node(CirExecution execution) throws Exception {
		this.writer.write("\t" + "[node]");
		this.writer.write("\t" + this.token_string(execution));
		this.writer.write("\t" + this.token_string(execution.get_statement()));
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
		for(CirFunction function : this.source_file.get_cir_tree().get_function_call_graph().get_functions()) {
			this.write_cir_function(function);
			this.writer.write("\n");
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
		this.writer.write(this.token_string(test) + "\t" + this.token_string(test.get_parameter()) + "\n");
	}
	/**
	 * ID tst@parameter
	 * @throws Exception
	 */
	private void write_tst() throws Exception {
		this.open(".tst");
		MuTestProjectTestSpace tspace = this.source_file.get_code_space().get_project().get_test_space();
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
		this.writer.write("" + this.token_string(mutant));
		this.writer.write("\t" + mutant.get_mutation().get_class());
		this.writer.write("\t" + mutant.get_mutation().get_operator());
		this.writer.write("\t" + this.token_string(mutant.get_mutation().get_location()));
		this.writer.write("\t" + this.token_string(mutant.get_mutation().get_parameter()));
		
		this.writer.write("\t" + this.token_string(mutant.get_coverage_mutant()));
		this.writer.write("\t" + this.token_string(mutant.get_weak_mutant()));
		this.writer.write("\t" + this.token_string(mutant.get_strong_mutant()));
		
		this.writer.write("\n");
	}
	/**
	 * ID class operator location parameter coverage weak strong
	 * @throws Exception
	 */
	private void write_mut() throws Exception {
		this.open(".mut");
		for(Mutant mutant : this.source_file.get_mutant_space().get_mutants()) {
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
		this.writer.write(this.token_string(result.get_mutant()) + "\t" + result.get_kill_set().toString() + "\n");
	}
	/**
	 * MID bit_string
	 * @throws Exception
	 */
	private void write_res() throws Exception {
		this.open(".res");
		MuTestProjectTestSpace tspace = this.source_file.get_code_space().get_project().get_test_space();
		for(Mutant mutant : this.source_file.get_mutant_space().get_mutants()) {
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
		else if(this.source_file.get_code_space().get_project().get_test_space().get_test_result(mutant) == null) {
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
			this.source_file.get_code_space().get_project().execute_instrumental(test_cases);
	}
	/**
	 * perform static or dynamic evaluation on the specified test cases.
	 * @param trees the collection of symbolic instance trees to be evaluated
	 * @param test_cases
	 * @throws Exception
	 */
	private void evaluate_sym_instance_trees(Collection<SymInstanceTree> trees, 
			Collection<TestInput> test_cases) throws Exception {
		MuTestProjectTestSpace tspace = this.source_file.get_code_space().get_project().get_test_space();
		if(this.is_selected(test_cases)) {				/* CASE-I. DYNAMIC EVALUATION USED */
			for(TestInput test_case : test_cases) {
				CStatePath state_path = tspace.load_instrumental_path(this.source_file.get_sizeof_template(), 
									this.source_file.get_ast_tree(), this.source_file.get_cir_tree(), test_case);
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
		this.writer.write("$" + this.token_string(execution));
		this.writer.write("$" + this.token_string(location));
		this.writer.write("$" + this.token_string(parameter));
		
		if(parameter != null) { this.symbolic_nodes.add(parameter); }
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
		String node_key = this.token_string(node);
		
		if(!records.contains(node_key)) {
			this.writer.write(this.token_string(node));
			
			String class_name = node.getClass().getSimpleName();
			this.writer.write("\t" + class_name.substring(6));
			this.writer.write("\t" + this.token_string(node.get_source()));
			
			CType data_type;
			if(node instanceof SymbolExpression) {
				data_type = ((SymbolExpression) node).get_data_type();
			}
			else {
				data_type = null;
			}
			this.writer.write("\t" + this.token_string(data_type));
			
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
			this.writer.write("\t" + this.token_string(content));
			
			this.writer.write("\t" + this.token_string(node.generate_code(true)));
			
			this.writer.write("\t[");
			for(SymbolNode child : node.get_children()) {
				this.writer.write(" " + this.token_string(child));
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
		for(SymbolNode node : this.symbolic_nodes) {
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
		this.symbolic_nodes.clear();
		Collection<SymInstanceTree> trees = new ArrayList<SymInstanceTree>(); 
		int number_of_trees = 0, number_of_paths = 0, number_of_nodes = 0, number_of_mutants;
		
		/* 2. generate symbolic instance trees for each available mutant */
		number_of_mutants = this.source_file.get_mutant_space().size();
		for(Mutant mutant : this.source_file.get_mutant_space().get_mutants()) {
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
		CirFunction root_function = this.
				source_file.get_cir_tree().get_function_call_graph().get_main_function();
		CDependGraph dependence_graph = CDependGraph.graph(CirCallContextInstanceGraph.
						graph(root_function, CirFunctionCallPathType.unique_path, -1));
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
		fwriter.write_all(input, output_directory, test_cases, max_distance);
	}
	
}
