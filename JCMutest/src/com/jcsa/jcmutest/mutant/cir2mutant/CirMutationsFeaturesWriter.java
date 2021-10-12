package com.jcsa.jcmutest.mutant.cir2mutant;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcmutest.mutant.cir2mutant.tree.CirAnnotation;
import com.jcsa.jcmutest.mutant.cir2mutant.tree.CirAnnotationClass;
import com.jcsa.jcmutest.mutant.cir2mutant.tree.CirAnnotationNode;
import com.jcsa.jcmutest.mutant.cir2mutant.tree.CirAnnotationTree;
import com.jcsa.jcmutest.mutant.cir2mutant.tree.CirAttributeNode;
import com.jcsa.jcmutest.mutant.cir2mutant.tree.CirAttributeTree;
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
 * It implements the generation of feature information from MuTestProjectCodeFile
 * to produce features related with CirMutation(s) of each mutant and annotations
 * for killing them in symbolic executions.
 * <br>
 * 
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
 * 	|	xxx.ant	|	Tree structure of annotations based on subsumption.	|	<br>
 * 	|	xxx.sym	|	Symbolic nodes generated in evaluation				|	<br>
 * 	|	xxx.stp	|	CirInfectionEdge(s) by leaf-root path evaluated.	|	<br>
 * 	+-------------------------------------------------------------------+	<br>
 * </code>
 * 
 * <br>
 * @author yukimula
 *
 */
public class CirMutationsFeaturesWriter {
	
	/* definitions */
	/** the input source code file for creating features **/
	private MuTestProjectCodeFile 	inputs_file;
	/** the directory where feature files were generated **/
	private File 					o_directory;
	/** it is used to output string text to current file **/
	private FileWriter 				file_writer;
	/** set to preserve symbolic nodes used for features **/
	private Set<SymbolNode> 		symbol_vexs;
	/** the set of annotations produced for feature file **/
	private Set<CirAnnotation>		annotations;
	/** the set of attributes to denote a program state **/
	private Set<CirAttribute>		attributes;
	/** the maximal times of evaluation on state infection **/
	private int						meval_times;
	
	/* singleton mode */ 
	/**
	 * Private constructor of singleton mode
	 */
	private CirMutationsFeaturesWriter() {
		this.inputs_file = null;
		this.o_directory = null;
		this.file_writer = null;
		this.symbol_vexs = new HashSet<SymbolNode>();
		this.attributes  = new HashSet<CirAttribute>();
		this.annotations = new HashSet<CirAnnotation>();
		this.meval_times = 0;
	}
	private static CirMutationsFeaturesWriter writer = new CirMutationsFeaturesWriter();
	
	/* inputs-output methods */
	/**
	 * It closes the file output stream to files
	 * @throws Exception
	 */
	private void close() throws Exception {
		if(this.file_writer != null) {
			this.file_writer.close();
			this.file_writer = null;
		}
	}
	/**
	 * It opens the output stream to a specified file
	 * @param postfix
	 * @throws Exception
	 */
	private void open(String postfix) throws Exception {
		/* 1. extract the source code file name */
		String file_name; int index; 
		file_name = this.inputs_file.get_name(); 
		index = file_name.indexOf('.');
		file_name = file_name.substring(0, index).trim();
		
		/* 2. get the output file with specified postfix */
		File output_file;
		output_file = new File(this.o_directory.getAbsolutePath() + "/" + file_name + postfix);
		System.out.println("\t==> Start to Write on " + output_file.getAbsolutePath() + "...");
		
		/* 3. close the output stream and re-open it */
		this.close(); this.file_writer = new FileWriter(output_file);
	}
	/**
	 * It resets the inputs and output location in the given evaluation times
	 * @param inputs
	 * @param output_directory
	 * @param max_evaluation_times
	 * @throws Exception
	 */
	private void reset(MuTestProjectCodeFile inputs, File 
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
			if(!output_directory.exists()) {
				FileOperations.mkdir(output_directory);
			}
			this.inputs_file = inputs;
			this.o_directory = output_directory;
			this.close();
			this.meval_times = max_evaluation_times;
		}
	}
	
	/* basic methods */
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
	 * @return BASIC: 	n@null, b@bool, c@char, i@int, f@real, x@r@i, s@txt;
	 * @throws Exception
	 */
	private String encode_base_object(Object token) throws Exception {
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
			return this.encode_base_object(((CConstant) token).get_object());
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
	private String encode_code_object(Object token) throws Exception {
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
		else {
			return null;
		}
	}
	/**
	 * @param token
	 * @return	FEATU:	sym@txt@int, mut@int, tst@int;
	 * 			WORDS:	logic_type$execution$store_unit$symb_value;
	 * @throws Exception
	 */
	private String encode_test_object(Object token) throws Exception {
		if(token instanceof Mutant) {
			return "mut@" + ((Mutant) token).get_id();
		}
		else if(token instanceof TestInput) {
			return "tst@" + ((TestInput) token).get_id();
		}
		else if(token instanceof CirAnnotation) {
			CirAnnotation annotation = (CirAnnotation) token;
			if(!this.annotations.contains(annotation)) {
				this.annotations.add(annotation); 
			}
			
			String logic_type = annotation.get_logic_type().toString();
			String execution = this.encode_token(annotation.get_execution());
			String store_unit = this.encode_token(annotation.get_store_unit());
			String symb_value = this.encode_token(annotation.get_symb_value());
			return String.format("%s$%s$%s$%s", logic_type, execution, store_unit, symb_value);
		}
		else if(token instanceof CirAttribute) {
			CirAttribute attribute = (CirAttribute) token;
			if(!this.attributes.contains(attribute)) {
				this.attributes.add(attribute);
			}
			
			String logic_type;
			switch(attribute.get_type()) {
			case condition:	logic_type = CirAnnotationClass.eva_expr.toString(); 	break;
			case cov_count:	logic_type = CirAnnotationClass.cov_stmt.toString(); 	break;
			case dif_error:	logic_type = CirAnnotationClass.mut_expr.toString(); 	break;
			case val_error:	logic_type = CirAnnotationClass.mut_expr.toString(); 	break;
			case ref_error:	logic_type = CirAnnotationClass.mut_expr.toString(); 	break;
			case sta_error:	logic_type = CirAnnotationClass.mut_stat.toString(); 	break;
			case blk_error:	logic_type = CirAnnotationClass.mut_stmt.toString(); 	break;
			case flw_error:	logic_type = CirAnnotationClass.mut_flow.toString(); 	break;
			case trp_error:	logic_type = CirAnnotationClass.trp_stmt.toString(); 	break;
			default:		logic_type = attribute.get_type().toString(); 			break;
			}
			String execution = this.encode_token(attribute.get_execution());
			String store_unit = this.encode_token(attribute.get_location());
			String symb_value = this.encode_token(attribute.get_parameter());
			return String.format("%s$%s$%s$%s", logic_type, execution, store_unit, symb_value);
		}
		else if(token instanceof SymbolNode) {
			if(!this.symbol_vexs.contains(token)) {
				this.symbol_vexs.add((SymbolNode) token);
			}
			return "sym@" + token.getClass().getSimpleName().substring(6).trim() + "@" + token.hashCode();
		}
		else {
			return null;
		}
	}
	/** 
	 * @param token
	 * @return 	BASIC: 	n@null, b@bool, c@char, i@int, f@real, x@r@i, s@txt;
	 * 			ENUMS:	key@keyword, opr@operator, pun@punctuator, typ@type;
	 * 			OBJEC:	ast@int, cir@int, exe@txt@int, ins@txt@int@int;
	 * 			FEATU:	sym@txt@int, mut@int, tst@int;
	 * 			WORDS:	logic_type$execution$store_unit$symb_value;
	 * @throws Exception
	 */
	private String encode_token(Object token) throws Exception {
		String text = this.encode_base_object(token);
		if(text == null) {
			text = this.encode_code_object(token);
			if(text == null) {
				text = this.encode_test_object(token);
				if(text == null) {
					throw new IllegalArgumentException("Unsupport: " + token);
				}
			}
		}
		return text;
	}
	/**
	 * @param tree
	 * @return whether the tree can be printed on output stream
	 * @throws Exception
	 */
	private boolean is_available(CirAttributeTree tree) throws Exception {
		MuTestProjectTestSpace tspace = this.inputs_file.get_code_space().get_project().get_test_space();
		if(tree == null) {
			return false;
		}
		else if(tspace.get_test_result(tree.get_mutant()) == null) {
			return false;
		}
		else {
			return tree.has_cir_mutations();
		}
	}
	/**
	 * @param test_cases
	 * @return arrange each test case in set to the corresponding mutants it needs be executed on
	 * @throws Exception
	 */
	private Map<TestInput, Collection<Mutant>> partition_mutants_by_tests(Collection<TestInput> test_cases) throws Exception {
		/* perform executions on instrumental version */
		MuTestProjectTestSpace tspace = this.inputs_file.get_code_space().get_project().get_test_space();
		Map<TestInput, Collection<Mutant>> maps = new HashMap<TestInput, Collection<Mutant>>();
		this.inputs_file.get_code_space().get_project().execute_instrumental(test_cases);
		
		/* mapping each test to corresponding mutants */
		for(TestInput test_case : test_cases) {
			/* create the mutation buffer to preserve available ones */
			Collection<Mutant> mutants = new ArrayList<Mutant>();
			
			for(Mutant mutant : this.inputs_file.get_mutant_space().get_mutants()) {
				/* 1. capture the strong, weak and coverage execution results */
				MuTestProjectTestResult s_result = tspace.get_test_result(mutant);
				MuTestProjectTestResult w_result = tspace.get_test_result(mutant.get_weak_mutant());
				MuTestProjectTestResult c_result = tspace.get_test_result(mutant.get_coverage_mutant());
				
				/* 2. determine whether to include the mutation for that test */
				if(s_result == null) {	continue; }			/** ignore the untested mutation **/
				else if(s_result != null && s_result.get_kill_set().get(test_case.get_id())) {
					mutants.add(mutant);					/** add mutant when it is killed **/
				}
				else if(w_result != null && w_result.get_kill_set().get(test_case.get_id())) {
					mutants.add(mutant);					/** add mutant when it is infected **/
				}
				else if(c_result != null && c_result.get_kill_set().get(test_case.get_id())) {
					mutants.add(mutant);					/** add mutant at least when it is reached **/
				}
				else { continue; }							/** ignore the un-covered mutation **/
			}
			
			/* update the mapping from test case to its available mutations */
			if(!mutants.isEmpty()) { maps.put(test_case, mutants); }
		}
		return maps;
	}
	
	/* static code information */
	/**
	 * xxx.c
	 * @throws Exception
	 */
	private void write_cpp() throws Exception {
		this.open(".c");
		FileReader reader = new FileReader(this.inputs_file.get_ifile());
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
		AstTree ast_tree = this.inputs_file.get_ast_tree();
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
		for(CirNode node : this.inputs_file.get_cir_tree().get_nodes()) {
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
		for(CirFunction function : this.inputs_file.get_cir_tree().get_function_call_graph().get_functions()) {
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
	
	/* dependence model information */
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
		MuTestProjectTestSpace tspace = this.inputs_file.get_code_space().get_project().get_test_space();
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
		for(Mutant mutant : this.inputs_file.get_mutant_space().get_mutants()) {
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
		MuTestProjectTestSpace tspace = this.inputs_file.get_code_space().get_project().get_test_space();
		for(Mutant mutant : this.inputs_file.get_mutant_space().get_mutants()) {
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
	
	/* symbolic annotation features */
	/**
	 * collect all the symbolic nodes under the node into the collection
	 * @param node
	 * @param nodes
	 */
	private void get_sym_nodes_in(SymbolNode node, Collection<SymbolNode> nodes) {
		nodes.add(node);
		for(SymbolNode child : node.get_children()) {
			this.get_sym_nodes_in(child, nodes);
		}
	}
	/**
	 * @return the set of all symbolic nodes incorporated within
	 * @throws Exception
	 */
	private Collection<SymbolNode> get_sym_nodes() throws Exception {
		Set<SymbolNode> nodes = new HashSet<SymbolNode>();
		for(SymbolNode node : this.symbol_vexs) {
			this.get_sym_nodes_in(node, nodes);
		}
		return nodes;
	}
	/**
	 * ID class source{Ast|Cir|Exe|Null|Const} data_type content code [child*]
	 * @param node
	 * @throws Exception
	 */
	private void	write_sym_node(SymbolNode node) throws Exception {
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
	/**
	 * write the symbolic expression nodes in this.sym_nodes to writer and output the number
	 * of symbolic expression nodes being printed to output file.
	 * @return
	 * @throws Exception
	 */
	private int 	write_sym_nodes() throws Exception {
		Collection<SymbolNode> nodes = this.get_sym_nodes();
		for(SymbolNode node : nodes) {
			this.write_sym_node(node);
		}
		return nodes.size();
	}
	/**
	 * write the symbolic nodes into xxx.sym
	 * @return
	 * @throws Exception
	 */
	private int 	write_sym() throws Exception {
		this.open(".sym");
		int number = this.write_sym_nodes();
		this.close();
		return number;
	}
	/**
	 * parent {child}*
	 * @param node
	 * @throws Exception
	 */
	private void 	write_ant_node(CirAnnotationNode node) throws Exception {
		this.file_writer.write(this.encode_token(node.get_annotation()));
		for(CirAnnotationNode child : node.get_children()) {
			this.file_writer.write("\t");
			this.file_writer.write(this.encode_token(child.get_annotation()));
		}
		this.file_writer.write("\n");
	}
	/**
	 * [parent {child}* \n]+
	 * @param tree
	 * @throws Exception
	 */
	private void	write_ant_tree(CirAnnotationTree tree) throws Exception {
		for(CirAnnotationNode node : tree.get_nodes()) {
			this.write_ant_node(node);
		}
	}
	/**
	 * xxx.ant
	 * @throws Exception
	 */
	private void 	write_ant() throws Exception {
		this.open(".ant");
		
		/* 1. attributes */
		for(CirAttribute attribute : this.attributes) {
			this.file_writer.write(this.encode_token(attribute) + "\n");
		}
		
		/* 2. annotations and their subsumption */
		CirAnnotationTree tree = new CirAnnotationTree();
		for(CirAnnotation annotation : this.annotations) {
			tree.extend_from(annotation);
		}
		this.write_ant_tree(tree);
		
		this.close();
	}
	/**
	 * (\t attribute) (\t annotation)+ (\t ;)
	 * @param node
	 * @return
	 * @throws Exception
	 */
	private int		write_cir_attribute_node(CirAttributeNode node) throws Exception {
		int number_of_words = 0;
		
		this.file_writer.write("\t" + this.encode_token(node.get_attribute()));
		if(node.get_data().is_executed()) {
			for(CirAnnotation annotation : node.get_data().get_abs_annotations()) {
				this.file_writer.write("\t" + this.encode_token(annotation));
				number_of_words++;
			}
		}
		this.file_writer.write("\t;");
		
		return number_of_words;
	}
	/**
	 * mid tid {(\t attribute) (\t annotation)+ (\t ;)}+ \n
	 * @param leaf
	 * @param test
	 * @return [number_of_words, number_of_nodes, number_of_paths]
	 * @throws Exception
	 */
	private int[]	write_cir_attribute_path(CirAttributeNode leaf, TestInput test) throws Exception {
		/* mid tid */
		String mid = this.encode_token(leaf.get_tree().get_mutant());
		String tid = this.encode_token(test);
		this.file_writer.write(mid + "\t" + tid);
		
		/* {(\t attribute) (\t annotation)* (\t ;)}+ */
		int number_of_words = 0;
		int number_of_nodes = 0;
		int number_of_lines = 0;
		for(CirAttributeNode node : leaf.get_pred_nodes()) {
			int word_number = this.write_cir_attribute_node(node);
			if(word_number > 0) {
				number_of_words += word_number;
				number_of_nodes += 1;
			}
		}
		
		/* new line */
		this.file_writer.write("\n");
		number_of_lines += 1;
		
		/* return [number_of_words, number_of_nodes, number_of_lines] */
		return new int[] { number_of_words, number_of_nodes, number_of_lines };
	}
	/**
	 * {mid tid {(\t attribute) (\t annotation)* (\t ;)}+ \n}+
	 * @param tree
	 * @param test
	 * @return [number_of_words, number_of_nodes, number_of_lines, number_of_trees]
	 * @throws Exception
	 */
	private int[]	write_cir_attribute_tree(CirAttributeTree tree, TestInput test) throws Exception {
		/* declarations of the counting variables */
		int number_of_words = 0, number_of_nodes = 0, number_of_lines = 0, number_of_trees = 0;
		
		/* write the tree information only when the tree is available for test */
		if(this.is_available(tree)) {
			tree.sum_states();	/* summarize for generating annotations */
			
			/* write for each root-leaf path a line in the xxx.stp */
			for(CirAttributeNode leaf : tree.get_leafs()) {
				/* write only one line for the node-list in the tree */
				int[] words_nodes_lines = this.write_cir_attribute_path(leaf, test);
				number_of_words += words_nodes_lines[0];
				number_of_nodes += words_nodes_lines[1];
				number_of_lines += words_nodes_lines[2];
			}
			number_of_trees++;
		}
		
		/* return */	
		return new int[] { number_of_words, number_of_nodes, number_of_lines, number_of_trees };
	}
	/**
	 * 
	 * @param dependence_graph
	 * @param test
	 * @throws Exception
	 */
	private	void	write_cir_attribute_trees(CDependGraph dependence_graph, Collection<TestInput> test_cases) throws Exception {
		/* declarations */
		int number_of_words = 0, number_of_nodes = 0;
		int number_of_lines = 0, number_of_trees = 0;
		Set<Mutant> mutants = new HashSet<Mutant>( );
		int[] words_nodes_lines_trees;
		
		/* static analysis */
		if(test_cases == null || test_cases.isEmpty()) {
			for(Mutant mutant : this.inputs_file.get_mutant_space().get_mutants()) {
				CirAttributeTree tree = CirAttributeTree.new_tree(mutant, dependence_graph);
				tree.add_states(null, this.meval_times, null);
				words_nodes_lines_trees = this.write_cir_attribute_tree(tree, null);
				number_of_words += words_nodes_lines_trees[0];
				number_of_nodes += words_nodes_lines_trees[1];
				number_of_lines += words_nodes_lines_trees[2];
				number_of_trees += words_nodes_lines_trees[3];
				if(words_nodes_lines_trees[3] > 0) { mutants.add(tree.get_mutant()); }
			}
		}
		/* dynamic analysis */
		else {
			/* Fetch the mapping from each test to available mutations and generate paths */
			Map<TestInput, Collection<Mutant>> maps = this.partition_mutants_by_tests(test_cases);
			int proceed_counter = 0, number_of_tests = maps.size();
			
			/* Dynamically analyze the mutations for each test input in given collections */
			for(TestInput test : maps.keySet()) {
				/* I. Obtain the execution path with state from */
				MuTestProjectTestSpace tspace = this.inputs_file.get_code_space().get_project().get_test_space();
				CStatePath state_path = tspace.load_instrumental_path(this.inputs_file.get_sizeof_template(), 
						this.inputs_file.get_ast_tree(), this.inputs_file.get_cir_tree(), test); proceed_counter++;
				if(state_path == null) { continue; }	/** ignore the test if it cannot be instrumented analysis **/
				System.out.println(String.format("\t\t==> Proceeding [%d/%d]", proceed_counter, number_of_tests));
				
				/* II. construct the state infection tree for each available mutation */
				Collection<CirAttributeTree> trees = new ArrayList<CirAttributeTree>();
				for(Mutant mutant : maps.get(test)) {
					CirAttributeTree tree = CirAttributeTree.new_tree(mutant, state_path);
					if(this.is_available(tree)) { trees.add(tree); }
				}
				
				/* III. perform dynamic analysis for evaluating the state infection tree */
				SymbolProcess context = new SymbolProcess(
								this.inputs_file.get_ast_tree(), this.inputs_file.get_cir_tree());
				for(CStateNode state_node : state_path.get_nodes()) {
					context.accumulate(state_node);
					for(CirAttributeTree tree : trees) {
						tree.add_states(state_node.get_execution(), this.meval_times, context);
					}
				}
				
				/* IV. print each tree's nodes onto the lines in the xxx.stn or xxx.stp */
				for(CirAttributeTree tree : trees) {
					words_nodes_lines_trees = this.write_cir_attribute_tree(tree, test);
					number_of_words += words_nodes_lines_trees[0];
					number_of_nodes += words_nodes_lines_trees[1];
					number_of_lines += words_nodes_lines_trees[2];
					number_of_trees += words_nodes_lines_trees[3];
					if(words_nodes_lines_trees[3] > 0) { mutants.add(tree.get_mutant()); }
				}
			}
		}
		
		/* reporting information to debugging */
		System.out.println(String.format("\t\t--> %d word, %d node, %d path, %d tree, %d/%d mutant, %d expr", 
				number_of_words, number_of_nodes, number_of_lines, number_of_trees, 
				mutants.size(), this.inputs_file.get_mutant_space().size(), this.symbol_vexs.size()));
	}
	/**
	 * xxx.stp
	 * @param dependence_graph
	 * @param test_cases
	 * @throws Exception
	 */
	private void	write_stp(CDependGraph dependence_graph, Collection<TestInput> test_cases) throws Exception {
		this.open(".stp");
		this.write_cir_attribute_trees(dependence_graph, test_cases);
		this.close();
	}
	/**
	 * xxx.stp xxx.sym xxx.ant
	 * @param dependence_graph
	 * @param test_cases
	 * @throws Exception
	 */
	private void	write_symb_features(CDependGraph dependence_graph, Collection<TestInput> test_cases) throws Exception {
		this.symbol_vexs.clear();
		this.annotations.clear();
		this.attributes.clear();
		
		this.write_stp(dependence_graph, test_cases);
		this.write_ant();
		this.write_sym();
		
		this.attributes.clear();
		this.annotations.clear();
		this.symbol_vexs.clear();
	}
	
	/* public writer interfaces */
	/**
	 * @param input
	 * @param output_directory
	 * @param max_evaluation_times
	 * @param test_cases
	 * @throws Exception
	 */
	public static void write_features(MuTestProjectCodeFile input, File output_directory, 
			int max_evaluation_times, Collection<TestInput> test_cases) throws Exception {
		/* 1. initialize the writer and generate dependence graph for printing  */	
		writer.reset(input, output_directory, max_evaluation_times);
		CirFunction root_function =
					input.get_cir_tree().get_function_call_graph().get_main_function();
		CDependGraph dependence_graph = CDependGraph.graph(CirCallContextInstanceGraph.
						graph(root_function, CirFunctionCallPathType.unique_path, -1));
		
		/* 2. write the feature files onto specified output-directory */
		writer.write_code_features();
		writer.write_test_features();
		writer.write_flow_features(dependence_graph);
		writer.write_symb_features(dependence_graph, test_cases);
	}
	
}
