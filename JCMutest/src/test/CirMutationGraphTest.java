package test;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.ast2mutant.MutationGenerators;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.struct.CirDetectionLevel;
import com.jcsa.jcmutest.mutant.cir2mutant.struct.CirMutationDetection;
import com.jcsa.jcmutest.mutant.cir2mutant.struct.CirMutationNode;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.MutaClass;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
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

public class CirMutationGraphTest {
	
	private static final String root_path = "/home/dzt2/Development/Data/";
	private static final File sizeof_template_file = new File("config/cruntime.txt");
	private static final File instrument_head_file = new File("config/jcinst.h");
	private static final File preprocess_macro_file = new File("config/linux.h");
	private static final File mutation_head_file = new File("config/jcmutest.h");
	private static final long max_timeout_seconds = 5;
	private static final String result_dir = "result/cir2/";
	
	public static void main(String[] args) throws Exception {
		String name = "profit.c";
		testing(new File(root_path + "cfiles/" + name), 1000);
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
	private static Collection<CirMutationDetection> get_detectors(
			Mutant mutant, CDominanceGraph dominance_graph) throws Exception {
		List<CirMutationDetection> detectors = new ArrayList<CirMutationDetection>();
		for(CirMutation cir_mutation : mutant.get_cir_mutations()) {
			detectors.add(CirMutationDetection.new_detector(mutant.get_space().
					get_cir_mutations(), cir_mutation, dominance_graph));
		}
		return detectors;
	}
	private static void output_mutation(MuTestProject project, Mutant mutant, 
			Collection<CirMutationDetection> detectors, File output) throws Exception {
		FileWriter writer = new FileWriter(output);
		AstMutation mutation = mutant.get_mutation();
		AstNode location = mutation.get_location();
		int line = location.get_location().line_of() + 1;
		String ast_class = location.getClass().getSimpleName();
		ast_class = ast_class.substring(3, ast_class.length() - 4).strip();
		String ast_code = location.get_location().trim_code(64);
		MuTestProjectCodeFile code_file = 
				project.get_code_space().get_code_files().iterator().next();
		
		/* mutant information */
		writer.write("Mutant Information\n");
		writer.write("\tOperator: " + mutation.get_class() + "::" + mutation.get_operator() + "\n");
		writer.write("\tLocation[" + line + "]: " + ast_class + "\n");
		writer.write("\tCode: " + ast_code + "\n");
		if(mutation.has_parameter())
			writer.write("\tParameter: " + mutation.get_parameter() + "\n");
		
		/* traverse each test in the space */
		for(int tid = 0; tid < project.get_test_space().number_of_test_inputs(); tid++) {
			System.out.println("\t\t--> Analyze on test#" + tid);
			TestInput input = project.get_test_space().get_test_space().get_input(tid);
			writer.write("\n+================================================+\n");
			writer.write("Test#" + input.get_id() + ": " + input.get_parameter() + "\n");
			
			CStatePath state_path = project.get_test_space().
					load_instrumental_path(code_file.get_sizeof_template(), 
							code_file.get_ast_tree(), code_file.get_cir_tree(), 
							input);
			if(state_path != null) {
				writer.write("==> Load " + state_path.size() + " state nodes from.\n");
				for(CirMutationDetection detector : detectors) {
					writer.write("\t+---------------------------------------------+\n");
					Map<CirMutationNode, CirDetectionLevel> results = detector.detection_analysis(state_path);
					for(CirMutationNode mutation_node : results.keySet()) {
						CirDetectionLevel result = results.get(mutation_node);
						writer.write("\t" + result.toString() + ":\t");
						writer.write(mutation_node.get_mutation().toString() + "\n");
					}
					writer.write("\t+---------------------------------------------+\n");
				}
			}
			else {
				writer.write("==> No instrumental path is loaded\n");
			}
			
			writer.write("\n+================================================+\n");
		}
		
		writer.close();
	}
	private static void testing(File cfile, int mid) throws Exception {
		MuTestProject project = get_project(cfile);
		System.out.println("Testing on " + cfile.getName());
		System.out.println("\tCreate testing project.");
		
		MuTestProjectCodeFile code_file = 
				project.get_code_space().get_code_files().iterator().next();
		Mutant mutant = code_file.get_mutant_space().get_mutant(mid);
		CirInstanceGraph instance_graph = translate(code_file.get_cir_tree());
		CDominanceGraph dominance_graph = generate(instance_graph);
		Collection<CirMutationDetection> detectors = get_detectors(mutant, dominance_graph);
		File output = new File(result_dir + project.get_name() + "." + mutant.get_id() + ".txt");
		System.out.println("\tFind mutant: " + mutant.get_mutation());
		System.out.println("\tObtain " + detectors.size() + " detectors in");
		
		output_mutation(project, mutant, detectors, output);
		
		System.out.println("");
	}
	
}
