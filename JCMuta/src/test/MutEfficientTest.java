package test;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import __backup__.CodeMutationType;
import __backup__.JCMConfig;
import __backup__.JCMT_Builder;
import __backup__.JCMT_Project;
import __backup__.JCMT_Tester;
import __backup__.Mutant;
import __backup__.MutantSpace;
import __backup__.TestCase;
import __backup__.TestSpace;

public class MutEfficientTest {
	
	public static final String prefix = "/home/dzt2/experiment/_expr_/";
	public static final String CODE_DIR = prefix + "CODE/ifiles/";
	public static final String INPUT_DIR = prefix + "CODE/inputs/";
	public static final String SUITE_DIR = prefix + "CODE/suite/";
	public static final String PROJECT_DIR = prefix + "TestProjects/";
	
	protected static File[] include;
	protected static String[] library;
	protected static int buff_size;
	protected static long timeout;
	protected static CodeMutationType type;
	protected static int threads;
	/**
	 * main executor
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		test_experiment("heap_sort", 0, 5 * 1024, 1000); 
	}
	
	/* executor methods */
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
		type = CodeMutationType.stronger;
		threads = 4;
	}
	/**
	 * experiment testing

	 * @param name
	 * @param beg_mutant
	 * @param bufferSize
	 * @param timtout
	 * @throws Exception
	 */
	protected static void test_experiment(String name, int beg_mutant, int bufferSize, long timeout) throws Exception {
		setting(bufferSize, timeout);
		JCMT_Project project = get(name);
		report("(1) Get the mutation test project.");
		
		File cfile = new File(CODE_DIR + name + ".c");
		JCMT_Tester tester = set(project, cfile);
		report("(2) Get the mutation tester item.");
		
		List<Mutant> mutants = new ArrayList<Mutant>();
		List<TestCase> tests = new ArrayList<TestCase>();
		collect_mutants(project, beg_mutant, mutants);
		collect_tests(project, tests);
		report("(3) Get " + mutants.size() + 
				" mutants and " + tests.size() + " tests.");
		
		/* execute the testing for coverage, weak and strong */
		tester.exec(4, mutants, tests); tester.close();
	}
	/* input-output methods */
	/**
	 * Get the test project from xxx/TestProject/{name}.
	 * If this project has existed, then open it.
	 * @param name
	 * @return
	 * @throws Exception
	 */
	private static JCMT_Project get(String name) throws Exception {
		if(name == null || name.isEmpty())
			throw new IllegalArgumentException("invalid name: null");
		else {
			File root = new File(PROJECT_DIR + name);
			JCMT_Project project;
			
			/* open the project */
			if(root.exists()) {
				project = JCMT_Builder.open(root);
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
	private static JCMT_Tester set(JCMT_Project project, File cfile) throws Exception {
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
		tester.open(incl_list, lib_list, buff_size, timeout, true);	// ignore the results
		return tester;
	}
	/* basic methods */
	private static void report(String msg) {
		System.out.println("[MSG]: " + msg);
	}
	private static void collect_mutants(JCMT_Project project, int begin, List<Mutant> mutants) throws Exception {
		mutants.clear();
		MutantSpace mspace = project.get_code_manager().get_mutant_space();
		for(int k = begin; k < mspace.size(); k++) mutants.add(mspace.get(k));
	}
	private static void collect_tests(JCMT_Project project, List<TestCase> tests) throws Exception {
		TestSpace tspace = project.get_test_manager().get_test_space();
		Iterator<TestCase> iter = tspace.gets(); tests.clear();
		while(iter.hasNext()) tests.add(iter.next());
	}
	
}
