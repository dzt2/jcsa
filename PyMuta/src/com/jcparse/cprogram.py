"""
It defines the top-level model of C program, including source_code, astree, cirtree, function_call_graph
instance_graph, prev_dominance_graph, post_dominance_graph, and dependence_graph.
"""


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


if __name__ == "__main__":
    prefix = "C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data"
    for filename in os.listdir(prefix):
        directory = os.path.join(prefix, filename)
        source_file = os.path.join(directory, filename + ".c")
        ast_tree_file = os.path.join(directory, filename + ".ast")
        cir_tree_file = os.path.join(directory, filename + ".cir")
        exe_flow_file = os.path.join(directory, filename + ".flw")
        instance_file = os.path.join(directory, filename + ".ins")
        prev_dominance_file = os.path.join(directory, filename + ".pre")
        ast_tree = astree.AstTree(source_file, ast_tree_file)
        cir_tree = cirtree.CirTree(ast_tree, cir_tree_file)
        function_call_graph = cirflow.CirFunctionCallGraph(cir_tree, exe_flow_file)
        instance_graph = cirinst.CirInstanceGraph(function_call_graph=function_call_graph, instance_file=instance_file)
        prev_dominance_graph = CDominanceGraph(instance_graph, prev_dominance_file)
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
                            dominance_node = prev_dominance_graph.get_node(instance_node)
                            dominance_node: CDominanceNode
                            for dominated_node in dominance_node.get_ou_nodes():
                                dominated_node: CDominanceNode
                                if dominated_node.is_instance_node():
                                    writer.write("\t==>\t" + str(dominated_node.get_instance_node()) + "\n")
                                else:
                                    writer.write("\t==>\t" + str(dominated_node.get_instance_edge()) + "\n")
                    writer.write("End Function\n\n")
    print("Testing end for all...")