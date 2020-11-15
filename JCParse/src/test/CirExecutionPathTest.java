package test;

import java.io.File;
import java.io.FileWriter;
import java.util.Set;

import com.jcsa.jcparse.flwa.context.CirCallContextInstanceGraph;
import com.jcsa.jcparse.flwa.context.CirFunctionCallPathType;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.flwa.graph.CirInstanceGraph;
import com.jcsa.jcparse.lang.AstCirFile;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.irlang.CirTree;
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
	
	private static final String prefix = "/home/dzt2/Development/Data/ifiles/";
	private static final File template_file = new File("config/cruntime.txt");
	private static final String postfx = "result/paths/";
	
	public static void main(String[] args) throws Exception {
		for(File file : new File(prefix).listFiles()) {
			testing(file);
		}
	}
	
	/* parsing */
	private static AstCirFile parse1(File file) throws Exception {
		return AstCirFile.parse(file, template_file, ClangStandard.gnu_c89);
	}
	private static CirCallContextInstanceGraph parse2(CirTree cir_tree) throws Exception {
		CirFunction root_function = cir_tree.get_function_call_graph().get_function("main");
		return CirCallContextInstanceGraph.graph(root_function, 
				CirFunctionCallPathType.unique_path, -1);
	}
	private static CDependGraph parse3(CirInstanceGraph instance_graph) throws Exception {
		return CDependGraph.graph(instance_graph);
	}
	private static CDependGraph parse(File file) throws Exception {
		return parse3(parse2(parse1(file).get_cir_tree()));
	}
	
	/* outputs */
	private static void output_path(FileWriter writer, CDependGraph dependence_graph, CirExecution execution) throws Exception {
		writer.write("\t" + execution + ": " + execution.get_statement().generate_code(true) + "\n");
		CirStatement statement = execution.get_statement();
		if(statement instanceof CirAssignStatement 
			|| statement instanceof CirIfStatement
			|| statement instanceof CirCaseStatement
			|| statement instanceof CirCallStatement
			|| statement instanceof CirBegStatement
			|| statement instanceof CirEndStatement) {
			Set<CirExecutionPath> paths = CirExecutionPathFinder.finder.find_dependence_paths(dependence_graph, execution);
			for(CirExecutionPath path : paths) { writer.write("\t==> "); writer.write(path.toString()); writer.write("\n"); }
		}
	}
	private static void output_paths(FileWriter writer, CDependGraph dependence_graph, CirFunction function) throws Exception {
		System.out.println("\tFunction: " + function.get_name());
		writer.write("Beg " + function.get_name() + ":\n");
		for(int k = 1; k <= function.get_flow_graph().size(); k++) {
			CirExecution execution = function.get_flow_graph().
					get_execution(k % function.get_flow_graph().size());
			System.out.println("\t\t" + execution);
			output_path(writer, dependence_graph, execution);
		}
		writer.write("End\n");
	}
	private static void output(CDependGraph dependence_graph, File output) throws Exception {
		System.out.println("2. Output to " + output.getAbsolutePath());
		FileWriter writer = new FileWriter(output);
		for(CirFunction function : dependence_graph.get_program_graph().get_cir_tree().get_function_call_graph().get_functions()) {
			output_paths(writer, dependence_graph, function);
		}
		writer.close();
	}
	
	/* testing */
	protected static void testing(File file) throws Exception {
		System.out.println("Testing on " + file.getName());
		CDependGraph dependence_graph = parse(file);
		output(dependence_graph, new File(postfx + file.getName() + ".txt"));
	}
	
}
