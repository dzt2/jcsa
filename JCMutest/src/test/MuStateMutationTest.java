package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.sta2mutant.StateMutation;
import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.util.MuCommandUtil;

public class MuStateMutationTest {
	
	private static final String root_path = "/home/dzt2/Development/Data/projects/";
	private static final String output_dir = "results/";
	
	public static void main(String[] args) throws Exception {
		for(File root : new File(root_path).listFiles()) {
			testing(root, new File(output_dir + root.getName() + ".txt"));
		}
	}
	
	private static MuTestProject get_project(File root) throws Exception {
		return new MuTestProject(root, MuCommandUtil.linux_util);
	}
	
	private static void testing(File root, File output) throws Exception {
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		
		MuTestProject project = get_project(root);
		int succeed = 0, total = 0; boolean passed;
		MuTestProjectCodeFile file = project.get_code_space().get_code_files().iterator().next();
		
		for(Mutant mutant : file.get_mutant_space().get_mutants()) {
			Iterable<StateMutation> mutations = StateMutations.parse(mutant);
			passed = mutations.iterator().hasNext();
			if(passed) { succeed++; }	total++;
			
			AstMutation mutation = mutant.get_mutation();
			int mid = mutant.get_id();
			String muclass = mutation.get_class().toString();
			String operator = mutation.get_operator().toString();
			int line = mutation.get_location().get_location().line_of();
			String code = mutation.get_location().get_code();
			if(code.length() > 64) { code = code.substring(0, 64) + "..."; }
			code = "\"" + code + "\"";
			writer.write("#" + mid + "\t" + muclass + "\t" + operator + "\t#" + line + "\t" + code + "\t" + mutation.get_parameter());
			writer.write("\n");
			if(passed) {
				for(StateMutation state_mutation : mutations) {
					writer.write("\t@\t" + state_mutation.get_r_execution() + 
							"\t" + state_mutation.get_istate() +
							"\t" + state_mutation.get_pstate() + "\n");
				}
			}
			else {
				writer.write("\tPARSED ERROR!!\n");
			}
		}
		
		writer.close();
		
		double rate = ((double) succeed) / (total + 0.0001);
		rate = ((int) (rate * 1000000)) / 10000.0;
		System.out.println(project.get_name() + ":\t" + succeed + "/" + total + "\t" + rate + "%");
	}
	
}
