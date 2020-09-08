package test;

import java.io.File;
import java.io.FileWriter;
import java.util.Random;

import com.jcsa.jcparse.lang.AstCirFile;
import com.jcsa.jcparse.test.CommandUtil;
import com.jcsa.jcparse.test.file.JCTestProject;
import com.jcsa.jcparse.test.file.TestInput;
import com.jcsa.jcparse.test.inst.InstrumentalNode;
import com.jcsa.jcparse.test.inst.InstrumentalPath;
import com.jcsa.jcparse.test.inst.InstrumentalType;
import com.jcsa.jcparse.test.inst.InstrumentalUnit;

public class JCProjectResultTest {
	
	private static final String root_path = "/home/dzt2/Development/Data/";
	private static final CommandUtil command_util = CommandUtil.linux_util;
	private static final File project_dir = new File(root_path + "projects");
	private static final File result_dir = new File("result/ins");
	private static final Random random = new Random(System.currentTimeMillis());
	
	public static void main(String[] args) throws Exception {
		for(File file : project_dir.listFiles()) {
			JCTestProject project = get_project(file);
			File ofile = new File(result_dir.getAbsolutePath() + "/" + file.getName() + ".txt");
			FileWriter writer = new FileWriter(ofile);
			for(int k = 0; k < 6; k++) {
				int tid = Math.abs(random.nextInt()) % 
						project.get_test_part().number_of_test_inputs();
				if(!print_instrumental_path(project, tid, writer)) k--;
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
	protected static boolean print_instrumental_path(JCTestProject project, int tid, FileWriter writer) throws Exception {
		AstCirFile program = project.get_code_part().get_program(0);
		TestInput input = project.get_test_part().get_test_inputs().get_input(tid);
		try {
			InstrumentalPath path = project.get_result_part().load_partial_path(program.
					get_run_template(), program.get_cir_tree(), program.get_ast_tree(), input);
			if(path != null) {
				writer.write("Instrument List of tests[" + tid + "]:\n");
				writer.write("Parameters: " + input.get_parameter() + "\n");
				for(InstrumentalNode node : path.get_nodes()) {
					writer.write("\n[" + node.get_index() + "]\t" + node.get_execution() + "\n");
					writer.write("\tStatement: " + node.get_statement().generate_code(true) + "\n");
					int index = 0;
					for(InstrumentalUnit unit : node.get_units()) {
						writer.write("\t\tUnits[" + (index++) + "] " + unit.get_type().toString());
						if(unit.get_type() == InstrumentalType.evaluate) {
							writer.write("::{" + unit.get_location().generate_code(true) + "}\n");
							if(unit.has_bytes()) {
								writer.write("\t\t\tValue: " + unit.toString() + "\n");
							}
						}
						else {
							writer.write("\n");
						}
					}
				}
				writer.write("\n\n");
				System.out.println("Load instrumental path for Test#" + tid);
			}
			return path != null;
		}
		catch(Exception ex) {
			throw ex;
		}
	}
	
}
