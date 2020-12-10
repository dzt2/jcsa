package com.jcsa.jcmutest.project;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirAnnotation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.path.SymInstanceEdge;
import com.jcsa.jcmutest.mutant.cir2mutant.path.SymInstanceGraph;
import com.jcsa.jcmutest.mutant.cir2mutant.path.SymInstanceNode;
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
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymNode;
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
	 * s@str [identifier, literal, cir_code]
	 * typ@str, sym@str, tst@str
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
		else if(token instanceof SymNode)
			return "sym@" + this.normalize_string(((SymNode) token).generate_code());
		else if(token instanceof TestInput)
			return "tst@" + ((TestInput) token).get_parameter();
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
	
	/* xxx.tst */ 
	/**
	 * ID tst@parameter
	 * @param test
	 * @throws Exception
	 */
	private void write_tst(TestInput test) throws Exception {
		this.writer.write(test.get_id() + "\t" + this.token_string(test) + "\n");
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
	
	/* xxx.sft|dft */
	/**
	 * constraint$execution$location$parameter
	 * @param constraint
	 * @throws Exception
	 */
	private void write_sym_word(SymConstraint constraint) throws Exception {
		this.writer.write("constraint");
		this.writer.write("$" + this.token_string(constraint.get_execution()));
		this.writer.write("$" + this.token_string(constraint.get_statement()));
		this.writer.write("$" + this.token_string(constraint.get_condition()));
	}
	/**
	 * type$execution$location$parameter
	 * @param annotation
	 * @throws Exception
	 */
	private void write_sym_word(CirAnnotation annotation) throws Exception {
		writer.write(annotation.get_type().toString());
		writer.write("$" + this.token_string(annotation.get_execution()));
		writer.write("$" + this.token_string(annotation.get_location()));
		writer.write("$");
		if(annotation.get_parameter() != null) {
			SymExpression parameter = (SymExpression) annotation.get_parameter();
			this.writer.write(this.token_string(parameter));
		}
		else {
			this.writer.write(this.token_string(null));
		}
	}
	/**
	 * constraint*
	 * @param edge
	 * @throws Exception
	 */
	private void write_sym_instance_edge(SymInstanceEdge edge) throws Exception {
		CirMutations cir_mutations = edge.get_source().get_graph().get_cir_mutations();
		SymConstraint constraint = edge.get_constraint(); 
		constraint = cir_mutations.optimize(constraint, null);
		Collection<SymConstraint> constraints = cir_mutations.improve_constraints(constraint);
		
		writer.write("\t");
		this.write_sym_word(edge.get_constraint());
		for(SymConstraint improved_constraint : constraints) {
			writer.write("\t");
			this.write_sym_word(improved_constraint);
		}
	}
	/**
	 * annotation+
	 * @param node
	 * @throws Exception
	 */
	private void write_sym_instance_node(SymInstanceNode node) throws Exception {
		if(node.has_state_error()) {
			for(CirAnnotation annotation : node.get_status().get_cir_annotations()) {
				writer.write("\t");
				this.write_sym_word(annotation);
			}
		}
	}
	/**
	 * cons error annotation ...
	 * @param path
	 * @throws Exception
	 */
	private void write_sym_instance_path(List<SymInstanceEdge> path) throws Exception {
		if(!path.isEmpty()) {
			this.write_sym_instance_node(path.get(0).get_source());
			for(SymInstanceEdge edge : path) {
				this.write_sym_instance_edge(edge);
				this.write_sym_instance_node(edge.get_target());
			}
		}
	}
	/**
	 * mid tid {cons|erro|anno}*
	 * @param graph
	 * @param test_case
	 * @throws Exception
	 */
	private void write_sym_instance_graph(SymInstanceGraph graph, TestInput test_case) throws Exception {
		Collection<List<SymInstanceEdge>> paths = graph.select_reachable_paths();
		int test_id = -1;
		if(test_case != null) test_id = test_case.get_id();
		for(List<SymInstanceEdge> path : paths) {
			writer.write(graph.get_mutant().get_id() + "\t" + test_id);
			this.write_sym_instance_path(path);
			writer.write("\n");
		}
	}
	/**
	 * mid tid res node edge node edge ... edge node*
	 * @param test_case
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private void write_sym_instance_graphs(TestInput test_case, CDependGraph dependence_graph, int max_distance) throws Exception {
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
				this.write_sym_instance_graph(graph, test_case);
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
	private void write_sym_instance_graphs(CDependGraph dependence_graph, int max_distance) throws Exception {
		MuTestProjectTestSpace tspace = this.source.get_code_space().get_project().get_test_space();
		this.open(".sft");
		for(Mutant mutant : this.source.get_mutant_space().get_mutants()) {
			MuTestProjectTestResult result = tspace.get_test_result(mutant);
			if(result == null) {
				continue;
			}
			
			SymInstanceGraph graph = SymInstanceGraph.new_graph(dependence_graph, mutant, max_distance);
			graph.evaluate();
			this.write_sym_instance_graph(graph, null);
		}
		this.close();
	}
	/**
	 * xxx.sym, xxx.0.sym, ..., xxx.n.sym
	 * @param max_distance
	 * @throws Exception
	 */
	public void write_features(int max_distance) throws Exception {
		CirFunction root_function = source.get_cir_tree().get_function_call_graph().get_main_function();
		CDependGraph dependence_graph = CDependGraph.graph(CirCallContextInstanceGraph.graph(root_function, 
				CirFunctionCallPathType.unique_path, -1));
		this.write_sym_instance_graphs(dependence_graph, max_distance);
		/*
		MuTestProjectTestSpace tspace = this.code_file.get_code_space().get_project().get_test_space();
		for(TestInput test_case : tspace.get_test_inputs()) {
			this.write_sym_instance_graphs(test_case, dependence_graph, max_distance);
		}
		*/
	}
	
}
