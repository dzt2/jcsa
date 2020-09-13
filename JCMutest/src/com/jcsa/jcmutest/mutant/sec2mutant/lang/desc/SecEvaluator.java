package com.jcsa.jcmutest.mutant.sec2mutant.lang.desc;

import java.util.HashSet;
import java.util.Set;

import com.jcsa.jcmutest.mutant.sec2mutant.SecValueTypes;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecAddExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecInsExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecSetExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecUnyExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.refs.SecAddReferenceError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.refs.SecInsReferenceError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.refs.SecReferenceError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.refs.SecSetReferenceError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.refs.SecUnyReferenceError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.stmt.SecPasStatementError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.stmt.SecStatementError;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymConstant;
import com.jcsa.jcparse.lang.sym.SymContexts;
import com.jcsa.jcparse.lang.sym.SymEvaluator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

/**
 * It is used to optimize the SecDescription in Symbolic Error & Constraint Language.
 * @author yukimula
 *
 */
public class SecEvaluator {
	
	/* definitions of singleton */
	/** the contextual information used to evaluate symbolic expression **/
	private SymContexts contexts;
	/** private constructor for singleton mode **/
	private SecEvaluator() { this.contexts = null; }
	/** the singleton instance of evaluator to allow static invocation **/
	private static final SecEvaluator evaluator = new SecEvaluator();
	/**
	 * @param source
	 * @return get the symbolic result of input expression using contextual data
	 *         provided by users in this.contexts.
	 * @throws Exception
	 */
	private SymExpression sym_eval(SymExpression source) throws Exception {
		return SymEvaluator.evaluate_on(source, this.contexts);
	}
	
	/* evaluation methods */
	private SecDescription eval(SecDescription source) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source as null");
		else if(source.is_constraint()) 
			return this.eval_constraint(source);
		else if(source.is_state_error()) 
			return this.eval_state_error(source);
		else
			throw new IllegalArgumentException("Inconsistent " + source);
	}
	private SecDescription eval_state_error(SecDescription source) throws Exception {
		if(source instanceof SecStatementError)
			return this.eval_statement_error((SecStatementError) source);
		else if(source instanceof SecExpressionError)
			return this.eval_expression_error((SecExpressionError) source);
		else if(source instanceof SecReferenceError)
			return this.eval_reference_error((SecReferenceError) source);
		else if(source instanceof SecConjunctDescriptions)
			return this.eval_conjunct_errors((SecConjunctDescriptions) source);
		else if(source instanceof SecDisjunctDescriptions)
			return this.eval_disjunct_errors((SecDisjunctDescriptions) source);
		else
			throw new IllegalArgumentException(source.generate_code());
	}
	
	/* constraint groups */
	private SecDescription eval_constraint(SecDescription source) throws Exception {
		if(source instanceof SecConstraint)
			return this.eval_assert_constraint((SecConstraint) source);
		else if(source instanceof SecConjunctDescriptions)
			return this.eval_conjunct_constraints((SecDescriptions) source);
		else if(source instanceof SecDisjunctDescriptions)
			return this.eval_disjunct_constraints((SecDescriptions) source);
		else
			throw new IllegalArgumentException(source.generate_code());
	}
	private SecDescription eval_assert_constraint(SecConstraint source) throws Exception {
		CirStatement statement = source.get_location().get_statement();
		SymExpression condition = source.get_condition().get_expression();
		return SecFactory.assert_constraint(statement, this.sym_eval(condition), true);
	}
	private SecDescription eval_conjunct_constraints(SecDescriptions source) throws Exception {
		/* 1. collect the children descriptions evaluated under the source */
		Set<SecDescription> children = new HashSet<SecDescription>();
		for(int k = 0; k < source.number_of_descriptions(); k++) {
			SecDescription constraint = this.eval(source.get_description(k));
			if(constraint.is_constraint()) {
				if(constraint instanceof SecConstraint) {
					SymExpression condition = 
							((SecConstraint) constraint).get_condition().get_expression();
					if(condition instanceof SymConstant) {
						if(((SymConstant) condition).get_bool()) {
							continue;			/* ignore true in conjunctions */
						}
						else {
							return constraint;	/* enforce as false in conjunctions */
						}
					}
					else {
						children.add(constraint);
					}
				}
				else {
					children.add(constraint);
				}
			}
			else {
				throw new IllegalArgumentException("Not an error: " + constraint);
			}
		}
		
		/* 2. construct the result constraints */
		CirStatement statement = source.get_location().get_statement();
		if(children.isEmpty()) 
			return SecFactory.assert_constraint(statement, Boolean.FALSE, true);
		else if(children.size() == 1)
			return children.iterator().next();
		else
			return SecFactory.conjunct(statement, children);
	}
	private SecDescription eval_disjunct_constraints(SecDescriptions source) throws Exception {
		/* 1. collect the children descriptions evaluated under the source */
		Set<SecDescription> children = new HashSet<SecDescription>();
		for(int k = 0; k < source.number_of_descriptions(); k++) {
			SecDescription constraint = this.eval(source.get_description(k));
			if(constraint.is_constraint()) {
				if(constraint instanceof SecConstraint) {
					SymExpression condition = 
							((SecConstraint) constraint).get_condition().get_expression();
					if(condition instanceof SymConstant) {
						if(((SymConstant) condition).get_bool()) {
							return constraint;	/* enforce as true in disjunctions */
						}
						else {
							continue;			/* ignore the false in disjunction */
						}
					}
					else {
						children.add(constraint);
					}
				}
				else {
					children.add(constraint);
				}
			}
			else {
				throw new IllegalArgumentException("Not an error: " + constraint);
			}
		}
		
		/* 2. construct the result constraints */
		CirStatement statement = source.get_location().get_statement();
		if(children.isEmpty()) 
			return SecFactory.assert_constraint(statement, Boolean.TRUE, true);
		else if(children.size() == 1)
			return children.iterator().next();
		else
			return SecFactory.disjunct(statement, children);
	}
	
	/* state error groups */
	private SecDescription eval_statement_error(SecStatementError source) throws Exception {
		return source;
	}
	private SecDescription eval_expression_error(SecExpressionError source) throws Exception {
		if(source instanceof SecSetExpressionError)
			return this.eval_set_expression((SecSetExpressionError) source);
		else if(source instanceof SecAddExpressionError)
			return this.eval_add_expression((SecAddExpressionError) source);
		else if(source instanceof SecInsExpressionError)
			return this.eval_ins_expression((SecInsExpressionError) source);
		else if(source instanceof SecUnyExpressionError)
			return this.eval_uny_expression((SecUnyExpressionError) source);
		else
			throw new IllegalArgumentException(source.toString());
	}
	private SecDescription eval_reference_error(SecReferenceError source) throws Exception {
		if(source instanceof SecSetReferenceError)
			return this.eval_set_reference((SecSetReferenceError) source);
		else if(source instanceof SecAddReferenceError)
			return this.eval_add_reference((SecAddReferenceError) source);
		else if(source instanceof SecInsReferenceError)
			return this.eval_ins_reference((SecInsReferenceError) source);
		else if(source instanceof SecUnyReferenceError)
			return this.eval_uny_reference((SecUnyReferenceError) source);
		else
			throw new IllegalArgumentException(source.generate_code());
	}
	private SecDescription eval_set_expression(SecSetExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_statement();
		SymExpression orig_expression = source.get_orig_expression().get_expression();
		SymExpression muta_expression = source.get_muta_expression().get_expression();
		CirExpression expression = orig_expression.get_cir_source();
		SecValueTypes vtype = source.get_orig_expression().get_type().get_vtype();
		
		orig_expression = this.sym_eval(orig_expression);
		muta_expression = this.sym_eval(muta_expression);
		if(orig_expression.equals(muta_expression)) {
			return SecFactory.pass_statement(statement);
		}
		else {
			Set<SecDescription> descriptions = new HashSet<SecDescription>();
			
			/* (1) type casting in constant or variable as normal */
			if(muta_expression instanceof SymConstant) {
				SymConstant constant = (SymConstant) muta_expression;
				switch(vtype) {
				case cbool:	descriptions.add(SecFactory.set_expression(statement, expression, constant.get_bool())); 	break;
				case cchar:	descriptions.add(SecFactory.set_expression(statement, expression, constant.get_char())); 	break;
				case csign:	descriptions.add(SecFactory.set_expression(statement, expression, constant.get_long())); 	break;
				case usign:	descriptions.add(SecFactory.set_expression(statement, expression, constant.get_long())); 	break;
				case creal:	descriptions.add(SecFactory.set_expression(statement, expression, constant.get_double())); 	break;
				case caddr:	descriptions.add(SecFactory.set_expression(statement, expression, constant.get_long()));	break;
				default:	descriptions.add(SecFactory.set_expression(statement, expression, muta_expression));		break;
				}
			}
			else {
				descriptions.add(SecFactory.set_expression(statement, expression, muta_expression));
			}
			
			/* (2) translated as add_expresison using muta_expr - orig_expr */
			switch(vtype) {
			case cchar:
			case csign:
			case usign:
			case creal:
			case caddr:
			{
				SymExpression difference = this.sym_eval(SymFactory.arith_sub(
						expression.get_data_type(), muta_expression, orig_expression));
				if(difference instanceof SymConstant) {
					descriptions.add(SecFactory.add_expression(
							statement, expression, COperator.arith_add, difference));
				}
			}
			default:	break;
			}
			
			/* (3) decide the unary expression error when orig_expr is const */
			if(orig_expression instanceof SymConstant && muta_expression instanceof SymConstant) {
				SymConstant lconstant = (SymConstant) orig_expression;
				SymConstant rconstant = (SymConstant) muta_expression;
				switch(vtype) {
				case cbool:
				{
					if(lconstant.get_bool() == rconstant.get_bool()) {
						return SecFactory.pass_statement(statement);
					}
					else {
						descriptions.add(SecFactory.uny_expression(statement, expression, COperator.logic_not));
					}
					break;
				}
				case cchar:
				case csign:
				case usign:
				{
					if(lconstant.get_long() == rconstant.get_long()) {
						return SecFactory.pass_statement(statement);
					}
					else if(lconstant.get_long() == -rconstant.get_long()) {
						descriptions.add(SecFactory.uny_expression(statement, expression, COperator.negative));
					}
					else if(lconstant.get_long() == ~rconstant.get_long()) {
						descriptions.add(SecFactory.uny_expression(statement, expression, COperator.bit_not));
					}
					break;
				}
				case creal:
				{
					if(lconstant.get_double() == rconstant.get_double()) {
						return SecFactory.pass_statement(statement);
					}
					else if(lconstant.get_double() == -rconstant.get_double()) {
						descriptions.add(SecFactory.uny_expression(statement, expression, COperator.negative));
					}
					break;
				}
				default: break;
				}
			}
			
			/* get the conjunctions of all extended errors */
			if(descriptions.isEmpty()) {
				return SecFactory.pass_statement(statement);
			}
			else if(descriptions.size() == 1) {
				return descriptions.iterator().next();
			}
			else {
				return SecFactory.conjunct(statement, descriptions);
			}
		}
	}
	private SecDescription eval_uny_expression(SecUnyExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_statement();
		SymExpression orig_expression = source.get_orig_expression().get_expression();
		CirExpression expression = orig_expression.get_cir_source();
		
		SymExpression muta_expression;
		switch(source.get_operator().get_operator()) {
		case logic_not:
			muta_expression = SymFactory.logic_not(orig_expression); break;
		case negative:
			muta_expression = SymFactory.arith_neg(expression.get_data_type(), orig_expression); break;
		case bit_not:
			muta_expression = SymFactory.bitws_rsv(expression.get_data_type(), orig_expression); break;
		default: throw new IllegalArgumentException(source.generate_code());
		}
		muta_expression = this.sym_eval(muta_expression);
		
		if(muta_expression instanceof SymConstant) {
			return this.eval(SecFactory.set_expression(statement, expression, muta_expression));
		}
		else {
			return source;
		}
	}
	private SecDescription eval_add_expression(SecAddExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_statement();
		SymExpression orig_expression = source.get_orig_expression().get_expression();
		CirExpression expression = orig_expression.get_cir_source();
		Set<SecDescription> descriptions = new HashSet<SecDescription>();
		
		/* operator and operand normalization */
		COperator operator = source.get_operator().get_operator();
		SymExpression operand = source.get_operand().get_expression();
		if(operator == COperator.arith_sub) {
			operator = COperator.arith_add;
			operand = SymFactory.arith_neg(operand.get_data_type(), operand);
		}
		operand = this.sym_eval(operand);
		descriptions.add(SecFactory.add_expression(statement, expression, operator, operand));
		
		/* get baseline and extended version by set-expression */
		SymExpression muta_expression;
		switch(operator) {
		case arith_add:	muta_expression = SymFactory.arith_add(expression.get_data_type(), orig_expression, operand); break;
		case arith_mul:	muta_expression = SymFactory.arith_mul(expression.get_data_type(), orig_expression, operand); break;
		case arith_div:	muta_expression = SymFactory.arith_div(expression.get_data_type(), orig_expression, operand); break;
		case arith_mod:	muta_expression = SymFactory.arith_mod(expression.get_data_type(), orig_expression, operand); break;
		case bit_and:	muta_expression = SymFactory.bitws_and(expression.get_data_type(), orig_expression, operand); break;
		case bit_or:	muta_expression = SymFactory.bitws_ior(expression.get_data_type(), orig_expression, operand); break;
		case bit_xor:	muta_expression = SymFactory.bitws_xor(expression.get_data_type(), orig_expression, operand); break;
		case left_shift:muta_expression = SymFactory.bitws_lsh(expression.get_data_type(), orig_expression, operand); break;
		case righ_shift:muta_expression = SymFactory.bitws_rsh(expression.get_data_type(), orig_expression, operand); break;
		case logic_and:	muta_expression = SymFactory.logic_and(orig_expression, operand); break;
		case logic_or:	muta_expression = SymFactory.logic_ior(orig_expression, operand); break;
		default: throw new IllegalArgumentException(source.generate_code());
		}
		SecDescription result = this.eval(SecFactory.set_expression(statement, expression, muta_expression));
		
		/* construct the following descriptions */
		if(result instanceof SecPasStatementError) {
			return result;
		}
		else if(result instanceof SecConjunctDescriptions) {
			SecDescriptions results = (SecDescriptions) result;
			for(int k = 0; k < results.number_of_descriptions(); k++) {
				descriptions.add(results.get_description(k));
			}
		}
		else {
			descriptions.add(result);
		}
		
		/* conjunction of all results */
		if(descriptions.isEmpty()) {
			return SecFactory.pass_statement(statement);
		}
		else if(descriptions.size() == 1) {
			return descriptions.iterator().next();
		}
		else {
			return SecFactory.conjunct(statement, descriptions);
		}
	}
	private SecDescription eval_ins_expression(SecInsExpressionError source) throws Exception {
		CirStatement statement = source.get_location().get_statement();
		SymExpression orig_expression = source.get_orig_expression().get_expression();
		CirExpression expression = orig_expression.get_cir_source();
		Set<SecDescription> descriptions = new HashSet<SecDescription>();
		
		/* operator and operand normalization */
		COperator operator = source.get_operator().get_operator();
		SymExpression operand = source.get_operand().get_expression();
		operand = this.sym_eval(operand);
		descriptions.add(SecFactory.ins_expression(statement, expression, operator, operand));
		
		/* get baseline and extended version by set-expression */
		SymExpression muta_expression;
		switch(operator) {
		case arith_sub:	muta_expression = SymFactory.arith_div(expression.get_data_type(), operand, orig_expression); break;
		case arith_div:	muta_expression = SymFactory.arith_div(expression.get_data_type(), operand, orig_expression); break;
		case arith_mod:	muta_expression = SymFactory.arith_mod(expression.get_data_type(), operand, orig_expression); break;
		case left_shift:muta_expression = SymFactory.bitws_lsh(expression.get_data_type(), operand, orig_expression); break;
		case righ_shift:muta_expression = SymFactory.bitws_rsh(expression.get_data_type(), operand, orig_expression); break;
		default: throw new IllegalArgumentException(source.generate_code());
		}
		SecDescription result = this.eval(SecFactory.set_expression(statement, expression, muta_expression));
		
		/* construct the following descriptions */
		if(result instanceof SecPasStatementError) {
			return result;
		}
		else if(result instanceof SecConjunctDescriptions) {
			SecDescriptions results = (SecDescriptions) result;
			for(int k = 0; k < results.number_of_descriptions(); k++) {
				descriptions.add(results.get_description(k));
			}
		}
		else {
			descriptions.add(result);
		}
		
		/* conjunction of all results */
		if(descriptions.isEmpty()) {
			return SecFactory.pass_statement(statement);
		}
		else if(descriptions.size() == 1) {
			return descriptions.iterator().next();
		}
		else {
			return SecFactory.conjunct(statement, descriptions);
		}
	}
	private SecDescription eval_set_reference(SecSetReferenceError source) throws Exception {
		CirStatement statement = source.get_location().get_statement();
		SymExpression orig_expression = source.get_orig_reference().get_expression();
		SymExpression muta_expression = source.get_muta_expression().get_expression();
		CirExpression expression = orig_expression.get_cir_source();
		SecValueTypes vtype = source.get_orig_reference().get_type().get_vtype();
		
		orig_expression = this.sym_eval(orig_expression);
		muta_expression = this.sym_eval(muta_expression);
		if(orig_expression.equals(muta_expression)) {
			return SecFactory.pass_statement(statement);
		}
		else {
			Set<SecDescription> descriptions = new HashSet<SecDescription>();
			
			/* (1) type casting in constant or variable as normal */
			if(muta_expression instanceof SymConstant) {
				SymConstant constant = (SymConstant) muta_expression;
				switch(vtype) {
				case cbool:	descriptions.add(SecFactory.set_reference(statement, expression, constant.get_bool())); 	break;
				case cchar:	descriptions.add(SecFactory.set_reference(statement, expression, constant.get_char())); 	break;
				case csign:	descriptions.add(SecFactory.set_reference(statement, expression, constant.get_long())); 	break;
				case usign:	descriptions.add(SecFactory.set_reference(statement, expression, constant.get_long())); 	break;
				case creal:	descriptions.add(SecFactory.set_reference(statement, expression, constant.get_double())); 	break;
				case caddr:	descriptions.add(SecFactory.set_reference(statement, expression, constant.get_long()));	break;
				default:	descriptions.add(SecFactory.set_reference(statement, expression, muta_expression));		break;
				}
			}
			else {
				descriptions.add(SecFactory.set_reference(statement, expression, muta_expression));
			}
			
			/* (2) translated as add_expresison using muta_expr - orig_expr */
			switch(vtype) {
			case cchar:
			case csign:
			case usign:
			case creal:
			case caddr:
			{
				SymExpression difference = this.sym_eval(SymFactory.arith_sub(
						expression.get_data_type(), muta_expression, orig_expression));
				if(difference instanceof SymConstant) {
					descriptions.add(SecFactory.add_reference(
							statement, expression, COperator.arith_add, difference));
				}
			}
			default:	break;
			}
			
			/* (3) decide the unary expression error when orig_expr is const */
			if(orig_expression instanceof SymConstant && muta_expression instanceof SymConstant) {
				SymConstant lconstant = (SymConstant) orig_expression;
				SymConstant rconstant = (SymConstant) muta_expression;
				switch(vtype) {
				case cbool:
				{
					if(lconstant.get_bool() == rconstant.get_bool()) {
						return SecFactory.pass_statement(statement);
					}
					else {
						descriptions.add(SecFactory.uny_reference(statement, expression, COperator.logic_not));
					}
					break;
				}
				case cchar:
				case csign:
				case usign:
				{
					if(lconstant.get_long() == rconstant.get_long()) {
						return SecFactory.pass_statement(statement);
					}
					else if(lconstant.get_long() == -rconstant.get_long()) {
						descriptions.add(SecFactory.uny_reference(statement, expression, COperator.negative));
					}
					else if(lconstant.get_long() == ~rconstant.get_long()) {
						descriptions.add(SecFactory.uny_reference(statement, expression, COperator.bit_not));
					}
					break;
				}
				case creal:
				{
					if(lconstant.get_double() == rconstant.get_double()) {
						return SecFactory.pass_statement(statement);
					}
					else if(lconstant.get_double() == -rconstant.get_double()) {
						descriptions.add(SecFactory.uny_reference(statement, expression, COperator.negative));
					}
					break;
				}
				default: break;
				}
			}
			
			/* get the conjunctions of all extended errors */
			if(descriptions.isEmpty()) {
				return SecFactory.pass_statement(statement);
			}
			else if(descriptions.size() == 1) {
				return descriptions.iterator().next();
			}
			else {
				return SecFactory.conjunct(statement, descriptions);
			}
		}
	}
	private SecDescription eval_uny_reference(SecUnyReferenceError source) throws Exception {
		CirStatement statement = source.get_location().get_statement();
		SymExpression orig_expression = source.get_orig_reference().get_expression();
		CirExpression expression = orig_expression.get_cir_source();
		
		SymExpression muta_expression;
		switch(source.get_operator().get_operator()) {
		case logic_not:
			muta_expression = SymFactory.logic_not(orig_expression); break;
		case negative:
			muta_expression = SymFactory.arith_neg(expression.get_data_type(), orig_expression); break;
		case bit_not:
			muta_expression = SymFactory.bitws_rsv(expression.get_data_type(), orig_expression); break;
		default: throw new IllegalArgumentException(source.generate_code());
		}
		muta_expression = this.sym_eval(muta_expression);
		
		if(muta_expression instanceof SymConstant) {
			return this.eval(SecFactory.set_reference(statement, expression, muta_expression));
		}
		else {
			return source;
		}
	}
	private SecDescription eval_add_reference(SecAddReferenceError source) throws Exception {
		CirStatement statement = source.get_location().get_statement();
		SymExpression orig_expression = source.get_orig_reference().get_expression();
		CirExpression expression = orig_expression.get_cir_source();
		Set<SecDescription> descriptions = new HashSet<SecDescription>();
		
		/* operator and operand normalization */
		COperator operator = source.get_operator().get_operator();
		SymExpression operand = source.get_operand().get_expression();
		if(operator == COperator.arith_sub) {
			operator = COperator.arith_add;
			operand = SymFactory.arith_neg(operand.get_data_type(), operand);
		}
		operand = this.sym_eval(operand);
		descriptions.add(SecFactory.add_reference(statement, expression, operator, operand));
		
		/* get baseline and extended version by set-expression */
		SymExpression muta_expression;
		switch(operator) {
		case arith_add:	muta_expression = SymFactory.arith_add(expression.get_data_type(), orig_expression, operand); break;
		case arith_mul:	muta_expression = SymFactory.arith_mul(expression.get_data_type(), orig_expression, operand); break;
		case arith_div:	muta_expression = SymFactory.arith_div(expression.get_data_type(), orig_expression, operand); break;
		case arith_mod:	muta_expression = SymFactory.arith_mod(expression.get_data_type(), orig_expression, operand); break;
		case bit_and:	muta_expression = SymFactory.bitws_and(expression.get_data_type(), orig_expression, operand); break;
		case bit_or:	muta_expression = SymFactory.bitws_ior(expression.get_data_type(), orig_expression, operand); break;
		case bit_xor:	muta_expression = SymFactory.bitws_xor(expression.get_data_type(), orig_expression, operand); break;
		case left_shift:muta_expression = SymFactory.bitws_lsh(expression.get_data_type(), orig_expression, operand); break;
		case righ_shift:muta_expression = SymFactory.bitws_rsh(expression.get_data_type(), orig_expression, operand); break;
		case logic_and:	muta_expression = SymFactory.logic_and(orig_expression, operand); break;
		case logic_or:	muta_expression = SymFactory.logic_ior(orig_expression, operand); break;
		default: throw new IllegalArgumentException(source.generate_code());
		}
		SecDescription result = this.eval(SecFactory.set_reference(statement, expression, muta_expression));
		
		/* construct the following descriptions */
		if(result instanceof SecPasStatementError) {
			return result;
		}
		else if(result instanceof SecConjunctDescriptions) {
			SecDescriptions results = (SecDescriptions) result;
			for(int k = 0; k < results.number_of_descriptions(); k++) {
				descriptions.add(results.get_description(k));
			}
		}
		else {
			descriptions.add(result);
		}
		
		/* conjunction of all results */
		if(descriptions.isEmpty()) {
			return SecFactory.pass_statement(statement);
		}
		else if(descriptions.size() == 1) {
			return descriptions.iterator().next();
		}
		else {
			return SecFactory.conjunct(statement, descriptions);
		}
	}
	private SecDescription eval_ins_reference(SecInsReferenceError source) throws Exception {
		CirStatement statement = source.get_location().get_statement();
		SymExpression orig_expression = source.get_orig_reference().get_expression();
		CirExpression expression = orig_expression.get_cir_source();
		Set<SecDescription> descriptions = new HashSet<SecDescription>();
		
		/* operator and operand normalization */
		COperator operator = source.get_operator().get_operator();
		SymExpression operand = source.get_operand().get_expression();
		operand = this.sym_eval(operand);
		descriptions.add(SecFactory.ins_reference(statement, expression, operator, operand));
		
		/* get baseline and extended version by set-expression */
		SymExpression muta_expression;
		switch(operator) {
		case arith_sub:	muta_expression = SymFactory.arith_div(expression.get_data_type(), operand, orig_expression); break;
		case arith_div:	muta_expression = SymFactory.arith_div(expression.get_data_type(), operand, orig_expression); break;
		case arith_mod:	muta_expression = SymFactory.arith_mod(expression.get_data_type(), operand, orig_expression); break;
		case left_shift:muta_expression = SymFactory.bitws_lsh(expression.get_data_type(), operand, orig_expression); break;
		case righ_shift:muta_expression = SymFactory.bitws_rsh(expression.get_data_type(), operand, orig_expression); break;
		default: throw new IllegalArgumentException(source.generate_code());
		}
		SecDescription result = this.eval(SecFactory.set_reference(statement, expression, muta_expression));
		
		/* construct the following descriptions */
		if(result instanceof SecPasStatementError) {
			return result;
		}
		else if(result instanceof SecConjunctDescriptions) {
			SecDescriptions results = (SecDescriptions) result;
			for(int k = 0; k < results.number_of_descriptions(); k++) {
				descriptions.add(results.get_description(k));
			}
		}
		else {
			descriptions.add(result);
		}
		
		/* conjunction of all results */
		if(descriptions.isEmpty()) {
			return SecFactory.pass_statement(statement);
		}
		else if(descriptions.size() == 1) {
			return descriptions.iterator().next();
		}
		else {
			return SecFactory.conjunct(statement, descriptions);
		}
	}
	
	/* conjunction & disjunction of errors */
	private void collect_descriptions(boolean conjunct, SecDescription description, Set<SecDescription> descriptions) throws Exception {
		if(description instanceof SecDescriptions) {
			if(description instanceof SecConjunctDescriptions && conjunct) {
				int length = ((SecDescriptions) description).number_of_descriptions();
				for(int k = 0; k < length; k++) {
					descriptions.add(((SecDescriptions) description).get_description(k));
				}
			}
			else if(description instanceof SecDisjunctDescriptions && !conjunct) {
				int length = ((SecDescriptions) description).number_of_descriptions();
				for(int k = 0; k < length; k++) {
					descriptions.add(((SecDescriptions) description).get_description(k));
				}
			}
			else {
				descriptions.add(description);
			}
		}
		else if(description instanceof SecStatementError) {
			if(!(description instanceof SecPasStatementError)) {
				descriptions.add(description);
			}
		}
		else if(description instanceof SecExpressionError || 
				description instanceof SecReferenceError) {
			descriptions.add(description);
		}
	}
	private SecDescription eval_conjunct_errors(SecConjunctDescriptions source) throws Exception {
		Set<SecDescription> descriptions = new HashSet<SecDescription>();
		for(int k = 0; k < source.number_of_descriptions(); k++) {
			this.collect_descriptions(true, 
					this.eval(source.get_description(k)), descriptions);
		}
		CirStatement statement = source.get_location().get_statement();
		if(descriptions.isEmpty()) {
			return SecFactory.pass_statement(statement);
		}
		else if(descriptions.size() == 1) {
			return descriptions.iterator().next();
		}
		else {
			return SecFactory.conjunct(statement, descriptions);
		}
	}
	private SecDescription eval_disjunct_errors(SecDisjunctDescriptions source) throws Exception {
		Set<SecDescription> descriptions = new HashSet<SecDescription>();
		for(int k = 0; k < source.number_of_descriptions(); k++) {
			this.collect_descriptions(false, 
					this.eval(source.get_description(k)), descriptions);
		}
		CirStatement statement = source.get_location().get_statement();
		if(descriptions.isEmpty()) {
			return SecFactory.pass_statement(statement);
		}
		else if(descriptions.size() == 1) {
			return descriptions.iterator().next();
		}
		else {
			return SecFactory.disjunct(statement, descriptions);
		}
	}
	
	/* open API */
	/**
	 * @param source
	 * @param contexts
	 * @return the descriptions optimized and extended under the contextual scope
	 * @throws Exception
	 */
	protected static SecDescription evaluate(SecDescription source, SymContexts contexts) throws Exception {
		evaluator.contexts = contexts;
		return evaluator.eval(source);
	}
	
}
