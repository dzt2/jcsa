package test;

import java.io.File;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.sta2mutant.StateMutation;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.StateMutationParsers;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.util.MuCommandUtil;

public class MuStateMutationTest {
	
	private static final String root_path = "/home/dzt2/Development/Data/projects/";
	
	public static void main(String[] args) throws Exception {
		for(File root : new File(root_path).listFiles()) {
			testing(root, false);
		}
	}
	
	private static MuTestProject get_project(File root) throws Exception {
		return new MuTestProject(root, MuCommandUtil.linux_util);
	}
	
	private static void testing(File root, boolean error_print) throws Exception {
		MuTestProject project = get_project(root);
		int succeed = 0, total = 0;
		System.out.println("Testing on: " + project.get_name());
		MuTestProjectCodeFile file = project.get_code_space().get_code_files().iterator().next();
		for(Mutant mutant : file.get_mutant_space().get_mutants()) {
			Iterable<StateMutation> mutations = StateMutationParsers.parse(mutant);
			if(mutations.iterator().hasNext()) {
				succeed++;
			}
			else if(error_print) {
				System.out.println("\tError occurs at " + mutant);
			}
			total++;
		}
		double rate = 100 * ((double) succeed) / (total + 0.0001);
		System.out.println("\tPass-Rate as " + succeed + "/" + total + "\t" + rate + "%\n");
		
	}
	
}
