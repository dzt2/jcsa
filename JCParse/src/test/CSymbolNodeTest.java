package test;

import java.io.File;
import java.io.FileWriter;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowGraph;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCallGraph;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symb.SymbolExpression;
import com.jcsa.jcparse.lang.symb.SymbolFactory;
import com.jcsa.jcparse.parse.CTranslate;

public class CSymbolNodeTest {
	
	private static final String root_path = "/home/dzt2/Development/Data/projects";
	private static final File sizeof_template_file = new File("config/cruntime.txt");
	private static final String output_directory = "results/";
	
	public static void main(String[] args) throws Exception {
		for(File root : new File(root_path).listFiles()) {
			System.out.println("Testing on " + root.getName());
			CirTree tree = parse(root);
			write(tree, new File(output_directory + root.getName() + ".txt"));
		}
	}
	
	private static CirTree parse(File root) throws Exception { 
		File cfile = new File(root.getAbsolutePath() + "/code/ifiles/" + root.getName() + ".c");
		CRunTemplate template = new CRunTemplate(sizeof_template_file);
		AstTree ast_tree = CTranslate.parse(cfile, ClangStandard.gnu_c89, template);
		return CTranslate.parse(ast_tree, template);
	}
	
	private static void write(CirTree tree, File output) throws Exception {
		FileWriter writer = new FileWriter(output);
		
		CirFunctionCallGraph graph = tree.get_function_call_graph();
		for(CirFunction function : graph.get_functions()) {
			writer.write("FUNC " + function.get_name() + "\n");
			CirExecutionFlowGraph fgraph = function.get_flow_graph();
			for(int k = 1; k <= fgraph.size(); k++) {
				CirExecution execution = fgraph.get_execution(k % fgraph.size());
				writer.write("\t" + execution.toString() + ":\t" + execution.get_statement().generate_code(true) + "\n");
				CirStatement statement = execution.get_statement();
				if(statement instanceof CirAssignStatement) {
					SymbolExpression lvalue = SymbolFactory.sym_expression(((CirAssignStatement) statement).get_lvalue());
					SymbolExpression rvalue = SymbolFactory.sym_expression(((CirAssignStatement) statement).get_rvalue());
					writer.write("\t==> [1] " + lvalue.generate_code(true) + "\n");
					writer.write("\t==> [2] " + rvalue.generate_code(true) + "\n");
				}
				else if(statement instanceof CirIfStatement) {
					SymbolExpression value = SymbolFactory.sym_expression(((CirIfStatement) statement).get_condition());
					writer.write("\t==> [1] " + value.generate_code(true) + "\n");
				}
				else if(statement instanceof CirCaseStatement) {
					SymbolExpression value = SymbolFactory.sym_expression(((CirCaseStatement) statement).get_condition());
					writer.write("\t==> [1] " + value.generate_code(true) + "\n");
				}
			}
			writer.write("END FUNC\n");
		}
		
		writer.close();
	}
	
	
}
