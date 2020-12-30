package com.jcsa.jcmutest.project;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirAnnotation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.path.SymInstanceEdge;
import com.jcsa.jcmutest.mutant.cir2mutant.path.SymInstanceGraph;
import com.jcsa.jcmutest.mutant.cir2mutant.path.SymInstanceNode;
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
import com.jcsa.jcparse.lang.sym.SymBinaryExpression;
import com.jcsa.jcparse.lang.sym.SymConstant;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymField;
import com.jcsa.jcparse.lang.sym.SymFieldExpression;
import com.jcsa.jcparse.lang.sym.SymIdentifier;
import com.jcsa.jcparse.lang.sym.SymLiteral;
import com.jcsa.jcparse.lang.sym.SymNode;
import com.jcsa.jcparse.lang.sym.SymOperator;
import com.jcsa.jcparse.lang.sym.SymUnaryExpression;
import com.jcsa.jcparse.test.file.TestInput;
import com.jcsa.jcparse.test.state.CStatePath;

/**
 * It implements to write the feature information to output file, including:<br>
 * 	1. xxx.cpp; xxx.ast; xxx.cir; xxx.flw;	[code]
 * 	2. xxx.tst; xxx.mut; xxx.res; xxx.sft;	[muta]
 * 	3. xxx.tid.dft;							[test]
 * 
 * @author yukimula
 *
 */
public class MuTestProjectFeatureWriter {
	
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
	
	/* constructor */
	/**
	 * create a writer for generating feature data in project
	 * @param output_directory
	 * @param file_name
	 * @throws IllegalArgumentException
	 */
	public MuTestProjectFeatureWriter(MuTestProjectCodeFile source, File output_directory) throws IllegalArgumentException {
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
		}
	}
	
	/* output stream methods */
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
	
	/* basic data method */
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
		else if(token instanceof SymNode) {
			SymNode node = (SymNode) token;
			return "sym@" + node.getClass().getSimpleName().substring(3) + "@" + node.hashCode();
		}
		else
			throw new IllegalArgumentException("Unsupport: " + token.getClass().getSimpleName());
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
	/* xxx.cpp, xxx.ast, xxx.cir, xxx.flw */
	/**
	 * xxx.cpp, xxx.ast, xxx.cir, xxx.flw
	 * @throws Exception
	 */
	public void write_code() throws Exception {
		this.write_cpp();
		this.write_ast();
		this.write_cir();
		this.write_flw();
	}
	
	/* xxx.ins xxx.dep */
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
	private void write_instance_graph(CirInstanceGraph graph) throws Exception {
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
	 * static information from dependence graph
	 * @param graph
	 * @throws Exception
	 */
	private void write_stat(CDependGraph graph) throws Exception {
		this.write_instance_graph(graph.get_program_graph());
		this.write_dependence_graph(graph);
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
	/* xxx.tst; xxx.mut; xxx.res; */
	/**
	 * xxx.tst; xxx.mut; xxx.res;
	 * @throws Exception
	 */
	public void write_muta() throws Exception {
		this.write_tst();
		this.write_mut();
		this.write_res();
	}
	
	/* xxx.sft|dft xxx.sym */
	/**
	 * collect all the nodes under the given input node into nodes collection
	 * @param node
	 * @param nodes
	 * @throws Exception
	 */
	private void collect_sym_node(SymNode node, Collection<SymNode> nodes) throws Exception {
		Queue<SymNode> queue = new LinkedList<SymNode>(); queue.add(node);
		while(!queue.isEmpty()) {
			SymNode parent = queue.poll(); nodes.add(parent);
			for(SymNode child : parent.get_children()) queue.add(child);
		}
	}
	/**
	 * ID class source{Ast|Cir|Exe|Null|Const} data_type content code [child*]
	 * @param node
	 * @throws Exception
	 */
	private void write_sym_node(SymNode node) throws Exception {
		this.writer.write(this.token_string(node));
		
		String class_name = node.getClass().getSimpleName();
		this.writer.write("\t" + class_name.substring(3));
		this.writer.write("\t" + this.token_string(node.get_source()));
		
		CType data_type;
		if(node instanceof SymExpression) {
			data_type = ((SymExpression) node).get_data_type();
		}
		else {
			data_type = null;
		}
		this.writer.write("\t" + this.token_string(data_type));
		
		Object content;
		if(node instanceof SymField) {
			content = ((SymField) node).get_name();
		}
		else if(node instanceof SymOperator) {
			content = ((SymOperator) node).get_operator();
		}
		else if(node instanceof SymIdentifier) {
			content = ((SymIdentifier) node).get_name();
		}
		else if(node instanceof SymConstant) {
			content = ((SymConstant) node).get_constant();
		}
		else if(node instanceof SymLiteral) {
			content = ((SymLiteral) node).get_literal();
		}
		else if(node instanceof SymBinaryExpression) {
			content = ((SymBinaryExpression) node).get_operator().get_operator();
		}
		else if(node instanceof SymUnaryExpression) {
			content = ((SymUnaryExpression) node).get_operator().get_operator();
		}
		else if(node instanceof SymFieldExpression) {
			content = CPunctuator.dot;
		}
		else {
			content = null;
		}
		this.writer.write("\t" + this.token_string(content));
		
		this.writer.write("\t" + this.token_string(node.generate_code(true)));
		
		this.writer.write("\t[");
		for(SymNode child : node.get_children()) {
			this.writer.write(" " + this.token_string(child));
		}
		this.writer.write(" ]");
		
		this.writer.write("\n");
	}
	/**
	 * ID class source{Ast|Cir|Exe|Null|Const} data_type content code [child*]
	 * @param nodes
	 * @throws Exception
	 */
	private void write_sym_nodes(Collection<SymNode> nodes) throws Exception {
		this.open(".sym");
		for(SymNode node : nodes) {
			this.write_sym_node(node);
		}
		this.close();
	}
	/**
	 * [ const execution location sym_expression ]
	 * @param constraint
	 * @throws Exception
	 */
	private void write_feature_word(SymConstraint constraint, Collection<SymNode> nodes) throws Exception {
		this.collect_sym_node(constraint.get_condition(), nodes);
		this.writer.write("const");
		this.writer.write("$" + this.token_string(constraint.get_execution()));
		this.writer.write("$" + this.token_string(constraint.get_statement()));
		this.writer.write("$" + this.token_string(constraint.get_condition()));
	}
	/**
	 * [ type execution location sym_expression? ]
	 * @param annotation
	 * @throws Exception
	 */
	private void write_feature_word(CirAnnotation annotation, Collection<SymNode> nodes) throws Exception {
		if(annotation.get_parameter() instanceof SymNode) 
			this.collect_sym_node((SymNode) annotation.get_parameter(), nodes);
		this.writer.write(annotation.get_type().toString());
		this.writer.write("$" + this.token_string(annotation.get_execution()));
		this.writer.write("$" + this.token_string(annotation.get_location()));
		this.writer.write("$" + this.token_string(annotation.get_parameter()));
	}
	/**
	 * constraint*
	 * @param edge
	 * @throws Exception
	 */
	private void write_sym_instance_edge(SymInstanceEdge edge, Collection<SymNode> nodes) throws Exception {
		CirMutations cir_mutations = edge.get_source().get_graph().get_cir_mutations();
		SymConstraint constraint = edge.get_constraint(); 
		constraint = cir_mutations.optimize(constraint, null);
		Collection<SymConstraint> constraints = cir_mutations.improve_constraints(constraint);
		
		/* TODO improved constraint + annotations */
		for(SymConstraint improved_constraint : constraints) {
			writer.write("\t");
			this.write_feature_word(improved_constraint, nodes);
		}
		for(CirAnnotation annotation : edge.get_status().get_cir_annotations()) {
			writer.write("\t");
			this.write_feature_word(annotation, nodes);
		}
	}
	/**
	 * annotation+
	 * @param node
	 * @throws Exception
	 */
	private void write_sym_instance_node(SymInstanceNode node, Collection<SymNode> nodes) throws Exception {
		if(node.has_state_error()) {
			for(CirAnnotation annotation : node.get_status().get_cir_annotations()) {
				writer.write("\t");
				this.write_feature_word(annotation, nodes);
			}
		}
	}
	/**
	 * cons error annotation ...
	 * @param path
	 * @throws Exception
	 */
	private void write_sym_instance_path(List<SymInstanceEdge> path, Collection<SymNode> nodes) throws Exception {
		if(!path.isEmpty()) {
			this.write_sym_instance_node(path.get(0).get_source(), nodes);
			for(SymInstanceEdge edge : path) {
				this.write_sym_instance_edge(edge, nodes);
				this.write_sym_instance_node(edge.get_target(), nodes);
			}
		}
	}
	/**
	 * mid tid {cons|erro|anno}*
	 * @param graph
	 * @param test_case
	 * @throws Exception
	 */
	private void write_sym_instance_graph(SymInstanceGraph graph, TestInput test_case, Collection<SymNode> nodes) throws Exception {
		Collection<List<SymInstanceEdge>> paths = graph.select_reachable_paths();
		int test_id = -1;
		if(test_case != null) test_id = test_case.get_id();
		for(List<SymInstanceEdge> path : paths) {
			writer.write(graph.get_mutant().get_id() + "\t" + test_id);
			this.write_sym_instance_path(path, nodes);
			writer.write("\n");
		}
	}
	/**
	 * mid tid res node edge node edge ... edge node*
	 * @param test_case
	 * @throws Exception
	 */
	private void write_sym_instance_graphs(
			TestInput test_case, CDependGraph dependence_graph, 
			int max_distance, Collection<SymNode> nodes) throws Exception {
		MuTestProjectTestSpace tspace = this.source.get_code_space().get_project().get_test_space();
		CStatePath state_path = tspace.load_instrumental_path(this.source.get_sizeof_template(), 
							this.source.get_ast_tree(), this.source.get_cir_tree(), test_case);
		if(state_path != null) {
			this.open("." + test_case.get_id() + ".dft");
			for(Mutant mutant : this.source.get_mutant_space().get_mutants()) {
				MuTestProjectTestResult result = tspace.get_test_result(mutant);
				if(result == null) {
					continue;
				}
				SymInstanceGraph graph = SymInstanceGraph.new_graph(dependence_graph, mutant, max_distance);
				graph.evaluate(state_path);
				this.write_sym_instance_graph(graph, test_case, nodes);
			}
			this.close();
		}
	}
	/**
	 * 
	 * @param dependence_graph
	 * @param max_distance
	 * @throws Exception
	 */
	private void write_sym_instance_graphs(CDependGraph dependence_graph, int max_distance, Collection<SymNode> nodes) throws Exception {
		MuTestProjectTestSpace tspace = this.source.get_code_space().get_project().get_test_space();
		this.open(".sft");
		for(Mutant mutant : this.source.get_mutant_space().get_mutants()) {
			MuTestProjectTestResult result = tspace.get_test_result(mutant);
			if(result == null) {
				continue;
			}
			
			SymInstanceGraph graph = SymInstanceGraph.new_graph(dependence_graph, mutant, max_distance);
			graph.evaluate();
			this.write_sym_instance_graph(graph, null, nodes);
		}
		this.close();
	}
	/**
	 * xxx.sym, xxx.0.sym, ..., xxx.n.sym
	 * @param max_distance
	 * @throws Exception
	 */
	public void write_features(int max_distance, Collection<TestInput> test_suite) throws Exception {
		CirFunction root_function = source.get_cir_tree().get_function_call_graph().get_main_function();
		CDependGraph dependence_graph = CDependGraph.graph(CirCallContextInstanceGraph.graph(root_function, 
				CirFunctionCallPathType.unique_path, -1));
		this.write_stat(dependence_graph);
		
		Set<SymNode> sym_nodes = new HashSet<SymNode>();
		this.write_sym_instance_graphs(dependence_graph, max_distance, sym_nodes);
		if(test_suite != null && !test_suite.isEmpty()) {
			for(TestInput test_case : test_suite) 
				this.write_sym_instance_graphs(test_case, dependence_graph, max_distance, sym_nodes);
		}
		this.write_sym_nodes(sym_nodes);
	}
	
}
