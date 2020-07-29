package test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.test.exe.CCompiler;
import com.jcsa.jcparse.test.exe.CommandUtil;
import com.jcsa.jcparse.test.file.JCTestConfig;
import com.jcsa.jcparse.test.file.JCTestProject;

public class JCProjectTesting {
	
	private static final ClangStandard lang_standard = ClangStandard.gnu_c89;
	private static final CommandUtil command_util = CommandUtil.linux_util;
	private static final CCompiler compiler = CCompiler.clang;
	private static final File c_template_file = new File("config/cruntime.txt");
	private static final File c_instrument_head_file = new File("config/jcinst.h");
	private static final File c_pre_process_mac_file = new File("config/linux.h");
	private static final List<String> compile_parameters = new ArrayList<String>();
	
	private static final String prefix = "/home/dzt2/Development/Data/Code2/cfiles/";
	private static final String postfix = "/home/dzt2/Development/Data/Code2/projects/";
	
	public static void main(String[] args) throws Exception {
		compile_parameters.add("-lm");
		for(File cfile : new File(prefix).listFiles()) {
			if(cfile.getName().endsWith(".c")) {
				try {
					System.out.println("Create project at " + cfile.getAbsolutePath());
					// create_project(cfile);
					open_project(cfile);
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	protected static JCTestProject create_project(File cfile) throws Exception {
		String name = cfile.getName();
		int index = name.indexOf(".");
		name = name.substring(0, index).strip();
		JCTestProject project = JCTestProject.new_project(
				new File(postfix + name), command_util, 
				compiler, lang_standard, c_template_file, 
				c_instrument_head_file, c_pre_process_mac_file, 
				compile_parameters);
		List<File> cfiles = new ArrayList<File>();
		List<File> hfiles = new ArrayList<File>();
		List<File> lfiles = new ArrayList<File>();
		cfiles.add(cfile);
		project.set_code(cfiles, hfiles, lfiles);
		return project;
	}
	
	protected static JCTestProject open_project(File cfile) throws Exception {
		String name = cfile.getName();
		int index = name.indexOf(".");
		name = name.substring(0, index).strip();
		JCTestProject project = 
				JCTestProject.open_project(new File(postfix + name), command_util);
		
		JCTestConfig config = project.get_config();
		System.out.println("\t" + config.get_lang_standard().toString());
		System.out.println("\t" + config.get_command_util().toString());
		System.out.println("\t" + config.get_compiler().toString());
		System.out.println("\t" + config.get_compile_parameters().toString());
		
		return project;
	}
	
}
