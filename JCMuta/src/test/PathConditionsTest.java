package test;

import java.io.File;
import java.io.FileWriter;

import com.jcsa.jcmuta.mutant.error2mutation.PathConditions;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowGraph;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.parse.CTranslate;
import com.jcsa.jcparse.lang.symb.StateConstraint;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lopt.context.CirCallContextInstanceGraph;
import com.jcsa.jcparse.lopt.context.CirFunctionCallPathType;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceGraph;
import com.jcsa.jcparse.lopt.models.dominate.CDominanceGraph;

public class PathConditionsTest {
	
	protected static final String prefix = "D:/SourceCode/MyData/CODE2/gfiles/";
	protected static final String postfx = "results/path/";
	
	public static void main(String[] args) throws Exception {
		for(File file : new File(prefix).listFiles()) {
			testing(file);
		}
	}
	
	private static AstTree parse(File file) throws Exception {
		return CTranslate.parse(file, ClangStandard.gnu_c89);
	}
	private static CirTree parse(AstTree ast_tree) throws Exception {
		return CTranslate.parse(ast_tree);
	}
	private static CDominanceGraph parse(CirTree cir_tree) throws Exception {
		CirFunction root_function = cir_tree.get_function_call_graph().get_function("main");
		CirInstanceGraph instance_graph = CirCallContextInstanceGraph.graph(root_function, 
				CirFunctionCallPathType.unique_path, -1);
		return CDominanceGraph.forward_dominance_graph(instance_graph);
	}
	private static void output_conditions(CirTree cir_tree, CDominanceGraph dgraph, File output) throws Exception {
		FileWriter writer = new FileWriter(output);
		
		for(CirFunction function : cir_tree.get_function_call_graph().get_functions()) {
			writer.write("FUNCTION " + function.get_name() + "\n");
			CirExecutionFlowGraph fgraph = function.get_flow_graph();
			for(int k = 1; k <= fgraph.size(); k++) {
				CirExecution execution = fgraph.get_execution(k % fgraph.size());
				StateConstraints constraints = 
						PathConditions.path_constraints(execution.get_statement(), dgraph);
				writer.write("\t" + execution.toString() + "\t\"" + execution.get_statement().generate_trim_code() + "\"\n");
				writer.write("\t{\n");
				for(StateConstraint constraint : constraints.get_constraints()) 
					writer.write("\t\t" + constraint + "\n");
				writer.write("\t}\n");
			}
			writer.write("END FUNCTION\n\n");
		}
		
		writer.close();
	}
	protected static void testing(File file) throws Exception {
		System.out.println("Testing " + file.getName());
		
		AstTree ast_tree = parse(file);
		System.out.println("\t(1) parsing to AST tree");
		
		CirTree cir_tree = parse(ast_tree);
		System.out.println("\t(2) parsing to CIR tree");
		
		CDominanceGraph dgraph = parse(cir_tree);
		System.out.println("\t(2) parsing to DOM graph");
		
		output_conditions(cir_tree, dgraph, new File(postfx + file.getName() + ".txt"));
	}
	
}
