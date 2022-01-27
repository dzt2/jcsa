package test;

import java.io.File;

import com.jcsa.jcmutest.mutant.sta2mutant.StateMutationsWriter;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcmutest.project.util.MuCommandUtil;

public class MuTestProjectFeatureWriter {
	
	private static final String root_path = "/home/dzt2/Development/Data/projects/";
	private static String result_dir = "/home/dzt2/Development/Data/zext/features/";
	private static final int max_subsumption_distance = 12;
	
	public static void main(String[] args) throws Exception {
		for(File root : new File(root_path).listFiles()) {
			testing(root);
		}
	}
	
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
		
		/* TODO only implement the static features writing here... */
		StateMutationsWriter.write_static_features(code_file, output_directory, max_subsumption_distance);
		System.out.println();
	}
	
}
