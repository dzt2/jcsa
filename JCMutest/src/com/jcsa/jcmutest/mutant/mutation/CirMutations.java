package com.jcsa.jcmutest.mutant.mutation;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * It provides interfaces to create cir-mutations.
 * 
 * @author yukimula
 *
 */
public class CirMutations {
	
	/**
	 * @param statement
	 * @param loop_time
	 * @return trap_on_stmt(statement, int)
	 * @throws Exception
	 */
	public static CirMutation trap_on_stmt(CirStatement 
			statement, int loop_time) throws Exception {
		CirMutation mutation = new 
					CirMutation(MutaFunction.trap_on_stmt);
		mutation.add_parameter(statement);
		mutation.add_parameter(Integer.valueOf(loop_time));
		return mutation;
	}
	
	/**
	 * @param expression
	 * @param parameter
	 * @return trap_on_equal(expression, bool|long|double|string|cir_expression)
	 * @throws Exception
	 */
	public static CirMutation trap_on_equal(CirExpression 
			expression, boolean parameter) throws Exception {
		CirMutation mutation = new 
				CirMutation(MutaFunction.trap_on_equal);
		mutation.add_parameter(expression);
		mutation.add_parameter(Boolean.valueOf(parameter));
		return mutation;
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return trap_on_equal(expression, bool|long|double|string|cir_expression)
	 * @throws Exception
	 */
	public static CirMutation trap_on_equal(CirExpression 
			expression, long parameter) throws Exception {
		CirMutation mutation = new 
				CirMutation(MutaFunction.trap_on_equal);
		mutation.add_parameter(expression);
		mutation.add_parameter(Long.valueOf(parameter));
		return mutation;
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return trap_on_equal(expression, bool|long|double|string|cir_expression)
	 * @throws Exception
	 */
	public static CirMutation trap_on_equal(CirExpression 
			expression, String parameter) throws Exception {
		CirMutation mutation = new 
				CirMutation(MutaFunction.trap_on_equal);
		mutation.add_parameter(expression);
		mutation.add_parameter(parameter);
		return mutation;
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return trap_on_equal(expression, bool|long|double|string|cir_expression)
	 * @throws Exception
	 */
	public static CirMutation trap_on_equal(CirExpression 
			expression, CirExpression parameter) throws Exception {
		CirMutation mutation = new 
				CirMutation(MutaFunction.trap_on_equal);
		mutation.add_parameter(expression);
		mutation.add_parameter(parameter);
		return mutation;
	}
	
	/**
	 * @param expression
	 * @param parameter
	 * @return trap_on_diff(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation trap_on_diff(CirExpression 
			expression, long parameter) throws Exception {
		CirMutation mutation = new 
				CirMutation(MutaFunction.trap_on_diff);
		mutation.add_parameter(expression);
		mutation.add_parameter(Long.valueOf(parameter));
		return mutation;
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return trap_on_diff(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation trap_on_diff(CirExpression 
			expression, double parameter) throws Exception {
		CirMutation mutation = new 
				CirMutation(MutaFunction.trap_on_diff);
		mutation.add_parameter(expression);
		mutation.add_parameter(Double.valueOf(parameter));
		return mutation;
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return trap_on_diff(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation trap_on_diff(CirExpression 
			expression, String parameter) throws Exception {
		CirMutation mutation = new 
				CirMutation(MutaFunction.trap_on_diff);
		mutation.add_parameter(expression);
		mutation.add_parameter(parameter);
		return mutation;
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return trap_on_diff(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation trap_on_diff(CirExpression 
			expression, CirExpression parameter) throws Exception {
		CirMutation mutation = new 
				CirMutation(MutaFunction.trap_on_diff);
		mutation.add_parameter(expression);
		mutation.add_parameter(parameter);
		return mutation;
	}
	
	/**
	 * @param expression
	 * @param parameter
	 * @return trap_on_great(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation trap_on_great(CirExpression 
			expression, long parameter) throws Exception {
		CirMutation mutation = new 
				CirMutation(MutaFunction.trap_on_great);
		mutation.add_parameter(expression);
		mutation.add_parameter(Long.valueOf(parameter));
		return mutation;
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return trap_on_great(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation trap_on_great(CirExpression 
			expression, double parameter) throws Exception {
		CirMutation mutation = new 
				CirMutation(MutaFunction.trap_on_great);
		mutation.add_parameter(expression);
		mutation.add_parameter(Double.valueOf(parameter));
		return mutation;
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return trap_on_great(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation trap_on_great(CirExpression 
			expression, String parameter) throws Exception {
		CirMutation mutation = new 
				CirMutation(MutaFunction.trap_on_great);
		mutation.add_parameter(expression);
		mutation.add_parameter(parameter);
		return mutation;
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return trap_on_great(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation trap_on_great(CirExpression 
			expression, CirExpression parameter) throws Exception {
		CirMutation mutation = new 
				CirMutation(MutaFunction.trap_on_great);
		mutation.add_parameter(expression);
		mutation.add_parameter(parameter);
		return mutation;
	}
	
	/**
	 * @param expression
	 * @param parameter
	 * @return trap_on_small(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation trap_on_small(CirExpression 
			expression, long parameter) throws Exception {
		CirMutation mutation = new 
				CirMutation(MutaFunction.trap_on_small);
		mutation.add_parameter(expression);
		mutation.add_parameter(Long.valueOf(parameter));
		return mutation;
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return trap_on_small(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation trap_on_small(CirExpression 
			expression, double parameter) throws Exception {
		CirMutation mutation = new 
				CirMutation(MutaFunction.trap_on_small);
		mutation.add_parameter(expression);
		mutation.add_parameter(Double.valueOf(parameter));
		return mutation;
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return trap_on_small(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation trap_on_small(CirExpression 
			expression, String parameter) throws Exception {
		CirMutation mutation = new 
				CirMutation(MutaFunction.trap_on_small);
		mutation.add_parameter(expression);
		mutation.add_parameter(parameter);
		return mutation;
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return trap_on_small(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation trap_on_small(CirExpression 
			expression, CirExpression parameter) throws Exception {
		CirMutation mutation = new 
				CirMutation(MutaFunction.trap_on_small);
		mutation.add_parameter(expression);
		mutation.add_parameter(parameter);
		return mutation;
	}
	
	/**
	 * @param source
	 * @param target
	 * @return set_goto_stmt(source, target)
	 * @throws Exception
	 */
	public static CirMutation set_goto_stmt(CirStatement 
			source, CirStatement target) throws Exception {
		CirMutation mutation = new CirMutation(MutaFunction.set_goto_stmt);
		mutation.add_parameter(source);
		mutation.add_parameter(target);
		return mutation;
	}
	
	/**
	 * @param statement
	 * @return delete_stmt(statement)
	 * @throws Exception
	 */
	public static CirMutation delete_stmt(
			CirStatement statement) throws Exception {
		CirMutation mutation = new 
				CirMutation(MutaFunction.delete_stmt);
		mutation.add_parameter(statement);
		return mutation;
	}
	
	/**
	 * @param expression
	 * @param parameter
	 * @return set_expression(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation set_expression(CirExpression 
			expression, boolean parameter) throws Exception {
		CirMutation mutation = new CirMutation(MutaFunction.set_expression);
		mutation.add_parameter(expression);
		mutation.add_parameter(Boolean.valueOf(parameter));
		return mutation;
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return set_expression(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation set_expression(CirExpression 
			expression, long parameter) throws Exception {
		CirMutation mutation = new CirMutation(MutaFunction.set_expression);
		mutation.add_parameter(expression);
		mutation.add_parameter(Long.valueOf(parameter));
		return mutation;
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return set_expression(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation set_expression(CirExpression 
			expression, double parameter) throws Exception {
		CirMutation mutation = new CirMutation(MutaFunction.set_expression);
		mutation.add_parameter(expression);
		mutation.add_parameter(Double.valueOf(parameter));
		return mutation;
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return set_expression(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation set_expression(CirExpression 
			expression, String parameter) throws Exception {
		CirMutation mutation = new CirMutation(MutaFunction.set_expression);
		mutation.add_parameter(expression);
		mutation.add_parameter(parameter);
		return mutation;
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return set_expression(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation set_expression(CirExpression 
			expression, CirExpression parameter) throws Exception {
		CirMutation mutation = new CirMutation(MutaFunction.set_expression);
		mutation.add_parameter(expression);
		mutation.add_parameter(parameter);
		return mutation;
	}
	
	/**
	 * @param expression
	 * @param parameter
	 * @return set_data_state(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation set_data_state(CirExpression 
			expression, boolean parameter, int time) throws Exception {
		CirMutation mutation = new 
				CirMutation(MutaFunction.set_data_state);
		mutation.add_parameter(expression);
		mutation.add_parameter(Boolean.valueOf(parameter));
		mutation.add_parameter(Integer.valueOf(time));
		return mutation;
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return set_data_state(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation set_data_state(CirExpression 
			expression, long parameter, int time) throws Exception {
		CirMutation mutation = new 
				CirMutation(MutaFunction.set_data_state);
		mutation.add_parameter(expression);
		mutation.add_parameter(Long.valueOf(parameter));
		mutation.add_parameter(Integer.valueOf(time));
		return mutation;
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return set_data_state(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation set_data_state(CirExpression 
			expression, double parameter, int time) throws Exception {
		CirMutation mutation = new 
				CirMutation(MutaFunction.set_data_state);
		mutation.add_parameter(expression);
		mutation.add_parameter(Double.valueOf(parameter));
		mutation.add_parameter(Integer.valueOf(time));
		return mutation;
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return set_data_state(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation set_data_state(CirExpression 
			expression, String parameter, int time) throws Exception {
		CirMutation mutation = new 
				CirMutation(MutaFunction.set_data_state);
		mutation.add_parameter(expression);
		mutation.add_parameter(parameter);
		mutation.add_parameter(Integer.valueOf(time));
		return mutation;
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return set_data_state(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation set_data_state(CirExpression 
			expression, CirExpression parameter, int time) throws Exception {
		CirMutation mutation = new 
				CirMutation(MutaFunction.set_data_state);
		mutation.add_parameter(expression);
		mutation.add_parameter(parameter);
		mutation.add_parameter(Integer.valueOf(time));
		return mutation;
	}
	
	/**
	 * @param expression
	 * @param parameter
	 * @return inc_expression(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation inc_expression(CirExpression 
			expression, long parameter) throws Exception {
		CirMutation mutation = new CirMutation(MutaFunction.inc_expression);
		mutation.add_parameter(expression);
		mutation.add_parameter(Long.valueOf(parameter));
		return mutation;
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return inc_expression(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation inc_expression(CirExpression 
			expression, double parameter) throws Exception {
		CirMutation mutation = new CirMutation(MutaFunction.inc_expression);
		mutation.add_parameter(expression);
		mutation.add_parameter(Double.valueOf(parameter));
		return mutation;
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return inc_expression(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation inc_expression(CirExpression 
			expression, String parameter) throws Exception {
		CirMutation mutation = new CirMutation(MutaFunction.inc_expression);
		mutation.add_parameter(expression);
		mutation.add_parameter(parameter);
		return mutation;
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return inc_expression(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation inc_expression(CirExpression 
			expression, CirExpression parameter) throws Exception {
		CirMutation mutation = new CirMutation(MutaFunction.inc_expression);
		mutation.add_parameter(expression);
		mutation.add_parameter(parameter);
		return mutation;
	}
	
	/**
	 * @param expression
	 * @param parameter
	 * @return mul_expression(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation mul_expression(CirExpression 
			expression, long parameter) throws Exception {
		CirMutation mutation = new CirMutation(MutaFunction.mul_expression);
		mutation.add_parameter(expression);
		mutation.add_parameter(Long.valueOf(parameter));
		return mutation;
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return mul_expression(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation mul_expression(CirExpression 
			expression, double parameter) throws Exception {
		CirMutation mutation = new CirMutation(MutaFunction.mul_expression);
		mutation.add_parameter(expression);
		mutation.add_parameter(Double.valueOf(parameter));
		return mutation;
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return mul_expression(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation mul_expression(CirExpression 
			expression, String parameter) throws Exception {
		CirMutation mutation = new CirMutation(MutaFunction.mul_expression);
		mutation.add_parameter(expression);
		mutation.add_parameter(parameter);
		return mutation;
	}
	/**
	 * @param expression
	 * @param parameter
	 * @return mul_expression(expression, parameter)
	 * @throws Exception
	 */
	public static CirMutation mul_expression(CirExpression 
			expression, CirExpression parameter) throws Exception {
		CirMutation mutation = new CirMutation(MutaFunction.mul_expression);
		mutation.add_parameter(expression);
		mutation.add_parameter(parameter);
		return mutation;
	}
	
	/**
	 * @param expression
	 * @param operator
	 * @return set_operator(expression, operator)
	 * @throws Exception
	 */
	public static CirMutation set_operator(CirExpression 
			expression, COperator operator) throws Exception {
		CirMutation mutation = new CirMutation(MutaFunction.set_operator);
		mutation.add_parameter(expression);
		mutation.add_parameter(operator);
		return mutation;
	}
	/**
	 * @param expression
	 * @param operator
	 * @return trap_operator(expression, operator)
	 * @throws Exception
	 */
	public static CirMutation trap_operator(CirExpression 
			expression, COperator operator) throws Exception {
		CirMutation mutation = new CirMutation(
					MutaFunction.trap_operator);
		mutation.add_parameter(expression);
		mutation.add_parameter(operator);
		return mutation;
	}
	/**
	 * @param expression
	 * @param operator
	 * @return del_operator(expression)
	 * @throws Exception
	 */
	public static CirMutation del_operator(CirExpression expression) throws Exception {
		CirMutation mutation = new CirMutation(
					MutaFunction.del_operator);
		mutation.add_parameter(expression);
		return mutation;
	}
	
}
