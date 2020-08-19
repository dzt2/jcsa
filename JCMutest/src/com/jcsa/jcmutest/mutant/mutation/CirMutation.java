package com.jcsa.jcmutest.mutant.mutation;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * The mutation defined on standard intermediate representation for C (CIR).
 * 
 * @author yukimula
 *
 */
public class CirMutation {
	
	/* attributes */
	/** the group of mutation operator **/
	private MutaGroup muta_group;
	/** the class of mutation operator **/
	private MutaClass muta_class;
	/** the mutation operator on AST **/
	private MutaOperator operator;
	/** the location in which the mutation is seeded **/
	private CirNode location;
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
	protected CirMutation(MutaGroup muta_group, MutaClass muta_class, 
			MutaOperator operator, CirNode location, Object parameter) 
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
	public CirNode get_location() { return this.location; }
	/**
	 * @return the parameter that defines the mutation
	 */
	public Object get_parameter() { return this.parameter; }
	/**
	 * @return whether the parameter is non-null in mutation
	 */
	public boolean has_parameter() { return parameter != null; }
	
	/* identify */
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
	private String parameter2string() {
		if(this.parameter == null)
			return "";
		else if(this.parameter instanceof Boolean) {
			return "b@" + this.parameter;
		}
		else if(this.parameter instanceof Integer) {
			return "i@" + this.parameter;
		}
		else if(this.parameter instanceof Long) {
			return "l@" + this.parameter;
		}
		else if(this.parameter instanceof Double) {
			return "d@" + this.parameter;
		}
		else if(this.parameter instanceof String) {
			return "s@" + this.parameter;
		}
		else if(this.parameter instanceof COperator) {
			return "o@" + this.parameter;
		}
		else if(this.parameter instanceof CirNode) {
			return "r@" + this.parameter;
		}
		else {
			throw new IllegalArgumentException("Invalid parameter: " + parameter);
		}
	}
	@Override
	public String toString() {
		return this.muta_group.toString() + "\t"
				+ this.muta_class.toString() + "\t"
				+ this.operator.toString() + "\t"
				+ this.location.get_node_id() + "\t"
				+ this.parameter2string();
	}
	private static Object string2parameter(CirTree tree, String text) {
		int index = text.indexOf('@');
		char title = text.charAt(0);
		String value = text.substring(index + 1).strip();
		switch(title) {
		case 'b':
			if(value.equals("true")) {
				return Boolean.TRUE;
			}
			else {
				return Boolean.FALSE;
			}
		case 'i':
			return Integer.valueOf(value);
		case 'l':
			return Long.valueOf(value);
		case 'd':
			return Double.valueOf(value);
		case 's':
			return value;
		case 'o':
			return COperator.valueOf(value);
		case 'r':
			return tree.get_node(Integer.parseInt(value));
		default: throw new IllegalArgumentException("Invalid " + text);
		}
	}
	/**
	 * @param line
	 * @return group class operator location parameter
	 * @throws Exception
	 */
	public static CirMutation parse(CirTree tree, String line) throws Exception {
		if(line == null || line.isBlank())
			throw new IllegalArgumentException("Empty line");
		else if(tree == null)
			throw new IllegalArgumentException("Invalid tree");
		else {
			String[] items = line.strip().split("\t");
			MutaGroup group = MutaGroup.valueOf(items[0].strip());
			MutaClass mclass = MutaClass.valueOf(items[1].strip());
			MutaOperator operator = MutaOperator.valueOf(items[2].strip());
			CirNode location = tree.get_node(Integer.valueOf(items[3].strip()));
			Object parameter = null;
			if(items.length > 4) {
				parameter = string2parameter(tree, items[4].strip());
			}
			return new CirMutation(group, mclass, operator, location, parameter);
		}
	}
	
}
