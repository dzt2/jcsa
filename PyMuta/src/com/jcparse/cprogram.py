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


class CInformationFlowType(Enum):
    """
    To describe the type of information flow in C program.
    """
    ''' A. information by control dependence '''
    execute_in_stmt = 0     # assign_statement | if_statement | case_statement | call_statement  --> leafs
    execute_in_true = 1     # if_statement.condition | case_statement.condition --> statement*
    execute_in_false = 2    # if_statement.condition | case_statement.condition --> statement*
    execute_in_call = 3     # call_statement.callee --> statement*
    execute_in_exit = 4     # wait_assign_statement --> statement*
    execute_in = 12         # execution --> statement

    ''' B. information by data dependence '''
    operand = 5             # operand --> parent as defer_expr, addr_expr, cast_expr, abbreviate compute_expr
    operand_ = 6            # operand_{k} --> compute_expr, initializer_body
    lr_assign = 7           # assign_statement.rvalue --> assign_statement.lvalue
    df_assign = 8           # definition --> usage_point*
    argument_ = 9           # expression_{k} --> parent as argument_list
    arguments = 10          # argument_list --> callee in call_function
    apply_callee = 11       # callee in call_statement --> wait_expr in wait_assign_statement


class CInformationFlow:
    """
    {flow_type, parameter}
    """
    def __init__(self, flow_type: CInformationFlowType, parameter: int):
        self.flow_type = flow_type
        self.parameter = parameter
        return

    def get_flow_type(self):
        return self.flow_type

    def get_parameter(self):
        return self.parameter

    def __str__(self):
        string = str(self.flow_type)
        if string.endswith('_'):
            return string + str(self.parameter)
        else:
            return string


class CInformationEdge:
    """
    {flow, source, target}
    """
    def __init__(self, source, target, flow: CInformationFlow):
        self.source = source
        self.target = target
        self.flow = flow
        return

    def get_source(self):
        """
        :return: source node from the edge points
        """
        return self.source

    def get_target(self):
        """
        :return: target node to the edge pointed
        """
        return self.target

    def get_flow(self):
        """
        :return: information flow that defines the edge
        """
        return self.flow


class CInformationNode:
    """
    {graph, instance, in_edges, ou_edges}
    """
    def __init__(self, graph, instance: cirinst.CirInstance):
        self.graph = graph
        self.instance = instance
        self.in_edges = list()
        self.ou_edges = list()
        return

    def get_graph(self):
        return self.graph

    def get_instance(self):
        return self.instance

    def get_in_edges(self):
        return self.in_edges

    def get_ou_edges(self):
        return self.ou_edges

    def link_to(self, target, flow_type: CInformationFlowType, parameter=0):
        """
        :param target:
        :param flow_type:
        :param parameter:
        :return: information flow edge in graph
        """
        flow = CInformationFlow(flow_type, parameter)
        edge = CInformationEdge(self, target, flow)
        self.ou_edges.append(edge)
        target.in_edges.append(edge)
        return edge


class CInformationFlowGraph:
    """
    information flow graph as {program, nodes}
    """
    def __init__(self, program, dep_file: str):
        self.program = program
        self.nodes = dict()
        self.__create_nodes__()
        return

    def get_nodes(self):
        return self.nodes.values()

    def get_instances(self):
        return self.nodes.keys()

    def get_program(self):
        self.program: CProgram
        return self.program

    def __create_nodes__(self):
        self.nodes.clear()
        instance_graph = program.get_instance_graph()
        for instance_node in instance_graph.get_nodes():
            instance_node: cirinst.CirInstanceNode
            self.nodes[instance_node] = CInformationNode(self, instance_node)
            for code_instance in instance_node.get_code_instances():
                self.nodes[code_instance] = CInformationNode(self, code_instance)
        return

    def __create_non_dependency__(self, instance: cirinst.CirInstance):
        """
        :return:
        """
        if isinstance(instance, cirinst.CirInstanceNode):
            instance: cirinst.CirInstanceNode
            if not instance.get_source_statement().is_tag_statement():
                source = self.nodes[instance]
                target = self.nodes[instance.get_root_code_instance()]
                source: CInformationNode
                target: CInformationNode
                source.link_to(target, CInformationFlowType.execute_in)
        else:
            instance: cirinst.CirInstanceCode
            cir_source_node = instance.get_cir_source_node()
            # TODO implement non-dependence information flow
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
        self.source_code.program = self
        self.ast_tree.program = self
        self.cir_tree.program = self
        self.function_call_graph.program = self
        self.instance_graph.program = self
        self.prev_dominance_graph.program = self
        self.post_dominance_graph.program = self
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


if __name__ == "__main__":
    prefix = "C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data"
    for filename in os.listdir(prefix):
        directory = os.path.join(prefix, filename)
        program = CProgram(directory)
        function_call_graph = program.get_function_call_graph()
        instance_graph = program.get_instance_graph()
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
                    writer.write("End Function\n\n")
    print("Testing end for all...")
