package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator.DeclaratorProduction;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConditionalExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFunCallExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.text.CLocation;

import __backup__.CodeMutationType;
import __backup__.CompileRecord;
import __backup__.JCMT_Builder;
import __backup__.JCMT_Project;
import __backup__.MutScore;
import __backup__.MutScoreCluster;
import __backup__.MutScoreClusters;
import __backup__.MutSubsumeGraph;
import __backup__.MutSubsumeNode;
import __backup__.MutTestDomain;
import __backup__.MutTestDomains;
import __backup__.Mutant;
import __backup__.MutantSpace;
import __backup__.TestOracleManager;
import __backup__.TextMutation;

public class MutScoreAnalysisTest {
	
	protected static final String prefix = "D:\\SourceCode\\MyData\\CODE2\\";
	protected static final String cfidir = prefix + "ifiles/";
	protected static final String neqdir = prefix + "nequiv/";
	protected static final String eqvdir = prefix + "equivs/";
	protected static final String prodir = prefix + "TestProjects/";
	
	public static void main(String[] args) throws Exception {
		File[] files = new File(prodir).listFiles();
		
		FileWriter writer = new FileWriter("results/clustering.txt");
		FileWriter writer2 = new FileWriter("results/clustersum.txt");
		FileWriter writer3 = new FileWriter("results/subsumption.txt");
		FileWriter writer4 = new FileWriter("results/mutation_score.txt");
		
		writer.write("program\tcluster\tmutant\tcoverage\tweak\tstrong"
				+ "\tfunction\tline\tlocation\tpoint\toperator\tmode\treplace\n");
		writer2.write("program\tcluster\tmutants\n");
		writer3.write("program\tnode\tmutants\tdegree\tchildren\n");
		writer4.write("program\tkey\tall_cov_score\tmin_cov_score\tall_stu_score\tmin_stu_score\n");
		
		for(File file : files) {
			String program = file.getName();
			testing(program, writer, writer2, writer3, writer4);
		}
		
		writer.close(); writer2.close(); writer3.close(); writer4.close();
	}
	
	/* project getters */
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
	
	/* scores getter */
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
	
	/* clustering analysis */
	private static Map<Mutant, MutScore> filtering(Map<Mutant, MutScore[]> scores, 
			Collection<Integer> equivs, Collection<Integer> nequivs) throws Exception {
		Map<Mutant, MutScore> fscores = new HashMap<Mutant, MutScore>();
		for(Mutant mutant : scores.keySet()) {
			MutScore[] mscores = scores.get(mutant);
			if(!is_equivalent(mscores[2], equivs, nequivs)) {
				fscores.put(mutant, mscores[2]);
			}
		}
		return fscores;
	}
	private static MutScoreClusters clustering(Map<Mutant, MutScore> scores) throws Exception {
		return MutScoreClusters.clustering(scores);
	}
	private static void output_clusters(String program, MutScoreClusters cluster_set, 
			Map<Mutant, MutScore[]> scores, FileWriter writer) throws Exception {
		Iterable<MutScoreCluster> clusters = cluster_set.get_clusters();
		int cluster_id = 0;
		for(MutScoreCluster cluster : clusters) {
			Iterable<Mutant> mutants = cluster.get_mutants();
			
			for(Mutant mutant : mutants) {
				// ID
				writer.write(program + "\t");
				writer.write(cluster_id + "\t");
				writer.write(mutant.get_mutant_id() + "\t");
				
				// score
				MutScore[] mscores = scores.get(mutant);
				writer.write(mscores[0].get_score_degree() + "\t");
				writer.write(mscores[1].get_score_degree() + "\t");
				writer.write(mscores[2].get_score_degree() + "\t");
				
				// location
				TextMutation mutation = mutant.get_mutation();
				AstNode location = mutation.get_origin();
				writer.write(function_name(location) + "\t");
				writer.write(location.get_location().line_of() + "\t");
				AstNode main_loc = main_location(location);
				writer.write(main_loc.get_location().trim_code(32) + "\t");
				writer.write(location.get_location().trim_code(32) + "\t");
				
				// operator
				writer.write(mutation.get_operator().toString() + "\t");
				writer.write(mutation.get_mode() + "\t");
				writer.write(CLocation.trim_code(mutation.get_replace()) + "\t");
				
				writer.write('\n');
			}
			
			cluster_id++;
		}
	}
	private static String function_name(AstNode node) throws Exception {
		while(!(node instanceof AstFunctionDefinition)) {
			node = node.get_parent();
		}
		AstFunctionDefinition def = (AstFunctionDefinition) node;
		AstDeclarator declarator = def.get_declarator();
		while(declarator.get_production() != DeclaratorProduction.identifier)
			declarator = declarator.get_declarator();
		return declarator.get_identifier().get_name();
	}
	private static AstNode main_location(AstNode location) throws Exception {
		while(location != null) {
			if(location instanceof AstAssignExpression
				|| location instanceof AstArithAssignExpression
				|| location instanceof AstBitwiseAssignExpression
				|| location instanceof AstShiftAssignExpression
				|| location instanceof AstConditionalExpression
				|| location instanceof AstFunCallExpression) {
				return location;
			}
			else if(location instanceof AstExpressionStatement) {
				if(((AstExpressionStatement) location).has_expression())
					return CTypeAnalyzer.get_expression_of(((AstExpressionStatement) location).get_expression());
				else return location;
			}
			else if(location.get_parent() instanceof AstStatement) {
				return location;
			}
			else if(location instanceof AstStatement) {
				return location;
			}
			else location = location.get_parent();
		}
		throw new IllegalArgumentException("Unable to find location...");
	}
	private static void output_clusters_summary(String program, MutScoreClusters cluster_set, FileWriter writer) throws Exception {
		Iterable<MutScoreCluster> clusters = cluster_set.get_clusters();
		int cluster_id = 0;
		for(MutScoreCluster cluster : clusters) {
			writer.write(program + "\t" + cluster_id + "\t" + cluster.size());
			cluster_id++; writer.write('\n');
		}
	}
	
	/* subsumption analysis */
	private static MutSubsumeGraph subsumption(MutScoreClusters cluster_set) throws Exception {
		return MutSubsumeGraph.graph(cluster_set);
	}
	/**
	 * program, ID, mutants, degree, [children]
	 * @param graph
	 * @param writer
	 * @throws Exception
	 */
	private static void output_subsumption(String program, MutSubsumeGraph graph, FileWriter writer) throws Exception {
		Iterable<MutSubsumeNode> nodes = graph.get_nodes();
		for(MutSubsumeNode node : nodes) {
			writer.write(program + "\t");
			writer.write(node.get_id() + "\t");
			writer.write(node.size() + "\t");
			writer.write(node.get_score().get_score_degree() + "\t");
			
			Iterable<MutSubsumeNode> children = node.get_subsummed_nodes();
			writer.write("[ ");
			for(MutSubsumeNode child : children) {
				writer.write(child.get_id() + "; ");
			}
			writer.write("]\n");
		}
	}
	
	/* stubbornness analysis */
	protected static MutTestDomains stmt_domains(MutScoreClusters cluster_set) throws Exception {
		return MutTestDomains.get_statement_domains(cluster_set);
	}
	protected static MutTestDomains brch_domains(MutScoreClusters cluster_set) throws Exception {
		return MutTestDomains.get_branching_domains(cluster_set);
	}
	protected static MutTestDomains mcdc_domains(MutScoreClusters cluster_set) throws Exception {
		return MutTestDomains.get_condition_domains(cluster_set);
	}
	private static final int MAX_TRY_TIMES = 256;
	private static int count_mutants_in(Iterable<MutTestDomain> domains) throws Exception {
		int mutants = 0;
		for(MutTestDomain domain : domains) {
			mutants += domain.get_cluster().size();
		}
		return mutants;
	}
	private static int count_mutants_of(Iterable<MutScoreCluster> clusters) throws Exception {
		int mutants = 0;
		for(MutScoreCluster cluster : clusters) {
			//if(cluster.get_score_key().get_score_degree() > 0)
				mutants += cluster.size();
		}
		return mutants;
	}
	private static void output_mutation_scores(String program, 
			MutTestDomains coverage_domains, 
			MutTestDomains stubborn_domains,
			FileWriter writer) throws Exception {
		Map<MutTestDomain, Integer> all_cov_tests, min_cov_tests;
		Map<MutTestDomain, Integer> all_stu_tests, min_stu_tests;
		MutScoreClusters cluster_set = coverage_domains.get_cluster_set();
		double min_cov_score_sum = 0.0, all_cov_score_sum = 0.0, all_cov_score, min_cov_score;
		double min_stu_score_sum = 0.0, all_stu_score_sum = 0.0, all_stu_score, min_stu_score;
		
		boolean equivalence_counter = false, cluster_counter = true;
		for(int k = 0; k < MAX_TRY_TIMES; k++) {
			all_cov_tests = MutTestDomains.select_tests(coverage_domains.get_all_domains());
			min_cov_tests = MutTestDomains.select_tests(coverage_domains.get_min_domains());
			all_stu_tests = MutTestDomains.select_tests(stubborn_domains.get_all_domains());
			min_stu_tests = MutTestDomains.select_tests(stubborn_domains.get_min_domains());
			
			all_cov_score = cluster_set.mutation_score(all_cov_tests.values(), cluster_counter, equivalence_counter);
			min_cov_score = cluster_set.mutation_score(min_cov_tests.values(), cluster_counter, equivalence_counter);
			all_stu_score = cluster_set.mutation_score(all_stu_tests.values(), cluster_counter, equivalence_counter);
			min_stu_score = cluster_set.mutation_score(min_stu_tests.values(), cluster_counter, equivalence_counter);
			
			all_cov_score_sum += all_cov_score; min_cov_score_sum += min_cov_score;
			all_stu_score_sum += all_stu_score; min_stu_score_sum += min_stu_score;
			
			writer.write(program + "\t");
			writer.write("test[" + k + "]\t");
			writer.write(all_cov_score + "\t");
			writer.write(min_cov_score + "\t");
			writer.write(all_stu_score + "\t");
			writer.write(min_stu_score + "\t");
			writer.write("\n");
		}
		
		all_cov_score = all_cov_score_sum / MAX_TRY_TIMES;
		min_cov_score = min_cov_score_sum / MAX_TRY_TIMES;
		all_stu_score = all_stu_score_sum / MAX_TRY_TIMES;
		min_stu_score = min_stu_score_sum / MAX_TRY_TIMES;
		
		writer.write(program + "\t");
		writer.write("average\t");
		writer.write(all_cov_score + "\t");
		writer.write(min_cov_score + "\t");
		writer.write(all_stu_score + "\t");
		writer.write(min_stu_score + "\t");
		writer.write("\n");
		
		writer.write(program + "\t");
		writer.write("domains\t");
		writer.write(coverage_domains.size() + "\t");
		writer.write(coverage_domains.min_size() + "\t");
		writer.write(stubborn_domains.size() + "\t");
		writer.write(stubborn_domains.min_size() + "\t");
		writer.write("\n");
		
		double total = count_mutants_of(cluster_set.get_clusters());
		writer.write(program + "\t");
		writer.write("mutations\t");
		writer.write(count_mutants_in(coverage_domains.get_all_domains()) + "\t");
		writer.write(coverage_domains.size() / total + "\t");
		writer.write(count_mutants_in(stubborn_domains.get_all_domains()) + "\t");
		writer.write(stubborn_domains.size() / total + "\t");
		writer.write("\n");
		
		writer.flush();
	}
	
	/* testing method */
	private static void testing(String program, FileWriter writer, 
			FileWriter writer2, FileWriter writer3, FileWriter writer4) throws Exception {
		long beg_time, end_time, time_duration;
		System.out.println("Testing on " + program);
		
		// 1. open project and read mutations
		File[] files = get_files_of(program);
		JCMT_Project project = get_project(files[0], files[1]);
		Collection<Integer> equivs = get_non_equivalence(files[2]);
		Collection<Integer> nequiv = get_non_equivalence(files[3]);
		System.out.println("\t(1) get project [" + program + "]: "
				+ project.get_code_manager().get_mutant_space().size() + " mutants"
				+ " and " + project.get_test_manager().get_test_space().size() + " tests.");
		
		// 2. scores + clustering mutations
		Map<Mutant, MutScore[]> scores = get_scores(project, files[1]);
		Map<Mutant, MutScore> fscores = filtering(scores, equivs, nequiv);
		MutScoreClusters cluster_set = clustering(fscores);
		output_clusters(program, cluster_set, scores, writer);
		output_clusters_summary(program, cluster_set, writer2);
		System.out.println("\t(2) generate " + cluster_set.size() + 
				" clusters for " + fscores.size() + " non-equivalent mutants.");
		
		// 3. subsumption analysis
		beg_time = System.currentTimeMillis();
		MutSubsumeGraph graph = subsumption(cluster_set);
		output_subsumption(program, graph, writer3);
		end_time = System.currentTimeMillis();
		time_duration = (end_time - beg_time) / 1000;
		System.out.println("\t(3) generate " + graph.size() + " nodes in subsumption graph (" + time_duration + " ms).");
		
		// 4. mutation score analysis
		MutTestDomains cov_domains = mcdc_domains(cluster_set);
		MutTestDomains stu_domains = MutTestDomains.get_stubborn_domains(cov_domains, 0.50);
		output_mutation_scores(program, cov_domains, stu_domains, writer4);
		System.out.println("\t(4) generate " + cov_domains.get_all_domains().size() + 
				" domains and " + cov_domains.get_min_domains().size() + " essential ones.");
		
		System.out.println();
	}
	
}
