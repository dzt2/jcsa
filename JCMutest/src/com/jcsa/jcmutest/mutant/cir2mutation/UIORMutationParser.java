package com.jcsa.jcmutest.mutant.cir2mutation;

import java.util.Collection;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutations;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirSaveAssignStatement;

public class UIORMutationParser extends CirMutationParser {
	
	private void prev_inc_to_prev_dec(CirTree cir_tree, AstMutation 
			source, Collection<CirMutation> targets) throws Exception {
		CirAssignStatement statement = (CirAssignStatement) this.get_cir_node(
				cir_tree, source.get_location(), CirIncreAssignStatement.class, 0);
		CirExpression expression = statement.get_rvalue();
		targets.add(CirMutations.VINC(expression, -2));
	}
	private void prev_inc_to_post_inc(CirTree cir_tree, AstMutation 
			source, Collection<CirMutation> targets) throws Exception {
		CirExpression expression = this.get_use_point(
				cir_tree, (AstExpression) source.get_location());
		targets.add(CirMutations.VINC(expression, -1));
	}
	private void prev_inc_to_post_dec(CirTree cir_tree, AstMutation 
			source, Collection<CirMutation> targets) throws Exception {
		CirAssignStatement statement = (CirAssignStatement) this.get_cir_node(
				cir_tree, source.get_location(), CirIncreAssignStatement.class, 0);
		targets.add(CirMutations.VINC(statement.get_rvalue(), -2));
		CirExpression expression = this.get_use_point(
				cir_tree, (AstExpression) source.get_location());
		targets.add(CirMutations.VINC(expression, -1));
	}
	
	private void prev_dec_to_prev_inc(CirTree cir_tree, AstMutation 
			source, Collection<CirMutation> targets) throws Exception {
		CirAssignStatement statement = (CirAssignStatement) this.get_cir_node(
				cir_tree, source.get_location(), CirIncreAssignStatement.class, 0);
		CirExpression expression = statement.get_rvalue();
		targets.add(CirMutations.VINC(expression, 2));
	}
	private void prev_dec_to_post_dec(CirTree cir_tree, AstMutation 
			source, Collection<CirMutation> targets) throws Exception {
		CirExpression expression = this.get_use_point(
				cir_tree, (AstExpression) source.get_location());
		targets.add(CirMutations.VINC(expression, 1));
	}
	private void prev_dec_to_post_inc(CirTree cir_tree, AstMutation 
			source, Collection<CirMutation> targets) throws Exception {
		CirAssignStatement statement = (CirAssignStatement) this.get_cir_node(
				cir_tree, source.get_location(), CirIncreAssignStatement.class, 0);
		targets.add(CirMutations.VINC(statement.get_rvalue(), 2));
		CirExpression expression = this.get_use_point(
				cir_tree, (AstExpression) source.get_location());
		targets.add(CirMutations.VINC(expression, 1));
	}
	
	private void post_inc_to_post_dec(CirTree cir_tree, AstMutation 
			source, Collection<CirMutation> targets) throws Exception {
		CirAssignStatement inc_statement = (CirAssignStatement) this.get_cir_node(
				cir_tree, source.get_location(), CirIncreAssignStatement.class, 0);
		targets.add(CirMutations.VINC(inc_statement.get_rvalue(), -2));
	}
	private void post_inc_to_prev_inc(CirTree cir_tree, AstMutation 
			source, Collection<CirMutation> targets) throws Exception {
		CirAssignStatement sav_statement = (CirAssignStatement) this.get_cir_node(
				cir_tree, source.get_location(), CirSaveAssignStatement.class, 0);
		targets.add(CirMutations.VINC(sav_statement.get_rvalue(), 1));
	}
	private void post_inc_to_prev_dec(CirTree cir_tree, AstMutation 
			source, Collection<CirMutation> targets) throws Exception {
		CirAssignStatement inc_statement = (CirAssignStatement) this.get_cir_node(
				cir_tree, source.get_location(), CirIncreAssignStatement.class, 0);
		targets.add(CirMutations.VINC(inc_statement.get_rvalue(), -2));
		
		CirAssignStatement sav_statement = (CirAssignStatement) this.get_cir_node(
				cir_tree, source.get_location(), CirSaveAssignStatement.class, 0);
		targets.add(CirMutations.VINC(sav_statement.get_rvalue(), -1));
	}
	
	private void post_dec_to_post_inc(CirTree cir_tree, AstMutation 
			source, Collection<CirMutation> targets) throws Exception {
		CirAssignStatement inc_statement = (CirAssignStatement) this.get_cir_node(
				cir_tree, source.get_location(), CirIncreAssignStatement.class, 0);
		targets.add(CirMutations.VINC(inc_statement.get_rvalue(), 2));
	}
	private void post_dec_to_prev_dec(CirTree cir_tree, AstMutation 
			source, Collection<CirMutation> targets) throws Exception {
		CirAssignStatement sav_statement = (CirAssignStatement) this.get_cir_node(
				cir_tree, source.get_location(), CirSaveAssignStatement.class, 0);
		targets.add(CirMutations.VINC(sav_statement.get_rvalue(), -1));
	}
	private void post_dec_to_prev_inc(CirTree cir_tree, AstMutation 
			source, Collection<CirMutation> targets) throws Exception {
		CirAssignStatement inc_statement = (CirAssignStatement) this.get_cir_node(
				cir_tree, source.get_location(), CirIncreAssignStatement.class, 0);
		targets.add(CirMutations.VINC(inc_statement.get_rvalue(), 2));
		
		CirAssignStatement sav_statement = (CirAssignStatement) this.get_cir_node(
				cir_tree, source.get_location(), CirSaveAssignStatement.class, 0);
		targets.add(CirMutations.VINC(sav_statement.get_rvalue(), 1));
	}
	
	@Override
	public void parse(CirTree cir_tree, AstMutation source, Collection<CirMutation> targets) throws Exception {
		switch(source.get_operator()) {
		case prev_inc_to_prev_dec:	this.prev_inc_to_prev_dec(cir_tree, source, targets); break;
		case prev_inc_to_post_inc:	this.prev_inc_to_post_inc(cir_tree, source, targets); break;
		case prev_inc_to_post_dec:	this.prev_inc_to_post_dec(cir_tree, source, targets); break;
		case prev_dec_to_prev_inc:	this.prev_dec_to_prev_inc(cir_tree, source, targets); break;
		case prev_dec_to_post_dec:	this.prev_dec_to_post_dec(cir_tree, source, targets); break;
		case prev_dec_to_post_inc:	this.prev_dec_to_post_inc(cir_tree, source, targets); break;
		case post_inc_to_post_dec:	this.post_inc_to_post_dec(cir_tree, source, targets); break;
		case post_inc_to_prev_inc:	this.post_inc_to_prev_inc(cir_tree, source, targets); break;
		case post_inc_to_prev_dec:	this.post_inc_to_prev_dec(cir_tree, source, targets); break;
		case post_dec_to_post_inc:	this.post_dec_to_post_inc(cir_tree, source, targets); break;
		case post_dec_to_prev_dec:	this.post_dec_to_prev_dec(cir_tree, source, targets); break;
		case post_dec_to_prev_inc:	this.post_dec_to_prev_inc(cir_tree, source, targets); break;
		default: throw new IllegalArgumentException("Unsupport mutation: " + source.toString());
		}
	}

}
