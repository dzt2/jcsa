package com.jcsa.jcmuta.mutant.ast2mutation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;

/**
 * trap_at_statement(loop_statement, loop_time)
 * @author yukimula
 *
 */
public class TTRPMutationGenerator extends AstMutationGenerator {
	
	private List<Integer> loop_times;
	protected TTRPMutationGenerator() {
		this.loop_times = new ArrayList<Integer>();
		
		this.loop_times.add(2);
		this.loop_times.add(3);
		this.loop_times.add(4);
		this.loop_times.add(5);
		this.loop_times.add(6);
		this.loop_times.add(8);
		this.loop_times.add(10);
		this.loop_times.add(12);
		this.loop_times.add(14);
		this.loop_times.add(16);
		this.loop_times.add(18);
		this.loop_times.add(20);
		
		this.loop_times.add(25);
		this.loop_times.add(30);
		this.loop_times.add(40);
		this.loop_times.add(50);
		this.loop_times.add(60);
		this.loop_times.add(80);
		
		this.loop_times.add(100);
		this.loop_times.add(125);
		this.loop_times.add(150);
		this.loop_times.add(175);
		this.loop_times.add(200);
		this.loop_times.add(225);
		this.loop_times.add(250);
		this.loop_times.add(275);
		this.loop_times.add(300);
		
		this.loop_times.add(400);
		this.loop_times.add(500);
		this.loop_times.add(600);
		this.loop_times.add(700);
		this.loop_times.add(800);
		this.loop_times.add(900);
		this.loop_times.add(1000);
	}

	@Override
	protected void collect_locations(AstNode location, Collection<AstNode> locations) throws Exception {
		if(location instanceof AstWhileStatement
			|| location instanceof AstDoWhileStatement
			|| location instanceof AstForStatement) {
			locations.add(location);
		}
	}

	@Override
	protected void generate_mutations(AstNode location, Collection<AstMutation> mutations) throws Exception {
		AstStatement statement = (AstStatement) location;
		for(Integer loop_time : loop_times) {
			mutations.add(AstMutation.TTRP(statement, loop_time.intValue()));
		}
	}

}
