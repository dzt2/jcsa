package test;

import java.io.File;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.MuTestProjectFeatureWriter;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcmutest.project.util.MuCommandUtil;

/**
 * Write xxx.ast xxx.cir ... xxx.sft and xxx.sym
 * @author yukimula
 *
 */
public class MuTestProjectStaticFeatureWritings {
	
	private static final String root_path = "/home/dzt2/Development/Data/rprojects/"; 
	private static final String result_dir = "result/features/";
	private static final int max_distance = 2;
	
	public static void main(String[] args) throws Exception {
		for(File root : new File(root_path).listFiles()) {
			testing(root);
		} 
	}
	
	private static MuTestProject get_project(File root) throws Exception {
		return new MuTestProject(root, MuCommandUtil.linux_util);
	}
	protected static void testing(File root) throws Exception {
		/* 1. open project and get data interface */
		MuTestProject project = get_project(root);
		File output_directory = new File(result_dir + project.get_name());
		FileOperations.mkdir(output_directory);
		MuTestProjectCodeFile code_file = project.get_code_space().get_code_files().iterator().next();
		System.out.println("Testing on " + code_file.get_name() + " for writing features.");
		
		/* 2. write feature information to output directory */
		MuTestProjectFeatureWriter writer = new MuTestProjectFeatureWriter(code_file, output_directory);
		writer.write_s_features(max_distance);
		System.out.println();
	}
	
}
