package test;

import java.io.File;

import com.jcsa.jcparse.flwa.dynamics.AstPath;
import com.jcsa.jcparse.flwa.dynamics.AstPathElement;
import com.jcsa.jcparse.flwa.dynamics.AstPathElementType;
import com.jcsa.jcparse.lang.AstCirFile;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.test.exe.CommandUtil;
import com.jcsa.jcparse.test.exe.TestInput;
import com.jcsa.jcparse.test.file.JCTestProject;
import com.jcsa.jcparse.test.file.TestExecutionResult;

public class JCProjectResultTest {
	
	private static final String root_path = "/home/dzt2/Development/Data/Code2/";
	private static final CommandUtil command_util = CommandUtil.linux_util;
	private static final File project_dir = new File(root_path + "projects");
	
	public static void main(String[] args) throws Exception {
		for(File file : project_dir.listFiles()) {
			JCTestProject project = get_project(file);
			for(int tid = 0; tid < project.get_test_part().get_test_inputs().number_of_inputs(); tid++) {
				print_execution_path(project, tid);
			}
		}
	}
	
	private static JCTestProject get_project(File root) throws Exception {
		JCTestProject project = JCTestProject.open_project(root, command_util);
		System.out.println("\t1. Get the project for " + root.getName());
		System.out.println("\t\t==> include " + project.get_test_part().get_test_inputs().number_of_inputs() + " test inputs.");
		return project;
	}
	private static void print_execution_path(JCTestProject project, int tid) throws Exception {
		AstCirFile program = project.get_code_part().get_program(0);
		TestInput input = project.get_test_part().get_test_inputs().get_input(tid);
		TestExecutionResult result = project.get_result(program, input);
		System.out.println("\t2. Analyze test result of test[" + result.get_test_input().get_id() + 
							"] as \"" + result.get_test_input().get_parameter() + "\":");
		
		AstPath path = result.load_ast_path();
		if(path == null) return;
		for(AstPathElement element : path.get_elements()) {
			AstNode location = element.get_ast_location();
			int line = location.get_location().line_of();
			System.out.println("\t\t==> " + location.getClass().getSimpleName() + 
								"[" + location.get_key() + "] at line " + line);
			if(location instanceof AstExpression) {
				System.out.println("\t\t\tCode: " + location.generate_code());
				System.out.print("\t\t\tValue:");
				for(byte value : element.get_ast_status()) {
					System.out.print(" " + value);
				}
				System.out.println();
			}
			else if(element.get_type() == AstPathElementType.beg_stmt) {
				System.out.println("\t\t\tStart-Statement");
			}
			else {
				System.out.println("\t\t\tComplete-Statement");
			}
		}
	}
	
}
