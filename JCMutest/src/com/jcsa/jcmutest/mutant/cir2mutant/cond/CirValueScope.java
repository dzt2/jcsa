package com.jcsa.jcmutest.mutant.cir2mutant.cond;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.ctype.impl.CTypeFactory;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * It defines the abstract scope for annotation definition.
 * 
 * @author yukimula
 *
 */
final class CirValueScope {
	
	protected static final SymbolExpression bool_value = SymbolFactory.variable(CBasicTypeImpl.bool_type, "@BoolValue");
	protected static final SymbolExpression true_value = SymbolFactory.variable(CBasicTypeImpl.bool_type, "@TrueValue");
	protected static final SymbolExpression fals_value = SymbolFactory.variable(CBasicTypeImpl.bool_type, "@FalsValue");
	
	protected static final SymbolExpression numb_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NumbValue");
	
	protected static final SymbolExpression post_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@PostValue");
	protected static final SymbolExpression negt_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NegtValue");
	protected static final SymbolExpression zero_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@ZeroValue");
	
	protected static final SymbolExpression npos_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NposValue");
	protected static final SymbolExpression nneg_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NnegValue");
	protected static final SymbolExpression nzro_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NzroValue");
	
	protected static final SymbolExpression addr_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@AddrValue");
	protected static final SymbolExpression null_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NullValue");
	protected static final SymbolExpression nnul_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NnulValue");
	
	protected static final SymbolExpression expt_value = SymbolFactory.variable(CBasicTypeImpl.bool_type, "@Exception");
	
	protected static SymbolExpression evaluate(SymbolExpression expression, SymbolProcess context) throws Exception {
		try {
			return expression.evaluate(context);
		}
		catch(ArithmeticException ex) {
			return expt_value;
		}
	}
	protected static SymbolExpression ext_diff(Object orig_value, Object muta_value) throws Exception {
		CType type = (new CTypeFactory()).get_variable_function_type(CBasicTypeImpl.double_type);
		SymbolExpression function = SymbolFactory.identifier(type, "fabs");
		List<Object> arguments = new ArrayList<Object>();
		
		arguments.clear(); 
		arguments.add(muta_value);
		muta_value = SymbolFactory.call_expression(function, arguments);
		
		arguments.clear();
		arguments.add(orig_value);
		orig_value = SymbolFactory.call_expression(function, arguments);
		
		return SymbolFactory.arith_sub(CBasicTypeImpl.double_type, muta_value, orig_value);
	}
	
}
