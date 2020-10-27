package test;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.ast2mutant.MutationGenerators;
import com.jcsa.jcmutest.mutant.cir2mutant.CirStateErrorWord;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.ptree.CirMutationStatus;
import com.jcsa.jcmutest.mutant.cir2mutant.ptree.CirMutationTree;
import com.jcsa.jcmutest.mutant.cir2mutant.ptree.CirMutationTreeNode;
import com.jcsa.jcmutest.mutant.cir2mutant.ptree.CirMutationTrees;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.MutaClass;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.MuTestProjectTestResult;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.flwa.context.CirCallContextInstanceGraph;
import com.jcsa.jcparse.flwa.context.CirFunctionCallPathType;
import com.jcsa.jcparse.flwa.dominate.CDominanceGraph;
import com.jcsa.jcparse.flwa.graph.CirInstanceGraph;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.test.cmd.CCompiler;
import com.jcsa.jcparse.test.file.TestInput;
import com.jcsa.jcparse.test.state.CStatePath;

public class CirMutationTreeTest {
	
	private static final String root_path = "/home/dzt2/Development/Data/";
	private static final File sizeof_template_file = new File("config/cruntime.txt");
	private static final File instrument_head_file = new File("config/jcinst.h");
	private static final File preprocess_macro_file = new File("config/linux.h");
	private static final File mutation_head_file = new File("config/jcmutest.h");
	private static final long max_timeout_seconds = 5;
	private static final String result_dir = "result/ctree/";
	
	/* project getters */
	private static String get_name(File cfile) {
		int index = cfile.getName().lastIndexOf('.');
		return cfile.getName().substring(0, index).strip();
	}
	private static Iterable<MutaClass> get_classes() {
		Set<MutaClass> classes = new HashSet<MutaClass>();
		classes.addAll(MutationGenerators.trapping_classes());
		classes.addAll(MutationGenerators.unary_classes());
		classes.addAll(MutationGenerators.statement_classes());
		classes.addAll(MutationGenerators.operator_classes());
		classes.addAll(MutationGenerators.assign_classes());
		classes.addAll(MutationGenerators.reference_classes());
		return classes;
	}
	private static MuTestProject get_project(File cfile) throws Exception {
		String name = get_name(cfile);
		File root = new File(root_path + "rprojects/" + name);
		if(!root.exists()) {
			MuTestProject project = new MuTestProject(root, MuCommandUtil.linux_util);
			
			/* set configuration data */
			List<String> parameters = new ArrayList<String>();
			parameters.add("-lm");
			project.set_config(CCompiler.clang, ClangStandard.gnu_c89, 
					parameters, sizeof_template_file, instrument_head_file, 
					preprocess_macro_file, mutation_head_file, max_timeout_seconds);
			
			/* input the code files */
			List<File> cfiles = new ArrayList<File>();
			List<File> hfiles = new ArrayList<File>();
			List<File> lfiles = new ArrayList<File>();
			cfiles.add(cfile);
			project.set_cfiles(cfiles, hfiles, lfiles);
			
			/* input the test inputs */
			File test_suite_file = new File(root_path + "tests/" + name + ".txt");
			List<File> test_suite_files = new ArrayList<File>();
			if(test_suite_file.exists()) test_suite_files.add(test_suite_file);
			File inputs_directory = new File(root_path + "vinputs/");
			if(!inputs_directory.exists()) FileOperations.mkdir(inputs_directory);
			project.set_inputs_directory(inputs_directory);
			project.add_test_inputs(test_suite_files);
			
			/* generate mutations */
			project.generate_mutants(get_classes());
			
			return project;
		}
		else {
			return new MuTestProject(root, MuCommandUtil.linux_util);
		}
	}
	private static CirCallContextInstanceGraph translate(CirTree cir_tree) throws Exception {
		CirFunction root_function = cir_tree.get_function_call_graph().get_function("main");
		return CirCallContextInstanceGraph.graph(root_function, 
				CirFunctionCallPathType.unique_path, -1);
	}
	private static CDominanceGraph generate(CirInstanceGraph graph) throws Exception {
		return CDominanceGraph.forward_dominance_graph(graph);
	}
	
	/* generation methods */
	/**
	 * @param text
	 * @return the normalized text without spaces and \n
	 */
	private static String normalize_text(String text) {
		StringBuilder buffer = new StringBuilder();
		for(int k = 0; k < text.length(); k++) {
			char ch = text.charAt(k);
			if(Character.isWhitespace(ch)) {
				ch = ' ';
			}
			buffer.append(ch);
		}
		return buffer.toString();
	}
	/**
	 * put N tabs before line
	 * @param writer
	 * @param tabs
	 * @throws Exception
	 */
	private static void new_line(FileWriter writer, int tabs) throws Exception {
		writer.write("\n");
		for(int k = 0; k < tabs; k++) writer.write("\t");
	}
	
	/* detection summary */
	/**
	 * @param tree_node
	 * @param writer
	 * @param tabs
	 * @throws Exception
	 */
	private static void output_level(CirMutationTreeNode tree_node, 
			CirMutationStatus level, FileWriter writer, int tabs) throws Exception {
		new_line(writer, tabs);
		writer.write("[TreeNode]");
		tabs++;
		{
			new_line(writer, tabs);
			writer.write("Statement: ");
			writer.write(normalize_text(tree_node.get_cir_mutation().get_statement().generate_code(true)));
			new_line(writer, tabs);
			writer.write("--> " + level.get_execution_times() + " times.");
			
			new_line(writer, tabs);
			writer.write("Constraint: ");
			writer.write(normalize_text(tree_node.get_cir_mutation().get_constraint().toString()));
			new_line(writer, tabs);
			writer.write("--> " + level.get_constraint_acceptions() + " accepted & " + 
						level.get_constraint_rejections() + " rejected.");
			
			new_line(writer, tabs);
			writer.write("StateError: ");
			writer.write(normalize_text(tree_node.get_cir_mutation().get_state_error().toString()));
			new_line(writer, tabs);
			writer.write("--> ");
			writer.write(level.get_state_error_acceptions() + " accepted & " + level.get_state_error_rejections() + " rejected: ");
			for(CirStateErrorWord word : level.get_error_words()) {
				writer.write(word.toString() + "; ");
			}
		}
		tabs--;
		new_line(writer, tabs);
		writer.write("[TreeNode]");
	}
	/**
	 * @param tree
	 * @param results
	 * @param writer
	 * @param tabs
	 * @throws Exception
	 */
	private static void output_level(CirMutationTree tree, 
			Map<CirMutationTreeNode, CirMutationStatus> results, 
			FileWriter writer, int tabs) throws Exception {
		new_line(writer, tabs);
		writer.write("[Tree]");
		
		tabs++;
		{
			Queue<CirMutationTreeNode> queue = new LinkedList<CirMutationTreeNode>();
			queue.add(tree.get_root());
			while(!queue.isEmpty()) {
				CirMutationTreeNode tree_node = queue.poll();
				output_level(tree_node, results.get(tree_node), writer, tabs);
				for(CirMutationTreeNode child : tree_node.get_children()) {
					queue.add(child);
				}
			}
		}
		tabs--;
		
		new_line(writer, tabs);
		writer.write("[Tree]");
	}
	private static void output_level(MuTestProject project, Mutant mutant, CStatePath path, int tid,
			FileWriter writer, CirTree cir_tree, CDominanceGraph dominance_graph) throws Exception {
		/* getters */
		CirMutationTrees trees = CirMutationTrees.new_trees(cir_tree, mutant, dominance_graph);
		Map<CirMutationTreeNode, CirMutationStatus> results = trees.abs_interpret(path);
		
		int tabs = 0;
		new_line(writer, tabs);
		writer.write("[Mutant]");
		
		//tabs++;
		{
			AstMutation mutation = mutant.get_mutation();
			AstNode location = mutation.get_location();
			int line = location.get_location().line_of() + 1;
			
			new_line(writer, tabs);
			writer.write("Class: ");
			writer.write(mutation.get_class() + "::" + mutation.get_operator());
			
			new_line(writer, tabs);
			writer.write("Line#" + line + ": ");
			String code = normalize_text(location.generate_code());
			if(code.length() > 512) {
				code = code.substring(0, 512);
			}
			writer.write(code);
			
			if(mutation.has_parameter()) {
				new_line(writer, tabs);
				writer.write("Parameter: ");
				writer.write(mutation.get_parameter().toString());
			}
			
			MuTestProjectTestResult tresult = project.get_test_space().get_test_result(mutant);
			new_line(writer, tabs);
			writer.write("Result: ");
			if(tresult == null) {
				writer.write("Not_Executed");
			}
			else if(tresult.get_exec_set().get(tid)){
				writer.write("Executed_");
				if(tresult.get_kill_set().get(tid)) {
					writer.write("Killed");
				}
				else {
					writer.write("Alived");
				}
			}
			else {
				writer.write("Not_Executed");
			}
			
			tabs++;
			for(CirMutationTree tree : trees.get_trees()) {
				output_level(tree, results, writer, tabs);
			}
			tabs--;
		}
		//tabs--;
		
		new_line(writer, tabs);
		writer.write("[Mutant]");
		new_line(writer, tabs);
	}
	protected static void output_level(MuTestProject project, int test_id) throws Exception {
		/* declarations */
		TestInput test = project.get_test_space().get_test_space().get_input(test_id);
		MuTestProjectCodeFile cfile = project.get_code_space().get_code_files().iterator().next();
		CStatePath path = project.get_test_space().load_instrumental_path(
				cfile.get_sizeof_template(), cfile.get_ast_tree(), cfile.get_cir_tree(), test);
		CDominanceGraph dominance_graph = generate(translate(cfile.get_cir_tree()));
		
		/* output file */
		String output_name;
		if(path == null) {
			output_name = result_dir + project.get_name() + ".sum";
		}
		else {
			output_name = result_dir + project.get_name() + "." + test_id + ".sum";
		}
		FileWriter writer = new FileWriter(new File(output_name));
		writer.write("Test: " + test.get_parameter() + "\n\n");
		
		/* generation */
		for(Mutant mutant : cfile.get_mutant_space().get_mutants()) {
			output_level(project, mutant, path, test_id, writer, cfile.get_cir_tree(), dominance_graph);
			System.out.println("\t==> Complete mutant [" + mutant.get_id() + "/" + cfile.get_mutant_space().size() + "]");
		}
		writer.close();
	}
	
	/* detection details */
	private static void output_details(CirMutationTreeNode tree_node, 
			Iterable<CirMutation> results, FileWriter writer, int tabs) throws Exception {
		new_line(writer, tabs);
		writer.write("[TreeNode]");
		tabs++;
		{
			new_line(writer, tabs);
			writer.write("Statement: ");
			writer.write(normalize_text(tree_node.get_cir_mutation().get_statement().generate_code(true)));
			new_line(writer, tabs);
			writer.write("Constraint: ");
			writer.write(normalize_text(tree_node.get_cir_mutation().get_constraint().toString()));
			new_line(writer, tabs);
			writer.write("StateError: ");
			writer.write(normalize_text(tree_node.get_cir_mutation().get_state_error().toString()));
			new_line(writer, tabs);
			writer.write("Detection-Details: ");
			tabs++;
			for(CirMutation result : results) {
				new_line(writer, tabs);
				writer.write(result.toString());
			}
			tabs--;
		}
		tabs--;
		new_line(writer, tabs);
		writer.write("[TreeNode]");
	}
	private static void output_details(CirMutationTree tree, 
			Map<CirMutationTreeNode, List<CirMutation>> results, 
			FileWriter writer, int tabs) throws Exception {
		new_line(writer, tabs);
		writer.write("[Tree]");
		
		tabs++;
		{
			Queue<CirMutationTreeNode> queue = new LinkedList<CirMutationTreeNode>();
			queue.add(tree.get_root());
			while(!queue.isEmpty()) {
				CirMutationTreeNode tree_node = queue.poll();
				output_details(tree_node, results.get(tree_node), writer, tabs);
				for(CirMutationTreeNode child : tree_node.get_children()) {
					queue.add(child);
				}
			}
		}
		tabs--;
		
		new_line(writer, tabs);
		writer.write("[Tree]");
	}
	private static void output_details(MuTestProject project, Mutant mutant, CStatePath path, int tid,
			FileWriter writer, CirTree cir_tree, CDominanceGraph dominance_graph) throws Exception {
		/* getters */
		CirMutationTrees trees = CirMutationTrees.new_trees(cir_tree, mutant, dominance_graph);
		Map<CirMutationTreeNode, List<CirMutation>> results = trees.con_interpret(path);
		
		int tabs = 0;
		new_line(writer, tabs);
		writer.write("[Mutant]");
		
		//tabs++;
		{
			AstMutation mutation = mutant.get_mutation();
			AstNode location = mutation.get_location();
			int line = location.get_location().line_of() + 1;
			
			new_line(writer, tabs);
			writer.write("Class: ");
			writer.write(mutation.get_class() + "::" + mutation.get_operator());
			
			new_line(writer, tabs);
			writer.write("Line#" + line + ": ");
			String code = normalize_text(location.generate_code());
			if(code.length() > 512) {
				code = code.substring(0, 512);
			}
			writer.write(code);
			
			if(mutation.has_parameter()) {
				new_line(writer, tabs);
				writer.write("Parameter: ");
				writer.write(mutation.get_parameter().toString());
			}
			
			MuTestProjectTestResult tresult = project.get_test_space().get_test_result(mutant);
			new_line(writer, tabs);
			writer.write("Result: ");
			if(tresult == null) {
				writer.write("Not_Executed");
			}
			else if(tresult.get_exec_set().get(tid)){
				writer.write("Executed_");
				if(tresult.get_kill_set().get(tid)) {
					writer.write("Killed");
				}
				else {
					writer.write("Alived");
				}
			}
			else {
				writer.write("Not_Executed");
			}
			
			tabs++;
			for(CirMutationTree tree : trees.get_trees()) {
				output_details(tree, results, writer, tabs);
			}
			tabs--;
		}
		//tabs--;
		
		new_line(writer, tabs);
		writer.write("[Mutant]");
		new_line(writer, tabs);
	}
	protected static void output_details(MuTestProject project, int test_id) throws Exception {
		/* declarations */
		TestInput test = project.get_test_space().get_test_space().get_input(test_id);
		MuTestProjectCodeFile cfile = project.get_code_space().get_code_files().iterator().next();
		CStatePath path = project.get_test_space().load_instrumental_path(
				cfile.get_sizeof_template(), cfile.get_ast_tree(), cfile.get_cir_tree(), test);
		CDominanceGraph dominance_graph = generate(translate(cfile.get_cir_tree()));
		
		/* output file */
		String output_name;
		if(path == null) {
			output_name = result_dir + project.get_name() + ".mut";
		}
		else {
			output_name = result_dir + project.get_name() + "." + test_id + ".mut";
		}
		FileWriter writer = new FileWriter(new File(output_name));
		writer.write("Test: " + test.get_parameter() + "\n\n");
		
		/* generation */
		for(Mutant mutant : cfile.get_mutant_space().get_mutants()) {
			output_details(project, mutant, path, test_id, writer, cfile.get_cir_tree(), dominance_graph);
			System.out.println("\t==> Complete mutant [" + mutant.get_id() + "/" + cfile.get_mutant_space().size() + "]");
		}
		writer.close();
	}
	
	/* testing method */
	protected static void testing(String name, int tid) throws Exception {
		File cfile = new File(root_path + "cfiles/" + name + ".c");
		MuTestProject project = get_project(cfile);
		System.out.println("1. Get mutation project for " + project.get_name());
		
		output_details(project, tid);
		// output_levels(project, tid);
		output_level(project, tid);
		
		System.out.println("2. Output the mutation information to XML.");
		System.out.println();
	}
	public static void main(String[] args) throws Exception {
		String name = "bi_search";
		int tid = 64;
		testing(name, tid);
	}
	
}
