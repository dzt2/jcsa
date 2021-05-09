package com.jcsa.jcmutest.project;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.sym2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.sym2mutant.cond.SymCondition;
import com.jcsa.jcmutest.mutant.sym2mutant.tree.SymInstanceContent;
import com.jcsa.jcmutest.mutant.sym2mutant.tree.SymInstanceTree;
import com.jcsa.jcmutest.mutant.sym2mutant.tree.SymInstanceTreeEdge;
import com.jcsa.jcmutest.mutant.sym2mutant.tree.SymInstanceTreeNode;
import com.jcsa.jcparse.base.BitSequence;
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
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPathFinder;
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
import com.jcsa.jcparse.test.state.CStateNode;
import com.jcsa.jcparse.test.state.CStatePath;

/**
 * 	It performs unified feature encoding and outputs for PyMuta to read and parse, including:
 * 	<br>
 * 	<code>
 * 	+-------------------------------------------------------------------------------------------+	<br>
 * 	|	static information																		|	<br>
 * 	|	---	xxx.cpp: the source code file with text for being read and review.					|	<br>
 * 	|	---	xxx.ast: the abstract syntactic tree nodes information to parse.					|	<br>
 * 	|	---	xxx.cir: the C-intermediate representation code for data flow analysis.				|	<br>
 * 	|	---	xxx.flw: the control flow graph in static form for dependence analysis.				|	<br>
 * 	|	---	xxx.ins: the instance of control flow graph for inter-procedural analysis.			|	<br>
 * 	|	---	xxx.dep: the C dependence graph based on C-intermediate representation code.		|	<br>
 * 	|	---	xxx.mut: the syntactic mutation information for analysis and review.				|	<br>
 * 	|	---	xxx.tst: the parameters used to formalize and execute each test case in project.	|	<br>
 * 	|	---	xxx.res: the test results obtained between every mutation and every test input.		|	<br>
 * 	+-------------------------------------------------------------------------------------------+	<br>
 * 	|	Dynamic information		{test suite is applied}											|	<br>
 * 	|	---	xxx.stc: the collection of test cases selected for mining algorithm to assume.		|	<br>
 * 	|	---	xxx.cov: the coverage matrix of which statement covered by which test case.			|	<br>
 * 	|	---	xxx.sit: the information to preserve status of each symbolic instance among tree.	|	<br>
 * 	|	---	xxx.sip: the information to preserve status of each symbolic instance in tree path.|	<br>
 * 	|	---	xxx.sym: the information of structural description for symbolic expression or node.	|	<br>
 * 	+-------------------------------------------------------------------------------------------+	<br>
 * 	</code>
 * 	<br>	
 * 	@author yukimula
 *
 */
public class MuTestProjectFeaturesWriter {
	
	/* definitions */
	/** it provides the data source for featuring **/
	private MuTestProjectCodeFile source;
	/** xxx as the name of output file **/
	private String file_name;
	/** directory where the output files are preserved **/
	private File output_directory;
	/** the file being written by now **/
	private File output_file;
	/** output stream to write feature data to file **/
	private FileWriter writer;
	/** it preserves the set of symbolic nodes used to define parameters in symbolic features **/
	private Set<SymbolNode> sym_nodes; 
	
	/* constructor */
	/**
	 * create a writer for generating feature data in project
	 * @param output_directory
	 * @param file_name
	 * @throws IllegalArgumentException
	 */
	public MuTestProjectFeaturesWriter(MuTestProjectCodeFile source, File output_directory) throws IllegalArgumentException {
		if(source == null)
			throw new IllegalArgumentException("Invalid source as null");
		else if(output_directory == null || !output_directory.isDirectory())
			throw new IllegalArgumentException("Not a directory: " + output_directory);
		else {
			this.source = source;
			this.file_name = source.get_name();
			int index = file_name.lastIndexOf('.');
			this.file_name = this.file_name.substring(0, index).strip();
			this.output_directory = output_directory;
			this.writer = null;
			this.output_file = null;
			this.sym_nodes = new HashSet<SymbolNode>();
		}
	}
	
	/* basic methods */
	/**
	 * close the output stream
	 * @throws IOException
	 */
	private void close() throws IOException {
		if(this.writer != null) {
			this.writer.close();
			this.writer = null;
			this.output_file = null;
		}
	}
	/**
	 * open a new output stream to xxx.extension in output directory
	 * @param extension
	 * @throws IOException
	 */
	private void open(String extension) throws IOException {
		this.close();
		this.output_file = new File(this.output_directory.getAbsolutePath() + "/" + this.file_name + extension);
		this.writer = new FileWriter(this.output_file);
		System.out.println("\t==> Write to file: " + this.output_file.getAbsolutePath()); /* WARN inform the users */
	}
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
	 * ast@int, cir@int, mut@int, exe@str@int, 
	 * ins@cotext@func@index, dep@context@func@index
	 * s@str [identifier, literal, cir_code]
	 * typ@str, sym@type@hashCode
	 * @param token
	 * @return
	 */
	private String token_string(Object token) throws Exception {
		if(token == null)
			return "n@null";
		else if(token instanceof Boolean)
			return "b@" + token.toString();
		else if(token instanceof Character)
			return "c@" + ((int) ((Character) token).charValue());
		else if(token instanceof Integer || token instanceof Long)
			return "i@" + token.toString();
		else if(token instanceof Float || token instanceof Double)
			return "f@" + token.toString();
		else if(token instanceof Complex)
			return "x@" + ((Complex) token).real() + "@" + ((Complex) token).imag();
		else if(token instanceof CConstant) 
			return this.token_string(((CConstant) token).get_object());
		else if(token instanceof CKeyword)
			return "key@" + token.toString();
		else if(token instanceof CPunctuator)
			return "pun@" + token.toString();
		else if(token instanceof COperator)
			return "opr@" + token.toString();
		else if(token instanceof AstNode)
			return "ast@" + ((AstNode) token).get_key();
		else if(token instanceof CirNode)
			return "cir@" + ((CirNode) token).get_node_id();
		else if(token instanceof Mutant)
			return "mut@" + ((Mutant) token).get_id();
		else if(token instanceof CirExecution)
			return "exe@" + ((CirExecution) token).get_graph().get_function().get_name() + "@" + ((CirExecution) token).get_id();
		else if(token instanceof String)
			return "s@" + this.normalize_string(token.toString());
		else if(token instanceof CType)
			return "typ@" + this.normalize_string(((CType) token).generate_code());
		else if(token instanceof CirInstanceNode) {
			CirInstanceNode node = (CirInstanceNode) token;
			return "ins@" + node.get_context().hashCode() + "@" + 
					node.get_execution().get_graph().get_function().get_name() + 
					"@" + node.get_execution().get_id();
		}
		else if(token instanceof CDependNode) {
			CirInstanceNode node = ((CDependNode) token).get_instance();
			return "dep@" + node.get_context().hashCode() + "@" + 
					node.get_execution().get_graph().get_function().get_name() + 
					"@" + node.get_execution().get_id();
		}
		else if(token instanceof SymbolNode) {
			SymbolNode node = (SymbolNode) token;
			return "sym@" + node.getClass().getSimpleName().substring(6) + "@" + node.hashCode();
		}
		else
			throw new IllegalArgumentException("Unsupport: " + token.getClass().getSimpleName());
	}
	/**
	 * @param test_cases
	 * @return True if test cases have been selected or False to evaluate under static analysis
	 */
	private boolean is_selected(Collection<TestInput> test_cases) {
		return (test_cases != null) && !(test_cases.isEmpty());
	}
	
	/* xxx.cpp */
	/**
	 * copy the code from ifile to xxx.cpp
	 * @throws Exception
	 */
	private void write_cpp() throws Exception {
		this.open(".c");
		FileReader reader = new FileReader(this.source.get_ifile());
		char[] buffer = new char[1024 * 1024 * 8]; int length;
		while((length = reader.read(buffer)) >= 0) 
			this.writer.write(buffer, 0, length);
		reader.close();
		this.close();
	}
	
	/* xxx.ast */
	/**
	 * ID class beg_index end_index data_type token [ child child ... child ]
	 * @param node
	 * @throws Exception
	 */
	private void write_ast(AstNode node) throws Exception {
		this.writer.write(this.token_string(node));
		
		String class_name = node.getClass().getSimpleName().strip();
		class_name = class_name.substring(3, class_name.length() - 4).strip();
		writer.write("\t" + class_name);
		
		int beg_index = node.get_location().get_bias();
		int end_index = beg_index + node.get_location().get_length();
		this.writer.write("\t" + beg_index + "\t" + end_index);
		
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
		AstTree ast_tree = this.source.get_ast_tree();
		for(int k = 0; k < ast_tree.number_of_nodes(); k++) {
			AstNode node = ast_tree.get_node(k);
			this.write_ast(node);
		}
		this.close();
	}
	
	/* xxx.cir */
	/**
	 * ID class ast_id data_type token [ child child ... child ] code
	 * @param node
	 * @throws Exception
	 */
	private void write_cir(CirNode node) throws Exception {
		this.writer.write(this.token_string(node));
		
		String class_name = node.getClass().getSimpleName().strip();
		class_name = class_name.substring(3, class_name.length() - 4).strip();
		writer.write("\t" + class_name);
		
		writer.write("\t" + this.token_string(node.get_ast_source()));
		
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
		for(CirNode node : this.source.get_cir_tree().get_nodes()) {
			this.write_cir(node);
		}
		this.close();
	}
	
	/* xxx.flw */
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
		for(CirFunction function : this.source.get_cir_tree().get_function_call_graph().get_functions()) {
			this.write_cir_function(function);
			this.writer.write("\n");
		}
		this.close();
	}
	
	/* xxx.ins */
	/**
	 * #node ID context execution
	 * @param node
	 * @throws Exception
	 */
	private void write_instance_node(CirInstanceNode node) throws Exception {
		this.writer.write("#node");
		this.writer.write("\t" + this.token_string(node));
		this.writer.write("\t" + node.get_context().hashCode());
		this.writer.write("\t" + this.token_string(node.get_execution()));
		this.writer.write("\n");
	}
	/**
	 * #edge type source_ID target_ID
	 * @param edge
	 * @throws Exception
	 */
	private void write_instance_edge(CirInstanceEdge edge) throws Exception {
		this.writer.write("#edge");
		this.writer.write("\t" + edge.get_type().toString());
		this.writer.write("\t" + this.token_string(edge.get_source()));
		this.writer.write("\t" + this.token_string(edge.get_target()));
		this.writer.write("\n");
	}
	/**
	 * #node ID context execution
	 * #edge type source_ID target_ID
	 * @param graph
	 * @throws Exception
	 */
	private void write_ins(CirInstanceGraph graph) throws Exception {
		this.open(".ins");
		for(Object context : graph.get_contexts()) {
			for(CirInstance instance : graph.get_instances(context)) {
				if(instance instanceof CirInstanceNode) {
					CirInstanceNode node = (CirInstanceNode) instance;
					this.write_instance_node(node);
					for(CirInstanceEdge edge : node.get_ou_edges()) {
						this.write_instance_edge(edge);
					}
				}
			}
		}
		this.close();
	}
	
	/* xxx.ins */
	/**
	 * #node dependence_ID instance_ID
	 * @param node
	 * @throws Exception
	 */
	private void write_dependence_node(CDependNode node) throws Exception {
		this.writer.write("#node");
		this.writer.write("\t" + this.token_string(node));
		this.writer.write("\t" + this.token_string(node.get_instance()));
		this.writer.write("\n");
	}
	/**
	 * #edge [predicate_depend] source_instance_ID target_instance_ID cir_condition bool
	 * #edge [stmt_call_depend] source_instance_ID target_instance_ID call_stmt entr_stmt
	 * #edge [stmt_exit_depend] source_instance_ID target_instance_ID exit_stmt wait_stmt
	 * 
	 * #edge [use_defin_depend] source_instance_ID target_instance_ID use_expr define_expr
	 * #edge [param_arg_depend] source_instance_ID target_instance_ID use_expr define_expr
	 * #edge [wait_retr_depend] source_instance_ID target_instance_ID use_expr define_expr
	 * 
	 * @param edge
	 * @throws Exception
	 */
	private void write_dependence_edge(CDependEdge edge) throws Exception {
		this.writer.write("#edge");
		this.writer.write("\t" + edge.get_type().toString());
		this.writer.write("\t" + this.token_string(edge.get_source()));
		this.writer.write("\t" + this.token_string(edge.get_target()));
		
		switch(edge.get_type()) {
		case predicate_depend:
		{
			CDependPredicate predicate = (CDependPredicate) edge.get_element();
			this.writer.write("\t" + this.token_string(predicate.get_condition()));
			this.writer.write("\t" + this.token_string(predicate.get_predicate_value()));
			break;
		}
		case stmt_call_depend:
		{
			CirExecution call_execution = edge.get_target().get_execution();
			CirExecutionFlow call_flow = call_execution.get_ou_flow(0);
			this.writer.write("\t" + this.token_string(call_flow.get_source()));
			this.writer.write("\t" + this.token_string(call_flow.get_target()));
			break;
		}
		case stmt_exit_depend:
		{
			CirExecution wait_execution = edge.get_target().get_execution();
			CirExecutionFlow retr_flow = wait_execution.get_in_flow(0);
			this.writer.write("\t" + this.token_string(retr_flow.get_source()));
			this.writer.write("\t" + this.token_string(retr_flow.get_target()));
			break;
		}
		case use_defin_depend:
		case param_arg_depend:
		case wait_retr_depend:
		{
			CDependReference reference = (CDependReference) edge.get_element();
			this.writer.write("\t" + this.token_string(reference.get_use()));
			this.writer.write("\t" + this.token_string(reference.get_def()));
			break;
		}
		default: throw new IllegalArgumentException("Unsupport: " + edge.get_type());
		}
		
		this.writer.write("\n");
	}
	/**
	 * #node dependence_ID instance_ID
	 * #edge [predicate_depend] source_instance_ID target_instance_ID cir_condition bool
	 * #edge [stmt_call_depend] source_instance_ID target_instance_ID call_stmt entr_stmt
	 * #edge [stmt_exit_depend] source_instance_ID target_instance_ID exit_stmt wait_stmt
	 * #edge [use_defin_depend] source_instance_ID target_instance_ID use_expr define_expr
	 * #edge [param_arg_depend] source_instance_ID target_instance_ID use_expr define_expr
	 * #edge [wait_retr_depend] source_instance_ID target_instance_ID use_expr define_expr
	 * @param graph
	 * @throws Exception
	 */
	private void write_dep(CDependGraph graph) throws Exception {
		this.open(".dep");
		for(CDependNode node : graph.get_nodes()) {
			this.write_dependence_node(node);
			for(CDependEdge edge : node.get_ou_edges()) {
				this.write_dependence_edge(edge);
			}
		}
		this.close();
	}
	
	/* xxx.tst */
	/**
	 * ID parameter
	 * @param test
	 * @throws Exception
	 */
	private void write_tst(TestInput test) throws Exception {
		this.writer.write(test.get_id() + "\t" + this.token_string(test.get_parameter()) + "\n");
	}
	/**
	 * ID tst@parameter
	 * @throws Exception
	 */
	private void write_tst() throws Exception {
		this.open(".tst");
		MuTestProjectTestSpace tspace = this.source.get_code_space().get_project().get_test_space();
		for(TestInput test : tspace.get_test_space().get_inputs()) {
			this.write_tst(test);
		}
		this.close();
	}
	
	/* xxx.mut */
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
		for(Mutant mutant : this.source.get_mutant_space().get_mutants()) {
			this.write_mut(mutant);
		}
		this.close();
	}
	
	/* xxx.res */
	/**
	 * MID bit_string
	 * @param result
	 * @throws Exception
	 */
	private void write_res(MuTestProjectTestResult result) throws Exception {
		this.writer.write(result.get_mutant().get_id() + "\t" + result.get_kill_set().toString() + "\n");
	}
	/**
	 * MID bit_string
	 * @throws Exception
	 */
	private void write_res() throws Exception {
		this.open(".res");
		MuTestProjectTestSpace tspace = this.source.get_code_space().get_project().get_test_space();
		for(Mutant mutant : this.source.get_mutant_space().get_mutants()) {
			MuTestProjectTestResult result = tspace.get_test_result(mutant);
			if(result != null)
				this.write_res(result);
		}
		this.close();
	}
	
	/* xxx.stc */
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
	
	/* xxx.cov */
	/**
	 * @return initialize the mapping from execution point to the bit-string representing
	 * 		   of which test cover the corresponding execution node in control flow graph
	 * @throws Exception
	 */
	private Map<CirExecution, BitSequence> new_coverage_matrix() throws Exception {
		Map<CirExecution, BitSequence> cmat = new HashMap<CirExecution, BitSequence>();
		int test_number = this.source.get_code_space().get_project().get_test_space().number_of_test_inputs();
		for(CirFunction function : this.source.get_cir_tree().get_function_call_graph().get_functions()) {
			if(!function.get_name().equals("#init")) {
				for(CirExecution execution : function.get_flow_graph().get_executions()) {
					cmat.put(execution, new BitSequence(test_number));
				}
			}
		}
		return cmat;
	}
	/**
	 * update the matrix by loading STRP or ETRP or BTRP using their seeded statement
	 * @param cmat
	 * @param mutant
	 * @throws Exception
	 */
	private void set_coverage_matrix(Map<CirExecution, BitSequence> cmat, Mutant mutant) throws Exception {
		if(mutant.has_cir_mutations()) {
			MuTestProjectTestSpace tspace = this.source.get_code_space().get_project().get_test_space();
			MuTestProjectTestResult result = tspace.get_test_result(mutant);
			if(result != null) {
				/*
				System.out.println("\t\t~~> Update Coverage Matrics for Mutant#" + 
						mutant.get_id() + " among " + mutant.get_space().size() + " mutations.");
				*/
				BitSequence killings = result.get_kill_set();
				for(CirMutation cir_mutation : mutant.get_cir_mutations()) {
					BitSequence coverage = cmat.get(cir_mutation.get_execution());
					for(int k = 0; k < killings.length(); k++) {
						if(killings.get(k)) {
							coverage.set(k, BitSequence.BIT1);
						}
					}
				}
			}
		}
	}
	/**
	 * generate extension set from each execution node covered
	 * @param cmat
	 * @throws Exception
	 */
	private void ext_coverage_matrix(Map<CirExecution, BitSequence> cmat) throws Exception {
		for(CirExecution execution : cmat.keySet()) {
			CirExecutionPath path = new CirExecutionPath(execution);
			CirExecutionPathFinder.finder.db_extend(path);
			CirExecutionPathFinder.finder.df_extend(path);
			Set<CirExecution> extensions = new HashSet<CirExecution>();
			for(CirExecutionEdge edge : path.get_edges()) {
				extensions.add(edge.get_source());
				extensions.add(edge.get_target());
			}
			
			BitSequence source = cmat.get(execution);
			for(CirExecution extension : extensions) {
				BitSequence target = cmat.get(extension);
				for(int k = 0; k < source.length(); k++) {
					if(source.get(k)) {
						target.set(k, BitSequence.BIT1);
					}
				}
			}
		}
	}
	/**
	 * [execution bits \n]+
	 * @param cmat
	 * @throws Exception
	 */
	private void put_coverage_matrix(Map<CirExecution, BitSequence> cmat) throws Exception {
		for(CirExecution execution : cmat.keySet()) {
			BitSequence coverage = cmat.get(execution);
			this.writer.write(this.token_string(execution) + "\t" + coverage.toString() + "\n");
		}
	}
	/**
	 * update the matrix by loading instrumental path
	 * @param cmat
	 * @param test
	 * @throws Exception
	 */
	private void set_coverage_matrix(Map<CirExecution, BitSequence> cmat, TestInput test) throws Exception {
		MuTestProjectTestSpace tspace = this.source.get_code_space().get_project().get_test_space();
		CStatePath test_path = tspace.load_instrumental_path(this.source.get_sizeof_template(), 
									this.source.get_ast_tree(), this.source.get_cir_tree(), test);
		if(test_path != null) {
			System.out.println("\t\t~~> Update Coverage Matrics for Test#" + test.get_id() + 
								" among " + tspace.number_of_test_inputs() + " test cases.");
			for(CStateNode state_node : test_path.get_nodes()) {
				cmat.get(state_node.get_execution()).set(test.get_id(), BitSequence.BIT1);
			}
		}
	}
	/**
	 * update the coverage metrics using results of mutant against a test suite
	 * @param cmat
	 * @param mutant
	 * @param test_cases
	 * @throws Exception
	 */
	private void set_coverage_matrix(Map<CirExecution, BitSequence> cmat, Mutant mutant, Collection<TestInput> test_cases) throws Exception {
		if(mutant.has_cir_mutations()) {
			MuTestProjectTestSpace tspace = this.source.get_code_space().get_project().get_test_space();
			MuTestProjectTestResult result = tspace.get_test_result(mutant);
			if(result != null) {
				System.out.println("\t\t~~> Update Coverage Matrics for Mutant#" + 
						mutant.get_id() + " among " + mutant.get_space().size() + " mutations.");
				BitSequence killings = result.get_kill_set();
				for(CirMutation cir_mutation : mutant.get_cir_mutations()) {
					BitSequence coverage = cmat.get(cir_mutation.get_execution());
					for(TestInput test_case : test_cases) {
						if(killings.get(test_case.get_id())) {
							coverage.set(test_case.get_id(), BitSequence.BIT1);
						}
					}
				}
			}
		}
	}
	/**
	 * xxx.cov
	 * @param test_cases null to count on mutation only
	 * @throws Exception
	 */
	private void write_cov(Collection<TestInput> test_cases) throws Exception {
		Map<CirExecution, BitSequence> cmat = this.new_coverage_matrix();
		
		if(this.is_selected(test_cases)) {
			for(TestInput test_case : test_cases) 
				this.set_coverage_matrix(cmat, test_case);
			for(Mutant mutant : this.source.get_mutant_space().get_mutants()) 
				this.set_coverage_matrix(cmat, mutant, test_cases);
		}
		else {
			for(Mutant mutant : this.source.get_mutant_space().get_mutants()) 
				this.set_coverage_matrix(cmat, mutant);
		}
		
		this.ext_coverage_matrix(cmat);
		
		this.open(".cov");
		this.put_coverage_matrix(cmat);
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
		
		if(parameter != null) { this.sym_nodes.add(parameter); }
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
	private void write_sym_instance_tree(SymInstanceTree tree) throws Exception {
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
	}
	/**
	 * write the features in set of trees in form of node and edges rather than path
	 * @param trees
	 * @throws Exception
	 */
	private int write_sym_instance_trees(MuTestProjectTestSpace tspace, 
			Collection<SymInstanceTree> trees, Collection<TestInput> test_cases) throws Exception {
		/* evaluate each tree on specified contexts (statically or dynamically) */
		if(!this.is_selected(test_cases)) {
			for(SymInstanceTree tree : trees) tree.evaluate();
		}
		else {
			for(TestInput test_case : test_cases) {
				CStatePath state_path = tspace.load_instrumental_path(this.source.get_sizeof_template(), 
									this.source.get_ast_tree(), this.source.get_cir_tree(), test_case);
				if(state_path != null) { for(SymInstanceTree tree : trees) tree.evaluate(state_path); }
			}
		}
		
		/* output feature information */	
		for(SymInstanceTree tree : trees) { this.write_sym_instance_tree(tree); }
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
	 * write the features in set of trees in form of node and edges rather than path
	 * @param tspace
	 * @param trees
	 * @param test_cases
	 * @throws Exception
	 */
	private int write_sym_instance_paths(MuTestProjectTestSpace tspace, 
			Collection<SymInstanceTree> trees, Collection<TestInput> test_cases) throws Exception {
		/* evaluate each tree on specified contexts (statically or dynamically) */
		if(!this.is_selected(test_cases)) {
			for(SymInstanceTree tree : trees) tree.evaluate();
		}
		else {
			for(TestInput test_case : test_cases) {
				CStatePath state_path = tspace.load_instrumental_path(this.source.get_sizeof_template(), 
									this.source.get_ast_tree(), this.source.get_cir_tree(), test_case);
				if(state_path != null) { for(SymInstanceTree tree : trees) tree.evaluate(state_path); }
			}
		}
		
		/* output feature information */
		int counter = 0;
		for(SymInstanceTree tree : trees) { counter += this.write_sym_instance_path(tree); }
		return counter;
	}
	
	/* xxx.sit xxx.sip xxx.sym */
	/**
	 * generate instrumental files for given test cases in the source project by dynamic testing
	 * @param test_cases
	 * @throws Exception
	 */
	private void generate_instrument_files(Collection<TestInput> test_cases) throws Exception {
		if(this.is_selected(test_cases))
			this.source.get_code_space().get_project().execute_instrumental(test_cases);
	}
	/** the size of buffer to preserve symbolic trees generated and evaluated in feature writing **/
	private static final int SYM_TREE_BUFFER_SIZE = 128;
	/**
	 * xxx.sit
	 * @param dependence_graph used to generate symbolic instance tree
	 * @param max_distance maximal error propagation distance from infection point
	 * @param test_cases the set of test cases selected as context for analysis
	 * @throws Exception
	 */
	private int write_sit(CDependGraph dependence_graph, int max_distance, Collection<TestInput> test_cases) throws Exception {
		this.open(".sit");
		
		/* 1. declarations */
		Collection<SymInstanceTree> trees = new ArrayList<SymInstanceTree>(); int counters = 0;
		MuTestProjectTestSpace tspace = this.source.get_code_space().get_project().get_test_space();
		
		/* 2. generate all the trees and evaluate and write features on fly */
		for(Mutant mutant : this.source.get_mutant_space().get_mutants()) {
			if(mutant.has_cir_mutations() && tspace.get_test_result(mutant) != null) {
				trees.add(SymInstanceTree.new_tree(mutant, max_distance, dependence_graph));
			}
			
			if(trees.size() >= SYM_TREE_BUFFER_SIZE) {
				counters += this.write_sym_instance_trees(tspace, trees, test_cases);
				trees.clear();
			}
		}
		if(!trees.isEmpty()) { counters += this.write_sym_instance_trees(tspace, trees, test_cases); }
		
		this.close();
		return counters;
	}
	/**
	 * xxx.sip
	 * @param dependence_graph used to generate symbolic instance tree
	 * @param max_distance maximal error propagation distance from infection point
	 * @param test_cases the set of test cases selected as context for analysis
	 * @throws Exception
	 */
	private int write_sip(CDependGraph dependence_graph, int max_distance, Collection<TestInput> test_cases) throws Exception {
		this.open(".sip");
		
		/* 1. declarations */
		Collection<SymInstanceTree> trees = new ArrayList<SymInstanceTree>(); int counters = 0;
		MuTestProjectTestSpace tspace = this.source.get_code_space().get_project().get_test_space();
		
		/* 2. generate all the trees and evaluate and write features on fly */
		for(Mutant mutant : this.source.get_mutant_space().get_mutants()) {
			if(mutant.has_cir_mutations() && tspace.get_test_result(mutant) != null) {
				trees.add(SymInstanceTree.new_tree(mutant, max_distance, dependence_graph));
			}
			
			if(trees.size() >= SYM_TREE_BUFFER_SIZE) {
				counters += this.write_sym_instance_paths(tspace, trees, test_cases);
				trees.clear();
			}
		}
		if(!trees.isEmpty()) { counters += this.write_sym_instance_paths(tspace, trees, test_cases); }
		
		this.close();
		return counters;
	}
	/**
	 * xxx.sip and xxx.sym
	 * @param dependence_graph used to generate symbolic instance tree
	 * @param max_distance maximal error propagation distance from infection point
	 * @param test_cases the set of test cases selected as context for analysis
	 * @throws Exception
	 */
	private void write_sit_sip_sym(CDependGraph dependence_graph, int 
			max_distance, Collection<TestInput> test_cases) throws Exception {
		int number_of_trees = this.write_sit(dependence_graph, max_distance, test_cases);
		int number_of_paths = this.write_sip(dependence_graph, max_distance, test_cases);
		System.out.println("\t\t\tWrite " + number_of_trees + " mutants with " + number_of_paths + 
				" symbolic paths using " + this.sym_nodes.size() + " expression nodes.");
		this.write_sym_nodes(); this.sym_nodes.clear();
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
	 * write all the symbolic expressions in the buffer
	 * @throws Exception
	 */
	private void write_sym_nodes() throws Exception {
		this.open(".sym");
		Set<String> records = new HashSet<String>();
		for(SymbolNode node : this.sym_nodes) {
			this.write_sym_node(node, records);
		}
		this.close();
	}
	
	/* API interface for utilization */
	/**
	 * @param test_cases the collection of test cases selected in context of program analysis
	 * @param max_distance maximal error propagation distance for constructing symbolic trees
	 * @throws Exception
	 */
	public void write_features(Collection<TestInput> test_cases, int max_distance) throws Exception {
		/* CODE */
		this.write_cpp();
		this.write_ast();
		this.write_cir();
		
		/* FLOW */
		this.write_flw();
		CirFunction root_function = source.get_cir_tree().get_function_call_graph().get_main_function();
		CDependGraph dependence_graph = CDependGraph.graph(CirCallContextInstanceGraph.
						graph(root_function, CirFunctionCallPathType.unique_path, -1));
		this.write_ins(dependence_graph.get_program_graph());
		this.write_dep(dependence_graph);
		
		/* PROJ */
		this.write_mut();
		this.write_tst();
		this.write_res();
		
		/* TEST */
		this.generate_instrument_files(test_cases);
		this.write_stc(test_cases);
		this.write_cov(test_cases);
		this.write_sit_sip_sym(dependence_graph, max_distance, test_cases);
	}
	
}
