package __backup__;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.jcsa.jcparse.lang.AstCirFile;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCallGraph;

/**
 * To build up the environment for a mutation
 * testing in its project (JCMTManager), including:<br>
 * <br>
 * 	1) create | open a mutation project;<br>
 * 	2) input code | test suite | inputs;<br>
 * 	3) update test suite | inputs;<br>
 * 	4) set the cursor and load mutants;<br>
 * 	5) dispatch the cache directories;<br>
 * @author yukimula
 */
public class JCMT_Builder {
	
	/* open or create a project */
	/**
	 * create an empty project for mutation testing...
	 * @param root
	 * @return
	 * @throws Exception
	 */
	public static JCMT_Project create(File root) throws Exception {
		if(root == null)
			throw new IllegalArgumentException("Invalid root: null");
		else {
			FileProcess.remove(root);
			return new JCMT_Project(root);
		}
	}
	/**
	 * open an existing mutation testing manager
	 * @param root
	 * @return
	 * @throws Exception
	 */
	public static JCMT_Project open(File root) throws Exception {
		if(root == null || !root.exists() || !root.isDirectory())
			throw new IllegalArgumentException("Undefined: " + root);
		else return new JCMT_Project(root);
	}
	
	/* input the program data */
	/**
	 * Set the inputs to the (created new) project.
	 * <br>
	 * This will clear all of the data files under the
	 * <code>xxx/project/source/</code>, 
	 * <code>xxx/project/test/</code>, and
	 * <code>xxx/project/score/</code>, including their
	 * cache file list. And rebuild the project, including:<br>
	 * <br>
	 * 	1) reset the code files directory;	<br>
	 * 	2) re-generate mutations for code;	<br>
	 * 	3) clear the _compile_ cache list;	<br>
	 * 	4) reset the testcase.db file;		<br>
	 * 	5) reset the test inputs directory; <br>
	 * 	6) clear the _exec_ cache list;		<br>
	 * 	7) reset the database in score;		<br>
	 * @param project
	 * @param cfiles
	 * @param tests
	 * @param inputs
	 * @throws Exception
	 */
	public static JCMT_Project input(JCMT_Project project, Iterator<File> cfiles, 
			Iterator<File> suites, File inputs) throws Exception {
		if(project == null)
			throw new IllegalArgumentException("Invalid project: null");
		else if(cfiles == null)
			throw new IllegalArgumentException("Invalid code: null");
		else if(suites == null)
			throw new IllegalArgumentException("Invalid test: null");
		else if(inputs == null || !inputs.exists() || !inputs.isDirectory()) 
			throw new IllegalArgumentException("Invalid inputs: null");
		else {
			/* declarations */
			CodeManager  code  = project.get_code_manager();
			TestManager  test  = project.get_test_manager();
			// ScoreManager score = project.get_score_manager();
			
			/* reset source/code/ */ 	code.reset_code(cfiles);
			/* reset source/muta/ */	code.reset_muta();
			/* reset source/mutac/ */	code.reset_mutac();
			/* reset source/_comp_ */	code.reset_cache(0);
			
			/* reset test/testcase.db */test.reset_testDB(suites);
			/* reset test/inputs/ */	test.reset_inputs(inputs);
			/* reset test/_exec_ */		test.reset_cache(0);
			
			/* reset score/xxx.db */	//score.reset(code.get_resource());
			
			/* return */	return project;
		}
		
	}
	/**
	 * append new test case to the mutation testing project
	 * @param project
	 * @param suites
	 * @return
	 * @throws Exception
	 */
	public static JCMT_Project append(JCMT_Project project, Iterator<File> suites,
			File inputs) throws Exception {
		if(project == null)
			throw new IllegalArgumentException("Invalid project: null");
		else {
			if(suites != null) 
				project.get_test_manager().append_testDB(suites); 
			if(inputs != null && inputs.isDirectory())
				project.get_test_manager().reset_inputs(inputs);
			return project;
		}
		
	}
	
	/* set the testing cursor and load mutants */
	/**
	 * set the cursor in xxx/project/code and reload all of the mutations
	 * from the database file 
	 * @param project
	 * @param cfile
	 * @return
	 * @throws Exception
	 */
	public static JCMT_Project set_muta_cursor(JCMT_Project project, File cfile) throws Exception {
		if(project == null)
			throw new IllegalArgumentException("Invalid project: null");
		else if(cfile == null || !cfile.exists() || cfile.isDirectory())
			throw new IllegalArgumentException("Invalid cfile: " + cfile);
		else {
			CodeManager code = project.get_code_manager();
			code.open_cursor(cfile); code.load_muta();
			return project;
		}
	}
	/**
	 * set the cursor in xxx/project/code and reload mutations of specified operators
	 * @param project
	 * @param cfile
	 * @param operators
	 * @return
	 * @throws Exception
	 */
	public static JCMT_Project set_muta_cursor(JCMT_Project project, 
			File cfile, MutOperator[] operators) throws Exception {
		if(project == null)
			throw new IllegalArgumentException("Invalid project: null");
		else if(cfile == null || !cfile.exists() || cfile.isDirectory())
			throw new IllegalArgumentException("Invalid cfile: " + cfile);
		else { 
			Set<MutOperator> ops = new HashSet<MutOperator>();
			if(operators != null) {
				for(int i = 0; i < operators.length; i++) {
					ops.add(operators[i]);
				}
			}
			CodeManager code = project.get_code_manager();
			
			code.open_cursor(cfile); 
			for(MutOperator op : ops)
				code.load_muta(op);
			return project;
		}
	}
	/**
	 * set the cursor in xxx/project/code and reload mutations of specified mutation modes
	 * @param project
	 * @param cfile
	 * @param operators
	 * @return
	 * @throws Exception
	 */
	public static JCMT_Project set_muta_cursor(JCMT_Project project,
			File cfile, MutationMode[] modes) throws Exception {
		if(project == null)
			throw new IllegalArgumentException("Invalid project: null");
		else if(cfile == null || !cfile.exists() || cfile.isDirectory())
			throw new IllegalArgumentException("Invalid cfile: " + cfile);
		else { 
			Set<MutationMode> mds = new HashSet<MutationMode>();
			if(modes != null) {
				for(int i = 0; i < modes.length; i++) {
					mds.add(modes[i]);
				}
			}
			CodeManager code = project.get_code_manager();
			
			code.open_cursor(cfile); 
			for(MutationMode mode : mds)
				code.load_muta(mode);
			return project;
		}
	}
	/**
	 * set the cursor in xxx/project/code and reload mutations of specified functions
	 * @param project
	 * @param cfile
	 * @param functions
	 * @return
	 * @throws Exception
	 */
	public static JCMT_Project set_muta_cursor(JCMT_Project project, 
			File cfile, String[] functions) throws Exception {
		if(project == null)
			throw new IllegalArgumentException("Invalid project: null");
		else if(cfile == null || !cfile.exists() || cfile.isDirectory())
			throw new IllegalArgumentException("Invalid cfile: " + cfile);
		else {
			Set<String> fnames = new HashSet<String>();
			if(functions != null) {
				for(int i = 0; i < functions.length; i++) 
					if(functions[i] != null) {
						fnames.add(functions[i]);
					}
			}
			CodeManager code = project.get_code_manager();
			
			code.open_cursor(cfile); 
			AstCirFile astfile = project.get_code_manager().get_cursor();
			CirFunctionCallGraph graph = astfile.get_cir_tree().get_function_call_graph();
			
			for(String name : fnames) {
				if(graph.has_function(name)) {
					CirFunction function = graph.get_function(name);
					code.load_muta((AstFunctionDefinition) function.get_definition().get_ast_source());
				}
			}
			
			return project;
		}
	}
	
	/* set the testing cursor and load context mutations */
	/**
	 * set the cursor in xxx/project/code/mutac and reload all of the 
	 * context mutations from the database file 
	 * @param project
	 * @param cfile
	 * @return
	 * @throws Exception
	 */
	public static JCMT_Project set_mutac_cursor(JCMT_Project project, File cfile) throws Exception {
		if(project == null)
			throw new IllegalArgumentException("Invalid project: null");
		else if(cfile == null || !cfile.exists() || cfile.isDirectory())
			throw new IllegalArgumentException("Invalid cfile: " + cfile);
		else {
			CodeManager code = project.get_code_manager();
			code.open_cursor(cfile); code.load_mutac();
			return project;
		}
	}
	/**
	 * set the cursor in xxx/project/code/mutac and reload all of the 
	 * context mutations from the database file with specified operators
	 * @param project
	 * @param cfile
	 * @param operators
	 * @return
	 * @throws Exception
	 */
	public static JCMT_Project set_mutac_cursor(JCMT_Project project, 
			File cfile, MutOperator[] operators) throws Exception {
		if(project == null)
			throw new IllegalArgumentException("Invalid project: null");
		else if(cfile == null || !cfile.exists() || cfile.isDirectory())
			throw new IllegalArgumentException("Invalid cfile: " + cfile);
		else { 
			Set<MutOperator> ops = new HashSet<MutOperator>();
			if(operators != null) {
				for(int i = 0; i < operators.length; i++) {
					ops.add(operators[i]);
				}
			}
			CodeManager code = project.get_code_manager();
			
			code.open_cursor(cfile); 
			for(MutOperator op : ops)
				code.load_mutac(op);
			return project;
		}
	}
	/**
	 * set the cursor in xxx/project/code/mutac and reload all of the 
	 * context mutations from the database file with specified modes.
	 * @param project
	 * @param cfile
	 * @param modes
	 * @return
	 * @throws Exception
	 */
	public static JCMT_Project set_mutac_cursor(JCMT_Project project,
			File cfile, MutationMode[] modes) throws Exception {
		if(project == null)
			throw new IllegalArgumentException("Invalid project: null");
		else if(cfile == null || !cfile.exists() || cfile.isDirectory())
			throw new IllegalArgumentException("Invalid cfile: " + cfile);
		else { 
			Set<MutationMode> mds = new HashSet<MutationMode>();
			if(modes != null) {
				for(int i = 0; i < modes.length; i++) {
					mds.add(modes[i]);
				}
			}
			CodeManager code = project.get_code_manager();
			
			code.open_cursor(cfile); 
			for(MutationMode mode : mds)
				code.load_mutac(mode);
			return project;
		}
	}
	/**
	 * set the cursor in xxx/project/code/mutac and reload all of the 
	 * context mutations from the database file for specified functions.
	 * @param project
	 * @param cfile
	 * @param functions
	 * @return
	 * @throws Exception
	 */
	public static JCMT_Project set_mutac_cursor(JCMT_Project project, 
			File cfile, String[] functions) throws Exception {
		if(project == null)
			throw new IllegalArgumentException("Invalid project: null");
		else if(cfile == null || !cfile.exists() || cfile.isDirectory())
			throw new IllegalArgumentException("Invalid cfile: " + cfile);
		else {
			Set<String> fnames = new HashSet<String>();
			if(functions != null) {
				for(int i = 0; i < functions.length; i++) 
					if(functions[i] != null) {
						fnames.add(functions[i]);
					}
			}
			CodeManager code = project.get_code_manager();
			
			code.open_cursor(cfile); 
			AstCirFile astfile = project.get_code_manager().get_cursor();
			CirFunctionCallGraph graph = astfile.get_cir_tree().get_function_call_graph();
			
			for(String name : fnames) {
				if(graph.has_function(name)) {
					CirFunction function = graph.get_function(name);
					code.load_muta((AstFunctionDefinition) function.get_definition().get_ast_source());
				}
			}
			
			return project;
		}
	}
	
	/* LOGO reporter */
	public static void report(String message) {
		if(message != null) {
			System.out.println("\t[M]: " + message);
		}
	}
	public static void report2(String message) {
		if(message != null) {
			System.out.println("\t\t[M]: " + message);
		}
	}
}
