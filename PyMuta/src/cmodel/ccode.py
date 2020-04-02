"""
ccode.py defines the data mining describing the source code element, including:
    SourceCode
    AstTree
    AstNode
    CirTree
    CirNode
"""


import os
from collections import deque

import src.cmodel.ctoken as ctoken
import src.cmodel.ctype as ctype


class SourceCode:
    """
    Used to extract the code segment based on location range.
    """

    def __init__(self, program, file_path: str):
        self.program = program
        self.text = ''
        self.line = list()
        self.__read__(file_path)
        return

    def __read__(self, file_path: str):
        with open(file_path, 'r') as reader:
            total_length = 0
            for line in reader:
                length = len(line)
                self.text += line
                self.line.append(total_length)
                total_length += length
            self.line.append(total_length)
        return

    def get_program(self):
        return self.program

    def get_text(self):
        return self.text

    def __strip_code__(self, beg_index: int, end_index: int):
        code, found_space = '', False
        for index in range(beg_index, end_index):
            char = self.text[index]
            if str.isspace(char):
                found_space = True
            else:
                if found_space:
                    found_space = False
                    code += ' '
                code += char
        return code

    def get_code(self, beg_index: int, end_index: int, strip=False):
        if strip:
            return self.__strip_code__(beg_index, end_index)
        else:
            return self.text[beg_index: end_index]

    def get_length(self):
        return len(self.text)

    def number_of_lines(self):
        return len(self.line) - 1

    def get_code_at_line(self, line: int, strip=False):
        """
        :param line: start from 0
        :param strip:
        :return:
        """
        beg_index = self.line[line]
        end_index = self.line[line + 1]
        return self.get_code(beg_index, end_index, strip)

    def line_of(self, index: int):
        """
        :param index:
        :return: start from 0
        """
        beg, end = 0, len(self.line) - 1
        while beg <= end:
            mid = (beg + end) // 2
            beg_index = self.line[mid]
            end_index = self.line[mid + 1]
            if (index >= beg_index) and (index < end_index):
                return mid
            elif index < beg_index:
                end = mid - 1
            else:
                beg = mid + 1
        return None


class AstNode:
    """
    tree, id, type, beg_index, end_index, data_type, token, parent, children
    """

    def __init__(self, tree, id: int, ast_type: str, beg_index: int,
                 end_index: int, data_type: ctype.CType, token: ctoken.CToken):
        self.tree = tree
        self.id = id
        self.ast_type = ast_type
        self.beg_index = beg_index
        self.end_index = end_index
        self.data_type = data_type
        self.token = token
        self.parent = None
        self.children = list()
        return

    def get_tree(self):
        return self.tree

    def get_id(self):
        return self.id

    def get_ast_type(self):
        return self.ast_type

    def get_beg_index(self):
        return self.beg_index

    def get_end_index(self):
        return self.end_index

    def has_data_type(self):
        return self.data_type is not None

    def get_data_type(self):
        return self.data_type

    def has_token(self):
        return self.token is not None

    def get_token(self):
        return self.token

    def is_root(self):
        return self.parent is None

    def get_parent(self):
        return self.parent

    def is_leaf(self):
        return len(self.children) == 0

    def get_children(self):
        return self.children

    def add_child(self, child):
        child: AstNode
        if (child not in self.children) and (child.parent is None):
            self.children.append(child)
            child.parent = self
        return

    def get_code(self, strip=False):
        source_code = self.tree.program.source_code
        source_code: SourceCode
        return source_code.get_code(self.beg_index, self.end_index, strip)

    def get_code_string(self):
        """
        ast_type[line]: "code_segment"
        :return:
        """
        word = self.ast_type
        word += "["
        line = self.tree.program.source_code.line_of(self.beg_index) + 1
        word += str(line)
        word += "]: \""
        word += self.get_code(True)
        word += "\""
        return word

    def __str__(self):
        return self.get_code_string()


class AstTree:
    def __init__(self, program, file_path: str):
        self.program = program
        self.nodes = list()
        self.__parse__(file_path)
        return

    def get_program(self):
        return self.program

    def get_ast_root(self):
        return self.nodes[0]

    def get_ast_nodes(self):
        return self.nodes

    def size(self):
        return len(self.nodes)

    def __read_nodes__(self, file_path: str):
        first = True
        node_dict = dict()
        with open(file_path, 'r') as reader:
            for line in reader:
                if first:
                    first = False
                else:
                    if len(line.strip()) == 0:
                        continue
                    items = line.strip().split('\t')
                    id = int(items[0].strip())
                    ast_type = items[1].strip()
                    beg_index = int(items[2].strip())
                    end_index = int(items[3].strip())
                    data_type = None
                    if len(items[4].strip()) > 0:
                        data_type = ctype.c_type_parser.parse(items[4].strip())
                    token = None
                    if len(items[5].strip()) > 0:
                        if ast_type == 'Constant':
                            token = ctoken.CToken.get_constant(items[5].strip())
                        else:
                            token = ctoken.CToken.get_token(items[5].strip())
                    ast_node = AstNode(self, id, ast_type, beg_index, end_index, data_type, token)
                    node_dict[id] = ast_node
        for index in range(0, len(node_dict)):
            self.nodes.append(node_dict[index])
        return

    def __read_edges__(self, file_path: str):
        with open(file_path, 'r') as reader:
            first = True
            for line in reader:
                if first:
                    first = False
                else:
                    line = line.strip()
                    if len(line) > 0:
                        items = line.split('\t')
                        parent = self.nodes[int(items[0].strip())]
                        parent: AstNode
                        children_text = items[-1].strip()
                        children_items = children_text.split(' ')
                        for index in range(1, len(children_items) - 1):
                            child = self.nodes[int(children_items[index].strip())]
                            parent.add_child(child)
        return

    def __parse__(self, file_path: str):
        self.nodes.clear()
        self.__read_nodes__(file_path)
        self.__read_edges__(file_path)
        return


__cir_statement_types__ = {
    "BinAssignStatement", "IncreAssignStatement", "InitAssignStatement", "ReturnAssignStatement", "WaitAssignStatement",
    "SaveAssignStatement", "CallStatement", "CaseStatement", "GotoStatement", "IfStatement",
    "BegStatement", "EndStatement", "CaseEndStatement", "DefaultStatement", "LabelStatement", "IfEndStatement"
}

__cir_assign_statement_types__ = {
    "BinAssignStatement", "IncreAssignStatement", "InitAssignStatement", "ReturnAssignStatement", "WaitAssignStatement",
    "SaveAssignStatement"
}

__cir_condition_statement_types__ = {
    "CaseStatement", "IfStatement"
}

__cir_tag_statement_types__ = {
    "BegStatement", "EndStatement", "CaseEndStatement", "DefaultStatement", "LabelStatement", "IfEndStatement"
}

__cir_expression_types__ = {
    "Declarator", "Identifier", "Implicator", "ReturnPoint", "DeferExpression", "FieldExpression",
    "ConstExpression", "StringLiteral", "DefaultValue",
    "AddressExpression", "CastExpression",  "InitializerBody", "WaitExpression",
    "ArithExpression", "BitwsExpression", "LogicExpression", "RelationExpression"
}

__cir_refer_expression_types__ = {
    "Declarator", "Identifier", "Implicator", "ReturnPoint", "DeferExpression", "FieldExpression"
}

__cir_value_expression_types__ = {
    "ConstExpression", "StringLiteral", "DefaultValue",
    "AddressExpression", "CastExpression",  "InitializerBody", "WaitExpression",
    "ArithExpression", "BitwsExpression", "LogicExpression", "RelationExpression"
}


class CirNode:
    """
    [tree id cir_type ast_source data_type token parent children]
    """

    def __init__(self, tree, id: int, cir_type: str, ast_source: AstNode, data_type: ctype.CType, token: ctoken.CToken):
        self.tree = tree
        self.id = id
        self.cir_type = cir_type
        self.ast_source = ast_source
        self.data_type = data_type
        self.token = token
        self.parent = None
        self.children = list()
        return

    def get_tree(self):
        return self.tree

    def get_id(self):
        return self.id

    def get_cir_type(self):
        return self.cir_type

    def has_ast_source(self):
        return self.ast_source is not None

    def get_ast_source(self):
        return self.ast_source

    def has_data_type(self):
        return self.data_type is not None

    def get_data_type(self):
        return self.data_type

    def has_token(self):
        return self.token is not None

    def get_token(self):
        return self.token

    def is_root(self):
        return self.parent is None

    def get_parent(self):
        return self.parent

    def is_leaf(self):
        return len(self.children) == 0

    def get_children(self):
        return self.children

    def add_child(self, child):
        child: CirNode
        if child not in self.children and child.parent is None:
            self.children.append(child)
            child.parent = self
        return

    def __str__(self):
        return self.tree.program.name + '#' + str(self.id)

    def get_code_string(self):
        word = self.cir_type
        if self.ast_source is None:
            word += "#" + str(self.id)
        else:
            code = self.ast_source.get_code_string()
            index = code.index("[")
            code = code[index - 1:].strip()
            word += code
        return word

    def is_statement(self):
        return self.cir_type in __cir_statement_types__

    def is_tag_statement(self):
        return self.cir_type in __cir_tag_statement_types__

    def is_assign_statement(self):
        return self.cir_type in __cir_assign_statement_types__

    def is_condition_statement(self):
        return self.cir_type in __cir_condition_statement_types__

    def is_expression(self):
        return self.cir_type in __cir_expression_types__

    def is_refer_expression(self):
        return self.cir_type in __cir_refer_expression_types__

    def is_value_expression(self):
        return self.cir_type in __cir_value_expression_types__

    def references_in(self):
        """
        collect all the reference expressions within the node (including)
        :return:
        """
        queue = deque()
        queue.append(self)
        references = set()
        while len(queue) > 0:
            node = queue.popleft()
            node: CirNode
            if node.is_refer_expression():
                references.add(node)
            for child in node.children:
                queue.append(child)
        return references

    def statement_of(self):
        """
        get the statement that the node belongs to
        :return: None if the node is not in statement
        """
        node = self
        while node is not None:
            if node.is_statement():
                return node
            else:
                node = node.parent
        return None

    def function_definition_of(self):
        """
        function definition where the node belongs to
        :return: None if the node is not in definition
        """
        node = self
        while node is not None:
            if node.cir_type == "FunctionDefinition":
                return node
            else:
                node = node.parent
        return None

    def top_expression_of(self):
        child = self
        parent = self.parent
        while parent is not None:
            parent: CirNode
            if not parent.is_expression():
                if child.is_expression():
                    return child
                else:
                    return None
            else:
                child = parent
                parent = parent.parent

        return None

    def execution_of(self):
        program = self.tree.program
        function_graph = program.function_graph
        return function_graph.get_execution_of_cir_node(self)

    def generate_code(self):
        return cir_code_generate(self)


class CirTree:
    def __parse_nodes__(self, ast_tree: AstTree, file_path: str):
        first, node_dict = True, dict()
        with open(file_path, 'r') as reader:
            for line in reader:
                if first:
                    first = False
                else:
                    line = line.strip()
                    if len(line) > 0:
                        items = line.split('\t')
                        id = int(items[0].strip())
                        cir_type = items[1].strip()
                        ast_source = None
                        if len(items[2].strip()) > 0:
                            ast_source = ast_tree.nodes[int(items[2].strip())]
                        data_type = None
                        if len(items[3].strip()) > 0:
                            data_type = ctype.c_type_parser.parse(items[3].strip())
                        token = None
                        if len(items[4].strip()) > 0:
                            if (cir_type == 'ConstExpression') or (cir_type == 'Label'):
                                token = ctoken.CToken.get_constant(items[4].strip())
                            else:
                                token = ctoken.CToken.get_token(items[4].strip())
                        cir_node = CirNode(self, id, cir_type, ast_source, data_type, token)
                        node_dict[id] = cir_node
        for index in range(0, len(node_dict)):
            self.nodes.append(node_dict[index])
        return

    def __parse_edges__(self, file_path: str):
        first = True
        with open(file_path, 'r') as reader:
            for line in reader:
                if first:
                    first = False
                else:
                    line = line.strip()
                    if len(line) > 0:
                        items = line.split('\t')
                        parent = self.nodes[int(items[0].strip())]
                        parent: CirNode
                        children_list = items[-1].strip()
                        children_items = children_list.split(' ')
                        for index in range(1, len(children_items) - 1):
                            item = children_items[index].strip()
                            child = self.nodes[int(item)]
                            parent.add_child(child)
        return

    def __init__(self, ast_tree: AstTree, file_path: str):
        self.program = ast_tree.program
        self.nodes = list()
        self.__parse_nodes__(ast_tree, file_path)
        self.__parse_edges__(file_path)
        return

    def get_program(self):
        return self.program

    def get_cir_root(self):
        return self.nodes[0]

    def get_cir_nodes(self):
        return self.nodes

    def size(self):
        return len(self.nodes)


def cir_code_generate(node: CirNode):
    if node.cir_type in { "Declarator", "Implicator", "Identifier", "ReturnPoint", "Field"}:
        return str(node.get_token())
    elif node.cir_type == "Label":
        return str(node.token)
    elif node.cir_type == "DeferExpression":
        return "(defer " + cir_code_generate(node.children[0]) + ")"
    elif node.cir_type == "FieldExpression":
        return "(field " + cir_code_generate(node.children[0]) + ", " + cir_code_generate(node.children[1]) + ")"
    elif node.cir_type == "AddressExpression":
        return "(address " + cir_code_generate(node.children[0]) + ")"
    elif node.cir_type == "CastExpression":
        return "(cast " + str(node.get_data_type()) + ", " + cir_code_generate(node.children[1]) + ")"
    elif node.cir_type == "ConstExpression":
        return str(node.token)
    elif node.cir_type == "StringLiteral":
        return "\"" + node.ast_source.get_code(True) + "\""
    elif node.cir_type == "DefaultValue":
        return "?"
    elif node.cir_type == "InitializerBody":
        text = "{"
        for child in node.children:
            text += " "
            text += cir_code_generate(child)
        return text + "}"
    elif node.cir_type == "WaitExpression":
        return "(wait " + cir_code_generate(node.children[0]) + ")"
    elif node.cir_type in { "ArithExpression", "BitwsExpression", "LogicExpression", "RelationExpression" }:
        text = "(" + str(node.token)
        for child in node.children:
            text += " "
            text += cir_code_generate(child)
        return text + ")"
    elif node.cir_type == "Type":
        return str(node.get_data_type())
    elif node.cir_type == "ArgumentList":
        text = ""
        for argument in node.children:
            text += " " + cir_code_generate(argument)
        return text
    elif node.is_assign_statement():
        execution = node.execution_of()
        return "[" + str(execution.id) + "]\t" + cir_code_generate(node.children[0]) + \
               " := " + cir_code_generate(node.children[1]) + ";"
    elif node.cir_type == "CallStatement":
        execution = node.execution_of()
        return "[" + str(execution.id) + "]\t" + "call " + cir_code_generate(node.children[0]) + \
               " by " + cir_code_generate(node.children[1]) + ";"
    elif node.cir_type == "IfStatement":
        execution = node.execution_of()
        return "[" + str(execution.id) + "]\t" + "if " + cir_code_generate(node.children[0]) + " then " + \
               cir_code_generate(node.children[1]) + " else " + cir_code_generate(node.children[2]) + ";"
    elif node.cir_type == "CaseStatement":
        execution = node.execution_of()
        return "[" + str(execution.id) + "]\t" + "case " + cir_code_generate(node.children[0]) + " then " + \
               cir_code_generate(node.children[1]) + ";"
    elif node.cir_type == "GotoStatement":
        execution = node.execution_of()
        return "[" + str(execution.id) + "]\t" + "goto " + cir_code_generate(node.children[0]) + ";"
    elif node.cir_type == "BegStatement":
        execution = node.execution_of()
        return "[" + str(execution.id) + "]\t" + "begin:"
    elif node.cir_type == "EndStatement":
        execution = node.execution_of()
        return "[" + str(execution.id) + "]\t" + "end:"
    elif node.cir_type == "CaseEndStatement":
        execution = node.execution_of()
        return "[" + str(execution.id) + "]\t" + "end case"
    elif node.cir_type == "IfEndStatement":
        execution = node.execution_of()
        return "[" + str(execution.id) + "]\t" + "end if"
    elif node.cir_type == "LabelStatement":
        execution = node.execution_of()
        return "[" + str(execution.id) + "]\t" + "label#" + cir_code_generate(node.children[0]) + ":"
    elif node.cir_type == "DefaultStatement":
        execution = node.execution_of()
        return "[" + str(execution.id) + "]\t" + "default:"
    elif node.cir_type == "FunctionBody":
        code = ""
        for child in node.children:
            code += "\t" + cir_code_generate(child) + "\n"
        return code
    elif node.cir_type == "FunctionDefinition":
        code = cir_code_generate(node.children[0]) + "():\n"
        code += cir_code_generate(node.children[1])
        return code + "\n"
    else:
        print("Invalid cir_type: " + node.cir_type)
        exit(1)
    return


# testing methods

def test_ast_tree():
    data_directory = 'C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data'
    output_directory = 'C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output'
    for file_name in os.listdir(data_directory):
        file_directory = os.path.join(data_directory, file_name)
        ast_file = os.path.join(file_directory, file_name + '.ast')
        ast_tree = AstTree(None, ast_file)
        output_file = os.path.join(output_directory, file_name + '.res')
        with open(output_file, 'w') as writer:
            for ast_node in ast_tree.get_ast_nodes():
                ast_node: AstNode
                writer.write(str(ast_node.id) + '\t')
                writer.write(ast_node.ast_type + '\t')
                writer.write(str(ast_node.beg_index) + '\t')
                writer.write(str(ast_node.end_index) + '\t')
                writer.write('[')
                for child in ast_node.children:
                    writer.write(' ' + str(child.id))
                writer.write(' ]')
                writer.write('\n')
        print('Testing on', file_name)
    return


def test_cir_tree():
    data_directory = 'C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data'
    output_directory = 'C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output'
    for file_name in os.listdir(data_directory):
        file_directory = os.path.join(data_directory, file_name)
        ast_file = os.path.join(file_directory, file_name + '.ast')
        cir_file = os.path.join(file_directory, file_name + '.cir')
        ast_tree = AstTree(None, ast_file)
        cir_tree = CirTree(ast_tree, cir_file)
        output_file = os.path.join(output_directory, file_name + '.res')
        with open(output_file, 'w') as writer:
            for cir_node in cir_tree.get_cir_nodes():
                cir_node: CirNode
                writer.write(str(cir_node.id) + '\t')
                writer.write(cir_node.cir_type + '\t')
                if cir_node.has_ast_source():
                    writer.write(str(cir_node.ast_source.id) + '\t')
                else:
                    writer.write('none\t')
                if cir_node.has_data_type():
                    writer.write(str(cir_node.data_type) + '\t')
                else:
                    writer.write('none_type\t')
                if cir_node.has_token():
                    writer.write(str(cir_node.token) + '\t')
                else:
                    writer.write('none_token\t')
                writer.write('[')
                for child in cir_node.children:
                    writer.write(' ' + str(child.id))
                writer.write(' ]')
                writer.write('\n')
        print('Testing on', file_name)
    return


if __name__ == '__main__':
    # test_ast_tree()
    test_cir_tree()
