package com.jcsa.jcmuta.mutant.err2mutation;

import java.util.LinkedList;
import java.util.List;

/**
 * State error is defined as following:<br>
 * <code>
 * 	execute(stmt)					<br>
 * 	not_execute(stmt)				<br>
 * 	set_bool --> not_bool			<br>
 * 	set_numb, xor_numb				<br>
 * 	neg_numb, rsv_numb				<br>
 * 	dif_numb, inc_numb, dec_numb	<br>
 * 	chg_numb						<br>
 * 	dif_addr, set_addr, mov_addr	<br>
 * 	mut_value, mut_refer			<br>
 * </code>
 * @author yukimula
 *
 */
public class StateError {
	
	/* attributes */
	/** the state error type **/
	private ErrorType type;
	/** the operands used in state error **/
	protected List<Object> operands;
	
	/* constructor */
	/**
	 * create the state error instance
	 * @param type
	 * @throws IllegalArgumentException
	 */
	protected StateError(ErrorType type) throws IllegalArgumentException {
		if(type == null)
			throw new IllegalArgumentException("Invalid type: " + type);
		else { this.type = type; this.operands = new LinkedList<Object>(); }
	}
	
	/* getters */
	/**
	 * get the error type
	 * @return
	 */
	public ErrorType get_type() { return this.type; }
	/**
	 * get the operands in the error
	 * @return
	 */
	public Iterable<Object> get_operands() { return this.operands; }
	/**
	 * get the number of operands in the state error
	 * @return
	 */
	public int number_of_operands() { return this.operands.size(); }
	/**
	 * get the kth operand in the state error
	 * @param k
	 * @return
	 * @throws IllegalArgumentException
	 */
	public Object get_operand(int k) throws IllegalArgumentException { return this.operands.get(k); }
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		
		buffer.append(this.type.toString());
		buffer.append("[ ");
		for(Object operand : this.operands) {
			buffer.append(operand.toString());
			buffer.append("; ");
		}
		buffer.append("]");
		
		return buffer.toString();
	}
	
}
