package com.jcsa.jcmutest.mutant.cir2mutant.write;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.MutantSpace;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirExpressionError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirFlowError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirReferenceError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirStateValueError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirTrapError;
import com.jcsa.jcmutest.mutant.cir2mutant.ptree.CirMutationStatus;
import com.jcsa.jcmutest.mutant.cir2mutant.ptree.CirMutationTree;
import com.jcsa.jcmutest.mutant.cir2mutant.ptree.CirMutationTreeNode;
import com.jcsa.jcmutest.mutant.cir2mutant.ptree.CirMutationTrees;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.MuTestProjectTestResult;
import com.jcsa.jcparse.flwa.dominate.CDominanceGraph;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
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
 * Used to write symbolic expression, constraint, state_error,
 * cir-mutation, cir-mutation-tree & cir-mutation-status.
 * 
 * @author yukimula
 *
 */
public class MuTestWordFeatureWriter {
	
	/* symbolic node encoding */
	/**
	 * write the code of the symbolic node into the file stream
	 * @param node
	 * @param writer
	 * @throws Exception
	 */
	private static void write_sym_node(SymNode node, FileWriter writer) throws Exception {
		if(node == null)
			throw new IllegalArgumentException("Invalid node: null");
		else if(node instanceof SymIdentifier)
			write_sym_identifier((SymIdentifier) node, writer);
		else if(node instanceof SymConstant)
			write_sym_constant((SymConstant) node, writer);
		else if(node instanceof SymLiteral)
			write_sym_literal((SymLiteral) node, writer);
		else if(node instanceof SymUnaryExpression)
			write_sym_unary_expression((SymUnaryExpression) node, writer);
		else if(node instanceof SymBinaryExpression)
			write_sym_binary_expression((SymBinaryExpression) node, writer);
		else if(node instanceof SymOperator)
			write_sym_operator((SymOperator) node, writer);
		else if(node instanceof SymCallExpression)
			write_sym_call_expression((SymCallExpression) node, writer);
		else if(node instanceof SymArgumentList)
			write_sym_argument_list((SymArgumentList) node, writer);
		else if(node instanceof SymFieldExpression)
			write_sym_field_expression((SymFieldExpression) node, writer);
		else if(node instanceof SymField)
			write_sym_field((SymField) node, writer);
		else if(node instanceof SymInitializerList)
			write_sym_initializer_list((SymInitializerList) node, writer);
		else if(node instanceof SymLabel)
			write_sym_label((SymLabel) node, writer);
		else
			throw new IllegalArgumentException(node.generate_code());
	}
	private static void write_sym_identifier(SymIdentifier node, FileWriter writer) throws Exception {
		String name = node.get_name();
		int index = name.indexOf('@');
		String prev = name.substring(0, index).strip();
		if(prev.isEmpty()) {
			AstNode ast_node = node.get_ast_source();
			if(ast_node == null)
				ast_node = node.get_cir_source().get_ast_source();
			String code = ast_node.generate_code();
			for(int k = 0; k < code.length(); k++) {
				char ch = code.charAt(k);
				if(Character.isWhitespace(ch))
					ch = ' ';
				writer.write(ch);
			}
		}
		else {
			writer.write(prev);
		}
		
	}
	private static void write_sym_constant(SymConstant node, FileWriter writer) throws Exception {
		writer.write(node.generate_code());
	}
	private static void write_sym_literal(SymLiteral node, FileWriter writer) throws Exception {
		writer.write("@Literal");
	}
	private static void write_sym_unary_expression(SymUnaryExpression node, FileWriter writer) throws Exception {
		write_sym_node(node.get_operator(), writer);
		writer.write("(");
		write_sym_node(node.get_operand(), writer);
		writer.write(")");
	}
	private static void write_sym_binary_expression(SymBinaryExpression node, FileWriter writer) throws Exception {
		writer.write("(");
		write_sym_node(node.get_loperand(), writer);
		writer.write(") ");
		write_sym_node(node.get_operator(), writer);
		writer.write(" (");
		write_sym_node(node.get_roperand(), writer);
		writer.write(")");
	}
	private static void write_sym_call_expression(SymCallExpression node, FileWriter writer) throws Exception {
		write_sym_node(node.get_function(), writer);
		write_sym_node(node.get_argument_list(), writer);
	}
	private static void write_sym_argument_list(SymArgumentList node, FileWriter writer) throws Exception {
		writer.write("(");
		for(int k = 0; k < node.number_of_arguments(); k++) {
			write_sym_node(node.get_argument(k), writer);
			if(k < node.number_of_arguments() - 1) {
				writer.write(", ");
			}
		}
		writer.write(")");
	}
	private static void write_sym_field_expression(SymFieldExpression node, FileWriter writer) throws Exception {
		writer.write("(");
		write_sym_node(node.get_body(), writer);
		writer.write(").");
		write_sym_node(node.get_field(), writer);
	}
	private static void write_sym_field(SymField node, FileWriter writer) throws Exception {
		writer.write(node.get_name());
	}
	private static void write_sym_initializer_list(SymInitializerList node, FileWriter writer) throws Exception {
		writer.write("{");
		for(int k = 0; k < node.number_of_elements(); k++) {
			write_sym_node(node.get_element(k), writer);
			if(k < node.number_of_elements() - 1) {
				writer.write(", ");
			}
		}
		writer.write("}");
	}
	private static void write_sym_label(SymLabel node, FileWriter writer) throws Exception {
		writer.write("->" + node.get_execution());
	}
	private static void write_sym_operator(SymOperator node, FileWriter writer) throws Exception {
		writer.write(node.generate_code());
	}
	
	/* constraint, state_error, cir_mutation */
	/**
	 * execution::condition
	 * @param constraint
	 * @param writer
	 * @throws Exception
	 */
	private static void write_constraint(CirConstraint constraint, FileWriter writer) throws Exception {
		writer.write(constraint.get_execution() + "::(");
		write_sym_node(constraint.get_condition(), writer);
		writer.write(")");
	}
	/**
	 * execution::flow(orig_target, muta_target)
	 * execution::trap()
	 * execution::expr(location, orig, muta)
	 * execution::refer(location, orig, muta)
	 * execution::refer(location, orig, muta)
	 * 
	 * @param state_error
	 * @param writer
	 * @throws Exception
	 */
	private static void write_state_error(CirStateError state_error, FileWriter writer) throws Exception {
		writer.write(state_error.get_execution() + "::");
		if(state_error instanceof CirFlowError) {
			writer.write("flow(");
			writer.write(((CirFlowError) state_error).get_original_flow().get_target() + ", ");
			writer.write(((CirFlowError) state_error).get_mutation_flow().get_target() + ")");
		}
		else if(state_error instanceof CirTrapError) {
			writer.write("trap()");
		}
		else if(state_error instanceof CirExpressionError) {
			writer.write("expr(");
			writer.write(MuTestWritingUtils.encode(((CirExpressionError) state_error).get_expression()));
			writer.write(", ");
			write_sym_node(((CirExpressionError) state_error).get_original_value(), writer);
			writer.write(", ");
			write_sym_node(((CirExpressionError) state_error).get_mutation_value(), writer);
			writer.write(")");
		}
		else if(state_error instanceof CirReferenceError) {
			writer.write("refer(");
			writer.write(MuTestWritingUtils.encode(((CirReferenceError) state_error).get_reference()));
			writer.write(", ");
			write_sym_node(((CirReferenceError) state_error).get_original_value(), writer);
			writer.write(", ");
			write_sym_node(((CirReferenceError) state_error).get_mutation_value(), writer);
			writer.write(")");
		}
		else if(state_error instanceof CirStateValueError) {
			writer.write("state(");
			writer.write(MuTestWritingUtils.encode(((CirStateValueError) state_error).get_reference()));
			writer.write(", ");
			write_sym_node(((CirStateValueError) state_error).get_original_value(), writer);
			writer.write(", ");
			write_sym_node(((CirStateValueError) state_error).get_mutation_value(), writer);
			writer.write(")");
		}
		else {
			throw new IllegalArgumentException(state_error.toString());
		}
	}
	/**
	 * #CirMutation constraint state_error #EndMutation
	 * @param cir_mutation
	 * @param writer
	 * @throws Exception
	 */
	private static void write_cir_mutation(CirMutation cir_mutation, FileWriter writer) throws Exception {
		writer.write("#CirMutation");
		writer.write("\t");
		write_constraint(cir_mutation.get_constraint(), writer);
		writer.write("\t");
		write_state_error(cir_mutation.get_state_error(), writer);
		writer.write("\t#EndMutation");
	}
	
	/* cir-mutation trees as feature template */
	/**
	 * 	[tree_node] id
	 * 		{cir_mutation}
	 * 		[children] id1 id2 ... idN
	 * 	[end_tree_node]
	 * @param tree_node
	 * @param writer
	 * @throws Exception
	 */
	private static void write_cir_mutation_tree_node(
			CirMutationTreeNode tree_node, FileWriter writer) throws Exception {
		writer.write("[tree_node]\t" + tree_node.get_tree_node_id() + "\n");
		writer.write("\t");
		write_cir_mutation(tree_node.get_cir_mutation(), writer);
		writer.write("\t[children]");
		for(CirMutationTreeNode child : tree_node.get_children()) {
			writer.write("\t" + child.get_tree_node_id());
		}
		writer.write("\n[end_tree_node]\n");
		
		for(CirMutationTreeNode child : tree_node.get_children()) {
			write_cir_mutation_tree_node(child, writer);
		}
	}
	/**
	 * 	[tree]
	 * 		[statement]	execution
	 * 		[path] C1 C2 C3 ... CL
	 * 		[tree_node]...[end_tree_node]
	 * 		...
	 * 		...
	 * 		[tree_node]...[end_tree_node]
	 * 	[end_tree]
	 * @param tree
	 * @param writer
	 * @throws Exception
	 */
	private static void write_cir_mutation_tree(CirMutationTree tree, FileWriter writer) throws Exception {
		writer.write("[tree]\n");
		
		CirStatement statement = tree.get_root_mutation().get_statement();
		CirExecution execution = statement.get_tree().get_localizer().get_execution(statement);
		writer.write("[statement]\t" + execution + "\n");
		
		writer.write("[path]");
		for(CirConstraint constraint : tree.get_path_constraints()) {
			writer.write("\t");
			write_constraint(constraint, writer);
		}
		writer.write("\n");
		
		write_cir_mutation_tree_node(tree.get_root(), writer);
		writer.write("[end_tree]\n");
	}
	/**
	 * 	[trees]
	 * 		[tree]...[end_tree]
	 * 		...
	 * 		[tree]...[end_tree]
	 * 	[end_trees]
	 * @param trees
	 * @param writer
	 * @throws Exception
	 */
	private static void write_cir_mutation_trees(CirMutationTrees trees,
			FileWriter writer) throws Exception {
		writer.write("[trees]\n");
		for(CirMutationTree tree : trees.get_trees()) {
			write_cir_mutation_tree(tree, writer);
		}
		writer.write("[end_trees]\n");
	}
	/**
	 * write the state error trees to the file for each mutant in the space as:
	 * 	[mutant] id
	 * 		[trees]...[end_trees]
	 * 	[end_mutant]
	 * @param space
	 * @param tfile
	 * @throws Exception
	 */
	protected static void write_mutant_trees(MutantSpace space, File tfile, CDominanceGraph dominance_graph) throws Exception {
		if(space == null)
			throw new IllegalArgumentException("Invalid space: null");
		else if(tfile == null)
			throw new IllegalArgumentException("Invalid tfile: null");
		else if(dominance_graph == null)
			throw new IllegalArgumentException("No dominance graphs");
		else {
			FileWriter writer = new FileWriter(tfile);
			for(Mutant mutant : space.get_mutants()) {
				CirMutationTrees trees = CirMutationTrees.new_trees(
						space.get_cir_tree(), mutant, dominance_graph);
				writer.write("[mutant]\t" + mutant.get_id() + "\n");
				write_cir_mutation_trees(trees, writer);
				writer.write("[end_mutant]\n");
			}
			writer.close();
			System.out.println("\t--> Write " + tfile.getName());
		}
	}
	
	/* cir-mutation status */
	/**
	 * [tree_id, exec_times, constraint_accept, constraint_reject, state_error_accept, state_error_reject]
	 * @param tree_node
	 * @param status
	 * @throws Exception
	 */
	private static void write_cir_mutation_status(CirMutationTreeNode tree_node, 
			CirMutationStatus status, FileWriter writer) throws Exception {
		writer.write("[");
		writer.write(" " + tree_node.get_tree_node_id());
		writer.write(" " + status.get_execution_times());
		writer.write(" " + status.get_constraint_acceptions());
		writer.write(" " + status.get_constraint_rejections());
		writer.write(" " + status.get_state_error_acceptions());
		writer.write(" " + status.get_state_error_rejections());
		writer.write(" ]");
	}
	/**
	 * mid tid 1(killed)/0(alive)/2(unknown) status status ... status
	 * @param mutant
	 * @param trees
	 * @param input
	 * @param path
	 * @throws Exception
	 */
	private static void write_mutant_status(Mutant mutant, CirMutationTrees trees, TestInput input, 
			CStatePath path, MuTestProjectTestResult result, FileWriter writer) throws Exception {
		writer.write(mutant.get_id() + "\t");
		writer.write(input.get_id() + "\t");
		if(result != null && result.get_exec_set().get(input.get_id())) {
			if(result.get_kill_set().get(input.get_id())) {
				writer.write("1");
			}
			else {
				writer.write("0");
			}
		}
		else {
			writer.write("2");
		}
		
		Map<CirMutationTreeNode, CirMutationStatus> results = trees.sum_interpret(path);
		for(CirMutationTreeNode tree_node : results.keySet()) {
			writer.write("\t");
			CirMutationStatus status = results.get(tree_node);
			write_cir_mutation_status(tree_node, status, writer);
		}
		
		writer.write("\n");
	}
	/**
	 * @param space
	 * @param input
	 * @param writer
	 * @param dominance_graph
	 * @throws Exception
	 */
	protected static void write_mutants_test_results(MuTestProjectCodeFile code_file, TestInput input, 
			File tfile, CDominanceGraph dominance_graph) throws Exception {
		if(code_file == null)
			throw new IllegalArgumentException("Invalid code_file: null");
		else if(input == null)
			throw new IllegalArgumentException("Invalid input: null");
		else if(tfile == null)
			throw new IllegalArgumentException("Invalid tfile: null");
		else if(dominance_graph == null)
			throw new IllegalArgumentException("Invalid dominance_graph");
		else {
			FileWriter writer = new FileWriter(tfile);
			CStatePath path = code_file.get_code_space().get_project().get_test_space().
					load_instrumental_path(code_file.get_sizeof_template(), 
							code_file.get_ast_tree(), code_file.get_cir_tree(), input);
			for(Mutant mutant : code_file.get_mutant_space().get_mutants()) {
				CirMutationTrees trees = CirMutationTrees.new_trees(
						mutant.get_space().get_cir_tree(), mutant, dominance_graph);
				MuTestProjectTestResult result = code_file.get_code_space().
						get_project().get_test_space().get_test_result(mutant);
				write_mutant_status(mutant, trees, input, path, result, writer);
			}
			writer.close();
			System.out.println("\t--> Write " + tfile.getName());
		}
	}
	
	/* generation methods */
	public static void write(MuTestProject project, CDominanceGraph dominance_graph, File tdir) throws Exception {
		if(tdir == null || !tdir.isDirectory()) 
			throw new IllegalArgumentException("Invalid tdir: null");
		else {
			MuTestProjectCodeFile code_file = project.
					get_code_space().get_code_files().iterator().next();
			String name = MuTestWritingUtils.basename_without_postfix(code_file.get_cfile());
			
			write_mutant_trees(code_file.get_mutant_space(), new 
					File(tdir.getAbsolutePath() + "/" + name + ".tre"), dominance_graph);
			for(TestInput input : project.get_test_space().get_test_space().get_inputs()) {
				File tfile = new File(tdir.getAbsolutePath() + "/" + name + "." + input.get_id() + ".res");
				write_mutants_test_results(code_file, input, tfile, dominance_graph);
			}
		}
	}
	
}
