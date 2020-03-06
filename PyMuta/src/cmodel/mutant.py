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
        self.label = None
        self.feature_vec = None
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

    def get_label(self):
        return self.label


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
        with open(file_path, 'r') as reader:
            for line in reader:
                if first:
                    first = False
                else:
                    line = line.strip()
                    if len(line) > 0:
                        items = line.split('\t')
                        id = int(items[0].strip())
                        label = items[1].strip()
                        mutant = self.mutants[id]
                        mutant.label = label
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
            lines, mutant = list(), None
            for line in reader:
                line = line.strip()
                if line.startswith('[mutant]'):
                    lines.clear()
                    items = line.split('\t')
                    mutant = self.mutants[int(items[1].strip())]
                lines.append(line)
                if line.startswith('[end_mutant]'):
                    feature = StateErrorGraph(self.assertions, lines)
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


class StateErrorFlow:
    def __init__(self, source, target, constraints):
        self.source = source
        self.target = target
        self.constraints = constraints
        return

    def get_source(self):
        return self.source

    def get_target(self):
        return self.target

    def get_constraints(self):
        return self.constraints

    def is_infection(self):
        return self.source is None


class StateError:
    def __init__(self, graph, id: int, location: ccode.CirNode, assertions: list):
        self.graph = graph
        self.id = id
        self.location = location
        self.assertions = assertions
        self.in_flows = list()
        self.ou_flows = list()
        return

    def get_graph(self):
        return self.graph

    def get_id(self):
        return self.id

    def get_location(self):
        return self.location

    def get_assertions(self):
        return self.assertions

    def is_empty(self):
        return len(self.assertions) == 0

    def link(self, constraints: list, target):
        target: StateError
        flow = StateErrorFlow(self, target, constraints)
        self.ou_flows.append(flow)
        target.in_flows.append(flow)
        return flow


class StateErrorGraph:
    def __init__(self, assertions: SemanticAssertions, lines: list):
        self.assertions = assertions
        self.reachability = None
        self.errors = dict()
        self.infections = list()
        self.__parse_nodes__(lines)
        self.__parse_flows__(lines)
        return

    def get_reachability(self):
        return self.reachability

    def get_assertions(self):
        return self.assertions

    def get_errors(self):
        return self.errors.values()

    def get_infections(self):
        return self.infections

    def __parse_nodes__(self, lines: list):
        for line in lines:
            line: str
            line = line.strip()
            if len(line) > 0:
                items = line.split('\t')
                if items[0].strip() == '[node]':
                    id = int(items[1].strip())
                    id_string = items[2].strip()
                    location = None
                    if len(id_string) > 0:
                        index = id_string.index('#')
                        id_string = id_string[index + 1:].strip()
                        location = self.assertions.cir_tree.nodes[int(id_string)]
                    assertions = list()
                    for index in range(3, len(items)):
                        ass_string = items[index].strip()
                        if len(ass_string) > 0:
                            assertion = self.assertions.get_assertion(ass_string)
                            assertions.append(assertion)
                    error = StateError(self, id, location, assertions)
                    self.errors[id] = error
                elif items[0].strip() == '[cover]':
                    self.reachability = self.assertions.get_assertion(items[1].strip())
        return

    def __parse_flows__(self, lines: list):
        for line in lines:
            line: str
            line = line.strip()
            if len(line) > 0:
                items = line.split('\t')
                if items[0].strip() == '[flow]':
                    source = self.errors[int(items[1].strip())]
                    target = self.errors[int(items[2].strip())]
                    assertions = list()
                    for index in range(3, len(items)):
                        ass_string = items[index].strip()
                        if len(ass_string) > 0:
                            assertion = self.assertions.get_assertion(ass_string)
                            assertions.append(assertion)
                    source: StateError
                    source.link(assertions, target)
                elif items[0].strip() == '[infect]':
                    source = None
                    target = self.errors[int(items[1].strip())]
                    assertions = list()
                    for index in range(2, len(items)):
                        ass_string = items[index].strip()
                        if len(ass_string) > 0:
                            assertion = self.assertions.get_assertion(ass_string)
                            assertions.append(assertion)
                    flow = StateErrorFlow(source, target, assertions)
                    target: StateError
                    self.infections.append(flow)
                    target.in_flows.append(flow)
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
                writer.write(mutant.label + '\t')
                writer.write('\n')
        print('Testing on', file_name)
    return


if __name__ == '__main__':
    test_mutants()
