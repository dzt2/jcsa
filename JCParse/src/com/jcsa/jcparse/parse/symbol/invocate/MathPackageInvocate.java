package com.jcsa.jcparse.parse.symbol.invocate;

import com.jcsa.jcparse.lang.symbol.SymbolCallExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolIdentifier;
import com.jcsa.jcparse.parse.symbol.SymbolInvocate;

public class MathPackageInvocate implements SymbolInvocate {

	@Override
	public SymbolExpression invocate(SymbolCallExpression source) throws Exception {
		SymbolExpression function = source.get_function();
		if(function instanceof SymbolIdentifier) {
			String name = function.generate_code(true);
			if(name.equals("sin"))
				return this.sin(source);
			else if(name.equals("cos"))
				return this.cos(source);
			else if(name.equals("tan"))
				return this.tan(source);
			else if(name.equals("asin"))
				return this.asin(source);
			else if(name.equals("acos"))
				return this.acos(source);
			else if(name.equals("atan"))
				return this.atan(source);
			else if(name.equals("atan2"))
				return this.atan2(source);
			
			else if(name.equals("sinh"))
				return this.sinh(source);
			else if(name.equals("cosh"))
				return this.cosh(source);
			else if(name.equals("tanh"))
				return this.tanh(source);
			
			else if(name.equals("log"))
				return this.log(source);
			else if(name.equals("log10"))
				return this.log10(source);
			else if(name.equals("exp"))
				return this.exp(source);
			else if(name.equals("sqrt"))
				return this.sqrt(source);
			
			else if(name.equals("ldexp"))
				return this.ldexp(source);
			else if(name.equals("pow"))
				return this.pow(source);
			
			else if(name.equals("fabs"))
				return this.fabs(source);
			else if(name.equals("ceil"))
				return this.ceil(source);
			else if(name.equals("floor"))
				return this.floor(source);
			else if(name.equals("fmod"))
				return this.fmod(source);
			
			else
				return source;
		}
		else {
			return source;
		}
	}
	
	private SymbolExpression get_argument(SymbolCallExpression source, int k) throws Exception {
		return source.get_argument_list().get_argument(k).evaluate(null);
	}
	
	private SymbolExpression sin(SymbolCallExpression source) throws Exception {
		SymbolExpression argument = this.get_argument(source, 0);
		if(argument instanceof SymbolConstant) {
			double result = Math.sin(((SymbolConstant) argument).get_double());
			return SymbolFactory.sym_expression(Double.valueOf(result));
		}
		else {
			return source;
		}
	}
	private SymbolExpression cos(SymbolCallExpression source) throws Exception {
		SymbolExpression argument = this.get_argument(source, 0);
		if(argument instanceof SymbolConstant) {
			double result = Math.cos(((SymbolConstant) argument).get_double());
			return SymbolFactory.sym_expression(Double.valueOf(result));
		}
		else {
			return source;
		}
	}
	private SymbolExpression tan(SymbolCallExpression source) throws Exception {
		SymbolExpression argument = this.get_argument(source, 0);
		if(argument instanceof SymbolConstant) {
			double result = Math.tan(((SymbolConstant) argument).get_double());
			return SymbolFactory.sym_expression(Double.valueOf(result));
		}
		else {
			return source;
		}
	}
	
	private SymbolExpression asin(SymbolCallExpression source) throws Exception {
		SymbolExpression argument = this.get_argument(source, 0);
		if(argument instanceof SymbolConstant) {
			double result = Math.asin(((SymbolConstant) argument).get_double());
			return SymbolFactory.sym_expression(Double.valueOf(result));
		}
		else {
			return source;
		}
	}
	private SymbolExpression acos(SymbolCallExpression source) throws Exception {
		SymbolExpression argument = this.get_argument(source, 0);
		if(argument instanceof SymbolConstant) {
			double result = Math.acos(((SymbolConstant) argument).get_double());
			return SymbolFactory.sym_expression(Double.valueOf(result));
		}
		else {
			return source;
		}
	}
	private SymbolExpression atan(SymbolCallExpression source) throws Exception {
		SymbolExpression argument = this.get_argument(source, 0);
		if(argument instanceof SymbolConstant) {
			double result = Math.atan(((SymbolConstant) argument).get_double());
			return SymbolFactory.sym_expression(Double.valueOf(result));
		}
		else {
			return source;
		}
	}
	private SymbolExpression atan2(SymbolCallExpression source) throws Exception {
		SymbolExpression argument0 = this.get_argument(source, 0);
		SymbolExpression argument1 = this.get_argument(source, 1);
		if(argument0 instanceof SymbolConstant && argument1 instanceof SymbolConstant) {
			double result = Math.atan2(
					((SymbolConstant) argument0).get_double(),
					((SymbolConstant) argument1).get_double());
			return SymbolFactory.sym_expression(Double.valueOf(result));
		}
		else {
			return source;
		}
	}
	
	private SymbolExpression sinh(SymbolCallExpression source) throws Exception {
		SymbolExpression argument = this.get_argument(source, 0);
		if(argument instanceof SymbolConstant) {
			double result = Math.sinh(((SymbolConstant) argument).get_double());
			return SymbolFactory.sym_expression(Double.valueOf(result));
		}
		else {
			return source;
		}
	}
	private SymbolExpression cosh(SymbolCallExpression source) throws Exception {
		SymbolExpression argument = this.get_argument(source, 0);
		if(argument instanceof SymbolConstant) {
			double result = Math.cosh(((SymbolConstant) argument).get_double());
			return SymbolFactory.sym_expression(Double.valueOf(result));
		}
		else {
			return source;
		}
	}
	private SymbolExpression tanh(SymbolCallExpression source) throws Exception {
		SymbolExpression argument = this.get_argument(source, 0);
		if(argument instanceof SymbolConstant) {
			double result = Math.tanh(((SymbolConstant) argument).get_double());
			return SymbolFactory.sym_expression(Double.valueOf(result));
		}
		else {
			return source;
		}
	}
	
	private SymbolExpression log(SymbolCallExpression source) throws Exception {
		SymbolExpression argument = this.get_argument(source, 0);
		if(argument instanceof SymbolConstant) {
			double result = Math.log(((SymbolConstant) argument).get_double());
			return SymbolFactory.sym_expression(Double.valueOf(result));
		}
		else {
			return source;
		}
	}
	private SymbolExpression log10(SymbolCallExpression source) throws Exception {
		SymbolExpression argument = this.get_argument(source, 0);
		if(argument instanceof SymbolConstant) {
			double result = Math.log10(((SymbolConstant) argument).get_double());
			return SymbolFactory.sym_expression(Double.valueOf(result));
		}
		else {
			return source;
		}
	}
	private SymbolExpression exp(SymbolCallExpression source) throws Exception {
		SymbolExpression argument = this.get_argument(source, 0);
		if(argument instanceof SymbolConstant) {
			double result = Math.exp(((SymbolConstant) argument).get_double());
			return SymbolFactory.sym_expression(Double.valueOf(result));
		}
		else {
			return source;
		}
	}
	private SymbolExpression sqrt(SymbolCallExpression source) throws Exception {
		SymbolExpression argument = this.get_argument(source, 0);
		if(argument instanceof SymbolConstant) {
			double result = Math.sqrt(((SymbolConstant) argument).get_double());
			return SymbolFactory.sym_expression(Double.valueOf(result));
		}
		else {
			return source;
		}
	}
	
	private SymbolExpression ldexp(SymbolCallExpression source) throws Exception {
		SymbolExpression argument0 = this.get_argument(source, 0);
		SymbolExpression argument1 = this.get_argument(source, 1);
		if(argument0 instanceof SymbolConstant && argument1 instanceof SymbolConstant) {
			double result = ((SymbolConstant) argument0).get_double() * 
					Math.pow(2.0, ((SymbolConstant) argument1).get_long());
			return SymbolFactory.sym_expression(Double.valueOf(result));
		}
		else {
			return source;
		}
	}
	private SymbolExpression pow(SymbolCallExpression source) throws Exception {
		SymbolExpression argument0 = this.get_argument(source, 0);
		SymbolExpression argument1 = this.get_argument(source, 1);
		if(argument0 instanceof SymbolConstant && argument1 instanceof SymbolConstant) {
			double result = Math.pow(
					((SymbolConstant) argument0).get_double(),
					((SymbolConstant) argument1).get_double());
			return SymbolFactory.sym_expression(Double.valueOf(result));
		}
		else {
			return source;
		}
	}
	
	private SymbolExpression fabs(SymbolCallExpression source) throws Exception {
		SymbolExpression argument = this.get_argument(source, 0);
		if(argument instanceof SymbolConstant) {
			double result = Math.abs(((SymbolConstant) argument).get_double());
			return SymbolFactory.sym_expression(Double.valueOf(result));
		}
		else {
			return source;
		}
	}
	private SymbolExpression ceil(SymbolCallExpression source) throws Exception {
		SymbolExpression argument = this.get_argument(source, 0);
		if(argument instanceof SymbolConstant) {
			double result = Math.ceil(((SymbolConstant) argument).get_double());
			return SymbolFactory.sym_expression(Double.valueOf(result));
		}
		else {
			return source;
		}
	}
	private SymbolExpression floor(SymbolCallExpression source) throws Exception {
		SymbolExpression argument = this.get_argument(source, 0);
		if(argument instanceof SymbolConstant) {
			double result = Math.floor(((SymbolConstant) argument).get_double());
			return SymbolFactory.sym_expression(Double.valueOf(result));
		}
		else {
			return source;
		}
	}
	private SymbolExpression fmod(SymbolCallExpression source) throws Exception {
		SymbolExpression argument0 = this.get_argument(source, 0);
		SymbolExpression argument1 = this.get_argument(source, 1);
		if(argument0 instanceof SymbolConstant && argument1 instanceof SymbolConstant) {
			long result = ((SymbolConstant) argument0).get_long()
					% ((SymbolConstant) argument1).get_long();
			return SymbolFactory.sym_expression(Double.valueOf(result));
		}
		else {
			return source;
		}
	}
	
}
