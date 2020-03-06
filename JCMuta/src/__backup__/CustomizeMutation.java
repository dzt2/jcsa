package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;

/**
 * Customized mutation can be used to customize another argument in the mutant
 * @author yukimula
 */
public abstract class CustomizeMutation implements Mutation {
	
	protected MutOperator operator;
	protected MutationMode mutation_mode;
	protected AstNode source;
	protected Object argument;
	
	protected CustomizeMutation(MutOperator operator, MutationMode mode, AstNode source, Object argument) throws Exception {
		if(operator == null)
			throw new IllegalArgumentException("Invalid operator: null");
		else if(mode == null)
			throw new IllegalArgumentException("Invalid mode: null");
		else if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(argument == null)
			throw new IllegalArgumentException("Invalid argument: null");
		else if(!this.validate(mode, source, argument))
			throw new IllegalArgumentException("Invalid composition: { " + mode + ", " + source + ", " + argument + " }");
		else {
			this.operator = operator;
			this.mutation_mode = mode;
			this.source = source;
			this.argument = argument;
		}
	}

	@Override
	public MutOperator get_operator() {return operator;}
	@Override
	public MutationMode get_mode() {return mutation_mode;}
	@Override
	public AstNode get_location() {return source;}
	/**
	 * get the argument of the mutation
	 * @return
	 */
	public Object get_argument() {return argument;}

	/**
	 * validate whether the argument and source | mode are valid of composition.
	 * @param mode
	 * @param source
	 * @param argument
	 * @return
	 */
	protected abstract boolean validate(MutationMode mode, AstNode source, Object argument);
}
