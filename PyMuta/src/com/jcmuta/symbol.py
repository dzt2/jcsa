from collections import deque
from enum import Enum
import random
import src.com.jcparse.base as base
import src.com.jcparse.cirtree as cirtree
import src.com.jcparse.cirflow as cirflow
import src.com.jcparse.cirinst as cirinst


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
        self.parent: CSymbolNode
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

    def is_reference(self):
        """
        :return: dereference or field-expression
        """
        return self.sym_type == CSymbolType.FieldExpression or self.get_operator() == base.COperator.dereference


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

    def parse_by_execution_flow(self, execution_flow: cirflow.CirExecutionFlow, optimize: bool):
        """
        :param optimize:
        :param execution_flow:
        :return:
            (1) true_flow   --> flow.source.condition is true
            (2) false_flow  --> flow.target.condition is false
            (3) otherwise   --> true
        """
        if execution_flow.get_flow_type() == cirflow.CirExecutionFlowType.true_flow:
            cir_condition = execution_flow.get_source().get_statement().get_child(0)
            sym_condition = self.parse_by_cir_tree(cir_condition)
        elif execution_flow.get_flow_type() == cirflow.CirExecutionFlowType.false_flow:
            cir_condition = execution_flow.get_source().get_statement().get_child(0)
            sym_operand = self.parse_by_cir_tree(cir_condition)
            sym_condition = CSymbolNode(CSymbolType.UnaryExpression, cir_condition.data_type, base.COperator.logic_not)
            sym_condition.add_child(sym_operand)
        else:
            sym_condition = CSymbolNode(CSymbolType.Constant, base.CType(base.CMetaType.BoolType), True)
        if optimize:
            return sym_evaluator.evaluate(sym_condition)
        else:
            return sym_condition

    def parse_by_instance_edge(self, instance_edge: cirinst.CirInstanceEdge, optimize: bool):
        """
        :param optimize:
        :param instance_edge:
        :return:
            (1) true_flow   --> flow.source.condition is true
            (2) false_flow  --> flow.target.condition is false
            (3) otherwise   --> true
        """
        if instance_edge.get_flow_type() == cirflow.CirExecutionFlowType.true_flow:
            cir_condition = instance_edge.get_source().get_source_execution().get_statement().get_child(0)
            sym_condition = self.parse_by_cir_tree(cir_condition)
        elif instance_edge.get_flow_type() == cirflow.CirExecutionFlowType.false_flow:
            cir_condition = instance_edge.get_source().get_source_execution().get_statement().get_child(0)
            sym_operand = self.parse_by_cir_tree(cir_condition)
            sym_condition = CSymbolNode(CSymbolType.UnaryExpression, cir_condition.data_type, base.COperator.logic_not)
            sym_condition.add_child(sym_operand)
        else:
            sym_condition = CSymbolNode(CSymbolType.Constant, base.CType(base.CMetaType.BoolType), True)
        if optimize:
            return sym_evaluator.evaluate(sym_condition)
        else:
            return sym_condition


sym_parser = CSymParser()


class CSymEvaluator:
    """
    To evaluate the value hold by symbolic expression into standard form: CSymNode --> CSymNode.
    """
    def __init__(self):
        self.__memory__ = CSymMemorySpace()
        self.__debugging__ = False
        return

    def reset(self):
        """
        :return: reset the state of memory
        """
        self.__memory__ = CSymMemorySpace()
        self.__debugging__ = False
        return

    def __done__(self):
        return self

    def evaluate(self, expression: CSymbolNode):
        """
        :param expression:
        :return: standardized symbolic expression
        """
        if expression.sym_type == CSymbolType.Address:
            result = self.__eval_address__(expression)
        elif expression.sym_type == CSymbolType.Constant:
            result = self.__eval_constant__(expression)
        elif expression.sym_type == CSymbolType.Literal:
            result = self.__eval_literal__(expression)
        elif expression.sym_type == CSymbolType.DefaultValue:
            result = self.__eval_default_value__(expression)
        elif expression.sym_type == CSymbolType.Field:
            result = self.__eval_field__(expression)
        elif expression.sym_type == CSymbolType.FieldExpression:
            result = self.__eval_field_expression__(expression)
        elif expression.sym_type == CSymbolType.ArgumentList:
            result = self.__eval_argument_list__(expression)
        elif expression.sym_type == CSymbolType.CallExpression:
            result = self.__eval_call_expression__(expression)
        elif expression.sym_type == CSymbolType.SequenceExpression:
            result = self.__eval_sequence_expression__(expression)
        elif expression.sym_type == CSymbolType.UnaryExpression:
            operator = expression.get_operator()
            if operator == base.COperator.positive:
                result = self.__eval_positive_expression__(expression)
            elif operator == base.COperator.negative:
                result = self.__eval_negative_expression__(expression)
            elif operator == base.COperator.bitws_rsv:
                result = self.__eval_bitws_rsv_expression__(expression)
            elif operator == base.COperator.logic_not:
                result = self.__eval_logic_not_expression__(expression)
            elif operator == base.COperator.address_of:
                result = self.__eval_address_of_expression__(expression)
            elif operator == base.COperator.dereference:
                result = self.__eval_dereference_expression__(expression)
            elif operator == base.COperator.assign:
                result = self.__eval_cast_expression__(expression)
            else:
                result = self.__eval_otherwise__(expression)
        elif expression.sym_type == CSymbolType.MultiExpression or expression.sym_type == CSymbolType.BinaryExpression:
            operator = expression.get_operator()
            if operator == base.COperator.arith_add:
                result = self.__eval_arith_add__(expression)
            elif operator == base.COperator.arith_sub:
                result = self.__eval_arith_sub__(expression)
            elif operator == base.COperator.arith_mul:
                result = self.__eval_arith_mul__(expression)
            elif operator == base.COperator.arith_div:
                result = self.__eval_arith_div__(expression)
            elif operator == base.COperator.arith_mod:
                result = self.__eval_arith_mod__(expression)
            elif operator == base.COperator.bitws_and:
                result = self.__eval_bitws_and__(expression)
            elif operator == base.COperator.bitws_ior:
                result = self.__eval_bitws_ior__(expression)
            elif operator == base.COperator.bitws_xor:
                result = self.__eval_bitws_xor__(expression)
            elif operator == base.COperator.bitws_lsh:
                result = self.__eval_bitws_lsh__(expression)
            elif operator == base.COperator.bitws_rsh:
                result = self.__eval_bitws_rsh__(expression)
            elif operator == base.COperator.logic_and:
                result = self.__eval_logic_and__(expression)
            elif operator == base.COperator.logic_ior:
                result = self.__eval_logic_ior__(expression)
            elif operator == base.COperator.greater_tn:
                result = self.__eval_greater_tn__(expression)
            elif operator == base.COperator.greater_eq:
                result = self.__eval_greater_eq__(expression)
            elif operator == base.COperator.smaller_tn:
                result = self.__eval_smaller_tn__(expression)
            elif operator == base.COperator.smaller_eq:
                result = self.__eval_smaller_eq__(expression)
            elif operator == base.COperator.not_equals:
                result = self.__eval_not_equals__(expression)
            elif operator == base.COperator.equal_with:
                result = self.__eval_equal_with__(expression)
            else:
                result = self.__eval_otherwise__(expression)
        else:
            result = self.__eval_otherwise__(expression)
        if self.__debugging__:
            print("\t==> DEBUG:", expression.generate_code(True), "\t==>", result.generate_code(True))
        return result

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

    def __extend_in_operand__(self, expression: CSymbolNode, operand: CSymbolNode, operands: list):
        """
        :param expression: multi-operands expression
        :param operand:
        :param operands:
        :return: append the available operand in available level to the operands of list
        """
        operator = expression.get_operator()
        if operand.get_operator() == operator:
            for operand_child in operand.get_children():
                self.__extend_in_operand__(operand, operand_child, operands)
        else:
            operands.append(operand)
        return

    def __extend_operands__(self, expression: CSymbolNode):
        """
        :param expression:
        :return: operands in the same level w.r.t. the operator of expression
        """
        operands = list()
        for child in expression.get_children():
            operand = self.evaluate(child)
            self.__extend_in_operand__(expression, operand, operands)
        return operands

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

    def __eval_otherwise__(self, expression: CSymbolNode):
        result = CSymbolNode(expression.sym_type, expression.data_type, expression.content)
        for child in expression.get_children():
            result.add_child(self.evaluate(child))
        return result

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
            (3) operand as multiply --> -1 * operand
            (4) negative[operand]
        """
        operand = self.evaluate(expression.get_child(0))
        if operand.sym_type == CSymbolType.Constant:
            number = self.__number__(operand.content)
            return CSymbolNode(CSymbolType.Constant, expression.data_type, -number)
        elif operand.sym_type == CSymbolType.UnaryExpression and operand.get_operator() == base.COperator.negative:
            return operand.get_child(0).clone()
        elif operand.sym_type == CSymbolType.MultiExpression and operand.get_operator() == base.COperator.arith_mul:
            operand.add_child(CSymbolNode(CSymbolType.Constant, expression.data_type, -1))
            return self.evaluate(operand)
        elif operand.sym_type == CSymbolType.MultiExpression and operand.get_operator() == base.COperator.arith_add:
            result = CSymbolNode(CSymbolType.MultiExpression, expression.data_type, base.COperator.arith_add)
            for child in operand.get_children():
                child: CSymbolNode
                new_child = CSymbolNode(CSymbolType.UnaryExpression, child.data_type, base.COperator.negative)
                new_child.add_child(child.clone())
                result.add_child(new_child)
            return self.evaluate(result)
        elif operand.sym_type == CSymbolType.BinaryExpression and operand.get_operator() == base.COperator.arith_sub:
            l_operand = operand.get_child(0)
            r_operand = operand.get_child(1)
            result = CSymbolNode(CSymbolType.BinaryExpression, expression.data_type, base.COperator.arith_sub)
            result.add_child(r_operand.clone())
            result.add_child(l_operand.clone())
            return self.evaluate(result)
        else:
            result = CSymbolNode(CSymbolType.UnaryExpression, expression.data_type, base.COperator.negative)
            result.add_child(operand)
            return result

    def __eval_bitws_rsv_expression__(self, expression: CSymbolNode):
        """
        :param expression:
        :return:
            (1) constant    --> ~constant
            (2) operand as ~    --> operand.children[0]
            (3) x1 & x2 & x3    --> {~x1} | {~x2} | {~x3}
                x1 | x2 | x3    --> {~x1} | {~x2} | {~x3}
            (4) operand as -    --> operand.children[0] - 1
            (5) ~{operand}
        """
        operand = self.evaluate(expression.get_child(0))
        if operand.sym_type == CSymbolType.Constant:
            constant = self.__integer__(operand.content)
            return CSymbolNode(CSymbolType.Constant, expression.data_type, ~constant)
        elif operand.sym_type == CSymbolType.UnaryExpression and operand.get_operator() == base.COperator.negative:
            result = CSymbolNode(CSymbolType.BinaryExpression, expression.data_type, base.COperator.arith_sub)
            result.add_child(operand.get_child(0).clone())
            result.add_child(CSymbolNode(CSymbolType.Constant, expression.data_type, 1))
            return self.evaluate(result)
        elif operand.sym_type == CSymbolType.UnaryExpression and operand.get_operator() == base.COperator.bitws_rsv:
            return operand.get_child(0).clone()
        elif operand.sym_type == CSymbolType.MultiExpression and operand.get_operator() == base.COperator.bitws_and:
            result = CSymbolNode(CSymbolType.MultiExpression, expression.data_type, base.COperator.bitws_ior)
            for operand_child in operand.get_children():
                operand_child: CSymbolNode
                new_child = CSymbolNode(CSymbolType.UnaryExpression, operand_child.data_type, base.COperator.bitws_rsv)
                new_child.add_child(operand_child.clone())
                result.add_child(new_child)
            return self.evaluate(result)
        elif operand.sym_type == CSymbolType.MultiExpression and operand.get_operator() == base.COperator.bitws_ior:
            result = CSymbolNode(CSymbolType.MultiExpression, expression.data_type, base.COperator.bitws_and)
            for operand_child in operand.get_children():
                operand_child: CSymbolNode
                new_child = CSymbolNode(CSymbolType.UnaryExpression, operand_child.data_type, base.COperator.bitws_rsv)
                new_child.add_child(operand_child.clone())
                result.add_child(new_child)
            return self.evaluate(result)
        else:
            result = CSymbolNode(CSymbolType.UnaryExpression, expression.data_type, base.COperator.bitws_rsv)
            result.add_child(operand)
            return result

    def __eval_logic_not_expression__(self, expression: CSymbolNode):
        """
        :param expression:
        :return:
            (1) constant        --> !constant
            (2) operand as !    --> operand.children[0]
            (3) x1 && x2 && x3  --> !x1 || !x2 || !x3
            (4) x1 || x2 || x3  --> !x1 && !x2 && !x3
            (5) !(x1 == x2)     --> x1 != x2
                !(x1 != x2)     --> x1 == x2
                !(x1 > x2)      --> x1 <= x2
                !(x1 >= x2)     --> x1 < x2
                !(x1 < x2)      --> x2 <= x1
                !(x1 <= x2)     --> x2 < x1
            (6) !{operand}
        """
        operand = self.evaluate(expression.get_child(0))
        if operand.sym_type == CSymbolType.Constant:
            constant = self.__boolean__(operand.content)
            return CSymbolNode(CSymbolType.Constant, expression.data_type, not constant)
        elif operand.sym_type == CSymbolType.UnaryExpression and operand.get_operator() == base.COperator.logic_not:
            return operand.get_child(0).clone()
        elif operand.sym_type == CSymbolType.MultiExpression and operand.get_operator() == base.COperator.logic_and:
            result = CSymbolNode(CSymbolType.MultiExpression, expression.data_type, base.COperator.logic_ior)
            for operand_child in operand.get_children():
                operand_child: CSymbolNode
                new_child = CSymbolNode(CSymbolType.UnaryExpression, operand_child.data_type, base.COperator.logic_not)
                new_child.add_child(operand_child.clone())
                result.add_child(new_child)
            return self.evaluate(result)
        elif operand.sym_type == CSymbolType.MultiExpression and operand.get_operator() == base.COperator.logic_ior:
            result = CSymbolNode(CSymbolType.MultiExpression, expression.data_type, base.COperator.logic_and)
            for operand_child in operand.get_children():
                operand_child: CSymbolNode
                new_child = CSymbolNode(CSymbolType.UnaryExpression, operand_child.data_type, base.COperator.logic_not)
                new_child.add_child(operand_child.clone())
                result.add_child(new_child)
            return self.evaluate(result)
        elif operand.sym_type == CSymbolType.BinaryExpression and operand.get_operator() == base.COperator.equal_with:
            l_operand = operand.get_child(0).clone()
            r_operand = operand.get_child(1).clone()
            result = CSymbolNode(CSymbolType.BinaryExpression, expression.data_type, base.COperator.not_equals)
            result.add_child(l_operand)
            result.add_child(r_operand)
            return result
        elif operand.sym_type == CSymbolType.BinaryExpression and operand.get_operator() == base.COperator.not_equals:
            l_operand = operand.get_child(0).clone()
            r_operand = operand.get_child(1).clone()
            result = CSymbolNode(CSymbolType.BinaryExpression, expression.data_type, base.COperator.equal_with)
            result.add_child(l_operand)
            result.add_child(r_operand)
            return result
        elif operand.sym_type == CSymbolType.BinaryExpression and operand.get_operator() == base.COperator.greater_tn:
            l_operand = operand.get_child(0).clone()
            r_operand = operand.get_child(1).clone()
            result = CSymbolNode(CSymbolType.BinaryExpression, expression.data_type, base.COperator.smaller_eq)
            result.add_child(l_operand)
            result.add_child(r_operand)
            return result
        elif operand.sym_type == CSymbolType.BinaryExpression and operand.get_operator() == base.COperator.greater_eq:
            l_operand = operand.get_child(0).clone()
            r_operand = operand.get_child(1).clone()
            result = CSymbolNode(CSymbolType.BinaryExpression, expression.data_type, base.COperator.smaller_tn)
            result.add_child(l_operand)
            result.add_child(r_operand)
            return result
        elif operand.sym_type == CSymbolType.BinaryExpression and operand.get_operator() == base.COperator.smaller_tn:
            l_operand = operand.get_child(0).clone()
            r_operand = operand.get_child(1).clone()
            result = CSymbolNode(CSymbolType.BinaryExpression, expression.data_type, base.COperator.smaller_eq)
            result.add_child(r_operand)
            result.add_child(l_operand)
            return result
        elif operand.sym_type == CSymbolType.BinaryExpression and operand.get_operator() == base.COperator.smaller_eq:
            l_operand = operand.get_child(0).clone()
            r_operand = operand.get_child(1).clone()
            result = CSymbolNode(CSymbolType.BinaryExpression, expression.data_type, base.COperator.smaller_tn)
            result.add_child(r_operand)
            result.add_child(l_operand)
            return result
        else:
            result = CSymbolNode(CSymbolType.UnaryExpression, expression.data_type, base.COperator.logic_not)
            result.add_child(operand)
            return result

    def __eval_address_of_expression__(self, expression: CSymbolNode):
        """
        :param expression:
        :return:
            (1) operand as *x   --> evaluate(x)
            (2) &{operand}
        """
        operand = expression.get_child(0)
        if operand.sym_type == CSymbolType.UnaryExpression and operand.get_operator() == base.COperator.dereference:
            return self.evaluate(operand.get_child(0))
        else:
            result = CSymbolNode(CSymbolType.UnaryExpression, expression.data_type, base.COperator.address_of)
            result.add_child(self.evaluate(operand))
            return result

    def __eval_dereference_expression__(self, expression: CSymbolNode):
        """
        :param expression:
        :return:
            (1) constant --> try-load{integer_constant}
            (2) operand as &x   --> evaluate(x)
            (3) *{operand}
        """
        operand = expression.get_child(0)
        if operand.sym_type == CSymbolType.UnaryExpression and operand.get_operator() == base.COperator.address_of:
            return self.evaluate(operand.get_child(0))
        else:
            operand = self.evaluate(operand)
            if operand.sym_type == CSymbolType.Constant:
                int_address = self.__integer__(operand.content)
                value = self.__memory__.load(int_address)
                if value is None:
                    sym_address = self.__memory__.sym_address(int_address)
                    result = CSymbolNode(CSymbolType.UnaryExpression, expression.data_type, base.COperator.dereference)
                    result.add_child(CSymbolNode(CSymbolType.Address, operand.data_type, sym_address))
                    return result
                else:
                    value: CSymbolNode
                    return value
            else:
                result = CSymbolNode(CSymbolType.UnaryExpression, expression.data_type, base.COperator.dereference)
                result.add_child(operand)
                return result

    def __eval_cast_expression__(self, expression: CSymbolNode):
        """
        :param expression:
        :return:
            (1) constant    --> cast[constant]
            (2) cast{operand}
        """
        operand = self.evaluate(expression.get_child(0))
        if operand.sym_type == CSymbolType.Constant:
            cast_type = expression.get_data_type()
            if cast_type.is_bool_type():
                constant = self.__boolean__(operand.content)
            elif cast_type.is_integer_type():
                constant = self.__integer__(operand.content)
            elif cast_type.is_real_type():
                constant = self.__float__(operand.content)
            else:
                constant = operand.content
            return CSymbolNode(CSymbolType.Constant, expression.data_type, constant)
        else:
            result = CSymbolNode(CSymbolType.UnaryExpression, expression.data_type, base.COperator.assign)
            result.add_child(operand)
            return result

    # binary expressions

    def __divide_operands_in_arith_add__(self, operands: list):
        """
        :param operands:
        :return: variables, constant
        """
        variables, constant = list(), 0
        for operand in operands:
            if operand.sym_type == CSymbolType.Constant:
                number = self.__number__(operand.content)
                constant = constant + number
            else:
                variables.append(operand)
        return variables, constant

    def __reconstruct_for_arith_add__(self, expression: CSymbolNode, variables: list, constant):
        """
        :param variables:
        :param constant:
        :return: standard generation of arith addition w.r.t. variable operands and one constant
        """
        # 1. generate the operands in arith addition expression
        self.__done__()
        operands = list()
        for variable in variables:
            variable: CSymbolNode
            operands.append(variable)
        if constant != 0:
            operands.append(CSymbolNode(CSymbolType.Constant, expression.data_type, constant))
        # 2. generate standard form
        if len(operands) == 0:
            return CSymbolNode(CSymbolType.Constant, expression.data_type, 0)
        elif len(operands) == 1:
            return operands[0]
        else:
            result = CSymbolNode(CSymbolType.MultiExpression, expression.data_type, base.COperator.arith_add)
            for operand in operands:
                result.add_child(operand)
            return result

    def __eval_arith_add__(self, expression: CSymbolNode):
        """
        :param expression:
        :return: concatenation
        """
        # 1. collect the operands in the same level
        operands = self.__extend_operands__(expression)
        # 2. divide into variables and constant
        variables, constant = self.__divide_operands_in_arith_add__(operands)
        # 3. generate the standard arith addition
        return self.__reconstruct_for_arith_add__(expression, variables, constant)

    def __eval_arith_sub__(self, expression: CSymbolNode):
        """
        :param expression:
        :return: evaluate(x + (-y))
        """
        result = CSymbolNode(CSymbolType.MultiExpression, expression.data_type, base.COperator.arith_add)
        result.add_child(expression.get_child(0).clone())
        neg_y = CSymbolNode(CSymbolType.UnaryExpression, expression.get_child(1).data_type, base.COperator.negative)
        neg_y.add_child(expression.get_child(1).clone())
        result.add_child(neg_y)
        return self.evaluate(result)

    def __divide_operands_in_arith_mul__(self, operands: list):
        """
        :param operands:
        :return: variables, constant
        """
        variables, constant = list(), 1
        for operand in operands:
            if operand.sym_type == CSymbolType.Constant:
                number = self.__number__(operand.content)
                constant = constant * number
            else:
                variables.append(operand)
        return variables, constant

    def __reconstruct_for_arith_mul__(self, expression: CSymbolNode, variables: list, constant):
        """
        :param expression:
        :param variables:
        :param constant:
        :return: standard generation of arithmetic multiplication
        """
        self.__done__()
        if constant == 0:
            return CSymbolNode(CSymbolType.Constant, expression.data_type, 0)
        else:
            operands = list()
            if constant != 1:
                operands.append(CSymbolNode(CSymbolType.Constant, expression.data_type, constant))
            for variable in variables:
                variable: CSymbolNode
                operands.append(variable)
            if len(operands) == 0:
                return CSymbolNode(CSymbolType.Constant, expression.data_type, 1)
            elif len(operands) == 1:
                return operands[0]
            else:
                result = CSymbolNode(CSymbolType.MultiExpression, expression.data_type, base.COperator.arith_mul)
                for operand in operands:
                    result.add_child(operand)
                return result

    def __eval_arith_mul__(self, expression: CSymbolNode):
        """
        :param expression:
        :return:
        """
        # 1. collect the operands in the same level
        operands = self.__extend_operands__(expression)
        # 2. divide operands into variables and constant
        variables, constant = self.__divide_operands_in_arith_mul__(operands)
        # 3. generate standard format of arith multiply
        return self.__reconstruct_for_arith_mul__(expression, variables, constant)

    def __eval_arith_div__(self, expression: CSymbolNode):
        """
        :param expression:
        :return:
            (1) constant % constant
            (2) 0 / any
            (3) any / 1 or -1
            (4) [loperand] / [roperand]
        """
        l_operand = self.evaluate(expression.get_child(0))
        r_operand = self.evaluate(expression.get_child(1))
        if l_operand.sym_type == CSymbolType.Constant and self.__number__(l_operand.content) == 0:
            return CSymbolNode(CSymbolType.Constant, expression.data_type, 0)
        elif r_operand.sym_type == CSymbolType.Constant and self.__number__(r_operand.content) == 1:
            return l_operand
        elif r_operand.sym_type == CSymbolType.Constant and self.__number__(r_operand.content) == -1:
            result = CSymbolNode(CSymbolType.UnaryExpression, expression.data_type, base.COperator.negative)
            result.add_child(l_operand)
            return self.evaluate(result)
        elif l_operand.sym_type == CSymbolType.Constant and r_operand.sym_type == CSymbolType.Constant:
            l_value = self.__number__(l_operand.content)
            r_value = self.__number__(r_operand.content)
            if expression.get_data_type().is_real_type():
                constant = l_value / r_value
            else:
                constant = l_value // r_value
            return CSymbolNode(CSymbolType.Constant, expression.data_type, constant)
        else:
            result = CSymbolNode(CSymbolType.BinaryExpression, expression.data_type, base.COperator.arith_div)
            result.add_child(l_operand)
            result.add_child(r_operand)
            return result

    def __eval_arith_mod__(self, expression: CSymbolNode):
        """
        :param expression:
        :return:
            (1) constant % constant
            (2) 0, 1, -1 % any
            (3) any % 1, -1
            (4) [loperand] % [roperand]
        """
        l_operand = self.evaluate(expression.get_child(0))
        r_operand = self.evaluate(expression.get_child(1))
        if l_operand.sym_type == CSymbolType.Constant and self.__integer__(l_operand.content) == 0:
            return CSymbolNode(CSymbolType.Constant, expression.data_type, 0)
        elif l_operand.sym_type == CSymbolType.Constant and self.__integer__(l_operand.content) == 1:
            return CSymbolNode(CSymbolType.Constant, expression.data_type, 1)
        elif l_operand.sym_type == CSymbolType.Constant and self.__integer__(l_operand.content) == -1:
            return CSymbolNode(CSymbolType.Constant, expression.data_type, 1)
        elif r_operand.sym_type == CSymbolType.Constant and self.__integer__(r_operand.content) == 1:
            return CSymbolNode(CSymbolType.Constant, expression.data_type, 0)
        elif r_operand.sym_type == CSymbolType.Constant and self.__integer__(r_operand.content) == -1:
            return CSymbolNode(CSymbolType.Constant, expression.data_type, 0)
        elif l_operand.sym_type == CSymbolType.Constant and r_operand.sym_type == CSymbolType.Constant:
            l_value = self.__integer__(l_operand.content)
            r_value = self.__integer__(r_operand.content)
            return CSymbolNode(CSymbolType.Constant, expression.data_type, l_value % r_value)
        else:
            result = CSymbolNode(CSymbolType.BinaryExpression, expression.data_type, base.COperator.arith_mod)
            result.add_child(l_operand)
            result.add_child(r_operand)
            return result

    def __divide_operands_in_bitws_and__(self, operands: list):
        """
        :param operands:
        :return: variables, constant
        """
        variables, constant = list(), ~0
        for operand in operands:
            operand: CSymbolNode
            if operand.sym_type == CSymbolType.Constant:
                number = self.__integer__(operand.content)
                constant = constant & number
            else:
                variables.append(operand)
        return variables, constant

    def __reconstruct_for_bitws_and__(self, expression: CSymbolNode, variables: list, constant: int):
        self.__done__()
        if constant == 0:
            return CSymbolNode(CSymbolType.Constant, expression.data_type, 0)
        else:
            operands = list()
            if constant != ~0:
                operands.append(CSymbolNode(CSymbolType.Constant, expression.data_type, constant))
            for variable in variables:
                variable: CSymbolNode
                operands.append(variable)
            if len(operands) == 0:
                return CSymbolNode(CSymbolType.Constant, expression.data_type, ~0)
            elif len(operands) == 1:
                return operands[0]
            else:
                result = CSymbolNode(CSymbolType.MultiExpression, expression.data_type, base.COperator.bitws_and)
                for operand in operands:
                    result.add_child(operand)
                return result

    def __eval_bitws_and__(self, expression: CSymbolNode):
        """
        :param expression:
        :return:
        """
        operands = self.__extend_operands__(expression)
        variables, constant = self.__divide_operands_in_bitws_and__(operands)
        return self.__reconstruct_for_bitws_and__(expression, variables, constant)

    def __divide_operands_in_bitws_ior__(self, operands: list):
        """
        :param operands:
        :return: variables, constant
        """
        variables, constant = list(), 0
        for operand in operands:
            operand: CSymbolNode
            if operand.sym_type == CSymbolType.Constant:
                number = self.__integer__(operand.content)
                constant = constant | number
            else:
                variables.append(operand)
        return variables, constant

    def __reconstruct_for_bitws_ior__(self, expression: CSymbolNode, variables: list, constant: int):
        self.__done__()
        if constant == ~0:
            return CSymbolNode(CSymbolType.Constant, expression.data_type, ~0)
        else:
            operands = list()
            if constant != 0:
                operands.append(CSymbolNode(CSymbolType.Constant, expression.data_type, constant))
            for variable in variables:
                variable: CSymbolNode
                operands.append(variable)
            if len(operands) == 0:
                return CSymbolNode(CSymbolType.Constant, expression.data_type, 0)
            elif len(operands) == 1:
                return operands[0]
            else:
                result = CSymbolNode(CSymbolType.Constant, expression.data_type, base.COperator.bitws_ior)
                for operand in operands:
                    result.add_child(operand)
                return result

    def __eval_bitws_ior__(self, expression: CSymbolNode):
        """
        :param expression:
        :return:
        """
        operands = self.__extend_operands__(expression)
        variables, constant = self.__divide_operands_in_bitws_ior__(operands)
        return self.__reconstruct_for_bitws_ior__(expression, variables, constant)

    def __divide_operands_in_bitws_xor__(self, operands: list):
        variables, constant = list(), 0
        for operand in operands:
            operand: CSymbolNode
            if operand.sym_type == CSymbolType.Constant:
                number = self.__integer__(operand.content)
                constant = constant ^ number
            else:
                variables.append(operand)
        return variables, constant

    def __reconstruct_for_bitws_xor__(self, expression: CSymbolNode, variables: list, constant: int):
        self.__done__()
        operands = list()
        if constant != 0:
            operands.append(CSymbolNode(CSymbolType.Constant, expression.data_type, constant))
        for variable in variables:
            variable: CSymbolNode
            operands.append(variable)
        if len(operands) == 0:
            return CSymbolNode(CSymbolType.Constant, expression.data_type, 0)
        elif len(operands) == 1:
            return operands[0]
        else:
            result = CSymbolNode(CSymbolType.MultiExpression, expression.data_type, base.COperator.bitws_xor)
            for operand in operands:
                result.add_child(operand)
            return result

    def __eval_bitws_xor__(self, expression: CSymbolNode):
        operands = self.__extend_operands__(expression)
        variables, constant = self.__divide_operands_in_bitws_xor__(operands)
        return self.__reconstruct_for_bitws_xor__(expression, variables, constant)

    def __eval_bitws_lsh__(self, expression: CSymbolNode):
        """
        :param expression:
        :return:
            (1) constant << constant
            (2) 0 << ant
            (3) any << 0
            (4) otherwise
        """
        l_operand = self.evaluate(expression.get_child(0))
        r_operand = self.evaluate(expression.get_child(1))
        if l_operand.sym_type == CSymbolType.Constant and self.__integer__(l_operand.content) == 0:
            return CSymbolNode(CSymbolType.Constant, expression.data_type, 0)
        elif r_operand.sym_type == CSymbolType.Constant and self.__integer__(r_operand.content) == 0:
            return l_operand
        elif l_operand.sym_type == CSymbolType.Constant and r_operand.sym_type == CSymbolType.Constant:
            l_value = self.__integer__(l_operand.content)
            r_value = self.__integer__(r_operand.content)
            return CSymbolNode(CSymbolType.Constant, expression.data_type, l_value << r_value)
        else:
            result = CSymbolNode(CSymbolType.BinaryExpression, expression.data_type, base.COperator.bitws_lsh)
            result.add_child(l_operand)
            result.add_child(r_operand)
            return result

    def __eval_bitws_rsh__(self, expression: CSymbolNode):
        """
        :param expression:
        :return:
            (1) constant >> constant
            (2) 0 >> ant
            (3) any >> 0
            (4) otherwise
        """
        l_operand = self.evaluate(expression.get_child(0))
        r_operand = self.evaluate(expression.get_child(1))
        if l_operand.sym_type == CSymbolType.Constant and self.__integer__(l_operand.content) == 0:
            return CSymbolNode(CSymbolType.Constant, expression.data_type, 0)
        elif r_operand.sym_type == CSymbolType.Constant and self.__integer__(r_operand.content) == 0:
            return l_operand
        elif l_operand.sym_type == CSymbolType.Constant and r_operand.sym_type == CSymbolType.Constant:
            l_value = self.__integer__(l_operand.content)
            r_value = self.__integer__(r_operand.content)
            return CSymbolNode(CSymbolType.Constant, expression.data_type, l_value >> r_value)
        else:
            result = CSymbolNode(CSymbolType.BinaryExpression, expression.data_type, base.COperator.bitws_rsh)
            result.add_child(l_operand)
            result.add_child(r_operand)
            return result

    def __divide_operands_in_logic_and__(self, operands: list):
        variables, constant = list(), True
        for operand in operands:
            operand: CSymbolNode
            if operand.sym_type == CSymbolType.Constant:
                value = self.__boolean__(operand.content)
                constant = (constant and value)
            else:
                variables.append(operand)
        return variables, constant

    def __reconstruct_for_logic_and__(self, expression: CSymbolNode, variables: list, constant: bool):
        self.__done__()
        if constant:
            if len(variables) == 0:
                return CSymbolNode(CSymbolType.Constant, expression.data_type, True)
            elif len(variables) == 1:
                result = variables[0]
                result: CSymbolNode
                return result
            else:
                result = CSymbolNode(CSymbolType.MultiExpression, expression.data_type, base.COperator.logic_and)
                for variable in variables:
                    result.add_child(variable)
                return result
        else:
            return CSymbolNode(CSymbolType.Constant, expression.data_type, False)

    def __eval_logic_and__(self, expression: CSymbolNode):
        operands = self.__extend_operands__(expression)
        variables, constant = self.__divide_operands_in_logic_and__(operands)
        return self.__reconstruct_for_logic_and__(expression, variables, constant)

    def __divide_operands_in_logic_ior__(self, operands: list):
        variables, constant = list(), False
        for operand in operands:
            operand: CSymbolNode
            if operand.sym_type == CSymbolType.Constant:
                value = self.__boolean__(operand.content)
                constant = (constant or value)
            else:
                variables.append(operand)
        return variables, constant

    def __reconstruct_for_logic_ior__(self, expression: CSymbolNode, variables: list, constant: bool):
        self.__done__()
        if constant:
            return CSymbolNode(CSymbolType.Constant, expression.data_type, True)
        else:
            if len(variables) == 0:
                return CSymbolNode(CSymbolType.Constant, expression.data_type, False)
            elif len(variables) == 1:
                variables[0]: CSymbolNode
                return variables[0]
            else:
                result = CSymbolNode(CSymbolType.MultiExpression, expression.data_type, base.COperator.logic_ior)
                for variable in variables:
                    result.add_child(variable)
                return result

    def __eval_logic_ior__(self, expression: CSymbolNode):
        operands = self.__extend_operands__(expression)
        variables, constant = self.__divide_operands_in_logic_ior__(operands)
        return self.__reconstruct_for_logic_ior__(expression, variables, constant)

    def __eval_greater_tn__(self, expression: CSymbolNode):
        l_operand = self.evaluate(expression.get_child(0))
        r_operand = self.evaluate(expression.get_child(1))
        if l_operand.sym_type == CSymbolType.Constant and r_operand.sym_type == CSymbolType.Constant:
            l_value = self.__number__(l_operand.content)
            r_value = self.__number__(r_operand.content)
            return CSymbolNode(CSymbolType.Constant, expression.data_type, l_value > r_value)
        else:
            result = CSymbolNode(CSymbolType.BinaryExpression, expression.data_type, base.COperator.smaller_tn)
            result.add_child(r_operand)
            result.add_child(l_operand)
            return result

    def __eval_greater_eq__(self, expression: CSymbolNode):
        l_operand = self.evaluate(expression.get_child(0))
        r_operand = self.evaluate(expression.get_child(1))
        if l_operand.sym_type == CSymbolType.Constant and r_operand.sym_type == CSymbolType.Constant:
            l_value = self.__number__(l_operand.content)
            r_value = self.__number__(r_operand.content)
            return CSymbolNode(CSymbolType.Constant, expression.data_type, l_value >= r_value)
        else:
            result = CSymbolNode(CSymbolType.BinaryExpression, expression.data_type, base.COperator.smaller_eq)
            result.add_child(r_operand)
            result.add_child(l_operand)
            return result

    def __eval_smaller_tn__(self, expression: CSymbolNode):
        l_operand = self.evaluate(expression.get_child(0))
        r_operand = self.evaluate(expression.get_child(1))
        if l_operand.sym_type == CSymbolType.Constant and r_operand.sym_type == CSymbolType.Constant:
            l_value = self.__number__(l_operand.content)
            r_value = self.__number__(r_operand.content)
            return CSymbolNode(CSymbolType.Constant, expression.data_type, l_value < r_value)
        else:
            result = CSymbolNode(CSymbolType.BinaryExpression, expression.data_type, base.COperator.smaller_tn)
            result.add_child(l_operand)
            result.add_child(r_operand)
            return result

    def __eval_smaller_eq__(self, expression: CSymbolNode):
        l_operand = self.evaluate(expression.get_child(0))
        r_operand = self.evaluate(expression.get_child(1))
        if l_operand.sym_type == CSymbolType.Constant and r_operand.sym_type == CSymbolType.Constant:
            l_value = self.__number__(l_operand.content)
            r_value = self.__number__(r_operand.content)
            return CSymbolNode(CSymbolType.Constant, expression.data_type, l_value <= r_value)
        else:
            result = CSymbolNode(CSymbolType.BinaryExpression, expression.data_type, base.COperator.smaller_eq)
            result.add_child(l_operand)
            result.add_child(r_operand)
            return result

    def __eval_equal_with__(self, expression: CSymbolNode):
        l_operand = self.evaluate(expression.get_child(0))
        r_operand = self.evaluate(expression.get_child(1))
        if l_operand.sym_type == CSymbolType.Constant and r_operand.sym_type == CSymbolType.Constant:
            l_value = self.__number__(l_operand.content)
            r_value = self.__number__(r_operand.content)
            return CSymbolNode(CSymbolType.Constant, expression.data_type, l_value == r_value)
        else:
            result = CSymbolNode(CSymbolType.BinaryExpression, expression.data_type, base.COperator.equal_with)
            result.add_child(l_operand)
            result.add_child(r_operand)
            return result

    def __eval_not_equals__(self, expression: CSymbolNode):
        l_operand = self.evaluate(expression.get_child(0))
        r_operand = self.evaluate(expression.get_child(1))
        if l_operand.sym_type == CSymbolType.Constant and r_operand.sym_type == CSymbolType.Constant:
            l_value = self.__number__(l_operand.content)
            r_value = self.__number__(r_operand.content)
            return CSymbolNode(CSymbolType.Constant, expression.data_type, l_value != r_value)
        else:
            result = CSymbolNode(CSymbolType.BinaryExpression, expression.data_type, base.COperator.not_equals)
            result.add_child(l_operand)
            result.add_child(r_operand)
            return result


sym_evaluator = CSymEvaluator()


class CSymTemplate:
    """
    [sym_template_expression{@input, @any}; cir_source_list+]
    """
    def __init__(self, sym_template: CSymbolNode, cir_nodes: list):
        self.sym_template = sym_template
        self.cir_source_list = list()
        for cir_node in cir_nodes:
            cir_node: cirtree.CirNode
            self.cir_source_list.append(cir_node)
        return

    def get_sym_template(self):
        """
        :return: template of symbolic expression with @input and @any
        """
        return self.sym_template

    def get_cir_source_list(self):
        """
        :return: source w.r.t. @input in CIR-source code
        """
        return self.cir_source_list

    @staticmethod
    def sym_cir_associations(sym_root: CSymbolNode, cir_root: cirtree.CirNode):
        """
        :param sym_root:
        :param cir_root:
        :return: dict[sym_node, list[cir_node]]: the cir-location(s) to the entry of @input in the symbolic expression
        """
        # 1. collect references in cir_tree
        cir_references = set()
        cir_queue = deque()
        cir_queue.append(cir_root)
        while len(cir_queue) > 0:
            cir_node = cir_queue.popleft()
            cir_node: cirtree.CirNode
            if cir_node.is_reference_expression():
                cir_references.add(cir_node)
            for child in cir_node.get_children():
                cir_queue.append(child)
        # 2. collect references in symbolic root
        sym_references = set()
        sym_queue = deque()
        sym_queue.append(sym_root)
        while len(sym_queue) > 0:
            sym_node = sym_queue.popleft()
            sym_node: CSymbolNode
            if sym_node.is_reference():
                sym_references.add(sym_node)
            for child in sym_node.get_children():
                sym_queue.append(child)
        # 3. generate association maps from sym-reference to its cir-reference(s)
        sym_cir_dict = dict()
        for sym_reference in sym_references:
            cir_buffer = list()
            for cir_reference in cir_references:
                if sym_reference.generate_code(True) == sym_parser.parse_by_cir_tree(cir_reference).generate_code(True):
                    cir_buffer.append(cir_reference)
            sym_cir_dict[sym_reference] = cir_buffer
        return sym_cir_dict

    @staticmethod
    def __input_entry__(data_type: base.CType):
        """
        :param data_type:
        :return: *(@input)
        """
        address_type = base.CType(base.CMetaType.PointType)
        address_type.get_operands().append(data_type)
        address = CSymbolNode(CSymbolType.Address, address_type, "@Input")
        result = CSymbolNode(CSymbolType.UnaryExpression, data_type, base.COperator.dereference)
        result.add_child(address)
        return result

    @staticmethod
    def __any_replacement__(data_type: base.CType):
        """
        :param data_type:
        :return: *(@any)
        """
        address_type = base.CType(base.CMetaType.PointType)
        address_type.get_operands().append(data_type)
        address = CSymbolNode(CSymbolType.Address, address_type, "@Any")
        result = CSymbolNode(CSymbolType.UnaryExpression, data_type, base.COperator.dereference)
        result.add_child(address)
        return result

    @staticmethod
    def __deep_subtree__(sym_root: CSymbolNode, max_depth: int):
        """
        :param sym_root:
        :param max_depth:
        :return: symbolic subtree cloned from root w.r.t. maximal depth of specified
        """
        if max_depth <= 0:
            return CSymTemplate.__any_replacement__(sym_root.data_type)
        else:
            result = CSymbolNode(sym_root.sym_type, sym_root.data_type, sym_root.content)
            for child in sym_root.get_children():
                child_result = CSymTemplate.__deep_subtree__(child, max_depth - 1)
                result.add_child(child_result)
            return result

    @staticmethod
    def path_template(sym_reference: CSymbolNode, cir_references: list, max_depth=0):
        """
        :param sym_reference: replaced as @input
        :param cir_references: as the source of @input in template expression
        :param max_depth: maximal depth of the subtree along the path to root
        :return:
        """
        # 1. original tree and clone tree
        sym_node = sym_reference
        sym_clone_node = CSymTemplate.__input_entry__(sym_reference.data_type)
        # 2. generate from the root node
        while sym_node.get_parent() is not None:
            sym_parent = sym_node.get_parent()
            sym_clone_parent = CSymbolNode(sym_parent.sym_type, sym_parent.data_type, sym_parent.content)
            for sym_child in sym_parent.get_children():
                if sym_child == sym_node:
                    sym_clone_parent.add_child(sym_clone_node)
                else:
                    sym_clone_parent.add_child(CSymTemplate.__deep_subtree__(sym_child, max_depth))
            sym_node = sym_parent
            sym_clone_node = sym_clone_parent
        # 3. construct the symbolic template
        return CSymTemplate(sym_clone_node, cir_references)

    @staticmethod
    def templates(sym_root: CSymbolNode, cir_root: cirtree.CirNode, max_depth: int):
        """
        :param sym_root:
        :param cir_root:
        :param max_depth:
        :return: set of symbolic templates generated from symbolic root and cir references in specified root.
        """
        sym_cir_associations = CSymTemplate.sym_cir_associations(sym_root, cir_root)
        sym_templates = set()
        for sym_node, cir_nodes in sym_cir_associations.items():
            if len(cir_nodes) > 0:
                sym_template = CSymTemplate.path_template(sym_node, cir_nodes, max_depth)
                sym_templates.add(sym_template)
        return sym_templates


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
    sym_evaluator.__debugging__ = True
    node2 = sym_evaluator.evaluate(node)
    print(node2.generate_code(True))
    print(len(node.children))
    print(base.CType.parse("(function (pointer (pointer (array 27 short))))"))

