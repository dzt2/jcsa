package test;

import java.io.File;

import com.jcsa.jcmutest.mutant.sta2mutant.base.AstContextTree;
import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.parse.CTranslate;

public class AstContextTreeTest {
	
	/* parameters */
	//private static final String root_path = "/home/dzt2/Development/Data/";
	private static final String code_path = "/home/dzt2/Development/Data/ifiles/";
	private static final File sizeof_template_file = new File("config/cruntime.txt");
	public static void main(String[] args) throws Exception {
		for(File cfile : new File(code_path).listFiles()) {
			System.out.println("Testing on " + cfile.getName());
			AstContextTree tree = parse(cfile);
			System.out.println("\t--> " + tree.number_of_nodes() + " nodes...");
		}
	}
	
	private static	AstContextTree parse(File cfile) throws Exception {
		CRunTemplate template = new CRunTemplate(sizeof_template_file);
		AstTree ast_tree = CTranslate.parse(cfile, ClangStandard.gnu_c89, template);
		CirTree cir_tree = CTranslate.parse(ast_tree, template);
		return new AstContextTree(ast_tree, cir_tree);
	}
	
	
	
	
	
}
