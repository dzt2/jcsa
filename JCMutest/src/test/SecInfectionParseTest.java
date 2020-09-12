package test;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.MutantSpace;
import com.jcsa.jcmutest.mutant.ast2mutant.MutationGenerators;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.MutaClass;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfection;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.test.cmd.CCompiler;

public class SecInfectionParseTest {
	
	private static final String root_path = "/home/dzt2/Development/Data/";
	private static final File sizeof_template_file = new File("config/cruntime.txt");
	private static final File instrument_head_file = new File("config/jcinst.h");
	private static final File preprocess_macro_file = new File("config/linux.h");
	private static final File mutation_head_file = new File("config/jcmutest.h");
	private static final long max_timeout_seconds = 5;
	private static final String result_dir = "result/inc/";
	
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
	private static void output_infection(SecInfection infection, FileWriter writer) throws Exception {
		AstMutation mutation = infection.get_mutation();
		writer.write("Mutant#" + infection.get_mutant().get_id() + "::" + 
				mutation.get_class() + "::" + mutation.get_operator() + "\n");
		AstNode location = infection.get_mutation().get_location();
		int line = location.get_location().line_of();
		String code = location.generate_code();
		if(code.contains("\n")) {
			int index = code.indexOf('\n');
			code = code.substring(0, index).strip();
		}
		writer.write("Location[" + line + "]: " + location.generate_code() + "\n");
		if(mutation.has_parameter())
			writer.write("Parameter: " + mutation.get_parameter().toString() + "\n");
		writer.write("+--------------------------------------------------------+\n");
		if(infection.has_statement())
			writer.write("\tStatement: " + infection.get_statement().generate_code(true) + "\n");
		else
			writer.write("\tStatement: #None\n");
		if(infection.has_infection_pairs()) {
			for(int k = 0; k < infection.number_of_infection_pairs(); k++) {
				SecDescription[] pairs = infection.get_infection_pair(k);
				writer.write("\tPair[" + k +"]:\n\t\t");
				/* original generation method */
				//writer.write(pairs[0].generate_code() + "\n");
				//writer.write("\t\t" + pairs[1].generate_code() + "\n");
				/* optimized generation method */
				writer.write(pairs[0].optimize().generate_code() + "\n");
				writer.write("\t\t" + pairs[1].optimize().generate_code() + "\n");
			}
		}
		else {
			writer.write("\tPair[@NONE]\n");
		}
		writer.write("+--------------------------------------------------------+\n\n");
	}
	private static void output_mutation(Mutant mutant, FileWriter writer) throws Exception {
		AstMutation mutation = mutant.get_mutation();
		writer.write("Mutant#" + mutant.get_id() + "::" + 
				mutation.get_class() + "::" + mutation.get_operator() + "\n");
		AstNode location = mutant.get_mutation().get_location();
		int line = location.get_location().line_of();
		String code = location.generate_code();
		if(code.contains("\n")) {
			int index = code.indexOf('\n');
			code = code.substring(0, index).strip();
		}
		writer.write("Location[" + line + "]: " + location.generate_code() + "\n");
		if(mutation.has_parameter())
			writer.write("Parameter: " + mutation.get_parameter().toString() + "\n");
	}
	protected static void testing(File cfile) throws Exception {
		MuTestProject project = get_project(cfile);
		System.out.println("Create project: " + cfile.getName());
		
		MuTestProjectCodeFile code_file = project.get_code_space().get_code_file(cfile);
		MutantSpace mspace = code_file.get_mutant_space();
		FileWriter writer = new FileWriter(new File(result_dir + cfile.getName() + ".txt"));
		FileWriter writer2 = new FileWriter(new File(result_dir + cfile.getName() + ".err"));
		int error = 0, total = 0;
		for(Mutant mutant : mspace.get_mutants()) {
			total++;
			try {
				SecInfection infection = SecInfection.parse(code_file.get_cir_tree(), mutant);
				// System.out.println("\t==> Output: " + mutant.get_mutation());
				output_infection(infection, writer);
			}
			catch(UnsupportedOperationException ex) {
				// System.err.println("\t==> Error: " + mutant.get_mutation());
				error++;
				output_mutation(mutant, writer2);
			}
		}
		System.out.println("Error-Rate: " + error + "/" + total);
		writer.close(); writer.close();
	}
	
}
