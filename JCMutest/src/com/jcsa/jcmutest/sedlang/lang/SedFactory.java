package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.ctype.impl.CTypeFactory;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;

public class SedFactory {
	
	private static final CTypeFactory tfactory = new CTypeFactory();
	
	public static SedOperator new_operator(COperator operator) {
		return new SedOperator(null, operator);
	}
	public static SedField new_field(String name) {
		return new SedField(null, name);
	}
	public static SedLabel new_label(CirExecution execution) {
		return new SedLabel(execution.get_statement(), execution);
	}
	
	public static SedIdentifier new_identifier(CType type, String name) {
		return new SedIdentifier(null, type, name);
	}
	public static SedConstant new_constant(boolean value) {
		CConstant constant = new CConstant();
		constant.set_bool(value);
		return new SedConstant(null, constant);
	}
	public static SedConstant new_constant(char value) {
		CConstant constant = new CConstant();
		constant.set_char(value);
		return new SedConstant(null, constant);
	}
	public static SedConstant new_constant(int value) {
		CConstant constant = new CConstant();
		constant.set_int(value);
		return new SedConstant(null, constant);
	}
	public static SedConstant new_constant(long value) {
		CConstant constant = new CConstant();
		constant.set_long(value);
		return new SedConstant(null, constant);
	}
	public static SedConstant new_constant(float value) {
		CConstant constant = new CConstant();
		constant.set_float(value);
		return new SedConstant(null, constant);
	}
	public static SedConstant new_constant(double value) {
		CConstant constant = new CConstant();
		constant.set_double(value);
		return new SedConstant(null, constant);
	}
	public static SedLiteral new_literal(String literal) throws Exception {
		return new SedLiteral(null, tfactory.get_array_type(
				CBasicTypeImpl.char_type, literal.length() + 1), literal);
	}
	public static SedDefaultValue new_default_value(CType type) {
		return new SedDefaultValue(null, type);
	}
	
	
	
	
	
}
