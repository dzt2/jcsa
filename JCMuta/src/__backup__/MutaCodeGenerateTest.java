package __backup__;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import com.jcsa.jcmuta.MutaClass;
import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.ast2mutation.AstMutationGenerators;
import com.jcsa.jcmuta.mutant.code2mutation.MutaCodeGenerators;
import com.jcsa.jcmuta.mutant.code2mutation.MutationCodeType;
import com.jcsa.jcparse.lang.AstCirFile;
import com.jcsa.jcparse.lang.ClangStandard;

public class MutaCodeGenerateTest {
	
	protected static final String prefix = "D:/SourceCode/MyData/CODE2/gfiles/";
	protected static final String postfx = "results/code/";
	private static final String command_template = "clang %s %s -o %s";
	private static final File template_file = new File("config/run_temp.txt");
	
	public static void main(String[] args) throws Exception {
		File[] files = new File(prefix).listFiles();
		Set<MutaClass> operators = all_operators();
		for(File file : files) {
			test_output_code_samples(file, operators);
		}
	}
	
	private static AstCirFile parse(File file) throws Exception {
		return AstCirFile.parse(file, template_file, ClangStandard.gnu_c89);
	}
	private static Set<MutaClass> all_operators() {
		Set<MutaClass> operators = new HashSet<MutaClass>();
		operators.addAll(AstMutationGenerators.trapping_classes);
		operators.addAll(AstMutationGenerators.statement_classes);
		operators.addAll(AstMutationGenerators.unary_classes);
		operators.addAll(AstMutationGenerators.operator_classes);
		operators.addAll(AstMutationGenerators.expression_classes);
		operators.addAll(AstMutationGenerators.semantic_classes);
		operators.add(MutaClass.VBRP);
		operators.add(MutaClass.VCRP);
		operators.remove(MutaClass.VRRP);
		return operators;
	}
	private static void compile(AstMutation mutation, MutationCodeType type) throws Exception {
		File output = new File(postfx + "program.c");
		File cfile = new File(postfx + "jcmulib.c");
		File program = new File(postfx + "a.exe");
		output.delete(); program.delete();
		
		MutaCodeGenerators.generate_code(mutation, type, output);
		Runtime runtime = Runtime.getRuntime();
		String command = String.format(
				command_template, 
				output.getAbsolutePath(), 
				cfile.getAbsolutePath(),
				program.getAbsolutePath());
		Process process = runtime.exec(command);
		process.waitFor();
		
		if(!program.exists())
			throw new RuntimeException("Unable to compile: " + output);
		else
			System.out.println("\t\t==> generate {" + mutation.toString() + "} at [" + type + "]");
	}
	
	private static Collection<AstMutation> mutate(AstCirFile program, MutaClass operator) throws Exception {
		Set<MutaClass> operator_set = new HashSet<MutaClass>();
		operator_set.add(operator);
		return AstMutationGenerators.generate(program.get_ast_tree(), operator_set);
	}
	protected static Collection<AstMutation> select(Collection<AstMutation> mutations, int number) throws Exception {
		Random random = new Random(System.currentTimeMillis());
		List<AstMutation> selected_ones = new ArrayList<AstMutation>();
		if(mutations.size() > 0) {
			while(number-- > 0) {
				int index = random.nextInt() % mutations.size();
				for(AstMutation mutation : mutations) {
					if(index-- <= 0) {
						selected_ones.add(mutation); 
						break;
					}
				}
			}
		}
		return selected_ones;
	}
	protected static AstMutation select_one(Random random, Collection<AstMutation> mutations) throws Exception {
		int index = random.nextInt() % mutations.size();
		while(true) {
			for(AstMutation mutation : mutations) {
				if(index-- <= 0)
					return mutation;
			}
		}
	}
	
	protected static void test_output_code_samples(File file, Set<MutaClass> operators) throws Exception {
		AstCirFile program = parse(file);
		Random random = new Random(System.currentTimeMillis());
		Scanner in = new Scanner(System.in);
		
		String name = program.get_source_file().getName();
		int index = name.lastIndexOf('.');
		name = name.substring(0, index).strip();
		System.out.println("Testing on " + name);
		File program_dir = new File(postfx + name);
		if(!program_dir.exists()) program_dir.mkdir();
		
		for(MutaClass operator : operators) {
			Collection<AstMutation> mutations = mutate(program, operator);
			if(!mutations.isEmpty()) {
				File operator_dir = new File(program_dir.getAbsolutePath() + "/" + operator);
				if(!operator_dir.exists()) {
					operator_dir.mkdir();
				}
				
				File output = new File(operator_dir.getAbsolutePath() + "/output.c");
				int total = 0, coverage_pass = 0, weak_pass = 0, strong_pass = 0;
				for(AstMutation mutation : mutations) {
					total++;
					
					try {
						MutaCodeGenerators.generate_code(mutation, MutationCodeType.Coverage, output);
						coverage_pass++;
					}
					catch(Exception ex) {
						ex.printStackTrace();
						in.nextLine();
					}
					
					try {
						MutaCodeGenerators.generate_code(mutation, MutationCodeType.Weakness, output);
						weak_pass++;
					}
					catch(Exception ex) {
						ex.printStackTrace();
						in.nextLine();
					}
					
					try {
						MutaCodeGenerators.generate_code(mutation, MutationCodeType.Stronger, output);
						strong_pass++;
					}
					catch(Exception ex) {
						ex.printStackTrace();
						in.nextLine();
					}
				}
				
				output.delete(); in.close();
				double coverage_rate = 100 * ((double) coverage_pass) / ((double) total);
				double weak_rate = 100 * ((double) coverage_pass) / ((double) total);
				double strong_rate = 100 * ((double) strong_pass) / ((double) total);
				System.out.println(operator + " �� " + total + "\t" + coverage_pass + " ("
						+ coverage_rate + ")\t" + weak_pass + " (" + weak_rate + ")\t"
						+ strong_pass + "(" + strong_rate + ")");
				
				int counter = 8;
				while(counter > 0) {
					AstMutation mutation = select_one(random, mutations);
					
					try {
						MutaCodeGenerators.generate_code(mutation, MutationCodeType.Coverage, 
								new File(operator_dir.getAbsolutePath() + "/c" + (counter) + ".c"));
						MutaCodeGenerators.generate_code(mutation, MutationCodeType.Weakness, 
								new File(operator_dir.getAbsolutePath() + "/w" + (counter) + ".c"));
						MutaCodeGenerators.generate_code(mutation, MutationCodeType.Stronger, 
								new File(operator_dir.getAbsolutePath() + "/s" + (counter) + ".c"));
						counter--;
					}
					catch(Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}
	
	protected static void testing(File file) throws Exception {
		System.out.println("Testing " + file.getName());
		AstCirFile ast_file = parse(file);
		System.out.println("\t1. parse the source code.");
		
		Set<MutaClass> operators = all_operators();
		Collection<AstMutation> mutations = AstMutationGenerators.
				generate(ast_file.get_ast_tree(), operators);
		System.out.println("\t2. generate " + mutations.size() + " mutations.");
		
		System.out.println("\t3. start to test and compile...");
		for(AstMutation mutation : mutations) {
			compile(mutation, MutationCodeType.Coverage);
			compile(mutation, MutationCodeType.Weakness);
			compile(mutation, MutationCodeType.Stronger);
		}
		
		System.out.println();
	}
	
}
