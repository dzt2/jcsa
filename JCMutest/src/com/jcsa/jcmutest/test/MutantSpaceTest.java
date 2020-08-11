package com.jcsa.jcmutest.test;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.jcsa.jcmutest.MutaClass;
import com.jcsa.jcmutest.mutant.MutantSpace;
import com.jcsa.jcmutest.mutant.parse.Ast2Mutation;
import com.jcsa.jcparse.lang.AstCirFile;
import com.jcsa.jcparse.lang.ClangStandard;

public class MutantSpaceTest {
	
	protected static final String prefix = "/home/dzt2/Development/Data/Code/ifiles/";
	protected static final String postfix = "result/mut/";
	protected static final File template_file = new File("config/cruntime.txt");
	
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
		System.out.println();
	}
	private static AstCirFile parse(File cfile) throws Exception {
		System.out.println("\t1. Parse program from " + cfile.getName());
		return AstCirFile.parse(cfile, template_file, ClangStandard.gnu_c89);
	}
	private static Iterable<MutaClass> get_classes() {
		Set<MutaClass> classes = new HashSet<MutaClass>();
		classes.addAll(Ast2Mutation.trap_mutation_classes());
		classes.addAll(Ast2Mutation.unary_mutation_classes());
		classes.addAll(Ast2Mutation.statement_mutation_classes());
		classes.addAll(Ast2Mutation.operator_mutation_classes());
		classes.addAll(Ast2Mutation.assign_mutation_classes());
		classes.addAll(Ast2Mutation.reference_mutation_classes());
		return classes;
	}
	private static MutantSpace new_space(AstCirFile program) throws Exception {
		MutantSpace space = new MutantSpace(program);
		space.generate(get_classes());
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
	
	
}
