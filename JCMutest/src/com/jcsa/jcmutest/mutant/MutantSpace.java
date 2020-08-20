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
import com.jcsa.jcparse.lang.irlang.CirTree;

public class MutantSpace {
	
	/* definition */
	/** syntax tree in which mutants are seeded **/
	private AstTree ast_tree;
	/** syntax tree in C-intermediate representation **/
	private CirTree cir_tree;
	/** mutants created in the space **/
	private List<Mutant> mutants;
	/** mapping from the mutation to its mutant **/
	private Map<String, Mutant> index;
	/**
	 * create an empty space for mutants seeded in AST
	 * @param ast_tree
	 * @throws Exception
	 */
	public MutantSpace(AstTree ast_tree, CirTree cir_tree) throws Exception {
		if(ast_tree == null)
			throw new IllegalArgumentException("Invalid ast_tree");
		else if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree");
		else {
			this.ast_tree = ast_tree;
			this.cir_tree = cir_tree;
			this.mutants = new ArrayList<Mutant>();
			this.index = new HashMap<String, Mutant>();
		}
	}
	
	/* getters */
	/**
	 * @return syntax tree in which mutants are seeded
	 */
	public AstTree get_ast_tree() { return this.ast_tree; }
	public CirTree get_cir_tree() { return this.cir_tree; }
	/**
	 * @return the number of mutants in the space
	 */
	public int size() { return this.mutants.size(); }
	/**
	 * @return the mutants managed in the space
	 */
	public Iterable<Mutant> get_mutants() { return mutants; }
	/**
	 * @param id
	 * @return the mutant w.r.t. the id
	 * @throws IndexOutOfBoundsException
	 */
	public Mutant get_mutant(int id) throws IndexOutOfBoundsException {
		return this.mutants.get(id);
	}
	/**
	 * @param mutation
	 * @return whether there is mutant w.r.t. the mutation
	 */
	public boolean has_mutant(AstMutation mutation) {
		if(mutation == null)
			return false;
		else
			return this.index.containsKey(mutation.toString());
	}
	/**
	 * @param mutation
	 * @return the mutant w.r.t. the mutation
	 * @throws Exception
	 */
	public Mutant get_mutant(AstMutation mutation) throws Exception {
		if(mutation == null || !index.containsKey(mutation.toString())) {
			throw new IllegalArgumentException("Invalid mutation: null");
		}
		else {
			return this.index.get(mutation.toString());
		}
	}
	
	/* setters */
	/**
	 * @param mutation
	 * @return 
	 * @throws Exception
	 */
	private Mutant new_mutant(AstMutation mutation) throws Exception {
		String key = mutation.toString();
		if(!this.index.containsKey(key)) {
			Mutant mutant = new Mutant(this, mutants.size(), mutation);
			this.mutants.add(mutant);
			this.index.put(key, mutant);
		}
		return this.index.get(key);
	}
	/**
	 * remove all the mutants in the space
	 */
	public void clear() {
		for(Mutant mutant : this.mutants) {
			mutant.delete();
		}
		this.mutants.clear();
		this.index.clear();
	}
	/**
	 * save the mutants in the space to the file
	 * @param file
	 * @throws Exception
	 */
	public void save(File file) throws Exception {
		FileWriter writer = new FileWriter(file);
		
		/* mutation writer */
		for(Mutant mutant : this.mutants) {
			writer.write(mutant.get_mutation().toString());
			writer.write("\n");
		}
		
		/* mutation version connection */
		for(Mutant mutant : this.mutants) {
			writer.write("#");
			writer.write(" " + mutant.get_id());
			writer.write(" " + mutant.get_coverage_mutant().get_id());
			writer.write(" " + mutant.get_weak_mutant().get_id());
			writer.write(" " + mutant.get_strong_mutant().get_id());
			writer.write("\n");
		}
		
		writer.close();
	}
	/**
	 * load the mutants from the file
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
					String[] items = line.strip().split(" ");
					int id = Integer.parseInt(items[1].strip());
					int cov_id = Integer.parseInt(items[1].strip());
					int wek_id = Integer.parseInt(items[2].strip());
					int str_id = Integer.parseInt(items[3].strip());
					Mutant mutant = this.mutants.get(id);
					Mutant cov_mutant = this.mutants.get(cov_id);
					Mutant wek_mutant = this.mutants.get(wek_id);
					Mutant str_mutant = this.mutants.get(str_id);
					mutant.versions[0] = cov_mutant;
					mutant.versions[1] = wek_mutant;
					mutant.versions[2] = str_mutant;
				}
				else {
					this.new_mutant(AstMutation.parse(ast_tree, line.strip()));
				}
			}
		}
		reader.close();
		return this.mutants.size();
	}
	/**
	 * @param mutation_classes
	 * @return the number of generated mutants
	 * @throws Exception
	 */
	public int update(Iterable<MutaClass> mutation_classes) throws Exception {
		/* 1. clear the space */	this.clear();
		
		/* 2. generate the mutations in program */
		List<AstMutation> mutations = 
				MutationGenerators.generate(ast_tree, mutation_classes);
		for(AstMutation mutation : mutations) {
			this.new_mutant(mutation); 
			AstMutation[] versions = MutationExtensions.extend(mutation);
			this.new_mutant(versions[0]);
			this.new_mutant(versions[1]);
			this.new_mutant(versions[2]);
		}
		
		/* 3. connect the mutants with versions */
		for(Mutant mutant : this.mutants) {
			AstMutation[] versions = MutationExtensions.extend(mutant.get_mutation());
			Mutant cov_mutant = this.get_mutant(versions[0]);
			Mutant wek_mutant = this.get_mutant(versions[1]);
			Mutant str_mutant = this.get_mutant(versions[2]);
			mutant.versions[0] = cov_mutant;
			mutant.versions[1] = wek_mutant;
			mutant.versions[2] = str_mutant;
		}
		
		/* 4. the number of generated ones */	return mutants.size();
	}
	
}
