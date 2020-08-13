package com.jcsa.jcmutest.mutant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.ast2mutant.MutationGenerators;
import com.jcsa.jcmutest.mutant.ext2mutant.MutationExtensions;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.MutaClass;
import com.jcsa.jcparse.lang.astree.AstTree;

/**
 * The mutant space that manages the mutations being generated from the program.
 * 
 * @author yukimula
 *
 */
public class MutantSpace {
	
	/* attributes */
	/** the abstract syntax tree on which the mutants are seeded **/
	private AstTree tree;
	/** mappings from the unique string of mutation to itself **/
	private Map<String, AstMutation> mutations;
	/** the list of mutants that contain coverage, weak and strong versions **/
	private List<Mutant> mutants;
	/** mapping from the representative mutation to mutant object in space **/
	private Map<AstMutation, Mutant> index;
	
	/* constructor */
	/**
	 * create an empty mutant space defined on the AST of the program
	 * @param tree
	 * @throws Exception
	 */
	public MutantSpace(AstTree tree) throws Exception {
		if(tree == null)
			throw new IllegalArgumentException("Invalid tree: null");
		else {
			this.tree = tree;
			this.mutations = new HashMap<String, AstMutation>();
			this.mutants = new ArrayList<Mutant>();
			this.index = new HashMap<AstMutation, Mutant>();
		}
	}
	
	/* getters */
	/**
	 * @return the abstract syntax tree on which the mutations are seeded
	 */
	public AstTree get_ast_tree() { return this.tree; }
	/**
	 * @return the number of mutants for being tested in the program
	 */
	public int number_of_mutants() { return this.mutants.size(); }
	/**
	 * @return the mutants created in the program
	 */
	public Iterable<Mutant> get_mutants() { return this.mutants; }
	/**
	 * @param id
	 * @return the mutant w.r.t. the unique ID
	 * @throws IndexOutOfBoundsException
	 */
	public Mutant get_mutant(int id) throws IndexOutOfBoundsException {
		return this.mutants.get(id);
	}
	/**
	 * @param mutation
	 * @return whether there is a mutant w.r.t. the mutation as its strong version
	 */
	public boolean has_mutant(AstMutation mutation) {
		return this.index.containsKey(mutation);
	}
	/**
	 * @param mutation
	 * @return the mutant w.r.t. the mutation as its strong version or
	 * 		   null when no such mutant exists
	 * @throws Exception
	 */
	public Mutant get_mutant(AstMutation mutation) throws Exception {
		if(this.index.containsKey(mutation))
			return this.index.get(mutation);
		else return null;
	}
	/**
	 * @return the number of mutations generated from the source code
	 */
	public int number_of_mutations() { return this.mutations.size(); }
	/**
	 * @return the {coverage, weak, strong} mutations for the mutants in the space
	 */
	public Iterable<AstMutation> get_mutations() { return this.mutations.values(); }
	
	/* setters */
	/**
	 * remove all the mutations and mutants in the space
	 */
	public void clear() {
		this.mutations.clear();
		for(Mutant mutant : this.mutants) {
			mutant.delete();
		}
		this.mutants.clear();
		this.index.clear();
	}
	/**
	 * @param mutation
	 * @return the unique version of the mutation
	 */
	private AstMutation get_mutation(AstMutation mutation) {
		String key = mutation.toString();
		if(!this.mutations.containsKey(key)) {
			this.mutations.put(key, mutation);
		}
		return this.mutations.get(key);
	}
	/**
	 * @param mutation_classes
	 * @return the number of mutants being seeded in the program
	 * @throws Exception
	 */
	public int update(Iterable<MutaClass> mutation_classes) throws Exception {
		this.clear();
		List<AstMutation> mutations = 
				MutationGenerators.generate(tree, mutation_classes);
		for(AstMutation mutation : mutations) {
			AstMutation[] mutant_mutations = MutationExtensions.extend(mutation);
			mutant_mutations[0] = this.get_mutation(mutant_mutations[0]);
			mutant_mutations[1] = this.get_mutation(mutant_mutations[1]);
			mutant_mutations[2] = this.get_mutation(mutant_mutations[2]);
			if(!this.index.containsKey(mutant_mutations[2])) {
				Mutant mutant = new Mutant(this, mutants.size(), mutant_mutations);
				this.mutants.add(mutant);
				this.index.put(mutant.get_mutation(), mutant);
			}
		}
		return this.mutants.size();
	}
	/**
	 * write the mutant and mutations in the file
	 * @param file
	 * @throws Exception
	 */
	public void save(File file) throws Exception {
		FileWriter writer = new FileWriter(file);
		for(Mutant mutant : this.mutants) {
			writer.write("#\n");
			writer.write(mutant.get_coverage_mutation().toString() + "\n");
			writer.write(mutant.get_weak_mutation().toString() + "\n");
			writer.write(mutant.get_strong_mutation().toString() + "\n");
		}
		writer.close();
	}
	/**
	 * @param file
	 * @return the number of mutants read from the file
	 * @throws Exception
	 */
	public int load(File file) throws Exception {
		this.clear();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		while((line = reader.readLine()) != null) {
			if(!line.isBlank()) {
				line = line.strip();
				if(line.startsWith("#")) {
					AstMutation cov_mutation = this.get_mutation(
							AstMutation.parse(tree, reader.readLine().strip()));
					AstMutation weak_mutation = this.get_mutation(
							AstMutation.parse(tree, reader.readLine().strip()));
					AstMutation strong_mutation = this.get_mutation(
							AstMutation.parse(tree, reader.readLine().strip()));
					if(!this.index.containsKey(strong_mutation)) {
						Mutant mutant = new Mutant(this, mutants.size(), new AstMutation[] {
								cov_mutation, weak_mutation, strong_mutation
						});
						this.mutants.add(mutant);
						this.index.put(mutant.get_strong_mutation(), mutant);
					}
				}
			}
		}
		reader.close();
		return this.mutants.size();
	}
	
}
