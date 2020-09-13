package com.jcsa.jcmutest.mutant.sec2mutant.lang.desc;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

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
	
}
