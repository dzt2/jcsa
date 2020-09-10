package com.jcsa.jcmutest.mutant.sec2mutant.muta.proc;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SetOperatorProcess;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;

public class SetArithSubProcess extends SetOperatorProcess {

	@Override
	protected boolean to_assign() throws Exception {
		return this.unsupport_exception();
	}

	@Override
	protected boolean arith_add() throws Exception {
		/**
		 * 	[y != 0] --> add_expr(expr, +, 2 * y)
		 **/
		SecConstraint constraint; SecDescription init_error;
		constraint = this.get_constraint(this.sym_expression(
				COperator.not_equals, this.roperand, Integer.valueOf(0)));
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.add_expression(this.sym_expression(
					COperator.arith_mul, Integer.valueOf(2), roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_sub() throws Exception {
		return this.unsupport_exception();
	}

	@Override
	protected boolean arith_mul() throws Exception {
		/**
		 * 	[x != 0 || y != 0] --> set_expr(x * y)
		 **/
		SecDescription constraint, init_error; SymExpression condition;
		List<SecDescription> constraints = new ArrayList<SecDescription>();
		
		condition = this.sym_expression(COperator.not_equals, this.loperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		condition = this.sym_expression(COperator.not_equals, this.roperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		
		constraint = this.disjunct(constraints);
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(sym_expression(COperator.arith_mul, loperand, roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_div() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean arith_mod() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean bitws_and() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean bitws_ior() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean bitws_xor() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean bitws_lsh() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean bitws_rsh() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean logic_and() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean logic_ior() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean greater_tn() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean greater_eq() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean smaller_tn() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean smaller_eq() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean equal_with() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean not_equals() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

}
