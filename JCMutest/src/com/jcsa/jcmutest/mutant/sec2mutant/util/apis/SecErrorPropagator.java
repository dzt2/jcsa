package com.jcsa.jcmutest.mutant.sec2mutant.util.apis;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecStateError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.cons.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecAddExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecInsExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecSetExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecUnyExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.refer.SecAddReferenceError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.refer.SecInsReferenceError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.refer.SecSetReferenceError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.refer.SecUnyReferenceError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.stmt.SecAddStatementError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.stmt.SecDelStatementError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.stmt.SecSetStatementError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.uniq.SecNoneError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.uniq.SecTrapError;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;


/**
 * It implements the construction of error propagation from one error in source
 * point to the error(s) in target point in cir-code w.r.t. some constraints so
 * that the errors in target location will be caused if the source error occurs.
 * 
 * @author yukimula
 *
 */
public abstract class SecErrorPropagator {
	
	/* definitions */
	/** the statement where the target errors occur **/
	private CirStatement statement;
	/** the location where the errors are propagated **/
	private CirNode target_location;
	/** error in source to cause errors in target **/
	private SecStateError source_error;
	/** mapping from target-error to its constraint **/
	private Map<SecStateError, SecConstraint> results;
	/**
	 * create an non-initialized error propagator
	 */
	public SecErrorPropagator() { }
	
	/* main method */
	/**
	 * @param statement the statement where the target errors occur
	 * @param target_location the location where the errors are propagated
	 * @param src_error error in source to cause errors in target
	 * @return mapping from target-error to its constraint
	 * @throws Exception
	 */
	public Map<SecStateError, SecConstraint> propagate(CirStatement statement,
			CirNode target_location, SecStateError source_error) throws Exception {
		if(statement == null)
			throw new IllegalArgumentException("Invalid statement");
		else if(target_location == null || !this.test_target_location(target_location))
			throw new IllegalArgumentException("Invalid target: " + target_location);
		else if(source_error == null)
			throw new IllegalArgumentException("Invalid source-error: null");
		else {
			/* set the data attributes */
			this.statement = statement;
			this.target_location = target_location;
			this.source_error = source_error;
			this.results = new HashMap<SecStateError, SecConstraint>();
			
			if(source_error instanceof SecAddStatementError) {
				this.add_statement_error((SecAddStatementError) source_error);
			}
			else if(source_error instanceof SecDelStatementError) {
				this.del_statement_error((SecDelStatementError) source_error);
			}
			else if(source_error instanceof SecSetStatementError) {
				this.set_statement_error((SecSetStatementError) source_error);
			}
			else if(source_error instanceof SecSetExpressionError) {
				this.set_expression_error((SecSetExpressionError) source_error);
			}
			else if(source_error instanceof SecAddExpressionError) {
				this.add_expression_error((SecAddExpressionError) source_error);
			}
			else if(source_error instanceof SecInsExpressionError) {
				this.ins_expression_error((SecInsExpressionError) source_error);
			}
			else if(source_error instanceof SecUnyExpressionError) {
				this.uny_expression_error((SecUnyExpressionError) source_error);
			}
			else if(source_error instanceof SecSetReferenceError) {
				this.set_reference_error((SecSetReferenceError) source_error);
			}
			else if(source_error instanceof SecAddReferenceError) {
				this.add_reference_error((SecAddReferenceError) source_error);
			}
			else if(source_error instanceof SecInsReferenceError) {
				this.ins_reference_error((SecInsReferenceError) source_error);
			}
			else if(source_error instanceof SecUnyReferenceError) {
				this.uny_reference_error((SecUnyReferenceError) source_error);
			}
			else if(source_error instanceof SecTrapError) {
				this.trap_error((SecTrapError) source_error);
			}
			else if(source_error instanceof SecNoneError) {
				this.none_error((SecNoneError) source_error);
			}
			else {
				throw new IllegalArgumentException("Invalid: " + source_error);
			}
			
			/* return the propagation maps */	return this.results;
		}
	}
	
	/* abstract propagation methods */
	/**
	 * add_statement(stmt)
	 * @param error
	 * @throws Exception
	 */
	protected abstract void add_statement_error(SecAddStatementError error) throws Exception;
	/**
	 * del_statement(stmt)
	 * @param error
	 * @throws Exception
	 */
	protected abstract void del_statement_error(SecDelStatementError error) throws Exception;
	/**
	 * set_statement(stmt, stmt)
	 * @param error
	 * @throws Exception
	 */
	protected abstract void set_statement_error(SecSetStatementError error) throws Exception;
	/**
	 * trap()
	 * @param error
	 * @throws Exception
	 */
	protected abstract void trap_error(SecTrapError error) throws Exception;
	/**
	 * none()
	 * @param error
	 * @throws Exception
	 */
	protected abstract void none_error(SecNoneError error) throws Exception;
	/**
	 * set_expression(expr, expr)
	 * @param source
	 * @throws Exception
	 */
	protected abstract void set_expression_error(SecSetExpressionError error) throws Exception;
	/**
	 * add_expression(expr, oprt, expr)
	 * @param source
	 * @throws Exception
	 */
	protected abstract void add_expression_error(SecAddExpressionError error) throws Exception;
	/**
	 * ins_expression(expr, oprt, expr)
	 * @param source
	 * @throws Exception
	 */
	protected abstract void ins_expression_error(SecInsExpressionError error) throws Exception;
	/**
	 * uny_expression(expr, oprt)
	 * @param source
	 * @throws Exception
	 */
	protected abstract void uny_expression_error(SecUnyExpressionError error) throws Exception;
	/**
	 * set_reference(expr, expr)
	 * @param source
	 * @throws Exception
	 */
	protected abstract void set_reference_error(SecSetReferenceError error) throws Exception;
	/**
	 * add_reference(expr, oprt, expr)
	 * @param source
	 * @throws Exception
	 */
	protected abstract void add_reference_error(SecAddReferenceError error) throws Exception;
	/**
	 * ins_reference(expr, oprt, expr)
	 * @param source
	 * @throws Exception
	 */
	protected abstract void ins_reference_error(SecInsReferenceError error) throws Exception;
	/**
	 * uny_reference(expr, oprt)
	 * @param source
	 * @throws Exception
	 */
	protected abstract void uny_reference_error(SecUnyReferenceError error) throws Exception;
	
	/* basic methods */
	/**
	 * @param location
	 * @return whether the location is available for error to propagate.
	 * @throws Exception
	 */
	protected abstract boolean test_target_location(CirNode location) throws Exception;
	/**
	 * throw an unsupported operation exception to the propagation process.
	 * @throws Exception
	 */
	protected void report_unsupported_operations() throws Exception {
		throw new UnsupportedOperationException("Unsupported{\n"
				+ "\tSource_Error: " + source_error.generate_code() + "\n"
				+ "\tTarget_location: " + target_location.generate_code(true) + "\n"
				+ "\tTarget_statement: " + statement.generate_code(true) + "\n}\n");
	}
	/**
	 * add the constraint-error pair as the propagation from source to the target.
	 * @param constraint
	 * @param target_error
	 * @throws Exception
	 */
	protected void add_propagation_pair(SecConstraint constraint, SecStateError target_error) throws Exception {
		if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint: null");
		else if(target_error == null)
			throw new IllegalArgumentException("Invalid target_error: null");
		else {
			this.results.put(target_error, constraint);
		}
	}
	/**
	 * @return the target_location as expression
	 * @throws Exception
	 */
	protected CirExpression target_expression() throws Exception {
		if(this.target_location instanceof CirExpression)
			return (CirExpression) this.target_location;
		else
			throw new IllegalArgumentException("Not an expression: " + this.target_location);
	}
	/**
	 * @return the target location as reference
	 * @throws Exception
	 */
	protected CirReferExpression target_reference() throws Exception {
		if(this.target_location instanceof CirReferExpression)
			return (CirReferExpression) this.target_location;
		else
			throw new IllegalArgumentException("Not a reference: " + this.target_location);
	}
	/**
	 * @return the computational expression with binary operands
	 * @throws Exception
	 */
	protected CirComputeExpression target_binary_expression() throws Exception {
		if(this.target_location instanceof CirComputeExpression) {
			if(((CirComputeExpression) this.target_location).number_of_operand() == 2) {
				return (CirComputeExpression) this.target_location;
			}
			else {
				throw new IllegalArgumentException(target_location.generate_code(true));
			}
		}
		else {
			throw new IllegalArgumentException(target_location.toString());
		}
	}
	
	/* constraint generation */
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
	
	/* expression error generation */
	/**
	 * @param muta_expression
	 * @return set_expression(target_location, muta_expression) 
	 * @throws Exception
	 */
	protected SecStateError set_expression(Object muta_expression) throws Exception {
		return SecFactory.set_expression(statement, this.target_expression(), muta_expression);
	}
	/**
	 * @param operator
	 * @param operand
	 * @return add_expression(target_location, operator, operand)
	 * @throws Exception
	 */
	protected SecStateError add_expression(COperator operator, Object operand) throws Exception {
		return SecFactory.add_expression(statement, this.target_expression(), operator, operand);
	}
	/**
	 * @param operator
	 * @param operand
	 * @return ins_expression(target_location, operator, operand)
	 * @throws Exception
	 */
	protected SecStateError ins_expression(COperator operator, Object operand) throws Exception {
		return SecFactory.ins_expression(statement, this.target_expression(), operator, operand);
	}
	/**
	 * @param operator
	 * @return uny_expression(target_location, operator)
	 * @throws Exception
	 */
	protected SecStateError uny_expression(COperator operator) throws Exception {
		return SecFactory.uny_expression(statement, this.target_expression(), operator);
	}
	
	/* reference error generation */
	/**
	 * @param muta_expression
	 * @return set_reference(target_location, 
	 * @throws Exception
	 */
	protected SecStateError set_reference(Object muta_expression) throws Exception {
		return SecFactory.set_reference(statement, this.target_reference(), muta_expression);
	}
	/**
	 * @param muta_expression
	 * @return set_reference(target_location, 
	 * @throws Exception
	 */
	protected SecStateError add_reference(COperator operator, Object operand) throws Exception {
		return SecFactory.add_reference(statement, this.target_reference(), operator, operand);
	}
	/**
	 * @param muta_expression
	 * @return set_reference(target_location, 
	 * @throws Exception
	 */
	protected SecStateError ins_reference(COperator operator, Object operand) throws Exception {
		return SecFactory.add_reference(statement, this.target_reference(), operator, operand);
	}
	/**
	 * @param muta_expression
	 * @return set_reference(target_location, 
	 * @throws Exception
	 */
	protected SecStateError uny_reference(COperator operator) throws Exception {
		return SecFactory.uny_reference(statement, this.target_reference(), operator);
	}
	
	/* statement or unique error */
	/**
	 * @return add_statement(statement)
	 * @throws Exception
	 */
	protected SecStateError add_statement() throws Exception {
		return SecFactory.add_statement(this.statement);
	}
	/**
	 * @return del_statement(statement)
	 * @throws Exception
	 */
	protected SecStateError del_statement() throws Exception {
		return SecFactory.del_statement(this.statement);
	}
	/**
	 * @return trap(statement)
	 * @throws Exception
	 */
	protected SecStateError trap_statement() throws Exception {
		return SecFactory.trap_error(statement);
	}
	/**
	 * @return none(statement)
	 * @throws Exception
	 */
	protected SecStateError none_statement() throws Exception {
		return SecFactory.none_error(statement);
	}
	
}
