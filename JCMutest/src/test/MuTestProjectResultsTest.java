package test;

import java.io.File;
import java.io.FileWriter;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.MuTestProjectTestResult;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.lang.astree.AstNode;

public class MuTestProjectResultsTest {
	
	private static final String root_path = "/home/dzt2/Development/Data/rprojects/";
	private static final String result_dir = "result/res/";
	
	public static void main(String[] args) throws Exception {
		for(File root : new File(root_path).listFiles()) {
			System.out.println("Testing on " + root.getName());
			testing(root);
		}
	}
	
	private static MuTestProject get_project(File root) throws Exception {
		return new MuTestProject(root, MuCommandUtil.linux_util);
	}
	private static void output_mutations(MuTestProject project, File output) throws Exception {
		FileWriter writer = new FileWriter(output);
		writer.write("Project: " + project.get_name() + "\n");
		
		MuTestProjectCodeFile code_file = project.
				get_code_space().get_code_files().iterator().next();
		writer.write("File: " + code_file.get_cfile().getAbsolutePath() + "\n");
		writer.write("Mutants: " + code_file.get_mutant_space().size() + "\n");
		writer.write("Tests: " + project.get_test_space().number_of_test_inputs() + "\n");
		
		writer.write("+-------------------------------------------------+\n");
		for(Mutant mutant : code_file.get_mutant_space().get_mutants()) {
			AstMutation mutation = mutant.get_mutation();
			writer.write("\n\tMutant#" + mutant.get_id() + "::" + 
					mutation.get_class() + "::" + mutation.get_operator() + "\n");
			AstNode location = mutant.get_mutation().get_location();
			int line = location.get_location().line_of();
			String code = location.generate_code();
			if(code.contains("\n")) {
				int index = code.indexOf('\n');
				code = code.substring(0, index).strip();
			}
			String ast_class = location.getClass().getSimpleName();
			ast_class = ast_class.substring(3, ast_class.length() - 4).strip();
			writer.write("\tLocation[" + line + "]: " + location.generate_code() + " {" + ast_class + "}\n");
			if(mutation.has_parameter())
				writer.write("\tParameter: " + mutation.get_parameter().toString() + "\n");
			MuTestProjectTestResult result = project.get_test_space().get_test_result(mutant);
			if(result != null) {
				if(result.get_kill_set().degree() == 0)
					writer.write("\tResults: not killed by any tests\n");
				else
					writer.write("\tResults: killed by " + result.get_kill_set().degree() + " tests.\n");
			}
			writer.write("\n");
		}
		writer.write("+-------------------------------------------------+\n");
		
		writer.close();
	}
	protected static void testing(File root) throws Exception {
		MuTestProject project = get_project(root);
		File output = new File(result_dir + project.get_name() + ".txt");
		output_mutations(project, output);
	}
	
}
