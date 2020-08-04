package com.jcsa.jcparse.parse.parser1;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.AstTypeName;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConditionalExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstParanthExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstSizeofExpression;
import com.jcsa.jcparse.lang.ctype.CEnumerator;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.scope.CEnumeratorName;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.text.CLocation;

/**
 * To evaluate the value of const-expression. A const expression is assumed to
 * involve constant (char|int|real) and other arithmetic operators. <br>
 * <br>
 * Errors could be thrown if the const-expression is invalid. For example, a
 * const expression contains <code>AstLiteral</code> or
 * <code>AstIdExpression</code>.<br>
 * <br>
 * This evaluator is also responsible for the determination of types for each
 * sub-expression among the const expression node.<br>
 * 
 * @author yukimula
 *
 */
public class CConstantEvaluator {
	// components
	/** sequence to evaluate expression nodes **/
	protected Stack<AstExpression> stack;
	/** queue to schedule the sequence for solving **/
	protected Queue<AstExpression> queue;
	/**
	 * map from node to its solution (constant)
	 */
	protected Map<AstExpression, CConstant> solutions;
	/** template to compute the sizeof operator **/
	protected CRunTemplate template;

	// constructor
	/**
	 * constructor
	 */
	public CConstantEvaluator(CRunTemplate template) {
		if (template == null)
			throw new IllegalArgumentException("Invalid template: null");

		this.template = template;
		this.stack = new Stack<AstExpression>();
		this.queue = new LinkedList<AstExpression>();
		this.solutions = new HashMap<AstExpression, CConstant>();
	}

	// solve the const-expression
	/**
	 * evaluate the value of const-expression
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	public CConstant evaluate(AstConstExpression expr) throws Exception {
		if (expr == null)
			throw new IllegalArgumentException("Invalid expr: null");
		else
			return this.solve(expr);
	}

	// solve methods
	/**
	 * update the stack in which expression will be solved
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	protected boolean schedule(AstConstExpression expr) throws Exception {
		/* initialization */
		queue.clear();
		stack.clear();
		queue.add(expr.get_expression());

		/* schedule the nodes in expression */
		while (!queue.isEmpty()) {
			AstExpression node = queue.poll();

			int n = node.number_of_children();
			for (int i = 0; i < n; i++) {
				AstNode child = node.get_child(i);
				if (child instanceof AstExpression)
					queue.add((AstExpression) child);
			}

			stack.push(node);
		}

		/* return */ return true;
	}

	/**
	 * evaluate the value of expression (when its children have been solved)
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	protected CConstant eval_next(AstExpression expr) throws Exception {
		CConstant constant;
		if (expr == null)
			throw new IllegalArgumentException("Invalid expr: null");
		else if (expr instanceof AstIdExpression)
			constant = this.eval((AstIdExpression) expr);
		else if (expr instanceof AstConstant)
			constant = this.eval((AstConstant) expr);
		else if (expr instanceof AstArithBinaryExpression)
			constant = this.eval((AstArithBinaryExpression) expr);
		else if (expr instanceof AstArithUnaryExpression)
			constant = this.eval((AstArithUnaryExpression) expr);
		else if (expr instanceof AstBitwiseBinaryExpression)
			constant = this.eval((AstBitwiseBinaryExpression) expr);
		else if (expr instanceof AstBitwiseUnaryExpression)
			constant = this.eval((AstBitwiseUnaryExpression) expr);
		else if (expr instanceof AstShiftBinaryExpression)
			constant = this.eval((AstShiftBinaryExpression) expr);
		else if (expr instanceof AstLogicBinaryExpression)
			constant = this.eval((AstLogicBinaryExpression) expr);
		else if (expr instanceof AstLogicUnaryExpression)
			constant = this.eval((AstLogicUnaryExpression) expr);
		else if (expr instanceof AstRelationExpression)
			constant = this.eval((AstRelationExpression) expr);
		else if (expr instanceof AstSizeofExpression)
			constant = this.eval((AstSizeofExpression) expr);
		else if (expr instanceof AstParanthExpression)
			constant = this.eval((AstParanthExpression) expr);
		else if (expr instanceof AstConditionalExpression)
			constant = this.eval((AstConditionalExpression) expr);
		else
			throw new IllegalArgumentException("Invalid const-expression: " + expr.getClass().getSimpleName()
					+ "\n\tAt line " + this.line_of(expr) + " : \"" + this.code_of(expr) + "\"");
		return constant;
	}

	/**
	 * compute the constant of expression
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	protected CConstant solve(AstConstExpression expr) throws Exception {
		/* initialization */
		this.schedule(expr);
		CConstant constant = null;

		/* solve the constant for this expression */
		this.solutions.clear();
		while (!stack.empty()) {
			AstExpression next = stack.pop();
			constant = this.eval_next(next);
			this.solutions.put(next, constant);
		}
		this.solutions.clear();

		/* return and validation */
		if (constant == null)
			throw new RuntimeException(
					"Unable to evaluate: at line " + this.line_of(expr) + "\n\t\"" + this.code_of(expr) + "\"");
		else
			return constant;
	}

	// basic expression
	/**
	 * enumerator as constant
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	protected CConstant eval(AstIdExpression expr) throws Exception {
		CName cname = expr.get_cname();
		if (cname instanceof CEnumeratorName) {
			CEnumerator enumerator = ((CEnumeratorName) cname).get_enumerator();
			CConstant constant = new CConstant();
			constant.set_int(enumerator.get_value());
			return constant;
		} else
			throw new RuntimeException(
					"At line " + this.line_of(expr) + " : \"" + this.code_of(expr) + "\"\n\tNot a constant!");
	}

	/**
	 * get constant from node
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	protected CConstant eval(AstConstant expr) throws Exception {
		return expr.get_constant();
	}

	// arithmetic expression
	protected CConstant eval(AstArithBinaryExpression expr) throws Exception {
		AstExpression E1 = expr.get_loperand();
		AstExpression E2 = expr.get_roperand();
		CConstant x = this.get_solution(E1);
		CConstant y = this.get_solution(E2);

		boolean is_real = false;
		switch (x.get_type().get_tag()) {
		case c_float:
		case c_double:
		case c_ldouble:
			is_real = true;
		default:
			break;
		}
		switch (y.get_type().get_tag()) {
		case c_float:
		case c_double:
		case c_ldouble:
			is_real = true;
		default:
			break;
		}

		if (is_real) {
			double xval = this.cast_to_real(x);
			double yval = this.cast_to_real(y);

			double result;
			switch (expr.get_operator().get_operator()) {
			case arith_add:
				result = xval + yval;
				break;
			case arith_sub:
				result = xval - yval;
				break;
			case arith_mul:
				result = xval * yval;
				break;
			case arith_div:
				result = xval / yval;
				break;
			default:
				throw new RuntimeException("Invalid operator at line " + this.line_of(expr) + " : \""
						+ this.code_of(expr) + "\"\n\tUnable to be applied on real-evaluation.");
			}

			CConstant constant = new CConstant();
			constant.set_double(result);
			return constant;
		} else {
			long xval = this.cast_to_int(x);
			long yval = this.cast_to_int(y);

			long result;
			switch (expr.get_operator().get_operator()) {
			case arith_add:
				result = xval + yval;
				break;
			case arith_sub:
				result = xval - yval;
				break;
			case arith_mul:
				result = xval * yval;
				break;
			case arith_div:
				result = xval / yval;
				break;
			case arith_mod:
				result = xval % yval;
				break;
			default:
				throw new RuntimeException("Invalid operator at line " + this.line_of(expr) + " : \""
						+ this.code_of(expr) + "\"\n\tUnable to be applied on real-evaluation.");
			}

			CConstant constant = new CConstant();
			constant.set_int((int) result);
			return constant;
		}
	}

	protected CConstant eval(AstArithUnaryExpression expr) throws Exception {
		AstExpression E = expr.get_operand();
		CConstant x = this.get_solution(E);

		if (expr.get_operator().get_operator() == COperator.negative) {
			CConstant y = new CConstant();
			switch (x.get_type().get_tag()) {
			case c_char:
			case c_uchar:
				char ch = x.get_char();
				y.set_int(-ch);
				break;
			case c_int:
			case c_uint:
				int iv = x.get_integer();
				y.set_int(-iv);
				break;
			case c_long:
			case c_ulong:
				long lv = x.get_long();
				y.set_long(-lv);
				break;
			case c_llong:
			case c_ullong:
				long llv = x.get_long();
				y.set_llong(-llv);
				break;
			case c_float:
				float fv = x.get_float();
				y.set_float(-fv);
				break;
			case c_double:
				double dv = x.get_double();
				y.set_double(-dv);
				break;
			case c_ldouble:
				double ldv = x.get_double();
				y.set_double(-ldv);
				break;
			default:
				throw new IllegalArgumentException(
						"Unable to evaluate \"" + this.code_of(expr) + "\" at line " + this.line_of(expr));
			}
			return y;
		} else
			return x;
	}

	// bitwise expression
	protected CConstant eval(AstBitwiseBinaryExpression expr) throws Exception {
		AstExpression E1 = expr.get_loperand();
		AstExpression E2 = expr.get_roperand();
		CConstant x = this.get_solution(E1);
		CConstant y = this.get_solution(E2);
		long xval = this.cast_to_int(x);
		long yval = this.cast_to_int(y);

		long result;
		switch (expr.get_operator().get_operator()) {
		case bit_and:
			result = xval & yval;
			break;
		case bit_or:
			result = xval | yval;
			break;
		case bit_xor:
			result = xval ^ yval;
			break;
		default:
			throw new RuntimeException(
					"Unable to compute \"" + this.code_of(expr) + "\" at line " + this.line_of(expr));
		}

		CConstant constant = new CConstant();
		constant.set_int((int) result);
		return constant;
	}

	protected CConstant eval(AstBitwiseUnaryExpression expr) throws Exception {
		AstExpression E = expr.get_operand();
		CConstant x = this.get_solution(E);
		long xval = this.cast_to_int(x);

		CConstant y = new CConstant();
		switch (x.get_type().get_tag()) {
		case c_char:
		case c_uchar:
		case c_int:
			y.set_int((int) xval);
			break;
		case c_uint:
			y.set_uint((int) xval);
			break;
		case c_long:
			y.set_long(xval);
			break;
		case c_ulong:
			y.set_ulong(xval);
			break;
		case c_llong:
			y.set_llong(xval);
			break;
		case c_ullong:
			y.set_ullong(xval);
			break;
		default:
			throw new IllegalArgumentException(
					"Unable to evaluate \"" + this.code_of(expr) + "\" at line " + this.line_of(expr));
		}
		return y;
	}

	// shift expression
	protected CConstant eval(AstShiftBinaryExpression expr) throws Exception {
		AstExpression E1 = expr.get_loperand();
		AstExpression E2 = expr.get_roperand();
		CConstant x = this.get_solution(E1);
		CConstant y = this.get_solution(E2);
		long xval = this.cast_to_int(x);
		long yval = this.cast_to_int(y);

		long result;
		switch (expr.get_operator().get_operator()) {
		case left_shift:
			result = (xval << yval);
			break;
		case righ_shift:
			result = (xval >> yval);
			break;
		default:
			throw new RuntimeException(
					"Unable to compute \"" + this.code_of(expr) + "\" at line " + this.line_of(expr));
		}

		CConstant constant = new CConstant();
		constant.set_int((int) result);
		return constant;
	}

	// logical expression
	protected CConstant eval(AstLogicBinaryExpression expr) throws Exception {
		AstExpression E1 = expr.get_loperand();
		AstExpression E2 = expr.get_roperand();
		CConstant x = this.get_solution(E1);
		CConstant y = this.get_solution(E2);
		boolean xval = this.cast_to_bool(x);
		boolean yval = this.cast_to_bool(y);

		boolean result;
		switch (expr.get_operator().get_operator()) {
		case logic_and:
			result = xval && yval;
			break;
		case logic_or:
			result = xval || yval;
			break;
		default:
			throw new RuntimeException(
					"Unable to compute \"" + this.code_of(expr) + "\" at line " + this.line_of(expr));
		}

		CConstant constant = new CConstant();
		if (result)
			constant.set_int(1);
		else
			constant.set_int(0);
		return constant;
	}

	protected CConstant eval(AstLogicUnaryExpression expr) throws Exception {
		AstExpression E = expr.get_operand();
		CConstant x = this.get_solution(E);
		boolean xval = this.cast_to_bool(x);

		int result = xval ? 1 : 0;
		CConstant constant = new CConstant();
		constant.set_int(result);
		return constant;
	}

	// relational expression
	protected CConstant eval(AstRelationExpression expr) throws Exception {
		AstExpression E1 = expr.get_loperand();
		AstExpression E2 = expr.get_roperand();
		CConstant x = this.get_solution(E1);
		CConstant y = this.get_solution(E2);
		double xval = this.cast_to_real(x);
		double yval = this.cast_to_real(y);

		boolean result;
		switch (expr.get_operator().get_operator()) {
		case greater_tn:
			result = (xval > yval);
			break;
		case greater_eq:
			result = (xval >= yval);
			break;
		case smaller_tn:
			result = (xval <= yval);
			break;
		case smaller_eq:
			result = (xval < yval);
			break;
		case equal_with:
			result = (xval == yval);
			break;
		case not_equals:
			result = (xval != yval);
			break;
		default:
			throw new RuntimeException(
					"Unable to compute \"" + this.code_of(expr) + "\" at line " + this.line_of(expr));
		}

		CConstant constant = new CConstant();
		if (result)
			constant.set_int(1);
		else
			constant.set_int(0);
		return constant;
	}

	// sizeof expression
	protected CConstant eval(AstSizeofExpression expr) throws Exception {
		int size;
		if (expr.is_expression()) {
			AstExpression child = expr.get_expression();
			size = this.template.sizeof(child.get_value_type());
			// size = CSizeofBase.Sizeof(child.get_value_type());
		} else {
			AstTypeName typename = expr.get_typename();
			size = this.template.sizeof(typename.get_type());
			// size = CSizeofBase.Sizeof(typename.get_type());
		}

		CConstant constant = new CConstant();
		constant.set_int(size);
		return constant;
	}

	// paranth expression
	protected CConstant eval(AstParanthExpression expr) throws Exception {
		AstExpression child = expr.get_sub_expression();
		return this.get_solution(child);
	}

	// conditional expression
	protected CConstant eval(AstConditionalExpression expr) throws Exception {
		AstExpression CE = expr.get_condition();
		AstExpression TE = expr.get_true_branch();
		AstExpression FE = expr.get_false_branch();
		CConstant cval = this.get_solution(CE);
		CConstant tval = this.get_solution(TE);
		CConstant fval = this.get_solution(FE);

		boolean c = this.cast_to_bool(cval);
		if (c)
			return tval;
		else
			return fval;

	}

	// value getter
	/**
	 * get the value as boolean
	 * 
	 * @param x
	 * @return
	 * @throws Exception
	 */
	private boolean cast_to_bool(CConstant x) throws Exception {
		switch (x.get_type().get_tag()) {
		case c_char:
		case c_uchar:
			return (x.get_char() != '\0');
		case c_int:
		case c_uint:
			return (x.get_integer() != 0);
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:
			return (x.get_long() != 0L);
		case c_float:
			return (x.get_float() != 0.0F);
		case c_double:
		case c_ldouble:
			return (x.get_double() != 0.0);
		default:
			throw new IllegalArgumentException("Unable to cast to integer: " + x);
		}
	}

	/**
	 * get the value of constant as integer
	 * 
	 * @param x
	 * @return
	 * @throws Exception
	 */
	private long cast_to_int(CConstant x) throws Exception {
		switch (x.get_type().get_tag()) {
		case c_char:
		case c_uchar:
			char ch = x.get_char();
			return ch;
		case c_int:
		case c_uint:
			int val = x.get_integer();
			return val;
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:
			return x.get_long();
		default:
			throw new IllegalArgumentException("Unable to cast to integer: " + x);
		}
	}

	/**
	 * get the value of constant as real
	 * 
	 * @param x
	 * @return
	 * @throws Exception
	 */
	private double cast_to_real(CConstant x) throws Exception {
		switch (x.get_type().get_tag()) {
		case c_char:
		case c_uchar:
			char ch = x.get_char();
			return ch;
		case c_int:
		case c_uint:
			int val = x.get_integer();
			return val;
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:
			return x.get_long();
		case c_float:
			float fval = x.get_float();
			return fval;
		case c_double:
		case c_ldouble:
			double dval = x.get_double();
			return dval;
		default:
			throw new IllegalArgumentException("Unable to cast to integer: " + x);
		}
	}

	/**
	 * get the solution from map based on its node
	 * 
	 * @param e
	 * @return
	 * @throws Exception
	 */
	private CConstant get_solution(AstExpression expr) throws Exception {
		if (expr == null)
			throw new IllegalArgumentException("Invalid expr: null");
		else if (!solutions.containsKey(expr))
			throw new IllegalArgumentException("Undefined solutions for: " + expr);
		else
			return solutions.get(expr);
	}

	// code-getters
	/**
	 * get the line of the node in source text
	 * 
	 * @param node
	 * @return
	 * @throws Exception
	 */
	private int line_of(AstNode node) throws Exception {
		CLocation loc = node.get_location();
		return loc.get_source().line_of(loc.get_bias());
	}

	/***
	 * get the code of the node in source text
	 * 
	 * @param node
	 * @return
	 * @throws Exception
	 */
	private String code_of(AstNode node) throws Exception {
		CLocation loc = node.get_location();
		return loc.read();
	}
}
