package com.jcsa.jcparse.test.path.find;

import com.jcsa.jcparse.test.path.AstExecutionNode;
import com.jcsa.jcparse.test.path.AstExecutionType;
import com.jcsa.jcparse.test.path.AstExecutionUnit;
import com.jcsa.jcparse.test.path.InstrumentLine;
import com.jcsa.jcparse.test.path.InstrumentList;

/**
 * The consumer of instrumental list, which is used as the input of path construction.
 * 
 * @author yukimula
 *
 */
class InstrumentListConsumer {
	
	/* attributes */
	/** the list of instrumental lines to be consumed **/
	private InstrumentList list;
	/** the index to the instrumental line in the list **/
	private int instrument_index;
	
	/* constructor */
	/**
	 * create a consumer to consume the instrumental lines in the given list.
	 * @param list
	 * @throws IllegalArgumentException
	 */
	protected InstrumentListConsumer(InstrumentList list) throws IllegalArgumentException {
		if(list == null)
			throw new IllegalArgumentException("Invalid list: null");
		else {
			this.list = list;
			this.instrument_index = 0;
		}
	}
	
	/* access APIs */
	/**
	 * @return whether there is more instrumental line to be consumed
	 */
	protected boolean has() { 
		return this.instrument_index < this.list.length(); 
	}
	/**
	 * @return the current instrumental line to be consumed or null
	 */
	protected InstrumentLine get() {
		if(this.instrument_index < this.list.length())
			return this.list.get_line(this.instrument_index);
		else
			return null;
	}
	/**
	 * @return true if the index is moved to the next line which is valid
	 */
	protected boolean next() {
		if(this.instrument_index < this.list.length()) {
			this.instrument_index++;
		}
		return this.instrument_index < this.list.length();
	}
	/**
	 * @param execution
	 * @return whether the execution node matches with the current instrumental line.
	 */
	protected boolean match(AstExecutionNode execution) {
		if(execution != null && this.has()) {
			InstrumentLine line = this.get();
			AstExecutionUnit unit = execution.get_unit();
			switch(unit.get_type()) {
			case beg_stmt:
				return (unit.get_type() == AstExecutionType.beg_stmt
						|| unit.get_type() == AstExecutionType.execute)
						&& (unit.get_location() == line.get_location());
			case end_stmt:
				return (unit.get_type() == AstExecutionType.end_stmt
						|| unit.get_type() == AstExecutionType.execute)
						&& (unit.get_location() == line.get_location());
			default:
				return (unit.get_type() == AstExecutionType.end_expr
						|| unit.get_type() == AstExecutionType.evaluate)
						&& (unit.get_location() == line.get_location());
			}
		}
		else {
			return false;
		}
	}
	
}
