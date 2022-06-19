package test;

import java.io.File;
import java.io.FileWriter;
import java.util.Collection;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.lang.astree.AstNode;

public class MuTestTrivialEquivalenceTest {
	
	private static final String root_path = "/home/dzt2/Development/Data/projectsAll/";
	private static String result_dir = "results/TCE/";
	
	/* testing functions */
	private static MuTestProject get_project(File root) throws Exception {
		return new MuTestProject(root, MuCommandUtil.linux_util);
	}
	private static String strip_code(String code, int max_length) {
		StringBuilder buffer = new StringBuilder();
		for(int k = 0; k < max_length && k < code.length(); k++) {
			char ch = code.charAt(k);
			if(Character.isWhitespace(ch)) {
				ch = ' ';
			}
			buffer.append(ch);
		}
		if(buffer.length() >= max_length) { buffer.append("..."); }
		return buffer.toString();
	}
	private static void testing(File root) throws Exception {
		MuTestProject project = get_project(root);
		File out_file = new File(result_dir + project.get_name() + ".txt");
		MuTestProjectCodeFile code_file = project.get_code_space().get_code_files().iterator().next(); 
		System.out.println("Testing on " + project.get_name() + " and " + code_file.get_mutant_space().size() + " mutants.");
		
		Collection<Mutant> equivalent_mutants = project.check_trivial_equivalence(code_file, new String[] { "-O3" });
		double ratio = ((double) equivalent_mutants.size()) / ((double) code_file.get_mutant_space().size());
		ratio = ((int) (ratio * 10000)) / 100.0;
		System.out.println("\t==> Equivalent\t" + equivalent_mutants.size() + 
				"/" + code_file.get_mutant_space().size() + "\t(" + ratio + "%)");
		
		FileWriter writer = new FileWriter(out_file);
		writer.write("ID\tClass\tOperator\tLine\tCode\tParameter\n");
		for(Mutant mutant : code_file.get_mutant_space().get_mutants()) {
			if(equivalent_mutants.contains(mutant)) {
				int mid = mutant.get_id();
				String mclass = mutant.get_mutation().get_class().toString();
				String moprt = mutant.get_mutation().get_operator().toString();
				AstNode location = mutant.get_mutation().get_location();
				int line = location.get_location().line_of();
				String code = strip_code(location.generate_code(), 96);
				String parameter = "" + mutant.get_mutation().get_parameter();
				writer.write(mid + "\t" + mclass + "\t" + moprt);
				writer.write("\t" + line + "\t\"" + code + "\"\t" + parameter + "\n");
			}
		}
		writer.close();
	}
	
	public static void main(String[] args) throws Exception {
		for(File root : new File(root_path).listFiles()) {
			testing(root);
		}
	}

}
