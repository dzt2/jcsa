package com.jcsa.jcmuta.mutant.ast2mutation;

import java.util.Collection;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

/**
 * trap_on_pos(expression) where expression is:
 * (1) NOT left-value;
 * (2) NOT constant;
 * (3) char | integer | real value-expression.
 * @author yukimula
 *
 */
public class VTRPMutationGenerator extends AstMutationGenerator {
	
	private boolean is_left_value(AstExpression expression) throws Exception {
		AstNode parent = expression.get_parent();
		if(parent instanceof AstAssignExpression
			|| parent instanceof AstArithAssignExpression
			|| parent instanceof AstBitwiseAssignExpression
			|| parent instanceof AstShiftAssignExpression) {
			return ((AstBinaryExpression) parent).get_loperand() == expression;
		}
		else if(parent instanceof AstIncreUnaryExpression) {
			return ((AstIncreUnaryExpression) parent).get_operand() == expression;
		}
		else if(parent instanceof AstIncrePostfixExpression) {
			return ((AstIncrePostfixExpression) parent).get_operand() == expression;
		}
		else return false;
	}
	
	private boolean is_number_type(CType data_type) throws Exception {
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
		else { return false; }
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
		else { return false; }
	}
	
	@Override
	protected void collect_locations(AstNode location, Collection<AstNode> locations) throws Exception {
		if(location instanceof AstExpression) {
			if(!(location instanceof AstConstant) && !(location instanceof AstInitializerBody)) {
				if(((AstExpression) location).get_value_type() != null) {
					if(this.is_number_type(((AstExpression) location).get_value_type())) {
						if(!this.is_left_value((AstExpression) location)) 
							locations.add(location);
					}
				}
			}
		}
	}

	@Override
	protected void generate_mutations(AstNode location, Collection<AstMutation> mutations) throws Exception {
		AstExpression expression = (AstExpression) location;
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_value_type());
		mutations.add(AstMutation.VTRP(expression, 'p'));
		if(!this.is_unsigned_type(data_type))
			mutations.add(AstMutation.VTRP(expression, 'n'));
		mutations.add(AstMutation.VTRP(expression, '0'));
	}

}
