package com.jcsa.jcmuta.mutant.error2mutation;

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
	
	/* parameters */
	/** null address **/
	public static final String NullPointer = "#null";
	/** invalid address **/
	public static final String InvalidAddr = "#invalid";
	
	/* attributes */
	/** set from which the error is created **/
	private StateErrors errors;
	/** the state error type **/
	private ErrorType type;
	/** the level of the state error **/
	private int error_level;
	/** the operands used in state error **/
	protected List<Object> operands;
	
	/* constructor */
	/**
	 * create the state error instance
	 * @param type
	 * @throws IllegalArgumentException
	 */
	protected StateError(StateErrors errors, ErrorType type) throws IllegalArgumentException {
		if(type == null)
			throw new IllegalArgumentException("Invalid type: " + type);
		else if(errors == null)
			throw new IllegalArgumentException("Invalid errors: null");
		else { 
			this.type = type; this.errors = errors;
			this.operands = new LinkedList<Object>(); 
			this.error_level = this.generate_level();
		}
	}
	/**
	 * generate the level of the state error:<br>
	 * 	set_bool | set_numb | xor_numb | set_addr	==>	4<br>
	 * 	neg_numb | rsv_numb | inc_numb | dec_numb	==> 3<br>
	 * 	set_addr | dif_addr							==> 3<br>
	 * 	chg_bool | chg_numb | chg_addr				==> 2<br>
	 * 	mut_expr | mut_refer						==> 1<br>
	 * 	execute | not_execute						==>	0<br>
	 * @throws IllegalArgumentException
	 */
	private int generate_level() throws IllegalArgumentException {
		switch(this.type) {
		case failure:		return 0;
		case syntax_error:	return 0;
		case execute_for:	return 1;
		case execute:		return 0;
		case not_execute:	return 0;
		case set_bool:		return 4;
		case chg_bool:		return 2;
		case set_numb:		return 4;
		case neg_numb:		return 3;
		case xor_numb:		return 4;
		case rsv_numb:		return 3;
		case dif_numb:		return 4;
		case inc_numb:		return 3;
		case dec_numb:		return 3;
		case chg_numb:		return 2;
		case dif_addr:		return 4;
		case set_addr:		return 4;
		case chg_addr:		return 2;
		case mut_expr:		return 1;
		case mut_refer:		return 1;
		default: throw new IllegalArgumentException("Unsupport " + this.type);
		}
	}
	
	/* getters */
	/**
	 * get the errors from which the error is created
	 * @return
	 */
	public StateErrors get_errors() { return this.errors; }
	/**
	 * get the error type
	 * @return
	 */
	public ErrorType get_type() { return this.type; }
	/**
	 * get the level of the state errors
	 * @return
	 */
	public int get_error_level() { return this.error_level; }
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
