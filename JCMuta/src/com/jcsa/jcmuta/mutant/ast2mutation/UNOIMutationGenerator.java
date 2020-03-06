package com.jcsa.jcmuta.mutant.ast2mutation;

import java.util.Collection;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class UNOIMutationGenerator extends AstMutationGenerator {
	
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
		if(location instanceof AstExpression) {
			AstExpression expression = (AstExpression) location;
			if(!this.is_left_reference(expression)) {
				if(expression.get_value_type() != null) {
					CType data_type = CTypeAnalyzer.get_value_type(expression.get_value_type());
					if(CTypeAnalyzer.is_integer(data_type) || CTypeAnalyzer.is_real(data_type)) {
						locations.add(CTypeAnalyzer.get_expression_of(expression));
					}
				}
			}
		}
	}
	
	private boolean is_unsigned_type(CType data_type) throws Exception {
		if(data_type instanceof CBasicType) {
			switch(((CBasicType) data_type).get_tag()) {
			case c_uchar:
			case c_ushort:
			case c_uint:
			case c_ulong:
			case c_ullong:
				return true;
			default: return false;
			}
		}
		else return false;
	}
	
	@Override
	protected void generate_mutations(AstNode location, Collection<AstMutation> mutations) throws Exception {
		AstExpression expression = (AstExpression) location;
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_value_type());
		
		mutations.add(AstMutation.UNOI(expression, COperator.negative));
		if(CTypeAnalyzer.is_integer(data_type)) {
			mutations.add(AstMutation.UNOI(expression, COperator.bit_not));
		}
		mutations.add(AstMutation.UNOI(expression, COperator.logic_not));
		if(!this.is_unsigned_type(data_type)) {
			mutations.add(AstMutation.UNOI(expression, COperator.positive));
			mutations.add(AstMutation.UNOI(expression, COperator.decrement));
		}
	}
}
