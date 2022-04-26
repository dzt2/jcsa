package test;

import java.io.File;
import java.io.FileWriter;

import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.program.AstCirEdge;
import com.jcsa.jcparse.lang.program.AstCirLink;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.program.AstCirTree;
import com.jcsa.jcparse.lang.program.types.AstCirLinkType;
import com.jcsa.jcparse.lang.program.types.AstCirNodeType;
import com.jcsa.jcparse.lang.program.types.AstCirParChild;

public class ASTCodeTest {

	protected static final String prefix = "/home/dzt2/Development/Data/ifiles/";
	protected static final String postfx = "results/cfiles/";
	protected static final File template_file = new File("config/cruntime.txt");

	public static void main(String[] args) throws Exception {
		File dir = new File(prefix);
		File[] files = dir.listFiles();
		for (File file : files) {
			System.out.println("Start testing: " + file.getName());

			AstCirTree source = parse(file);
			System.out.println("\t(1) Parse to AST and IR...");

			File output1 = new File(postfx + file.getName() + ".nrm.c");
			normal_code(source, output1);
			System.out.println("\t(2) Translate to normal...");

			File output2 = new File(postfx + file.getName());
			write_code(source, output2);
			
			File output3 = new File(postfx + file.getName() + ".aci");
			write_ast_cir_tree(source, output3);
		}

		File[] ofiles = new File(postfx).listFiles();
		for (File ofile : ofiles) {
			if(true) {
				try {
					parse(ofile);
					System.out.println(ofile.getName() + " being parsed");
				}
				catch(Exception ex) {
					ex.printStackTrace();
					System.out.println(ofile.getName() + " being failed");
				}
			}
		}
	}

	private static AstCirTree parse(File file) throws Exception {
		return AstCirTree.parse(file, template_file, ClangStandard.gnu_c89);
	}
	private static void normal_code(AstCirTree source_program, File target_file) throws Exception {
		/*
		source_program.get_ast_tree().generate(true, target_file);
		parse(target_file);
		*/
	}
	private static void write_code(AstCirTree source_program, File target_file) throws Exception {
		source_program.get_ast_tree().generate(false, target_file);
		parse(target_file);
	}
	private static String strip_code(String code, int max_length) throws Exception {
		StringBuilder buffer = new StringBuilder();
		for(int k = 0; k < code.length(); k++) {
			char ch = code.charAt(k);
			if(Character.isWhitespace(ch)) {
				ch = ' ';
			}
			buffer.append(ch);
			if(buffer.length() > max_length) {
				buffer.append("..."); break;
			}
		}
		return buffer.toString();
	}
	private	static void write_node(FileWriter writer, AstCirNode tree_node) throws Exception {
		int node_id = tree_node.get_node_id();
		AstCirNodeType node_type = tree_node.get_node_type();
		AstNode ast_source = tree_node.get_ast_source();
		Object token = tree_node.get_token();
		String token_code = "null";
		if(token != null) {
			token_code = strip_code(token.toString(), 96);
		}
		AstCirParChild edge = tree_node.get_child_type();
		String ast_class = ast_source.getClass().getSimpleName();
		ast_class = ast_class.substring(3, ast_class.length() - 4).strip();
		String ast_code = strip_code(ast_source.generate_code(), 96);
		int code_line = ast_source.get_location().line_of();
		
		writer.write("[" + node_id + "]");
		writer.write("\t" + node_type);
		writer.write("\t{" + token_code + "}");
		writer.write("\t<" + edge + ">\n");
		writer.write("\t" + ast_class);
		writer.write("\t#" + code_line);
		writer.write("\t\"" + ast_code + "\"\n");
		writer.write("\tChild\t");
		for(AstCirNode child : tree_node.get_children()) {
			writer.write("\t" + child.get_node_id());
		}
		writer.write("\n");
		for(AstCirLink link : tree_node.get_links()) {
			AstCirLinkType cir_type = link.get_type();
			CirNode cir_node = link.get_target();
			String cir_class = cir_node.getClass().getSimpleName();
			cir_class = cir_class.substring(3, cir_class.length() - 4);
			String cir_code = strip_code(cir_node.generate_code(true), 96);
			writer.write("\tlink\t" + cir_type + "\t\"" + cir_code + "\"\n");
		}
		for(AstCirEdge dep_edge : tree_node.get_ou_edges()) {
			writer.write("\tedge\t" + dep_edge.get_type() + "\t" + dep_edge.get_target().get_node_id() + "\n");
		}
	}
	private	static void write_ast_cir_tree(AstCirTree tree, File output) throws Exception {
		FileWriter writer = new FileWriter(output);
		for(AstCirNode tree_node : tree.get_tree_nodes()) {
			write_node(writer, tree_node);
		}
		writer.close();
	}
	
}
