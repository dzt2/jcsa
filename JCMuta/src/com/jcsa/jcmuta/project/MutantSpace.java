package com.jcsa.jcmuta.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmuta.MutaClass;
import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.ast2mutation.AstMutationGenerators;
import com.jcsa.jcparse.lang.astree.AstTree;

public class MutantSpace {
	
	/* properties */
	/** the source file where the mutants are generated **/
	private MutaSourceFile source_file;
	/** the sequence of mutants being generated from **/
	private List<Mutant> mutants;
	
	/* constructor */
	/**
	 * create a mutant space with corresponding to the source file
	 * @param source_file
	 * @throws Exception
	 */
	protected MutantSpace(MutaSourceFile source_file) throws Exception {
		if(source_file == null)
			throw new IllegalArgumentException("Invalid file: null");
		else {
			/** initial declaration **/
			this.source_file = source_file;
			this.mutants = new ArrayList<Mutant>();
			
			/** load the mutants from data file **/ 
			this.load_mutants();
		}
	}
	
	/* synchornize */
	/**
	 * read the mutants in data file to the memory
	 * @throws Exception
	 */
	private void load_mutants() throws Exception {
		File mutant_file = this.source_file.get_mutant_file();
		if(mutant_file.exists()) {
			this.mutants.clear(); String line; 
			BufferedReader reader = new BufferedReader(new FileReader(mutant_file));
			
			AstTree ast_tree = this.source_file.get_ast_tree();
			while((line = reader.readLine()) != null) {
				line = line.strip();
				if(line.length() > 0) {
					AstMutation mutation = AstMutation.parse(ast_tree, line);
					mutants.add(new Mutant(this, mutants.size(), mutation));
				}
			}
			reader.close();
		}
	}
	/**
	 * write the mutants into data file from memory
	 * @throws Exception
	 */
	private void save_mutants() throws Exception {
		File mutant_file = this.source_file.get_mutant_file();
		FileWriter writer = new FileWriter(mutant_file);
		
		for(Mutant mutant : this.mutants) {
			String line = mutant.get_mutation().toString().strip();
			writer.write(line); 
			writer.write("\n");
		}
		
		writer.close();
	}
	
	/* getters */
	/**
	 * get the source file from which the mutants are generated in this space
	 * @return
	 */
	public MutaSourceFile get_source_file() { return this.source_file; }
	/**
	 * get the mutants generated in the space
	 * @return
	 */
	public Iterable<Mutant> get_mutants() { return this.mutants; }
	/**
	 * get the mutant with respect to the ID in the space
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Mutant get_mutant(int id) throws Exception { return mutants.get(id); }
	/***
	 * set the mutants in the space as the set of operators
	 * (1) this method may take a very long time
	 * (2) this will update the mutant data file
	 * @param operators
	 * @throws Exception
	 */
	public void set_mutants(Iterable<MutaClass> operators) throws Exception {
		if(operators == null)
			throw new IllegalArgumentException("Invalid operators: null");
		else {
			AstTree ast_tree = this.source_file.get_ast_tree();
			Collection<AstMutation> mutations; mutants.clear();
			
			/** remove the duplicated mutation operators **/
			Set<MutaClass> operator_set = new HashSet<MutaClass>();
			for(MutaClass operator : operators) operator_set.add(operator);
			
			/** generate the mutants in the operators **/
			for(MutaClass operator : operator_set) {
				mutations = AstMutationGenerators.generate(ast_tree, operator);
				
				for(AstMutation mutation : mutations) {
					mutants.add(new Mutant(this, mutants.size(), mutation));
				}
			}
			
			/** save the mutants generated **/	this.save_mutants();
		}
	}
	/**
	 * get the number of mutants generated from the source file
	 * @return
	 */
	public int size() { return mutants.size(); }
	/**
	 * Set the mutations in the space and save them
	 * @param mutations
	 * @throws Exception
	 */
	public void set_mutants(Collection<AstMutation> mutations) throws Exception {
		this.mutants.clear();
		for(AstMutation mutation : mutations) {
			this.mutants.add(new Mutant(this, mutants.size(), mutation));
		}
		this.save_mutants();
	}
	
}
