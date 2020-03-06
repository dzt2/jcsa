#include "jcmulib.h"
#include <stdlib.h>

const int jcm_trape_code = -74291;
const int jcm_error_code = -15377;
const jcm_bool jcm_true_constant = 1;
const jcm_bool jcm_false_constant = 0;

const char jcm_pos_domain = 1;
const char jcm_neg_domain = 2;
const char jcm_zro_domain = 3;

const char jcm_arith_neg_operator = 4;
const char jcm_bitws_rsv_operator = 5;
const char jcm_logic_not_operator = 6;
const char jcm_abs_invoc_operator = 7;
const char jcm_nabs_invoc_operator= 8;

const char jcm_arith_add_operator = 9;
const char jcm_arith_sub_operator = 10;
const char jcm_arith_mul_operator = 11;
const char jcm_arith_div_operator = 12;
const char jcm_arith_mod_operator = 13;
#define jcm_is_arith_operator(x)    ((x >= 9) && (x <= 13))

const char jcm_bitws_and_operator = 14;
const char jcm_bitws_ior_operator = 15;
const char jcm_bitws_xor_operator = 16;
const char jcm_bitws_lsh_operator = 17;
const char jcm_bitws_rsh_operator = 18;
#define jcm_is_bitws_operator(x)    ((x >= 14) && (x <= 18))

const char jcm_logic_and_operator = 19;
const char jcm_logic_ior_operator = 20;
#define jcm_is_logic_operator(x)    ((x >= 19) && (x <= 20))

const char jcm_greater_tn_operator = 21;
const char jcm_greater_eq_operator = 22;
const char jcm_smaller_tn_operator = 23;
const char jcm_smaller_eq_operator = 24;
const char jcm_equal_with_operator = 25;
const char jcm_not_equals_operator = 26;
#define jcm_is_relation_operator(x) ((x >= 21) && (x <= 26))

const jcm_float jcm_real_minimum = 10e-12;

int jcm_traps() { exit(jcm_trape_code); return 0; }

int jcm_error() { exit(jcm_error_code); return 0; }

jcm_bool jcm_trap_on_boolean_i(jcm_int expression, jcm_bool value) {
    jcm_bool expr_value = jcm_cast_to_bool(expression);
    if(value) {     /* trap_on_true */
        if(expr_value) {
            jcm_traps();
        }
        return expr_value;
    }
    else {
        if(!expr_value) {
            jcm_traps();
        }
        return expr_value;
    }
}

jcm_bool jcm_trap_on_boolean_u(jcm_uint expression, jcm_bool value) {
    jcm_bool expr_value = jcm_cast_to_bool(expression);
    if(value) {     /* trap_on_true */
        if(expr_value) {
            jcm_traps();
        }
        return expr_value;
    }
    else {
        if(!expr_value) {
            jcm_traps();
        }
        return expr_value;
    }
}

jcm_bool jcm_trap_on_boolean_f(jcm_float expression, jcm_bool value) {
    jcm_bool expr_value = jcm_cast_to_bool(expression);
    if(value) {     /* trap_on_true */
        if(expr_value) {
            jcm_traps();
        }
        return expr_value;
    }
    else {
        if(!expr_value) {
            jcm_traps();
        }
        return expr_value;
    }
}

jcm_int jcm_trap_on_case_i(jcm_int condition, jcm_int case_value) {
    if(condition == case_value) {
        jcm_traps();
    }
    return condition;
}

jcm_uint jcm_trap_on_case_u(jcm_uint condition, jcm_uint case_value) {
    if(condition == case_value) {
        jcm_traps();
    }
    return condition;
}

int jcm_trap_timmer;

void jcm_init_trap_timmer(int timmer) { jcm_trap_timmer = timmer; }

void jcm_incre_trap_timmer() {
    jcm_trap_timmer--;
    if(jcm_trap_timmer < 0) {
        jcm_traps();
    }
}

jcm_bool jcm_trap_on_true_at_b(jcm_int expression, int times) {
    jcm_bool condition = jcm_cast_to_bool(expression);
    jcm_trap_timmer++;
    if(jcm_trap_timmer == times) {
        if(condition) {
            jcm_traps();
        }
    }
    return condition;
}

jcm_int jcm_trap_on_domain_i(jcm_int expression, char domain) {
    switch(domain) {
        /* trap_on_pos */
        case jcm_pos_domain:
        {
            if(expression > 0) {
                jcm_traps();
            }
        }
        break;
        /* trap_on_neg */
        case jcm_neg_domain:
        {
            if(expression < 0) {
                jcm_traps();
            }
        }
        break;
        /* trap_on_zro */
        case jcm_zro_domain:
        {
            if(expression == 0) {
                jcm_traps();
            }
        }
        break;
        /* syntax error */
        default:
        {
            jcm_error();
        }
        break;
    }
    return expression;
}

jcm_uint jcm_trap_on_domain_u(jcm_uint expression, char domain) {
    switch(domain) {
        /* trap_on_pos */
        case jcm_pos_domain:
        {
            if(expression > 0) {
                jcm_traps();
            }
        }
        break;
        /* trap_on_zro */
        case jcm_zro_domain:
        {
            if(expression == 0) {
                jcm_traps();
            }
        }
        break;
        /* syntax error */
        default:
        {
            jcm_error();
        }
        break;
    }
    return expression;
}

jcm_float jcm_trap_on_domain_f(jcm_float expression, char domain) {
    switch(domain) {
        /* trap_on_pos */
        case jcm_pos_domain:
        {
            if(expression > 0) {
                jcm_traps();
            }
        }
        break;
        /* trap_on_neg */
        case jcm_neg_domain:
        {
            if(expression < 0) {
                jcm_traps();
            }
        }
        break;
        /* trap_on_zro */
        case jcm_zro_domain:
        {
            if(expression == 0) {
                jcm_traps();
            }
        }
        break;
        /* syntax error */
        default:
        {
            jcm_error();
        }
        break;
    }
    return expression;
}

jcm_int jcm_unary_operation_i(char operator, jcm_int expression) {
    jcm_int result;
    
    switch(operator) {
        case jcm_arith_neg_operator:
        {
            result = -expression;
        }
        break;
        case jcm_bitws_rsv_operator:
        {
            result = ~expression;
        }
        break;
        case jcm_logic_not_operator:
        {
            result = !expression;
        }
        break;
        case jcm_abs_invoc_operator:
        {
            if(expression < 0)
                result = -expression;
            else
                result = expression;
        }
        break;
        case jcm_nabs_invoc_operator:
        {
            if(expression < 0)
                result = expression;
            else
                result = -expression;
        }
        break;
        default:
        {
            jcm_error();
        }
        break;
    }

    return result;
}

jcm_uint jcm_unary_operation_u(char operator, jcm_uint expression) {
    jcm_uint result;
    
    switch(operator) {
        case jcm_arith_neg_operator:
        {
            result = -expression;
        }
        break;
        case jcm_bitws_rsv_operator:
        {
            result = ~expression;
        }
        break;
        case jcm_logic_not_operator:
        {
            result = !expression;
        }
        break;
        default:
        {
            jcm_error();
        }
        break;
    }

    return result;
}

jcm_float jcm_unary_operation_f(char operator, jcm_float expression) {
    jcm_float result;
    
    switch(operator) {
        case jcm_arith_neg_operator:
        {
            result = -expression;
        }
        break;
        case jcm_logic_not_operator:
        {
            result = !expression;
        }
        break;
        case jcm_abs_invoc_operator:
        {
            if(expression < 0)
                result = -expression;
            else
                result = expression;
        }
        break;
        case jcm_nabs_invoc_operator:
        {
            if(expression < 0)
                result = expression;
            else
                result = -expression;
        }
        break;
        default:
        {
            jcm_error();
        }
        break;
    }

    return result;
}

jcm_int jcm_arith_operation_i(char operator, jcm_int loperand, jcm_int roperand) {
    jcm_int result;

    switch(operator) {
        case jcm_arith_add_operator:
        {
            result = (loperand + roperand);
        }
        break;
        case jcm_arith_sub_operator:
        {
            result = (loperand - roperand);
        }
        break;
        case jcm_arith_mul_operator:
        {
            result = (loperand * roperand);
        }
        break;
        case jcm_arith_div_operator:
        {
            result = (loperand / roperand);
        }
        break;
        case jcm_arith_mod_operator:
        {
            result = (loperand % roperand);
        }
        break;
        default:
        {
            jcm_error();
        }
        break;
    }

    return result;
}

jcm_uint jcm_arith_operation_u(char operator, jcm_uint loperand, jcm_uint roperand) {
    jcm_uint result;

    switch(operator) {
        case jcm_arith_add_operator:
        {
            result = (loperand + roperand);
        }
        break;
        case jcm_arith_sub_operator:
        {
            result = (loperand - roperand);
        }
        break;
        case jcm_arith_mul_operator:
        {
            result = (loperand * roperand);
        }
        break;
        case jcm_arith_div_operator:
        {
            result = (loperand / roperand);
        }
        break;
        case jcm_arith_mod_operator:
        {
            result = (loperand % roperand);
        }
        break;
        default:
        {
            jcm_error();
        }
        break;
    }

    return result;
}

jcm_float jcm_arith_operation_f(char operator, jcm_float loperand, jcm_float roperand) {
    jcm_float result;

    switch(operator) {
        case jcm_arith_add_operator:
        {
            result = (loperand + roperand);
        }
        break;
        case jcm_arith_sub_operator:
        {
            result = (loperand - roperand);
        }
        break;
        case jcm_arith_mul_operator:
        {
            result = (loperand * roperand);
        }
        break;
        case jcm_arith_div_operator:
        {
            result = (loperand / roperand);
        }
        break;
        default:
        {
            jcm_error();
        }
        break;
    }

    return result;
}

jcm_int jcm_bitws_operation_i(char operator, jcm_int loperand, jcm_int roperand) {
    jcm_int result;

    switch(operator){
        case jcm_bitws_and_operator:
        {
            result = (loperand & roperand);
        }
        break;
        case jcm_bitws_ior_operator:
        {
            result = (loperand | roperand);
        }
        break;
        case jcm_bitws_xor_operator:
        {
            result = (loperand ^ roperand);
        }
        break;
        case jcm_bitws_lsh_operator:
        {
            result = (loperand << roperand);
        }
        break;
        case jcm_bitws_rsh_operator:
        {
            result = (loperand >> roperand);
        }
        break;
        default:
        {
            jcm_error();
        }
        break;
    }

    return result;
}

jcm_uint jcm_bitws_operation_u(char operator, jcm_uint loperand, jcm_uint roperand) {
    jcm_uint result;

    switch(operator){
        case jcm_bitws_and_operator:
        {
            result = (loperand & roperand);
        }
        break;
        case jcm_bitws_ior_operator:
        {
            result = (loperand | roperand);
        }
        break;
        case jcm_bitws_xor_operator:
        {
            result = (loperand ^ roperand);
        }
        break;
        case jcm_bitws_lsh_operator:
        {
            result = (loperand << roperand);
        }
        break;
        case jcm_bitws_rsh_operator:
        {
            result = (loperand >> roperand);
        }
        break;
        default:
        {
            jcm_error();
        }
        break;
    }

    return result;
}

jcm_bool jcm_logic_operation_i(char operator, jcm_int loperand, jcm_int roperand) {
    jcm_bool result;
    loperand = jcm_cast_to_bool(loperand);
    roperand = jcm_cast_to_bool(roperand);

    switch(operator) {
        case jcm_logic_and_operator:
        {
            result = (loperand && roperand);
        }
        break;
        case jcm_logic_ior_operator:
        {
            result = (loperand || roperand);
        }
        break;
        default:
        {
            jcm_error();
        }
        break;
    }

    return result;
}

jcm_bool jcm_logic_operation_u(char operator, jcm_uint loperand, jcm_uint roperand) {
    jcm_bool result;
    loperand = jcm_cast_to_bool(loperand);
    roperand = jcm_cast_to_bool(roperand);

    switch(operator) {
        case jcm_logic_and_operator:
        {
            result = (loperand && roperand);
        }
        break;
        case jcm_logic_ior_operator:
        {
            result = (loperand || roperand);
        }
        break;
        default:
        {
            jcm_error();
        }
        break;
    }

    return result;
}

jcm_bool jcm_logic_operation_f(char operator, jcm_float loperand, jcm_float roperand) {
    jcm_bool result;
    loperand = jcm_cast_to_bool(loperand);
    roperand = jcm_cast_to_bool(roperand);

    switch(operator) {
        case jcm_logic_and_operator:
        {
            result = (loperand && roperand);
        }
        break;
        case jcm_logic_ior_operator:
        {
            result = (loperand || roperand);
        }
        break;
        default:
        {
            jcm_error();
        }
        break;
    }

    return result;
}

jcm_bool jcm_relation_operation_i(char operator, jcm_int loperand, jcm_int roperand) {
    jcm_bool result;

    switch(operator) {
        case jcm_greater_tn_operator:
        {
            result = (loperand > roperand);
        }
        break;
        case jcm_greater_eq_operator:
        {
            result = (loperand >= roperand);
        }
        break;
        case jcm_smaller_tn_operator:
        {
            result = (loperand < roperand);
        }
        break;
        case jcm_smaller_eq_operator:
        {
            result = (loperand <= roperand);
        }
        break;
        case jcm_equal_with_operator:
        {
            result = (loperand == roperand);
        }
        break;
        case jcm_not_equals_operator:
        {
            result = (loperand != roperand);
        }
        break;
        default:
        {
            jcm_error();
        }
        break;
    }

    return result;
}

jcm_bool jcm_relation_operation_u(char operator, jcm_uint loperand, jcm_uint roperand) {
    jcm_bool result;

    switch(operator) {
        case jcm_greater_tn_operator:
        {
            result = (loperand > roperand);
        }
        break;
        case jcm_greater_eq_operator:
        {
            result = (loperand >= roperand);
        }
        break;
        case jcm_smaller_tn_operator:
        {
            result = (loperand < roperand);
        }
        break;
        case jcm_smaller_eq_operator:
        {
            result = (loperand <= roperand);
        }
        break;
        case jcm_equal_with_operator:
        {
            result = (loperand == roperand);
        }
        break;
        case jcm_not_equals_operator:
        {
            result = (loperand != roperand);
        }
        break;
        default:
        {
            jcm_error();
        }
        break;
    }

    return result;
}

jcm_bool jcm_relation_operation_f(char operator, jcm_float loperand, jcm_float roperand) {
    jcm_bool result;

    switch(operator) {
        case jcm_greater_tn_operator:
        {
            result = (loperand > roperand);
        }
        break;
        case jcm_greater_eq_operator:
        {
            result = (loperand >= roperand);
        }
        break;
        case jcm_smaller_tn_operator:
        {
            result = (loperand < roperand);
        }
        break;
        case jcm_smaller_eq_operator:
        {
            result = (loperand <= roperand);
        }
        break;
        case jcm_equal_with_operator:
        {
            result = (loperand == roperand);
        }
        break;
        case jcm_not_equals_operator:
        {
            result = (loperand != roperand);
        }
        break;
        default:
        {
            jcm_error();
        }
        break;
    }

    return result;
}

jcm_int jcm_delete_arith_operand_i(char operator, jcm_int loperand, jcm_int roperand, jcm_bool left_right) {
    jcm_int result = jcm_arith_operation_i(operator, loperand, roperand);
    if(left_right) {
        if(result != roperand) {
            jcm_traps();
        }
        return roperand;
    }
    else {
        if(result != loperand) {
            jcm_traps();
        }
        return loperand;
    }
}

jcm_int jcm_delete_bitws_operand_i(char operator, jcm_int loperand, jcm_int roperand, jcm_bool left_right) {
    jcm_int result = jcm_bitws_operation_i(operator, loperand, roperand);
    if(left_right) {
        if(result != roperand) {
            jcm_traps();
        }
        return roperand;
    }
    else {
        if(result != loperand) {
            jcm_traps();
        }
        return loperand;
    }
}

jcm_bool jcm_delete_logic_operand_i(char operator, jcm_int loperand, jcm_int roperand, jcm_bool left_right) {
    jcm_bool result = jcm_logic_operation_i(operator, loperand, roperand);
    loperand = jcm_cast_to_bool(loperand);
    roperand = jcm_cast_to_bool(roperand);

    if(left_right) {
        if(result != roperand) {
            jcm_traps();
        }
        return roperand;
    }
    else {
        if(result != loperand) {
            jcm_traps();
        }
        return loperand;
    }
}

jcm_bool jcm_delete_relation_operand_i(char operator, jcm_int loperand, jcm_int roperand, jcm_bool left_right) {
    jcm_bool result = jcm_relation_operation_i(operator, loperand, roperand);
    loperand = jcm_cast_to_bool(loperand);
    roperand = jcm_cast_to_bool(roperand);

    if(left_right) {
        if(result != roperand) {
            jcm_traps();
        }
        return roperand;
    }
    else {
        if(result != loperand) {
            jcm_traps();
        }
        return loperand;
    }
}

jcm_uint jcm_delete_arith_operand_u(char operator, jcm_uint loperand, jcm_uint roperand, jcm_bool left_right) {
    jcm_uint result = jcm_arith_operation_u(operator, loperand, roperand);
    if(left_right) {
        if(result != roperand) {
            jcm_traps();
        }
        return roperand;
    }
    else {
        if(result != loperand) {
            jcm_traps();
        }
        return loperand;
    }
}

jcm_uint jcm_delete_bitws_operand_u(char operator, jcm_uint loperand, jcm_uint roperand, jcm_bool left_right) {
    jcm_uint result = jcm_bitws_operation_u(operator, loperand, roperand);
    if(left_right) {
        if(result != roperand) {
            jcm_traps();
        }
        return roperand;
    }
    else {
        if(result != loperand) {
            jcm_traps();
        }
        return loperand;
    }
}

jcm_bool jcm_delete_logic_operand_u(char operator, jcm_uint loperand, jcm_uint roperand, jcm_bool left_right) {
    jcm_bool result = jcm_logic_operation_u(operator, loperand, roperand);
    loperand = jcm_cast_to_bool(loperand);
    roperand = jcm_cast_to_bool(roperand);

    if(left_right) {
        if(result != roperand) {
            jcm_traps();
        }
        return roperand;
    }
    else {
        if(result != loperand) {
            jcm_traps();
        }
        return loperand;
    }
}

jcm_bool jcm_delete_relation_operand_u(char operator, jcm_uint loperand, jcm_uint roperand, jcm_bool left_right) {
    jcm_bool result = jcm_relation_operation_u(operator, loperand, roperand);
    loperand = jcm_cast_to_bool(loperand);
    roperand = jcm_cast_to_bool(roperand);

    if(left_right) {
        if(result != roperand) {
            jcm_traps();
        }
        return roperand;
    }
    else {
        if(result != loperand) {
            jcm_traps();
        }
        return loperand;
    }
}

jcm_float jcm_delete_arith_operand_f(char operator, jcm_float loperand, jcm_float roperand, jcm_bool left_right) {
    jcm_float result = jcm_arith_operation_f(operator, loperand, roperand);
    if(left_right) {
        if(result != roperand) {
            jcm_traps();
        }
        return roperand;
    }
    else {
        if(result != loperand) {
            jcm_traps();
        }
        return loperand;
    }
}

jcm_bool jcm_delete_logic_operand_f(char operator, jcm_float loperand, jcm_float roperand, jcm_bool left_right) {
    jcm_bool result = jcm_logic_operation_u(operator, loperand, roperand);
    loperand = jcm_cast_to_bool(loperand);
    roperand = jcm_cast_to_bool(roperand);

    if(left_right) {
        if(result != roperand) {
            jcm_traps();
        }
        return roperand;
    }
    else {
        if(result != loperand) {
            jcm_traps();
        }
        return loperand;
    }
}

jcm_bool jcm_delete_relation_operand_f(char operator, jcm_float loperand, jcm_float roperand, jcm_bool left_right) {
    jcm_bool result = jcm_relation_operation_u(operator, loperand, roperand);
    loperand = jcm_cast_to_bool(loperand);
    roperand = jcm_cast_to_bool(roperand);

    if(left_right) {
        if(result != roperand) {
            jcm_traps();
        }
        return roperand;
    }
    else {
        if(result != loperand) {
            jcm_traps();
        }
        return loperand;
    }
}

jcm_int jcm_insert_unary_operator_i(char operator, jcm_int expression) {
    jcm_int result = jcm_unary_operation_i(operator, expression);
    if(result != expression) {
        jcm_traps();
    }
    return result;
}

jcm_uint jcm_insert_unary_operator_u(char operator, jcm_uint expression) {
    jcm_uint result = jcm_unary_operation_u(operator, expression);
    if(result != expression) {
        jcm_traps();
    }
    return result;
}

jcm_float jcm_insert_unary_operator_f(char operator, jcm_float expression) {
    jcm_float result = jcm_unary_operation_f(operator, expression);
    if(result != expression) {
        jcm_traps();
    }
    return result;
}

jcm_int jcm_delete_unary_operator_i(char operator, jcm_int operand) {
    jcm_int result = jcm_unary_operation_i(operator, operand);
    if(result != operand) {
        jcm_traps();
    }
    return operand;
}

jcm_uint jcm_delete_unary_operator_u(char operator, jcm_uint operand) {
    jcm_uint result = jcm_unary_operation_u(operator, operand);
    if(result != operand) {
        jcm_traps();
    }
    return operand;
}

jcm_float jcm_delete_unary_operator_f(char operator, jcm_float operand) {
    jcm_float result = jcm_unary_operation_f(operator, operand);
    if(result != operand) {
        jcm_traps();
    }
    return operand;
}

jcm_int jcm_arith_mutate_operator_i(char operator1, jcm_int loperand, jcm_int roperand, char operator2) {
    jcm_int result1 = jcm_arith_operation_i(operator1, loperand, roperand);

    if(jcm_is_arith_operator(operator2)) {
        jcm_int result2 = jcm_arith_operation_i(operator2, loperand, roperand);
        if(result1 != result2) {
            jcm_traps();
        }
    }
    else if(jcm_is_bitws_operator(operator2)) {
        jcm_int result2 = jcm_bitws_operation_i(operator2, loperand, roperand);
        if(result1 != result2) {
            jcm_traps();
        }
    }
    else if(jcm_is_logic_operator(operator2)) {
        jcm_bool result2 = jcm_logic_operation_i(operator2, loperand, roperand);
        if(result1 != result2) {
            jcm_traps();
        }
    }
    else if(jcm_is_relation_operator(operator2)) {
        jcm_bool result2 = jcm_relation_operation_i(operator2, loperand, roperand);
        if(result1 != result2) {
            jcm_traps();
        }
    }
    else {
        jcm_error();
    }

    return result1;
}

jcm_int jcm_bitws_mutate_operator_i(char operator1, jcm_int loperand, jcm_int roperand, char operator2) {
    jcm_int result1 = jcm_bitws_operation_i(operator1, loperand, roperand);

    if(jcm_is_arith_operator(operator2)) {
        jcm_int result2 = jcm_arith_operation_i(operator2, loperand, roperand);
        if(result1 != result2) {
            jcm_traps();
        }
    }
    else if(jcm_is_bitws_operator(operator2)) {
        jcm_int result2 = jcm_bitws_operation_i(operator2, loperand, roperand);
        if(result1 != result2) {
            jcm_traps();
        }
    }
    else if(jcm_is_logic_operator(operator2)) {
        jcm_bool result2 = jcm_logic_operation_i(operator2, loperand, roperand);
        if(result1 != result2) {
            jcm_traps();
        }
    }
    else if(jcm_is_relation_operator(operator2)) {
        jcm_bool result2 = jcm_relation_operation_i(operator2, loperand, roperand);
        if(result1 != result2) {
            jcm_traps();
        }
    }
    else {
        jcm_error();
    }

    return result1;
}

jcm_bool jcm_logic_mutate_operator_i(char operator1, jcm_int loperand, jcm_int roperand, char operator2) {
    jcm_bool result1 = jcm_logic_operation_i(operator1, loperand, roperand);

    if(jcm_is_arith_operator(operator2)) {
        jcm_int result2 = jcm_arith_operation_i(operator2, loperand, roperand);
        if(result1 != jcm_cast_to_bool(result2)) {
            jcm_traps();
        }
    }
    else if(jcm_is_bitws_operator(operator2)) {
        jcm_int result2 = jcm_bitws_operation_i(operator2, loperand, roperand);
        if(result1 != jcm_cast_to_bool(result2)) {
            jcm_traps();
        }
    }
    else if(jcm_is_logic_operator(operator2)) {
        jcm_bool result2 = jcm_logic_operation_i(operator2, loperand, roperand);
        if(result1 != result2) {
            jcm_traps();
        }
    }
    else if(jcm_is_relation_operator(operator2)) {
        jcm_bool result2 = jcm_relation_operation_i(operator2, loperand, roperand);
        if(result1 != result2) {
            jcm_traps();
        }
    }
    else {
        jcm_error();
    }

    return result1;
}

jcm_bool jcm_relation_mutate_operator_i(char operator1, jcm_int loperand, jcm_int roperand, char operator2) {
    jcm_bool result1 = jcm_relation_operation_i(operator1, loperand, roperand);

    if(jcm_is_arith_operator(operator2)) {
        jcm_int result2 = jcm_arith_operation_i(operator2, loperand, roperand);
        if(result1 != jcm_cast_to_bool(result2)) {
            jcm_traps();
        }
    }
    else if(jcm_is_bitws_operator(operator2)) {
        jcm_int result2 = jcm_bitws_operation_i(operator2, loperand, roperand);
        if(result1 != jcm_cast_to_bool(result2)) {
            jcm_traps();
        }
    }
    else if(jcm_is_logic_operator(operator2)) {
        jcm_bool result2 = jcm_logic_operation_i(operator2, loperand, roperand);
        if(result1 != result2) {
            jcm_traps();
        }
    }
    else if(jcm_is_relation_operator(operator2)) {
        jcm_bool result2 = jcm_relation_operation_i(operator2, loperand, roperand);
        if(result1 != result2) {
            jcm_traps();
        }
    }
    else {
        jcm_error();
    }

    return result1;
}

jcm_uint jcm_arith_mutate_operator_u(char operator1, jcm_uint loperand, jcm_uint roperand, char operator2) {
    jcm_uint result1 = jcm_arith_operation_u(operator1, loperand, roperand);

    if(jcm_is_arith_operator(operator2)) {
        jcm_uint result2 = jcm_arith_operation_u(operator2, loperand, roperand);
        if(result1 != result2) {
            jcm_traps();
        }
    }
    else if(jcm_is_bitws_operator(operator2)) {
        jcm_uint result2 = jcm_bitws_operation_u(operator2, loperand, roperand);
        if(result1 != result2) {
            jcm_traps();
        }
    }
    else if(jcm_is_logic_operator(operator2)) {
        jcm_bool result2 = jcm_logic_operation_u(operator2, loperand, roperand);
        if(result1 != result2) {
            jcm_traps();
        }
    }
    else if(jcm_is_relation_operator(operator2)) {
        jcm_bool result2 = jcm_relation_operation_u(operator2, loperand, roperand);
        if(result1 != result2) {
            jcm_traps();
        }
    }
    else {
        jcm_error();
    }

    return result1;
}

jcm_uint jcm_bitws_mutate_operator_u(char operator1, jcm_uint loperand, jcm_uint roperand, char operator2) {
    jcm_uint result1 = jcm_bitws_operation_u(operator1, loperand, roperand);

    if(jcm_is_arith_operator(operator2)) {
        jcm_uint result2 = jcm_arith_operation_u(operator2, loperand, roperand);
        if(result1 != result2) {
            jcm_traps();
        }
    }
    else if(jcm_is_bitws_operator(operator2)) {
        jcm_uint result2 = jcm_bitws_operation_u(operator2, loperand, roperand);
        if(result1 != result2) {
            jcm_traps();
        }
    }
    else if(jcm_is_logic_operator(operator2)) {
        jcm_bool result2 = jcm_logic_operation_u(operator2, loperand, roperand);
        if(result1 != result2) {
            jcm_traps();
        }
    }
    else if(jcm_is_relation_operator(operator2)) {
        jcm_bool result2 = jcm_relation_operation_u(operator2, loperand, roperand);
        if(result1 != result2) {
            jcm_traps();
        }
    }
    else {
        jcm_error();
    }

    return result1;
}

jcm_bool jcm_logic_mutate_operator_u(char operator1, jcm_uint loperand, jcm_uint roperand, char operator2) {
    jcm_bool result1 = jcm_logic_operation_u(operator1, loperand, roperand);

    if(jcm_is_arith_operator(operator2)) {
        jcm_uint result2 = jcm_arith_operation_u(operator2, loperand, roperand);
        if(result1 != jcm_cast_to_bool(result2)) {
            jcm_traps();
        }
    }
    else if(jcm_is_bitws_operator(operator2)) {
        jcm_uint result2 = jcm_bitws_operation_u(operator2, loperand, roperand);
        if(result1 != jcm_cast_to_bool(result2)) {
            jcm_traps();
        }
    }
    else if(jcm_is_logic_operator(operator2)) {
        jcm_bool result2 = jcm_logic_operation_u(operator2, loperand, roperand);
        if(result1 != result2) {
            jcm_traps();
        }
    }
    else if(jcm_is_relation_operator(operator2)) {
        jcm_bool result2 = jcm_relation_operation_u(operator2, loperand, roperand);
        if(result1 != result2) {
            jcm_traps();
        }
    }
    else {
        jcm_error();
    }

    return result1;
}

jcm_bool jcm_relation_mutate_operator_u(char operator1, jcm_uint loperand, jcm_uint roperand, char operator2) {
    jcm_bool result1 = jcm_relation_operation_u(operator1, loperand, roperand);

    if(jcm_is_arith_operator(operator2)) {
        jcm_uint result2 = jcm_arith_operation_u(operator2, loperand, roperand);
        if(result1 != jcm_cast_to_bool(result2)) {
            jcm_traps();
        }
    }
    else if(jcm_is_bitws_operator(operator2)) {
        jcm_uint result2 = jcm_bitws_operation_u(operator2, loperand, roperand);
        if(result1 != jcm_cast_to_bool(result2)) {
            jcm_traps();
        }
    }
    else if(jcm_is_logic_operator(operator2)) {
        jcm_bool result2 = jcm_logic_operation_u(operator2, loperand, roperand);
        if(result1 != result2) {
            jcm_traps();
        }
    }
    else if(jcm_is_relation_operator(operator2)) {
        jcm_bool result2 = jcm_relation_operation_u(operator2, loperand, roperand);
        if(result1 != result2) {
            jcm_traps();
        }
    }
    else {
        jcm_error();
    }

    return result1;
}

jcm_float jcm_arith_mutate_operator_f(char operator1, jcm_float loperand, jcm_float roperand, char operator2) {
    jcm_float result1 = jcm_arith_operation_f(operator1, loperand, roperand);

    if(jcm_is_arith_operator(operator2)) {
        jcm_float result2 = jcm_arith_operation_f(operator2, loperand, roperand);
        if(result1 != result2) {
            jcm_traps();
        }
    }
    else if(jcm_is_logic_operator(operator2)) {
        jcm_bool result2 = jcm_logic_operation_f(operator2, loperand, roperand);
        if(result1 != result2) {
            jcm_traps();
        }
    }
    else if(jcm_is_relation_operator(operator2)) {
        jcm_bool result2 = jcm_relation_operation_f(operator2, loperand, roperand);
        if(result1 != result2) {
            jcm_traps();
        }
    }
    else {
        jcm_error();
    }

    return result1;
}

jcm_bool jcm_logic_mutate_operator_f(char operator1, jcm_float loperand, jcm_float roperand, char operator2) {
    jcm_bool result1 = jcm_logic_operation_f(operator1, loperand, roperand);

    if(jcm_is_arith_operator(operator2)) {
        jcm_float result2 = jcm_arith_operation_f(operator2, loperand, roperand);
        if(result1 != jcm_cast_to_bool(result2)) {
            jcm_traps();
        }
    }
    else if(jcm_is_logic_operator(operator2)) {
        jcm_bool result2 = jcm_logic_operation_f(operator2, loperand, roperand);
        if(result1 != result2) {
            jcm_traps();
        }
    }
    else if(jcm_is_relation_operator(operator2)) {
        jcm_bool result2 = jcm_relation_operation_f(operator2, loperand, roperand);
        if(result1 != result2) {
            jcm_traps();
        }
    }
    else {
        jcm_error();
    }

    return result1;
}

jcm_bool jcm_relation_mutate_operator_f(char operator1, jcm_float loperand, jcm_float roperand, char operator2) {
    jcm_bool result1 = jcm_relation_operation_f(operator1, loperand, roperand);

    if(jcm_is_arith_operator(operator2)) {
        jcm_float result2 = jcm_arith_operation_f(operator2, loperand, roperand);
        if(result1 != jcm_cast_to_bool(result2)) {
            jcm_traps();
        }
    }
    else if(jcm_is_logic_operator(operator2)) {
        jcm_bool result2 = jcm_logic_operation_f(operator2, loperand, roperand);
        if(result1 != result2) {
            jcm_traps();
        }
    }
    else if(jcm_is_relation_operator(operator2)) {
        jcm_bool result2 = jcm_relation_operation_f(operator2, loperand, roperand);
        if(result1 != result2) {
            jcm_traps();
        }
    }
    else {
        jcm_error();
    }

    return result1;
}

jcm_int jcm_abs_operation_i(jcm_int expression) {
    if(expression < 0)
        return -expression;
    else
        return expression;
}

jcm_float jcm_abs_operation_f(jcm_float expression) {
    if(expression < 0)
        return -expression;
    else
        return expression;
}

jcm_int jcm_nabs_operation_i(jcm_int expression) {
    if(expression > 0)
        return -expression;
    else
        return expression;
}

jcm_float jcm_nabs_operation_f(jcm_float expression) {
    if(expression > 0)
        return -expression;
    else
        return expression;
}

jcm_bool jcm_real_equal_with(jcm_float x, jcm_float y) {
    jcm_float difference = x - y;
    return (difference <= jcm_real_minimum) && (difference >= -jcm_real_minimum);
}

jcm_bool jcm_real_not_equals(jcm_float x, jcm_float y) {
    jcm_float difference = x - y;
    return (difference > jcm_real_minimum) || (difference < -jcm_real_minimum);
}

jcm_bool jcm_real_equal_with_weak(jcm_float x, jcm_float y) {
    jcm_float difference = x - y;
    jcm_bool result1 = ((difference <= jcm_real_minimum) && (difference >= -jcm_real_minimum));
    jcm_bool result2 = (x == y);
    if(result1 != result2) {
        jcm_traps();
    }
    return result2;
}

jcm_bool jcm_real_not_equals_weak(jcm_float x, jcm_float y) {
    jcm_float difference = x - y;
    jcm_bool result1 = ((difference > jcm_real_minimum) || (difference < -jcm_real_minimum));
    jcm_bool result2 = (x != y);
    if(result1 != result2) {
        jcm_traps();
    }
    return result2;
}

// end of all