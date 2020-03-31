"""
    cflow.py defines the data model describing the program flow graph, together with its dependence graph.
        CirExecution
        CirExecutionFlow
        CirFunction
        CirFunctionGraph

        CInfluenceNode
        CInfluenceEdge
        CInfluenceGraph
"""


import os
import src.cmodel.ccode as ccode


class CirExecutionFlow:
    """
    [flow_type, source, target]
    """
    def __init__(self, flow_type: str, source, target):
        self.flow_type = flow_type
        self.source = source
        self.target = target
        return

    def get_flow_type(self):
        return self.flow_type

    def get_source(self):
        return self.source

    def get_target(self):
        return self.target


class CirExecution:
    """
    [function, id: int, statement: CirNode, in_flows, ou_flows]
    """
    def __init__(self, function, id: int, statement: ccode.CirNode):
        self.function = function
        self.id = id
        self.statement = statement
        self.in_flows = list()
        self.ou_flows = list()
        return

    def get_function(self):
        return self.function

    def get_id(self):
        return self.id

    def get_statement(self):
        return self.statement

    def get_in_flows(self):
        return self.in_flows

    def get_ou_flows(self):
        return self.ou_flows

    def __str__(self):
        return self.function.name + '[' + str(self.id) + ']'

    def link_to(self, target, flow_type: str):
        target: CirExecution
        flow = CirExecutionFlow(flow_type, self, target)
        self.ou_flows.append(flow)
        target.in_flows.append(flow)
        return flow


class CirFunction:
    """
    [graph, name, executions]
    """
    def __init__(self, graph, name: str):
        self.graph = graph
        self.name = name
        self.executions = list()
        return

    def get_graph(self):
        return self.graph

    def get_name(self):
        return self.name

    def get_executions(self):
        return self.executions

    def get_execution(self, id: int):
        return self.executions[id]

    @staticmethod
    def __get_function_and_id__(identifier: str):
        """
        :param identifier:
        :return: function.name execution.id
        """
        beg = identifier.index('[')
        end = identifier.index(']')
        name = identifier[0:beg].strip()
        id = identifier[beg + 1: end].strip()
        return name, int(id)

    def parse_nodes(self, cir_tree: ccode.CirTree, execution_lines: list):
        self.executions.clear()
        execution_dict = dict()
        for execution_line in execution_lines:
            execution_line: str
            execution_line = execution_line.strip()
            items = execution_line.split('\t')
            name, execution_id = CirFunction.__get_function_and_id__(items[0].strip())
            statement = cir_tree.nodes[int(items[1].strip())]
            execution = CirExecution(self, execution_id, statement)
            execution_dict[execution_id] = execution
        for k in range(0, len(execution_dict)):
            execution = execution_dict[k]
            self.executions.append(execution)
        return


class CirFunctionGraph:
    """
    [program, {name, function}]
    """
    def __init__(self, program, file_path: str):
        self.program = program
        self.functions = dict()
        self.__parse_nodes__(program.cir_tree, file_path)
        self.__parse_flows__(file_path)
        return

    def get_program(self):
        return self.program

    def get_function_names(self):
        return self.functions.keys()

    def get_functions(self):
        return self.functions.values()

    def get_function(self, name: str):
        return self.functions[name]

    def get_execution(self, name: str, id: int):
        return self.functions[name].get_execution(id)

    def get_execution_by_id(self, identifier: str):
        name, id = CirFunction.__get_function_and_id__(identifier)
        return self.functions[name].get_execution(id)

    def __parse_nodes__(self, cir_tree: ccode.CirTree, file_path: str):
        self.functions.clear()
        with open(file_path, 'r') as reader:
            first = True
            execution_lines = list()
            for line in reader:
                if first:
                    first = False
                else:
                    line = line.strip()
                    if len(line) > 0:
                        if line.startswith('function'):
                            items = line.split('\t')
                            name = items[1].strip()
                            function = CirFunction(self, name)
                            self.functions[name] = function
                        elif line.startswith("end function"):
                            function: CirFunction
                            function.parse_nodes(cir_tree, execution_lines)
                            execution_lines.clear()
                        else:
                            execution_lines.append(line.strip())
        return

    def __parse_flows__(self, file_path: str):
        with open(file_path, 'r') as reader:
            first = True
            for line in reader:
                if first:
                    first = False
                else:
                    line = line.strip()
                    if line.startswith("function") or line.startswith("end function") or len(line) == 0:
                        continue        # ignore the function head and tail summary
                    else:
                        items = line.strip().split('\t')
                        for k in range(2, len(items)):
                            flow_string = items[k].strip()
                            flow_items = flow_string.split(' ')
                            flow_type = flow_items[1].strip()
                            source_id = flow_items[2].strip()
                            target_id = flow_items[3].strip()
                            source = self.get_execution_by_id(source_id)
                            target = self.get_execution_by_id(target_id)
                            source: CirExecution
                            target: CirExecution
                            source.link_to(target, flow_type)
        return


class CInfluenceEdge:
    """
    [influence_type, source, target]
    """
    def __init__(self, influence_type: str, source, target):
        self.influence_type = influence_type
        self.source = source
        self.target = target
        return

    def get_influence_type(self):
        return self.influence_type

    def get_source(self):
        return self.source

    def get_target(self):
        return self.target


class CInfluenceNode:
    """
    [graph, node_type, execution, cir_source, in_edges, ou_edges]
    """

    def __init__(self, graph, id: str, node_type: str, execution: CirExecution, cir_source: ccode.CirNode):
        self.graph = graph
        self.id = id
        self.node_type = node_type
        self.execution = execution
        self.cir_source = cir_source
        self.in_edges = list()
        self.ou_edges = list()
        return

    def get_graph(self):
        return self.graph

    def get_id(self):
        return self.id

    def get_node_type(self):
        return self.node_type

    def get_execution(self):
        return self.execution

    def get_cir_source(self):
        return self.cir_source

    def get_in_edges(self):
        return self.in_edges

    def get_ou_edges(self):
        return self.ou_edges

    def link_to(self, target, influence_type: str):
        target: CInfluenceNode
        edge = CInfluenceEdge(influence_type, self, target)
        self.ou_edges.append(edge)
        target.in_edges.append(edge)
        return edge


class CInfluenceGraph:
    """
    [program, node_dict]
    """

    def __init__(self, program, file_path: str):
        self.program = program
        self.nodes = dict()
        self.cir_index = dict()
        self.__parse_nodes__(file_path)
        self.__parse_edges__(file_path)
        return

    def get_nodes(self):
        return self.nodes.values()

    def get_node_keys(self):
        return self.nodes.keys()

    def get_node(self, id: str):
        return self.nodes[id]

    def has_nodes_of(self, cir_source: ccode.CirNode):
        return cir_source in self.cir_index

    def get_nodes_of(self, cir_source: ccode.CirNode):
        if cir_source in self.cir_index:
            return self.cir_index[cir_source]
        else:
            return list()

    def __parse_nodes__(self, file_path: str):
        self.nodes.clear()
        self.cir_index.clear()
        with open(file_path, 'r') as reader:
            first = True
            for line in reader:
                if first:
                    first = False
                else:
                    line = line.strip()
                    if len(line) > 0:
                        items = line.strip().split('\t')
                        id = items[0].strip()
                        node_type = items[1].strip()
                        execution = self.program.function_graph.get_execution_by_id(items[2].strip())
                        cir_source = self.program.cir_tree.nodes[int(items[3].strip())]
                        node = CInfluenceNode(self, id, node_type, execution, cir_source)
                        self.nodes[id] = node
                        if cir_source not in self.cir_index:
                            self.cir_index[cir_source] = list()
                        self.cir_index[cir_source].append(node)
        return

    def __parse_edges__(self, file_path: str):
        with open(file_path, 'r') as reader:
            first = True
            for line in reader:
                if first:
                    first = False
                else:
                    line = line.strip()
                    if len(line) > 0:
                        items = line.strip().split('\t')
                        for k in range(4, len(items)):
                            edge_string = items[k].strip()
                            edge_items = edge_string.split(' ')
                            edge_type = edge_items[1].strip()
                            source = self.nodes[edge_items[2].strip()]
                            target = self.nodes[edge_items[3].strip()]
                            source: CInfluenceNode
                            target: CInfluenceNode
                            source.link_to(target, edge_type)
        return





