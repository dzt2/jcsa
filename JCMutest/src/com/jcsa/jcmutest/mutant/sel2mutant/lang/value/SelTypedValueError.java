package com.jcsa.jcmutest.mutant.sel2mutant.lang.value;

import com.jcsa.jcmutest.mutant.sel2mutant.SelKeywords;
import com.jcsa.jcmutest.mutant.sel2mutant.SelValueType;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.desc.SelDescription;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.token.SelDataType;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.token.SelExpression;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymFactory;

/**
 * <code>
 * 	+----------------------------------------------------------------------+<br>
 * 	SelTypedValueError				{orig_expression; type: SelDataType}	<br>
 * 	|--	SelUnaryValueError													<br>
 * 	|--	|--	SelChgValueError		chg_val[bool|char|sign|usign|...|body)	<br>
 * 	|--	|--	SelNegValueError		neg_val[char|sign|usign|real]			<br>
 * 	|--	|--	SelRsvValueError		rsv_val[char|sign|usign]				<br>
 * 	|--	|--	SelIncValueError		inc_val[char|sign|usign|real|addr]		<br>
 * 	|--	|--	SelDecValueError		dec_val[char|sign|usign|real|addr]		<br>
 * 	|--	|--	SelExtValueError		ext_val[char|sign|usign|real]			<br>
 * 	|--	|--	SelShkValueError		shk_val[char|sign|usign|real]			<br>
 * 	|--	SelBinaryValueError			{muta_expression: SelExpression}		<br>
 * 	|--	|--	SelSetValueError		set_val[bool|char|sign|usign|...|body]	<br>
 * 	|--	|--	SelAddValueError		add_val[char|sign|usign|real|addr]		<br>
 * 	|--	|--	SelMulValueError		mul_val[char|sign|usign|real]			<br>
 * 	|--	|--	SelModValueError		mod_val[char|sign|usign]				<br>
 * 	|--	|--	SelAndValueError		and_val[char|sign|usign]				<br>
 * 	|--	|--	SelIorValueError		ior_val[char|sign|usign]				<br>
 * 	|--	|--	SelXorValueError		xor_val[char|sign|usign]				<br>
 * 	+----------------------------------------------------------------------+<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SelTypedValueError extends SelDescription {

	public SelTypedValueError(CirStatement statement, SelKeywords 
			keyword, CirExpression orig_expression) throws Exception {
		super(statement, keyword);
		if(orig_expression == null)
			throw new IllegalArgumentException("Invalid orig_expression");
		else {
			CType data_type = orig_expression.get_data_type();
			if(data_type == null) data_type = CBasicTypeImpl.void_type;
			this.add_child(new SelDataType(data_type));
			this.add_child(new SelExpression(SymFactory.parse(orig_expression)));
			
			if(!this.is_valid_type(this.get_value_type().get_value_type())) {
				throw new IllegalArgumentException(get_value_type().generate_code());
			}
		}
	}
	
	/**
	 * @return the type of the value of original expression
	 */
	public SelDataType get_value_type() { 
		return (SelDataType) this.get_child(2); 
	}
	
	/**
	 * @return the original expression where error is caused
	 */
	public SelExpression get_orig_expression() {
		return (SelExpression) this.get_child(3);
	}
	
	/**
	 * @param type
	 * @return whether the value type is valid
	 */
	protected abstract boolean is_valid_type(SelValueType type);
	
}
