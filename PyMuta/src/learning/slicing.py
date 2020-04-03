import src.cmodel.ccode as ccode
import src.cmodel.cflow as cflow
import src.cmodel.mutant as cmutant
import src.learning.encoding as encoding
import src.learning.selection as selection
import random


class CirInstanceSubGraph:
    def __init__(self):
        self.graph = None
        self.nodes = set()
        self.edges = set()
        return


class CDependenceSubGraph:
    def __init__(self):
        self.graph = None
        self.nodes = set()
        self.edges = set()
        return

    def union(self, another):
        another: CDependenceSubGraph
        graph = CDependenceSubGraph()
        graph.graph = self.graph
        for node in self.nodes:
            graph.nodes.add(node)
        for node in another.nodes:
            graph.nodes.add(node)
        for edge in self.edges:
            graph.edges.add(edge)
        for edge in another.edges:
            graph.edges.add(edge)
        return graph

    def instance_graph(self):
        """
        generate a subgraph of instances
        :return: CirInstanceSubGraph
        """
        graph = CirInstanceSubGraph()
        if len(self.nodes) > 0:
            for node in self.nodes:
                node: cflow.CDependenceNode
                graph.graph = node.graph.program.instance_graph
                break
            for node in self.nodes:
                node: cflow.CDependenceNode
                graph.nodes.add(node.get_instance())
            for edge in self.edges:
                edge: cflow.CDependenceEdge
                if edge.is_control_depend():
                    instance = edge.get_target().get_instance()
                    instance: cflow.CirInstanceNode
                    if edge.get_depend_type() == "predicate_depend":
                        element = edge.get_element()
                        element: cflow.CPredicateElement
                        if element.get_value():
                            instance_edge = instance.get_ou_edge("true_flow")
                        else:
                            instance_edge = instance.get_ou_edge("fals_flow")
                    elif edge.get_depend_type() == "stmt_call_depend":
                        instance_edge = instance.get_ou_edge("call_flow")
                    else:
                        instance_edge = instance.get_ou_edge("retr_flow")
                    if instance_edge is not None:
                        graph.edges.add(instance_edge)
        return graph


class CDependenceBuilder:
    def __init__(self):
        self.nodes = set()
        self.edges = set()
        return

    @staticmethod
    def instances_of(node: ccode.CirNode):
        program = node.tree.program
        function_graph = program.function_graph
        function_graph: cflow.CirFunctionGraph
        execution = function_graph.get_execution_of_cir_node(node)
        if execution is not None:
            execution: cflow.CirExecution
            instance_graph = program.instance_graph
            instance_graph: cflow.CirInstanceGraph
            return instance_graph.get_nodes_of_execution(execution)
        else:
            return list()

    @staticmethod
    def __dependence_node__(instance: cflow.CirInstanceNode):
        program = instance.graph.program
        dependence_graph = program.dependence_graph
        dependence_graph: cflow.CDependenceGraph
        return dependence_graph.get_node(instance)

    def __direct_control_depend_on__(self, source: cflow.CDependenceNode):
        """
        :param source:
        :return: nodes directly dependent by the source
        """
        next_nodes = set()
        for depend_edge in source.ou_edges:
            depend_edge: cflow.CDependenceEdge
            if depend_edge.is_control_depend():
                next_nodes.add(depend_edge.get_target())
                self.nodes.add(depend_edge.get_target())
                self.edges.add(depend_edge)
        return next_nodes

    def __direct_data_depend_on__(self, source: cflow.CDependenceNode, location=None):
        """
        :param source:
        :param location: None when location refers to the statement of source itself
        :return: nodes that the source directly depends on via references in location
        """
        ''' get the references being depended '''
        if location is None:
            location = source.get_instance().get_execution().get_statement()
        location: ccode.CirNode
        references = location.references_in()

        '''collect dependence via references in data'''
        next_nodes = set()
        if len(references) > 0:
            for depend_edge in source.get_ou_edges():
                depend_edge: cflow.CDependenceEdge
                if depend_edge.is_data_depend():
                    element = depend_edge.get_element()
                    element: cflow.CReferenceElement
                    if element.get_use() in references:
                        next_nodes.add(depend_edge.get_target())
                        self.nodes.add(depend_edge.get_target())
                        self.edges.add(depend_edge)
        return next_nodes

    def __direct_control_depend_by__(self, target: cflow.CDependenceNode):
        """
        :param target:
        :return: nodes directly depend on the target
        """
        next_nodes = set()
        for depend_edge in target.get_in_edges():
            depend_edge: cflow.CDependenceEdge
            if depend_edge.is_control_depend():
                next_nodes.add(depend_edge.get_source())
                self.nodes.add(depend_edge.get_source())
                self.edges.add(depend_edge)
        return next_nodes

    def __direct_data_depend_by__(self, target: cflow.CDependenceNode):
        """"
        :param target:
        :return: nodes directly depend on the target
        """
        next_nodes = set()
        for depend_edge in target.get_in_edges():
            depend_edge: cflow.CDependenceEdge
            if depend_edge.is_data_depend():
                next_nodes.add(depend_edge.get_source())
                self.nodes.add(depend_edge.get_source())
                self.edges.add(depend_edge)
        return next_nodes

    def __build_sub_graph__(self):
        """
        build the subgraph of dependence from nodes and edges collected
        :return:
        """
        graph = CDependenceSubGraph()
        for node in self.nodes:
            graph.nodes.add(node)
            graph.graph = node.graph
        for edge in self.edges:
            graph.edges.add(edge)
        return graph

    def control_depend_on_path(self, instance: cflow.CirInstanceNode, length: int):
        """
        CD(DD) --> CD(DD) --> ... --> CD(DD)
        :param length: length of the control dependence path
        :param instance:
        :return: CDependenceSubGraph
        """
        source = CDependenceBuilder.__dependence_node__(instance)
        self.nodes.clear()
        self.edges.clear()
        if source is not None:
            source: cflow.CDependenceNode
            # self.nodes.add(source)
            nodes = [source]
            while length > 0:
                length = length - 1
                next_nodes = set()
                for node in nodes:
                    node: cflow.CDependenceNode
                    iteration = self.__direct_control_depend_on__(node)
                    for next_node in iteration:
                        next_nodes.add(next_node)
                        self.__direct_data_depend_on__(next_node, None)
                nodes = next_nodes
        return self.__build_sub_graph__()

    def data_depend_on_path(self, instance: cflow.CirInstanceNode, location: ccode.CirNode, length: int):
        """
        DD --> DD --> DD --> ... --> DD
        :param location:
        :param instance:
        :param length:
        :return:
        """
        source = CDependenceBuilder.__dependence_node__(instance)
        self.nodes.clear()
        self.edges.clear()
        if source is not None:
            source: cflow.CDependenceNode
            # self.nodes.add(source)
            nodes = [source]
            while length > 0:
                length = length - 1
                next_nodes = set()
                for node in nodes:
                    node: cflow.CDependenceNode
                    iteration = self.__direct_data_depend_on__(node, location)
                    for next_node in iteration:
                        next_nodes.add(next_node)
                location = None
                nodes = next_nodes
        return self.__build_sub_graph__()

    @staticmethod
    def __definition__(location: ccode.CirNode):
        expression = location.top_expression_of()
        if expression is not None:
            parent = expression.get_parent()
            parent: ccode.CirNode
            if parent.is_assign_statement():
                return parent.get_children()[0]
            else:
                return expression
        else:
            return None

    def control_depend_by_path(self, instance: cflow.CirInstanceNode, length: int):
        """
        --> CD --> CD --> ... --> CD
        :param instance:
        :param length:
        :return:
        """
        target = CDependenceBuilder.__dependence_node__(instance)
        self.nodes.clear()
        self.edges.clear()
        if target is not None:
            target: cflow.CDependenceNode
            self.nodes.add(target)
            nodes = [target]
            while length > 0:
                length = length - 1
                next_nodes = set()
                for node in nodes:
                    node: cflow.CDependenceNode
                    iteration = self.__direct_control_depend_by__(node)
                    for next_node in iteration:
                        next_nodes.add(next_node)
                nodes = next_nodes
        return self.__build_sub_graph__()

    def data_depend_by_path(self, instance: cflow.CirInstanceNode, length: int):
        target = CDependenceBuilder.__dependence_node__(instance)
        self.nodes.clear()
        self.edges.clear()
        if target is not None:
            target: cflow.CDependenceNode
            self.nodes.add(target)
            nodes = [target]
            while length > 0:
                length = length - 1
                next_nodes = set()
                for node in nodes:
                    node: cflow.CDependenceNode
                    iteration = self.__direct_data_depend_by__(node)
                    for next_node in iteration:
                        next_nodes.add(next_node)
                nodes = next_nodes
        return self.__build_sub_graph__()

    def dependence_slice(self, assertion: cmutant.SemanticAssertion, length: int):
        """
        get the slice based on dependence graph for semantic assertion
        :param length:
        :param assertion:
        :return: {instance, dependence_graph} ==> instance_graph
        """
        instance_graphs = dict()
        for operand in assertion.get_operands():
            if isinstance(operand, ccode.CirNode):
                operand: ccode.CirNode
                instances = CDependenceBuilder.instances_of(operand)
                for instance in instances:
                    instance: cflow.CirInstanceNode
                    if assertion.is_constraint():
                        if operand.is_statement():
                            dependence_graph = self.control_depend_on_path(instance, length)
                        else:
                            dependence_graph = self.data_depend_on_path(instance, operand, length)
                    else:
                        if operand.is_statement():
                            dependence_graph = self.control_depend_by_path(instance, length)
                            dependence_graph2 = self.data_depend_by_path(instance, length)
                            dependence_graph = dependence_graph.union(dependence_graph2)
                        else:
                            statement = operand.statement_of()
                            if statement.is_assign_statement():
                                dependence_graph = self.data_depend_by_path(instance, length)
                            elif statement.cir_type == "CallStatement":
                                dependence_graph = self.data_depend_by_path(instance, length)
                            elif statement.is_condition_statement():
                                dependence_graph = self.control_depend_by_path(instance, length)
                            else:
                                dependence_graph = None
                    if dependence_graph is not None:
                        instance_graphs[instance] = dependence_graph
        return instance_graphs


def write_context_patterns(clusters: selection.SemanticFeatureClusters, file_path: str, length: int):
    """
    :param length:
    :param clusters:
    :param file_path:
    :return:
    """
    builder = CDependenceBuilder()
    with open(file_path, 'w') as writer:
        for key, cluster in clusters.clusters.items():
            cluster: selection.SemanticFeatureCluster
            writer.write("PATTERN#" + str(cluster.feature_vector) + " with " + str(cluster.get_total()) +
                         " mutants and " + str(cluster.get_alive()) + " equivalent ones by " +
                         str(cluster.get_probability()) + " confidence.\n")
            writer.write("WORDS: " + str(cluster.get_words(encoding.SemanticFeatureEncodeFunctions.
                                                           get_assertion_source_code)) + "\n")
            for assertion in cluster.assertions:
                assertion: cmutant.SemanticAssertion
                ''' 1. print the title of the pattern '''
                instance_graphs = builder.dependence_slice(assertion, length)
                writer.write("\t" + encoding.SemanticFeatureEncodeFunctions.
                             get_assertion_source_code(assertion) + "\n")
                ''' 2. select a random instance graph from the table '''
                index = random.randint(0, len(instance_graphs))
                instance_graph = None
                for instance, graph in instance_graphs.items():
                    graph: CDependenceSubGraph
                    instance_graph = graph.instance_graph()
                    if index <= 0:
                        break
                    else:
                        index = index - 1
                ''' 3. print the statements and flows in the graph '''
                if instance_graph is not None:
                    for node in instance_graph.nodes:
                        node: cflow.CirInstanceNode
                        statement = node.get_execution().get_statement()
                        statement: ccode.CirNode
                        writer.write("\t" + statement.generate_code() + "\n")
                    for edge in instance_graph.edges:
                        edge: cflow.CirInstanceEdge
                        writer.write("\t==> " + str(edge.get_source().get_execution()) +
                                     ", " + str(edge.get_target().get_execution()) + "\n")
            writer.write("\n")
    return

