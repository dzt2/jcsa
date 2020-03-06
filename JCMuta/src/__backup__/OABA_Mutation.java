package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OABA_Mutation extends SingleMutation {

	protected OABA_Mutation(MutationMode mode, AstArithAssignExpression location) throws Exception {
		super(MutOperator.OABA, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstArithAssignExpression expr = (AstArithAssignExpression) location;
		COperator oprt = expr.get_operator().get_operator();
		switch(mode) {
		case ADD_BAN_A: case ADD_BOR_A: case ADD_BXR_A: 
			return oprt == COperator.arith_add;
		case SUB_BAN_A: case SUB_BOR_A: case SUB_BXR_A: 
			return oprt == COperator.arith_sub;
		case MUL_BAN_A: case MUL_BOR_A: case MUL_BXR_A: 
			return oprt == COperator.arith_mul;
		case DIV_BAN_A: case DIV_BOR_A: case DIV_BXR_A: 
			return oprt == COperator.arith_div;
		case MOD_BAN_A: case MOD_BOR_A: case MOD_BXR_A: 
			return oprt == COperator.arith_mod;
		default: return false;
		}
	}

}
