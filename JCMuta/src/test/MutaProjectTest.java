package test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmuta.MutaClass;
import com.jcsa.jcmuta.mutant.ast2mutation.AstMutationGenerators;
import com.jcsa.jcmuta.project.MutaProject;
import com.jcsa.jcmuta.project.MutaSourceFile;

public class MutaProjectTest {
	
	protected static final String prefix = "D:/SourceCode/MyData/CODE2/gfiles/";
	protected static final String inputs_dir = "D:\\SourceCode\\MyData\\CODE2\\inputs\\";
	protected static final String suites_dir = "D:\\SourceCode\\MyData\\CODE2\\suite\\";
	protected static final String postfx = "D:\\SourceCode\\MyData\\projects\\";
	
	public static void main(String[] args) throws Exception {
		for(File file : new File(prefix).listFiles()) {
			// testing(file.getName());
			testing2(file.getName());
		}
	}
	
	private static Collection<MutaClass> operators() throws Exception {
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
	protected static void testing(String name) throws Exception {
		int index = name.lastIndexOf('.');
		name = name.substring(0, index).strip();
		
		File code_file = new File(prefix + name + ".c");
		Collection<MutaClass> operators = operators();
		File project_directory = new File(postfx + name);
		System.out.println("Testing on " + name);
		
		MutaProject project = new MutaProject(project_directory);
		System.out.println("\t1. Create project for mutation test.");
		
		project.get_config().set_csizeof_file(new File("config/csizeof.txt"));
		project.get_config().set_jcmulib_header_file(new File("config/jcmulib.h"));
		project.get_config().set_jcmulib_source_file(new File("config/jcmulib.c"));
		project.get_config().set_parameter_file(new File("config/parameter.txt"));
		System.out.println("\t2. Complete configuration");
		
		project.get_source_files().add_source_file(code_file);
		for(MutaSourceFile source_file : project.get_source_files().get_source_files()) {
			source_file.get_mutant_space().set_mutants(operators);
			System.out.println("\t\t==> " + source_file.get_mutant_space().size() 
					+ " mutants for " + source_file.get_source_file().getName());
		} 
		System.out.println("\t3. Generate mutants and code.");
		
		File suite_directory = new File(suites_dir + name);
		List<File> suite_files = new ArrayList<File>();
		if(suite_directory.exists()) {
			File[] files = suite_directory.listFiles();
			if(files != null) {
				for(File file : files) {
					suite_files.add(file);
				}
			}
		}
		project.get_test_space().set_test_cases(suite_files);
		File inputs_directory = new File(inputs_dir + name);
		if(inputs_directory.exists()) 
			project.get_test_space().set_test_inputs(inputs_directory);
		System.out.println("\t4. Generate " + project.
				get_test_space().number_of_test_cases() + " test cases.");
		
		System.out.println();
	}
	protected static void testing2(String name) throws Exception {
		int index = name.lastIndexOf('.');
		name = name.substring(0, index).strip();
		
		Collection<MutaClass> operators = operators();
		File project_directory = new File(postfx + name);
		System.out.println("Testing on " + name);
		
		MutaProject project = new MutaProject(project_directory);
		System.out.println("\t1. Create project for mutation test.");
		
		project.get_config().set_csizeof_file(new File("config/csizeof.txt"));
		project.get_config().set_jcmulib_header_file(new File("config/jcmulib.h"));
		project.get_config().set_jcmulib_source_file(new File("config/jcmulib.c"));
		project.get_config().set_parameter_file(new File("config/parameter.txt"));
		System.out.println("\t2. Complete configuration");
		
		for(MutaSourceFile source_file : project.get_source_files().get_source_files()) {
			source_file.get_mutant_space().set_mutants(operators);
			System.out.println("\t\t==> " + source_file.get_mutant_space().size() 
					+ " mutants for " + source_file.get_source_file().getName());
		} 
		System.out.println("\t3. Generate mutants and code.");
		
		System.out.println("\t4. Generate " + project.
				get_test_space().number_of_test_cases() + " test cases.");
		
		project.get_binaries().generate_test_scripts(3, 2);
		
		System.out.println();
	}
	
}
