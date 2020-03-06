package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstName;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.scope.CInstanceName;

public class VSRR_Mutation extends CustomizeMutation {

	protected VSRR_Mutation(AstExpression source, AstName argument) throws Exception {
		super(MutOperator.VSRR, MutationMode.REF_TOT_REF, source, argument);
	}

	/**
	 * get the node to replace original node
	 * @return
	 */
	public AstName get_replace() {return (AstName) argument;}

	@Override
	protected boolean validate(MutationMode mode, AstNode source, Object argument) {
		AstExpression origins = (AstExpression) source;
		AstName replace = (AstName) argument;
		CInstanceName cname = (CInstanceName) replace.get_cname();
		
		if(JC_Classifier.is_access_path(origins)) {
			
			CType type1 = origins.get_value_type();
			CType type2 = cname.get_instance().get_type();
			type1 = JC_Classifier.get_value_type(type1);
			type2 = JC_Classifier.get_value_type(type2);
			
			if(JC_Classifier.is_integer_type(type1) && JC_Classifier.is_integer_type(type2))
				return true;
			else if(JC_Classifier.is_real_type(type1) && JC_Classifier.is_real_type(type2))
				return true;
			else return false;
		}
		else return false;
	}

}
