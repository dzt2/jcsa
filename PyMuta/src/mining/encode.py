"""
encode.py encodes the features of each mutant into set of words (normalized).
"""


import os
import src.cmodel.ccode as ccode
import src.cmodel.program as cprogram
import src.cmodel.mutant as cmutant


class Word2Integer:
    """
    Used to translate word into integer code or reversed
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
            code_list.append(self.index[word])
        return code_list

    def decode(self, code: int):
        return self.words[code]

    def decodes(self, code_list):
        words = list()
        for code in code_list:
            word = self.words[code]
            words.append(word)
        return words

    def save(self, file_path: str):
        with open(file_path, 'w') as writer:
            for word in self.words:
                writer.write(word)
                writer.write('\n')
        return

    def load(self, file_path: str):
        self.words.clear()
        self.index.clear()
        with open(file_path, 'r') as reader:
            for line in reader:
                line = line.strip()
                if len(line) > 0:
                    word = line
                    self.index[word] = len(self.words)
                    self.words.append(word)
        return

    def __len__(self):
        return len(self.words)

    def __str__(self):
        return str(self.words)


def normalize_word(program: cprogram.Program, assertion: str, max_code_length=32):
    """
    translate the string of the semantic assertion into normalized form with code segment at line
    :param max_code_length:
    :param program:
    :param assertion:
    :return:
    """
    index = assertion.index('(')
    func_name = assertion[0: index].strip()
    param_str = assertion[index + 1: len(assertion) - 1].strip()
    if ';' in param_str:
        parameters = param_str.split(';')
    else:
        parameters = [param_str]
    norm_parameters = list()
    for parameter in parameters:
        if '#' in parameter:
            k = parameter.index('#')
            cir_id = int(parameter[k + 1:].strip())
            cir_node = program.cir_tree.nodes[cir_id]
            cir_node: ccode.CirNode
            if cir_node.has_ast_source():
                ast_source = cir_node.ast_source
                code_segment = ast_source.get_code(True)
                if len(code_segment) > max_code_length:
                    code_segment = code_segment[0: max_code_length] + '...'
                line = program.source_code.line_of(ast_source.beg_index) + 1
                parameter = '\"' + code_segment + '\" at line ' + str(line)
        norm_parameters.append(parameter)
    norm_word = func_name + '( '
    for parameter in norm_parameters:
        norm_word += parameter + '; '
    return norm_word + ')'


def normalize_words(program: cprogram.Program, assertions, max_code_length=32):
    norm_assertions = list()
    for assertion in assertions:
        norm_assertions.append(normalize_word(program, assertion, max_code_length))
    return norm_assertions


def encode_mutant_label(label: str):
    if label == 'E' or label == 'e':
        return 0
    elif label == 'N' or label == 'n':
        return 1
    else:
        return label


class StateInfectionEncode:
    """
    Only encode the state errors and constraint correlated with infection on initial point.
    """

    def __init__(self):
        self.word2int = Word2Integer()
        return

    def __get_assertions__(self, mutant: cmutant.Mutant):
        assertions = set()
        for infection in mutant.features.get_infections():
            infection: cmutant.StateErrorFlow
            for constraint in infection.constraints:
                word = str(constraint)
                self.word2int.encode(word)
                assertions.add(word)
            for assertion in infection.target.assertions:
                word = str(assertion)
                self.word2int.encode(word)
                assertions.add(word)
        return assertions

    def encode(self, mutant: cmutant.Mutant):
        """
        add an attribute feature_vec to the mutant as the feature vector
        encoded as word-bag list of its feature words and the feature_words
        as the normalized word representing the assertion in mutant.
        :param mutant:
        :return:
        """
        assertions = self.__get_assertions__(mutant)
        feature_vec = self.word2int.encodes(assertions)
        feature_vec.sort()
        mutant.feature_vec = feature_vec
        mutant.feature_words = normalize_words(mutant.space.program, assertions)
        mutant.label = encode_mutant_label(mutant.label)
        return


class StateErrorSetEncode:
    """
    Only encode the entire set of state errors into the mutant's feature vector
    """

    def __init__(self):
        self.word2int = Word2Integer()
        return

    def __get_assertions__(self, mutant: cmutant.Mutant):
        assertions = set()
        for infection in mutant.features.get_infections():
            infection: cmutant.StateErrorFlow
            for constraint in infection.constraints:
                word = str(constraint)
                self.word2int.encode(word)
                assertions.add(word)
            for assertion in infection.target.assertions:
                word = str(assertion)
                self.word2int.encode(word)
                assertions.add(word)
        for state_error in mutant.features.get_errors():
            state_error: cmutant.StateError
            for assertion in state_error.get_assertions():
                word = str(assertion)
                self.word2int.encode(word)
                assertions.add(word)
        return assertions

    def encode(self, mutant: cmutant.Mutant):
        """
        add an attribute feature_vec to the mutant as the feature vector
        encoded as word-bag list of its feature words and the feature_words
        as the normalized word representing the assertion in mutant.
        :param mutant:
        :return:
        """
        assertions = self.__get_assertions__(mutant)
        feature_vec = self.word2int.encodes(assertions)
        feature_vec.sort()
        mutant.feature_vec = feature_vec
        mutant.feature_words = normalize_words(mutant.space.program, assertions)
        mutant.label = encode_mutant_label(mutant.label)
        return


class MutantDataFrame:
    """
    It contains feature vector, words and labeled information for each mutant
    """
    def __get_normal_words__(self, encoder):
        self.words = list()
        for word in encoder.word2int.words:
            word = normalize_word(self.program, word)
            self.words.append(word)
        return

    def __init__(self, program: cprogram.Program, encoder):
        """
        :param program:
        :param encoder: either StateInfectionEncode or StateErrorSetEncode
        """
        self.program = program
        for mutant in self.program.mutant_space.get_mutants():
            encoder.encode(mutant)
        self.__get_normal_words__(encoder)
        return


def test_data_frame():
    data_directory = 'C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data'
    output_directory = 'C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output'
    for file_name in os.listdir(data_directory):
        program_directory = os.path.join(data_directory, file_name)
        program = cprogram.Program(program_directory)
        encoder = StateInfectionEncode()
        data_frame = MutantDataFrame(program, encoder)
        print('Load', len(data_frame.program.mutant_space.mutants), 'mutants from', file_name)
        encoder.word2int.save(os.path.join(output_directory, file_name + '.txt'))
    return


if __name__ == '__main__':
    test_data_frame()
