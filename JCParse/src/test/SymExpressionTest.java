package test;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.flwa.symbol.SymEvaluator;
import com.jcsa.jcparse.lang.AstCirFile;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowGraph;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCallGraph;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;
import com.jcsa.jcparse.test.CommandUtil;
import com.jcsa.jcparse.test.cmd.CCompiler;
import com.jcsa.jcparse.test.file.JCTestProject;

public class SymExpressionTest {

	private static final ClangStandard lang_standard = ClangStandard.gnu_c89;
	private static final CommandUtil command_util = CommandUtil.linux_util;
	private static final CCompiler compiler = CCompiler.clang;
	private static final File c_template_file = new File("config/cruntime.txt");
	private static final File c_instrument_head_file = new File("config/jcinst.h");
	private static final File c_pre_process_mac_file = new File("config/linux.h");
	private static final List<String> compile_parameters = new ArrayList<String>();
	
	private static final String root_path = "/home/dzt2/Development/Data/";
	private static final File cfile_dir = new File(root_path + "cfiles");
	private static final File input_dir = new File(root_path + "inputs");
	private static final File tests_dir = new File(root_path + "tests");
	private static final File project_dir = new File(root_path + "cprojects");
	
	public static void main(String[] args) throws Exception {
		for(File cfile : cfile_dir.listFiles()) {
			if(cfile.getName().endsWith(".c")) {
				System.out.println("Testing " + cfile.getName());
				testing(cfile);
			}
		}
	}
	
	/**
	 * @param name
	 * @return [xxx.c, inputs/xxx/, tests/xxx.txt, project/xxx/]
	 * @throws Exception
	 */
	private static File[] get_input_files(String name) throws Exception {
		int index = name.indexOf(".");
		if(index >= 0)
			name = name.substring(0, index).strip();
		File cfile = new File(cfile_dir.getAbsolutePath() + "/" + name + ".c");
		File idir = new File(input_dir.getAbsolutePath() + "/" + name);
		File tfile = new File(tests_dir.getAbsolutePath() + "/" + name + ".txt");
		File pdir = new File(project_dir.getAbsolutePath() + "/" + name);
		
		if(cfile.exists() && tfile.exists()) {
			File[] results = new File[] { null, null, null, null };
			results[0] = cfile;
			results[1] = idir;
			results[2] = tfile;
			results[3] = pdir;
			if(!idir.exists()) idir.mkdir();
			return results;
		}
		else {
			return null;
		}
	}
	/**
	 * create a test-project for executing tests.
	 * @param files
	 * @return
	 * @throws Exception
	 */
	private static JCTestProject get_project(File[] files) throws Exception {
		JCTestProject project;
		if(files[3].exists()) {
			project = JCTestProject.open_project(files[3], command_util);
			List<File> tfiles = new ArrayList<File>();
			tfiles.add(files[2]);
			project.set_tests(tfiles, files[1]);
		}
		else {
			compile_parameters.clear(); compile_parameters.add("-lm");
			project = JCTestProject.new_project(files[3], command_util, compiler, lang_standard, 
					c_template_file, c_instrument_head_file, c_pre_process_mac_file, compile_parameters);
			List<File> cfiles = new ArrayList<File>();
			List<File> hfiles = new ArrayList<File>();
			List<File> lfiles = new ArrayList<File>();
			cfiles.add(files[0]);
			List<File> tfiles = new ArrayList<File>();
			tfiles.add(files[2]);
			project.set_code(cfiles, hfiles, lfiles);
			project.set_tests(tfiles, files[1]);
		}
		
		System.out.println("\t1. Get the project for " + files[0].getName());
		System.out.println("\t\t==> include " + project.get_test_part().get_test_inputs().number_of_inputs() + " test inputs.");
		return project;
	}
	/**
	 * @param project
	 * @param cfile
	 * @param output
	 * @throws Exception
	 */
	private static void output_symbolic_nodes(JCTestProject project, File output) throws Exception {
		FileWriter writer = new FileWriter(output);
		AstCirFile program = project.get_code_part().get_program(0);
		SymFactory.config(program.get_run_template(), true);
		CirFunctionCallGraph graph = program.get_function_call_graph();
		for(CirFunction function : graph.get_functions()) {
			writer.write("FUNC " + function.get_name() + "\n");
			CirExecutionFlowGraph fgraph = function.get_flow_graph();
			for(int k = 1; k <= fgraph.size(); k++) {
				CirExecution execution = fgraph.get_execution(k % fgraph.size());
				writer.write("\t" + execution.toString() + ":\t" + execution.get_statement().generate_code(true) + "\n");
				CirStatement statement = execution.get_statement();
				if(statement instanceof CirAssignStatement) {
					SymExpression lvalue = SymFactory.sym_expression(((CirAssignStatement) statement).get_lvalue());
					SymExpression rvalue = SymFactory.sym_expression(((CirAssignStatement) statement).get_rvalue());
					writer.write("\t==> [1] " + lvalue.generate_code(true) + "\n");
					writer.write("\t==> [2] " + rvalue.generate_code(true) + "\n");
					lvalue = SymEvaluator.evaluate_on(lvalue); 
					rvalue = SymEvaluator.evaluate_on(rvalue);
					writer.write("\t~~> [1] " + lvalue.generate_code(true) + "\n");
					writer.write("\t~~> [2] " + rvalue.generate_code(true) + "\n");
				}
				else if(statement instanceof CirIfStatement) {
					SymExpression value = SymFactory.sym_expression(((CirIfStatement) statement).get_condition());
					writer.write("\t==> [1] " + value.generate_code(true) + "\n");
					value = SymEvaluator.evaluate_on(value);
					writer.write("\t~~> [1] " + value.generate_code(true) + "\n");
				}
				else if(statement instanceof CirCaseStatement) {
					SymExpression value = SymFactory.sym_expression(((CirCaseStatement) statement).get_condition());
					writer.write("\t==> [1] " + value.generate_code(true) + "\n");
					value = SymEvaluator.evaluate_on(value);
					writer.write("\t~~> [1] " + value.generate_code(true) + "\n");
				}
			}
			writer.write("END FUNC\n");
		}
		
		writer.close();
	}
	protected static void testing(File cfile) throws Exception {
		String name = cfile.getName();
		int index = name.indexOf('.');
		name = name.substring(0, index);
		File[] files = get_input_files(name);
		if(files == null) return;
		JCTestProject project = get_project(files);
		output_symbolic_nodes(project, new File("result/sym/" + name + ".txt"));
	}
	
}
