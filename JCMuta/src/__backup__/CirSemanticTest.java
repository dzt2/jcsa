package __backup__;

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
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;

public class CirSemanticTest {
	
	protected static final String prefix = "D:\\SourceCode\\MyData\\CODE2\\";
	protected static final String cfidir = prefix + "ifiles/";
	protected static final String neqdir = prefix + "nequiv/";
	protected static final String eqvdir = prefix + "equivs/";
	protected static final String prodir = prefix + "TestProjects/";
	protected static final String result = "results/sem/";
	
	public static void main(String[] args) throws Exception {
		File[] files = new File(prodir).listFiles();
		for(File file : files) {
			testing(file.getName());
		}
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
	/***
	 * determine the category of the mutation
	 * @param mutant
	 * @param scores
	 * @param nequiv
	 * @param equivs
	 * @return
	 * @throws Exception
	 */
	private static String classify_mutant(Mutant mutant, Map<Mutant, MutScore[]> scores, 
			Collection<Integer> nequiv, Collection<Integer> equivs) throws Exception {
		if(!scores.containsKey(mutant)) {
			return "error";
		}
		else {
			MutScore[] mut_scores = scores.get(mutant);
			if(is_equivalent(mut_scores[2], equivs, nequiv)) {
				return "equivalent";
			}
			else {
				return "non_equivalent";
			}
		}
	}
	
	/* print output */
	private static String get_function_name(AstNode location) throws Exception {
		AstFunctionDefinition def = null;
		while(location != null) {
			if(location instanceof AstFunctionDefinition) {
				def = (AstFunctionDefinition) location; break;
			}
			else location = location.get_parent();
		}
		
		AstDeclarator decl = def.get_declarator();
		while(decl.get_production() != DeclaratorProduction.identifier) {
			decl = decl.get_declarator();
		}
		return decl.get_identifier().get_name();
	}
	private static void output(CirSemanticLink link, FileWriter writer) throws Exception {
		writer.write("\t{\n");
		for(CirSemanticNode in : link.get_in_nodes()) {
			writer.write("\t\t");
			writer.write(in.get_word() + "\t");
			writer.write(in.get_location().generate_code());
			if(in.get_parameter() != null) {
				writer.write("\t");
				writer.write(in.get_parameter().toString());
			}
			writer.write("\n");
		}
		writer.write("\t\t==>\n");
		for(CirSemanticNode ou : link.get_ou_nodes()) {
			writer.write("\t\t");
			writer.write(ou.get_word() + "\t");
			writer.write(ou.get_location().generate_code());
			if(ou.get_parameter() != null) {
				writer.write("\t");
				writer.write(ou.get_parameter().toString());
			}
			writer.write("\n");
		}
		writer.write("\t}\n");
	}
	private static void output(String program, Mutant mutant, String type, Mutation2CirSemantic translator, FileWriter writer) throws Exception {
		/** program operator ID type **/
		writer.write(program + "\t");
		writer.write(mutant.get_mutation().get_operator() + "\t");
		writer.write(mutant.get_mutant_id() + "\t");
		writer.write(type + "\n");
		
		/** [function, line, mode, code, replace] **/
		AstNode location = mutant.get_mutation().get_origin();
		writer.write("\t");
		writer.write(get_function_name(location));
		writer.write("\t");
		writer.write(location.get_location().line_of() + "\t");
		writer.write(location.get_location().trim_code() + "\t");
		writer.write(mutant.get_mutation().get_replace() + "\n");
		
		/** print semantic mutation **/
		CirSemanticMutation sem_mutation = translator.translate(mutant.get_mutation());
		if(sem_mutation != null) {
			for(CirSemanticLink link : sem_mutation.get_reachable_constraint().get_ou_links()) {
				output(link, writer);
			}
		}
		writer.write("\n");
	}
	private static void output(JCMT_Project project, File cfile, File nequiv_file, File equivs_file, FileWriter writer) throws Exception {
		String program = project.get_resource().get_root().getName();
		
		Map<Mutant, MutScore[]> scores = get_scores(project, cfile);
		Collection<Integer> nequiv = get_non_equivalence(nequiv_file);
		Collection<Integer> equivs = get_non_equivalence(equivs_file);
		
		Mutation2CirSemantic translator = new Mutation2CirSemantic();
		translator.open(project.get_code_manager().get_cursor().get_cir_tree());
		int errors = 0, total = 0;
		for(Mutant mutant : scores.keySet()) {
			String type = classify_mutant(mutant, scores, nequiv, equivs);
			total++;
			
			try {
				output(program, mutant, type, translator, writer);
			}
			catch(Exception ex) {
				// ex.printStackTrace();
				errors++;
			}
		}
		translator.close();
		
		System.out.println("\t\t==> Error-Rate: " + errors + "/" + total);
	}
	
	/* testing methods */
	private static void testing(String name) throws Exception {
		File[] files = get_files_of(name);
		System.out.println("Testing " + name);
		
		JCMT_Project project = get_project(files[0], files[1]);
		System.out.println("\t(1) load " + project.get_code_manager().
				get_mutant_space().size() + " mutants in " + name);
		
		FileWriter writer = new FileWriter(result + name + ".txt");
		output(project, files[1], files[3], files[2], writer);
		System.out.println("\t(2) output all the mutations to file");
		writer.close();
		
		System.out.println();
	}
	
}
