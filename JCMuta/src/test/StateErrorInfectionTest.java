package test;

import java.io.File;
import java.io.FileWriter;

import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorBuilder;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorEdge;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorNode;
import com.jcsa.jcmuta.project.MutaProject;
import com.jcsa.jcmuta.project.MutaSourceFile;
import com.jcsa.jcmuta.project.Mutant;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.symb.StateConstraint;
import com.jcsa.jcparse.lopt.context.CirCallContextInstanceGraph;
import com.jcsa.jcparse.lopt.context.CirFunctionCallPathType;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceGraph;
import com.jcsa.jcparse.lopt.models.depend.CDependGraph;
import com.jcsa.jcparse.lopt.models.dominate.CDominanceGraph;

public class StateErrorInfectionTest {
	
	private static int error_counter;
	private static final String prefix = "D:\\SourceCode\\MyData\\CODE3\\projects\\";
	private static final String postfx = "results\\test\\";
	private static final int max_distance = 1;
	// private static final double threshold = 0.01;
	
	public static void main(String[] args) throws Exception {
		for(File file : new File(prefix).listFiles()) {
			System.out.println("Testing on " + file.getName());
			testing(file.getName());
			System.out.println();
		}
	}
	private static void testing(String name) throws Exception {
		MutaProject project = open_project(name);
		File output_directory = get_output_directory(name);
		output_state_error_graph(project, output_directory);
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
	
	private static CirCallContextInstanceGraph translate(CirTree cir_tree) throws Exception {
		CirFunction root_function = cir_tree.get_function_call_graph().get_function("main");
		return CirCallContextInstanceGraph.graph(root_function, 
				CirFunctionCallPathType.unique_path, -1);
	}
	private static CDominanceGraph generate(CirInstanceGraph graph) throws Exception {
		return CDominanceGraph.forward_dominance_graph(graph);
	}
	private static CDependGraph generate2(CirInstanceGraph graph) throws Exception {
		return CDependGraph.graph(graph);
	}
	
	/**
	 * 
	 * @param project
	 * @param output_directory
	 * @throws Exception
	 */
	private static void output_state_error_graph(MutaProject project, File output_directory) throws Exception {
		File output = new File(output_directory.getAbsolutePath() + "/" + project.get_name() + ".err");
		MutaSourceFile source_file = project.get_source_files().get_source_files().iterator().next();
		
		FileWriter writer = new FileWriter(output); error_counter = 0;
		CirInstanceGraph program_graph = translate(source_file.get_cir_tree());
		CDominanceGraph dgraph = generate(program_graph);
		CDependGraph rgraph = generate2(program_graph);
		for(Mutant mutant : source_file.get_mutant_space().get_mutants()) {
			// System.out.println("\t--> Output " + mutant.get_mutation());
			output_ast_mutation(source_file, mutant, dgraph, rgraph, writer);
		}
		writer.close();
		System.out.println("\tOutput " + source_file.get_mutant_space().size() + 
								" mutants with " + error_counter + " errors.");
	}
	/**
	 * 	#MUTANT ID CODE
	 * 		#ERROR error1 error2 ... errorn
	 * @param mutation
	 * @param writer
	 * @throws Exception
	 */
	private static void output_ast_mutation(MutaSourceFile source_file, 
			Mutant mutant, CDominanceGraph dgraph, CDependGraph rgraph, FileWriter writer) throws Exception {
		writer.write("#MUTANT[" + mutant.get_id() + "]:\t" + mutant.get_mutation() + "\n");
		
		StateErrorGraph state_error_graph = null;
		state_error_graph = StateErrorBuilder.build(mutant.get_mutation(), 
				source_file.get_cir_tree(), dgraph, rgraph, max_distance);
		
		if(state_error_graph == null) {
			error_counter++;
		}
		else {
			for(StateErrorNode error_node : state_error_graph.get_nodes()) {
				writer.write("\t#ERROR[" + error_node.get_identifier() + "]:\t");
				for(StateError error : error_node.get_errors()) {
					output_state_error(error, writer);
					writer.write("\t");
				}
				writer.write("\n");
				
				for(StateErrorEdge edge : error_node.get_ou_edges()) {
					writer.write("\t\t==>");
					writer.write("#ERROR[" + edge.get_target().get_identifier() + "] for \t");
					writer.write(edge.get_constraints().is_conjunct() + "{ ");
					for(StateConstraint constraint : edge.get_constraints().get_constraints()) {
						writer.write(constraint.toString() + "; ");
					}
					writer.write("}\n");
				}
				writer.write("\n");
			}
		}
		
		writer.write("\n");
	}
	private static void output_state_error(StateError error, FileWriter writer) throws Exception {
		writer.write(error.get_type().toString());
		writer.write("(");
		for(Object operand : error.get_operands()) {
			if(operand instanceof CirNode) {
				CirNode cir_node = (CirNode) operand;
				if(cir_node.get_ast_source() != null) {
					int line = cir_node.get_ast_source().get_location().line_of();
					writer.write("Code[" + line + "]");
					writer.write("\"" + cir_node.get_ast_source().get_location().trim_code(32) + "\"");
				}
				else {
					writer.write("CIR{" + cir_node.generate_code() + "}"); 
				}
			}
			else {
				writer.write(operand.toString());
			}
			writer.write("; "); 
		}
		writer.write(")");
	}
	
}
