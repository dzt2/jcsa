package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithBinaryExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * <code>{+, -, *, /, %} |==> {+, -, *, /, %}</code>
 * @author yukimula
 */
public class OAAN_Mutation extends SingleMutation {

	protected OAAN_Mutation(MutationMode mode, AstArithBinaryExpression location) throws Exception {
		super(MutOperator.OAAN, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstArithBinaryExpression expr = (AstArithBinaryExpression) location;
		COperator oprt = expr.get_operator().get_operator();
		switch(mode) {
		case ADD_SUB: case ADD_MUL: case ADD_DIV: case ADD_MOD:
			return oprt == COperator.arith_add;
		case SUB_ADD: case SUB_MUL: case SUB_DIV: case SUB_MOD:
			return oprt == COperator.arith_sub;
		case MUL_ADD: case MUL_SUB: case MUL_DIV: case MUL_MOD:
			return oprt == COperator.arith_mul;
		case DIV_ADD: case DIV_SUB: case DIV_MUL: case DIV_MOD:
			return oprt == COperator.arith_div;
		case MOD_ADD: case MOD_SUB: case MOD_MUL: case MOD_DIV:
			return oprt == COperator.arith_mod;
		default: return false;
		}
		
	}

}
