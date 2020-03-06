package __backup__;

/**
 * In the layer of program semantics, a mutation refers to a set of semantic links that connect
 * from a set of initial causes (constraints) to a set of initial effects (state errors).
 * 
 * @author yukimula
 *
 */
public class CirSemanticMutation {
	
	/* constructor */
	/** the node describing the reachability constraint for mutation be killed **/
	private CirSemanticNode reachable;
	/**
	 * create a semantic-based mutation
	 * @param mutant
	 * @param reachable
	 * @throws Exception
	 */
	protected CirSemanticMutation(CirSemanticNode reachable) throws Exception {
		if(reachable == null)
			throw new IllegalArgumentException("Invalid reachability");
		else { this.reachable = reachable; }
	}
	
	/* getters */
	/**
	 * get the reachability constraint of the mutation (cannot be null)
	 * @return
	 */
	public CirSemanticNode get_reachable_constraint() { return this.reachable; }
	/**
	 * get all the initial cause-effect propagation from initial constraint to the initial state errors.
	 * @return
	 */
	public Iterable<CirSemanticLink> get_initial_propagations() { return this.reachable.get_ou_links(); }
	
}
