package test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.AstFile;

import __backup__.CodeMutationType;
import __backup__.JCMT_Builder;
import __backup__.JCMT_Project;
import __backup__.JCMutationUtil;
import __backup__.Mutant;
import __backup__.MutantSpace;

/**
 * To check the experiment data in project
 * @author yukimula
 */
public class MutExperimentCheck {
	
	public static final String prefix = "../../../MyData/CODE2/";
	public static final String project_dir = prefix + "TestProjects/";
	public static final String project2_dir = prefix + "TestProjectsNew/";
	public static final String code_dir = prefix + "ifiles/";
	public static final String input_dir = prefix + "inputs/";
	public static final String suite_dir = prefix + "suite/";
	public static final String result_dir = "results/";
	public static void main(String[] args) throws Exception {
		// check_mutant("calendar", 1614, MutaCodeGenerator.WEAKNESS);
		// check_mutant("triangle", 40, MutaCodeGenerator.COVERAGE);
		// test_gen_mutations("triangle", new File(result_dir + "_test_.c"));
	}
	
	public static void check_mutant(String name, int mid, CodeMutationType mtype) throws Exception {
		File root = new File(project2_dir + name);
		File cfile = new File(code_dir + name + ".c");
		JCMT_Project project = get_project(root, cfile);
		check_mutation_code(project, mid, mtype, new File(result_dir + name + "_mutant_" + mid + ".c"));
	}
	public static void test_gen_mutations(String name, File test) throws Exception {
		JCMT_Project project = new_project(name);
		System.out.println(name + " : " + project.get_code_manager().get_mutant_space().size() + " mutants");
		
		MutantSpace mspace = project.
				get_code_manager().get_mutant_space();
		for(int k = 0; k < mspace.size(); k++) {
			System.out.println("\t--> Mutant #" + k + " (coverage)");
			check_mutation_code(project, k, CodeMutationType.coverage, test);
			System.out.println("\t--> Mutant #" + k + " (weakness)");
			check_mutation_code(project, k, CodeMutationType.weakness, test);
			System.out.println("\t--> Mutant #" + k + " (stronger)");
			check_mutation_code(project, k, CodeMutationType.stronger, test);
		}
	}
	
	/**
	 * Open an existing test project from file
	 * @param name
	 * @return
	 * @throws Exception
	 */
	protected static JCMT_Project get_project(File root, File cfile) throws Exception {
		JCMT_Project project = JCMT_Builder.open(root);
		project.get_test_manager().append_testDB(null);
		JCMT_Builder.set_muta_cursor(project, cfile); return project;
	}
	/**
	 * write the mutation to the code file
	 * @param project
	 * @param mutant
	 * @param output
	 * @throws Exception
	 */
	protected static void check_mutation_code(JCMT_Project 
			project, int mutant, CodeMutationType mtype, File output) throws Exception {
		AstFile source = project.get_code_manager().get_cursor();
		Mutant mut = project.get_code_manager().get_mutant_space().get(mutant);
		JCMutationUtil.write_mutation_to_file(mut, source, output, mtype);
	}
	/**
	 * create a new mutation project
	 * @param name
	 * @return
	 * @throws Exception
	 */
	protected static JCMT_Project new_project(String name) throws Exception {
		if(name == null || name.isEmpty())
			throw new IllegalArgumentException("invalid name: null");
		else {
			File root = new File(project2_dir + name);
			JCMT_Project project;
			
			/* input declarations */
			List<File> cfiles = new ArrayList<File>();
			cfiles.add(new File(code_dir + name + ".c"));
			
			List<File> suites = new ArrayList<File>();
			File[] files = new File(suite_dir + name).listFiles();
			for(int k = 0; k < files.length; k++) {
				if(!files[k].isDirectory()) suites.add(files[k]);
			}
			File inputs = new File(suite_dir + name);
			
			/* create test project */
			project = JCMT_Builder.create(root);
			JCMT_Builder.input(project, cfiles.iterator(), 
					suites.iterator(), inputs);
			
			/* set cursor for project */
			JCMT_Builder.set_muta_cursor(project, 
					new File(code_dir + name + ".c")); 
			
			/* return */	return project;
		}
	}
	
}
