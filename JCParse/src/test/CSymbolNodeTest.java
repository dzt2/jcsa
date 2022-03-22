package test;

import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.jcsa.jcparse.lang.AstCirFile;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator.DeclaratorProduction;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowGraph;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCallGraph;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolProcess;

public class CSymbolNodeTest {
	
	private static final String root_path = "/home/dzt2/Development/Data/ifiles/";
	private static final File sizeof_template_file = new File("config/cruntime.txt");
	private static final String output_directory = "results/symb/";
	
	public static void main(String[] args) throws Exception {
		for(File file : new File(root_path).listFiles()) {
			System.out.println("Testing on " + file.getName());
			AstCirFile cfile = parse(file);
			SymbolFactory.set_config(cfile.get_run_template(), true);	// configure
			write(cfile.get_ast_tree(), new File(output_directory + file.getName() + ".ast"));
			write(cfile.get_cir_tree(), new File(output_directory + file.getName() + ".cir"));
		}
	}
	
	private static AstCirFile parse(File cfile) throws Exception { 
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
	
	/**
	 * @param writer
	 * @param node
	 * @throws Exception
	 */
	private	static void write_ast_node(FileWriter writer, AstNode node, int max_length) throws Exception {
		if(node instanceof AstExpression || node instanceof AstStatement) {
			String class_name = node.getClass().getSimpleName().strip();
			class_name = class_name.substring(3, class_name.length() - 4);
			int node_id = node.get_key();
			AstFunctionDefinition definition = node.get_function_of();
			String func_name;
			if(definition == null) {
				func_name = "Null";
			}
			else {
				AstDeclarator declarator = definition.get_declarator();
				while(declarator.get_production() != DeclaratorProduction.identifier) {
					declarator = declarator.get_declarator();
				}
				func_name = declarator.get_identifier().get_name();
			}
			int code_line = node.get_location().line_of();
			String ast_code = strip_code(node.generate_code(), max_length);
			
			writer.write("BEG\n");
			writer.write("\tASTN:\t" + class_name + "#" + node_id + "\n");
			writer.write("\tLOCT:\t" + func_name + "#" + code_line + "\n");
			writer.write("\tCODE:\t\"" + ast_code + "\"\n");
			
			SymbolExpression expression = SymbolFactory.sym_expression(node);
			SymbolProcess context = new SymbolProcess();
			SymbolExpression eval_expr = expression.evaluate(null, context);
			SymbolExpression norm_expr = expression.normalize();
			
			writer.write("\tSEXP:\t" + expression.generate_simple_code() + "\n");
			writer.write("\t\tRES <-- " + eval_expr.generate_simple_code() + "\n");
			Map<SymbolExpression, SymbolExpression> output = context.get_value_table();
			for(SymbolExpression lvalue : output.keySet()) {
				SymbolExpression rvalue = output.get(lvalue);
				writer.write("\t\t" + lvalue.generate_simple_code() + " <-- " + rvalue.generate_simple_code() + "\n");
			}
			writer.write("\tNEXP:\t" + norm_expr.generate_unique_code() + "\n");
			
			writer.write("END\n");
			writer.write("\n");
		}
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
			write_ast_node(writer, node, 96);
		}
		writer.close();
	}
	
	private	static void write_cir_node(FileWriter writer, CirNode node, int max_length) throws Exception {
		if(node instanceof CirExpression || node instanceof CirStatement) {
			String class_name = node.getClass().getSimpleName().strip();
			class_name = class_name.substring(3, class_name.length() - 4);
			int node_id = node.get_node_id();
			String func_name = "Null"; int code_line = -1;
			if(node.get_ast_source() != null) {
				AstFunctionDefinition definition = node.get_ast_source().get_function_of();
				if(definition != null) {
					AstDeclarator declarator = definition.get_declarator();
					while(declarator.get_production() != DeclaratorProduction.identifier) {
						declarator = declarator.get_declarator();
					}
					func_name = declarator.get_identifier().get_name();
					code_line = node.get_ast_source().get_location().line_of();
				}
			}
			String cir_code = strip_code(node.generate_code(true), max_length);
			CirExecution execution = node.execution_of();
			
			writer.write("\tBEG\n");
			writer.write("\t\tCIRN:\t" + class_name + "#" + node_id + "\n");
			writer.write("\t\tLOCT:\t" + func_name + "#" + code_line + "#" + execution + "\n");
			writer.write("\t\tCODE:\t\"" + cir_code + "\"\n");
			
			SymbolExpression expression = SymbolFactory.sym_expression(node);
			SymbolProcess context = new SymbolProcess();
			SymbolExpression eval_expr = expression.evaluate(null, context);
			SymbolExpression norm_expr = expression.normalize();
			
			writer.write("\t\tSEXP:\t" + expression.generate_simple_code() + "\n");
			writer.write("\t\t\tRES <-- " + eval_expr.generate_simple_code() + "\n");
			Map<SymbolExpression, SymbolExpression> output = context.get_value_table();
			for(SymbolExpression lvalue : output.keySet()) {
				SymbolExpression rvalue = output.get(lvalue);
				writer.write("\t\t\t" + lvalue.generate_simple_code() + " <-- " + rvalue.generate_simple_code() + "\n");
			}
			writer.write("\t\tNEXP:\t" + norm_expr.generate_unique_code() + "\n");
			
			writer.write("\tEND\n");
			writer.write("\n");
		}
	}
	
	private static void write(CirTree tree, File output) throws Exception {
		FileWriter writer = new FileWriter(output);
		
		CirFunctionCallGraph graph = tree.get_function_call_graph();
		for(CirFunction function : graph.get_functions()) {
			writer.write("FUNC " + function.get_name() + "\n");
			CirExecutionFlowGraph fgraph = function.get_flow_graph();
			for(int k = 1; k <= fgraph.size(); k++) {
				CirExecution execution = fgraph.get_execution(k % fgraph.size());
				write_cir_node(writer, execution.get_statement(), 96);
			}
			writer.write("END FUNC\n");
		}
		
		writer.close();
	}
	
}
