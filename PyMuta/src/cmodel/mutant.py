"""
mutant.py defines the data mining describing the mutation, including:
    Mutant
    MutantSpace

    SemanticAssertion
    SemanticAssertions

    TestConstraint
    StateError
    StateErrorFlow
    StateErrorGraph
"""


import os
import src.cmodel.ccode as ccode
import src.cmodel.ctoken as ctoken
import src.cmodel.program as cprogram


# from score string to label
mutant_labels = dict()


class MutantLabels:
    """
    score; cluster; label; kill_counter; probability;
    """

    def __init__(self, score: str, cluster: int):
        self.score = score
        self.cluster = cluster
        self.kill_counter = 0
        for k in range(0, len(score)):
            if score[k] == '1':
                self.kill_counter += 1
        self.probability = self.kill_counter / (len(score) + 0.0)
        if self.kill_counter == 0:
            self.label = 0
        else:
            self.label = 1
        return

    def get_score_string(self):
        return self.score

    def number_of_tests(self):
        return len(self.score)

    def is_killed(self, test: int):
        return self.score[test] == '1'

    def get_cluster_id(self):
        return self.cluster

    def get_kill_counter(self):
        return self.kill_counter

    def get_kill_probability(self):
        return self.probability

    def get_killing_label(self):
        return self.label

    def set_killing_label_by_probability(self, prob_threshold: float):
        if self.probability <= prob_threshold:
            self.label = 0
        else:
            self.label = 1
        return


class Mutant:
    """
    [space, id, class, operator, location, parameter]
    """

    def __init__(self, space, id: int, muta_class: str, muta_operator: str, location: ccode.AstNode, parameter):
        self.space = space
        self.id = id
        self.muta_class = muta_class
        self.muta_operator = muta_operator
        self.location = location
        self.parameter = parameter
        self.features = None
        self.labels = None
        self.feature_vector = None
        self.feature_words = None
        return

    def get_space(self):
        return self.space

    def get_id(self):
        return self.id

    def get_muta_class(self):
        return self.muta_class

    def get_muta_operator(self):
        return self.muta_operator

    def get_location(self):
        return self.location

    def has_parameter(self):
        return self.parameter is not None

    def get_parameter(self):
        return self.parameter

    def get_features(self):
        return self.features

    def get_labels(self):
        return self.labels


class MutantSpace:

    def __parse_mutants__(self, ast_tree: ccode.AstTree, file_path: str):
        first = True
        with open(file_path, 'r') as reader:
            for line in reader:
                if first:
                    first = False
                else:
                    line = line.strip()
                    if len(line) > 0:
                        items = line.split('\t')
                        id = int(items[0].strip())
                        muta_class = items[1].strip()
                        muta_operator = items[2].strip()
                        location = ast_tree.nodes[int(items[3].strip())]
                        parameter = None
                        if (len(items) > 4) and (len(items[4].strip()) > 0):
                            if muta_class in {'TTRP', 'VINC', 'VCRP'}:
                                parameter = ctoken.CToken.get_constant(items[4].strip())
                            elif muta_class in {'VRRP'}:
                                parameter = ctoken.CToken.get_token(items[4].strip())
                            elif muta_class in {'CTRP', 'SGLR', 'SRTR'}:
                                parameter = ast_tree.nodes[int(items[4].strip())]
                            else:
                                parameter = None
                        mutant = Mutant(self, id, muta_class, muta_operator, location, parameter)
                        self.mutants[mutant.id] = mutant
        return

    def __parse_labels__(self, file_path: str):
        first = True
        mutant_labels.clear()
        with open(file_path, 'r') as reader:
            for line in reader:
                if first:
                    first = False
                else:
                    line = line.strip()
                    if len(line) > 0:
                        items = line.split('\t')
                        id = int(items[0].strip())
                        score = items[1].strip()
                        if score not in mutant_labels:
                            cluster = len(mutant_labels)
                            mutant_labels[score] = MutantLabels(score, cluster)
                        mutant = self.mutants[id]
                        mutant.labels = mutant_labels[score]
        mutant_labels.clear()
        return

    def __init__(self, ast_tree: ccode.AstTree, cir_tree: ccode.CirTree, file_path: str,
                 label_path: str, feature_path: str):
        self.assertions = SemanticAssertions(cir_tree)
        self.program = ast_tree.program
        self.mutants = dict()
        self.__parse_mutants__(ast_tree, file_path)
        self.__parse_labels__(label_path)
        self.__parse_features__(feature_path)
        return

    def get_program(self):
        return self.program

    def get_mutants(self):
        return self.mutants.values()

    def __parse_features__(self, file_path: str):
        with open(file_path, 'r') as reader:
            lines, mutant = list(), self.mutants[0]
            for line in reader:
                line = line.strip()
                if line.startswith('[mutant]'):
                    lines.clear()
                    items = line.split('\t')
                    mutant = self.mutants[int(items[1].strip())]
                lines.append(line)
                if line.startswith('[end_mutant]'):
                    feature = SemanticErrorGraph(mutant, lines)
                    mutant: Mutant
                    mutant.features = feature
        return


class SemanticAssertion:
    """
    function, operands
    """
    def __init__(self, assertions, function: str):
        self.assertions = assertions
        self.function = function
        self.operands = list()
        return

    def get_assertions(self):
        return self.assertions

    def get_function(self):
        return self.function

    def get_operands(self):
        return self.operands

    def __str__(self):
        text = self.function
        text += '('
        for index in range(len(self.operands)):
            operand = self.operands[index]
            text += str(operand)
            if index < len(self.operands) - 1:
                text += '; '
        text += ')'
        return text


class SemanticAssertions:
    def __init__(self, cir_tree: ccode.CirTree):
        self.assertions = dict()
        self.cir_tree = cir_tree
        return

    def __add_assertion__(self, other: SemanticAssertion):
        key = str(other)
        if key not in self.assertions:
            self.assertions[key] = other
        return self.assertions[key]

    def get_assertion(self, text: str):
        """
        create a semantic assertion in space
        :param text:
        :return:
        """
        index = text.index('(')
        function = text[0:index].strip()
        operands = list()
        operands_str = text[index + 1: len(text) - 1].strip()
        if len(operands_str) > 0:
            operands_items = operands_str.split(';')
            for operand_item in operands_items:
                items = operand_item.strip().split('#')
                itype = items[0].strip()
                ivalue = items[1].strip()
                if itype == 'b':
                    if ivalue == 'true':
                        operand = True
                    else:
                        operand = False
                elif itype == 'i':
                    operand = int(ivalue)
                elif itype == 'f':
                    operand = float(ivalue)
                elif itype == 's':
                    operand = ivalue
                elif itype == 'cir':
                    operand = self.cir_tree.nodes[int(ivalue)]
                else:
                    operand = None
                if operand is not None:
                    operands.append(operand)
        assertion = SemanticAssertion(self, function)
        assertion.operands = operands
        return self.__add_assertion__(assertion)

    def get_assertions(self):
        return self.assertions.values()


class SemanticErrorEdge:
    """
    [assertions, source, target]
    """
    def __init__(self, assertions, source, target):
        self.assertions = assertions
        self.source = source
        self.target = target
        return

    def get_assertions(self):
        return self.assertions

    def get_source(self):
        return self.source

    def get_target(self):
        return self.target


class SemanticErrorNode:
    """
    [graph, id, assertions, in_edges, ou_edges]
    """
    def __init__(self, graph, id: int, location, assertions):
        self.graph = graph
        self.location = location
        self.id = id
        self.assertions = assertions
        self.in_edges = list()
        self.ou_edges = list()
        return

    def get_graph(self):
        return self.graph

    def get_location(self):
        return self.location

    def get_id(self):
        return self.id

    def get_assertions(self):
        return self.assertions

    def get_in_edges(self):
        return self.in_edges

    def get_ou_edges(self):
        return self.ou_edges

    def link_to(self, target, assertions):
        target: SemanticErrorNode
        edge = SemanticErrorEdge(assertions, self, target)
        self.ou_edges.append(edge)
        target.in_edges.append(edge)
        return edge


class SemanticErrorGraph:
    """
    [mutant, nodes]
    """
    def __init__(self, mutant: Mutant, error_lines: list):
        self.mutant = mutant
        self.nodes = dict()
        self.__parse_nodes__(mutant.space.assertions, error_lines)
        self.__parse_edges__(mutant.space.assertions, error_lines)
        return

    def get_mutant(self):
        return self.mutant

    def __len__(self):
        return len(self.nodes)

    def get_nodes(self):
        return self.nodes.values()

    def get_node(self, id: int):
        return self.nodes[id]

    def __parse_nodes__(self, assertions: SemanticAssertions, error_lines: list):
        self.nodes.clear()
        for error_line in error_lines:
            error_line: str
            line = error_line.strip()
            if line.startswith("[node]"):
                items = line.split('\t')
                id = int(items[1].strip())
                cir_node = None
                if len(items) > 2 and len(items[2].strip()) > 0:
                    index = items[2].index('#')
                    cir_id = int(items[2][index + 1:].strip())
                    cir_node = assertions.cir_tree.nodes[cir_id]
                assertion_list = list()
                for k in range(3, len(items)):
                    assertion = assertions.get_assertion(items[k].strip())
                    assertion_list.append(assertion)
                error_node = SemanticErrorNode(self, id, cir_node, assertion_list)
                self.nodes[error_node.id] = error_node
        return

    def __parse_edges__(self, assertions: SemanticAssertions, error_lines: list):
        for error_line in error_lines:
            error_line: str
            line = error_line.strip()
            if line.startswith("[flow]"):
                items = line.split('\t')
                source = self.nodes[int(items[1].strip())]
                target = self.nodes[int(items[2].strip())]
                assertion_list = list()
                for k in range(3, len(items)):
                    assertion = assertions.get_assertion(items[k].strip())
                    assertion_list.append(assertion)
                source: SemanticErrorNode
                target: SemanticErrorNode
                source.link_to(target, assertion_list)
        return


# testing method

def test_mutants():
    data_directory = 'C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data'
    output_directory = 'C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output'
    for file_name in os.listdir(data_directory):
        file_directory = os.path.join(data_directory, file_name)
        program = cprogram.Program(file_directory)
        space = program.mutant_space
        output_file = os.path.join(output_directory, file_name + '.res')
        with open(output_file, 'w') as writer:
            for mutant in space.get_mutants():
                mutant: Mutant
                writer.write(str(mutant.id) + '\t')
                writer.write(mutant.muta_class + '\t')
                writer.write(mutant.muta_operator + '\t')
                if mutant.has_parameter():
                    writer.write(str(mutant.parameter) + '\t')
                else:
                    writer.write('non_parameter\t')
                writer.write(str(mutant.labels.cluster) + '\t')
                writer.write(str(mutant.labels.label) + '\t')
                writer.write('\n')
        print('Testing on', file_name)
    return


if __name__ == '__main__':
    test_mutants()
