package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;

/**
 * To represent how the code is syntactically changed.<br>
 * <br>
 * <code>(location, operator, mode)</code><br>
 * <br>
 * A mutant could contain more than one mutation (for higher-order)
 * @author yukimula
 */
public interface Mutation {
	/**
	 * get the operator of this mutation
	 * @return
	 */
	public MutOperator get_operator();
	/**
	 * get the mutation mode
	 * @return
	 */
	public MutationMode get_mode();
	/**
	 * get the location to seed mutation
	 * @return
	 */
	public AstNode get_location();
}
