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


class CirCallEdge:
    """
    call_execution, wait_execution
    call_flow, wait_flow
    callee, caller
    """
    def __init__(self, call_execution: CirExecution, wait_execution: CirExecution):
        self.call_execution = call_execution
        self.wait_execution = wait_execution
        return

    def get_call_flow(self):
        return self.call_execution.ou_flows[0]

    def get_retr_flow(self):
        return self.wait_execution.in_flows[0]

    def get_call_execution(self):
        return self.call_execution

    def get_callee_entry(self):
        return self.call_execution.ou_flows[0].target

    def get_callee_exit(self):
        return self.wait_execution.in_flows[0].source

    def get_callee(self):
        return self.call_execution.ou_flows[0].target.function

    def get_caller(self):
        return self.call_execution.function


class CirFunction:
    """
    [graph, name, executions, in_calls, ou_calls]
    """
    def __init__(self, graph, name: str):
        self.graph = graph
        self.name = name
        self.executions = list()
        self.in_calls = list()
        self.ou_calls = list()
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
    def __init__(self, program, file_path: str, call_path: str):
        self.program = program
        self.functions = dict()
        self.exec_index = dict()
        self.callings = dict()
        self.__parse_nodes__(program.cir_tree, file_path)
        self.__parse_flows__(file_path)
        self.__parse_calls__(call_path)
        self.__parse_index__()
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

    def get_execution_by_statement(self, statement: ccode.CirNode):
        return self.exec_index[statement]

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

    def __parse_calls__(self, file_path: str):
        self.callings.clear()
        with open(file_path, 'r') as reader:
            for line in reader:
                line = line.strip()
                if len(line) > 0:
                    items = line.split('\t')
                    call_execution = self.get_execution_by_id(items[0].strip())
                    wait_execution = self.get_execution_by_id(items[-1].strip())
                    call = CirCallEdge(call_execution, wait_execution)
                    caller = call.get_caller()
                    callee = call.get_callee()
                    call_flow = call.get_call_flow()
                    retr_flow = call.get_retr_flow()
                    caller: CirFunction
                    callee: CirFunction
                    caller.ou_calls.append(call)
                    callee.in_calls.append(call)
                    self.callings[call_flow] = call
                    self.callings[retr_flow] = call
        return

    def __parse_index__(self):
        self.exec_index.clear()
        for function in self.functions.values():
            function: CirFunction
            for execution in function.executions:
                execution: CirExecution
                statement = execution.statement
                if statement is not None:
                    self.exec_index[statement] = execution
        return


class CirInstanceEdge:
    """
    [edge] edge_type source target
    """
    def __init__(self, edge_type: str, source, target):
        self.edge_type = edge_type
        self.source = source
        self.target = target
        return

    def get_edge_type(self):
        """
        :return: [next_flow, true_flow, fals_flow, call_flow, retr_flow, skip_flow]
        """
        return self.edge_type

    def get_source(self):
        return self.source

    def get_target(self):
        return self.target


class CirInstanceNode:
    """
    graph id context execution
    """
    def __init__(self, graph, id: str, flow_graph: CirFunctionGraph):
        self.graph = graph
        self.id = id
        index = id.index('@')
        self.execution = flow_graph.get_execution_by_id(id[0: index].strip())
        self.context = int(id[index + 1:].strip())
        self.in_edges = list()
        self.ou_edges = list()
        return

    def get_graph(self):
        return self.graph

    def get_id(self):
        return self.id

    def get_context(self):
        return self.context

    def get_execution(self):
        return self.execution

    def get_in_edges(self):
        return self.in_edges

    def get_ou_edges(self):
        return self.ou_edges

    def __link__(self, target, edge_type: str):
        edge = CirInstanceEdge(edge_type, self, target)
        self.ou_edges.append(edge)
        target.in_edges.append(edge)
        return edge


class CirInstanceGraph:

    def __parse_nodes__(self, file_path: str, flow_graph: CirFunctionGraph):
        self.nodes.clear()
        with open(file_path, 'r') as reader:
            for line in reader:
                line = line.strip()
                if len(line) > 0:
                    items = line.split('\t')
                    title = items[0].strip()
                    if title == '[node]':
                        id = items[1].strip()
                        node = CirInstanceNode(self, id, flow_graph)
                        self.nodes[id] = node
        return

    def __parse_edges__(self, file_path: str):
        with open(file_path, 'r') as reader:
            for line in reader:
                line = line.strip()
                if len(line) > 0:
                    items = line.split('\t')
                    title = items[0].strip()
                    if title == '[edge]':
                        edge_type = items[1].strip()
                        source = self.nodes[items[2].strip()]
                        target = self.nodes[items[3].strip()]
                        source: CirInstanceNode
                        source.__link__(target, edge_type)
        return

    def __init__(self, program, file_path: str):
        self.program = program
        self.nodes = dict()
        self.__parse_nodes__(file_path, self.program.function_graph)
        self.__parse_edges__(file_path)
        return

    def get_program(self):
        return self.program

    def get_nodes(self):
        return self.nodes.values()

    def get_node(self, key: str):
        return self.nodes[key]


class CPredicateElement:
    """
    [condition, value]
    """
    def __init__(self, condition: ccode.CirNode, value: bool):
        self.condition = condition
        self.value = value
        return

    def get_condition(self):
        return self.condition

    def get_value(self):
        return self.value


class CReferenceElement:
    """
    [define, use]
    """
    def __init__(self, define: ccode.CirNode, use: ccode.CirNode):
        self.define = define
        self.use = use
        return

    def get_define(self):
        return self.define

    def get_use(self):
        return self.use


class CDependenceEdge:
    """
    depend_type, source, target, element
    """
    def __init__(self, depend_type: str, source, target, element):
        self.depend_type = depend_type
        self.source = source
        self.target = target
        self.element = element
        return

    def get_depend_type(self):
        return self.depend_type

    def get_source(self):
        return self.source

    def get_target(self):
        return self.target

    def get_element(self):
        return self.element

    def has_element(self):
        return self.element is not None


class CDependenceNode:
    """
    graph, instance, in_edges, ou_edges
    """
    def __init__(self, graph, instance: CirInstanceNode):
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

    def __link__(self, target, depend_type: str, element):
        edge = CDependenceEdge(depend_type, self, target, element)
        self.ou_edges.append(edge)
        target.in_edges.append(edge)
        return edge


class CDependenceGraph:
    """
    program, nodes
    """
    def __parse_nodes__(self, file_path: str):
        self.nodes.clear()
        instance_graph = self.program.instance_graph
        instance_graph: CirInstanceGraph
        with open(file_path, 'r') as reader:
            for line in reader:
                line = line.strip()
                if len(line) > 0:
                    items = line.split('\t')
                    if items[0].strip() == "[node]":
                        key = items[1].strip()
                        instance = instance_graph.get_node(key)
                        node = CDependenceNode(self, instance)
                        self.nodes[instance] = node
        return

    def __parse_edges__(self, file_path: str):
        instance_graph = self.program.instance_graph
        instance_graph: CirInstanceGraph
        with open(file_path, 'r') as reader:
            for line in reader:
                line = line.strip()
                if len(line) > 0:
                    items = line.split('\t')
                    if items[0].strip() == "[edge]":
                        source_key = instance_graph.get_node(items[1].strip())
                        target_key = instance_graph.get_node(items[2].strip())
                        source = self.nodes[source_key]
                        target = self.nodes[target_key]
                        depend_type = items[3].strip()
                        if len(items) > 4:
                            element = self.__parse_element__(items[4].strip())
                        else:
                            element = None
                        source: CDependenceNode
                        source.__link__(target, depend_type, element)
        return

    def __get_cir_node__(self, key: str):
        index = key.index('#')
        key = int(key[index + 1:].strip())
        cir_tree = self.program.cir_tree
        cir_tree: ccode.CirTree
        return cir_tree.nodes[key]

    def __parse_element__(self, element_string: str):
        if len(element_string) == 0:
            return None
        items = element_string.split(' ')
        if items[1].strip() == "predicate":
            condition = self.__get_cir_node__(items[2].strip())
            if items[3].strip() == "true":
                value = True
            else:
                value = False
            return CPredicateElement(condition, value)
        else:
            define = self.__get_cir_node__(items[2].strip())
            use = self.__get_cir_node__(items[3].strip())
            return CReferenceElement(define, use)

    def __init__(self, program, file_path: str):
        self.program = program
        self.nodes = dict()
        self.__parse_nodes__(file_path)
        self.__parse_edges__(file_path)
        return

    def get_program(self):
        return self.program

    def get_instances(self):
        return self.nodes.keys()

    def get_nodes(self):
        return self.nodes.values()
