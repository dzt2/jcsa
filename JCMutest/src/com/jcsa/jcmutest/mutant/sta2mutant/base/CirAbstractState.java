package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * 	The abstract execution state defined in the context of mutation testing.	<br>
 * 	<br>
 * 	<code>
 * 	CirAbstractState			st_class(storage_l; l_operand, r_operand)		<br>
 * 	|--	CirConditionState		st_class(statement; l_operand, r_operand)		<br>
 * 	|--	|--	CirSeedMutantState	sed_muta(statement; mutant_ID, clas_oprt)		<br>
 * 	|--	|--	CirCoverTimesState	cov_time(statement; min_times, max_times)		<br>
 * 	|--	|--	CirConstraintState	eva_cond(statement; condition, must_need)		<br>
 * 	|--	CirPathErrorState		st_class(statement; l_operand, r_operand)		<br>
 * 	|--	|--	CirBlockErrorState	mut_stmt(statement; orig_exec, muta_exec)		<br>
 * 	|--	|--	CirFlowsErrorState	mut_flow(statement; orig_next, muta_next)		<br>
 * 	|--	|--	CirTrapsErrorState	mut_trap(statement; exception, exception)		<br>
 * 	|--	CirDataErrorState		st_class(stmt|expr; l_operand, r_operand)		<br>
 * 	|--	|--	CirValueErrorState	set_expr(stmt|expr; orig_expr, muta_expr)		<br>
 * 	|--	|--	CirIncreErrorState	inc_expr(stmt|expr; orig_expr, muta_expr)		<br>
 * 	|--	|--	CirBixorErrorState	xor_expr(stmt|expr; orig_expr, muta_expr)		<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public abstract class CirAbstractState {
	
	/* definitions */
	private	CirAbstractClass	category;
	private	CirAbstractStore	location;
	private	SymbolExpression	loperand;
	private	SymbolExpression	roperand;
	protected CirAbstractState(CirAbstractClass category,
			CirAbstractStore location, 
			SymbolExpression loperand, 
			SymbolExpression roperand) throws Exception {
		if(category == null) {
			throw new IllegalArgumentException("Invalid category: null");
		}
		else if(location == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			this.category = category; this.location = location;
			this.loperand = loperand; this.roperand = roperand;
		}
	}
	
	/* getters */
	/**
	 * @return the class of this abstract execution state
	 */
	public CirAbstractClass get_state_class() { return this.category; }
	/**
	 * @return the storage location to preserve its value
	 */
	public CirAbstractStore get_state_store() { return this.location; }
	/**
	 * @return the class of the storage location of state
	 */
	public CirAbstractLType get_store_class() { return this.location.get_store_class(); }
	/**
	 * @return the left-operand for describing this state
	 */
	public SymbolExpression get_loperand()	  { return this.loperand; }
	/**
	 * @return the right-operand for describing the state
	 */
	public SymbolExpression get_roperand()    { return this.roperand; }
	/**
	 * @return the ast-based location to preserve the state
	 */
	public AstNode		get_ast_location() { return this.location.get_ast_location(); }
	/**
	 * @return the cir-based location to preserve the state
	 */
	public CirNode		get_cir_location() { return this.location.get_cir_location(); }
	/**
	 * @return the exe-based location to preserve the state
	 */
	public CirExecution get_execution() { return this.location.get_exe_location(); }
	/**
	 * @return the statement location to preserve the state
	 */
	public CirStatement get_statement() { return this.location.get_exe_location().get_statement(); }
	@Override
	public String toString() {
		return this.category + "(" + this.location + "; " + 
				this.loperand + ", " + this.roperand + ")";
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof CirAbstractState) {
			return obj.toString().equals(this.toString());
		}
		else {
			return false;
		}
	}
	
	/* factory */
	/**
	 * @param source
	 * @param statement
	 * @param min_times
	 * @param max_times
	 * @return cov_time(statement; min_times, max_times)
	 * @throws Exception
	 */
	public static CirCoverTimesState cov_time(AstNode source, CirStatement 
			statement, int min_times, int max_times) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else if(min_times > max_times || max_times <= 0) {
			throw new IllegalArgumentException(min_times + " -> " + max_times);
		}
		else {
			return new CirCoverTimesState(CirAbstractStore.
					new_store(source, statement), min_times, max_times);
		}
	}
	/**
	 * @param source
	 * @param statement
	 * @param condition
	 * @param must_need
	 * @return eva_cond(statement; condition, must_need)
	 * @throws Exception
	 */
	public static CirConstraintState eva_cond(AstNode source, CirStatement
			statement, Object condition, boolean must_need) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			return new CirConstraintState(CirAbstractStore.
					new_store(source, statement), condition, must_need);
		}
	}
	/**
	 * @param source
	 * @param statement
	 * @param mutant
	 * @return sed_muta(statement; mutant_ID, clas_oprt)
	 * @throws Exception
	 */
	public static CirSeedMutantState sed_muta(AstNode source, CirStatement statement, Mutant mutant) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant as: null");
		}
		else {
			return new CirSeedMutantState(CirAbstractStore.new_store(source, statement), mutant);
		}
	}
	/**
	 * @param source
	 * @param statement
	 * @param muta_exec
	 * @return mut_stmt(statement; !muta_exec, muta_exec)
	 * @throws Exception
	 */
	public static CirBlockErrorState mut_stmt(AstNode source, CirStatement statement, boolean muta_exec) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else {
			return new CirBlockErrorState(CirAbstractStore.new_store(source, statement), !muta_exec, muta_exec);
		}
	}
	/**
	 * @param source
	 * @param statement
	 * @param orig_next
	 * @param muta_next
	 * @return mut_flow(statement; orig_next, muta_next)
	 * @throws Exception
	 */
	public static CirFlowsErrorState mut_flow(AstNode source, CirStatement statement, 
			CirExecution orig_next, CirExecution muta_next) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else if(orig_next == null) {
			throw new IllegalArgumentException("Invalid orig_next: null");
		}
		else if(muta_next == null) {
			throw new IllegalArgumentException("Invalid muta_next: null");
		}
		else {
			return new CirFlowsErrorState(CirAbstractStore.new_store(source, statement), orig_next, muta_next);
		}
	}
	/**
	 * @param source
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	public static CirTrapsErrorState mut_trap(AstNode source, CirStatement statement) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else {
			return new CirTrapsErrorState(CirAbstractStore.new_store(source, statement));
		}
	}
	/**
	 * @param source
	 * @param expression
	 * @param orig_value
	 * @param muta_value
	 * @return set_expr(expression; orig_value, muta_value)
	 * @throws Exception
	 */
	public static CirValueErrorState set_expr(AstNode source, CirExpression expression, 
			Object orig_value, Object muta_value) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(orig_value == null) {
			throw new IllegalArgumentException("Invalid orig_value: null");
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else {
			SymbolExpression ovalue, mvalue;
			if(SymbolFactory.is_bool(expression.get_data_type())) {
				ovalue = SymbolFactory.sym_condition(orig_value, true);
				mvalue = SymbolFactory.sym_condition(muta_value, true);
			}
			else {
				ovalue = SymbolFactory.sym_expression(orig_value);
				mvalue = SymbolFactory.sym_expression(muta_value);
			}
			return new CirValueErrorState(CirAbstractStore.new_store(source, expression), ovalue, mvalue);
		}
	}
	/**
	 * @param source
	 * @param expression
	 * @param orig_value
	 * @param muta_value
	 * @return inc_expr(expression, orig_value, difference)
	 * @throws Exception
	 */
	public static CirIncreErrorState inc_expr(AstNode source, CirExpression expression, 
			Object orig_value, Object muta_value) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(orig_value == null) {
			throw new IllegalArgumentException("Invalid orig_value: null");
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else {
			if(StateMutations.is_address(expression) || StateMutations.is_numeric(expression)) {
				return new CirIncreErrorState(CirAbstractStore.new_store(source, expression),
						SymbolFactory.sym_expression(orig_value), SymbolFactory.sym_expression(muta_value));
			}
			else {
				throw new IllegalArgumentException(expression.get_data_type().generate_code());
			}
		}
	}
	/**
	 * @param source
	 * @param expression
	 * @param orig_value
	 * @param muta_value
	 * @return inc_expr(expression, orig_value, difference)
	 * @throws Exception
	 */
	public static CirBixorErrorState xor_expr(AstNode source, CirExpression expression, 
			Object orig_value, Object muta_value) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(orig_value == null) {
			throw new IllegalArgumentException("Invalid orig_value: null");
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else {
			if(StateMutations.is_integer(expression)) {
				return new CirBixorErrorState(CirAbstractStore.new_store(source, expression),
						SymbolFactory.sym_expression(orig_value), SymbolFactory.sym_expression(muta_value));
			}
			else {
				throw new IllegalArgumentException(expression.get_data_type().generate_code());
			}
		}
	}
	/**
	 * @param source
	 * @param statement
	 * @param identifier
	 * @param muta_value
	 * @return
	 * @throws Exception
	 */
	public static CirValueErrorState set_expr(AstNode source, CirStatement statement,
			SymbolExpression identifier, Object muta_value) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(statement == null) {
			throw new IllegalArgumentException("Invalid statements: null");
		}
		else if(identifier == null) {
			throw new IllegalArgumentException("Invalid identifier: null");
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else {
			if(SymbolFactory.is_bool(identifier)) {
				return new CirValueErrorState(CirAbstractStore.new_store(source, statement), 
								identifier, SymbolFactory.sym_condition(muta_value, true));
			}
			else {
				return new CirValueErrorState(CirAbstractStore.new_store(source, statement), 
									identifier, SymbolFactory.sym_expression(muta_value));
			}
		}
	}
	/**
	 * @param source
	 * @param statement
	 * @param identifier
	 * @param muta_value
	 * @return inc_expr(statement; orig_value, difference)
	 * @throws Exception
	 */
	public static CirIncreErrorState inc_expr(AstNode source, CirStatement statement, 
			SymbolExpression identifier, Object muta_value) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(statement == null) {
			throw new IllegalArgumentException("Invalid statements: null");
		}
		else if(identifier == null) {
			throw new IllegalArgumentException("Invalid identifier: null");
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else if(SymbolFactory.is_numb(identifier) ||
				SymbolFactory.is_addr(identifier) || 
				SymbolFactory.is_real(identifier)) {
			return new CirIncreErrorState(CirAbstractStore.new_store(source, statement), 
					identifier, SymbolFactory.sym_expression(muta_value));
		}
		else {
			throw new IllegalArgumentException(identifier.get_simple_code());
		}
	}
	/**
	 * @param source
	 * @param statement
	 * @param identifier
	 * @param muta_value
	 * @return inc_expr(statement; orig_value, difference)
	 * @throws Exception
	 */
	public static CirBixorErrorState xor_expr(AstNode source, CirStatement statement, 
			SymbolExpression identifier, Object muta_value) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(statement == null) {
			throw new IllegalArgumentException("Invalid statements: null");
		}
		else if(identifier == null) {
			throw new IllegalArgumentException("Invalid identifier: null");
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else if(SymbolFactory.is_numb(identifier)) {
			return new CirBixorErrorState(CirAbstractStore.new_store(source, statement), 
					identifier, SymbolFactory.sym_expression(muta_value));
		}
		else {
			throw new IllegalArgumentException(identifier.get_simple_code());
		}
	}
	
}
