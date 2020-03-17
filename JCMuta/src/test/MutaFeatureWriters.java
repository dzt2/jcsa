package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.jcsa.jcmuta.MutaOperator;
import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.code2mutation.MutationCodeType;
import com.jcsa.jcmuta.mutant.sem2mutation.SemanticMutationParsers;
import com.jcsa.jcmuta.mutant.sem2mutation.error.StateError;
import com.jcsa.jcmuta.mutant.sem2mutation.error.StateErrorFlow;
import com.jcsa.jcmuta.mutant.sem2mutation.error.StateErrorGraph;
import com.jcsa.jcmuta.mutant.sem2mutation.error.StateErrorProcesses;
import com.jcsa.jcmuta.mutant.sem2mutation.error.StateInfection;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutation;
import com.jcsa.jcmuta.project.MutaProject;
import com.jcsa.jcmuta.project.MutaSourceFile;
import com.jcsa.jcmuta.project.MutaTestResult;
import com.jcsa.jcmuta.project.Mutant;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.astree.base.AstIdentifier;
import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstOperator;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CFunctionType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CQualifierType;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CUnionType;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
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
import com.jcsa.jcparse.lang.irlang.stmt.CirLabel;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.lexical.CPunctuator;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lopt.analysis.flow.CInfluenceEdge;
import com.jcsa.jcparse.lopt.analysis.flow.CInfluenceGraph;
import com.jcsa.jcparse.lopt.analysis.flow.CInfluenceNode;
import com.jcsa.jcparse.lopt.context.CirCallContextInstanceGraph;
import com.jcsa.jcparse.lopt.context.CirFunctionCallPathType;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceGraph;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceNode;

public class MutaFeatureWriters {
	
	private static final String prefix = "D:\\SourceCode\\MyData\\CODE3\\projects\\";
	private static final String postfx = "results\\data\\";
	private static final String main_function = "main";
	private static final int max_layer = 5;
	// private static final double threshold = 1e-3;
	
	public static void main(String[] args) throws Exception {
		for(File file : new File(prefix).listFiles()) {
			testing(file.getName());
		}
	}
	protected static void testing(String name) throws Exception {
		System.out.println("Testing on " + name);
		
		MutaProject project = open_project(name);
		File output_directory = get_output_directory(name);
		System.out.println("\t1. open mutation test project.");
		
		output_code(project, output_directory);
		output_ast(project, output_directory);
		output_cir(project, output_directory);
		System.out.println("\t2. generate the code and AST.");
		
		output_ast_mutations(project, output_directory);
		output_mutant_labels(project, output_directory);
		output_state_mutations(project, output_directory);
		System.out.println("\t3. generate the feature info.");
		
		output_flow_graphs(project, output_directory);
		output_influence_graph(project, output_directory);
		System.out.println("\t4. generate flow graph info.");
		
		System.out.println();
	}
	
	/* project getters */
	/**
	 * open the mutation project in specified directory of name
	 * @param name
	 * @return
	 * @throws Exception
	 */
	private static MutaProject open_project(String name) throws Exception {
		return new MutaProject(new File(prefix + name));
	}
	/**
	 * create a directory for output the mutation information
	 * @param name
	 * @return
	 * @throws Exception
	 */
	private static File get_output_directory(String name) throws Exception {
		File dir = new File(postfx + name);
		if(!dir.exists()) dir.mkdir();
		return dir;
	}
	
	/* code writers */
	private static void output_code(MutaProject project, File output_directory) throws Exception {
		File output = new File(output_directory.getAbsolutePath() + "/" + project.get_name() + ".c");
		MutaSourceFile source_file = project.get_source_files().get_source_files().iterator().next();
		File source = source_file.get_source_file();
		
		FileInputStream in = new FileInputStream(source);
		FileOutputStream ou = new FileOutputStream(output);
		byte[] buffer = new byte[1024 * 1024]; int length;
		while((length = in.read(buffer)) != -1) {
			ou.write(buffer, 0, length);
		}
		in.close(); ou.close();
	}
	/**
	 * ID type beg end data_type content children_list
	 * @param project
	 * @param output_directory
	 * @throws Exception
	 */
	private static void output_ast(MutaProject project, File output_directory) throws Exception {
		File output = new File(output_directory.getAbsolutePath() + "/" + project.get_name() + ".ast");
		MutaSourceFile source_file = project.get_source_files().get_source_files().iterator().next();
		
		AstTree ast_tree = source_file.get_ast_tree();
		Queue<AstNode> ast_queue = new LinkedList<AstNode>();
		ast_queue.add(ast_tree.get_ast_root());
		
		FileWriter writer = new FileWriter(output);
		writer.write("id\ttype\tbeg\tend\tdata_type\tcontent\tchildren\n");
		while(!ast_queue.isEmpty()) {
			AstNode ast_node = ast_queue.poll();
			output_ast_node(ast_node, writer);
			
			for(int k = 0; k < ast_node.number_of_children(); k++) {
				ast_queue.add(ast_node.get_child(k));
			}
		}
		writer.close();
	}
	/**
	 * ID type beg end data_type content children_list
	 * @param ast_node
	 * @param writer
	 * @throws Exception
	 */
	private static void output_ast_node(AstNode ast_node, FileWriter writer) throws Exception {
		int id = ast_node.get_key();
		String type = ast_node.getClass().getSimpleName();
		type = type.substring(3, type.length() - 4).strip();
		int beg = ast_node.get_location().get_bias();
		int end = beg + ast_node.get_location().get_length();
		String data_type = "";
		if(ast_node instanceof AstExpression) {
			CType dtype = ((AstExpression) ast_node).get_value_type();
			if(dtype != null) data_type = data_type_code(dtype);
		}
		String content = get_ast_content(ast_node);
		
		writer.write(id + "\t");
		writer.write(type + "\t");
		writer.write(beg + "\t");
		writer.write(end + "\t");
		writer.write(data_type + "\t");
		writer.write(content + "\t");
		
		writer.write("[");
		for(int k = 0; k < ast_node.number_of_children(); k++) {
			AstNode child = ast_node.get_child(k);
			writer.write(" " + child.get_key());
		}
		writer.write(" ]");
		
		writer.write("\n");
		
	}
	/**
	 * void, bool, char, int, long, float, double, complex, imaginary;
	 * (pointer <data_type>)
	 * (function <data_type>)
	 * (array length <data_type>)
	 * (struct|union <name>?)
	 * @param data_type
	 * @return
	 * @throws Exception
	 */
	private static String data_type_code(CType data_type) throws Exception {
		if(data_type instanceof CBasicType) {
			switch(((CBasicType) data_type).get_tag()) {
			case c_void:				return "void";
			case c_bool:				return "bool";
			case c_char:
			case c_uchar:				return "char";
			case c_short:
			case c_ushort:				return "short";
			case c_int:
			case c_uint:				return "int";
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:				return "long";
			case c_float:				return "float";
			case c_double:
			case c_ldouble:				return "double";
			case c_float_complex:		
			case c_double_complex:
			case c_ldouble_complex:		return "complex";
			case c_float_imaginary:
			case c_double_imaginary:
			case c_ldouble_imaginary:	return "imaginary";
			case gnu_va_list:			return "va_list";
			default: throw new IllegalArgumentException("Invalid: " + data_type);
			}
		}
		else if(data_type instanceof CArrayType) {
			return "(array " + ((CArrayType) data_type).length() + " " + 
					data_type_code(((CArrayType) data_type).get_element_type()) + ")";
		}
		else if(data_type instanceof CPointerType) {
			return "(pointer " + data_type_code(((CPointerType) data_type).get_pointed_type()) + ")";
		}
		else if(data_type instanceof CFunctionType) {
			return "(function " + data_type_code(((CFunctionType) data_type).get_return_type()) + ")";
		}
		else if(data_type instanceof CStructType) {
			String name = ((CStructType) data_type).get_name();
			if(name != null && !name.isBlank())
				return "(" + name + ")";
			else return "(struct)";
		}
		else if(data_type instanceof CUnionType) {
			String name = ((CUnionType) data_type).get_name();
			if(name != null && !name.isBlank())
				return "(" + name + ")";
			else return "(union)";
		}
		else if(data_type instanceof CEnumType) {
			return "int";
		}
		else if(data_type instanceof CQualifierType) {
			return data_type_code(((CQualifierType) data_type).get_reference());
		}
		else {
			throw new IllegalArgumentException("Invalid data type");
		}
	}
	/**
	 * either identifier name, keyword, constant (bool, int, double), operator, punctuator.
	 * @param ast_node
	 * @return
	 * @throws Exception
	 */
	private static String get_ast_content(AstNode ast_node) throws Exception {
		if(ast_node instanceof AstIdentifier) {
			return ((AstIdentifier) ast_node).get_name();
		}
		else if(ast_node instanceof AstKeyword) {
			return ((AstKeyword) ast_node).get_keyword().toString();
		}
		else if(ast_node instanceof AstConstant) {
			CConstant constant = ((AstConstant) ast_node).get_constant();
			switch(constant.get_type().get_tag()) {
			case c_bool:		
				return constant.get_bool().toString();
			case c_char:
			case c_uchar:		
				int value = constant.get_char().charValue();
				return "" + value;
			case c_short:
			case c_ushort:
			case c_int:
			case c_uint:
				return constant.get_integer().toString();
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:
				return constant.get_long().toString();
			case c_float:
				return constant.get_float().toString();
			case c_double:
			case c_ldouble:
				return constant.get_double().toString();
			default: throw new IllegalArgumentException("Invalid data type");
			}
		}
		else if(ast_node instanceof AstOperator) {
			return ((AstOperator) ast_node).get_operator().name();
		}
		else if(ast_node instanceof AstPunctuator) {
			return ((AstPunctuator) ast_node).get_punctuator().toString();
		}
		else {
			return "";
		}
	}
	/**
	 * id type ast_source data_type content children_list
	 * @param cir_node
	 * @param writer
	 * @throws Exception
	 */
	private static void output_cir_node(CirNode cir_node, FileWriter writer) throws Exception {
		/** ID + CIR-type **/
		int id = cir_node.get_node_id();
		String type = cir_node.getClass().getSimpleName();
		type = type.substring(3, type.length() - 4).strip();
		
		/** AST-KEY **/
		String ast_source = "";
		if(cir_node.get_ast_source() != null)
			ast_source = "" + cir_node.get_ast_source().get_key();
		
		/** DATA-TYPE **/
		CType dtype;
		if(cir_node instanceof CirExpression) {
			dtype = ((CirExpression) cir_node).get_data_type();
		}
		else if(cir_node instanceof CirType) {
			dtype = ((CirType) cir_node).get_typename();
		}
		else {
			dtype = null;
		}
		String data_type = "";
		if(dtype != null) data_type = data_type_code(dtype);
		
		/** content **/
		String content = get_cir_content(cir_node);
		
		writer.write(id + "\t");
		writer.write(type + "\t");
		writer.write(ast_source + "\t");
		writer.write(data_type + "\t");
		writer.write(content + "\t");
		writer.write("[");
		for(int k = 0; k < cir_node.number_of_children(); k++) {
			CirNode child = cir_node.get_child(k);
			writer.write(" " + child.get_node_id());
		}
		writer.write(" ]");
		writer.write("\n");
		
	}
	/**
	 * name | label	| field	==> name
	 * other_expression		==> operator
	 * const_expression		==> constant
	 * default_expression	==> ""
	 * @param cir_node
	 * @return
	 * @throws Exception
	 */
	private static String get_cir_content(CirNode cir_node) throws Exception {
		if(cir_node instanceof CirNameExpression) {
			return ((CirNameExpression) cir_node).get_name();
		}
		else if(cir_node instanceof CirDeferExpression) {
			return COperator.dereference.toString();
		}
		else if(cir_node instanceof CirFieldExpression) {
			return CPunctuator.dot.toString();
		}
		else if(cir_node instanceof CirAddressExpression) {
			return COperator.address_of.toString();
		}
		else if(cir_node instanceof CirCastExpression) {
			return "";
		}
		else if(cir_node instanceof CirComputeExpression) {
			return ((CirComputeExpression) cir_node).get_operator().toString();
		}
		else if(cir_node instanceof CirConstExpression) {
			CConstant constant = ((CirConstExpression) cir_node).get_constant();
			switch(constant.get_type().get_tag()) {
			case c_bool:		
				return constant.get_bool().toString();
			case c_char:
			case c_uchar:		
				int value = constant.get_char().charValue();
				return "" + value;
			case c_short:
			case c_ushort:
			case c_int:
			case c_uint:
				return constant.get_integer().toString();
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:
				return constant.get_long().toString();
			case c_float:
				return constant.get_float().toString();
			case c_double:
			case c_ldouble:
				return constant.get_double().toString();
			default: throw new IllegalArgumentException("Invalid data type");
			}
		}
		else if(cir_node instanceof CirField) {
			return ((CirField) cir_node).get_name();
		}
		else if(cir_node instanceof CirLabel) {
			return ((CirLabel) cir_node).get_target_node_id() + "";
		}
		else {
			return "";
		}
	}
	/**
	 * id type ast_source data_type content children_list
	 * @param cir_tree
	 * @param writer
	 * @throws Exception
	 */
	private static void output_cir(MutaProject project, File output_directory) throws Exception {
		File output = new File(output_directory.getAbsolutePath() + "/" + project.get_name() + ".cir");
		MutaSourceFile source_file = project.get_source_files().get_source_files().iterator().next();
		
		CirTree cir_tree = source_file.get_cir_tree();
		
		FileWriter writer = new FileWriter(output);
		writer.write("id\ttype\tast_source\tdata_type\tcontent\tchildren\n");
		for(CirNode cir_node : cir_tree.get_nodes()) {
			output_cir_node(cir_node, writer);
		}
		writer.close();
	}
	
	/* program writers */
	/**
	 * (flow_type source_id target_id)
	 * @param flow
	 * @param writer
	 * @throws Exception
	 */
	private static void output_execution_flow(CirExecutionFlow flow, FileWriter writer) throws Exception {
		writer.write("( ");
		writer.write(flow.get_type() + " ");
		writer.write(flow.get_source().toString() + " ");
		writer.write(flow.get_target().toString() + " ");
		writer.write(")");
	}
	/**
	 * \t id(string) cir_statement_id flow*
	 * @param execution
	 * @param writer
	 * @throws Exception
	 */
	private static void output_execution(CirExecution execution, FileWriter writer) throws Exception {
		writer.write("\t");
		writer.write(execution.toString());
		writer.write("\t");
		writer.write(execution.get_statement().get_node_id() + "");
		for(CirExecutionFlow flow : execution.get_ou_flows()) {
			writer.write("\t");
			output_execution_flow(flow, writer);
		}
		writer.write("\n");
	}
	/**
	 * function name
	 * 	[execution]+
	 * end function
	 * @param function
	 * @param writer
	 * @throws Exception
	 */
	private static void output_function(CirFunction function, FileWriter writer) throws Exception {
		writer.write("function\t" + function.get_name() + "\n");
		for(CirExecution execution : function.get_flow_graph().get_executions()) {
			output_execution(execution, writer);
		}
		writer.write("end function\n");
	}
	/**
	 * function name
	 * 	exec_id statement_id [flow]*
	 * end function
	 * @param project
	 * @param output_directory
	 * @throws Exception
	 */
	private static void output_flow_graphs(MutaProject project, File output_directory) throws Exception {
		File output = new File(output_directory.getAbsolutePath() + "/" + project.get_name() + ".flw");
		MutaSourceFile source_file = project.get_source_files().get_source_files().iterator().next();
		
		CirTree cir_tree = source_file.get_cir_tree();
		FileWriter writer = new FileWriter(output);
		writer.write("id\ttype\tast_source\tdata_type\tcontent\tchildren\n");
		for(CirFunction function : cir_tree.get_function_call_graph().get_functions()) {
			output_function(function, writer);
		}
		writer.close();
	}
	/**
	 * contenxt::cir_source
	 * @param node
	 * @return
	 * @throws Exception
	 */
	private static String influence_node_key(CInfluenceNode node) throws Exception {
		String context = node.get_instance_context().hashCode() + "::";
		String cir_source = node.get_cir_source().get_node_id() + "";
		return context + cir_source;
	}
	/**
	 * [ type source target ]
	 * @param edge
	 * @param writer
	 * @throws Exception
	 */
	private static void output_influence_edge(CInfluenceEdge edge, FileWriter writer) throws Exception {
		writer.write("[");
		writer.write(" " + edge.get_type().toString());
		writer.write(" " + influence_node_key(edge.get_source()));
		writer.write(" " + influence_node_key(edge.get_target()));
		writer.write(" ]");
	}
	/**
	 * id type execution cir_source [type source target]*
	 * @param node
	 * @param writer
	 * @throws Exception
	 */
	private static void output_influence_node(CInfluenceNode node, FileWriter writer) throws Exception {
		String id = influence_node_key(node);
		String type = node.get_node_type().toString();
		String execution = node.get_execution().toString();
		String cir_source = node.get_cir_source().get_node_id() + "";
		
		writer.write(id);
		writer.write("\t" + type);
		writer.write("\t" + execution);
		writer.write("\t" + cir_source);
		for(CInfluenceEdge edge : node.get_ou_edges()) {
			writer.write("\t");
			output_influence_edge(edge, writer);
		}
		writer.write("\n");
	}
	/**
	 * id type execution cir_source [type source target]*
	 * @param project
	 * @param output_directory
	 * @throws Exception
	 */
	private static void output_influence_graph(MutaProject project, File output_directory) throws Exception {
		File output = new File(output_directory.getAbsolutePath() + "/" + project.get_name() + ".inf");
		MutaSourceFile source_file = project.get_source_files().get_source_files().iterator().next();
		
		CirTree cir_tree = source_file.get_cir_tree();
		CirFunction root_function = cir_tree.get_function_call_graph().get_function("main");
		CirInstanceGraph program_graph =  CirCallContextInstanceGraph.graph(root_function, 
				CirFunctionCallPathType.unique_path, -1);
		CInfluenceGraph influence_graph = CInfluenceGraph.graph(program_graph);
		
		FileWriter writer = new FileWriter(output);
		writer.write("id\ttype\texecution\tcir_source\tedges\n");
		for(CirInstanceNode instance : influence_graph.get_instances()) {
			for(CInfluenceNode influence_node : influence_graph.get_nodes(instance)) {
				output_influence_node(influence_node, writer);
			}
		}
		writer.close();
	}
	
	/* mutant writers */
	private static String get_mutant_parameter(AstMutation mutation) throws Exception {
		switch(mutation.get_mutation_class()) {
		case TTRP:
		{
			int value = (int) mutation.get_parameter();
			return value + "";
		}
		case CTRP:
		case SGLR:
		case SRTR:
		{
			AstNode parameter = (AstNode) mutation.get_parameter();
			return parameter.get_key() + "";
		}
		case VINC:
		{
			if(mutation.get_mutation_operator() == MutaOperator.inc_value) {
				int value = (int) mutation.get_parameter();
				return value + "";
			}
			else {
				double value = (double) mutation.get_parameter();
				return value + "";
			}
		}
		case VCRP:
		{
			CConstant constant = (CConstant) mutation.get_parameter();
			switch(constant.get_type().get_tag()) {
			case c_bool:		
				return constant.get_bool().toString();
			case c_char:
			case c_uchar:		
				int value = constant.get_char().charValue();
				return "" + value;
			case c_short:
			case c_ushort:
			case c_int:
			case c_uint:
				return constant.get_integer().toString();
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:
				return constant.get_long().toString();
			case c_float:
				return constant.get_float().toString();
			case c_double:
			case c_ldouble:
				return constant.get_double().toString();
			default: throw new IllegalArgumentException("Invalid data type");
			}
		}
		case VRRP:
		{
			CName name = (CName) mutation.get_parameter();
			if(name == null) return "";
			else return name.get_name();
		}
		default: return "";
		}
	}
	/**
	 * id class operator location parameter?
	 * @param mutation
	 * @param writer
	 * @throws Exception
	 */
	private static void output_ast_mutation(Mutant mutant, FileWriter writer) throws Exception {
		writer.write(mutant.get_id() + "\t");
		AstMutation mutation = mutant.get_mutation();
		writer.write(mutation.get_mutation_class() + "\t");
		writer.write(mutation.get_mutation_operator() + "\t");
		writer.write(mutation.get_location().get_key() + "\t");
		writer.write(get_mutant_parameter(mutation));
		writer.write("\n");
	}
	/**
	 * id class operator location parameter?
	 * @param project
	 * @param output_directory
	 * @throws Exception
	 */
	private static void output_ast_mutations(MutaProject project, File output_directory) throws Exception {
		File output = new File(output_directory.getAbsolutePath() + "/" + project.get_name() + ".mut");
		MutaSourceFile source_file = project.get_source_files().get_source_files().iterator().next();
		
		FileWriter writer = new FileWriter(output);
		writer.write("id\tclass\toperator\tlocation\tparameter\n");
		for(Mutant mutant : source_file.get_mutant_space().get_mutants()) {
			output_ast_mutation(mutant, writer);
		}
		writer.close();
	}
	/**
	 * id score
	 * @param project
	 * @param mutant
	 * @param writer
	 * @throws Exception
	 */
	private static void output_mutant_label(MutaProject project, Mutant mutant, FileWriter writer) throws Exception {
		Map<MutationCodeType, MutaTestResult> results = 
				project.get_results().read_test_results(mutant);
		MutaTestResult result = results.get(MutationCodeType.Stronger);
		writer.write(mutant.get_id() + "\t");
		writer.write(result.get_test_result().toString());
		writer.write("\n");
	}
	/**
	 * id label probability
	 * @param project
	 * @param output_directory
	 * @throws Exception
	 */
	private static void output_mutant_labels(MutaProject project, File output_directory) throws Exception {
		File output = new File(output_directory.getAbsolutePath() + "/" + project.get_name() + ".lab");
		MutaSourceFile source_file = project.get_source_files().get_source_files().iterator().next();
		
		FileWriter writer = new FileWriter(output);
		writer.write("id\tscore\n");
		for(Mutant mutant : source_file.get_mutant_space().get_mutants()) {
			output_mutant_label(project, mutant, writer);
		}
		writer.close();
	}
	
	/* semantic mutation */
	private static void output_semantic_assertion(SemanticAssertion assertion, FileWriter writer) throws Exception {
		writer.write(assertion.get_function().toString());
		writer.write("(");
		boolean first = true;
		for(Object operand : assertion.get_operands()) {
			if(first) first = false;
			else writer.write("; ");
			
			if(operand instanceof CirNode) {
				writer.write("cir#" + ((CirNode) operand).get_node_id());
			}
			else if(operand instanceof Boolean) {
				writer.write("b#" + operand.toString());
			}
			else if(operand instanceof Character) {
				writer.write("i#" + (int)(((Character) operand).charValue()));
			}
			else if(operand instanceof Integer) {
				writer.write("i#" + operand.toString());
			}
			else if(operand instanceof Long) {
				writer.write("i#" + operand.toString());
			}
			else if(operand instanceof Double) {
				writer.write("f#" + operand.toString());
			}
			else if(operand instanceof String) {
				writer.write("s#\"" + operand.toString() + "\"");
			}
			else {
				throw new IllegalArgumentException("Unknown: " + operand.getClass().getSimpleName());
			}
		}
		writer.write(")");
	}
	/**
	 * [flow] source target (assertion)*
	 * @param constraints
	 * @param writer
	 * @throws Exception
	 */
	private static void output_error_flow(StateErrorFlow flow, FileWriter writer) throws Exception {
		writer.write("[flow]");
		writer.write("\t" + flow.get_source().get_id());
		writer.write("\t" + flow.get_target().get_id());
		
		for(SemanticAssertion constraint : flow.get_constraints().get_constraints()) {
			writer.write("\t");
			output_semantic_assertion(constraint, writer);
		}
	}	
	/**
	 * 	[error] 
	 * 		[node] id location assertion*
	 * 		[flow] source target assertion*
	 * 		......
	 * 		[flow] source target assertion*
	 * 	[end_error]
	 * @param error
	 * @param writer
	 * @throws Exception
	 */
	private static void output_state_error(StateError error, FileWriter writer) throws Exception {
		writer.write("[error]\n");
		
		writer.write("\t[node]");
		writer.write("\t" + error.get_id());
		if(error.get_location() == null)
			writer.write("\t");
		else
			writer.write("\tcir#" + error.get_location().get_node_id());
		for(SemanticAssertion assertion : error.get_assertions()) {
			writer.write("\t");
			output_semantic_assertion(assertion, writer);
		}
		writer.write("\n");
		
		for(StateErrorFlow flow : error.get_ou_flows()) {
			writer.write("\t");
			output_error_flow(flow, writer);
			writer.write("\n");
		}
		
		writer.write("[end_error]\n");
	}
	/**
	 * [infect] error (assertion)*
	 * @param infection
	 * @param writer
	 * @throws Exception
	 */
	private static void output_infection(StateInfection infection, FileWriter writer) throws Exception {
		writer.write("[infect]");
		writer.write("\t" + infection.get_state_error().get_id());
		for(SemanticAssertion constraint : infection.get_constraints().get_constraints()) {
			writer.write("\t");
			output_semantic_assertion(constraint, writer);
		}
		writer.write("\n");
	}
	/**
	 * [graph]
	 * [infect] ...
	 * [infect] ...
	 * [error]......[end_error]*
	 * [end_graph]
	 * @param graph
	 * @param writer
	 * @throws Exception
	 */
	private static void output_state_errors(StateErrorGraph graph, FileWriter writer) throws Exception {
		writer.write("[graph]\n");
		for(StateInfection infection : graph.get_infections()) {
			output_infection(infection, writer);
		}
		for(StateError error : graph.get_errors()) {
			output_state_error(error, writer);
		}
		writer.write("[end_graph]\n");
	}
	/**
	 * [mutant] id
	 * [graph]......[end_graph]
	 * [end_mutant]
	 * @param mutant
	 * @param writer
	 * @throws Exception
	 */
	private static void output_state_mutation(Mutant mutant, FileWriter writer) throws Exception {
		writer.write("[mutant]\t" + mutant.get_id() + "\n");
		
		SemanticMutation mutation = null; 
		try {
			mutation = SemanticMutationParsers.parse(mutant);
		}
		catch(Exception ex) {
			// System.out.println("\t\t==> Error at Mu#" + mutant.get_id());
		}
		
		if(mutation != null) {
			StateErrorGraph graph = StateErrorProcesses.processes.process(mutation, max_layer);
			writer.write("[cover]");
			for(SemanticAssertion assertion : graph.get_reach_constraints().get_constraints()) {
				writer.write("\t");
				output_semantic_assertion(assertion, writer);
			}
			writer.write("\n");
			output_state_errors(graph, writer);
		}
		
		writer.write("[end_mutant]\n");
	}
	private static void output_state_mutations(MutaProject project, File output_directory) throws Exception {
		File output = new File(output_directory.getAbsolutePath() + "/" + project.get_name() + ".sem");
		MutaSourceFile source_file = project.get_source_files().get_source_files().iterator().next();
		
		FileWriter writer = new FileWriter(output);
		StateErrorProcesses.processes.open(source_file.get_cir_tree(), main_function);
		for(Mutant mutant : source_file.get_mutant_space().get_mutants()) {
			output_state_mutation(mutant, writer);
		}
		StateErrorProcesses.processes.close();
		writer.close();
	}
	
}
