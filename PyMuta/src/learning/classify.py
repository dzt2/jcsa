import os
import src.cmodel.ccode as ccode
import src.cmodel.mutant as cmutant
import src.cmodel.program as cprogram
import src.learning.encode as encode
import math
import sklearn.metrics as metrics
import seaborn as sns
import matplotlib.pyplot as plt
import random
import sklearn.tree as tree
import pydotplus
from six import StringIO


class ClassificationEvaluator:
    """
    To evaluate the performance of classification on specified data set.
    """

    def __init__(self):
        self.mapping = dict()
        return

    @staticmethod
    def __get_key__(mutant: cmutant.Mutant):
        feature_vector = list()
        for feature in mutant.feature_vector:
            feature_vector.append(feature)
        feature_vector.sort()
        return mutant.space.program.name + '@' + str(feature_vector)

    def fit(self, data_frame: encode.MutantDataFrame):
        for mutant in data_frame.get_program().mutant_space.get_mutants():
            key = ClassificationEvaluator.__get_key__(mutant)
            if key not in self.mapping:
                self.mapping[key] = list()
            self.mapping[key].append(mutant)
        return

    def __get_distribution__(self, key: str):
        """
        count the distribution of samples in feature point
        :param key:
        :return: dict[label, integer]
        """
        distribution = dict()
        distribution[0] = 0
        distribution[1] = 0
        for mutant in self.mapping[key]:
            mutant: cmutant.Mutant
            label = mutant.labels.label
            distribution[label] += 1
        return distribution

    def __evaluate_on__(self, key: str):
        """
        :param key:
        :return: pred_label, error_rate, entropy
        """
        distribution = self.__get_distribution__(key)
        p_label, p_number, total_size = None, 0, 0.0
        for label, number in distribution.items():
            if number > p_number:
                p_number = number
                p_label = label
                total_size += number
        probability = distribution[p_label] / total_size
        if p_number == 0 or p_number == total_size:
            entropy = 0.0
        else:
            entropy = probability * math.log(probability, 2) + (1 - probability) * math.log(1 - probability, 2)
        return p_label, 1 - probability, -entropy

    def __generate_line_of_mutant__(self, key: str, mutant: cmutant.Mutant, p_label):
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
        label = mutant.labels.label
        result = (label == p_label)
        counter_map = self.__get_distribution__(key)
        return [program, identifier, m_class, m_operator, line, code_segment, parameter,
                label, p_label, result, mutant.feature_words,
                counter_map[0] + counter_map[1], counter_map[0], counter_map[1]]

    def evaluate(self, file_path: str):
        entropy_list, labels, p_labels = list(), list(), list()
        error_map = dict()
        error_size = 0
        with open(file_path, 'w') as writer:
            writer.write('program\tid\tclass\toperator\tline\tlocation\tparameter'
                         'label\tp_label\tresult\tfeature\tsize\tEqvSize\tTriSize\n')
            for key, mutants in self.mapping.items():
                p_label, error_rate, entropy = self.__evaluate_on__(key)
                entropy_list.append(entropy)
                for mutant in mutants:
                    mutant: cmutant.Mutant
                    labels.append(mutant.labels.label)
                    p_labels.append(p_label)
                    str_line = self.__generate_line_of_mutant__(key, mutant, p_label)
                    for argument in str_line:
                        writer.write(str(argument))
                        writer.write('\t')
                    writer.write('\n')
                    if mutant.labels.label != p_label:
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


def evaluate_classifier(get_assertions, assertion_encode, prob_threshold):
    data_directory = 'C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data'
    output_directory = 'C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output'
    evaluator = ClassificationEvaluator()
    for file_name in os.listdir(data_directory):
        program_directory = os.path.join(data_directory, file_name)
        encoder = encode.MutantWordEncode(get_assertions, assertion_encode, prob_threshold)
        data_frame = encode.MutantDataFrame(program_directory, encoder)
        print('Load', len(data_frame.program.mutant_space.mutants), 'mutants from', file_name)
        evaluator.fit(data_frame)
    print()
    evaluator.evaluate(os.path.join(output_directory, 'feature_evaluate.txt'))
    return


class CompositeSearchClassifier:
    """
    Using composition algorithm to search the valid pattern of equivalent mutant.
    """

    def __init__(self, max_pattern_size: int, min_samples: int, min_confidence: float, max_confidence: float,
                 max_feature_size: int, target_label: int):
        """
        :param max_pattern_size: maximal size of pattern vector
        :param min_samples:      minimal number of mutants in pattern
        :param min_confidence:   minimal confidence of target mutants
        :param target_label:     the target label to be mined
        """
        self.solutions = dict()
        self.max_pattern_size = max_pattern_size
        self.min_samples = min_samples
        self.min_confidence = min_confidence
        self.max_confidence = max_confidence
        self.target_label = target_label
        self.max_feature_size = max_feature_size
        return

    def __mine__(self, parent_pattern: encode.MutantCluster, features):
        if str(parent_pattern) not in self.solutions:
            self.solutions[str(parent_pattern)] = parent_pattern
            if len(parent_pattern) >= self.max_pattern_size:
                return
            elif len(parent_pattern.samples) < self.min_samples:
                return
            elif parent_pattern.get_confidence(self.target_label) >= self.max_confidence:
                return
            else:
                for feature in features:
                    if feature not in parent_pattern.pattern_vector:
                        child_pattern = parent_pattern.get_child(feature)
                        self.__mine__(child_pattern, features)
        return

    def __filter__(self):
        remove_set = set()
        for key, pattern in self.solutions.items():
            pattern: encode.MutantCluster
            if len(pattern.samples) < self.min_samples:
                remove_set.add(key)
            elif pattern.get_confidence(self.target_label) < self.min_confidence:
                remove_set.add(key)
        for key in remove_set:
            self.solutions.pop(key)
        return

    @staticmethod
    def is_parent_of(pattern1: encode.MutantCluster, pattern2: encode.MutantCluster):
        for code in pattern1.pattern_vector:
            if code not in pattern2.pattern_vector:
                return False
        return True

    @staticmethod
    def is_subsume_on(pattern1: encode.MutantCluster, pattern2: encode.MutantCluster):
        if len(pattern1.pattern_vector) > len(pattern2.pattern_vector):
            return False
        else:
            mutants = set()
            for sample in pattern1.samples:
                mutants.add(sample.id)
            for sample in pattern2.samples:
                if sample.id not in mutants:
                    return False
            return True

    def __remove_redundant__(self, is_better):
        """
        remove the redundant patterns based on parent-child elimination algorithm
        :return:
        """
        # remove children parent
        removed_set = set()
        visited_set = set()
        while True:
            # 1. select one not-visited pattern
            parent_pattern = None
            for key, pattern in self.solutions.items():
                if key not in visited_set:
                    pattern: encode.MutantCluster
                    visited_set.add(key)
                    parent_pattern = pattern
                    break

            # 2. when the redundant ones are removed
            if parent_pattern is None:
                break

            # 3. find all the redundant patterns to remove
            removed_set.clear()
            for key, pattern in self.solutions.items():
                if is_better(parent_pattern, pattern):
                    removed_set.add(key)

            # 4. remove all the redundant patterns within
            for key in removed_set:
                self.solutions.pop(key)
            self.solutions[str(parent_pattern)] = parent_pattern
        return

    def __output__(self, file_path: str, data_frame: encode.MutantDataFrame):
        with open(file_path, 'w') as writer:
            writer.write('length\tfeature_words\ttotal_size\t'
                         'equivalent\ttrivial\tconfidence\tmutants\n')
            equivalence = 0
            for mutant in data_frame.program.mutant_space.get_mutants():
                if mutant.labels.label == self.target_label:
                    equivalence += 1
            mutants_set = set()
            predict_set = set()
            for key, pattern in self.solutions.items():
                if pattern is not None:
                    pattern: encode.MutantCluster
                    feature_words = list()
                    for code in pattern.pattern_vector:
                        word = data_frame.words[code]
                        feature_words.append(word)
                    writer.write(str(len(pattern)) + '\t')
                    writer.write(str(feature_words) + '\t')
                    writer.write(str(len(pattern.samples)) + '\t')
                    writer.write(str(pattern.distribution[0]) + '\t')
                    writer.write(str(pattern.distribution[1]) + '\t')
                    writer.write(str(pattern.get_confidence(self.target_label)) + '\t')
                    mutant_list = list()
                    for mutant in pattern.samples:
                        if mutant.labels.label == 0:
                            mutant_list.append(mutant.id)
                            if mutant.id not in mutants_set:
                                mutants_set.add(mutant.id)
                        predict_set.add(mutant.id)
                    writer.write(str(mutant_list) + '\t')
                    writer.write('\n')
            if equivalence > 0:
                writer.write('Equivalence\t' + str(equivalence) + '\tPatterns\t' +
                             str(len(self.solutions)) + '\tCovering\t' + str(len(mutants_set))
                             + '\tPrecision\t' + str(int((100 * len(mutants_set)) / len(predict_set))) + '%'
                             + '\tRecall\t' + str(int((100 * len(mutants_set)) / equivalence)) + '%')
            return mutants_set, predict_set

    def __features__(self, mutant: cmutant.Mutant):
        if len(mutant.feature_vector) <= self.max_feature_size:
            return mutant.feature_vector
        else:
            return mutant.feature_vector[0: self.max_feature_size]

    def mine(self, data_frame: encode.MutantDataFrame, file_path: str):
        # 1. mining all the possible valid pattern
        self.solutions.clear()
        for mutant in data_frame.program.mutant_space.get_mutants():
            mutant: cmutant.Mutant
            if mutant.labels.label == self.target_label:
                # print('\t\tFetch features from', len(mutant.feature_vector))
                features = self.__features__(mutant)
                print('\t\tStart mine mutant', mutant.id, 'with', len(features), 'features using', len(self.solutions))
                for feature in features:
                    init_pattern_vector = [feature]
                    if str(init_pattern_vector) not in self.solutions:
                        init_pattern = encode.MutantCluster(init_pattern_vector, mutant.space.get_mutants())
                        self.__mine__(init_pattern, features)
                # print('\t\tObtain', len(self.solutions), 'patterns until the mutant', mutant.id)

        # 2. get the optimal pattern set
        self.__filter__()
        self.__remove_redundant__(CompositeSearchClassifier.is_subsume_on)

        # 3. output the solutions to the file
        self.__output__(file_path, data_frame)
        self.solutions.clear()
        return


def composition_search_mining(get_assertions, assertion_encode, prob_threshold,
                              max_pattern_size, min_samples, min_confidence, max_feature_size):
    data_directory = 'C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data'
    output_directory = 'C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output\\freq_pattern'
    if not os.path.exists(output_directory):
        os.mkdir(output_directory)
    for file_name in os.listdir(data_directory):
        program_directory = os.path.join(data_directory, file_name)
        encoder = encode.MutantWordEncode(get_assertions, assertion_encode, prob_threshold)
        data_frame = encode.MutantDataFrame(program_directory, encoder)
        print('Load', len(data_frame.program.mutant_space.mutants), 'mutants with',
              len(data_frame.words), 'words in', file_name)
        output_dir = os.path.join(output_directory, file_name)
        if not os.path.exists(output_dir):
            os.mkdir(output_dir)
        pattern_file = os.path.join(output_dir, file_name + '.pls')
        miner = CompositeSearchClassifier(max_pattern_size, min_samples, min_confidence, 1.0, max_feature_size, 0)
        miner.mine(data_frame, pattern_file)
        print('Mining', len(data_frame.program.mutant_space.mutants), 'mutants in', file_name)
    return


class DecisionTreePathMiner:
    """
    Using decision tree to mine the pattern based on decision path
    """

    def __init__(self, min_samples: int, min_confidence: float, target_label: int):
        self.classifier = None
        self.solutions = dict()
        self.min_samples = min_samples
        self.min_confidence = min_confidence
        self.target_label = target_label
        return

    def __extract_data_lines__(self, data_frame: encode.MutantDataFrame):
        """
        extract list of mutation identifiers, feature vectors (in 0-1 form) and labels
        :param data_frame:
        :return: identifiers, feature_vectors, labels
        """
        self.classifier = tree.DecisionTreeClassifier(criterion='entropy')
        self.data_frame = data_frame
        self.identifiers = data_frame.get_mutants()
        self.feature_vectors = data_frame.get_features()
        self.labels = data_frame.get_labels()
        return

    def __mine__(self):
        self.classifier: tree.DecisionTreeClassifier
        threshold = self.classifier.tree_.threshold
        tree_features = self.classifier.tree_.feature
        features = self.feature_vectors
        node_indicator = self.classifier.decision_path(features)
        leave_id = self.classifier.apply(features)
        self.solutions.clear()
        for index in range(0, features.shape[0]):
            if True:
                feature_vector = (features[[index], :].todense())[0]
                p_label = self.classifier.predict(feature_vector)[0]
                if p_label == self.target_label:
                    node_index = node_indicator.indices[node_indicator.indptr[index]: node_indicator.indptr[index + 1]]
                    decision_path = list()
                    feature_vector = (features[[index], :].toarray())[0]
                    for tree_node_id in node_index:
                        if leave_id[index] == tree_node_id:  # when root is reached
                            continue
                        elif feature_vector[tree_features[tree_node_id]] >= threshold[tree_node_id]:
                            # when feature holds
                            decision_path.append(tree_features[tree_node_id])
                    while len(decision_path) > 0:
                        sub_decision_path = list()
                        for feature in decision_path:
                            sub_decision_path.append(feature)
                        decision_path = decision_path[0: len(decision_path) - 1]
                        sub_decision_path.sort()
                        key = str(sub_decision_path)
                        if key not in self.solutions:
                            self.solutions[key] = encode.MutantCluster(
                                sub_decision_path, self.data_frame.program.mutant_space.get_mutants())
        return

    def __filter__(self):
        remove_set = set()
        for key, pattern in self.solutions.items():
            pattern: encode.MutantCluster
            if len(pattern.samples) < self.min_samples:
                remove_set.add(key)
            elif pattern.get_confidence(self.target_label) < self.min_confidence:
                remove_set.add(key)
        for key in remove_set:
            self.solutions.pop(key)
        return

    def __remove_redundant__(self, is_better):
        """
        remove the redundant patterns based on parent-child elimination algorithm
        :return:
        """
        # remove children parent
        removed_set = set()
        visited_set = set()
        while True:
            # 1. select one not-visited pattern
            parent_pattern = None
            for key, pattern in self.solutions.items():
                if key not in visited_set:
                    pattern: encode.MutantCluster
                    visited_set.add(key)
                    parent_pattern = pattern
                    break

            # 2. when the redundant ones are removed
            if parent_pattern is None:
                break

            # 3. find all the redundant patterns to remove
            removed_set.clear()
            for key, pattern in self.solutions.items():
                if is_better(parent_pattern, pattern):
                    removed_set.add(key)

            # 4. remove all the redundant patterns within
            for key in removed_set:
                self.solutions.pop(key)
            self.solutions[str(parent_pattern)] = parent_pattern
        return

    def __output__(self, file_path: str, data_frame: encode.MutantDataFrame):
        with open(file_path, 'w') as writer:
            writer.write('length\tfeature_words\ttotal_size\t'
                         'equivalent\ttrivial\tconfidence\tmutants\n')
            equivalence = 0
            for mutant in data_frame.program.mutant_space.get_mutants():
                if mutant.labels.label == self.target_label:
                    equivalence += 1
            mutants_set = set()
            predict_set = set()
            for key, pattern in self.solutions.items():
                if pattern is not None:
                    pattern: encode.MutantCluster
                    feature_words = list()
                    for code in pattern.pattern_vector:
                        word = data_frame.words[code]
                        feature_words.append(word)
                    writer.write(str(len(pattern)) + '\t')
                    writer.write(str(feature_words) + '\t')
                    writer.write(str(len(pattern.samples)) + '\t')
                    writer.write(str(pattern.distribution[0]) + '\t')
                    writer.write(str(pattern.distribution[1]) + '\t')
                    writer.write(str(pattern.get_confidence(self.target_label)) + '\t')
                    mutant_list = list()
                    for mutant in pattern.samples:
                        if mutant.labels.label == 0:
                            mutant_list.append(mutant.id)
                            if mutant.id not in mutants_set:
                                mutants_set.add(mutant.id)
                        predict_set.add(mutant.id)
                    writer.write(str(mutant_list) + '\t')
                    writer.write('\n')
            if equivalence > 0:
                writer.write('Equivalence\t' + str(equivalence) + '\tPatterns\t' +
                             str(len(self.solutions)) + '\tCovering\t' + str(len(mutants_set))
                             + '\tPrecision\t' + str(int((100 * len(mutants_set)) / len(predict_set))) + '%'
                             + '\tRecall\t' + str(int((100 * len(mutants_set)) / equivalence)) + '%')
            return mutants_set, predict_set

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
        p_labels = self.classifier.predict(self.feature_vectors)
        print(metrics.classification_report(self.labels, p_labels))
        return

    def write_decision_tree(self, tree_file: str):
        dot_data = StringIO()
        self.classifier: tree.DecisionTreeClassifier
        tree.export_graphviz(self.classifier, feature_names=DecisionTreePathMiner.__normalize_graphviz_words__(
            self.data_frame.words), class_names=['Equiv', 'Non-Equiv'], filled=True, out_file=dot_data)
        graph = pydotplus.graph_from_dot_data(dot_data.getvalue())
        graph.write_pdf(tree_file)
        return

    def mine(self, data_frame: encode.MutantDataFrame, pattern_file: str):
        """
        mine the pattern of decision tree path
        :param pattern_file:
        :param data_frame:
        :return:
        """
        self.__extract_data_lines__(data_frame)
        self.classifier.fit(self.feature_vectors, self.labels)
        self.solutions.clear()
        self.__mine__()
        self.__filter__()
        self.__remove_redundant__(CompositeSearchClassifier.is_subsume_on)
        self.__output__(pattern_file, data_frame)
        self.solutions.clear()
        return


def decision_tree_path_mine(get_assertions, assertion_encode, prob_threshold, min_samples, min_confidence):
    data_directory = 'C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data'
    output_directory = 'C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output\\decision_tree'
    if not os.path.exists(output_directory):
        os.mkdir(output_directory)
    for file_name in os.listdir(data_directory):
        program_directory = os.path.join(data_directory, file_name)
        encoder = encode.MutantWordEncode(get_assertions, assertion_encode, prob_threshold)
        data_frame = encode.MutantDataFrame(program_directory, encoder)
        print('Load', len(data_frame.program.mutant_space.mutants), 'mutants with',
              len(data_frame.words), 'words in', file_name)
        output_dir = os.path.join(output_directory, file_name)
        if not os.path.exists(output_dir):
            os.mkdir(output_dir)
        pattern_file = os.path.join(output_dir, file_name + '.pat')
        decision_tree_file = os.path.join(output_dir, file_name + '.pdf')
        miner = DecisionTreePathMiner(min_samples=min_samples, min_confidence=min_confidence, target_label=0)
        miner.mine(data_frame, pattern_file)
        miner.write_decision_tree(decision_tree_file)
        miner.evaluate()
    return


if __name__ == '__main__':
    print('Testing start here.')
    # evaluate_classifier(encode.SemanticAssertionEncode.get_infection_assertions,
    #                    encode.SemanticAssertionEncode.get_assertion_source_code, 0.002)
    # composition_search_mining(encode.SemanticAssertionEncode.get_infection_assertions,
    #                          encode.SemanticAssertionEncode.get_assertion_source_code, 0.004,
    #                          max_pattern_size=4, min_samples=1, min_confidence=0.7, max_feature_size=12)
    decision_tree_path_mine(encode.SemanticAssertionEncode.get_infection_assertions,
                            encode.SemanticAssertionEncode.get_assertion_source_code, 0.002,
                            min_samples=1, min_confidence=0.60)
    print('Testing end for all.')

