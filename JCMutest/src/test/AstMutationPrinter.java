package test;

import java.io.File;
import java.io.FileWriter;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.MuTestProjectTestResult;
import com.jcsa.jcmutest.project.MuTestProjectTestSpace;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;

public class AstMutationPrinter {
	
	private static final String root_path = "/home/dzt2/Development/Data/projects/";
	private static String result_dir = "results/mutants/";

	public static void main(String[] args) throws Exception {
		for(File root : new File(root_path).listFiles()) {
			File output = new File(result_dir + root.getName() + ".mut");
			testing(root, output);
		}
	}
	
	/**
	 * It simply opens the mutation test project from root directory
	 * @param root
	 * @return
	 * @throws Exception
	 */
	private static MuTestProject get_project(File root) throws Exception {
		return new MuTestProject(root, MuCommandUtil.linux_util);
	}
	/**
	 * It removes the \n, \t
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private static String strip_code(AstNode location, int max_length) throws Exception {
		StringBuilder buffer = new StringBuilder();
		String code = location.generate_code();
		for(int k = 0; k < code.length() && buffer.length() <= max_length; k++) {
			char ch = code.charAt(k);
			if(Character.isWhitespace(ch)) {
				ch = ' ';
			}
			buffer.append(ch);
		}
		if(buffer.length() > max_length) {
			buffer.append("...");
		}
		return buffer.toString();
	}
	/**
	 * MID CLAS OPRT PARM LINE LOCT PART STMT
	 * @param writer
	 * @param mutant
	 * @throws Exception
	 */
	private static void write_mutation(MuTestProject project, 
			FileWriter writer, Mutant mutant, int max_length) throws Exception {
		MuTestProjectTestSpace tspace = project.get_test_space();
		MuTestProjectTestResult result = tspace.get_test_result(mutant);
		if(result != null && result.get_kill_set().degree() <= 0) {
			int mid = mutant.get_id();
			AstMutation mutation = mutant.get_mutation();
			String mu_clas = mutation.get_class().toString();
			String mu_oprt = mutation.get_operator().toString();
			Object parameter = mutation.get_parameter(); String param;
			if(parameter == null) {
				param = "None";
			}
			else if(parameter instanceof AstNode) {
				param = "ast@" + ((AstNode) parameter).get_key();
			}
			else {
				param = parameter.toString();
			}
			
			AstNode location = mutation.get_location();
			int line = location.get_location().line_of();
			String loct = strip_code(location, max_length);
			String parent, statement;
			if(location instanceof AstStatement && !(location.get_parent() instanceof AstForStatement)) {
				parent = "None";
				statement = loct;
			}
			else {
				location = location.get_parent();
				parent = strip_code(location, max_length);
				while(!(location instanceof AstStatement)) {
					location = location.get_parent();
				}
				statement = strip_code(location, max_length);
			}
			
			writer.write(mid + "\t" + mu_clas + "\t" + mu_oprt + "\t" + param + "\t");
			writer.write(line + "\t" + loct + "\t" + parent + "\t" + statement);
			writer.write("\n");
		}
	}
	/**
	 * It writes all undetected mutants to specified output file
	 * @param root
	 * @throws Exception
	 */
	private static void testing(File root, File output) throws Exception {
		System.out.println("Testing on " + root.getName());
		MuTestProject project = get_project(root);
		FileWriter writer = new FileWriter(output);
		MuTestProjectCodeFile cfile = project.get_code_space().get_code_files().iterator().next();
		writer.write("MID\tCLAS\tOPRT\tPARM\tLINE\tCODE\tPARENT\tSTATEMENT\n");
		for(Mutant mutant : cfile.get_mutant_space().get_mutants()) {
			write_mutation(project, writer, mutant, 96);
		}
		writer.close();
	}
	
}
