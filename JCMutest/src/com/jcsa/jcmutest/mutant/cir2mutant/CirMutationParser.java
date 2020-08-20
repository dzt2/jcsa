package com.jcsa.jcmutest.mutant.cir2mutant;

import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPointUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArrayExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCastExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCommaExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConditionalExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFunCallExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstParanthExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstSizeofExpression;
import com.jcsa.jcparse.lang.irlang.AstCirPair;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirBinAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirSaveAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;

/**
 * It parses the ast-mutation to generate cir-mutation
 * 
 * @author yukimula
 *
 */
public abstract class CirMutationParser {
	
	/**
	 * @param tree the syntax tree of C-intermediate representation
	 * @param source the ast-mutation from which the cir-mutations are produced
	 * @param targets the set of cir-mutations parsed from the ast-mutation
	 * @throws Exception
	 */
	protected abstract void parse(CirTree tree, AstMutation 
			source, List<CirMutation> targets) throws Exception;
	
	/* utility methods */
	/**
	 * @param tree syntactic tree for intermediate representation language
	 * @param location the location in abstract syntax tree from C program
	 * @return the range of cir-code to which the ast-location corresponds
	 * @throws Exception
	 */
	protected AstCirPair get_cir_range(CirTree tree, AstNode location) throws Exception {
		if(tree.has_cir_range(location)) {
			return tree.get_cir_range(location);
		}
		else {
			return null;
		}
	}
	/**
	 * @param tree
	 * @param location
	 * @return the cir-nodes to which the location corresponds w.r.t. the specified type
	 * @throws Exception
	 */
	protected List<CirNode> get_cir_nodes(CirTree tree, AstNode location, Class<?> type) throws Exception {
		return tree.get_cir_nodes(location, type);
	}
	/**
	 * @param tree
	 * @param location
	 * @return the expression to which the location corresponds
	 * @throws Exception
	 */
	protected CirExpression get_use_point(CirTree tree, AstNode location) throws Exception {
		AstCirPair range = this.get_cir_range(tree, location);
		if(range == null || !range.computational()) {
			throw new IllegalArgumentException("Not computable: " + location);
		}
		else if(location instanceof AstIdExpression
				|| location instanceof AstConstant
				|| location instanceof AstLiteral
				|| location instanceof AstSizeofExpression
				|| location instanceof AstArrayExpression
				|| location instanceof AstCastExpression
				|| location instanceof AstFieldExpression
				|| location instanceof AstArithBinaryExpression
				|| location instanceof AstBitwiseBinaryExpression
				|| location instanceof AstShiftBinaryExpression
				|| location instanceof AstRelationExpression
				|| location instanceof AstArithUnaryExpression
				|| location instanceof AstBitwiseUnaryExpression
				|| location instanceof AstLogicUnaryExpression
				|| location instanceof AstPointUnaryExpression
				|| location instanceof AstLogicBinaryExpression
				|| location instanceof AstConditionalExpression
				|| location instanceof AstCommaExpression) {
			return range.get_result();
		}
		else if(location instanceof AstConstExpression) {
			return this.get_use_point(tree, 
					((AstConstExpression) location).get_expression());
		}
		else if(location instanceof AstParanthExpression) {
			return this.get_use_point(tree, ((AstParanthExpression) 
									location).get_sub_expression());
		}
		else if(location instanceof AstAssignExpression
				|| location instanceof AstArithAssignExpression
				|| location instanceof AstBitwiseAssignExpression
				|| location instanceof AstShiftAssignExpression) {
			CirAssignStatement statement = (CirAssignStatement) get_cir_nodes(
							tree, location, CirBinAssignStatement.class).get(0);
			return statement.get_rvalue();
		}
		else if(location instanceof AstFunCallExpression) {
			CirAssignStatement statement = (CirAssignStatement) get_cir_nodes(
						tree, location, CirWaitAssignStatement.class).get(0);
			return statement.get_rvalue();
		}
		else if(location instanceof AstIncreUnaryExpression) {
			CirAssignStatement statement = (CirAssignStatement) get_cir_nodes(
						tree, location, CirIncreAssignStatement.class).get(0);
			return statement.get_rvalue();
		}
		else if(location instanceof AstIncrePostfixExpression) {
			CirAssignStatement statement = (CirAssignStatement) get_cir_nodes(
						tree, location, CirSaveAssignStatement.class).get(0);
			return statement.get_rvalue();
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + location);
		}
	}
	
	
	
	
	
	
}
