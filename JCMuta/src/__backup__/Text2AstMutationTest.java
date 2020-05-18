package __backup__;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.back2mutation.Text2MutaTranslators;
import com.jcsa.jcmuta.mutant.code2mutation.MutationCodeType;
import com.jcsa.jcmuta.project.MutaProject;
import com.jcsa.jcmuta.project.MutaSourceFile;
import com.jcsa.jcmuta.project.MutaTestResult;
import com.jcsa.jcparse.lang.astree.AstNode;

public class Text2AstMutationTest {
	
	protected static final String prefix = "D:\\SourceCode\\MyData\\CODE2\\";
	protected static final String cfidir = prefix + "ifiles/";
	protected static final String neqdir = prefix + "nequiv/";
	protected static final String eqvdir = prefix + "equivs/";
	protected static final String prodir = prefix + "TestProjects/";
	protected static final String postfx = "results/inputs/";
	protected static final String target = "D:\\SourceCode\\MyData\\CODE3\\projects2\\";
	private static final double threshold = 0.50;
	private static int total_number, parse_number;
	
	public static void main(String[] args) throws Exception {
		total_number = 0;
		parse_number = 0;
		File[] files = new File(prodir).listFiles();
		FileWriter writer = new FileWriter(postfx + "transition.txt");
		writer.write("Version\tProgram\tID\tClass\tOperator\tLine\tLocation\tCoverage\tWeakness\tStronger\n");
		for(File file : files) {
			String program = file.getName(); 
			testing(program, writer);
		}
		writer.close();
		System.out.println("Parse " + parse_number + " mutations from " + total_number + " mutants.");
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
	
	/* translators */
	protected static void testing(String program, FileWriter muta_writer) throws Exception {
		System.out.println("Start translation at " + program);
		MutaProject test_project = new MutaProject(new File(target + program));
		test_project.get_config().set_csizeof_file(new File("config/csizeof.txt"));
		test_project.get_config().set_jcmulib_header_file(new File("config/jcmulib.h"));
		test_project.get_config().set_jcmulib_source_file(new File("config/jcmulib.c"));
		test_project.get_config().set_parameter_file(new File("config/parameter.txt"));
		
		/** 1. open the testing project and get mutations **/
		File[] files = get_files_of(program);
		JCMT_Project project = get_project(files[0], files[1]);
		Collection<Integer> equivs = get_non_equivalence(files[2]);
		Collection<Integer> nequiv = get_non_equivalence(files[3]);
		test_project.get_source_files().add_source_file(files[1]);
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
		
		/** 3. parse the TextMutation as AstMutation **/
		Map<AstMutation, Mutant> ast_mutations = new HashMap<AstMutation, Mutant>();
		for(Mutant mutant : classifier.keySet()) {
			total_number++;
			try {
				AstMutation mutation = 
						Text2MutaTranslators.parse(mutant.get_mutation());
				if(mutation != null) { 
					String key = mutation.toString();
					AstMutation.parse(mutation.get_location().get_tree(), key);
					ast_mutations.put(mutation, mutant);
					parse_number++;
				}
			}
			catch(Exception ex) {
				if(mutant.get_mutation().get_operator() != MutOperator.VSFR) {
					System.err.println("Error occurs at Old-Mutant#" + mutant.get_mutant_id());
					ex.printStackTrace();
				}
			}
		}
		for(MutaSourceFile source_file : test_project.get_source_files().get_source_files()) {
			source_file.get_mutant_space().set_mutants(ast_mutations.keySet());
			System.out.println("\t(3) generate " + 
					source_file.get_mutant_space().size() + 
					" mutations from " + classifier.size() + 
					" mutants in " + source_file.get_source_file().getName());
		}
		
		/** 4. parse the test cases between two test projects **/
		TestSpace test_space = project.get_test_manager().get_test_space();
		Iterator<TestCase> test_cases = test_space.gets();
		File intermediate_file = new File(postfx + "__tests__");
		FileWriter writer = new FileWriter(intermediate_file);
		while(test_cases.hasNext()) {
			writer.write(test_cases.next().get_command());
			writer.write("\n");
		}
		writer.close();
		List<File> test_data_files = new ArrayList<File>();
		test_data_files.add(intermediate_file);
		test_project.get_test_space().set_test_cases(test_data_files);
		System.out.println("\t(4) generate " + 
				test_project.get_test_space().number_of_test_cases() + 
				" tests from " + test_space.size() + " test cases.");
		
		/** 5. translate the testing results to new test project */
		for(MutaSourceFile source_file : test_project.get_source_files().get_source_files()) {
			for(com.jcsa.jcmuta.project.Mutant mutant : source_file.get_mutant_space().get_mutants()) {
				Mutant original_mutant = ast_mutations.get(mutant.get_mutation());
				MutScore[] mutant_scores = scores.get(original_mutant);
				MutaTestResult result1 = new MutaTestResult(mutant, true, MutationCodeType.Coverage, mutant_scores[0].get_score_set());
				MutaTestResult result2 = new MutaTestResult(mutant, true, MutationCodeType.Weakness, mutant_scores[1].get_score_set());
				MutaTestResult result3 = new MutaTestResult(mutant, true, MutationCodeType.Stronger, mutant_scores[2].get_score_set());
				test_project.get_results().add_test_result(mutant, new MutaTestResult[] { result1, result2, result3 });
			}
		}
		
		/** 6. record the mutant translation as output **/
		for(MutaSourceFile source_file : test_project.get_source_files().get_source_files()) {
			for(com.jcsa.jcmuta.project.Mutant mutant : source_file.get_mutant_space().get_mutants()) {
				Mutant original_mutant = ast_mutations.get(mutant.get_mutation());
				MutScore[] mutant_scores = scores.get(original_mutant);
				muta_writer.write("ORI\t");
				muta_writer.write(program + "\t");
				muta_writer.write(original_mutant.get_mutant_id() + "\t");
				muta_writer.write(original_mutant.get_mutation().get_operator() + "\t");
				muta_writer.write(original_mutant.get_mutation().get_mode() + "\t");
				AstNode location = original_mutant.get_mutation().get_origin();
				int line = location.get_location().line_of();
				muta_writer.write(line + "\t\"" + location.get_location().trim_code() + "\"\t");
				muta_writer.write(mutant_scores[0].get_score_degree() + "\t");
				muta_writer.write(mutant_scores[1].get_score_degree() + "\t");
				muta_writer.write(mutant_scores[2].get_score_degree() + "\t");
				muta_writer.write("\n");
				
				AstMutation translate_mutant = mutant.get_mutation();
				Map<MutationCodeType, MutaTestResult> results = test_project.get_results().read_test_results(mutant);
				muta_writer.write("NEW\t");
				muta_writer.write(program + "\t");
				muta_writer.write(mutant.get_id() + "\t");
				muta_writer.write(translate_mutant.get_mutation_class() + "\t");
				muta_writer.write(translate_mutant.get_mutation_operator() + "\t");
				AstNode new_location = translate_mutant.get_location();
				int new_line = new_location.get_location().line_of();
				muta_writer.write(new_line + "\t\"" + new_location.get_location().trim_code() + "\"\t");
				muta_writer.write(results.get(MutationCodeType.Coverage).get_test_result().degree() + "\t");
				muta_writer.write(results.get(MutationCodeType.Weakness).get_test_result().degree() + "\t");
				muta_writer.write(results.get(MutationCodeType.Stronger).get_test_result().degree() + "\t");
				muta_writer.write("\n");
			}
		}
		
		System.out.println("Testing end for " + program + "\n");
	}
	protected static void translate(String program) throws Exception {
		System.out.println("Start translation at " + program);
		
		/** 1. open the testing project and get mutations **/
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
		
		/** 3. parse the TextMutation as AstMutation **/
		Collection<AstMutation> ast_mutations = new ArrayList<AstMutation>();
		for(Mutant mutant : classifier.keySet()) {
			total_number++;
			try {
				AstMutation mutation = 
						Text2MutaTranslators.parse(mutant.get_mutation());
				if(mutation != null) { 
					ast_mutations.add(mutation); 
					parse_number++;
				}
			}
			catch(Exception ex) {
				if(mutant.get_mutation().get_operator() != MutOperator.VSFR)
					ex.printStackTrace();
			}
		}
		System.out.println("\t(3) generate " + ast_mutations.size() + 
				" mutations from " + classifier.size() + " mutants.");
		
		
	}
	
	
}
