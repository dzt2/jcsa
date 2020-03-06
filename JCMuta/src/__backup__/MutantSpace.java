package __backup__;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * space for storing and creating mutants and provide identities for them.
 * @author yukimula
 */
public class MutantSpace {
	
	/* constructor */
	private CodeManager manager;
	protected Map<Integer, Mutant> mutants;
	public MutantSpace(CodeManager manager) {
		this.manager = manager;
		mutants = new HashMap<Integer, Mutant>();
	}
	
	/* getters */
	/**
	 * get the manager that creates this mutation space
	 * @return
	 */
	public CodeManager get_manager() { return this.manager; }
	/**
	 * number of mutants in the space
	 * @return
	 */
	public int size() { return mutants.size(); }
	/**
	 * whether there is mutant to the specified id in this space
	 * @param id
	 * @return
	 */
	public boolean has(int id) { return mutants.containsKey(id); }
	/**
	 * get the mutant of the specified id
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Mutant get(int id) throws Exception {
		if(this.mutants.containsKey(id)) return this.mutants.get(id);
		else throw new IllegalArgumentException("Undefined: " + id);
	}
	/**
	 * get the set of mutants in the space
	 * @return
	 */
	public Iterator<Mutant> gets() { return mutants.values().iterator(); }
	/**
	 * Get all the mutants in the space
	 * @return
	 */
	public Collection<Mutant> get_all() { return mutants.values(); }
	/**
	 * set of the id(s) to mutants in the space
	 * @return
	 */
	public Iterator<Integer> idSet() { return mutants.keySet().iterator(); }
	
	/* setters */
	/**
	 * clear the space
	 */
	public void clear() { 
		Collection<Mutant> values = mutants.values();
		for(Mutant mutant : values) mutant.space = null;
		mutants.clear(); 
	}
	/**
	 * create a new mutant in the space
	 * @param id
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	public Mutant new_mutant(int id, TextMutation mutation) throws Exception {
		if(mutants.containsKey(id))
			throw new IllegalArgumentException("Duplicated: " + id);
		else if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else {
			Mutant mutant = new Mutant(this, id, mutation);
			this.mutants.put(id, mutant); return mutant;
		}
	}
	/**
	 * delete an existing mutant from space
	 * @param mutant
	 * @return
	 * @throws Exception
	 */
	public boolean del_mutant(Mutant mutant) throws Exception {
		if(mutant == null || mutant.space != this)
			throw new IllegalArgumentException("Invalid mutant: " + mutant);
		else if(!mutants.containsKey(mutant.mutant_id))
			throw new IllegalArgumentException("Internal error: " + mutant.mutant_id);
		else { this.mutants.remove(mutant.mutant_id); return true; }
	}
	
}
