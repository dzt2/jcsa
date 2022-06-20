package test;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator.DeclaratorProduction;

public class MuTestTrivialEquivalenceTest {
	
	private static final String root_path = "/home/dzt2/Development/Data/projectsAll/";
	private static String result_dir = "results/TCE/";
	private static final String[] optimize_parameters = new String[] { "-O3" };
	private static final int report_size = 512;
	
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
	private static void write_mutant(FileWriter writer, Mutant mutant, long time) throws Exception {
		int mid = mutant.get_id();
		String mclass = mutant.get_mutation().get_class().toString();
		String moperator = mutant.get_mutation().get_operator().toString();
		AstNode location = mutant.get_mutation().get_location();
		int code_line = location.get_location().line_of() + 1;
		String code = strip_code(location.generate_code(), 96);
		String code_class = location.getClass().getSimpleName();
		code_class = code_class.substring(3, code_class.length() - 4);
		AstDeclarator declarator = location.get_function_of().get_declarator();
		while(declarator.get_production() != DeclaratorProduction.identifier) {
			declarator = declarator.get_declarator();
		}
		String func_name = declarator.get_identifier().get_name();
		String parameter = "" + mutant.get_mutation().get_parameter();
		writer.write(mid + "\t" + mclass + "\t" + moperator + "\t");
		writer.write(func_name + "\t" + code_line + "\t" + code_class + "\t\"" + code + "\"" + parameter);
		if(time >= 0) {
			writer.write("\t" + time);
		}
		writer.write("\n");
		return;
	}
	private static void testing(File root) throws Exception {
		MuTestProject project = get_project(root);
		MuTestProjectCodeFile code_file = project.get_code_space().get_code_files().iterator().next(); 
		System.out.println("Testing on " + project.get_name() + " and " + code_file.get_mutant_space().size() + " mutants.");
		
		long begTime = System.currentTimeMillis(), endTime, beg, end, seconds;
		List<Mutant> inputs = new ArrayList<Mutant>(), output;
		Collection<Mutant> equivalent_mutants = new ArrayList<Mutant>();
		File out_file = new File(result_dir + project.get_name() + ".txt");
		int counter = 0, total_mutants = code_file.get_mutant_space().size();
		
		FileWriter writer = new FileWriter(out_file);
		writer.write("ID\tCLAS\tOPRT\tFUNC\tLINE\tASTC\tCODE\tPARM\tTIME\n");
		for(Mutant mutant : code_file.get_mutant_space().get_mutants()) {
			inputs.add(mutant); counter++;
			if(inputs.size() >= report_size) {
				System.out.print("\t==> Compile[" + counter + "/" + total_mutants + "]:");
				beg = System.currentTimeMillis();
				output = project.check_trivial_equivalence(inputs, optimize_parameters);
				inputs.clear();
				end = System.currentTimeMillis();
				seconds = (end - beg) / 1000;
				equivalent_mutants.addAll(output);
				System.out.println("\t" + output.size() + " EQUIV\t" + seconds + " .sec");
				
				for(int k = 0; k < output.size(); k++) {
					if(k < output.size() - 1) {
						write_mutant(writer, output.get(k), -1);
					}
					else {
						write_mutant(writer, output.get(k), seconds);
					}
				}
			}
		}
		if(!inputs.isEmpty()) {
			System.out.print("\t==> Compile[" + counter + "/" + total_mutants + "]:");
			beg = System.currentTimeMillis();
			output = project.check_trivial_equivalence(inputs, optimize_parameters);
			inputs.clear();
			end = System.currentTimeMillis();
			seconds = (end - beg) / 1000;
			equivalent_mutants.addAll(output);
			System.out.println("\t" + output.size() + " EQUIV\t" + seconds + " .sec");
			
			for(int k = 0; k < output.size(); k++) {
				if(k < output.size() - 1) {
					write_mutant(writer, output.get(k), -1);
				}
				else {
					write_mutant(writer, output.get(k), seconds);
				}
			}
		}
		
		double ratio = ((double) (equivalent_mutants.size())) / ((double) (total_mutants));
		ratio = ((int) (ratio * 10000)) / 100.0;
		endTime = System.currentTimeMillis();
		seconds = (endTime - begTime) / 1000;
		writer.write("\nTotal\t" + total_mutants + "\tEquiv\t" + equivalent_mutants.size() + "\tRatio\t" + ratio + "%\tTimes\t" + seconds + "\n");
		writer.close();
		System.out.println("\tTotal\t" + total_mutants + "\tEquiv\t" + equivalent_mutants.size() + "\tRatio\t" + ratio + "%\tTimes\t" + seconds);
		return;
	}
	
	public static void main(String[] args) throws Exception {
		for(File root : new File(root_path).listFiles()) {
			testing(root);
		}
	}

}
