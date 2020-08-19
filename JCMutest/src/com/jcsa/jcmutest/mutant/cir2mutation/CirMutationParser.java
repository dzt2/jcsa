package com.jcsa.jcmutest.mutant.cir2mutation;

import java.util.Collection;
import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
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
 * It is used to parse the cir-mutation from ast-mutation.
 * 
 * @author yukimula
 *
 */
public abstract class CirMutationParser {
	
	/**
	 * parse from the ast-mutation to the cir-mutation(s) that describes it
	 * or none which implies the source mutation is an equivalent mutation.
	 * @param source
	 * @param targets
	 * @throws Exception
	 */
	public abstract void parse(CirTree cir_tree, AstMutation source, Collection<CirMutation> targets) throws Exception;
	
	/* utility methods */
	protected AstCirPair get_cir_range(CirTree cir_tree, AstNode location) throws Exception {
		if(cir_tree.has_cir_range(location))
			return cir_tree.get_cir_range(location);
		else
			return null;
	}
	protected CirNode get_cir_node(CirTree cir_tree, AstNode location, Class<?> cir_class, int index) throws Exception {
		List<CirNode> cir_nodes = cir_tree.
				get_cir_nodes(location, cir_class);
		if(index >= cir_nodes.size()) return null;
		else return cir_nodes.get(index);
	}
	/**
	 * @param source
	 * @return the expression in intermediate representation that can represent
	 * 		   the source expression in abstract syntactic structure.
	 * @throws Exception
	 */
	protected CirExpression get_use_point(CirTree cir_tree, AstExpression source) throws Exception {
		AstCirPair range = this.get_cir_range(cir_tree, source);
		if(range == null) {
			return null;
		}
		else if(source instanceof AstIdExpression) {
			return range.get_result();
		}
		else if(source instanceof AstConstant) {
			return range.get_result();
		}
		else if(source instanceof AstLiteral) {
			return range.get_result();
		}
		else if(source instanceof AstArithUnaryExpression) {
			return range.get_result();
		}
		else if(source instanceof AstBitwiseUnaryExpression) {
			return range.get_result();
		}
		else if(source instanceof AstLogicUnaryExpression) {
			return range.get_result();
		}
		else if(source instanceof AstPointUnaryExpression) {
			return range.get_result();
		}
		else if(source instanceof AstIncreUnaryExpression) {
			CirAssignStatement statement = (CirAssignStatement) this.get_cir_node(
							cir_tree, source, CirIncreAssignStatement.class, 0);
			return statement.get_rvalue();
		}
		else if(source instanceof AstIncrePostfixExpression) {
			CirAssignStatement statement = (CirAssignStatement) this.get_cir_node(
								cir_tree, source, CirSaveAssignStatement.class, 0);
			return statement.get_rvalue();
		}
		else if(source instanceof AstArithBinaryExpression
				|| source instanceof AstBitwiseBinaryExpression
				|| source instanceof AstShiftBinaryExpression
				|| source instanceof AstRelationExpression) {
			return range.get_result();
		}
		else if(source instanceof AstLogicBinaryExpression) {
			return range.get_result();
		}
		else if(source instanceof AstAssignExpression
				|| source instanceof AstArithAssignExpression
				|| source instanceof AstBitwiseAssignExpression
				|| source instanceof AstShiftAssignExpression) {
			CirAssignStatement statement = (CirAssignStatement) this.get_cir_node(
								cir_tree, source, CirBinAssignStatement.class, 0);
			return statement.get_rvalue();
		}
		else if(source instanceof AstArrayExpression
				|| source instanceof AstCastExpression
				|| source instanceof AstFieldExpression
				|| source instanceof AstSizeofExpression
				|| source instanceof AstCommaExpression) {
			return range.get_result();
		}
		else if(source instanceof AstParanthExpression) {
			return this.get_use_point(cir_tree, ((AstParanthExpression) source).get_sub_expression());
		}
		else if(source instanceof AstConstExpression) {
			return this.get_use_point(cir_tree, ((AstConstExpression) source).get_expression());
		}
		else if(source instanceof AstConditionalExpression) {
			return range.get_result();
		}
		else if(source instanceof AstFunCallExpression) {
			CirAssignStatement statement = (CirAssignStatement) this.get_cir_node(
								cir_tree, source, CirWaitAssignStatement.class, 0);
			return statement.get_rvalue();
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + source);
		}
	}
	
	
}
