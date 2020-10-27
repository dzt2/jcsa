package com.jcsa.jcmutest.project;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutationUtils;
import com.jcsa.jcmutest.mutant.cir2mutant.CirStateErrorWord;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirExpressionError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirFlowError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirReferenceError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirStateValueError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirTrapError;
import com.jcsa.jcmutest.mutant.cir2mutant.graph.CirMutationGraph;
import com.jcsa.jcmutest.mutant.cir2mutant.graph.CirMutationNode;
import com.jcsa.jcmutest.mutant.cir2mutant.graph.CirMutationResult;
import com.jcsa.jcmutest.mutant.cir2mutant.graph.CirMutationTreeNode;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcparse.base.Complex;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.astree.base.AstIdentifier;
import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.AstTypeName;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstOperator;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CFunctionType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CQualifierType;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.CUnionType;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirConstExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirField;
import com.jcsa.jcparse.lang.irlang.expr.CirNameExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirType;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCall;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCallGraph;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabel;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.CKeyword;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.lexical.CPunctuator;
import com.jcsa.jcparse.lang.sym.SymArgumentList;
import com.jcsa.jcparse.lang.sym.SymBinaryExpression;
import com.jcsa.jcparse.lang.sym.SymCallExpression;
import com.jcsa.jcparse.lang.sym.SymConstant;
import com.jcsa.jcparse.lang.sym.SymField;
import com.jcsa.jcparse.lang.sym.SymFieldExpression;
import com.jcsa.jcparse.lang.sym.SymIdentifier;
import com.jcsa.jcparse.lang.sym.SymInitializerList;
import com.jcsa.jcparse.lang.sym.SymLabel;
import com.jcsa.jcparse.lang.sym.SymLiteral;
import com.jcsa.jcparse.lang.sym.SymNode;
import com.jcsa.jcparse.lang.sym.SymOperator;
import com.jcsa.jcparse.lang.sym.SymUnaryExpression;
import com.jcsa.jcparse.test.file.TestInput;
import com.jcsa.jcparse.test.state.CStatePath;


/**
 * 	Used to write the feature information for:<br>
 * 	<code>
 * 	<br>
 * 	+------------------------------------------------------------------------------+<br>
 * 	(0)	Element: {Boolean|Character|Short|Integer|Long|Float|Double|Complex|String}	<br>
 * 	(1)	CType | CConstant | SymNode	| CirConstraint | CirStateError	| CirMutation	<br>
 * 	+------------------------------------------------------------------------------+<br>
 * 	<br>
 * 	+------------------------------------------------------------------------------+<br>
 * 	(1)	CSourceCode: copy(ifile, xxx.c)												<br>
 * 	(2) AstTree: id class beg_index end_index type token children		{xxx.ast}	<br>
 * 	(3)	CirTree: id class ast_source type token children				{xxx.cir}	<br>
 * 	(4)	CirFunctionCallGraph:											{xxx.flw}	<br>
 * 		#BegFunc	name															<br>
 * 			#Exec exec_id cir_stmt			(+)										<br>
 * 			#Flow flow_type source target	(+)										<br>
 * 			#Call call_exec wait_exec		(*)										<br>
 * 		#EndFunc																	<br>
 * 	(5)	MutantSpace: 	id class operator location parameter			{xxx.mut}	<br>
 * 	(6)	TestInputs:	 	id: parameter									{xxx.tst}	<br>
 * 	+------------------------------------------------------------------------------+<br>
 * 	<br>
 * 	+------------------------------------------------------------------------------+<br>
 * 	(7)	CirMutationTrees:												{xxx.tre}	<br>
 * 		#BegTrees																	<br>
 * 			#muta	id																<br>
 * 			#path	constraint constraint constraint ... constraint					<br>
 * 			#node 	id constraint state_error [children]							<br>
 * 		#EndTrees																	<br>
 * 	(8)	CirMutationStatus:												{xxx.res}	<br>
 * 		#BegStatus	mid tid {0(alive); 1(killed); 2(unknown)}						<br>
 * 			#status	tree_id exec_times reject_cons accept_cons reject_stat
 * 					accept_stat [ error_word* ]	<br>
 * 		#EndStatus																	<br>
 * 	+------------------------------------------------------------------------------+<br>
 * 	<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *	
 */
public class MuTestFeatureWriter {
	
	/* definitions */
	/** file to be written **/
	private File output_file;
	/** file stream writer **/
	private FileWriter writer;
	/**
	 * create the writer to write code, mutation features.
	 */
	public MuTestFeatureWriter() { this.writer = null; }
	
	/* file access methods */
	/**
	 * @param directory
	 * @param cfile
	 * @param extension
	 * @return directory/{basename}.extension
	 * @throws Exception
	 */
	private File get_output_file(File directory, File cfile, String extension) throws Exception {
		if(directory == null || !directory.isDirectory())
			throw new IllegalArgumentException("Invalid directory: null");
		else if(cfile == null)
			throw new IllegalArgumentException("Invalid source c-file");
		else if(extension == null || extension.isBlank())
			throw new IllegalArgumentException("Invalid extension: of file");
		else {
			String name = cfile.getName();
			int index = name.lastIndexOf('.');
			if(index > 0) {
				name = name.substring(0, index);
			}
			return new File(directory.getAbsolutePath() + "/" + name.strip() + extension);
		}
	}
	/**
	 * open the new file stream for writing on output file
	 * @param file
	 * @throws Exception
	 */
	private void open_writer(File file) throws Exception {
		this.close_writer();
		this.writer = new FileWriter(file);
		this.output_file = file;
	}
	/**
	 * close the file stream writer
	 * @throws Exception
	 */
	private void close_writer() throws Exception {
		if(this.writer != null) {
			System.out.println("\t--> Write on " + this.output_file.getName());
			this.writer.close();
			this.writer = null;
			this.output_file = null;
		}
	}
	
	/* element + constant */
	/**
	 * 	null					--> n@none
	 * 	Boolean					--> b@true|false	
	 * 	Character				--> c@char_integer
	 * 	Short|Integer|Long		-->	i@number
	 * 	Float|Double			--> f@number
	 * 	String					--> s@text
	 * 	Complex					-->	x@number@number
	 * 	AstNode					--> a@ast_id
	 * 	CirNode					-->	r@cir_id
	 * 	CConstant				--> write_element(constant.object)
	 * 	CKeyword				--> k@keyword
	 * 	CPunctuator				-->	p@punctuator
	 * 	COperator				--> o@operator
	 * 	
	 * 	@param element
	 * 	@throws Exception
	 * 	
	 */
	private void write_element(Object element) throws Exception {
		if(element == null)
			writer.write("n@none");
		else if(element instanceof Boolean)
			writer.write("b@" + element);
		else if(element instanceof Character) 
			writer.write("c@" + ((int) ((Character) element).charValue()));
		else if(element instanceof Short || 
				element instanceof Integer || 
				element instanceof Long)
			writer.write("i@" + element);
		else if(element instanceof Float || element instanceof Double)
			writer.write("f@" + element);
		else if(element instanceof Complex)
			writer.write("x@" + ((Complex) element).get_x() + "@" + ((Complex) element).get_y());
		else if(element instanceof AstNode)
			writer.write("a@" + ((AstNode) element).get_key());
		else if(element instanceof CirNode)
			writer.write("r@" + ((CirNode) element).get_node_id());
		else if(element instanceof String) {
			writer.write("s@");
			String text = element.toString();
			for(int k = 0; k < text.length(); k++) {
				char ch = text.charAt(k);
				if(Character.isWhitespace(ch))
					writer.write("\\s");
				else
					writer.write(ch);
			}
		}
		else if(element instanceof CKeyword)
			writer.write("k@" + element);
		else if(element instanceof CPunctuator)
			writer.write("p@" + element);
		else if(element instanceof COperator)
			writer.write("o@" + element);
		else if(element instanceof CConstant)
			this.write_element(((CConstant) element).get_object());
		else 
			throw new IllegalArgumentException(element.getClass().getSimpleName());
	}
	
	/* CType */
	/**
	 * @param type
	 * @throws Exception
	 */
	private void write_type(CType type) throws Exception { 
		if(type == null)
			writer.write("");
		else if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_void:				writer.write("void"); break;
			case c_bool:				writer.write("bool"); break;
			case c_char:				writer.write("char"); break;
			case c_uchar:				writer.write("unsigned char"); break;
			case c_short:				writer.write("short"); break;
			case c_ushort:				writer.write("unsigned short"); break;
			case c_int:					writer.write("int"); break;
			case c_uint:				writer.write("unsigned int"); break;
			case c_long:				writer.write("long"); break;
			case c_ulong:				writer.write("unsigned long"); break;
			case c_llong:				writer.write("long long"); break;
			case c_ullong:				writer.write("unsigned long long"); break;
			case c_float:				writer.write("float"); break;
			case c_double:				writer.write("double"); break;
			case c_ldouble:				writer.write("long double"); break;
			case c_float_complex:		writer.write("float _Complex"); break;
			case c_double_complex:		writer.write("double _Complex"); break;
			case c_ldouble_complex:		writer.write("long double _Complex"); break;
			case c_float_imaginary:		writer.write("float _Imaginary"); break;
			case c_double_imaginary:	writer.write("double _Imaginary"); break;
			case c_ldouble_imaginary:	writer.write("long double _Imaginary"); break;
			case gnu_va_list:			writer.write("va_list"); break;
			default: throw new IllegalArgumentException(type.generate_code());
			}
		}
		else if(type instanceof CArrayType) {
			writer.write("(");
			this.write_type(((CArrayType) type).get_element_type());
			writer.write(")");
			int length = ((CArrayType) type).length();
			if(length < 0)
				writer.write("*");
			else
				writer.write("[" + length + "]");
		}
		else if(type instanceof CPointerType) {
			writer.write("(");
			this.write_type(((CPointerType) type).get_pointed_type());
			writer.write(")");
			writer.write("*");
		}
		else if(type instanceof CFunctionType) {
			writer.write("(");
			this.write_type(((CFunctionType) type).get_return_type());
			writer.write(")");
			writer.write("@");
		}
		else if(type instanceof CStructType) {
			writer.write(((CStructType) type).get_name());
		}
		else if(type instanceof CUnionType) {
			writer.write(((CUnionType) type).get_name());
		}
		else if(type instanceof CEnumType) {
			writer.write("int");
		}
		else if(type instanceof CQualifierType) {
			this.write_type(((CQualifierType) type).get_reference());
		}
		else
			throw new IllegalArgumentException("Unsupported: " + type.generate_code());
	}
	
	/* source code + AstNode */
	/**
	 * write the source code in the specified directory
	 * @param source_code
	 * @param directory
	 * @throws Exception
	 */
	private void write_source_code(AstTree ast_tree,
			File cfile, File directory) throws Exception {
		File target = this.get_output_file(directory, cfile, ".c");
		FileOperations.copy(ast_tree.get_source_file(), target);
	}
	/**
	 * id class beg_index end_index type token children
	 * @param ast_node
	 * @throws Exception
	 */
	private void write_ast_node(AstNode ast_node) throws Exception {
		/* id */
		writer.write(ast_node.get_key() + "\t");
		
		/* class */
		String ast_class = ast_node.getClass().getSimpleName();
		ast_class = ast_class.substring(3, ast_class.length() - 4);
		writer.write(ast_class.strip() + "\t");
		
		/* beg_index end_index */
		int beg_index = ast_node.get_location().get_bias();
		int end_index = beg_index + ast_node.get_location().get_length();
		writer.write(beg_index + "\t" + end_index);
		
		/* data type */
		CType type;
		if(ast_node instanceof AstExpression) 
			type = ((AstExpression) ast_node).get_value_type();
		else if(ast_node instanceof AstTypeName) 
			type = ((AstTypeName) ast_node).get_type();
		else 
			type = null;
		writer.write("\t");
		this.write_type(type);
		
		/* token */
		Object token;
		if(ast_node instanceof AstIdentifier) {
			token = ((AstIdentifier) ast_node).get_name();
		}
		else if(ast_node instanceof AstConstant) {
			token = ((AstConstant) ast_node).get_constant();
		}
		else if(ast_node instanceof AstLiteral) {
			token = null;
		}
		else if(ast_node instanceof AstKeyword) {
			token = ((AstKeyword) ast_node).get_keyword();
		}
		else if(ast_node instanceof AstPunctuator) {
			token = ((AstPunctuator) ast_node).get_punctuator();
		}
		else if(ast_node instanceof AstOperator) {
			token = ((AstOperator) ast_node).get_operator();
		}
		else {
			token = null;
		}
		writer.write("\t");
		this.write_element(token);
		
		writer.write("\t");
		writer.write("[");
		for(int k = 0; k < ast_node.number_of_children(); k++) {
			writer.write(" " + ast_node.get_child(k).get_key());
		}
		writer.write(" ]");
	}
	/**
	 * write the abstract syntax tree to the xxx.ast file in the directory
	 * @param ast_tree
	 * @param directory
	 * @throws Exception
	 */
	private void write_ast_tree(AstTree ast_tree, File cfile, File directory) throws Exception {
		this.open_writer(this.get_output_file(directory, cfile, ".ast"));
		for(int k = 0; k < ast_tree.number_of_nodes(); k++) {
			this.write_ast_node(ast_tree.get_node(k));
			writer.write("\n");
		}
		this.close_writer();
	}
	/**
	 * id class ast_source type token children
	 * @param cir_node
	 * @throws Exception
	 */
	private void write_cir_node(CirNode cir_node) throws Exception {
		writer.write(cir_node.get_node_id() + "\t");
		
		String cir_class = cir_node.getClass().getSimpleName();
		cir_class = cir_class.substring(3, cir_class.length() - 4);
		writer.write(cir_class.strip() + "\t");
		this.write_element(cir_node.get_ast_source());
		
		CType type;
		if(cir_node instanceof CirExpression) {
			type = ((CirExpression) cir_node).get_data_type();
		}
		else if(cir_node instanceof CirType) {
			type = ((CirType) cir_node).get_typename();
		}
		else {
			type = null;
		}
		writer.write("\t");
		this.write_type(type);
		
		Object token;
		if(cir_node instanceof CirField) {
			token = ((CirField) cir_node).get_name();
		}
		else if(cir_node instanceof CirLabel) {
			token = ((CirLabel) cir_node).get_target_node_id();
		}
		else if(cir_node instanceof CirNameExpression) {
			token = ((CirNameExpression) cir_node).get_name();
		}
		else if(cir_node instanceof CirConstExpression) {
			token = ((CirConstExpression) cir_node).get_constant();
		}
		else if(cir_node instanceof CirComputeExpression) {
			token = ((CirComputeExpression) cir_node).get_operator();
		}
		else {
			token = null;
		}
		writer.write("\t");
		this.write_element(token);
		
		writer.write("\t");
		writer.write("[");
		for(CirNode child : cir_node.get_children()) {
			writer.write(" " + child.get_node_id());
		}
		writer.write(" ]");
	}
	/**
	 * write the C-intermediate representation to the xxx.cir in directory
	 * @param cir_tree
	 * @param cfile
	 * @param directory
	 * @throws Exception
	 */
	private void write_cir_tree(CirTree cir_tree, File cfile, File directory) throws Exception {
		this.open_writer(this.get_output_file(directory, cfile, ".cir"));
		for(int k = 0; k < cir_tree.size(); k++) {
			this.write_cir_node(cir_tree.get_node(k));
			writer.write("\n");
		}
		this.close_writer();
	}
	
	/* function call graph */
	/**
	 * #flow type source target
	 * @param flow
	 * @throws Exception
	 */
	private void write_execution_flow(CirExecutionFlow flow) throws Exception {
		writer.write("#flow");
		writer.write("\t" + flow.get_type());
		writer.write("\t" + flow.get_source());
		writer.write("\t" + flow.get_target());
	}
	/**
	 * #exec id cir_statement
	 * @param execution
	 * @throws Exception
	 */
	private void write_execution_node(CirExecution execution) throws Exception {
		writer.write("#exec");
		writer.write("\t" + execution.toString());
		writer.write("\t");
		this.write_element(execution.get_statement());
	}
	/**
	 * #call call_exec wait_exec
	 * @param call
	 * @throws Exception
	 */
	private void write_function_call(CirFunctionCall call) throws Exception {
		writer.write("#call");
		writer.write("\t" + call.get_call_execution());
		writer.write("\t" + call.get_wait_execution());
	}
	/**
	 * 	#BegFunc name
	 * 		#exec id cir_statement
	 * 		#flow type source target
	 * 		#call call_exec wait_exec
	 * 	#EndFunc
	 * @param function
	 * @throws Exception
	 */
	private void write_function(CirFunction function) throws Exception {
		writer.write("#BegFunc\t" + function.get_name() + "\n");
		for(CirExecution execution : function.get_flow_graph().get_executions()) {
			writer.write("\t");
			this.write_execution_node(execution);
			writer.write("\n");
			
			for(CirExecutionFlow flow : execution.get_ou_flows()) {
				writer.write("\t");
				this.write_execution_flow(flow);
				writer.write("\n");
			}
		}
		for(CirFunctionCall call : function.get_ou_calls()) {
			writer.write("\t");
			this.write_function_call(call);
			writer.write("\n");
		}
		writer.write("#EndFunc\n");
	}
	/**
	 * write the function call and flow graph to the xxx.flw in directory
	 * @param graph
	 * @param cfile
	 * @param directory
	 * @throws Exception
	 */
	private void write_function_call_graph(CirFunctionCallGraph graph, 
			File cfile, File directory) throws Exception {
		this.open_writer(this.get_output_file(directory, cfile, ".flw"));
		for(CirFunction function : graph.get_functions()) {
			this.write_function(function);
			writer.write("\n");
		}
		this.close_writer();
	}
	
	/* test + mutant */ 
	/**
	 * id: parameter
	 * @param input
	 * @throws Exception
	 */
	private void write_test_input(TestInput input) throws Exception {
		writer.write(input.get_id() + ": ");
		writer.write(input.get_parameter());
	}
	/**
	 * write the test inputs in space to xxx.tst in directorycbuf
	 * @param space
	 * @param cfile
	 * @param directory
	 * @throws Exception
	 */
	private void write_test_inputs(MuTestProjectTestSpace space, 
			File cfile, File directory) throws Exception {
		this.open_writer(this.get_output_file(directory, cfile, ".tst"));
		for(TestInput input : space.get_test_inputs()) {
			this.write_test_input(input);
			writer.write("\n");
		}
		this.close_writer();
	}
	/**
	 * id class operator location parameter
	 * @param mutant
	 * @throws Exception
	 */
	private void write_mutant(Mutant mutant) throws Exception {
		writer.write(mutant.get_id() + "\t");
		AstMutation mutation = mutant.get_mutation();
		writer.write(mutation.get_class() + "\t");
		writer.write(mutation.get_operator() + "\t");
		this.write_element(mutation.get_location());
		writer.write("\t");
		this.write_element(mutation.get_parameter());
	}
	/**
	 * write the mutants in the space to xxx.mut in directory
	 * @param space
	 * @param cfile
	 * @param directory
	 * @throws Exception
	 */
	private void write_mutants(MuTestProjectCodeFile space,
			File cfile, File directory) throws Exception {
		this.open_writer(this.get_output_file(directory, cfile, ".mut"));
		for(Mutant mutant : space.get_mutant_space().get_mutants()) {
			this.write_mutant(mutant);
			writer.write("\n");
		}
		this.close_writer();
	}
	
	/* feature word generation: CirConstraint, CirFlowError, 
	 * CirTrapError, CirExpressionError, CirReferenceError,
	 * CirStateValueError */
	/**
	 * @param result
	 * @return 1{true} 0{false} -1{unknown}
	 */
	private int get_label_code(Boolean result) {
		if(result == null)
			return -1;		/* unknown */
		else if(result.booleanValue())
			return 1;		/* true */
		else
			return 0;		/* false */
	}
	/**
	 * write the code that describes the node into word
	 * @param node 
	 * @throws Exception
	 */
	private void write_sym_node(AstTree ast_tree, SymNode node) throws Exception {
		if(node == null)
			throw new IllegalArgumentException("Invalid node: null");
		else if(node instanceof SymIdentifier) {
			/* 1. get the simplified name of identifier */
			String name = ((SymIdentifier) node).get_name();
			int index = name.indexOf('#'); String prev, post;
			if(index >= 0) {
				prev = name.substring(0, index).strip();
				post = name.substring(index + 1).strip();
			}
			else {
				prev = name; post = "";
			}
			
			/* 2. generate the ast-code when name is implicator */
			if(prev.isEmpty()) {
				int ast_key = Integer.parseInt(post);
				AstNode source = ast_tree.get_node(ast_key);
				if(source instanceof AstSwitchStatement) {
					source = CTypeAnalyzer.get_expression_of(((AstSwitchStatement) source).get_condition());
				}
				String code = source.generate_code();
				for(int k = 0; k < code.length(); k++) {
					char ch = code.charAt(k);
					if(Character.isWhitespace(ch))
						ch = ' ';
					writer.write(ch);
				}
			}
			else if(prev.equals("return")) {
				writer.write(name);
			}
			else {
				writer.write(prev);
			}
		}
		else if(node instanceof SymConstant) {
			writer.write(node.generate_code());
		}
		else if(node instanceof SymLiteral) {
			writer.write("@Literal");
		}
		else if(node instanceof SymUnaryExpression) {
			this.write_sym_node(ast_tree, ((SymUnaryExpression) node).get_operator());
			writer.write("(");
			this.write_sym_node(ast_tree, ((SymUnaryExpression) node).get_operand());
			writer.write(")");
		}
		else if(node instanceof SymBinaryExpression) {
			writer.write("(");
			this.write_sym_node(ast_tree, ((SymBinaryExpression) node).get_loperand());
			writer.write(") ");
			this.write_sym_node(ast_tree, ((SymBinaryExpression) node).get_operator());
			writer.write(" (");
			this.write_sym_node(ast_tree, ((SymBinaryExpression) node).get_roperand());
			writer.write(")");
		}
		else if(node instanceof SymOperator) {
			writer.write(node.generate_code());
		}
		else if(node instanceof SymCallExpression) {
			this.write_sym_node(ast_tree, ((SymCallExpression) node).get_function());
			this.write_sym_node(ast_tree, ((SymCallExpression) node).get_argument_list());
		}
		else if(node instanceof SymArgumentList) {
			SymArgumentList list = (SymArgumentList) node;
			writer.write("(");
			for(int k = 0; k < list.number_of_arguments(); k++) {
				this.write_sym_node(ast_tree, list.get_argument(k));
				if(k < list.number_of_arguments() - 1) {
					writer.write(", ");
				}
			}
			writer.write(")");
		}
		else if(node instanceof SymFieldExpression) {
			writer.write("(");
			this.write_sym_node(ast_tree, ((SymFieldExpression) node).get_body());
			writer.write(").");
			this.write_sym_node(ast_tree, ((SymFieldExpression) node).get_field());
		}
		else if(node instanceof SymField) {
			writer.write(((SymField) node).get_name());
		}
		else if(node instanceof SymInitializerList) {
			writer.write("{");
			SymInitializerList list = (SymInitializerList) node;
			for(int k = 0; k < list.number_of_elements(); k++) {
				this.write_sym_node(ast_tree, list.get_element(k));
				if(k < list.number_of_elements() - 1) {
					writer.write(", ");
				}
			}
			writer.write("}");
		}
		else if(node instanceof SymLabel) {
			writer.write(((SymLabel) node).get_execution().toString());
		}
		else
			throw new IllegalArgumentException(node.generate_code());
	}
	/**
	 * #word label type execution location parameter word*
	 * @param ast_tree
	 * @param feature
	 * @param result
	 * @throws Exception
	 */
	private void write_feature_word(AstTree ast_tree, Object feature, Boolean result, CirMutationResult status) throws Exception {
		/* #word label */
		int label = this.get_label_code(result);
		writer.write("#word\t" + label);
		
		/* #cons execution statement condition */
		if(feature instanceof CirConstraint) {
			CirConstraint constraint = (CirConstraint) feature;
			writer.write("\t#cons");
			writer.write("\t" + constraint.get_execution());
			writer.write("\t");
			this.write_element(constraint.get_statement());
			writer.write("\t");
			this.write_sym_node(ast_tree, constraint.get_condition());
		}
		/* #flow execution statement target_execution */
		else if(feature instanceof CirFlowError) {
			CirFlowError state_error = (CirFlowError) feature;
			writer.write("\t#flow");
			writer.write("\t" + state_error.get_execution());
			writer.write("\t");
			this.write_element(state_error.get_statement());
			writer.write("\t" + state_error.get_mutation_flow().get_target());
		}
		/* #trap execution statement word* */
		else if(feature instanceof CirTrapError) {
			CirTrapError state_error = (CirTrapError) feature;
			writer.write("\t#trap");
			writer.write("\t" + state_error.get_execution());
			writer.write("\t");
			this.write_element(state_error.get_statement());
		}
		/* #expr execution expression muta_value word* */
		else if(feature instanceof CirExpressionError) {
			CirExpressionError state_error = (CirExpressionError) feature;
			writer.write("\t#expr");
			writer.write("\t" + state_error.get_execution());
			writer.write("\t");
			this.write_element(state_error.get_expression());
			writer.write("\t");
			this.write_sym_node(ast_tree, state_error.get_mutation_value());
		}
		/* #refr execution expression muta_value word* */
		else if(feature instanceof CirReferenceError) {
			CirReferenceError state_error = (CirReferenceError) feature;
			writer.write("\t#refr");
			writer.write("\t" + state_error.get_execution());
			writer.write("\t");
			this.write_element(state_error.get_reference());
			writer.write("\t");
			this.write_sym_node(ast_tree, state_error.get_mutation_value());
		}
		/* #stat execution expression muta_value word* */
		else if(feature instanceof CirStateValueError) {
			CirStateValueError state_error = (CirStateValueError) feature;
			writer.write("\t#stat");
			writer.write("\t" + state_error.get_execution());
			writer.write("\t");
			this.write_element(state_error.get_reference());
			writer.write("\t");
			this.write_sym_node(ast_tree, state_error.get_mutation_value());
		}
		else {
			throw new IllegalArgumentException(feature.toString());
		}
		
		if(feature instanceof CirStateError && status != null) {
			for(CirStateErrorWord word : status.get_error_words()) {
				writer.write("\t" + word.toString());
			}
		}
 	}
	/**
	 * 	#BegMutation
	 * 		#summary mutant label_code
	 * 		#word label type execution location parameter word*
	 * 		...
	 * 		#word label type execution location parameter word*
	 * 	#EndMutation
	 * @param mutant
	 * @param dominance_graph
	 * @throws Exception
	 */
	private void write_static_mutation_features_and_label(MuTestProjectCodeFile code_file, 
			Mutant mutant, CDependGraph dependence_graph, int max_distance) throws Exception {
		/* 1. data getters */
		AstTree ast_tree = mutant.get_space().get_ast_tree();
		CirMutationGraph mutation_graph = CirMutationGraph.new_graph(mutant, dependence_graph, max_distance);
		Map<CirMutationTreeNode, CirMutationResult> tree_result_map = mutation_graph.abs_evaluate();
		Map<Object, Boolean> feature_valid_map = CirMutationUtils.select_features(tree_result_map);
		MuTestProjectTestResult test_result = code_file.get_code_space().get_project().get_test_space().get_test_result(mutant);
		int label;
		if(test_result == null)
			label = this.get_label_code(null);
		else
			label = this.get_label_code(test_result.get_kill_set().degree() != 0);
		
		/* 2. #summary mutant label_code */
		writer.write("#BegMutation\n");
		writer.write("\t#summary\t" + mutant.get_id() + "\t" + label + "\n");
		
		/* 3. #word label type execution location parameter word* */
		for(Object feature : feature_valid_map.keySet()) {
			CirMutationResult status = null;
			for(CirMutationResult test_status : tree_result_map.values()) {
				if(test_status.get_tree_node().get_cir_mutation().get_state_error() == feature) {
					status = test_status;
					break;
				}
			}
			this.write_feature_word(ast_tree, feature, feature_valid_map.get(feature), status);
			writer.write("\n");
		}
		
		/* 4. end of all */	writer.write("#EndMutation\n");
	}
	/**
	 * 	#BegMutation
	 * 		#summary mutant label_code
	 * 		#word label type execution location parameter word*
	 * 		...
	 * 		#word label type execution location parameter word*
	 * 	#EndMutation
	 * 	@param code_file
	 * 	@param mutant
	 * 	@param test_path
	 * 	@param dominance_graph
	 * 	@throws Exception
	 */
	private void write_dynamic_mutation_features_and_label(MuTestProjectCodeFile code_file, Mutant mutant, 
			TestInput test_case, CStatePath test_path, CDependGraph dependence_graph, int maximal_distance) throws Exception {
		/* 1. data getters */
		AstTree ast_tree = mutant.get_space().get_ast_tree();
		CirMutationGraph mutation_graph = CirMutationGraph.new_graph(mutant, dependence_graph, maximal_distance);
		Map<CirMutationTreeNode, CirMutationResult> tree_result_map = mutation_graph.abs_evaluate(test_path);
		Map<Object, Boolean> feature_valid_map = CirMutationUtils.select_features(tree_result_map);
		MuTestProjectTestResult test_result = code_file.get_code_space().get_project().get_test_space().get_test_result(mutant);
		int label;
		if(test_result == null || !test_result.get_exec_set().get(test_case.get_id()))
			label = this.get_label_code(null);
		else
			label = this.get_label_code(test_result.get_kill_set().get(test_case.get_id()));
		
		/* 2. #summary mutant label_code */
		writer.write("#BegMutation\n");
		writer.write("\t#summary\t" + mutant.get_id() + "\t" + label + "\n");
		
		/* 3. #word label type execution location parameter word* */
		for(Object feature : feature_valid_map.keySet()) {
			CirMutationResult status = null;
			for(CirMutationResult test_status : tree_result_map.values()) {
				if(test_status.get_tree_node().get_cir_mutation().get_state_error() == feature) {
					status = test_status;
					break;
				}
			}
			this.write_feature_word(ast_tree, feature, feature_valid_map.get(feature), status);
			writer.write("\n");
		}
		
		/* 4. end of all */	writer.write("#EndMutation\n");
	}
	/**
	 * write the features and labels generated from none of test cases to xxx.sfl
	 * @param code_file
	 * @param directory
	 * @param cfile
	 * @param dominance_graph
	 * @throws Exception
	 */
	private void write_static_mutants_features_and_labels(MuTestProjectCodeFile code_file, 
			File directory, File cfile, CDependGraph dependence_graph, int maximal_distance) throws Exception {
		this.open_writer(this.get_output_file(directory, cfile, ".sfl"));
		for(Mutant mutant : code_file.get_mutant_space().get_mutants()) {
			this.write_static_mutation_features_and_label(code_file, mutant, dependence_graph, maximal_distance);
			writer.write("\n");
		}
		this.close_writer();
	}
	/**
	 * write the features and labels of mutants under the specific test case to xxx.dsl{tid}
	 * @param code_file
	 * @param directory
	 * @param cfile
	 * @param test_case
	 * @param dominance_graph
	 * @throws Exception
	 */
	private void write_dynamic_mutants_features_and_labels(MuTestProjectCodeFile code_file,
			File directory, File cfile, TestInput test_case, CDependGraph dependence_graph, 
			int maximal_distance) throws Exception {
		CStatePath test_path = code_file.get_code_space().get_project().get_test_space().
				load_instrumental_path(code_file.get_sizeof_template(), 
						code_file.get_ast_tree(), code_file.get_cir_tree(), test_case);
		if(test_path != null) {
			this.open_writer(this.get_output_file(directory, cfile, ".dfl" + test_case.get_id()));
			for(Mutant mutant : code_file.get_mutant_space().get_mutants()) {
				this.write_dynamic_mutation_features_and_label(code_file, mutant, test_case, test_path, dependence_graph, maximal_distance);
				writer.write("\n");
			}
			this.close_writer();
		}
	}
	/**
	 * write the accumulated status results of each mutation to xxx.afl
	 * @param code_file
	 * @param directory
	 * @param cfile
	 * @param dominance_graph
	 * @throws Exception
	 */
	private void write_accumulated_mutants_features_and_labels(MuTestProjectCodeFile code_file,
			File directory, File cfile, CDependGraph dependence_graph, int maximal_distance) throws Exception {
		/* 1. initialization */
		AstTree ast_tree = code_file.get_ast_tree();
		CirTree cir_tree = code_file.get_cir_tree();
		Map<Mutant, CirMutationGraph> mutation_graph_map = new HashMap<Mutant, CirMutationGraph>();
		Map<Mutant, Map<CirMutationTreeNode, CirMutationResult>> accumulation_map = 
				new HashMap<Mutant, Map<CirMutationTreeNode, CirMutationResult>>();
		for(Mutant mutant : code_file.get_mutant_space().get_mutants()) {
			CirMutationGraph mutation_graph = CirMutationGraph.
					new_graph(mutant, dependence_graph, maximal_distance);
			mutation_graph_map.put(mutant, mutation_graph);
			
			Queue<CirMutationTreeNode> queue = new LinkedList<CirMutationTreeNode>();
			Map<CirMutationTreeNode, CirMutationResult> mutant_status_map = 
					new HashMap<CirMutationTreeNode, CirMutationResult>();
			
			for(CirMutationNode node : mutation_graph.get_nodes()) {
				queue.add(node.get_tree().get_root());
				while(!queue.isEmpty()) {
					CirMutationTreeNode tree_node = queue.poll();
					for(CirMutationTreeNode child : tree_node.get_children()) {
						queue.add(child);
					}
					mutant_status_map.put(tree_node, null);
				}
			}
			
			accumulation_map.put(mutant, mutant_status_map);
		}
		
		/* 2. accumulations */
		MuTestProject project = code_file.get_code_space().get_project();
		for(TestInput test_case : project.get_test_space().get_test_inputs()) {
			CStatePath test_path = project.get_test_space().load_instrumental_path(
					code_file.get_sizeof_template(), ast_tree, cir_tree, test_case);
			if(test_path != null) {
				for(Mutant mutant : code_file.get_mutant_space().get_mutants()) {
					Map<CirMutationTreeNode, CirMutationResult> local_status_map = 
							mutation_graph_map.get(mutant).abs_evaluate(test_path);
					Map<CirMutationTreeNode, CirMutationResult> accumulate_map = accumulation_map.get(mutant);
					for(CirMutationTreeNode tree_node : local_status_map.keySet()) {
						CirMutationResult local_status = local_status_map.get(tree_node);
						CirMutationResult accumulate_status = accumulate_map.get(tree_node);
						if(accumulate_status == null) {
							accumulate_map.put(tree_node, local_status);
						}
						else {
							accumulate_status.accumulate_mutation_status(local_status);
						}
					}
				}
			}
			System.out.println("\t\t==> Accumulate on " + test_case.get_id() + "/" + 
					project.get_test_space().number_of_test_inputs() + " test cases.");
		}
		
		/* write the accumulation feature and labels */
		this.open_writer(this.get_output_file(directory, cfile, ".afl"));
		for(Mutant mutant : code_file.get_mutant_space().get_mutants()) {
			/* get the feature and selection labels */
			Map<CirMutationTreeNode, CirMutationResult> accumulate_map = accumulation_map.get(mutant);
			Map<Object, Boolean> feature_label_map = CirMutationUtils.select_features(accumulate_map);
			MuTestProjectTestResult test_result = 
					code_file.get_code_space().get_project().get_test_space().get_test_result(mutant);
			int label;
			if(test_result == null)
				label = this.get_label_code(null);
			else
				label = this.get_label_code(test_result.get_kill_set().degree() != 0);
			
			/* 2. write features & labels of mutant to the file */
			writer.write("#BegMutation\n");
			writer.write("\t#summary\t" + mutant.get_id() + "\t" + label + "\n");
			for(Object feature : feature_label_map.keySet()) {
				CirMutationResult status = null;
				for(CirMutationResult test_status : accumulate_map.values()) {
					if(test_status.get_tree_node().get_cir_mutation().get_state_error() == feature) {
						status = test_status;
						break;
					}
				}
				this.write_feature_word(ast_tree, feature, feature_label_map.get(feature), status);
				writer.write("\n");
			}
			writer.write("#EndMutation\n");
			writer.write("\n");
		}
		this.close_writer();
	}
	
	/* feature generation method */
	/**
	 * write the feature information to files in the directory
	 * @param project
	 * @param cfile
	 * @param directory
	 * @throws Exception
	 */
	public void write_features(MuTestProject project, File cfile, File directory, 
			CDependGraph dependence_graph, boolean write_dynamic_features, 
			boolean write_accumulate_features, int max_distance) throws Exception {
		if(project == null)
			throw new IllegalArgumentException("Invalid project: null");
		else if(cfile == null)
			throw new IllegalArgumentException("Invalid cfile: null");
		else if(directory == null || !directory.isDirectory())
			throw new IllegalArgumentException("Invalid directory: null");
		else if(dependence_graph == null)
			throw new IllegalArgumentException("Invalid dependence_graph");
		else {
			MuTestProjectCodeFile cspace = project.get_code_space().get_code_file(cfile);
			MuTestProjectTestSpace tspace = project.get_test_space();
			this.write_source_code(cspace.get_ast_tree(), cfile, directory);
			this.write_ast_tree(cspace.get_ast_tree(), cfile, directory);
			this.write_cir_tree(cspace.get_cir_tree(), cfile, directory);
			this.write_function_call_graph(cspace.get_cir_tree().get_function_call_graph(), cfile, directory);
			this.write_test_inputs(tspace, cfile, directory);
			this.write_mutants(cspace, cfile, directory);
			this.write_static_mutants_features_and_labels(cspace, directory, cfile, dependence_graph, max_distance);
			
			if(write_dynamic_features) {
				for(TestInput test_case : tspace.get_test_inputs()) {
					this.write_dynamic_mutants_features_and_labels(cspace, directory, cfile, test_case, dependence_graph, max_distance);
				}
			}
			
			if(write_accumulate_features) {
				this.write_accumulated_mutants_features_and_labels(cspace, directory, cfile, dependence_graph, max_distance);
			}
		}
	}
	
}
