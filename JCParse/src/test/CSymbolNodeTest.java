package test;

import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.Queue;

import com.jcsa.jcparse.lang.AstCirFile;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator.DeclaratorProduction;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowGraph;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCallGraph;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.parse.symbolic.SymbolExpression;
import com.jcsa.jcparse.parse.symbolic.SymbolFactory;

public class CSymbolNodeTest {
	
	private static final String root_path = "/home/dzt2/Development/Data/projects";
	private static final File sizeof_template_file = new File("config/cruntime.txt");
	private static final String output_directory = "results/";
	
	public static void main(String[] args) throws Exception {
		for(File root : new File(root_path).listFiles()) {
			System.out.println("Testing on " + root.getName());
			AstCirFile cfile = parse(root);
			SymbolFactory.set_config(cfile.get_run_template(), true);	// configure
			write(cfile.get_ast_tree(), new File(output_directory + root.getName() + ".ast"));
			write(cfile.get_cir_tree(), new File(output_directory + root.getName() + ".cir"));
		}
	}
	
	private static AstCirFile parse(File root) throws Exception { 
		File cfile = new File(root.getAbsolutePath() + "/code/ifiles/" + root.getName() + ".c");
		return AstCirFile.parse(cfile, sizeof_template_file, ClangStandard.gnu_c89);
	}
	
	private static String strip_code(String code, int max_length) {
		StringBuilder buffer = new StringBuilder();
		for(int k = 0; k < code.length() && buffer.length() < max_length; k++) {
			char ch = code.charAt(k);
			if(Character.isWhitespace(ch)) {
				ch = ' ';
			}
			buffer.append(ch);
		}
		if(buffer.length() >= max_length) buffer.append("...");
		return buffer.toString();
	}
	
	private static void write(AstTree tree, File output) throws Exception {
		Queue<AstNode> queue = new LinkedList<AstNode>();
		queue.add(tree.get_ast_root());
		
		FileWriter writer = new FileWriter(output);
		while(!queue.isEmpty()) {
			AstNode node = queue.poll();
			for(int k = 0; k < node.number_of_children(); k++) {
				queue.add(node.get_child(k));
			}
			
			if(node instanceof AstIfStatement
				|| node instanceof AstSwitchStatement
				|| node instanceof AstWhileStatement
				|| node instanceof AstDoWhileStatement
				|| node instanceof AstForStatement
				|| node instanceof AstExpressionStatement
				|| node instanceof AstExpression
				|| node instanceof AstReturnStatement) {
				/* AST[KEY] CLASS FUNC LINE CODE */
				int key = node.get_key();
				String class_name = node.getClass().getSimpleName();
				class_name = class_name.substring(3, class_name.length() - 4).strip();
				AstFunctionDefinition def = node.get_function_of();
				String function = "null";
				if(def != null) {
					AstDeclarator declarator = def.get_declarator();
					while(declarator.get_production() != DeclaratorProduction.identifier) {
						declarator = declarator.get_declarator();
					}
					function = declarator.get_identifier().get_name();
				}
				int line = node.get_location().line_of();
				String code = node.generate_code();
				code = "\"" + strip_code(code, 96) + "\"";
				
				writer.write("AST\t" + key + "\t" + class_name + "\t" + function + "\t" + line + "\n");
				writer.write("COD:\t" + code + "\n");
				SymbolExpression expression = SymbolFactory.sym_expression(node);
				writer.write("SYM:\t" + expression.generate_simple_code() + "\n");
				writer.write("\n");
			}
		}
		writer.close();
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
					writer.write("\t==> [1] " + lvalue.generate_simple_code() + "\n");
					writer.write("\t==> [2] " + rvalue.generate_simple_code() + "\n");
				}
				else if(statement instanceof CirIfStatement) {
					SymbolExpression value = SymbolFactory.sym_expression(((CirIfStatement) statement).get_condition());
					writer.write("\t==> [1] " + value.generate_simple_code() + "\n");
				}
				else if(statement instanceof CirCaseStatement) {
					SymbolExpression value = SymbolFactory.sym_expression(((CirCaseStatement) statement).get_condition());
					writer.write("\t==> [1] " + value.generate_simple_code() + "\n");
				}
			}
			writer.write("END FUNC\n");
		}
		
		writer.close();
	}
	
}
