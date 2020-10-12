package test;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.ast2mutant.MutationGenerators;
import com.jcsa.jcmutest.mutant.cir2mutant.ptree.CirDetectionLevel;
import com.jcsa.jcmutest.mutant.cir2mutant.ptree.CirMutationTree;
import com.jcsa.jcmutest.mutant.cir2mutant.ptree.CirMutationTreeNode;
import com.jcsa.jcmutest.mutant.cir2mutant.ptree.CirMutationTrees;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.MutaClass;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.MuTestProjectTestResult;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.flwa.context.CirCallContextInstanceGraph;
import com.jcsa.jcparse.flwa.context.CirFunctionCallPathType;
import com.jcsa.jcparse.flwa.dominate.CDominanceGraph;
import com.jcsa.jcparse.flwa.graph.CirInstanceGraph;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.test.cmd.CCompiler;
import com.jcsa.jcparse.test.file.TestInput;
import com.jcsa.jcparse.test.state.CStatePath;

public class CirMutationTreeTest {
	
	private static final String root_path = "/home/dzt2/Development/Data/";
	private static final File sizeof_template_file = new File("config/cruntime.txt");
	private static final File instrument_head_file = new File("config/jcinst.h");
	private static final File preprocess_macro_file = new File("config/linux.h");
	private static final File mutation_head_file = new File("config/jcmutest.h");
	private static final long max_timeout_seconds = 5;
	private static final String result_dir = "result/ctree/";
	
	/* project getters */
	private static String get_name(File cfile) {
		int index = cfile.getName().lastIndexOf('.');
		return cfile.getName().substring(0, index).strip();
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
	private static MuTestProject get_project(File cfile) throws Exception {
		String name = get_name(cfile);
		File root = new File(root_path + "rprojects/" + name);
		if(!root.exists()) {
			MuTestProject project = new MuTestProject(root, MuCommandUtil.linux_util);
			
			/* set configuration data */
			List<String> parameters = new ArrayList<String>();
			parameters.add("-lm");
			project.set_config(CCompiler.clang, ClangStandard.gnu_c89, 
					parameters, sizeof_template_file, instrument_head_file, 
					preprocess_macro_file, mutation_head_file, max_timeout_seconds);
			
			/* input the code files */
			List<File> cfiles = new ArrayList<File>();
			List<File> hfiles = new ArrayList<File>();
			List<File> lfiles = new ArrayList<File>();
			cfiles.add(cfile);
			project.set_cfiles(cfiles, hfiles, lfiles);
			
			/* input the test inputs */
			File test_suite_file = new File(root_path + "tests/" + name + ".txt");
			List<File> test_suite_files = new ArrayList<File>();
			if(test_suite_file.exists()) test_suite_files.add(test_suite_file);
			File inputs_directory = new File(root_path + "vinputs/");
			if(!inputs_directory.exists()) FileOperations.mkdir(inputs_directory);
			project.set_inputs_directory(inputs_directory);
			project.add_test_inputs(test_suite_files);
			
			/* generate mutations */
			project.generate_mutants(get_classes());
			
			return project;
		}
		else {
			return new MuTestProject(root, MuCommandUtil.linux_util);
		}
	}
	private static CirCallContextInstanceGraph translate(CirTree cir_tree) throws Exception {
		CirFunction root_function = cir_tree.get_function_call_graph().get_function("main");
		return CirCallContextInstanceGraph.graph(root_function, 
				CirFunctionCallPathType.unique_path, -1);
	}
	private static CDominanceGraph generate(CirInstanceGraph graph) throws Exception {
		return CDominanceGraph.forward_dominance_graph(graph);
	}
	
	/* mutation output methods */
	/*
	private static String normalize_text(String text) {
		StringBuilder buffer = new StringBuilder();
		for(int k = 0; k < text.length(); k++) {
			char ch = text.charAt(k);
			switch(ch) {
			case '<':	buffer.append("[SMT]"); break;
			case '>':	buffer.append("[GRT]"); break;
			//case '/':	buffer.append("[DIV]");	break;
			//case '%':	buffer.append("[MOD]");	break;
			case '\\':	buffer.append("[NDV]"); break;
			case '\n':	buffer.append(" "); break;
			case '\t':	buffer.append(" "); break;
			case '\r':	buffer.append(" "); break;
			case '\'':	buffer.append("[SRF]"); break;
			case '\"':	buffer.append("[REF]"); break;
			//case '@':	buffer.append("[ATU]"); break;
			//case '#':	buffer.append("[HED]"); break;
			//case '$':	buffer.append("[MON]"); break;
			//case '^':	buffer.append("[XOR]"); break;
			//case '&':	buffer.append("[BAN]"); break;
			//case '*':	buffer.append("[MUL]"); break;
			//case '~':	buffer.append("[RSV]"); break;
			//case '!':	buffer.append("[NOT]"); break;
			default:	buffer.append(ch);	break;
			}
		}
		return buffer.toString();
	}
	private static Element generate_mutation(CirMutation mutation, Iterable<CirDetectionLevel> results) throws Exception {
		Element element = new Element("CirMutation");
		
		CirStatement statement = mutation.get_statement();
		CirConstraint constraint = mutation.get_constraint();
		CirStateError state_error = mutation.get_state_error();
		
		element.setAttribute("STMT", normalize_text(statement.get_tree().get_localizer().get_execution(statement).toString()));
		Element cons_element = new Element("Constraint");
		cons_element.setText(normalize_text(constraint.get_condition().generate_code()));
		element.addContent(cons_element);
		Element error_element = new Element("StateError");
		error_element.setText(normalize_text(state_error.toString()));
		element.addContent(error_element);
		
		Element child = new Element("DetectionResults");
		int index = 0;
		for(CirDetectionLevel result : results) {
			child.setAttribute("R" + (index++) + "", normalize_text(result.toString()));
		}
		element.addContent(child);
		
		return element;
	}
	private static Element generate_tree_node(CirMutationTreeNode node, 
			Map<CirMutationTreeNode, List<CirDetectionLevel>> results) throws Exception {
		Element element = new Element("CirMutationTreeNode");
		if(node.get_flow_type() != null)
			element.setAttribute("FLOW", normalize_text(node.get_flow_type().toString()));
		element.addContent(generate_mutation(node.get_cir_mutation(), results.get(node)));
		for(CirMutationTreeNode child : node.get_children()) {
			element.addContent(generate_tree_node(child, results));
		}
		return element;
	}
	private static Element generate_trees(CirMutationTrees trees, CStatePath path) throws Exception {
		Element trees_element = new Element("CirMutationTrees");
		Map<CirMutationTreeNode, List<CirDetectionLevel>> results = trees.analyze(path);
		for(CirMutationTree tree : trees.get_trees()) {
			Element tree_element = new Element("CirMutationTree");
			tree_element.addContent(generate_tree_node(tree.get_root(), results));
			trees_element.addContent(tree_element);
		}
		return trees_element;
	}
	private static Element generate_mutant(Mutant mutant, CStatePath path, CirTree cir_tree,
			CDominanceGraph dominance_graph) throws Exception {
		CirMutationTrees trees = CirMutationTrees.new_trees(cir_tree, mutant, dominance_graph);
		Element mutant_element = new Element("Mutant");
		AstMutation mutation = mutant.get_mutation();
		
		mutant_element.setAttribute("MID", "" + mutant.get_id());
		mutant_element.setAttribute("CLASS", mutation.get_class() + "::" + mutation.get_operator());
		
		AstNode location = mutation.get_location();
		int line = location.get_location().line_of() + 1;
		String code = location.generate_code();
		code = normalize_text(code.strip()).strip();
		
		mutant_element.setAttribute("LINE", "" + line);
		Element code_element = new Element("CODE");
		code_element.setText(code);
		
		if(mutation.has_parameter())
			mutant_element.setAttribute("PARAM", normalize_text(mutation.get_parameter().toString()));
		
		mutant_element.addContent(generate_trees(trees, path));
		return mutant_element;
	}
	*/
	
	/**
	 * @param text
	 * @return the normalized text without spaces and \n
	 */
	private static String normalize_text(String text) {
		StringBuilder buffer = new StringBuilder();
		for(int k = 0; k < text.length(); k++) {
			char ch = text.charAt(k);
			if(Character.isWhitespace(ch)) {
				ch = ' ';
			}
			buffer.append(ch);
		}
		return buffer.toString();
	}
	/**
	 * put N tabs before line
	 * @param writer
	 * @param tabs
	 * @throws Exception
	 */
	private static void new_line(FileWriter writer, int tabs) throws Exception {
		writer.write("\n");
		for(int k = 0; k < tabs; k++) writer.write("\t");
	}
	/**
	 * @param tree_node
	 * @param writer
	 * @param tabs
	 * @throws Exception
	 */
	private static void output_tree_node(CirMutationTreeNode tree_node, 
			Iterable<CirDetectionLevel> results, FileWriter writer, int tabs) throws Exception {
		new_line(writer, tabs);
		writer.write("[TreeNode]");
		tabs++;
		{
			new_line(writer, tabs);
			writer.write("Statement: ");
			writer.write(normalize_text(tree_node.get_cir_mutation().get_statement().generate_code(true)));
			new_line(writer, tabs);
			writer.write("Constraint: ");
			writer.write(normalize_text(tree_node.get_cir_mutation().get_constraint().toString()));
			new_line(writer, tabs);
			writer.write("StateError: ");
			writer.write(normalize_text(tree_node.get_cir_mutation().get_state_error().toString()));
			new_line(writer, tabs);
			writer.write("Detections: ");
			for(CirDetectionLevel result : results) {
				writer.write(result.toString() + "; ");
			}
		}
		tabs--;
		new_line(writer, tabs);
		writer.write("[TreeNode]");
	}
	/**
	 * @param tree
	 * @param results
	 * @param writer
	 * @param tabs
	 * @throws Exception
	 */
	private static void output_tree(CirMutationTree tree, 
			Map<CirMutationTreeNode, List<CirDetectionLevel>> results, 
			FileWriter writer, int tabs) throws Exception {
		new_line(writer, tabs);
		writer.write("[Tree]");
		
		tabs++;
		{
			Queue<CirMutationTreeNode> queue = new LinkedList<CirMutationTreeNode>();
			queue.add(tree.get_root());
			while(!queue.isEmpty()) {
				CirMutationTreeNode tree_node = queue.poll();
				output_tree_node(tree_node, results.get(tree_node), writer, tabs);
			}
		}
		tabs--;
		
		new_line(writer, tabs);
		writer.write("[Tree]");
	}
	/**
	 * @param mutant
	 * @param writer
	 * @param cir_tree
	 * @param dominance_graph
	 * @throws Exception
	 */
	private static void output_mutant(MuTestProject project, Mutant mutant, CStatePath path, int tid,
			FileWriter writer, CirTree cir_tree, CDominanceGraph dominance_graph) throws Exception {
		/* getters */
		CirMutationTrees trees = CirMutationTrees.new_trees(cir_tree, mutant, dominance_graph);
		Map<CirMutationTreeNode, List<CirDetectionLevel>> results = trees.analyze(path);
		
		int tabs = 0;
		new_line(writer, tabs);
		writer.write("[Mutant]");
		
		//tabs++;
		{
			AstMutation mutation = mutant.get_mutation();
			AstNode location = mutation.get_location();
			int line = location.get_location().line_of() + 1;
			
			new_line(writer, tabs);
			writer.write("Class: ");
			writer.write(mutation.get_class() + "::" + mutation.get_operator());
			
			new_line(writer, tabs);
			writer.write("Line#" + line + ": ");
			String code = normalize_text(location.generate_code());
			if(code.length() > 512) {
				code = code.substring(0, 512);
			}
			writer.write(code);
			
			if(mutation.has_parameter()) {
				new_line(writer, tabs);
				writer.write("Parameter: ");
				writer.write(mutation.get_parameter().toString());
			}
			
			MuTestProjectTestResult tresult = project.get_test_space().get_test_result(mutant);
			new_line(writer, tabs);
			writer.write("Result: ");
			if(tresult == null) {
				writer.write("Not_Executed");
			}
			else if(tresult.get_exec_set().get(tid)){
				writer.write("Executed_");
				if(tresult.get_kill_set().get(tid)) {
					writer.write("Killed");
				}
				else {
					writer.write("Alived");
				}
			}
			else {
				writer.write("Not_Executed");
			}
			
			tabs++;
			for(CirMutationTree tree : trees.get_trees()) {
				output_tree(tree, results, writer, tabs);
			}
			tabs--;
		}
		//tabs--;
		
		new_line(writer, tabs);
		writer.write("[Mutant]");
		new_line(writer, tabs);
	}
	private static void output(MuTestProject project, int test_id) throws Exception {
		/* declarations */
		TestInput test = project.get_test_space().get_test_space().get_input(test_id);
		MuTestProjectCodeFile cfile = project.get_code_space().get_code_files().iterator().next();
		CStatePath path = project.get_test_space().load_instrumental_path(
				cfile.get_sizeof_template(), cfile.get_ast_tree(), cfile.get_cir_tree(), test);
		CDominanceGraph dominance_graph = generate(translate(cfile.get_cir_tree()));
		
		/* output file */
		String output_name;
		if(path == null) {
			output_name = result_dir + project.get_name() + ".txt";
		}
		else {
			output_name = result_dir + project.get_name() + "." + test_id + ".txt";
		}
		FileWriter writer = new FileWriter(new File(output_name));
		writer.write("Test: " + test.get_parameter() + "\n\n");
		
		/* generation */
		for(Mutant mutant : cfile.get_mutant_space().get_mutants()) {
			output_mutant(project, mutant, path, test_id, writer, cfile.get_cir_tree(), dominance_graph);
			System.out.println("\t==> Complete mutant [" + mutant.get_id() + "/" + cfile.get_mutant_space().size() + "]");
		}
		writer.close();
	}
	
	/* testing method */
	private static void testing(String name, int tid) throws Exception {
		File cfile = new File(root_path + "cfiles/" + name + ".c");
		MuTestProject project = get_project(cfile);
		System.out.println("1. Get mutation project for " + project.get_name());
		
		output(project, tid);
		System.out.println("2. Output the mutation information to XML.");
		System.out.println();
	}
	public static void main(String[] args) throws Exception {
		String name = "triangle";
		int tid = 697;
		testing(name, tid);
	}
	
}
