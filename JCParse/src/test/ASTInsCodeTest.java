package test;

import java.io.File;
import java.io.FileWriter;

import com.jcsa.jcparse.lang.AstCirFile;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.code.CodeGeneration;
import com.jcsa.jcparse.lang.irlang.CirTree;

public class ASTInsCodeTest {
	
	protected static final String prefix = "/home/dzt2/Development/DataSet/Code/ifiles/";
	protected static final String postfx = "/home/dzt2/Development/DataSet/Code/gfiles/"; 
	protected static final File template_file = new File("config/cruntime.txt");
	
	private static AstCirFile parse(File file) throws Exception {
		return AstCirFile.parse(file, template_file, ClangStandard.gnu_c89);
	}
	private static void output_source_code(AstTree tree, File output_file) throws Exception {
		String code = CodeGeneration.generate_code(tree.get_ast_root());
		FileWriter writer = new FileWriter(output_file);
		writer.write(code);
		writer.close();
	}
	private static void output_instrument_code(AstTree tree, File result_file, File output_file) throws Exception {
		String code = CodeGeneration.instrument_code(tree, result_file);
		FileWriter writer = new FileWriter(output_file);
		writer.write(code);
		writer.close();
	}
	private static void output_cir_code(CirTree tree, File output_file) throws Exception {
		String code = CodeGeneration.generate_code(true, tree.get_root());
		FileWriter writer = new FileWriter(output_file);
		writer.write(code);
		writer.close();
	}
	
	private static void testing(File source_file) throws Exception {
		System.out.println("Testing on " + source_file.getName());
		
		AstCirFile ast_cir_file = parse(source_file);
		File ast_output_file = new File(postfx + "src/" + source_file.getName());
		File cir_output_file = new File(postfx + "cir/" + source_file.getName() + ".cir");
		File ins_output_file = new File(postfx + "ins/" + source_file.getName());
		File res_output_file = new File(postfx + "res/" + source_file.getName() + ".txt");
		System.out.println("\t1. Parse from " + source_file.getAbsoluteFile());
		
		output_source_code(ast_cir_file.get_ast_tree(), ast_output_file);
		System.out.println("\t2. Generate source code of " + source_file.getName());
		
		output_cir_code(ast_cir_file.get_cir_tree(), cir_output_file);
		System.out.println("\t3. Generate CIR source code of " + source_file.getName());
		
		output_instrument_code(
						ast_cir_file.get_ast_tree(), res_output_file, ins_output_file);
		System.out.println("\t4. Generate instrumental code of " + source_file.getName());
		
		System.out.println();
	}
	
	public static void main(String[] args) throws Exception {
		File dir = new File(prefix);
		File[] files = dir.listFiles();
		for(File file : files) {
			testing(file);
		}
	}
	
}
