package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collection;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirConditionState;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;

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
	
	private static String normalize_code(String code, int max_length) {
		StringBuilder buffer = new StringBuilder();
		for(int k = 0; k < code.length(); k++) {
			char ch = code.charAt(k);
			if(ch == Character.LINE_SEPARATOR
				|| ch == '\n') {
				ch = ' ';
			}
			buffer.append(ch);
			if(buffer.length() > max_length) {
				buffer.append("...");
				break;
			}
		}
		return buffer.toString();
	}
	
	protected static void testing(File root, File output) throws Exception {
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		
		MuTestProject project = get_project(root);
		int succeed = 0, total = 0; Boolean passed;
		MuTestProjectCodeFile file = project.get_code_space().get_code_files().iterator().next();
		
		for(Mutant mutant : file.get_mutant_space().get_mutants()) {
			/* mutation --> state-mutations parsing */
			Collection<CirMutation> mutations = CirMutations.parse(mutant);
			passed = !mutations.isEmpty();
			if(passed) { succeed++; }	total++;
			
			/* syntactic mutation head */
			AstMutation mutation = mutant.get_mutation();
			int mid = mutant.get_id();
			String muclass = mutation.get_class().toString();
			String operator = mutation.get_operator().toString();
			String parameter = "" + mutation.get_parameter();
			int line = mutation.get_location().get_location().line_of();
			String code = mutation.get_location().get_code();
			writer.write("");
			writer.write(String.format("MUT[%d]\t%s\t%s\t%s\n", mid, muclass, operator, parameter));
			writer.write(String.format("LIN[%d]\t\"%s\"\n", line, normalize_code(code, 96)));
			writer.write(String.format("PARSE\t(%s)\t%d mutations\n", passed.toString(), mutations.size()));
			
			/* state mutation information */
			int index = 0;
			for(CirMutation cir_mutation : mutations) {
				CirExecution execution = cir_mutation.get_execution();
				CirConditionState constraint = cir_mutation.get_i_state();
				CirAbstErrorState init_error = cir_mutation.get_p_state();
				
				writer.write(String.format("\t%d[R]\t%s\t%s\t\"%s\"\n", index, execution.toString(),
						execution.get_statement().getClass().getSimpleName(),
						normalize_code(execution.get_statement().generate_code(true), 96)));
				writer.write(String.format("\t%d[I]\t%s\t%s\t[%s]\n", index, constraint.get_execution().toString(),
						constraint.getClass().getSimpleName(), constraint.get_roperand().get_simple_code()));
				writer.write(String.format("\t%d[P]\t%s\t%s\t\"%s\"\t[%s]\n", index, init_error.get_execution().toString(),
						init_error.getClass().getSimpleName(),
						normalize_code(init_error.get_location().generate_code(true), 96),
						init_error.get_roperand().get_simple_code()));
				index++;
			}
			writer.write("\n");
		}
		
		writer.close();
		
		double rate = ((double) succeed) / (total + 0.00);
		rate = ((int) (rate * 1000000)) / 10000.0;
		System.out.println(project.get_name() + ":\t" + succeed + "/" + total + "\t" + rate + "%");
	}
	
}
