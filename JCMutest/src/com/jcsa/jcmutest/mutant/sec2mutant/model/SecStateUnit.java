package com.jcsa.jcmutest.mutant.sec2mutant.model;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;

/**
 * Each unit provides a symbolic description on program state.
 * @author yukimula
 *
 */
public class SecStateUnit {
	
	private SecDescription description;
	protected SecStateUnit(SecDescription description) throws Exception {
		if(description == null)
			throw new IllegalArgumentException("Invalid description");
		else 
			this.description = description;
	}
	
	public SecDescription get_description() { return this.description; }
	@Override
	public String toString() {
		return this.description.toString();
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		else if(obj instanceof SecStateUnit) {
			return obj.toString().equals(this.toString());
		}
		else {
			return false;
		}
	}
	public boolean is_constraint() { return SecFactory.is_constraint(description); }
	public boolean is_state_error() { return SecFactory.is_state_error(description); }
	
}
