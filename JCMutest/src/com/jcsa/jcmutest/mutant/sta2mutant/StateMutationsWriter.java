package com.jcsa.jcmutest.mutant.sta2mutant;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirConditionState;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.MuTestProjectTestResult;
import com.jcsa.jcmutest.project.MuTestProjectTestSpace;
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
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
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
 * It generates the feature information from mutation and its state versions to
 * the specified output files in a specified directory.
 * 
 * @author yukimula
 *
 */
public class StateMutationsWriter {
	
	/* attributes */
	/**	the source file defined in mutation testing project	**/
	private	MuTestProjectCodeFile							source_cfile;
	/**	the directory in which the output files are created	**/
	private	File											ou_directory;
	/**	the file writer to generate feature information for **/
	private FileWriter 										cfile_writer;	
	/**	the set of symbolic nodes to be printed to the file	**/
	private Set<SymbolNode>									symbol_nodes;
	/** the mapping from mutant or state to states subsumed **/
	private Map<Object, Set<CirAbstractState>>				subsume_maps;
	/** the mapping from state node in MSG hierarchy to extended states **/
	private Map<CirAbstractState, Set<CirAbstractState>>	extended_map;
	/**	the maximal distance from mutations to other states	**/
	private int												max_distance;
	
	/* constructor & singleton mode */
	/**
	 * private constructor for creating singleton mode
	 */
	private StateMutationsWriter() {
		this.source_cfile = null;
		this.ou_directory = null;
		this.cfile_writer = null;
		this.symbol_nodes = new HashSet<SymbolNode>();
		this.subsume_maps = new HashMap<Object, Set<CirAbstractState>>();
		this.extended_map = new HashMap<CirAbstractState, Set<CirAbstractState>>();
		this.max_distance = 0;
	}
	/** the singleton instance of the feature writer for writing features **/
	private static final StateMutationsWriter fwriter = new StateMutationsWriter();
	
	/* input-output based operation */
	/**
	 * It closes the writer and reset the feature output buffer.
	 * @throws Exception
	 */
	private void close() throws Exception {
		if(this.cfile_writer != null) {
			this.cfile_writer.close();
			this.cfile_writer = null;
		}
	}
	/**
	 * It opens the file writer to the ou_directory/name.postfx
	 * @param postfix
	 * @throws Exception
	 */
	private void open(String postfix) throws Exception {
		/* 1. determine the output file absolute file in directory */
		String name = this.source_cfile.get_name();
		int index = name.lastIndexOf('.');
		if(index > 0) { name = name.substring(0, index).strip(); }
		String oufile = this.ou_directory.getAbsolutePath() + "/" + name + postfix;
		
		/* 2. reset the output stream writer and print to consoles */
		this.close(); System.out.println("\t--> Write to " + oufile);
		this.cfile_writer = new FileWriter(new File(oufile));
	}
	/**
	 * It resets the inputs cfile and out-directory for writing
	 * @param source_cfile	the mutation source code file for writing features
	 * @param ou_directory	the directory in which the ouput files are written
	 * @param max_distance	the maximal distance of subsumption from mutations
	 * @throws Exception
	 */
	private void reset(MuTestProjectCodeFile source_cfile, 
			File ou_directory, int max_distance) throws Exception {
		if(source_cfile == null) {
			throw new IllegalArgumentException("Invalid source_cfile: null");
		}
		else if(ou_directory == null) {
			throw new IllegalArgumentException("Invalid ou_directory: null");
		}
		else if(ou_directory.exists() && !ou_directory.isDirectory()) {
			throw new IllegalArgumentException("Undefined: " + ou_directory);
		}
		else if(max_distance <= 0) {
			throw new IllegalArgumentException("Invalid max_distance: " + max_distance);
		}
		else {
			if(!ou_directory.exists()) {
				FileOperations.mkdir(ou_directory);
			}
			this.source_cfile = source_cfile;
			this.ou_directory = ou_directory;
			this.max_distance = max_distance;
			this.symbol_nodes.clear();
			this.subsume_maps.clear();
			this.extended_map.clear();
			this.close();
		}
	}
	
	/* basic token endocing methods */
	/**
	 * translate special characters to specified format
	 * @param text
	 * @return {@ --> \a; space --> \s; $ --> \p;}
	 * @throws Exception
	 */
	private String normalize_cstring(String text) throws Exception {
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
	 * 
	 * @param token
	 * @return	BASIC: 	n@null, b@bool, c@char, i@int, f@real, x@r@i, s@txt;
	 * @throws Exception
	 */
	private String encode_base_token(Object token) throws Exception {
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
			return "x@" + ((Complex) token).get_x() + "@" + ((Complex) token).get_y();
		}
		else if(token instanceof String) {
			return "s@" + this.normalize_cstring(token.toString());
		}
		else if(token instanceof CConstant) {
			return this.encode_base_token(((CConstant) token).get_object());
		}
		else {
			return null;
		}
	}
	/**
	 * @param token
	 * @return	ENUMS:	key@keyword, opr@operator, pun@punctuator, typ@type;
	 * 			OBJEC:	ast@int, cir@int, exe@txt@int, ins@txt@int@int;
	 * @throws Exception
	 */
	private String encode_code_token(Object token) throws Exception {
		if(token instanceof CKeyword) {
			return "key@" + token.toString();
		}
		else if(token instanceof COperator) {
			return "opr@" + token.toString();
		}
		else if(token instanceof CPunctuator) {
			return "pun@" + token.toString();
		}
		else if(token instanceof CType) {
			return "typ@" + this.normalize_cstring(((CType) token).generate_code());
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
		else {
			return null;
		}
	}
	/**
	 * @param token
	 * @return	FEATU:	sym@txt@int, mut@int, tst@int;
	 * 			WORDS:	execution$store_class$store_unit$value_class$loperand$roperand
	 * @throws Exception
	 */
	private String encode_test_token(Object token) throws Exception {
		if(token instanceof Mutant) {
			return "mut@" + ((Mutant) token).get_id();
		}
		else if(token instanceof TestInput) {
			return "tst@" + ((TestInput) token).get_id();
		}
		else if(token instanceof SymbolNode) {
			if(!this.symbol_nodes.contains((SymbolNode) token)) this.symbol_nodes.add((SymbolNode) token); 
			String key = "sym@" + token.getClass().getSimpleName().substring(6).trim() + "@" + token.hashCode();
			return key;
		}
		else if(token instanceof CirAbstractState) {
			CirAbstractState state = (CirAbstractState) token;
			String exec_token = this.encode_token(state.get_execution());
			String store_type = state.get_store_type().toString();
			String store_unit = this.encode_code_token(state.get_clocation());
			String value_type = state.get_operator().toString();
			String lparameter = this.encode_token(state.get_loperand());
			String rparameter = this.encode_token(state.get_roperand());
			return String.format("%s$%s$%s$%s$%s$%s", exec_token, store_type, 
							store_unit, value_type, lparameter, rparameter);
		}
		else {
			return null;
		}
	}
	/**
	 * @param token
	 * @return
	 * @throws Exception
	 */
	private String encode_token(Object token) throws Exception {
		String text;
		text = this.encode_test_token(token);
		if(text == null) {
			text = this.encode_code_token(token);
			if(text == null) {
				text = this.encode_base_token(token);
			}
		}
		if(text != null) { return text; }
		throw new IllegalArgumentException("Unsupport: " + token);
	}
	
	/* source code feature information */
	/**
	 * xxx.c
	 * @throws Exception
	 */
	private void write_cpp() throws Exception {
		this.open(".c");
		FileReader reader = new FileReader(this.source_cfile.get_ifile());
		char[] buffer = new char[1024 * 1024 * 8]; int length;
		while((length = reader.read(buffer)) >= 0)
			this.cfile_writer.write(buffer, 0, length);
		reader.close();
		this.close();
	}
	/**
	 * ast@key class_name beg_index end_index data_type content [ {ast@key}* ]
	 * @param node	the abstract syntactic node of which information is output
	 * @throws Exception
	 */
	private void write_ast_node(AstNode node) throws Exception {
		this.cfile_writer.write(this.encode_token(node));

		String class_name = node.getClass().getSimpleName();
		class_name = class_name.substring(3, class_name.length() - 4).trim();
		int beg_index = node.get_location().get_bias();
		int end_index = beg_index + node.get_location().get_length();
		this.cfile_writer.write("\t" + class_name + "\t" + beg_index + "\t" + end_index);

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
		this.cfile_writer.write("\t" + this.encode_token(data_type));

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
		this.cfile_writer.write("\t" + this.encode_token(content));

		this.cfile_writer.write("\t[");
		for(int k = 0; k < node.number_of_children(); k++) {
			this.cfile_writer.write(" " + this.encode_token(node.get_child(k)));
		}
		this.cfile_writer.write(" ]");

		this.cfile_writer.write("\n");
	}
	/**
	 * ast@key class_name beg_index end_index data_type content [ {ast@key}* ] \n
	 * @throws Exception
	 */
	private void write_ast() throws Exception {
		this.open(".ast");
		
		AstTree ast_tree = this.source_cfile.get_ast_tree();
		for(int k = 0; k < ast_tree.number_of_nodes(); k++) {
			this.write_ast_node(ast_tree.get_node(k));
		}
		
		this.close();
	}
	/**
	 * cir@key class_name ast_source data_type content [ {cir@key}* ] code
	 * @param node	the C-intermediate representative element being written
	 * @throws Exception
	 */
	private void write_cir_node(CirNode node) throws Exception {
		this.cfile_writer.write(this.encode_token(node));

		String class_name = node.getClass().getSimpleName();
		class_name = class_name.substring(3, class_name.length() - 4).trim();
		this.cfile_writer.write("\t" + class_name + "\t" + this.encode_token(node.get_ast_source()));

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
		this.cfile_writer.write("\t" + this.encode_token(data_type));

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
		this.cfile_writer.write("\t" + this.encode_token(content));

		this.cfile_writer.write("\t[");
		for(CirNode child : node.get_children()) {
			this.cfile_writer.write(" " + this.encode_token(child));
		}
		this.cfile_writer.write(" ]");

		String code = null;
		if(!(node instanceof CirFunctionDefinition
			|| node instanceof CirTransitionUnit
			|| node instanceof CirFunctionBody)) {
			code = node.generate_code(true);
		}
		this.cfile_writer.write("\t" + this.encode_token(code));

		this.cfile_writer.write("\n");
	}
	/**
	 * cir@key class_name ast_source data_type content [ {cir@key}* ] code \n
	 * @throws Exception
	 */
	private void write_cir() throws Exception {
		this.open(".cir");
		for(CirNode node : this.source_cfile.get_cir_tree().get_nodes()) {
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
		this.cfile_writer.write("\t" + "[edge]");
		this.cfile_writer.write("\t" + flow.get_type());
		this.cfile_writer.write("\t" + this.encode_token(flow.get_source()));
		this.cfile_writer.write("\t" + this.encode_token(flow.get_target()));
		this.cfile_writer.write("\n");
	}
	/**
	 * [call] call_exec wait_exec
	 * @param call
	 * @throws Exception
	 */
	private void write_execution_call(CirFunctionCall call) throws Exception {
		this.cfile_writer.write("\t" + "[call]");
		this.cfile_writer.write("\t" + this.encode_token(call.get_call_execution()));
		this.cfile_writer.write("\t" + this.encode_token(call.get_wait_execution()));
		this.cfile_writer.write("\n");
	}
	/**
	 * [node] ID cir_statement
	 * @param execution
	 * @throws Exception
	 */
	private void write_execution_node(CirExecution execution) throws Exception {
		this.cfile_writer.write("\t" + "[node]");
		this.cfile_writer.write("\t" + this.encode_token(execution));
		this.cfile_writer.write("\t" + this.encode_token(execution.get_statement()));
		this.cfile_writer.write("\n");
	}
	/**
	 * 	[beg]	name
	 * 	[node] 	ID cir_statement
	 * 	[edge] 	type source target
	 * 	[call] 	call_exec wait_exec
	 * 	[end]	name
	 * @param function
	 * @throws Exception
	 */
	private void write_cir_function(CirFunction function) throws Exception {
		this.cfile_writer.write("[beg]\t" + function.get_name() + "\n");
		for(CirExecution execution : function.get_flow_graph().get_executions()) {
			this.write_execution_node(execution);
			for(CirExecutionFlow flow : execution.get_ou_flows()) {
				this.write_execution_flow(flow);
			}
		}
		for(CirFunctionCall call : function.get_ou_calls()) {
			this.write_execution_call(call);
		}
		this.cfile_writer.write("[end]\t" + function.get_name() + "\n");
	}
	/**
	 * 	[beg]	name
	 * 	[node] 	ID cir_statement
	 * 	[edge] 	type source target
	 * 	[call] 	call_exec wait_exec
	 * 	[end]	name
	 * 	\n
	 * @throws Exception
	 */
	private void write_flw() throws Exception {
		this.open(".flw");
		for(CirFunction function : this.source_cfile.get_cir_tree().
						get_function_call_graph().get_functions()) {
			this.write_cir_function(function);
			this.cfile_writer.write("\n");
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
	
	/* dependence model information */
	/**
	 * [edge] type source target
	 * @param edge
	 * @throws Exception
	 */
	private void write_instance_edge(CirInstanceEdge edge) throws Exception {
		this.cfile_writer.write("[edge]\t" + edge.get_type());
		this.cfile_writer.write("\t" + this.encode_token(edge.get_source()));
		this.cfile_writer.write("\t" + this.encode_token(edge.get_target()));
		this.cfile_writer.write("\n");
	}
	/**
	 * [node] ins@txt@int@int execution context
	 * @param node
	 * @throws Exception
	 */
	private void write_instance_node(CirInstanceNode node) throws Exception {
		this.cfile_writer.write("[node]");
		this.cfile_writer.write("\t" + this.encode_token(node));
		this.cfile_writer.write("\t" + this.encode_token(node.get_execution()));
		int context = 0;
		if(node.get_context() != null) context = node.get_context().hashCode();
		this.cfile_writer.write("\t" + context);
		this.cfile_writer.write("\n");
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
		this.cfile_writer.write("[edge]");
		this.cfile_writer.write("\t" + edge.get_type());
		this.cfile_writer.write("\t" + this.encode_token(edge.get_source().get_instance()));
		this.cfile_writer.write("\t" + this.encode_token(edge.get_target().get_instance()));

		Object element = edge.get_element();
		if(element instanceof CDependPredicate) {
			CDependPredicate content = (CDependPredicate) element;
			this.cfile_writer.write("\t" + this.encode_token(content.get_condition()));
			this.cfile_writer.write("\t" + this.encode_token(content.get_predicate_value()));
		}
		else if(element instanceof CDependReference) {
			CDependReference content = (CDependReference) element;
			this.cfile_writer.write("\t" + this.encode_token(content.get_def()));
			this.cfile_writer.write("\t" + this.encode_token(content.get_use()));
		}
		else {
			this.cfile_writer.write("\t" + this.encode_token(null));
			this.cfile_writer.write("\t" + this.encode_token(null));
		}

		this.cfile_writer.write("\n");
	}
	/**
	 * [node] instance
	 * @param node
	 * @throws Exception
	 */
	private void write_dependence_node(CDependNode node) throws Exception {
		this.cfile_writer.write("[node]\t" + this.encode_token(node.get_instance()) + "\n");
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
		this.cfile_writer.write(this.encode_token(test) + "\t" + this.encode_token(test.get_parameter()) + "\n");
	}
	/**
	 * ID tst@parameter
	 * @throws Exception
	 */
	private void write_tst() throws Exception {
		this.open(".tst");
		MuTestProjectTestSpace tspace = this.source_cfile.
					get_code_space().get_project().get_test_space();
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
		this.cfile_writer.write("" + this.encode_token(mutant));
		this.cfile_writer.write("\t" + mutant.get_mutation().get_class());
		this.cfile_writer.write("\t" + mutant.get_mutation().get_operator());
		this.cfile_writer.write("\t" + this.encode_token(mutant.get_mutation().get_location()));
		this.cfile_writer.write("\t" + this.encode_token(mutant.get_mutation().get_parameter()));

		this.cfile_writer.write("\t" + this.encode_token(mutant.get_coverage_mutant()));
		this.cfile_writer.write("\t" + this.encode_token(mutant.get_weak_mutant()));
		this.cfile_writer.write("\t" + this.encode_token(mutant.get_strong_mutant()));

		this.cfile_writer.write("\n");
	}
	/**
	 * ID class operator location parameter coverage weak strong
	 * @throws Exception
	 */
	private void write_mut() throws Exception {
		this.open(".mut");
		for(Mutant mutant : this.source_cfile.get_mutant_space().get_mutants()) {
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
		this.cfile_writer.write(this.encode_token(result.get_mutant()) + "\t" + result.get_kill_set().toString() + "\n");
	}
	/**
	 * MID bit_string
	 * @throws Exception
	 */
	private void write_res() throws Exception {
		this.open(".res");
		MuTestProjectTestSpace tspace = this.source_cfile.get_code_space().get_project().get_test_space();
		for(Mutant mutant : this.source_cfile.get_mutant_space().get_mutants()) {
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
	
	/* symbolic related features */
	/**
	 * It generates all the symbolic nodes in the existing ones in this.sym_nodes
	 * and update this.sym_nodes by adding all the remaining ones.
	 * 
	 * @throws Exception
	 */
	private void extend_sym_nodes() throws Exception {
		/* 1. declarations and initialization */
		Queue<SymbolNode> queue = new LinkedList<SymbolNode>();
		HashSet<SymbolNode> records = new HashSet<SymbolNode>();
		
		/* 2. complete all the nodes under each root */
		for(SymbolNode sym_node : this.symbol_nodes) {
			/** BF-traverse algorithm to derive nodes **/
			queue.add(sym_node);
			while(!queue.isEmpty()) {
				SymbolNode parent = queue.poll();
				records.add(parent);
				for(SymbolNode child : parent.get_children()) {
					queue.add(child);
				}
			}
		}
		
		/* 4. appending all the remaining nodes to this.sym_nodes */
		this.symbol_nodes.clear(); this.symbol_nodes.addAll(records);
	}
	/**
	 * ID class source{Ast|Cir|Exe|Null|Const} data_type content code [child*]
	 * @param node
	 * @throws Exception
	 */
	private void write_sym_node(SymbolNode node) throws Exception {
		this.cfile_writer.write(this.encode_token(node));

		String class_name = node.getClass().getSimpleName();
		this.cfile_writer.write("\t" + class_name.substring(6));
		this.cfile_writer.write("\t" + this.encode_token(node.get_source()));
		
		CType data_type;
		if(node instanceof SymbolExpression) {
			data_type = ((SymbolExpression) node).get_data_type();
		}
		else {
			data_type = null;
		}
		this.cfile_writer.write("\t" + this.encode_token(data_type));

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
		this.cfile_writer.write("\t" + this.encode_token(content));

		this.cfile_writer.write("\t" + this.encode_token(node.generate_code(true)));

		this.cfile_writer.write("\t[");
		for(SymbolNode child : node.get_children()) {
			this.cfile_writer.write(" " + this.encode_token(child));
		}
		this.cfile_writer.write(" ]");

		this.cfile_writer.write("\n");
	}
	/**
	 * It writes all the symbolic nodes into the account
	 * @throws Exception
	 */
	private void write_sym(String middle_name) throws Exception {
		this.open(middle_name + ".sym");
		this.extend_sym_nodes();
		for(SymbolNode node : this.symbol_nodes) {
			this.write_sym_node(node);
		}
		this.close();
	}
	/**
	 * It recursively extends the source state to those subsumed by it in a given distance
	 * @param source	the abstract execution state from which the subsumption is extended
	 * @param context	CDependGraph | CirExecutionPath | CStatePath | null in which extension is performed
	 * @param distance	the distance that still remains for extending the subsumption relations
	 * @throws Exception	
	 */
	private void extend_subsume_at_state(CirAbstractState source, Object context, int distance) throws Exception {
		if(source == null || distance <= 0) { return; }		/* reach border */
		else {
			/* generate the subsumed states from source if not contained */
			if(!this.subsume_maps.containsKey(source)) {
				this.subsume_maps.put(source, new HashSet<CirAbstractState>());
				try {
					Collection<CirAbstractState> targets = source.subsumption(context);
					this.subsume_maps.get(source).addAll(targets);
				}
				catch(Exception ex) {
					this.subsume_maps.get(source).clear();
				}
			}
			
			/* recursively extend the subsumption relations from its children */
			Collection<CirAbstractState> targets = this.subsume_maps.get(source);
			for(CirAbstractState target : targets) {
				this.extend_subsume_at_state(target, context, distance - 1);
			}
		}
	}
	/**
	 * It recursively extends the subsumption relation hierarchies from the mutant in a given context and 
	 * the limit of maximal distance.
	 * @param mutant
	 * @param context
	 * @param max_distance
	 * @throws Exception
	 */
	private void extend_subsume_at_mutant(Mutant mutant, Object context, int max_distance) throws Exception {
		/* generate the state mutation(s) as the source */
		Collection<StateMutation> state_mutations = StateMutations.parse(mutant);
		if(state_mutations != null && !state_mutations.isEmpty()) {
			/* generate mutation and its initial subsumption from mutant */
			this.subsume_maps.put(mutant, new HashSet<CirAbstractState>());
			Set<CirAbstractState> targets = this.subsume_maps.get(mutant);
			for(StateMutation state_mutation : state_mutations) {
				targets.add(state_mutation.get_istate());
				targets.add(state_mutation.get_pstate());
			}
			
			/* recursively extend each initial state with max_distance */
			for(CirAbstractState target : targets) {
				this.extend_subsume_at_state(target, context, max_distance);
			}
		}
	}
	/**
	 * @param context
	 * @return the set of mutants available at given context
	 * @throws Exception
	 */
	private Iterable<Mutant> select_mutants_in(Object context) throws Exception {
		if(context instanceof CStatePath) {
			/* mapping from execution to corresponding mutants */
			Map<CirExecution, Set<Mutant>> exec_mutants = new HashMap<CirExecution, Set<Mutant>>();
			for(Mutant mutant : this.source_cfile.get_mutant_space().get_mutants()) {
				for(StateMutation mutation : StateMutations.parse(mutant)) {
					CirExecution execution = mutation.get_r_execution();
					if(!exec_mutants.containsKey(execution)) {
						exec_mutants.put(execution, new HashSet<Mutant>());
					}
					exec_mutants.get(execution).add(mutant);
				}
			}
			
			/* collect the covered execution sets */
			Set<Mutant> mutants = new HashSet<Mutant>();
			for(CStateNode state_node : ((CStatePath) context).get_nodes()) {
				CirExecution execution = state_node.get_execution();
				if(exec_mutants.containsKey(execution)) {
					mutants.addAll(exec_mutants.get(execution));
				}
			}
			return mutants;
		}
		else if(context instanceof CirExecutionPath) {
			/* mapping from execution to corresponding mutants */
			Map<CirExecution, Set<Mutant>> exec_mutants = new HashMap<CirExecution, Set<Mutant>>();
			for(Mutant mutant : this.source_cfile.get_mutant_space().get_mutants()) {
				for(StateMutation mutation : StateMutations.parse(mutant)) {
					CirExecution execution = mutation.get_r_execution();
					if(!exec_mutants.containsKey(execution)) {
						exec_mutants.put(execution, new HashSet<Mutant>());
					}
					exec_mutants.get(execution).add(mutant);
				}
			}
			
			/* collect the covered execution sets */
			Set<Mutant> mutants = new HashSet<Mutant>();
			for(CirExecutionEdge edge : ((CirExecutionPath) context).get_edges()) {
				if(exec_mutants.containsKey(edge.get_source())) {
					mutants.addAll(exec_mutants.get(edge.get_source()));
				}
			}
			if(exec_mutants.containsKey(((CirExecutionPath) context).get_target())) {
				mutants.addAll(exec_mutants.get(((CirExecutionPath) context).get_target()));
			}
			return mutants;
		}
		else {
			return this.source_cfile.get_mutant_space().get_mutants();
		}
	}
	/**
	 * It resets and generates the subsumption hierarchies for each mutant under the given context
	 * @param context	CDependGraph | CirExecutionPath | CStatePath | null in which extension is performed
	 * @throws Exception
	 */
	private void extend_subsume(Object context) throws Exception {
		this.subsume_maps.clear();
		for(Mutant mutant : this.select_mutants_in(context)) {
			this.extend_subsume_at_mutant(mutant, context, this.max_distance);
		}
	}
	/**
	 * It generates the summarized states from each state node in hierarchy
	 * @param context
	 * @throws Exception
	 */
	private void extend_summarization(Object context) throws Exception {
		/* collect all the nodes generated in subsumption hierarchies */
		Set<CirAbstractState> nodes = new HashSet<CirAbstractState>();
		for(Object source : this.subsume_maps.keySet()) {
			if(source instanceof CirAbstractState) {
				nodes.add((CirAbstractState) source);
			}
			nodes.addAll(this.subsume_maps.get(source));
		}
		
		/* initialization and generate the empty summarization states */
		this.extended_map.clear();
		for(CirAbstractState node : nodes) {
			this.extended_map.put(node, new HashSet<CirAbstractState>());
		}
		
		/* dynamic analysis */
		if(context instanceof CStatePath) {
			/* derive the mapping from execution to corresponding states */
			Map<CirExecution, Set<CirAbstractState>> exec_states = 
					new HashMap<CirExecution, Set<CirAbstractState>>();
			for(CirAbstractState node : nodes) {
				CirExecution execution = node.get_execution();
				if(!exec_states.containsKey(execution)) {
					exec_states.put(execution, new HashSet<CirAbstractState>());
				}
				exec_states.get(execution).add(node);
			}
			
			/* dynamic analysis using symbolic context and state paths */
			SymbolProcess process = new SymbolProcess(
					this.source_cfile.get_ast_tree(), 
					this.source_cfile.get_cir_tree());
			for(CStateNode state_node : ((CStatePath) context).get_nodes()) {
				process.accumulate(state_node);
				CirExecution execution = state_node.get_execution();
				if(exec_states.containsKey(execution)) {
					for(CirAbstractState state : exec_states.get(execution)) {
						this.extended_map.get(state).addAll(state.summarize(process));
					}
				}
			}
		}
		/* static analysis */
		else {
			for(CirAbstractState node : nodes) {
				this.extended_map.get(node).addAll(node.extend_all());
			}
		}
	}
	/**
	 * 	It writes xxx.ctxt.msh of specified subsumption context a hierarchy.<br>
	 * 	[M]	MID	INFEATION_STATE INFECTED_STATE								<br>
	 * 	[L] CONDITION_STATE		{CONDITION_STATE}+							<br>
	 * 	[P]	ERROR_STATE			{ERROR|CONDITION_STATE}+					<br>
	 * 	[X]	ABSTRACT_STATE		{EXTENDED_STATE}+							<br>
	 * 	<br>
	 * 	@param context		CDependGraph | CStatePath | null
	 * 	@param middile_name	".pdg" | ".tid" | ".nul"
	 * 	@param	[node_number, word_number]
	 * 	@throws Exception
	 */
	private int[] write_msg(Object context, String middile_name) throws Exception {
		/* 1. open the symbolic file names */
		this.open(middile_name + ".msg");
		
		/* 2. create subsumption hierarchy */
		this.extend_subsume(context);
		
		/* 3. write the state informations */
		for(Object source : this.subsume_maps.keySet()) {
			/* [M] for Mutant; [L] for Condition; [P] for Error. */
			if(source instanceof Mutant) {
				this.cfile_writer.write("[M]");
			}
			else if(source instanceof CirConditionState) {
				this.cfile_writer.write("[C]");
			}
			else {
				this.cfile_writer.write("[P]");
			}
			this.cfile_writer.write("\t" + this.encode_token(source));
			
			/* write the set of directly subsumed states */
			Set<CirAbstractState> targets = this.subsume_maps.get(source);
			for(CirAbstractState target : targets) {
				this.cfile_writer.write("\t");
				this.cfile_writer.write(this.encode_token(target));
			}
			this.cfile_writer.write("\n");
		}
		
		/* 4. write the extension set for each created state */
		this.extend_summarization(context);
		Set<CirAbstractState> all_states = new HashSet<CirAbstractState>();
		for(CirAbstractState state : this.extended_map.keySet()) {
			Set<CirAbstractState> extended_states = this.extended_map.get(state);
			all_states.add(state); all_states.addAll(extended_states);
			
			this.cfile_writer.write("[X]\t" + this.encode_token(state));
			for(CirAbstractState extended_state : extended_states) {
				this.cfile_writer.write("\t");
				this.cfile_writer.write(this.encode_token(extended_state));
			}
			this.cfile_writer.write("\n");
		}
		
		/* 5. close writing */	this.close();
		
		/* 6. return [node_number, word_number] */
		return new int[] { this.extended_map.size(), all_states.size() };
	}
	/**
	 * xxx.msh xxx.sym
	 * @param context
	 * @throws Exception
	 */
	private void write_symb_features(Object context, String middle_name) throws Exception {
		int[] numbers = this.write_msg(context, middle_name);
		this.write_sym(middle_name);
		
		/* TODO inform the users feature counters */
		int mutants = 0, symbols = this.symbol_nodes.size();
		for(Object source : this.subsume_maps.keySet()) {
			if(this.subsume_maps.get(source).size() > 0) {
				if(source instanceof Mutant) {
					mutants++;
				}
			}
		}
		System.out.println(String.format("\t\t==> %d/%d mutants; %d (%d) states; %d symbol-nodes", 
					mutants, source_cfile.get_mutant_space().size(), numbers[0], numbers[1], symbols));
	}
	
	/* write features for all */
	private CDependGraph new_dependence_graph() throws Exception {
		CirFunction root_function = this.source_cfile.
				get_cir_tree().get_function_call_graph().get_main_function();
		return CDependGraph.graph(CirCallContextInstanceGraph.graph(
				root_function, CirFunctionCallPathType.unique_path, -1));
	}
	/**
	 * It writes both static and dynamic subsumption hierarchies and features.
	 * @param source_cfile	the mutation testing project source file to print
	 * @param ou_directory	the directory where the output files are generated
	 * @param max_distance	the maximal distance to create subsumption of mutant
	 * @param test_cases	the set of test cases to generate dynamic MSG
	 * @throws Exception
	 */
	private void write(MuTestProjectCodeFile source_cfile, File ou_directory,
			int max_distance, Collection<TestInput> test_cases) throws Exception {
		if(source_cfile == null) {
			throw new IllegalArgumentException("Invalid source_cfile: null");
		}
		else if(ou_directory == null) {
			throw new IllegalArgumentException("Invalid ou_directory: null");
		}
		else if(max_distance < 0) {
			throw new IllegalArgumentException("Invalid max_distance: null");
		}
		else {
			/* static information generation */
			this.reset(source_cfile, ou_directory, max_distance);
			CDependGraph dependence_graph = this.new_dependence_graph();
			this.write_code_features();
			this.write_test_features();
			this.write_flow_features(dependence_graph);
			this.write_symb_features(dependence_graph, ".pdg");
			
			/* dynamic information generation */
			MuTestProjectTestSpace tspace = this.source_cfile.
					get_code_space().get_project().get_test_space();
			if(test_cases != null && !test_cases.isEmpty()) {
				for(TestInput test_case : test_cases) {
					CStatePath context = tspace.load_instrumental_path(
							this.source_cfile.get_sizeof_template(), 
							this.source_cfile.get_ast_tree(), 
							this.source_cfile.get_cir_tree(), test_case);
					if(context != null) {
						this.write_symb_features(context, "." + test_case.get_id());
					}
				}
			}
		}
	}
	/**
	 * It write code, test, dependence and state information under given context
	 * @param source_cfile
	 * @param ou_directory
	 * @param max_distance
	 * @param context		CDependGraph | CStatePath | CirExecutionPath | null
	 * @throws Exception
	 */
	public static void write_features(MuTestProjectCodeFile source_cfile,
			File ou_directory, int max_distance, 
			Collection<TestInput> test_cases) throws Exception {
		if(source_cfile == null) {
			throw new IllegalArgumentException("Invalid source_cfile: null");
		}
		else if(ou_directory == null) {
			throw new IllegalArgumentException("Invalid ou_directory: null");
		}
		else if(max_distance < 0) {
			throw new IllegalArgumentException("Invalid max_distance: null");
		}
		else {
			fwriter.write(source_cfile, ou_directory, max_distance, test_cases);
		}
	}
	
	
}
