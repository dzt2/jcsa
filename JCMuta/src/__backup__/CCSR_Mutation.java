package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstName;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.scope.CInstanceName;

public class CCSR_Mutation extends CustomizeMutation {

	protected CCSR_Mutation(AstConstant location, AstName replace) throws Exception {
		super(MutOperator.CCSR, MutationMode.CST_TOT_REF, location, replace);
	}

	/**
	 * get the access-path of expression to replace original constant
	 * @return
	 */
	public AstName get_replace() {return (AstName) argument;}
	
	@Override
	protected boolean validate(MutationMode mode, AstNode source, Object argument) {
		// type for constant
		AstConstant constant = (AstConstant) source;
		CBasicType type1 = constant.get_constant().get_type();
		// type for identifier
		AstName replace = (AstName) argument;
		CInstanceName cname = (CInstanceName) replace.get_cname();
		CType type2 = cname.get_instance().get_type();
		type2 = JC_Classifier.get_value_type(type2);
		
		switch(type1.get_tag()) {
		case c_char:
		case c_uchar:
			return JC_Classifier.is_character_type(type2);
		case c_int:
		case c_uint:
		case c_long:
		case c_llong:
		case c_ulong:
		case c_ullong:
			return JC_Classifier.is_integer_type(type2);
		case c_float:
		case c_double:
		case c_ldouble:
			return JC_Classifier.is_real_type(type2);
		default: return false;
		}
	}

}
