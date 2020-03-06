package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;

/**
 * To translate <code>Mutation</code> to <code>TextMutation</code>
 * @author yukimula
 */
public abstract class MutTranslator {
	
	/** used to preserve the mutated code in mutation **/
	protected StringBuilder buffer;
	/**
	 * abstract constructor 
	 */
	public MutTranslator() {
		buffer = new StringBuilder();
	}
	
	/* main method */
	/**
	 * translate the syntactic mutation to text mutation of first-order
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	public TextMutation parse(Mutation mutation) throws Exception {
		if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else if(!this.validate(mutation))
			throw new IllegalArgumentException(
					"Unknown mutation: " + mutation.getClass().getSimpleName());
		else {
			AstNode origin = this.derive(mutation);
			buffer.setLength(0); this.mutate(mutation);
			return TextMutation.produce(
					mutation.get_operator(), mutation.get_mode(), 
					origin, buffer.toString());
		}
	}
	
	/* refinition */
	/**
	 * validate whether the mutation is available for current translator
	 * @param mutation
	 * @return
	 */
	protected abstract boolean validate(Mutation mutation);
	/**
	 * extract the location where mutant is seeded
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected abstract AstNode derive(Mutation mutation) throws Exception;
	/**
	 * get the text to be mutated in original source code
	 * @param mutation
	 * @throws Exception
	 */
	protected abstract void mutate(Mutation mutation) throws Exception;
}
