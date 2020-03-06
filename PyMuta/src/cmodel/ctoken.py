"""
ctoken.py defines the keyword in C programming language.
"""

c_keywords = {
    'c89_break', 'c89_auto', 'c89_case', 'c89_char', 'c89_const', 'c89_continue', 'c89_default', 'c89_do',
    'c89_double', 'c89_else', 'c89_enum', 'c89_extern', 'c89_float', 'c89_for', 'c89_for', 'c89_goto', 'c89_if',
    'c89_int', 'c89_long', 'c89_register', 'c89_return', 'c89_short', 'c89_signed', 'c89_sizeof', 'c89_static',
    'c89_struct', 'c89_switch', 'c89_typedef', 'c89_union', 'c89_unsigned', 'c89_void', 'c89_volatile', 'c89_while',
    'c99_inline', 'c99_restrict', 'c99_bool', 'c99_complex', 'c99_imaginary',
    'gnu_function', 'gnu_pretty_function', 'gnu_alignof', 'gnu_asm', 'gnu_attribute', 'gnu_builtin_offsetof',
    'gnu_builtin_va_arg', 'gnu_builtin_va_list', 'gnu_extension', 'gnu_func', 'gnu_label', 'gnu_null', 'gnu_real',
    'gnu_typeof', 'gnu_thread'
}

c_operators = {
    'assign', 'arith_add', 'arith_sub', 'arith_mul', 'arith_div', 'arith_mod', 'arith_add_assign',
    'arith_sub_assign', 'arith_mul_assign', 'arith_div_assign', 'arith_mod_assign', 'increment',
    'decrement', 'positive', 'negative', 'address_of', 'dereference', 'left_shift', 'righ_shift',
    'left_shift_assign', 'righ_shift_assign', 'greater_tn', 'greater_eq', 'smaller_tn', 'smaller_eq',
    'equal_with', 'not_equals', 'logic_and', 'logic_or', 'logic_not', 'bit_not', 'bit_and', 'bit_or',
    'bit_xor', 'bit_and_assign', 'bit_or_assign', 'bit_xor_assign'
}

c_punctuates = {
    'lex_error', 'left_bracket', 'right_bracket', 'left_paranth', 'right_paranth', 'left_brace', 'right_brace',
    'dot', 'arrow', 'increment', 'decrement', 'bit_not', 'bit_and', 'bit_or', 'bit_xor', 'ari_add', 'ari_sub',
    'ari_mul', 'ari_div', 'ari_mod', 'log_and', 'log_or', 'log_or', 'log_not', 'left_shift', 'right_shift',
    'left_shift_assign', 'right_shift_assign', 'greater_tn', 'greater_eq', 'smaller_tn', 'smaller_eq', 'equal_with',
    'not_equals', 'ari_add_assign', 'ari_sub_assign', 'ari_mul_assign', 'ari_div_assign', 'ari_mod_assign',
    'bit_and_assign', 'bit_or_assign', 'bit_xor_assign', 'comma', 'semicolon', 'colon', 'ellipsis', 'question',
    'assign', 'hash', 'hash_hash'
}

c_keywords_dict = dict()

c_operators_dict = dict()

c_punctuates_dict = dict()


class CToken:
    """
    Token can be keyword(k), operator(o), punctuate(p), constant(b|f), identifier(n)
    """

    def __init__(self, category: str, value):
        self.category = category
        self.value = value
        return

    def is_keyword(self):
        return self.category == 'k'

    def is_operator(self):
        return self.category == 'o'

    def is_punctuate(self):
        return self.category == 'p'

    def is_identifier(self):
        return self.category == 'n'

    def is_boolean(self):
        return self.category == 'b'

    def is_float(self):
        return self.category == 'f'

    def get_value(self):
        return self.value

    def __str__(self):
        return str(self.value)

    @staticmethod
    def get_token(text: str):
        '''
        get token representing keyword, operator, punctuate, identifier
        :param text:
        :return:
        '''
        if text in c_keywords:
            if text not in c_keywords_dict:
                c_keywords_dict[text] = CToken('k', text)
            return c_keywords_dict[text]
        elif text in c_operators:
            if text not in c_operators_dict:
                c_operators_dict[text] = CToken('o', text)
            return c_operators_dict[text]
        elif text in c_punctuates:
            if text not in c_punctuates_dict:
                c_punctuates_dict[text] = CToken('p', text)
            return c_punctuates_dict[text]
        else:
            return CToken('n', text)

    @staticmethod
    def get_constant(text: str):
        if text == 'true':
            return CToken('b', True)
        elif text == 'false':
            return CToken('b', False)
        else:
            return CToken('f', float(text))

    @staticmethod
    def load_keywords():
        """
        load all the keywords, operators and punctuates in the dictionary
        :return:
        """
        for name in c_keywords:
            CToken.get_token(name)
        for name in c_punctuates:
            CToken.get_token(name)
        for name in c_operators:
            CToken.get_token(name)
        return

