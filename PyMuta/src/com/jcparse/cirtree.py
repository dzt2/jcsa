"""
C-intermediate representation models are:
        CirType, CirNode, CirTree   ==> AstTree
"""


from enum import Enum
import os
import src.com.jcparse.base as base
import src.com.jcparse.astree as astree


class CirType(Enum):
    argument_list = 0
    function_definition = 1
    field = 2
    function_body = 3
    label = 4
    Type = 5
    transition_unit = 6
    defer_expression = 7
    field_expression = 8
    declarator = 9
    identifier = 10
    implicator = 11
    return_point = 12
    address_expression = 13
    cast_expression = 14
    constant = 16
    default_value = 17
    initializer_body = 18
    literal = 19
    wait_expression = 20
    binary_assign_statement = 21
    incre_assign_statement = 22
    init_assign_statement = 23
    return_assign_statement = 24
    save_assign_statement = 25
    wait_assign_statement = 26
    call_statement = 27
    case_statement = 28
    goto_statement = 29
    if_statement = 30
    beg_statement = 31
    end_statement = 32
    case_end_statement = 33
    if_end_statement = 34
    default_statement = 35
    labeled_statement = 36
    arith_expression = 37
    bitws_expression = 38
    logic_expression = 39
    relational_expression = 40

    def __str__(self):
        return self.name

    @staticmethod
    def parse(text: str):
        if text == "TransitionUnit":
            return CirType.transition_unit
        elif text == "ArgumentList":
            return CirType.argument_list
        elif text == "FunctionDefinition":
            return CirType.function_definition
        elif text == "FunctionBody":
            return CirType.function_body
        elif text == "Label":
            return CirType.label
        elif text == "Type":
            return CirType.Type
        elif text == "Field":
            return CirType.field
        elif text == "BinAssignStatement":
            return CirType.binary_assign_statement
        elif text == "IncreAssignStatement":
            return CirType.incre_assign_statement
        elif text == "InitAssignStatement":
            return CirType.init_assign_statement
        elif text == "ReturnAssignStatement":
            return CirType.return_assign_statement
        elif text == "SaveAssignStatement":
            return CirType.save_assign_statement
        elif text == "WaitAssignStatement":
            return CirType.wait_assign_statement
        elif text == "CallStatement":
            return CirType.call_statement
        elif text == "CaseStatement":
            return CirType.case_statement
        elif text == "GotoStatement":
            return CirType.goto_statement
        elif text == "IfStatement":
            return CirType.if_statement
        elif text == "BegStatement":
            return CirType.beg_statement
        elif text == "EndStatement":
            return CirType.end_statement
        elif text == "IfEndStatement":
            return CirType.if_end_statement
        elif text == "CaseEndStatement":
            return CirType.case_end_statement
        elif text == "DefaultStatement":
            return CirType.default_statement
        elif text == "LabelStatement":
            return CirType.labeled_statement
        elif text == "Declarator":
            return CirType.declarator
        elif text == "Identifier":
            return CirType.identifier
        elif text == "Implicator":
            return CirType.implicator
        elif text == "ReturnPoint":
            return CirType.return_point
        elif text == "FieldExpression":
            return CirType.field_expression
        elif text == "DeferExpression":
            return CirType.defer_expression
        elif text == "AddressExpression":
            return CirType.address_expression
        elif text == "ArithExpression":
            return CirType.arith_expression
        elif text == "BitwsExpression":
            return CirType.bitws_expression
        elif text == "LogicExpression":
            return CirType.logic_expression
        elif text == "RelationExpression":
            return CirType.relational_expression
        elif text == "CastExpression":
            return CirType.cast_expression
        elif text == "ConstExpression":
            return CirType.constant
        elif text == "StringLiteral":
            return CirType.literal
        elif text == "InitializerBody":
            return CirType.initializer_body
        elif text == "DefaultValue":
            return CirType.default_value
        elif text == "WaitExpression":
            return CirType.wait_expression
        else:
            return None


class CirNode:
    """
    C-intermediate representation tree node: {tree, id; parent, children; cir_type, data_type, content}
        (1) declarator, identifier, implicator, return_point, field ==> name {String}
        (2) defer_expression, field_expression, address_expression, cast_expression, arith_expression,
            bitws_expression, logic_expression, relational_expression   ==> operator {COperator}
        (3) label   ==> label {int}
        (4) literal ==> literal {String}
        (5) constant    ==> bool or int or double
    """
    def __init__(self, tree, id: int, ast_source: astree.AstNode, cir_type: CirType, data_type: base.CType, content):
        """
        create an isolated node in C-intermediate representation tree of code
        :param tree:
        :param id:
        :param ast_source
        :param cir_type:
        :param data_type:
        :param content:
        """
        self.tree = tree
        self.id = id
        self.ast_source = ast_source
        self.cir_type = cir_type
        self.data_type = data_type
        self.content = content
        self.parent = None
        self.children = list()
        return

    def get_tree(self):
        return self.tree

    def get_id(self):
        return self.id

    def get_ast_source(self):
        return self.ast_source

    def get_cir_type(self):
        return self.cir_type

    def get_data_type(self):
        return self.data_type

    def get_parent(self):
        return self.parent

    def get_children(self):
        return self.children

    def get_child(self, k: int):
        return self.children[k]

    def number_of_children(self):
        return len(self.children)

    def is_name_expression(self):
        return self.cir_type == CirType.declarator or self.cir_type == CirType.identifier or \
               self.cir_type == CirType.implicator or self.cir_type == CirType.return_point

    def is_reference_expression(self):
        return self.is_name_expression() or self.cir_type == CirType.defer_expression or \
               self.cir_type == CirType.field_expression

    def is_operation_expression(self):
        return self.cir_type == CirType.address_expression or self.cir_type == CirType.defer_expression or \
               self.cir_type == CirType.arith_expression or self.cir_type == CirType.bitws_expression or \
               self.cir_type == CirType.logic_expression or self.cir_type == CirType.relational_expression or \
               self.cir_type == CirType.field_expression or self.cir_type == CirType.cast_expression

    def is_computational_expression(self):
        return self.cir_type == CirType.arith_expression or self.cir_type == CirType.bitws_expression or \
               self.cir_type == CirType.logic_expression or self.cir_type == CirType.relational_expression

    def is_value_expression(self):
        return self.cir_type == CirType.cast_expression or self.cir_type == CirType.wait_expression or \
               self.cir_type == CirType.constant or self.cir_type == CirType.literal or \
               self.cir_type == CirType.default_value or self.cir_type == CirType.initializer_body or \
               self.is_computational_expression()

    def is_expression(self):
        return self.is_reference_expression() or self.is_value_expression()

    def is_assign_statement(self):
        return self.cir_type == CirType.binary_assign_statement or self.cir_type == CirType.incre_assign_statement or \
               self.cir_type == CirType.save_assign_statement or self.cir_type == CirType.return_assign_statement or \
               self.cir_type == CirType.wait_assign_statement or self.cir_type == CirType.init_assign_statement

    def is_tag_statement(self):
        return self.cir_type == CirType.beg_statement or self.cir_type == CirType.end_statement or \
               self.cir_type == CirType.if_end_statement or self.cir_type == CirType.case_end_statement or \
               self.cir_type == CirType.default_statement or self.cir_type == CirType.labeled_statement

    def is_statement(self):
        return self.is_assign_statement() or self.is_tag_statement() or self.cir_type == CirType.call_statement or \
               self.cir_type == CirType.if_statement or self.cir_type == CirType.goto_statement or \
               self.cir_type == CirType.case_statement

    def get_constant(self):
        """
        :return: bool | int | double
        """
        if self.cir_type == CirType.constant:
            return self.content
        else:
            return None

    def get_literal(self):
        if self.cir_type == CirType.literal:
            return self.content
        else:
            return None

    def get_name(self):
        """
        :return: name of the identifier as expression or field
        """
        if self.is_name_expression() or self.cir_type == CirType.field:
            return self.content
        else:
            return None

    def get_label(self):
        if self.cir_type == CirType.label:
            return self.content
        else:
            return None

    def get_operator(self):
        """
        :return: address_expression (&), cast_expression (=), defer_expression (*), field_expression (.),
                arith_expression {+, -, *, /, %, +, -}, bitws_expression {~, &, |, ^, <<, >>}, logic_expression {!}
                and relational_expression {<, <=, >, >=, ==, !=}
        """
        if isinstance(self.content, base.COperator):
            return self.content
        else:
            return None

    def statement_of(self):
        cir_node = self
        while cir_node is not None:
            if cir_node.is_statement():
                return cir_node
            else:
                cir_node = cir_node.parent
        return None

    def function_body_of(self):
        """
        :return: function body where the node belongs to
        """
        cir_node = self
        while cir_node is not None:
            if cir_node.cir_type == CirType.function_body:
                return cir_node
            else:
                cir_node = cir_node.parent
        return None

    def generate_code(self, simplified=False):
        if self.is_name_expression():
            if simplified:
                name = str(self.content)
                index = name.index("#")
                simple_name = name[0:index].strip()
                if len(simple_name) == 0:
                    return name
                else:
                    return simple_name
            else:
                return str(self.content)
        elif self.cir_type == CirType.field:
            return str(self.content)
        elif self.cir_type == CirType.defer_expression:
            child = self.children[0]
            child: CirNode
            return "*(" + child.generate_code(simplified) + ")"
        elif self.cir_type == CirType.field_expression:
            body = self.children[0]
            field = self.children[1]
            body: CirNode
            field: CirNode
            return body.generate_code(simplified) + "." + field.generate_code(simplified)
        elif self.cir_type == CirType.constant:
            return str(self.content)
        elif self.cir_type == CirType.literal:
            text = str(self.content)
            buffer = "\""
            for k in range(0, len(text)):
                char = text[k]
                if simplified and char.isspace():
                    buffer += "\\s"
                else:
                    buffer += char
            buffer += "\""
            return buffer
        elif self.cir_type == CirType.default_value:
            return "[?]"
        elif self.cir_type == CirType.initializer_body:
            buffer = "["
            for child in self.children:
                child: CirNode
                buffer += " " + child.generate_code(simplified)
            buffer += " ]"
            return buffer
        elif self.cir_type == CirType.wait_expression:
            child = self.children[0]
            child: CirNode
            return child.generate_code(simplified) + "(...)"
        elif self.cir_type == CirType.cast_expression:
            cast_type = self.children[0]
            value = self.children[1]
            cast_type: CirNode
            value: CirNode
            return "(" + str(cast_type.get_data_type()) + ") (" + value.generate_code(simplified) + ")"
        elif self.cir_type == CirType.address_expression:
            child = self.children[0]
            child: CirNode
            return "&(" + child.generate_code(simplified) + ")"
        elif self.cir_type == CirType.arith_expression:
            operator = self.get_operator()
            if operator == base.COperator.arith_add:
                return "(" + self.children[0].generate_code(simplified) + \
                       ") + (" + self.children[1].generate_code(simplified) + ")"
            elif operator == base.COperator.arith_sub:
                return "(" + self.children[0].generate_code(simplified) + \
                       ") - (" + self.children[1].generate_code(simplified) + ")"
            elif operator == base.COperator.arith_mul:
                return "(" + self.children[0].generate_code(simplified) + \
                       ") * (" + self.children[1].generate_code(simplified) + ")"
            elif operator == base.COperator.arith_div:
                return "(" + self.children[0].generate_code(simplified) + \
                       ") / (" + self.children[1].generate_code(simplified) + ")"
            elif operator == base.COperator.arith_mod:
                return "(" + self.children[0].generate_code(simplified) + \
                       ") % (" + self.children[1].generate_code(simplified) + ")"
            elif operator == base.COperator.negative:
                return "-(" + self.children[0].generate_code(simplified) + ")"
            elif operator == base.COperator.positive:
                return self.children[0].generate_code(simplified)
            else:
                return None
        elif self.cir_type == CirType.bitws_expression:
            operator = self.get_operator()
            if operator == base.COperator.bitws_and:
                return "(" + self.children[0].generate_code(simplified) + \
                       ") & (" + self.children[1].generate_code(simplified) + ")"
            elif operator == base.COperator.bitws_ior:
                return "(" + self.children[0].generate_code(simplified) + \
                       ") | (" + self.children[1].generate_code(simplified) + ")"
            elif operator == base.COperator.bitws_xor:
                return "(" + self.children[0].generate_code(simplified) + \
                       ") ^ (" + self.children[1].generate_code(simplified) + ")"
            elif operator == base.COperator.bitws_lsh:
                return "(" + self.children[0].generate_code(simplified) + \
                       ") << (" + self.children[1].generate_code(simplified) + ")"
            elif operator == base.COperator.bitws_rsh:
                return "(" + self.children[0].generate_code(simplified) + \
                       ") >> (" + self.children[1].generate_code(simplified) + ")"
            elif operator == base.COperator.bitws_rsv:
                return "~(" + self.children[0].generate_code(simplified) + ")"
            else:
                return None
        elif self.cir_type == CirType.logic_expression:
            operator = self.get_operator()
            if operator == base.COperator.logic_not:
                return "!(" + self.children[0].generate_code(simplified) + ")"
            else:
                return None
        elif self.cir_type == CirType.relational_expression:
            operator = self.get_operator()
            if operator == base.COperator.greater_tn:
                return "(" + self.children[0].generate_code(simplified) + \
                       ") > (" + self.children[1].generate_code(simplified) + ")"
            elif operator == base.COperator.greater_eq:
                return "(" + self.children[0].generate_code(simplified) + \
                       ") >= (" + self.children[1].generate_code(simplified) + ")"
            elif operator == base.COperator.smaller_tn:
                return "(" + self.children[0].generate_code(simplified) + \
                       ") < (" + self.children[1].generate_code(simplified) + ")"
            elif operator == base.COperator.smaller_eq:
                return "(" + self.children[0].generate_code(simplified) + \
                       ") <= (" + self.children[1].generate_code(simplified) + ")"
            elif operator == base.COperator.equal_with:
                return "(" + self.children[0].generate_code(simplified) + \
                       ") == (" + self.children[1].generate_code(simplified) + ")"
            elif operator == base.COperator.not_equals:
                return "(" + self.children[0].generate_code(simplified) + \
                       ") != (" + self.children[1].generate_code(simplified) + ")"
            else:
                return None
        elif self.cir_type == CirType.Type:
            return str(self.data_type)
        elif self.is_assign_statement():
            return self.children[0].generate_code(simplified) + " := " + \
                   self.children[1].generate_code(simplified) + ";"
        elif self.cir_type == CirType.goto_statement:
            return "goto " + self.children[0].generate_code(simplified) + ";"
        elif self.cir_type == CirType.label:
            return "[" + str(self.content) + "]"
        elif self.cir_type == CirType.if_statement:
            return "if(" + self.children[0].generate_code(simplified) + ") then " + \
                   self.children[1].generate_code(simplified) + " or " + \
                   self.children[2].generate_code(simplified) + ";"
        elif self.cir_type == CirType.case_statement:
            return "if_case(" + self.children[0].generate_code(simplified) + ") then next or " + \
                   self.children[1].generate_code(simplified) + ";"
        elif self.cir_type == CirType.call_statement:
            return "call " + self.children[0].generate_code(simplified) + " for " + \
                   self.children[1].generate_code(simplified) + ";"
        elif self.cir_type == CirType.argument_list:
            buffer = "("
            for child in self.children:
                buffer += " " + child.generate_code(simplified)
            buffer += " )"
            return buffer
        elif self.cir_type == CirType.beg_statement:
            return "#BEG:"
        elif self.cir_type == CirType.end_statement:
            return "#END:"
        elif self.cir_type == CirType.if_end_statement:
            return "#EndIf:"
        elif self.cir_type == CirType.case_end_statement:
            return "#EndCase:"
        elif self.cir_type == CirType.labeled_statement:
            return "#Label:"
        elif self.cir_type == CirType.default_statement:
            return "#Default:"
        elif self.cir_type == CirType.function_body:
            buffer = "{\n"
            for child in self.children:
                buffer += "\t" + child.generate_code(simplified) + "\n"
            buffer += "}\n"
            return buffer
        elif self.cir_type == CirType.function_definition:
            return "def " + self.children[0].generate_code(simplified) + " " + \
                   self.children[1].generate_code(simplified)
        elif self.cir_type == CirType.transition_unit:
            buffer = ""
            for child in self.children:
                buffer += child.generate_code(simplified) + "\n"
            return buffer
        else:
            return None

    def __str__(self):
        return "cir#" + str(self.id)


class CirTree:
    """
    C-intermediate representation tree as {ast_tree, tree_nodes; ast_cir_index;}
    """

    def __init__(self, ast_tree: astree.AstTree, cir_file: str):
        self.program = None
        self.ast_tree = ast_tree
        self.tree_nodes = list()
        self.ast_cir_index = dict()
        self.__parse__(cir_file)
        return

    def __parse__(self, cir_file: str):
        ''' (1) create nodes '''
        nodes = dict()
        with open(cir_file, 'r') as reader:
            for line in reader:
                line = line.strip()
                if len(line) > 0:
                    items = line.split('\t')
                    while len(items) < 5:
                        items.append("")
                    id = int(items[0].strip())
                    cir_type = CirType.parse(items[1].strip())
                    ast_source = None
                    if len(items[2].strip()) > 0:
                        ast_source = self.ast_tree.get_tree_node(int(items[2].strip()))
                    data_type = base.CType.parse(items[3].strip())
                    content = base.get_content_of(items[4].strip())
                    if cir_type is not None:
                        node = CirNode(self, id, ast_source, cir_type, data_type, content)
                        nodes[id] = node
                        if cir_type == CirType.defer_expression or cir_type == CirType.field_expression or \
                                cir_type == CirType.cast_expression or cir_type == CirType.address_expression or \
                                cir_type == CirType.arith_expression or cir_type == CirType.bitws_expression or \
                                cir_type == CirType.logic_expression or cir_type == CirType.relational_expression:
                            node.content = base.COperator.get_operator_of(content)
                    else:
                        print("Found error for", line.split('\t'))
        ''' (2) create edges '''
        with open(cir_file, 'r') as reader:
            for line in reader:
                line = line.strip()
                if len(line) > 0:
                    items = line.split('\t')
                    parent = nodes[int(items[0].strip())]
                    parent: CirNode
                    for k in range(5, len(items)):
                        child = nodes[int(items[k].strip())]
                        child: CirNode
                        parent.children.append(child)
                        child.parent = parent
        ''' (3) create trees '''
        self.tree_nodes.clear()
        self.ast_cir_index.clear()
        for k in range(0, len(nodes)):
            cir_node = nodes[k]
            self.tree_nodes.append(cir_node)
            cir_node: CirNode
            ast_source = cir_node.get_ast_source()
            if ast_source is not None:
                if ast_source not in self.ast_cir_index:
                    self.ast_cir_index[ast_source] = list()
                self.ast_cir_index[ast_source].append(cir_node)
        return

    def number_of_nodes(self):
        return len(self.tree_nodes)

    def get_nodes(self):
        return self.tree_nodes

    def get_node(self, id: int):
        return self.tree_nodes[id]

    def get_nodes_of(self, ast_source: astree.AstNode):
        """
        :param ast_source:
        :return: cir-nodes corresponding to the AST source node
        """
        if ast_source in self.ast_cir_index:
            return self.ast_cir_index[ast_source]
        else:
            return list()


if __name__ == "__main__":
    prefix = "C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data"
    for filename in os.listdir(prefix):
        directory = os.path.join(prefix, filename)
        source_file = os.path.join(directory, filename + ".c")
        ast_tree_file = os.path.join(directory, filename + ".ast")
        cir_tree_file = os.path.join(directory, filename + ".cir")
        ast_tree = astree.AstTree(source_file, ast_tree_file)
        cir_tree = CirTree(ast_tree, cir_tree_file)
        output_file = os.path.join("C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output", filename + ".ast")
        print("Open the abstract syntax tree and CIR-tree for", filename)
        with open(output_file, 'w') as writer:
            for cir_node in cir_tree.get_nodes():
                cir_node: CirNode
                writer.write(str(cir_node.id) + "\t")
                if cir_node.parent is not None:
                    writer.write(str(cir_node.parent.id) + "\t")
                else:
                    writer.write("ROOT\t")
                writer.write(str(cir_node.cir_type) + "\t")
                if cir_node.data_type is not None:
                    writer.write(str(cir_node.get_data_type()) + "\t")
                else:
                    writer.write("\t")
                if cir_node.content is not None:
                    writer.write(str(cir_node.content) + "\t")
                else:
                    writer.write("\t")
                code = ""
                # if cir_node.ast_source is not None:
                #    code = cir_node.get_ast_source().get_code(True)
                code = cir_node.generate_code(True)
                writer.write("\"" + code + "\"")
                for child in cir_node.children:
                    writer.write("\t" + str(child.id))
                writer.write("\n")
    print("Testing end for all...")
