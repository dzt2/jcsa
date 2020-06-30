"""
Read feature and print it in a human-friend way.
"""


import os
import src.com.jcparse.base as base
import src.com.jcparse.astree as astree
import src.com.jcparse.cirtree as cirtree
import src.com.jcparse.cirflow as cirflow
import src.com.jcparse.cirinst as cirinst
import src.com.jcparse.cprogram as cprog
import src.com.jcmuta.operator as mop
import src.com.jcmuta.symbol as sym
import src.com.jcmuta.mutation as mut
import graphviz
import random
import math
import sklearn.metrics as metrics
import seaborn as sns
import matplotlib.pyplot as plt
import gensim
from gensim.models.doc2vec import Doc2Vec, TaggedDocument
import sklearn.tree as sktree


def normalize_text(text: str):
    buffer = ""
    for k in range(0, len(text)):
        char = text[k]
        if char == '\r' or char == '\n':
            char = ''
        buffer += char
    return buffer


def get_ast_code(ast_node: astree.AstNode, length: int):
    """
    :param ast_node:
    :param length: maximal number of characters of code being presented
    :return: get the formulated structural description of AstNode
    """
    beg_line = ast_node.get_beg_line() + 1
    end_line = ast_node.get_end_line() + 1
    code = ast_node.get_code(True)
    if len(code) > length:
        code = code[0:length]
    return str(ast_node.get_ast_type()) + "[" + str(beg_line) + ", " + str(end_line) + "]::\"" + code + "\""


def get_execution_code(execution: cirflow.CirExecution):
    code = execution.get_statement().generate_code(True)
    return str(execution) + "::\"" + code + "\""


def create_node_in_di_graph(di_graph: graphviz.Digraph, dependence_node: cprog.CDependenceNode):
    instance = dependence_node.get_instance()
    instance: cirinst.CirInstance
    key = str(instance)
    if isinstance(instance, cirinst.CirInstanceNode):
        instance: cirinst.CirInstanceNode
        content = get_execution_code(instance.get_source_execution())
    elif isinstance(instance, cirinst.CirInstanceCode):
        instance: cirinst.CirInstanceCode
        cir_node = instance.get_cir_source_node()
        cir_node: cirtree.CirNode
        content = cir_node.generate_code(True)
    else:
        instance: cirinst.CirInstanceEdge
        content = str(instance)
    di_graph.node(name=key, label=normalize_text(content))
    return di_graph


def create_edge_in_di_graph(di_graph: graphviz.Digraph, dependence_edge: cprog.CDependenceEdge):
    source_key = str(dependence_edge.get_source().get_instance())
    target_key = str(dependence_edge.get_target().get_instance())
    di_graph.edge(source_key, target_key, label=str(dependence_edge.get_dependence_type()))
    return


def print_dependence_graph(graph: cprog.CDependenceGraph, output: str, view=False):
    """
    :param graph: program dependence graph
    :param output: output file where .gv is created
    :param view: whether to open the .gv.pdf file
    :return: print dependence information on file.pdf
    """
    # 1. declarations
    program = graph.program
    program: cprog.CProgram
    di_graph = graphviz.Digraph(name=program.get_file_name())

    # 2. create nodes
    for dependence_node in graph.get_nodes():
        dependence_node: cprog.CDependenceNode
        create_node_in_di_graph(di_graph, dependence_node)

    # 3. create edges
    for dependence_node in graph.get_nodes():
        dependence_node: cprog.CDependenceNode
        for dependence_edge in dependence_node.get_ou_edges():
            dependence_edge: cprog.CDependenceEdge
            create_edge_in_di_graph(di_graph, dependence_edge)

    # 4. print pdf file
    di_graph.render(filename=output, view=view)
    return


class CDependenceTreeNode:
    """
    {node, edge; tree, parent, children;}
    """
    def __init__(self, tree, node: cprog.CDependenceNode, edge):
        self.tree = tree
        self.node = node
        self.edge = edge
        self.edge: cprog.CDependenceEdge
        self.parent = None
        self.children = list()
        return

    def get_tree(self):
        return self.tree

    def is_influence_edge(self):
        if self.edge is not None:
            return self.edge.get_source() == self.node
        else:
            return False

    def is_dependence_edge(self):
        if self.edge is not None:
            return self.edge.get_target() == self.node
        else:
            return False

    def get_dependence_node(self):
        return self.node

    def get_dependence_edge(self):
        self.edge: cprog.CDependenceEdge
        return self.edge

    def get_parent(self):
        return self.parent

    def get_children(self):
        return self.children

    def add_child(self, edge: cprog.CDependenceEdge):
        for child in self.children:
            child: CDependenceTreeNode
            if child.get_dependence_edge() == edge:
                return child
        if edge.get_source() == self.node:
            child = CDependenceTreeNode(self.tree, edge.get_target(), edge)
            self.children.append(child)
            child.parent = self
            return child
        elif edge.get_target() == self.node:
            child = CDependenceTreeNode(self.tree, edge.get_source(), edge)
            self.children.append(child)
            child.parent = self
            return child
        else:
            return None

    def __leafs__(self, leafs: list):
        if len(self.children) == 0:
            leafs.append(self)
        else:
            for child in self.children:
                child: CDependenceTreeNode
                child.__leafs__(leafs)
        return

    def __nodes__(self, nodes: list):
        nodes.append(self)
        for child in self.children:
            child.__nodes__(nodes)
        return

    def get_all_nodes(self):
        nodes = list()
        self.__nodes__(nodes)
        return nodes

    def get_leafs(self):
        leafs = list()
        self.__leafs__(leafs)
        return leafs

    def extend_from_statement_point(self):
        """
        :return: new_children
        """
        new_children = list()
        for edge in self.node.get_in_edges():
            edge: cprog.CDependenceEdge
            if edge.is_operand_dependence():
                child = self.add_child(edge)
                if child is not None:
                    child.extend_from_statement_point()
                    new_children.append(child)
        return new_children

    def extend_to_operands_points(self):
        """
        :return:
        """
        new_children = list()
        for edge in self.node.get_ou_edges():
            edge: cprog.CDependenceEdge
            if edge.is_operand_dependence():
                child = self.add_child(edge)
                if child is not None:
                    new_children.append(child)
                    child.extend_to_operands_points()
        return new_children

    def extend_to_control_dependence(self):
        """
        :return:
        """
        new_children = list()
        for edge in self.node.get_ou_edges():
            edge: cprog.CDependenceEdge
            if edge.is_control_dependence():
                child = self.add_child(edge)
                if child is not None:
                    new_children.append(child)
        return new_children

    def extend_to_data_dependence(self):
        new_children = list()
        for edge in self.node.get_ou_edges():
            edge: cprog.CDependenceEdge
            if edge.is_data_dependence():
                child = self.add_child(edge)
                if child is not None:
                    new_children.append(child)
        return new_children

    def extend_from_control_dependence(self):
        new_children = list()
        for edge in self.node.get_in_edges():
            edge: cprog.CDependenceEdge
            if edge.is_control_dependence():
                child = self.add_child(edge)
                if child is not None:
                    new_children.append(child)
        return new_children

    def extend_from_data_dependence(self):
        new_children = list()
        for edge in self.node.get_in_edges():
            edge: cprog.CDependenceEdge
            if edge.is_data_dependence():
                child = self.add_child(edge)
                if child is not None:
                    new_children.append(child)
        return new_children

    def path_of(self):
        """
        :return: List[CDependenceNode|CDependenceEdge]
        """
        tree_node = self
        path = list()
        while tree_node is not None:
            node = tree_node.get_dependence_node()
            edge = tree_node.get_dependence_edge()
            path.append(node)
            if edge is not None:
                path.append(edge)
            tree_node = tree_node.parent
        path.reverse()
        return path


class CDependenceTree:
    def __init__(self):
        self.root = None
        return

    def get_root(self):
        self.root: CDependenceTreeNode
        return self.root

    def __nodes__(self, parent: CDependenceTreeNode, nodes: list):
        nodes.append(parent)
        for child in parent.get_children():
            child: CDependenceTreeNode
            self.__nodes__(child, nodes)
        return

    def get_nodes(self):
        nodes = list()
        self.__nodes__(self.root, nodes)
        return nodes

    def get_leafs(self):
        return self.root.get_leafs()

    def print_graph_visual(self, di_graph: graphviz.Digraph):
        tree_nodes = self.get_nodes()
        # create nodes
        for tree_node in tree_nodes:
            tree_node: CDependenceTreeNode
            dependence_node = tree_node.get_dependence_node()
            key = str(tree_node)
            instance = dependence_node.get_instance()
            instance: cirinst.CirInstance
            if isinstance(instance, cirinst.CirInstanceNode):
                instance: cirinst.CirInstanceNode
                content = get_execution_code(instance.get_source_execution())
            elif isinstance(instance, cirinst.CirInstanceCode):
                instance: cirinst.CirInstanceCode
                cir_node = instance.get_cir_source_node()
                cir_node: cirtree.CirNode
                content = cir_node.generate_code(True)
            else:
                instance: cirinst.CirInstanceEdge
                content = str(instance)
            di_graph.node(name=key, label=normalize_text(content))
        # create edges
        for tree_node in tree_nodes:
            tree_node: CDependenceTreeNode
            for child in tree_node.get_children():
                child: CDependenceTreeNode
                di_graph.edge(str(tree_node), str(child), label=str(child.get_dependence_edge().get_dependence_type()))
        return

    @staticmethod
    def __control_dependence_tree__(parent: CDependenceTreeNode, distance: int):
        if distance > 0:
            statement = parent.extend_from_statement_point()
            if len(statement) > 0:
                parent = statement[0].get_leafs()[0]
            CDependenceTree.__data_dependence_tree__(parent, 1)
            new_children = parent.extend_to_control_dependence()
            for new_child in new_children:
                CDependenceTree.__control_dependence_tree__(new_child, distance - 1)
        return

    @staticmethod
    def control_dependence_tree(root_node: cprog.CDependenceNode, distance: int):
        tree = CDependenceTree()
        tree.root = CDependenceTreeNode(tree, root_node, None)
        CDependenceTree.__control_dependence_tree__(tree.root, distance)
        return tree

    @staticmethod
    def __data_dependence_tree__(parent: CDependenceTreeNode, distance: int):
        if distance > 0:
            new_operands = parent.extend_to_operands_points()
            all_operands = list()
            for new_operand in new_operands:
                all_in_new_operand = new_operand.get_all_nodes()
                for node in all_in_new_operand:
                    all_operands.append(node)
            new_children = list()
            for operand in all_operands:
                operand: CDependenceTreeNode
                children = operand.extend_to_data_dependence()
                for child in children:
                    new_children.append(child)
            for child in new_children:
                CDependenceTree.__data_dependence_tree__(child, distance - 1)
        return

    @staticmethod
    def data_dependence_tree(root_node: cprog.CDependenceNode, distance: int):
        tree = CDependenceTree()
        tree.root = CDependenceTreeNode(tree, root_node, None)
        CDependenceTree.__data_dependence_tree__(tree.root, distance)
        return tree

    @staticmethod
    def __influence_tree__(parent: CDependenceTreeNode, distance: int):
        if distance > 0:
            parent_sequence = parent.extend_from_statement_point()
            parent_nodes = set()
            parent_nodes.add(parent)
            for parent_node in parent_sequence:
                all_nodes = parent_node.get_all_nodes()
                for node in all_nodes:
                    parent_nodes.add(node)
            new_children = list()
            for parent_node in parent_nodes:
                children = parent_node.extend_from_data_dependence()
                for child in children:
                    new_children.append(child)
                children = parent_node.extend_from_control_dependence()
                for child in children:
                    new_children.append(child)
            for child in new_children:
                CDependenceTree.__influence_tree__(child, distance - 1)
        return

    @staticmethod
    def influence_tree(root_node: cprog.CDependenceNode, distance: int):
        tree = CDependenceTree()
        tree.root = CDependenceTreeNode(tree, root_node, None)
        CDependenceTree.__influence_tree__(tree.root, distance)
        return tree


def print_reach_context(mutant: mut.Mutant, filename: str, view=False, max_distance=2):
    program = mutant.get_mutant_space().get_program()
    program: cprog.CProgram
    instance_graph = program.get_instance_graph()
    instance_graph: cirinst.CirInstanceGraph
    dependence_graph = program.get_dependence_graph()
    dependence_graph: cprog.CDependenceGraph

    start_execution = mutant.get_features().get_faulty_execution()
    start_instances = list()
    for instance_node in instance_graph.get_nodes():
        instance_node: cirinst.CirInstanceNode
        if instance_node.get_source_execution() == start_execution:
            start_instances.append(instance_node)

    di_graph = graphviz.Digraph(name=filename + ".reach." + str(mutant.id))
    if len(start_instances) > 0:
        start_instance = start_instances[random.randint(0, len(start_instances) - 1)]
        dependence_node = dependence_graph.get_node(start_instance)
        control_dependence_tree = CDependenceTree.control_dependence_tree(dependence_node, max_distance)
        control_dependence_tree.print_graph_visual(di_graph)
    di_graph.render(filename=filename + ".reach." + str(mutant.id), view=view)

    return


def print_infect_context(mutant: mut.Mutant, filename: str, view=False, max_distance=1):
    program = mutant.get_mutant_space().get_program()
    program: cprog.CProgram
    instance_graph = program.get_instance_graph()
    instance_graph: cirinst.CirInstanceGraph
    dependence_graph = program.get_dependence_graph()
    dependence_graph: cprog.CDependenceGraph

    start_execution = mutant.get_features().get_faulty_execution()
    start_instances = list()
    for instance_node in instance_graph.get_nodes():
        instance_node: cirinst.CirInstanceNode
        if instance_node.get_source_execution() == start_execution:
            start_instances.append(instance_node)

    di_graph = graphviz.Digraph(name=filename + ".infect." + str(mutant.id))
    if len(start_instances) > 0:
        start_instance = start_instances[random.randint(0, len(start_instances) - 1)]
        dependence_node = dependence_graph.get_node(start_instance)
        data_dependence_tree = CDependenceTree.data_dependence_tree(dependence_node, max_distance)
        data_dependence_tree.print_graph_visual(di_graph)
    di_graph.render(filename=filename + ".infect." + str(mutant.id), view=view)
    return


def print_propagate_context(mutant: mut.Mutant, filename: str, view=False, max_distance=1):
    program = mutant.get_mutant_space().get_program()
    program: cprog.CProgram
    instance_graph = program.get_instance_graph()
    instance_graph: cirinst.CirInstanceGraph
    dependence_graph = program.get_dependence_graph()
    dependence_graph: cprog.CDependenceGraph

    location, source_error = None, None
    for state_error in mutant.get_features().get_state_errors():
        state_error: mut.StateError
        for operand in state_error.get_operands():
            if isinstance(operand, cirtree.CirNode):
                location = operand
                break
        if location is not None:
            source_error = state_error
            break

    if location is not None:
        location: cirtree.CirNode
        source_error: mut.StateError
        source_instance = None
        for instance_node in instance_graph.get_nodes():
            instance_node: cirinst.CirInstanceNode
            for instance_code in instance_node.get_cir_instances_in():
                instance_code: cirinst.CirInstanceCode
                if instance_code.get_cir_source_node() == location:
                    source_instance = instance_code
                    break
            if source_instance is not None:
                break
        if source_instance is not None:
            dependence_node = dependence_graph.get_node(source_instance)
            propagate_tree = CDependenceTree.influence_tree(dependence_node, max_distance)
            di_graph = graphviz.Digraph(name=filename + ".propagate." + str(mutant.id))
            propagate_tree.print_graph_visual(di_graph)
            di_graph.node(str(source_error), label=str(source_error))
            di_graph.edge(str(source_error), str(propagate_tree.root), label="seeded_in")
            di_graph.render(filename=filename + ".propagate." + str(mutant.id), view=view)
    return


def print_words_in_paths(mutant: mut.Mutant, filename: str, max_distance=1):
    filename = filename + ".path." + str(mutant.id)
    with open(filename, 'w') as writer:
        program = mutant.get_mutant_space().get_program()
        program: cprog.CProgram
        instance_graph = program.get_instance_graph()
        instance_graph: cirinst.CirInstanceGraph
        dependence_graph = program.get_dependence_graph()
        dependence_graph: cprog.CDependenceGraph

        writer.write("Mutant:\n")
        writer.write("Class:\t" + str(mutant.mutation.get_mutation_operator()) + "\n")
        writer.write("Operator:\t" + str(mutant.mutation.get_mutation_operator()) + "\n")
        writer.write("Location:\t" + get_ast_code(mutant.mutation.get_location(), 128) + "\n")
        if mutant.mutation.has_parameter():
            writer.write("Parameter:\t" + str(mutant.mutation.get_parameter()) + "\n")

        writer.write("\n#PATHS:\n")
        location, source_error = None, None
        for state_error in mutant.get_features().get_state_errors():
            state_error: mut.StateError
            for operand in state_error.get_operands():
                if isinstance(operand, cirtree.CirNode):
                    location = operand
                    break
            if location is not None:
                source_error = state_error
                break

        if location is not None:
            location: cirtree.CirNode
            source_error: mut.StateError
            source_instance = None
            for instance_node in instance_graph.get_nodes():
                instance_node: cirinst.CirInstanceNode
                for instance_code in instance_node.get_cir_instances_in():
                    instance_code: cirinst.CirInstanceCode
                    if instance_code.get_cir_source_node() == location:
                        source_instance = instance_code
                        break
                if source_instance is not None:
                    break
            if source_instance is not None:
                dependence_node = dependence_graph.get_node(source_instance)
                # generate dependence tree here
                propagation_tree = CDependenceTree.influence_tree(dependence_node, max_distance)
                for leaf in propagation_tree.get_leafs():
                    leaf: CDependenceTreeNode
                    path = leaf.path_of()
                    writer.write(str(mutant.mutation.get_mutation_operator()) + "\t")
                    writer.write("seeded_in")
                    for path_node in path:
                        if isinstance(path_node, cprog.CDependenceNode):
                            path_node: cprog.CDependenceNode
                            instance = path_node.get_instance()
                            if isinstance(instance, cirinst.CirInstanceNode):
                                instance: cirinst.CirInstanceNode
                                writer.write("\t" + str(instance.get_source_statement().cir_type))
                            else:
                                instance: cirinst.CirInstanceCode
                                writer.write("\t" + str(instance.get_cir_source_node().cir_type))
                        else:
                            path_node: cprog.CDependenceEdge
                            if path_node.dependence_type == cprog.CDependenceType.parent_child_depend:
                                writer.write("\toperand_of")
                            else:
                                writer.write("\t" + str(path_node.dependence_type))
                    writer.write("\n")
        writer.write("\nEND_PATHS")
    return


class Word2Int:
    def __init__(self):
        self.encode_dict = dict()
        self.decode_list = list()
        return

    def encode(self, word: str):
        if word not in self.encode_dict:
            self.encode_dict[word] = len(self.decode_list)
            self.decode_list.append(word)
        return self.encode_dict[word]

    def encodes(self, words):
        code_list = list()
        for word in words:
            if word not in self.encode_dict:
                self.encode_dict[word] = len(self.decode_list)
                self.decode_list.append(word)
            code_list.append(self.encode_dict[word])
        code_list.sort()
        return code_list

    def decode(self, code: int):
        return self.decode_list[code]

    def decodes(self, code_list):
        words = list()
        for code in code_list:
            word = self.decode_list[code]
            words.append(word)
        return words

    def __len__(self):
        return len(self.decode_list)

    def save(self, file: str):
        with open(file, 'w') as writer:
            for word in self.decode_list:
                writer.write(str(word))
                writer.write("\n")
        return

    def load(self, file: str):
        with open(file, 'r') as reader:
            self.encode_dict.clear()
            self.decode_list.clear()
            for line in reader:
                word = line.strip()
                if len(word) > 0:
                    if word not in self.encode_dict:
                        self.encode_dict[word] = len(self.decode_list)
                        self.decode_list.append(word)
        return

    def clear(self):
        self.encode_dict.clear()
        self.decode_list.clear()
        return


class FeatureEvaluation:
    def __init__(self, prob_threshold=0.01):
        self.classifier = dict()
        self.word2int = Word2Int()
        self.prob_threshold = prob_threshold
        return

    def __key__(self, mutant: mut.Mutant):
        words = set()
        for state_error in mutant.get_features().get_state_errors():
            word = mutant.get_mutant_space().program.get_file_name()
            word += "::" + str(state_error)
            words.add(word)
            constraints = mutant.get_features().get_constraints_for(state_error)
            for constraint in constraints.get_constraints():
                word += mutant.get_mutant_space().program.get_file_name()
                word += str(constraint.get_execution()) + "::"
                word += constraint.get_condition().generate_code(True)
                words.add(word)
        return str(self.word2int.encodes(words))

    def __label__(self, mutant: mut.Mutant):
        mutant.get_labels().define_category(self.prob_threshold)
        return mutant.get_labels().get_category().value

    def train(self, mutants):
        for mutant in mutants:
            mutant: mut.Mutant
            key = self.__key__(mutant)
            self.__label__(mutant)
            if key not in self.classifier:
                self.classifier[key] = set()
            self.classifier[key].add(mutant)
        return

    def __distribution__(self, key: str):
        """
        :param key:
        :return: distribution, predict_label
        """
        distribution = dict()
        distribution[0] = list()
        distribution[1] = list()
        for mutant in self.classifier[key]:
            mutant: mut.Mutant
            label = mutant.get_labels().get_category().value
            distribution[label].append(mutant)
        if len(distribution[0]) > len(distribution[1]):
            return distribution, 0
        else:
            return distribution, 1

    @staticmethod
    def __entropy__(distribution: dict):
        if len(distribution[0]) == 0 or len(distribution[1]) == 0:
            return 0
        else:
            p1 = len(distribution[0]) / (len(distribution[1]) + len(distribution[0]) + 0.0)
            p2 = 1.0 - p1
            return -(p1 * math.log2(p1) + p2 * math.log2(p2))

    def evaluate(self, error_file: str):
        entropy_list = list()
        error_samples = list()
        t_labels, p_labels = list(), list()
        transitions = dict()
        for key, mutants in self.classifier.items():
            distribution, p_label = self.__distribution__(key)
            entropy_list.append(FeatureEvaluation.__entropy__(distribution))
            for mutant in mutants:
                mutant: mut.Mutant
                if mutant.get_labels().get_category().value != p_label:
                    error_samples.append(mutant)
                tab = str(mutant.get_labels().get_category().value) + "_" + str(p_label)
                t_labels.append(mutant.get_labels().get_category().value)
                p_labels.append(p_label)
                if tab not in transitions:
                    transitions[tab] = 0
                transitions[tab] += 1
        with open(error_file, 'w') as writer:
            for mutant in error_samples:
                writer.write(mutant.get_mutant_space().get_program().get_file_name())
                writer.write("\t" + str(mutant.id) + "\t" + str(mutant.get_labels().category.name))
                if mutant.get_labels().get_category() == mut.MutationCategory.killable:
                    writer.write("\tunkillable\n")
                else:
                    writer.write("\tkillable\n")
        print("Classifier Accuracy <=", metrics.accuracy_score(t_labels, p_labels))
        print(metrics.classification_report(t_labels, p_labels, target_names=["Killable", "Equivalent"]))
        print("Transition Table =", str(transitions))
        sns.kdeplot(entropy_list, shade=True, color="b", label=" Entropy")
        plt.xlabel("Feature Entropy")
        plt.ylabel("Entropy Distribution")
        plt.show()
        return


class FeatureEncoding:
    def __init__(self):
        self.sentences = list()
        self.model = None
        return

    def reset(self):
        self.model = None
        self.sentences.clear()
        return

    def __word__(self, instance):
        if isinstance(instance, cprog.CDependenceNode):
            instance: cprog.CDependenceNode
            return self.__word__(instance.get_instance())
        elif isinstance(instance, cprog.CDependenceEdge):
            instance: cprog.CDependenceEdge
            if instance.is_control_dependence():
                return str(instance.get_dependence_type())
            elif instance.is_data_dependence():
                return "define_use"
            else:
                return "operand_of"
        elif isinstance(instance, cirinst.CirInstanceNode):
            instance: cirinst.CirInstanceNode
            return self.__word__(instance.get_source_statement())
        elif isinstance(instance, cirinst.CirInstanceCode):
            instance: cirinst.CirInstanceCode
            return self.__word__(instance.get_cir_source_node())
        elif isinstance(instance, cirtree.CirNode):
            instance: cirtree.CirNode
            if instance.is_assign_statement():
                return "assignment"
            elif instance.is_computational_expression():
                return str(instance.get_cir_type()) + "_" + str(instance.get_operator())
            elif instance.is_tag_statement():
                return "#tag"
            else:
                return str(instance.get_cir_type())
        else:
            return str(instance)

    def __paths__(self, mutant: mut.Mutant, max_distance: int):
        """
        :param mutant:
        :param max_distance:
        :return: coverage-tree, infection-tree and propagation-tree
        """
        program = mutant.get_mutant_space().get_program()
        program: cprog.CProgram
        instance_graph = program.get_instance_graph()
        instance_graph: cirinst.CirInstanceGraph
        dependence_graph = program.get_dependence_graph()
        dependence_graph: cprog.CDependenceGraph
        paths = dict()

        start_execution = mutant.get_features().get_faulty_execution()
        start_instances = list()
        for instance_node in instance_graph.get_nodes():
            instance_node: cirinst.CirInstanceNode
            if instance_node.get_source_execution() == start_execution:
                start_instances.append(instance_node)

        if len(start_instances) > 0:
            start_instance = start_instances[random.randint(0, len(start_instances) - 1)]
            dependence_node = dependence_graph.get_node(start_instance)
            coverage_tree = CDependenceTree.control_dependence_tree(dependence_node, max_distance)
            infection_tree = CDependenceTree.data_dependence_tree(dependence_node, max_distance)
            leafs = coverage_tree.get_leafs()
            for leaf in leafs:
                leaf: CDependenceTreeNode
                orig_path = leaf.path_of()
                path = list()
                path.append(str(mutant.mutation.get_mutation_operator()))
                path.append("seeded_in")
                for orig_object in orig_path:
                    path.append(self.__word__(orig_object))
                key = str(path)
                if key not in paths:
                    paths[key] = path
            leafs = infection_tree.get_leafs()
            for leaf in leafs:
                leaf: CDependenceTreeNode
                orig_path = leaf.path_of()
                path = list()
                path.append(str(mutant.mutation.get_mutation_operator()))
                path.append("seeded_in")
                for orig_object in orig_path:
                    path.append(self.__word__(orig_object))
                key = str(path)
                if key not in paths:
                    paths[key] = path

        for state_error in mutant.get_features().get_state_errors():
            state_error: mut.StateError
            for operand in state_error.get_operands():
                if isinstance(operand, cirtree.CirNode):
                    operand: cirtree.CirNode
                    dependence_nodes = list()
                    for dependence_node in dependence_graph.get_nodes():
                        dependence_node: cprog.CDependenceNode
                        instance = dependence_node.get_instance()
                        if isinstance(instance, cirinst.CirInstanceCode):
                            instance: cirinst.CirInstanceCode
                            if instance.get_cir_source_node() == operand:
                                dependence_nodes.append(dependence_node)
                    if len(dependence_nodes) > 0:
                        dependence_node = dependence_nodes[random.randint(0, len(dependence_nodes) - 1)]
                        propagation_tree = CDependenceTree.influence_tree(dependence_node, max_distance)
                        leafs = propagation_tree.get_leafs()
                        for leaf in leafs:
                            leaf: CDependenceTreeNode
                            orig_path = leaf.path_of()
                            path = list()
                            path.append(str(state_error.get_error_type()))
                            path.append("caused_to")
                            for orig_object in orig_path:
                                path.append(self.__word__(orig_object))
                            key = str(path)
                            if key not in paths:
                                paths[key] = path
                    break

        return paths

    def print_paths(self, mutants, output_file: str, max_distance: int):
        with open(output_file, 'w') as writer:
            for mutant in mutants:
                mutant: mut.Mutant
                paths = self.__paths__(mutant, max_distance)
                writer.write("Mutant#" + str(mutant.get_id()) + "\n")
                writer.write("\tClass: " + str(mutant.mutation.get_mutation_class()) + "\n")
                writer.write("\tOperator: " + str(mutant.mutation.get_mutation_operator()) + "\n")
                writer.write("\tLocation: " + get_ast_code(mutant.mutation.get_location(), 128) + "\n")
                if mutant.mutation.has_parameter():
                    writer.write("\tParameter: " + str(mutant.mutation.get_parameter()) + "\n")
                writer.write("\t#PATH:\n")
                for key, path in paths.items():
                    writer.write("\t\t" + str(path) + "\n")
                writer.write("\t#END_PATH\n\n")
        return

    def encode_paths(self, mutants, max_distance: int):
        for mutant in mutants:
            paths = self.__paths__(mutant, max_distance)
            for key, path in paths.items():
                self.sentences.append(TaggedDocument(words=path, tags=key))
        return

    def generate_doc2vec(self, model_file: str, corpus_file: str):
        self.model = Doc2Vec(self.sentences, vector_size=32, window=2, min_count=1, workers=4, epochs=2)
        print("\t\t\t--> Generate model of encoding.")
        self.model.save(model_file)
        with open(corpus_file, 'w') as writer:
            for sentence in self.sentences:
                sentence: TaggedDocument
                writer.write(str(sentence.words))
                writer.write("\n")
        return

    def generate_data_file(self, mutants, max_distance: int, data_file: str):
        self.model: Doc2Vec
        first = True
        with open(data_file, 'w') as writer:
            for mutant in mutants:
                mutant: mut.Mutant
                paths = self.__paths__(mutant, max_distance)
                vectors = list()
                for key, path in paths.items():
                    vector = self.model.infer_vector(path)
                    vectors.append(vector)
                if len(vectors) > 0:
                    if first:
                        first = False
                        writer.write("Program,ID,Label")
                        for k in range(0, len(vectors[0])):
                            writer.write(",x" + str(k))
                        writer.write("\n")
                    writer.write(mutant.get_mutant_space().get_program().get_file_name())
                    writer.write("," + str(mutant.id))
                    writer.write("," + str(mutant.get_labels().get_category().value))
                    feature_vector = vectors[random.randint(0, len(vectors) - 1)]
                    for code in feature_vector:
                        writer.write("," + str(code))
                    writer.write("\n")
        return


def read_dependence_graphs():
    """
    :return: Read dependence graph feature and generate pdf file for review.
    """
    prefix = "C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data"
    postfix = "C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output\\dependence"
    print("Start testing here.")
    for filename in os.listdir(prefix):
        directory = os.path.join(prefix, filename)
        program = cprog.CProgram(directory)
        output_file = os.path.join(postfix, filename)
        print_dependence_graph(program.get_dependence_graph(), output_file, False)
        print("\tPrint dependence graph for", filename)
    print("Testing end for all.")
    return


def read_fault_detection_contexts():
    """
    :return: Generate the k-degree of fault detection context for reachability, infection and propagation of each mutant
    """
    prefix = "C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data"
    postfix = "C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output\\features"
    max_distance = 3
    print("Start testing here.")
    for filename in os.listdir(prefix):
        directory = os.path.join(prefix, filename)
        program = cprog.CProgram(directory)
        mutant_space = mut.MutantSpace(program)
        output_file = os.path.join(postfix, filename)
        mutant = mutant_space.get_mutant(random.randint(0, len(mutant_space.mutants) - 1))
        print_reach_context(mutant, output_file, False, max_distance)
        mutant = mutant_space.get_mutant(random.randint(0, len(mutant_space.mutants) - 1))
        print_infect_context(mutant, output_file, False, max_distance)
        mutant = mutant_space.get_mutant(random.randint(0, len(mutant_space.mutants) - 1))
        print_propagate_context(mutant, output_file, False, max_distance)
        mutant = mutant_space.get_mutant(random.randint(0, len(mutant_space.mutants) - 1))
        print("Testing on", program.filename, "for mutant", mutant.id)
    print("Testing end for all.")
    return


def evaluate_feature_model():
    """
    :return: evaluate the feature entropy and accuracy metrics.
    """
    prefix = "C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data"
    postfix = "C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output\\evaluate"
    print("Start testing here.")
    evaluator = FeatureEvaluation()
    for filename in os.listdir(prefix):
        directory = os.path.join(prefix, filename)
        program = cprog.CProgram(directory)
        mutant_space = mut.MutantSpace(program)
        evaluator.train(mutant_space.get_mutants())
        print("\tTraining from", filename, "for", len(mutant_space.get_mutants()), "mutants.")
    evaluator.evaluate(os.path.join(postfix, "feature.error.txt"))
    return


def encoding_feature_vectors():
    """
    :return: generate .corpus, .path and .csv
    """
    prefix = "C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data"
    postfix = "C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output\\encoding"
    print("Start testing here.")
    max_distance = 3
    encoder = FeatureEncoding()
    csv_files = list()
    for filename in os.listdir(prefix):
        directory = os.path.join(prefix, filename)
        program = cprog.CProgram(directory)
        mutant_space = mut.MutantSpace(program)
        encoder.reset()
        print("Testing for", filename, "starts.")
        print("\t\t1. Parsing program and", len(mutant_space.get_mutants()), "mutants from code.")
        encoder.encode_paths(mutant_space.get_mutants(), max_distance)
        encoder.print_paths(mutant_space.get_mutants(), os.path.join(postfix, filename + ".path.txt"), max_distance)
        print("\t\t2. Encode the documents in mutation with", max_distance, "length.")
        encoder.generate_doc2vec(os.path.join(postfix, filename + ".encoding.model"),
                                 os.path.join(postfix, filename + "paths.corpus"))
        print("\t\t3. Generate .corpus and .model file in", postfix)
        csv_files.append(os.path.join(postfix, filename + ".csv"))
        encoder.generate_data_file(mutant_space.get_mutants(), max_distance, os.path.join(postfix, filename + ".csv"))
        print("\t\t4. Generate .csv file in", postfix)
        print("\tTesting for", program.get_file_name(), "finished.")
    csv_total_file = os.path.join(postfix, "training_data.csv")
    with open(csv_total_file, 'w') as writer:
        first = True
        for csv_file in csv_files:
            first2 = True
            with open(csv_file, 'r') as reader:
                for line in reader:
                    line = line.strip()
                    if first2:
                        first2 = False
                        if first:
                            first = False
                            writer.write(line.strip() + "\n")
                    elif len(line) > 0:
                        writer.write(line + "\n")
    print("Testing end for all.")
    return


class FeatureEncoding2:
    def __init__(self, prob_threshold: float):
        self.sentences = list()
        self.prob_threshold = prob_threshold
        return

    def __sentence__(self, mutant: mut.Mutant):
        sentence = list()
        for state_error in mutant.get_features().get_state_errors():
            constraints = mutant.get_features().get_constraints_for(state_error)
            sentence.append(mutant.get_mutant_space().program.get_file_name() + "::" + str(state_error))
            for constraint in constraints:
                constraint: mut.StateConstraint
                sentence.append(mutant.get_mutant_space().program.get_file_name() + "::" +
                                str(constraint.get_execution()) + "::" + constraint.get_condition().generate_code(True))
        self.sentences.append(TaggedDocument(sentence, str(sentence)))
        return sentence

    def collect_for(self, mutants):
        for mutant in mutants:
            self.__sentence__(mutant)
        return

    def generate_doc2vec(self, model_file: str, corpus_file: str):
        model = Doc2Vec(self.sentences, vector_size=32, window=2, min_count=1, workers=4, epochs=2)
        print("\t\t\t--> Generate model of encoding.")
        model.save(model_file)


if __name__ == "__main__":
    evaluate_feature_model()
    # encoding_feature_vectors()
