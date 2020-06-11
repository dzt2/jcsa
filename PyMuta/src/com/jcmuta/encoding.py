"""
It defines the data model and algorithms to describe and construct the features of mutant.
"""

import os
import numpy as np
from typing import TextIO
import src.com.jcparse.astree as astree
import src.com.jcparse.cirtree as cirtree
import src.com.jcparse.cirflow as cirflow
import src.com.jcparse.cirinst as cirinst
import src.com.jcparse.cprogram as cpro
import src.com.jcmuta.mutation as mut
import src.com.jcmuta.symbol as sym
from gensim.models.doc2vec import Doc2Vec, TaggedDocument


class WordsCorpus:
    """
    To build up the word to describe an instance
    """
    def __init__(self):
        self.words = list()
        self.index = dict()
        return

    def __add__(self, word):
        """
        :param word:
        :return: word, code
        """
        word: str
        if len(word) == 0:
            return word, -1
        else:
            if word not in self.index:
                self.index[word] = len(self.words)
                self.words.append(word)
            code = self.index[word]
            code: int
            return word, code

    def save(self, file: str):
        with open(file, 'w') as writer:
            for word in self.words:
                writer.write(str(word) + "\n")
        return

    def load(self, file: str):
        self.words.clear()
        self.index.clear()
        with open(file, 'r') as reader:
            for line in reader:
                line = line.strip()
                if len(line) > 0:
                    self.__add__(line)
        return

    def get_words(self):
        return self.words

    def encode(self, word: str):
        """
        :param word:
        :return: -1 if the word is not in the corpus
        """
        if word in self.index:
            code = self.index[word]
            code: int
            return code
        else:
            return -1

    def decode(self, code: int):
        """
        :param code:
        :return: "" if code is out of range of the words
        """
        if code < 0 or code >= len(self.words):
            return ""
        else:
            return self.words[code]

    def __cir_word__(self, cir_node: cirtree.CirNode):
        if cir_node.cir_type == cirtree.CirType.binary_assign_statement:
            word = "assign_statement"
        elif cir_node.cir_type == cirtree.CirType.init_assign_statement:
            word = "declare_statement"
        elif cir_node.cir_type == cirtree.CirType.incre_assign_statement:
            word = "increase_statement"
        elif cir_node.cir_type == cirtree.CirType.return_assign_statement:
            word = "return_statement"
        elif cir_node.cir_type == cirtree.CirType.save_assign_statement:
            word = "assign_statement"
        elif cir_node.cir_type == cirtree.CirType.wait_assign_statement:
            word = "wait_statement"
        elif cir_node.cir_type == cirtree.CirType.call_statement:
            word = "call_statement"
        elif cir_node.cir_type == cirtree.CirType.if_statement:
            word = "if_statement"
        elif cir_node.cir_type == cirtree.CirType.case_statement:
            word = "case_statement"
        elif cir_node.cir_type == cirtree.CirType.goto_statement or cir_node.is_tag_statement() or cir_node.cir_type == cirtree.CirType.labeled_statement:
            word = ""       # tag statement does not define meaningful semantics
        elif cir_node.is_computational_expression():
            if cir_node.get_operator() is not None:
                word = str(cir_node.get_operator())
            else:
                word = ""
        elif cir_node.is_name_expression():
            word = "identifier"
            name = cir_node.get_name()
            name: str
            if "#" in name:
                index = name.index("#")
                name = name[0: index].strip()
            word += "@" + name
        elif cir_node.cir_type == cirtree.CirType.defer_expression:
            word = "defer_expression"
        elif cir_node.cir_type == cirtree.CirType.field_expression:
            word = "field_expression"
        elif cir_node.cir_type == cirtree.CirType.field:
            word = "field"
            name = cir_node.get_name()
            name: str
            if "#" in name:
                index = name.index("#")
                name = name[0: index].strip()
            word += "@" + name
        elif cir_node.cir_type == cirtree.CirType.address_expression:
            word = "address_expression"
        elif cir_node.cir_type == cirtree.CirType.cast_expression:
            word = "cast_expression"
        elif cir_node.cir_type == cirtree.CirType.wait_expression:
            word = ""
        elif cir_node.cir_type == cirtree.CirType.literal:
            word = "literal"
        elif cir_node.cir_type == cirtree.CirType.constant:
            word = "constant#" + str(cir_node.get_constant())
        elif cir_node.cir_type == cirtree.CirType.default_value:
            word = "default_value"
        else:
            word = str(cir_node.get_cir_type())
        return self.__add__(word)

    def __ast_word__(self, ast_node: astree.AstNode):
        word = str(ast_node.get_ast_type())
        return self.__add__(word)

    def __exe_word__(self, execution: cirflow.CirExecution):
        return self.__cir_word__(execution.get_statement())

    def __flow_word__(self, flow: cirflow.CirExecutionFlow):
        word = str(flow.get_flow_type())
        return self.__add__(word)

    def __inst_word__(self, instance: cirinst.CirInstance):
        if isinstance(instance, cirinst.CirInstanceCode):
            instance: cirinst.CirInstanceCode
            return self.__cir_word__(instance.get_cir_source_node())
        elif isinstance(instance, cirinst.CirInstanceNode):
            instance: cirinst.CirInstanceNode
            return self.__exe_word__(instance.get_source_execution())
        else:
            instance: cirinst.CirInstanceEdge
            word = str(instance.get_flow_type())
            return self.__add__(word)

    def __error_word__(self, error: mut.StateError):
        word = str(error.get_error_type())
        word += "[ "
        for operand in error.get_operands():
            if isinstance(operand, cirtree.CirNode):
                word += "@CirNode"
            else:
                word += str(operand)
            word += "; "
        word += "]"
        return self.__add__(word)

    def __sym_word__(self, condition: sym.CSymbolNode):
        word = condition.generate_code(True)
        return self.__add__(word)

    def __inf_node_word__(self, node: cpro.CInformationNode):
        return self.__inst_word__(node.get_instance())

    def __inf_edge_word__(self, edge: cpro.CInformationEdge):
        word = str(edge.get_flow())
        return self.__add__(word)

    def __dep_node_word__(self, node: cpro.CDependenceNode):
        return self.__inst_word__(node.get_instance())

    def __dep_edge_word__(self, edge: cpro.CDependenceEdge):
        word = str(edge.get_flow())
        return self.__add__(word)

    def word(self, obj):
        """
        :param obj:
        :return: word, code
        """
        if isinstance(obj, astree.AstNode):
            return self.__ast_word__(obj)
        elif isinstance(obj, cirtree.CirNode):
            return self.__cir_word__(obj)
        elif isinstance(obj, cirflow.CirExecution):
            return self.__exe_word__(obj)
        elif isinstance(obj, cirflow.CirExecutionFlow):
            return self.__flow_word__(obj)
        elif isinstance(obj, cirinst.CirInstance):
            return self.__inst_word__(obj)
        elif isinstance(obj, mut.StateError):
            return self.__error_word__(obj)
        elif isinstance(obj, sym.CSymbolNode):
            return self.__sym_word__(obj)
        elif isinstance(obj, cpro.CInformationNode):
            return self.__inf_node_word__(obj)
        elif isinstance(obj, cpro.CInformationEdge):
            return self.__inf_edge_word__(obj)
        elif isinstance(obj, cpro.CDependenceNode):
            return self.__dep_node_word__(obj)
        elif isinstance(obj, cpro.CDependenceEdge):
            return self.__dep_edge_word__(obj)
        else:
            word = str(obj)
            return self.__add__(word)

    def reset(self):
        self.words.clear()
        self.index.clear()
        return

    def __len__(self):
        return len(self.words)


class ErrorInformationPath:
    """
    [source; "caused_in"; information_path]
    """
    def __init__(self, source: mut.StateError, information_path):
        self.error_path = list()
        self.error_path.append(source)
        if information_path is not None:
            information_path: cpro.CInformationPath
            self.error_path.append("caused_in")
            self.error_path.append(information_path.get_source())
            for information_edge in information_path.get_edges():
                information_edge: cpro.CInformationEdge
                self.error_path.append(information_edge)
                self.error_path.append(information_edge.get_target())
        return

    def get_error_path(self):
        """
        :return: [source; "caused_in"; information_node; (information_edge; information_node;)*]
        """
        return self.error_path

    @staticmethod
    def __information_nodes__(program: cpro.CProgram, source: mut.StateError):
        cir_location = source.get_cir_location()
        if cir_location is not None:
            instance_graph = program.get_instance_graph()
            instances = instance_graph.get_instances().get_instances_of_object(cir_location)
            information_nodes = set()
            information_graph = program.get_information_graph()
            for instance in instances:
                instance: cirinst.CirInstance
                if information_graph.has_node(instance):
                    information_node = information_graph.get_node(instance)
                    information_nodes.add(information_node)
            return information_nodes
        else:
            return set()

    @staticmethod
    def error_information_paths(program: cpro.CProgram, source: mut.StateError, distance: int):
        """
        :param program:
        :param source:
        :param distance:
        :return: dict{information_root or None: (error_information_path)+}
        """
        information_nodes = ErrorInformationPath.__information_nodes__(program, source)
        error_path_dict = dict()
        if len(information_nodes) > 0:
            for information_root in information_nodes:
                information_root: cpro.CInformationNode
                information_paths = cpro.get_information_paths(information_root, distance)
                error_paths = set()
                for information_path in information_paths:
                    error_path = ErrorInformationPath(source, information_path)
                    error_paths.add(error_path)
                error_path_dict[information_root] = error_paths
        else:
            error_paths = set()
            error_paths.add(ErrorInformationPath(source, None))
            error_path_dict[None] = error_paths
        return error_path_dict

    def get_words(self, corpus: WordsCorpus):
        """
        :param corpus:
        :return: word_list, code_list
        """
        word_list, code_list = list(), list()
        for obj in self.error_path:
            word, code = corpus.word(obj)
            if len(word) > 0:
                word_list.append(word)
                code_list.append(code)
        return word_list, code_list


class ConstraintDependencePath:
    """
    [sym_template; depend_on; dependence_node; (dependence_edge; dependence_node)+]
    """
    def __init__(self, sym_template: sym.CSymbolNode, dependence_path):
        self.constraint_path = list()
        self.constraint_path.append(sym_template)
        if dependence_path is not None:
            dependence_path: cpro.CDependencePath
            self.constraint_path.append("depend_on")
            self.constraint_path.append(dependence_path.get_source())
            for dependence_edge in dependence_path.get_edges():
                dependence_edge: cpro.CDependenceEdge
                self.constraint_path.append(dependence_edge)
                self.constraint_path.append(dependence_edge.get_target())
        return

    def get_constraint_path(self):
        """
        :return: [sym_template; depend_on; dependence_node; (dependence_edge; dependence_node)+]
        """
        return self.constraint_path

    @staticmethod
    def __dependence_nodes__(program: cpro.CProgram, cir_location: cirtree.CirNode):
        """
        :param program:
        :param cir_location:
        :return: set of dependence nodes w.r.t. the location in CIR source code
        """
        instance_graph = program.get_instance_graph()
        instances = instance_graph.get_instances().get_instances_of_object(cir_location)
        dependence_graph = program.get_dependence_graph()
        dependence_nodes = set()
        for instance in instances:
            instance: cirinst.CirInstance
            if dependence_graph.has_node(instance):
                dependence_node = dependence_graph.get_node(instance)
                dependence_nodes.add(dependence_node)
        return dependence_nodes

    @staticmethod
    def constraint_dependence_paths(program: cpro.CProgram, source: mut.StateConstraint,
                                    distance: int, optimize: bool, sym_template_depth: int):
        """
        :param sym_template_depth:
        :param optimize:
        :param program:
        :param source:
        :param distance:
        :return: dict[dependence_root] = set[constraint_dependence_path+]
        """
        # 1. get the symbolic templates to create path
        sym_root = source.get_condition()
        if optimize:
            sym_root = sym.sym_evaluator.evaluate(sym_root)
        cir_root = source.get_execution().get_statement()
        sym_templates = sym.CSymTemplate.templates(sym_root, cir_root, sym_template_depth)
        # 2. generate all the constraint path by templates
        constraint_paths = dict()
        for sym_template in sym_templates:
            sym_expression = sym_template.get_sym_template()
            cir_locations = sym_template.get_cir_source_list()
            for cir_location in cir_locations:
                dependence_nodes = ConstraintDependencePath.__dependence_nodes__(program, cir_location)
                for dependence_node in dependence_nodes:
                    constraint_dependence_paths = set()
                    dependence_paths = cpro.get_dependence_paths(dependence_node, distance)
                    for dependence_path in dependence_paths:
                        constraint_dependence_path = ConstraintDependencePath(sym_expression, dependence_path)
                        constraint_dependence_paths.add(constraint_dependence_path)
                    constraint_paths[dependence_node] = constraint_dependence_paths
        # 3. to avoid no dependence path in generation
        if len(constraint_paths) == 0:
            paths = set()
            paths.add(ConstraintDependencePath(sym_root, None))
            constraint_paths[None] = paths
        return constraint_paths

    def get_words(self, corpus: WordsCorpus):
        """
        :param corpus:
        :return: word_list, code_list
        """
        word_list, code_list = list(), list()
        for obj in self.constraint_path:
            word, code = corpus.word(obj)
            if len(word) > 0:
                word_list.append(word)
                code_list.append(code)
        return word_list, code_list


common_corpus = WordsCorpus()


def __output_state_error__(program: cpro.CProgram, state_error: mut.StateError,
                           state_constraints: mut.StateConstraints, writer: TextIO,
                           distance: int, sym_depth: int):
    # error information paths
    writer.write("\t+-----------------------------------------------------------------------+\n")
    writer.write("\t[ERROR]\t")
    writer.write(str(state_error))
    writer.write("\n")
    writer.write("\t{\n")
    error_information_dict = ErrorInformationPath.error_information_paths(program, state_error, distance)
    for error_information_paths in error_information_dict.values():
        for error_information_path in error_information_paths:
            writer.write("\t\t[ERROR_PATH]: ")
            word_list, code_list = error_information_path.get_words(common_corpus)
            for word in word_list:
                writer.write(word + "; ")
            writer.write("\n")
    writer.write("\t}\n")
    # constraint dependence paths
    for state_constraint in state_constraints.get_constraints():
        sym_condition = state_constraint.get_condition()
        sym_condition = sym.sym_evaluator.evaluate(sym_condition)
        writer.write("\t[Constraint]\t" + sym_condition.generate_code(True) +
                     "\tat\t" + str(state_constraint.get_execution()) + "\n")
        writer.write("\t{\n")
        constraint_dependence_paths = ConstraintDependencePath.\
            constraint_dependence_paths(program, state_constraint, distance, True, sym_depth)
        for dependence_paths in constraint_dependence_paths.values():
            for dependence_path in dependence_paths:
                writer.write("\t\t[CONST_PATH]: ")
                word_list, code_list = dependence_path.get_words(common_corpus)
                for word in word_list:
                    writer.write(word + "; ")
                writer.write("\n")
        writer.write("\t}\n")
    writer.write("\t+-----------------------------------------------------------------------+\n")
    return


def __output_state_error_main__():
    prefix = "C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data"
    distance, sym_depth = 3, 2
    for filename in os.listdir(prefix):
        directory = os.path.join(prefix, filename)
        program = cpro.CProgram(directory)
        mutant_space = mut.MutantSpace(program)
        common_corpus.reset()  # reset the encoding feature space
        print("Get mutation information for", program.get_file_name())
        output_file = os.path.join("C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output\\feature_paths", filename + ".path")
        with open(output_file, 'w') as writer:
            for mutant in mutant_space.get_mutants():
                writer.write(str(mutant.get_id()) + "\t")
                writer.write(str(mutant.mutation.get_mutation_class()) + "\t")
                writer.write(str(mutant.mutation.get_mutation_operator()) + "\t")
                location = mutant.mutation.get_location()
                location: astree.AstNode
                writer.write(str(location.get_beg_line()) + "\t")
                writer.write("\"" + location.get_code(True) + "\"\t")
                if mutant.mutation.has_parameter():
                    writer.write(str(mutant.mutation.get_parameter()))
                else:
                    writer.write("")
                writer.write("\t")
                labels = mutant.labels
                labels: mut.MutantLabels
                writer.write(str(labels.get_category()) + "\t")
                writer.write("\n")
                state_infection = mutant.get_features()
                state_infection: mut.StateInfection
                writer.write("coverage at:\t" + str(state_infection.get_faulty_execution()) + "\n")
                for state_error, constraints in state_infection.error_infections.items():
                    constraints: mut.StateConstraints
                    __output_state_error__(program, state_error, constraints, writer, distance, sym_depth)
                writer.write("\n")
    print("Testing end for all.")


class PathFeatureEncoding:
    def __init__(self, path_distance: int, sym_depth: int, optimize: bool, vector_size: int):
        """
        :param path_distance: maximal distance of error or constraint path
        :param sym_depth: maximal depth to generate symbolic templates
        :param optimize: whether to optimize the symbolic constraint at first
        """
        self.__corpus__ = WordsCorpus()
        self.path_distance = path_distance
        self.sym_depth = sym_depth
        self.optimize = optimize
        self.tagged_sentences = list()
        self.doc2vec = None
        self.vector_size = vector_size
        return

    def encoding_word_vectors(self, mutant: mut.Mutant):
        """
        :param mutant:
        :return: set of feature path words as sentences
        """
        infection = mutant.get_features()
        infection: mut.StateInfection
        program = mutant.get_mutant_space().program
        program: cpro.CProgram
        sentence_dict = dict()
        for state_error, state_constraints in infection.error_infections.items():
            state_error: mut.StateError
            state_constraints: mut.StateConstraints
            error_information_dict = ErrorInformationPath.error_information_paths(program, state_error,
                                                                                  self.path_distance)
            for error_information_paths in error_information_dict.values():
                for error_information_path in error_information_paths:
                    # encoding error information path
                    word_list, code_list = error_information_path.get_words(self.__corpus__)
                    sentence_dict[str(word_list)] = word_list
            for state_constraint in state_constraints.get_constraints():
                state_constraint: mut.StateConstraint
                constraint_dependence_dict = ConstraintDependencePath.constraint_dependence_paths(
                    program, state_constraint, self.path_distance, self.optimize, self.sym_depth)
                for constraint_dependence_paths in constraint_dependence_dict.values():
                    for constraint_dependence_path in constraint_dependence_paths:
                        # encoding constraint path
                        word_list, code_list = constraint_dependence_path.get_words(self.__corpus__)
                        sentence_dict[str(word_list)] = word_list
        tagged_sentences = list()
        for key, sentence in sentence_dict.items():
            tagged_sentence = TaggedDocument(words=sentence, tags=key)
            tagged_sentences.append(tagged_sentence)
            self.tagged_sentences.append(tagged_sentence)
        return tagged_sentences

    def train_doc2vec(self, epochs, model_file: str):
        self.doc2vec = Doc2Vec(self.tagged_sentences, vector_size=self.vector_size, workers=4, epochs=epochs)
        self.doc2vec.save(model_file)
        return

    def __average_feature_vectors__(self, feature_vectors: list):
        sum_vector = np.zeros(self.vector_size)
        for feature_vector in feature_vectors:
            feature_vector = np.array(feature_vector)
            sum_vector = sum_vector + feature_vector
        return sum_vector
        # weights = 1.0
        # if len(feature_vectors) > 0:
        #     weights /= len(feature_vectors)
        # return sum_vector ** weights

    def encode_feature_vectors(self, mutant: mut.Mutant):
        """
        :param mutant:
        :return: feature vector encoded by doc2vec in average weights
        """
        infection = mutant.get_features()
        infection: mut.StateInfection
        program = mutant.get_mutant_space().program
        program: cpro.CProgram
        sentence_dict = dict()
        for state_error, state_constraints in infection.error_infections.items():
            state_error: mut.StateError
            state_constraints: mut.StateConstraints
            error_information_dict = ErrorInformationPath.error_information_paths(program, state_error,
                                                                                  self.path_distance)
            for error_information_paths in error_information_dict.values():
                for error_information_path in error_information_paths:
                    # encoding error information path
                    word_list, code_list = error_information_path.get_words(self.__corpus__)
                    sentence_dict[str(word_list)] = word_list
            for state_constraint in state_constraints.get_constraints():
                state_constraint: mut.StateConstraint
                constraint_dependence_dict = ConstraintDependencePath.constraint_dependence_paths(
                    program, state_constraint, self.path_distance, self.optimize, self.sym_depth)
                for constraint_dependence_paths in constraint_dependence_dict.values():
                    for constraint_dependence_path in constraint_dependence_paths:
                        # encoding constraint path
                        word_list, code_list = constraint_dependence_path.get_words(self.__corpus__)
                        sentence_dict[str(word_list)] = word_list
        self.doc2vec: Doc2Vec
        feature_vectors = list()
        for key, sentence in sentence_dict.items():
            feature_vector = self.doc2vec.infer_vector(sentence)
            feature_vectors.append(feature_vector)
        return self.__average_feature_vectors__(feature_vectors)

    def write_csv_file(self, mutant_space: mut.MutantSpace, csv_file: str):
        with open(csv_file, 'w') as writer:
            writer.write("program,id,label")
            for k in range(0, self.vector_size):
                writer.write(",k" + str(k))
            writer.write("\n")
            name = mutant_space.get_program().get_file_name()
            for mutant in mutant_space.get_mutants():
                mutant: mut.Mutant
                if mutant.features is not None:
                    writer.write(name)
                    writer.write("," + str(mutant.id))
                    writer.write("," + str(mutant.get_labels().get_category().value))
                    feature_vector = self.encode_feature_vectors(mutant)
                    for k in range(0, self.vector_size):
                        writer.write("," + str(feature_vector[k]))
                    writer.write("\n")
        return


def training_doc2vec_encoding():
    prefix = "C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data"
    distance, sym_depth = 3, 2
    encoder = PathFeatureEncoding(path_distance=distance, sym_depth=sym_depth, optimize=True, vector_size=128)
    print("Testing start for files.")
    for filename in os.listdir(prefix):
        directory = os.path.join(prefix, filename)
        program = cpro.CProgram(directory)
        mutant_space = mut.MutantSpace(program)
        for mutant in mutant_space.get_mutants():
            encoder.encoding_word_vectors(mutant)
        print("\tComplete encoding", len(mutant_space.get_mutants()), "mutants for", filename)
    print("Start training doc2vec with", len(encoder.tagged_sentences), "sentences.")
    encoder.train_doc2vec(4, os.path.join("C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output\\doc2vec.model"))
    print("Complete doc2vec training")
    return


def generate_training_csv_files():
    prefix = "C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data"
    distance, sym_depth = 3, 2
    encoder = PathFeatureEncoding(path_distance=distance, sym_depth=sym_depth, optimize=True, vector_size=128)
    encoder.doc2vec = Doc2Vec.load(os.path.join("C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output\\doc2vec.model"))
    for filename in os.listdir(prefix):
        directory = os.path.join(prefix, filename)
        program = cpro.CProgram(directory)
        mutant_space = mut.MutantSpace(program)
        output_file = os.path.join("C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output\\training_files", filename + ".csv")
        encoder.write_csv_file(mutant_space, output_file)
        print("\tOutput csv file for", filename, "with", len(mutant_space.mutants), "mutants.")
    print("Testing end for all.")


def generate_predict_csv_files():
    prefix = "C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\pdata"
    distance, sym_depth = 3, 2
    encoder = PathFeatureEncoding(path_distance=distance, sym_depth=sym_depth, optimize=True, vector_size=128)
    encoder.doc2vec = Doc2Vec.load(os.path.join("C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output\\doc2vec.model"))
    for filename in os.listdir(prefix):
        directory = os.path.join(prefix, filename)
        program = cpro.CProgram(directory)
        mutant_space = mut.MutantSpace(program)
        output_file = os.path.join("C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output\\predict_files", filename + ".csv")
        encoder.write_csv_file(mutant_space, output_file)
        print("\tOutput csv file for", filename, "with", len(mutant_space.mutants), "mutants.")
    print("Testing end for all.")


if __name__ == "__main__":
    # __output_state_error_main__()
    # training_doc2vec_encoding()
    # generate_training_csv_files()
    generate_training_csv_files()
    generate_predict_csv_files()
