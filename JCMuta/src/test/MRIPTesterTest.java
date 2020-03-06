package test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import __backup__.JCMConfig;
import __backup__.JCMT_Builder;
import __backup__.JCMT_Project;
import __backup__.JCMT_Tester;
import __backup__.Mutant;
import __backup__.MutantSpace;
import __backup__.TestCase;

public class MRIPTesterTest {
	
	public static final String prefix = "/home/dzt2/experiment/_expr_/";
	public static final String CODE_DIR = prefix + "CODE2/ifiles/";
	public static final String INPUT_DIR = prefix + "CODE2/inputs/";
	public static final String SUITE_DIR = prefix + "CODE2/suite/";
	public static final String PROJECT_DIR = prefix + "TestProjects/";
	
	protected static File[] include;
	protected static String[] library;
	protected static int buff_size;
	protected static long timeout;

	public static void main(String[] args) throws Exception {
		doExperiment("md4", 64, 1000, 3552);
	}
	
	/**
	 * set the testing arguments
	 * @param mtype
	 * @param bufferSize
	 * @param time_limit
	 */
	protected static void setting(int bufferSize, long time_limit) {
		include = new File[] {JCMConfig.JCM_CONFIG_ROOT};
		library = new String[] {"-lm"};
		buff_size = bufferSize; 
		timeout = time_limit;
	}
	/**
	 * execute the mutants against every tests in space 
	 * for coverage, weak and strong mutation testing.
	 * @param name
	 * @throws Exception
	 */
	protected static void doExperiment(String name, int bufferSize, long timeout, int begin_mutant) throws Exception {
		setting(bufferSize, timeout);
		/* get the mutation test project */
		JCMT_Project project = get(name);
		
		/* get the prepared data inputs */
		File cfile = get_code(project);
		
		/* set the project and get tester */
		JCMT_Tester tester = set(project, cfile, include, 
				library, buff_size, timeout);
		
		/* collect the inputs */
		MutantSpace mspace = project.get_code_manager().get_mutant_space();
		Collection<Mutant> M = new ArrayList<Mutant>(); int n = mspace.size();
		for(int k = begin_mutant; k < n; k++) { M.add(mspace.get(k)); }
		Iterator<TestCase> tests = project.get_test_manager().get_test_space().gets();
		Collection<TestCase> T = new ArrayList<TestCase>(); while(tests.hasNext()) T.add(tests.next());
		
		/* execute the testing for coverage, weak and strong */
		tester.exec(4, M, T); tester.close();
	}
	
	/**
	 * Get the test project from xxx/TestProject/{name}.
	 * If this project has existed, then open it.
	 * @param name
	 * @return
	 * @throws Exception
	 */
	protected static JCMT_Project get(String name) throws Exception {
		if(name == null || name.isEmpty())
			throw new IllegalArgumentException("invalid name: null");
		else {
			File root = new File(PROJECT_DIR + name);
			JCMT_Project project;
			
			/* open the project */
			if(root.exists()) {
				project = JCMT_Builder.open(root);
				report("Openning project at: " + root.getAbsolutePath());
			}
			/* create project */
			else {
				/* input declarations */
				List<File> cfiles = new ArrayList<File>();
				cfiles.add(new File(CODE_DIR + name + ".c"));
				
				List<File> suites = new ArrayList<File>();
				File[] files = new File(SUITE_DIR + name).listFiles();
				for(int k = 0; k < files.length; k++) {
					if(!files[k].isDirectory()) suites.add(files[k]);
				}
				File inputs = new File(INPUT_DIR + name);
				
				/* create test project */
				project = JCMT_Builder.create(root);
				JCMT_Builder.input(project, cfiles.iterator(), 
						suites.iterator(), inputs);
				report("Creating project at: " + root.getAbsolutePath());
			}
			
			/* return */	return project;
		}
	}
	/**
	 * Set the cursor in project and configure the tester
	 * for its compilation arguments (and testing arguments).
	 * @param project
	 * @param cfile
	 * @param include
	 * @param library
	 * @param buff_size
	 * @param timeout
	 * @return
	 * @throws Exception
	 */
	protected static JCMT_Tester set(JCMT_Project project, File cfile, 
			File[] include, String[] library, int buff_size, long timeout) throws Exception {
		/* declarations */
		List<File> incl_list = new ArrayList<File>();
		if(include != null) {
			for(int i = 0; i < include.length; i++)
				incl_list.add(include[i]);
		}
		List<String> lib_list = new ArrayList<String>();
		if(library != null) {
			for(int i = 0; i < library.length; i++)
				lib_list.add(library[i]);
		}
		
		/* set cursor for project */
		JCMT_Builder.set_muta_cursor(project, cfile);
		
		/* return */
		JCMT_Tester tester = new JCMT_Tester(project);
		tester.open(incl_list, lib_list, buff_size, timeout, true);
		report("Set project to: " + cfile.getAbsolutePath()); 
		return tester;
	}
	
	/* basic methods */
	private static void report(String msg) {
		System.out.println("[MSG]: " + msg);
	}
	private static File get_code(JCMT_Project project) throws Exception {
		File cfile = null; 
		File[] cfiles = project.get_resource().
				get_source().get_code().get_files();
		for(int i = 0; i < cfiles.length; i++) {
			String fname = cfiles[i].getName();
			if(!fname.equals("main.c")) {
				cfile = cfiles[i]; break;
			}
		}
		if(cfile == null)
			throw new IllegalArgumentException("No .c file is found");
		else return cfile;
	}
}
