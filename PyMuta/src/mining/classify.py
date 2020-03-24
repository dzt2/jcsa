import os
import math
import pydotplus
from six import StringIO
import src.cmodel.ccode as ccode
import src.cmodel.cflow as cflow
import src.cmodel.mutant as cmutant
import src.mining.encode as encode
import sklearn.metrics as metrics
import seaborn as sns
import matplotlib.pyplot as plt
import sklearn.tree as tree


class MutantClassification:
    """
    Provide methods for determining the label of a set of mutants.
    """

    @staticmethod
    def get_label_distribution(mutants):
        """
        :param mutants:
        :return: {[0]: number; [1]: number}
        """
        distribution = dict()
        distribution[0] = 0
        distribution[1] = 0
        for mutant in mutants:
            label = mutant.labels.label
            distribution[label] += 1
        return distribution

    @staticmethod
    def get_main_label(mutants):
        distribution = MutantClassification.get_label_distribution(mutants)
        max_label, max_number = None, 0
        for label, number in distribution.items():
            if number > max_number:
                max_label = label
                max_number = number
        return max_label

    @staticmethod
    def get_main_support(mutants):
        distribution = MutantClassification.get_label_distribution(mutants)
        max_label, max_number = None, 0
        for label, number in distribution.items():
            if number > max_number:
                max_label = label
                max_number = number
        if max_label is None:
            return 0
        else:
            return distribution[max_label]

    @staticmethod
    def get_support(mutants, target_label: int):
        distribution = MutantClassification.get_label_distribution(mutants)
        if target_label in distribution:
            return distribution[target_label]
        else:
            return 0

    @staticmethod
    def get_main_confidence(mutants):
        distribution = MutantClassification.get_label_distribution(mutants)
        max_label, max_number = None, 0
        for label, number in distribution.items():
            if number > max_number:
                max_label = label
                max_number = number
        if max_label is None or len(mutants) == 0:
            return 0.0
        else:
            return max_number / (len(mutants) + 0.0)

    @staticmethod
    def get_confidence(mutants, target_label: int):
        distribution = MutantClassification.get_label_distribution(mutants)
        if target_label in distribution and len(mutants) > 0:
            return distribution[target_label] / (len(mutants) + 0.0)
        else:
            return 0.0

    @staticmethod
    def get_entropy(mutants):
        probability = MutantClassification.get_main_confidence(mutants)
        if len(mutants) == 0 or probability == 1:
            return 0.0
        else:
            return -(probability * math.log(probability + 1e-10, 2) + (1 - probability) *
                     math.log(1 - probability + 1e-10, 2))


class FeatureClassifierEvaluate:
    """
    Used to evaluate the performance of feature model for classifying mutant as killed or non-killed
    """

    def __init__(self):
        self.mapping = dict()
        return

    @staticmethod
    def __key__(mutant: cmutant.Mutant):
        feature_vector = list()
        for feature in mutant.feature_vector:
            feature_vector.append(feature)
        feature_vector.sort()
        return mutant.space.program.name + "#" + str(feature_vector)

    def fit(self, data_frame: encode.MutantDataFrame):
        for mutant in data_frame.program.mutant_space.get_mutants():
            mutant: cmutant.Mutant
            key = FeatureClassifierEvaluate.__key__(mutant)
            if key not in self.mapping:
                self.mapping[key] = list()
            self.mapping[key].append(mutant)
        return

    def __evaluate_feature__(self, mutant: cmutant.Mutant):
        """
        :param mutant:
        :return: feature_string, feature_size, predict_label, main_support, main_confidence, feature_entropy
        """
        feature_string = FeatureClassifierEvaluate.__key__(mutant)
        mutants = self.mapping[feature_string]
        feature_size = len(mutant.feature_vector)
        predict_label = MutantClassification.get_main_label(mutants)
        main_support = MutantClassification.get_main_support(mutants)
        main_confidence = MutantClassification.get_main_confidence(mutants)
        feature_entropy = MutantClassification.get_entropy(mutants)
        return [feature_string, feature_size, predict_label, main_support, main_confidence, feature_entropy]

    @staticmethod
    def __generate_line_of_mutant__(mutant: cmutant.Mutant, feature_line):
        """
        program, id, class, operator, line, code_segment, parameter?,
        label, p_label, result, feature, feature_size, main_support, main_confidence, feature_entropy
        :param feature_line:
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
        result = (label == feature_line[2])
        return [program, identifier, m_class, m_operator, line, code_segment, parameter,
                label, feature_line[2], result, mutant.feature_words, len(mutant.feature_vector),
                feature_line[3], feature_line[4], feature_line[5]]

    def evaluate(self, file_path: str):
        entropy_list, labels, p_labels = list(), list(), list()
        error_map = dict()
        error_size = 0
        with open(file_path, 'w') as writer:
            writer.write('program\tid\tclass\toperator\tline\tlocation\tparameter'
                         'label\tp_label\tresult\tfeature\tfeature_size\tsupport\tconfidence\tentropy\n')
            for key, mutants in self.mapping.items():
                feature_line = self.__evaluate_feature__(mutants[0])
                entropy_list.append(feature_line[5])
                for mutant in mutants:
                    mutant: cmutant.Mutant
                    labels.append(mutant.labels.label)
                    p_labels.append(feature_line[2])
                    str_line = FeatureClassifierEvaluate.__generate_line_of_mutant__(mutant, feature_line)
                    for argument in str_line:
                        writer.write(str(argument))
                        writer.write('\t')
                    writer.write('\n')
                    if mutant.labels.label != feature_line[2]:
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
    evaluator = FeatureClassifierEvaluate()
    for file_name in os.listdir(data_directory):
        program_directory = os.path.join(data_directory, file_name)
        encoder = encode.MutantFeatureEncoder(get_assertions, assertion_encode, prob_threshold)
        data_frame = encode.MutantDataFrame(program_directory, encoder)
        print('Load', len(data_frame.program.mutant_space.mutants), 'mutants from', file_name)
        evaluator.fit(data_frame)
    print()
    evaluator.evaluate(os.path.join(output_directory, 'feature_evaluate.txt'))
    return


class MutantPattern:
    """
    The pattern describes a set of samples with corresponding to the pattern feature.
    [pattern_vector, mutants]
    """

    def __match__(self, feature_vector):
        """
        the feature vector matches with the pattern when all the features in pattern are in the feature vector
        :param feature_vector:
        :return:
        """
        for feature in self.pattern_vector:
            if feature not in feature_vector:
                return False
        return True

    def __init__(self, pattern_vector, mutants):
        self.pattern_vector = list()
        for feature in pattern_vector:
            self.pattern_vector.append(feature)
        self.pattern_vector.sort()
        self.mutants = list()
        for mutant in mutants:
            mutant: cmutant.Mutant
            if self.__match__(mutant.feature_vector):
                self.mutants.append(mutant)
        return

    def get_pattern_vector(self):
        return self.pattern_vector

    def get_mutants(self):
        return self.mutants

    def __str__(self):
        return str(self.pattern_vector)

    def __len__(self):
        return len(self.pattern_vector)

    def get_child(self, feature: int):
        """
        create a child pattern by adding a new feature
        :param feature:
        :return:
        """
        if feature in self.pattern_vector:
            return self
        else:
            pattern_set = set()
            pattern_set.add(feature)
            for old_feature in self.pattern_vector:
                pattern_set.add(old_feature)
            return MutantPattern(pattern_set, self.mutants)


class MutantPatterns:
    def __init__(self):
        self.patterns = dict()
        return

    @staticmethod
    def __key__(pattern_vector):
        pattern_vector_list = list()
        for feature in pattern_vector:
            pattern_vector_list.append(feature)
        pattern_vector_list.sort()
        return str(pattern_vector_list)

    def has(self, pattern_vector):
        key = MutantPatterns.__key__(pattern_vector)
        return key in self.patterns

    def get(self, pattern_vector):
        key = MutantPatterns.__key__(pattern_vector)
        return self.patterns[key]

    def set(self, pattern_vector, mutants):
        key = MutantPatterns.__key__(pattern_vector)
        self.patterns[key] = MutantPattern(pattern_vector, mutants)
        return

    @staticmethod
    def is_parent_of(pattern1: MutantPattern, pattern2: MutantPattern):
        for code in pattern1.pattern_vector:
            if code not in pattern2.pattern_vector:
                return False
        return True

    @staticmethod
    def is_subsume_on(pattern1: MutantPattern, pattern2: MutantPattern):
        if len(pattern1.pattern_vector) > len(pattern2.pattern_vector):
            return False
        else:
            mutants = set()
            for sample in pattern1.mutants:
                mutants.add(sample.id)
            for sample in pattern2.mutants:
                if sample.id not in mutants:
                    return False
            return True

    def filter(self, target_label: int, min_samples: int, min_confidence: float):
        """
        remove the patterns that cannot reach the arguments
        :param target_label: target label to match with the pattern
        :param min_samples: the minimal number of mutants in pattern
        :param min_confidence: the minimal confidence of target label in the pattern
        :return:
        """
        remove_set = set()
        for key, pattern in self.patterns.items():
            pattern: MutantPattern
            if len(pattern.mutants) < min_samples:
                remove_set.add(key)
            elif MutantClassification.get_main_label(pattern.mutants) != target_label:
                remove_set.add(key)
            elif MutantClassification.get_confidence(pattern.mutants, target_label) < min_confidence:
                remove_set.add(key)
        for key in remove_set:
            self.patterns.pop(key)
        return

    def optimize(self, is_better):
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
            for key, pattern in self.patterns.items():
                if key not in visited_set:
                    pattern: MutantPattern
                    visited_set.add(key)
                    parent_pattern = pattern
                    break

            # 2. when the redundant ones are removed
            if parent_pattern is None:
                break

            # 3. find all the redundant patterns to remove
            removed_set.clear()
            for key, pattern in self.patterns.items():
                if is_better(parent_pattern, pattern):
                    removed_set.add(key)

            # 4. remove all the redundant patterns within
            for key in removed_set:
                self.patterns.pop(key)
            self.patterns[str(parent_pattern)] = parent_pattern
        return

    def output(self, file_path: str, data_frame: encode.MutantDataFrame, target_label: int):
        with open(file_path, 'w') as writer:
            writer.write('length\tfeature_words\ttotal_size\t'
                         'equivalent\ttrivial\tconfidence\tmutants\n')
            equivalence = 0
            for mutant in data_frame.program.mutant_space.get_mutants():
                if mutant.labels.label == target_label:
                    equivalence += 1
            mutants_set = set()
            predict_set = set()
            for key, pattern in self.patterns.items():
                if pattern is not None:
                    pattern: MutantPattern
                    feature_words = list()
                    for code in pattern.pattern_vector:
                        word = data_frame.words[code]
                        feature_words.append(word)
                    writer.write(str(len(pattern)) + '\t')
                    writer.write(str(feature_words) + '\t')
                    writer.write(str(len(pattern.mutants)) + '\t')
                    distribution = MutantClassification.get_label_distribution(pattern.mutants)
                    writer.write(str(distribution[0]) + '\t')
                    writer.write(str(distribution[1]) + '\t')
                    writer.write(str(MutantClassification.get_confidence(pattern.mutants, target_label)) + '\t')
                    mutant_list = list()
                    for mutant in pattern.mutants:
                        if mutant.labels.label == target_label:
                            mutant_list.append(mutant.id)
                            if mutant.id not in mutants_set:
                                mutants_set.add(mutant.id)
                        predict_set.add(mutant.id)
                    writer.write(str(mutant_list) + '\t')
                    writer.write('\n')
            if equivalence > 0:
                writer.write('Equivalence\t' + str(equivalence) + '\tPatterns\t' +
                             str(len(self.patterns)) + '\tCovering\t' + str(len(mutants_set))
                             + '\tPrecision\t' + str(int((100 * len(mutants_set)) / len(predict_set))) + '%'
                             + '\tRecall\t' + str(int((100 * len(mutants_set)) / equivalence)) + '%')
        return

    def copy(self):
        new_patterns = MutantPatterns()
        for key, pattern in self.patterns.items():
            new_patterns.patterns[key] = pattern
        return new_patterns


class MutantPatternSlicer:
    """
    Used to get the code slice of the semantic assertion(s)
    """

    @staticmethod
    def __get_statement__(location: ccode.CirNode):
        while location is not None:
            cir_type = location.get_cir_type()
            if cir_type.endswith("Statement"):
                return location
            else:
                location = location.get_parent()
        return location

    @staticmethod
    def __deep_slice__(location: ccode.CirNode, statements: set, direction: bool, max_layer: int, visit_set: set):
        if location not in visit_set and max_layer >= 0:
            visit_set.add(location)
            statement = MutantPatternSlicer.__get_statement__(location)
            if statement is not None:
                statements.add(statement)
            influence_graph = location.tree.program.influence_graph
            influence_graph: cflow.CInfluenceGraph
            children = set()
            for node in influence_graph.get_nodes_of(location):
                node: cflow.CInfluenceNode
                if direction:
                    for edge in node.get_ou_edges():
                        target = edge.target
                        target: cflow.CInfluenceNode
                        if target.get_cir_source() is not None:
                            children.add(target.get_cir_source())
                else:
                    for edge in node.get_in_edges():
                        source = edge.source
                        source: cflow.CInfluenceNode
                        if source.get_cir_source() is not None:
                            children.add(source.get_cir_source())
                for child in children:
                    MutantPatternSlicer.__deep_slice__(child, statements, direction, max_layer - 1, visit_set)
        return

    @staticmethod
    def __size_slice__(location: ccode.CirNode, statements: set, direction: bool, max_size: int, visit_set: set):
        if location not in visit_set and len(statements) < max_size:
            visit_set.add(location)
            statement = MutantPatternSlicer.__get_statement__(location)
            if statement is not None:
                statements.add(statement)
            influence_graph = location.tree.program.influence_graph
            influence_graph: cflow.CInfluenceGraph
            children = set()
            for node in influence_graph.get_nodes_of(location):
                node: cflow.CInfluenceNode
                if direction:
                    for edge in node.get_ou_edges():
                        target = edge.target
                        target: cflow.CInfluenceNode
                        if target.get_cir_source() is not None:
                            children.add(target.get_cir_source())
                else:
                    for edge in node.get_in_edges():
                        source = edge.source
                        source: cflow.CInfluenceNode
                        if source.get_cir_source() is not None:
                            children.add(source.get_cir_source())
                for child in children:
                    MutantPatternSlicer.__size_slice__(child, statements, direction, max_size, visit_set)
        return

    @staticmethod
    def deep_slice(assertion: cmutant.SemanticAssertion, max_layer: int):
        locations = set()
        for operand in assertion.get_operands():
            if isinstance(operand, ccode.CirNode):
                statements = set()
                MutantPatternSlicer.__deep_slice__(operand, statements, assertion.is_state_error(), max_layer, set())
                for statement in statements:
                    locations.add(statement)
        return locations

    @staticmethod
    def size_slice(assertion: cmutant.SemanticAssertion, max_size: int):
        locations = set()
        for operand in assertion.get_operands():
            if isinstance(operand, ccode.CirNode):
                statements = set()
                MutantPatternSlicer.__size_slice__(operand, statements, assertion.is_state_error(), max_size, set())
                for statement in statements:
                    locations.add(statement)
        return locations

    @staticmethod
    def output_deep(patterns: MutantPatterns, file_path: str, data_frame: encode.MutantDataFrame,
                    target_label: int, max_layer: int):
        with open(file_path, 'w') as writer:
            for key, pattern in patterns.patterns.items():
                pattern: MutantPattern
                feature_words = list()
                for code in pattern.pattern_vector:
                    word = data_frame.words[code]
                    feature_words.append(word)
                writer.write("#PATTERN\t" + str(key) + "\t")
                distribution = MutantClassification.get_label_distribution(pattern.mutants)
                writer.write(str(distribution[0]) + '\t')
                writer.write(str(distribution[1]) + '\t')
                writer.write(str(MutantClassification.get_confidence(pattern.mutants, target_label)) + '\n')
                alive_mutants, killed_mutants = set(), set()
                for mutant in pattern.get_mutants():
                    if mutant.labels.label == 0:
                        alive_mutants.add(mutant.id)
                    else:
                        killed_mutants.add(mutant.id)
                if len(alive_mutants) > 0:
                    writer.write("\tALIVE\t" + str(alive_mutants) + "\n")
                if len(killed_mutants) > 0:
                    writer.write("\tKILLS\t" + str(killed_mutants) + "\n")
                writer.write("\t=======================================================================\n")
                for assertion in feature_words:
                    if isinstance(assertion, cmutant.SemanticAssertion):
                        statements = MutantPatternSlicer.deep_slice(assertion, max_layer)
                        writer.write("\tAssertion: " + encode.SemanticAssertionEncodeFunctions.
                                     get_assertion_source_code(assertion) + "\n")
                        for statement in statements:
                            statement: ccode.CirNode
                            writer.write("\t\t" + statement.get_code_string() + "\n")
                writer.write("\t=======================================================================\n")
                writer.write("\n")
        return

    @staticmethod
    def output_size(patterns: MutantPatterns, file_path: str, data_frame: encode.MutantDataFrame,
                    target_label: int, max_size: int):
        with open(file_path, 'w') as writer:
            for key, pattern in patterns.patterns.items():
                pattern: MutantPattern
                feature_words = list()
                for code in pattern.pattern_vector:
                    word = data_frame.words[code]
                    feature_words.append(word)
                writer.write("#PATTERN\t" + str(key) + "\t")
                distribution = MutantClassification.get_label_distribution(pattern.mutants)
                writer.write(str(distribution[0]) + '\t')
                writer.write(str(distribution[1]) + '\t')
                writer.write(str(MutantClassification.get_confidence(pattern.mutants, target_label)) + '\n')
                alive_mutants, killed_mutants = set(), set()
                for mutant in pattern.get_mutants():
                    if mutant.labels.label == 0:
                        alive_mutants.add(mutant.id)
                    else:
                        killed_mutants.add(mutant.id)
                if len(alive_mutants) > 0:
                    writer.write("\tALIVE\t" + str(alive_mutants) + "\n")
                if len(killed_mutants) > 0:
                    writer.write("\tKILLS\t" + str(killed_mutants) + "\n")
                writer.write("\t=======================================================================\n")
                for assertion in feature_words:
                    if isinstance(assertion, cmutant.SemanticAssertion):
                        statements = MutantPatternSlicer.size_slice(assertion, max_size)
                        writer.write("\tAssertion: " + encode.SemanticAssertionEncodeFunctions.
                                     get_assertion_source_code(assertion) + "\n")
                        for statement in statements:
                            statement: ccode.CirNode
                            writer.write("\t\t" + statement.get_code_string() + "\n")
                writer.write("\t=======================================================================\n")
                writer.write("\n")
        return


class CompositeSearchMiner:
    """
    Use composite search algorithm to find the frequent patterns.
    """

    def __init__(self, max_pattern_size: int, min_samples: int, min_confidence: float,
                 max_feature_size: int, target_label: int):
        """
        :param max_pattern_size: maximal size of pattern vector
        :param min_samples:      minimal number of mutants in pattern
        :param min_confidence:   minimal confidence of target mutants
        :param target_label:     the target label to be mined
        """
        self.patterns = MutantPatterns()
        self.max_pattern_size = max_pattern_size
        self.min_samples = min_samples
        self.min_confidence = min_confidence
        self.target_label = target_label
        self.max_feature_size = max_feature_size
        return

    def __features__(self, mutant: cmutant.Mutant):
        """
        get the features being used to generate pattern
        :param mutant:
        :return:
        """
        if len(mutant.feature_vector) <= self.max_feature_size:
            return mutant.feature_vector
        else:
            return mutant.feature_vector[0: self.max_feature_size]

    def __mine__(self, features, mutants, pattern_vector):
        if not self.patterns.has(pattern_vector):
            self.patterns.set(pattern_vector, mutants)
            pattern = self.patterns.get(pattern_vector)
            pattern: MutantPattern
            if len(pattern) >= self.max_pattern_size:
                return
            elif len(pattern.mutants) < self.min_samples:
                return
            else:
                for feature in features:
                    if feature not in pattern.pattern_vector:
                        child_vector = list()
                        child_vector.append(feature)
                        for old_feature in pattern.pattern_vector:
                            child_vector.append(old_feature)
                        child_vector.sort()
                        self.__mine__(features, pattern.mutants, child_vector)
        return

    def mine(self, data_frame: encode.MutantDataFrame, file_path: str):
        # 1. mining all the possible valid pattern
        self.patterns.patterns.clear()
        for mutant in data_frame.program.mutant_space.get_mutants():
            mutant: cmutant.Mutant
            if mutant.labels.label == self.target_label:
                # print('\t\tFetch features from', len(mutant.feature_vector))
                features = self.__features__(mutant)
                print('\t\tStart mine mutant', mutant.id, 'with', len(features),
                      'features under', len(self.patterns.patterns), 'patterns.')
                for feature in features:
                    init_pattern_vector = [feature]
                    self.__mine__(features, mutant.space.get_mutants(), init_pattern_vector)

        # 2. get the optimal pattern set
        self.patterns.filter(self.target_label, self.min_samples, self.min_confidence)
        self.patterns.optimize(MutantPatterns.is_subsume_on)

        # 3. output the solutions to the file
        self.patterns.output(file_path, data_frame, target_label=self.target_label)
        new_patterns = self.patterns.copy()
        self.patterns.patterns.clear()
        return new_patterns


def composition_search_mining(get_assertions, assertion_encode, prob_threshold,
                              max_pattern_size, min_samples, min_confidence, max_feature_size):
    data_directory = 'C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data'
    output_directory = 'C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output\\freq_pattern'
    if not os.path.exists(output_directory):
        os.mkdir(output_directory)
    for file_name in os.listdir(data_directory):
        program_directory = os.path.join(data_directory, file_name)
        encoder = encode.MutantFeatureEncoder(get_assertions, assertion_encode, prob_threshold)
        data_frame = encode.MutantDataFrame(program_directory, encoder)
        print('Load', len(data_frame.program.mutant_space.mutants), 'mutants with',
              len(data_frame.words), 'words in', file_name)
        output_dir = os.path.join(output_directory, file_name)
        if not os.path.exists(output_dir):
            os.mkdir(output_dir)
        pattern_file = os.path.join(output_dir, file_name + '.pls')
        miner = CompositeSearchMiner(max_pattern_size=max_pattern_size, min_samples=min_samples,
                                     min_confidence=min_confidence, max_feature_size=max_feature_size, target_label=0)
        miner.mine(data_frame, pattern_file)
        print('Mining', len(data_frame.program.mutant_space.mutants), 'mutants in', file_name)
    return


def composition_search_deep_context_mining(get_assertions, prob_threshold, max_pattern_size,
                                           min_samples, min_confidence, max_feature_size, max_layer):
    data_directory = 'C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data'
    output_directory = 'C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output\\freq_pattern'
    if not os.path.exists(output_directory):
        os.mkdir(output_directory)
    for file_name in os.listdir(data_directory):
        program_directory = os.path.join(data_directory, file_name)
        encoder = encode.MutantFeatureEncoder(get_assertions,
                                              encode.SemanticAssertionEncodeFunctions.get_assertion_instance,
                                              prob_threshold)
        data_frame = encode.MutantDataFrame(program_directory, encoder)
        print('Load', len(data_frame.program.mutant_space.mutants), 'mutants with',
              len(data_frame.words), 'words in', file_name)
        output_dir = os.path.join(output_directory, file_name)
        if not os.path.exists(output_dir):
            os.mkdir(output_dir)
        pattern_file = os.path.join(output_dir, file_name + '.pls')
        miner = CompositeSearchMiner(max_pattern_size=max_pattern_size, min_samples=min_samples,
                                     min_confidence=min_confidence, max_feature_size=max_feature_size, target_label=0)
        patterns = miner.mine(data_frame, pattern_file)
        MutantPatternSlicer.output_deep(patterns, os.path.join(output_dir, file_name + ".dep.ctx"), data_frame, 0,
                                        max_layer)
        print('Mining', len(data_frame.program.mutant_space.mutants), 'mutants in', file_name)
    return


def composition_search_size_context_mining(get_assertions, prob_threshold, max_pattern_size,
                                           min_samples, min_confidence, max_feature_size, max_size):
    data_directory = 'C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data'
    output_directory = 'C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output\\freq_pattern'
    if not os.path.exists(output_directory):
        os.mkdir(output_directory)
    for file_name in os.listdir(data_directory):
        program_directory = os.path.join(data_directory, file_name)
        encoder = encode.MutantFeatureEncoder(get_assertions,
                                              encode.SemanticAssertionEncodeFunctions.get_assertion_instance,
                                              prob_threshold)
        data_frame = encode.MutantDataFrame(program_directory, encoder)
        print('Load', len(data_frame.program.mutant_space.mutants), 'mutants with',
              len(data_frame.words), 'words in', file_name)
        output_dir = os.path.join(output_directory, file_name)
        if not os.path.exists(output_dir):
            os.mkdir(output_dir)
        pattern_file = os.path.join(output_dir, file_name + '.pls')
        miner = CompositeSearchMiner(max_pattern_size=max_pattern_size, min_samples=min_samples,
                                     min_confidence=min_confidence, max_feature_size=max_feature_size, target_label=0)
        patterns = miner.mine(data_frame, pattern_file)
        MutantPatternSlicer.output_size(patterns, os.path.join(output_dir, file_name + ".siz.ctx"), data_frame, 0,
                                        max_size)
        print('Mining', len(data_frame.program.mutant_space.mutants), 'mutants in', file_name)
    return


class DecisionTreePathMiner:
    """
    Use path on decision tree to mine the pattern
    """
    def __init__(self, target_label: int, min_samples: int, min_confidence: float):
        self.patterns = MutantPatterns()
        self.target_label = target_label
        self.min_samples = min_samples
        self.min_confidence = min_confidence
        return

    def __fit__(self, data_frame: encode.MutantDataFrame):
        self.patterns.patterns.clear()
        self.classifier = tree.DecisionTreeClassifier()
        self.features = data_frame.get_features()
        self.labels = data_frame.get_labels()
        self.classifier.fit(self.features, self.labels)
        self.p_labels = self.classifier.predict(self.features)
        print(metrics.classification_report(self.labels, self.p_labels))
        return

    @staticmethod
    def __normalize_graphviz_words__(words):
        new_words = list()
        for word in words:
            word = str(word)
            new_word = ''
            for index in range(0, len(word)):
                char = word[index]
                if char == '\"':
                    char = '``'
                new_word += char
            new_words.append(new_word)
        return new_words

    def __write_decision_tree__(self, data_frame: encode.MutantDataFrame, tree_file: str):
        dot_data = StringIO()
        self.classifier: tree.DecisionTreeClassifier
        tree.export_graphviz(self.classifier, feature_names=DecisionTreePathMiner.__normalize_graphviz_words__(
            data_frame.words), class_names=['Equiv', 'Non-Equiv'], filled=True, out_file=dot_data)
        graph = pydotplus.graph_from_dot_data(dot_data.getvalue())
        graph.write_pdf(tree_file)
        return

    def __mine__(self, data_frame: encode.MutantDataFrame):
        self.classifier: tree.DecisionTreeClassifier
        threshold = self.classifier.tree_.threshold
        tree_features = self.classifier.tree_.feature
        features = self.features
        node_indicator = self.classifier.decision_path(features)
        leave_id = self.classifier.apply(features)
        self.patterns.patterns.clear()
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
                        if key not in self.patterns.patterns:
                            self.patterns.set(sub_decision_path, data_frame.program.mutant_space.get_mutants())
        return

    def mine(self, data_frame: encode.MutantDataFrame, pattern_file: str, tree_file: str):
        """
        mine the pattern of decision tree path
        :param tree_file:
        :param pattern_file:
        :param data_frame:
        :return:
        """
        self.__fit__(data_frame)
        self.__mine__(data_frame)
        self.patterns.filter(self.target_label, self.min_samples, self.min_confidence)
        self.patterns.optimize(MutantPatterns.is_subsume_on)
        self.patterns.output(pattern_file, data_frame, self.target_label)
        new_patterns = self.patterns.copy()
        self.__write_decision_tree__(data_frame, tree_file)
        return new_patterns


def decision_tree_mine(get_assertions, assertion_encode, prob_threshold, min_samples, min_confidence):
    data_directory = 'C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data'
    output_directory = 'C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output\\decision_tree'
    if not os.path.exists(output_directory):
        os.mkdir(output_directory)
    for file_name in os.listdir(data_directory):
        program_directory = os.path.join(data_directory, file_name)
        encoder = encode.MutantFeatureEncoder(get_assertions, assertion_encode, prob_threshold)
        data_frame = encode.MutantDataFrame(program_directory, encoder)
        print('Load', len(data_frame.program.mutant_space.mutants), 'mutants with',
              len(data_frame.words), 'words in', file_name)
        output_dir = os.path.join(output_directory, file_name)
        if not os.path.exists(output_dir):
            os.mkdir(output_dir)
        pattern_file = os.path.join(output_dir, file_name + '.pat')
        decision_tree_file = os.path.join(output_dir, file_name + '.pdf')
        miner = DecisionTreePathMiner(target_label=0, min_samples=min_samples, min_confidence=min_confidence)
        miner.mine(data_frame, pattern_file, decision_tree_file)
    return


def decision_tree_deep_context_mining(get_assertions, prob_threshold, min_samples, min_confidence, max_layer):
    data_directory = 'C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data'
    output_directory = 'C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output\\decision_tree'
    if not os.path.exists(output_directory):
        os.mkdir(output_directory)
    for file_name in os.listdir(data_directory):
        program_directory = os.path.join(data_directory, file_name)
        encoder = encode.MutantFeatureEncoder(get_assertions,
                                              encode.SemanticAssertionEncodeFunctions.get_assertion_instance,
                                              prob_threshold)
        data_frame = encode.MutantDataFrame(program_directory, encoder)
        print('Load', len(data_frame.program.mutant_space.mutants), 'mutants with',
              len(data_frame.words), 'words in', file_name)
        output_dir = os.path.join(output_directory, file_name)
        if not os.path.exists(output_dir):
            os.mkdir(output_dir)
        pattern_file = os.path.join(output_dir, file_name + '.pat')
        decision_tree_file = os.path.join(output_dir, file_name + '.pdf')
        miner = DecisionTreePathMiner(target_label=0, min_samples=min_samples, min_confidence=min_confidence)
        patterns = miner.mine(data_frame, pattern_file, decision_tree_file)
        MutantPatternSlicer.output_deep(patterns, os.path.join(output_dir, file_name + ".dep.ctx"), data_frame, 0,
                                        max_layer)
    return


def decision_tree_size_context_mining(get_assertions, prob_threshold, min_samples, min_confidence, max_size):
    data_directory = 'C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data'
    output_directory = 'C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output\\decision_tree'
    if not os.path.exists(output_directory):
        os.mkdir(output_directory)
    for file_name in os.listdir(data_directory):
        program_directory = os.path.join(data_directory, file_name)
        encoder = encode.MutantFeatureEncoder(get_assertions,
                                              encode.SemanticAssertionEncodeFunctions.get_assertion_instance,
                                              prob_threshold)
        data_frame = encode.MutantDataFrame(program_directory, encoder)
        print('Load', len(data_frame.program.mutant_space.mutants), 'mutants with',
              len(data_frame.words), 'words in', file_name)
        output_dir = os.path.join(output_directory, file_name)
        if not os.path.exists(output_dir):
            os.mkdir(output_dir)
        pattern_file = os.path.join(output_dir, file_name + '.pat')
        decision_tree_file = os.path.join(output_dir, file_name + '.pdf')
        miner = DecisionTreePathMiner(target_label=0, min_samples=min_samples, min_confidence=min_confidence)
        patterns = miner.mine(data_frame, pattern_file, decision_tree_file)
        MutantPatternSlicer.output_size(patterns, os.path.join(output_dir, file_name + ".siz.ctx"), data_frame, 0,
                                        max_size)
    return


if __name__ == "__main__":
    print("Testing starts.")
    # evaluate_classifier(encode.SemanticAssertionEncodeFunctions.get_all_error_assertions,
    #                    encode.SemanticAssertionEncodeFunctions.get_assertion_string, 0.002)
    # composition_search_mining(encode.SemanticAssertionEncodeFunctions.get_infection_assertions,
    #                          encode.SemanticAssertionEncodeFunctions.get_assertion_source_code, 0.002,
    #                          max_pattern_size=4, min_samples=1, min_confidence=0.70, max_feature_size=12)
    # composition_search_deep_context_mining(encode.SemanticAssertionEncodeFunctions.get_all_error_assertions, 0.002,
    #                                       max_pattern_size=4, min_samples=1, min_confidence=0.60, max_feature_size=14,
    #                                       max_layer=6)
    decision_tree_mine(encode.SemanticAssertionEncodeFunctions.get_all_error_assertions,
                       encode.SemanticAssertionEncodeFunctions.get_assertion_source_code, 0.002,
                       min_samples=1, min_confidence=0.70)
    # decision_tree_deep_context_mining(encode.SemanticAssertionEncodeFunctions.get_all_error_assertions,
    #                                  0.002, min_samples=1, min_confidence=0.60, max_layer=4)
    # decision_tree_size_context_mining(encode.SemanticAssertionEncodeFunctions.get_all_error_assertions, 0.002,
    #                                  min_samples=1, min_confidence=0.60, max_size=6)
    print("Testing end.")
