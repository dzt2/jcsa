package test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.test.CommandUtil;
import com.jcsa.jcparse.test.cmd.CCompiler;
import com.jcsa.jcparse.test.file.JCTestProject;

public class JCProjectExecute {
	
	private static final ClangStandard lang_standard = ClangStandard.gnu_c89;
	private static final CommandUtil command_util = CommandUtil.linux_util;
	private static final CCompiler compiler = CCompiler.clang;
	private static final File c_template_file = new File("config/cruntime.txt");
	private static final File c_instrument_head_file = new File("config/jcinst.h");
	private static final File c_pre_process_mac_file = new File("config/linux.h");
	private static final List<String> compile_parameters = new ArrayList<String>();
	
	private static final String root_path = "/home/dzt2/Development/Data/Code2/";
	private static final File cfile_dir = new File(root_path + "cfiles");
	private static final File input_dir = new File(root_path + "inputs");
	private static final File tests_dir = new File(root_path + "tests");
	private static final File project_dir = new File(root_path + "projects");
	
	public static void main(String[] args) throws Exception {
		/*
		for(File file : cfile_dir.listFiles()) {
			if(file.getName().endsWith(".c")) {
				File[] files = get_input_files(file.getName());
				if(files != null) {
					System.out.println("Testing for " + file.getAbsolutePath());
					JCTestProject project = get_project(files);
					
					System.out.println();
				}
				else {
					// System.out.println("Unable to execute " + file.getAbsolutePath());
				}
			}
		}
		*/
		testing("bubble_sort");
	}
	protected static void testing(String name) throws Exception {
		compile_parameters.clear();
		compile_parameters.add("-lm");
		File[] files = get_input_files(name);
		
		if(files != null) {
			System.out.println("Testing for " + name);
			JCTestProject project = get_project(files);
			normal_testing(project, -1);
			instrument_testing(project, -1);
			System.out.println();
		}
		else {
			System.out.println("Unable to execute " + name);
		}
	}
	
	/**
	 * @param name
	 * @return [xxx.c, inputs/xxx/, tests/xxx.txt, project/xxx/]
	 * @throws Exception
	 */
	private static File[] get_input_files(String name) throws Exception {
		int index = name.indexOf(".");
		if(index >= 0)
			name = name.substring(0, index).strip();
		File cfile = new File(cfile_dir.getAbsolutePath() + "/" + name + ".c");
		File idir = new File(input_dir.getAbsolutePath() + "/" + name);
		File tfile = new File(tests_dir.getAbsolutePath() + "/" + name + ".c.txt");
		File pdir = new File(project_dir.getAbsolutePath() + "/" + name);
		
		if(cfile.exists() && tfile.exists()) {
			File[] results = new File[] { null, null, null, null };
			results[0] = cfile;
			results[1] = idir;
			results[2] = tfile;
			results[3] = pdir;
			if(!idir.exists()) idir.mkdir();
			return results;
		}
		else {
			return null;
		}
	}
	/**
	 * create a test-project for executing tests.
	 * @param files
	 * @return
	 * @throws Exception
	 */
	private static JCTestProject get_project(File[] files) throws Exception {
		JCTestProject project;
		if(files[3].exists()) {
			project = JCTestProject.open_project(files[3], command_util);
			List<File> tfiles = new ArrayList<File>();
			tfiles.add(files[2]);
			project.set_tests(tfiles, files[1]);
		}
		else {
			project = JCTestProject.new_project(files[3], command_util, compiler, lang_standard, 
					c_template_file, c_instrument_head_file, c_pre_process_mac_file, compile_parameters);
			List<File> cfiles = new ArrayList<File>();
			List<File> hfiles = new ArrayList<File>();
			List<File> lfiles = new ArrayList<File>();
			cfiles.add(files[0]);
			List<File> tfiles = new ArrayList<File>();
			tfiles.add(files[2]);
			project.set_code(cfiles, hfiles, lfiles);
			project.set_tests(tfiles, files[1]);
		}
		
		System.out.println("\t1. Get the project for " + files[0].getName());
		System.out.println("\t\t==> include " + project.get_test_part().get_test_inputs().number_of_inputs() + " test inputs.");
		return project;
	}
	/**
	 * execute normal testing on project
	 * @param project
	 * @param timeout 
	 * @throws Exception
	 */
	private static void normal_testing(JCTestProject project, long timeout) throws Exception {
		System.out.println("\t2. Execute normal testing on " + 
							project.get_test_part().get_test_inputs().number_of_inputs() + " inputs.");
		System.out.println("\t\t==> executing on...");
		project.normal_execute(project.get_test_part().get_test_inputs().get_inputs(), timeout);
		System.out.println("\t\t==> complete the normal testing execution.");
	}
	/**
	 * perform the instrumental testing on the project.
	 * @param project
	 * @param timeout
	 * @throws Exception
	 */
	private static void instrument_testing(JCTestProject project, long timeout) throws Exception {
		System.out.println("\t3. Execute instrumental testing on " + 
							project.get_test_part().get_test_inputs().number_of_inputs() + " inputs.");
		System.out.println("\t\t==> executing on...");
		project.instrument_execute(project.get_test_part().get_test_inputs().get_inputs(), timeout);
		System.out.println("\t\t==> complete the instrumental testing execution.");
	}
	
}
