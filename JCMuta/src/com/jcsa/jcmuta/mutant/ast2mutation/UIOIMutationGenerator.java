package com.jcsa.jcmuta.mutant.ast2mutation;

import java.util.Collection;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPointUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArrayExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

/**
 * 
 * @author yukimula
 *
 */
public class UIOIMutationGenerator extends AstMutationGenerator {
	
	private boolean is_reference(AstNode location) throws Exception {
		if(location instanceof AstIdExpression
			|| location instanceof AstArrayExpression
			|| location instanceof AstFieldExpression) {
			return true;
		}
		else if(location instanceof AstPointUnaryExpression) {
			return ((AstPointUnaryExpression) location).get_operator().
					get_operator() == COperator.dereference;
		}
		else return false;
	}
	
	private boolean is_number_or_pointer_type(CType data_type) throws Exception {
		data_type = CTypeAnalyzer.get_value_type(data_type);
		if(data_type instanceof CBasicType) {
			switch(((CBasicType) data_type).get_tag()) {
			case c_char: case c_uchar:
			case c_short: case c_ushort:
			case c_int: case c_uint:
			case c_long: case c_ulong:
			case c_llong: case c_ullong:
			case c_float: case c_double: case c_ldouble:
				return true;
			default: return false;
			}
		}
		else if(data_type instanceof CEnumType) {
			return true;
		}
		else if(data_type instanceof CPointerType) {
			return true;
		}
		else { return false; }
	}
	
	private boolean is_left_reference(AstNode location) throws Exception {
		AstNode parent = location.get_parent();
		if(parent instanceof AstAssignExpression
			|| parent instanceof AstBitwiseAssignExpression
			|| parent instanceof AstArithAssignExpression
			|| parent instanceof AstShiftAssignExpression) {
			AstBinaryExpression expr = (AstBinaryExpression) parent;
			return expr.get_loperand() == location;
		}
		else if(parent instanceof AstIncreUnaryExpression
				|| parent instanceof AstIncrePostfixExpression) {
			return true;
		}
		else if(parent instanceof AstFieldExpression) {
			return ((AstFieldExpression) parent).get_operator().get_punctuator() == CPunctuator.dot;
		}
		else {
			return false;
		}
	}
	
	@Override
	protected void collect_locations(AstNode location, Collection<AstNode> locations) throws Exception {
		if(this.is_reference(location)) {
			AstExpression expression = (AstExpression) location;
			if(this.is_number_or_pointer_type(expression.get_value_type())) {
				if(!this.is_left_reference(expression))
					locations.add(expression);
			}
		}
	}

	@Override
	protected void generate_mutations(AstNode location, Collection<AstMutation> mutations) throws Exception {
		AstExpression expression = (AstExpression) location;
		mutations.add(AstMutation.UIOI(expression, true,  COperator.increment));
		mutations.add(AstMutation.UIOI(expression, true,  COperator.decrement));
		mutations.add(AstMutation.UIOI(expression, false, COperator.increment));
		mutations.add(AstMutation.UIOI(expression, false, COperator.decrement));
	}

}
