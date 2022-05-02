package com.jcsa.jcmutest.mutant.ctx2mutant.tree;

import com.jcsa.jcmutest.mutant.ctx2mutant.ContextMutations;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class ContextAnnotation {
	
	/* definitions */
	/** the category of the annotation being defined **/
	private ContextAnnotationClass	category;
	/** the location where the annotation is defined **/
	private	AstCirNode				location;
	/** the symbolic expression to the left-parameter **/
	private	SymbolExpression		loperand;
	/** the symbolic expression as the right-parameter **/
	private	SymbolExpression		roperand;
	/**
	 * It creates an annotation with specified category and location.
	 * @param category	the category of the annotation being defined
	 * @param location	the location where the annotation is defined
	 * @param parameter	the symbolic value to define this annotation
	 * @throws Exception
	 */
	private ContextAnnotation(ContextAnnotationClass category,
			AstCirNode location, SymbolExpression loperand,
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
			this.loperand = ContextMutations.evaluate(loperand, null, null);
			this.roperand = ContextMutations.evaluate(roperand, null, null);
		}
	}
	
	/* getters */
	/**
	 * @return	the category of the annotation being defined
	 */
	public ContextAnnotationClass 	get_category() 	{ return this.category; }
	/**
	 * @return	the location where the annotation is defined
	 */
	public AstCirNode				get_location()	{ return this.location; }
	/**
	 * @return 	the symbolic expression to the left-parameter
	 */
	public SymbolExpression			get_loperand() 	{ return this.loperand; }
	/**
	 * @return	the symbolic expression as the right-parameter 
	 */
	public SymbolExpression			get_roperand()	{ return this.roperand; }
	@Override
	public String toString() { 
		return this.category + "(" + this.location.get_node_id() + 
				"; " + this.loperand + ", " + this.roperand + ")"; 
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ContextAnnotation) {
			return this.toString().equals(obj.toString());
		}
		else {
			return false;
		}
	}
	
	/* factory */
	/**
	 * @param statement
	 * @param min_times
	 * @param max_times
	 * @return cov_stmt(statement; min_times, max_times)
	 * @throws Exception
	 */
	protected static ContextAnnotation cov_time(AstCirNode statement, int min_times, int max_times) throws Exception {
		if(statement == null || !statement.is_statement_node()) {
			throw new IllegalArgumentException("Invalid statement: " + statement);
		}
		else if(min_times > max_times || min_times <= 0) {
			throw new IllegalArgumentException(min_times + " |--> " + max_times);
		}
		else {
			return new ContextAnnotation(ContextAnnotationClass.cov_time, statement, 
					SymbolFactory.sym_constant(Integer.valueOf(min_times)),
					SymbolFactory.sym_constant(Integer.valueOf(max_times)));
		}
	}
	/**
	 * @param statement
	 * @param condition
	 * @param must_need
	 * @return
	 * @throws Exception
	 */
	protected static ContextAnnotation eva_cond(AstCirNode statement, Object condition, boolean must_need) throws Exception {
		if(statement == null || !statement.is_statement_node()) {
			throw new IllegalArgumentException("Invalid statement: " + statement);
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			return new ContextAnnotation(ContextAnnotationClass.eva_cond, statement,
					SymbolFactory.sym_condition(condition, true), 
					SymbolFactory.sym_constant(Boolean.valueOf(must_need)));
		}
	}
	/**
	 * @param statement
	 * @param muta_exec
	 * @return
	 * @throws Exception
	 */
	protected static ContextAnnotation set_stmt(AstCirNode statement, boolean muta_exec) throws Exception {
		if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: " + statement);
		}
		else {
			return new ContextAnnotation(ContextAnnotationClass.set_stmt, statement,
					SymbolFactory.sym_constant(Boolean.valueOf(!muta_exec)),
					SymbolFactory.sym_constant(Boolean.valueOf(muta_exec)));
		}
	}
	/**
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	protected static ContextAnnotation trp_stmt(AstCirNode statement) throws Exception {
		if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: " + statement);
		}
		else {
			return new ContextAnnotation(ContextAnnotationClass.trp_stmt, statement.
					module_of(), ContextMutations.trap_value, ContextMutations.trap_value);
		}
	}
	/**
	 * @param statement
	 * @param orig_next
	 * @param muta_next
	 * @return
	 * @throws Exception
	 */
	protected static ContextAnnotation set_flow(AstCirNode statement, AstCirNode orig_next, AstCirNode muta_next) throws Exception {
		if(statement == null || !statement.is_statement_node()) {
			throw new IllegalArgumentException("Invalid statement: " + statement);
		}
		else if(orig_next == null) {
			throw new IllegalArgumentException("Invalid orig_next: null");
		}
		else if(muta_next == null) {
			throw new IllegalArgumentException("Invalid muta_next: null");
		}
		else {
			return new ContextAnnotation(ContextAnnotationClass.set_flow, statement,
					SymbolFactory.sym_constant(Integer.valueOf(orig_next.get_node_id())),
					SymbolFactory.sym_constant(Integer.valueOf(muta_next.get_node_id())));
		}
	}
	/**
	 * @param location
	 * @param orig_expr
	 * @param muta_value
	 * @return
	 * @throws Exception
	 */
	protected static ContextAnnotation set_expr(AstCirNode location, SymbolExpression orig_expr, SymbolExpression muta_expr) throws Exception {
		if(location == null) {
			throw new IllegalArgumentException("Invalid location: " + location);
		}
		else if(orig_expr == null) {
			throw new IllegalArgumentException("Invalid statement: " + orig_expr);
		}
		else if(muta_expr == null) {
			throw new IllegalArgumentException("Invalid statement: " + muta_expr);
		}
		else {
			return new ContextAnnotation(ContextAnnotationClass.set_expr, location, orig_expr, muta_expr);
		}
	}
	/**
	 * @param location
	 * @param orig_expr
	 * @param muta_value
	 * @return
	 * @throws Exception
	 */
	protected static ContextAnnotation inc_expr(AstCirNode location, SymbolExpression orig_expr, SymbolExpression difference) throws Exception {
		if(location == null) {
			throw new IllegalArgumentException("Invalid location: " + location);
		}
		else if(orig_expr == null) {
			throw new IllegalArgumentException("Invalid orig_expr: " + orig_expr);
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference: " + difference);
		}
		else {
			return new ContextAnnotation(ContextAnnotationClass.inc_expr, location, orig_expr, difference);
		}
	}
	/**
	 * @param location
	 * @param orig_expr
	 * @param muta_value
	 * @return
	 * @throws Exception
	 */
	protected static ContextAnnotation xor_expr(AstCirNode location, SymbolExpression orig_expr, SymbolExpression difference) throws Exception {
		if(location == null) {
			throw new IllegalArgumentException("Invalid location: " + location);
		}
		else if(orig_expr == null) {
			throw new IllegalArgumentException("Invalid orig_expr: " + orig_expr);
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference: " + difference);
		}
		else {
			return new ContextAnnotation(ContextAnnotationClass.xor_expr, location, orig_expr, difference);
		}
	}
	
}
