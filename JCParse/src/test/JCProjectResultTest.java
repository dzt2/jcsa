package test;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Random;

import com.jcsa.jcparse.lang.AstCirFile;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.test.CommandUtil;
import com.jcsa.jcparse.test.file.JCTestProject;
import com.jcsa.jcparse.test.file.TestInput;
import com.jcsa.jcparse.test.inst.InstrumentalLine;

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
			for(int k = 0; k < 1; k++) {
				int tid = Math.abs(random.nextInt()) % 
						project.get_test_part().number_of_test_inputs();
				if(!print_instrumental_lines(project, tid, writer)) k--;
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
	
	protected static boolean print_instrumental_lines(JCTestProject project, int tid, FileWriter writer) throws Exception {
		AstCirFile program = project.get_code_part().get_program(0);
		TestInput input = project.get_test_part().get_test_inputs().get_input(tid);
		try {
			List<InstrumentalLine> lines = project.get_result_part().load_instrumental_lines(
					program.get_run_template(), program.get_ast_tree(), input, true);
			if(lines != null) {
				writer.write("Instrument List of tests[" + tid + "]:\n");
				writer.write("Parameters: " + input.get_parameter() + "\n");
				int index = 0;
				for(InstrumentalLine line : lines) {
					AstNode location = line.get_location();
					String class_name = location.getClass().getSimpleName();
					class_name = class_name.substring(3, class_name.length() - 4).strip();
					if(line.is_beg())
						writer.write("Line[" + (index++) + "]:BEG:" + line.get_type() + "\n");
					else
						writer.write("Line[" + (index++) + "]:END:" + line.get_type() + "\n");
					
					String ast_code = location.generate_code();
					if(ast_code.contains("\n")) {
						ast_code = ast_code.substring(0, ast_code.indexOf('\n')).strip();
					}
					writer.write("\tclass-type: " + class_name + "\n");
					writer.write("\t" + "at Line " + location.get_location().line_of() + ": " + ast_code + "\n");
					if(line.has_value())
						writer.write("\tValue: " + line.get_value().toString() + "\n");
					writer.write("\n");
				}
				writer.write("\n\n");
				System.out.println("\t\tLoad path for Test#" + tid);
			}
			return lines != null;
		}
		catch(Exception ex) {
			throw ex;
		}
	}
	
}
