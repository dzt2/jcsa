package com.jcsa.jcparse.test.inst;

import java.util.List;
import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * Each token in instrumental path refers to a CirNode location with some
 * specified value and timeline tags.
 * 
 * @author yukimula
 *
 */
public class InstrumentalUnit {
	
	/* definitions */
	/** the type of the instrument token **/
	private InstrumentalType type;
	/** the location where the token was injected **/
	private CirNode location;
	/** the Java object as the value of the unit **/
	private Object value;
	/** see constructor methods as following **/
	private InstrumentalUnit() { }
	
	/* constructors */
	protected static InstrumentalUnit beg_stmt(CirStatement statement) throws Exception {
		if(statement == null)
			throw new IllegalArgumentException("Invalid statement: null");
		else {
			InstrumentalUnit token = new InstrumentalUnit();
			token.type = InstrumentalType.beg_stmt;
			token.location = statement;
			token.value = null;
			return token;
		}
	}
	protected static InstrumentalUnit end_stmt(CirStatement statement) throws Exception {
		if(statement == null)
			throw new IllegalArgumentException("Invalid statement: null");
		else {
			InstrumentalUnit token = new InstrumentalUnit();
			token.type = InstrumentalType.end_stmt;
			token.location = statement;
			token.value = null;
			return token;
		}
	}
	protected static InstrumentalUnit evaluate(CRunTemplate template,
			CirExpression expression, byte[] value) throws Exception {
		if(expression == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else if(value == null || value.length == 0)
			throw new IllegalArgumentException("Invalid value as null");
		else {
			InstrumentalUnit token = new InstrumentalUnit();
			token.type = InstrumentalType.evaluate;
			token.location = expression;
			token.value = template.generate_value(expression.get_data_type(), value);
			return token;
		}
	}
	
	/* getters */
	/**
	 * @return the type of the instrumental line
	 */
	public InstrumentalType get_type() { return this.type; }
	/**
	 * @return the location where the instrument is seeded
	 */
	public CirNode get_location() { return this.location; }
	/**
	 * @return whether the unit defines any value for its expression.
	 */
	public boolean has_value() { return this.value != null; }
	/**
	 * @return Boolean|Character|Short|Integer|Long|Float|Double|Complex|byte[]
	 */
	public Object get_value() { return this.value; }
	@Override
	public String toString() {
		return this.value.toString();
	}
	
	public static List<InstrumentalUnit> parse(CRunTemplate template,
			CirTree cir_tree, List<InstrumentalLine> lines) throws Exception {
		return InstrumentalUnitParser.parse(template, cir_tree, lines);
	}
	
}
