package com.jcsa.jcmutest.project;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirAnnotateType;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirAnnotation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymExpressionError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymFlowError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymInstance;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymReferenceError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymStateValueError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymTrapError;
import com.jcsa.jcmutest.mutant.cir2mutant.path.MutantKillingPath;
import com.jcsa.jcmutest.mutant.cir2mutant.path.MutantKillingState;
import com.jcsa.jcmutest.mutant.cir2mutant.path.SymInstanceEdge;
import com.jcsa.jcmutest.mutant.cir2mutant.path.SymInstanceGraph;
import com.jcsa.jcmutest.mutant.cir2mutant.path.SymInstanceNode;
import com.jcsa.jcmutest.mutant.cir2mutant.path.SymInstanceNodeType;
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
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
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
	/** it preserves the set of symbolic nodes used to define parameters in symbolic features **/
	private Set<SymbolNode> sym_nodes;
	/**
	 * category of symbolic feature in mutant execution.
	 * 
	 * @author yukimula
	 *
	 */
	protected static enum SymConditionCategory {
		/** it refers to a context-constraint for being satisfied **/	
		satisfaction,
		/** it refers to a infected state in context for observed **/
		observations,
	}
	
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
			this.sym_nodes = new HashSet<SymbolNode>();
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
		else if(token instanceof SymbolNode) {
			SymbolNode node = (SymbolNode) token;
			return "sym@" + node.getClass().getSimpleName().substring(6) + "@" + node.hashCode();
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
	
	/* xxx.sft, xxx.dft, xxx.sym */
	/**
	 * category$operator$validate$execution$location$parameter
	 * --> to record the symbolic expression of parameter into local buffer
	 * 
	 * @param condition
	 * @throws Exception
	 */
	private void write_sym_condition(Object condition) throws Exception {
		/* 1. declarations */
		SymConditionCategory category;
		CirAnnotateType operator;
		Boolean validate;
		CirExecution execution; 
		CirNode location;
		SymbolExpression parameter;
		
		/* Case-1. satisfaction$eval_stmt$validate$execution$statement$expression */
		if(condition instanceof SymConstraint) {
			category = SymConditionCategory.satisfaction;
			operator = CirAnnotateType.eval_stmt;
			validate = ((SymConstraint) condition).validate(null);
			execution = ((SymConstraint) condition).get_execution();
			location = execution.get_statement();
			parameter = ((SymConstraint) condition).get_condition();
		}
		/* Case-2. observation$mut_value$validate$execution$expression$mut_expr */
		else if(condition instanceof SymExpressionError) {
			category = SymConditionCategory.observations;
			operator = CirAnnotateType.mut_value;
			validate = ((SymExpressionError) condition).validate(null);
			execution = ((SymExpressionError) condition).get_execution();
			location = ((SymExpressionError) condition).get_expression();
			parameter = ((SymExpressionError) condition).get_mutation_value();
		}
		/* Case-3. observation$mut_refer$validate$execution$expression$mut_expr */
		else if(condition instanceof SymReferenceError) {
			category = SymConditionCategory.observations;
			operator = CirAnnotateType.mut_refer;
			validate = ((SymReferenceError) condition).validate(null);
			execution = ((SymReferenceError) condition).get_execution();
			location = ((SymReferenceError) condition).get_expression();
			parameter = ((SymReferenceError) condition).get_mutation_value();
		}
		/* Case-4. observation$mut_state$validate$execution$expression$mut_expr */
		else if(condition instanceof SymStateValueError) {
			category = SymConditionCategory.observations;
			operator = CirAnnotateType.mut_state;
			validate = ((SymStateValueError) condition).validate(null);
			execution = ((SymStateValueError) condition).get_execution();
			location = ((SymStateValueError) condition).get_expression();
			parameter = ((SymStateValueError) condition).get_mutation_value();
		}
		/* Case-4. observation$trap_error$True$execution$statement$null */
		else if(condition instanceof SymTrapError) {
			category = SymConditionCategory.observations;
			operator = CirAnnotateType.trap_stmt;
			validate = Boolean.TRUE;
			execution = ((SymTrapError) condition).get_execution();
			location = execution.get_statement();
			parameter = null;
		}
		/* Case-5. observation$mut_flow$True$execution$statement$target_stmt */
		else if(condition instanceof SymFlowError) {
			category = SymConditionCategory.observations;
			operator = CirAnnotateType.mut_flow;
			validate = ((SymFlowError) condition).validate(null);
			execution = ((SymFlowError) condition).get_execution();
			location = ((SymFlowError) condition).get_original_flow().get_source().get_statement();
			parameter = SymbolFactory.sym_expression(((SymFlowError) condition).get_mutation_flow().get_target());
		}
		/* Case-6. category$type$validate$execution$location$parameter */
		else if(condition instanceof CirAnnotation) {
			CirAnnotation annotation = (CirAnnotation) condition;
			validate = Boolean.TRUE;
			switch(annotation.get_type()) {
			case covr_stmt:
			case eval_stmt:
						category = SymConditionCategory.satisfaction;	break;
			default: 	category = SymConditionCategory.observations;	break;
			}
			operator = annotation.get_type();
			execution = annotation.get_execution();
			location = annotation.get_location();
			parameter = (SymbolExpression) annotation.get_parameter();
		}
		/* Otherwise, the class of the condition is not supported to be used */
		else {
			throw new IllegalArgumentException("Invalid class: " + condition.getClass().getSimpleName());
		}
		
		/* 3. output the condition to file stream writer */
		this.writer.write(category + "$" + operator);
		this.writer.write("$" + this.token_string(validate));
		this.writer.write("$" + this.token_string(execution));
		this.writer.write("$" + this.token_string(location));
		this.writer.write("$" + this.token_string(parameter));
		if(parameter != null) this.sym_nodes.add(parameter);
	}
	/**
	 * improved_constraints + annotations
	 * @param edge
	 * @throws Exception
	 */
	private void write_sym_instance_edge(SymInstanceEdge edge) throws Exception {
		CirMutations cir_mutations = edge.get_source().get_graph().get_cir_mutations();
		SymConstraint constraint = edge.get_constraint(); 
		constraint = cir_mutations.optimize(constraint, null);
		Collection<SymConstraint> constraints = cir_mutations.improve_constraints(constraint);
		/* improved_constraint* & const_annotation* */
		for(SymConstraint improved_constraint : constraints) {
			writer.write("\t");
			this.write_sym_condition(improved_constraint);
		}
		for(CirAnnotation annotation : edge.get_status().get_cir_annotations()) {
			writer.write("\t");
			this.write_sym_condition(annotation);
		}
	}
	/**
	 * error + annotations
	 * @param node
	 * @throws Exception
	 */
	private void write_sym_instance_node(SymInstanceNode node) throws Exception {
		/* error error_annotation */
		if(node.has_state_error()) {
			SymStateError state_error = node.get_state_error();
			state_error = node.get_graph().get_cir_mutations().optimize(state_error, null);
			writer.write("\t"); 
			this.write_sym_condition(state_error);
			for(CirAnnotation annotation : node.get_status().get_cir_annotations()) {
				writer.write("\t");
				this.write_sym_condition(annotation);
			}
		}
	}
	/**
	 * mid tid condition*
	 * @param mutant
	 * @param test
	 * @param path
	 * @throws Exception
	 */
	private void write_sym_instance_path(Mutant mutant, TestInput test, List<SymInstanceEdge> path) throws Exception {
		int mid = mutant.get_id();
		int tid = (test == null) ? -1 : test.get_id();
		this.writer.write(mid + "\t" + tid);
		if(!path.isEmpty()) {
			this.write_sym_instance_node(path.get(0).get_source());
			for(SymInstanceEdge edge : path) {
				this.write_sym_instance_edge(edge);
				this.write_sym_instance_node(edge.get_target());
			}
		}
		this.writer.write("\n");
	}
	/**
	 * [mid tid path_conditions]+
	 * @param graph
	 * @param test
	 * @throws Exception
	 */
	private void write_sym_instance_graph(SymInstanceGraph graph, TestInput test) throws Exception {
		Collection<List<SymInstanceEdge>> paths = graph.select_reachable_paths();
		for(List<SymInstanceEdge> path : paths) {
			this.write_sym_instance_path(graph.get_mutant(), test, path);
		}
	}
	/**
	 * @param dependence_graph
	 * @param max_distance
	 * @param test
	 * @throws Exception
	 */
	private void write_sym_instance_graphs(CDependGraph dependence_graph, int max_distance, TestInput test) throws Exception {
		MuTestProjectTestSpace tspace = this.source.get_code_space().get_project().get_test_space();
		/* static analysis */
		if(test == null) {
			this.open(".sft");
			for(Mutant mutant : this.source.get_mutant_space().get_mutants()) {
				MuTestProjectTestResult result = tspace.get_test_result(mutant);
				if(result == null) { continue; }
				
				SymInstanceGraph graph = SymInstanceGraph.new_graph(dependence_graph, mutant, max_distance);
				graph.evaluate();
				this.write_sym_instance_graph(graph, null);
			}
			this.close();
		}
		/* dynamic analysis */
		else {
			CStatePath state_path = tspace.load_instrumental_path(this.source.get_sizeof_template(), 
									this.source.get_ast_tree(), this.source.get_cir_tree(), test);
			if(state_path != null) {
				this.open("." + test.get_id() + ".dft");
				for(Mutant mutant : this.source.get_mutant_space().get_mutants()) {
					MuTestProjectTestResult result = tspace.get_test_result(mutant);
					if(result == null) {
						continue;
					}
					SymInstanceGraph graph = SymInstanceGraph.new_graph(dependence_graph, mutant, max_distance);
					graph.evaluate(state_path);
					this.write_sym_instance_graph(graph, test);
				}
				this.close();
			}
		}
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
	/**
	 * xxx.sym, xxx.0.sym, ..., xxx.n.sym
	 * @param max_distance
	 * @throws Exception
	 */
	public void write_features(int max_distance, Collection<TestInput> test_suite) throws Exception {
		/* 0. initializations */
		this.sym_nodes.clear();
		
		/* 1. xxx.ins + xxx.dep */
		CirFunction root_function = source.get_cir_tree().get_function_call_graph().get_main_function();
		CDependGraph dependence_graph = CDependGraph.graph(CirCallContextInstanceGraph.graph(root_function, 
				CirFunctionCallPathType.unique_path, -1));
		this.write_stat(dependence_graph);
		
		/* 2. xxx.sft + xxx.y.dft */
		this.write_sym_instance_graphs(dependence_graph, max_distance, null);
		if(test_suite != null && !test_suite.isEmpty()) {
			for(TestInput test_case : test_suite) 
				this.write_sym_instance_graphs(dependence_graph, max_distance, test_case);
		}
		
		/* 3. xxx.sym */
		this.write_sym_nodes();
		this.sym_nodes.clear();
	}
	
	/* killing path as xxx.sft xxx.dft */
	/**
	 * @param mutant
	 * @param test
	 * @param path
	 * @throws Exception
	 */
	private void write_killing_paths(Mutant mutant, TestInput test, 
			MutantKillingPath path, CirMutations cir_mutations) throws Exception {
		int mid = mutant.get_id();
		int tid = (test == null) ? -1 : test.get_id();
		this.writer.write(mid + "\t" + tid);
		for(CirExecutionEdge edge : path.get_execution_edges()) {
			MutantKillingState state = (MutantKillingState) edge.get_annotation();
			for(SymInstance instance : state.get_instances()) {
				writer.write("\t");
				this.write_sym_condition(instance);
				if(instance instanceof SymConstraint) {
					Collection<SymConstraint> constraints = cir_mutations.
							improve_constraints((SymConstraint) instance);
					for(SymConstraint improved_constraint : constraints) {
						writer.write("\t");
						this.write_sym_condition(improved_constraint);
					}
				}
			}
			for(CirAnnotation annotation : state.get_annotations()) {
				writer.write("\t");
				this.write_sym_condition(annotation);
			}
		}
		this.writer.write("\n");
	}
	/**
	 * all the paths that pass through mutation
	 * @param graph
	 * @param test
	 * @param dependence_graph
	 * @throws Exception
	 */
	private void write_killing_paths(SymInstanceGraph graph, TestInput test, 
			CDependGraph dependence_graph) throws Exception {
		Collection<List<SymInstanceEdge>> sym_paths = graph.select_reachable_paths();
		
		for(List<SymInstanceEdge> sym_path : sym_paths) {
			CirExecution location = null;
			for(SymInstanceEdge sym_edge : sym_path) {
				if(sym_edge.get_source().get_type() == SymInstanceNodeType.muta_node) {
					location = sym_edge.get_source().get_execution();
					break;
				}
				location = sym_edge.get_target().get_execution();
			}
			
			Iterable<MutantKillingPath> kill_paths = MutantKillingPath.killing_paths(
							graph.get_mutant(), location, sym_path, dependence_graph, 
							graph.get_cir_mutations());
			for(MutantKillingPath kill_path : kill_paths) {
				this.write_killing_paths(graph.get_mutant(), test, kill_path, graph.get_cir_mutations());
			}
		}
	}
	/**
	 * @param dependence_graph
	 * @param max_distance
	 * @param test
	 * @throws Exception
	 */
	private void write_killing_paths(CDependGraph dependence_graph, int max_distance, TestInput test) throws Exception {
		MuTestProjectTestSpace tspace = this.source.get_code_space().get_project().get_test_space();
		/* static analysis */
		if(test == null) {
			this.open(".sft");
			for(Mutant mutant : this.source.get_mutant_space().get_mutants()) {
				MuTestProjectTestResult result = tspace.get_test_result(mutant);
				if(result == null) { continue; }
				
				SymInstanceGraph graph = SymInstanceGraph.new_graph(dependence_graph, mutant, max_distance);
				graph.evaluate();
				this.write_killing_paths(graph, null, dependence_graph);
			}
			this.close();
		}
		/* dynamic analysis */
		else {
			CStatePath state_path = tspace.load_instrumental_path(this.source.get_sizeof_template(), 
									this.source.get_ast_tree(), this.source.get_cir_tree(), test);
			if(state_path != null) {
				this.open("." + test.get_id() + ".dft");
				for(Mutant mutant : this.source.get_mutant_space().get_mutants()) {
					MuTestProjectTestResult result = tspace.get_test_result(mutant);
					if(result == null) {
						continue;
					}
					SymInstanceGraph graph = SymInstanceGraph.new_graph(dependence_graph, mutant, max_distance);
					graph.evaluate(state_path);
					this.write_killing_paths(graph, test, dependence_graph);
				}
				this.close();
			}
		}
	}
	/**
	 * xxx.sym, xxx.0.sym, ..., xxx.n.sym
	 * @param max_distance
	 * @param test_suite
	 * @throws Exception
	 */
	public void write_killing_paths(int max_distance, Collection<TestInput> test_suite) throws Exception {
		/* 0. initializations */
		this.sym_nodes.clear();
		
		/* 1. xxx.ins + xxx.dep */
		CirFunction root_function = source.get_cir_tree().get_function_call_graph().get_main_function();
		CDependGraph dependence_graph = CDependGraph.graph(CirCallContextInstanceGraph.graph(root_function, 
				CirFunctionCallPathType.unique_path, -1));
		this.write_stat(dependence_graph);
		
		/* 2. xxx.sft + xxx.y.dft */
		this.write_killing_paths(dependence_graph, max_distance, null);
		if(test_suite != null && !test_suite.isEmpty()) {
			for(TestInput test_case : test_suite) 
				this.write_killing_paths(dependence_graph, max_distance, test_case);
		}
		
		/* 3. xxx.sym */
		this.write_sym_nodes();
		this.sym_nodes.clear();
	}
	
}
