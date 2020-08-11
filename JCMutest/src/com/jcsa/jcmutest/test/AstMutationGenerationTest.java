package com.jcsa.jcmutest.test;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;

import com.jcsa.jcmutest.MutaClass;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.parse.AstMutationGeneration;
import com.jcsa.jcparse.lang.AstCirFile;
import com.jcsa.jcparse.lang.ClangStandard;

public class AstMutationGenerationTest {
	
	protected static final String prefix = 
			"/home/dzt2/Development/DataSet/Code/ifiles/";
	protected static final String postfix = "result/mut/";
	protected static final File template_file = new File("config/cruntime.txt");
	
	public static void main(String[] args) throws Exception {
		for(File cfile : new File(prefix).listFiles()) {
			testing(cfile);
		}
	}
	
	private static void testing(File cfile) throws Exception {
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
		System.out.println("Generate " + mutations.size() + " mutants for " + cfile.getName());
		
		FileWriter writer = new FileWriter(new File(postfix + cfile.getName() + ".m"));
		for(AstMutation mutation : mutations) {
			writer.write(mutation.toString());
			writer.write("\n");
		}
		writer.close();
	}
	
}
