package test;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.ast2mutant.MutationGenerators;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.MutaClass;
import com.jcsa.jcmutest.mutant.sec2mutant.util.SecStateEdge;
import com.jcsa.jcmutest.mutant.sec2mutant.util.SecStateGraph;
import com.jcsa.jcmutest.mutant.sec2mutant.util.SecStateNode;
import com.jcsa.jcmutest.mutant.sec2mutant.util.prog.SecExpressionPropagators;
import com.jcsa.jcmutest.mutant.sec2mutant.util.prog.SecStatementPropagators;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.flwa.context.CirCallContextInstanceGraph;
import com.jcsa.jcparse.flwa.context.CirFunctionCallPathType;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.test.cmd.CCompiler;

public class SecStatePropagationTest {
	
	private static final String root_path = "/home/dzt2/Development/Data/";
	private static final File sizeof_template_file = new File("config/cruntime.txt");
	private static final File instrument_head_file = new File("config/jcinst.h");
	private static final File preprocess_macro_file = new File("config/linux.h");
	private static final File mutation_head_file = new File("config/jcmutest.h");
	private static final long max_timeout_seconds = 5;
	private static final String result_dir = "result/pro/";
	
	public static void main(String[] args) throws Exception {
		for(File cfile : new File(root_path + "cfiles").listFiles()) {
			if(cfile.getName().endsWith(".c")) {
				System.out.println("+-----------------------------------------+");
				testing(cfile);
				System.out.println("+-----------------------------------------+\n");
			}
		}
	}
	
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
		File root = new File(root_path + "mprojects/" + name);
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
	private static CirCallContextInstanceGraph translate(CirTree cir_tree) throws Exception {
		CirFunction root_function = cir_tree.get_function_call_graph().get_function("main");
		return CirCallContextInstanceGraph.graph(root_function, 
				CirFunctionCallPathType.unique_path, -1);
	}
	private static CDependGraph dependence_graph(CirTree cir_tree) throws Exception {
		return CDependGraph.graph(translate(cir_tree));
	}
	private static SecStateGraph state_error_graph(
			CirTree cir_tree, Mutant mutant, CDependGraph dependence_graph) throws Exception {
		SecStateGraph graph = new SecStateGraph(cir_tree, mutant);
		graph.initialize(dependence_graph);
		try {
			if(graph.has_reach_node()) {
				List<SecStateNode> next_nodes = new ArrayList<SecStateNode>();
				for(SecStateEdge edge : graph.get_reach_node().get_ou_edges()) {
					next_nodes.addAll(SecExpressionPropagators.
							propagate(edge.get_target(), null));
				}
				
				for(SecStateNode next_node : next_nodes) {
					SecStatementPropagators.propagate(next_node, dependence_graph);
				}
			}
		}
		catch(Exception ex) {
			graph = null;
		}
		return graph;
	}
	private static void write_mutation(Mutant mutant, SecStateGraph graph, FileWriter writer) throws Exception {
		AstMutation mutation = mutant.get_mutation();
		AstNode location = mutation.get_location();
		writer.write("Mutant#" + mutant.get_id() + "\n");
		writer.write("Class: " + mutation.get_class() + "@" + mutation.get_operator() + "\n");
		String code = location.generate_code();
		if(code.contains("\n")) {
			code = code.substring(0, code.indexOf('\n'));
		}
		writer.write("Location#" + location.get_location().line_of() + ": " + code.strip() + "\n");
		if(mutation.has_parameter()) {
			writer.write("Parameter: " + mutation.get_parameter() + "\n");
		}
		
		if(graph != null) {
			/* state-graph information */
			if(graph.has_reach_node()) {
				Queue<SecStateNode> queue = new LinkedList<SecStateNode>();
				queue.add(graph.get_reach_node());
				writer.write("State-Graph\n");
				writer.write("{\n");
				while(!queue.isEmpty()) {
					SecStateNode node = queue.poll();
					writer.write("\tNode: " + node.toString() + "\n");
					if(node.is_state_error())
						writer.write("\t\tErrors: " + node.get_state_error().extend(null) + "\n");
					for(SecStateEdge edge : node.get_ou_edges()) {
						writer.write("\t\t==(" + edge.get_type() + ")==> " + edge.get_constraint().optimize(null) + "\n");
						writer.write("\t\t\t==> " + edge.get_target() + "\n");
						queue.add(edge.get_target());
					}
				}
				writer.write("}\n");
			}
		}
		writer.write("+------------------------------------------------------+\n\n");
	}
	private static void write_state_graph(SecStateGraph graph, FileWriter writer) throws Exception {
		writer.write("+------------------------------------------------------+\n");
		/* mutation information */
		Mutant mutant = graph.get_mutant();
		AstMutation mutation = mutant.get_mutation();
		AstNode location = mutation.get_location();
		writer.write("Mutant#" + mutant.get_id() + "\n");
		writer.write("Class: " + mutation.get_class() + "@" + mutation.get_operator() + "\n");
		String code = location.generate_code();
		if(code.contains("\n")) {
			code = code.substring(0, code.indexOf('\n'));
		}
		writer.write("Location#" + location.get_location().line_of() + ": " + code.strip() + "\n");
		if(mutation.has_parameter()) {
			writer.write("Parameter: " + mutation.get_parameter() + "\n");
		}
		
		/* state-graph information */
		if(graph.has_reach_node()) {
			Queue<SecStateNode> queue = new LinkedList<SecStateNode>();
			queue.add(graph.get_reach_node());
			writer.write("State-Graph\n");
			writer.write("{\n");
			while(!queue.isEmpty()) {
				SecStateNode node = queue.poll();
				writer.write("\tNode: " + node.toString() + "\n");
				if(node.is_state_error())
					writer.write("\t\tErrors: " + node.get_state_error().extend(null) + "\n");
				for(SecStateEdge edge : node.get_ou_edges()) {
					writer.write("\t\t==(" + edge.get_type() + ")==> " + edge.get_constraint().optimize(null) + "\n");
					writer.write("\t\t\t==> " + edge.get_target() + "\n");
					queue.add(edge.get_target());
				}
			}
			writer.write("}\n");
		}
		writer.write("+------------------------------------------------------+\n\n");
	}
	protected static void testing(File cfile) throws Exception {
		MuTestProject project = get_project(cfile);
		MuTestProjectCodeFile code_file = 
				project.get_code_space().get_code_file(cfile);
		System.out.println("1. Get mutation test project: " + cfile.getName());
		
		CDependGraph dgraph = dependence_graph(code_file.get_cir_tree());
		System.out.println("2. Get program dependence graph.");
		
		FileWriter writer = new FileWriter(result_dir + cfile.getName() + ".sec");
		FileWriter writer2 = new FileWriter(result_dir + cfile.getName() + ".err");
		int error = 0, total = 0;
		for(Mutant mutant : code_file.get_mutant_space().get_mutants()) {
			// System.out.println("\t\t==> Mutant#" + mutant.get_id());
			SecStateGraph sgraph = state_error_graph(code_file.get_cir_tree(), mutant, dgraph);
			if(sgraph != null && sgraph.has_reach_node()) {
				write_state_graph(sgraph, writer);
			}
			else {
				write_mutation(mutant, sgraph, writer2);
				error++;
			}
			total++;
		}
		writer.close(); writer2.close();
		System.out.println("3. Error-Rate: " + error + "/" + total);
	}
	
}
