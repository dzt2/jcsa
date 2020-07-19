package test;

import java.io.File;
import java.io.FileWriter;

import com.jcsa.jcparse.lang.AstCirFile;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.astree.code.AstCodeGenerator;

public class ASTCodeTest {
	
	protected static final String prefix = "D:\\SourceCode\\MyData\\CODE2\\ifiles";
	protected static final String postfx = "D:\\SourceCode\\MyData\\CODE2\\gfiles\\"; 
	protected static final File template_file = new File("config/cruntime.txt");
	
	public static void main(String[] args) throws Exception {
		File dir = new File(prefix);
		File[] files = dir.listFiles();
		for(int k = 0; k < files.length; k++) {
			File file = files[k];
			System.out.println("Start testing: " + file.getName());
			
			AstCirFile source = parse(file);
			System.out.println("\t(1) Parse to AST and IR...");
			
			File output1 = new File(postfx + file.getName() + ".nrm.c");
			normal_code(source, output1);
			System.out.println("\t(2) Translate to normal...");
			
			File output2 = new File(postfx + file.getName());
			write_code(source, output2);
		}
		
		File[] ofiles = new File(postfx).listFiles();
		for(int k = 0; k < ofiles.length; k++) {
			File ofile = ofiles[k];
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
	
	private static AstCirFile parse(File file) throws Exception {
		return AstCirFile.parse(file, template_file, ClangStandard.gnu_c89);
	}
	private static void normal_code(AstCirFile source_program, File target_file) throws Exception {
		String code = AstCodeGenerator.generator.generate_code(
				source_program.get_ast_tree().get_ast_root());
		FileWriter writer = new FileWriter(target_file);
		writer.write(code); writer.close();
		parse(target_file);
	}
	private static void write_code(AstCirFile source_program, File target_file) throws Exception {
		FileWriter writer = new FileWriter(target_file);
		writer.write(source_program.get_ast_tree().get_ast_root().get_code());
		writer.close();
		parse(target_file);
	}
	
}
