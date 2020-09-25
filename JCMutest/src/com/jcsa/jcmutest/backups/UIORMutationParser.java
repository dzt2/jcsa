package com.jcsa.jcmutest.backups;

import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;

public class UIORMutationParser extends CirMutationParser {
	
	/* ++x ==> --x */
	private void prev_inc_to_prev_dec(CirTree tree, AstNode 
			location, List<CirMutation> targets) throws Exception {
		CirAssignStatement inc_stmt = (CirAssignStatement) get_cir_nodes(
					tree, location, CirIncreAssignStatement.class).get(0);
		targets.add(CirMutations.inc_expression(inc_stmt.get_rvalue(), -2));
	}
	/* ++x ==> x++ */
	private void prev_inc_to_post_inc(CirTree tree, AstNode 
			location, List<CirMutation> targets) throws Exception {
		CirExpression use_expr = this.get_use_point(tree, location);
		targets.add(CirMutations.inc_expression(use_expr, -1));
	}
	/* ++x ==> x-- */
	private void prev_inc_to_post_dec(CirTree tree, AstNode 
			location, List<CirMutation> targets) throws Exception {
		CirAssignStatement inc_stmt = (CirAssignStatement) get_cir_nodes(
				tree, location, CirIncreAssignStatement.class).get(0);
		targets.add(CirMutations.inc_expression(inc_stmt.get_rvalue(), -2));
		CirExpression use_expr = this.get_use_point(tree, location);
		targets.add(CirMutations.inc_expression(use_expr, -1));
	}
	
	/* --x ==> ++x */
	private void prev_dec_to_prev_inc(CirTree tree, AstNode 
			location, List<CirMutation> targets) throws Exception {
		CirAssignStatement inc_stmt = (CirAssignStatement) get_cir_nodes(
					tree, location, CirIncreAssignStatement.class).get(0);
		targets.add(CirMutations.inc_expression(inc_stmt.get_rvalue(), 2));
	}
	/* --x ==> x-- */
	private void prev_dec_to_post_dec(CirTree tree, AstNode 
			location, List<CirMutation> targets) throws Exception {
		CirExpression use_expr = this.get_use_point(tree, location);
		targets.add(CirMutations.inc_expression(use_expr, 1));
	}
	/* --x ==> x++ */
	private void prev_dec_to_post_inc(CirTree tree, AstNode 
			location, List<CirMutation> targets) throws Exception {
		CirAssignStatement inc_stmt = (CirAssignStatement) get_cir_nodes(
				tree, location, CirIncreAssignStatement.class).get(0);
		targets.add(CirMutations.inc_expression(inc_stmt.get_rvalue(), 2));
		CirExpression use_expr = this.get_use_point(tree, location);
		targets.add(CirMutations.inc_expression(use_expr, 1));
	}
	
	/* x++ ==> x-- */
	private void post_inc_to_post_dec(CirTree tree, AstNode 
			location, List<CirMutation> targets) throws Exception {
		CirAssignStatement inc_stmt = (CirAssignStatement) get_cir_nodes(
					tree, location, CirIncreAssignStatement.class).get(0);
		targets.add(CirMutations.inc_expression(inc_stmt.get_rvalue(), -2));
	}
	/* x++ ==> ++x */
	private void post_inc_to_prev_inc(CirTree tree, AstNode 
			location, List<CirMutation> targets) throws Exception {
		CirExpression use_expr = this.get_use_point(tree, location);
		targets.add(CirMutations.inc_expression(use_expr, 1));
	}
	/* x++ ==> --x */
	private void post_inc_to_prev_dec(CirTree tree, AstNode 
			location, List<CirMutation> targets) throws Exception {
		CirAssignStatement inc_stmt = (CirAssignStatement) get_cir_nodes(
				tree, location, CirIncreAssignStatement.class).get(0);
		targets.add(CirMutations.inc_expression(inc_stmt.get_rvalue(), -2));
		CirExpression use_expr = this.get_use_point(tree, location);
		targets.add(CirMutations.inc_expression(use_expr, -1));
	}
	
	/* x-- ==> x++ */
	private void post_dec_to_post_inc(CirTree tree, AstNode 
			location, List<CirMutation> targets) throws Exception {
		CirAssignStatement inc_stmt = (CirAssignStatement) get_cir_nodes(
					tree, location, CirIncreAssignStatement.class).get(0);
		targets.add(CirMutations.inc_expression(inc_stmt.get_rvalue(), 2));
	}
	/* x-- ==> --x */
	private void post_dec_to_prev_dec(CirTree tree, AstNode 
			location, List<CirMutation> targets) throws Exception {
		CirExpression use_expr = this.get_use_point(tree, location);
		targets.add(CirMutations.inc_expression(use_expr, -1));
	}
	/* x-- ==> ++x */
	private void post_dec_to_prev_inc(CirTree tree, AstNode 
			location, List<CirMutation> targets) throws Exception {
		CirAssignStatement inc_stmt = (CirAssignStatement) get_cir_nodes(
					tree, location, CirIncreAssignStatement.class).get(0);
		targets.add(CirMutations.inc_expression(inc_stmt.get_rvalue(), 2));
		CirExpression use_expr = this.get_use_point(tree, location);
		targets.add(CirMutations.inc_expression(use_expr, 1));
	}
	
	@Override
	protected void parse(CirTree tree, AstMutation source, List<CirMutation> targets) throws Exception {
		AstNode location = source.get_location();
		switch(source.get_operator()) {
		case prev_inc_to_prev_dec:	this.prev_inc_to_prev_dec(tree, location, targets); break;
		case prev_inc_to_post_inc:	this.prev_inc_to_post_inc(tree, location, targets); break;
		case prev_inc_to_post_dec:	this.prev_inc_to_post_dec(tree, location, targets); break;
		case prev_dec_to_prev_inc: 	this.prev_dec_to_prev_inc(tree, location, targets); break;
		case prev_dec_to_post_dec: 	this.prev_dec_to_post_dec(tree, location, targets); break;
		case prev_dec_to_post_inc: 	this.prev_dec_to_post_inc(tree, location, targets); break;
		case post_inc_to_post_dec:	this.post_inc_to_post_dec(tree, location, targets); break;
		case post_inc_to_prev_inc:	this.post_inc_to_prev_inc(tree, location, targets); break;
		case post_inc_to_prev_dec:	this.post_inc_to_prev_dec(tree, location, targets); break;
		case post_dec_to_post_inc:	this.post_dec_to_post_inc(tree, location, targets); break;
		case post_dec_to_prev_dec:	this.post_dec_to_prev_dec(tree, location, targets); break;
		case post_dec_to_prev_inc:	this.post_dec_to_prev_inc(tree, location, targets); break;
		default: throw new IllegalArgumentException("Invalid source: " + source.toString());
		}
	}

}
