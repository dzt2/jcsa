package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OASA_Mutation extends SingleMutation {

	protected OASA_Mutation(MutationMode mode, AstArithAssignExpression location) throws Exception {
		super(MutOperator.OASA, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstArithAssignExpression expr = (AstArithAssignExpression) location;
		COperator oprt = expr.get_operator().get_operator();
		switch(mode) {
		case ADD_LSH_A: case ADD_RSH_A: 
			return oprt == COperator.arith_add;
		case SUB_LSH_A: case SUB_RSH_A: 
			return oprt == COperator.arith_sub;
		case MUL_LSH_A: case MUL_RSH_A: 
			return oprt == COperator.arith_mul;
		case DIV_LSH_A: case DIV_RSH_A: 
			return oprt == COperator.arith_div;
		case MOD_LSH_A: case MOD_RSH_A: 
			return oprt == COperator.arith_mod;
		default: return false;
		}
	}

}
