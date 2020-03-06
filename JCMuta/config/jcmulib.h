#include <stdbool.h>

/* data type definition */
typedef bool jcm_bool;                  /** _Bool **/
typedef long long jcm_int;              /** char, short, int, long, long long, X* **/
typedef unsigned long long jcm_uint;    /** unsigned char, short, int, long, long long **/
typedef long double jcm_float;          /** float, double, long double **/

/* constant declarations */
extern const int jcm_trape_code;
extern const int jcm_error_code;

extern const jcm_bool jcm_true_constant;
extern const jcm_bool jcm_false_constant;

extern const char jcm_pos_domain;
extern const char jcm_neg_domain;
extern const char jcm_zro_domain;

extern const char jcm_arith_neg_operator;
extern const char jcm_bitws_rsv_operator;
extern const char jcm_logic_not_operator;
extern const char jcm_abs_invoc_operator;
extern const char jcm_nabs_invoc_operator;

extern const char jcm_arith_add_operator;
extern const char jcm_arith_sub_operator;
extern const char jcm_arith_mul_operator;
extern const char jcm_arith_div_operator;
extern const char jcm_arith_mod_operator;

extern const char jcm_bitws_and_operator;
extern const char jcm_bitws_ior_operator;
extern const char jcm_bitws_xor_operator;
extern const char jcm_bitws_lsh_operator;
extern const char jcm_bitws_rsh_operator;

extern const char jcm_logic_and_operator;
extern const char jcm_logic_ior_operator;

extern const char jcm_greater_tn_operator;
extern const char jcm_greater_eq_operator;
extern const char jcm_smaller_tn_operator;
extern const char jcm_smaller_eq_operator;
extern const char jcm_equal_with_operator;
extern const char jcm_not_equals_operator;

/* verification methods */
#define jcm_cast_to_bool(x) ((x) != 0)

/* trapping mutation function */
extern int jcm_traps();
extern int jcm_error();

extern jcm_bool jcm_trap_on_boolean_i(jcm_int, jcm_bool);
extern jcm_bool jcm_trap_on_boolean_u(jcm_uint, jcm_bool);
extern jcm_bool jcm_trap_on_boolean_f(jcm_float, jcm_bool);

extern jcm_int jcm_trap_on_case_i(jcm_int, jcm_int);
extern jcm_uint jcm_trap_on_case_u(jcm_uint, jcm_uint);

#define jcm_trap_on_expression(expr)    (jcm_traps(), (expr))

extern int jcm_trap_timmer;
extern void jcm_init_trap_timmer(int);
extern void jcm_incre_trap_timmer();

extern jcm_bool jcm_trap_on_true_at_b(jcm_int, int);

extern jcm_int jcm_trap_on_domain_i(jcm_int, char);
extern jcm_uint jcm_trap_on_domain_u(jcm_uint, char);
extern jcm_float jcm_trap_on_domain_f(jcm_float, char);

/* expression evaluation function */
extern jcm_int jcm_unary_operation_i(char, jcm_int);
extern jcm_uint jcm_unary_operation_u(char, jcm_uint);
extern jcm_float jcm_unary_operation_f(char, jcm_float);

extern jcm_int jcm_arith_operation_i(char, jcm_int, jcm_int);
extern jcm_int jcm_bitws_operation_i(char, jcm_int, jcm_int);
extern jcm_bool jcm_logic_operation_i(char, jcm_int, jcm_int);
extern jcm_bool jcm_relation_operation_i(char, jcm_int, jcm_int);

extern jcm_uint jcm_arith_operation_u(char, jcm_uint, jcm_uint);
extern jcm_uint jcm_bitws_operation_u(char, jcm_uint, jcm_uint);
extern jcm_bool jcm_logic_operation_u(char, jcm_uint, jcm_uint);
extern jcm_bool jcm_relation_operation_u(char, jcm_uint, jcm_uint);

extern jcm_float jcm_arith_operation_f(char, jcm_float, jcm_float);
extern jcm_bool jcm_logic_operation_f(char, jcm_float, jcm_float);
extern jcm_bool jcm_relation_operation_f(char, jcm_float, jcm_float);

extern jcm_int jcm_abs_operation_i(jcm_int);
extern jcm_float jcm_abs_operation_f(jcm_float);
extern jcm_int jcm_nabs_operation_i(jcm_int);
extern jcm_float jcm_nabs_operation_f(jcm_float);

/* statement mutation operators */
extern jcm_int jcm_delete_arith_operand_i(char, jcm_int, jcm_int, jcm_bool);
extern jcm_int jcm_delete_bitws_operand_i(char, jcm_int, jcm_int, jcm_bool);
extern jcm_bool jcm_delete_logic_operand_i(char, jcm_int, jcm_int, jcm_bool);
extern jcm_bool jcm_delete_relation_operand_i(char, jcm_int, jcm_int, jcm_bool);

extern jcm_uint jcm_delete_arith_operand_u(char, jcm_uint, jcm_uint, jcm_bool);
extern jcm_uint jcm_delete_bitws_operand_u(char, jcm_uint, jcm_uint, jcm_bool);
extern jcm_bool jcm_delete_logic_operand_u(char, jcm_uint, jcm_uint, jcm_bool);
extern jcm_bool jcm_delete_relation_operand_u(char, jcm_uint, jcm_uint, jcm_bool);

extern jcm_float jcm_delete_arith_operand_f(char, jcm_float, jcm_float, jcm_bool);
extern jcm_bool jcm_delete_logic_operand_f(char, jcm_float, jcm_float, jcm_bool);
extern jcm_bool jcm_delete_relation_operand_f(char, jcm_float, jcm_float, jcm_bool);

extern jcm_int jcm_insert_unary_operator_i(char, jcm_int);
extern jcm_uint jcm_insert_unary_operator_u(char, jcm_uint);
extern jcm_float jcm_insert_unary_operator_f(char, jcm_float);

extern jcm_int jcm_delete_unary_operator_i(char, jcm_int);
extern jcm_uint jcm_delete_unary_operator_u(char, jcm_uint);
extern jcm_float jcm_delete_unary_operator_f(char, jcm_float);

/* operator mutations */
extern jcm_int  jcm_arith_mutate_operator_i(char, jcm_int, jcm_int, char);
extern jcm_int  jcm_bitws_mutate_operator_i(char, jcm_int, jcm_int, char);
extern jcm_bool jcm_logic_mutate_operator_i(char, jcm_int, jcm_int, char);
extern jcm_bool jcm_relation_mutate_operator_i(char, jcm_int, jcm_int, char);

extern jcm_uint jcm_arith_mutate_operator_u(char, jcm_uint, jcm_uint, char);
extern jcm_uint jcm_bitws_mutate_operator_u(char, jcm_uint, jcm_uint, char);
extern jcm_bool jcm_logic_mutate_operator_u(char, jcm_uint, jcm_uint, char);
extern jcm_bool jcm_relation_mutate_operator_u(char, jcm_uint, jcm_uint, char);

extern jcm_float jcm_arith_mutate_operator_f(char, jcm_float, jcm_float, char);
extern jcm_bool jcm_logic_mutate_operator_f(char, jcm_float, jcm_float, char);
extern jcm_bool jcm_relation_mutate_operator_f(char, jcm_float, jcm_float, char);

/* reference mutation */
#define jcm_trap_if_different(x, y)    (((x) != (y))? jcm_traps() : (x))

/* real comparator */
extern const jcm_float jcm_real_minimum;
extern jcm_bool jcm_real_equal_with(jcm_float, jcm_float);
extern jcm_bool jcm_real_not_equals(jcm_float, jcm_float);
extern jcm_bool jcm_real_equal_with_weak(jcm_float, jcm_float);
extern jcm_bool jcm_real_not_equals_weak(jcm_float, jcm_float);
