package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.CConstant;

/**
 * <code>int-const |==> {0, +1, -1, +inf, -inf}</code>
 * @author yukimula
 */
public class CRCR_Mutation extends SingleMutation {
	
	protected CRCR_Mutation(MutationMode mode, AstConstant location) throws Exception {
		super(MutOperator.CRCR, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstConstant constant = (AstConstant) location;
		CType type = constant.get_value_type();
		type = JC_Classifier.get_value_type(type);
		CConstant value = constant.get_constant();
		
		if(JC_Classifier.is_integer_type(type)) {
			/* get the number */
			long number;
			if(JC_Classifier.is_character_type(type))
				number = value.get_char();
			else if(JC_Classifier.is_long_integer(type))
				number = value.get_long();
			else number = value.get_integer();
			
			/* validate mutation mode */
			switch(mode) {
			case CST_TOT_ZRO: return number != 0L;
			case CST_POS_ONE: return number != 1L;
			case CST_NEG_ONE: return number != -1L;
			case CST_NEG_CST: return number != 0L;
			case CST_INC_ONE: 
			case CST_DEC_ONE: return true;
			default: return false;
			}
		}
		else return false;
	}

}
