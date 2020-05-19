"""
astree.py defines syntactic model of source program under test.
"""


from enum import Enum
import os
import src.com.jcmuta.base as base


class SourceCode:
    """
    Structural text of file.
    """

    def __init__(self, file_path: str):
        self.text = ""
        self.index = list()
        self.file_path = file_path
        with open(file_path, 'r') as reader:
            index = 0
            for line in reader:
                self.index.append(index)
                self.text += line
                index += len(line)
            self.index.append(index)
        return

    def get_text(self):
        """
        :return: source code text string
        """
        return self.text

    def length(self):
        """
        :return: length of source code text
        """
        return len(self.text)

    def get_file(self):
        """
        :return: path of source code file (.c)
        """
        return self.file_path

    def number_of_lines(self):
        """
        :return: number of lines in the source code file
        """
        return len(self.index) - 1

    def get_text_in_line(self, line: int):
        """
        :param line: range from [0, n) where n is number of lines
        :return: get the text in kth line as specified
        """
        beg = self.index[line]
        end = self.index[line + 1]
        return self.text[beg:end]

    def get_line_of_char(self, k: int):
        """
        :param k: the index of the character
        :return: the line in which the kth character in source code belongs to or None if not found
        """
        beg, end = 0, len(self.index) - 1
        while beg <= end:
            mid = (beg + end) // 2
            k1 = self.index[mid]
            k2 = self.index[mid + 1]
            if k < k1:
                end = mid - 1
            elif k >= k2:
                beg = mid + 1
            else:
                return mid
        return None     # line is not found


def get_simplified_text(text: str):
    """
    translate the simplified code text in which spaces are removed
    :param text:
    :return:
    """
    buffer = ""
    k, n = 0, len(text)
    while k < n:
        if text[k].isspace():
            while k < n:
                if text[k].isspace():
                    k = k + 1
                else:
                    break
            buffer += " "
        else:
            buffer += text[k]
            k = k + 1
    return buffer


class AstType(Enum):
    abs_declarator = 0
    argument_list = 1
    array_qualifier_list = 2
    declaration = 3
    declaration_list = 4
    declaration_specifiers = 5
    declarator = 6
    designator = 7
    designator_list = 8
    dimension = 9
    directive = 10
    enumerator = 11
    enumerator_list = 12
    field_initializer = 13
    header = 14
    identifier_list = 15
    init_declarator = 16
    init_declarator_list = 17
    initializer = 18
    initializer_list = 19
    keyword = 20
    macro_body = 21
    macro_list = 22
    operator = 23
    parameter_declaration = 24
    parameter_list = 25
    parameter_type_list = 26
    pointer = 27
    punctuate = 28
    specifier_qualifier_list = 29
    statement_list = 30
    struct_declaration = 31
    struct_declaration_list = 32
    struct_declarator = 33
    struct_declarator_list = 34
    type_name = 35
    enum_specifier = 36
    function_qualifier = 37
    storage_class = 38
    struct_specifier = 39
    typedef_name = 40
    type_keyword = 41
    type_qualifier = 42
    union_specifier = 43
    break_statement = 44
    case_statement = 45
    compound_statement = 46
    continue_statement = 47
    declaration_statement = 48
    default_statement = 49
    do_while_statement = 50
    expression_statement = 51
    for_statement = 52
    goto_statement = 53
    if_statement = 54
    labeled_statement = 55
    return_statement = 56
    switch_statement = 57
    while_statement = 58
    enumerator_body = 59
    function_definition = 60
    parameter_body = 61
    struct_union_body = 62
    translation_unit = 63
    field = 64
    label = 65
    macro = 66
    Name = 67
    preprocess_line = 68
    preprocess_none_line = 69
    array_expression = 70
    constant = 71
    id_expression = 72
    literal = 73
    arith_assign_expression = 74
    arith_binary_expression = 75
    bitws_assign_expression = 76
    bitws_binary_expression = 77
    logic_binary_expression = 78
    relational_expression = 79
    cast_expression = 80
    comma_expression = 81
    conditional_expression = 82
    const_expression = 83
    field_expression = 84
    function_call_expression = 85
    initializer_body = 86
    paranth_expression = 87
    incre_postfix_expression = 88
    sizeof_expression = 89
    arith_unary_expression = 90
    bitws_unary_expression = 91
    incre_unary_expression = 92
    logic_unary_expression = 93
    point_unary_expression = 94
    assign_expression = 95

    def __str__(self):
        return self.name

    @staticmethod
    def parse(text: str):
        if text == "AbsDeclarator":
            return AstType.abs_declarator
        elif text == "ArgumentList":
            return AstType.argument_list
        elif text == "ArrayQualifierList":
            return AstType.array_qualifier_list
        elif text == "Declaration":
            return AstType.declaration
        elif text == "DeclarationList":
            return AstType.declaration_list
        elif text == "DeclarationSpecifiers":
            return AstType.declaration_specifiers
        elif text == "Declarator":
            return AstType.declarator
        elif text == "Designator":
            return AstType.designator
        elif text == "DesignatorList":
            return AstType.designator_list
        elif text == "Dimension":
            return AstType.dimension
        elif text == "Directive":
            return AstType.directive
        elif text == "Enumerator":
            return AstType.enumerator
        elif text == "EnumeratorList":
            return AstType.enumerator_list
        elif text == "FieldInitializer":
            return AstType.field_initializer
        elif text == "Header":
            return AstType.header
        elif text == "IdentifierList":
            return AstType.identifier_list
        elif text == "InitDeclarator":
            return AstType.init_declarator
        elif text == "InitDeclaratorList":
            return AstType.init_declarator_list
        elif text == "Initializer":
            return AstType.initializer
        elif text == "InitializerList":
            return AstType.initializer_list
        elif text == "Keyword":
            return AstType.keyword
        elif text == "MacroBody":
            return AstType.macro_body
        elif text == "MacroList":
            return AstType.macro_list
        elif text == "Operator":
            return AstType.operator
        elif text == "ParameterDeclaration":
            return AstType.parameter_declaration
        elif text == "ParameterList":
            return AstType.parameter_list
        elif text == "ParameterTypeList":
            return AstType.parameter_type_list
        elif text == "Pointer":
            return AstType.pointer
        elif text == "Punctuator":
            return AstType.punctuate
        elif text == "SpecifierQualifierList":
            return AstType.specifier_qualifier_list
        elif text == "StatementList":
            return AstType.statement_list
        elif text == "StructDeclaration":
            return AstType.struct_declaration
        elif text == "StructDeclarationList":
            return AstType.struct_declaration_list
        elif text == "StructDeclarator":
            return AstType.struct_declarator
        elif text == "StructDeclaratorList":
            return AstType.struct_declarator_list
        elif text == "TypeName":
            return AstType.type_name
        elif text == "BreakStatement":
            return AstType.break_statement
        elif text == "CaseStatement":
            return AstType.case_statement
        elif text == "CompoundStatement":
            return AstType.compound_statement
        elif text == "ContinueStatement":
            return AstType.continue_statement
        elif text == "DeclarationStatement":
            return AstType.declaration_statement
        elif text == "DefaultStatement":
            return AstType.default_statement
        elif text == "DoWhileStatement":
            return AstType.do_while_statement
        elif text == "ExpressionStatement":
            return AstType.expression_statement
        elif text == "ForStatement":
            return AstType.for_statement
        elif text == "GotoStatement":
            return AstType.goto_statement
        elif text == "IfStatement":
            return AstType.if_statement
        elif text == "LabeledStatement":
            return AstType.labeled_statement
        elif text == "ReturnStatement":
            return AstType.return_statement
        elif text == "SwitchStatement":
            return AstType.switch_statement
        elif text == "WhileStatement":
            return AstType.while_statement
        elif text == "EnumSpecifier":
            return AstType.enum_specifier
        elif text == "FunctionQualifier":
            return AstType.function_qualifier
        elif text == "StorageClass":
            return AstType.storage_class
        elif text == "StructSpecifier":
            return AstType.struct_specifier
        elif text == "TypedefName":
            return AstType.typedef_name
        elif text == "TypeKeyword":
            return AstType.type_keyword
        elif text == "TypeQualifier":
            return AstType.type_qualifier
        elif text == "UnionSpecifier":
            return AstType.union_specifier
        elif text == "EnumeratorBody":
            return AstType.enumerator_body
        elif text == "FunctionDefinition":
            return AstType.function_definition
        elif text == "ParameterBody":
            return AstType.parameter_body
        elif text == "StructUnionBody":
            return AstType.struct_union_body
        elif text == "TranslationUnit":
            return AstType.translation_unit
        elif text == "Field":
            return AstType.field
        elif text == "IdExpression":
            return AstType.id_expression
        elif text == "Label":
            return AstType.label
        elif text == "Macro":
            return AstType.macro
        elif text == "Name":
            return AstType.Name
        elif text == "PreprocessLine":
            return AstType.preprocess_line
        elif text == "PreprocessNoneLine":
            return AstType.preprocess_none_line
        elif text == "ArrayExpression":
            return AstType.array_expression
        elif text == "Constant":
            return AstType.constant
        elif text == "Literal":
            return AstType.literal
        elif text == "ArithAssignExpression":
            return AstType.arith_assign_expression
        elif text == "ArithBinaryExpression":
            return AstType.arith_binary_expression
        elif text == "AssignExpression":
            return AstType.assign_expression
        elif text == "BitwiseAssignExpression":
            return AstType.bitws_assign_expression
        elif text == "BitwiseBinaryExpression":
            return AstType.bitws_binary_expression
        elif text == "LogicBinaryExpression":
            return AstType.logic_binary_expression
        elif text == "RelationExpression":
            return AstType.relational_expression
        elif text == "ShiftAssignExpression":
            return AstType.bitws_assign_expression
        elif text == "ShiftBinaryExpression":
            return AstType.bitws_binary_expression
        elif text == "CastExpression":
            return AstType.cast_expression
        elif text == "CommaExpression":
            return AstType.comma_expression
        elif text == "ConditionalExpression":
            return AstType.conditional_expression
        elif text == "ConstExpression":
            return AstType.const_expression
        elif text == "FieldExpression":
            return AstType.field_expression
        elif text == "FunCallExpression":
            return AstType.function_call_expression
        elif text == "InitializerBody":
            return AstType.initializer_body
        elif text == "ParanthExpression":
            return AstType.paranth_expression
        elif text == "IncrePostfixExpression":
            return AstType.incre_postfix_expression
        elif text == "SizeofExpression":
            return AstType.sizeof_expression
        elif text == "ArithUnaryExpression":
            return AstType.arith_unary_expression
        elif text == "BitwiseUnaryExpression":
            return AstType.bitws_unary_expression
        elif text == "LogicUnaryExpression":
            return AstType.logic_unary_expression
        elif text == "IncreUnaryExpression":
            return AstType.incre_unary_expression
        elif text == "PointUnaryExpression":
            return AstType.point_unary_expression
        else:
            return None


class AstNode:
    """
    abstract syntax tree node as {tree, id; beg_index, end_index;
                                    parent, children; ast_type, data_type, content}
        (1) identifier {field, id_expression, label, macro, Name, TypedefName}
            |-- content as string name;
        (2) keyword
            |-- content as base.CKeyword
        (3) constant
            |-- content as boolean, integer, double
        (4) literal
            |-- content as string text
        (5) punctuate
            |-- content as CPunctuate
        (6) operator
            |-- content as COperator
    """

    def __init__(self, tree, id: int, ast_type: AstType, data_type: base.CType,
                 content, beg_index: int, end_index: int):
        """
        :param tree: abstract syntax tree where the node is created
        :param id: the integer ID of the node in abstract syntax tree
        :param ast_type: the syntactic type of the tree node (@see AstType)
        :param data_type: the data type to describe its value {for expression or type_name}
        :param content:
                (1) string for identifier and literal
                (2) int or double for constant
                (3) CKeyword for keyword
                (4) COperator for operator
                (5) CPunctuate for punctuate
        :param beg_index
        :param end_index
        """
        self.tree = tree
        self.id = id
        self.ast_type = ast_type
        self.data_type = data_type
        self.content = content
        self.beg_index = beg_index
        self.end_index = end_index
        self.parent = None
        self.children = list()
        return

    def get_tree(self):
        """
        :return: abstract syntax tree
        """
        return self.tree

    def get_id(self):
        """
        :return: get the integer ID
        """
        return self.id

    def get_code(self, simplified=False):
        """
        :param simplified:
        :return: get the code to which the AST node refers
        """
        source_code = self.tree.source_code
        source_code: SourceCode
        code = source_code.text[self.beg_index:self.end_index]
        if simplified:
            code = get_simplified_text(code)
        return code

    def get_beg_line(self):
        """
        :return: the line number from which the first character in the node refers
        """
        source_code = self.tree.source_code
        source_code: SourceCode
        return source_code.get_line_of_char(self.beg_index)

    def get_end_line(self):
        """
        :return: the line number from which the final character in the node refers
        """
        source_code = self.tree.source_code
        source_code: SourceCode
        return source_code.get_line_of_char(self.end_index)

    def get_ast_type(self):
        """
        :return: syntactic type of this node
        """
        return self.ast_type

    def get_data_type(self):
        """
        :return: data type of the value hold at this node {expression or type_name} or none
        """
        return self.data_type

    def get_keyword_token(self):
        """
        :return: get the keyword token if self.ast_type == AstType.keyword
        """
        if isinstance(self.content, base.CKeyword):
            return self.content
        else:
            return None

    def get_punctuate_token(self):
        """
        :return: token for AstType.punctuate
        """
        if isinstance(self.content, base.CPunctuate):
            return self.content
        else:
            return None

    def get_operator_token(self):
        """
        :return: token for AstType.operator
        """
        if isinstance(self.content, base.COperator):
            return self.content
        else:
            return None

    def get_parent(self):
        """
        :return: the parent of this node
        """
        return self.parent

    def get_child(self, k: int):
        """
        :param k:
        :return: get the kth child in the node
        """
        return self.children[k]

    def get_children(self):
        """
        :return: get the children of this node
        """
        return self.children

    def is_statement(self):
        return self.ast_type == AstType.break_statement or self.ast_type == AstType.case_statement or \
               self.ast_type == AstType.continue_statement or self.ast_type == AstType.compound_statement or \
               self.ast_type == AstType.declaration_statement or self.ast_type == AstType.do_while_statement or \
               self.ast_type == AstType.default_statement or self.ast_type == AstType.expression_statement or \
               self.ast_type == AstType.for_statement or self.ast_type == AstType.goto_statement or \
               self.ast_type == AstType.if_statement or self.ast_type == AstType.labeled_statement or \
               self.ast_type == AstType.return_statement or self.ast_type == AstType.switch_statement or \
               self.ast_type == AstType.while_statement

    def is_specifier(self):
        return self.ast_type == AstType.type_keyword or self.ast_type == AstType.storage_class or \
               self.ast_type == AstType.function_qualifier or self.ast_type == AstType.type_qualifier or \
               self.ast_type == AstType.struct_specifier or self.ast_type == AstType.union_specifier or \
               self.ast_type == AstType.enum_specifier

    def is_identifier(self):
        return self.ast_type == AstType.id_expression or self.ast_type == AstType.label or \
               self.ast_type == AstType.macro or self.ast_type == AstType.field or self.ast_type == AstType.Name or \
               self.ast_type == AstType.typedef_name

    def is_basic_expression(self):
        return self.ast_type == AstType.id_expression or self.ast_type == AstType.constant or \
               self.ast_type == AstType.literal

    def is_binary_expression(self):
        return self.ast_type == AstType.arith_binary_expression or self.ast_type == AstType.arith_assign_expression or \
               self.ast_type == AstType.bitws_binary_expression or self.ast_type == AstType.bitws_assign_expression or \
               self.ast_type == AstType.logic_binary_expression or self.ast_type == AstType.relational_expression or \
               self.ast_type == AstType.assign_expression

    def is_unary_or_postfix_expression(self):
        return self.ast_type == AstType.incre_unary_expression or self.ast_type == AstType.arith_unary_expression or \
               self.ast_type == AstType.bitws_unary_expression or self.ast_type == AstType.logic_unary_expression or \
               self.ast_type == AstType.point_unary_expression or self.ast_type == AstType.incre_postfix_expression

    def is_expression(self):
        return self.is_basic_expression() or self.is_binary_expression() or self.is_unary_or_postfix_expression() or \
               self.ast_type == AstType.array_expression or self.ast_type == AstType.cast_expression or \
               self.ast_type == AstType.comma_expression or self.ast_type == AstType.conditional_expression or \
               self.ast_type == AstType.const_expression or self.ast_type == AstType.field_expression or \
               self.ast_type == AstType.function_call_expression or self.ast_type == AstType.initializer_body or \
               self.ast_type == AstType.paranth_expression or self.ast_type == AstType.sizeof_expression

    def statement_of(self):
        parent = self
        while parent is not None:
            if parent.is_statement():
                return parent
            else:
                parent = parent.parent
        return None

    def expression_of(self):
        if self.ast_type == AstType.paranth_expression or self.ast_type == AstType.const_expression or \
                self.ast_type == AstType.initializer:
            return self.children[0].expression_of()
        elif self.ast_type == AstType.expression_statement:
            if len(self.children) > 0:
                return self.children[0].expression_of()
            else:
                return None
        elif self.is_expression():
            return self
        else:
            return None


class AstTree:
    """
    abstract syntax tree as {source_code; tree_nodes; root}
    """

    def __init__(self, source_file: str, ast_tree_file: str):
        """
        :param source_file: xxx.c
        :param ast_tree_file: xxx.ast
        """
        self.source_code = SourceCode(source_file)
        self.tree_nodes = list()
        self.__parse__(ast_tree_file)
        return

    def get_source_code(self):
        """
        :return: source code from which the tree is parsed
        """
        return self.source_code

    def get_tree_nodes(self):
        """
        :return: list of tree nodes
        """
        return self.tree_nodes

    def length(self):
        """
        :return: the number of tree nodes
        """
        return len(self.tree_nodes)

    def get_tree_node(self, id: int):
        """
        :param id:
        :return: get the tree node w.r.t. the given ID
        """
        return self.tree_nodes[id]

    def get_ast_root(self):
        """
        :return: the root in the abstract syntax tree
        """
        return self.tree_nodes[0]

    def __parse__(self, ast_tree_file: str):
        """
        {id; ast_type; beg_index; end_index; data_type; content}
        :param ast_tree_file:
        :return:
        """
        nodes = dict()
        ''' (1) create nodes '''
        with open(ast_tree_file, 'r') as reader:
            for line in reader:
                line = line.strip()
                if len(line) > 0:
                    items = line.split('\t')
                    id = int(items[0].strip())
                    ast_type = AstType.parse(items[1].strip())
                    beg_index = int(items[2].strip())
                    end_index = int(items[3].strip())
                    if len(items) > 4:
                        data_type = base.CType.parse(items[4].strip())
                    else:
                        data_type = None
                    if len(items) > 5:
                        content = base.get_content_of(items[5].strip())
                    else:
                        content = None
                    if ast_type is not None:
                        node = AstNode(self, id, ast_type, data_type, content, beg_index, end_index)
                        nodes[id] = node
                        if ast_type == AstType.keyword:
                            node.content = base.CKeyword.parse(content)
                        elif ast_type == AstType.operator:
                            node.content = base.COperator.get_operator_of(content)
                        elif ast_type == AstType.punctuate:
                            node.content = base.COperator.get_operator_of(content)
                            if node.content is None:
                                node.content = base.CPunctuate.parse(content)
        ''' (2) create edges '''
        with open(ast_tree_file, 'r') as reader:
            for line in reader:
                line = line.strip()
                if len(line) > 0:
                    items = line.split('\t')
                    parent = nodes[int(items[0].strip())]
                    parent: AstNode
                    for k in range(6, len(items)):
                        child = nodes[int(items[k].strip())]
                        parent.children.append(child)
                        child: AstNode
                        child.parent = parent
        ''' (3) create trees '''
        self.tree_nodes.clear()
        for k in range(0, len(nodes)):
            self.tree_nodes.append(nodes[k])
        return


if __name__ == "__main__":
    prefix = "C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data"
    for filename in os.listdir(prefix):
        directory = os.path.join(prefix, filename)
        source_file = os.path.join(directory, filename + ".c")
        ast_tree_file = os.path.join(directory, filename + ".ast")
        ast_tree = AstTree(source_file, ast_tree_file)
        output_file = os.path.join("C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output", filename + ".ast")
        print("Open the abstract syntax tree for", filename)
        with open(output_file, 'w') as writer:
            for node in ast_tree.get_tree_nodes():
                node: AstNode
                writer.write(str(node.id) + "\t")
                if node.parent is not None:
                    writer.write(str(node.parent.id) + "\t")
                else:
                    writer.write("ROOT\t")
                writer.write(str(node.ast_type) + "\t")
                writer.write(str(node.beg_index) + "\t")
                writer.write(str(node.end_index) + "\t")
                data_type = ""
                if node.get_data_type() is not None:
                    data_type = str(node.data_type)
                content = ""
                if node.content is not None:
                    content = str(node.content)
                writer.write(data_type + "\t" + content + "\t")
                writer.write("\"" + node.get_code(True) + "\"")
                for child in node.children:
                    writer.write("\t" + str(child.id))
                writer.write("\n")
    print("Testing finished.")
