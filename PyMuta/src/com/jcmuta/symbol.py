from enum import Enum
import random
import src.com.jcparse.base as base
import src.com.jcparse.cirtree as cirtree


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
    # basic node
    Address = 0
    Constant = 1
    DefaultValue = 2
    Literal = 3
    Field = 10
    # compositional expression
    BinaryExpression = 4
    MultiExpression = 5
    UnaryExpression = 6
    # special expressions
    FieldExpression = 7
    SequenceExpression = 9
    # calling expression
    CallExpression = 8
    ArgumentList = 11

    @staticmethod
    def parse(class_name: str):
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


class CSymbolNode:
    """
    To describe symbolic expression as [sym_type, data_type, content, parent, children]
    """

    def __init__(self, sym_type: CSymbolType, data_type: base.CType, content):
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
        child = self.children[k]
        child: CSymbolNode
        return child

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

    def add_child(self, child):
        child: CSymbolNode
        child.parent = self
        self.children.append(child)
        return

    def get_data_type(self):
        return self.data_type

    def clone(self):
        """
        :return: get the clone of the symbolic description
        """
        copy = CSymbolNode(self.sym_type, self.data_type, self.content)
        for child in self.children:
            copy.add_child(child.clone())
        return copy


def parse_from_text_lines(lines: list):
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
        sym_type = CSymbolType.parse(items[2].strip())
        data_type = base.CType.parse(items[3].strip())
        content = base.get_content_of(items[4].strip())
        node = CSymbolNode(sym_type, data_type, content)
        nodes[key] = node
        ''' generate the operator as content of each expression node '''
        if sym_type == CSymbolType.BinaryExpression or sym_type == CSymbolType.MultiExpression \
                or sym_type == CSymbolType.UnaryExpression:
            node.content = base.COperator.get_operator_of(base.get_content_of(items[4].strip()))
        elif sym_type == CSymbolType.FieldExpression:
            node.content = base.COperator.field_of
        elif sym_type == CSymbolType.CallExpression:
            node.content = base.COperator.invocate
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
            parent.add_child(child)
    return root


def parse_from_cir_node(source: cirtree.CirNode):
    """
    :param source:
    :return: symbolic representation of cir source node or none
    """
    if source.cir_type == cirtree.CirType.argument_list:
        target = CSymbolNode(CSymbolType.ArgumentList, base.CType(base.CMetaType.VoidType), None)
        for child in source.get_children():
            child: cirtree.CirNode
            target_child = parse_from_cir_node(child)
            target.add_child(target_child)
    elif source.cir_type == cirtree.CirType.field:
        target = CSymbolNode(CSymbolType.Field, source.get_data_type(), source.content)
    elif source.cir_type == cirtree.CirType.defer_expression:
        target = CSymbolNode(CSymbolType.UnaryExpression, source.get_data_type(), base.COperator.dereference)
        target.add_child(parse_from_cir_node(source.get_child(0)))
    elif source.cir_type == cirtree.CirType.field_expression:
        target = CSymbolNode(CSymbolType.FieldExpression, source.get_data_type(), None)
        target.add_child(parse_from_cir_node(source.get_child(0)))
        target.add_child(parse_from_cir_node(source.get_child(1)))
    elif source.cir_type == cirtree.CirType.declarator or source.cir_type == cirtree.CirType.implicator or \
            source.cir_type == cirtree.CirType.identifier or source.cir_type == cirtree.CirType.return_point:
        pointer_type = base.CType(base.CMetaType.PointType)
        pointer_type.operands.append(source.get_data_type())
        target_address = CSymbolNode(CSymbolType.Address, pointer_type, source.content)
        target = CSymbolNode(CSymbolType.UnaryExpression, source.get_data_type(), None)
        target.add_child(target_address)
    elif source.cir_type == cirtree.CirType.address_expression:
        target = CSymbolNode(CSymbolType.UnaryExpression, source.get_data_type(), base.COperator.address_of)
        target.add_child(parse_from_cir_node(source.get_child(0)))
    elif source.cir_type == cirtree.CirType.cast_expression:
        target = CSymbolNode(CSymbolType.UnaryExpression, source.get_data_type(), base.COperator.assign)
        target.add_child(parse_from_cir_node(source.get_child(1)))
    elif source.cir_type == cirtree.CirType.constant:
        target = CSymbolNode(CSymbolType.Constant, source.get_data_type(), source.content)
    elif source.cir_type == cirtree.CirType.default_value:
        target = CSymbolNode(CSymbolType.DefaultValue, source.get_data_type(), None)
    elif source.cir_type == cirtree.CirType.literal:
        target = CSymbolNode(CSymbolType.Literal, source.get_data_type(), source.content)
    elif source.cir_type == cirtree.CirType.initializer_body:
        target = CSymbolNode(CSymbolType.SequenceExpression, source.get_data_type(), None)
        for child in source.get_children():
            target.add_child(parse_from_cir_node(child))
    elif source.cir_type == cirtree.CirType.wait_expression:
        target = CSymbolNode(CSymbolType.CallExpression, source.get_data_type(), None)
        target.add_child(parse_from_cir_node(source.get_child(0)))
    elif source.cir_type == cirtree.CirType.arith_expression:
        operator = source.get_operator()
        if operator == base.COperator.negative:
            target = CSymbolNode(CSymbolType.UnaryExpression, source.get_data_type(), operator)
            target.add_child(parse_from_cir_node(source.get_child(0)))
        elif operator == base.COperator.positive:
            target = parse_from_cir_node(source.get_child(0))
        elif operator == base.COperator.arith_add or operator == base.COperator.arith_mul:
            target = CSymbolNode(CSymbolType.MultiExpression, source.get_data_type(), operator)
            for child in source.get_children():
                target.add_child(parse_from_cir_node(child))
        else:
            target = CSymbolNode(CSymbolType.BinaryExpression, source.get_data_type(), operator)
            target.add_child(parse_from_cir_node(source.get_child(0)))
            target.add_child(parse_from_cir_node(source.get_child(1)))
    elif source.cir_type == cirtree.CirType.bitws_expression:
        operator = source.get_operator()
        if operator == base.COperator.bitws_rsv:
            target = CSymbolNode(CSymbolType.UnaryExpression, source.get_data_type(), operator)
            target.add_child(parse_from_cir_node(source.get_child(0)))
        elif operator == base.COperator.bitws_and or operator == base.COperator.bitws_ior or \
                operator == base.COperator.bitws_xor:
            target = CSymbolNode(CSymbolType.MultiExpression, source.get_data_type(), operator)
            for child in source.get_children():
                target.add_child(parse_from_cir_node(child))
        else:
            target = CSymbolNode(CSymbolType.BinaryExpression, source.get_data_type(), operator)
            target.add_child(parse_from_cir_node(source.get_child(0)))
            target.add_child(parse_from_cir_node(source.get_child(1)))
    elif source.cir_type == cirtree.CirType.logic_expression:
        operator = source.get_operator()
        if operator == base.COperator.logic_not:
            target = CSymbolNode(CSymbolType.UnaryExpression, source.get_data_type(), operator)
            target.add_child(parse_from_cir_node(source.get_child(0)))
        else:
            target = CSymbolNode(CSymbolType.MultiExpression, source.get_data_type(), operator)
            for child in source.get_children():
                target.add_child(parse_from_cir_node(child))
    elif source.cir_type == cirtree.CirType.relational_expression:
        operator = source.get_operator()
        target = CSymbolNode(CSymbolType.BinaryExpression, source.get_data_type(), operator)
        target.add_child(parse_from_cir_node(source.get_child(0)))
        target.add_child(parse_from_cir_node(source.get_child(1)))
    elif source.cir_type == cirtree.CirType.call_statement:
        target = CSymbolNode(CSymbolType.CallExpression, base.CType(base.CMetaType.VoidType), None)
        target.add_child(parse_from_cir_node(source.get_child(0)))
        target.add_child(parse_from_cir_node(source.get_child(1)))
    else:
        target = None
    return target


class CSymbolMemoryBlock:
    """
    The block in memory space of symbolic execution contains a finite number of bytes with a symbol being referred,
    a base-address (integer) referring to the first byte in the block, the number of valid and entire block as well.
    """

    def __init__(self, memory, symbol: str, base_address: int, access_range: int, block_size: int):
        self.memory = memory
        self.symbol = symbol
        self.base_address = base_address
        self.access_range = access_range
        self.block_size = block_size
        self.block_state_table = dict()
        return

    def get_memory(self):
        self.memory: CSymbolMemory
        return self.memory

    def get_symbol(self):
        return self.symbol

    def get_base_address(self):
        return self.base_address

    def get_access_range(self):
        return self.access_range

    def get_block_size(self):
        return self.block_size

    def __str__(self):
        return self.symbol + "::[" + str(self.base_address) + ", " + \
               str(self.base_address + self.access_range) + ", " + \
               str(self.base_address + self.block_size) + "]"

    def save_value(self, bias: int, value):
        """
        :param bias:
        :param value: value to be saved in state table or None to remove the state item
        :return: True if the bias {relative address} is accessible.
        """
        if bias < self.access_range:
            self.block_state_table[bias] = value
            if value is None:
                self.block_state_table.pop(bias)
            return True
        else:
            return False

    def load_value(self, bias: int):
        """
        :param bias:
        :return: None if no value is recorded or it is invalid address
        """
        if bias < self.access_range:
            if bias in self.block_state_table:
                return self.block_state_table[bias]
            else:
                return None
        else:
            return None


class CSymbolMemory:
    def __init__(self, access_range=1024, block_size=1024 * 16):
        self.symbol_blocks = dict()
        self.__pointer__ = -block_size
        self.__access_range__ = access_range
        self.__block_size__ = block_size
        self.restart()
        return

    def __allocate__(self, symbol: str):
        """
        :param symbol:
        :return: allocate the address space for the given symbol
        """
        if symbol not in self.symbol_blocks:
            block = CSymbolMemoryBlock(self, symbol, self.__pointer__, self.__access_range__, self.__block_size__)
            self.symbol_blocks[symbol] = block
            self.__pointer__ = self.__pointer__ + self.__block_size__
        return self.symbol_blocks[symbol]

    def restart(self):
        self.symbol_blocks.clear()
        self.__allocate__("#invalid")
        self.__allocate__("#null")
        return

    def encode(self, symbol: str):
        """
        :param symbol: symbol_base{@integer_bias}
        :return: integer address w.r.t. symbolic address
        """
        if '@' in symbol:
            index = symbol.index('@')
            bias = int(symbol[index+1].strip())
            symbol = symbol[0:index].strip()
        else:
            bias = 0
        block = self.__allocate__(symbol)
        block: CSymbolMemoryBlock
        return block.base_address + bias

    def __locate_in__(self, address: int):
        """
        :param address:
        :return: block, bias {relative address}
        """
        for symbol, block in self.symbol_blocks.items():
            block: CSymbolMemoryBlock
            if (address >= block.base_address) and (address < block.base_address + block.block_size):
                return block, address - block.base_address
        return None, -1

    def decode(self, address: int):
        """
        :param address:
        :return: symbol_address@{bias}
        """
        block, bias = self.__locate_in__(address)
        if block is None:
            return "#invalid"
        elif bias == 0:
            return block.symbol
        else:
            return block.symbol + "@" + str(bias)

    def save_value(self, address: int, value: CSymbolNode):
        block, bias = self.__locate_in__(address)
        if block is None:
            return False
        else:
            block: CSymbolMemoryBlock
            return block.save_value(bias, value)

    def load_value(self, address: int):
        block, bias = self.__locate_in__(address)
        if block is None:
            return None
        else:
            block: CSymbolMemoryBlock
            return block.load_value(bias)


class CSymbolEvaluator:
    def __init__(self):
        self.__memory__ = CSymbolMemory()
        return

    # basic methods

    @staticmethod
    def __number_of__(constant):
        if isinstance(constant, bool):
            if constant:
                return 1
            else:
                return 0
        elif isinstance(constant, int):
            return constant
        elif isinstance(constant, float):
            return constant
        else:
            return None

    @staticmethod
    def __boolean_of__(constant):
        if isinstance(constant, bool):
            return constant
        elif isinstance(constant, int):
            return constant != 0
        elif isinstance(constant, float):
            return constant != 0.0
        else:
            return None

    @staticmethod
    def __integer_of__(constant):
        if isinstance(constant, bool):
            if constant:
                return 1
            else:
                return 0
        elif isinstance(constant, int):
            return constant
        elif isinstance(constant, float):
            return int(constant)
        else:
            return None

    def __self_done__(self):
        return self.__memory__

    # evaluation methods

    def __eval__(self, source: CSymbolNode):
        # TODO implement this method...
        if source.sym_type == CSymbolType.Address:
            return self.__eval_address_expression__(source)
        elif source.sym_type == CSymbolType.Constant:
            return self.__eval_constant_expression(source)
        elif source.sym_type == CSymbolType.Literal:
            return self.__eval_string_literal__(source)
        elif source.sym_type == CSymbolType.DefaultValue:
            return self.__eval_default_value__(source)
        elif source.sym_type == CSymbolType.Field:
            return self.__eval_field__(source)
        elif source.sym_type == CSymbolType.UnaryExpression:
            operator = source.get_operator()
            if operator == base.COperator.positive:
                return self.__eval_positive_expression__(source)
            elif operator == base.COperator.negative:
                return self.__eval_negative_expression__(source)
            else:
                return None     # Invalid case being evaluated
        else:
            return None         # invalid case

    # basic expression node

    def __eval_address_expression__(self, source: CSymbolNode):
        """
        :param source:
        :return: address ==> constant {integer}
        """
        sym_address = str(source.content)
        address = self.__memory__.encode(sym_address)
        return CSymbolNode(CSymbolType.Constant, source.get_data_type(), address)

    def __eval_constant_expression(self, source: CSymbolNode):
        """
        :param source:
        :return: source.clone()
        """
        self.__self_done__()
        return source.clone()

    def __eval_string_literal__(self, source: CSymbolNode):
        """
        :param source:
        :return: source.clone()
        """
        self.__self_done__()
        return source.clone()

    def __eval_default_value__(self, source: CSymbolNode):
        """
        :param source:
        :return: source.clone()
        """
        self.__self_done__()
        return source.clone()

    def __eval_field__(self, source: CSymbolNode):
        """
        :param source:
        :return: source.clone()
        """
        self.__self_done__()
        return source.clone()

    # unary expression

    def __eval_positive_expression__(self, source: CSymbolNode):
        """
        :param source:
        :return: __eval__(source.get_child(0))
        """
        self.__self_done__()
        return self.__eval__(source.get_child(0))

    def __eval_negative_expression__(self, source: CSymbolNode):
        """
        :param source:
        :return: down-stream evaluation.
            (0) constant    ==> -constant
                -{-expr}    ==> expr
            (1) x - y       ==> y - x
            (2) x * y * z   ==> -1 * x * y * z
            (3) x / y       ==> -x / y
            (5) {expr}      ==> -expr
        """
        operand = self.__eval__(source.get_child(0))
        operand: CSymbolNode
        if operand.sym_type == CSymbolType.Constant:
            return CSymbolNode(CSymbolType.Constant, source.get_data_type(),
                               -CSymbolEvaluator.__number_of__(source.content))
        elif operand.sym_type == CSymbolType.UnaryExpression:
            if operand.get_operator() == base.COperator.negative:
                return operand.get_child(0).clone()
            else:
                expression = CSymbolNode(CSymbolType.UnaryExpression, source.get_data_type(), base.COperator.negative)
                expression.add_child(operand)
                return expression
        elif operand.sym_type == CSymbolType.BinaryExpression:
            if operand.get_operator() == base.COperator.arith_sub:
                l_operand = operand.get_child(0)
                r_operand = operand.get_child(1)
                expression = CSymbolNode(CSymbolType.BinaryExpression, source.get_data_type(), base.COperator.arith_sub)
                expression.add_child(r_operand.clone())
                expression.add_child(l_operand.clone())
                return expression
            elif operand.get_operator() == base.COperator.arith_div:
                l_operand = operand.get_child(0)
                r_operand = operand.get_child(1)
                ln_operand = CSymbolNode(CSymbolType.UnaryExpression, l_operand.get_data_type(), base.COperator.negative)
                ln_operand.add_child(l_operand.clone())
                ln_operand = self.__eval__(ln_operand)
                rn_operand = r_operand.clone()
                expression = CSymbolNode(CSymbolType.BinaryExpression, source.get_data_type(), base.COperator.arith_div)
                expression.add_child(ln_operand)
                expression.add_child(rn_operand)
                return expression
            else:
                expression = CSymbolNode(CSymbolType.UnaryExpression, source.get_data_type(), base.COperator.negative)
                expression.add_child(operand)
                return expression
        elif operand.sym_type == CSymbolType.MultiExpression:
            if operand.get_operator() == base.COperator.arith_mul:
                operand.add_child(CSymbolNode(CSymbolType.Constant, operand.get_data_type(), -1))
                return self.__eval__(operand)
            else:
                expression = CSymbolNode(CSymbolType.UnaryExpression, source.get_data_type(), base.COperator.negative)
                expression.add_child(operand)
                return expression
        else:
            expression = CSymbolNode(CSymbolType.UnaryExpression, source.get_data_type(), base.COperator.negative)
            expression.add_child(operand)
            return expression

    def __eval_bitws_rsv_expression__(self, source: CSymbolNode):
        """
        :param source:
        :return:
            (1) constant    ==> ~constant
                ~operand    ==> operand
            (2) ~expression
        """
        operand = self.__eval__(source.get_child(0))
        operand: CSymbolNode
        if operand.sym_type == CSymbolType.Constant:
            return CSymbolNode(CSymbolType.Constant, source.get_data_type(),
                               ~CSymbolEvaluator.__number_of__(source.content))
        elif operand.sym_type == CSymbolType.UnaryExpression:
            if operand.get_operator() == base.COperator.bitws_rsv:
                return operand.get_child(0).clone()
            else:
                expression = CSymbolNode(CSymbolType.UnaryExpression, source.get_data_type(), base.COperator.bitws_rsv)
                expression.add_child(operand)
                return expression
        else:
            expression = CSymbolNode(CSymbolType.UnaryExpression, source.get_data_type(), base.COperator.bitws_rsv)
            expression.add_child(operand)
            return expression

    # TODO ...


if __name__ == "__main__":
    lines = list()
    lines.append("[sym]\t1060229007\tSymBinaryExpression\tbool\tstring@greater_tn\t678565780\t889659405")
    lines.append("[sym]\t678565780\tSymMultiExpression\tint\tstring@arith_mul\t1303448479\t2085480465")
    lines.append("[sym]\t1303448479\tSymConstant\tlong\tint@-1")
    lines.append("[sym]\t2085480465\tSymUnaryExpression\tint\tstring@dereference\t1144757947")
    lines.append("[sym]\t1144757947\tSymAddress\t(pointer int)\tstring@line#589058590")
    lines.append("[sym]\t889659405\tSymConstant\tlong\tint@0")
    node = parse_from_text_lines(lines)
    print(node.generate_code(True))
    print(len(node.children))
    print(base.CType.parse("(function (pointer (pointer (array 27 short))))"))

