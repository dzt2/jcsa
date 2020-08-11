package com.jcsa.jcmutest.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmutest.MutaClass;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcmutest.mutant.parse.AstMutationGeneration;
import com.jcsa.jcparse.lang.AstCirFile;
import com.jcsa.jcparse.lang.ClangStandard;

public class AstMutationGenerationTest {
	
	protected static final String prefix = "/home/dzt2/Development/Data/Code/ifiles/";
	protected static final String postfix = "result/mut/";
	protected static final File template_file = new File("config/cruntime.txt");
	
	public static void main(String[] args) throws Exception {
		for(File cfile : new File(prefix).listFiles()) {
			test_generate(cfile);
			// test_reloading(cfile);
		}
	}
	
	protected static void test_generate(File cfile) throws Exception {
		AstCirFile file = AstCirFile.parse(cfile, template_file, ClangStandard.gnu_c89);
		ArrayList<MutaClass> mutation_classes = new ArrayList<MutaClass>();
		mutation_classes.addAll(AstMutationGeneration.trap_mutation_classes());
		mutation_classes.addAll(AstMutationGeneration.unary_mutation_classes());
		mutation_classes.addAll(AstMutationGeneration.statement_mutation_classes());
		mutation_classes.addAll(AstMutationGeneration.operator_mutation_classes());
		mutation_classes.addAll(AstMutationGeneration.assign_mutation_classes());
		mutation_classes.addAll(AstMutationGeneration.reference_mutation_classes());
		
		Collection<AstMutation> mutations = AstMutationGeneration.
				seed(file.get_ast_tree(), mutation_classes);
		Map<MutaClass, Integer> counters = new HashMap<MutaClass, Integer>();
		
		FileWriter writer = new FileWriter(new File(postfix + cfile.getName() + ".m"));
		for(AstMutation mutation : mutations) {
			writer.write(mutation.toString());
			writer.write("\n");
			
			if(!counters.containsKey(mutation.get_class())) {
				counters.put(mutation.get_class(), 0);
			}
			counters.put(mutation.get_class(), counters.get(mutation.get_class()) + 1);
		}
		writer.close();
		
		System.out.println("Generate " + mutations.size() + " mutants for " + cfile.getName());
		for(MutaClass mclass : counters.keySet()) {
			System.out.println("\t" + mclass + ": " + counters.get(mclass));
		}
		System.out.println();
	}
	
	protected static void test_reloading(File cfile) throws Exception {
		AstCirFile file = AstCirFile.parse(cfile, template_file, ClangStandard.gnu_c89);
		
		File mfile = new File(postfix + cfile.getName() + ".m");
		BufferedReader reader = new BufferedReader(new FileReader(mfile));
		String line; int counter = 0;
		Map<MutaClass, Integer> counters = new HashMap<MutaClass, Integer>();
		while((line = reader.readLine()) != null) {
			if(!line.isBlank()) {
				AstMutation mutation = AstMutations.string2mutation(file.get_ast_tree(), line.strip());
				if(!counters.containsKey(mutation.get_class())) {
					counters.put(mutation.get_class(), 0);
				}
				counters.put(mutation.get_class(), counters.get(mutation.get_class()) + 1);
				counter++;
			}
		}
		reader.close();
		
		System.out.println("Reading " + counter + " mutations from " + cfile.getName());
		for(MutaClass mclass : counters.keySet()) {
			System.out.println("\t" + mclass + ": " + counters.get(mclass));
		}
		System.out.println();
	}
	
}
