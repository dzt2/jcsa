package com.jcsa.jcparse.test.path.find;

import com.jcsa.jcparse.test.path.AstExecutionType;
import com.jcsa.jcparse.test.path.AstExecutionUnit;
import com.jcsa.jcparse.test.path.InstrumentLine;
import com.jcsa.jcparse.test.path.InstrumentList;

/**
 * To consume the line in instrumental list.
 * @author yukimula
 *
 */
class InstrumentListConsumer {
	private InstrumentList list;
	private int cursor;
	InstrumentListConsumer(InstrumentList list) {
		this.list = list;
		this.cursor = 0;
	}
	InstrumentLine get() {
		if(this.cursor >= list.length())
			return null;
		else
			return this.list.get_line(cursor);
	}
	void next() { this.cursor++; }
	boolean match(AstExecutionUnit unit) {
		InstrumentLine line = this.get();
		if(line == null)
			return false;	/* no more line from tokens */
		else {
			switch(line.get_type()) {
			case beg_stmt:		/* beg_stmt[statement] */
				return (unit.get_type() == AstExecutionType.beg_stmt
						|| unit.get_type() == AstExecutionType.execute)
						&& unit.get_location() == line.get_location();
			case end_stmt:		/* end_stmt [statement] */
				return (unit.get_type() == AstExecutionType.end_stmt
						|| unit.get_type() == AstExecutionType.execute)
						&& unit.get_location() == line.get_location();
			case evaluate:		/* evaluate|end_expr[expression] */
			default:
				return (unit.get_type() == AstExecutionType.end_expr
						|| unit.get_type() == AstExecutionType.evaluate)
						&& unit.get_location() == line.get_location();
			}
		}
	}
}
