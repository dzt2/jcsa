package com.jcsa.jcmutest.mutant.sec2mutant.lang.desc;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.stmt.SecPasStatementError;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymConstant;
import com.jcsa.jcparse.lang.sym.SymContexts;
import com.jcsa.jcparse.lang.sym.SymExpression;

public class SecConjunctDescriptions extends SecDescriptions {

	public SecConjunctDescriptions(CirStatement statement) throws Exception {
		super(statement, SecKeywords.conjunct);
	}
	
	@Override
	public boolean is_constraint() {
		for(int k = 0; k < this.number_of_descriptions(); k++) {
			if(!this.get_description(k).is_constraint()) {
				return false;
			}
		}
		return this.number_of_descriptions() > 0;
	}
	
	@Override
	public boolean is_state_error() {
		for(int k = 0; k < this.number_of_descriptions(); k++) {
			if(!this.get_description(k).is_state_error()) {
				return false;
			}
		}
		return this.number_of_descriptions() > 0;
	}
	
	@Override
	public SecDescription optimize(SymContexts contexts) throws Exception {
		if(this.is_constraint()) {
			return this.optimize_constraints(contexts);
		}
		else if(this.is_state_error()) {
			return this.optimize_state_error(contexts);
		}
		else {
			throw new IllegalArgumentException("Inconsistent: " + this.generate_code());
		}
	}
	
	/**
	 * @param contexts
	 * @return optimize as the constraint description.
	 * @throws Exception
	 */
	private SecDescription optimize_constraints(SymContexts contexts) throws Exception {
		/* using map to avoid duplicated constraints within the children level */
		Map<String, SecDescription> descriptions = new HashMap<String, SecDescription>();
		
		/* collect the optimized constraints in the map */
		for(int k = 0; k < this.number_of_descriptions(); k++) {
			SecDescription description = this.get_description(k).optimize(contexts);
			if(description.is_constraint()) {
				if(description instanceof SecConstraint) {
					SymExpression condition = 
							((SecConstraint) description).get_condition().get_expression();
					if(condition instanceof SymConstant) {
						if(((SymConstant) condition).get_bool()) {
							continue;			/* ignore the true constraints */
						}
						else {
							return description;	/* enforce false in conjunctions */
						}
					}
					else {
						descriptions.put(description.generate_code(), description);
					}
				}
				else {
					descriptions.put(description.generate_code(), description);
				}
			}
			else {
				throw new IllegalArgumentException("Inconsistent: " + description);
			}
		}
		
		/* all of are true and get true in conjunctions */
		if(descriptions.isEmpty()) {
			return SecFactory.assert_constraint(this.get_location().get_statement(), Boolean.TRUE, true);
		}
		/* the unique constraint in the conjunctions */
		else if(descriptions.size() == 1) {
			return descriptions.values().iterator().next();
		}
		/* obtain the conjunctions for the descriptions being generated */
		else {
			return SecFactory.conjunct(this.get_location().get_statement(), descriptions.values());
		}
	}
	
	/**
	 * @param contexts
	 * @return 
	 * @throws Exception
	 */
	private SecDescription optimize_state_error(SymContexts contexts) throws Exception {
		/* using map to avoid duplicated constraints within the children level */
		Map<String, SecDescription> descriptions = new HashMap<String, SecDescription>();
		
		/* collect the optimized constraints in the map */
		for(int k = 0; k < this.number_of_descriptions(); k++) {
			SecDescription description = this.get_description(k).optimize(contexts);
			if(description.is_state_error()) {
				if(description instanceof SecPasStatementError) {
					continue;	/* no errors will be ignored in the conjunctions */
				}
				else {
					descriptions.put(description.generate_code(), description);
				}
			}
			else {
				throw new IllegalArgumentException("Inconsistent: " + this.generate_code());
			}
		}
		
		/* no errors occur in the conjunctions and thus returns no-error */
		if(descriptions.isEmpty()) {
			return SecFactory.pass_statement(this.get_location().get_statement());
		}
		/* the unique constraint in the conjunctions */
		else if(descriptions.size() == 1) {
			return descriptions.values().iterator().next();
		}
		/* obtain the conjunctions for the descriptions being generated */
		else {
			return SecFactory.conjunct(this.get_location().get_statement(), descriptions.values());
		}
	}
	
}
