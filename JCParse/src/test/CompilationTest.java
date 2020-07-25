package test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.AstCirFile;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.test.CCompiler;
import com.jcsa.jcparse.test.CCompilers;

public class CompilationTest {
	
	private static final String cdirectory = "/home/dzt2/Development/DataSet/Code/cfiles/";
	private static final String idirectory = "/home/dzt2/Development/DataSet/Code/sfiles/";
	private static final String edirectory = "/home/dzt2/Development/DataSet/Code/efiles/";
	
	public static void main(String[] args) throws Exception {
		File[] cfiles = new File(cdirectory).listFiles();
		for(File cfile : cfiles) {
			if(cfile.getName().endsWith(".c")) {
				System.out.println("Testing on " + cfile.getName());
				File tfile = testing_preprocess(cfile);
				testing_compilation(tfile);
				testing_parsing(tfile);
				System.out.println();
			}
		}
	}
	
	protected static File testing_preprocess(File source_file) throws Exception {
		File tfile = new File(idirectory + source_file.getName());
		File hfile = new File("config/linux.h");
		List<File> hfiles = new ArrayList<File>();
		hfiles.add(new File(cdirectory));
		
		CCompiler compiler = CCompiler.get_compiler(CCompilers.clang);
		if(compiler.do_preprocess(source_file, tfile, hfile, hfiles)) {
			System.out.println("\t1. Preprocess to generate " + tfile.getAbsolutePath());
			return tfile;
		}
		else {
			throw new RuntimeException("Unable to preprocess " + source_file.getAbsolutePath());
		}
	}
	
	protected static void testing_parsing(File tfile) throws Exception {
		try {
			AstCirFile.parse(tfile, new File("config/cruntime.txt"), ClangStandard.gnu_c89);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	protected static File testing_compilation(File tfile) throws Exception {
		List<File> cfiles = new ArrayList<File>();
		cfiles.add(tfile);
		List<File> hfiles = new ArrayList<File>();
		List<File> lfiles = new ArrayList<File>();
		List<String> params = new ArrayList<String>();
		params.add("-lm");
		File efile = new File(edirectory + tfile.getName() + ".exe");
		
		CCompiler compiler = CCompiler.get_compiler(CCompilers.clang);
		if(compiler.do_compile(cfiles, hfiles, lfiles, params, efile)) {
			System.out.println("\t2. Succeed to compile " + efile.getAbsolutePath());
		}
		else {
			System.out.println("\t2. Failed to compile " + efile.getAbsolutePath());
		}
		return efile;
	}
	
}
