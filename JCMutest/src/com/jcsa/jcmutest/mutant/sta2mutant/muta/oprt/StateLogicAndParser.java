package com.jcsa.jcmutest.mutant.sta2mutant.muta.oprt;

import com.jcsa.jcmutest.mutant.sta2mutant.muta.StateOperatorParser;

public class StateLogicAndParser extends StateOperatorParser {

	@Override
	protected boolean to_assign() throws Exception {
		return this.unsupport_exception();
	}

	@Override
	protected boolean arith_add() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean arith_sub() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean arith_mul() throws Exception {
		return this.report_equivalences();
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
		return this.report_equivalences();
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
		return this.report_equivalences();
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
