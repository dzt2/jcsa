package __backup__;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * To produce the semantic nodes and links freely.
 * 
 * @author yukimula
 *
 */
public class CirSemanticFactory {
	
	/**
	 * cover(statement)
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	public static CirSemanticNode cover_statement(CirStatement statement) throws Exception {
		return new CirSemanticNode(CirSemanticWord.cover, statement, null);
	}
	/**
	 * repeat(statement, loop_times)
	 * @param statement
	 * @param loop_times
	 * @return
	 * @throws Exception
	 */
	public static CirSemanticNode repeat_statement(CirStatement statement, int loop_times) throws Exception {
		return new CirSemanticNode(CirSemanticWord.repeat, statement, loop_times);
	}
	/**
	 * execute(statement)
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	public static CirSemanticNode execute_statement(CirStatement statement) throws Exception {
		return new CirSemanticNode(CirSemanticWord.execute, statement, null);
	}
	/**
	 * non_execute(statement)
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	public static CirSemanticNode non_execute_statement(CirStatement statement) throws Exception {
		return new CirSemanticNode(CirSemanticWord.non_execute, statement, null);
	}
	/**
	 * expression == value
	 * @param expression
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static CirSemanticNode equal_with(CirExpression expression, Object value) throws Exception {
		return new CirSemanticNode(CirSemanticWord.equal_with, expression, value);
	}
	/**
	 * expression != value
	 * @param expression
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static CirSemanticNode not_equals(CirExpression expression, Object value) throws Exception {
		return new CirSemanticNode(CirSemanticWord.not_equals, expression, value);
	}
	/**
	 * expression > value
	 * @param expression
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static CirSemanticNode greater_tn(CirExpression expression, Object value) throws Exception {
		return new CirSemanticNode(CirSemanticWord.greater_tn, expression, value);
	}
	/**
	 * expression >= value
	 * @param expression
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static CirSemanticNode greater_eq(CirExpression expression, Object value) throws Exception {
		return new CirSemanticNode(CirSemanticWord.greater_eq, expression, value);
	}
	/**
	 * expression < value
	 * @param expression
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static CirSemanticNode smaller_tn(CirExpression expression, Object value) throws Exception {
		return new CirSemanticNode(CirSemanticWord.smaller_tn, expression, value);
	}
	/**
	 * expression <= value
	 * @param expression
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static CirSemanticNode smaller_eq(CirExpression expression, Object value) throws Exception {
		return new CirSemanticNode(CirSemanticWord.smaller_eq, expression, value);
	}
	/**
	 * in_range(expression, domain)
	 * @param expression
	 * @param domain
	 * @return
	 * @throws Exception
	 */
	public static CirSemanticNode in_range(CirExpression expression, Object domain) throws Exception {
		return new CirSemanticNode(CirSemanticWord.in_range, expression, domain);
	}
	/**
	 * not_in_range(expression, domain)
	 * @param expression
	 * @param domain
	 * @return
	 * @throws Exception
	 */
	public static CirSemanticNode not_in_range(CirExpression expression, Object domain) throws Exception {
		return new CirSemanticNode(CirSemanticWord.not_in_range, expression, domain);
	}
	/**
	 * expression & value != 0
	 * @param expression
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static CirSemanticNode bit_intersc(CirExpression expression, Object value) throws Exception {
		return new CirSemanticNode(CirSemanticWord.bit_intersc, expression, value);
	}
	/**
	 * expression & value == 0
	 * @param expression
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static CirSemanticNode bit_exclude(CirExpression expression, Object value) throws Exception {
		return new CirSemanticNode(CirSemanticWord.bit_exclude, expression, value);
	}
	/**
	 * expression & value == value
	 * @param expression
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static CirSemanticNode bit_include(CirExpression expression, Object value) throws Exception {
		return new CirSemanticNode(CirSemanticWord.bit_include, expression, value);
	}
	/**
	 * expression & value != value
	 * @param expression
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static CirSemanticNode bno_include(CirExpression expression, Object value) throws Exception {
		return new CirSemanticNode(CirSemanticWord.bno_include, expression, value);
	}
	/**
	 * expression = {integer} * value
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static CirSemanticNode is_multiply(CirExpression expression, Object value) throws Exception {
		return new CirSemanticNode(CirSemanticWord.is_multiply, expression, value);
	}
	/**
	 * expression = -value
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static CirSemanticNode is_negative(CirExpression expression, Object value) throws Exception {
		return new CirSemanticNode(CirSemanticWord.is_negative, expression, value);
	}
	/**
	 * expression != -value
	 * @param expression
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static CirSemanticNode not_negative(CirExpression expression, Object value) throws Exception {
		return new CirSemanticNode(CirSemanticWord.not_negative, expression, value);
	}
	
	/**
	 * traping(statement)
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	public static CirSemanticNode traping(CirStatement statement) throws Exception {
		return new CirSemanticNode(CirSemanticWord.traping, statement, null);
	}
	/**
	 * expression <== ?
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public static CirSemanticNode chg_val(CirExpression expression) throws Exception {
		return new CirSemanticNode(CirSemanticWord.chg_val, expression, null);
	}
	/**
	 * expression <== value
	 * @param expression
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static CirSemanticNode set_val(CirExpression expression, Object value) throws Exception {
		return new CirSemanticNode(CirSemanticWord.set_val, expression, value);
	}
	/**
	 * expression <== expression + difference
	 * @param expression
	 * @param differece
	 * @return
	 * @throws Exception
	 */
	public static CirSemanticNode inc_val(CirExpression expression, int differece) throws Exception {
		return new CirSemanticNode(CirSemanticWord.inc_val, expression, differece);
	}
	/**
	 * expression <== {-, ~, !} expression
	 * @param expression
	 * @param operator
	 * @return
	 * @throws Exception
	 */
	public static CirSemanticNode neg_val(CirExpression expression, COperator operator) throws Exception {
		return new CirSemanticNode(CirSemanticWord.neg_val, expression, operator);
	}
	/**
	 * construct a single production [cause ==> effect]
	 * @param cause
	 * @param effect
	 * @return
	 * @throws Exception
	 */
	public static CirSemanticLink infer(CirSemanticNode cause, CirSemanticNode effect) throws Exception {
		if(cause == null)
			throw new IllegalArgumentException("Invalid cause: null");
		else if(effect == null)
			throw new IllegalArgumentException("Invalid effect: null");
		else {
			CirSemanticLink link = new CirSemanticLink();
			cause.ou.add(link); 	link.in.add(cause);
			effect.in.add(link);	link.ou.add(effect);
			return link;
		}
	}
	/**
	 * construct a production [n:1]
	 * @param causes
	 * @param effect
	 * @return
	 * @throws Exception
	 */
	public static CirSemanticLink infer(CirSemanticNode[] causes, CirSemanticNode effect) throws Exception {
		if(causes == null || causes.length == 0)
			throw new IllegalArgumentException("Invalid causes: null");
		else if(effect == null)
			throw new IllegalArgumentException("Invalid effect: null");
		else {
			CirSemanticLink link = new CirSemanticLink();
			effect.in.add(link);	link.ou.add(effect);
			for(CirSemanticNode cause : causes) {
				cause.ou.add(link);
				link.in.add(cause);
			}
			return link;
		}
	}
	/**
	 * construct a production [1:n]
	 * @param cause
	 * @param effects
	 * @return
	 * @throws Exception
	 */
	public static CirSemanticLink infer(CirSemanticNode cause, CirSemanticNode[] effects) throws Exception {
		if(cause == null)
			throw new IllegalArgumentException("Invalid cause: null");
		else if(effects == null || effects.length == 0)
			throw new IllegalArgumentException("Invalid effects: null");
		else {
			CirSemanticLink link = new CirSemanticLink();
			cause.ou.add(link); 	link.in.add(cause);
			for(CirSemanticNode effect : effects) {
				effect.in.add(link); 
				link.ou.add(effect);
			}
			return link;
		}
	}
	/**
	 * construct a production [n:m]
	 * @param causes
	 * @param effects
	 * @return
	 * @throws Exception
	 */
	public static CirSemanticLink infer(CirSemanticNode[] causes, CirSemanticNode[] effects) throws Exception {
		if(causes == null || causes.length == 0)
			throw new IllegalArgumentException("Invalid causes: null");
		else if(effects == null || effects.length == 0)
			throw new IllegalArgumentException("Invalid effects: null");
		else {
			CirSemanticLink link = new CirSemanticLink();
			for(CirSemanticNode cause : causes) {
				cause.ou.add(link);  link.in.add(cause);
			}
			for(CirSemanticNode effect : effects) {
				effect.in.add(link); link.ou.add(effect);
			}
			return link;
		}
	}
	
}
