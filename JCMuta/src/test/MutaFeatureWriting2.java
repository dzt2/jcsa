package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmuta.MutaClass;
import com.jcsa.jcmuta.MutaOperator;
import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.ast2mutation.AstMutationGenerators;
import com.jcsa.jcmuta.mutant.error2mutation.MutantInfection;
import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateInfections;
import com.jcsa.jcmuta.project.MutaProject;
import com.jcsa.jcmuta.project.MutaSourceFile;
import com.jcsa.jcmuta.project.Mutant;
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
import com.jcsa.jcparse.lang.irlang.expr.CirStringLiteral;
import com.jcsa.jcparse.lang.irlang.expr.CirType;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabel;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabelStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.lexical.CPunctuator;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.symb.StateConstraint;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymAddress;
import com.jcsa.jcparse.lang.symb.SymBinaryExpression;
import com.jcsa.jcparse.lang.symb.SymConstant;
import com.jcsa.jcparse.lang.symb.SymDefaultValue;
import com.jcsa.jcparse.lang.symb.SymExpression;
import com.jcsa.jcparse.lang.symb.SymField;
import com.jcsa.jcparse.lang.symb.SymLiteral;
import com.jcsa.jcparse.lang.symb.SymMultiExpression;
import com.jcsa.jcparse.lang.symb.SymNode;
import com.jcsa.jcparse.lang.symb.SymUnaryExpression;
import com.jcsa.jcparse.lopt.CirInstance;
import com.jcsa.jcparse.lopt.context.CirCallContextInstanceGraph;
import com.jcsa.jcparse.lopt.context.CirFunctionCallPathType;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceEdge;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceGraph;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceNode;
import com.jcsa.jcparse.lopt.models.depend.CDependEdge;
import com.jcsa.jcparse.lopt.models.depend.CDependGraph;
import com.jcsa.jcparse.lopt.models.depend.CDependNode;
import com.jcsa.jcparse.lopt.models.depend.CDependPredicate;
import com.jcsa.jcparse.lopt.models.depend.CDependReference;
import com.jcsa.jcparse.lopt.models.dominate.CDominanceGraph;
import com.jcsa.jcparse.lopt.models.dominate.CDominanceNode;

public class MutaFeatureWriting2 {

	/* parameters */
	private static int error_counter;
	/** path from which the original data are extracted **/
	private static final String prefix = "D:\\SourceCode\\MyData\\CODE3\\";
	/** path to which the feature informations are written **/
	private static final String postfx = "results\\pdata\\";
	/** the name of the main function to create instance graph **/
	private static final String main_function = "main";
	/** whether to optimize the symbolic constraint in state infection step **/
	private static final boolean extend_constraint = false;
	/** used to replace the space character in String instance encoding **/
	private static final String space_replacement = "\\s";
	
	private static Collection<MutaClass> operators() throws Exception {
		Set<MutaClass> operators = new HashSet<MutaClass>();
		operators.addAll(AstMutationGenerators.trapping_classes);
		operators.addAll(AstMutationGenerators.statement_classes);
		operators.addAll(AstMutationGenerators.unary_classes);
		operators.addAll(AstMutationGenerators.operator_classes);
		operators.add(MutaClass.VBRP);
		operators.add(MutaClass.VCRP);
		operators.remove(MutaClass.OPDL);
		return operators;
	}
	private static MutaProject create_project(String name) throws Exception {
		File code_file = new File(prefix + "ifiles\\" + name + ".c");
		File project_directory = new File(prefix + "projects2\\" + name);
		File suite_directory = new File(prefix + "suite\\" + name);
		File inputs_directory = new File(prefix + "inputs\\" + name);
		MutaProject project = new MutaProject(project_directory);
		
		project.get_config().set_csizeof_file(new File("config/csizeof.txt"));
		project.get_config().set_jcmulib_header_file(new File("config/jcmulib.h"));
		project.get_config().set_jcmulib_source_file(new File("config/jcmulib.c"));
		project.get_config().set_parameter_file(new File("config/parameter.txt"));
		
		project.get_source_files().add_source_file(code_file);
		for(MutaSourceFile source_file : project.get_source_files().get_source_files()) {
			source_file.get_mutant_space().set_mutants(operators());
		}
		
		List<File> suite_files = new ArrayList<File>();
		if(suite_directory.exists()) {
			File[] files = suite_directory.listFiles();
			if(files != null) {
				for(File file : files) {
					suite_files.add(file);
				}
			}
		}
		project.get_test_space().set_test_cases(suite_files);
		
		if(inputs_directory.exists()) 
			project.get_test_space().set_test_inputs(inputs_directory);
		
		System.out.println("Create project for " + name + " with " + 
							project.get_source_files().get_source_files().iterator().next().get_mutant_space().size() + 
							" mutants and " + project.get_test_space().number_of_test_cases() + " test cases.");
		return project;
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
	/**
	 * generate the instance flow graph for context-sensitive analysis
	 * @param project
	 * @return
	 * @throws Exception
	 */
	private static CirInstanceGraph get_instance_flow_graph(MutaProject project) throws Exception {
		MutaSourceFile source_file = project.get_source_files().get_source_files().iterator().next();
		CirTree cir_tree = source_file.get_cir_tree();
		CirFunction root_function = cir_tree.get_function_call_graph().get_function(main_function);
		CirInstanceGraph program_graph =  CirCallContextInstanceGraph.graph(root_function, 
				CirFunctionCallPathType.unique_path, -1);
		return program_graph;
	}
	
	/* basic text translator */
	/**
	 * ast@identifier
	 * cir@identifier
	 * bool@value
	 * int@value
	 * double@value
	 * string@value
	 * ins@hashcode
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private static String get_parameter_content(Object parameter) throws Exception {
		if(parameter instanceof Integer || parameter instanceof Long) {
			return "int@" + parameter.toString();
		}
		else if(parameter instanceof Character) {
			int value = ((Character) parameter).charValue();
			return "int@" + value;
		}
		else if(parameter instanceof Boolean) {
			return "bool@" + parameter.toString();
		}
		else if(parameter instanceof Float || parameter instanceof Double) {
			return "double@" + parameter.toString();
		}
		else if(parameter instanceof String) {
			String content = parameter.toString();
			StringBuilder buffer = new StringBuilder();
			
			buffer.append("string@");
			for(int k = 0; k < content.length(); k++) {
				char ch = content.charAt(k);
				if(!Character.isSpaceChar(ch) && ch != '\n' && ch != '\r' && ch != '\t') {
					buffer.append(ch);
				}
				else {
					buffer.append(space_replacement);
				}
			}
			return buffer.toString();
		}
		else if(parameter instanceof AstNode) {
			return "ast@" + ((AstNode) parameter).get_key();
		}
		else if(parameter instanceof CirNode) {
			return "cir@" + ((CirNode) parameter).get_node_id();
		}
		else if(parameter instanceof CirInstance) {
			return "ins@" + parameter.hashCode();
		}
		else {
			throw new IllegalArgumentException("Invalid parameter: " + parameter.getClass().getSimpleName());
		}
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
		if(data_type == null) {
			return "";
		}
		else if(data_type instanceof CBasicType) {
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
			else return "(struct #" + data_type.hashCode() + ")";
		}
		else if(data_type instanceof CUnionType) {
			String name = ((CUnionType) data_type).get_name();
			if(name != null && !name.isBlank())
				return "(" + name + ")";
			else return "(union #" + data_type.hashCode() + ")";
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
	 * address | constant | default_value{?} | literal {string} | bin_expr{oprt} | 
	 * field {name} | multi_expr{operator} | unary_expr{operator}
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private static String get_symbolic_content(SymNode source) throws Exception {
		if(source instanceof SymAddress) {
			return get_parameter_content(((SymAddress) source).get_address());
		}
		else if(source instanceof SymConstant) {
			CConstant constant = ((SymConstant) source).get_constant();
			switch(constant.get_type().get_tag()) {
			case c_bool:		
				return get_parameter_content(constant.get_bool().toString());
			case c_char:
			case c_uchar:		
				int value = constant.get_char().charValue();
				return get_parameter_content(value);
			case c_short:
			case c_ushort:
			case c_int:
			case c_uint:
				return get_parameter_content(constant.get_integer());
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:
				return get_parameter_content(constant.get_long());
			case c_float:
				return get_parameter_content(constant.get_float());
			case c_double:
			case c_ldouble:
				return get_parameter_content(constant.get_double());
			default: throw new IllegalArgumentException("Invalid data type");
			}
		}
		else if(source instanceof SymDefaultValue) {
			return get_parameter_content("?");
		}
		else if(source instanceof SymLiteral) {
			return get_parameter_content(((SymLiteral) source).get_literal());
		}
		else if(source instanceof SymField) {
			return get_parameter_content(((SymField) source).get_name());
		}
		else if(source instanceof SymUnaryExpression) {
			return get_parameter_content(((SymUnaryExpression) source).get_operator().toString());
		}
		else if(source instanceof SymBinaryExpression) {
			return get_parameter_content(((SymBinaryExpression) source).get_operator().toString());
		}
		else if(source instanceof SymMultiExpression) {
			return get_parameter_content(((SymMultiExpression) source).get_operator().toString());
		}
		else {
			return "";
		}
	}
	/**
	 * [sym]	hashcode	sym_type	data_type{or empty}	content	children
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private static void symbolic_node_code(SymNode source, FileWriter writer, int tabs) throws Exception {
		for(int k = 0; k < tabs; k++) writer.write('\t');
		writer.write("[sym]\t");
		writer.write(source.hashCode() + "\t");
		writer.write(source.getClass().getSimpleName());
		writer.write("\t");
		if(source instanceof SymExpression) {
			writer.write(data_type_code(((SymExpression) source).get_data_type()));
		}
		else {
			writer.write("");
		}
		writer.write("\t");
		writer.write(get_symbolic_content(source));
		for(SymNode child : source.get_children()) {
			writer.write("\t" + child.hashCode());
		}
		writer.write("\n");
	}
	
	/* source code writing */
	/**
	 * output the source code text
	 * @param project
	 * @param output_directory
	 * @throws Exception
	 */
	private static void output_source_code(MutaProject project, File output_directory) throws Exception {
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
	
	/* static program information writing */
	/**
	 * either identifier name, keyword, constant (bool, int, double), operator, punctuator.
	 * @param ast_node
	 * @return
	 * @throws Exception
	 */
	private static String get_ast_content(AstNode ast_node) throws Exception {
		if(ast_node instanceof AstIdentifier) {
			return get_parameter_content(((AstIdentifier) ast_node).get_name());
		}
		else if(ast_node instanceof AstKeyword) {
			return get_parameter_content(((AstKeyword) ast_node).get_keyword().toString());
		}
		else if(ast_node instanceof AstConstant) {
			CConstant constant = ((AstConstant) ast_node).get_constant();
			switch(constant.get_type().get_tag()) {
			case c_bool:		
				return get_parameter_content(constant.get_bool().toString());
			case c_char:
			case c_uchar:		
				int value = constant.get_char().charValue();
				return get_parameter_content(value);
			case c_short:
			case c_ushort:
			case c_int:
			case c_uint:
				return get_parameter_content(constant.get_integer());
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:
				return get_parameter_content(constant.get_long());
			case c_float:
				return get_parameter_content(constant.get_float());
			case c_double:
			case c_ldouble:
				return get_parameter_content(constant.get_double());
			default: throw new IllegalArgumentException("Invalid data type");
			}
		}
		else if(ast_node instanceof AstLiteral) {
			return get_parameter_content(((AstLiteral) ast_node).get_literal());
		}
		else if(ast_node instanceof AstOperator) {
			return get_parameter_content(((AstOperator) ast_node).get_operator().name());
		}
		else if(ast_node instanceof AstPunctuator) {
			return get_parameter_content(((AstPunctuator) ast_node).get_punctuator().toString());
		}
		else {
			return "";
		}
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
		String data_type;
		if(ast_node instanceof AstExpression) 
			data_type = data_type_code(((AstExpression) ast_node).get_value_type());
		else if(ast_node instanceof AstTypeName) 
			data_type = data_type_code(((AstTypeName) ast_node).get_type());
		else
			data_type = "";
		String content = get_ast_content(ast_node);
		
		writer.write(id + "\t");
		writer.write(type + "\t");
		writer.write(beg + "\t");
		writer.write(end + "\t");
		writer.write(data_type + "\t");
		writer.write(content);
		
		for(int k = 0; k < ast_node.number_of_children(); k++) 
			writer.write("\t" + ast_node.get_child(k).get_key());
		
		writer.write("\n");
	}
	/**
	 * ID type beg end data_type content children_list
	 * @param project
	 * @param output_directory
	 * @throws Exception
	 */
	private static void output_ast_tree(MutaProject project, File output_directory) throws Exception {
		File output = new File(output_directory.getAbsolutePath() + "/" + project.get_name() + ".ast");
		MutaSourceFile source_file = project.get_source_files().get_source_files().iterator().next();
		
		AstTree ast_tree = source_file.get_ast_tree();
		Queue<AstNode> ast_queue = new LinkedList<AstNode>();
		ast_queue.add(ast_tree.get_ast_root());
		
		FileWriter writer = new FileWriter(output);
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
	 * name | label	| field	==> name
	 * other_expression		==> operator
	 * const_expression		==> constant
	 * default_expression	==> "?"
	 * @param cir_node
	 * @return
	 * @throws Exception
	 */
	private static String get_cir_content(CirNode cir_node) throws Exception {
		if(cir_node instanceof CirNameExpression) {
			return get_parameter_content(((CirNameExpression) cir_node).get_unique_name());
		}
		else if(cir_node instanceof CirDeferExpression) {
			return get_parameter_content(COperator.dereference.toString());
		}
		else if(cir_node instanceof CirFieldExpression) {
			return get_parameter_content(CPunctuator.dot.toString());
		}
		else if(cir_node instanceof CirAddressExpression) {
			return get_parameter_content(COperator.address_of.toString());
		}
		else if(cir_node instanceof CirCastExpression) {
			return get_parameter_content(COperator.assign.toString());
		}
		else if(cir_node instanceof CirComputeExpression) {
			return get_parameter_content(((CirComputeExpression) cir_node).get_operator().toString());
		}
		else if(cir_node instanceof CirConstExpression) {
			CConstant constant = ((CirConstExpression) cir_node).get_constant();
			switch(constant.get_type().get_tag()) {
			case c_bool:		
				return get_parameter_content(constant.get_bool());
			case c_char:
			case c_uchar:		
				int value = constant.get_char().charValue();
				return get_parameter_content(value);
			case c_short:
			case c_ushort:
			case c_int:
			case c_uint:
				return get_parameter_content(constant.get_integer());
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:
				return get_parameter_content(constant.get_long());
			case c_float:
				return get_parameter_content(constant.get_float());
			case c_double:
			case c_ldouble:
				return get_parameter_content(constant.get_double());
			default: throw new IllegalArgumentException("Invalid data type");
			} 
		}
		else if(cir_node instanceof CirField) {
			return get_parameter_content(((CirField) cir_node).get_name());
		}
		else if(cir_node instanceof CirLabel) {
			return get_parameter_content(((CirLabel) cir_node).get_target_node_id());
		}
		else if(cir_node instanceof CirLabelStatement) {
			return "";
		}
		else if(cir_node instanceof CirStringLiteral) {
			return get_parameter_content(((CirStringLiteral) cir_node).get_literal());
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
		writer.write(content);
		
		/** children **/
		for(CirNode child : cir_node.get_children()) {
			writer.write("\t" + child.get_node_id());
		}
		
		writer.write("\n");
	}
	/**
	 * id type ast_source data_type content children_list
	 * @param cir_tree
	 * @param writer
	 * @throws Exception
	 */
	private static void output_cir_tree(MutaProject project, File output_directory) throws Exception {
		File output = new File(output_directory.getAbsolutePath() + "/" + project.get_name() + ".cir");
		MutaSourceFile source_file = project.get_source_files().get_source_files().iterator().next();
		
		CirTree cir_tree = source_file.get_cir_tree();
		
		FileWriter writer = new FileWriter(output);
		for(CirNode cir_node : cir_tree.get_nodes()) {
			output_cir_node(cir_node, writer);
		}
		writer.close();
	}
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
	 * \t [execution] id(string) cir_statement_id flow*
	 * @param execution
	 * @param writer
	 * @throws Exception
	 */
	private static void output_execution(CirExecution execution, FileWriter writer) throws Exception {
		writer.write("[execution]");
		writer.write("\t" + execution.toString());
		writer.write("\t" + execution.get_statement().get_node_id());
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
	private static void output_function_graph(MutaProject project, File output_directory) throws Exception {
		File output = new File(output_directory.getAbsolutePath() + "/" + project.get_name() + ".flw");
		MutaSourceFile source_file = project.get_source_files().get_source_files().iterator().next();
		
		CirTree cir_tree = source_file.get_cir_tree();
		FileWriter writer = new FileWriter(output);
		for(CirFunction function : cir_tree.get_function_call_graph().get_functions()) {
			output_function(function, writer);
		}
		
		writer.close(); 
	}
	
	/* context-sensitive information writing */
	/**
	 * [edge] hashcode type source target
	 * @param node
	 * @param writer
	 * @throws Exception
	 */
	private static void output_instance_edge(CirInstanceEdge edge, FileWriter writer) throws Exception {
		writer.write("\t[edge]");
		writer.write("\t" + get_parameter_content(edge));
		writer.write("\t" + edge.get_type().toString());
		writer.write("\t" + get_parameter_content(edge.get_source()));
		writer.write("\t" + get_parameter_content(edge.get_target()));
		writer.write("\n");
	}
	/**
	 * [node] hashcode context execution
	 * @param node
	 */
	private static void output_instance_node(CirInstanceNode node, FileWriter writer) throws Exception {
		writer.write("[node]");
		writer.write("\t" + get_parameter_content(node));
		writer.write("\t" + node.get_context().hashCode());
		writer.write("\t" + node.get_execution().toString());
		writer.write("\n");
		
		for(CirInstanceEdge edge : node.get_ou_edges()) {
			output_instance_edge(edge, writer);
		}
	}
	/**
	 * project.ins
	 * @param project
	 * @param graph
	 * @param output_directory
	 * @throws Exception
	 */
	private static void output_instance_graph(MutaProject project, CirInstanceGraph graph, File output_directory) throws Exception {
		File output = new File(output_directory.getAbsolutePath() + "/" + project.get_name() + ".ins");
		FileWriter writer = new FileWriter(output);
		for(Object context : graph.get_contexts()) {
			for(CirInstance instance : graph.get_instances(context)) {
				if(instance instanceof CirInstanceNode) {
					output_instance_node((CirInstanceNode) instance, writer);
				}
			}
		}
		writer.close();
	}
	/**
	 * source ou_node1 ou_node2 ... ou_nodeN
	 * @param edge
	 * @param writer
	 * @throws Exception
	 */
	private static void output_dominance_node(CDominanceNode node, FileWriter writer) throws Exception {
		writer.write(get_parameter_content(node.get_instance()));
		for(CDominanceNode next_node : node.get_ou_nodes()) {
			writer.write("\t");
			writer.write(get_parameter_content(next_node.get_instance()));
		}
		writer.write("\n");
	}
	/**
	 * source ou_node ou_node ... ou_node +
	 * @param project
	 * @param graph
	 * @param output_directory
	 * @throws Exception
	 */
	private static void output_dominance_graph(MutaProject project, CirInstanceGraph graph, File output_directory) throws Exception {
		File output = new File(output_directory.getAbsolutePath() + "/" + project.get_name() + ".pre");
		FileWriter writer = new FileWriter(output);
		CDominanceGraph dominance_graph = CDominanceGraph.forward_dominance_graph(graph);
		for(CDominanceNode dominance_node : dominance_graph.get_nodes()) {
			output_dominance_node(dominance_node, writer);
		}
		writer.close();
		
		File output2 = new File(output_directory.getAbsolutePath() + "/" + project.get_name() + ".pos");
		FileWriter writer2 = new FileWriter(output2);
		CDominanceGraph dominance_graph2 = CDominanceGraph.backward_dominance_graph(graph);
		for(CDominanceNode dominance_node : dominance_graph2.get_nodes()) {
			output_dominance_node(dominance_node, writer2);
		}
		writer2.close();
	}
	/**
	 * (predicate expression true|false)
	 * (reference define use)
	 * @param element
	 * @param writer
	 * @throws Exception
	 */
	private static void output_dependence_element(Object element, FileWriter writer) throws Exception {
		if(element instanceof CDependPredicate) {
			CDependPredicate predicate = (CDependPredicate) element;
			writer.write("( predicate ");
			writer.write(get_parameter_content(predicate.get_condition()));
			writer.write(" ");
			writer.write(get_parameter_content(predicate.get_predicate_value()));
			writer.write(" )");
		}
		else if(element instanceof CDependReference) {
			CDependReference reference = (CDependReference) element;
			writer.write("( reference ");
			writer.write(get_parameter_content(reference.get_def()));
			writer.write(" ");
			writer.write(get_parameter_content(reference.get_use()));
			writer.write(" )");
		}
	}
	/**
	 * [edge] source.inst target.inst dep_type {element}
	 * @param edge
	 * @param writer
	 * @throws Exception
	 */
	private static void output_dependence_edge(CDependEdge edge, FileWriter writer) throws Exception {
		writer.write("\t[edge]");
		writer.write("\t" + get_parameter_content(edge.get_source().get_instance()));
		writer.write("\t" + get_parameter_content(edge.get_target().get_instance()));
		writer.write("\t" + edge.get_type().toString());
		writer.write("\t");
		output_dependence_element(edge.get_element(), writer);
		writer.write("\n");
	}
	/**
	 * [node] instance
	 * @param node
	 * @param writer
	 * @throws Exception
	 */
	private static void output_dependence_node(CDependNode node, FileWriter writer) throws Exception {
		writer.write("[node]");
		writer.write("\t" + get_parameter_content(node.get_instance()));
		writer.write("\n");
		
		for(CDependEdge edge : node.get_ou_edges()) {
			output_dependence_edge(edge, writer);
		}
	}
	/**
	 * xxx.dep
	 * @param project
	 * @param program_graph
	 * @param output_directory
	 * @throws Exception
	 */
	private static void output_dependence_graph(MutaProject project, CirInstanceGraph program_graph, File output_directory) throws Exception {
		File output = new File(output_directory.getAbsolutePath() + "/" + project.get_name() + ".dep");
		CDependGraph graph = CDependGraph.graph(program_graph);
		FileWriter writer = new FileWriter(output);
		for(CDependNode node : graph.get_nodes()) 
			output_dependence_node(node, writer);
		writer.close();
	}
	
	/* mutation information: object + label + feature */
	/**
	 * 
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private static String get_mutant_parameter(AstMutation mutation) throws Exception {
		switch(mutation.get_mutation_class()) {
		case TTRP:
		{
			int value = (int) mutation.get_parameter();
			return get_parameter_content(value);
		}
		case CTRP:
		case SGLR:
		case SRTR:
		{
			AstNode value = (AstNode) mutation.get_parameter();
			return get_parameter_content(value);
		}
		case VINC:
		{
			if(mutation.get_mutation_operator() == MutaOperator.inc_value) {
				int value = (int) mutation.get_parameter();
				return get_parameter_content(value);
			}
			else {
				double value = (double) mutation.get_parameter();
				return get_parameter_content(value);
			}
		}
		case VCRP:
		{
			CConstant constant = (CConstant) mutation.get_parameter();
			switch(constant.get_type().get_tag()) {
			case c_bool:		
				return get_parameter_content(constant.get_bool());
			case c_char:
			case c_uchar:		
				return get_parameter_content(constant.get_char());
			case c_short:
			case c_ushort:
			case c_int:
			case c_uint:
				return get_parameter_content(constant.get_integer());
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:
				return get_parameter_content(constant.get_long());
			case c_float:
				return get_parameter_content(constant.get_float());
			case c_double:
			case c_ldouble:
				return get_parameter_content(constant.get_double());
			default: throw new IllegalArgumentException("Invalid data type");
			}
		}
		case VRRP:
		{
			CName name = (CName) mutation.get_parameter();
			if(name == null) return get_parameter_content("?");
			else return get_parameter_content(name.get_name());
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
		writer.write(get_parameter_content(mutation.get_location()) + "\t");
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
		/*
		Map<MutationCodeType, MutaTestResult> results = 
				project.get_results().read_test_results(mutant);
		MutaTestResult result = results.get(MutationCodeType.Stronger);
		writer.write(mutant.get_id() + "\t");
		writer.write(result.get_test_result().toString());
		writer.write("\n");
		*/
		writer.write(mutant.get_id() + "\t");
		for(int k = 0; k < 256; k++) writer.write("0");
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
		for(Mutant mutant : source_file.get_mutant_space().get_mutants()) {
			output_mutant_label(project, mutant, writer);
		}
		writer.close();
	}
	/**
	 * write all the nodes in symbolic expression tree into the file
	 * @param node
	 * @param writer
	 * @throws Exception
	 */
	private static void output_symbolic_node(SymNode node, FileWriter writer, int tabs) throws Exception {
		symbolic_node_code(node, writer, tabs);
		for(SymNode child : node.get_children()) {
			output_symbolic_node(child, writer, tabs);
		}
	}
	/**
	 * [error] 
	 * 		[define] type operand ...
	 * 		[extend] type operand ...
	 * 		[constraints]
	 * 			[type] conjunct | disjunct
	 * 			[constraint] 
	 * 				[execution]	id
	 * 				[sym]	xxxxxx
	 * 				[sym]	xxxxxx
	 * 				......
	 * 				[sym]	xxxxxx
	 * 			[end_constraint]
	 * 		[end_constraints]
	 * [end_error]
	 * @param error
	 * @param writer
	 * @throws Exception
	 */
	private static void output_state_error(StateError error, StateConstraints constraints, FileWriter writer) throws Exception {
		writer.write("\t[error]\n");
		writer.write("\t\t[define]");
		writer.write("\t" + error.get_type().toString());
		for(Object operand : error.get_operands()) 
			writer.write("\t" + get_parameter_content(operand));
		writer.write("\n");
		
		/* add the extension set of state error */
		List<StateError> all_errors = error.get_errors().extend(error);
		for(StateError new_error : all_errors) {
			writer.write("\t\t[extend]\t" + new_error.get_type().toString());
			for(Object operand : new_error.get_operands()) 
				writer.write("\t" + get_parameter_content(operand));
			writer.write("\n");
		}
		
		writer.write("\t\t[constraints]\n");
		if(constraints.is_conjunct())
			writer.write("\t\t\t[type]\tconjunct\n");
		else 
			writer.write("\t\t\t[type]\tdisjunct\n");
		for(StateConstraint constraint : constraints.get_constraints()) {
			writer.write("\t\t\t[constraint]\n");
			writer.write("\t\t\t\t[execution]\t" + constraint.get_execution() + "\n");
			output_symbolic_node(constraint.get_condition(), writer, 4);
			writer.write("\t\t\t[end_constraint]\n");
		}
		writer.write("\t\t[end_constraints]\n");
		
		writer.write("\t[end_error]\n");
	}
	/**
	 * [coverage] execution
	 * @param statement
	 * @param constraints
	 * @param writer
	 * @throws Exception
	 */
	private static void output_reachability(CirStatement statement, StateConstraints constraints, FileWriter writer) throws Exception {
		writer.write("\t[coverage]");
		CirExecution execution = statement.get_tree().get_function_call_graph().
				get_function(statement).get_flow_graph().get_execution(statement);
		writer.write("\t" + execution.toString());
		writer.write("\n");
	}
	/**
	 * create the infection information
	 * 	[mutant]
	 * 		[id] identifier
	 * 		[coverage] location
	 * 		[error] ... [end_error]
	 * 	[end_mutant]
	 * @param mutant
	 * @param writer
	 * @throws Exception
	 */
	private static void output_mutant_infection(CirTree cir_tree, Mutant mutant, FileWriter writer) throws Exception {
		MutantInfection infection = null;
		try {
			infection = StateInfections.infect(cir_tree, mutant);
		}
		catch(Exception ex) {
			// System.out.println("\t==> error infection for " + mutant.get_id());
			error_counter++;
		}
		if(infection != null) {
			writer.write("[mutant]\n");
			writer.write("\t[id]\t" + mutant.get_id() + "\n");
			
			output_reachability(infection.get_faulty_statement(), infection.get_path_condition(), writer);
			for(StateError error : infection.get_initial_errors()) {
				StateConstraints constraints = infection.get_infection_constraint(error);
				output_state_error(error, constraints, writer);
			}
			
			writer.write("[end_mutant]\n");
		}
	}
	/**
	 * xxx.sem
	 * @param project
	 * @param output_directory
	 * @throws Exception
	 */
	private static void output_mutant_infections(MutaProject project, File output_directory) throws Exception {
		File output = new File(output_directory.getAbsolutePath() + "/" + project.get_name() + ".sem");
		MutaSourceFile source_file = project.get_source_files().get_source_files().iterator().next();
		
		error_counter = 0;
		FileWriter writer = new FileWriter(output);
		for(Mutant mutant : source_file.get_mutant_space().get_mutants()) {
			output_mutant_infection(source_file.get_cir_tree(), mutant, writer);
		}
		System.out.println("\t\tMutants = " + source_file.get_mutant_space().size() + "\tErrors = " + error_counter);
		writer.close();
	}
	 
	/* testing method */
	/**
	 * generate feature, labeled and source information for each program
	 * and mutant under test.
	 * @param name
	 * @throws Exception
	 */
	private static void testing(String name) throws Exception {
		System.out.println("Testing on " + name);
		
		MutaProject project = create_project(name);
		File output_directory = get_output_directory(name);
		System.out.println("\t1. open mutation test project.");
		
		output_source_code(project, output_directory);
		output_ast_tree(project, output_directory);
		output_cir_tree(project, output_directory);
		output_function_graph(project, output_directory);
		System.out.println("\t2. generate the code and AST.");
		
		output_ast_mutations(project, output_directory);
		output_mutant_labels(project, output_directory);
		output_mutant_infections(project, output_directory);
		System.out.println("\t3. generate the feature info.");
		
		CirInstanceGraph program_graph = get_instance_flow_graph(project);
		output_instance_graph(project, program_graph, output_directory);
		output_dominance_graph(project, program_graph, output_directory);
		output_dependence_graph(project, program_graph, output_directory);
		System.out.println("\t4. generate flow graph info.");
		
		System.out.println();
	}
	
	/* Main Runners */
	/**
	 * MAIN TESTING
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		File cdir = new File(prefix + "ifiles");
		File[] cfiles = cdir.listFiles();
		StateInfections.set_optimize(extend_constraint);
		for(File cfile : cfiles) {
			String name = cfile.getName();
			if(name.endsWith(".c")) {
				testing(name.substring(0, name.length() - 2));
			}
		}
	}
	
}
