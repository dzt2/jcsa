package test;

import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.Queue;

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
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.program.AstCirTree;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.parse.parser2.AstCirLocalizer;

public class AstCirLocalizerTest {
	
	private static final String root_path = "/home/dzt2/Development/Data/ifiles/";
	private static final File sizeof_template_file = new File("config/cruntime.txt");
	private static final String output_directory = "results/loct/";
	
	public static void main(String[] args) throws Exception {
		int max_length = 96;
		for(File file : new File(root_path).listFiles()) {
			System.out.println("Testing on " + file.getName());
			AstCirTree ast_file = parse(file);
			SymbolFactory.set_config(ast_file.get_sizeof_template(), true);	// configure
			write_ast(ast_file, new File(output_directory + file.getName() + ".ast"), max_length);
			write_cir(ast_file, new File(output_directory + file.getName() + ".cir"), max_length);
			write_ast_cir_tree(ast_file, new File(output_directory + file.getName() + ".tre"), max_length);
		}
	}
	
	private static AstCirTree parse(File cfile) throws Exception { 
		return AstCirTree.parse(cfile, sizeof_template_file, ClangStandard.gnu_c89);
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
	
	private static String generate_code(Object source, int max_length) throws Exception {
		if(source == null) {
			return "NULL";
		}
		else if(source instanceof AstNode) {
			AstNode node = (AstNode) source;
			return strip_code(node.generate_code(), max_length);
		}
		else {
			CirNode node = (CirNode) source;
			return strip_code(node.generate_code(true), max_length);
		}
	}
	
	private static void	write_ast_node(FileWriter writer, AstCirLocalizer localizer, AstNode node, int max_length) throws Exception {
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
			
			CirNode cir_location = localizer.localize(node);
			CirStatement beg_statement = localizer.localize(node, true);
			CirStatement end_statement = localizer.localize(node, false);
			
			writer.write("\t\tBEG_STMT:\t\"" + generate_code(beg_statement, max_length) + "\"\n");
			writer.write("\t\tCIR_NODE:\t\"" + generate_code(cir_location,  max_length) + "\"\n");
			writer.write("\t\tEND_STMT:\t\"" + generate_code(end_statement, max_length) + "\"\n");
			writer.write("END\n\n");
		}
	}
	
	private static void write_ast(AstCirTree ast_file, File output_file, int max_length) throws Exception {
		FileWriter writer = new FileWriter(output_file);
		AstTree tree = ast_file.get_ast_tree();
		AstCirLocalizer localizer = new AstCirLocalizer(ast_file.get_cir_tree());
		
		Queue<AstNode> queue = new LinkedList<AstNode>();
		queue.add(tree.get_ast_root());
		while(!queue.isEmpty()) {
			AstNode node = queue.poll();
			for(int k = 0; k < node.number_of_children(); k++) {
				queue.add(node.get_child(k));
			}
			write_ast_node(writer, localizer, node, max_length);
		}
		
		writer.close();
	}
	
	private static void write_cir_node(FileWriter writer, AstCirLocalizer localizer, CirNode node, int max_length) throws Exception {
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
			
			AstNode source = localizer.localize(node);
			if(source  != null) {
				String ast_class = source.getClass().getSimpleName();
				ast_class = ast_class.substring(3, ast_class.length() - 4).strip();
				int line = source.get_location().line_of();
				writer.write("\t\tASTL:\t" + ast_class + "\tLINE#" + line + "\n");
				writer.write("\t\tASTS:\t\"" + strip_code(source.generate_code(), max_length) + "\"\n");
			}
			else {
				writer.write("\t\tASTL:\t#ERROR\n");
			}
			
			writer.write("\tEND\n\n");
		}
	}
	
	private static void write_cir(AstCirTree ast_file, File output_file, int max_length) throws Exception {
		FileWriter writer = new FileWriter(output_file);
		
		CirTree cir_tree = ast_file.get_cir_tree();
		CirFunctionCallGraph graph = cir_tree.get_function_call_graph();
		AstCirLocalizer localizer = new AstCirLocalizer(cir_tree);
		
		for(CirFunction function : graph.get_functions()) {
			writer.write("FUNC " + function.get_name() + "\n");
			CirExecutionFlowGraph fgraph = function.get_flow_graph();
			for(int k = 1; k <= fgraph.size(); k++) {
				CirExecution execution = fgraph.get_execution(k % fgraph.size());
				write_cir_node(writer, localizer, execution.get_statement(), 96);
			}
			writer.write("END FUNC\n");
		}
		
		writer.close();
	}
	
	private static void write_ast_cir_node(FileWriter writer, AstCirNode node, int max_length) throws Exception {
		writer.write("[BEG]\n");
		
		String node_type = node.get_node_type().toString();
		String edge_type = "" + node.get_child_type(); int node_id = node.get_node_id();
		writer.write("\tTYPE = " + node_type + "\tNODE = " + edge_type + "[" + node_id + "]\n");
		writer.write("\tCHILD[" + node.number_of_children() + "] = {");
		for(AstCirNode child : node.get_children()) {
			writer.write(" " + child.get_child_type() + "[" + child.get_node_id() + "]");
		}
		writer.write(" }\n");
		
		AstNode source = node.get_ast_source();
		String class_name = source.getClass().getSimpleName().strip();
		class_name = class_name.substring(3, class_name.length() - 4);
		int ast_key = source.get_key();
		AstFunctionDefinition definition = source.get_function_of();
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
		int code_line = source.get_location().line_of();
		String ast_code = strip_code(source.generate_code(), max_length);
		writer.write("\tASTN:\t" + class_name + "#" + ast_key + "\n");
		writer.write("\tLOCT:\t" + func_name + "#" + code_line + "\n");
		writer.write("\tCODE:\t\"" + ast_code + "\"\n");
		
		writer.write("[END]\n\n");
	}
	
	private static void write_ast_cir_tree(AstCirTree tree, File output_file, int max_length) throws Exception {
		FileWriter writer = new FileWriter(output_file);
		for(AstCirNode node : tree.get_tree_nodes()) {
			write_ast_cir_node(writer, node, max_length);
		}
		writer.close();
	}
	
	
	
}
