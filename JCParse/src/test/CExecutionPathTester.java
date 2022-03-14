package test;

import java.io.File;
import java.io.FileWriter;

import com.jcsa.jcparse.lang.AstCirFile;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.test.state.CStateNode;
import com.jcsa.jcparse.test.state.CStatePath;

public class CExecutionPathTester {
	
	/* parameters */
	/** the path where the root directory of mutation test project is created **/
	static final String projects_directory = "/home/dzt2/Development/Data/projects/";
	/** the file used to configuration as the AstTree's sizeof evaluation **/
	static final File sizeof_template_file = new File("config/cruntime.txt");
	/** the directory where the execution path is written and visualized **/
	static final String outputs_directory = "results/paths/";
	/**
	 * It performs the main execution entry
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		for(File cfile : new File(projects_directory).listFiles()) {
			String name = cfile.getName();
			int beg_tid = 0;
			int end_tid = beg_tid + 96;
			testing(name, beg_tid, end_tid);
		}
	}
	
	/* functions */
	/**
	 * It derives projects/name/code/ifiles/name.c
	 * @param name
	 * @return
	 * @throws Exception
	 */
	private static AstCirFile derive_ast_cir(String name) throws Exception {
		String path = projects_directory + name + "/code/ifiles/" + name + ".c";
		return AstCirFile.parse(new File(path), sizeof_template_file, ClangStandard.gnu_c89);
	}
	/**
	 * It loads the coverage path from projects/name/test/s_output/tid.ins
	 * @param ast_file
	 * @param tid
	 * @return
	 * @throws Exception
	 */
	private static CStatePath load_state_path(String name, AstCirFile ast_file, int tid) throws Exception {
		String path = projects_directory + name + "/test/s_output/" + tid + ".ins";
		return CStatePath.read_path(ast_file.get_run_template(), ast_file.get_ast_tree(), ast_file.get_cir_tree(), new File(path));
	}
	/**
	 * @param name
	 * @param ast_file
	 * @param tid
	 * @throws Exception
	 */
	private static boolean write_state_path(String name, AstCirFile ast_file, int tid) throws Exception {
		CStatePath path;
		try {
			path = load_state_path(name, ast_file, tid);
		}
		catch(Exception ex) {
			path = null;
		}
		
		if(path != null) {
			File output = new File(outputs_directory + name + "." + tid + ".txt");
			FileWriter writer = new FileWriter(output);
			writer.write("EID\tCLS\tCIR\tLINE\tAST\tCODE\n");
			for(CStateNode state_node : path.get_nodes()) {
				CirStatement statement = state_node.get_statement();
				String eid = state_node.get_execution().toString();
				String cls = statement.getClass().getSimpleName();
				cls = cls.substring(3, cls.length() - 4).strip();
				String cir = strip_code(statement.generate_code(true), 96);
				
				String line = "null", ast = "", code = "";
				if(statement.get_ast_source() != null) {
					AstNode ast_node = statement.get_ast_source();
					line = "" + ast_node.get_location().line_of();
					ast = ast_node.getClass().getSimpleName();
					ast = ast.substring(3, ast.length() - 4).strip();
					code = strip_code(ast_node.generate_code(), 96);
					code = "\"" + code + "\"";
				}
				writer.write(eid + "\t" + cls + "\t" + cir + "\t" + line + "\t" + ast + "\t" + code);
				writer.write("\n");
			}
			writer.close();
			return true;
		}
		else {
			return false;
		}
	}
	/**
	 * remove the spaces
	 * @param code
	 * @param max_length
	 * @return
	 */
	private static String strip_code(String code, int max_length) {
		StringBuilder buffer = new StringBuilder();
		for(int k = 0; k < code.length() && k < max_length; k++) {
			char ch = code.charAt(k);
			if(Character.isWhitespace(ch)) {
				ch = ' ';
			}
			buffer.append(ch);
		}
		if(buffer.length() >= max_length) {
			buffer.append("...");
		}
		return buffer.toString();
	}
	/**
	 * @param name
	 * @param beg_tid
	 * @param end_tid
	 * @throws Exception
	 */
	protected static void testing(String name, int beg_tid, int end_tid) throws Exception {
		AstCirFile ast_file = derive_ast_cir(name);
		System.out.println("Testing on " + ast_file.get_source_file().getName());
		int total = 0, succeeds = 0;
		for(int tid = beg_tid; tid < end_tid; tid++) {
			if(write_state_path(name, ast_file, tid)) succeeds++;
			total++;
			//System.out.println("\tRead Coverage Path from test#" + tid);
		}
		double ratio = ((double) succeeds) / ((double) total);
		ratio = ((int) (ratio * 10000)) / 100.0;
		System.out.println("\t--> Succeed: " + succeeds + " (" + ratio + "%)");
		System.out.println();
	}
	
}
