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
import com.jcsa.jcparse.test.inst.InstrumentalUnit;

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
			for(int k = 0; k < 12; k++) {
				int tid = Math.abs(random.nextInt()) % 
						project.get_test_part().number_of_test_inputs();
				// if(!print_complete_path(project, tid, writer)) { k--; }
				if(!print_simple_path(project, tid, writer)) k = k - 1; 
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
	protected static boolean print_complete_path(JCTestProject project, int tid, FileWriter writer) throws Exception {
		AstCirFile program = project.get_code_part().get_program(0);
		TestInput input = project.get_test_part().get_test_inputs().get_input(tid);
		try {
			InstrumentalPath path = project.get_result_part().load_complete_path(program.get_ast_tree(), input);
			if(path != null) {
				writer.write("Instrument List of tests[" + tid + "]:\n");
				writer.write("\tParameters: " + input.get_parameter() + "\n");
				
				int index = 0;
				InstrumentalNode node = path.get_source();
				while(node != null) {
					InstrumentalUnit unit = node.get_unit();
					writer.write("[" + index + "]::" + unit.get_tag() + "\n");
					String ast_code = unit.get_location().generate_code();
					int line_index = ast_code.indexOf('\n');
					if(line_index >= 0) {
						ast_code = ast_code.substring(0, line_index);
					}
					String ast_type = unit.get_location().getClass().getSimpleName();
					ast_type = ast_type.substring(3, ast_type.length() - 4).strip();
					writer.write("\t\t==> type: " + ast_type + "[" + unit.get_location().get_key() + "]\n");
					writer.write("\t\t==> code: " + ast_code.strip() + "\n");
					if(unit.has_value()) {
						writer.write("\t\t==> bytes:");
						for(byte value : unit.get_value()) {
							writer.write(" " + value);
						}
						writer.write("\n");
					}
					writer.write("\n");
					
					index++;
					node = node.get_ou_node();
				}
				
				writer.write("\n");
				writer.flush();
				System.out.println("\t==> Complete parsing the test#" + tid);
			}
			return path != null;
		}
		catch(Exception ex) {
			throw ex;
		}
	}
	protected static boolean print_simple_path(JCTestProject project, int tid, FileWriter writer) throws Exception {
		AstCirFile program = project.get_code_part().get_program(0);
		TestInput input = project.get_test_part().get_test_inputs().get_input(tid);
		try {
			InstrumentalPath path = project.get_result_part().load_simple_path(program.get_ast_tree(), input);
			if(path != null) {
				writer.write("Instrument List of tests[" + tid + "]:\n");
				writer.write("\tParameters: " + input.get_parameter() + "\n");
				
				int index = 0;
				InstrumentalNode node = path.get_source();
				while(node != null) {
					InstrumentalUnit unit = node.get_unit();
					writer.write("[" + index + "]::" + unit.get_tag() + "\n");
					String ast_code = unit.get_location().generate_code();
					int line_index = ast_code.indexOf('\n');
					if(line_index >= 0) {
						ast_code = ast_code.substring(0, line_index);
					}
					String ast_type = unit.get_location().getClass().getSimpleName();
					ast_type = ast_type.substring(3, ast_type.length() - 4).strip();
					writer.write("\t\t==> type: " + ast_type + "[" + unit.get_location().get_key() + "]\n");
					writer.write("\t\t==> code: " + ast_code.strip() + "\n");
					if(unit.has_value()) {
						writer.write("\t\t==> bytes:");
						for(byte value : unit.get_value()) {
							writer.write(" " + value);
						}
						writer.write("\n");
					}
					writer.write("\n");
					
					index++;
					node = node.get_ou_node();
				}
				
				writer.write("\n");
				writer.flush();
				System.out.println("\t==> Complete parsing the test#" + tid);
			}
			return path != null;
		}
		catch(Exception ex) {
			throw ex;
		}
	}
	
}
