package test;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Random;

import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.MuTestProjectTestSpace;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.test.state.CStateUnit;

public class MutExecutionPathTest {

	private static final Random random = new Random();
	private static final String root_path = "/home/dzt2/Development/Data/rprojects/";
	private static final String result_dir = "result/paths/";

	private static MuTestProject get_project(File root) throws Exception {
		return new MuTestProject(root, MuCommandUtil.linux_util);
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
	@SuppressWarnings("unchecked")
	private static void write_execution_path(FileWriter writer, CirExecutionPath path) throws Exception {
		for(CirExecutionEdge edge : path.get_edges()) {
			writer.write(edge.toString() + "\n");
			writer.write("\t" + edge.get_target().toString() + ": ");
			writer.write(strip_code(edge.get_target().get_statement().generate_code(true)) + "\n");
			if(edge.get_annotation() instanceof List) {
				List<CStateUnit> units = (List<CStateUnit>) edge.get_annotation();
				for(CStateUnit unit : units) {
					writer.write("\t" + strip_code(unit.get_expression().generate_code(true)));
					writer.write(": " + strip_code(SymbolFactory.sym_expression(unit.get_value()).generate_code(false)) + "\n");
				}
			}
		}
	}
	public static void testing(File root) throws Exception {
		MuTestProject project = get_project(root);
		MuTestProjectTestSpace tspace = project.get_test_space();
		int test_number = tspace.number_of_test_inputs();
		int test_id = Math.abs(random.nextInt()) % test_number;
		for(int k = 0; k < 16; k++) {
			MuTestProjectCodeFile code_file = project.get_code_space().get_code_files().iterator().next();
			CirExecutionPath path = tspace.load_execution_path(code_file.get_sizeof_template(),
					code_file.get_ast_tree(), code_file.get_cir_tree(), tspace.get_test_space().get_input(test_id));
			if(path != null) {
				FileWriter writer = new FileWriter(new File(result_dir + code_file.get_name() + ".txt"));
				writer.write("Test: " + tspace.get_test_space().get_input(test_id).get_parameter() + "\n");
				write_execution_path(writer, path);
				writer.close();
				break;
			}
		}
	}
	public static void main(String[] args) throws Exception {
		for(File root : new File(root_path).listFiles()) {
			testing(root);
		}
	}

}
