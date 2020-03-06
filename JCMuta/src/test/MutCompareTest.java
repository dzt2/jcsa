package test;

import java.io.File;

import __backup__.CodeMutationType;
import __backup__.JCMT_Builder;
import __backup__.JCMT_Project;
import __backup__.MutDifference;
import __backup__.Mutant;
import __backup__.MutantSpace;
import __backup__.TestOracleManager;

public class MutCompareTest {
	
	/* arguments */
	protected static final String prefix = "../../../MyData/CODE2/";
	protected static final String cfidir = prefix + "ifiles/";
	protected static final String eqvdir = prefix + "nequiv/";
	protected static final String prodir = prefix + "TestProjects/";
	protected static final String result_dir = "results/";
	
	/* execution methods */
	/**
	 * execute the main testing
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		do_experiment();
	}
	private static void do_experiment() throws Exception {
		File[] files = new File(prodir).listFiles();
 		for(int k = 0; k < files.length; k++) {
 			System.out.println("Test project -- " + files[k].getName());
 			do_experiment(files[k].getName()); System.out.println("");
 		}
	}
	private static void do_experiment(String name) throws Exception {
		File[] files = get_files_of(name);
		JCMT_Project project = get_project(files[0], files[1]);
		System.out.println("\t1. Open project: " + name);
		generate_differences(project, files[1]);
		System.out.println("\t2. generate differences...");
	}
	
	/* project getters */
 	/**
	 * get the [project_root; code_file; nequiv_file; ]
	 * @param name
	 * @return
	 * @throws Exception
	 */
	private static File[] get_files_of(String name) throws Exception {
		File root = new File(prodir + name);
		File cfile = new File(cfidir + name + ".c");
		File efile = new File(eqvdir + name + ".txt");
		return new File[] { root, cfile, efile };
	}
	/**
	 * Open an existing test project from file
	 * @param name
	 * @return
	 * @throws Exception
	 */
	private static JCMT_Project get_project(File root, File cfile) throws Exception {
		JCMT_Project project = JCMT_Builder.open(root);
		JCMT_Builder.set_muta_cursor(project, cfile); return project;
	}
	/**
	 * Generate the difference information in DB file of the project
	 * @param project
	 * @throws Exception
	 */
	private static void generate_differences(JCMT_Project project, File cfile) throws Exception {
		TestOracleManager oracle = project.
				get_oracle_manager(cfile, CodeMutationType.coverage);
		
		MutantSpace mspace = project.get_code_manager().get_mutant_space();
		for(int i = 0; i < mspace.size(); i++) {
			Mutant source = mspace.get(i);
			for(int j = i + 1; j < mspace.size(); j++) {
				Mutant target = mspace.get(j);
				MutDifference diff = oracle.compare(source, target);
				System.out.println("\t\t(" + i + ", " + j + "): \t" + diff.get_distance());
			}
		}
		
	}
	
}
