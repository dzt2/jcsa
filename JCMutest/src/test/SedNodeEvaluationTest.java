package test;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcmutest.selang.lang.SedNode;
import com.jcsa.jcmutest.selang.lang.expr.SedExpression;
import com.jcsa.jcmutest.selang.util.SedEvaluator;
import com.jcsa.jcmutest.selang.util.SedFactory;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowGraph;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCallGraph;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.test.cmd.CCompiler;

public class SedNodeEvaluationTest {
	
	/* parameters */
	protected static final String root_path = "/home/dzt2/Development/Data/";
	protected static final String postfix = "result/sed/";
	private static final File sizeof_template_file = new File("config/cruntime.txt");
	private static final File instrument_head_file = new File("config/jcinst.h");
	private static final File preprocess_macro_file = new File("config/linux.h");
	private static final File mutation_head_file = new File("config/jcmutest.h");
	private static final long max_timeout_seconds = 5;
	
	public static void main(String[] args) throws Exception {
		for(File cfile : new File(root_path + "/cfiles/").listFiles()) {
			if(cfile.getName().endsWith(".c"))
				testing(cfile);
		}
	}
	
	/* testing functions */
	private static String get_name(File cfile) {
		int index = cfile.getName().lastIndexOf('.');
		return cfile.getName().substring(0, index).strip();
	}
	private static MuTestProject get_project(File cfile) throws Exception {
		/* project name getters */
		String name = get_name(cfile);
		File root = new File(root_path + "projects/" + name);
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
		
		/* return the created project */
		return project;
	}
	/**
	 * write the sed-node information to the output file
	 * @param cir_tree
	 * @param output
	 * @throws Exception
	 */
	private static void write_sed(CirTree cir_tree, File output) throws Exception {
		CirFunctionCallGraph call_graph = cir_tree.get_function_call_graph();
		FileWriter writer = new FileWriter(output);
		SedEvaluator evaluator = new SedEvaluator();
		for(CirFunction function : call_graph.get_functions()) {
			write_sed(evaluator, function, writer);
			writer.write("\n");
		}
		writer.close();
	}
	private static void write_sed(SedEvaluator evaluator, CirFunction function, FileWriter writer) throws Exception {
		writer.write("function ");
		writer.write(function.get_name());
		writer.write(":\n");
		CirExecutionFlowGraph flow_graph = function.get_flow_graph();
		for(int i = 1; i <= flow_graph.size(); i++) {
			write_sed(evaluator, flow_graph.get_execution(i % flow_graph.size()), writer);
		}
		writer.write("end function\n");
	}
	private static void write_sed(SedEvaluator evaluator, CirExecution execution, FileWriter writer) throws Exception {
		writer.write("\t");
		writer.write(execution.toString());
		writer.write(":\t");
		writer.write(execution.get_statement().generate_code(false));
		writer.write("\n");
		writer.write("\t\t[1] " + SedFactory.parse(execution.get_statement()).generate_code() + "\n");
		
		CirStatement statement = execution.get_statement();
		if(statement instanceof CirAssignStatement) {
			SedNode lvalue = SedFactory.parse(((CirAssignStatement) statement).get_lvalue());
			SedNode rvalue = SedFactory.parse(((CirAssignStatement) statement).get_rvalue());
			SedExpression lexpression = evaluator.evaluate((SedExpression) lvalue);
			SedExpression rexpression = evaluator.evaluate((SedExpression) rvalue);
			writer.write("\t\t[2] " + lexpression.clone().generate_code() + "\n");
			writer.write("\t\t[3] " + rexpression.clone().generate_code() + "\n");
		}
		else if(statement instanceof CirIfStatement) {
			SedNode value = SedFactory.parse(((CirIfStatement) statement).get_condition());
			SedExpression expression = evaluator.evaluate((SedExpression) value);
			writer.write("\t\t[2] " + expression.clone().generate_code() + "\n");
		}
		else if(statement instanceof CirCaseStatement) {
			SedNode value = SedFactory.parse(((CirCaseStatement) statement).get_condition());
			SedExpression expression = evaluator.evaluate((SedExpression) value);
			writer.write("\t\t[2] " + expression.clone().generate_code() + "\n");
		}
	}
	protected static void testing(File cfile) throws Exception {
		MuTestProject project = get_project(cfile);
		for(MuTestProjectCodeFile code_file : project.get_code_space().get_code_files()) {
			write_sed(code_file.get_cir_tree(), new File(postfix + cfile.getName() + ".sd"));
			System.out.println("Complete testing on " + cfile.getName());
		}
	}
	
}
