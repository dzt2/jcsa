extern int printf (const char * __format, ...);
extern void exit (int __status) ;

/** trapping-function **/
static int jcm_trap()
{
	printf("\n\nException occurs in testing.\n");
	exit(1);
	return -1;
}


/* trapping-mutation-class */
#define jcm_trap_on_true(e)		{ typeof(e) rs = (e); if(e) jcm_trap(); rs; }
#define jcm_trap_on_false(e)	{ typeof(e) rs = (e); if(!e) jcm_trap(); rs; }
#define jcm_trap_on_case(e, c)	{ typeof(e) rs = (e); if(rs == (c)) jcm_trap(); rs; }
#define jcm_trap_on_expr(e)		(jcm_trap(), (e))
static int jcm_loop_counter;
static void jcm_init_loop_counter(int counter)
{
	jcm_loop_counter = counter;
}
static void jcm_decre_loop_counter()
{
	jcm_loop_counter--;
	if(jcm_loop_counter <= 0)
		jcm_trap();
}
#define jcm_trap_on_pos(e)		{ typeof(e) rs = (e); if(rs > 0) jcm_trap(); rs; }
#define jcm_trap_on_neg(e)		{ typeof(e) rs = (e); if(rs < 0) jcm_trap(); rs; }
#define jcm_trap_on_zro(e)		{ typeof(e) rs = (e); if(rs == 0) jcm_trap(); rs; }
#define jcm_trap_on_dif(x, y)	{ typeof(x) rs = (x); if(rs != (y)) jcm_trap(); rs; }

/* unary-mutation-class */
#define jcm_inc_constant(e, c)	((e) + (c))
#define jcm_mul_constant(e, m)	((e) * (m))
#define jcm_insert_arith_neg(e)	(-(e))
#define jcm_insert_bitws_rsv(e)	(~(e))
#define jcm_insert_logic_not(e)	(!(e))
#define jcm_insert_abs_value(e)	{ typeof(e) rs = (e); if(rs < 0) rs = -rs; rs; }
#define jcm_insert_nabs_value(e)	{typeof(e) rs = (e); if(rs > 0) rs = -rs; rs; }

/* reference-mutation-class */
#define jcm_set_expression(x, y)	(y)

/* operator-mutation-class */
// OAXN
#define jcm_arith_add_arith_add(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l + r) != (l + r))) jcm_trap();  l + r; }
#define jcm_arith_add_arith_sub(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l + r) != (l - r))) jcm_trap();  l - r; }
#define jcm_arith_add_arith_mul(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l + r) != (l * r))) jcm_trap();  l * r; }
#define jcm_arith_add_arith_div(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l + r) != (l / r))) jcm_trap();  l / r; }
#define jcm_arith_add_arith_mod(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l + r) != (l % r))) jcm_trap();  l % r; }
#define jcm_arith_add_bit_and(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l + r) != (l & r))) jcm_trap();  l & r; }
#define jcm_arith_add_bit_or(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l + r) != (l | r))) jcm_trap();  l | r; }
#define jcm_arith_add_bit_xor(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l + r) != (l ^ r))) jcm_trap();  l ^ r; }
#define jcm_arith_add_left_shift(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l + r) != (l << r))) jcm_trap(); l << r; }
#define jcm_arith_add_righ_shift(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l + r) != (l >> r))) jcm_trap(); l >> r; }
#define jcm_arith_add_logic_and(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l + r) != (l && r))) jcm_trap(); l && r; }
#define jcm_arith_add_logic_or(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l + r) != (l || r))) jcm_trap(); l || r; }
#define jcm_arith_add_greater_tn(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l + r) != (l > r))) jcm_trap();  l > r; }
#define jcm_arith_add_greater_eq(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l + r) != (l >= r))) jcm_trap(); l >= r; }
#define jcm_arith_add_smaller_tn(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l + r) != (l < r))) jcm_trap();  l < r; }
#define jcm_arith_add_smaller_eq(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l + r) != (l <= r))) jcm_trap(); l <= r; }
#define jcm_arith_add_equal_with(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l + r) != (l == r))) jcm_trap(); l == r; }
#define jcm_arith_add_not_equals(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l + r) != (l != r))) jcm_trap(); l != r; }

#define jcm_arith_sub_arith_add(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l - r) != (l + r))) jcm_trap();  l + r; }
#define jcm_arith_sub_arith_sub(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l - r) != (l - r))) jcm_trap();  l - r; }
#define jcm_arith_sub_arith_mul(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l - r) != (l * r))) jcm_trap();  l * r; }
#define jcm_arith_sub_arith_div(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l - r) != (l / r))) jcm_trap();  l / r; }
#define jcm_arith_sub_arith_mod(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l - r) != (l % r))) jcm_trap();  l % r; }
#define jcm_arith_sub_bit_and(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l - r) != (l & r))) jcm_trap();  l & r; }
#define jcm_arith_sub_bit_or(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l - r) != (l | r))) jcm_trap();  l | r; }
#define jcm_arith_sub_bit_xor(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l - r) != (l ^ r))) jcm_trap();  l ^ r; }
#define jcm_arith_sub_left_shift(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l - r) != (l << r))) jcm_trap(); l << r; }
#define jcm_arith_sub_righ_shift(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l - r) != (l >> r))) jcm_trap(); l >> r; }
#define jcm_arith_sub_logic_and(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l - r) != (l && r))) jcm_trap(); l && r; }
#define jcm_arith_sub_logic_or(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l - r) != (l || r))) jcm_trap(); l || r; }
#define jcm_arith_sub_greater_tn(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l - r) != (l > r))) jcm_trap();  l > r; }
#define jcm_arith_sub_greater_eq(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l - r) != (l >= r))) jcm_trap(); l >= r; }
#define jcm_arith_sub_smaller_tn(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l - r) != (l < r))) jcm_trap();  l < r; }
#define jcm_arith_sub_smaller_eq(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l - r) != (l <= r))) jcm_trap(); l <= r; }
#define jcm_arith_sub_equal_with(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l - r) != (l == r))) jcm_trap(); l == r; }
#define jcm_arith_sub_not_equals(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l - r) != (l != r))) jcm_trap(); l != r; }

#define jcm_arith_mul_arith_add(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l * r) != (l + r))) jcm_trap();  l + r; }
#define jcm_arith_mul_arith_sub(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l * r) != (l - r))) jcm_trap();  l - r; }
#define jcm_arith_mul_arith_mul(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l * r) != (l * r))) jcm_trap();  l * r; }
#define jcm_arith_mul_arith_div(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l * r) != (l / r))) jcm_trap();  l / r; }
#define jcm_arith_mul_arith_mod(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l * r) != (l % r))) jcm_trap();  l % r; }
#define jcm_arith_mul_bit_and(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l * r) != (l & r))) jcm_trap();  l & r; }
#define jcm_arith_mul_bit_or(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l * r) != (l | r))) jcm_trap();  l | r; }
#define jcm_arith_mul_bit_xor(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l * r) != (l ^ r))) jcm_trap();  l ^ r; }
#define jcm_arith_mul_left_shift(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l * r) != (l << r))) jcm_trap(); l << r; }
#define jcm_arith_mul_righ_shift(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l * r) != (l >> r))) jcm_trap(); l >> r; }
#define jcm_arith_mul_logic_and(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l * r) != (l && r))) jcm_trap(); l && r; }
#define jcm_arith_mul_logic_or(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l * r) != (l || r))) jcm_trap(); l || r; }
#define jcm_arith_mul_greater_tn(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l * r) != (l > r))) jcm_trap();  l > r; }
#define jcm_arith_mul_greater_eq(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l * r) != (l >= r))) jcm_trap(); l >= r; }
#define jcm_arith_mul_smaller_tn(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l * r) != (l < r))) jcm_trap();  l < r; }
#define jcm_arith_mul_smaller_eq(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l * r) != (l <= r))) jcm_trap(); l <= r; }
#define jcm_arith_mul_equal_with(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l * r) != (l == r))) jcm_trap(); l == r; }
#define jcm_arith_mul_not_equals(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l * r) != (l != r))) jcm_trap(); l != r; }

#define jcm_arith_div_arith_add(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l / r) != (l + r))) jcm_trap();  l + r; }
#define jcm_arith_div_arith_sub(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l / r) != (l - r))) jcm_trap();  l - r; }
#define jcm_arith_div_arith_mul(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l / r) != (l * r))) jcm_trap();  l * r; }
#define jcm_arith_div_arith_div(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l / r) != (l / r))) jcm_trap();  l / r; }
#define jcm_arith_div_arith_mod(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l / r) != (l % r))) jcm_trap();  l % r; }
#define jcm_arith_div_bit_and(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l / r) != (l & r))) jcm_trap();  l & r; }
#define jcm_arith_div_bit_or(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l / r) != (l | r))) jcm_trap();  l | r; }
#define jcm_arith_div_bit_xor(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l / r) != (l ^ r))) jcm_trap();  l ^ r; }
#define jcm_arith_div_left_shift(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l / r) != (l << r))) jcm_trap(); l << r; }
#define jcm_arith_div_righ_shift(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l / r) != (l >> r))) jcm_trap(); l >> r; }
#define jcm_arith_div_logic_and(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l / r) != (l && r))) jcm_trap(); l && r; }
#define jcm_arith_div_logic_or(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l / r) != (l || r))) jcm_trap(); l || r; }
#define jcm_arith_div_greater_tn(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l / r) != (l > r))) jcm_trap();  l > r; }
#define jcm_arith_div_greater_eq(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l / r) != (l >= r))) jcm_trap(); l >= r; }
#define jcm_arith_div_smaller_tn(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l / r) != (l < r))) jcm_trap();  l < r; }
#define jcm_arith_div_smaller_eq(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l / r) != (l <= r))) jcm_trap(); l <= r; }
#define jcm_arith_div_equal_with(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l / r) != (l == r))) jcm_trap(); l == r; }
#define jcm_arith_div_not_equals(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l / r) != (l != r))) jcm_trap(); l != r; }

#define jcm_arith_mod_arith_add(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l % r) != (l + r))) jcm_trap();  l + r; }
#define jcm_arith_mod_arith_sub(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l % r) != (l - r))) jcm_trap();  l - r; }
#define jcm_arith_mod_arith_mul(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l % r) != (l * r))) jcm_trap();  l * r; }
#define jcm_arith_mod_arith_div(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l % r) != (l / r))) jcm_trap();  l / r; }
#define jcm_arith_mod_arith_mod(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l % r) != (l % r))) jcm_trap();  l % r; }
#define jcm_arith_mod_bit_and(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l % r) != (l & r))) jcm_trap();  l & r; }
#define jcm_arith_mod_bit_or(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l % r) != (l | r))) jcm_trap();  l | r; }
#define jcm_arith_mod_bit_xor(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l % r) != (l ^ r))) jcm_trap();  l ^ r; }
#define jcm_arith_mod_left_shift(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l % r) != (l << r))) jcm_trap(); l << r; }
#define jcm_arith_mod_righ_shift(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l % r) != (l >> r))) jcm_trap(); l >> r; }
#define jcm_arith_mod_logic_and(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l % r) != (l && r))) jcm_trap(); l && r; }
#define jcm_arith_mod_logic_or(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l % r) != (l || r))) jcm_trap(); l || r; }
#define jcm_arith_mod_greater_tn(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l % r) != (l > r))) jcm_trap();  l > r; }
#define jcm_arith_mod_greater_eq(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l % r) != (l >= r))) jcm_trap(); l >= r; }
#define jcm_arith_mod_smaller_tn(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l % r) != (l < r))) jcm_trap();  l < r; }
#define jcm_arith_mod_smaller_eq(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l % r) != (l <= r))) jcm_trap(); l <= r; }
#define jcm_arith_mod_equal_with(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l % r) != (l == r))) jcm_trap(); l == r; }
#define jcm_arith_mod_not_equals(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l % r) != (l != r))) jcm_trap(); l != r; }

// OBXN
#define jcm_bit_and_arith_add(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l & r) != (l + r))) jcm_trap();  l + r; }
#define jcm_bit_and_arith_sub(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l & r) != (l - r))) jcm_trap();  l - r; }
#define jcm_bit_and_arith_mul(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l & r) != (l * r))) jcm_trap();  l * r; }
#define jcm_bit_and_arith_div(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l & r) != (l / r))) jcm_trap();  l / r; }
#define jcm_bit_and_arith_mod(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l & r) != (l % r))) jcm_trap();  l % r; }
#define jcm_bit_and_bit_and(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l & r) != (l & r))) jcm_trap();  l & r; }
#define jcm_bit_and_bit_or(w, x, y)			{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l & r) != (l | r))) jcm_trap();  l | r; }
#define jcm_bit_and_bit_xor(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l & r) != (l ^ r))) jcm_trap();  l ^ r; }
#define jcm_bit_and_left_shift(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l & r) != (l << r))) jcm_trap(); l << r; }
#define jcm_bit_and_righ_shift(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l & r) != (l >> r))) jcm_trap(); l >> r; }
#define jcm_bit_and_logic_and(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l & r) != (l && r))) jcm_trap(); l && r; }
#define jcm_bit_and_logic_or(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l & r) != (l || r))) jcm_trap(); l || r; }
#define jcm_bit_and_greater_tn(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l & r) != (l > r))) jcm_trap();  l > r; }
#define jcm_bit_and_greater_eq(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l & r) != (l >= r))) jcm_trap(); l >= r; }
#define jcm_bit_and_smaller_tn(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l & r) != (l < r))) jcm_trap();  l < r; }
#define jcm_bit_and_smaller_eq(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l & r) != (l <= r))) jcm_trap(); l <= r; }
#define jcm_bit_and_equal_with(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l & r) != (l == r))) jcm_trap(); l == r; }
#define jcm_bit_and_not_equals(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l & r) != (l != r))) jcm_trap(); l != r; }

#define jcm_bit_or_arith_add(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l | r) != (l + r))) jcm_trap();  l + r; }
#define jcm_bit_or_arith_sub(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l | r) != (l - r))) jcm_trap();  l - r; }
#define jcm_bit_or_arith_mul(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l | r) != (l * r))) jcm_trap();  l * r; }
#define jcm_bit_or_arith_div(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l | r) != (l / r))) jcm_trap();  l / r; }
#define jcm_bit_or_arith_mod(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l | r) != (l % r))) jcm_trap();  l % r; }
#define jcm_bit_or_bit_and(w, x, y)			{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l | r) != (l & r))) jcm_trap();  l & r; }
#define jcm_bit_or_bit_or(w, x, y)			{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l | r) != (l | r))) jcm_trap();  l | r; }
#define jcm_bit_or_bit_xor(w, x, y)			{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l | r) != (l ^ r))) jcm_trap();  l ^ r; }
#define jcm_bit_or_left_shift(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l | r) != (l << r))) jcm_trap(); l << r; }
#define jcm_bit_or_righ_shift(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l | r) != (l >> r))) jcm_trap(); l >> r; }
#define jcm_bit_or_logic_and(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l | r) != (l && r))) jcm_trap(); l && r; }
#define jcm_bit_or_logic_or(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l | r) != (l || r))) jcm_trap(); l || r; }
#define jcm_bit_or_greater_tn(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l | r) != (l > r))) jcm_trap();  l > r; }
#define jcm_bit_or_greater_eq(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l | r) != (l >= r))) jcm_trap(); l >= r; }
#define jcm_bit_or_smaller_tn(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l | r) != (l < r))) jcm_trap();  l < r; }
#define jcm_bit_or_smaller_eq(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l | r) != (l <= r))) jcm_trap(); l <= r; }
#define jcm_bit_or_equal_with(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l | r) != (l == r))) jcm_trap(); l == r; }
#define jcm_bit_or_not_equals(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l | r) != (l != r))) jcm_trap(); l != r; }

#define jcm_bit_xor_arith_add(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l ^ r) != (l + r))) jcm_trap();  l + r; }
#define jcm_bit_xor_arith_sub(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l ^ r) != (l - r))) jcm_trap();  l - r; }
#define jcm_bit_xor_arith_mul(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l ^ r) != (l * r))) jcm_trap();  l * r; }
#define jcm_bit_xor_arith_div(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l ^ r) != (l / r))) jcm_trap();  l / r; }
#define jcm_bit_xor_arith_mod(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l ^ r) != (l % r))) jcm_trap();  l % r; }
#define jcm_bit_xor_bit_and(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l ^ r) != (l & r))) jcm_trap();  l & r; }
#define jcm_bit_xor_bit_or(w, x, y)			{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l ^ r) != (l | r))) jcm_trap();  l | r; }
#define jcm_bit_xor_bit_xor(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l ^ r) != (l ^ r))) jcm_trap();  l ^ r; }
#define jcm_bit_xor_left_shift(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l ^ r) != (l << r))) jcm_trap(); l << r; }
#define jcm_bit_xor_righ_shift(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l ^ r) != (l >> r))) jcm_trap(); l >> r; }
#define jcm_bit_xor_logic_and(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l ^ r) != (l && r))) jcm_trap(); l && r; }
#define jcm_bit_xor_logic_or(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l ^ r) != (l || r))) jcm_trap(); l || r; }
#define jcm_bit_xor_greater_tn(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l ^ r) != (l > r))) jcm_trap();  l > r; }
#define jcm_bit_xor_greater_eq(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l ^ r) != (l >= r))) jcm_trap(); l >= r; }
#define jcm_bit_xor_smaller_tn(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l ^ r) != (l < r))) jcm_trap();  l < r; }
#define jcm_bit_xor_smaller_eq(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l ^ r) != (l <= r))) jcm_trap(); l <= r; }
#define jcm_bit_xor_equal_with(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l ^ r) != (l == r))) jcm_trap(); l == r; }
#define jcm_bit_xor_not_equals(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l ^ r) != (l != r))) jcm_trap(); l != r; }

#define jcm_left_shift_arith_add(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l << r) != (l + r))) jcm_trap();  l + r; }
#define jcm_left_shift_arith_sub(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l << r) != (l - r))) jcm_trap();  l - r; }
#define jcm_left_shift_arith_mul(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l << r) != (l * r))) jcm_trap();  l * r; }
#define jcm_left_shift_arith_div(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l << r) != (l / r))) jcm_trap();  l / r; }
#define jcm_left_shift_arith_mod(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l << r) != (l % r))) jcm_trap();  l % r; }
#define jcm_left_shift_bit_and(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l << r) != (l & r))) jcm_trap();  l & r; }
#define jcm_left_shift_bit_or(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l << r) != (l | r))) jcm_trap();  l | r; }
#define jcm_left_shift_bit_xor(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l << r) != (l ^ r))) jcm_trap();  l ^ r; }
#define jcm_left_shift_left_shift(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l << r) != (l << r))) jcm_trap(); l << r; }
#define jcm_left_shift_righ_shift(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l << r) != (l >> r))) jcm_trap(); l >> r; }
#define jcm_left_shift_logic_and(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l << r) != (l && r))) jcm_trap(); l && r; }
#define jcm_left_shift_logic_or(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l << r) != (l || r))) jcm_trap(); l || r; }
#define jcm_left_shift_greater_tn(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l << r) != (l > r))) jcm_trap();  l > r; }
#define jcm_left_shift_greater_eq(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l << r) != (l >= r))) jcm_trap(); l >= r; }
#define jcm_left_shift_smaller_tn(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l << r) != (l < r))) jcm_trap();  l < r; }
#define jcm_left_shift_smaller_eq(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l << r) != (l <= r))) jcm_trap(); l <= r; }
#define jcm_left_shift_equal_with(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l << r) != (l == r))) jcm_trap(); l == r; }
#define jcm_left_shift_not_equals(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l << r) != (l != r))) jcm_trap(); l != r; }

#define jcm_righ_shift_arith_add(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >> r) != (l + r))) jcm_trap();  l + r; }
#define jcm_righ_shift_arith_sub(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >> r) != (l - r))) jcm_trap();  l - r; }
#define jcm_righ_shift_arith_mul(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >> r) != (l * r))) jcm_trap();  l * r; }
#define jcm_righ_shift_arith_div(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >> r) != (l / r))) jcm_trap();  l / r; }
#define jcm_righ_shift_arith_mod(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >> r) != (l % r))) jcm_trap();  l % r; }
#define jcm_righ_shift_bit_and(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >> r) != (l & r))) jcm_trap();  l & r; }
#define jcm_righ_shift_bit_or(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >> r) != (l | r))) jcm_trap();  l | r; }
#define jcm_righ_shift_bit_xor(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >> r) != (l ^ r))) jcm_trap();  l ^ r; }
#define jcm_righ_shift_left_shift(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >> r) != (l << r))) jcm_trap(); l << r; }
#define jcm_righ_shift_righ_shift(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >> r) != (l >> r))) jcm_trap(); l >> r; }
#define jcm_righ_shift_logic_and(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >> r) != (l && r))) jcm_trap(); l && r; }
#define jcm_righ_shift_logic_or(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >> r) != (l || r))) jcm_trap(); l || r; }
#define jcm_righ_shift_greater_tn(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >> r) != (l > r))) jcm_trap();  l > r; }
#define jcm_righ_shift_greater_eq(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >> r) != (l >= r))) jcm_trap(); l >= r; }
#define jcm_righ_shift_smaller_tn(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >> r) != (l < r))) jcm_trap();  l < r; }
#define jcm_righ_shift_smaller_eq(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >> r) != (l <= r))) jcm_trap(); l <= r; }
#define jcm_righ_shift_equal_with(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >> r) != (l == r))) jcm_trap(); l == r; }
#define jcm_righ_shift_not_equals(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >> r) != (l != r))) jcm_trap(); l != r; }

// OLXN
#define jcm_logic_and_arith_add(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l && r) != (l + r))) jcm_trap();  l + r; }
#define jcm_logic_and_arith_sub(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l && r) != (l - r))) jcm_trap();  l - r; }
#define jcm_logic_and_arith_mul(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l && r) != (l * r))) jcm_trap();  l * r; }
#define jcm_logic_and_arith_div(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l && r) != (l / r))) jcm_trap();  l / r; }
#define jcm_logic_and_arith_mod(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l && r) != (l % r))) jcm_trap();  l % r; }
#define jcm_logic_and_bit_and(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l && r) != (l & r))) jcm_trap();  l & r; }
#define jcm_logic_and_bit_or(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l && r) != (l | r))) jcm_trap();  l | r; }
#define jcm_logic_and_bit_xor(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l && r) != (l ^ r))) jcm_trap();  l ^ r; }
#define jcm_logic_and_left_shift(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l && r) != (l << r))) jcm_trap(); l << r; }
#define jcm_logic_and_righ_shift(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l && r) != (l >> r))) jcm_trap(); l >> r; }
#define jcm_logic_and_logic_and(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l && r) != (l && r))) jcm_trap(); l && r; }
#define jcm_logic_and_logic_or(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l && r) != (l || r))) jcm_trap(); l || r; }
#define jcm_logic_and_greater_tn(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l && r) != (l > r))) jcm_trap();  l > r; }
#define jcm_logic_and_greater_eq(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l && r) != (l >= r))) jcm_trap(); l >= r; }
#define jcm_logic_and_smaller_tn(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l && r) != (l < r))) jcm_trap();  l < r; }
#define jcm_logic_and_smaller_eq(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l && r) != (l <= r))) jcm_trap(); l <= r; }
#define jcm_logic_and_equal_with(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l && r) != (l == r))) jcm_trap(); l == r; }
#define jcm_logic_and_not_equals(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l && r) != (l != r))) jcm_trap(); l != r; }

#define jcm_logic_or_arith_add(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l || r) != (l + r))) jcm_trap();  l + r; }
#define jcm_logic_or_arith_sub(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l || r) != (l - r))) jcm_trap();  l - r; }
#define jcm_logic_or_arith_mul(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l || r) != (l * r))) jcm_trap();  l * r; }
#define jcm_logic_or_arith_div(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l || r) != (l / r))) jcm_trap();  l / r; }
#define jcm_logic_or_arith_mod(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l || r) != (l % r))) jcm_trap();  l % r; }
#define jcm_logic_or_bit_and(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l || r) != (l & r))) jcm_trap();  l & r; }
#define jcm_logic_or_bit_or(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l || r) != (l | r))) jcm_trap();  l | r; }
#define jcm_logic_or_bit_xor(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l || r) != (l ^ r))) jcm_trap();  l ^ r; }
#define jcm_logic_or_left_shift(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l || r) != (l << r))) jcm_trap(); l << r; }
#define jcm_logic_or_righ_shift(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l || r) != (l >> r))) jcm_trap(); l >> r; }
#define jcm_logic_or_logic_and(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l || r) != (l && r))) jcm_trap(); l && r; }
#define jcm_logic_or_logic_or(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l || r) != (l || r))) jcm_trap(); l || r; }
#define jcm_logic_or_greater_tn(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l || r) != (l > r))) jcm_trap();  l > r; }
#define jcm_logic_or_greater_eq(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l || r) != (l >= r))) jcm_trap(); l >= r; }
#define jcm_logic_or_smaller_tn(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l || r) != (l < r))) jcm_trap();  l < r; }
#define jcm_logic_or_smaller_eq(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l || r) != (l <= r))) jcm_trap(); l <= r; }
#define jcm_logic_or_equal_with(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l || r) != (l == r))) jcm_trap(); l == r; }
#define jcm_logic_or_not_equals(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l || r) != (l != r))) jcm_trap(); l != r; }

// ORXN
#define jcm_greater_tn_arith_add(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l > r) != (l + r))) jcm_trap();  l + r; }
#define jcm_greater_tn_arith_sub(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l > r) != (l - r))) jcm_trap();  l - r; }
#define jcm_greater_tn_arith_mul(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l > r) != (l * r))) jcm_trap();  l * r; }
#define jcm_greater_tn_arith_div(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l > r) != (l / r))) jcm_trap();  l / r; }
#define jcm_greater_tn_arith_mod(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l > r) != (l % r))) jcm_trap();  l % r; }
#define jcm_greater_tn_bit_and(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l > r) != (l & r))) jcm_trap();  l & r; }
#define jcm_greater_tn_bit_or(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l > r) != (l | r))) jcm_trap();  l | r; }
#define jcm_greater_tn_bit_xor(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l > r) != (l ^ r))) jcm_trap();  l ^ r; }
#define jcm_greater_tn_left_shift(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l > r) != (l << r))) jcm_trap(); l << r; }
#define jcm_greater_tn_righ_shift(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l > r) != (l >> r))) jcm_trap(); l >> r; }
#define jcm_greater_tn_logic_and(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l > r) != (l && r))) jcm_trap(); l && r; }
#define jcm_greater_tn_logic_or(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l > r) != (l || r))) jcm_trap(); l || r; }
#define jcm_greater_tn_greater_tn(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l > r) != (l > r))) jcm_trap();  l > r; }
#define jcm_greater_tn_greater_eq(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l > r) != (l >= r))) jcm_trap(); l >= r; }
#define jcm_greater_tn_smaller_tn(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l > r) != (l < r))) jcm_trap();  l < r; }
#define jcm_greater_tn_smaller_eq(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l > r) != (l <= r))) jcm_trap(); l <= r; }
#define jcm_greater_tn_equal_with(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l > r) != (l == r))) jcm_trap(); l == r; }
#define jcm_greater_tn_not_equals(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l > r) != (l != r))) jcm_trap(); l != r; }

#define jcm_greater_eq_arith_add(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >= r) != (l + r))) jcm_trap();  l + r; }
#define jcm_greater_eq_arith_sub(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >= r) != (l - r))) jcm_trap();  l - r; }
#define jcm_greater_eq_arith_mul(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >= r) != (l * r))) jcm_trap();  l * r; }
#define jcm_greater_eq_arith_div(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >= r) != (l / r))) jcm_trap();  l / r; }
#define jcm_greater_eq_arith_mod(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >= r) != (l % r))) jcm_trap();  l % r; }
#define jcm_greater_eq_bit_and(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >= r) != (l & r))) jcm_trap();  l & r; }
#define jcm_greater_eq_bit_or(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >= r) != (l | r))) jcm_trap();  l | r; }
#define jcm_greater_eq_bit_xor(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >= r) != (l ^ r))) jcm_trap();  l ^ r; }
#define jcm_greater_eq_left_shift(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >= r) != (l << r))) jcm_trap(); l << r; }
#define jcm_greater_eq_righ_shift(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >= r) != (l >> r))) jcm_trap(); l >> r; }
#define jcm_greater_eq_logic_and(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >= r) != (l && r))) jcm_trap(); l && r; }
#define jcm_greater_eq_logic_or(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >= r) != (l || r))) jcm_trap(); l || r; }
#define jcm_greater_eq_greater_tn(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >= r) != (l > r))) jcm_trap();  l > r; }
#define jcm_greater_eq_greater_eq(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >= r) != (l >= r))) jcm_trap(); l >= r; }
#define jcm_greater_eq_smaller_tn(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >= r) != (l < r))) jcm_trap();  l < r; }
#define jcm_greater_eq_smaller_eq(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >= r) != (l <= r))) jcm_trap(); l <= r; }
#define jcm_greater_eq_equal_with(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >= r) != (l == r))) jcm_trap(); l == r; }
#define jcm_greater_eq_not_equals(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l >= r) != (l != r))) jcm_trap(); l != r; }

#define jcm_smaller_tn_arith_add(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l < r) != (l + r))) jcm_trap();  l + r; }
#define jcm_smaller_tn_arith_sub(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l < r) != (l - r))) jcm_trap();  l - r; }
#define jcm_smaller_tn_arith_mul(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l < r) != (l * r))) jcm_trap();  l * r; }
#define jcm_smaller_tn_arith_div(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l < r) != (l / r))) jcm_trap();  l / r; }
#define jcm_smaller_tn_arith_mod(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l < r) != (l % r))) jcm_trap();  l % r; }
#define jcm_smaller_tn_bit_and(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l < r) != (l & r))) jcm_trap();  l & r; }
#define jcm_smaller_tn_bit_or(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l < r) != (l | r))) jcm_trap();  l | r; }
#define jcm_smaller_tn_bit_xor(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l < r) != (l ^ r))) jcm_trap();  l ^ r; }
#define jcm_smaller_tn_left_shift(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l < r) != (l << r))) jcm_trap(); l << r; }
#define jcm_smaller_tn_righ_shift(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l < r) != (l >> r))) jcm_trap(); l >> r; }
#define jcm_smaller_tn_logic_and(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l < r) != (l && r))) jcm_trap(); l && r; }
#define jcm_smaller_tn_logic_or(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l < r) != (l || r))) jcm_trap(); l || r; }
#define jcm_smaller_tn_greater_tn(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l < r) != (l > r))) jcm_trap();  l > r; }
#define jcm_smaller_tn_greater_eq(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l < r) != (l >= r))) jcm_trap(); l >= r; }
#define jcm_smaller_tn_smaller_tn(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l < r) != (l < r))) jcm_trap();  l < r; }
#define jcm_smaller_tn_smaller_eq(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l < r) != (l <= r))) jcm_trap(); l <= r; }
#define jcm_smaller_tn_equal_with(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l < r) != (l == r))) jcm_trap(); l == r; }
#define jcm_smaller_tn_not_equals(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l < r) != (l != r))) jcm_trap(); l != r; }

#define jcm_smaller_eq_arith_add(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l <= r) != (l + r))) jcm_trap();  l + r; }
#define jcm_smaller_eq_arith_sub(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l <= r) != (l - r))) jcm_trap();  l - r; }
#define jcm_smaller_eq_arith_mul(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l <= r) != (l * r))) jcm_trap();  l * r; }
#define jcm_smaller_eq_arith_div(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l <= r) != (l / r))) jcm_trap();  l / r; }
#define jcm_smaller_eq_arith_mod(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l <= r) != (l % r))) jcm_trap();  l % r; }
#define jcm_smaller_eq_bit_and(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l <= r) != (l & r))) jcm_trap();  l & r; }
#define jcm_smaller_eq_bit_or(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l <= r) != (l | r))) jcm_trap();  l | r; }
#define jcm_smaller_eq_bit_xor(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l <= r) != (l ^ r))) jcm_trap();  l ^ r; }
#define jcm_smaller_eq_left_shift(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l <= r) != (l << r))) jcm_trap(); l << r; }
#define jcm_smaller_eq_righ_shift(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l <= r) != (l >> r))) jcm_trap(); l >> r; }
#define jcm_smaller_eq_logic_and(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l <= r) != (l && r))) jcm_trap(); l && r; }
#define jcm_smaller_eq_logic_or(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l <= r) != (l || r))) jcm_trap(); l || r; }
#define jcm_smaller_eq_greater_tn(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l <= r) != (l > r))) jcm_trap();  l > r; }
#define jcm_smaller_eq_greater_eq(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l <= r) != (l >= r))) jcm_trap(); l >= r; }
#define jcm_smaller_eq_smaller_tn(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l <= r) != (l < r))) jcm_trap();  l < r; }
#define jcm_smaller_eq_smaller_eq(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l <= r) != (l <= r))) jcm_trap(); l <= r; }
#define jcm_smaller_eq_equal_with(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l <= r) != (l == r))) jcm_trap(); l == r; }
#define jcm_smaller_eq_not_equals(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l <= r) != (l != r))) jcm_trap(); l != r; }

#define jcm_equal_with_arith_add(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l + r))) jcm_trap();  l + r; }
#define jcm_equal_with_arith_sub(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l - r))) jcm_trap();  l - r; }
#define jcm_equal_with_arith_mul(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l * r))) jcm_trap();  l * r; }
#define jcm_equal_with_arith_div(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l / r))) jcm_trap();  l / r; }
#define jcm_equal_with_arith_mod(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l % r))) jcm_trap();  l % r; }
#define jcm_equal_with_bit_and(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l & r))) jcm_trap();  l & r; }
#define jcm_equal_with_bit_or(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l | r))) jcm_trap();  l | r; }
#define jcm_equal_with_bit_xor(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l ^ r))) jcm_trap();  l ^ r; }
#define jcm_equal_with_left_shift(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l << r))) jcm_trap(); l << r; }
#define jcm_equal_with_righ_shift(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l >> r))) jcm_trap(); l >> r; }
#define jcm_equal_with_logic_and(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l && r))) jcm_trap(); l && r; }
#define jcm_equal_with_logic_or(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l || r))) jcm_trap(); l || r; }
#define jcm_equal_with_greater_tn(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l > r))) jcm_trap();  l > r; }
#define jcm_equal_with_greater_eq(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l >= r))) jcm_trap(); l >= r; }
#define jcm_equal_with_smaller_tn(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l < r))) jcm_trap();  l < r; }
#define jcm_equal_with_smaller_eq(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l <= r))) jcm_trap(); l <= r; }
#define jcm_equal_with_equal_with(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l == r))) jcm_trap(); l == r; }
#define jcm_equal_with_not_equals(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l != r))) jcm_trap(); l != r; }

#define jcm_not_equals_arith_add(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l + r))) jcm_trap();  l + r; }
#define jcm_not_equals_arith_sub(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l - r))) jcm_trap();  l - r; }
#define jcm_not_equals_arith_mul(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l * r))) jcm_trap();  l * r; }
#define jcm_not_equals_arith_div(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l / r))) jcm_trap();  l / r; }
#define jcm_not_equals_arith_mod(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l % r))) jcm_trap();  l % r; }
#define jcm_not_equals_bit_and(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l & r))) jcm_trap();  l & r; }
#define jcm_not_equals_bit_or(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l | r))) jcm_trap();  l | r; }
#define jcm_not_equals_bit_xor(w, x, y)		{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l ^ r))) jcm_trap();  l ^ r; }
#define jcm_not_equals_left_shift(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l << r))) jcm_trap(); l << r; }
#define jcm_not_equals_righ_shift(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l >> r))) jcm_trap(); l >> r; }
#define jcm_not_equals_logic_and(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l && r))) jcm_trap(); l && r; }
#define jcm_not_equals_logic_or(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l || r))) jcm_trap(); l || r; }
#define jcm_not_equals_greater_tn(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l > r))) jcm_trap();  l > r; }
#define jcm_not_equals_greater_eq(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l >= r))) jcm_trap(); l >= r; }
#define jcm_not_equals_smaller_tn(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l < r))) jcm_trap();  l < r; }
#define jcm_not_equals_smaller_eq(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l <= r))) jcm_trap(); l <= r; }
#define jcm_not_equals_equal_with(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l == r))) jcm_trap(); l == r; }
#define jcm_not_equals_not_equals(w, x, y)	{ typeof(x) l = (x); typeof(y) r = (y); if((w) && ((l == r) != (l != r))) jcm_trap(); l != r; }


