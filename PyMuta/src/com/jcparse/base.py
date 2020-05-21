"""
base.py defines the model for basic object in program analysis, including:
    --- CMetaType, CType
    --- SourceCode
    --- SymNode
"""

from enum import Enum


class CMetaType(Enum):
    """
    (void, bool, char, short, int, long, float, double, complex, imaginary, va_list)
    {array, pointer, function, struct, union}
    """
    VoidType = 0
    BoolType = 1
    CharType = 2
    ShortType = 3
    IntType = 4
    LongType = 5
    FloatType = 6
    DoubleType = 7
    ComplexType = 8
    ImaginaryType = 9
    VarListType = 10
    ArrayType = 11
    PointType = 12
    FunctionType = 13
    StructType = 14
    UnionType = 15

    def __str__(self):
        if self is CMetaType.VoidType:
            return "void"
        elif self is CMetaType.BoolType:
            return "bool"
        elif self is CMetaType.CharType:
            return "char"
        elif self is CMetaType.ShortType:
            return "short"
        elif self is CMetaType.IntType:
            return "int"
        elif self is CMetaType.LongType:
            return "long"
        elif self is CMetaType.FloatType:
            return "float"
        elif self is CMetaType.DoubleType:
            return "double"
        elif self is CMetaType.ComplexType:
            return "complex"
        elif self is CMetaType.ImaginaryType:
            return "imaginary"
        elif self is CMetaType.VarListType:
            return "va_list"
        elif self is CMetaType.ArrayType:
            return "array"
        elif self is CMetaType.PointType:
            return "pointer"
        elif self is CMetaType.FunctionType:
            return "function"
        elif self is CMetaType.StructType:
            return "struct"
        elif self is CMetaType.UnionType:
            return "union"
        else:
            return None


class CType:
    """
    data type in C program is a tree structural: meta_type, operands
    """
    def __init__(self, meta_type: CMetaType):
        self.meta_type = meta_type
        self.operands = list()
        return

    def get_meta_type(self):
        """
        :return: meta type of data type is referred to as element in CMetaType array
        """
        return self.meta_type

    def get_operands(self):
        """
        :return: get the operands to define the data type according to its meta type
        """
        return self.operands

    def __str__(self):
        if self.meta_type == CMetaType.VoidType:
            return "void"
        elif self.meta_type == CMetaType.BoolType:
            return "bool"
        elif self.meta_type == CMetaType.CharType:
            return "char"
        elif self.meta_type == CMetaType.ShortType:
            return "short"
        elif self.meta_type == CMetaType.IntType:
            return "int"
        elif self.meta_type == CMetaType.LongType:
            return "long"
        elif self.meta_type == CMetaType.FloatType:
            return "float"
        elif self.meta_type == CMetaType.DoubleType:
            return "double"
        elif self.meta_type == CMetaType.ComplexType:
            return "complex"
        elif self.meta_type == CMetaType.ImaginaryType:
            return "imaginary"
        elif self.meta_type == CMetaType.VarListType:
            return "va_list"
        elif self.meta_type == CMetaType.ArrayType:
            return str(self.operands[1]) + "[" + str(self.operands[0]) + "]"
        elif self.meta_type == CMetaType.PointType:
            return str(self.operands[0]) + "*"
        elif self.meta_type == CMetaType.FunctionType:
            return str(self.operands[0]) + "()"
        elif self.meta_type == CMetaType.StructType:
            return "struct " + str(self.operands[0])
        elif self.meta_type == CMetaType.UnionType:
            return "union " + str(self.operands[0])
        else:
            return None

    @staticmethod
    def parse(text: str):
        """
        translate the data type from text code file
        :param text:
        :return:
        """
        text = text.strip()
        if len(text) < 1:
            return None
        elif text[0] == '(' and text[len(text) - 1] == ')':
            text = text[1:len(text) - 1].strip()
            index = text.index(" ")
            name = text[0:index].strip()
            text = text[index + 1:].strip()
            if name == "array":
                index2 = text.index(" ")
                length = int(text[0:index2].strip())
                child = CType.parse(text[index2 + 1:].strip())
                parent = CType(CMetaType.ArrayType)
                parent.operands.append(length)
                parent.operands.append(child)
                return parent
            elif name == "pointer":
                child = CType.parse(text)
                parent = CType(CMetaType.PointType)
                parent.operands.append(child)
                return parent
            elif name == "function":
                child = CType.parse(text)
                parent = CType(CMetaType.FunctionType)
                parent.operands.append(child)
                return parent
            elif name == "struct":
                parent = CType(CMetaType.StructType)
                parent.operands.append(text)
                return parent
            elif name == "union":
                parent = CType(CMetaType.UnionType)
                parent.operands.append(text)
                return parent
            else:
                return None
        else:
            if text == "void":
                return CType(CMetaType.VoidType)
            elif text == "bool":
                return CType(CMetaType.BoolType)
            elif text == "char":
                return CType(CMetaType.CharType)
            elif text == "short":
                return CType(CMetaType.ShortType)
            elif text == "int":
                return CType(CMetaType.IntType)
            elif text == "long":
                return CType(CMetaType.LongType)
            elif text == "float":
                return CType(CMetaType.FloatType)
            elif text == "double":
                return CType(CMetaType.DoubleType)
            elif text == "complex":
                return CType(CMetaType.ComplexType)
            elif text == "imaginary":
                return CType(CMetaType.ImaginaryType)
            elif text == "va_list":
                return CType(CMetaType.VarListType)
            else:
                return None

    def is_basic_type(self):
        return self.meta_type == CMetaType.VoidType or self.meta_type == CMetaType.BoolType or \
               self.meta_type == CMetaType.CharType or self.meta_type == CMetaType.ShortType or \
               self.meta_type == CMetaType.IntType or self.meta_type == CMetaType.FloatType or \
               self.meta_type == CMetaType.DoubleType or self.meta_type == CMetaType.ComplexType or \
               self.meta_type == CMetaType.ImaginaryType or self.meta_type == CMetaType.VarListType

    def is_address_type(self):
        return self.meta_type == CMetaType.PointType or self.meta_type == CMetaType.ArrayType

    def is_void_type(self):
        return self.meta_type == CMetaType.VoidType

    def is_bool_type(self):
        return self.meta_type == CMetaType.BoolType

    def is_integer_type(self):
        return self.meta_type == CMetaType.BoolType or self.meta_type == CMetaType.CharType \
               or self.meta_type == CMetaType.ShortType or self.meta_type == CMetaType.IntType

    def is_real_type(self):
        return self.meta_type == CMetaType.FloatType or self.meta_type == CMetaType.DoubleType


class COperator(Enum):
    assign = 0
    arith_add = 1
    arith_sub = 2
    arith_mul = 3
    arith_div = 4
    arith_mod = 5
    arith_add_assign = 6
    arith_sub_assign = 7
    arith_mul_assign = 8
    arith_div_assign = 9
    arith_mod_assign = 10
    bitws_and = 11
    bitws_ior = 12
    bitws_xor = 13
    bitws_lsh = 14
    bitws_rsh = 15
    bitws_and_assign = 16
    bitws_ior_assign = 17
    bitws_xor_assign = 18
    bitws_lsh_assign = 19
    bitws_rsh_assign = 20
    logic_and = 21
    logic_ior = 22
    greater_tn = 23
    greater_eq = 24
    smaller_tn = 25
    smaller_eq = 26
    equal_with = 27
    not_equals = 28
    increment = 29
    decrement = 30
    positive = 31
    negative = 32
    address_of = 33
    dereference = 34
    bitws_rsv = 35
    logic_not = 36
    field_of = 37
    invocate = 38

    def __str__(self):
        if self == COperator.assign:
            return "="
        elif self == COperator.arith_add:
            return "+"
        elif self == COperator.arith_sub:
            return "-"
        elif self == COperator.arith_mul:
            return "*"
        elif self == COperator.arith_div:
            return "/"
        elif self == COperator.arith_mod:
            return "%"
        elif self == COperator.arith_add_assign:
            return "+="
        elif self == COperator.arith_sub_assign:
            return "-="
        elif self == COperator.arith_mul_assign:
            return "*="
        elif self == COperator.arith_div_assign:
            return "/="
        elif self == COperator.arith_mod_assign:
            return "%="
        elif self == COperator.bitws_and:
            return "&"
        elif self == COperator.bitws_ior:
            return "|"
        elif self == COperator.bitws_xor:
            return "^"
        elif self == COperator.bitws_lsh:
            return "<<"
        elif self == COperator.bitws_rsh:
            return ">>"
        elif self == COperator.bitws_and_assign:
            return "&="
        elif self == COperator.bitws_ior_assign:
            return "|="
        elif self == COperator.bitws_xor_assign:
            return "^="
        elif self == COperator.bitws_lsh_assign:
            return "<<="
        elif self == COperator.bitws_rsh_assign:
            return ">>="
        elif self == COperator.logic_and:
            return "&&"
        elif self == COperator.logic_ior:
            return "||"
        elif self == COperator.logic_not:
            return "!"
        elif self == COperator.positive:
            return "+"
        elif self == COperator.negative:
            return "-"
        elif self == COperator.address_of:
            return "&"
        elif self == COperator.dereference:
            return "*"
        elif self == COperator.field_of:
            return "."
        elif self == COperator.invocate:
            return "call"
        elif self == COperator.greater_tn:
            return ">"
        elif self == COperator.greater_eq:
            return ">="
        elif self == COperator.smaller_tn:
            return "<"
        elif self == COperator.smaller_eq:
            return "<="
        elif self == COperator.not_equals:
            return "!="
        elif self == COperator.equal_with:
            return "=="
        elif self == COperator.increment:
            return "++"
        elif self == COperator.decrement:
            return "--"
        elif self == COperator.bitws_rsv:
            return "~"
        else:
            return None

    @staticmethod
    def get_operator_of(text: str):
        if text == "assign":
            return COperator.assign
        elif text == "arith_add":
            return COperator.arith_add
        elif text == "arith_sub":
            return COperator.arith_sub
        elif text == "arith_mul":
            return COperator.arith_mul
        elif text == "arith_div":
            return COperator.arith_div
        elif text == "arith_mod":
            return COperator.arith_mod
        elif text == "arith_add_assign":
            return COperator.arith_add_assign
        elif text == "arith_sub_assign":
            return COperator.arith_sub_assign
        elif text == "arith_mul_assign":
            return COperator.arith_mul_assign
        elif text == "arith_div_assign":
            return COperator.arith_div_assign
        elif text == "arith_mod_assign":
            return COperator.arith_mod_assign
        elif text == "bit_and":
            return COperator.bitws_and
        elif text == "bit_or":
            return COperator.bitws_ior
        elif text == "bit_xor":
            return COperator.bitws_xor
        elif text == "left_shift":
            return COperator.bitws_lsh
        elif text == "righ_shift":
            return COperator.bitws_rsh
        elif text == "bit_and_assign":
            return COperator.bitws_and_assign
        elif text == "bit_or_assign":
            return COperator.bitws_ior_assign
        elif text == "bit_xor_assign":
            return COperator.bitws_xor_assign
        elif text == "left_shift_assign":
            return COperator.bitws_lsh_assign
        elif text == "righ_shift_assign":
            return COperator.bitws_rsh_assign
        elif text == "logic_and":
            return COperator.logic_and
        elif text == "logic_or":
            return COperator.logic_ior
        elif text == "logic_not":
            return COperator.logic_not
        elif text == "greater_tn":
            return COperator.greater_tn
        elif text == "greater_eq":
            return COperator.greater_eq
        elif text == "smaller_tn":
            return COperator.smaller_tn
        elif text == "smaller_eq":
            return COperator.smaller_eq
        elif text == "equal_with":
            return COperator.equal_with
        elif text == "not_equals":
            return COperator.not_equals
        elif text == "increment":
            return COperator.increment
        elif text == "decrement":
            return COperator.decrement
        elif text == "positive":
            return COperator.positive
        elif text == "negative":
            return COperator.negative
        elif text == "address_of":
            return COperator.address_of
        elif text == "dereference":
            return COperator.dereference
        elif text == "bit_not":
            return COperator.bitws_rsv
        elif text == "dot" or text == "arrow":
            return COperator.field_of
        else:
            return None


class CSymbolType(Enum):
    """
    expression
    |-- basic_expression
    |-- |-- address
    |-- |-- constant
    |-- |-- default_value
    |-- |-- literal
    |-- binary_expression {-, /, %, <<, >>, <, <=, >, >=, ==, !=}
    |-- multi_expression {+, *, &, |, ^, &&, ||}
    |-- unary_expression {pos, neg, ~, !, &, *, =}
    |-- field_expression
    |-- invoc_expression
    |-- sequence_expression
    field + argument_list
    """
    Address = 0
    Constant = 1
    DefaultValue = 2
    Literal = 3
    BinaryExpression = 4
    MultiExpression = 5
    UnaryExpression = 6
    FieldExpression = 7
    CallExpression = 8
    SequenceExpression = 9
    Field = 10
    ArgumentList = 11


class CSymbolNode:
    """
    To describe symbolic expression as [sym_type, data_type, content, parent, children]
    """

    def __init__(self, sym_type: CSymbolType, data_type: CType, content):
        """
        create an isolated tree node in symbolic expression
        :param sym_type:
        :param data_type:
        :param content:
        """
        self.sym_type = sym_type
        self.data_type = data_type
        self.content = content
        self.parent = None
        self.children = list()
        return

    def is_root(self):
        """
        :return: true if the expression node is in root
        """
        return self.parent is None

    def is_leaf(self):
        """
        :return: true if the expression contains no child
        """
        return len(self.children) == 0

    def get_parent(self):
        """
        :return: get the parent of the node in tree
        """
        return self.parent

    def get_children(self):
        """
        :return: the children belonging to this node
        """
        return self.children

    def get_child(self, k: int):
        """
        :param k:
        :return: the kth child under the node
        """
        return self.children[k]

    def get_value(self):
        """
        :return: constant {bool|int|double}; address literal default_value, field {string}
        """
        if (self.sym_type == CSymbolType.Address) or (self.sym_type == CSymbolType.Constant) or \
                (self.sym_type == CSymbolType.Literal) or (self.sym_type == CSymbolType.DefaultValue) or \
                (self.sym_type == CSymbolType.Field):
            return str(self.content)
        else:
            return None

    def get_operator(self):
        """
        :return: binary | multiple | unary operator as COperator
        """
        if (self.sym_type == CSymbolType.BinaryExpression) or (self.sym_type == CSymbolType.MultiExpression) or \
                (self.sym_type == CSymbolType.UnaryExpression):
            return self.content
        else:
            return None

    def __str__(self):
        if (self.sym_type == CSymbolType.Address) or (self.sym_type == CSymbolType.Constant) or \
                (self.sym_type == CSymbolType.Literal) or (self.sym_type == CSymbolType.DefaultValue) or \
                (self.sym_type == CSymbolType.Field):
            return str(self.content)
        elif (self.sym_type == CSymbolType.BinaryExpression) or (self.sym_type == CSymbolType.MultiExpression) or \
                (self.sym_type == CSymbolType.UnaryExpression):
            buffer = "("
            buffer += str(self.content)
            for child in self.children:
                buffer += " " + str(child)
            buffer += ")"
            return buffer
        elif self.sym_type == CSymbolType.FieldExpression:
            buffer = "(field_of"
            for child in self.children:
                buffer += " " + str(child)
            buffer += ")"
            return buffer
        elif self.sym_type == CSymbolType.CallExpression:
            buffer = "(call"
            for child in self.children:
                buffer += " " + str(child)
            buffer += ")"
            return buffer
        elif self.sym_type == CSymbolType.SequenceExpression:
            buffer = "{"
            for child in self.children:
                buffer += " " + str(child)
            buffer += " }"
            return buffer
        else:
            buffer = "("
            first = True
            for child in self.children:
                if first:
                    first = False
                else:
                    buffer += " "
                buffer += str(child)
            buffer += ")"
            return buffer

    def get_root_of(self):
        """
        :return: root of the tree of the node
        """
        root = self
        while root.parent is not None:
            root = root.parent
        return root

    @staticmethod
    def __get_sym_type__(class_name: str):
        if class_name == "SymAddress":
            return CSymbolType.Address
        elif class_name == "SymConstant":
            return CSymbolType.Constant
        elif class_name == "SymLiteral":
            return CSymbolType.Literal
        elif class_name == "SymDefaultValue":
            return CSymbolType.DefaultValue
        elif class_name == "SymField":
            return CSymbolType.Field
        elif class_name == "SymArgumentList":
            return CSymbolType.ArgumentList
        elif class_name == "SymBinaryExpression":
            return CSymbolType.BinaryExpression
        elif class_name == "SymMultiExpression":
            return CSymbolType.MultiExpression
        elif class_name == "SymUnaryExpression":
            return CSymbolType.UnaryExpression
        elif class_name == "SymFieldExpression":
            return CSymbolType.FieldExpression
        elif class_name == "SymInvocateExpression":
            return CSymbolType.CallExpression
        elif class_name == "SymSequenceExpression":
            return CSymbolType.SequenceExpression
        else:
            return None

    @staticmethod
    def parse(lines):
        """
        [sym] id sym_type data_type
        :param lines:
        :return:
        """
        nodes = dict()
        for line in lines:
            line: str
            items = line.strip().split('\t')
            key = int(items[1].strip())
            sym_type = CSymbolNode.__get_sym_type__(items[2].strip())
            data_type = CType.parse(items[3].strip())
            content = get_content_of(items[4].strip())
            node = CSymbolNode(sym_type, data_type, content)
            nodes[key] = node
            ''' generate the operator as content of each expression node '''
            if sym_type == CSymbolType.BinaryExpression or sym_type == CSymbolType.MultiExpression \
                    or sym_type == CSymbolType.UnaryExpression:
                node.content = COperator.get_operator_of(get_content_of(items[4].strip()))
            elif sym_type == CSymbolType.FieldExpression:
                node.content = COperator.field_of
            elif sym_type == CSymbolType.CallExpression:
                node.content = COperator.invocate
        root = None
        for line in lines:
            line: str
            items = line.strip().split('\t')
            key = int(items[1].strip())
            parent = nodes[key]
            if root is None:
                root = parent
            for k in range(5, len(items)):
                child = nodes[int(items[k].strip())]
                parent: CSymbolNode
                parent.children.append(child)
        return root

    def generate_code(self, simplified=False):
        if self.sym_type == CSymbolType.Address:
            name = str(self.content)
            if simplified and '#' in name:
                index = name.index('#')
                if index > 0:
                    name = name[0:index].strip()
            return name
        elif self.sym_type == CSymbolType.Constant:
            return str(self.content)
        elif self.sym_type == CSymbolType.Literal:
            buffer = "\""
            text = str(self.content)
            for k in range(0, len(text)):
                char = text[k]
                if char.isspace():
                    buffer += "\\s"
                else:
                    buffer += char
            buffer += "\""
            return buffer
        elif self.sym_type == CSymbolType.DefaultValue:
            return "[?]"
        elif self.sym_type == CSymbolType.Field:
            return str(self.content)
        elif self.sym_type == CSymbolType.BinaryExpression or self.sym_type == CSymbolType.MultiExpression:
            return "(" + self.children[0].generate_code(simplified) + ") " + str(self.content) + \
                   " (" + self.children[1].generate_code(simplified) + ")"
        elif self.sym_type == CSymbolType.UnaryExpression:
            return str(self.content) + "(" + self.children[0].generate_code(simplified) + ")"
        elif self.sym_type == CSymbolType.FieldExpression:
            return "(" + self.children[0].generate_code(simplified) + ")." + self.children[1].generate_code(simplified)
        elif self.sym_type == CSymbolType.CallExpression:
            return self.children[0].generate_code(simplified) + self.children[1].generate_code(simplified)
        elif self.sym_type == CSymbolType.ArgumentList:
            buffer = "("
            for child in self.children:
                buffer += " " + child.generate_code(simplified)
            buffer += " )"
            return buffer
        elif self.sym_type == CSymbolType.SequenceExpression:
            buffer = "["
            for child in self.children:
                buffer += " " + child.generate_code(simplified)
            buffer += " ]"
            return buffer
        else:
            return None


class CKeyword(Enum):
    auto = 0
    _break = 1
    case = 2
    char = 3
    const = 4
    _continue = 5
    default = 6
    do = 7
    double = 8
    _else = 9
    enum = 10
    extern = 11
    float = 12
    _for = 13
    goto = 14
    _if = 15
    int = 16
    long = 17
    register = 18
    _return = 19
    short = 20
    signed = 21
    sizeof = 22
    static = 23
    struct = 24
    switch = 25
    typedef = 26
    union = 27
    unsigned = 28
    void = 29
    volatile = 30
    _while = 31
    inline = 32
    restrict = 33
    bool = 34
    complex = 35
    imaginary = 36
    typeof = 37
    va_list = 38

    def __str__(self):
        name = self.name
        if name[0] == '_':
            name = name[1:]
        return name

    @staticmethod
    def parse(text: str):
        if text == "auto" or text == "c89_auto":
            return CKeyword.auto
        elif text == "c89_break" or text == "break":
            return CKeyword._break
        elif text == "c89_case" or text == "case":
            return CKeyword.case
        elif text == "c89_char" or text == "char":
            return CKeyword.char
        elif text == "c89_const" or text == "const":
            return CKeyword.const
        elif text == "c89_continue" or text == "continue":
            return CKeyword._continue
        elif text == "c89_default" or text == "default":
            return CKeyword.default
        elif text == "c89_do" or text == "do":
            return CKeyword.do
        elif text == "c89_double" or text == "double":
            return CKeyword.double
        elif text == "c89_else" or text == "else":
            return CKeyword._else
        elif text == "c89_enum" or text == "enum":
            return CKeyword.enum
        elif text == "c89_extern" or text == "extern":
            return CKeyword.extern
        elif text == "c89_float" or text == "float":
            return CKeyword.float
        elif text == "c89_for" or text == "for":
            return CKeyword._for
        elif text == "c89_goto" or text == "goto":
            return CKeyword.goto
        elif text == "c89_if" or text == "if":
            return CKeyword._if
        elif text == "c89_int" or text == "int":
            return CKeyword.int
        elif text == "c89_long" or text == "long":
            return CKeyword.long
        elif text == "c89_register" or text == "register":
            return CKeyword.register
        elif text == "c89_return" or text == "return":
            return CKeyword._return
        elif text == "c89_short" or text == "short":
            return CKeyword.short
        elif text == "c89_signed" or text == "signed":
            return CKeyword.signed
        elif text == "c89_sizeof" or text == "sizeof":
            return CKeyword.sizeof
        elif text == "c89_static" or text == "static":
            return CKeyword.static
        elif text == "c89_struct" or text == "struct":
            return CKeyword.struct
        elif text == "c89_switch" or text == "switch":
            return CKeyword.switch
        elif text == "c89_typedef" or text == "typedef":
            return CKeyword.typedef
        elif text == "c89_union" or text == "union":
            return CKeyword.union
        elif text == "c89_unsigned" or text == "unsigned":
            return CKeyword.unsigned
        elif text == "c89_void" or text == "void":
            return CKeyword.void
        elif text == "c89_volatile" or text == "volatile":
            return CKeyword.volatile
        elif text == "c89_while" or text == "while":
            return CKeyword._while
        elif text == "c99_inline" or text == "inline":
            return CKeyword.inline
        elif text == "c99_restrict" or text == "restrict":
            return CKeyword.restrict
        elif text == "c99_bool" or text == "bool":
            return CKeyword.bool
        elif text == "c99_complex" or text == "complex":
            return CKeyword.complex
        elif text == "c99_imaginary" or text == "imaginary":
            return CKeyword.imaginary
        elif text == "gnu_builtin_va_list":
            return CKeyword.va_list
        elif text == "gnu_typeof":
            return CKeyword.typeof
        else:
            return None


class CPunctuate(Enum):
    left_bracket = 0
    right_bracket = 1
    left_paranth = 2
    right_paranth = 3
    left_brace = 4
    right_brace = 5
    comma = 6
    semicolon = 7
    colon = 8
    ellipsis = 9
    question = 10
    hash = 11
    hashhash = 12

    def __str__(self):
        if self == CPunctuate.left_brace:
            return "{"
        elif self == CPunctuate.right_brace:
            return "}"
        elif self == CPunctuate.left_bracket:
            return "["
        elif self == CPunctuate.right_bracket:
            return "]"
        elif self == CPunctuate.left_paranth:
            return "("
        elif self == CPunctuate.right_paranth:
            return ")"
        elif self == CPunctuate.comma:
            return ","
        elif self == CPunctuate.semicolon:
            return ";"
        elif self == CPunctuate.colon:
            return ":"
        elif self == CPunctuate.ellipsis:
            return "..."
        elif self == CPunctuate.question:
            return "?"
        elif self == CPunctuate.hash:
            return "#"
        elif self == CPunctuate.hashhash:
            return "##"
        else:
            return None

    @staticmethod
    def parse(text: str):
        if text == "left_bracket":
            return CPunctuate.left_bracket
        elif text == "right_bracket":
            return CPunctuate.right_bracket
        elif text == "left_paranth":
            return CPunctuate.left_paranth
        elif text == "right_paranth":
            return CPunctuate.right_paranth
        elif text == "left_brace":
            return CPunctuate.left_brace
        elif text == "right_brace":
            return CPunctuate.right_brace
        elif text == "comma":
            return CPunctuate.comma
        elif text == "colon":
            return CPunctuate.colon
        elif text == "semicolon":
            return CPunctuate.semicolon
        elif text == "ellipsis":
            return CPunctuate.ellipsis
        elif text == "question":
            return CPunctuate.question
        elif text == "hash":
            return CPunctuate.hash
        elif text == "hashhash":
            return CPunctuate.hashhash
        else:
            return None


def get_content_of(text: str):
    """
    extract the data from text as:
    ast@identifier
    cir@identifier
    bool@value
    int@value
    double@value
    string@value
    ins@hashcode
    :param text:
    :return: None if cannot be interpreted
    """
    if '@' in text:
        index = text.index('@')
        name = text[0:index].strip()
        value = text[index+1:].strip()
        if name == "ast":
            value = int(value)
            return value
        elif name == "cir":
            value = int(value)
            return value
        elif name == "ins":
            value = int(value)
            return value
        elif name == "bool":
            if value == "true":
                return True
            else:
                return False
        elif name == "int":
            return int(value)
        elif name == "double":
            return float(value)
        elif name == "string":
            return value
        else:
            return None
    else:
        return None


if __name__ == "__main__":
    lines = list()
    lines.append("[sym]\t1060229007\tSymBinaryExpression\tbool\tstring@greater_tn\t678565780\t889659405")
    lines.append("[sym]\t678565780\tSymMultiExpression\tint\tstring@arith_mul\t1303448479\t2085480465")
    lines.append("[sym]\t1303448479\tSymConstant\tlong\tint@-1")
    lines.append("[sym]\t2085480465\tSymUnaryExpression\tint\tstring@dereference\t1144757947")
    lines.append("[sym]\t1144757947\tSymAddress\t(pointer int)\tstring@line#589058590")
    lines.append("[sym]\t889659405\tSymConstant\tlong\tint@0")
    node = CSymbolNode.parse(lines)
    print(node)
    print(len(node.children))
    print(CType.parse("(function (pointer (pointer (array 27 short))))"))
