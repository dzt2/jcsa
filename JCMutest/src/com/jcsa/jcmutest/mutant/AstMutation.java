package com.jcsa.jcmutest.mutant;

import com.jcsa.jcparse.lang.astree.AstNode;

/**
 * The mutation defined based on syntactic rule on abstract syntax tree node.
 * 
 * @author yukimula
 *
 */
public class AstMutation {
	
	/* attributes */
	/** the group of mutation operator **/
	private MutaGroup muta_group;
	/** the class of mutation operator **/
	private MutaClass muta_class;
	/** the mutation operator on AST **/
	private MutaOperator operator;
	/** the location in which the mutation is seeded **/
	private AstNode location;
	/** the parameter that defines the mutation **/
	private Object parameter;
	
	/* constructor */
	/**
	 * @param muta_group the group of mutation operator
	 * @param muta_class the class of mutation operator 
	 * @param operator the mutation operator on AST
	 * @param location the location in which the mutation is seeded
	 * @param parameter the parameter that defines the mutation
	 * @throws IllegalArgumentException
	 */
	protected AstMutation(MutaGroup muta_group, MutaClass muta_class, 
			MutaOperator operator, AstNode location, Object parameter) 
					throws IllegalArgumentException {
		if(muta_group == null)
			throw new IllegalArgumentException("Invalid group: null");
		else if(muta_class == null)
			throw new IllegalArgumentException("Invalid class: null");
		else if(operator == null)
			throw new IllegalArgumentException("Invalid operator: null");
		else if(location == null)
			throw new IllegalArgumentException("Invalid location: null");
		else {
			this.muta_group = muta_group;
			this.muta_class = muta_class;
			this.operator = operator;
			this.location = location;
			this.parameter = parameter;
		}
	}
	
	/* getters */
	/**
	 * @return the group of mutation operator
	 */
	public MutaGroup get_group() { return this.muta_group; }
	/**
	 * @return the class of mutation operator
	 */
	public MutaClass get_class() { return this.muta_class; }
	/**
	 * @return the mutation operator on AST
	 */
	public MutaOperator get_operator() { return operator; }
	/**
	 * @return the location in which the mutation is seeded
	 */
	public AstNode get_location() { return this.location; }
	/**
	 * @return the parameter that defines the mutation
	 */
	public Object get_parameter() { return this.parameter; }
	/**
	 * @return whether the parameter is non-null in mutation
	 */
	public boolean has_parameter() { return parameter != null; }
	
	/* identifier */
	@Override
	public String toString() {
		try {
			return AstMutations.mutation2string(this);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof AstMutation) {
			return this.toString().equals(obj.toString());
		}
		else {
			return false;
		}
	}
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	
}
