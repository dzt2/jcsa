package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CFieldBody;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CUnionType;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

/**
 * <code>x.field | x->filed ==> x.field2 | x->field2</code>
 * @author yukimula
 */
public class VSFR_Mutation extends CustomizeMutation {

	protected VSFR_Mutation(AstFieldExpression source, String argument) throws Exception {
		super(MutOperator.VSFR, MutationMode.FLD_TOT_FLD, source, argument);
	}
	
	/**
	 * get the field name
	 * @return
	 */
	public String get_field_name() {return (String) argument;}

	@Override
	protected boolean validate(MutationMode mode, AstNode source, Object argument) {
		/* get elements */
		AstFieldExpression expression = (AstFieldExpression) source;
		AstExpression body = expression.get_body();
		String field1 = expression.get_field().get_name();
		String field2 = (String) argument;
		
		/* equivalent field */
		if(field1.equals(field2)) return false;
		else {
			CPunctuator op = expression.get_operator().get_punctuator();
			CFieldBody fields = null; CType type = body.get_value_type();
			type = JC_Classifier.get_value_type(type);
			
			if(op == CPunctuator.arrow) {
				if(type instanceof CArrayType)
					 type = ((CArrayType) type).get_element_type();
				else type = ((CPointerType) type).get_pointed_type();
				type = JC_Classifier.get_value_type(type);
			}
			
			if(type instanceof CStructType)
				fields = ((CStructType) type).get_fields();
			else fields = ((CUnionType) type).get_fields();
			return fields.has_field(field2);
		}
	}

}
