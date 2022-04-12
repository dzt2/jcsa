package com.jcsa.jcparse.lang.symbol.eval;

import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.symbol.SymbolArgumentList;
import com.jcsa.jcparse.lang.symbol.SymbolCallExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolIdentifier;
import com.jcsa.jcparse.lang.symbol.SymbolLiteral;

/**
 * It implements the standard library functions.
 * 
 * @author yukimula
 *
 */
final class SymbolDefaultInvoker implements SymbolMethodInvoker {
	
	/* math.h */
	private	SymbolExpression acos(SymbolArgumentList alist) throws Exception {
		SymbolExpression arg0 = alist.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double input = ((SymbolConstant) arg0).get_double();
			Double output = Double.valueOf(Math.acos(input));
			return SymbolFactory.sym_constant(output);
		}
		return null;
	}
	private	SymbolExpression asin(SymbolArgumentList alist) throws Exception {
		SymbolExpression arg0 = alist.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double input = ((SymbolConstant) arg0).get_double();
			Double output = Double.valueOf(Math.asin(input));
			return SymbolFactory.sym_constant(output);
		}
		return null;
	}
	private	SymbolExpression atan(SymbolArgumentList alist) throws Exception {
		SymbolExpression arg0 = alist.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double input = ((SymbolConstant) arg0).get_double();
			Double output = Double.valueOf(Math.atan(input));
			return SymbolFactory.sym_constant(output);
		}
		return null;
	}
	
	private	SymbolExpression cos(SymbolArgumentList alist) throws Exception {
		SymbolExpression arg0 = alist.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double input = ((SymbolConstant) arg0).get_double();
			Double output = Double.valueOf(Math.cos(input));
			return SymbolFactory.sym_constant(output);
		}
		return null;
	}
	private	SymbolExpression cosh(SymbolArgumentList alist) throws Exception {
		SymbolExpression arg0 = alist.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double input = ((SymbolConstant) arg0).get_double();
			Double output = Double.valueOf(Math.cosh(input));
			return SymbolFactory.sym_constant(output);
		}
		return null;
	}
	private	SymbolExpression sin(SymbolArgumentList alist) throws Exception {
		SymbolExpression arg0 = alist.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double input = ((SymbolConstant) arg0).get_double();
			Double output = Double.valueOf(Math.sin(input));
			return SymbolFactory.sym_constant(output);
		}
		return null;
	}
	private	SymbolExpression sinh(SymbolArgumentList alist) throws Exception {
		SymbolExpression arg0 = alist.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double input = ((SymbolConstant) arg0).get_double();
			Double output = Double.valueOf(Math.sinh(input));
			return SymbolFactory.sym_constant(output);
		}
		return null;
	}
	
	private	SymbolExpression tanh(SymbolArgumentList alist) throws Exception {
		SymbolExpression arg0 = alist.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double input = ((SymbolConstant) arg0).get_double();
			Double output = Double.valueOf(Math.tanh(input));
			return SymbolFactory.sym_constant(output);
		}
		return null;
	}
	private	SymbolExpression tan(SymbolArgumentList alist) throws Exception {
		SymbolExpression arg0 = alist.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double input = ((SymbolConstant) arg0).get_double();
			Double output = Double.valueOf(Math.tan(input));
			return SymbolFactory.sym_constant(output);
		}
		return null;
	}
	
	private	SymbolExpression exp(SymbolArgumentList alist) throws Exception {
		SymbolExpression arg0 = alist.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double input = ((SymbolConstant) arg0).get_double();
			Double output = Double.valueOf(Math.exp(input));
			return SymbolFactory.sym_constant(output);
		}
		return null;
	}
	private	SymbolExpression log(SymbolArgumentList alist) throws Exception {
		SymbolExpression arg0 = alist.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double input = ((SymbolConstant) arg0).get_double();
			Double output = Double.valueOf(Math.log(input));
			return SymbolFactory.sym_constant(output);
		}
		return null;
	}
	private	SymbolExpression log10(SymbolArgumentList alist) throws Exception {
		SymbolExpression arg0 = alist.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double input = ((SymbolConstant) arg0).get_double();
			Double output = Double.valueOf(Math.log10(input));
			return SymbolFactory.sym_constant(output);
		}
		return null;
	}
	private	SymbolExpression sqrt(SymbolArgumentList alist) throws Exception {
		SymbolExpression arg0 = alist.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double input = ((SymbolConstant) arg0).get_double();
			Double output = Double.valueOf(Math.sqrt(input));
			return SymbolFactory.sym_constant(output);
		}
		return null;
	}
	
	private	SymbolExpression ceil(SymbolArgumentList alist) throws Exception {
		SymbolExpression arg0 = alist.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double input = ((SymbolConstant) arg0).get_double();
			Double output = Double.valueOf(Math.ceil(input));
			return SymbolFactory.sym_constant(output);
		}
		return null;
	}
	private	SymbolExpression floor(SymbolArgumentList alist) throws Exception {
		SymbolExpression arg0 = alist.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double input = ((SymbolConstant) arg0).get_double();
			Double output = Double.valueOf(Math.floor(input));
			return SymbolFactory.sym_constant(output);
		}
		return null;
	}
	private	SymbolExpression fabs(SymbolArgumentList alist) throws Exception {
		SymbolExpression arg0 = alist.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double input = ((SymbolConstant) arg0).get_double();
			Double output = Double.valueOf(Math.abs(input));
			return SymbolFactory.sym_constant(output);
		}
		else {
			SymbolExpression condition = SymbolFactory.smaller_tn(arg0, Integer.valueOf(0));
			SymbolExpression t_operand = SymbolFactory.arith_neg(arg0);
			SymbolExpression f_operand = SymbolFactory.sym_expression(arg0);
			return SymbolFactory.ifte_expression(arg0.get_data_type(), condition, t_operand, f_operand);
		}
	}
	
	private	SymbolExpression atan2(SymbolArgumentList alist) throws Exception {
		SymbolExpression arg0 = alist.get_argument(0);
		SymbolExpression arg1 = alist.get_argument(1);
		if(arg0 instanceof SymbolConstant && arg1 instanceof SymbolConstant) {
			Double input1 = ((SymbolConstant) arg0).get_double();
			Double input2 = ((SymbolConstant) arg1).get_double();
			Double output = Double.valueOf(Math.atan2(input1, input2));
			return SymbolFactory.sym_constant(output);
		}
		return null;
	}
	private	SymbolExpression pow(SymbolArgumentList alist) throws Exception {
		SymbolExpression arg0 = alist.get_argument(0);
		SymbolExpression arg1 = alist.get_argument(1);
		if(arg0 instanceof SymbolConstant && arg1 instanceof SymbolConstant) {
			Double input1 = ((SymbolConstant) arg0).get_double();
			Double input2 = ((SymbolConstant) arg1).get_double();
			Double output = Double.valueOf(Math.pow(input1, input2));
			return SymbolFactory.sym_constant(output);
		}
		return null;
	}
	
	private	SymbolExpression printf(SymbolArgumentList alist) throws Exception {
		SymbolExpression literal = alist.get_argument(0);
		if(literal instanceof SymbolLiteral) {
			String template = literal.get_simple_code();
			StringBuilder buffer = new StringBuilder();
			int j = 1;
			
			for(int k = 0; k < template.length(); k++) {
				char ch = template.charAt(k);
				if(ch == '%') {
					ch = template.charAt(++k);
					if(ch == 'l' && k < template.length()) {
						ch = template.charAt(++k);
					}
					SymbolExpression arg = alist.get_argument(j++);
					buffer.append(arg.get_simple_code());
				}
				else {
					buffer.append(ch);
				}
			}
			
			SymbolIdentifier source = SymbolFactory.identifier(
					CBasicTypeImpl.void_type, "stdout", alist);
			this.set_state_value(source, SymbolFactory.literal(buffer));
			return SymbolFactory.sym_constant(buffer.length());
		}
		else {
			return null;
		}
	}
	private	SymbolExpression fprintf(SymbolArgumentList alist) throws Exception {
		SymbolIdentifier source = SymbolFactory.identifier(CBasicTypeImpl.
				void_type, alist.get_argument(0).get_simple_code(), alist);
		SymbolExpression literal = alist.get_argument(1);
		if(literal instanceof SymbolLiteral) {
			String template = literal.get_simple_code();
			StringBuilder buffer = new StringBuilder();
			int j = 2;
			
			for(int k = 0; k < template.length(); k++) {
				char ch = template.charAt(k);
				if(ch == '%') {
					ch = template.charAt(++k);
					if(ch == 'l' && k < template.length()) {
						ch = template.charAt(++k);
					}
					SymbolExpression arg = alist.get_argument(j++);
					buffer.append(arg.get_simple_code());
				}
				else {
					buffer.append(ch);
				}
			}
			this.set_state_value(source, SymbolFactory.literal(buffer));
			return SymbolFactory.sym_constant(buffer.length());
		}
		else {
			return null;
		}
	}
	
	@Override
	public SymbolExpression invoke(SymbolCallExpression source, SymbolContext in_state, SymbolContext ou_state) throws Exception {
		this.ou_state = ou_state; this.in_state = in_state;
		SymbolArgumentList alist = source.get_argument_list();
		SymbolExpression function = source.get_function();
		
		if(function instanceof SymbolIdentifier) {
			String name = ((SymbolIdentifier) function).get_name();
			if(name.equals("acos")) {
				return this.acos(alist);
			}
			else if(name.equals("asin")) {
				return this.asin(alist);
			}
			else if(name.equals("atan")) {
				return this.atan(alist);
			}
			else if(name.equals("cos")) {
				return this.cos(alist);
			}
			else if(name.equals("cosh")) {
				return this.cosh(alist);
			}
			else if(name.equals("sin")) {
				return this.sin(alist);
			}
			else if(name.equals("sinh")) {
				return this.sinh(alist);
			}
			else if(name.equals("tanh")) {
				return this.tanh(alist);
			}
			else if(name.equals("tan")) {
				return this.tan(alist);
			}
			else if(name.equals("exp")) {
				return this.exp(alist);
			}
			else if(name.equals("log")) {
				return this.log(alist);
			}
			else if(name.equals("log10")) {
				return this.log10(alist);
			}
			else if(name.equals("sqrt")) {
				return this.sqrt(alist);
			}
			else if(name.equals("ceil")) {
				return this.ceil(alist);
			}
			else if(name.equals("floor")) {
				return this.floor(alist);
			}
			else if(name.equals("fabs") || name.equals("abs")) {
				return this.fabs(alist);
			}
			else if(name.equals("atan2")) {
				return this.atan2(alist);
			}
			else if(name.equals("pow")) {
				return this.pow(alist);
			}
			else if(name.equals("printf")) {
				return this.printf(alist);
			}
			else if(name.equals("fprintf")) {
				return this.fprintf(alist);
			}
			else {
				return null;
			}
		}
		return null;
	}
	
	protected	SymbolContext in_state, ou_state;
	/**
	 * It sets the source-target value-pair in the table
	 * @param reference
	 * @param value
	 * @throws Exception
	 */
	private void set_state_value(SymbolExpression reference, SymbolExpression value) throws Exception {
		if(reference == null) {
			throw new IllegalArgumentException("Invalid reference: null");
		}
		else if(value == null) {
			throw new IllegalArgumentException("Invalid value as null");
		}
		else if(!reference.is_reference()) {
			throw new IllegalArgumentException("Invalid reference: " + reference);
		}
		else if(this.ou_state != null) {this.ou_state.put_value(reference, value);}
		else { /* no state map is specified and thus no update arises here */ }
	}
	
}
