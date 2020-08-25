package com.jcsa.jcmutest.mutant.sad2mutant.muta;

import java.util.List;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArrayExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCastExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCommaExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConditionalExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFunCallExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstParanthExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstSizeofExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.irlang.AstCirPair;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirSaveAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;

/**
 * It generates the infection part in the symbolic graph, including:<br>
 * 	1. execute_on(location, 1) as requirement to reach the faulty statement;<br>
 * 	2. relation with constraint as the infection condition to initial error;<br>
 * 	3. targets from the above relations as the initial state errors being created.<br>
 * 
 * @author yukimula
 *
 */
public abstract class SadInfection {
	
	/* location identification */
	/**
	 * @param tree
	 * @param location
	 * @return the cir-code range to which the location refers or null
	 * @throws Exception
	 */
	protected AstCirPair get_cir_range(CirTree tree, AstNode location) throws Exception {
		if(tree.has_cir_range(location)) 
			return tree.get_cir_range(location);
		else return null;
	}
	/**
	 * @param tree
	 * @param location
	 * @param type
	 * @return the cir-nodes w.r.t. the location with specified type
	 * @throws Exception
	 */
	protected List<CirNode> get_cir_nodes(CirTree tree, AstNode location, Class<?> type) throws Exception {
		return tree.get_cir_nodes(location, type);
	}
	/**
	 * @param tree
	 * @param location
	 * @return the use point of the result of the expression at location.
	 * @throws Exception
	 */
	protected CirExpression find_result(CirTree tree, AstNode location) throws Exception {
		AstCirPair range = this.get_cir_range(tree, location);
		if(range == null || !range.computational()) {
			return null;	/* not available to represent value */
		}
		else if(location instanceof AstExpression) {
			if(location instanceof AstIdExpression
				|| location instanceof AstConstant
				|| location instanceof AstLiteral
				|| location instanceof AstArrayExpression
				|| location instanceof AstCastExpression
				|| location instanceof AstFieldExpression
				|| location instanceof AstSizeofExpression
				|| location instanceof AstInitializerBody
				|| location instanceof AstConditionalExpression) {
				return range.get_result();
			}
			else if(location instanceof AstConstExpression) {
				return this.find_result(tree, 
						((AstConstExpression) location).get_expression());
			}
			else if(location instanceof AstParanthExpression) {
				return this.find_result(tree, 
						((AstParanthExpression) location).get_sub_expression());
			}
			else if(location instanceof AstCommaExpression) {
				int index = ((AstCommaExpression) location).number_of_arguments();
				return this.find_result(tree, ((AstCommaExpression) location).get_expression(index - 1));
			}
			else if(location instanceof AstUnaryExpression) {
				switch(((AstUnaryExpression) location).get_operator().get_operator()) {
				case increment:
				case decrement:
				{
					CirAssignStatement statement = (CirAssignStatement) get_cir_nodes(
								tree, location, CirIncreAssignStatement.class).get(0);
					return statement.get_rvalue();
				}
				default:	return range.get_result();
				}
			}
			else if(location instanceof AstPostfixExpression) {
				switch(((AstPostfixExpression) location).get_operator().get_operator()) {
				case increment:
				case decrement:
				{
					CirAssignStatement statement = (CirAssignStatement) get_cir_nodes(
								tree, location, CirSaveAssignStatement.class).get(0);
					return statement.get_rvalue();
				}
				default: 	return range.get_result();
				}
			}
			else if(location instanceof AstBinaryExpression) {
				switch(((AstBinaryExpression) location).get_operator().get_operator()) {
				case assign:
				case arith_add_assign:
				case arith_sub_assign:
				case arith_mul_assign:
				case arith_div_assign:
				case arith_mod_assign:
				case bit_and_assign:
				case bit_or_assign:
				case bit_xor_assign:
				case left_shift_assign:
				case righ_shift_assign:
				{
					CirAssignStatement statement = (CirAssignStatement) get_cir_nodes(
									tree, location, CirAssignStatement.class).get(0);
					return statement.get_rvalue();
				}
				default:	return range.get_result();
				}
			}
			else if(location instanceof AstFunCallExpression) {
				CirWaitAssignStatement statement = (CirWaitAssignStatement) get_cir_nodes(
									tree, location, CirWaitAssignStatement.class).get(0);
				return statement.get_rvalue();
			}
			else {
				throw new RuntimeException("Unsupport: " + location);
			}
		}
		else {
			return range.get_result();
		}
	}
	/**
	 * @param tree
	 * @param location
	 * @return the first statement being executed for the location to be started
	 * @throws Exception
	 */
	protected CirStatement find_beg_stmt(CirTree tree, AstNode location) throws Exception {
		AstCirPair range = this.get_cir_range(tree, location);
		if(range == null || !range.executional()) {
			return null;	/* not available to locate the first statement */
		}
		else if(location instanceof AstExpression) {
			
		}
		else if(location instanceof AstStatement) {
			
		}
		else {
			return range.get_beg_statement();
		}
	}
	
	
	
	
	
}
