package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;

public abstract class SingleMutation implements Mutation {
	
	protected MutOperator operator;
	protected MutationMode mode;
	protected AstNode location;
	
	protected SingleMutation(MutOperator operator, MutationMode mode, AstNode location) throws Exception {
		if(mode == null)
			throw new IllegalArgumentException("Invalid mode: " + mode);
		else if(location == null)
			throw new IllegalArgumentException("Invalid point: " + location);
		else if(!this.validate(mode, location))
			throw new IllegalArgumentException(
					"Invalid mutation: " + mode + "; " + location);
		else {
			this.operator = operator;
			this.mode = mode;
			this.location = location;
		}
	}

	@Override
	public MutOperator get_operator() {return operator;}
	@Override
	public MutationMode get_mode() {return mode;}
	@Override
	public AstNode get_location() {return location;}

	/**
	 * whether the mode and location are valid composition in current mutation.
	 * @param mode
	 * @return
	 */
	protected abstract boolean validate(MutationMode mode, AstNode location);
}
