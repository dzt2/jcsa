package com.jcsa.jcmutest.mutant.mutation;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;

/**
 * The mutation that is injected in program written in C-intermediate
 * representation language.
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
	
	/* constructors */
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
	
	/* identification */
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
	/**
	 * @return bool | char | short | int | long | float | double | string | cir_node
	 */
	private String parameter2string() {
		if(parameter == null) {
			return "";
		}
		else if(parameter instanceof Boolean) {
			return "bool@" + parameter.toString();
		}
		else if(parameter instanceof Character) {
			return "char@" + ((int) ((Character) parameter).charValue());
		}
		else if(parameter instanceof Short) {
			return "short@" + ((Short) parameter).shortValue();
		}
		else if(parameter instanceof Integer) {
			return "int@" + parameter;
		}
		else if(parameter instanceof Long) {
			return "long@" + parameter;
		}
		else if(parameter instanceof Float) {
			return "float@" + parameter;
		}
		else if(parameter instanceof Double) {
			return "double@" + parameter;
		}
		else if(parameter instanceof String) {
			return "string@" + parameter;
		}
		else if(parameter instanceof CirNode) {
			return "cir@" + ((CirNode) parameter).get_node_id();
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + parameter);
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
	
	/* parsing methods */
	/**
	 * @param tree
	 * @param text
	 * @return bool | char | short | int | long | float | double | string | cir_node
	 * @throws Exception
	 */
	private static Object string2parameter(
			CirTree tree, String text) throws Exception {
		if(text == null || text.isBlank())
			return null;
		else {
			int index = text.indexOf('@');
			String title = text.substring(0, index).strip();
			String value = text.substring(index + 1).strip();
			
			if(title.equals("bool")) {
				return Boolean.valueOf(value.equals("true"));
			}
			else if(title.equals("char")) {
				return Character.valueOf((char) Integer.parseInt(value));
			}
			else if(title.equals("short")) {
				return Short.valueOf(value);
			}
			else if(title.equals("int")) {
				return Integer.parseInt(value);
			}
			else if(title.equals("long")) {
				return Long.parseLong(value);
			}
			else if(title.equals("float")) {
				return Float.valueOf(value);
			}
			else if(title.equals("double")) {
				return Double.valueOf(value);
			}
			else if(title.equals("string")) {
				return value;
			}
			else if(title.equals("cir")) {
				return tree.get_node(Integer.parseInt(value));
			}
			else {
				throw new IllegalArgumentException("Unsupport: " + text);
			}
		}
	}
	/**
	 * @param tree
	 * @param line
	 * @return the cir-mutation parsed from the string line
	 * @throws Exception
	 */
	public static CirMutation parse(CirTree tree, String line) throws Exception {
		if(line == null || line.isBlank())
			throw new IllegalArgumentException("Invalid line: null");
		else {
			String[] items = line.strip().split("\t");
			MutaGroup group = MutaGroup.valueOf(items[0].strip());
			MutaClass mclass = MutaClass.valueOf(items[1].strip());
			MutaOperator operator = MutaOperator.valueOf(items[2].strip());
			CirNode location = tree.get_node(Integer.parseInt(items[3].strip()));
			Object parameter = null;
			if(items.length > 4) {
				parameter = string2parameter(tree, items[4].strip());
			}
			return new CirMutation(group, mclass, operator, location, parameter);
		}
	}
	
}
