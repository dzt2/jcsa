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

public class MutantSpace {
	
	/** syntactic tree on which the mutations are seeded **/
	private AstTree tree;
	/** the list of mutants objects **/
	private List<Mutant> mutants;
	/** from strong mutation to its unique mutant in space **/
	private Map<AstMutation, Mutant> index;
	/** to ensure the uniqueness of each syntactic mutation **/
	private Map<String, AstMutation> mutations;
	/**
	 * an empty mutant space defined on specified program
	 * @param tree
	 * @throws Exception
	 */
	public MutantSpace(AstTree tree) throws Exception {
		if(tree == null)
			throw new IllegalArgumentException("Invalid tree: null");
		else {
			this.tree = tree;
			this.mutants = new ArrayList<Mutant>();
			this.index = new HashMap<AstMutation, Mutant>();
			this.mutations = new HashMap<String, AstMutation>();
		}
	}
	
	/* getters */
	/**
	 * @return the syntactic tree on which the mutations are seeded
	 */
	public AstTree get_ast_tree() { return this.tree; }
	/**
	 * @return the number of mutants managed under the space
	 */
	public int size() { return this.mutants.size(); }
	/**
	 * @return mutants managed under the space
	 */
	public Iterable<Mutant> get_mutants() { return this.mutants; }
	/**
	 * @param id
	 * @return mutant w.r.t. the unitue ID
	 * @throws IndexOutOfBoundsException
	 */
	public Mutant get_mutant(int id) throws IndexOutOfBoundsException {
		return this.mutants.get(id);
	}
	/**
	 * @param mutation
	 * @return whether there is mutant w.r.t. the strong mutation
	 */
	public boolean has_mutant(AstMutation mutation) {
		return this.index.containsKey(mutation);
	}
	/**
	 * @param mutation
	 * @return the mutant w.r.t. strong mutation as given
	 * @throws Exception
	 */
	public Mutant get_mutant(AstMutation mutation) throws Exception {
		return this.index.get(mutation);
	}
	/**
	 * @return the set of strong mutations w.r.t. all the mutants in the space
	 */
	public Iterable<AstMutation> get_mutations() {
		return this.index.keySet();
	}
	
	/* setters */
	/**
	 * remove all the mutants from the space
	 */
	public void clear() {
		for(Mutant mutant : this.mutants) {
			mutant.delete();
		}
		this.mutants.clear();
		this.index.clear();
		this.mutations.clear();
	}
	/**
	 * @param mutation
	 * @return the unique mutation in the space
	 */
	private AstMutation get_mutation(AstMutation mutation) {
		String key = mutation.toString();
		if(!this.mutations.containsKey(key)) {
			this.mutations.put(key, mutation);
		}
		return this.mutations.get(key);
	}
	/**
	 * update the space by removing old mutants and generate new ones for
	 * the entire program w.r.t. the mutation classes as specified.
	 * @param mutation_classes
	 * @return
	 * @throws Exception
	 */
	public int update(Iterable<MutaClass> mutation_classes) throws Exception {
		this.clear();
		
		List<AstMutation> source_mutations = 
				MutationGenerators.generate(tree, mutation_classes);
		for(AstMutation source_mutation : source_mutations) {
			AstMutation[] all_mutations = MutationExtensions.extend(source_mutation);
			all_mutations[0] = this.get_mutation(all_mutations[0]);
			all_mutations[1] = this.get_mutation(all_mutations[1]);
			all_mutations[2] = this.get_mutation(all_mutations[2]);
			if(!this.index.containsKey(all_mutations[2])) {
				Mutant mutant = new Mutant(this, mutants.size(), all_mutations);
				this.mutants.add(mutant);
				this.index.put(all_mutations[2], mutant);
			}
		}
		
		return this.mutants.size();
	}
	/**
	 * save the mutations in the specified file
	 * @param file
	 * @throws Exception
	 */
	public void save(File file) throws Exception {
		FileWriter writer = new FileWriter(file);
		for(Mutant mutant : this.mutants) {
			writer.write("#" + mutant.get_id() + "\n");
			writer.write(mutant.get_coverage_mutation().toString() + "\n");
			writer.write(mutant.get_weak_mutation().toString() + "\n");
			writer.write(mutant.get_strong_mutation().toString() + "\n");
		}
		writer.close();
	}
	/**
	 * read the data in specified file and build the mutants in space
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
				if(line.startsWith("#")) {
					AstMutation c_mutation = AstMutation.parse(tree, reader.readLine().strip());
					AstMutation w_mutation = AstMutation.parse(tree, reader.readLine().strip());
					AstMutation s_mutation = AstMutation.parse(tree, reader.readLine().strip());
					c_mutation = this.get_mutation(c_mutation);
					w_mutation = this.get_mutation(w_mutation);
					s_mutation = this.get_mutation(s_mutation);
					if(!this.index.containsKey(s_mutation)) {
						Mutant mutant = new Mutant(this, mutants.size(), new 
								AstMutation[] { c_mutation, w_mutation, s_mutation });
						this.mutants.add(mutant);
						this.index.put(s_mutation, mutant);
					}
				}
			}
		}
		reader.close();
		return this.mutants.size();
	}
	
}
