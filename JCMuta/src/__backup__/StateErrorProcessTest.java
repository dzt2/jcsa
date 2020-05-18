package __backup__;

import java.io.File;
import java.io.FileWriter;

import com.jcsa.jcmuta.mutant.sem2mutation.SemanticMutationParsers;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.SemanticErrorBuilder;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.SemanticErrorEdge;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.SemanticErrorGraph;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.SemanticErrorNode;
import com.jcsa.jcmuta.project.MutaProject;
import com.jcsa.jcmuta.project.MutaSourceFile;
import com.jcsa.jcmuta.project.Mutant;

public class StateErrorProcessTest {
	
	private static final String prefix = "D:\\SourceCode\\MyData\\CODE3\\projects\\";
	private static final String postfx = "results\\data\\";
	
	public static void main(String[] args) throws Exception {
		for(File file : new File(prefix).listFiles()) {
			testing(file.getName());
		}
	}
	
	/**
	 * open the mutation project in specified directory of name
	 * @param name
	 * @return
	 * @throws Exception
	 */
	private static MutaProject open_project(String name) throws Exception {
		return new MutaProject(new File(prefix + name));
	}
	/**
	 * create a directory for output the mutation information
	 * @param name
	 * @return
	 * @throws Exception
	 */
	private static File get_output_directory(String name) throws Exception {
		File dir = new File(postfx + name);
		if(!dir.exists()) dir.mkdir();
		return dir;
	}
	/**
	 * 
	 * @param project
	 * @param output_dir
	 * @throws Exception
	 */
	private static void output_semantic_mutations(MutaProject project, File output_dir) throws Exception {
		File output = new File(output_dir.getAbsoluteFile() + "/" + project.get_name() + ".mum");
		FileWriter writer = new FileWriter(output);
		
		for(MutaSourceFile source_file : project.get_source_files().get_source_files()) {
			SemanticErrorBuilder.builder.open(source_file.get_cir_tree(), "main");
			for(Mutant mutant : source_file.get_mutant_space().get_mutants()) {
				SemanticMutation mutation;
				try {
					mutation = SemanticMutationParsers.parse(mutant);
				}
				catch(Exception ex) {
					continue;
				}
				
				if(mutation != null) {
					SemanticErrorGraph graph = SemanticErrorBuilder.builder.build(mutation, true, 12, false);
					writer.write("Mutant#" + mutant.get_id() + "\t");
					writer.write(mutant.get_mutation().get_mutation_class() + "\n");
					for(SemanticErrorEdge infection : graph.get_infection_edges()) {
						SemanticErrorNode state_error = infection.get_target();
						if(!state_error.is_empty()) {
							writer.write("\t");
							writer.write(state_error.toString());
							writer.write("\n");
						}
					}
					writer.write("\n");
				}
			}
			SemanticErrorBuilder.builder.close();
		}
		
		writer.close();
	}
	
	private static void testing(String name) throws Exception {
		System.out.println("Testing on " + name);
		
		MutaProject project = open_project(name);
		File output_directory = get_output_directory(name);
		System.out.println("\t1. open mutation test project.");
		
		output_semantic_mutations(project, output_directory);
		System.out.println("\t2. output mutation information.");
		
		System.out.println();
	}
	
}
