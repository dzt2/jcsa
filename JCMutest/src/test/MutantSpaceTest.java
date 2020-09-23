package test;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.MutantSpace;
import com.jcsa.jcmutest.mutant.ast2mutant.MutationGenerators;
import com.jcsa.jcmutest.mutant.mutation.MutaClass;
import com.jcsa.jcmutest.mutant.txt2mutant.MutaCodeGeneration;
import com.jcsa.jcmutest.mutant.txt2mutant.MutationTestType;
import com.jcsa.jcparse.lang.AstCirFile;
import com.jcsa.jcparse.lang.ClangStandard;

public class MutantSpaceTest {
	
	protected static final String prefix = "/home/dzt2/Development/Data/ifiles/";
	protected static final String postfix = "result/mut/";
	protected static final File template_file = new File("config/cruntime.txt");
	private static final Random random = new Random(System.currentTimeMillis());
	
	public static void main(String[] args) throws Exception {
		for(File cfile : new File(prefix).listFiles()) {
			if(cfile.getName().endsWith(".c")) {
				testing(cfile);
			}
		}
	}
	
	protected static void testing(File cfile) throws Exception {
		System.out.println("Testing on " + cfile.getName());
		AstCirFile program = parse(cfile);
		MutantSpace space = new_space(program);
		save_space(space, cfile);
		load_space(space, cfile);
		print_space(space);
		String name = cfile.getName();
		name = name.substring(0, name.length() - 2);
		generate_mfiles(space, new File(postfix + name));
		System.out.println();
	}
	private static AstCirFile parse(File cfile) throws Exception {
		System.out.println("\t1. Parse program from " + cfile.getName());
		return AstCirFile.parse(cfile, template_file, ClangStandard.gnu_c89);
	}
	private static Iterable<MutaClass> get_classes() {
		Set<MutaClass> classes = new HashSet<MutaClass>();
		classes.addAll(MutationGenerators.trapping_classes());
		classes.addAll(MutationGenerators.unary_classes());
		classes.addAll(MutationGenerators.statement_classes());
		classes.addAll(MutationGenerators.operator_classes());
		classes.addAll(MutationGenerators.assign_classes());
		classes.addAll(MutationGenerators.reference_classes());
		return classes;
	}
	private static MutantSpace new_space(AstCirFile program) throws Exception {
		MutantSpace space = new MutantSpace(program.get_ast_tree(), program.get_cir_tree());
		space.update(get_classes());
		System.out.println("\t2. Generate " + space.size() + " mutants.");
		return space;
	}
	private static void save_space(MutantSpace space, File cfile) throws Exception {
		File mfile = new File(postfix + cfile.getName() + ".m");
		space.save(mfile);
		System.out.println("\t3. Save " + space.size() + " mutants to " + mfile.getAbsolutePath());
	}
	private static void load_space(MutantSpace space, File cfile) throws Exception {
		File mfile = new File(postfix + cfile.getName() + ".m");
		space.load(mfile);
		System.out.println("\t3. Load " + space.size() + " mutants in " + mfile.getAbsolutePath());
	}
	private static void print_space(MutantSpace space) throws Exception {
		Map<MutaClass, Integer> counter = new HashMap<MutaClass, Integer>();
		for(Mutant mutant : space.get_mutants()) {
			MutaClass mclass = mutant.get_mutation().get_class();
			if(!counter.containsKey(mclass)) {
				counter.put(mclass, 0);
			}
			counter.put(mclass, counter.get(mclass) + 1);
		}
		for(MutaClass mclass : counter.keySet()) {
			System.out.println("\t\t" + mclass + ": " + counter.get(mclass));
		}
	}
	private static void generate_mfiles(MutantSpace space, File dir) throws Exception {
		if(!dir.exists())
			dir.mkdir();
		for(int k = 0; k < 16; k++) {
			int mid = Math.abs(random.nextInt()) % space.size();
			Mutant mutant = space.get_mutant(mid);
			File ofile = new File(dir.getAbsolutePath() + "/" + mid + ".o.c");
			File cfile = new File(dir.getAbsolutePath() + "/" + mid + ".c.c");
			File wfile = new File(dir.getAbsolutePath() + "/" + mid + ".w.c");
			File sfile = new File(dir.getAbsolutePath() + "/" + mid + ".s.c");
			MutaCodeGeneration.generate(mutant, MutationTestType.original, 	ofile);
			MutaCodeGeneration.generate(mutant, MutationTestType.coverage, 	cfile);
			MutaCodeGeneration.generate(mutant, MutationTestType.weak, 		wfile);
			MutaCodeGeneration.generate(mutant, MutationTestType.strong, 	sfile);
		}
		System.out.println("\t4. Generate mutation code files.");
	}
	
}
