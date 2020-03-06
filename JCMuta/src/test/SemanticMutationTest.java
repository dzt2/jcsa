package test;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmuta.MutaClass;
import com.jcsa.jcmuta.mutant.sem2mutation.SemanticMutationParsers;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutation;
import com.jcsa.jcmuta.project.MutaProject;
import com.jcsa.jcmuta.project.MutaSourceFile;
import com.jcsa.jcmuta.project.Mutant;
import com.jcsa.jcparse.lang.astree.AstNode;

public class SemanticMutationTest {
	
	protected static final String postfx = "D:\\SourceCode\\MyData\\CODE3\\projects\\";
	// protected static final String postfx = "D:\\SourceCode\\MyData\\projects\\";
	private static int total_sum, total_pass, total_error, total_empty;
	protected static final String result = "results/sem_errors.txt";
	
	public static void main(String[] args) throws Exception {
		total_sum = 0; total_error = 0; 
		total_pass = 0; total_empty = 0;
		FileWriter writer = new FileWriter(result);
		for(File file : new File(postfx).listFiles()) {
			testing(file.getName(), writer);
		}
		System.out.println(String.format(
				"\nError = %d, Empty = %d, Passes = %d, Sum = %d", 
				total_error, total_empty, total_pass, total_sum));
		writer.close();
	}
	
	private static void inc_counter(Map<MutaClass, Integer> counter, Mutant mutant) throws Exception {
		MutaClass type = mutant.get_mutation().get_mutation_class();
		if(!counter.containsKey(type)) counter.put(type, 0);
		int value = counter.get(type); counter.put(type, value + 1);
	}
	protected static void testing(String name, FileWriter error_writer) throws Exception {
		System.out.println("Testing on " + name);
		
		MutaProject project = new MutaProject(new File(postfx + name));
		System.out.println("\t1. open the test project.");
		
		Set<MutaClass> filter = new HashSet<MutaClass>();
		// filter.add(MutaClass.OEAA); filter.add(MutaClass.OEBA); filter.add(MutaClass.OFLT);
		// filter.add(MutaClass.OAAA); filter.add(MutaClass.OABA); filter.add(MutaClass.OAEA);
		// filter.add(MutaClass.OBAA); filter.add(MutaClass.OBBA); filter.add(MutaClass.OBEA);
		filter.add(MutaClass.OIFI); filter.add(MutaClass.EQAR); filter.add(MutaClass.OSBI);
		filter.add(MutaClass.OIFR); filter.add(MutaClass.ODFI); filter.add(MutaClass.ODFR);
		filter.add(MutaClass.OPDL); int error = 0, empty = 0, pass = 0, sum = 0;
		Map<MutaClass, Integer> error_counter = new HashMap<MutaClass, Integer>();
		Map<MutaClass, Integer> empty_counter = new HashMap<MutaClass, Integer>();
		
		for(MutaSourceFile source_file : project.get_source_files().get_source_files()) {
			System.out.println("\t--> load " + source_file.get_mutant_space().size() + " mutants.");
			for(Mutant mutant : source_file.get_mutant_space().get_mutants()) {
				MutaClass operator_class = mutant.get_mutation().get_mutation_class();
				
				if(!filter.contains(mutant.get_mutation().get_mutation_class())) {
					try {
						SemanticMutation mutation = SemanticMutationParsers.parse(mutant);
						if(mutation == null) { throw new RuntimeException("Fail to see."); }
						else if(mutation.number_of_infections() == 0) {
							empty++; total_empty++;
							inc_counter(empty_counter, mutant); 
							write_mutant(name, error_writer, mutant, "EMPTY");
						}
						else {
							pass++; total_pass++;
						}
					}
					catch(Exception ex) {
						error++; total_error++;
						inc_counter(error_counter, mutant); 
						write_mutant(name, error_writer, mutant, "ERROR");
						
						if(operator_class == MutaClass.STDL) {
							ex.printStackTrace();
						}
					}
				}
				sum++; total_sum++;
			}
		}
		System.out.println(String.format(
				"\t3. error = %d, empty = %d, passes = %d, sum = %d", 
				error, empty, pass, sum));
		System.out.println("\t==> Errors = " + error_counter.toString());
		System.out.println("\t==> Emptys = " + empty_counter.toString());
		
		System.out.println();
	}
	private static void write_mutant(String program, FileWriter writer, Mutant mutant, String type) throws Exception {
		writer.write("#" + type + ": " + program + "\n" + mutant.get_mutation().toString() + "\n");
		AstNode location = mutant.get_mutation().get_location();
		String strip_code = location.get_location().trim_code(32);
		int line = location.get_location().line_of();
		writer.write("At location: \"" + strip_code + "\" In Line #" + line);
		writer.write("\n\n");
	}

}
