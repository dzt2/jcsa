import os
import src.cmodel.ccode as ccode
import src.cmodel.program as cprogram
import src.cmodel.mutant as cmutant
import scipy.sparse as sparse


class Word2Integer:
    """
        Used to translate the semantic assertion (or its word) to unique integer ID
        """

    def __init__(self):
        self.words = list()
        self.index = dict()
        return

    def encode(self, word):
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
                writer.write(str(word).strip())
                writer.write('\n')
        return


class SemanticFeatureEncodeFunctions:
    """
    Used to encode the semantic assertions
    """
    @staticmethod
    def get_direct_assertions(mutant: cmutant.Mutant):
        assertions = set()
        features = mutant.features
        features: cmutant.SemanticErrorGraph
        if len(features) > 0:
            entry = features.get_node(0)
            entry: cmutant.SemanticErrorNode
            for edge in entry.get_ou_edges():
                edge: cmutant.SemanticErrorEdge
                for assertion in edge.assertions:
                    assertions.add(assertion)
                node = edge.get_target()
                node: cmutant.SemanticErrorNode
                for assertion in node.assertions:
                    assertions.add(assertion)
        return assertions

    @staticmethod
    def get_all_assertions(mutant: cmutant.Mutant):
        assertions = set()
        features = mutant.features
        features: cmutant.SemanticErrorGraph
        for error_node in features.get_nodes():
            error_node: cmutant.SemanticErrorNode
            for assertion in error_node.assertions:
                assertions.add(assertion)
            for edge in error_node.get_ou_edges():
                edge: cmutant.SemanticErrorEdge
                for assertion in edge.assertions:
                    assertions.add(assertion)
        return assertions

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
    def get_assertion_instance(assertion: cmutant.SemanticAssertion):
        return assertion


class MutantFeatureEncoder:
    """
    To encode the semantic assertions hold by each mutation as feature vector
    """

    def __init__(self, get_assertions, prob_threshold: float):
        """
        :param get_assertions: to collect the semantic assertions being encoded
        :param prob_threshold: the minimal probability to determine equivalence
        """
        self.word2int = Word2Integer()
        self.get_assertions = get_assertions
        self.assertion_encode = SemanticFeatureEncodeFunctions.get_assertion_instance
        self.prob_threshold = prob_threshold
        return

    def encode(self, mutant: cmutant.Mutant):
        assertions = self.get_assertions(mutant)
        words = list()
        for assertion in assertions:
            word = self.assertion_encode(assertion)
            words.append(word)
        mutant.feature_vector = self.word2int.encodes(words)
        mutant.feature_words = words
        mutant.labels.set_killing_label_by_probability(self.prob_threshold)
        return


class MutantDataFrame:
    def __init__(self, program_directory: str, encoder: MutantFeatureEncoder):
        self.program = cprogram.Program(program_directory)
        for mutant in self.program.mutant_space.get_mutants():
            mutant: cmutant.Mutant
            encoder.encode(mutant)
        self.word2int = encoder.word2int
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
    output_directory = 'C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output\\mutation'
    if not os.path.exists(output_directory):
        os.mkdir(output_directory)
    for file_name in os.listdir(data_directory):
        program_directory = os.path.join(data_directory, file_name)
        encoder = MutantFeatureEncoder(SemanticFeatureEncodeFunctions.get_all_assertions, 0.005)
        data_frame = MutantDataFrame(program_directory, encoder)
        # encoder.word2int.save(os.path.join(output_directory, data_frame.get_name() + '.txt'))
        print('Load', len(data_frame.get_mutants()), 'mutants from', data_frame.get_name(),
              'with', len(data_frame.words), 'words')
        data_frame.program.mutant_space.output(os.path.join(output_directory, file_name + ".mut"),
                                               SemanticFeatureEncodeFunctions.get_assertion_source_code)
    return


if __name__ == "__main__":
    test_data_frame()
