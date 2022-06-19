package test;

import java.io.File;

import com.jcsa.jcmutest.mutant.ctx2mutant.ContextMutationZ3Code;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcmutest.project.util.MuCommandUtil;

public class MuTestContextZ3CodeTest {
	
	//private static final String root_path = "/home/dzt2/Development/Data/projects/";
	//private static String result_dir = "/home/dzt2/Development/Data/zext3/features/"; 
	private static final String root_path = "/home/dzt2/Development/Data/projectsAll/";
	private static String result_dir = "/home/dzt2/Development/Data/zext3/featuresBIG/";
	
	/* testing functions */
	private static MuTestProject get_project(File root) throws Exception {
		return new MuTestProject(root, MuCommandUtil.linux_util);
	}
	private static void testing(File root) throws Exception {
		/* 1. open project and get data interface */
		MuTestProject project = get_project(root);
		File output_directory = new File(result_dir + project.get_name());
		System.out.println("Testing on " + project.get_name() + " for writing features.");
		FileOperations.mkdir(output_directory);
		MuTestProjectCodeFile code_file = project.get_code_space().get_code_files().iterator().next();
		
		/* only implement the static features writing here... */
		ContextMutationZ3Code.write_features(code_file, output_directory);
		System.out.println();
	}
	public static void main(String[] args) throws Exception {
		for(File root : new File(root_path).listFiles()) {
			if(!root.getName().equals("md4"))
				testing(root);
		}
	}
	
}
