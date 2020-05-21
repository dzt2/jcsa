"""
It defines the top-level model of C program, including source_code, astree, cirtree, function_call_graph
instance_graph, prev_dominance_graph, post_dominance_graph, and dependence_graph.
"""


from enum import Enum
import os
import src.com.jcparse.base as base
import src.com.jcparse.astree as astree
import src.com.jcparse.cirtree as cirtree
import src.com.jcparse.cirflow as cirflow
import src.com.jcparse.cirinst as cirinst


class CDominanceNode:
    """
    Node in dominance graph as {graph, instance (CirInstanceNode|CirInstanceEdge), in_nodes, ou_nodes}
    """
    def __init__(self, graph, instance: cirinst.CirInstance):
        self.graph = graph
        self.instance = instance
        self.in_nodes = list()
        self.ou_nodes = list()
        return

    def get_graph(self):
        return self.graph

    def is_instance_node(self):
        return isinstance(self.instance, cirinst.CirInstanceNode)

    def is_instance_edge(self):
        return isinstance(self.instance, cirinst.CirInstanceEdge)

    def get_instance_node(self):
        if isinstance(self.instance, cirinst.CirInstanceNode):
            self.instance: cirinst.CirInstanceNode
            return self.instance
        else:
            return None

    def get_instance_edge(self):
        if isinstance(self.instance, cirinst.CirInstanceEdge):
            self.instance: cirinst.CirInstanceEdge
            return self.instance
        else:
            return None

    def get_in_nodes(self):
        """
        :return: the set of nodes that directly dominate this node
        """
        return self.in_nodes

    def get_ou_nodes(self):
        """
        :return: the set of nodes that are directly dominated by this node
        """
        return self.ou_nodes

    def dominate(self, target):
        """
        :param target: node that is directly dominated by this node (previous)
        :return:
        """
        target: CDominanceNode
        self.ou_nodes.append(target)
        target.in_nodes.append(self)
        return


class CDominanceGraph:
    def __init__(self, instance_graph: cirinst.CirInstanceGraph, dominance_file: str):
        self.program = None
        self.instance_graph = instance_graph
        self.nodes = dict()
        self.__parse__(dominance_file)
        return

    def get_instance_graph(self):
        return self.instance_graph

    def get_instances(self):
        return self.nodes.keys()

    def get_nodes(self):
        return self.nodes.values()

    def get_node(self, instance: cirinst.CirInstance):
        if instance in self.nodes:
            return self.nodes[instance]
        else:
            return None

    def __parse__(self, dominance_file: str):
        self.nodes.clear()
        with open(dominance_file, 'r') as reader:
            for line in reader:
                line = line.strip()
                if len(line) > 0:
                    items = line.split('\t')
                    instance = self.instance_graph.get_node_or_edge(base.get_content_of(items[0].strip()))
                    node = CDominanceNode(self, instance)
                    self.nodes[instance] = node
        with open(dominance_file, 'r') as reader:
            for line in reader:
                line = line.strip()
                if len(line) > 0:
                    items = line.split('\t')
                    source_instance = self.instance_graph.get_node_or_edge(base.get_content_of(items[0].strip()))
                    source = self.nodes[source_instance]
                    source: CDominanceNode
                    for i in range(1, len(items)):
                        target_instance = self.instance_graph.get_node_or_edge(base.get_content_of(items[i].strip()))
                        target = self.nodes[target_instance]
                        target: CDominanceNode
                        source.dominate(target)
        return


class CDependenceType(Enum):
    true_depend = 0
    false_depend = 1
    call_depend = 2
    exit_depend = 3
    use_define_depend = 4
    parameter_argument_depend = 5
    wait_return_depend = 6
    child_parent_depend = 7

    def __str__(self):
        return self.name

    @staticmethod
    def parse(text: str):
        if text == "predicate_depend::True":
            return CDependenceType.true_depend
        elif text == "predicate_depend::False":
            return CDependenceType.false_depend
        elif text == "stmt_exit_depend":
            return CDependenceType.exit_depend
        elif text == "stmt_call_depend":
            return CDependenceType.call_depend
        elif text == "use_defin_depend":
            return CDependenceType.use_define_depend
        elif text == "param_arg_depend":
            return CDependenceType.parameter_argument_depend
        elif text == "wait_retr_depend":
            return CDependenceType.wait_return_depend
        else:
            return None     # invalid case


class CDependenceEdge:
    """
    dependence relationship as {type, source, target}
    """
    def __init__(self, dependence_type: CDependenceType, source, target):
        self.dependence_type = dependence_type
        self.source = source
        self.target = target
        return

    def get_dependence_type(self):
        return self.dependence_type

    def get_source(self):
        """
        :return: node that depends on another via this relationship
        """
        return self.source

    def get_target(self):
        """
        :return: node that is depended by another via this relationship
        """
        return self.target

    def __str__(self):
        return str(self.dependence_type) + "::(" + str(self.source) + ", " + str(self.target) + ")"


class CDependenceNodeType(Enum):
    execution = 0
    condition = 1
    reference = 2
    expression = 3


class CDependenceNode:
    """
    node in dependence graph can be:
        (1) execution --> condition {true_depend or false_depend}
        (2) execution --> execution {call_depend or exit_depend}
        (3) reference --> reference {use_define_depend}
        (4) reference --> expression{parameter_argument_depend}
        (5) wait_expr --> ret_point {wait_return_depend}
    """
    def __init__(self, graph, instance: cirinst.CirInstance):
        self.graph = graph
        self.instance = instance
        self.in_edges = list()
        self.ou_edges = list()
        self.node_type = None
        if isinstance(instance, cirinst.CirInstanceNode):
            self.node_type = CDependenceNodeType.execution
        elif isinstance(instance, cirinst.CirInstanceCode):
            instance: cirinst.CirInstanceCode
            cir_node = instance.get_cir_source_node()
            if cir_node.is_expression():
                if cir_node.parent.get_cir_type() == cirtree.CirType.if_statement or \
                        cir_node.parent.get_cir_type() == cirtree.CirType.case_statement:
                    self.node_type = CDependenceNodeType.condition
                elif cir_node.is_reference_expression():
                    self.node_type = CDependenceNodeType.reference
                else:
                    self.node_type = CDependenceNodeType.expression
        return

    def get_graph(self):
        return self.graph

    def get_node_type(self):
        self.node_type: CDependenceNodeType
        return self.node_type

    def get_instance(self):
        return self.instance

    def get_in_edges(self):
        return self.in_edges

    def get_ou_edges(self):
        return self.ou_edges

    def depend_on(self, target, dependence_type: CDependenceType):
        target: CDependenceNode
        edge = CDependenceEdge(dependence_type, self, target)
        self.ou_edges.append(edge)
        target.in_edges.append(edge)
        return edge

    def __str__(self):
        if isinstance(self.instance, cirinst.CirInstanceNode):
            return str(self.instance)
        else:
            self.instance: cirinst.CirInstanceCode
            return "[" + str(self.instance.context) + ", " + \
                   str(self.instance.get_instance_node().get_source_execution()) + "::\"" + \
                   self.instance.get_cir_source_node().generate_code(True) + "\"]"


class CDependenceGraph:
    def __init__(self, instance_graph: cirinst.CirInstanceGraph, dependence_file: str):
        self.program = None
        self.instance_graph = instance_graph
        self.nodes = dict()
        self.__parse__(dependence_file)
        return

    def get_instance_graph(self):
        return self.instance_graph

    def get_instances(self):
        """
        :return: set of instances w.r.t. nodes in the graph
        """
        return self.nodes.keys()

    def get_nodes(self):
        return self.nodes.values()

    def get_node(self, instance: cirinst.CirInstance):
        node = self.nodes[instance]
        node: CDependenceNode
        return node

    def __elements_of__(self, text: str):
        """
        :param text:
        :return: [predicate|reference, cir_node, bool|cir_node]
        """
        elements = list()
        items = text.strip().split(' ')
        self.instance_graph: cirinst.CirInstanceGraph
        cir_tree = self.instance_graph.get_cir_tree()
        name = items[1].strip()
        if name == "predicate":
            elements.append(name)
            elements.append(cir_tree.get_node(base.get_content_of(items[2].strip())))
            elements.append(base.get_content_of(items[3].strip()))
        else:
            elements.append(name)
            elements.append(cir_tree.get_node(base.get_content_of(items[2].strip())))
            elements.append(cir_tree.get_node(base.get_content_of(items[3].strip())))
        return elements

    def __new_node__(self, instance: cirinst.CirInstance):
        """
        :param instance:
        :return: a newly created node or existing one w.r.t. the instance in graph
        """
        if instance is None:
            return None
        else:
            if instance not in self.nodes:
                node = CDependenceNode(self, instance)
                self.nodes[instance] = node
            return self.nodes[instance]

    def __build_nodes_in__(self, parent: CDependenceNode, instance_node: cirinst.CirInstanceNode,
                           cir_node: cirtree.CirNode):
        child = self.__new_node__(instance_node.get_cir_instance_of(cir_node))
        child: CDependenceNode
        child.depend_on(parent, CDependenceType.child_parent_depend)
        for cir_child in cir_node.get_children():
            self.__build_nodes_in__(child, instance_node, cir_child)
        return

    def __build_nodes__(self, instance_node: cirinst.CirInstanceNode):
        parent = self.__new_node__(instance_node)
        self.__build_nodes_in__(parent, instance_node, instance_node.get_source_statement())
        return

    def __parse__(self, dependence_file: str):
        self.nodes.clear()
        ''' create nodes and child-parent dependence edges '''
        with open(dependence_file, 'r') as reader:
            for line in reader:
                line = line.strip()
                if len(line) > 0:
                    items = line.split('\t')
                    if items[0].strip() == "[node]":
                        instance_node = self.instance_graph.get_node(base.get_content_of(items[1].strip()))
                        instance_node: cirinst.CirInstanceNode
                        self.__build_nodes__(instance_node)
        ''' create edges between statements instances '''
        with open(dependence_file, 'r') as reader:
            for line in reader:
                line = line.strip()
                if len(line) > 0:
                    items = line.split('\t')
                    if items[0].strip() == "[edge]":
                        source_instance = self.instance_graph.get_node(base.get_content_of(items[1].strip()))
                        target_instance = self.instance_graph.get_node(base.get_content_of(items[2].strip()))
                        source_instance: cirinst.CirInstanceNode
                        target_instance: cirinst.CirInstanceNode
                        if len(items) < 5:
                            dependence_type = CDependenceType.parse(items[3].strip())
                        else:
                            elements = self.__elements_of__(items[4].strip())
                            if elements[0] == "predicate":
                                target_instance = target_instance.get_cir_instance_of(elements[1])
                                if elements[2]:
                                    dependence_type = CDependenceType.true_depend
                                else:
                                    dependence_type = CDependenceType.false_depend
                            else:
                                source_instance = source_instance.get_cir_instance_of(elements[2])
                                target_instance = target_instance.get_cir_instance_of(elements[1])
                                dependence_type = CDependenceType.parse(items[3].strip())
                        source = self.nodes[source_instance]
                        target = self.nodes[target_instance]
                        source: CDependenceNode
                        target: CDependenceNode
                        source.depend_on(target, dependence_type)
        return


class CProgram:
    """
    CProgram defines information of program under test, including:
        source_code
        ast_tree
        cir_tree
        function_call_graph
        instance_graph
        prev_dominance_graph
        post_dominance_graph
        dependence_graph
    """

    def __init__(self, directory_path: str):
        """
        :param directory_path: xxx.c, xxx.ast, xxx.cir, xxx.flw, xxx.ins, xxx.pre, xxx.pos and xxx.dep
        """
        filename = os.path.basename(directory_path)
        self.directory = directory_path
        self.filename = filename
        self.ast_tree = astree.AstTree(os.path.join(directory_path, filename + ".c"),
                                       os.path.join(directory_path, filename + ".ast"))
        self.source_code = self.ast_tree.get_source_code()
        self.cir_tree = cirtree.CirTree(self.ast_tree, os.path.join(directory_path, filename + ".cir"))
        self.function_call_graph = cirflow.CirFunctionCallGraph(self.cir_tree,
                                                                os.path.join(directory_path, filename + ".flw"))
        self.instance_graph = cirinst.CirInstanceGraph(self.function_call_graph,
                                                       os.path.join(directory_path, filename + ".ins"))
        self.prev_dominance_graph = CDominanceGraph(self.instance_graph,
                                                    os.path.join(directory_path, filename + ".pre"))
        self.post_dominance_graph = CDominanceGraph(self.instance_graph,
                                                    os.path.join(directory_path, filename + ".pos"))
        self.dependence_graph = CDependenceGraph(self.instance_graph, os.path.join(directory_path, filename + ".dep"))
        self.source_code.program = self
        self.ast_tree.program = self
        self.cir_tree.program = self
        self.function_call_graph.program = self
        self.instance_graph.program = self
        self.prev_dominance_graph.program = self
        self.post_dominance_graph.program = self
        self.dependence_graph.program = self
        return

    def get_directory(self):
        return self.directory

    def get_file_name(self):
        return self.filename

    def get_source_code(self):
        return self.source_code

    def get_ast_tree(self):
        return self.ast_tree

    def get_cir_tree(self):
        return self.cir_tree

    def get_function_call_graph(self):
        return self.function_call_graph

    def get_instance_graph(self):
        return self.instance_graph

    def get_prev_dominance_graph(self):
        return self.prev_dominance_graph

    def get_post_dominance_graph(self):
        return self.post_dominance_graph

    def get_dependence_graph(self):
        return self.dependence_graph


if __name__ == "__main__":
    prefix = "C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data"
    for filename in os.listdir(prefix):
        directory = os.path.join(prefix, filename)
        program = CProgram(directory)
        function_call_graph = program.get_function_call_graph()
        instance_graph = program.get_instance_graph()
        dependence_graph = program.get_dependence_graph()
        output_file = os.path.join("C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output", filename + ".ast")
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
                            instance_node: cirinst.CirInstanceNode
                            # write information about the node and its output edges
                            writer.write("\t" + str(instance_node.get_source_execution()) + "\t\"" +
                                         instance_node.get_source_statement().generate_code(True) + "\"\n")
                            root = dependence_graph.get_node(instance_node)
                            writer.write("\t\t" + str(root) + "\n")
                            for edge in root.get_ou_edges():
                                edge: CDependenceEdge
                                writer.write("\t\t==>\t" + str(edge) + "\n")
                            for instance_code in instance_node.get_cir_instances_in():
                                node = dependence_graph.get_node(instance_code)
                                writer.write("\t\t" + str(node) + "\n")
                                for edge in node.get_ou_edges():
                                    edge: CDependenceEdge
                                    writer.write("\t\t==>\t" + str(edge) + "\n")
                    writer.write("End Function\n\n")
    print("Testing end for all...")