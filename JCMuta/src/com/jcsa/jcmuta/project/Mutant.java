package com.jcsa.jcmuta.project;

import java.io.File;

import com.jcsa.jcmuta.MutationUtil;
import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.code2mutation.MutationCodeType;

public class Mutant {
	
	/* properties */
	/** the space where the mutant is created **/
	private MutantSpace space;
	/** the integer ID of the mutant in space **/
	private int id;
	/** the mutation used to mutate source code **/
	private AstMutation mutation;
	
	/* constructor */
	/**
	 * create a mutant with respect to the mutation in the space with an ID
	 * @param space
	 * @param id
	 * @param mutation
	 * @throws Exception
	 */
	protected Mutant(MutantSpace space, int id, AstMutation mutation) throws Exception {
		if(space == null)
			throw new IllegalArgumentException("Invalid space: null");
		else if(mutation == null)
			throw new IllegalArgumentException("No mutation available");
		else { this.space = space; this.id = id; this.mutation = mutation; }
	}
	
	/* getters */
	/**
	 * get the space where the mutant is created
	 * @return
	 */
	public MutantSpace get_space() { return this.space; }
	/**
	 * get the integer ID for tag the mutant in space
	 * @return
	 */
	public int get_id() { return this.id; }
	/**
	 * get the mutation performed to mutate the code
	 * @return
	 */
	public AstMutation get_mutation() { return this.mutation; }
	@Override
	public String toString() { return mutation.toString(); }
	
	/* generator */
	/**
	 * generate a code file in the mutant directory named as 
	 * xxx.c the same as the original source file.
	 * @return
	 * @throws Exception
	 */
	public File generate_code(MutationCodeType type) throws Exception {
		File directory = this.space.get_source_file().get_files().get_mutant_directory();
		String file_name = 
				this.space.get_source_file().get_source_file().getName() + "." + id + ".c";
		
		File target = new File(directory + File.separator + file_name);
		switch(type) {
		case Coverage:	MutationUtil.write_coverage_mutation(mutation, target); break;
		case Weakness:	MutationUtil.write_weakness_mutation(mutation, target); break;
		case Stronger:	MutationUtil.write_stronger_mutation(mutation, target); break;
		default: throw new IllegalArgumentException("Invalid type: " + type);
		}
		
		return target;
	}
	
}
