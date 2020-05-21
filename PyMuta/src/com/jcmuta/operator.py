from enum import Enum


class MutaClass(Enum):
    BTRP = 0
    CTRP = 1
    ETRP = 2
    STRP = 3
    TTRP = 4
    VTRP = 5
    SBCR = 6
    SBCI = 7
    SWDR = 8
    SGLR = 9
    STDL = 10
    UIOR = 11
    UIOI = 12
    UIOD = 13
    VINC = 14
    UNOI = 15
    UNOD = 16
    OAAN = 17
    OABN = 18
    OALN = 19
    OARN = 20
    OBAN = 21
    OBBN = 22
    OBLN = 23
    OBRN = 24
    OLAN = 25
    OLBN = 26
    OLLN = 27
    OLRN = 28
    ORAN = 29
    ORBN = 30
    ORLN = 31
    ORRN = 32
    OEAA = 33
    OEBA = 34
    OAAA = 35
    OABA = 36
    OAEA = 37
    OBAA = 38
    OBBA = 39
    OBEA = 40
    OPDL = 41
    VBRP = 42
    VCRP = 43
    VRRP = 44
    SRTR = 45
    EQAR = 46
    OSBI = 47
    OIFI = 48
    OIFR = 49
    ODFI = 50
    ODFR = 51
    OFLT = 52

    def __str__(self):
        return self.name

    @staticmethod
    def parse(text: str):
        if text == "BTRP":
            return MutaClass.BTRP
        elif text == "CTRP":
            return MutaClass.CTRP
        elif text == "ETRP":
            return MutaClass.ETRP
        elif text == "STRP":
            return MutaClass.STRP
        elif text == "TTRP":
            return MutaClass.TTRP
        elif text == "VTRP":
            return MutaClass.VTRP
        elif text == "SBCR":
            return MutaClass.SBCR
        elif text == "SBCI":
            return MutaClass.SBCI
        elif text == "SWDR":
            return MutaClass.SWDR
        elif text == "SGLR":
            return MutaClass.SGLR
        elif text == "STDL":
            return MutaClass.STDL
        elif text == "UIOR":
            return MutaClass.UIOR
        elif text == "UIOI":
            return MutaClass.UIOI
        elif text == "UIOD":
            return MutaClass.UIOD
        elif text == "VINC":
            return MutaClass.VINC
        elif text == "UNOI":
            return MutaClass.UNOI
        elif text == "UNOD":
            return MutaClass.UNOD
        elif text == "OAAN":
            return MutaClass.OAAN
        elif text == "OABN":
            return MutaClass.OABN
        elif text == "OALN":
            return MutaClass.OALN
        elif text == "OARN":
            return MutaClass.OARN
        elif text == "OBAN":
            return MutaClass.OBAN
        elif text == "OBBN":
            return MutaClass.OBBN
        elif text == "OBLN":
            return MutaClass.OBLN
        elif text == "OBRN":
            return MutaClass.OBRN
        elif text == "OLAN":
            return MutaClass.OLAN
        elif text == "OLBN":
            return MutaClass.OLBN
        elif text == "OLLN":
            return MutaClass.OLLN
        elif text == "OLRN":
            return MutaClass.OLRN
        elif text == "ORAN":
            return MutaClass.ORAN
        elif text == "ORBN":
            return MutaClass.ORBN
        elif text == "ORLN":
            return MutaClass.ORLN
        elif text == "ORRN":
            return MutaClass.ORRN
        elif text == "OEAA":
            return MutaClass.OEAA
        elif text == "OEBA":
            return MutaClass.OEBA
        elif text == "OAAA":
            return MutaClass.OAAA
        elif text == "OABA":
            return MutaClass.OABA
        elif text == "OAEA":
            return MutaClass.OAEA
        elif text == "OBAA":
            return MutaClass.OBAA
        elif text == "OBBA":
            return MutaClass.OBBA
        elif text == "OBEA":
            return MutaClass.OBEA
        elif text == "OPDL":
            return MutaClass.OPDL
        elif text == "VBRP":
            return MutaClass.VBRP
        elif text == "VCRP":
            return MutaClass.VCRP
        elif text == "VRRP":
            return MutaClass.VRRP
        elif text == "SRTR":
            return MutaClass.SRTR
        elif text == "EQAR":
            return MutaClass.EQAR
        elif text == "OSBI":
            return MutaClass.OSBI
        elif text == "OIFI":
            return MutaClass.OIFI
        elif text == "OIFR":
            return MutaClass.OIFR
        elif text == "ODFI":
            return MutaClass.ODFI
        elif text == "ODFR":
            return MutaClass.ODFR
        elif text == "OFLT":
            return MutaClass.OFLT
        else:
            return None


class MutaOperator(Enum):
    trap_on_true = 0
    trap_on_false = 1
    trap_on_case = 2
    trap_on_expression = 3
    trap_on_statement = 4
    trap_at_statement = 5
    trap_on_pos = 6
    trap_on_neg = 7
    trap_on_zro = 8
    break_to_continue = 9
    continue_to_break = 10
    ins_break = 11
    ins_continue = 12
    do_to_while = 13
    while_to_do = 14
    set_goto_label = 15
    delete_statement = 16
    delete_operand = 17
    delete_element = 18
    set_return_value = 19
    prev_inc_to_prev_dec = 20
    prev_inc_to_post_inc = 21
    prev_inc_to_post_dec = 22
    prev_dec_to_prev_inc = 23
    prev_dec_to_post_dec = 24
    prev_dec_to_post_inc = 25
    post_inc_to_post_dec = 26
    post_inc_to_prev_inc = 27
    post_inc_to_prev_dec = 28
    post_dec_to_post_inc = 29
    post_dec_to_prev_inc = 30
    post_dec_to_prev_dec = 31
    insert_prev_inc = 32
    insert_prev_dec = 33
    insert_post_inc = 34
    insert_post_dec = 35
    delete_prev_inc = 36
    delete_prev_dec = 37
    delete_post_inc = 38
    delete_post_dec = 39
    inc_value = 40
    mul_value = 41
    insert_arith_neg = 42
    insert_bitws_rsv = 43
    insert_logic_not = 44
    insert_abs = 45
    insert_neg_abs = 46
    delete_arith_neg = 47
    delete_bitws_rsv = 48
    delete_logic_not = 49
    set_true = 50
    set_false = 51
    set_constant = 52
    set_reference = 53
    equal_with_to_assign = 54
    ins_empty_body = 55
    ins_elif_in_if = 56
    set_elif_as_else = 57
    ins_default = 58
    set_default = 59
    equal_with_to_real_compare = 60
    not_equals_to_real_compare = 61

    arith_add_to_arith_add = 62
    arith_add_to_arith_sub = 63
    arith_add_to_arith_mul = 64
    arith_add_to_arith_div = 65
    arith_add_to_arith_mod = 66
    arith_add_to_bitws_and = 67
    arith_add_to_bitws_ior = 68
    arith_add_to_bitws_xor = 69
    arith_add_to_bitws_lsh = 70
    arith_add_to_bitws_rsh = 71
    arith_add_to_logic_and = 72
    arith_add_to_logic_ior = 73
    arith_add_to_greater_tn = 74
    arith_add_to_greater_eq = 75
    arith_add_to_smaller_tn = 76
    arith_add_to_smaller_eq = 77
    arith_add_to_equal_with = 78
    arith_add_to_not_equals = 79

    arith_sub_to_arith_add = 80
    arith_sub_to_arith_sub = 81
    arith_sub_to_arith_mul = 82
    arith_sub_to_arith_div = 83
    arith_sub_to_arith_mod = 84
    arith_sub_to_bitws_and = 85
    arith_sub_to_bitws_ior = 86
    arith_sub_to_bitws_xor = 87
    arith_sub_to_bitws_lsh = 88
    arith_sub_to_bitws_rsh = 89
    arith_sub_to_logic_and = 90
    arith_sub_to_logic_ior = 91
    arith_sub_to_greater_tn = 92
    arith_sub_to_greater_eq = 93
    arith_sub_to_smaller_tn = 94
    arith_sub_to_smaller_eq = 95
    arith_sub_to_equal_with = 96
    arith_sub_to_not_equals = 97

    arith_mul_to_arith_add = 80
    arith_mul_to_arith_sub = 81
    arith_mul_to_arith_mul = 82
    arith_mul_to_arith_div = 83
    arith_mul_to_arith_mod = 84
    arith_mul_to_bitws_and = 85
    arith_mul_to_bitws_ior = 86
    arith_mul_to_bitws_xor = 87
    arith_mul_to_bitws_lsh = 88
    arith_mul_to_bitws_rsh = 89
    arith_mul_to_logic_and = 90
    arith_mul_to_logic_ior = 91
    arith_mul_to_greater_tn = 92
    arith_mul_to_greater_eq = 93
    arith_mul_to_smaller_tn = 94
    arith_mul_to_smaller_eq = 95
    arith_mul_to_equal_with = 96
    arith_mul_to_not_equals = 97

    arith_div_to_arith_add = 80
    arith_div_to_arith_sub = 81
    arith_div_to_arith_mul = 82
    arith_div_to_arith_div = 83
    arith_div_to_arith_mod = 84
    arith_div_to_bitws_and = 85
    arith_div_to_bitws_ior = 86
    arith_div_to_bitws_xor = 87
    arith_div_to_bitws_lsh = 88
    arith_div_to_bitws_rsh = 89
    arith_div_to_logic_and = 90
    arith_div_to_logic_ior = 91
    arith_div_to_greater_tn = 92
    arith_div_to_greater_eq = 93
    arith_div_to_smaller_tn = 94
    arith_div_to_smaller_eq = 95
    arith_div_to_equal_with = 96
    arith_div_to_not_equals = 97

    arith_mod_to_arith_add = 80
    arith_mod_to_arith_sub = 81
    arith_mod_to_arith_mul = 82
    arith_mod_to_arith_div = 83
    arith_mod_to_arith_mod = 84
    arith_mod_to_bitws_and = 85
    arith_mod_to_bitws_ior = 86
    arith_mod_to_bitws_xor = 87
    arith_mod_to_bitws_lsh = 88
    arith_mod_to_bitws_rsh = 89
    arith_mod_to_logic_and = 90
    arith_mod_to_logic_ior = 91
    arith_mod_to_greater_tn = 92
    arith_mod_to_greater_eq = 93
    arith_mod_to_smaller_tn = 94
    arith_mod_to_smaller_eq = 95
    arith_mod_to_equal_with = 96
    arith_mod_to_not_equals = 97

    bitws_and_to_arith_add = 80
    bitws_and_to_arith_sub = 81
    bitws_and_to_arith_mul = 82
    bitws_and_to_arith_div = 83
    bitws_and_to_arith_mod = 84
    bitws_and_to_bitws_and = 85
    bitws_and_to_bitws_ior = 86
    bitws_and_to_bitws_xor = 87
    bitws_and_to_bitws_lsh = 88
    bitws_and_to_bitws_rsh = 89
    bitws_and_to_logic_and = 90
    bitws_and_to_logic_ior = 91
    bitws_and_to_greater_tn = 92
    bitws_and_to_greater_eq = 93
    bitws_and_to_smaller_tn = 94
    bitws_and_to_smaller_eq = 95
    bitws_and_to_equal_with = 96
    bitws_and_to_not_equals = 97

    bitws_ior_to_arith_add = 80
    bitws_ior_to_arith_sub = 81
    bitws_ior_to_arith_mul = 82
    bitws_ior_to_arith_div = 83
    bitws_ior_to_arith_mod = 84
    bitws_ior_to_bitws_and = 85
    bitws_ior_to_bitws_ior = 86
    bitws_ior_to_bitws_xor = 87
    bitws_ior_to_bitws_lsh = 88
    bitws_ior_to_bitws_rsh = 89
    bitws_ior_to_logic_and = 90
    bitws_ior_to_logic_ior = 91
    bitws_ior_to_greater_tn = 92
    bitws_ior_to_greater_eq = 93
    bitws_ior_to_smaller_tn = 94
    bitws_ior_to_smaller_eq = 95
    bitws_ior_to_equal_with = 96
    bitws_ior_to_not_equals = 97

    bitws_xor_to_arith_add = 80
    bitws_xor_to_arith_sub = 81
    bitws_xor_to_arith_mul = 82
    bitws_xor_to_arith_div = 83
    bitws_xor_to_arith_mod = 84
    bitws_xor_to_bitws_and = 85
    bitws_xor_to_bitws_ior = 86
    bitws_xor_to_bitws_xor = 87
    bitws_xor_to_bitws_lsh = 88
    bitws_xor_to_bitws_rsh = 89
    bitws_xor_to_logic_and = 90
    bitws_xor_to_logic_ior = 91
    bitws_xor_to_greater_tn = 92
    bitws_xor_to_greater_eq = 93
    bitws_xor_to_smaller_tn = 94
    bitws_xor_to_smaller_eq = 95
    bitws_xor_to_equal_with = 96
    bitws_xor_to_not_equals = 97

    bitws_lsh_to_arith_add = 80
    bitws_lsh_to_arith_sub = 81
    bitws_lsh_to_arith_mul = 82
    bitws_lsh_to_arith_div = 83
    bitws_lsh_to_arith_mod = 84
    bitws_lsh_to_bitws_and = 85
    bitws_lsh_to_bitws_ior = 86
    bitws_lsh_to_bitws_xor = 87
    bitws_lsh_to_bitws_lsh = 88
    bitws_lsh_to_bitws_rsh = 89
    bitws_lsh_to_logic_and = 90
    bitws_lsh_to_logic_ior = 91
    bitws_lsh_to_greater_tn = 92
    bitws_lsh_to_greater_eq = 93
    bitws_lsh_to_smaller_tn = 94
    bitws_lsh_to_smaller_eq = 95
    bitws_lsh_to_equal_with = 96
    bitws_lsh_to_not_equals = 97

    bitws_rsh_to_arith_add = 80
    bitws_rsh_to_arith_sub = 81
    bitws_rsh_to_arith_mul = 82
    bitws_rsh_to_arith_div = 83
    bitws_rsh_to_arith_mod = 84
    bitws_rsh_to_bitws_and = 85
    bitws_rsh_to_bitws_ior = 86
    bitws_rsh_to_bitws_xor = 87
    bitws_rsh_to_bitws_lsh = 88
    bitws_rsh_to_bitws_rsh = 89
    bitws_rsh_to_logic_and = 90
    bitws_rsh_to_logic_ior = 91
    bitws_rsh_to_greater_tn = 92
    bitws_rsh_to_greater_eq = 93
    bitws_rsh_to_smaller_tn = 94
    bitws_rsh_to_smaller_eq = 95
    bitws_rsh_to_equal_with = 96
    bitws_rsh_to_not_equals = 97

    logic_and_to_arith_add = 80
    logic_and_to_arith_sub = 81
    logic_and_to_arith_mul = 82
    logic_and_to_arith_div = 83
    logic_and_to_arith_mod = 84
    logic_and_to_bitws_and = 85
    logic_and_to_bitws_ior = 86
    logic_and_to_bitws_xor = 87
    logic_and_to_bitws_lsh = 88
    logic_and_to_bitws_rsh = 89
    logic_and_to_logic_and = 90
    logic_and_to_logic_ior = 91
    logic_and_to_greater_tn = 92
    logic_and_to_greater_eq = 93
    logic_and_to_smaller_tn = 94
    logic_and_to_smaller_eq = 95
    logic_and_to_equal_with = 96
    logic_and_to_not_equals = 97

    logic_ior_to_arith_add = 80
    logic_ior_to_arith_sub = 81
    logic_ior_to_arith_mul = 82
    logic_ior_to_arith_div = 83
    logic_ior_to_arith_mod = 84
    logic_ior_to_bitws_and = 85
    logic_ior_to_bitws_ior = 86
    logic_ior_to_bitws_xor = 87
    logic_ior_to_bitws_lsh = 88
    logic_ior_to_bitws_rsh = 89
    logic_ior_to_logic_and = 90
    logic_ior_to_logic_ior = 91
    logic_ior_to_greater_tn = 92
    logic_ior_to_greater_eq = 93
    logic_ior_to_smaller_tn = 94
    logic_ior_to_smaller_eq = 95
    logic_ior_to_equal_with = 96
    logic_ior_to_not_equals = 97

    greater_tn_to_arith_add = 80
    greater_tn_to_arith_sub = 81
    greater_tn_to_arith_mul = 82
    greater_tn_to_arith_div = 83
    greater_tn_to_arith_mod = 84
    greater_tn_to_bitws_and = 85
    greater_tn_to_bitws_ior = 86
    greater_tn_to_bitws_xor = 87
    greater_tn_to_bitws_lsh = 88
    greater_tn_to_bitws_rsh = 89
    greater_tn_to_logic_and = 90
    greater_tn_to_logic_ior = 91
    greater_tn_to_greater_tn = 92
    greater_tn_to_greater_eq = 93
    greater_tn_to_smaller_tn = 94
    greater_tn_to_smaller_eq = 95
    greater_tn_to_equal_with = 96
    greater_tn_to_not_equals = 97

    greater_eq_to_arith_add = 80
    greater_eq_to_arith_sub = 81
    greater_eq_to_arith_mul = 82
    greater_eq_to_arith_div = 83
    greater_eq_to_arith_mod = 84
    greater_eq_to_bitws_and = 85
    greater_eq_to_bitws_ior = 86
    greater_eq_to_bitws_xor = 87
    greater_eq_to_bitws_lsh = 88
    greater_eq_to_bitws_rsh = 89
    greater_eq_to_logic_and = 90
    greater_eq_to_logic_ior = 91
    greater_eq_to_greater_tn = 92
    greater_eq_to_greater_eq = 93
    greater_eq_to_smaller_tn = 94
    greater_eq_to_smaller_eq = 95
    greater_eq_to_equal_with = 96
    greater_eq_to_not_equals = 97

    smaller_tn_to_arith_add = 80
    smaller_tn_to_arith_sub = 81
    smaller_tn_to_arith_mul = 82
    smaller_tn_to_arith_div = 83
    smaller_tn_to_arith_mod = 84
    smaller_tn_to_bitws_and = 85
    smaller_tn_to_bitws_ior = 86
    smaller_tn_to_bitws_xor = 87
    smaller_tn_to_bitws_lsh = 88
    smaller_tn_to_bitws_rsh = 89
    smaller_tn_to_logic_and = 90
    smaller_tn_to_logic_ior = 91
    smaller_tn_to_greater_tn = 92
    smaller_tn_to_greater_eq = 93
    smaller_tn_to_smaller_tn = 94
    smaller_tn_to_smaller_eq = 95
    smaller_tn_to_equal_with = 96
    smaller_tn_to_not_equals = 97

    smaller_eq_to_arith_add = 80
    smaller_eq_to_arith_sub = 81
    smaller_eq_to_arith_mul = 82
    smaller_eq_to_arith_div = 83
    smaller_eq_to_arith_mod = 84
    smaller_eq_to_bitws_and = 85
    smaller_eq_to_bitws_ior = 86
    smaller_eq_to_bitws_xor = 87
    smaller_eq_to_bitws_lsh = 88
    smaller_eq_to_bitws_rsh = 89
    smaller_eq_to_logic_and = 90
    smaller_eq_to_logic_ior = 91
    smaller_eq_to_greater_tn = 92
    smaller_eq_to_greater_eq = 93
    smaller_eq_to_smaller_tn = 94
    smaller_eq_to_smaller_eq = 95
    smaller_eq_to_equal_with = 96
    smaller_eq_to_not_equals = 97

    equal_with_to_arith_add = 80
    equal_with_to_arith_sub = 81
    equal_with_to_arith_mul = 82
    equal_with_to_arith_div = 83
    equal_with_to_arith_mod = 84
    equal_with_to_bitws_and = 85
    equal_with_to_bitws_ior = 86
    equal_with_to_bitws_xor = 87
    equal_with_to_bitws_lsh = 88
    equal_with_to_bitws_rsh = 89
    equal_with_to_logic_and = 90
    equal_with_to_logic_ior = 91
    equal_with_to_greater_tn = 92
    equal_with_to_greater_eq = 93
    equal_with_to_smaller_tn = 94
    equal_with_to_smaller_eq = 95
    equal_with_to_equal_with = 96
    equal_with_to_not_equals = 97

    not_equals_to_arith_add = 80
    not_equals_to_arith_sub = 81
    not_equals_to_arith_mul = 82
    not_equals_to_arith_div = 83
    not_equals_to_arith_mod = 84
    not_equals_to_bitws_and = 85
    not_equals_to_bitws_ior = 86
    not_equals_to_bitws_xor = 87
    not_equals_to_bitws_lsh = 88
    not_equals_to_bitws_rsh = 89
    not_equals_to_logic_and = 90
    not_equals_to_logic_ior = 91
    not_equals_to_greater_tn = 92
    not_equals_to_greater_eq = 93
    not_equals_to_smaller_tn = 94
    not_equals_to_smaller_eq = 95
    not_equals_to_equal_with = 96
    not_equals_to_not_equals = 97

    arith_add_assign_to_assign = 98
    arith_add_assign_to_arith_add_assign = 99
    arith_add_assign_to_arith_sub_assign = 100
    arith_add_assign_to_arith_mul_assign = 101
    arith_add_assign_to_arith_div_assign = 102
    arith_add_assign_to_arith_mod_assign = 103
    arith_add_assign_to_bitws_and_assign = 104
    arith_add_assign_to_bitws_ior_assign = 105
    arith_add_assign_to_bitws_xor_assign = 106
    arith_add_assign_to_bitws_lsh_assign = 107
    arith_add_assign_to_bitws_rsh_assign = 108

    arith_sub_assign_to_assign = 98
    arith_sub_assign_to_arith_add_assign = 99
    arith_sub_assign_to_arith_sub_assign = 100
    arith_sub_assign_to_arith_mul_assign = 101
    arith_sub_assign_to_arith_div_assign = 102
    arith_sub_assign_to_arith_mod_assign = 103
    arith_sub_assign_to_bitws_and_assign = 104
    arith_sub_assign_to_bitws_ior_assign = 105
    arith_sub_assign_to_bitws_xor_assign = 106
    arith_sub_assign_to_bitws_lsh_assign = 107
    arith_sub_assign_to_bitws_rsh_assign = 108

    arith_mul_assign_to_assign = 98
    arith_mul_assign_to_arith_add_assign = 99
    arith_mul_assign_to_arith_sub_assign = 100
    arith_mul_assign_to_arith_mul_assign = 101
    arith_mul_assign_to_arith_div_assign = 102
    arith_mul_assign_to_arith_mod_assign = 103
    arith_mul_assign_to_bitws_and_assign = 104
    arith_mul_assign_to_bitws_ior_assign = 105
    arith_mul_assign_to_bitws_xor_assign = 106
    arith_mul_assign_to_bitws_lsh_assign = 107
    arith_mul_assign_to_bitws_rsh_assign = 108

    arith_div_assign_to_assign = 98
    arith_div_assign_to_arith_add_assign = 99
    arith_div_assign_to_arith_sub_assign = 100
    arith_div_assign_to_arith_mul_assign = 101
    arith_div_assign_to_arith_div_assign = 102
    arith_div_assign_to_arith_mod_assign = 103
    arith_div_assign_to_bitws_and_assign = 104
    arith_div_assign_to_bitws_ior_assign = 105
    arith_div_assign_to_bitws_xor_assign = 106
    arith_div_assign_to_bitws_lsh_assign = 107
    arith_div_assign_to_bitws_rsh_assign = 108

    arith_mod_assign_to_assign = 98
    arith_mod_assign_to_arith_add_assign = 99
    arith_mod_assign_to_arith_sub_assign = 100
    arith_mod_assign_to_arith_mul_assign = 101
    arith_mod_assign_to_arith_div_assign = 102
    arith_mod_assign_to_arith_mod_assign = 103
    arith_mod_assign_to_bitws_and_assign = 104
    arith_mod_assign_to_bitws_ior_assign = 105
    arith_mod_assign_to_bitws_xor_assign = 106
    arith_mod_assign_to_bitws_lsh_assign = 107
    arith_mod_assign_to_bitws_rsh_assign = 108

    bitws_and_assign_to_assign = 109
    bitws_and_assign_to_arith_add_assign = 110
    bitws_and_assign_to_arith_sub_assign = 111
    bitws_and_assign_to_arith_mul_assign = 112
    bitws_and_assign_to_arith_div_assign = 113
    bitws_and_assign_to_arith_mod_assign = 114
    bitws_and_assign_to_bitws_and_assign = 115
    bitws_and_assign_to_bitws_ior_assign = 116
    bitws_and_assign_to_bitws_xor_assign = 117
    bitws_and_assign_to_bitws_lsh_assign = 118
    bitws_and_assign_to_bitws_rsh_assign = 119

    bitws_ior_assign_to_assign = 109
    bitws_ior_assign_to_arith_add_assign = 110
    bitws_ior_assign_to_arith_sub_assign = 111
    bitws_ior_assign_to_arith_mul_assign = 112
    bitws_ior_assign_to_arith_div_assign = 113
    bitws_ior_assign_to_arith_mod_assign = 114
    bitws_ior_assign_to_bitws_and_assign = 115
    bitws_ior_assign_to_bitws_ior_assign = 116
    bitws_ior_assign_to_bitws_xor_assign = 117
    bitws_ior_assign_to_bitws_lsh_assign = 118
    bitws_ior_assign_to_bitws_rsh_assign = 119

    bitws_xor_assign_to_assign = 109
    bitws_xor_assign_to_arith_add_assign = 110
    bitws_xor_assign_to_arith_sub_assign = 111
    bitws_xor_assign_to_arith_mul_assign = 112
    bitws_xor_assign_to_arith_div_assign = 113
    bitws_xor_assign_to_arith_mod_assign = 114
    bitws_xor_assign_to_bitws_and_assign = 115
    bitws_xor_assign_to_bitws_ior_assign = 116
    bitws_xor_assign_to_bitws_xor_assign = 117
    bitws_xor_assign_to_bitws_lsh_assign = 118
    bitws_xor_assign_to_bitws_rsh_assign = 119

    bitws_lsh_assign_to_assign = 109
    bitws_lsh_assign_to_arith_add_assign = 110
    bitws_lsh_assign_to_arith_sub_assign = 111
    bitws_lsh_assign_to_arith_mul_assign = 112
    bitws_lsh_assign_to_arith_div_assign = 113
    bitws_lsh_assign_to_arith_mod_assign = 114
    bitws_lsh_assign_to_bitws_and_assign = 115
    bitws_lsh_assign_to_bitws_ior_assign = 116
    bitws_lsh_assign_to_bitws_xor_assign = 117
    bitws_lsh_assign_to_bitws_lsh_assign = 118
    bitws_lsh_assign_to_bitws_rsh_assign = 119

    bitws_rsh_assign_to_assign = 109
    bitws_rsh_assign_to_arith_add_assign = 110
    bitws_rsh_assign_to_arith_sub_assign = 111
    bitws_rsh_assign_to_arith_mul_assign = 112
    bitws_rsh_assign_to_arith_div_assign = 113
    bitws_rsh_assign_to_arith_mod_assign = 114
    bitws_rsh_assign_to_bitws_and_assign = 115
    bitws_rsh_assign_to_bitws_ior_assign = 116
    bitws_rsh_assign_to_bitws_xor_assign = 117
    bitws_rsh_assign_to_bitws_lsh_assign = 118
    bitws_rsh_assign_to_bitws_rsh_assign = 119

    assign_to_arith_add_assign = 120
    assign_to_arith_sub_assign = 121
    assign_to_arith_mul_assign = 122
    assign_to_arith_div_assign = 123
    assign_to_arith_mod_assign = 124
    assign_to_bitws_and_assign = 125
    assign_to_bitws_ior_assign = 126
    assign_to_bitws_xor_assign = 127
    assign_to_bitws_lsh_assign = 128
    assign_to_bitws_rsh_assign = 129

    def __str__(self):
        return self.name

    @staticmethod
    def parse(text: str):
        return MutaOperator.__members__[text]


if __name__ == "__main__":
    operator = MutaOperator.parse("arith_add_to_arith_sub")
    print(operator.value)
