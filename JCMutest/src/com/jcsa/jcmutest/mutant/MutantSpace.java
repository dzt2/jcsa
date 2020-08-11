package com.jcsa.jcmutest.mutant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmutest.MutaClass;
import com.jcsa.jcmutest.mutant.ast2mutant.Ast2Mutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcparse.lang.AstCirFile;
import com.jcsa.jcparse.lang.astree.AstTree;

/**
 * It provides the interface to generate, manage, preserve and load
 * the syntactic mutations from programs written in C.
 * 
 * @author yukimula
 *
 */
public class MutantSpace {
	
	/* definitions */
	/** the program in which the mutants are created **/
	private AstCirFile program;
	/** the mutants created and managed under the space **/
	private List<Mutant> mutants;
	/**
	 * create an empty mutant space that manages the mutants generated
	 * over the program as specified.
	 * @param program
	 * @throws Exception
	 */
	public MutantSpace(AstCirFile program) throws Exception {
		if(program == null)
			throw new IllegalArgumentException("Invalid program: null");
		else {
			this.program = program;
			this.mutants = new ArrayList<Mutant>();
		}
	}
	
	/* getters */
	/**
	 * @return the abstract syntax tree on which the mutants are seeded
	 */
	public AstTree get_ast_tree() { return this.program.get_ast_tree(); }
	/**
	 * @return the number of mutants managed under the space
	 */
	public int size() { return this.mutants.size(); }
	/**
	 * @return the mutants that are generated and managed in this space
	 */
	public Iterable<Mutant> get_mutants() { return this.mutants; }
	/**
	 * @param id
	 * @return the mutant w.r.t. the id in the space
	 * @throws IndexOutOfBoundsException
	 */
	public Mutant get_mutant(int id) throws IndexOutOfBoundsException {
		return this.mutants.get(id);
	}
	
	/* management */
	/**
	 * remove all the mutants from the space
	 */
	public void clear() {
		for(Mutant mutant : this.mutants) {
			mutant.remove();
		}
		this.mutants.clear();
	}
	/**
	 * remove the old mutants and set those in space as newly generated ones
	 * @param mutation_classes used to generate new mutants in the space
	 * @return the number of newly generated mutants in this space
	 * @throws Exception
	 */
	public int generate(Iterable<MutaClass> mutation_classes) throws Exception {
		this.clear();
		
		Set<String> mutation_keys = new HashSet<String>();
		Collection<AstMutation> mutations = Ast2Mutation.
				seed(this.program.get_ast_tree(), mutation_classes);
		
		for(AstMutation mutation : mutations) {
			if(mutation != null) {
				String key = Ast2Mutation.mutation2string(mutation);
				if(!mutation_keys.contains(key)) {
					mutation_keys.add(key);
					Mutant mutant = new Mutant(this, mutants.size(), mutation);
					this.mutants.add(mutant);
				}
			}
		}
		
		return this.mutants.size();
	}
	/**
	 * save the mutants in the space to the specified file
	 * @param mfile
	 * @throws Exception
	 */
	public void save(File file) throws Exception {
		if(file == null)
			throw new IllegalArgumentException("Invalid file: null");
		else {
			FileWriter writer = new FileWriter(file);
			for(Mutant mutant : this.mutants) {
				writer.write(Ast2Mutation.mutation2string(mutant.get_mutation()));
				writer.write("\n");
			}
			writer.close();
		}
	}
	/**
	 * read the mutants preserved in the file and update the space
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public int load(File file) throws Exception {
		this.clear();
		
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		while((line = reader.readLine()) != null) {
			if(!line.isBlank()) {
				AstMutation mutation = Ast2Mutation.string2mutation(
						this.program.get_ast_tree(), line.strip());
				Mutant mutant = new Mutant(this, mutants.size(), mutation);
				this.mutants.add(mutant);
			}
		}
		reader.close();
		
		return this.mutants.size();
	}
	
}
