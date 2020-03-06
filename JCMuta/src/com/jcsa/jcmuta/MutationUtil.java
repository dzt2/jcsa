package com.jcsa.jcmuta;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.ast2mutation.AstMutationGenerators;
import com.jcsa.jcmuta.mutant.code2mutation.MutaCodeGenerators;
import com.jcsa.jcmuta.mutant.code2mutation.MutationCodeType;
import com.jcsa.jcparse.lang.astree.AstTree;

public class MutationUtil {
	
	/* mutation operators groups */
	/**
	 * get the operators for generating trapping mutations
	 * @return
	 */
	public static Collection<MutaClass> get_trapping_mutation_operators() {
		return AstMutationGenerators.trapping_classes;
	}
	/**
	 * get the operators for generating statement mutations
	 * @return
	 */
	public static Collection<MutaClass> get_statement_mutation_operators() {
		return AstMutationGenerators.statement_classes;
	}
	/**
	 * get the operators for generating unary operator mutations
	 * @return
	 */
	public static Collection<MutaClass> get_unary_mutation_operators() {
		return AstMutationGenerators.unary_classes;
	}
	/**
	 * get the operators for generating operator mutations
	 * @return
	 */
	public static Collection<MutaClass> get_operator_mutation_operators() {
		return AstMutationGenerators.operator_classes;
	}
	/**
	 * get the operators for generating reference | constant mutations
	 * @return
	 */
	public static Collection<MutaClass> get_reference_mutation_operators() {
		return AstMutationGenerators.expression_classes;
	}
	/**
	 * get the operators for generating semantic mutations
	 * @return
	 */
	public static Collection<MutaClass> get_semantic_mutation_operators() {
		return AstMutationGenerators.semantic_classes;
	}
	
	/* mutation generation methods */
	/** buffer to preserve the mutation operators used to generate mutations in code **/
	private static final Set<MutaClass> mutation_operators = new HashSet<MutaClass>();
	/**
	 * generate the mutations by mutating the syntax tree of the target program with respect to the given operator
	 * @param ast_tree
	 * @param operator
	 * @return
	 * @throws Exception
	 */
	public static Collection<AstMutation> generate(AstTree ast_tree, MutaClass operator) throws Exception {
		mutation_operators.clear(); mutation_operators.add(operator);
		return AstMutationGenerators.generate(ast_tree, mutation_operators);
	}
	/**
	 * generate the mutations by mutating the syntax tree of the target program with respect to the given operators
	 * @param ast_tree
	 * @param operators
	 * @return
	 * @throws Exception
	 */
	public static Collection<AstMutation> generate(AstTree ast_tree, Collection<MutaClass> operators) throws Exception {
		mutation_operators.clear(); mutation_operators.addAll(operators);
		return AstMutationGenerators.generate(ast_tree, mutation_operators);
	}
	
	/* mutation code generators */
	/**
	 * generate the code for mutation and write it to the mutation file in coverage level
	 * @param mutation
	 * @param mutation_file
	 * @throws Exception
	 */
	public static void write_coverage_mutation(AstMutation mutation, File mutation_file) throws Exception {
		MutaCodeGenerators.generate_code(mutation, MutationCodeType.Coverage, mutation_file);
	}
	/**
	 * generate the code for mutation and write it to the mutation file in weakness level
	 * @param mutation
	 * @param mutation_file
	 * @throws Exception
	 */
	public static void write_weakness_mutation(AstMutation mutation, File mutation_file) throws Exception {
		MutaCodeGenerators.generate_code(mutation, MutationCodeType.Weakness, mutation_file);
	}
	/**
	 * generate the code for mutation and write it to the mutation file in stronger level
	 * @param mutation
	 * @param mutation_file
	 * @throws Exception
	 */
	public static void write_stronger_mutation(AstMutation mutation, File mutation_file) throws Exception {
		MutaCodeGenerators.generate_code(mutation, MutationCodeType.Stronger, mutation_file);
	}
	
	/* utility methods */
	private static final byte[] file_buffer = new byte[1024 * 16];
	public static void copy_file(File source, File target) throws Exception {
		if(source == null || !source.exists() || source.isDirectory())
			throw new IllegalArgumentException("Not found: " + source.getAbsolutePath());
		else if(target == null || target.isDirectory())
			throw new IllegalArgumentException("Invalid target: null");
		else {
			FileInputStream in = new FileInputStream(source);
			FileOutputStream ou = new FileOutputStream(target);
			int length;
			while((length = in.read(file_buffer)) >= 0) 
				ou.write(file_buffer, 0, length);
			in.close(); ou.close(); return;
		}
	}
	public static void copy_directory(File source, File target) throws Exception {
		if(source == null || !source.isDirectory())
			throw new IllegalArgumentException("Invalid source: null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target: null");
		else {
			if(!target.exists()) target.mkdir();
			
			File[] source_files = source.listFiles();
			if(source_files != null) {
				for(File source_file : source_files) {
					File target_file = new File(target.getAbsolutePath() + 
							File.separator + source_file.getName());
					if(!source_file.isDirectory()) {
						MutationUtil.copy_file(source_file, target_file);
					}
					else {
						MutationUtil.copy_directory(source_file, target_file);
					}
				}
			}
		}
	}
	public static void delete_files(File directory) throws Exception {
		if(directory.isDirectory()) {
			File[] files = directory.listFiles();
			if(files != null) {
				for(File file : files) {
					delete_files(file);
					if(file.isDirectory())
						file.delete();
				}
			}
		}
		else { directory.delete(); }
	}
	
}
