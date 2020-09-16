package com.jcsa.jcmutest.mutant.sec2mutant.util.apis;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecStateError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.cons.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.*;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.refer.*;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.stmt.*;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.uniq.*;


/**
 * It implements the construction of error propagation from one source location
 * to another target location using static analysis w.r.t. specific constraint.
 * 
 * @author yukimula
 *
 */
public abstract class SecErrorPropagator {
	
	/* definitions */
	/** the statement where the target location is in **/
	private CirStatement statement;
	/** the location where the target error is created **/
	private CirNode target_location;
	/** the source error that is propagated to the target **/
	private SecStateError source_error;
	/** mapping from target-errors to their constraints **/
	private Map<SecStateError, SecConstraint> results;
	
	/* constructor */
	public SecErrorPropagator() { }
	
	/* propagation methods */
	/**
	 * @param statement
	 * @param target_location
	 * @param source_error
	 * @return propagation maps from target errors to the constraints required
	 * @throws Exception
	 */
	public Map<SecStateError, SecConstraint> propagate(CirStatement statement,
			CirNode target_location, SecStateError source_error) throws Exception {
		if(statement == null)
			throw new IllegalArgumentException("Invalid statement: null");
		else if(target_location == null)
			throw new IllegalArgumentException("Invalid target_location");
		else if(source_error == null)
			throw new IllegalArgumentException("Invalid source_error");
		else {
			this.statement = statement;
			this.target_location = target_location;
			this.source_error = source_error;
			this.results = new HashMap<SecStateError, SecConstraint>();
			
			if(source_error instanceof SecAddStatementError)
				this.propagate_add_statement((SecAddStatementError) source_error);
			else if(source_error instanceof SecDelStatementError)
				this.propagate_del_statement((SecDelStatementError) source_error);
			else if(source_error instanceof SecSetStatementError)
				this.propagate_set_statement((SecSetStatementError) source_error);
			else if(source_error instanceof SecTrapError)
				this.propagate_trap_error((SecTrapError) source_error);
			else if(source_error instanceof SecNoneError)
				this.propagate_none_error((SecNoneError) source_error);
			else if(source_error instanceof SecSetExpressionError)
				this.propagate_set_expression((SecSetExpressionError) source_error);
			else if(source_error instanceof SecAddExpressionError)
				this.propagate_add_expression((SecAddExpressionError) source_error);
			else if(source_error instanceof SecInsExpressionError)
				this.propagate_ins_expression((SecInsExpressionError) source_error);
			else if(source_error instanceof SecUnyExpressionError)
				this.propagate_uny_expression((SecUnyExpressionError) source_error);
			else if(source_error instanceof SecSetReferenceError)
				this.propagate_set_reference((SecSetReferenceError) source_error);
			else if(source_error instanceof SecAddReferenceError)
				this.propagate_add_reference((SecAddReferenceError) source_error);
			else if(source_error instanceof SecInsReferenceError)
				this.propagate_ins_reference((SecInsReferenceError) source_error);
			else if(source_error instanceof SecUnyReferenceError)
				this.propagate_uny_reference((SecUnyReferenceError) source_error);
			else
				throw new IllegalArgumentException("Unsupport: " + source_error);
			
			return this.results;
		}
	}
	
	/* syntax-directed translation */
	protected abstract void propagate_add_statement(SecAddStatementError error) throws Exception;
	protected abstract void propagate_del_statement(SecDelStatementError error) throws Exception;
	protected abstract void propagate_set_statement(SecSetStatementError error) throws Exception;
	protected abstract void propagate_trap_error(SecTrapError error) throws Exception;
	protected abstract void propagate_none_error(SecNoneError error) throws Exception;
	protected abstract void propagate_set_expression(SecSetExpressionError error) throws Exception;
	protected abstract void propagate_add_expression(SecAddExpressionError error) throws Exception;
	protected abstract void propagate_ins_expression(SecInsExpressionError error) throws Exception;
	protected abstract void propagate_uny_expression(SecUnyExpressionError error) throws Exception;
	protected abstract void propagate_set_reference(SecSetReferenceError error) throws Exception;
	protected abstract void propagate_add_reference(SecAddReferenceError error) throws Exception;
	protected abstract void propagate_ins_reference(SecInsReferenceError error) throws Exception;
	protected abstract void propagate_uny_reference(SecUnyReferenceError error) throws Exception;
	
	/* basic methods */
	/**
	 * report the unsupported exception from source to target errors.
	 * @throws Exception
	 */
	protected void report_unsupported_operation() throws Exception {
		throw new UnsupportedOperationException("Unsupported for:\n"
				+ "\tStatement: " + this.statement.generate_code(true)
				+ "\tTarget: " + this.target_location.generate_code(true)
				+ "\tSource-Error: " + this.source_error.generate_code());
	}
	/**
	 * append the constraint-error pair into the propagation maps
	 * @param constraint
	 * @param target_error
	 * @throws Exception
	 */
	protected void append_propagation_pair(SecConstraint constraint, SecStateError target_error) throws Exception {
		if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint: null");
		else if(target_error == null)
			throw new IllegalArgumentException("Invalid target_error");
		else 
			this.results.put(target_error, constraint);
	}
	/**
	 * @return target_location as expression
	 * @throws Exception
	 */
	protected CirExpression target_expression() throws Exception {
		return (CirExpression) this.target_location;
	}
	/**
	 * @return target_location as reference
	 * @throws Exception
	 */
	protected CirReferExpression target_reference() throws Exception {
		return (CirReferExpression) this.target_location;
	}
	
	/* constraint generators */
	/**
	 * @return TRUE as constraint
	 * @throws Exception
	 */
	protected SecConstraint condition_constraint() throws Exception {
		return SecFactory.condition_constraint(statement, Boolean.TRUE, true);
	}
	/**
	 * @param condition
	 * @return condition == true as constraint
	 * @throws Exception
	 */
	protected SecConstraint condition_constraint(Object condition) throws Exception {
		return SecFactory.condition_constraint(statement, condition, true);
	}
	/**
	 * @param condition
	 * @param value
	 * @return condition == value as constraint
	 * @throws Exception
	 */
	protected SecConstraint condition_constraint(Object condition, boolean value) throws Exception {
		return SecFactory.condition_constraint(statement, condition, value);
	}
	
	/* error generations */
	protected SecTrapError trap_statement() throws Exception {
		return (SecTrapError) SecFactory.trap_error(statement);
	}
	protected SecNoneError none_statement() throws Exception {
		return (SecNoneError) SecFactory.none_error(statement);
	}
	protected SecStatementError add_statement() throws Exception {
		return SecFactory.add_statement(statement);
	}
	protected SecStatementError del_statement() throws Exception {
		return SecFactory.del_statement(statement);
	}
	protected SecExpressionError set_expression(Object muta_expression) throws Exception {
		return SecFactory.set_expression(statement, this.target_expression(), muta_expression);
	}
	protected SecExpressionError add_expression(COperator operator, Object operand) throws Exception {
		return SecFactory.add_expression(statement, this.target_expression(), operator, operand);
	}
	protected SecExpressionError ins_expression(COperator operator, Object operand) throws Exception {
		return SecFactory.ins_expression(statement, this.target_expression(), operator, operand);
	}
	protected SecExpressionError uny_expression(COperator operator) throws Exception {
		return SecFactory.uny_expression(statement, this.target_expression(), operator);
	}
	protected SecReferenceError set_reference(Object muta_expression) throws Exception {
		return SecFactory.set_reference(statement, this.target_reference(), muta_expression);
	}
	protected SecReferenceError add_reference(COperator operator, Object operand) throws Exception {
		return SecFactory.add_reference(statement, this.target_reference(), operator, operand);
	}
	protected SecReferenceError ins_reference(COperator operator, Object operand) throws Exception {
		return SecFactory.ins_reference(statement, this.target_reference(), operator, operand);
	}
	protected SecReferenceError uny_reference(COperator operator) throws Exception {
		return SecFactory.uny_reference(statement, this.target_reference(), operator);
	}
	
}
