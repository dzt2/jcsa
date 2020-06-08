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
        target = CSymbolNode(CSymbolType.UnaryExpression, source.get_data_type(), base.COperator.dereference)
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
            target = CSymbolNode(CSymbolType.UnaryExpression, source.get_data_type(), base.COperator.negative)
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
            target = CSymbolNode(CSymbolType.UnaryExpression, source.get_data_type(), base.COperator.bitws_rsv)
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
            target = CSymbolNode(CSymbolType.UnaryExpression, source.get_data_type(), base.COperator.logic_not)
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

