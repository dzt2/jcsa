package __backup__;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.jcsa.jcmuta.MutaClass;
import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.ast2mutation.AstMutationGenerators;
import com.jcsa.jcparse.lang.AstCirFile;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.astree.AstTree;

public class AstMutationGeneratorTest {
	
	protected static final String prefix = "D:/SourceCode/MyData/CODE2/gfiles/";
	protected static final String postfx = "results/ast/";
	protected static final File template_file = new File("config/run_temp.txt");
	
	public static void main(String[] args) throws Exception {
		for(File file : new File(prefix).listFiles()) {
			testing(file);
		}
	}
	
	private static AstCirFile parse(File file) throws Exception {
		return AstCirFile.parse(file, template_file, ClangStandard.gnu_c89);
	}
	private static Set<MutaClass> all_operators() {
		Set<MutaClass> operators = new HashSet<MutaClass>();
		operators.addAll(AstMutationGenerators.trapping_classes);
		operators.addAll(AstMutationGenerators.statement_classes);
		operators.addAll(AstMutationGenerators.unary_classes);
		operators.addAll(AstMutationGenerators.operator_classes);
		// operators.addAll(AstMutationGenerators.expression_classes);
		operators.addAll(AstMutationGenerators.semantic_classes);
		operators.add(MutaClass.VBRP);
		operators.add(MutaClass.VCRP);
		return operators;
	}
	private static void output(Collection<AstMutation> mutations, File output) throws Exception {
		FileWriter writer = new FileWriter(output);
		for(AstMutation mutation : mutations) {
			writer.write(mutation.toString());
			writer.write("\n");
		}
		writer.close();
	}
	private static void test_input(AstTree ast_tree, File output) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(output));
		String line; int counter = 0;
		while((line = reader.readLine()) != null) {
			line = line.strip();
			if(line.length() > 0) {
				AstMutation.parse(ast_tree, line);
				counter++;
			}
		}
		System.out.println("\t4. read " + counter + " mutations from");
		reader.close();
	}
	
	private static void testing(File file) throws Exception {
		System.out.println("Testing " + file.getName());
		AstCirFile program = parse(file);
		System.out.println("\t1. parsing the source file");
		Collection<AstMutation> mutations = AstMutationGenerators.
				generate(program.get_ast_tree(), all_operators());
		System.out.println("\t2. generate " + mutations.size() + " mutations");
		output(mutations, new File(postfx + file.getName() + ".txt"));
		System.out.println("\t3. output mutations on file");
		test_input(program.get_ast_tree(), new File(postfx + file.getName() + ".txt"));
		System.out.println();
	}
	
}
