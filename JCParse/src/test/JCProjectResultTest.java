package test;

import java.io.File;
import java.io.FileWriter;
import java.util.Random;

import com.jcsa.jcparse.lang.AstCirFile;
import com.jcsa.jcparse.test.CommandUtil;
import com.jcsa.jcparse.test.file.JCTestProject;
import com.jcsa.jcparse.test.file.TestInput;
import com.jcsa.jcparse.test.path.AstExecutionNode;
import com.jcsa.jcparse.test.path.AstExecutionPath;

public class JCProjectResultTest {
	
	private static final String root_path = "/home/dzt2/Development/Data/Code2/";
	private static final CommandUtil command_util = CommandUtil.linux_util;
	private static final File project_dir = new File(root_path + "projects");
	private static final File result_dir = new File("result/ins");
	private static final Random random = new Random(System.currentTimeMillis());
	
	public static void main(String[] args) throws Exception {
		for(File file : project_dir.listFiles()) {
			JCTestProject project = get_project(file);
			File ofile = new File(result_dir.getAbsolutePath() + "/" + file.getName() + ".txt");
			FileWriter writer = new FileWriter(ofile);
			for(int k = 0; k < 24; k++) {
				int tid = Math.abs(random.nextInt()) % project.get_test_part().number_of_test_inputs();
				print_instrument_path(project, tid, writer);
			}
			writer.close();
		}
	}
	
	private static JCTestProject get_project(File root) throws Exception {
		JCTestProject project = JCTestProject.open_project(root, command_util);
		System.out.println("\t1. Get the project for " + root.getName());
		System.out.println("\t\t==> include " + project.get_test_part().get_test_inputs().number_of_inputs() + " test inputs.");
		return project;
	}
	private static void print_instrument_path(JCTestProject project, int tid, FileWriter writer) throws Exception {
		AstCirFile program = project.get_code_part().get_program(0);
		TestInput input = project.get_test_part().get_test_inputs().get_input(tid);
		
		try {
			AstExecutionPath path = project.get_result_part().load_ast_path(program.get_ast_tree(), input);
			writer.write("Instrument List of tests[" + tid + "]:\n");
			writer.write("\tParameters: " + input.get_parameter() + "\n");
			if(path != null) {
				for(AstExecutionNode execution : path.get_nodes()) {
					writer.write("[" + execution.get_index() + "]::" + execution.get_unit().get_type() + "\n");
					String ast_code = execution.get_unit().get_location().generate_code();
					int line_index = ast_code.indexOf('\n');
					if(line_index >= 0) {
						ast_code = ast_code.substring(0, line_index);
					}
					String ast_type = execution.get_unit().get_location().getClass().getSimpleName();
					ast_type = ast_type.substring(3, ast_type.length() - 4).strip();
					writer.write("\t\t==> type: " + ast_type + "[" + execution.get_unit().get_location().get_key() + "]\n");
					writer.write("\t\t==> code: " + ast_code.strip() + "\n");
					if(execution.get_unit().get_state().length > 0) {
						writer.write("\t\t==> bytes:");
						for(byte value : execution.get_unit().get_state()) {
							writer.write(" " + value);
						}
						writer.write("\n");
					}
					writer.write("\n");
				}
			}
			writer.write("\n");
			System.out.println("\t==> Complete parsing the test#" + tid);
		}
		catch(Exception ex) {
			throw ex;
		}
	}
	
}
