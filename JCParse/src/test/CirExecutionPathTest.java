package test;

import java.io.File;
import java.io.FileWriter;

import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPathFinder;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.program.AstCirTree;

public class CirExecutionPathTest {

	protected static final String prefix = "/home/dzt2/Development/Data/ifiles/";
	protected static final String postfx = "result/paths/";
	protected static final File template_file = new File("config/cruntime.txt");

	public static void main(String[] args) throws Exception {
		for(File cfile : new File(prefix).listFiles()) {
			if(cfile.getName().endsWith(".c")) {
				// test_df_paths(cfile);
				// test_db_paths(cfile);
				test_vf_paths(cfile);
				// test_vb_paths(cfile);
				// test_sf_paths(cfile);
			}
		}
	}

	private static AstCirTree parse(File file) throws Exception {
		return AstCirTree.parse(file, template_file, ClangStandard.gnu_c89);
	}
	private static String strip_code(String code) {
		StringBuilder buffer = new StringBuilder();
		for(int k = 0; k < code.length(); k++) {
			char ch = code.charAt(k);
			if(Character.isWhitespace(ch)) {
				ch = ' ';
			}
			buffer.append(ch);
		}
		return buffer.toString();
	}
	private static void output_path(FileWriter writer, CirExecutionPath path) throws Exception {
		int counter = 0;
		writer.write("\tPath[" + path.get_source() + ", " + path.get_target() + "]\n");
		writer.write("\t\t");
		for(CirExecutionEdge edge : path.get_edges()) {
			if((++counter) % 6 == 0) {
				writer.write("\n\t\t");
			}
			writer.write(edge.get_source().toString());
			writer.write(" <" + edge.get_type() + "> ");
		}
		writer.write(path.get_target() + "\n");
	}

	/* decidable forward path extension testing */
	private static void output_df_path(FileWriter writer, CirExecution source) throws Exception {
		writer.write("\t" + source + ": " + strip_code(source.get_statement().generate_code(true)) + "\n");
		CirExecutionPath path = CirExecutionPathFinder.finder.df_extend(source);
		output_path(writer, path);
	}
	private static void output_df_paths(FileWriter writer, CirFunction function) throws Exception {
		System.out.println("BEG " + function.get_name());
		writer.write("BEG " + function.get_name() + "\n");
		for(int k = 1; k <= function.get_flow_graph().size(); k++) {
			CirExecution execution = function.get_flow_graph().get_execution(k % function.get_flow_graph().size());
			writer.write("\t" + execution.toString());
			output_df_path(writer, execution);
		}
		writer.write("END " + function.get_name() + "\n");
		System.out.println("END " + function.get_name());
	}
	protected static void test_df_paths(File cfile) throws Exception {
		System.out.println("Testing on " + cfile.getName());
		AstCirTree ast_file = parse(cfile);
		FileWriter writer = new FileWriter(new File(postfx + ast_file.get_source_file().getName() + ".txt"));
		for(CirFunction function : ast_file.get_cir_tree().get_function_call_graph().get_functions()) {
			output_df_paths(writer, function);
		}
		writer.close();
	}

	/* decidable backward path extension testing */
	private static void output_db_path(FileWriter writer, CirExecution source) throws Exception {
		writer.write("\t" + source + ": " + strip_code(source.get_statement().generate_code(true)) + "\n");
		CirExecutionPath path = CirExecutionPathFinder.finder.db_extend(source);
		output_path(writer, path);
	}
	private static void output_db_paths(FileWriter writer, CirFunction function) throws Exception {
		System.out.println("BEG " + function.get_name());
		writer.write("BEG " + function.get_name() + "\n");
		for(int k = 1; k <= function.get_flow_graph().size(); k++) {
			CirExecution execution = function.get_flow_graph().get_execution(k % function.get_flow_graph().size());
			writer.write("\t" + execution.toString());
			output_db_path(writer, execution);
		}
		writer.write("END " + function.get_name() + "\n");
		System.out.println("END " + function.get_name());
	}
	protected static void test_db_paths(File cfile) throws Exception {
		System.out.println("Testing on " + cfile.getName());
		AstCirTree ast_file = parse(cfile);
		FileWriter writer = new FileWriter(new File(postfx + ast_file.get_source_file().getName() + ".txt"));
		for(CirFunction function : ast_file.get_cir_tree().get_function_call_graph().get_functions()) {
			output_db_paths(writer, function);
		}
		writer.close();
	}

	/* virtual forward path extension testing */
	private static void output_vf_path(FileWriter writer, CirExecution source) throws Exception {
		writer.write("\t" + source + ": " + strip_code(source.get_statement().generate_code(true)) + "\n");
		CirExecutionPath path = new CirExecutionPath(source);
		CirExecutionPathFinder.finder.vf_extend(path, source.get_graph().get_exit());
		output_path(writer, path);
	}
	private static void output_vf_paths(FileWriter writer, CirFunction function) throws Exception {
		System.out.println("BEG " + function.get_name());
		writer.write("BEG " + function.get_name() + "\n");
		for(int k = 1; k <= function.get_flow_graph().size(); k++) {
			CirExecution execution = function.get_flow_graph().get_execution(k % function.get_flow_graph().size());
			writer.write("\t" + execution.toString());
			output_vf_path(writer, execution);
		}
		writer.write("END " + function.get_name() + "\n");
		System.out.println("END " + function.get_name());
	}
	protected static void test_vf_paths(File cfile) throws Exception {
		System.out.println("Testing on " + cfile.getName());
		AstCirTree ast_file = parse(cfile);
		FileWriter writer = new FileWriter(new File(postfx + ast_file.get_source_file().getName() + ".txt"));
		for(CirFunction function : ast_file.get_cir_tree().get_function_call_graph().get_functions()) {
			output_vf_paths(writer, function);
		}
		writer.close();
	}

	/* virtual backward path extension testing */
	private static void output_vb_path(FileWriter writer, CirExecution source) throws Exception {
		writer.write("\t" + source + ": " + strip_code(source.get_statement().generate_code(true)) + "\n");
		CirExecutionPath path = new CirExecutionPath(source);
		CirExecutionPathFinder.finder.vb_extend(path, source.get_graph().get_entry());
		output_path(writer, path);
	}
	private static void output_vb_paths(FileWriter writer, CirFunction function) throws Exception {
		System.out.println("BEG " + function.get_name());
		writer.write("BEG " + function.get_name() + "\n");
		for(int k = 1; k <= function.get_flow_graph().size(); k++) {
			CirExecution execution = function.get_flow_graph().get_execution(k % function.get_flow_graph().size());
			writer.write("\t" + execution.toString());
			output_vb_path(writer, execution);
		}
		writer.write("END " + function.get_name() + "\n");
		System.out.println("END " + function.get_name());
	}
	protected static void test_vb_paths(File cfile) throws Exception {
		System.out.println("Testing on " + cfile.getName());
		AstCirTree ast_file = parse(cfile);
		FileWriter writer = new FileWriter(new File(postfx + ast_file.get_source_file().getName() + ".txt"));
		for(CirFunction function : ast_file.get_cir_tree().get_function_call_graph().get_functions()) {
			output_vb_paths(writer, function);
		}
		writer.close();
	}

}
