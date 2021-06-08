package com.jcsa.jcparse.parse.symbol.process;

import java.util.ArrayList;

import com.jcsa.jcparse.lang.symbol.SymbolArgumentList;
import com.jcsa.jcparse.lang.symbol.SymbolCallExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolIdentifier;

public class SymbolMathPackageInvoker implements SymbolInvoker {

	@Override
	public SymbolExpression invoke(SymbolCallExpression input_expression) throws Exception {
		SymbolExpression function = input_expression.get_function();
		SymbolArgumentList arguments = input_expression.get_argument_list();
		if(function instanceof SymbolIdentifier) {
			String name = function.generate_code(true);
			SymbolExpression result;
			if(name.equals("acos")) {
				result = this.acos(arguments);
			}
			else if(name.equals("asin")) {
				result = this.asin(arguments);
			}
			else if(name.equals("atan")) {
				result = this.atan(arguments);
			}
			else if(name.equals("atan2")) {
				result = this.atan2(arguments);
			}
			else if(name.equals("cos")) {
				result = this.cos(arguments);
			}
			else if(name.equals("cosh")) {
				result = this.cosh(arguments);
			}
			else if(name.equals("sin")) {
				result = this.sin(arguments);
			}
			else if(name.equals("sinh")) {
				result = this.sinh(arguments);
			}
			else if(name.equals("tan")) {
				result = this.tan(arguments);
			}
			else if(name.equals("tanh")) {
				result = this.tanh(arguments);
			}
			else if(name.equals("exp")) {
				result = this.exp(arguments);
			}
			else if(name.equals("log")) {
				result = this.log(arguments);
			}
			else if(name.equals("log10")) {
				result = this.log10(arguments);
			}
			else if(name.equals("sqrt")) {
				result = this.sqrt(arguments);
			}
			else if(name.equals("floor")) {
				result = this.floor(arguments);
			}
			else if(name.equals("ceil")) {
				result = this.ceil(arguments);
			}
			else if(name.equals("abs") || name.equals("fabs")) {
				result = this.fabs(arguments);
			}
			else {
				result = null;
			}
			
			if(result == null) {
				function = SymbolFactory.identifier(function.get_data_type(), name);
				ArrayList<Object> argument_list = new ArrayList<Object>();
				for(int k = 0; k < arguments.number_of_arguments(); k++) {
					argument_list.add(arguments.get_argument(k));
				}
				result = SymbolFactory.call_expression(function, argument_list);
			}
			
			return result;
		}
		else {
			return null;
		}
	}
	
	/* interpretation */
	private SymbolExpression acos(SymbolArgumentList arguments) throws Exception {
		SymbolExpression arg0 = arguments.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double result = Math.acos(((SymbolConstant) arg0).get_double());
			return SymbolFactory.sym_constant(result);
		}
		else {
			return null;
		}
	}
	private SymbolExpression asin(SymbolArgumentList arguments) throws Exception {
		SymbolExpression arg0 = arguments.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double result = Math.asin(((SymbolConstant) arg0).get_double());
			return SymbolFactory.sym_constant(result);
		}
		else {
			return null;
		}
	}
	private SymbolExpression atan(SymbolArgumentList arguments) throws Exception {
		SymbolExpression arg0 = arguments.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double result = Math.atan(((SymbolConstant) arg0).get_double());
			return SymbolFactory.sym_constant(result);
		}
		else {
			return null;
		}
	}
	private SymbolExpression atan2(SymbolArgumentList arguments) throws Exception {
		SymbolExpression arg0 = arguments.get_argument(0);
		SymbolExpression arg1 = arguments.get_argument(1);
		if(arg0 instanceof SymbolConstant) {
			if(arg1 instanceof SymbolConstant) {
				Double result = Math.atan2(
						((SymbolConstant) arg0).get_double(), 
						((SymbolConstant) arg1).get_double());
				return SymbolFactory.sym_constant(result);
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
	}
	private SymbolExpression cos(SymbolArgumentList arguments) throws Exception {
		SymbolExpression arg0 = arguments.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double result = Math.cos(((SymbolConstant) arg0).get_double());
			return SymbolFactory.sym_constant(result);
		}
		else {
			return null;
		}
	}
	private SymbolExpression cosh(SymbolArgumentList arguments) throws Exception {
		SymbolExpression arg0 = arguments.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double result = Math.cosh(((SymbolConstant) arg0).get_double());
			return SymbolFactory.sym_constant(result);
		}
		else {
			return null;
		}
	}
	private SymbolExpression sin(SymbolArgumentList arguments) throws Exception {
		SymbolExpression arg0 = arguments.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double result = Math.sin(((SymbolConstant) arg0).get_double());
			return SymbolFactory.sym_constant(result);
		}
		else {
			return null;
		}
	}
	private SymbolExpression sinh(SymbolArgumentList arguments) throws Exception {
		SymbolExpression arg0 = arguments.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double result = Math.sinh(((SymbolConstant) arg0).get_double());
			return SymbolFactory.sym_constant(result);
		}
		else {
			return null;
		}
	}
	private SymbolExpression tan(SymbolArgumentList arguments) throws Exception {
		SymbolExpression arg0 = arguments.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double result = Math.tan(((SymbolConstant) arg0).get_double());
			return SymbolFactory.sym_constant(result);
		}
		else {
			return null;
		}
	}
	private SymbolExpression tanh(SymbolArgumentList arguments) throws Exception {
		SymbolExpression arg0 = arguments.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double result = Math.tanh(((SymbolConstant) arg0).get_double());
			return SymbolFactory.sym_constant(result);
		}
		else {
			return null;
		}
	}
	private SymbolExpression exp(SymbolArgumentList arguments) throws Exception {
		SymbolExpression arg0 = arguments.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double result = Math.exp(((SymbolConstant) arg0).get_double());
			return SymbolFactory.sym_constant(result);
		}
		else {
			return null;
		}
	}
	private SymbolExpression log(SymbolArgumentList arguments) throws Exception {
		SymbolExpression arg0 = arguments.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double result = Math.log(((SymbolConstant) arg0).get_double());
			return SymbolFactory.sym_constant(result);
		}
		else {
			return null;
		}
	}
	private SymbolExpression log10(SymbolArgumentList arguments) throws Exception {
		SymbolExpression arg0 = arguments.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double result = Math.log10(((SymbolConstant) arg0).get_double());
			return SymbolFactory.sym_constant(result);
		}
		else {
			return null;
		}
	}
	private SymbolExpression sqrt(SymbolArgumentList arguments) throws Exception {
		SymbolExpression arg0 = arguments.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double result = Math.sqrt(((SymbolConstant) arg0).get_double());
			return SymbolFactory.sym_constant(result);
		}
		else {
			return null;
		}
	}
	private SymbolExpression ceil(SymbolArgumentList arguments) throws Exception {
		SymbolExpression arg0 = arguments.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double result = Math.ceil(((SymbolConstant) arg0).get_double());
			return SymbolFactory.sym_constant(result);
		}
		else {
			return null;
		}
	}
	private SymbolExpression fabs(SymbolArgumentList arguments) throws Exception {
		SymbolExpression arg0 = arguments.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double result = Math.abs(((SymbolConstant) arg0).get_double());
			return SymbolFactory.sym_constant(result);
		}
		else {
			return null;
		}
	}
	private SymbolExpression floor(SymbolArgumentList arguments) throws Exception {
		SymbolExpression arg0 = arguments.get_argument(0);
		if(arg0 instanceof SymbolConstant) {
			Double result = Math.floor(((SymbolConstant) arg0).get_double());
			return SymbolFactory.sym_constant(result);
		}
		else {
			return null;
		}
	}
	
}
