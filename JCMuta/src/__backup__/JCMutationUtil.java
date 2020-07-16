package __backup__;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.astree.unit.AstTranslationUnit;
import com.jcsa.jcparse.lang.AstCirFile;

/**
 * Provide basic APIs to generate mutants & mutations from source code.
 * @author yukimula
 */
public class JCMutationUtil {
	
	/* generators */
	protected static final Mutation2CoverageParser cov_parser = new Mutation2CoverageParser();
	protected static final Mutation2WeaknessParser weak_parser = new Mutation2WeaknessParser();
	protected static final MutGenerator mut_generator = new MutGenerator();
	protected static final MutaCodeGenerator writer = new MutaCodeGenerator();
	protected static final File template_file = new File("config/run_temp.txt");
	
	/* parsers */
	/**
	 * get the AST of the given C program file
	 * @param cfile
	 * @return
	 * @throws Exception
	 */
	public static AstCirFile ast_file_of(File cfile) throws Exception {
		return AstCirFile.parse(cfile, template_file, ClangStandard.gnu_c89);
	}
	
	/* mutation generation */
	/**
	 * generate all mutations in the AST root.
	 * @param astroot
	 * @return
	 * @throws Exception
	 */
	public static Set<Mutation> gen_mutation(AstTranslationUnit astroot) throws Exception {
		return mut_generator.generate_all(astroot);
	}
	/**
	 * parse the set of mutations to the set of text-mutations, 
	 * this will refresh the mutants among the space
	 * @param mutations
	 * @return
	 * @throws Exception
	 */
	public static void mutation2text(Set<Mutation> mutations, 
			MutantSpace space) throws Exception {
		if(space == null)
			throw new IllegalArgumentException("No outputs are specified");
		else {
			space.clear(); int mid = 0;
			Mutation2TextParser parser = new Mutation2TextParser();
			for(Mutation mutation : mutations) {
				TextMutation text = parser.parse(mutation);
				space.new_mutant(mid++, text);
			}
		}
	}
	/**
	 * parse the set of mutations to the set of context-mutations
	 * @param mutations
	 * @param space
	 * @throws Exception
	 */
	public static void mutation2context(Set<Mutation> mutations, 
			MutantSpace space, AstCirFile source) throws Exception {
		if(space == null)
			throw new IllegalArgumentException("No outputs are specified");
		else if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(mutations == null)
			throw new IllegalArgumentException("Invalid mutations: null");
		else {
			space.clear(); int mid = 0;
			Mutation2ContextParser parser = 
					new Mutation2ContextParser(source);
			Set<ContextMutation> results = 
					new HashSet<ContextMutation>();
			
			for(Mutation mutation : mutations) {
				parser.parse(mutation, results);
				for(ContextMutation text : results)
					space.new_mutant(mid++, text);
				results.clear();
			}
		}
	}
	
	/* mutation -- mutation translation */
	/**
	 * translate the mutation to coverage mutation
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	public static TextMutation mutation2coverage(TextMutation mutation) throws Exception {
		TextMutation ans = cov_parser.parse(mutation);
		if(mutation instanceof ContextMutation) {
			ContextMutation cm = (ContextMutation) mutation;
			ans = ContextMutation.produce(
					ans.get_operator(), ans.get_mode(), 
					ans.get_origin(), ans.get_replace(), 
					cm.get_callee(), cm.get_muta_function());
		}
		return ans;
	}
	/**
	 * translate the mutation to strong mutation
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	public static TextMutation mutation2strong(TextMutation mutation) throws Exception {
		return mutation;
	}
	/**
	 * translate the mutation to the weak mutation version
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	public static TextMutation mutation2weak(TextMutation mutation) throws Exception {
		return weak_parser.parse(mutation);
	}
	
	/* mutation --> code generation methods */
	/**
	 * write the coverage mutation code to specified file
	 * @param mutant
	 * @param source
	 * @param target
	 * @throws Exception
	 */
	/*public static void write_coverage_mutation(Mutant mutant, AstFile source, File target) throws Exception {
		writer.write(mutant, source, target, MutaCodeGenerator.COVERAGE);
	}*/
	/**
	 * write the weak mutation code to specified file
	 * @param mutant
	 * @param source
	 * @param target
	 * @throws Exception
	 */
	/*public static void write_weak_mutation(Mutant mutant, AstFile source, File target) throws Exception {
		writer.write(mutant, source, target, MutaCodeGenerator.WEAKNESS);
	}*/
	/**
	 * write the strong mutation code to specified file
	 * @param mutant
	 * @param source
	 * @param target
	 * @throws Exception
	 */
	/*public static void write_strong_mutation(Mutant mutant, AstFile source, File target) throws Exception {
		writer.write(mutant, source, target, MutaCodeGenerator.STRONGER);
	}*/
	/**
	 * write the mutation to the specified file
	 * @param mutant
	 * @param source
	 * @param target
	 * @param mtype : 0 for coverage, 1 for weak mutation, 2 for strong mutation
	 * @throws Exception
	 */
	public static void write_mutation_to_file(Mutant mutant, 
			AstCirFile source, File target, CodeMutationType mtype) throws Exception {
		writer.write(mutant, source, target, mtype);
	}
	
}
