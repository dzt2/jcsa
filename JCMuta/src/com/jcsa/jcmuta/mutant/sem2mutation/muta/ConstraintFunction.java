package com.jcsa.jcmuta.mutant.sem2mutation.muta;

public enum ConstraintFunction {
	
	/** cover(statement) **/			cover,
	/** cover_for(condition, int) **/	cover_for,
	/** equal_with(expr, expr) **/		equal_with,
	/** not_equals(expr, expr) **/		not_equals,
	/** smaller_tn(expr, expr) **/		smaller_tn,
	/** in_range(expr, string) **/		in_range,
	/** not_in_range(expr, string) **/	not_in_range,
	
	/** x & y != 0 **/					bit_intersect,
	/** x & y == 0 **/					bit_excluding,
	/** x & y == y **/					bit_subsuming,
	/** x & y != y **/					not_subsuming,
	/** x == -y **/						is_negative,
	/** x == k * y **/					is_multiply,
	
	/** all_possible **/			all_possible,
	/** impossible() **/			impossible,
}
