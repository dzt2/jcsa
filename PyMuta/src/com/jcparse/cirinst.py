"""
It defines the model to describe the path in C program instance flow graph as:
    CirInstance, CirInstances
    CirInstanceCode
    CirInstanceNode
    CirInstanceEdge
    CirInstanceGraph
"""


import os
import src.com.jcparse.base as base
import src.com.jcparse.astree as astree
import src.com.jcparse.cirtree as cirtree
import src.com.jcparse.cirflow as cirflow


class CirInstances:
    """
    Set of instances w.r.t. set of source objects under different contexts.
    """

    def __init__(self):
        self.context_instances = dict()     # mapping from context to instances
        self.object_instances = dict()      # mapping from source object to its instances in different context
        return

    def get_context_set(self):
        return self.context_instances.keys()

    def get_source_object_set(self):
        return self.object_instances.keys()

    def get_instances_in_context(self, context: int):
        """
        :param context:
        :return: set of instances created in one context as given
        """
        if context not in self.context_instances:
            self.context_instances[context] = list()
        return self.context_instances[context]

    def get_contexts_of_object(self, source_object):
        """
        :param source_object:
        :return: contexts in which the instances of the source object are defined
        """
        instances = self.get_instances_of_object(source_object)
        contexts = set()
        for instance in instances:
            instance: CirInstance
            contexts.add(instance.get_context())
        return contexts

    def has_instances_of_object(self, source_object):
        if source_object in self.object_instances:
            return len(self.object_instances[source_object]) > 0
        else:
            return False

    def get_instances_of_object(self, source_object):
        """
        :param source_object:
        :return: set of instances w.r.t. the source object in different contexts
        """
        if source_object is None:
            return list()
        else:
            if source_object not in self.object_instances:
                self.object_instances[source_object] = list()
            return self.object_instances[source_object]

    def get_instance(self, context: int, source_object):
        instances = self.get_instances_of_object(source_object)
        for instance in instances:
            if instance.get_context() == context:
                return instance
        return None

    def add(self, instance):
        """
        add an instance to the space
        :param instance:
        :return: False if the instance has been added in the space or invalid
        """
        if isinstance(instance, CirInstance):
            instance: CirInstance
            context = instance.get_context()
            source_object = instance.get_source_object()
            source_object_list = self.get_instances_of_object(source_object)
            for old_instance in source_object_list:
                old_instance: CirInstance
                if old_instance.get_source_object() == source_object:
                    return False
            source_object_list.append(instance)
            context_list = self.get_instances_in_context(context)
            context_list.append(instance)
            return True
        else:
            return False


class CirInstance:
    """
    Instance of objects in C-intermediate representation as {context; source_object} in which:
        (1) context is int
        (2) source_object is CirNode            --> CirInstanceCode
        (3) source_object is CirExecution       --> CirInstanceNode
        (4) source_object is CirExecutionFlow   --> CirInstanceEdge
    """

    def __init__(self, instances: CirInstances, context: int, source_object):
        self.instances = instances
        self.context = context
        self.source_object = source_object
        if self.source_object is not None:
            self.instances.add(self)
        return

    def get_instances(self):
        """
        get the instances library
        :return:
        """
        return self.instances

    def get_context(self):
        """
        :return: integer ID
        """
        return self.context

    def get_source_object(self):
        """
        :return: source of the instance in static program flow graph
        """
        return self.source_object

    def __str__(self):
        if self.source_object is not None:
            return "<" + str(self.context) + ", " + str(self.source_object) + ">"
        else:
            return "<" + str(self.context) + ", None>"


class CirInstanceCode(CirInstance):
    """
    It represents the instance of a code element as CirNode in instance graph.
    {instance_node; context, cir_source_object; parent, children}
    """

    def __init__(self, instance_node, context: int, source_object: cirtree.CirNode):
        instance_node: CirInstanceNode
        super().__init__(instance_node.get_instances(), context, source_object)
        self.instance_node = instance_node
        self.parent = None
        self.children = list()
        return

    def get_instance_node(self):
        """
        :return: the instance of execution statement where the node belongs to
        """
        return self.instance_node

    def get_cir_source_node(self):
        self.source_object: cirtree.CirNode
        return self.source_object

    def get_parent(self):
        self.parent: CirInstanceCode
        return self.parent

    def get_children(self):
        return self.children

    def get_child(self, k: int):
        child = self.children[k]
        child: CirInstanceCode
        return child

    def add_child(self, child):
        child: CirInstanceCode
        child.parent = self
        self.children.append(child)
        return

    def get_root(self):
        """
        :return: instance of the cir-statement of the execution
        """
        instance = self
        while instance.parent is not None:
            instance = instance.parent
        return instance

    def get_leafs(self):
        """
        :return: leafs in the node of code instance
        """
        if len(self.children) == 0:
            return [self]
        else:
            leafs = list()
            for child in self.children:
                child: CirInstanceCode
                child_leafs = child.get_leafs()
                for child_leaf in child_leafs:
                    child_leaf: CirInstanceCode
                    leafs.append(child_leaf)
            return leafs


class CirInstanceNode(CirInstance):
    """
    The node in instance graph describes an execution instance of statement in the program analysis as:
        --- graph, id, in_edges, ou_edges
        --- context, execution{statement}
        --- code_instances{CirInstanceCode+} and root_instance
    """

    def __new_instance_code__(self, node: cirtree.CirNode):
        """
        create the instances for the code elements in the statement of execution.
        :param node:
        :return:
        """
        instance = CirInstanceCode(self, self.context, node)
        self.code_instances.append(instance)
        for child in node.children:
            child_instance = self.__new_instance_code__(child)
            instance.add_child(child_instance)
        return instance

    def __init__(self, graph, context: int, execution: cirflow.CirExecution):
        super().__init__(graph.get_instances(), context, execution)
        self.graph = graph
        self.in_edges = list()
        self.ou_edges = list()
        self.code_instances = list()
        self.id = None
        statement = execution.get_statement()
        self.root_code_instance = self.__new_instance_code__(statement)
        return

    def get_graph(self):
        self.graph: CirInstanceGraph
        return self.graph

    def get_id(self):
        return self.id

    def get_source_execution(self):
        """
        :return: the execution of statement is its source
        """
        self.source_object: cirflow.CirExecution
        return self.source_object

    def get_source_statement(self):
        """
        :return: the statement being executed as represented
        """
        self.source_object: cirflow.CirExecution
        return self.source_object.get_statement()

    def get_root_code_instance(self):
        """
        :return: root of the code instance in the execution of statement in instance graph
        """
        return self.root_code_instance

    def get_code_instances(self):
        """
        :return: set of instances of code elements in the execution of the statement under the same context.
        """
        return self.code_instances

    def get_code_instance(self, cir_node: cirtree.CirNode):
        """
        :param cir_node:
        :return: the instance of CIR node within the execution of the statement or None
        """
        for code_instance in self.code_instances:
            code_instance: CirInstanceCode
            if code_instance.get_cir_source_node() == cir_node:
                return code_instance
        return None

    def link_to(self, flow_type: cirflow.CirExecutionFlowType, target):
        """
        :param flow_type:
        :param target:
        :return: the instance of execution flow from source to target.
        """
        target: CirInstanceNode
        edge = CirInstanceEdge(flow_type, self, target)
        self.ou_edges.append(edge)
        target.in_edges.append(edge)
        return edge

    def get_in_edges(self):
        return self.in_edges

    def get_ou_edges(self):
        return self.ou_edges


class CirInstanceEdge(CirInstance):
    """
    edge between instance node describes the instance of execution flow as:
        --- {flow_type, source, target}
        --- {context, source_object{flow or None}}
    """

    def __init__(self, flow_type: cirflow.CirExecutionFlowType, source: CirInstanceNode, target: CirInstanceNode):
        self.flow_type = flow_type
        self.source = source
        self.target = target
        self.id = None
        # decide the flow and context
        if flow_type == cirflow.CirExecutionFlowType.return_flow:
            context = target.get_context()
        else:
            context = source.get_context()
        source_execution = source.get_source_execution()
        target_execution = target.get_source_execution()
        source_object = None
        for flow in source_execution.get_ou_flows():
            flow: cirflow.CirExecutionFlow
            if flow.get_target() == target_execution:
                source_object = flow
                break
        # construct the context-source object pair
        super().__init__(source.get_instances(), context, source_object)
        return

    def get_graph(self):
        return self.source.get_graph()

    def get_id(self):
        return self.id

    def get_flow_type(self):
        """
        :return: the flow type of the execution
        """
        return self.flow_type

    def get_source(self):
        """
        :return: from which the flow points
        """
        return self.source

    def get_target(self):
        """
        :return: to which this flow corresponds
        """
        return self.target

    def __str__(self):
        return str(self.flow_type) + "(" + str(self.source) + ", " + str(self.target) + ")"


class CirInstanceGraph:
    """
    It describes the flow graph on context-sensitive instances of execution as:
        function_call_graph as source
        instances {context | source_object ==> instance}
        nodes {id ==> instance_node}
        edges {id ==> instance_edge}
    """
    def __init__(self, function_call_graph: cirflow.CirFunctionCallGraph, instance_file: str):
        self.program = None
        self.function_call_graph = function_call_graph
        self.instances = CirInstances()
        self.nodes = dict()
        self.edges = dict()
        self.__parse__(instance_file)
        return

    def get_function_call_graph(self):
        return self.function_call_graph

    def get_cir_tree(self):
        return self.function_call_graph.get_cir_tree()

    def get_instances(self):
        """
        :return: instances library
        """
        return self.instances

    def get_nodes(self):
        return self.nodes.values()

    def get_edges(self):
        return self.edges.values()

    def get_node(self, id: int):
        """
        :param id:
        :return: get the node w.r.t. the integer ID
        """
        return self.nodes[id]

    def get_edge(self, id: int):
        """
        :param id:
        :return: get the edge w.r.t. the integer ID
        """
        return self.edges[id]

    def get_node_or_edge(self, id: int):
        if id in self.nodes:
            return self.nodes[id]
        elif id in self.edges:
            return self.edges[id]
        else:
            return None

    def get_contexts_of(self, execution: cirflow.CirExecution):
        """
        :param execution:
        :return: set of contexts in which the instances of execution are defined
        """
        return self.instances.get_contexts_of_object(execution)

    def get_nodes_of(self, execution: cirflow.CirExecution):
        """
        :param execution:
        :return: the instances of execution nodes in the graph for different context
        """
        return self.instances.get_instances_of_object(execution)

    def get_node_of(self, context: int, execution: cirflow.CirExecution):
        """
        :param context:
        :param execution:
        :return: instance of execution in specified context
        """
        nodes = self.instances.get_instances_of_object(execution)
        for node in nodes:
            node: CirInstanceNode
            if node.context == context:
                return node
        return None

    def __parse__(self, instance_file: str):
        # 1. create nodes in the graph
        self.nodes.clear()
        with open(instance_file, 'r') as reader:
            for line in reader:
                line = line.strip()
                if len(line) > 0:
                    items = line.split('\t')
                    if items[0].strip() == "[node]":
                        node_id = base.get_content_of(items[1].strip())
                        context = int(items[2].strip())
                        execution = self.function_call_graph.get_execution(items[3].strip())
                        node = CirInstanceNode(self, context, execution)
                        self.nodes[node_id] = node
                        node.id = node_id
        # 2. create edges in the graph
        self.edges.clear()
        with open(instance_file, 'r') as reader:
            for line in reader:
                line = line.strip()
                if len(line) > 0:
                    items = line.split('\t')
                    if items[0].strip() == "[edge]":
                        edge_id = base.get_content_of(items[1].strip())
                        flow_type = cirflow.CirExecutionFlowType.parse(items[2].strip())
                        source_id = base.get_content_of(items[3].strip())
                        target_id = base.get_content_of(items[4].strip())
                        source = self.nodes[source_id]
                        target = self.nodes[target_id]
                        source: CirInstanceNode
                        target: CirInstanceNode
                        edge = source.link_to(flow_type, target)
                        self.edges[edge_id] = edge
                        edge.id = edge_id
        return


if __name__ == "__main__":
    prefix = "C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data"
    for filename in os.listdir(prefix):
        directory = os.path.join(prefix, filename)
        source_file = os.path.join(directory, filename + ".c")
        ast_tree_file = os.path.join(directory, filename + ".ast")
        cir_tree_file = os.path.join(directory, filename + ".cir")
        exe_flow_file = os.path.join(directory, filename + ".flw")
        instance_file = os.path.join(directory, filename + ".ins")
        ast_tree = astree.AstTree(source_file, ast_tree_file)
        cir_tree = cirtree.CirTree(ast_tree, cir_tree_file)
        function_call_graph = cirflow.CirFunctionCallGraph(cir_tree, exe_flow_file)
        instance_graph = CirInstanceGraph(function_call_graph=function_call_graph, instance_file=instance_file)
        output_file = os.path.join("C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output\\instance_output", filename + ".ins")
        print("Open the abstract syntax tree and CIR-tree for", filename)
        with open(output_file, 'w') as writer:
            for function in function_call_graph.get_functions():
                function: cirflow.CirFunction
                contexts = instance_graph.get_contexts_of(function.get_execution_flow_graph().get_entry())
                for context in contexts:
                    writer.write("Function\t" + function.get_name() + "\t[" + str(context) + "]\n")
                    for k in range(0, function.flow_graph.number_of_executions()):
                        id = (k + 1) % function.flow_graph.number_of_executions()
                        execution = function.flow_graph.get_execution(id)
                        instance_node = instance_graph.get_node_of(context, execution)
                        if instance_node is not None:
                            instance_node: CirInstanceNode
                            # write information about the node and its output edges
                            writer.write("\t" + str(instance_node.get_id()) + "\t" +
                                         str(instance_node.get_source_execution()) + "\t\"" +
                                         instance_node.get_source_statement().generate_code(True) + "\"\n")
                            for instance_edge in instance_node.get_ou_edges():
                                instance_edge: CirInstanceEdge
                                writer.write("\t==>\t" + str(instance_edge.get_id()) + "\t")
                                writer.write(str(instance_edge.get_flow_type()) + "\t")
                                writer.write(str(instance_edge.get_source()) + "\t")
                                writer.write(str(instance_edge.get_target()) + "\n")
                            for code_instance in instance_node.get_code_instances():
                                code_instance: CirInstanceCode
                                writer.write("\t~~>\t" + code_instance.get_cir_source_node().generate_code(True) + "\n")
                            writer.write("\n")
                    writer.write("End Function\n\n")
    print("Testing end for all...")
