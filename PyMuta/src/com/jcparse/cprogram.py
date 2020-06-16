"""
It defines the top-level model of C program, including source_code, astree, cirtree, function_call_graph
instance_graph, prev_dominance_graph, post_dominance_graph, and dependence_graph.
"""
from collections import deque
from enum import Enum
import os
from typing.io import IO, TextIO
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

    def has_node(self, instance: cirinst.CirInstance):
        return instance in self.nodes

    def get_node(self, instance: cirinst.CirInstance):
        if instance in self.nodes:
            node = self.nodes[instance]
            node: CDominanceNode
            return node
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


class CDominancePath:
    """
    --<prev_edges>-- mid --<post_edges>--
    """
    def __init__(self, mid_node: cirinst.CirInstanceNode):
        self.beg_node = None
        self.prev_edges = list()
        self.mid_node = mid_node
        self.post_edges = list()
        self.end_node = None
        self.__build__()
        return

    def __build__(self):
        program = self.mid_node.get_graph().program
        program: CProgram
        prev_dominance_graph = program.get_prev_dominance_graph()
        post_dominance_graph = program.get_post_dominance_graph()
        # build prev-path
        self.beg_node = self.mid_node
        self.prev_edges.clear()
        if prev_dominance_graph.has_node(self.mid_node):
            dominance_node = prev_dominance_graph.get_node(self.mid_node)
            while dominance_node is not None:
                # update edges and node
                if dominance_node.is_instance_edge():
                    edge = dominance_node.get_instance_edge()
                    if edge.get_flow_type() == cirflow.CirExecutionFlowType.true_flow or \
                            edge.get_flow_type() == cirflow.CirExecutionFlowType.false_flow or \
                            edge.get_flow_type() == cirflow.CirExecutionFlowType.call_flow or \
                            edge.get_flow_type() == cirflow.CirExecutionFlowType.return_flow:
                        self.prev_edges.append(edge)
                else:
                    self.beg_node = dominance_node.get_instance_node()
                # update dominance node
                if len(dominance_node.in_nodes) > 0:
                    dominance_node = dominance_node.in_nodes[0]
                else:
                    dominance_node = None
        self.prev_edges.reverse()
        # build post-path
        self.end_node = self.mid_node
        self.post_edges.clear()
        if post_dominance_graph.has_node(self.mid_node):
            dominance_node = post_dominance_graph.get_node(self.mid_node)
            while dominance_node is not None:
                # update edges and node
                if dominance_node.is_instance_edge():
                    edge = dominance_node.get_instance_edge()
                    if edge.get_flow_type() == cirflow.CirExecutionFlowType.true_flow or \
                            edge.get_flow_type() == cirflow.CirExecutionFlowType.false_flow or \
                            edge.get_flow_type() == cirflow.CirExecutionFlowType.call_flow or \
                            edge.get_flow_type() == cirflow.CirExecutionFlowType.return_flow:
                        self.post_edges.append(edge)
                else:
                    self.end_node = dominance_node.get_instance_node()
                # update dominance node
                if len(dominance_node.ou_nodes) > 0:
                    dominance_node = dominance_node.ou_nodes[0]
                else:
                    dominance_node = None
        # end of all
        return

    def get_beg_node(self):
        self.beg_node: cirinst.CirInstanceNode
        return self.beg_node

    def get_mid_node(self):
        return self.mid_node

    def get_end_node(self):
        self.end_node: cirinst.CirInstanceNode
        return self.end_node

    def get_prev_edges(self):
        return self.prev_edges

    def get_post_edges(self):
        return self.post_edges

    @staticmethod
    def dominance_paths(cir_location: cirtree.CirNode):
        """
        all the dominance paths through the statement of location in cir-source code
        :param cir_location:
        :return:
        """
        statement = cir_location.statement_of()
        program = cir_location.get_tree().program
        program: CProgram
        instance_graph = program.get_instance_graph()
        paths = set()
        if instance_graph.get_instances().has_instances_of_object(statement):
            code_instances = instance_graph.get_instances().get_instances_of_object(statement)
            for code_instance in code_instances:
                code_instance: cirinst.CirInstanceCode
                path = CDominancePath(code_instance.get_instance_node())
                paths.add(path)
        return paths


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
    execute_in = 5          # execution --> statement

    ''' B. information by data dependence '''
    operand = 6             # operand --> parent as defer_expr, addr_expr, cast_expr, abbreviate compute_expr
    operand_ = 7            # operand_{k} --> compute_expr, initializer_body
    lr_assign = 8           # assign_statement.rvalue --> assign_statement.lvalue
    du_assign = 9           # definition --> usage_point*
    argument_ = 10          # expression_{k} --> parent as argument_list
    arguments = 11          # argument_list --> callee in call_function
    apply_callee = 11       # callee in call_statement --> wait_expr in wait_assign_statement
    cast_type = 12          # type --> cast_expression

    def __str__(self):
        return self.name


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
        self.source: CInformationNode
        return self.source

    def get_target(self):
        """
        :return: target node to the edge pointed
        """
        self.target: CInformationNode
        return self.target

    def get_flow(self):
        """
        :return: information flow that defines the edge
        """
        self.flow: CInformationFlow
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


class CInformationGraph:
    """
    information flow graph as {program, nodes}
    """
    def __init__(self, program, dep_file: str):
        self.program = program
        self.nodes = dict()
        self.__create_nodes__()
        self.__create_non_dependencies__()
        self.__create_dependency__(dep_file)
        return

    def get_nodes(self):
        return self.nodes.values()

    def get_instances(self):
        return self.nodes.keys()

    def has_node(self, instance):
        return instance in self.nodes

    def get_program(self):
        self.program: CProgram
        return self.program

    def get_node(self, instance: cirinst.CirInstance):
        node = self.nodes[instance]
        node: CInformationNode
        return node

    def __create_nodes__(self):
        self.nodes.clear()
        instance_graph = self.program.get_instance_graph()
        for instance_node in instance_graph.get_nodes():
            instance_node: cirinst.CirInstanceNode
            self.nodes[instance_node] = CInformationNode(self, instance_node)
            for code_instance in instance_node.get_code_instances():
                self.nodes[code_instance] = CInformationNode(self, code_instance)
        return

    def __wait_expression__(self, call_statement: cirtree.CirNode):
        self.program: CProgram
        fun = self.program.get_function_call_graph().get_function_of(call_statement)
        call_execution = fun.get_execution_flow_graph().get_execution_of(call_statement)
        wait_execution = fun.get_execution_flow_graph().get_execution(call_execution.get_id() + 1)
        wait_statement = wait_execution.get_statement()
        return wait_statement.get_child(1)

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
            # get the cir source node and its parent
            instance: cirinst.CirInstanceCode
            cir_node = instance.get_cir_source_node()
            cir_parent = cir_node.get_parent()
            cir_parent: cirtree.CirNode
            # A. when cir_source is the statement   ==> execute_in_stmt
            if instance.get_parent() is None:
                # collect the node to be connected as leafs in the statement
                if cir_node.is_assign_statement():
                    leafs = instance.get_leafs()
                elif cir_node.get_cir_type() == cirtree.CirType.if_statement or \
                        cir_node.get_cir_type() == cirtree.CirType.case_statement:
                    leafs = instance.get_child(0).get_leafs()
                elif cir_node.get_cir_type() == cirtree.CirType.call_statement:
                    leafs = instance.get_child(1).get_leafs()
                else:
                    leafs = list()
                # statement ====> leafs in expression [execute_in_stmt]
                source = self.nodes[instance]
                source: CInformationNode
                for leaf in leafs:
                    target = self.nodes[leaf]
                    source.link_to(target, CInformationFlowType.execute_in_stmt)
            elif cir_parent.cir_type == cirtree.CirType.defer_expression or cir_parent.cir_type == \
                    cirtree.CirType.address_expression or cir_parent.cir_type == cirtree.CirType:
                # child ----{operand}----> parent
                parent_instance = instance.get_parent()
                source = self.nodes[instance]
                target = self.nodes[parent_instance]
                source: CInformationNode
                source.link_to(target, CInformationFlowType.operand)
            elif cir_parent.cir_type == cirtree.CirType.cast_expression:
                # child ----{cast_type or operand}----> parent
                parent_instance = instance.get_parent()
                source = self.nodes[instance]
                target = self.nodes[parent_instance]
                source: CInformationNode
                if cir_parent.get_child(0) == cir_node:
                    source.link_to(target, CInformationFlowType.cast_type)
                else:
                    source.link_to(target, CInformationFlowType.operand)
            elif cir_parent.cir_type == cirtree.CirType.field_expression:
                # child ----{body or field}----> parent
                parent_instance = instance.get_parent()
                source = self.nodes[instance]
                target = self.nodes[parent_instance]
                source: CInformationNode
                if cir_parent.get_child(0) == cir_node:
                    source.link_to(target, CInformationFlowType.operand_, 0)
                else:
                    source.link_to(target, CInformationFlowType.operand_, 1)
            elif cir_parent.is_computational_expression():
                parent_instance = instance.get_parent()
                source = self.nodes[instance]
                target = self.nodes[parent_instance]
                source: CInformationNode
                if len(cir_parent.get_children()) == 2:
                    if cir_node == cir_parent.get_child(0):
                        source.link_to(target, CInformationFlowType.operand_, 0)
                    else:
                        source.link_to(target, CInformationFlowType.operand_, 1)
                else:
                    source.link_to(target, CInformationFlowType.operand)
            elif cir_parent.is_assign_statement() and cir_node == cir_parent.get_child(1):
                parent_instance = instance.get_parent()
                source = self.nodes[parent_instance.get_child(1)]
                target = self.nodes[parent_instance.get_child(0)]
                source: CInformationNode
                source.link_to(target, CInformationFlowType.lr_assign)
            elif cir_parent.cir_type == cirtree.CirType.argument_list:
                parent_instance = instance.get_parent()
                source = self.nodes[instance]
                target = self.nodes[parent_instance]
                source: CInformationNode
                for k in range(0, len(cir_parent.children)):
                    if cir_parent.get_child(k) == cir_node:
                        source.link_to(target, CInformationFlowType.argument_, k)
            elif cir_parent.cir_type == cirtree.CirType.call_statement and cir_parent.get_child(1) == cir_node:
                parent_instance = instance.get_parent()
                source = self.nodes[parent_instance.get_child(1)]
                target = self.nodes[parent_instance.get_child(0)]
                source: CInformationNode
                source.link_to(target, CInformationFlowType.arguments)
            elif cir_parent.cir_type == cirtree.CirType.call_statement and cir_parent.get_child(0) == cir_node:
                call_statement = cir_parent
                wait_expression = self.__wait_expression__(call_statement)
                self.program: CProgram
                target_instances = self.program.get_instance_graph().\
                    get_instances().get_instances_of_object(wait_expression)
                for target_instance in target_instances:
                    target_instance: cirinst.CirInstanceCode
                    if instance.get_context() == target_instance.get_context():
                        source = self.nodes[instance]
                        target = self.nodes[target_instance]
                        source: CInformationNode
                        source.link_to(target, CInformationFlowType.apply_callee)
        return

    def __create_non_dependencies__(self):
        for instance in self.nodes.keys():
            self.__create_non_dependency__(instance)
        return

    def __create_dependency__(self, dep_file: str):
        self.program: CProgram
        instance_graph = self.program.get_instance_graph()
        with open(dep_file, 'r') as reader:
            for line in reader:
                line = line.strip()
                if len(line) > 0:
                    items = line.split('\t')
                    if items[0].strip() == "[node]":
                        source_instance = instance_graph.get_node(base.get_content_of(items[1].strip()))
                    elif items[0].strip() == "[edge]":
                        target_instance = instance_graph.get_node(base.get_content_of(items[2].strip()))
                        source_instance: cirinst.CirInstanceNode
                        target_instance: cirinst.CirInstanceNode
                        dep_type = items[3].strip()
                        if dep_type == "predicate_depend":
                            condition_instance = target_instance.get_root_code_instance().get_child(0)
                            source = self.nodes[condition_instance]
                            source: CInformationNode
                            target = self.nodes[source_instance]
                            if "true" in items[4].strip():
                                source.link_to(target, CInformationFlowType.execute_in_true)
                            else:
                                source.link_to(target, CInformationFlowType.execute_in_false)
                        elif dep_type == "stmt_exit_depend":
                            source = self.nodes[target_instance]
                            target = self.nodes[source_instance]
                            source: CInformationNode
                            source.link_to(target, CInformationFlowType.execute_in_exit)
                        elif dep_type == "stmt_call_depend":
                            source = self.nodes[target_instance]
                            target = self.nodes[source_instance]
                            source: CInformationNode
                            source.link_to(target, CInformationFlowType.execute_in_exit)
                        else:
                            elements = items[4].strip().split(' ')
                            def_cir_node = self.program.get_cir_tree().\
                                get_node(base.get_content_of(elements[2].strip()))
                            use_cir_node = self.program.get_cir_tree().\
                                get_node(base.get_content_of(elements[3].strip()))
                            def_instance = instance_graph.get_instances().\
                                get_instance(target_instance.get_context(), def_cir_node)
                            use_instance = instance_graph.get_instances().\
                                get_instance(source_instance.get_context(), use_cir_node)
                            if def_instance is not None and use_instance is not None:
                                source = self.nodes[def_instance]
                                target = self.nodes[use_instance]
                                source: CInformationNode
                                source.link_to(target, CInformationFlowType.du_assign)


class CDependenceType(Enum):
    """
        To describe the type of information flow in C program.
        """
    ''' A. information by control dependence '''
    execute_by_stmt = 0     # assign_statement | if_statement | case_statement | call_statement  <-- leafs
    execute_by_true = 1     # if_statement.condition | case_statement.condition <-- statement*
    execute_by_false = 2    # if_statement.condition | case_statement.condition <-- statement*
    execute_by_call = 3     # call_statement.callee <-- statement*
    execute_by_exit = 4     # wait_assign_statement <-- statement*
    execute_by = 5          # execution <-- statement

    ''' B. information by data dependence '''
    operand = 6  # operand --> parent as defer_expr, addr_expr, cast_expr, abbreviate compute_expr
    operand_ = 7  # operand_{k} --> compute_expr, initializer_body
    lr_assign_by = 8  # assign_statement.rvalue --> assign_statement.lvalue
    du_assign_by = 9  # definition --> usage_point*
    argument_ = 10  # expression_{k} --> parent as argument_list
    arguments = 11  # argument_list --> callee in call_function
    apply_callee_by = 11  # callee in call_statement --> wait_expr in wait_assign_statement
    cast_type = 12  # type --> cast_expression

    def __str__(self):
        return self.name


class CDependenceFlow:
    """
    {dep_type, parameter}
    """
    def __init__(self, dep_type: CDependenceType, parameter: int):
        self.dep_type = dep_type
        self.parameter = parameter
        return

    def get_dependence_type(self):
        return self.dep_type

    def get_parameter(self):
        return self.parameter

    def __str__(self):
        string = str(self.dep_type)
        if string.endswith('_'):
            return string + str(self.parameter)
        else:
            return string


class CDependenceEdge:
    """
    {flow: source, target}
    """
    def __init__(self, source, target, flow: CDependenceFlow):
        self.source = source
        self.target = target
        self.flow = flow
        return

    def get_source(self):
        """
        :return: source node from the edge points
        """
        self.source: CDependenceNode
        return self.source

    def get_target(self):
        """
        :return: target node to the edge pointed
        """
        self.target: CDependenceNode
        return self.target

    def get_flow(self):
        """
        :return: information flow that defines the edge
        """
        self.flow: CDependenceFlow
        return self.flow


class CDependenceNode:
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

    def link_to(self, target, dep_type: CDependenceType, parameter=0):
        """
        :param target:
        :param dep_type:
        :param parameter:
        :return: information flow edge in graph
        """
        flow = CDependenceFlow(dep_type, parameter)
        edge = CDependenceEdge(self, target, flow)
        self.ou_edges.append(edge)
        target.in_edges.append(edge)
        return edge


class CDependenceGraph:
    def __init__(self, program):
        program: CProgram
        self.program = program
        self.nodes = dict()
        self.__build__()
        return

    def get_nodes(self):
        return self.nodes.values()

    def get_instances(self):
        return self.nodes.keys()

    def get_program(self):
        self.program: CProgram
        return self.program

    def get_node(self, instance: cirinst.CirInstance):
        node = self.nodes[instance]
        node: CDependenceNode
        return node

    def has_node(self, instance):
        return instance in self.nodes

    def __build__(self):
        self.nodes.clear()
        for information_node in self.program.get_information_graph().get_nodes():
            information_node: CInformationNode
            instance = information_node.get_instance()
            self.nodes[instance] = CDependenceNode(self, instance)
        for information_node in self.program.get_information_graph().get_nodes():
            information_node: CInformationNode
            source = self.nodes[information_node.get_instance()]
            source: CDependenceNode
            for information_edge in information_node.get_ou_edges():
                information_edge: CInformationEdge
                target = self.nodes[information_edge.get_target().get_instance()]
                information_flow = information_edge.get_flow()
                information_type = information_flow.get_flow_type()
                if information_type == CInformationFlowType.execute_in:
                    dependence_type = CDependenceType.execute_by
                elif information_type == CInformationFlowType.execute_in_stmt:
                    dependence_type = CDependenceType.execute_by_stmt
                elif information_type == CInformationFlowType.execute_in_true:
                    dependence_type = CDependenceType.execute_by_true
                elif information_type == CInformationFlowType.execute_in_false:
                    dependence_type = CDependenceType.execute_by_false
                elif information_type == CInformationFlowType.execute_in_call:
                    dependence_type = CDependenceType.execute_by_call
                elif information_type == CInformationFlowType.execute_in_exit:
                    dependence_type = CDependenceType.execute_by_exit
                elif information_type == CInformationFlowType.operand:
                    dependence_type = CDependenceType.operand
                elif information_type == CInformationFlowType.operand_:
                    dependence_type = CDependenceType.operand_
                elif information_type == CInformationFlowType.argument_:
                    dependence_type = CDependenceType.argument_
                elif information_type == CInformationFlowType.arguments:
                    dependence_type = CDependenceType.arguments
                elif information_type == CInformationFlowType.cast_type:
                    dependence_type = CDependenceType.cast_type
                elif information_type == CInformationFlowType.apply_callee:
                    dependence_type = CDependenceType.apply_callee_by
                elif information_type == CInformationFlowType.lr_assign:
                    dependence_type = CDependenceType.lr_assign_by
                elif information_type == CInformationFlowType.du_assign:
                    dependence_type = CDependenceType.du_assign_by
                else:
                    dependence_type = None
                target.link_to(source, dependence_type, information_flow.get_parameter())
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
        self.information_graph = CInformationGraph(self, os.path.join(directory_path, filename + ".dep"))
        self.dependence_graph = CDependenceGraph(self)
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

    def get_information_graph(self):
        self.information_graph: CInformationGraph
        return self.information_graph

    def get_dependence_graph(self):
        return self.dependence_graph


def __output_information_node__(graph: CInformationGraph, instance: cirinst.CirInstance, writer: TextIO):
    node = graph.get_node(instance)
    if isinstance(instance, cirinst.CirInstanceNode):
        instance: cirinst.CirInstanceNode
        writer.write("[node]\t" + str(instance.get_source_execution()) + "\tin\t" + str(instance.get_context()) + "\n")
    else:
        instance: cirinst.CirInstanceCode
        writer.write("[code]\t" + instance.get_cir_source_node().generate_code(True) +
                     "\tat\t" + str(instance.get_instance_node().get_source_execution()) +
                     "\tin\t" + str(instance.get_context()) + "\n")
    for edge in node.get_ou_edges():
        edge: CInformationEdge
        writer.write("==>\t" + str(edge.get_flow()) + "\t")
        instance = edge.get_target().get_instance()
        if isinstance(instance, cirinst.CirInstanceNode):
            instance: cirinst.CirInstanceNode
            writer.write(str(instance.get_source_execution()) + "\tin\t" + str(instance.get_context()) + "\n")
        else:
            instance: cirinst.CirInstanceCode
            writer.write(instance.get_cir_source_node().generate_code(True) +
                         "\tat\t" + str(instance.get_instance_node().get_source_execution()) +
                         "\tin\t" + str(instance.get_context()) + "\n")
    return


def __output_dependence_node__(graph: CDependenceGraph, instance: cirinst.CirInstance, writer: TextIO):
    node = graph.get_node(instance)
    if isinstance(instance, cirinst.CirInstanceNode):
        instance: cirinst.CirInstanceNode
        writer.write("[node]\t" + str(instance.get_source_execution()) + "\tin\t" + str(instance.get_context()) + "\n")
    else:
        instance: cirinst.CirInstanceCode
        writer.write("[code]\t" + instance.get_cir_source_node().generate_code(True) +
                     "\tat\t" + str(instance.get_instance_node().get_source_execution()) +
                     "\tin\t" + str(instance.get_context()) + "\n")
    for edge in node.get_ou_edges():
        edge: CDependenceEdge
        writer.write("==>\t" + str(edge.get_flow()) + "\t")
        instance = edge.get_target().get_instance()
        if isinstance(instance, cirinst.CirInstanceNode):
            instance: cirinst.CirInstanceNode
            writer.write(str(instance.get_source_execution()) + "\tin\t" + str(instance.get_context()) + "\n")
        else:
            instance: cirinst.CirInstanceCode
            writer.write(instance.get_cir_source_node().generate_code(True) +
                         "\tat\t" + str(instance.get_instance_node().get_source_execution()) +
                         "\tin\t" + str(instance.get_context()) + "\n")
    return


class CInformationPath:
    """
    [source; edges; target]
    """
    def __init__(self, source: CInformationNode):
        """
        :param source: empty path starting from source
        """
        self.source = source
        self.target = source
        self.edges = list()
        return

    def get_source(self):
        """
        :return: beginning of the path
        """
        return self.source

    def get_target(self):
        """
        :return: final point of the path
        """
        return self.target

    def get_edges(self):
        """
        :return: information flow edges in the path
        """
        return self.edges

    def get_edge(self, k: int):
        """
        :param k:
        :return: the kth edge in the information path
        """
        edge = self.edges[k]
        edge: CInformationEdge
        return edge

    def add_edge(self, edge: CInformationEdge):
        if edge.get_source() == self.target:
            self.edges.append(edge)
            self.target = edge.get_target()
        return


class CInformationTreeNode:
    """
    [tree; parent, children; information_node, information_edge]
    """
    def __init__(self, tree, information_node: CInformationNode, information_edge=None):
        self.tree = tree
        self.information_node = information_node
        self.information_edge = information_edge
        self.parent = None
        self.children = list()
        return

    def is_leaf(self):
        return len(self.children) == 0

    def is_root(self):
        return self.parent is None

    def get_tree(self):
        self.tree: CInformationTree
        return self.tree

    def get_information_node(self):
        return self.information_node

    def get_information_edge(self):
        self.information_edge: CInformationEdge
        return self.information_edge

    def get_parent(self):
        self.parent: CInformationTreeNode
        return self.parent

    def get_children(self):
        return self.children

    def get_child(self, k: int):
        child = self.children[k]
        child: CInformationTreeNode
        return child

    def add_child(self, information_edge: CInformationEdge):
        """
        :param information_edge:
        :return: None if the information edge's source does not match the parent
        """
        if information_edge.get_source() == self.information_node:
            for child in self.children:
                child: CInformationTreeNode
                if child.information_edge == information_edge:
                    return child
            child = CInformationTreeNode(self.tree, information_edge.get_target(), information_edge)
            child.parent = self
            self.children.append(child)
            return child
        else:
            return None

    def get_path(self):
        """
        :return: path from root to this node in the tree
        """
        path = list()
        tree_node = self
        while not tree_node.is_root():
            path.append(tree_node.get_information_edge())
            tree_node = tree_node.get_parent()
        path.reverse()
        information_path = CInformationPath(tree_node.get_information_node())
        for information_edge in path:
            information_path.add_edge(information_edge)
        return information_path

    def get_path_edges(self):
        """
        :return: edges from the node to its root
        """
        tree_node = self
        edges = list()
        while not tree_node.is_root():
            edges.append(tree_node.get_information_edge())
            tree_node = tree_node.get_parent()
        return edges


class CInformationTree:
    """
    [root]
    """
    def __init__(self, information_root: CInformationNode, max_depth: int):
        self.root = CInformationTreeNode(self, information_root)
        self.__extend__(self.root, max_depth)
        return

    def __extend__(self, tree_node: CInformationTreeNode, max_depth: int):
        if max_depth > 0:
            edges = tree_node.get_path_edges()
            for information_edge in tree_node.get_information_node().get_ou_edges():
                information_edge: CInformationEdge
                if information_edge not in edges:       # to avoid duplicated edge
                    child = tree_node.add_child(information_edge)
                    self.__extend__(child, max_depth - 1)
        return

    def get_root(self):
        return self.root

    def get_leafs(self):
        leafs = set()
        queue = deque()
        queue.append(self.root)
        while len(queue) > 0:
            node = queue.popleft()
            node: CInformationTreeNode
            if node.is_leaf():
                leafs.add(node)
            else:
                for child in node.get_children():
                    child: CInformationTreeNode
                    queue.append(child)
        return leafs


def get_information_paths(root: CInformationNode, max_depth: int):
    """
    :param root:
    :param max_depth:
    :return: the set of information flow from the root w.r.t. maximal distance in information flow graph.
    """
    tree = CInformationTree(root, max_depth)
    leafs = tree.get_leafs()
    paths = set()
    for leaf in leafs:
        path = leaf.get_path()
        paths.add(path)
    return paths


class CDependencePath:
    """
    [source; edges; target]
    """
    def __init__(self, source: CDependenceNode):
        self.source = source
        self.target = source
        self.edges = list()
        return

    def get_source(self):
        return self.source

    def get_target(self):
        return self.target

    def get_edges(self):
        return self.edges

    def get_edge(self, k: int):
        edge = self.edges[k]
        edge: CDependenceEdge
        return edge

    def add_edge(self, edge: CDependenceEdge):
        if edge.get_source() == self.target:
            self.edges.append(edge)
            self.target = edge.get_target()
        return


class CDependenceTreeNode:
    """
    [tree; parent, children; dependence_node, dependence_edge]
    """
    def __init__(self, tree, dependence_node: CDependenceNode, dependence_edge=None):
        self.tree = tree
        self.dependence_node = dependence_node
        self.dependence_edge = dependence_edge
        self.parent = None
        self.children = list()
        return

    def get_tree(self):
        self.tree: CDependenceTree
        return self.tree

    def get_parent(self):
        self.parent: CDependenceTreeNode
        return self.parent

    def get_children(self):
        return self.children

    def get_child(self, k: int):
        child = self.children[k]
        child: CDependenceTreeNode
        return child

    def is_root(self):
        return self.parent is None

    def is_leaf(self):
        return len(self.children) == 0

    def get_dependence_node(self):
        return self.dependence_node

    def get_dependence_edge(self):
        self.dependence_edge: CDependenceEdge
        return self.dependence_edge

    def add_child(self, dependence_edge: CDependenceEdge):
        """
        :param dependence_edge:
        :return: None if the edge's source does not match the dependence node in current tree node
        """
        if dependence_edge.get_source() == self.dependence_node:
            for child in self.children:
                child: CDependenceTreeNode
                if child.dependence_edge == dependence_edge:
                    return child
            child = CDependenceTreeNode(self.tree, dependence_edge.get_target(), dependence_edge)
            child.parent = self
            self.children.append(child)
            return child
        else:
            return None

    def get_path_edges(self):
        """
        :return: edges from current node to its root
        """
        tree_node = self
        edges = list()
        while not tree_node.is_root():
            edges.append(tree_node.get_dependence_edge())
            tree_node = tree_node.get_parent()
        return edges

    def get_path(self):
        """
        :return: path from root to the current node
        """
        tree_node = self
        path = list()
        while not tree_node.is_root():
            path.append(tree_node.get_dependence_edge())
            tree_node = tree_node.get_parent()
        path.reverse()
        dependence_path = CDependencePath(tree_node.get_dependence_node())
        for dependence_edge in path:
            dependence_path.add_edge(dependence_edge)
        return dependence_path


class CDependenceTree:
    """
    [root]
    """
    def __init__(self, dependence_root: CDependenceNode, max_depth: int):
        self.root = CDependenceTreeNode(self, dependence_root)
        self.__extend__(self.root, max_depth)
        return

    def __extend__(self, tree_node: CDependenceTreeNode, max_depth: int):
        if max_depth > 0:
            edges = tree_node.get_path_edges()
            for dependence_edge in tree_node.get_dependence_node().get_ou_edges():
                dependence_edge: CDependenceEdge
                if dependence_edge not in edges:
                    child = tree_node.add_child(dependence_edge)
                    self.__extend__(child, max_depth - 1)
        return

    def get_root(self):
        return self.root

    def get_leafs(self):
        queue = deque()
        queue.append(self.root)
        leafs = set()
        while len(queue) > 0:
            tree_node = queue.popleft()
            tree_node: CDependenceTreeNode
            if tree_node.is_leaf():
                leafs.add(tree_node)
            else:
                for child in tree_node.get_children():
                    queue.append(child)
        return leafs


def get_dependence_paths(root: CDependenceNode, max_depth: int):
    """
    :param root:
    :param max_depth:
    :return: the set of dependence flow from the root w.r.t. maximal distance in information flow graph.
    """
    tree = CDependenceTree(root, max_depth)
    leafs = tree.get_leafs()
    paths = set()
    for leaf in leafs:
        path = leaf.get_path()
        paths.add(path)
    return paths


if __name__ == "__main__":
    prefix = "C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data"
    for filename in os.listdir(prefix):
        directory = os.path.join(prefix, filename)
        program = CProgram(directory)
        function_call_graph = program.get_function_call_graph()
        instance_graph = program.get_instance_graph()
        information_graph = program.get_information_graph()
        dependence_graph = program.get_dependence_graph()
        output_file = os.path.join("C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output\\inf_flow", filename + ".inf")
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
                            # __output_information_node__(information_graph, instance_node, writer)
                            # for code_instance in instance_node.get_code_instances():
                            #    __output_information_node__(information_graph, code_instance, writer)
                            __output_dependence_node__(dependence_graph, instance_node, writer)
                            for code_instance in instance_node.get_code_instances():
                                __output_dependence_node__(dependence_graph, code_instance, writer)
                    writer.write("End Function\n\n")
    print("Testing end for all...")
