package test;

import java.io.File;
import java.io.FileWriter;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowGraph;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.parse.CTranslate;
import com.jcsa.jcparse.lang.symb.SymExpression;
import com.jcsa.jcparse.lang.symb.SymProcess;

public class SymExprTest {
	
	protected static final String prefix = "D:/SourceCode/MyData/CODE2/gfiles/";
	protected static final String postfx = "result/sym/";
	
	public static void main(String[] args) throws Exception {
		for(File file : new File(prefix).listFiles()) testing(file);
	}
	protected static void testing(File file) throws Exception {
		System.out.println("Testing " + file.getName());
		
		AstTree ast_tree = parse(file);
		System.out.println("\t(1) parsing to AST tree");
		
		CirTree cir_tree = parse(ast_tree);
		System.out.println("\t(2) parsing to CIR tree");
		
		evaluate(cir_tree, new File(postfx + file.getName() + ".txt"));
		System.out.println("\t(3) generate symbolic tree");
		
		System.out.println();
	}
	
	private static AstTree parse(File file) throws Exception {
		return CTranslate.parse(file, ClangStandard.gnu_c89);
	}
	private static CirTree parse(AstTree ast_tree) throws Exception {
		return CTranslate.parse(ast_tree);
	}
	private static void evaluate(CirTree cir_tree, File output) throws Exception {
		FileWriter writer = new FileWriter(output);
		Iterable<CirFunction> functions = cir_tree.get_function_call_graph().get_functions();
		for(CirFunction function : functions) {
			CirExecutionFlowGraph flow_graph = function.get_flow_graph();
			writer.write("Function " + function.get_name() + "\n");
			for(int k = 1; k <= flow_graph.size(); k++) {
				/** 1. print execution **/
				CirExecution execution = flow_graph.get_execution(k % flow_graph.size());
				CirStatement statement = execution.get_statement();
				writer.write("\t" + execution.
						toString() + ": \t" + statement.generate_trim_code() + "\n");
				
				/** 2. print expressions under statement **/
				if(statement instanceof CirAssignStatement) {
					SymExpression loperand = SymProcess.parse(((CirAssignStatement) statement).get_lvalue());
					SymExpression roperand = SymProcess.parse(((CirAssignStatement) statement).get_rvalue());
					writer.write("\t\t==> " + loperand.toString() + "\n");
					writer.write("\t\t==> " + roperand.toString() + "\n");
				}
				else if(statement instanceof CirIfStatement) {
					SymExpression condition = SymProcess.parse(((CirIfStatement) statement).get_condition());
					writer.write("\t\t==> " + condition.toString() + "\n");
				}
				else if(statement instanceof CirCaseStatement) {
					SymExpression condition = SymProcess.parse(((CirCaseStatement) statement).get_condition());
					writer.write("\t\t==> " + condition.toString() + "\n");
				}
				/*
				else if(statement instanceof CirCallStatement) {
					SymExpression condition = SymProcess.parse(statement);
					writer.write("\t\t==> " + condition.toString() + "\n");
				}
				*/
			}
			writer.write("End Function\n\n");
		}
		writer.close();
	}
	
}
