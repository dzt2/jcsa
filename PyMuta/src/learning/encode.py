"""
encode.py encodes the features of each mutant into set of words (normalized).
"""


import os
import src.cmodel.ccode as ccode
import src.cmodel.program as cprogram
import src.cmodel.mutant as cmutant
import scipy.sparse as sparse


class Word2Integer:
    """
    Used to translate the string word to unique integer ID
    """

    def __init__(self):
        self.words = list()
        self.index = dict()
        return

    def encode(self, word: str):
        if word not in self.index:
            self.index[word] = len(self.words)
            self.words.append(word)
        return self.index[word]

    def encodes(self, words):
        code_list = list()
        for word in words:
            if word not in self.index:
                self.index[word] = len(self.words)
                self.words.append(word)
            code = self.index[word]
            code_list.append(code)
        return code_list

    def decode(self, code: int):
        return self.words[code]

    def decodes(self, code_list):
        words = list()
        for code in code_list:
            words.append(self.words[code])
        return words

    def save(self, file_path: str):
        with open(file_path, 'w') as writer:
            for word in self.words:
                writer.write(word.strip())
                writer.write('\n')
        return

    def load(self, file_path: str):
        with open(file_path, 'r') as reader:
            self.words.clear()
            self.index.clear()
            for line in reader:
                word = line.strip()
                if len(word) > 0:
                    if word not in self.index:
                        self.index[word] = len(self.words)
                        self.words.append(word)
        return


class SemanticAssertionEncode:
    """
    Used to encode the semantic assertion as word for description
    """

    @staticmethod
    def get_assertion_string(assertion: cmutant.SemanticAssertion):
        return str(assertion)

    @staticmethod
    def get_assertion_cir_node(assertion: cmutant.SemanticAssertion):
        word = assertion.get_function()
        word += '( '
        for operand in assertion.get_operands():
            if isinstance(operand, ccode.CirNode):
                operand: ccode.CirNode
                word += operand.get_cir_type()
                if operand.has_data_type():
                    word += '::'
                    word += str(operand.get_data_type())
            else:
                word += str(operand)
            word += '; '
        word += ')'
        return word

    @staticmethod
    def get_assertion_cir_nodes(assertion: cmutant.SemanticAssertion):
        """
        each cir-node is encoded as sequence from local point to the statement it belongs to
        :param assertion:
        :return:
        """
        word = assertion.get_function()
        word += '( '
        for operand in assertion.get_operands():
            if isinstance(operand, ccode.CirNode):
                while operand is not None:
                    operand: ccode.CirNode
                    word += operand.get_cir_type()
                    if operand.has_data_type():
                        word += '.'
                        word += str(operand.get_data_type())
                    if operand.cir_type.endswith('Statement'):
                        break
                    else:
                        word += '::'
                        operand = operand.get_parent()
            else:
                word += str(operand)
            word += '; '
        word += ')'
        return word

    @staticmethod
    def get_assertion_ast_node(assertion: cmutant.SemanticAssertion):
        """
        translate the word as describing the AstNode where the assertion hold.
        :param assertion:
        :return:
        """
        word = assertion.get_function()
        word += '( '
        for operand in assertion.get_operands():
            if isinstance(operand, ccode.CirNode):
                operand: ccode.CirNode
                if operand.has_ast_source():
                    ast_node = operand.get_ast_source()
                    ast_node: ccode.AstNode
                    word += ast_node.ast_type
                    if ast_node.has_data_type():
                        word += ':'
                        word += str(ast_node.get_data_type())
                    if ast_node.has_token():
                        word += ':'
                        word += str(ast_node.get_token())
                else:
                    word += operand.tree.program.name + '#' + str(operand.id)
            else:
                word += str(operand)
            word += '; '
        word += ')'
        return word

    @staticmethod
    def get_assertion_ast_nodes(assertion: cmutant.SemanticAssertion):
        """
        translate the word as describing the AstNode where the assertion hold.
        :param assertion:
        :return:
        """
        word = assertion.get_function()
        word += '( '
        for operand in assertion.get_operands():
            if isinstance(operand, ccode.CirNode):
                operand: ccode.CirNode
                if operand.has_ast_source():
                    ast_node = operand.get_ast_source()
                    ast_node: ccode.AstNode
                    while ast_node is not None:
                        word += ast_node.ast_type
                        if ast_node.has_data_type():
                            word += ':'
                            word += str(ast_node.get_data_type())
                        if ast_node.has_token():
                            word += ':'
                            word += str(ast_node.get_token())
                        if ast_node.parent is not None:
                            word += '|--'
                            for index in range(0, len(ast_node.parent.children)):
                                if ast_node.parent.children[index] == ast_node:
                                    word += '[' + str(index) + ']'
                            word += '--|'
                        ast_node = ast_node.parent
                else:
                    word += operand.tree.program.name + '#' + str(operand.id)
            else:
                word += str(operand)
            word += '; '
        word += ')'
        return word

    @staticmethod
    def get_assertion_source_code(assertion: cmutant.SemanticAssertion, max_code_length=48):
        """
        translate the word as describing the AstNode where the assertion hold.
        :param max_code_length:
        :param assertion:
        :return:
        """
        word = assertion.get_function()
        word += '( '
        for operand in assertion.get_operands():
            if isinstance(operand, ccode.CirNode):
                operand: ccode.CirNode
                if operand.has_ast_source():
                    ast_node = operand.get_ast_source()
                    ast_node: ccode.AstNode
                    ast_code = ast_node.get_code(strip=True)
                    if len(ast_code) > max_code_length:
                        ast_code = ast_code[0: max_code_length]
                        ast_code += '...'
                    source_code = ast_node.tree.program.source_code
                    source_code: ccode.SourceCode
                    line = source_code.line_of(ast_node.beg_index) + 1
                    word += ast_node.ast_type + '[' + str(line) + ']::\"' + ast_code + '\"'
                else:
                    word += operand.tree.program.name + '#' + str(operand.id)
            else:
                word += str(operand)
            word += '; '
        word += ')'
        return word

    @staticmethod
    def get_infection_assertions(mutant: cmutant.Mutant):
        assertions = set()
        features = mutant.features
        features: cmutant.StateErrorGraph
        if len(features.infections) > 0:
            for infection in features.infections:
                infection: cmutant.StateErrorFlow
                for constraint in infection.get_constraints():
                    assertions.add(constraint)
                for state_error in infection.get_target().assertions:
                    assertions.add(state_error)
        elif features.get_reachability() is not None:
            for assertion in features.reachability:
                assertions.add(assertion)
        return assertions

    @staticmethod
    def get_all_error_assertions(mutant: cmutant.Mutant):
        assertions = set()
        features = mutant.features
        features: cmutant.StateErrorGraph
        if len(features.infections) > 0:
            for state_error in features.get_errors():
                state_error: cmutant.StateError
                for assertion in state_error.get_assertions():
                    assertions.add(assertion)
                for flow in state_error.in_flows:
                    flow: cmutant.StateErrorFlow
                    for assertion in flow.get_constraints():
                        assertions.add(assertion)
        elif features.get_reachability() is not None:
            for assertion in features.reachability:
                assertions.add(assertion)
        return assertions


class MutantWordEncode:
    """
    Encode the features of mutation into words
    """

    def __init__(self, get_assertions, encode_assertion, prob_threshold: float):
        """
        :param get_assertions: collect the semantic assertions in features
        :param encode_assertion: generate the word for semantic assertions
        """
        self.word2int = Word2Integer()
        self.get_assertions = get_assertions
        self.encode_assertion = encode_assertion
        self.prob_threshold = prob_threshold
        return

    def encode(self, mutant: cmutant.Mutant):
        assertions = self.get_assertions(mutant)
        mutant.feature_vector = list()
        mutant.feature_words = list()
        for assertion in assertions:
            word = self.encode_assertion(assertion)
            code = self.word2int.encode(word)
            mutant.feature_vector.append(code)
            mutant.feature_words.append(word)
        mutant.labels.set_killing_label_by_probability(self.prob_threshold)
        return


class MutantDataFrame:
    def __init__(self, program_directory: str, encoder: MutantWordEncode):
        self.program = cprogram.Program(program_directory)
        for mutant in self.program.mutant_space.get_mutants():
            mutant: cmutant.Mutant
            encoder.encode(mutant)
        self.words = encoder.word2int.words
        return

    def get_program(self):
        return self.program

    def get_name(self):
        return self.program.name

    def get_mutants(self):
        """
        :return: list of mutation identifiers
        """
        mutants = list()
        for mutant in self.program.mutant_space.get_mutants():
            mutants.append(mutant.get_id())
        return mutants

    def get_features(self):
        """
        get the sparse matrix representing the features of each mutant
        :return:
        """
        row, rows, columns, data = 0, list(), list(), list()
        for mutant in self.program.mutant_space.get_mutants():
            for feature in mutant.feature_vector:
                rows.append(row)
                columns.append(feature)
                data.append(1)
            row = row + 1
        return sparse.csr_matrix((data, (rows, columns)), shape=(
            len(self.program.mutant_space.mutants), len(self.words)))

    def get_labels(self):
        labels = list()
        for mutant in self.program.mutant_space.get_mutants():
            labels.append(mutant.labels.label)
        return labels


def test_data_frame():
    data_directory = 'C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data'
    output_directory = 'C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output'
    for file_name in os.listdir(data_directory):
        program_directory = os.path.join(data_directory, file_name)
        encoder = MutantWordEncode(SemanticAssertionEncode.get_infection_assertions,
                                   SemanticAssertionEncode.get_assertion_cir_node, 0.002)
        data_frame = MutantDataFrame(program_directory, encoder)
        encoder.word2int.save(os.path.join(output_directory, data_frame.get_name() + '.txt'))
        print('Load', len(data_frame.get_mutants()), 'mutants from', data_frame.get_name())
    return


if __name__ == '__main__':
    test_data_frame()
