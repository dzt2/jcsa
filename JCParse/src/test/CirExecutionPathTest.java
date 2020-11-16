package test;

import java.io.File;
import java.io.FileWriter;

import com.jcsa.jcparse.flwa.context.CirCallContextInstanceGraph;
import com.jcsa.jcparse.flwa.context.CirFunctionCallPathType;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.flwa.graph.CirInstanceGraph;
import com.jcsa.jcparse.lang.AstCirFile;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPathFinder;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirBegStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class CirExecutionPathTest {
	
	protected static final String prefix = "/home/dzt2/Development/Data/ifiles/";
	protected static final String postfx = "result/paths/";
	protected static final File template_file = new File("config/cruntime.txt");
	
	public static void main(String[] args) throws Exception {
		for(File cfile : new File(prefix).listFiles()) {
			testing(cfile);
		}
	}
	
	/* getters */
	private static CDependGraph parse(File cfile) throws Exception {
		AstCirFile ast_file = AstCirFile.parse(cfile, template_file, ClangStandard.gnu_c89);
		CirFunction root_function = ast_file.get_cir_tree().get_function_call_graph().get_main_function();
		CirInstanceGraph instance_graph = CirCallContextInstanceGraph.graph(root_function, 
				CirFunctionCallPathType.unique_path, -1);
		return CDependGraph.graph(instance_graph);
	}
	private static void output_dependence_path(FileWriter writer, 
			CDependGraph dependence_graph, CirExecution execution) throws Exception {
		System.out.println("\t\t" + execution);
		writer.write("\t" + execution + ": " + execution.get_statement().generate_code(true) + "\n");
		for(CirExecutionPath path : CirExecutionPathFinder.finder.dependence_paths(dependence_graph, execution)) {
			writer.write("\t--> " + path.toString() + "\n");
		}
	}
	private static void output_dependence_paths(FileWriter writer, CDependGraph dependence_graph, CirFunction function) throws Exception {
		System.out.println("BEG " + function.get_name());
		writer.write("BEG " + function.get_name() + "\n");
		for(int k = 1; k <= function.get_flow_graph().size(); k++) {
			CirExecution execution = function.get_flow_graph().
					get_execution(k % function.get_flow_graph().size());
			CirStatement statement = execution.get_statement();
			if(statement instanceof CirAssignStatement
				|| statement instanceof CirIfStatement
				|| statement instanceof CirCaseStatement
				|| statement instanceof CirCallStatement
				|| statement instanceof CirBegStatement
				|| statement instanceof CirEndStatement)
				output_dependence_path(writer, dependence_graph, execution);
		}
		writer.write("END " + function.get_name() + "\n");
		System.out.println("END " + function.get_name());
	}
	private static void output(CDependGraph dependence_graph, File output) throws Exception {
		FileWriter writer = new FileWriter(output);
		for(CirFunction function : dependence_graph.get_program_graph().get_cir_tree().get_function_call_graph().get_functions()) {
			output_dependence_paths(writer, dependence_graph, function);
			writer.write("\n");
		}
	}
	protected static void testing(File cfile) throws Exception {
		System.out.println("Testing on " + cfile.getName());
		CDependGraph dependence_graph = parse(cfile);
		output(dependence_graph, new File(postfx + cfile.getName() + ".txt"));
	}
	
}
