package test;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Random;

import com.jcsa.jcparse.lang.AstCirFile;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.test.CommandUtil;
import com.jcsa.jcparse.test.file.JCTestProject;
import com.jcsa.jcparse.test.file.TestInput;
import com.jcsa.jcparse.test.inst.InstrumentalLine;
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
			for(int k = 0; k < 12; k++) {
				int tid = Math.abs(random.nextInt()) % 
						project.get_test_part().number_of_test_inputs();
				// if(!print_instrument_lines(project, tid, writer)) k--; 
				if(!print_instrument_units(project, tid, writer)) k--;
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
	protected static boolean print_instrument_lines(JCTestProject project, int tid, FileWriter writer) throws Exception {
		AstCirFile program = project.get_code_part().get_program(0);
		TestInput input = project.get_test_part().get_test_inputs().get_input(tid);
		try {
			List<InstrumentalLine> path = 
					project.get_result_part().load_instrumental_lines(
					program.get_run_template(), program.get_ast_tree(), input);
			
			if(path != null) {
				writer.write("Instrument List of tests[" + tid + "]:\n");
				writer.write("\tParameters: " + input.get_parameter() + "\n");
				for(int index = 0; index < path.size(); index++) {
					InstrumentalLine line = path.get(index);
					writer.write("[" + index + "]::" + line.get_type() + "\n");
					String ast_code = line.get_location().generate_code();
					int line_index = ast_code.indexOf('\n');
					if(line_index >= 0) {
						ast_code = ast_code.substring(0, line_index);
					}
					String ast_type = line.get_location().getClass().getSimpleName();
					ast_type = ast_type.substring(3, ast_type.length() - 4).strip();
					writer.write("\t\t==> type: " + ast_type + "[" + line.get_location().get_key() + "]\n");
					writer.write("\t\t==> code: " + ast_code.strip() + "\n");
					if(line.has_value()) {
						writer.write("\t\t==> bytes:");
						for(byte value : line.get_value()) {
							writer.write(" " + value);
						}
						writer.write("\n");
					}
					writer.write("\n");
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
	protected static boolean print_instrument_units(JCTestProject project, int tid, FileWriter writer) throws Exception {
		AstCirFile program = project.get_code_part().get_program(0);
		TestInput input = project.get_test_part().get_test_inputs().get_input(tid);
		try {
			List<InstrumentalUnit> path = project.get_result_part().load_instrumental_units(
					program.get_run_template(), program.get_cir_tree(), program.get_ast_tree(), input);
			
			if(path != null) {
				writer.write("Instrument List of tests[" + tid + "]:\n");
				writer.write("\tParameters: " + input.get_parameter() + "\n");
				for(int index = 0; index < path.size(); index++) {
					InstrumentalUnit line = path.get(index);
					writer.write("[" + index + "]::" + line.get_type() + "\n");
					String ast_code = line.get_location().generate_code(true);
					int line_index = ast_code.indexOf('\n');
					if(line_index >= 0) {
						ast_code = ast_code.substring(0, line_index);
					}
					String ast_type = line.get_location().getClass().getSimpleName();
					ast_type = ast_type.substring(3, ast_type.length() - 4).strip();
					CirNode location = line.get_location();
					if(location instanceof CirExpression) {
						CirStatement statement = ((CirExpression) location).statement_of();
						writer.write("\t\t==> stmt:");
						if(statement != null) writer.write(statement.generate_code(true));
						writer.write("\n");
					}
					writer.write("\t\t==> type: " + ast_type + "[" + line.get_location().get_node_id() + "]\n");
					writer.write("\t\t==> code: " + ast_code.strip() + "\n");
					if(line.has_bytes()) {
						writer.write("\t\t==> bytes:");
						for(byte value : line.get_bytes()) {
							writer.write(" " + value);
						}
						writer.write("\n");
						writer.write("\t\t==> value: " + line.toString());
					}
					writer.write("\n");
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
