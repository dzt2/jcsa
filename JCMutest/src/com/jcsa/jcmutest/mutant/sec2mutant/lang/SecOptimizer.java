package com.jcsa.jcmutest.mutant.sec2mutant.lang;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.cons.SecConditionConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.cons.SecConjunctConstraints;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.cons.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.cons.SecDisjunctConstraints;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.cons.SecExecutionConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecAddExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecInsExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecSetExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecUnyExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.refer.SecAddReferenceError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.refer.SecInsReferenceError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.refer.SecReferenceError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.refer.SecSetReferenceError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.refer.SecUnyReferenceError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.stmt.SecStatementError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.token.SecType;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.uniq.SecUniqueError;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymConstant;
import com.jcsa.jcparse.lang.sym.SymContexts;
import com.jcsa.jcparse.lang.sym.SymEvaluator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

/**
 * It is used to optimize the symbolic description (SecDescription), including:<br>
 * <code>
 * 	+----------------------------------------------------------------------+<br>
 * 	SecNode																	<br>
 * 	|--	SecDescription					{statement: SecStatement}			<br>
 * 	|--	|--	SecConstraint				{sym_condition: SymExpression}		<br>
 * 	|--	|--	SecStateError				{cir_location: CirNode}				<br>
 * 	+----------------------------------------------------------------------+<br>
 * 	SecConstraint															<br>
 * 	|--	SecConditionConstraint			assert(statement, expression)		<br>
 * 	|--	SecExecutionConstraint			execute(statement, expression)		<br>
 * 	|--	SecConjunctConstraints			conjunct{constraint+}				<br>
 * 	|--	SecDisjunctConstraints			disjunct{constraint+}				<br>
 * 	+----------------------------------------------------------------------+<br>
 * 	SecStateError															<br>
 * 	|--	SecStatementError				{orig_stmt: SecStatement}			<br>
 * 	|--	|--	SecAddStatementError		add_stmt(orig_stmt)					<br>
 * 	|--	|--	SecDelStatementError		del_stmt(orig_stmt)					<br>
 * 	|--	|--	SecSetStatementError		set_stmt(orig_stmt, muta_stmt)		<br>
 * 	|--	SecExpressionError				{orig_expr: SecExpression}			<br>
 * 	|--	|--	SecSetExpressionError		set_expr(orig_expr, muta_expr)		<br>
 * 	|--	|--	SecAddExpressionError		add_expr(orig_expr, oprt, muta_expr)<br>
 * 	|--	|--	SecInsExpressionError		ins_expr(orig_expr, oprt, muta_expr)<br>
 * 	|--	|--	SecUnyExpressionError		uny_expr(orig_expr, oprt)			<br>
 * 	|--	SecReferenceError				{orig_expr: SecExpression}			<br>
 * 	|--	|--	SecSetReferenceError		set_refr(orig_expr, muta_expr)		<br>
 * 	|--	|--	SecAddReferenceError		add_refr(orig_expr, oprt, muta_expr)<br>
 * 	|--	|--	SecInsReferenceError		ins_refr(orig_expr, oprt, muta_expr)<br>
 * 	|--	|--	SecUnyReferenceError		uny_expr(orig_expr, oprt)			<br>
 * 	|--	SecUniqueError														<br>
 * 	|--	|--	SecTrapError				trap()								<br>
 * 	|--	|--	SecNoneError				none()								<br>
 * 	+----------------------------------------------------------------------+<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public class SecOptimizer {
	
	/* definitions */
	/** it provides the contextual information to evaluate symbolic description **/
	private SymContexts contexts;
	/** private constructor for singleton mode **/
	private SecOptimizer() { this.contexts = null; }
	/** the singleton of the optimizer for evaluating the symbolic description **/
	private static final SecOptimizer optimizer = new SecOptimizer();
	
	/* optimization methods */
	/**
	 * @param source
	 * @return symbolic expression optimized from source using this.contexts
	 * @throws Exception
	 */
	private SymExpression sym_eval(SymExpression source) throws Exception {
		return SymEvaluator.evaluate_on(source, this.contexts);
	}
	/**
	 * 
	 * @param constraint
	 * @param contexts
	 * @return the constraint optimized from source using contextual data
	 * @throws Exception
	 */
	public static SecConstraint optimize(SecConstraint 
			constraint, SymContexts contexts) throws Exception {
		optimizer.contexts = contexts;
		return optimizer.opt_constraint(constraint);
	}
	/**
	 * @param source
	 * @return the set of state errors extended from the source, which are semantically
	 * 		   equivalent with the source state error.
	 * @throws Exception
	 */
	public static Iterable<SecStateError> extend(SecStateError source, SymContexts contexts) throws Exception {
		optimizer.contexts = contexts;
		optimizer.extension_records.clear();
		optimizer.extensions.clear();
		
		optimizer.ext_state_error(source);
		
		List<SecStateError> errors = new ArrayList<SecStateError>();
		for(SecStateError error : optimizer.extensions) {
			if(error instanceof SecUniqueError) {
				errors.clear();
				errors.add(error);
				break;
			}
			else {
				errors.add(error);
			}
		}
		return errors;
	}
	
	/* constraint optimization */
	/**
	 * @param source
	 * @return optimize the constraint using contextual information
	 * @throws Exception
	 */
	private SecConstraint opt_constraint(SecConstraint source) throws Exception {
		if(source instanceof SecExecutionConstraint) {
			return this.opt_execution_constraint((SecExecutionConstraint) source);
		}
		else if(source instanceof SecConditionConstraint) {
			return this.opt_condition_constraint((SecConditionConstraint) source);
		}
		else if(source instanceof SecConjunctConstraints) {
			return this.opt_conjunct_constraints((SecConjunctConstraints) source);
		}
		else if(source instanceof SecDisjunctConstraints) {
			return this.opt_disjunct_constraints((SecDisjunctConstraints) source);
		}
		else {
			throw new IllegalArgumentException(source.generate_code());
		}
	}
	/**
	 * @param source
	 * @return --> condition_constraint(true|false) | execution_constraint as original
	 * @throws Exception
	 */
	private SecConstraint opt_execution_constraint(
			SecExecutionConstraint source) throws Exception {
		SymExpression condition = source.get_sym_condition();
		condition = this.sym_eval(condition);
		if(condition instanceof SymConstant) {
			CirStatement statement = source.get_statement().get_statement();
			if(((SymConstant) condition).get_bool())
				return SecFactory.condition_constraint(statement, Boolean.TRUE, true);
			else
				return SecFactory.condition_constraint(statement, Boolean.FALSE, true);
		}
		else {
			return source;
		}
	}
	/**
	 * @param source
	 * @return condition_constraint(eval(condition))
	 * @throws Exception
	 */
	private SecConstraint opt_condition_constraint(
			SecConditionConstraint source) throws Exception {
		SymExpression condition = source.get_condition().get_expression();
		condition = this.sym_eval(condition);
		CirStatement statement = source.get_statement().get_statement();
		return SecFactory.condition_constraint(statement, condition, true);
	}
	/**
	 * collect the conditions in the conjunction or disjunction source
	 * @param conjunct
	 * @param source
	 * @param constraints to preserve the collected conditions in set
	 * @throws Exception
	 */
	private void collect_conditions_in(boolean conjunct, SecConstraint source, 
			Set<SecConstraint> constraints) throws Exception {
		if(source instanceof SecConjunctConstraints) {
			if(conjunct) {
				int n = ((SecConjunctConstraints) source).number_of_constraints();
				for(int k = 0; k < n; k++) {
					this.collect_conditions_in(
							conjunct, 
							((SecConjunctConstraints) source).get_constraint(k), 
							constraints);
				}
			}
			else {
				constraints.add(source);
			}
		}
		else if(source instanceof SecDisjunctConstraints) {
			if(conjunct) {
				constraints.add(source);
			}
			else {
				int n = ((SecDisjunctConstraints) source).number_of_constraints();
				for(int k = 0; k < n; k++) {
					this.collect_conditions_in(
							conjunct, 
							((SecDisjunctConstraints) source).get_constraint(k), 
							constraints);
				}
			}
		}
		else {
			constraints.add(source);
		}
	}
	/**
	 * @param source
	 * @return --> true|false|conjunct|single
	 * @throws Exception
	 */
	private SecConstraint opt_conjunct_constraints(
			SecConjunctConstraints source) throws Exception {
		/* 1. collect the constraints in conjunctions */
		Set<SecConstraint> constraints = new HashSet<SecConstraint>();
		this.collect_conditions_in(true, source, constraints);
		
		/* 2. optimize the constraints and partial evaluation */
		Set<SecConstraint> new_constraints = new HashSet<SecConstraint>();
		for(SecConstraint constraint : constraints) {
			SecConstraint new_constraint = 
					(SecConstraint) this.opt_constraint(constraint);
			SymExpression condition = new_constraint.get_sym_condition();
			if(condition instanceof SymConstant) {
				if(((SymConstant) condition).get_bool()) {
					continue;				/* ignore true in conjunctions */
				}
				else {
					return new_constraint;	/* enforce false in conjunctions */
				}
			}
			else {
				new_constraints.add(new_constraint);
			}
		}
		
		/* 3. construct the optimized conjunctions from source */
		CirStatement statement = source.get_statement().get_statement();
		if(new_constraints.isEmpty()) {
			return SecFactory.condition_constraint(statement, Boolean.TRUE, true);
		}
		else if(new_constraints.size() == 1) {
			return new_constraints.iterator().next();
		}
		else {
			return SecFactory.conjunct_constraints(statement, new_constraints);
		}
	}
	/**
	 * @param source
	 * @return --> true|false|conjunct|single
	 * @throws Exception
	 */
	private SecConstraint opt_disjunct_constraints(
			SecDisjunctConstraints source) throws Exception {
		/* 1. collect the constraints in disjunctions */
		Set<SecConstraint> constraints = new HashSet<SecConstraint>();
		this.collect_conditions_in(false, source, constraints);
		
		/* 2. optimize the disstraints and partial evaluation */
		Set<SecConstraint> new_constraints = new HashSet<SecConstraint>();
		for(SecConstraint constraint : constraints) {
			SecConstraint new_constraint = 
					(SecConstraint) this.opt_constraint(constraint);
			SymExpression condition = new_constraint.get_sym_condition();
			if(condition instanceof SymConstant) {
				if(((SymConstant) condition).get_bool()) {
					return new_constraint;	/* enforce true in disjunctions */
				}
				else {
					continue;				/* ignore false in disjunctions */
				}
			}
			else {
				new_constraints.add(new_constraint);
			}
		}
		
		/* 3. construct the optimized disjunctions from source */
		CirStatement statement = source.get_statement().get_statement();
		if(new_constraints.isEmpty()) {
			return SecFactory.condition_constraint(statement, Boolean.FALSE, true);
		}
		else if(new_constraints.size() == 1) {
			return new_constraints.iterator().next();
		}
		else {
			return SecFactory.disjunct_constraints(statement, new_constraints);
		}
	}
	
	/* state error extensions */
	/** it is used to record of which error has been extended **/
	private Set<String> extension_records = new HashSet<String>();
	/** the set of state errors extended from the source **/
	private Set<SecStateError> extensions = new HashSet<SecStateError>();
	/**
	 * generate the extension set of the source error within the set
	 * @param source
	 * @throws Exception
	 */
	private void ext_state_error(SecStateError source) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(extension_records.contains(source.generate_code()))
			return;
		else {
			extension_records.add(source.generate_code());
			if(source instanceof SecStatementError) {
				extensions.add(source);	
			}
			else if(source instanceof SecUniqueError) {
				extensions.add(source);	
			}
			else if(source instanceof SecExpressionError) {
				this.ext_expression_error((SecExpressionError) source);
			}
			else if(source instanceof SecReferenceError) {
				this.ext_reference_error((SecReferenceError) source);
			}
			else {
				throw new IllegalArgumentException(source.generate_code());
			}
		}
	}
	
	/* expression error extensions */
	private void ext_set_expression_error(SecSetExpressionError source) throws Exception {
		/* 1. elements getters */
		CirStatement statement = source.get_statement().get_statement();
		SymExpression orig_expression = source.get_orig_expression().get_expression();
		SymExpression muta_expression = source.get_muta_expression().get_expression();
		CirExpression expression = orig_expression.get_cir_source();
		SecType type = source.get_orig_expression().get_type();
		orig_expression = this.sym_eval(orig_expression); 
		muta_expression = this.sym_eval(muta_expression);
		SymExpression difference;
		
		/* 2.A. invalid case when orig_expr == muta_expr */
		if(orig_expression.equals(muta_expression)) {
			this.ext_state_error(SecFactory.none_error(statement)); return;
		}
		
		/* 3. type casting for muta-expression as constant or optimized as original */
		if(muta_expression instanceof SymConstant) {
			switch(type.get_vtype()) {
			case cbool:
				muta_expression = SymFactory.new_constant(((SymConstant) muta_expression).get_bool());
				break;
			case cchar:
				muta_expression = SymFactory.new_constant(((SymConstant) muta_expression).get_char());
				break;
			case csign:
			case usign:
			case caddr:
				muta_expression = SymFactory.new_constant(((SymConstant) muta_expression).get_long());
				break;
			case creal:
				muta_expression = SymFactory.new_constant(((SymConstant) muta_expression).get_double());
				break;
			default: throw new IllegalArgumentException(type.get_ctype().generate_code());
			}
		}
		this.extensions.add(SecFactory.set_expression(statement, expression, muta_expression));
		
		/* 4. extend on add_expression when difference is constant */
		difference = SymFactory.arith_sub(type.get_ctype(), muta_expression, orig_expression);
		difference = this.sym_eval(difference);
		if(difference instanceof SymConstant) {
			switch(type.get_vtype()) {
			case cchar:
			case csign:
			case usign:
			case caddr:
			case creal:
			{
				this.ext_state_error(SecFactory.add_expression(statement, expression, COperator.arith_add, difference));
				break;
			}
			default: break;
			}
		}
		
		/* 5. extend on unary-expression error of logic_not */
		switch(type.get_vtype()) {
		case cbool:
		{
			SymExpression operand = SymFactory.logic_not(orig_expression);
			operand = this.sym_eval(operand);
			if(operand.equals(muta_expression)) {
				this.ext_state_error(SecFactory.uny_expression(statement, expression, COperator.logic_not));
			}
		}
		default: break;
		}
		
		/* 6. extend on unary-expression error of bitws_rsv */
		switch(type.get_vtype()) {
		case cchar:
		case csign:
		case usign:
		{
			SymExpression operand = SymFactory.bitws_rsv(type.get_ctype(), orig_expression);
			operand = this.sym_eval(operand);
			if(operand.equals(muta_expression)) {
				this.ext_state_error(SecFactory.uny_expression(statement, expression, COperator.bit_not));
			}
		}
		default: break;
		}
		
		/* 7. extend on unary-expression error of arith_neg */
		switch(type.get_vtype()) {
		case cchar:
		case csign:
		case usign:
		case creal:
		{
			SymExpression operand = SymFactory.arith_neg(type.get_ctype(), orig_expression);
			operand = this.sym_eval(operand);
			if(operand.equals(muta_expression)) {
				this.ext_state_error(SecFactory.uny_expression(statement, expression, COperator.negative));
			}
		}
		default: break;
		}
	}
	private void ext_add_expression_error(SecAddExpressionError source) throws Exception {
		/* 1. element getters */
		CirStatement statement = source.get_statement().get_statement();
		SymExpression orig_expression = source.get_orig_expression().get_expression();
		SymExpression operand = source.get_operand().get_expression();
		CirExpression expression = orig_expression.get_cir_source();
		SecType type = source.get_orig_expression().get_type();
		orig_expression = this.sym_eval(orig_expression);
		operand = this.sym_eval(operand);
		COperator operator = source.get_operator().get_operator();
		
		/* 2. records add_expr(orig_expr, optr, optimize(operand)) */
		if(operator == COperator.arith_sub) {
			operand = SymFactory.arith_neg(operand.get_data_type(), operand);
			operand = this.sym_eval(operand); operator = COperator.arith_add;
		}
		this.extensions.add(SecFactory.add_expression(statement, expression, operator, operand));
		
		/* 3. extend on set_expr */
		SymExpression muta_expression;
		switch(operator) {
		case arith_add:	muta_expression = SymFactory.arith_add(type.get_ctype(), orig_expression, operand); break;
		case arith_mul:	muta_expression = SymFactory.arith_mul(type.get_ctype(), orig_expression, operand); break;
		case arith_div:	muta_expression = SymFactory.arith_div(type.get_ctype(), orig_expression, operand); break;
		case arith_mod:	muta_expression = SymFactory.arith_mod(type.get_ctype(), orig_expression, operand); break;
		case bit_and:	muta_expression = SymFactory.bitws_and(type.get_ctype(), orig_expression, operand); break;
		case bit_or:	muta_expression = SymFactory.bitws_ior(type.get_ctype(), orig_expression, operand); break;
		case bit_xor:	muta_expression = SymFactory.bitws_xor(type.get_ctype(), orig_expression, operand); break;
		case left_shift:muta_expression = SymFactory.bitws_lsh(type.get_ctype(), orig_expression, operand); break;
		case righ_shift:muta_expression = SymFactory.bitws_rsh(type.get_ctype(), orig_expression, operand); break;
		default: throw new IllegalArgumentException(operator.toString());
		}
		this.ext_state_error(SecFactory.set_expression(statement, expression, muta_expression));
	}
	private void ext_ins_expression_error(SecInsExpressionError source) throws Exception {
		/* 1. element getters */
		CirStatement statement = source.get_statement().get_statement();
		SymExpression orig_expression = source.get_orig_expression().get_expression();
		SymExpression operand = source.get_operand().get_expression();
		CirExpression expression = orig_expression.get_cir_source();
		SecType type = source.get_orig_expression().get_type();
		orig_expression = this.sym_eval(orig_expression);
		operand = this.sym_eval(operand);
		COperator operator = source.get_operator().get_operator();
		
		/* 2. records ins_expr(orig_expr, optr, optimize(operand)) */
		this.extensions.add(SecFactory.ins_expression(statement, expression, operator, operand));
		
		/* 3. extend on set_expr */
		SymExpression muta_expression;
		switch(operator) {
		case arith_sub:	muta_expression = SymFactory.arith_sub(type.get_ctype(), operand, orig_expression); break;
		case arith_div:	muta_expression = SymFactory.arith_div(type.get_ctype(), operand, orig_expression); break;
		case arith_mod:	muta_expression = SymFactory.arith_mod(type.get_ctype(), operand, orig_expression); break;
		case left_shift:muta_expression = SymFactory.bitws_lsh(type.get_ctype(), operand, orig_expression); break;
		case righ_shift:muta_expression = SymFactory.bitws_rsh(type.get_ctype(), operand, orig_expression); break;
		default: throw new IllegalArgumentException(operator.toString());
		}
		this.ext_state_error(SecFactory.set_expression(statement, expression, muta_expression));
	}
	private void ext_uny_expression_error(SecUnyExpressionError source) throws Exception {
		CirStatement statement = source.get_statement().get_statement();
		SymExpression orig_expression = source.get_orig_expression().get_expression();
		COperator operator = source.get_operator().get_operator();
		CirExpression expression = orig_expression.get_cir_source();
		this.extensions.add(SecFactory.uny_expression(statement, expression, operator));
		CType type = orig_expression.get_data_type();
		
		SymExpression muta_expression;
		switch(operator) {
		case negative:	muta_expression = SymFactory.arith_neg(type, orig_expression); 	break;
		case bit_not:	muta_expression = SymFactory.bitws_rsv(type, orig_expression); 	break;
		case logic_not:	muta_expression = SymFactory.logic_not(orig_expression);		break;
		default: throw new IllegalArgumentException(operator.toString());
		}
		this.ext_state_error(SecFactory.set_expression(statement, expression, muta_expression));
	}
	private void ext_expression_error(SecExpressionError source) throws Exception {
		if(source instanceof SecSetExpressionError)
			this.ext_set_expression_error((SecSetExpressionError) source);
		else if(source instanceof SecAddExpressionError)
			this.ext_add_expression_error((SecAddExpressionError) source);
		else if(source instanceof SecInsExpressionError)
			this.ext_ins_expression_error((SecInsExpressionError) source);
		else if(source instanceof SecUnyExpressionError)
			this.ext_uny_expression_error((SecUnyExpressionError) source);
		else
			throw new IllegalArgumentException(source.generate_code());
	}
	
	/* reference error extensions */
	private void ext_set_reference_error(SecSetReferenceError source) throws Exception {
		/* 1. elements getters */
		CirStatement statement = source.get_statement().get_statement();
		SymExpression orig_expression = source.get_orig_reference().get_expression();
		SymExpression muta_expression = source.get_muta_expression().get_expression();
		CirExpression expression = orig_expression.get_cir_source();
		SecType type = source.get_orig_reference().get_type();
		orig_expression = this.sym_eval(orig_expression); 
		muta_expression = this.sym_eval(muta_expression);
		SymExpression difference;
		
		/* 2.A. invalid case when orig_expr == muta_expr */
		if(orig_expression.equals(muta_expression)) {
			this.ext_state_error(SecFactory.none_error(statement)); return;
		}
		
		/* 3. type casting for muta-expression as constant or optimized as original */
		if(muta_expression instanceof SymConstant) {
			switch(type.get_vtype()) {
			case cbool:
				muta_expression = SymFactory.new_constant(((SymConstant) muta_expression).get_bool());
				break;
			case cchar:
				muta_expression = SymFactory.new_constant(((SymConstant) muta_expression).get_char());
				break;
			case csign:
			case usign:
			case caddr:
				muta_expression = SymFactory.new_constant(((SymConstant) muta_expression).get_long());
				break;
			case creal:
				muta_expression = SymFactory.new_constant(((SymConstant) muta_expression).get_double());
				break;
			default: throw new IllegalArgumentException(type.get_ctype().generate_code());
			}
		}
		this.extensions.add(SecFactory.set_reference(statement, expression, muta_expression));
		
		/* 4. extend on add_expression when difference is constant */
		difference = SymFactory.arith_sub(type.get_ctype(), muta_expression, orig_expression);
		difference = this.sym_eval(difference);
		if(difference instanceof SymConstant) {
			switch(type.get_vtype()) {
			case cchar:
			case csign:
			case usign:
			case caddr:
			case creal:
			{
				this.ext_state_error(SecFactory.add_reference(statement, expression, COperator.arith_add, difference));
				break;
			}
			default: break;
			}
		}
		
		/* 5. extend on unary-expression error of logic_not */
		switch(type.get_vtype()) {
		case cbool:
		{
			SymExpression operand = SymFactory.logic_not(orig_expression);
			operand = this.sym_eval(operand);
			if(operand.equals(muta_expression)) {
				this.ext_state_error(SecFactory.uny_reference(statement, expression, COperator.logic_not));
			}
		}
		default: break;
		}
		
		/* 6. extend on unary-expression error of bitws_rsv */
		switch(type.get_vtype()) {
		case cchar:
		case csign:
		case usign:
		{
			SymExpression operand = SymFactory.bitws_rsv(type.get_ctype(), orig_expression);
			operand = this.sym_eval(operand);
			if(operand.equals(muta_expression)) {
				this.ext_state_error(SecFactory.uny_reference(statement, expression, COperator.bit_not));
			}
		}
		default: break;
		}
		
		/* 7. extend on unary-expression error of arith_neg */
		switch(type.get_vtype()) {
		case cchar:
		case csign:
		case usign:
		case creal:
		{
			SymExpression operand = SymFactory.arith_neg(type.get_ctype(), orig_expression);
			operand = this.sym_eval(operand);
			if(operand.equals(muta_expression)) {
				this.ext_state_error(SecFactory.uny_reference(statement, expression, COperator.negative));
			}
		}
		default: break;
		}
	}
	private void ext_add_reference_error(SecAddReferenceError source) throws Exception {
		/* 1. element getters */
		CirStatement statement = source.get_statement().get_statement();
		SymExpression orig_expression = source.get_orig_reference().get_expression();
		SymExpression operand = source.get_operand().get_expression();
		CirExpression expression = orig_expression.get_cir_source();
		SecType type = source.get_orig_reference().get_type();
		orig_expression = this.sym_eval(orig_expression);
		operand = this.sym_eval(operand);
		COperator operator = source.get_operator().get_operator();
		
		/* 2. records add_expr(orig_expr, optr, optimize(operand)) */
		if(operator == COperator.arith_sub) {
			operand = SymFactory.arith_neg(operand.get_data_type(), operand);
			operand = this.sym_eval(operand); operator = COperator.arith_add;
		}
		this.extensions.add(SecFactory.add_reference(statement, expression, operator, operand));
		
		/* 3. extend on set_expr */
		SymExpression muta_expression;
		switch(operator) {
		case arith_add:	muta_expression = SymFactory.arith_add(type.get_ctype(), orig_expression, operand); break;
		case arith_mul:	muta_expression = SymFactory.arith_mul(type.get_ctype(), orig_expression, operand); break;
		case arith_div:	muta_expression = SymFactory.arith_div(type.get_ctype(), orig_expression, operand); break;
		case arith_mod:	muta_expression = SymFactory.arith_mod(type.get_ctype(), orig_expression, operand); break;
		case bit_and:	muta_expression = SymFactory.bitws_and(type.get_ctype(), orig_expression, operand); break;
		case bit_or:	muta_expression = SymFactory.bitws_ior(type.get_ctype(), orig_expression, operand); break;
		case bit_xor:	muta_expression = SymFactory.bitws_xor(type.get_ctype(), orig_expression, operand); break;
		case left_shift:muta_expression = SymFactory.bitws_lsh(type.get_ctype(), orig_expression, operand); break;
		case righ_shift:muta_expression = SymFactory.bitws_rsh(type.get_ctype(), orig_expression, operand); break;
		default: throw new IllegalArgumentException(operator.toString());
		}
		this.ext_state_error(SecFactory.set_reference(statement, expression, muta_expression));
	}
	private void ext_ins_reference_error(SecInsReferenceError source) throws Exception {
		/* 1. element getters */
		CirStatement statement = source.get_statement().get_statement();
		SymExpression orig_expression = source.get_orig_reference().get_expression();
		SymExpression operand = source.get_operand().get_expression();
		CirExpression expression = orig_expression.get_cir_source();
		SecType type = source.get_orig_reference().get_type();
		orig_expression = this.sym_eval(orig_expression);
		operand = this.sym_eval(operand);
		COperator operator = source.get_operator().get_operator();
		
		/* 2. records ins_expr(orig_expr, optr, optimize(operand)) */
		this.extensions.add(SecFactory.ins_reference(statement, expression, operator, operand));
		
		/* 3. extend on set_expr */
		SymExpression muta_expression;
		switch(operator) {
		case arith_sub:	muta_expression = SymFactory.arith_sub(type.get_ctype(), operand, orig_expression); break;
		case arith_div:	muta_expression = SymFactory.arith_div(type.get_ctype(), operand, orig_expression); break;
		case arith_mod:	muta_expression = SymFactory.arith_mod(type.get_ctype(), operand, orig_expression); break;
		case left_shift:muta_expression = SymFactory.bitws_lsh(type.get_ctype(), operand, orig_expression); break;
		case righ_shift:muta_expression = SymFactory.bitws_rsh(type.get_ctype(), operand, orig_expression); break;
		default: throw new IllegalArgumentException(operator.toString());
		}
		this.ext_state_error(SecFactory.set_reference(statement, expression, muta_expression));
	}
	private void ext_uny_reference_error(SecUnyReferenceError source) throws Exception {
		CirStatement statement = source.get_statement().get_statement();
		SymExpression orig_expression = source.get_orig_reference().get_expression();
		COperator operator = source.get_operator().get_operator();
		CirExpression expression = orig_expression.get_cir_source();
		this.extensions.add(SecFactory.uny_reference(statement, expression, operator));
		CType type = orig_expression.get_data_type();
		
		SymExpression muta_expression;
		switch(operator) {
		case negative:	muta_expression = SymFactory.arith_neg(type, orig_expression); 	break;
		case bit_not:	muta_expression = SymFactory.bitws_rsv(type, orig_expression); 	break;
		case logic_not:	muta_expression = SymFactory.logic_not(orig_expression);		break;
		default: throw new IllegalArgumentException(operator.toString());
		}
		this.ext_state_error(SecFactory.set_reference(statement, expression, muta_expression));
	}
	private void ext_reference_error(SecReferenceError source) throws Exception {
		if(source instanceof SecSetReferenceError) 
			this.ext_set_reference_error((SecSetReferenceError) source);
		else if(source instanceof SecAddReferenceError)
			this.ext_add_reference_error((SecAddReferenceError) source);
		else if(source instanceof SecInsReferenceError)
			this.ext_ins_reference_error((SecInsReferenceError) source);
		else if(source instanceof SecUnyReferenceError)
			this.ext_uny_reference_error((SecUnyReferenceError) source);
		else
			throw new IllegalArgumentException(source.generate_code());
	}
	
}
