import os
import math
import pydotplus
from six import StringIO
import src.cmodel.ccode as ccode
import src.cmodel.mutant as cmutant
import src.cmodel.program as cprogram
import src.mining.encode as encode
import sklearn.tree as tree
import sklearn.metrics as metrics
import seaborn as sns
import matplotlib.pyplot as plt
import random as random


class OptimalClassifyEvaluator:
    """
    Using optimal classification method to evaluate the performance of the encoded.
    """

    def __init__(self):
        self.mapping = dict()
        return

    def fit(self, data_frame: encode.MutantDataFrame):
        for mutant in data_frame.program.mutant_space.get_mutants():
            mutant: cmutant.Mutant
            key = str(mutant.feature_vec)
            if key not in self.mapping:
                self.mapping[key] = list()
            self.mapping[key].append(mutant)
        return

    def __count_on_feature__(self, key: str):
        """
        count the number of label-0 and label-1 in the feature vector lines
        :param feature_vec:
        :return: dict{}
        """
        counter_map = dict()
        counter_map[0] = 0
        counter_map[1] = 0
        if key in self.mapping:
            mutants = self.mapping[key]
            for mutant in mutants:
                label = mutant.label
                counter_map[label] += 1
        return counter_map

    def __get_entropy_of_feature__(self, key: str):
        """
        get the entropy of each feature point
        :return:
        """
        counter_map = self.__count_on_feature__(key)
        equivalence, trivial = counter_map[0], counter_map[1]
        if equivalence == 0 or trivial == 0:
            return 0
        else:
            prob = equivalence / (equivalence + trivial + 0.0)
            lent = prob * math.log(prob, 2)
            rent = (1 - prob) * math.log(1 - prob, 2)
            return - (lent + rent)

    def __predict_on_feature__(self, key: str):
        """
        determine the label hold at the feature point
        :param feature_vec:
        :return:
        """
        counter_map = self.__count_on_feature__(key)
        if counter_map[0] >= counter_map[1]:
            return 0
        else:
            return 1

    def __generate_line_of_mutant__(self, key: str, mutant: cmutant.Mutant):
        """
        program, id, class, operator, line, code_segment, parameter?,
        label, p_label, result, feature, feature_size, counter[0], counter[1]
        :param key:
        :param mutant:
        :return:
        """
        program = mutant.space.program.name
        identifier = mutant.get_id()
        m_class = mutant.muta_class
        m_operator = mutant.muta_operator
        ast_location = mutant.get_location()
        source_code = mutant.space.program.source_code
        source_code: ccode.SourceCode
        line = source_code.line_of(ast_location.beg_index)
        code_segment = '\"' + ast_location.get_code(True) + '\"'
        parameter = ''
        if mutant.has_parameter():
            parameter = str(mutant.get_parameter())
        label = mutant.label
        p_label = self.__predict_on_feature__(key)
        result = (label == p_label)
        counter_map = self.__count_on_feature__(key)
        return [program, identifier, m_class, m_operator, line, code_segment, parameter,
                label, p_label, result, mutant.feature_words,
                counter_map[0] + counter_map[1], counter_map[0], counter_map[1]]

    def evaluate(self, file_path: str):
        """
        evaluate over the existing samples in the mapping
        :return:
        """
        entropy_list = list()
        labels = list()
        p_labels = list()
        error_map = dict()
        error_size = 0
        with open(file_path, 'w') as writer:
            writer.write('program\tid\tclass\toperator\tline\tlocation\tparameter'
                         'label\tp_label\tresult\tfeature\tsize\tEqvSize\tTriSize\n')
            for key, mutants in self.mapping.items():
                entropy = self.__get_entropy_of_feature__(key)
                entropy_list.append(entropy)
                p_label = self.__predict_on_feature__(key)
                for mutant in mutants:
                    mutant: cmutant.Mutant
                    labels.append(mutant.label)
                    p_labels.append(p_label)
                    str_line = self.__generate_line_of_mutant__(key, mutant)
                    for argument in str_line:
                        writer.write(str(argument))
                        writer.write('\t')
                    writer.write('\n')
                    if mutant.label != p_label:
                        m_class = mutant.get_muta_class()
                        if m_class not in error_map:
                            error_map[m_class] = 0
                        error_map[m_class] += 1
                        error_size += 1
        print(metrics.classification_report(labels, p_labels, target_names=['Equiv', 'Trivial']))
        print()
        print('In total there are', error_size, 'errors in feature space.')
        print(error_map)
        sns.kdeplot(entropy_list, color='r', label='feature-entropy')
        plt.show()
        return


def evaluate_feature_space(encoding_class):
    """
    evaluate the feature space generated in specified encoding method
    :param encoding_class: infection or error_set
    :return:
    """
    data_directory = 'C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data'
    output_directory = 'C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output'
    evaluator = OptimalClassifyEvaluator()
    for file_name in os.listdir(data_directory):
        program_directory = os.path.join(data_directory, file_name)
        program = cprogram.Program(program_directory)
        data_frame = encode.MutantDataFrame(program, encoding_class())
        print('Load', len(data_frame.program.mutant_space.mutants), 'mutants from', file_name)
        evaluator.fit(data_frame)
    print()
    evaluator.evaluate(os.path.join(output_directory, 'feature_evaluate.txt'))
    return


class MutantPattern:
    """
    [pattern_vector, samples, equiv_size, trivial_size, probability]
    """

    @staticmethod
    def __match_with__(feature_vector, pattern_vector):
        """
        determine whether the feature vector matches with the pattern vector, iff.
        all the words in pattern vector occur at least once in feature vector.
        :param feature_vector:
        :param pattern_vector:
        :return:
        """
        for code in pattern_vector:
            if code not in feature_vector:
                return False
        return True

    def __init__(self, mutants, pattern_vector):
        self.pattern_vector = pattern_vector
        self.pattern_vector.sort()
        self.samples = list()
        self.equiv_size = 0
        self.trivial_size = 0
        for mutant in mutants:
            mutant: cmutant.Mutant
            if MutantPattern.__match_with__(mutant.feature_vec, self.pattern_vector):
                self.samples.append(mutant)
                if mutant.label == 0:
                    self.equiv_size += 1
                else:
                    self.trivial_size += 1
        self.total_size = self.equiv_size + self.trivial_size
        if self.total_size == 0:
            self.probability = 0
        else:
            self.probability = self.equiv_size / (self.total_size + 0.0)
        return

    def __len__(self):
        """
        number of words in the pattern
        :return:
        """
        return len(self.pattern_vector)

    def get_key(self):
        return str(self.pattern_vector)

    def is_parent_of(self, child_pattern):
        child_pattern: MutantPattern
        for code in self.pattern_vector:
            if code not in child_pattern.pattern_vector:
                return False
        return True

    def subsume(self, pattern):
        """
        whether the pattern subsumes another
        :param pattern:
        :return:
        """
        if len(self.pattern_vector) > len(pattern.pattern_vector):
            return False
        else:
            mutants = set()
            pattern: MutantPattern
            for sample in self.samples:
                mutants.add(sample.id)
            for sample in pattern.samples:
                if sample.id not in mutants:
                    return False
            return True


class CompositePatternMiner:
    """
    Mine the pattern of equivalent mutant patterns based on composition algorithm.
    """

    def __init__(self, prob_threshold: float, min_samples: int, max_pattern_size: int, max_feature_size: int):
        """
        :param prob_threshold: the threshold of probability that allows a pattern being filtered
        :param min_samples: the minimal number of mutation samples in each pattern
        :param max_pattern_size: the maximal size of pattern allowed being searched in algorithm
        :param max_feature_size: the maximal number of words considered in feature space.
        """
        self.prob_threshold = prob_threshold
        self.min_samples = min_samples
        self.max_pattern_size = max_pattern_size
        self.max_feature_size = max_feature_size
        self.solutions = dict()
        return

    def __get_features__(self, mutant: cmutant.Mutant):
        """
        get the features in mutant that will be used to compose in algorithm
        :return:
        """
        if len(mutant.feature_vec) <= self.max_feature_size:
            return mutant.feature_vec
        else:
            feature_set = set()
            while len(feature_set) < self.max_feature_size:
                index = random.randint(0, len(mutant.feature_vec))
                for feature in mutant.feature_vec:
                    if index <= 0:
                        feature_set.add(feature)
                        break
                    else:
                        index = index - 1
            return feature_set

    def __valid__(self, pattern: MutantPattern):
        """
        determine whether the pattern is valid for being searched
        :param pattern:
        :return:
        """
        if len(pattern.samples) < self.min_samples:
            return False        # too small
        elif len(pattern.pattern_vector) > self.max_pattern_size:
            return False        # too complicated
        else:
            return True         # valid for search

    def __mine__(self, features, parent_pattern: MutantPattern):
        """
        :param mutant:
        :param parent_pattern:
        :return:
        """
        if self.__valid__(parent_pattern):
            self.solutions[parent_pattern.get_key()] = parent_pattern
            if parent_pattern.probability >= self.prob_threshold:
                return      # when probability reaches the threshold, stop search the children
            else:
                for code in features:
                    if code not in parent_pattern.pattern_vector:
                        ''' 1. create the vector of child pattern '''
                        new_pattern_vector = list()
                        for old_code in parent_pattern.pattern_vector:
                            new_pattern_vector.append(old_code)
                        new_pattern_vector.append(code)
                        new_pattern_vector.sort()

                        if str(new_pattern_vector) not in self.solutions:
                            child_pattern = MutantPattern(parent_pattern.samples, new_pattern_vector)
                            self.__mine__(features, child_pattern)        # continue to search child pattern
        else:
            self.solutions[str(parent_pattern)] = None  # invalid pattern is not recorded
        return

    @staticmethod
    def is_parent(parent: MutantPattern, child: MutantPattern):
        return parent.is_parent_of(child)

    @staticmethod
    def is_subsume(parent: MutantPattern, child: MutantPattern):
        return parent.subsume(child)

    def __remove_redundant__(self, is_better):
        """
        remove the redundant patterns based on parent-child elimination algorithm
        :return:
        """
        # 1. remove invalid patterns
        removed_set = set()
        for key, pattern in self.solutions.items():
            if pattern is None:
                removed_set.add(key)
            elif pattern.probability < self.prob_threshold:
                removed_set.add(key)
        for key in removed_set:
            self.solutions.pop(key)

        # 2. remove children parent
        visited_set = set()
        while True:
            # 1. select one not-visited pattern
            parent_pattern = None
            for key, pattern in self.solutions.items():
                if key not in visited_set:
                    pattern: MutantPattern
                    visited_set.add(key)
                    parent_pattern = pattern
                    break

            # 2. when the redundant ones are removed
            if parent_pattern is None:
                break

            # 3. find all the redundant patterns to remove
            parent_pattern: MutantPattern
            removed_set.clear()
            for key, pattern in self.solutions.items():
                if is_better(parent_pattern, pattern):
                    removed_set.add(key)

            # 4. remove all the redundant patterns within
            for key in removed_set:
                self.solutions.pop(key)
            self.solutions[parent_pattern.get_key()] = parent_pattern
        return

    def __output__(self, writer, words):
        """
        feature_size, feature_words, mutants_size, equiv_size, trivial_size, probability, mutants
        :param writer:
        :return:
        """
        writer.write('length\tfeature_words\ttotal_size\t'
                     'equivalent\ttrivial\tprobability\tmutants\n')
        mutants_set = set()
        for key, pattern in self.solutions.items():
            pattern: MutantPattern
            feature_words = list()
            for code in pattern.pattern_vector:
                word = words[code]
                feature_words.append(word)
            writer.write(str(len(pattern)) + '\t')
            writer.write(str(feature_words) + '\t')
            writer.write(str(pattern.total_size) + '\t')
            writer.write(str(pattern.equiv_size) + '\t')
            writer.write(str(pattern.trivial_size) + '\t')
            writer.write(str(pattern.probability) + '\t')
            mutant_list = list()
            for mutant in pattern.samples:
                mutant_list.append(mutant.id)
                if mutant.id not in mutants_set:
                    mutants_set.add(mutant.id)
            writer.write(str(mutant_list) + '\t')
            writer.write('\n')
        return mutants_set

    def mine(self, data_frame: encode.MutantDataFrame, file_path: str):
        """
        Mine all the patterns for each equivalent mutant in data frame
        :param data_frame:
        :param file_path:
        :return:
        """
        counter, equivalence = 0, 0
        number = len(data_frame.program.mutant_space.mutants)
        self.solutions.clear()
        with open(file_path, 'w') as writer:
            for mutant in data_frame.program.mutant_space.get_mutants():
                counter += 1
                if mutant.label == 0:
                    equivalence += 1
                    features = self.__get_features__(mutant)
                    print('\t--> Start to mine mutant#', mutant.id, 'with selecting', len(features), 'words',
                          'of', self.max_feature_size, 'features.')
                    for feature in features:
                        init_pattern = MutantPattern(data_frame.program.mutant_space.get_mutants(), [feature])
                        self.__mine__(features, init_pattern)
                    print('\t--> Progress at mutant#', mutant.id, 'by',
                          len(self.solutions), 'patterns (', counter, '/', number, ')')
            # the algorithm here to eliminate redundant pattern
            self.__remove_redundant__(CompositePatternMiner.is_subsume)
            covering_set = self.__output__(writer, data_frame.words)
            if equivalence > 0:
                writer.write('Equivalence\t' + str(equivalence) + '\tPatterns\t' +
                             str(len(self.solutions)) + '\tCovering\t' + str(len(covering_set))
                             + '\tRecall\t' + str(int((100 * len(covering_set)) / equivalence)) + '%')
        self.solutions.clear()
        return


def composite_pattern_mine(encoding_class):
    """
    Use composite pattern mining algorithm
    :return:
    """
    data_directory = 'C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data'
    output_directory = 'C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output\\freq_pattern'
    if not os.path.exists(output_directory):
        os.mkdir(output_directory)
    for file_name in os.listdir(data_directory):
        program_directory = os.path.join(data_directory, file_name)
        program = cprogram.Program(program_directory)
        data_frame = encode.MutantDataFrame(program, encoding_class())
        print('Load', len(data_frame.program.mutant_space.mutants), 'mutants with',
              len(data_frame.words), 'words in', file_name)
        output_dir = os.path.join(output_directory, file_name)
        if not os.path.exists(output_dir):
            os.mkdir(output_dir)
        pattern_file = os.path.join(output_dir, file_name + '.pls')
        miner = CompositePatternMiner(0.70, 1, 4, 24)
        miner.mine(data_frame, pattern_file)
        print('Mining', len(data_frame.program.mutant_space.mutants), 'mutants in', file_name)
    return


class DecisionTreeMiner:
    """
    Use decision tree classifier to mine the pattern of equivalent mutant
    """

    def __init__(self, prob_threshold: float):
        self.classifier = None
        self.solutions = dict()
        self.prob_threshold = prob_threshold
        return

    def __extract_data_lines__(self, data_frame: encode.MutantDataFrame):
        """
        extract list of mutation identifiers, feature vectors (in 0-1 form) and labels
        :param data_frame:
        :return: identifiers, feature_vectors, labels
        """
        self.classifier = tree.DecisionTreeClassifier()
        self.data_frame = data_frame
        self.identifiers = list()
        self.feature_vectors = list()
        self.labels = list()
        length = len(data_frame.words)
        for mutant in data_frame.program.mutant_space.get_mutants():
            self.identifiers.append(mutant.id)
            self.labels.append(mutant.label)
            feature_vector = list()
            for k in range(0, length):
                feature_vector.append(0)
            for word_code in mutant.feature_vec:
                feature_vector[word_code] += 1
            self.feature_vectors.append(feature_vector)
        return

    def __mine__(self):
        self.classifier: tree.DecisionTreeClassifier
        threshold = self.classifier.tree_.threshold
        tree_features = self.classifier.tree_.feature
        features = self.feature_vectors
        node_indicator = self.classifier.decision_path(features)
        leave_id = self.classifier.apply(features)
        self.solutions.clear()
        equivalence = 0
        for index in range(0, len(features)):
            label = self.labels[index]
            if label == 0:
                equivalence += 1
                p_label = self.classifier.predict([features[index]])[0]
                if p_label == 0:
                    node_index = node_indicator.indices[node_indicator.indptr[index]: node_indicator.indptr[index + 1]]
                    decision_path = list()
                    for tree_node_id in node_index:
                        if leave_id[index] == tree_node_id:  # when root is reached
                            continue
                        elif features[index][tree_features[tree_node_id]] >= threshold[tree_node_id]:
                            # when feature holds
                            decision_path.append(tree_features[tree_node_id])
                    decision_path.sort()
                    key = str(decision_path)
                    if key not in self.solutions:
                        self.solutions[key] = MutantPattern(
                            self.data_frame.program.mutant_space.get_mutants(), decision_path)
        return equivalence

    @staticmethod
    def is_parent(parent: MutantPattern, child: MutantPattern):
        return parent.is_parent_of(child)

    @staticmethod
    def is_subsume(parent: MutantPattern, child: MutantPattern):
        return parent.subsume(child)

    def __remove_redundant__(self, is_better):
        """
        remove the redundant patterns based on parent-child elimination algorithm
        :return:
        """
        # 1. remove invalid patterns
        removed_set = set()
        for key, pattern in self.solutions.items():
            if pattern is None:
                removed_set.add(key)
            elif pattern.probability < self.prob_threshold:
                removed_set.add(key)
        for key in removed_set:
            self.solutions.pop(key)

        # 2. remove children parent
        visited_set = set()
        while True:
            # 1. select one not-visited pattern
            parent_pattern = None
            for key, pattern in self.solutions.items():
                if key not in visited_set:
                    pattern: MutantPattern
                    visited_set.add(key)
                    parent_pattern = pattern
                    break

            # 2. when the redundant ones are removed
            if parent_pattern is None:
                break

            # 3. find all the redundant patterns to remove
            parent_pattern: MutantPattern
            removed_set.clear()
            for key, pattern in self.solutions.items():
                if is_better(parent_pattern, pattern):
                    removed_set.add(key)

            # 4. remove all the redundant patterns within
            for key in removed_set:
                self.solutions.pop(key)
            self.solutions[parent_pattern.get_key()] = parent_pattern
        return

    def __output__(self, writer, words):
        """
        feature_size, feature_words, mutants_size, equiv_size, trivial_size, probability, mutants
        :param writer:
        :return:
        """
        writer.write('length\tfeature_words\ttotal_size\t'
                     'equivalent\ttrivial\tprobability\tmutants\n')
        mutants_set = set()
        for key, pattern in self.solutions.items():
            pattern: MutantPattern
            feature_words = list()
            for code in pattern.pattern_vector:
                word = words[code]
                feature_words.append(word)
            writer.write(str(len(pattern)) + '\t')
            writer.write(str(feature_words) + '\t')
            writer.write(str(pattern.total_size) + '\t')
            writer.write(str(pattern.equiv_size) + '\t')
            writer.write(str(pattern.trivial_size) + '\t')
            writer.write(str(pattern.probability) + '\t')
            mutant_list = list()
            for mutant in pattern.samples:
                mutant_list.append(mutant.id)
                if mutant.id not in mutants_set:
                    mutants_set.add(mutant.id)
            writer.write(str(mutant_list) + '\t')
            writer.write('\n')
        return mutants_set

    def mine(self, data_frame: encode.MutantDataFrame, pattern_file: str):
        """
        mine the pattern of decision tree path
        :param pattern_file:
        :param data_frame:
        :return:
        """
        self.__extract_data_lines__(data_frame)
        self.classifier.fit(self.feature_vectors, self.labels)
        equivalence = self.__mine__()
        self.__remove_redundant__(DecisionTreeMiner.is_subsume)
        with open(pattern_file, 'w') as writer:
            covering_set = self.__output__(writer, data_frame.words)
            if equivalence > 0:
                writer.write('Equivalence\t' + str(equivalence) + '\tPatterns\t' +
                             str(len(self.solutions)) + '\tCovering\t' + str(len(covering_set))
                             + '\tRecall\t' + str(int((100 * len(covering_set)) / equivalence)) + '%')
        return

    @staticmethod
    def __normalize_graphviz_words__(words):
        new_words = list()
        for word in words:
            new_word = ''
            for index in range(0, len(word)):
                char = word[index]
                if char == '\"':
                    char = '``'
                new_word += char
            new_words.append(new_word)
        return new_words

    def evaluate(self):
        p_labels = self.classifier.predict(self.feature_vectors, self.labels)
        print(metrics.classification_report(self.labels, p_labels))
        return

    def write_decision_tree(self, tree_file: str):
        dot_data = StringIO()
        self.classifier: tree.DecisionTreeClassifier
        tree.export_graphviz(self.classifier, feature_names=DecisionTreeMiner.__normalize_graphviz_words__(
            self.data_frame.words), class_names=['Equiv', 'Non-Equiv'], filled=True, out_file=dot_data)
        graph = pydotplus.graph_from_dot_data(dot_data.getvalue())
        graph.write_pdf(tree_file)
        return


def decision_tree_mine(encoding_class):
    """
    Use decision tree to mine the equivalent mutant pattern
    :param encoding_class:
    :return:
    """
    data_directory = 'C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data'
    output_directory = 'C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output\\decision_tree'
    if not os.path.exists(output_directory):
        os.mkdir(output_directory)
    filters = ['md4']
    for file_name in os.listdir(data_directory):
        if file_name in filters:
            continue
        program_directory = os.path.join(data_directory, file_name)
        program = cprogram.Program(program_directory)
        data_frame = encode.MutantDataFrame(program, encoding_class())
        print('Load', len(data_frame.program.mutant_space.mutants), 'mutants from', file_name)
        output_dir = os.path.join(output_directory, file_name)
        if not os.path.exists(output_dir):
            os.mkdir(output_dir)
        pattern_file = os.path.join(output_dir, file_name + '.dtp')
        miner = DecisionTreeMiner(0.70)
        miner.mine(data_frame, pattern_file)
        miner.evaluate()
        miner.write_decision_tree(os.path.join(output_dir, file_name + '.pdf'))
        print('Mining', len(data_frame.program.mutant_space.mutants), 'mutants in', file_name)
    return


if __name__ == '__main__':
    print('Testing start.')
    composite_pattern_mine(encode.StateErrorSetEncode)
    print('Testing end for all.')
