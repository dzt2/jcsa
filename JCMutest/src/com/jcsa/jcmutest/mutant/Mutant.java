package com.jcsa.jcmutest.mutant;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutation;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;

/**
 * It provides the interface to manage the access to the mutation data
 * in the space.
 * @author yukimula
 *
 */
public class Mutant {
	
	/* definitions */
	/** the space in which the mutant is managed **/
	private MutantSpace space;
	/** the unique ID that tags the mutant in space **/
	private int id;
	/** the mutation that the mutant represents **/
	private AstMutation mutation;
	/** coverage, weak and strong version of mutant **/
	protected Mutant[] versions;
	/** the set of cir-mutations parsed from ast-mutation **/
	private List<CirMutation> cir_mutations;
	/**
	 * create an isolated mutant in the space w.r.t the given mutation
	 * @param space
	 * @param id
	 * @param mutation
	 * @throws Exception
	 */
	protected Mutant(MutantSpace space, int id, AstMutation mutation) throws Exception {
		if(space == null)
			throw new IllegalArgumentException("Invalid space: null");
		else if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else {
			this.space = space;
			this.id = id;
			this.mutation = mutation;
			this.versions = new Mutant[] { null, null, null };
			try {
				this.cir_mutations = new ArrayList<CirMutation>();
				Iterable<CirMutation> buffer = this.space.generate_cir_mutation(mutation);
				for(CirMutation cir_mutation : buffer) this.cir_mutations.add(cir_mutation);
			}
			catch(UnsupportedOperationException ex) {
				this.cir_mutations = null;
			}
			catch(Exception ex) {
				/*
				ex.printStackTrace();
				this.cir_mutations = null;
				*/
				// ex.printStackTrace();
				this.cir_mutations = null;
			}
		}
	}
	
	/* getters */
	/**
	 * @return the space in which the mutant is managed
	 */
	public MutantSpace get_space() { return this.space; }
	/**
	 * @return the unique ID that tags the mutant in space
	 */
	public int get_id() { return this.id; }
	/**
	 * @return the mutation that the mutant represents
	 */
	public AstMutation get_mutation() { return this.mutation; }
	/**
	 * @return the coverage version of mutation for this mutant
	 */
	public Mutant get_coverage_mutant() { return this.versions[0]; }
	/**
	 * @return the weak version of mutation for this mutant
	 */
	public Mutant get_weak_mutant() { return this.versions[1]; }
	/**
	 * @return the strong version of mutation for this mutant
	 */
	public Mutant get_strong_mutant() { return this.versions[2]; }
	/**
	 * remove the mutant from its space
	 */
	protected void delete() {
		this.space = null;
		this.id = -1;
		this.mutation = null;
		this.versions = null;
	}
	@Override
	public String toString() {
		return this.mutation.get_location().get_tree().get_source_file().getName() + "[" + id + "]: " + mutation;
	}
	/**
	 * @return the cir-mutations is null if the mutant is syntactically error
	 */
	public boolean has_cir_mutations() { return this.cir_mutations != null; }
	/**
	 * @return the set of cir-mutations parsed from the AST location
	 */
	public Iterable<CirMutation> get_cir_mutations() { return this.cir_mutations; }
	
}
