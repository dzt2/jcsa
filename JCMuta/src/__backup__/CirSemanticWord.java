package __backup__;

/**
 * The keyword used to describe the semantic properties and their relationships
 * based on the C-like intermediate representation for program under analysis.<br>
 * <br>
 * The keywords can be classified into three categories based on the functions:<br>
 * (1) <b>execution-related</b>: <code>[cover, repeat, execute, non_execute]</code>.<br>
 * (2) <b>constrain-related</b>: <code>[equal_with, not_equals, greater_tn,
 * 									greater_eq, smaller_tn, smaller_eq]</code>.<br> 
 * (3) <b>influence-related</b>: <code>[traping, inc_val, neg_val, set_val, chg_val]</code>.
 * <br>
 * @author yukimula
 *
 */
public enum CirSemanticWord { 
	
	/* execution-related */
	/** #cover(statement) **/				cover,
	/** #repeat(statement, int) **/			repeat,
	/** $execute(statement) **/				execute,
	/** $execute(statement, null) **/		non_execute,
	
	/* constrain-related */
	/** #equal_with(expression, value) **/	equal_with,
	/** #not_equals(expression, value) **/	not_equals,
	/** #smaller_tn(expression, value) **/	smaller_tn,
	/** #smaller_eq(expression, value) **/	smaller_eq,
	/** #greater_tn(expression, value) **/	greater_tn,
	/** #greater_eq(expression, value) **/	greater_eq,
	/** #in_range(expression, value) **/	in_range,
	/** #not_in_range(expr, value) **/		not_in_range,
	
	/* influence-related */
	/** traping(statement) **/				traping,
	/** inc_val(expression, int) **/		inc_val,
	/** neg_val(expression, operator) **/	neg_val,
	/** set_val(expression, Object) **/		set_val,
	/** chg_val(expression) **/				chg_val,
	
	/* comparation-based */
	/** x & y != 0 **/						bit_intersc,
	/** x & y == 0 **/						bit_exclude,
	/** x & y == y **/						bit_include,
	/** x & y != y **/						bno_include,
	/** x = k * y **/						is_multiply,
	/** x = -y **/							is_negative,
	/** x != -y **/							not_negative,
}
