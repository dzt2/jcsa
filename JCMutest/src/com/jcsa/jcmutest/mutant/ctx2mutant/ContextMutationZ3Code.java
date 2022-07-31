package com.jcsa.jcmutest.mutant.ctx2mutant;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstContextState;
import com.jcsa.jcmutest.mutant.ctx2mutant.tree.ContextAnnotation;
import com.jcsa.jcmutest.mutant.ctx2mutant.tree.ContextMutationEdge;
import com.jcsa.jcmutest.mutant.ctx2mutant.tree.ContextMutationNode;
import com.jcsa.jcmutest.mutant.ctx2mutant.tree.ContextMutationTree;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.MuTestProjectTestResult;
import com.jcsa.jcmutest.project.MuTestProjectTestSpace;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcparse.base.Complex;
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
import com.jcsa.jcparse.lang.program.AstCirEdge;
import com.jcsa.jcparse.lang.program.AstCirLink;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.program.AstCirTree;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.symbol.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolField;
import com.jcsa.jcparse.lang.symbol.SymbolFieldExpression;
import com.jcsa.jcparse.lang.symbol.SymbolIdentifier;
import com.jcsa.jcparse.lang.symbol.SymbolLiteral;
import com.jcsa.jcparse.lang.symbol.SymbolNode;
import com.jcsa.jcparse.lang.symbol.SymbolOperator;
import com.jcsa.jcparse.lang.symbol.SymbolUnaryExpression;
import com.jcsa.jcparse.test.file.TestInput;

/**
 * 	It generates the z3-code for evaluating contextual expression mutation.
 * 	<br>
 * 	<code>
 * 	+-----------+---------------------------------------------------------------+<br>
 * 	|  File		|	File-Description											|<br>
 * 	+-----------+---------------------------------------------------------------+<br>
 * 	|  xxx.cpp	|	The source code of program under test.						|<br>
 * 	|  xxx.ast	|	The abstract syntax tree of program under test.				|<br>
 * 	|  xxx.cir	|	The C-intermediate representative code.						|<br>
 * 	|  xxx.flw	|	The program control flow graph for analysis.				|<br>
 * 	|  xxx.asc	|	The AST-CIR combined program syntactic tree.				|<br>
 * 	+-----------+---------------------------------------------------------------+<br>
 * 	|  xxx.tst	|	The set of test cases for executing testing.				|<br>
 * 	|  xxx.mut	|	The syntactic mutations being executed in.					|<br>
 * 	|  xxx.res	|	The mutation testing results being executed.				|<br>
 * 	|  xxx.sym	|	The symbolic expressions being used in encoding.			|<br>
 * 	|  xxx.ztx	|	The contextual expression mutation structural model.		|<br>
 * 	+-----------+---------------------------------------------------------------+<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public class ContextMutationZ3Code {
	
	/* attributes */
	/**	the source file defined in mutation testing project	**/
	private	MuTestProjectCodeFile							source_cfile;
	/**	the directory in which the output files are created	**/
	private	File											ou_directory;
	/**	the file writer to generate feature information for **/
	private FileWriter 										cfile_writer;	
	/**	the set of symbolic nodes to be printed to the file	**/
	private HashMap<String, SymbolNode>						symbol_nodes;
	/** single mode of context mutation writer from program **/
	private ContextMutationZ3Code() {
		this.source_cfile = null;
		this.ou_directory = null;
		this.cfile_writer = null;
		this.symbol_nodes = new HashMap<String, SymbolNode>();
	}
	
	/* IO-operation */
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
	 * It write the text to the output stream
	 * @param text
	 * @throws Exception
	 */
	private void write(String text) throws Exception { 
		if(this.cfile_writer == null) {
			throw new IllegalArgumentException("No file is opened");
		}
		else {
			this.cfile_writer.write(text);
		}
	}
	
	/* basic token encoding methods */
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
		else if(token instanceof CName) {
			return this.encode_token(((CName) token).get_name());
		}
		else if(token instanceof CConstant) {
			return this.encode_token(((CConstant) token).get_object());
		}
		else {
			return null;
		}
	}
	/**
	 * @param token
	 * @return	ENUMS:	key@keyword, opr@operator, pun@punctuator, typ@type;
	 * 			OBJEC:	ast@int, cir@int, asc@int, exe@txt@int, ins@txt@int@int;
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
		else if(token instanceof AstCirNode) {
			int key = ((AstCirNode) token).get_node_id();
			return "asc@" + key;
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
	 * 			WORDS:	bool$category$location$loperand$roperand
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
			String key = "sym@" + token.getClass().getSimpleName().substring(6).trim() + "@" + token.hashCode();
			if(!this.symbol_nodes.containsKey(key)) { this.symbol_nodes.put(key, (SymbolNode) token); }
			for(SymbolNode child : ((SymbolNode) token).get_children()) { this.encode_token(child); }
			return key;
		}
		else if(token instanceof AstContextState) {
			String head = this.encode_token(Boolean.TRUE);
			String category = ((AstContextState) token).get_category().toString();
			AstCirNode location = ((AstContextState) token).get_location();
			SymbolExpression loperand = ((AstContextState) token).get_loperand();
			SymbolExpression roperand = ((AstContextState) token).get_roperand();
			return 	head + "$" + category + "$" + this.encode_code_token(location) + "$" + 
					this.encode_test_token(loperand) + "$" + this.encode_test_token(roperand);
		}
		else if(token instanceof ContextAnnotation) {
			String head = this.encode_token(Boolean.FALSE);
			String category = ((ContextAnnotation) token).get_category().toString();
			AstCirNode location = ((ContextAnnotation) token).get_location();
			SymbolExpression loperand = ((ContextAnnotation) token).get_loperand();
			SymbolExpression roperand = ((ContextAnnotation) token).get_roperand();
			return 	head + "$" + category + "$" + this.encode_code_token(location) + "$" + 
					this.encode_test_token(loperand) + "$" + this.encode_test_token(roperand);
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
	
	/* source code feature writers */
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
	 * @param ast_node
	 * @throws Exception
	 */
	private void write_ast_node(AstNode node) throws Exception {
		/* ast_key ast_class beg_index end_index */
		String ast_key = this.encode_token(node);
		String class_name = node.getClass().getSimpleName();
		String ast_class = class_name.substring(3, class_name.length() - 4).strip();
		int beg_index = node.get_location().get_bias();
		int end_index = node.get_location().get_bias() + node.get_location().get_length();
		this.write(ast_key + "\t" + ast_class + "\t" + beg_index + "\t" + end_index);
		
		/* data_type */
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
		this.write("\t" + this.encode_token(data_type));
		
		/* content */
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
		this.write("\t" + this.encode_token(content));
		
		this.write("\t[");
		for(int k = 0; k < node.number_of_children(); k++) {
			this.write(" " + this.encode_token(node.get_child(k)));
		}
		this.write(" ]\n");
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
		/* cir_key cir_class ast_source */
		String cir_key = this.encode_token(node);
		String class_name = node.getClass().getSimpleName();
		String cir_class = class_name.substring(3, class_name.length() - 4).strip();
		String ast_source = this.encode_token(node.get_ast_source());
		this.write(cir_key + "\t" + cir_class + "\t" + ast_source);
		
		/* data_type */
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
		this.write("\t" + this.encode_token(data_type));
		
		/* content */
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
		this.write("\t" + this.encode_token(content));
		
		/* [ child_key* ] */
		this.write("\t[");
		for(CirNode child : node.get_children()) {
			this.write(" " + this.encode_token(child));
		}
		this.write(" ]");
		
		/* cir_code */
		String code = null;
		if(!(node instanceof CirFunctionDefinition
			|| node instanceof CirTransitionUnit
			|| node instanceof CirFunctionBody)) {
			code = node.generate_code(true);
		}
		this.write("\t" + this.encode_token(code));
		this.write("\n");
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
		this.write("\t[edge]");
		this.write("\t" + flow.get_type());
		this.write("\t" + this.encode_token(flow.get_source()));
		this.write("\t" + this.encode_token(flow.get_target()));
		this.write("\n");
	}
	/**
	 * [call] call_exec wait_exec
	 * @param call
	 * @throws Exception
	 */
	private void write_execution_call(CirFunctionCall call) throws Exception {
		this.write("\t[call]");
		this.write("\t" + this.encode_token(call.get_call_execution()));
		this.write("\t" + this.encode_token(call.get_wait_execution()));
		this.write("\n");
	}
	/**
	 * [node] ID cir_statement
	 * @param execution
	 * @throws Exception
	 */
	private void write_execution_node(CirExecution execution) throws Exception {
		this.write("\t[node]");
		this.write("\t" + this.encode_token(execution));
		this.write("\t" + this.encode_token(execution.get_statement()));
		this.write("\n");
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
		this.write("[beg]\t" + function.get_name() + "\n");
		for(CirExecution execution : function.get_flow_graph().get_executions()) {
			this.write_execution_node(execution);
			for(CirExecutionFlow flow : execution.get_ou_flows()) {
				this.write_execution_flow(flow);
			}
		}
		for(CirFunctionCall call : function.get_ou_calls()) {
			this.write_execution_call(call);
		}
		this.write("[end]\t" + function.get_name() + "\n");
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
			this.write("\n");
		}
		this.close();
	}
	/**
	 * [NODE] ID class ast_source token child_type \n
	 * @param node
	 * @throws Exception
	 */
	private	void write_ast_cir_node(AstCirNode node) throws Exception {
		String node_id = this.encode_token(node);
		String node_type = node.get_node_type().toString();
		String ast_source = this.encode_token(node.get_ast_source());
		String token = this.encode_token(node.get_token());
		String child_type;
		if(!node.is_root()) {
			child_type = this.encode_token(node.get_child_type().toString());
		}
		else {
			child_type = this.encode_token(null);
		}
		this.write("[NODE]");
		this.write("\t" + node_id);
		this.write("\t" + node_type);
		this.write("\t" + ast_source);
		this.write("\t" + token);
		this.write("\t" + child_type);
		this.write("\n");
	}
	/**
	 * [LIST] parent childID+ \n
	 * @param node
	 * @throws Exception
	 */
	private void write_ast_cir_list(AstCirNode node) throws Exception {
		this.write("[LIST]");
		this.write("\t" + this.encode_token(node));
		for(AstCirNode child : node.get_children()) {
			this.write("\t" + this.encode_token(child));
		}
		this.write("\n");
	}
	/**
	 * [LINK] node (link_type cir_node)* \n
	 * @param node
	 * @throws Exception
	 */
	private void write_ast_cir_link(AstCirNode node) throws Exception {
		this.write("[LINK]");
		this.write("\t" + this.encode_token(node));
		for(AstCirLink link : node.get_links()) {
			this.write("\t" + link.get_type());
			this.write("\t" + this.encode_token(link.get_target()));
		}
		this.write("\n");
	}
	/**
	 * [EDGE] node (edge_type ast_cir_node)* \n
	 * @param node
	 * @throws Exception
	 */
	private void write_ast_cir_edge(AstCirNode node) throws Exception {
		this.write("[EDGE]");
		this.write("\t" + this.encode_token(node));
		for(AstCirEdge edge : node.get_ou_edges()) {
			this.write("\t" + edge.get_type());
			this.write("\t" + this.encode_token(edge.get_target()));
		}
		this.write("\n");
	}
	/**
	 * xxx.asc as
	 * [NODE] ID class ast_source token child_typen	\n
	 * [LIST] parent childID+						\n
	 * [LINK] node (link_type cir_node)*			\n
	 * [EDGE] node (edge_type ast_cir_node)*		\n
	 * @throws Exception
	 */
	private void write_asc() throws Exception {
		this.open(".asc");
		AstCirTree tree = this.source_cfile.get_ast_file();
		for(AstCirNode node : tree.get_tree_nodes()) {
			this.write_ast_cir_node(node);
			this.write_ast_cir_list(node);
			this.write_ast_cir_link(node);
			this.write_ast_cir_edge(node);
		}
		this.close();
	}
	/**
	 * xxx.cpp, xxx.ast, xxx.cir, xxx.flw, xxx.asc
	 * @throws Exception
	 */
	private void write_code_features() throws Exception {
		this.write_cpp();
		this.write_ast();
		this.write_cir();
		this.write_flw();
		this.write_asc();
	}
	
	/* testing related data features */
	/**
	 * ID parameter
	 * @param test
	 * @throws Exception
	 */
	private void write_tst(TestInput test) throws Exception {
		this.write(this.encode_token(test) + "\t" + this.encode_token(test.get_parameter()) + "\n");
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
		this.write("" + this.encode_token(mutant));
		
		this.write("\t" + mutant.get_mutation().get_class());
		this.write("\t" + mutant.get_mutation().get_operator());
		this.write("\t" + this.encode_token(mutant.get_mutation().get_location()));
		this.write("\t" + this.encode_token(mutant.get_mutation().get_parameter()));

		this.write("\t" + this.encode_token(mutant.get_coverage_mutant()));
		this.write("\t" + this.encode_token(mutant.get_weak_mutant()));
		this.write("\t" + this.encode_token(mutant.get_strong_mutant()));

		this.write("\n");
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
		this.write(this.encode_token(result.get_mutant()) + "\t" + result.get_kill_set().toString() + "\n");
	}
	/**
	 * MID bit_string
	 * @throws Exception
	 */
	private int write_res() throws Exception {
		this.open(".res");
		int res_number = 0;
		MuTestProjectTestSpace tspace = this.source_cfile.get_code_space().get_project().get_test_space();
		for(Mutant mutant : this.source_cfile.get_mutant_space().get_mutants()) {
			MuTestProjectTestResult result = tspace.get_test_result(mutant);
			if(result != null) {
				this.write_res(result); 
				res_number++;
			}
		}
		this.close();
		return res_number;
	}
	/**
	 * ID class source{Ast|Cir|Exe|Null|Const} data_type content code [child*]
	 * @param node
	 * @throws Exception
	 */
	private void write_sym_node(SymbolNode node) throws Exception {
		/* sym_id class_name sym_source */
		this.write(this.encode_token(node));
		String class_name = node.getClass().getSimpleName();
		this.write("\t" + class_name.substring(6));
		this.write("\t" + this.encode_token(node.get_source()));
		
		/* data_type */
		CType data_type;
		if(node instanceof SymbolExpression) {
			data_type = ((SymbolExpression) node).get_data_type();
		}
		else {
			data_type = null;
		}
		this.write("\t" + this.encode_token(data_type));
		
		/* content */
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
		this.write("\t" + this.encode_token(content));
		
		/* code */
		this.write("\t" + this.encode_token(node.get_simple_code()));
		
		/* [ child_node* ] */
		this.write("\t[");
		for(SymbolNode child : node.get_children()) {
			this.write(" " + this.encode_token(child));
		}
		this.write(" ]\n");
	}
	/**
	 * It writes all the symbolic nodes into the account
	 * @throws Exception
	 */
	private void write_sym() throws Exception {
		this.open(".sym");
		for(SymbolNode node : this.symbol_nodes.values()) {
			this.write_sym_node(node);
		}
		this.close();
	}
	/**
	 * @param mutants
	 * @return it creates a tree of contextual mutations for input mutants
	 * @throws Exception
	 */
	private ContextMutationTree get_context_tree(Collection<Mutant> mutants) throws Exception {
		return ContextMutationTree.parse(this.source_cfile.get_ast_file(), mutants);
	}
	/**
	 * @param tree
	 * @param mutant
	 * @return the set of nodes connected with the tree
	 * @throws Exception
	 */
	private Collection<ContextMutationNode> get_context_nodes(ContextMutationTree tree, Mutant mutant) throws Exception {
		/*
		Collection<ContextMutationNode> nodes = new HashSet<ContextMutationNode>();
		Queue<ContextMutationNode> queue = new LinkedList<ContextMutationNode>();
		Collection<ContextMutationNode> results = new HashSet<ContextMutationNode>();
		if(tree.has_tree_node_of(mutant)) {
			queue.add(tree.get_tree_node_of(mutant));
			while(!queue.isEmpty()) {
				ContextMutationNode parent = queue.poll();
				nodes.add(parent);
				for(ContextMutationEdge edge : parent.get_ou_edges()) {
					ContextMutationNode child = edge.get_target();
					if(!nodes.contains(child)) { queue.add(child); }
				}
			}
		}
		return results;
		*/
		
		Collection<ContextMutationNode> results = new HashSet<ContextMutationNode>();
		Queue<ContextMutationNode> queue = new LinkedList<ContextMutationNode>();
		if(tree.has_tree_node_of(mutant)) {
			ContextMutationNode root = tree.get_tree_node_of(mutant);
			results.add(root.get_ou_edge(0).get_target());
			queue.add(root.get_ou_edge(1).get_target());
			while(!queue.isEmpty()) {
				ContextMutationNode node = queue.poll();
				results.add(node);
				for(ContextMutationEdge edge : node.get_ou_edges()) {
					queue.add(edge.get_target());
				}
			}
		}
		return results;
	}
	/**
	 * @param tree
	 * @param mutant mid {(node {annotation}+)+} \n
	 * @return [ pass_number, node_number, ant_number ]
	 * @throws Exception
	 */
	private int[] write_context_line(ContextMutationTree tree, Mutant mutant) throws Exception {
		int pass_number = 0, node_number = 0, anot_number = 0;
		Collection<ContextMutationNode> nodes = this.get_context_nodes(tree, mutant);
		if(!nodes.isEmpty()) {
			pass_number++; this.write(this.encode_token(mutant));
			for(ContextMutationNode node : nodes) {
				/* print state [cov_time, eva_cond, set_stmt, set_expr] */
				AstContextState state = node.get_state();
				switch(state.get_category()) {
				case cov_time:
				case eva_cond:
				case set_stmt:
				case set_expr:
				{
					node_number++;
					this.write("\t" + this.encode_token(node.get_state()));
				}
				default: break;
				}
				
				/* print annotations in [cov_time, eva_cond, set_expr, inc_expr, xor_expr] */
				for(ContextAnnotation annotation : node.get_annotations()) {
					switch(annotation.get_category()) {
					case cov_time:
					case eva_cond:
					case set_stmt:
					case set_expr:
					case inc_expr:
					case xor_expr:
					{
						anot_number++;
						this.write("\t" + this.encode_token(annotation));
					}
					default: break;
					}
				}
			}
			this.write("\n");
		}
		return new int[] { pass_number, node_number, anot_number };
	}
	/**
	 * @param mutants mid {(node {annotation}+)+} \n
	 * @return [ pass_number, node_number, anot_number ] 
	 * @throws Exception
	 */
	private int[] write_context_lines(Collection<Mutant> mutants) throws Exception {
		ContextMutationTree tree = this.get_context_tree(mutants);
		int pass_number = 0, node_number = 0, anot_number = 0;
		for(Mutant mutant : mutants) {
			int[] result = this.write_context_line(tree, mutant);
			pass_number += result[0];
			node_number += result[1];
			anot_number += result[2];
		}
		return new int[] { pass_number, node_number, anot_number };
	}
	/**
	 * mid {(node {annotation}+)+} \n
	 * @return [pass_number, node_number, ]
	 * @throws Exception
	 */
	private int[] write_ctx(int buffer_size) throws Exception {
		if(buffer_size <= 0) {
			throw new IllegalArgumentException("Invalid: " + buffer_size);
		}
		else {
			this.open(".ctx"); int[] results = new int[] {0,0,0};
			Collection<Mutant> mutants = new ArrayList<Mutant>();
			for(Mutant mutant : this.source_cfile.get_mutant_space().get_mutants()) {
				mutants.add(mutant);
				if(mutants.size() >= buffer_size) {
					int[] result = this.write_context_lines(mutants);
					for(int k = 0; k < result.length; k++) {
						results[k] = results[k] + result[k];
					}
					mutants.clear();
				}
			}
			if(!mutants.isEmpty()) {
				int[] result = this.write_context_lines(mutants);
				for(int k = 0; k < result.length; k++) {
					results[k] = results[k] + result[k];
				}
			}
			this.close();
			return results;
		}
	}
	/**
	 * It reports the number of features used in definition models
	 */
	private void report_summary(int res_number, int succ_number, int node_number, int anot_number) throws Exception {
		MuTestProjectTestSpace tspace = this.source_cfile.get_code_space().get_project().get_test_space();
		int code_lines = this.source_cfile.get_ast_file().get_source_code().number_of_lines();
		int tst_number = tspace.number_of_test_inputs();
		System.out.println("\t\t[FILE] = " + this.source_cfile.get_name() + ";\t[TEST] = " + tst_number);
		
		int ast_number = this.source_cfile.get_ast_tree().number_of_nodes();
		int asc_number = this.source_cfile.get_ast_file().number_of_tree_nodes();
		int cir_number = this.source_cfile.get_cir_tree().size();
		int exe_number = 0; int fun_number = 0;
		for(CirFunction function : this.source_cfile.get_cir_tree().get_function_call_graph().get_functions()) {
			exe_number += function.get_flow_graph().size(); fun_number++;
		}
		System.out.println("\t\t[LINE] = " + code_lines + ";\t[ASTN] = " + ast_number + ";\t[ASTC] = " + asc_number);
		System.out.println("\t\t[CIRN] = " + cir_number + ";\t[EXEC] = " + exe_number + ";\t[FUNC] = " + fun_number);
		
		int mut_number = this.source_cfile.get_mutant_space().size();
		double test_ratio = ((double) res_number) / ((double) mut_number);
		test_ratio = ((int) (test_ratio * 10000)) / 100.0;
		System.out.println("\t\t[MUTA] = " + mut_number + ";\t[REST] = " + res_number + ";\t(" + test_ratio + "%)");
		
		double succ_ratio = ((double) succ_number) / ((double) mut_number);
		succ_ratio = ((int) (succ_ratio * 10000)) / 100.0;
		System.out.println("\t\t[SUCC] = " + succ_number + ";\t[RATE] = " + succ_ratio + "%;");
		
		int symb_number = this.symbol_nodes.size();
		System.out.println("\t\t[NODE] = " + node_number + ";\t[ANOT] = " + anot_number + ";\t[SYMB] = " + symb_number);
	}
	/**
	 * xxx.mut, xxx.tst, xxx.res
	 * @throws Exception
	 */
	private void write_test_features() throws Exception {
		this.write_tst(); 
		this.write_mut(); 
		int res_number = this.write_res(); 
		int[] results = this.write_ctx(1024 * 8);
		this.write_sym();
		this.report_summary(res_number, results[0], results[1], results[2]);
	}
	
	/* interfaces */
	/**
	 * It resets the inputs cfile and out-directory for writing
	 * @param source_cfile	the mutation source code file for writing features
	 * @param ou_directory	the directory in which the ouput files are written
	 * @param max_distance	the maximal distance of subsumption from mutations
	 * @throws Exception
	 */
	private void reset(MuTestProjectCodeFile source_cfile, File ou_directory) throws Exception {
		if(source_cfile == null) {
			throw new IllegalArgumentException("Invalid source_cfile: null");
		}
		else if(ou_directory == null) {
			throw new IllegalArgumentException("Invalid ou_directory: null");
		}
		else if(ou_directory.exists() && !ou_directory.isDirectory()) {
			throw new IllegalArgumentException("Undefined: " + ou_directory);
		}
		else {
			if(!ou_directory.exists()) {
				FileOperations.mkdir(ou_directory);
			}
			this.source_cfile = source_cfile;
			this.ou_directory = ou_directory;
			this.symbol_nodes.clear();
			this.close();
			SymbolFactory.set_config(this.source_cfile.get_sizeof_template(), true);
		}
	}
	/**
	 * It writes both static and dynamic subsumption hierarchies and features.
	 * @param source_cfile	the mutation testing project source file to print
	 * @param max_distance	the maximal distance to create subsumption of mutant
	 * @throws Exception
	 */
	private void write(MuTestProjectCodeFile source_cfile, File ou_directory) throws Exception {
		if(source_cfile == null) {
			throw new IllegalArgumentException("Invalid source_cfile: null");
		}
		else if(ou_directory == null) {
			throw new IllegalArgumentException("Invalid ou_directory: null");
		}
		else {
			this.reset(source_cfile, ou_directory);
			this.write_code_features();
			this.write_test_features();
		}
	}
	private static final ContextMutationZ3Code fwriter = new ContextMutationZ3Code();
	/**
	 * It writes both static and dynamic subsumption hierarchies and features.
	 * @param source_cfile	the mutation testing project source file to print
	 * @param max_distance	the maximal distance to create subsumption of mutant
	 * @throws Exception
	 */
	public static void write_features(MuTestProjectCodeFile source_cfile,
			File ou_directory) throws Exception {
		if(source_cfile == null) {
			throw new IllegalArgumentException("Invalid source_cfile: null");
		}
		else if(ou_directory == null) {
			throw new IllegalArgumentException("Invalid ou_directory: null");
		}
		else {
			fwriter.write(source_cfile, ou_directory);
		}
	}
	
}
