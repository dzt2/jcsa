package com.jcsa.jcmuta.mutant.sem2mutation.muta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.SemanticMutationUtil;
import com.jcsa.jcmuta.project.Mutant;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.AstCirPair;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * Used to parse from AstMutation to generate SemanticMutation.
 * @author yukimula
 *
 */
public abstract class SemanticMutationParser {
	
	/* constants */
	/** invalid address value **/
	public static final String InvAddr = "invalid_addr";
	/** to represent the null pointer value in C **/
	public static final String Nullptr = "nullptr";
	/** the range of overflow in numeric domain **/
	public static final String Overflow = "overflow";
	/** when one-bit smaller than this will cause 0-mask **/
	protected static final int min_bit_ones = 2;
	/** the maximal bits to be shifted to get 0 **/
	public static final int max_shifting = 8;
	
	protected CirTree cir_tree;
	protected SemanticMutation sem_mutation;
	
	/**
	 * parse from AstMutation to generate SemanticMutation
	 * @param mutant
	 * @return
	 * @throws Exception
	 */
	public SemanticMutation parse(Mutant mutant) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("Invalid mutant: null");
		else {
			/* data extraction */
			AstMutation ast_mutation = mutant.get_mutation();
			this.cir_tree = mutant.get_space().get_source_file().get_cir_tree();
			
			/* construct the coverage and infection of state errors */
			CirStatement statement = this.get_statement(ast_mutation);
			if(statement != null) {
				this.sem_mutation = new SemanticMutation(statement);
				this.generate_infections(ast_mutation);
				//if(sem_mutation.number_of_infections() > 0) 
				return sem_mutation;
			}
			
			return null;	/* either unreachable or causes no state errors */
		}
	}
	
	/**
	 * determine the faulty statement where the mutant is seeded
	 * @param ast_mutation
	 * @param cir_tree
	 * @return
	 * @throws Exception
	 */
	protected abstract CirStatement get_statement(AstMutation ast_mutation) throws Exception;
	/**
	 * construct the state infection in the semantic mutation based on AST mutation and CIR code
	 * @param ast_mutation
	 * @param cir_tree
	 * @throws Exception
	 */
	protected abstract void generate_infections(AstMutation ast_mutation) throws Exception;
	
	/* utility methods */
	/**
	 * get the CIR code range with respect to the AST code element.
	 * @param cir_tree
	 * @param location
	 * @return
	 * @throws Exception
	 */
	protected AstCirPair get_cir_range(AstNode location) throws Exception {
		if(cir_tree.has_cir_range(location)) {
			return cir_tree.get_cir_range(location);
		}
		else return null;
	}
	/**
	 * get the statement as the begining of the node
	 * @param cir_tree
	 * @param location
	 * @return
	 * @throws Exception
	 */
	protected CirStatement get_beg_statement(AstNode location) throws Exception {
		if(cir_tree.has_cir_range(location)) {
			return cir_tree.get_cir_range(location).get_beg_statement();
		}
		else return null;
	}
	/**
	 * get the final statement as the end of the node
	 * @param cir_tree
	 * @param location
	 * @return
	 * @throws Exception
	 */
	protected CirStatement get_end_statement(AstNode location) throws Exception {
		if(cir_tree.has_cir_range(location)) {
			return cir_tree.get_cir_range(location).get_end_statement();
		}
		else return null;
	}
	/**
	 * get the expression that represents the usage of the result of the AST node
	 * @param cir_tree
	 * @param location
	 * @return
	 * @throws Exception
	 */
	protected CirExpression get_result(AstNode location) throws Exception {
		if(cir_tree.has_cir_range(location)) {
			return cir_tree.get_cir_range(location).get_result();
		}
		else return null;
	}
	/**
	 * get the statement either the first or the prev to the AST code element.
	 * @param cir_tree
	 * @param location
	 * @return
	 * @throws Exception
	 */
	protected CirStatement get_prev_statement(AstNode location) throws Exception {
		if(location instanceof AstExpressionStatement) {
			if(((AstExpressionStatement) location).has_expression()) {
				location = CTypeAnalyzer.get_expression_of(
						((AstExpressionStatement) location).get_expression());
			}
		}
		CirStatement beg_statement = this.get_beg_statement(location);
		
		if(beg_statement != null) { return beg_statement; }
		else {
			AstNode parent = location.get_parent(), child = location;
			while(parent != null) {
				boolean previous = true;
				CirStatement first_statement = null;
				for(int k = 0; k < parent.number_of_children(); k++) {
					if(child == parent.get_child(k)) { previous = false; }
					else if(previous) {
						CirStatement end_stmt = this.get_end_statement(parent.get_child(k));
						if(end_stmt != null) { first_statement = end_stmt; }
					}
					else if(first_statement != null) {
						CirStatement beg_stmt = this.get_beg_statement(parent.get_child(k));
						if(beg_stmt != null) { first_statement = beg_stmt; }
					}
				}
				
				if(first_statement != null) return first_statement;
				else {
					child = parent; parent = parent.get_parent();
				}
			}
			return null;
		}
	}
	/**
	 * get the CIR node with respect to the type specified within the AST element
	 * @param cir_tree
	 * @param location
	 * @param type
	 * @return
	 * @throws Exception
	 */
	protected CirNode get_cir_node(AstNode location, Class<?> type) throws Exception {
		Iterable<CirNode> cir_nodes = cir_tree.get_cir_nodes(location);
		for(CirNode cir_node : cir_nodes) {
			if(type.isInstance(cir_node)) return cir_node;
		}
		return null;
	}
	
	/* mutation infection */
	protected void infect(SemanticAssertion state_error) throws Exception {
		this.sem_mutation.infect(new SemanticAssertion[] {}, new SemanticAssertion[] {state_error});
	}
	protected void infect(Collection<SemanticAssertion> state_errors) throws Exception {
		SemanticAssertion[] error_list = new SemanticAssertion[state_errors.size()];
		int k = 0;
		for(SemanticAssertion state_error : state_errors) {
			error_list[k++] = state_error;
		}
		this.sem_mutation.infect(new SemanticAssertion[] {}, error_list);
	}
	protected void infect(SemanticAssertion[] state_errors) throws Exception {
		this.sem_mutation.infect(new SemanticAssertion[] {}, state_errors);
	}
	protected void infect(SemanticAssertion constraint, SemanticAssertion state_error) throws Exception {
		this.sem_mutation.infect(new SemanticAssertion[] {constraint}, new SemanticAssertion[] {state_error});
	}
	protected void infect(SemanticAssertion constraint, Collection<SemanticAssertion> state_errors) throws Exception {
		SemanticAssertion[] error_list = new SemanticAssertion[state_errors.size()];
		int k = 0;
		for(SemanticAssertion state_error : state_errors) {
			error_list[k++] = state_error;
		}
		this.sem_mutation.infect(new SemanticAssertion[] {constraint}, error_list);
	}
	protected void infect(SemanticAssertion constraint, SemanticAssertion[] state_errors) throws Exception {
		this.sem_mutation.infect(new SemanticAssertion[] {constraint}, state_errors);
	}
	protected void infect(Collection<SemanticAssertion> constraints, SemanticAssertion state_error) throws Exception {
		SemanticAssertion[] list = new SemanticAssertion[constraints.size()];
		int k = 0;
		for(SemanticAssertion constraint : constraints) {
			list[k] = constraint;
		}
		this.sem_mutation.infect(list, new SemanticAssertion[] {state_error});
	}
	protected void infect(Collection<SemanticAssertion> constraints, SemanticAssertion[] state_errors) throws Exception {
		SemanticAssertion[] list = new SemanticAssertion[constraints.size()];
		int k = 0;
		for(SemanticAssertion constraint : constraints) {
			list[k] = constraint;
		}
		this.sem_mutation.infect(list, state_errors);
	}
	protected void infect(Collection<SemanticAssertion> constraints, Collection<SemanticAssertion> state_errors) throws Exception {
		SemanticAssertion[] list = new SemanticAssertion[constraints.size()];
		int k = 0;
		for(SemanticAssertion constraint : constraints) {
			list[k] = constraint;
		}
		SemanticAssertion[] error_list = new SemanticAssertion[state_errors.size()];
		k = 0;
		for(SemanticAssertion state_error : state_errors) {
			error_list[k++] = state_error;
		}
		this.sem_mutation.infect(list, error_list);
	}
	protected void infect(SemanticAssertion[] constraints, SemanticAssertion state_error) throws Exception {
		this.sem_mutation.infect(constraints, new SemanticAssertion[] {state_error});
	}
	protected void infect(SemanticAssertion[] constraints, SemanticAssertion[] state_errors) throws Exception {
		this.sem_mutation.infect(constraints, state_errors);
	}
	protected void infect(SemanticAssertion[] constraints, Collection<SemanticAssertion> state_errors) throws Exception {
		SemanticAssertion[] error_list = new SemanticAssertion[state_errors.size()];
		int k = 0;
		for(SemanticAssertion state_error : state_errors) {
			error_list[k++] = state_error;
		}
		this.sem_mutation.infect(constraints, error_list);
	}
	
	/**
	 * generate the semantic mutation when both operands are constants
	 * @param orig_operator
	 * @param loperand
	 * @param roperand
	 * @param muta_operator
	 * @throws Exception
	 */
	protected void compute_const_to_const(CirExpression expression, COperator orig_operator, 
			Object loperand, Object roperand, COperator muta_operator) throws Exception {
		if(loperand instanceof CirExpression) 
			loperand = SemanticMutationUtil.get_constant((CirExpression) loperand);
		if(roperand instanceof CirExpression) 
			roperand = SemanticMutationUtil.get_constant((CirExpression) roperand);
		
		Object orig_value = SemanticMutationUtil.compute(orig_operator, loperand, roperand);
		Object muta_value = SemanticMutationUtil.compute(muta_operator, loperand, roperand);
		
		if(orig_value instanceof Boolean)
			orig_value = SemanticMutationUtil.cast_to(CBasicTypeImpl.long_type, orig_value);
		if(muta_value instanceof Boolean)
			muta_value = SemanticMutationUtil.cast_to(CBasicTypeImpl.long_type, muta_value);
		
		List<SemanticAssertion> state_errors = new ArrayList<SemanticAssertion>();
		if(orig_value instanceof Long) {
			long ori_value = ((Long) orig_value).longValue();
			if(muta_value instanceof Long) {
				long mut_value = ((Long) muta_value).longValue();
				if(ori_value == mut_value) return;
				else {
					state_errors.add(sem_mutation.get_assertions().diff_value(expression, mut_value - ori_value));
				}
			}
			else if(muta_value instanceof Double) {
				double mut_value = ((Double) muta_value).doubleValue();
				if(ori_value == mut_value) return;
				else {
					state_errors.add(sem_mutation.get_assertions().diff_value(expression, mut_value - ori_value));
				}
			}
			else {
				throw new IllegalArgumentException("Invalid muta_value");
			}
		}
		else if(orig_value instanceof Double) {
			double ori_value = ((Double) orig_value).doubleValue();
			if(muta_value instanceof Long) {
				long mut_value = ((Long) muta_value).longValue();
				if(ori_value == mut_value) return;
				else {
					state_errors.add(sem_mutation.get_assertions().diff_value(expression, mut_value - ori_value));
				}
			}
			else if(muta_value instanceof Double) {
				double mut_value = ((Double) muta_value).doubleValue();
				if(ori_value == mut_value) return;
				else {
					state_errors.add(sem_mutation.get_assertions().diff_value(expression, mut_value - ori_value));
				}
			}
			else {
				throw new IllegalArgumentException("Invalid muta_value");
			}
		}
		else {
			throw new IllegalArgumentException("Unable to compute: " + orig_value);
		}
		
		if(!state_errors.isEmpty()) this.infect(state_errors);
	}
	/**
	 * get the number of 1 in bit-string of value
	 * @param value
	 * @return
	 * @throws Exception
	 */
	protected int count_bit_ones(long value) throws Exception {
		int counter = 0;
		while(value != 0) {
			if(value % 2 != 0) {
				counter++;
			}
			value = value / 2;
		}
		return counter;
	}
	/**
	 * 
	 * @param expression
	 * @param orig_operator
	 * @param loperand
	 * @param roperand
	 * @param muta_operator
	 * @throws Exception
	 */
	protected void decide_cons_to_cons(CirExpression expression, COperator orig_operator, 
			Object loperand, Object roperand, COperator muta_operator) throws Exception {
		Object orig_value = SemanticMutationUtil.compute(orig_operator, loperand, roperand);
		Object muta_value = SemanticMutationUtil.compute(muta_operator, loperand, roperand);
		
		if(orig_value instanceof Boolean) { /** do nothing **/ }
		else if(orig_value instanceof Long) {
			orig_value = Boolean.valueOf(((Long) orig_value).longValue() != 0);
		}
		else if(orig_value instanceof Double) {
			orig_value = Boolean.valueOf(((Double) orig_value).doubleValue() != 0);
		}
		else {
			throw new IllegalArgumentException("Invalid orig_value: " + orig_value);
		}
		
		if(muta_value instanceof Boolean) { /** do nothing **/ }
		else if(muta_value instanceof Long) {
			muta_value = Boolean.valueOf(((Long) muta_value).longValue() != 0);
		}
		else if(muta_value instanceof Double) {
			muta_value = Boolean.valueOf(((Double) muta_value).doubleValue() != 0);
		}
		else {
			throw new IllegalArgumentException("Invalid muta_value: " + muta_value);
		}
		
		boolean value1 = ((Boolean) orig_value).booleanValue();
		boolean value2 = ((Boolean) muta_value).booleanValue();
		if(value1 != value2) {
			this.infect(new SemanticAssertion[] {
				sem_mutation.get_assertions().set_value(expression, Boolean.valueOf(value2))
			});
		}
	}
	/**
	 * true:
	 * 	bool 	==> equal_with(expr, true)
	 * 	int		==> not_equal(expr, 0)
	 * 	double	==> not_equal(expr, 0)
	 * 	point	==> not_equal(expr, Nullptr)
	 * @param expression
	 * @param value
	 * @return
	 * @throws Exception
	 */
	protected SemanticAssertion bool_verification(
			CirExpression expression, boolean value) throws Exception {
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		if(CTypeAnalyzer.is_boolean(data_type)) {
			if(value) {
				return sem_mutation.get_assertions().equal_with(expression, Boolean.TRUE);
			}
			else {
				return sem_mutation.get_assertions().equal_with(expression, Boolean.FALSE);
			}
		}
		else if(CTypeAnalyzer.is_number(data_type)) {
			if(value) {
				return sem_mutation.get_assertions().not_equals(expression, Long.valueOf(0));
			}
			else {
				return sem_mutation.get_assertions().equal_with(expression, Long.valueOf(0));
			}
		}
		else if(CTypeAnalyzer.is_pointer(data_type)) {
			if(value) {
				return sem_mutation.get_assertions().not_equals(expression, Nullptr);
			}
			else {
				return sem_mutation.get_assertions().equal_with(expression, Nullptr);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid data type");
		}
	}
	
	public void set_cir_tree(CirTree cir_tree) { this.cir_tree = cir_tree; }
	public void set_sem_mutation(SemanticMutation mutation) { this.sem_mutation = mutation; }
	
}
