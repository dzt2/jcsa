package test;

import java.io.File;

import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.MuTestProjectFeatureWriter;
import com.jcsa.jcmutest.project.util.MuCommandUtil;

public class MuTestProjectFeatureWritings {
	
	private static final String root_path = "/home/dzt2/Development/Data/rprojects/";
	private static final String result_dir = "result/features/";
	private static final int max_distance = 1;
	
	public static void main(String[] args) throws Exception {
		for(File root : new File(root_path).listFiles()) {
			testing(root);
		}
	}
	
	private static MuTestProject get_project(File root) throws Exception {
		return new MuTestProject(root, MuCommandUtil.linux_util);
	}
	protected static void testing(File root) throws Exception {
		MuTestProject project = get_project(root);
		File output_directory = new File(result_dir + project.get_name());
		MuTestProjectCodeFile code_file = project.get_code_space().get_code_files().iterator().next();
		System.out.println("Testing on " + code_file.get_name() + " for writing features.");
		MuTestProjectFeatureWriter.write_features(code_file, output_directory, max_distance);
		System.out.println();
	}
	
}
