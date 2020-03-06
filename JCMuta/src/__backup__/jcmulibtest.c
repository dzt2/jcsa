#include "jcmulib.h"
#include <stdio.h>


void test1() {
    printf("jcm_trape_code = %d\n", jcm_trape_code);
    printf("jcm_error_code = %d\n", jcm_error_code);
    printf("jcm_true_constant = %d\n", jcm_true_constant);
    printf("jcm_false_constant = %d\n", jcm_false_constant);
    printf("\n");
    printf("jcm_pos_domain = %d\n", jcm_pos_domain);
    printf("jcm_neg_domain = %d\n", jcm_neg_domain);
    printf("jcm_zro_domain = %d\n", jcm_zro_domain);
    printf("\n");
    printf("jcm_arith_neg_operator = %d\n", jcm_arith_neg_operator);
    printf("jcm_bitws_rsv_operator = %d\n", jcm_bitws_rsv_operator);
    printf("jcm_logic_not_operator = %d\n", jcm_logic_not_operator);
    printf("jcm_abs_invoc_operator = %d\n", jcm_abs_invoc_operator);
    printf("jcm_nabs_invoc_operator= %d\n", jcm_nabs_invoc_operator);
    printf("\n");
    printf("jcm_arith_add_operator = %d\n", jcm_arith_add_operator);
    printf("jcm_arith_sub_operator = %d\n", jcm_arith_sub_operator);
    printf("jcm_arith_mul_operator = %d\n", jcm_arith_mul_operator);
    printf("jcm_arith_div_operator = %d\n", jcm_arith_div_operator);
    printf("jcm_arith_mod_operator = %d\n", jcm_arith_mod_operator);
    printf("\n");
    printf("jcm_bitws_and_operator = %d\n", jcm_bitws_and_operator);
    printf("jcm_bitws_ior_operator = %d\n", jcm_bitws_ior_operator);
    printf("jcm_bitws_xor_operator = %d\n", jcm_bitws_xor_operator);
    printf("jcm_bitws_lsh_operator = %d\n", jcm_bitws_lsh_operator);
    printf("jcm_bitws_rsh_operator = %d\n", jcm_bitws_rsh_operator);
    printf("\n");
    printf("jcm_logic_and_operator = %d\n", jcm_logic_and_operator);
    printf("jcm_logic_ior_operator = %d\n", jcm_logic_ior_operator);
    printf("\n");
    printf("jcm_greater_tn_operator = %d\n", jcm_greater_tn_operator);
    printf("jcm_greater_eq_operator = %d\n", jcm_greater_eq_operator);
    printf("jcm_smaller_tn_operator = %d\n", jcm_smaller_tn_operator);
    printf("jcm_smaller_eq_operator = %d\n", jcm_smaller_eq_operator);
    printf("jcm_equal_with_operator = %d\n", jcm_equal_with_operator);
    printf("jcm_not_equals_operator = %d\n", jcm_not_equals_operator);
    printf("\n");
    jcm_error();
}

void test2() {
    printf("trap_on_true(0, true) = %d\n", jcm_trap_on_boolean_u(0, jcm_true_constant));
    printf("trap_on_false(13, false) = %d\n", jcm_trap_on_boolean_f(13, jcm_false_constant));
    printf("trap_on_true(-7, true) = %d\n", jcm_trap_on_boolean_u(-7, jcm_true_constant));
}

void test3() {
    int counter = 0;
    jcm_init_trap_timmer(6);
    while(jcm_true_constant) {
        jcm_incre_trap_timmer();
        printf("Loop at %d\n", ++counter);
    }
}

void test4() {
    int counter = 0;
    jcm_init_trap_timmer(0);
    while(jcm_trap_on_true_at_b(jcm_true_constant, 3)) {
        printf("Loop at %d\n", ++counter);
    }
}

void test5() {
    for(int k = -5; k < 5; k++) {
        jcm_trap_on_case_u(k, -2);
        printf("Loop at %d\n", k);
    }
}

void test6() {
    for(int k = 5; k >= -5; k--) {
        printf("%f\n", ((float) jcm_trap_on_domain_f(k, jcm_neg_domain)));
    }
}

void test7() {
    for(int k = -5; k <= 5; k++) {
        printf("%d\t%lld\t%lld\t%lld\t%lld\t%lld\n", k,
            jcm_unary_operation_i(jcm_arith_neg_operator, k),
            jcm_unary_operation_i(jcm_bitws_rsv_operator, k),
            jcm_unary_operation_i(jcm_logic_not_operator, k),
            jcm_unary_operation_i(jcm_abs_invoc_operator, k),
            jcm_unary_operation_i(jcm_nabs_invoc_operator, k));
    }
}

void test8() {
    printf("%lld\n", jcm_delete_arith_operand_i(jcm_arith_mul_operator, 5, 1, jcm_false_constant));
}

int main(int argc, char *argv[]) {
    // test1();
    // test2();
    // test3();
    // test4();
    // test5();
    // test6();
    // test7();
    test8();
    return 0;
}



