package test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.AstCirFile;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.test.CommandUtil;
import com.jcsa.jcparse.test.cmd.CCompiler;

public class CompilationTest {

	private static final String cdirectory = "/home/dzt2/Development/Data/Code/cfiles/";
	private static final String idirectory = "/home/dzt2/Development/Data/Code/sfiles/";
	private static final String edirectory = "/home/dzt2/Development/Data/Code/efiles/";

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

	protected static File testing_preprocess(File cfile) throws Exception {
		File ifile = new File(idirectory + cfile.getName());
		File mfile = new File("config/linux.h");
		List<File> mfiles = new ArrayList<>();
		mfiles.add(mfile);
		List<File> hdirs = new ArrayList<>();
		hdirs.add(new File(cdirectory));

		if(CommandUtil.linux_util.do_preprocess(CCompiler.clang, cfile, ifile, hdirs, mfiles)) {
			System.out.println("\t1. Preprocess to generate " + ifile.getAbsolutePath());
			return ifile;
		}
		else {
			throw new RuntimeException("Unable to preprocess " + cfile.getAbsolutePath());
		}
	}

	protected static File testing_compilation(File tfile) throws Exception {
		List<File> ifiles = new ArrayList<>();
		ifiles.add(tfile);
		List<File> hdirs = new ArrayList<>();
		List<File> lfiles = new ArrayList<>();
		List<String> params = new ArrayList<>();
		params.add("-lm");
		File efile = new File(edirectory + tfile.getName() + ".exe");

		if(CommandUtil.linux_util.do_compile(CCompiler.clang, ifiles, efile, hdirs, lfiles, params)) {
			System.out.println("\t2. Succeed to compile " + efile.getAbsolutePath());
		}
		else {
			System.out.println("\t2. Failed to compile " + efile.getAbsolutePath());
		}

		return efile;
	}

	protected static void testing_parsing(File tfile) throws Exception {
		try {
			AstCirFile.parse(tfile, new File("config/cruntime.txt"), ClangStandard.gnu_c89);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

}
