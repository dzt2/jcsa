package com.jcsa.jcmutest.mutant.sec2mutant.lang.desc;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcmutest.mutant.sec2mutant.SecValueTypes;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.token.SecExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymFactory;

/**
 * <code>
 * 	+----------------------------------------------------------------------+<br>
 * 	SecConcreteError					{orig_expr; type: SecType}			<br>
 * 	|--	SecUnaryValueError													<br>
 * 	|--	|--	SecChgValueError			chg_value[bool|char|sign...body]	<br>
 * 	|--	|--	SecNegValueError			neg_value[char|sign|usign|real]		<br>
 * 	|--	|--	SecRsvValueError			rsv_value[char|sign|usign]			<br>
 * 	|--	|--	SecIncValueError			inc_value[char|sign|usign|real|addr]<br>
 * 	|--	|--	SecDecValueError			dec_value[char|sign|usign|real|addr]<br>
 * 	|--	|--	SecExtValueError			ext_value[char|sign|usign|real]		<br>
 * 	|--	|--	SecShkValueError			shk_value[char|sign|usign|real]		<br>
 * 	|--	SecBinaryValueError				{muta_expr: SecExpression}			<br>
 * 	|--	|--	SecSetValueError			set_value[char|sign|usign|real|addr]<br>
 * 	|--	|--	SecAddValueError			add_value[char|sign|usign|real|addr]<br>
 * 	|--	|--	SecMulValueError			mul_value[char|sign|usign|real]		<br>
 * 	|--	|--	SecModValueError			mod_value[char|sign|usign]			<br>
 * 	|--	|--	SecAndValueError			and_value[char|sign|usign]			<br>
 * 	|--	|--	SecIorValueError			ior_value[char|sign|usign]			<br>
 * 	|--	|--	SecXorValueError			xor_value[char|sign|usign]			<br>
 * 	+----------------------------------------------------------------------+<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SecConcreteDescription extends SecDescription {

	public SecConcreteDescription(CirStatement statement, SecKeywords 
			keyword, CirExpression orig_expression) throws Exception {
		super(statement, keyword);
		this.add_child(new SecExpression(SymFactory.parse(orig_expression)));
		if(!this.verify_type(this.get_orig_expression().get_type().get_vtype()))
			throw new IllegalArgumentException(orig_expression.get_data_type().generate_code());
	}
	
	public SecExpression get_orig_expression() {
		return (SecExpression) this.get_child(2);
	}
	
	/**
	 * @param type
	 * @return whether the value type is available for this error
	 */
	protected abstract boolean verify_type(SecValueTypes type);
	
}
