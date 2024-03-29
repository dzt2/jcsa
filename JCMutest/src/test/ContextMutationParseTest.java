package test;

import java.io.File;
import java.io.FileWriter;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.MutantSpace;
import com.jcsa.jcmutest.mutant.ctx2mutant.ContextMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstContextState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstSeedMutantState;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.ContextMutationParsers;
import com.jcsa.jcmutest.mutant.ctx2mutant.tree.ContextMutationEdge;
import com.jcsa.jcmutest.mutant.ctx2mutant.tree.ContextMutationNode;
import com.jcsa.jcmutest.mutant.ctx2mutant.tree.ContextMutationTree;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator.DeclaratorProduction;
import com.jcsa.jcparse.lang.program.AstCirNode;

public class ContextMutationParseTest {
	
	private static final String root_path = "/home/dzt2/Development/Data/projects/";
	private static String result_dir = "results/ctxt/";
	
	private static void report_error_rate(int total, int succeed) throws Exception {
		int error = total - succeed;
		double error_rate = ((double) error) / ((double) total);
		error_rate = ((int) (error_rate * 10000)) / 100.0;
		System.out.println("\tSUCC = " + succeed + "\tTOTAL = " + total + "\tERROR = " + error + "\t(" + error_rate + "%)");
	}
	private static MuTestProject get_project(File root) throws Exception {
		return new MuTestProject(root, MuCommandUtil.linux_util);
	}
	private static String strip_code(String code, int max_length) {
		StringBuilder buffer = new StringBuilder();
		for(int k = 0; k < code.length(); k++) {
			char ch = code.charAt(k);
			if(Character.isWhitespace(ch)) {
				ch = ' ';
			}
			buffer.append(ch);
			if(buffer.length() > max_length) { 
				buffer.append("...");
				break; 
			}
		}
		return buffer.toString();
	}
	private static void write_context_state(FileWriter writer, String title, AstContextState state) throws Exception {
		String category = state.get_category().toString();
		String ast_class = state.get_location().get_node_type().toString();
		int line = state.get_location().get_ast_source().get_location().line_of();
		String ast_code = strip_code(state.get_location().get_ast_source().generate_code(), 32);
		String loperand = state.get_loperand().get_simple_code();
		String roperand = state.get_roperand().get_simple_code();
		writer.write("\t" + title + "\t" + category + "\t" + ast_class + "\t" + line + 
				"\t\"" + ast_code + "\"\t(" + loperand + ")\t(" + roperand + ")\n");
	}
	private static void	write_context_mutation(FileWriter writer, Mutant mutant, ContextMutation mutation) throws Exception {
		if(writer == null) {
			throw new IllegalArgumentException("Invalid writer: null");
		}
		else if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant: null");
		}
		else {
			writer.write("[BEG]\n");
			
			AstMutation ast_mutation = mutant.get_mutation();
			int muta_ID = mutant.get_id();
			String muta_class = ast_mutation.get_class().toString();
			String muta_operator = ast_mutation.get_operator().toString();
			Object parameter = ast_mutation.get_parameter();
			writer.write("\tID = " + muta_ID + "\tCLASS = " + muta_class + 
					"\tOPRT = " + muta_operator + "\tPARAM = " + parameter + "\n");
			
			AstNode location = ast_mutation.get_location();
			String ast_class = location.getClass().getSimpleName();
			ast_class = ast_class.substring(3, ast_class.length() - 4);
			int line = location.get_location().line_of();
			AstDeclarator declarator = location.get_function_of().get_declarator();
			while(declarator.get_production() != DeclaratorProduction.identifier) {
				declarator = declarator.get_declarator();
			}
			String func_name = declarator.get_identifier().get_name();
			String code = strip_code(location.generate_code(), 96);
			writer.write("\tFUNC = " + func_name + "\tLINE = " + line + "\tLOCT = " + ast_class + "\n");
			writer.write("\tCODE = \"" + code + "\"\n");
			
			if(mutation != null) {
				writer.write("\tTITLE\tCLASS\tLOCT\tLINE\tCODE\tLOPERAND\tROPERAND\n");
				write_context_state(writer, "[R]", mutation.get_coverage_state());
				write_context_state(writer, "[M]", mutation.get_mutation_state());
				for(int k = 0; k < mutation.number_of_infection_pairs(); k++) {
					write_context_state(writer, "[I." + k + "]", mutation.get_infection_state(k));
					write_context_state(writer, "[P." + k + "]", mutation.get_ini_error_state(k));
				}
			}
			else {
				writer.write("\t\t==> Parsed Failed for Contextual Mutation");
			}
			writer.write("[END]\n\n");
		}
	}
	private static void write_context_mutations(MuTestProject project, File output) throws Exception {
		MuTestProjectCodeFile cfile = project.get_code_space().get_code_files().iterator().next();
		FileWriter writer = new FileWriter(output); int succeed = 0, total = 0;
		for(Mutant mutant : cfile.get_mutant_space().get_mutants()) {
			total++;
			ContextMutation mutation;
			try {
				mutation = ContextMutationParsers.parse(mutant);
				succeed++;
			}
			catch(Exception ex) {
				/*
				String muta_class = mutant.get_mutation().get_class().toString();
				String operator = mutant.get_mutation().get_operator().toString();
				int mutant_ID = mutant.get_id();
				System.out.println("\t==> MUT#" + mutant_ID + "\t" + muta_class + "\t" + operator);
				*/
				mutation = null;
			}
			write_context_mutation(writer, mutant, mutation);
		}
		writer.close();
		report_error_rate(total, succeed);
	}
	private static void write_context_tree_node(FileWriter writer, MutantSpace mspace, ContextMutationNode node) throws Exception {
		writer.write("[BEG]\n");
		writer.write("\tTID\t[" + node.get_node_id() + "]");
		writer.write("\t==> {");
		for(ContextMutationEdge edge : node.get_ou_edges()) {
			writer.write(" [" + edge.get_target().get_node_id() + "]");
		}
		writer.write(" }\n");
		
		AstContextState state = node.get_state();
		if(state instanceof AstSeedMutantState) {
			Mutant mutant = mspace.get_mutant(((AstSeedMutantState) state).get_mutant_ID());
			AstMutation ast_mutation = mutant.get_mutation();
			int muta_ID = mutant.get_id();
			String muta_class = ast_mutation.get_class().toString();
			String muta_operator = ast_mutation.get_operator().toString();
			Object parameter = ast_mutation.get_parameter();
			writer.write("\tID = " + muta_ID + "\tCLASS = " + muta_class + 
					"\tOPRT = " + muta_operator + "\tPARAM = " + parameter + "\n");
			
			AstNode location = ast_mutation.get_location();
			String ast_class = location.getClass().getSimpleName();
			ast_class = ast_class.substring(3, ast_class.length() - 4);
			int line = location.get_location().line_of();
			AstDeclarator declarator = location.get_function_of().get_declarator();
			while(declarator.get_production() != DeclaratorProduction.identifier) {
				declarator = declarator.get_declarator();
			}
			String func_name = declarator.get_identifier().get_name();
			String code = strip_code(location.generate_code(), 96);
			writer.write("\tFUNC = " + func_name + "\tLINE = " + line + "\tLOCT = " + ast_class + "\n");
			writer.write("\tCODE = \"" + code + "\"\n");
		}
		else {
			String state_class = state.get_category().toString();
			AstCirNode ast_node = state.get_location();
			AstNode location = ast_node.get_ast_source();
			String ast_class = location.getClass().getSimpleName();
			ast_class = ast_class.substring(3, ast_class.length() - 4);
			int line = location.get_location().line_of();
			AstDeclarator declarator = location.get_function_of().get_declarator();
			while(declarator.get_production() != DeclaratorProduction.identifier) {
				declarator = declarator.get_declarator();
			}
			String func_name = declarator.get_identifier().get_name();
			String code = strip_code(location.generate_code(), 96);
			
			writer.write("\tCATE = " + state_class + "\tNODE = " + 
							ast_node.get_node_type() + "\tEDGE = " + ast_node.get_child_type() + "\n");
			writer.write("\tFUNC = " + func_name + "\tLINE = " + line + "\tLOCT = " + ast_class + "\n");
			writer.write("\tCODE = \"" + code + "\"\n");
			writer.write("\tLOPR = (" + state.get_loperand().get_simple_code() + ")");
			writer.write("\tROPR = (" + state.get_roperand().get_simple_code() + ")\n");
		}
		
		writer.write("[END]\n\n");
	}
	private static void write_context_tree(MuTestProject project, File output) throws Exception {
		/* 1. declaration and parsing methods */
		MuTestProjectCodeFile cfile = project.get_code_space().get_code_files().iterator().next();
		FileWriter writer = new FileWriter(output); MutantSpace mspace = cfile.get_mutant_space();
		ContextMutationTree tree = ContextMutationTree.parse(cfile.get_ast_file(), mspace.get_mutants());
		
		/* 2. write the tree nodes to the file */
		int succeed = 0, total = mspace.size(), nodes = tree.number_of_tree_nodes();
		for(ContextMutationNode tree_node : tree.get_tree_nodes()) {
			if(tree_node.get_in_degree() == 0) { succeed++; }
			write_context_tree_node(writer, mspace, tree_node);
		}
		writer.close();
		System.out.println("\tSUCC = " + succeed + "\tTOTAL = " + total + 
				"\tNODES = " + nodes + "\t(" + (100 * nodes / succeed) + "%)");
	}
	protected static void testing(File root) throws Exception {
		MuTestProject project = get_project(root);
		System.out.println("Testing on " + project.get_name() + " for contextual mutations.");
		write_context_mutations(project, new File(result_dir + root.getName() + ".txt"));
		write_context_tree(project, new File(result_dir + root.getName() + ".tre"));
	}
	
	public static void main(String[] args) throws Exception {
		String filter_project = "vvv";
		for(File root : new File(root_path).listFiles()) {
			if(root.getName().equals(filter_project)) {
				continue;
			}
			else {
				testing(root);
			}
		}
	}
	
}
