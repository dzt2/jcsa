package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.CConstant;

public class CCCR_Mutation extends CustomizeMutation {
	
	protected CCCR_Mutation(AstConstant location, AstConstant replace) throws Exception {
		super(MutOperator.CCCR, MutationMode.CST_TOT_CST, location, replace);
	}
	
	/**
	 * get the constant to replace original code
	 * @return
	 */
	public AstConstant get_replace() {return (AstConstant) argument;}
	
	@Override
	protected boolean validate(MutationMode mode, AstNode location, Object argument) {
		AstConstant source = (AstConstant) location;
		AstConstant replace = (AstConstant) argument;
		CConstant prev = source.get_constant();
		CConstant next = replace.get_constant();
		CType ptype = prev.get_type();
		CType ntype = next.get_type();
		
		if(JC_Classifier.is_integer_type(ptype)
				&& JC_Classifier.is_integer_type(ntype)) {
			long v1, v2;
			
			if(JC_Classifier.is_character_type(ptype))
				v1 = prev.get_char();
			else if(JC_Classifier.is_long_integer(ptype))
				v1 = prev.get_long();
			else v1 = prev.get_integer();
			
			if(JC_Classifier.is_character_type(ntype))
				v2 = next.get_char();
			else if(JC_Classifier.is_long_integer(ntype))
				v2 = next.get_long();
			else v2 = next.get_integer();
			
			return v1 != v2;
		}
		else if(JC_Classifier.is_real_type(ptype)
				&& JC_Classifier.is_real_type(ntype)) {
			double v1 = prev.get_double();
			double v2 = next.get_double();
			return v1 != v2;
		}
		else return false;
		
	}
	
}
