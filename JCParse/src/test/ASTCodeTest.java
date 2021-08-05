package test;

import java.io.File;

import com.jcsa.jcparse.lang.AstCirFile;
import com.jcsa.jcparse.lang.ClangStandard;

public class ASTCodeTest {

	protected static final String prefix = "/home/dzt2/Development/DataSet/Code/ifiles/";
	protected static final String postfx = "/home/dzt2/Development/DataSet/Code/gfiles/";
	protected static final File template_file = new File("config/cruntime.txt");

	public static void main(String[] args) throws Exception {
		File dir = new File(prefix);
		File[] files = dir.listFiles();
		for (File file : files) {
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

	private static AstCirFile parse(File file) throws Exception {
		return AstCirFile.parse(file, template_file, ClangStandard.gnu_c89);
	}
	private static void normal_code(AstCirFile source_program, File target_file) throws Exception {
		/*
		source_program.get_ast_tree().generate(true, target_file);
		parse(target_file);
		*/
	}
	private static void write_code(AstCirFile source_program, File target_file) throws Exception {
		source_program.get_ast_tree().generate(false, target_file);
		parse(target_file);
	}

}
