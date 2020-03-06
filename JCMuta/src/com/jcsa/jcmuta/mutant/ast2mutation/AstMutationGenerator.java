package com.jcsa.jcmuta.mutant.ast2mutation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.astree.unit.AstTranslationUnit;

/**
 * Used to generate the mutations of specified operator.
 * 
 * @author yukimula
 *
 */
public abstract class AstMutationGenerator {
	
	/* constructor */
	protected AstTree ast_tree;
	protected AstMutationGenerator() { this.ast_tree = null; }
	
	/* generator */
	protected void open(AstTree ast_tree) throws Exception {
		this.ast_tree = ast_tree;
	}
	/**
	 * generate all the mutations that mutate the syntactic structure of code
	 * based on 
	 * @param ast_tree
	 * @return
	 * @throws Exception
	 */
	public Collection<AstMutation> generate(AstTree ast_tree) throws Exception {
		if(ast_tree == null)
			throw new IllegalArgumentException("Invalid ast_tree: null");
		else {
			/** 1. declarations **/
			this.ast_tree = ast_tree; AstTranslationUnit ast_root;
			ast_root = this.ast_tree.get_ast_root();
			
			/** 2. collect all the function definitions for seeding faults **/
			Queue<AstNode> ast_queue = new LinkedList<AstNode>();
			for(int k = 0; k < ast_root.number_of_units(); k++) {
				AstNode child = ast_root.get_unit(k);
				if(child instanceof AstFunctionDefinition) {
					ast_queue.add(child);
				}
			}
			
			/** 3. collect all the locations that are available for mutation **/
			Set<AstNode> locations = new HashSet<AstNode>();
			while(!ast_queue.isEmpty()) {
				AstNode location = ast_queue.poll();
				for(int k = 0; k < location.number_of_children(); k++) {
					AstNode child = location.get_child(k);
					if(child != null) { ast_queue.add(child); }
				}
				this.collect_locations(location, locations);
			}
			
			/** 4. generate all the mutations of specified type in locations **/
			List<AstMutation> mutations = new ArrayList<AstMutation>();
			for(AstNode location : locations) {
				this.generate_mutations(location, mutations);
			}
			return mutations;
		}
	}
	
	/* implementations */
	/**
	 * collect all the locations that are available for being mutated with respect to the operator class
	 * @param location
	 * @param locations
	 * @throws Exception
	 */
	protected abstract void collect_locations(AstNode location, Collection<AstNode> locations) throws Exception;
	/**
	 * generate the mutation(s) of specified operator class by seeding fault in the specified location
	 * @param location
	 * @param mutations
	 * @throws Exception
	 */
	protected abstract void generate_mutations(AstNode location, Collection<AstMutation> mutations) throws Exception;
	
}
