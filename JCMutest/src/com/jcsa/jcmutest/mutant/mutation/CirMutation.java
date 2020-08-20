package com.jcsa.jcmutest.mutant.mutation;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * The mutation injected in C-intermediate representation.
 * 
 * @author yukimula
 *
 */
public class CirMutation {
	
	/* attributes */
	/** the function that defines the mutation in CIR **/
	private MutaFunction function;
	/** the parameters used to refine mutation in CIR **/
	private List<Object> parameters;
	/**
	 * create a cir-mutation without parameters
	 * @param function
	 * @throws Exception
	 */
	protected CirMutation(MutaFunction function) throws Exception {
		if(function == null)
			throw new IllegalArgumentException("Invalid function");
		else {
			this.function = function;
			this.parameters = new ArrayList<Object>();
		}
	}
	protected void add_parameter(Object parameter) throws Exception {
		if(parameter == null)
			throw new IllegalArgumentException("Invalid parameter");
		else
			this.parameters.add(parameter);
	}
	
	/* getters */
	/**
	 * @return the function that defines the mutation in CIR
	 */
	public MutaFunction get_function() {
		return this.function;
	}
	/**
	 * @return the location in which the mutation is seeded
	 */
	public CirNode get_location() {
		return (CirNode) this.parameters.get(0);
	}
	/**
	 * @return the number of parameters in the mutation
	 */
	public int number_of_parameters() {
		return this.parameters.size();
	}
	/**
	 * @return the parameters used to refine the mutation
	 */
	public Iterable<Object> get_parameters() {
		return this.parameters;
	}
	/**
	 * @param k
	 * @return the kth parameter that defines the cir-mutation
	 * @throws IndexOutOfBoundsException
	 */
	public Object get_parameter(int k) throws IndexOutOfBoundsException {
		return this.parameters.get(k);
	}
	/**
	 * @param parameter
	 * @return the typed string of parameter
	 */
	private String parameter2string(Object parameter) {
		if(parameter == null) {
			return "";
		}
		else if(parameter instanceof Boolean) {
			return "bool@" + parameter;
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
			return "string@" + parameter.toString();
		}
		else if(parameter instanceof COperator) {
			return "oprt@" + parameter.toString();
		}
		else if(parameter instanceof CirNode) {
			return "cir@" + ((CirNode) parameter).get_node_id();
		}
		else {
			return null;
		}
	}
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(this.function.toString());
		for(Object parameter : this.parameters) {
			buffer.append(" ").append(this.parameter2string(parameter));
		}
		return buffer.toString();
	}
	/**
	 * @param tree
	 * @param text
	 * @return the parameter translated from the text based on CIR-tree
	 * @throws Exception
	 */
	private static Object string2parameter(CirTree tree, String text) throws Exception {
		if(text == null || text.isBlank()) {
			return null;
		}
		else {
			int index = text.indexOf('@');
			String title = text.substring(0, index).strip();
			String value = text.substring(index + 1).strip();
			if(title.equals("bool")) {
				return Boolean.valueOf(Boolean.TRUE.toString().equals(value));
			}
			else if(title.equals("char")) {
				return Character.valueOf((char) Integer.parseInt(value));
			}
			else if(title.equals("short")) {
				return Short.valueOf((short) Integer.parseInt(value));
			}
			else if(title.equals("int")) {
				return Integer.parseInt(value);
			}
			else if(title.equals("long")) {
				return Long.parseLong(value);
			}
			else if(title.equals("float")) {
				return Float.parseFloat(value);
			}
			else if(title.equals("double")) {
				return Double.parseDouble(value);
			}
			else if(title.equals("string")) {
				return value;
			}
			else if(title.equals("oprt")) {
				return COperator.valueOf(value);
			}
			else if(title.equals("cir")) {
				return tree.get_node(Integer.parseInt(value));
			}
			else {
				throw new IllegalArgumentException("Invalid type: " + title);
			}
		}
	}
	/**
	 * @param tree
	 * @param line
	 * @return the cir-mutation parsed from string-line and cir-tree base.
	 * @throws Exception
	 */
	public static CirMutation parse(CirTree tree, String line) throws Exception {
		if(line == null || line.isBlank()) {
			return null;
		}
		else {
			String[] items = line.strip().split(" ");
			MutaFunction function = MutaFunction.valueOf(items[0].strip());
			CirMutation mutation = new CirMutation(function);
			for(int k = 1; k < items.length; k++) {
				if(!items[k].isBlank()) {
					mutation.parameters.add(string2parameter(tree, items[k]));
				}
			}
			return mutation;
		}
	}
	
}
