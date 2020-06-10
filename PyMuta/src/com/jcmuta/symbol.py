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


class CSymMemoryBlock:
    """
    [memory_space, symbol, base_address, valid_range, block_size, __state__]
    """
    def __init__(self, memory_space, symbol: str, base_address: int, valid_range: int, block_size: int):
        self.memory_space = memory_space
        self.symbol = symbol
        self.base_address = base_address
        self.valid_range = valid_range
        self.block_size = block_size
        self.__state__ = dict()
        return

    def get_memory_space(self):
        """
        :return: memory space where the block is defined
        """
        self.memory_space: CSymMemorySpace
        return self.memory_space

    def get_symbol(self):
        """
        :return: symbolic name of this block, referring to the first byte in the block
        """
        return self.symbol

    def get_beg_address(self):
        """
        :return: integer pointer to the first byte in the block
        """
        return self.base_address

    def get_end_address(self):
        """
        :return: integer pointer next to the final byte in the block
        """
        return self.base_address + self.block_size

    def save(self, bias_address: int, value: CSymbolNode):
        """
        :param bias_address:
        :param value:
        :return: true if the bias-address is in its valid range
        """
        if bias_address < 0 or bias_address >= self.valid_range:
            return False    # unable to access invalid range of block
        else:
            if value is None and bias_address in self.__state__:
                self.__state__.pop(bias_address)
            else:
                self.__state__[bias_address] = value
            return True

    def load(self, bias_address: int):
        """
        :param bias_address:
        :return: None if no value is defined in the biased address or it is out of range.
        """
        if bias_address in self.__state__:
            value = self.__state__[bias_address]
            value: CSymbolNode
            return value
        else:
            return None


class CSymMemorySpace:
    """
    memory space as {symbol: block}
    """
    def __init__(self, alloc_pointer=1024*1024*1024, valid_range=64, min_block_size=1024*8, max_block_size=1024*1024):
        """
        :param alloc_pointer:   integer pointer to the beginning of the next address
        :param valid_range:     valid range of each allocated block for user-defined name
        :param min_block_size:  minimal size of block to be allocated.
        :param max_block_size:  maximal size of block to be allocated.
        """
        self.__blocks__ = dict()
        self.alloc_pointer = -max_block_size
        self.min_block_size = min_block_size
        self.max_block_size = max_block_size
        self.__allocate__("#invalid", 0)
        self.alloc_pointer = 0
        self.__allocate__("#null", 0)
        self.alloc_pointer = alloc_pointer
        self.valid_range = valid_range
        return

    def __allocate__(self, symbol: str, valid_range: int):
        """
        :param symbol:
        :param valid_range:
        :return: return the block w.r.t. the symbol in the memory space being allocated.
        """
        if symbol not in self.__blocks__:
            min_block_size = self.min_block_size + valid_range
            max_block_size = self.max_block_size + valid_range
            block_size = random.randint(min_block_size, max_block_size)
            block = CSymMemoryBlock(self, symbol, self.alloc_pointer, valid_range, block_size)
            self.__blocks__[symbol] = block
            self.alloc_pointer = block.get_end_address()
        block = self.__blocks__[symbol]
        block: CSymMemoryBlock
        return block

    def int_address(self, symbol: str):
        """
        :param symbol: symbol_name{@integer}
        :return: integer address w.r.t. the symbolic address
        """
        if '@' in symbol:
            index = symbol.index('@')
            bias_address = int(symbol[index+1:].strip())
            symbol = symbol[0:index].strip()
        else:
            bias_address = 0
        block = self.__allocate__(symbol, self.valid_range)
        return block.get_beg_address() + bias_address

    def sym_address(self, address: int):
        """
        :param address:
        :return: symbolic address as name@integer to the integer address or #invalid for byte that cannot be accessed
        """
        symbol, bias_address = "#invalid", 0
        for key, block in self.__blocks__.items():
            block: CSymMemoryBlock
            if (address >= block.get_beg_address()) and (address < block.get_end_address()):
                symbol = block.get_symbol()
                bias_address = address - block.get_beg_address()
                break
        if bias_address > 0:
            return symbol + "@" + str(bias_address)
        return symbol

    def block_address(self, address: int):
        """
        :param address:
        :return: block, bias_address
        """
        symbol, bias_address = "#invalid", 0
        for key, block in self.__blocks__.items():
            block: CSymMemoryBlock
            if (address >= block.get_beg_address()) and (address < block.get_end_address()):
                symbol = block.get_symbol()
                bias_address = address - block.get_beg_address()
                break
        block = self.__blocks__[symbol]
        block: CSymMemoryBlock
        return block, bias_address

    def save(self, address: int, value: CSymbolNode):
        """
        :param address:
        :param value:
        :return: true if the address is in valid range
        """
        block, bias_address = self.block_address(address)
        return block.save(bias_address, value)

    def load(self, address: int):
        """
        :param address:
        :return: value hold at the address or none if it is not defined
        """
        block, bias_address = self.block_address(address)
        return block.load(bias_address)


class CSymParser:
    """
    To parse the symbolic expression from text-lines, cir-tree-node and itself to standard format.
    """
    def __init__(self):
        return

    def __done__(self):
        """
        :return: to cancel the warning of static-method suggestion
        """
        return self

    def parse_by_text_lines(self, text_lines: list):
        """
        [sym] id sym_type data_type
        :param text_lines:
        :return:
        """
        self.__done__()
        nodes = dict()
        for line in text_lines:
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
        for line in text_lines:
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

    def parse_by_cir_tree(self, cir_node: cirtree.CirNode):
        """
        :param cir_node:
        :return: symbolic representation of cir source node or none
        """
        self.__done__()
        source = cir_node
        if source.cir_type == cirtree.CirType.argument_list:
            target = CSymbolNode(CSymbolType.ArgumentList, base.CType(base.CMetaType.VoidType), None)
            for child in source.get_children():
                child: cirtree.CirNode
                target_child = self.parse_by_cir_tree(child)
                target.add_child(target_child)
        elif source.cir_type == cirtree.CirType.field:
            target = CSymbolNode(CSymbolType.Field, source.get_data_type(), source.content)
        elif source.cir_type == cirtree.CirType.defer_expression:
            target = CSymbolNode(CSymbolType.UnaryExpression, source.get_data_type(), base.COperator.dereference)
            target.add_child(self.parse_by_cir_tree(source.get_child(0)))
        elif source.cir_type == cirtree.CirType.field_expression:
            target = CSymbolNode(CSymbolType.FieldExpression, source.get_data_type(), None)
            target.add_child(self.parse_by_cir_tree(source.get_child(0)))
            target.add_child(self.parse_by_cir_tree(source.get_child(1)))
        elif source.cir_type == cirtree.CirType.declarator or source.cir_type == cirtree.CirType.implicator or \
                source.cir_type == cirtree.CirType.identifier or source.cir_type == cirtree.CirType.return_point:
            pointer_type = base.CType(base.CMetaType.PointType)
            pointer_type.operands.append(source.get_data_type())
            target_address = CSymbolNode(CSymbolType.Address, pointer_type, source.content)
            target = CSymbolNode(CSymbolType.UnaryExpression, source.get_data_type(), base.COperator.dereference)
            target.add_child(target_address)
        elif source.cir_type == cirtree.CirType.address_expression:
            target = CSymbolNode(CSymbolType.UnaryExpression, source.get_data_type(), base.COperator.address_of)
            target.add_child(self.parse_by_cir_tree(source.get_child(0)))
        elif source.cir_type == cirtree.CirType.cast_expression:
            target = CSymbolNode(CSymbolType.UnaryExpression, source.get_data_type(), base.COperator.assign)
            target.add_child(self.parse_by_cir_tree(source.get_child(1)))
        elif source.cir_type == cirtree.CirType.constant:
            target = CSymbolNode(CSymbolType.Constant, source.get_data_type(), source.content)
        elif source.cir_type == cirtree.CirType.default_value:
            target = CSymbolNode(CSymbolType.DefaultValue, source.get_data_type(), None)
        elif source.cir_type == cirtree.CirType.literal:
            target = CSymbolNode(CSymbolType.Literal, source.get_data_type(), source.content)
        elif source.cir_type == cirtree.CirType.initializer_body:
            target = CSymbolNode(CSymbolType.SequenceExpression, source.get_data_type(), None)
            for child in source.get_children():
                target.add_child(self.parse_by_cir_tree(child))
        elif source.cir_type == cirtree.CirType.wait_expression:
            target = CSymbolNode(CSymbolType.CallExpression, source.get_data_type(), None)
            target.add_child(self.parse_by_cir_tree(source.get_child(0)))
        elif source.cir_type == cirtree.CirType.arith_expression:
            operator = source.get_operator()
            if operator == base.COperator.negative:
                target = CSymbolNode(CSymbolType.UnaryExpression, source.get_data_type(), base.COperator.negative)
                target.add_child(self.parse_by_cir_tree(source.get_child(0)))
            elif operator == base.COperator.positive:
                target = self.parse_by_cir_tree(source.get_child(0))
            elif operator == base.COperator.arith_add or operator == base.COperator.arith_mul:
                target = CSymbolNode(CSymbolType.MultiExpression, source.get_data_type(), operator)
                for child in source.get_children():
                    target.add_child(self.parse_by_cir_tree(child))
            else:
                target = CSymbolNode(CSymbolType.BinaryExpression, source.get_data_type(), operator)
                target.add_child(self.parse_by_cir_tree(source.get_child(0)))
                target.add_child(self.parse_by_cir_tree(source.get_child(1)))
        elif source.cir_type == cirtree.CirType.bitws_expression:
            operator = source.get_operator()
            if operator == base.COperator.bitws_rsv:
                target = CSymbolNode(CSymbolType.UnaryExpression, source.get_data_type(), base.COperator.bitws_rsv)
                target.add_child(self.parse_by_cir_tree(source.get_child(0)))
            elif operator == base.COperator.bitws_and or operator == base.COperator.bitws_ior or \
                    operator == base.COperator.bitws_xor:
                target = CSymbolNode(CSymbolType.MultiExpression, source.get_data_type(), operator)
                for child in source.get_children():
                    target.add_child(self.parse_by_cir_tree(child))
            else:
                target = CSymbolNode(CSymbolType.BinaryExpression, source.get_data_type(), operator)
                target.add_child(self.parse_by_cir_tree(source.get_child(0)))
                target.add_child(self.parse_by_cir_tree(source.get_child(1)))
        elif source.cir_type == cirtree.CirType.logic_expression:
            operator = source.get_operator()
            if operator == base.COperator.logic_not:
                target = CSymbolNode(CSymbolType.UnaryExpression, source.get_data_type(), base.COperator.logic_not)
                target.add_child(self.parse_by_cir_tree(source.get_child(0)))
            else:
                target = CSymbolNode(CSymbolType.MultiExpression, source.get_data_type(), operator)
                for child in source.get_children():
                    target.add_child(self.parse_by_cir_tree(child))
        elif source.cir_type == cirtree.CirType.relational_expression:
            operator = source.get_operator()
            target = CSymbolNode(CSymbolType.BinaryExpression, source.get_data_type(), operator)
            target.add_child(self.parse_by_cir_tree(source.get_child(0)))
            target.add_child(self.parse_by_cir_tree(source.get_child(1)))
        elif source.cir_type == cirtree.CirType.call_statement:
            target = CSymbolNode(CSymbolType.CallExpression, base.CType(base.CMetaType.VoidType), None)
            target.add_child(self.parse_by_cir_tree(source.get_child(0)))
            target.add_child(self.parse_by_cir_tree(source.get_child(1)))
        else:
            target = None
        return target


sym_parser = CSymParser()


class CSymEvaluator:
    """
    To evaluate the value hold by symbolic expression into standard form: CSymNode --> CSymNode.
    """
    def __init__(self):
        self.__memory__ = CSymMemorySpace()
        return

    def reset(self):
        """
        :return: reset the state of memory
        """
        self.__memory__ = CSymMemorySpace()
        return

    def __done__(self):
        return self

    def evaluate(self, expression: CSymbolNode):
        if expression.sym_type == CSymbolType.Address:
            return self.__eval_address__(expression)
        elif expression.sym_type == CSymbolType.Constant:
            return self.__eval_constant__(expression)
        elif expression.sym_type == CSymbolType.Literal:
            return self.__eval_literal__(expression)
        elif expression.sym_type == CSymbolType.DefaultValue:
            return self.__eval_default_value__(expression)
        elif expression.sym_type == CSymbolType.Field:
            return self.__eval_field__(expression)
        elif expression.sym_type == CSymbolType.FieldExpression:
            return self.__eval_field_expression__(expression)
        elif expression.sym_type == CSymbolType.ArgumentList:
            return self.__eval_argument_list__(expression)
        elif expression.sym_type == CSymbolType.CallExpression:
            return self.__eval_call_expression__(expression)
        elif expression.sym_type == CSymbolType.SequenceExpression:
            return self.__eval_sequence_expression__(expression)
        # TODO implement more here.
        else:
            return expression.clone()

    # toolkit methods

    def __boolean__(self, constant):
        """
        :param constant: bool, int, float
        :return: bool
        """
        self.__done__()
        if isinstance(constant, bool):
            constant: bool
            return constant
        elif isinstance(constant, int):
            return constant != 0
        elif isinstance(constant, float):
            return constant != 0.0
        else:
            return False

    def __integer__(self, constant):
        """
        :param constant: bool, int, float
        :return: int or -1024*1024*1024
        """
        self.__done__()
        if isinstance(constant, bool):
            if constant:
                return 1
            else:
                return 0
        elif isinstance(constant, int):
            constant: int
            return constant
        elif isinstance(constant, float):
            return int(constant)
        else:
            return -1024*1024*1024

    def __float__(self, constant):
        """
        :param constant: bool, int, float
        :return: float or -1024*1024*1024f
        """
        self.__done__()
        if isinstance(constant, bool):
            if constant:
                return 1.0
            else:
                return 0.0
        elif isinstance(constant, int):
            return float(constant)
        elif isinstance(constant, float):
            constant: float
            return constant
        else:
            return -1024.0 * 1024.0 * 1024.0

    def __number__(self, constant):
        """
        :param constant: bool, int, float
        :return: int, float
        """
        self.__done__()
        if isinstance(constant, bool):
            if constant:
                return 1
            else:
                return 0
        elif isinstance(constant, int):
            constant: int
            return constant
        elif isinstance(constant, float):
            constant: float
            return constant
        else:
            return -1024 * 1024 * 1024

    # basic expressions

    def __eval_address__(self, expression: CSymbolNode):
        """
        :param expression:
        :return: Address --> Constant[int]
        """
        sym_address = str(expression.content)
        int_address = self.__memory__.int_address(sym_address)
        return CSymbolNode(CSymbolType.Constant, expression.data_type, int_address)

    def __eval_constant__(self, expression: CSymbolNode):
        """
        :param expression:
        :return: clone()
        """
        self.__done__()
        return expression.clone()

    def __eval_default_value__(self, expression: CSymbolNode):
        """
        :param expression:
        :return:
            bool    --> false
            int | real  --> 0
            pointer --> #invalid
            other   --> clone()
        """
        data_type = expression.get_data_type()
        if data_type.is_bool_type():
            return CSymbolNode(CSymbolType.Constant, data_type, False)
        elif data_type.is_integer_type() or data_type.is_real_type():
            return CSymbolNode(CSymbolType.Constant, data_type, 0)
        elif data_type.is_address_type():
            invalid_address = self.__memory__.int_address("#invalid")
            return CSymbolNode(CSymbolType.Constant, data_type, invalid_address)
        else:
            return expression.clone()

    def __eval_literal__(self, expression: CSymbolNode):
        """
        :param expression:
        :return: clone()
        """
        self.__done__()
        return expression.clone()

    # recursive cases

    def __eval_field__(self, field: CSymbolNode):
        """
        :param field:
        :return: clone()
        """
        self.__done__()
        return field.clone()

    def __eval_field_expression__(self, expression: CSymbolNode):
        """
        :param expression:
        :return:
        """
        body = self.evaluate(expression.get_child(0))
        field = self.evaluate(expression.get_child(1))
        result = CSymbolNode(CSymbolType.FieldExpression, expression.data_type, expression.content)
        result.add_child(body)
        result.add_child(field)
        return result

    def __eval_sequence_expression__(self, expression: CSymbolNode):
        """
        :param expression:
        :return: {(expression)+}
        """
        sequence = CSymbolNode(CSymbolType.SequenceExpression, expression.data_type, expression.content)
        for child in expression.get_children():
            child: CSymbolNode
            element = self.evaluate(child)
            sequence.add_child(element)
        return sequence

    def __eval_argument_list__(self, arguments: CSymbolNode):
        """
        :param arguments:
        :return:
        """
        argument_list = CSymbolNode(CSymbolType.ArgumentList, arguments.data_type, arguments.content)
        for child in arguments.get_children():
            child: CSymbolNode
            argument = self.evaluate(child)
            argument_list.add_child(argument)
        return argument_list

    def __eval_call_expression__(self, expression: CSymbolNode):
        """
        :param expression:
        :return:
        """
        call_expression = CSymbolNode(CSymbolType.CallExpression, expression.data_type, expression.content)
        function = self.evaluate(expression.get_child(0))
        arguments = self.evaluate(expression.get_child(1))
        call_expression.add_child(function)
        call_expression.add_child(arguments)
        return call_expression

    # unary expression

    def __eval_positive_expression__(self, expression: CSymbolNode):
        """
        :param expression:
        :return: evaluate(expression.operand)
        """
        return self.evaluate(expression.get_child(0))

    def __eval_negative_expression__(self, expression: CSymbolNode):
        """
        :param expression:
        :return:
            (1) operand as constant --> -constant
            (2) operand as negative --> operand.children[0]
            (3) operand as 
        """



if __name__ == "__main__":
    lines = list()
    lines.append("[sym]\t1060229007\tSymBinaryExpression\tbool\tstring@greater_tn\t678565780\t889659405")
    lines.append("[sym]\t678565780\tSymMultiExpression\tint\tstring@arith_mul\t1303448479\t2085480465")
    lines.append("[sym]\t1303448479\tSymConstant\tlong\tint@-1")
    lines.append("[sym]\t2085480465\tSymUnaryExpression\tint\tstring@dereference\t1144757947")
    lines.append("[sym]\t1144757947\tSymAddress\t(pointer int)\tstring@line#589058590")
    lines.append("[sym]\t889659405\tSymConstant\tlong\tint@0")
    node = sym_parser.parse_by_text_lines(lines)
    print(node.generate_code(True))
    print(len(node.children))
    print(base.CType.parse("(function (pointer (pointer (array 27 short))))"))

