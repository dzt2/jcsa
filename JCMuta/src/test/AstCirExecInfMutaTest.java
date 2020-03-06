package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.jcsa.jcparse.lang.AstFile;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.astree.base.AstIdentifier;
import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.AstTypeName;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.base.BitSequence;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirConstExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirField;
import com.jcsa.jcparse.lang.irlang.expr.CirNameExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowGraph;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCallGraph;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabel;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.text.CLocation;
import com.jcsa.jcparse.lopt.context.CirCallContextInstanceGraph;
import com.jcsa.jcparse.lopt.context.CirFunctionCallPathType;
import com.jcsa.jcparse.lopt.context.CirFunctionCallTreeNode;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceGraph;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceNode;

import __backup__.CWord;
import __backup__.CirInfluenceEdge;
import __backup__.CirInfluenceGraph;
import __backup__.CirInfluenceNode;
import __backup__.CirSemanticLink;
import __backup__.CirSemanticMutation;
import __backup__.CirSemanticNode;
import __backup__.CodeMutationType;
import __backup__.CompileRecord;
import __backup__.JCMT_Builder;
import __backup__.JCMT_Project;
import __backup__.MutScore;
import __backup__.MutScoreClusters;
import __backup__.MutTestDomains;
import __backup__.Mutant;
import __backup__.MutantSpace;
import __backup__.Mutation2CirSemantic;
import __backup__.MutationMode;
import __backup__.TestOracleManager;
import __backup__.TextMutation;

/**
 * Used to generate the following files:
 * .ast	==> abstract syntactic tree							AstTree
 * .cir	==> C-like intermediate representation tree			CirTree
 * .exc	==> the execution flow graph of each tree node		
 * 
 * @author yukimula
 *
 */
public class AstCirExecInfMutaTest {
	
	/* arguments */
	protected static final String prefix = "D:\\SourceCode\\MyData\\CODE2\\";
	protected static final String cfidir = prefix + "ifiles/";
	protected static final String neqdir = prefix + "nequiv/";
	protected static final String eqvdir = prefix + "equivs/";
	protected static final String prodir = prefix + "TestProjects/";
	protected static final String postfx = "results/inputs/";
	private static final double threshold = 0.50;
	
	public static void main(String[] args) throws Exception {
		File[] files = new File(prodir).listFiles();
		for(File file : files) {
			String program = file.getName(); 
			testing(program);
		}
	}
	
	/* data generation methods */
	/**
	 * get the [project_root; code_file; nequiv_file; ]
	 * @param name
	 * @return
	 * @throws Exception
	 */
	private static File[] get_files_of(String name) throws Exception {
		File root = new File(prodir + name);
		File cfile = new File(cfidir + name + ".c");
		File efile = new File(eqvdir + name + ".txt");
		File nfile = new File(neqdir + name + ".txt");
		return new File[] {root, cfile, efile, nfile};
	}
	/**
	 * Open an existing test project from file
	 * @param name
	 * @return
	 * @throws Exception
	 */
	private static JCMT_Project get_project(File root, File cfile) throws Exception {
		JCMT_Project project = JCMT_Builder.open(root);
		JCMT_Builder.set_muta_cursor(project, cfile); return project;
	}
	/**
	 * collect the id of non-equivalent but unkilled mutants
	 * @param nequiv
	 * @return
	 * @throws Exception
	 */
	private static Collection<Integer> get_non_equivalence(File nequiv) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(nequiv));
		String line; Collection<Integer> nequivs = new ArrayList<Integer>();
		while((line = reader.readLine()) != null) {
			line = line.trim();
			if(!line.isEmpty()) nequivs.add(Integer.parseInt(line));
		}
		reader.close(); return nequivs;
	}
	/**
	 * whether the unkilled mutant is equivalent
	 * @param mid
	 * @param nequivs
	 * @return
	 */
	private static boolean is_equivalent(MutScore score, 
			Collection<Integer> equivs, Collection<Integer> nequiv) {
		if(score.get_score_degree() == 0)
			return !nequiv.contains(score.get_mutant());
		else return equivs.contains(score.get_mutant());
	}
	/**
	 * get the set of coverage, infection and killing scores of mutants 
	 * @param project
	 * @param cfile
	 * @return
	 * @throws Exception
	 */
	private static Map<Mutant, MutScore[]> get_scores(
			JCMT_Project project, File cfile) throws Exception {
		if(project == null || !project.get_code_manager().is_cursor_openned())
			throw new IllegalArgumentException("no mutant and code are specified");
		else if(cfile == null)
			throw new IllegalArgumentException("invalid cfile: null");
		else {
			/* declarations */
			Map<Mutant, MutScore[]> scores = new HashMap<Mutant, MutScore[]>();
			List<MutScore> covers = get_scores_of(project, cfile, CodeMutationType.coverage);
			List<MutScore> infect = get_scores_of(project, cfile, CodeMutationType.weakness);
			List<MutScore> strong = get_scores_of(project, cfile, CodeMutationType.stronger);
			MutantSpace mspace = project.get_code_manager().get_mutant_space();
			
			/* collect coverage scores */
			for(MutScore score : covers) {
				Mutant mutant = mspace.get(score.get_mutant());
				if(!scores.containsKey(mutant)) {
					MutScore[] ans = new MutScore[3];
					for(int k = 0; k < ans.length; k++)
						ans[k] = null;
					scores.put(mutant, ans);
				}
				MutScore[] ans = scores.get(mutant);
				ans[0] = score;
			}
			/* collect weak mutation scores */
			for(MutScore score : infect) {
				Mutant mutant = mspace.get(score.get_mutant());
				if(scores.containsKey(mutant)) {
					MutScore[] ans = scores.get(mutant);
					ans[1] = score;
				}
			}
			/* collect strong mutation scores */
			int unkilled = 0;
			for(MutScore score : strong) {
				Mutant mutant = mspace.get(score.get_mutant());
				if(scores.containsKey(mutant)) {
					MutScore[] ans = scores.get(mutant);
					ans[2] = score;
					if(score.get_score_degree() == 0) unkilled++;
				}
			}
			System.out.println("\t\t--> Unkilled mutants: " + unkilled);
			/* return */	return scores;
		}
	}
	/**
	 * get the scores of all mutants in given option
	 * @param project
	 * @param cfile
	 * @param option
	 * @return
	 * @throws Exception
	 */
	private static List<MutScore> get_scores_of(JCMT_Project project, 
			File cfile, CodeMutationType option) throws Exception {
		if(project == null || !project.get_code_manager().is_cursor_openned())
			throw new IllegalArgumentException("no mutant and code are specified");
		else if(cfile == null)
			throw new IllegalArgumentException("invalid cfile: null");
		else if(option == null)
			throw new IllegalArgumentException("invalid cfile: null");
		else {
			MutantSpace mspace = project.get_code_manager().get_mutant_space();
			TestOracleManager oracle = project.get_oracle_manager(cfile, option);
			List<MutScore> buffer = new ArrayList<MutScore>();
			Collection<Mutant> mutants = mspace.get_all();
			for(Mutant mutant : mutants) {
				if(is_syntax_correct(oracle, mutant)) ;
					buffer.add(oracle.produce_score(mutant)); 
			}
			oracle.load_scores(buffer); return buffer;
		}
	}
	/**
	 * determine whether the mutant is syntactically correct.
	 * @param oracle
	 * @param mutant
	 * @return
	 * @throws Exception
	 */
	private static boolean is_syntax_correct(TestOracleManager oracle, Mutant mutant) throws Exception {
		if(oracle.has_record_of(mutant)) {
			CompileRecord record = oracle.get_record(mutant);
			return record.get_tag();
		}
		else return false;
	}
	private static Map<Mutant, MutScore> filtering(Map<Mutant, MutScore[]> scores) throws Exception {
		Map<Mutant, MutScore> mscores = new HashMap<Mutant, MutScore>();
		for(Mutant mutant : scores.keySet()) {
			mscores.put(mutant, scores.get(mutant)[2]);
		}
		return mscores;
	}
	private static MutScoreClusters clustering(Map<Mutant, MutScore> scores) throws Exception {
		return MutScoreClusters.clustering(scores);
	}
	protected static MutTestDomains stmt_domains(MutScoreClusters cluster_set) throws Exception {
		return MutTestDomains.get_statement_domains(cluster_set);
	}
	protected static MutTestDomains brch_domains(MutScoreClusters cluster_set) throws Exception {
		return MutTestDomains.get_branching_domains(cluster_set);
	}
	protected static MutTestDomains mcdc_domains(MutScoreClusters cluster_set) throws Exception {
		return MutTestDomains.get_condition_domains(cluster_set);
	}
	private static Map<Mutant, Object[]> classifier(
			Map<Mutant, MutScore> scores, MutTestDomains domains,
			Collection<Integer> nequiv, Collection<Integer> equivs) throws Exception {
		Map<Mutant, Object[]> classifier = new HashMap<Mutant, Object[]>();
		
		for(Mutant mutant : scores.keySet()) {
			int cluster = -1;
			if(domains.get_cluster_set().in_cluster(mutant)) {
				cluster = domains.get_cluster_set().get_cluster(mutant).get_id();
			}
			MutScore score = scores.get(mutant);
			if(is_equivalent(score, equivs, nequiv)) {
				classifier.put(mutant, new Object[] {'E', cluster, score});
			}
			else if(domains.detection_probability(score, domains.get_min_domains()) >= threshold) {
				classifier.put(mutant, new Object[] {'T', cluster, score});
			}
			else {
				classifier.put(mutant, new Object[] {'S', cluster, score});
			}
		}
		
		return classifier;
	}
	private static CirCallContextInstanceGraph translate(CirTree cir_tree) throws Exception {
		CirFunction root_function = cir_tree.get_function_call_graph().get_function("main");
		return CirCallContextInstanceGraph.graph(root_function, 
				CirFunctionCallPathType.unique_path, -1);
	}
	private static CirInfluenceGraph generate(CirInstanceGraph program_graph) throws Exception {
		return CirInfluenceGraph.graph(program_graph);
	}
	
	/* CODE & AST output */
	/**
	 * .c file to be preserved
	 * @param ast_tree
	 * @param output
	 * @throws Exception
	 */
	private static void output_code(AstTree ast_tree, File output) throws Exception {
		FileInputStream in = new FileInputStream(ast_tree.get_source_file());
		FileOutputStream ou = new FileOutputStream(output);
		byte[] buffer = new byte[1024 * 1024]; int length;
		while((length = in.read(buffer)) >= 0) { 
			ou.write(buffer, 0, length);
		}
		in.close(); ou.close();
	}
	private static final StringBuilder ast_typename = new StringBuilder();
	private static String get_ast_typename(AstNode ast_node) throws Exception {
		String typename = ast_node.getClass().getSimpleName();
		typename = typename.substring(3, typename.length() - 4).strip();
		
		ast_typename.setLength(0);
		for(int k = 0; k < typename.length(); k++) {
			char ch = typename.charAt(k);
			if(Character.isUpperCase(ch)) {
				ch = Character.toLowerCase(ch);
				if(k != 0) 
					ast_typename.append('_');
			}
			ast_typename.append(ch);
		}
		return ast_typename.toString();
	}
	private static final StringBuilder ast_content = new StringBuilder();
	private static String get_ast_content(AstNode ast_node) throws Exception {
		ast_content.setLength(0);
		
		if(ast_node instanceof AstIdentifier) {
			ast_content.append(((AstIdentifier) ast_node).get_name());
		}
		else if(ast_node instanceof AstKeyword) {
			ast_content.append(((AstKeyword) ast_node).get_keyword().toString());
		}
		else if(ast_node instanceof AstPunctuator) {
			ast_content.append(((AstPunctuator) ast_node).get_punctuator().toString());
		}
		else if(ast_node instanceof AstConstant) {
			CConstant constant = ((AstConstant) ast_node).get_constant();
			Object value;
			switch(constant.get_type().get_tag()) {
			case c_bool:
				value = constant.get_bool(); break;
			case c_char: case c_uchar:
				value = (int) constant.get_char().charValue(); break;
			case c_short: case c_ushort: case c_int: case c_uint:
				value = constant.get_integer(); break;
			case c_long: case c_ulong: case c_llong: case c_ullong:
				value = constant.get_long(); break;
			case c_float:
				value = constant.get_float(); break;
			case c_double:
			case c_ldouble:
				value = constant.get_double(); break;
			default: throw new IllegalArgumentException("Invalid type");
			}
			ast_content.append(value.toString());
		}
		
		return ast_content.toString();
	}
	/**
	 * ID beg end ast_name data_type? content? child_id child_id ... child_id
	 * @param ast_tree
	 * @param output
	 * @throws Exception
	 */
	private static void output_ast(AstTree ast_tree, File output) throws Exception {
		Queue<AstNode> ast_queue = new LinkedList<AstNode>();
		FileWriter writer = new FileWriter(output);
		writer.write("ID\tBEG\tEND\tAST\tType\tContent\tChildren\n");
		
		ast_queue.add(ast_tree.get_ast_root());
		while(!ast_queue.isEmpty()) {
			AstNode ast_node = ast_queue.poll();
			CLocation location = ast_node.get_location();
			
			/** ID beg end ast_name **/
			writer.write(ast_node.get_key() + "\t");
			writer.write(location.get_bias() + "\t");
			writer.write((location.get_bias() + location.get_length()) + "\t");
			writer.write(get_ast_typename(ast_node) + "\t");
			
			/** data_type? **/
			CType data_type = null;
			if(ast_node instanceof AstExpression) {
				data_type = ((AstExpression) ast_node).get_value_type();
			}
			else if(ast_node instanceof AstTypeName) {
				data_type = ((AstTypeName) ast_node).get_type();
			}
			if(data_type != null) {
				CWord tword = CWord.word(data_type);
				writer.write(tword.toString() + "\t");
			}
			else writer.write(" \t");
			
			/** content? **/
			writer.write(get_ast_content(ast_node) + " \t");
			
			/** children_ID **/
			for(int k = 0; k < ast_node.number_of_children(); k++) {
				AstNode child = ast_node.get_child(k);
				if(child != null) {
					ast_queue.add(child);
					writer.write(child.get_key() + "\t");
				}
			}
			
			/** next_line **/
			writer.write("\n");
		}
		
		writer.close();
	}
	
	/* CIR output methods */
	private static final Map<CirNode, Integer> cir_index = new HashMap<CirNode, Integer>();
	private static Map<CirNode, Integer> get_cir_index(CirTree cir_tree) throws Exception {
		Queue<CirNode> cir_queue = new LinkedList<CirNode>();
		cir_queue.add(cir_tree.get_root()); int index = 0;
		
		cir_index.clear();
		while(!cir_queue.isEmpty()) {
			CirNode cir_node = cir_queue.poll();
			cir_index.put(cir_node, index++);
			for(CirNode child : cir_node.get_children()) {
				if(child != null) cir_queue.add(child);
			}
		}
		return cir_index;
	}
	private static final StringBuilder cir_typename = new StringBuilder();
	private static String get_cir_typename(CirNode cir_node) throws Exception {
		String typename = cir_node.getClass().getSimpleName();
		typename = typename.substring(3, typename.length() - 4).strip();
		
		cir_typename.setLength(0);
		for(int k = 0; k < typename.length(); k++) {
			char ch = typename.charAt(k);
			if(Character.isUpperCase(ch)) {
				ch = Character.toLowerCase(ch);
				if(k != 0) 
					cir_typename.append('_');
			}
			cir_typename.append(ch);
		}
		return cir_typename.toString();
	}
	private static String get_cir_content(CirNode cir_node) throws Exception {
		if(cir_node instanceof CirNameExpression) {
			return ((CirNameExpression) cir_node).get_name();
		}
		else if(cir_node instanceof CirComputeExpression) {
			return ((CirComputeExpression) cir_node).get_operator().toString();
		}
		else if(cir_node instanceof CirConstExpression) {
			CConstant constant = ((CirConstExpression) cir_node).get_constant();
			
			Object value;
			switch(constant.get_type().get_tag()) {
			case c_bool:
				value = constant.get_bool(); break;
			case c_char: case c_uchar:
				value = (int) constant.get_char().charValue(); break;
			case c_short: case c_ushort: case c_int: case c_uint:
				value = constant.get_integer(); break;
			case c_long: case c_ulong: case c_llong: case c_ullong:
				value = constant.get_long(); break;
			case c_float:
				value = constant.get_float(); break;
			case c_double:
			case c_ldouble:
				value = constant.get_double(); break;
			default: throw new IllegalArgumentException("Invalid type");
			}
			
			return value.toString();
		}
		else if(cir_node instanceof CirField) {
			return ((CirField) cir_node).get_name();
		}
		else if(cir_node instanceof CirLabel) {
			return ((CirLabel) cir_node).get_target_node_id() + "";
		}
		else {
			return " ";
		}
	}
	/**
	 * ID ast_key? cir_name cir_word? data_type? content? childId childId ... childId
	 * @param cir_tree
	 * @param output
	 * @throws Exception
	 */
	private static void output_cir(CirTree cir_tree, File output) throws Exception {
		FileWriter writer = new FileWriter(output);
		
		writer.write("ID\tast_key\tcir_name\tcir_word\tdata_type\tcontent\tChildren\n");
		
		Map<CirNode, Integer> cir_index = get_cir_index(cir_tree);
		Queue<CirNode> cir_queue = new LinkedList<CirNode>();
		cir_queue.add(cir_tree.get_root());
		
		while(!cir_queue.isEmpty()) {
			CirNode cir_node = cir_queue.poll();
			
			/** ID ast_key? cir_name **/
			writer.write(cir_index.get(cir_node) + "\t");
			if(cir_node.get_ast_source() != null) {
				writer.write(cir_node.get_ast_source().get_key() + "\t");
			}
			else {
				writer.write(" \t");
			}
			writer.write(get_cir_typename(cir_node) + "\t");
			
			/** cir_word? **/
			try {
				CWord word = CWord.word(cir_node);
				writer.write(word.toString() + "\t");
			}
			catch(Exception ex) {
				writer.write(" \t");
			}
			
			/** data_type? **/
			if(cir_node instanceof CirExpression) {
				CType data_type = ((CirExpression) cir_node).get_data_type();
				if(data_type != null) {
					CWord word = CWord.word(data_type);
					writer.write(word.toString() + "\t");
				}
				else {
					writer.write(" \t");
				}
			}
			else {
				writer.write(" \t");
			}
			
			/** content? **/
			writer.write(get_cir_content(cir_node) + "\t");
			
			/** childrenId childrenId ... **/
			for(CirNode child : cir_node.get_children()) {
				if(child != null) {
					cir_queue.add(child);
					writer.write(cir_index.get(child) + "\t");
				}
			}
			writer.write("\n");
		}
		
		writer.close();
	}
	
	/* EXE output methods */
	/**
	 * function ID exe_type cir_key {flow_type trg_id}*
	 * @param execution
	 * @param writer
	 * @throws Exception
	 */
	private static void output_exe(CirExecution execution, FileWriter writer) throws Exception {
		writer.write(execution.get_graph().get_function().get_name() + "\t");
		writer.write(execution.toString() + "\t");
		writer.write(execution.get_type().toString() + "\t");
		writer.write(cir_index.get(execution.get_statement()) + "\t");
		
		for(CirExecutionFlow flow : execution.get_ou_flows()) {
			writer.write(flow.get_type().toString() + "\t");
			writer.write(flow.get_target().toString() + "\t");
		}
		
		writer.write("\n");
	}
	private static void output_exe(CirTree cir_tree, File output) throws Exception {
		FileWriter writer = new FileWriter(output);
		writer.write("Function\tID\texe_type\tcir_key\tou_edges\n");
		
		CirFunctionCallGraph call_graph = cir_tree.get_function_call_graph();
		for(CirFunction function : call_graph.get_functions()) {
			CirExecutionFlowGraph flow_graph = function.get_flow_graph();
			for(CirExecution execution : flow_graph.get_executions()) {
				output_exe(execution, writer);
			}
		}
		
		writer.close();
	}
	
	/* INF output methods */
	private static final StringBuilder context_key = new StringBuilder();
	private static String get_context(CirFunctionCallTreeNode context) throws Exception {
		context_key.setLength(0);
		
		while(context != null) {
			if(context.get_parent() == null) {
				context_key.append(context.get_function().get_name());
			}
			else {
				context_key.append(context.get_context().get_call_execution().toString());
				context_key.append(".");
			}
			context = context.get_parent();
		}
		
		return context_key.toString();
	}
	private static String get_influence_key(CirInfluenceNode node) throws Exception {
		String context = get_context((CirFunctionCallTreeNode) node.get_instance().get_context());
		String execution = node.get_execution().toString();
		int cir_source = cir_index.get(node.get_cir_source());
		return context + ":" + execution + ":" + cir_source;
	}
	/**
	 * ID context execution cir_source {edge_word target_ID}
	 * @param node
	 * @param writer
	 * @throws Exception
	 */
	private static void output_inf(CirInfluenceNode node, FileWriter writer) throws Exception {
		String context = get_context((CirFunctionCallTreeNode) node.get_instance().get_context());
		String execution = node.get_execution().toString();
		int cir_source = cir_index.get(node.get_cir_source());
		
		writer.write(get_influence_key(node) + "\t");
		writer.write(context + "\t");
		writer.write(execution + "\t");
		writer.write(cir_source + "\t");
		
		for(CirInfluenceEdge edge : node.get_ou_edges()) {
			CWord word = CWord.word(edge);
			writer.write(word.toString() + "\t");
			writer.write(get_influence_key(edge.get_target()) + "\t");
		}
		
		writer.write("\n");
	}
	private static void output_inf(CirInfluenceGraph graph, File output) throws Exception {
		FileWriter writer = new FileWriter(output);
		
		writer.write("ID\tcontext\texecution\tcir_key\tedges\n");
		for(CirInstanceNode instance : graph.get_instances()) {
			for(CirInfluenceNode node : graph.get_nodes(instance)) {
				output_inf(node, writer);
			}
		}
		
		writer.close();
	}
	
	/* MUT output methods */
	private static final StringBuilder parameter_buff = new StringBuilder();
	private static String get_operator_of(String text) throws Exception {
		if(text.equals("ADD")) 
			return "+";
		else if(text.equals("SUB"))
			return "-";
		else if(text.equals("MUL"))
			return "*";
		else if(text.equals("DIV"))
			return "/";
		else if(text.equals("MOD"))
			return "%";
		else if(text.equals("BAN"))
			return "&";
		else if(text.equals("BOR"))
			return "|";
		else if(text.equals("BXR"))
			return "^";
		else if(text.equals("LSH"))
			return "<<";
		else if(text.equals("RSH"))
			return ">>";
		else if(text.equals("GRT"))
			return ">";
		else if(text.equals("GRE"))
			return ">=";
		else if(text.equals("SME"))
			return "<=";
		else if(text.equals("SMT"))
			return "<";
		else if(text.equals("EQV"))
			return "==";
		else if(text.equals("NEQ"))
			return "!=";
		else if(text.equals("LAN"))
			return "&&";
		else if(text.equals("LOR"))
			return "||";
		else if(text.equals("ASG"))
			return "=";
		else throw new IllegalArgumentException("Invalid text: " + text);
	}
	private static String get_mutant_parameter(TextMutation mutation) throws Exception {
		parameter_buff.setLength(0);
		
		switch(mutation.get_operator()) {
		case STRI:
		case STRC:
		{
			if(mutation.get_mode() == MutationMode.TRAP_ON_TRUE) 
				parameter_buff.append("true");
			else parameter_buff.append("false");
		}
		break;
		case SSDL: case STRP: case OLNG: case OCNG: case OBNG:
		case SBRC: case SCRB: case SWDD: case SDWD: case VABS:
		{
			parameter_buff.append(" ");
		}
		break;
		case SSWM:
		{
			String replace = mutation.get_replace();
			int beg = replace.lastIndexOf(',') + 1;
			int end = replace.lastIndexOf(')');
			int value = Integer.parseInt(replace.substring(beg, end).strip());
			parameter_buff.append(value);
		}
		break;
		case SMTC:
		{
			String replace = mutation.get_replace();
			int beg = replace.indexOf('(') + 1;
			int end = replace.indexOf(')');
			int value = Integer.parseInt(replace.substring(beg, end).strip());
			parameter_buff.append(value);
		}
		break;
		case OPPO:
		case OMMO:
		{
			switch(mutation.get_mode()) {
			case POST_PREV_INC:	parameter_buff.append("++");	break;
			case POST_INC_DEC:	parameter_buff.append("--");	break;
			case POST_PREV_DEC:	parameter_buff.append("--");	break;
			case POST_DEC_INC:	parameter_buff.append("++");	break;
			case PREV_POST_INC:	parameter_buff.append("++");	break;
			case PREV_INC_DEC:	parameter_buff.append("--");	break;
			case PREV_POST_DEC:	parameter_buff.append("--");	break;
			case PREV_DEC_INC:	parameter_buff.append("++");	break;
			default: parameter_buff.append(" "); break;
			}
		}
		break;
		case UIOI:
		{
			switch(mutation.get_mode()) {
			case POST_INC_INS:	parameter_buff.append("x++");	break;
			case POST_DEC_INS:	parameter_buff.append("x--");	break;
			case PREV_INC_INS:	parameter_buff.append("++x");	break;
			case PREV_DEC_INS:	parameter_buff.append("--x");	break;
			default: parameter_buff.append(" "); break;
			}
		}
		break;
		case ONDU:
		{
			switch(mutation.get_mode()) {
			case ANG_DELETE:	parameter_buff.append("-"); break;
			case BNG_DELETE:	parameter_buff.append("~"); break;
			case LNG_DELETE:	parameter_buff.append("!"); break;
			default: parameter_buff.append(" "); break;
			}
		}
		break;
		case VBCR:
		{
			switch(mutation.get_mode()) {
			case MUT_TRUE:	parameter_buff.append("true"); break;
			case MUT_FALSE:	parameter_buff.append("false"); break;
			default: parameter_buff.append(" "); break;
			}
		}
		break;
		case VDTR:
		{
			switch(mutation.get_mode()) {
			case TRAP_ON_POS:	parameter_buff.append("+");	break;
			case TRAP_ON_NEG:	parameter_buff.append("-");	break;
			case TRAP_ON_ZRO:	parameter_buff.append("0");	break;
			default: parameter_buff.append(" "); break;
			}
		}
		break;
		case VTWD:
		{
			switch(mutation.get_mode()) {
			case SUCC_VAL:	parameter_buff.append("++"); break;
			case PRED_VAL:	parameter_buff.append("--"); break;
			default: parameter_buff.append(" "); break;
			}
		}
		break;
		case CRCR:
		{
			switch(mutation.get_mode()) {
			case CST_TOT_ZRO:	parameter_buff.append("0"); break;
			case CST_POS_ONE:	parameter_buff.append("1"); break;
			case CST_NEG_ONE:	parameter_buff.append("-1"); break;
			case CST_NEG_CST:	parameter_buff.append("-"); break;
			case CST_INC_ONE:	parameter_buff.append("++"); break;
			case CST_DEC_ONE:	parameter_buff.append("--"); break;
			default: parameter_buff.append(" "); break;
			}
		}
		break;
		case CCCR:
		case CCSR:
		{
			String replace = mutation.get_replace();
			int beg = replace.indexOf('(') + 1;
			int end = replace.lastIndexOf(')');
			String value = replace.substring(beg, end).strip();
			parameter_buff.append(value);
		}
		break;
		case VARR:
		case VPRR:
		case VSRR:
		case VTRR:
		case VSFR:
		{
			parameter_buff.append(mutation.get_replace().strip());
		}
		break;
		default: 
		{
			String mode = mutation.get_mode().toString();
			int index = mode.indexOf('_');
			if(index >= 0) {
				String prev = mode.substring(0, index).strip();
				String post = mode.substring(index + 1).strip();
				// System.out.println(mode);
				prev = get_operator_of(prev);
				post = get_operator_of(post);
				parameter_buff.append(prev);
				parameter_buff.append(",");
				parameter_buff.append(post);
			}
			else {
				parameter_buff.append(" ");
			}
		}
		break;
		}
		
		return parameter_buff.toString();
	}
	private static String get_score_bits(MutScore score) throws Exception {
		parameter_buff.setLength(0);
		
		BitSequence bits = score.get_score_set();
		for(int k = 0; k < bits.length(); k++) {
			if(bits.get(k)) {
				parameter_buff.append("1");
			}
			else {
				parameter_buff.append("0");
			}
		}
		
		return parameter_buff.toString();
	}
	/**
	 * program ID operator location(int) parameter label cluster score_bits
	 * @param classifier
	 * @param output
	 * @throws Exception
	 */
	private static void output_mutants(String program, Map<Mutant, Object[]> classifier, File output) throws Exception {
		FileWriter writer = new FileWriter(output);
		writer.write("Program\tID\tOperator\tLocation\tParameter\tLabel\tCluster\tScore\n");
		for(Mutant mutant : classifier.keySet()) {
			writer.write(program + "\t");
			writer.write(mutant.get_mutant_id() + "\t");
			writer.write(mutant.get_mutation().get_operator().toString() + "\t");
			writer.write(mutant.get_mutation().get_origin().get_key() + "\t");
			writer.write(get_mutant_parameter(mutant.get_mutation()) + "\t");
			
			Object[] labels = classifier.get(mutant);
			Character label = (char) labels[0];
			int cluster = (int) labels[1];
			writer.write(label.toString() + "\t");
			writer.write(cluster + "\t");
			MutScore score = (MutScore) labels[2];
			writer.write(get_score_bits(score));
			writer.write("\n");
		}
		writer.close();
	}
	
	/* SEM output methods */
	/**
	 * get the influence with respect to the CIR syntax node in the given context
	 * @param influence_graph
	 * @param context
	 * @param cir_source
	 * @return
	 * @throws Exception
	 */
	private static CirInfluenceNode find_influence_node(
			CirInstanceGraph program_graph, CirInfluenceGraph influence_graph, 
			CirFunctionCallTreeNode context, CirNode cir_source) throws Exception {
		CirStatement statement = null; 
		CirNode old_source = cir_source;
		while(cir_source != null) {
			if(cir_source instanceof CirStatement) {
				statement = (CirStatement) cir_source;
				break;
			}
			else cir_source = cir_source.get_parent();
		}
		if(statement == null) { return null; }
		
		CirTree cir_tree = statement.get_tree();
		CirFunction function = cir_tree.get_function_call_graph().get_function(statement);
		CirExecutionFlowGraph flow_graph = function.get_flow_graph();
		
		if(flow_graph.has_execution(statement)) {
			CirExecution execution = flow_graph.get_execution(statement);
			if(program_graph.has_instance(context, execution)) {
				CirInstanceNode instance = program_graph.get_instance(context, execution);
				if(influence_graph.has_node(instance, old_source)) {
					return influence_graph.get_node(instance, old_source);
				}
				else return null;
			}
			else return null;
		}
		else return null;
	}
	/**
	 * 	[mutation]
	 * 		[property] in|ou semantic_word location (parameter)?
	 * 	[end_mutation]
	 * @param link
	 * @param writer
	 * @throws Exception
	 */
	private static void output_semantic_mutation(CirInstanceGraph program_graph, CirInfluenceGraph influence_graph, 
			CirFunctionCallTreeNode context, CirSemanticLink link, FileWriter writer) throws Exception {
		writer.write("[mutation]\n");
		
		for(CirSemanticNode cause : link.get_in_nodes()) {
			writer.write("[property]\tin\t");
			writer.write(CWord.word(cause).toString() + "\t");
			
			CirInfluenceNode location = find_influence_node(program_graph, influence_graph, context, cause.get_location());
			CirInfluenceNode parameter = null;
			if(cause.get_parameter() instanceof CirNode)
				parameter = find_influence_node(program_graph, influence_graph, context, (CirNode) cause.get_parameter());
			
			if(location == null) writer.write(" ");
			else writer.write(get_influence_key(location));
			writer.write("\t");
			
			if(parameter == null) writer.write(" ");
			else writer.write(get_influence_key(parameter));
			
			writer.write("\n");
		}
		
		for(CirSemanticNode effect : link.get_ou_nodes()) {
			writer.write("[property]\tou\t");
			writer.write(CWord.word(effect).toString() + "\t");
			
			CirInfluenceNode location = find_influence_node(program_graph, influence_graph, context, effect.get_location());
			CirInfluenceNode parameter = null;
			if(effect.get_parameter() instanceof CirNode)
				parameter = find_influence_node(program_graph, influence_graph, context, (CirNode) effect.get_parameter());
			
			if(location == null) writer.write(" ");
			else writer.write(get_influence_key(location));
			writer.write("\t");
			
			if(parameter == null) writer.write(" ");
			else writer.write(get_influence_key(parameter));
			
			writer.write("\n");
		}
		
		writer.write("[end_mutation]\n");
	}
	private static void output_semantic_links(CirInstanceGraph program_graph, CirInfluenceGraph influence_graph,
			CirFunctionCallTreeNode context, CirSemanticMutation mutations, FileWriter writer) throws Exception {
		writer.write("[context]\n");
		for(CirSemanticLink semantic_mutation : mutations.get_reachable_constraint().get_ou_links()) {
			output_semantic_mutation(program_graph, influence_graph, context, semantic_mutation, writer);
		}
		writer.write("[end_context]\n");
	}
	/**
	 * [context]
	 * [end_context]
	 * @param program_graph
	 * @param influence_graph
	 * @param mutations
	 * @param writer
	 * @throws Exception
	 */
	private static void output_context_mutations(CirInstanceGraph program_graph, 
			CirInfluenceGraph influence_graph, CirSemanticMutation mutations, FileWriter writer) throws Exception {
		CirStatement statement = (CirStatement) mutations.get_reachable_constraint().get_location();
		CirFunction function = statement.get_tree().get_function_call_graph().get_function(statement);
		CirExecutionFlowGraph flow_graph = function.get_flow_graph();
		
		if(flow_graph.has_execution(statement)) {
			CirExecution execution = flow_graph.get_execution(statement);
			if(program_graph.has_instances_of(execution)) {
				for(CirInstanceNode instance : program_graph.get_instances_of(execution)) {
					CirFunctionCallTreeNode context = (CirFunctionCallTreeNode) instance.get_context();
					output_semantic_links(program_graph, influence_graph, context, mutations, writer);
				}
			}
		}
	}
	/**
	 * [mutant] program ID
	 * [context]...[end_context]+
	 * [end_mutant]
	 * @param ast_file
	 * @param program_graph
	 * @param influence_graph
	 * @param classifier
	 * @param writer
	 * @throws Exception
	 */
	private static void output_semantic_mutations(String program, AstFile ast_file,
			CirInstanceGraph program_graph, CirInfluenceGraph influence_graph,
			Map<Mutant, Object[]> classifier, File output) throws Exception {
		Mutation2CirSemantic parser = new Mutation2CirSemantic();
		FileWriter writer = new FileWriter(output);
		
		parser.open(ast_file.get_cir_tree());
		for(Mutant mutant : classifier.keySet()) {
			writer.write("[mutant]\t");
			writer.write(program + "\t");
			writer.write(mutant.get_mutant_id() + "\t");
			writer.write("\n");
			
			CirSemanticMutation mutation;
			try {
				mutation = parser.translate(mutant.get_mutation());
			}
			catch(Exception ex) {
				mutation = null;
			}
			if(mutation != null)
				output_context_mutations(program_graph, influence_graph, mutation, writer);
			else {
				/* System.out.println("\t==> ERROR: " + program + "\t" + mutant.
						get_mutant_id() + "\t" + mutant.get_mutation().get_operator()); */
			}
			writer.write("[end_mutant]\n");
		}
		parser.close();
		writer.close();
	}
	
	/* generation method */
	private static void testing(String program) throws Exception {
		File output_dir = new File(postfx + program);
		if(!output_dir.exists()) output_dir.mkdir();
		System.out.println("Testing " + program);
		
		/** 1. open the testing project and mutations be read **/
		File[] files = get_files_of(program);
		JCMT_Project project = get_project(files[0], files[1]);
		Collection<Integer> equivs = get_non_equivalence(files[2]);
		Collection<Integer> nequiv = get_non_equivalence(files[3]);
		System.out.println("\t(1) get project [" + program + "]: "
				+ project.get_code_manager().get_mutant_space().size() + " mutants"
				+ " and " + project.get_test_manager().get_test_space().size() + " tests.");
		
		/** 2. classify the traning mutant samples */
		Map<Mutant, MutScore[]> scores = get_scores(project, files[1]);
		Map<Mutant, MutScore> fscores = filtering(scores);
		MutScoreClusters cluster_set = clustering(fscores);
		MutTestDomains domains = brch_domains(cluster_set);
		Map<Mutant, Object[]> classifier = classifier(fscores, domains, nequiv, equivs);
		System.out.println("\t(2) generate " + cluster_set.size() + 
				" clusters for " + classifier.size() + " mutants.");
		
		/** 3. parsing the program flow and influence graph **/
		AstFile ast_file = project.get_code_manager().get_cursor();
		CirTree cir_tree = ast_file.get_cir_tree();
		CirCallContextInstanceGraph program_graph = translate(cir_tree);
		CirInfluenceGraph influence_graph = generate(program_graph);
		System.out.println("\t(3) generate " + influence_graph.size() + 
				" influence nodes for " + classifier.size() + " mutants.");
		
		/** 4. output all the informations **/
		output_code(ast_file.get_ast_tree(), new File(output_dir + "/" + program + ".c"));
		output_ast(ast_file.get_ast_tree(), new File(output_dir + "/" + program + ".ast"));
		output_cir(ast_file.get_cir_tree(), new File(output_dir + "/" + program + ".cir"));
		output_exe(ast_file.get_cir_tree(), new File(output_dir + "/" + program + ".flw"));
		output_inf(influence_graph, new File(output_dir + "/" + program + ".inf"));
		output_mutants(program, classifier, new File(output_dir + "/" + program + ".mut"));
		output_semantic_mutations(program, ast_file, program_graph, influence_graph, classifier,
				new File(output_dir + "/" + program + ".sem"));
		System.out.println("\t(4) Output all the Code, AST, CIR, EXE, MUT and SEM.");
	} 
	
}
