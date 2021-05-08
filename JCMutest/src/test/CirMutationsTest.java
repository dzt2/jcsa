package test;

import java.io.File;
import java.io.FileWriter;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.sym2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.cond.SymCondition;
import com.jcsa.jcmutest.mutant.sym2mutant.cond.SymConditions;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.lang.astree.AstNode;

public class CirMutationsTest {
	
	private static final String root_path = "/home/dzt2/Development/Data/mprojects/";
	private static final String result_dir = "result/const/";
	
	public static void main(String[] args) throws Exception {
		for(File root : new File(root_path).listFiles()) {
			testing(root);
		}
	}
	
	private static MuTestProject get_project(File root) throws Exception {
		return new MuTestProject(root, MuCommandUtil.linux_util);
	}
	private static String strip_code(String code) {
		StringBuilder buffer = new StringBuilder();
		for(int k = 0; k < code.length(); k++) {
			char ch = code.charAt(k);
			if(Character.isWhitespace(ch)) {
				ch = ' ';
			}
			buffer.append(ch);
		}
		return buffer.toString();
	}
	/**
	 * output the set of optimized constraints being evaluated
	 * @param writer
	 * @param cir_mutations
	 * @param constraint
	 * @throws Exception
	 */
	private static void output_opt_constraints(FileWriter writer, SymConstraint constraint) throws Exception {
		for(SymCondition opt_const : SymConditions.generate(constraint)) {
			writer.write("\t\t==> " + opt_const + "\n");
		}
	}
	/**
	 * output the cir-mutation into the writer
	 * @param writer
	 * @param cir_mutations
	 * @param cir_mutation
	 * @throws Exception
	 */
	private static void output_cir_mutation(FileWriter writer, CirMutation cir_mutation) throws Exception {
		SymConstraint constraint = cir_mutation.get_constraint();
		writer.write("\t\t" + constraint.get_condition() + "\tat \"" + 
				strip_code(constraint.get_statement().generate_code(true)) + "\"\n");
		output_opt_constraints(writer, constraint);
		writer.write("\t\t" + cir_mutation.get_state_error().toString() + "\n");
	}
	private static void output_mutant(FileWriter writer, Mutant mutant) throws Exception {
		writer.write("#Mutant " + mutant.get_id() + "\n");
		writer.write("\tClass: " + mutant.get_mutation().get_class() + 
					":" + mutant.get_mutation().get_operator() + "\n");
		AstNode location = mutant.get_mutation().get_location();
		writer.write("\tLine[" + location.get_location().line_of() + "]: " + strip_code(location.generate_code()) + "\n");
		if(mutant.get_mutation().has_parameter()) {
			writer.write("\tParam: " + mutant.get_mutation().get_parameter() + "\n"); 
		}
		if(mutant.has_cir_mutations()) {
			for(CirMutation cir_mutation : mutant.get_cir_mutations()) {
				output_cir_mutation(writer, cir_mutation);
			}
		}
		writer.write("#EndMutant\n");
	}
	private static void output(MuTestProject project, File output) throws Exception {
		MuTestProjectCodeFile code_file = project.get_code_space().get_code_files().iterator().next();
		FileWriter writer = new FileWriter(output);
		for(Mutant mutant : code_file.get_mutant_space().get_mutants()) {
			output_mutant(writer, mutant);
			writer.write("\n");
		}
		writer.close();
	}
	protected static void testing(File root) throws Exception {
		MuTestProject project = get_project(root);
		System.out.println("Testing on " + root.getName());
		output(project, new File(result_dir + root.getName() + ".txt"));
	}
	
}
