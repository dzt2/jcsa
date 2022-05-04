package com.jcsa.jcmutest.mutant.ctx2mutant;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

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
 * 	It implements the generation of features related with contextual mutation.
 * 		
 * 	@author yukimula
 *
 */
public class ContextMutationFeatureWriter {
	
	/* attributes */
	/**	the source file defined in mutation testing project	**/
	private	MuTestProjectCodeFile							source_cfile;
	/**	the directory in which the output files are created	**/
	private	File											ou_directory;
	/**	the file writer to generate feature information for **/
	private FileWriter 										cfile_writer;	
	/**	the set of symbolic nodes to be printed to the file	**/
	private HashMap<String, SymbolNode>						symbol_nodes;
	
	/* singleton mode and constructor */ 
	private ContextMutationFeatureWriter() {
		this.source_cfile = null;
		this.ou_directory = null;
		this.cfile_writer = null;
		this.symbol_nodes = new HashMap<String, SymbolNode>();
	}
	private static final ContextMutationFeatureWriter fwriter = new ContextMutationFeatureWriter();
	
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
		else if(token instanceof CName) {
			return this.encode_base_token(((CName) token).get_name());
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
	 * 			WORDS:	category$location$loperand$roperand
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
			String category = ((AstContextState) token).get_category().toString();
			AstCirNode location = ((AstContextState) token).get_location();
			SymbolExpression loperand = ((AstContextState) token).get_loperand();
			SymbolExpression roperand = ((AstContextState) token).get_roperand();
			return category + "$" + this.encode_code_token(location) + "$" + this.
					encode_test_token(loperand) + "$" + this.encode_test_token(roperand);
		}
		else if(token instanceof ContextAnnotation) {
			String category = ((ContextAnnotation) token).get_category().toString();
			AstCirNode location = ((ContextAnnotation) token).get_location();
			SymbolExpression loperand = ((ContextAnnotation) token).get_loperand();
			SymbolExpression roperand = ((ContextAnnotation) token).get_roperand();
			return category + "$" + this.encode_code_token(location) + "$" + this.
					encode_test_token(loperand) + "$" + this.encode_test_token(roperand);
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
		this.cfile_writer.write("[NODE]");
		this.cfile_writer.write("\t" + node_id);
		this.cfile_writer.write("\t" + node_type);
		this.cfile_writer.write("\t" + ast_source);
		this.cfile_writer.write("\t" + token);
		this.cfile_writer.write("\t" + child_type);
		this.cfile_writer.write("\n");
	}
	/**
	 * [LIST] parent childID+ \n
	 * @param node
	 * @throws Exception
	 */
	private void write_ast_cir_list(AstCirNode node) throws Exception {
		this.cfile_writer.write("[LIST]");
		this.cfile_writer.write("\t" + this.encode_token(node));
		for(AstCirNode child : node.get_children()) {
			this.cfile_writer.write("\t" + this.encode_token(child));
		}
		this.cfile_writer.write("\n");
	}
	/**
	 * [LINK] node (link_type cir_node)* \n
	 * @param node
	 * @throws Exception
	 */
	private void write_ast_cir_link(AstCirNode node) throws Exception {
		this.cfile_writer.write("[LINK]");
		this.cfile_writer.write("\t" + this.encode_token(node));
		for(AstCirLink link : node.get_links()) {
			this.cfile_writer.write("\t" + link.get_type());
			this.cfile_writer.write("\t" + this.encode_token(link.get_target()));
		}
		this.cfile_writer.write("\n");
	}
	/**
	 * [EDGE] node (edge_type ast_cir_node)* \n
	 * @param node
	 * @throws Exception
	 */
	private void write_ast_cir_edge(AstCirNode node) throws Exception {
		this.cfile_writer.write("[EDGE]");
		this.cfile_writer.write("\t" + this.encode_token(node));
		for(AstCirEdge edge : node.get_ou_edges()) {
			this.cfile_writer.write("\t" + edge.get_type());
			this.cfile_writer.write("\t" + this.encode_token(edge.get_target()));
		}
		this.cfile_writer.write("\n");
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
	 * [NODE] ID STATE (ANNOTATION)* \n
	 * @param node
	 * @throws Exception
	 */
	private int write_context_node(ContextMutationNode node) throws Exception {
		this.cfile_writer.write("[NODE]\t" + node.get_node_id());
		this.cfile_writer.write("\t" + this.encode_token(node.get_state()));
		int ant_number = 0;
		for(ContextAnnotation annotation : node.get_annotations()) {
			this.cfile_writer.write("\t" + this.encode_token(annotation));
			ant_number++;
		}
		this.cfile_writer.write("\n");
		return ant_number;
	}
	/**
	 * [EDGE] source (target)+ \n
	 * @param node
	 * @throws Exception
	 */
	private int write_context_edge(ContextMutationNode node) throws Exception {
		this.cfile_writer.write("[EDGE]\t" + node.get_node_id());
		for(ContextMutationEdge edge : node.get_ou_edges()) {
			this.cfile_writer.write("\t" + edge.get_target().get_node_id());
		}
		this.cfile_writer.write("\n");
		return node.get_ou_degree();
	}
	/**
	 * [LINK] mut@int node_id
	 * @param tree
	 * @throws Exception
	 */
	private int write_context_links(ContextMutationTree tree) throws Exception {
		int succ = 0;
		for(Mutant mutant : tree.get_mutants()) {
			ContextMutationNode node = tree.get_tree_node_of(mutant); succ++;
			this.cfile_writer.write("[LINK]\t" + this.encode_token(mutant) + "\t" + node.get_node_id() + "\n");
		}
		return succ;
	}
	/**
	 * [NODE] ID STATE (ANNOTATION)* \n
	 * [EDGE] source (target)+ \n
	 * @param tree
	 * @throws Exception
	 */
	private int[] write_context_tree(ContextMutationTree tree) throws Exception {
		int nod_number = tree.number_of_tree_nodes();
		int ant_number = 0, edg_number = 0, mut_number = 0;
		for(ContextMutationNode node : tree.get_tree_nodes()) {
			ant_number += this.write_context_node(node);
			edg_number += this.write_context_edge(node);
		}
		mut_number = this.write_context_links(tree);
		return new int[] { mut_number, nod_number, edg_number, ant_number };
	}
	/**
	 * xxx.ctx
	 * [NODE] ID STATE (ANNOTATION)* \n
	 * [EDGE] source (target)+ \n
	 * @throws Exception
	 */
	private int[] write_ctx() throws Exception {
		this.open(".ctx");
		ContextMutationTree tree = ContextMutationTree.parse(
				this.source_cfile.get_ast_file(), 
				this.source_cfile.get_mutant_space().get_mutants());
		int[] results = this.write_context_tree(tree);
		this.close();
		return results;
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

		this.cfile_writer.write("\t" + this.encode_token(node.get_simple_code()));

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
	private void write_sym() throws Exception {
		this.open(".sym");
		for(SymbolNode node : this.symbol_nodes.values()) {
			this.write_sym_node(node);
		}
		this.close();
	}
	/**
	 * xxx.mut, xxx.tst, xxx.res
	 * @throws Exception
	 */
	private void write_test_features() throws Exception {
		this.write_tst(); 
		this.write_mut(); 
		int res_number = this.write_res(); 
		int[] results = this.write_ctx();
		this.write_sym();
		this.report_summary(res_number, results[0], results[1], results[2], results[3]);
	}
	
	/* interfaces */
	/**
	 * It reports the number of features used in definition models
	 */
	private void report_summary(int res_number, int succ_number, int node_number, int edge_number, int ant_number) throws Exception {
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
		System.out.println("\t\t[NODE] = " + node_number + ";\t[CMUT] = " + succ_number + ";\t(" + succ_ratio + "%)");
		
		int sym_number = this.symbol_nodes.size();
		System.out.println("\t\t[EDGE] = " + edge_number + ";\t[ANOT] = " + ant_number + ";\t[SYMB] = " + sym_number);
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
