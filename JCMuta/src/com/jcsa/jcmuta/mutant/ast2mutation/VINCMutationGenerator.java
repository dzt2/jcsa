package com.jcsa.jcmuta.mutant.ast2mutation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class VINCMutationGenerator extends AstMutationGenerator {
	
	private static final List<Integer> int_differences = new ArrayList<Integer>();
	private static final List<Double> flt_differences = new ArrayList<Double>();
	static {
		int_differences.add(1);
		int_differences.add(2);
		int_differences.add(3);
		int_differences.add(4);
		int_differences.add(-1);
		int_differences.add(-2);
		int_differences.add(-3);
		int_differences.add(-4);
		
		flt_differences.add(1.5);
		flt_differences.add(1.2);
		flt_differences.add(1.1);
		flt_differences.add(1.05);
		flt_differences.add(1.01);
		flt_differences.add(1.005);
		flt_differences.add(1.001);
		
		flt_differences.add(0.999);
		flt_differences.add(0.995);
		flt_differences.add(0.99);
		flt_differences.add(0.95);
		flt_differences.add(0.9);
		flt_differences.add(0.8);
		flt_differences.add(0.5);
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

	@Override
	protected void generate_mutations(AstNode location, Collection<AstMutation> mutations) throws Exception {
		AstExpression expression = (AstExpression) location;
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_value_type());
		
		if(CTypeAnalyzer.is_integer(data_type)) {
			for(Integer value : int_differences) {
				mutations.add(AstMutation.VINC(expression, value));
			}
		}
		else if(CTypeAnalyzer.is_real(data_type)) {
			for(Double value : flt_differences) {
				mutations.add(AstMutation.VINC(expression, value));
			}
		}
	}

}
