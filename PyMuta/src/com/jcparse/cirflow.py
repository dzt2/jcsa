"""
cirflow.py defines model to describe static program flow graph, including:
        CirExecution, CirExecutionFlow, CirExecutionFlowGraph
        CirFunction, CirFunctionCall, CirFunctionGraph
        CirProgram
"""


import os
from enum import Enum
import src.com.jcparse.astree as astree
import src.com.jcparse.cirtree as cirtree


class CirExecutionFlowType(Enum):
    next_flow = 0
    true_flow = 1
    false_flow = 2
    call_flow = 3
    return_flow = 4
    skip_flow = 5

    def __str__(self):
        return self.name

    @staticmethod
    def parse(text: str):
        if text == "next_flow":
            return CirExecutionFlowType.next_flow
        elif text == "true_flow":
            return CirExecutionFlowType.true_flow
        elif text == "fals_flow":
            return CirExecutionFlowType.false_flow
        elif text == "call_flow":
            return CirExecutionFlowType.call_flow
        elif text == "retr_flow":
            return CirExecutionFlowType.return_flow
        elif text == "skip_flow":
            return CirExecutionFlowType.skip_flow
        else:
            return None


class CirExecutionFlow:
    """
    (flow_type, source, target)
    """
    def __init__(self, flow_type: CirExecutionFlowType, source, target):
        self.flow_type = flow_type
        self.source = source
        self.target = target
        return

    def get_flow_type(self):
        return self.flow_type

    def get_source(self):
        self.source: CirExecution
        return self.source

    def get_target(self):
        self.target: CirExecution
        return self.target

    def __str__(self):
        return str(self.flow_type) + "(" + str(self.source) + ", " + str(self.target) + ")"


class CirExecution:
    """
    execution instance is a node in flow graph referring to one CirStatement in program.
    """
    def __init__(self, graph, id: int, statement: cirtree.CirNode):
        self.graph = graph
        self.id = id
        self.statement = statement
        self.in_flows = list()
        self.ou_flows = list()
        return

    def get_graph(self):
        self.graph: CirExecutionFlowGraph
        return self.graph

    def get_id(self):
        return self.id

    def get_statement(self):
        return self.statement

    def get_cir_type(self):
        """
        :return: CIR node type of the statement
        """
        return self.statement.cir_type

    def get_in_flows(self):
        return self.in_flows

    def get_ou_flows(self):
        return self.ou_flows

    def link_to(self, target, flow_type: CirExecutionFlowType):
        target: CirExecution
        flow = CirExecutionFlow(flow_type, self, target)
        self.ou_flows.append(flow)
        target.in_flows.append(flow)
        return flow

    def __str__(self):
        return self.graph.function.get_name() + "[" + str(self.id) + "]"


class CirExecutionFlowGraph:
    """
    static program graph within the function as:
    {function; executions; cir_index;}
    """

    def __init__(self, function):
        function: CirFunction
        self.function = function
        self.executions = list()
        self.cir_index = dict()
        return

    def get_function(self):
        self.function: CirFunction
        return self.function

    def get_executions(self):
        return self.executions

    def get_execution(self, id: int):
        execution = self.executions[id]
        execution: CirExecution
        return execution

    def number_of_executions(self):
        return len(self.executions)

    def get_entry(self):
        return self.executions[1]

    def get_exit(self):
        return self.executions[0]

    def get_execution_of(self, statement: cirtree.CirNode):
        execution = self.cir_index[statement]
        execution: CirExecution
        return execution


class CirFunctionCall:
    """
    calling relationship between functions as:
    {caller, callee, call_flow, return_flow}
    """

    def __init__(self, call_flow: CirExecutionFlow, return_flow: CirExecutionFlow):
        self.call_flow = call_flow
        self.return_flow = return_flow
        return

    def get_call_flow(self):
        return self.call_flow

    def get_return_flow(self):
        return self.return_flow

    def get_caller(self):
        return self.call_flow.get_source().get_graph().get_function()

    def get_callee(self):
        return self.call_flow.get_target().get_graph().get_function()

    def get_call_execution(self):
        return self.call_flow.get_source()

    def get_beg_execution(self):
        return self.call_flow.get_target()

    def get_end_execution(self):
        return self.return_flow.get_source()

    def get_wait_execution(self):
        return self.return_flow.get_target()

    def get_call_statement(self):
        return self.call_flow.get_source().get_statement()

    def get_wait_statement(self):
        return self.return_flow.get_target().get_statement()


class CirFunction:
    """
    function as {graph, name; flow_grpah; in_calls; ou_calls;}
    """
    def __init__(self, graph, name: str):
        graph: CirFunctionCallGraph
        self.graph = graph
        self.name = name
        self.definition = None
        self.flow_graph = CirExecutionFlowGraph(self)
        self.in_calls = list()
        self.ou_calls = list()
        return

    def get_function_call_graph(self):
        self.graph: CirFunctionCallGraph
        return self.graph

    def get_name(self):
        return self.name

    def get_definition(self):
        self.definition: cirtree.CirNode
        return self.definition

    def get_execution_flow_graph(self):
        self.flow_graph: CirExecutionFlowGraph
        return self.flow_graph

    def get_in_calls(self):
        return self.in_calls

    def get_ou_calls(self):
        return self.ou_calls


class CirFunctionCallGraph:
    """
    function calling graph as {cir_tree; functions; call_relations; cir_index;}
    """

    def __init__(self, cir_tree: cirtree.CirTree, flow_file: str):
        self.program = None
        self.cir_tree = cir_tree
        self.functions = dict()
        self.call_relations = dict()
        self.cir_index = dict()
        self.__parse_functions__(flow_file)
        self.__parse_executions__(flow_file)
        self.__parse_flows__(flow_file)
        self.__parse_calling__()
        return

    def get_cir_tree(self):
        return self.cir_tree

    def get_functions(self):
        return self.functions.values()

    def names_of_functions(self):
        return self.functions.keys()

    def get_function(self, name: str):
        return self.functions[name]

    def get_call_relations(self):
        return self.call_relations.values()

    def get_call_relation(self, flow: CirExecutionFlow):
        return self.call_relations[flow]

    def call(self, call_flow: CirExecutionFlow, return_flow: CirExecutionFlow):
        """
        record the calling relationship from call_flow to return_flow
        :param call_flow:
        :param return_flow:
        :return:
        """
        if call_flow.get_flow_type() == CirExecutionFlowType.call_flow and \
                return_flow.get_flow_type() == CirExecutionFlowType.return_flow:
            calling = CirFunctionCall(call_flow, return_flow)
            self.call_relations[call_flow] = calling
            self.call_relations[return_flow] = calling
            source = calling.get_caller()
            target = calling.get_callee()
            source.ou_calls.append(calling)
            target.in_calls.append(calling)
            return calling
        else:
            return None

    def get_execution(self, text: str):
        """
        get the execution w.r.t. its string identifier as function_name[execution_id]
        :param text:
        :return:
        """
        beg = text.index('[')
        end = text.index(']')
        name = text[0:beg].strip()
        id = int(text[beg+1:end].strip())
        function = self.functions[name]
        function: CirFunction
        return function.get_execution_flow_graph().get_execution(id)

    def get_function_of(self, cir_node: cirtree.CirNode):
        """
        :param cir_node:
        :return: the function in which the CIR-node is created
        """
        definition = cir_node.function_body_of().parent
        function = self.cir_index[definition]
        function: CirFunction
        return function

    def __parse_functions__(self, flow_file: str):
        self.functions.clear()
        with open(flow_file, 'r') as reader:
            for line in reader:
                line = line.strip()
                if len(line) > 0:
                    items = line.split('\t')
                    if items[0].strip() == "function":
                        name = items[1].strip()
                        function = CirFunction(self, name)
                        self.functions[name] = function
        return

    def __parse_executions__(self, flow_file: str):
        buffer = dict()
        for function in self.functions.values():
            buffer[function] = dict()
        with open(flow_file, 'r') as reader:
            for line in reader:
                line = line.strip()
                if len(line) > 0:
                    items = line.split('\t')
                    if items[0].strip() == "[execution]":
                        identifier = items[1].strip()
                        beg = identifier.index('[')
                        end = identifier.index(']')
                        name = identifier[0:beg].strip()
                        id = int(identifier[beg + 1:end].strip())
                        statement = self.cir_tree.get_node(int(items[2].strip()))
                        function = self.functions[name]
                        function: CirFunction
                        execution = CirExecution(function.get_execution_flow_graph(), id, statement)
                        executions = buffer[function]
                        executions[execution.id] = execution
                        function.get_execution_flow_graph().cir_index[statement] = execution
        self.cir_index.clear()
        for function, executions in buffer.items():
            for k in range(0, len(executions)):
                execution = executions[k]
                execution: CirExecution
                function.get_execution_flow_graph().executions.append(execution)
                statement = execution.get_statement()
                definition = statement.function_body_of().parent
                if definition not in self.cir_index:
                    self.cir_index[definition] = function
                    function.definition = definition
        return

    def __parse_flows__(self, flow_file: str):
        with open(flow_file, 'r') as reader:
            for line in reader:
                line = line.strip()
                if len(line) > 0:
                    items = line.split('\t')
                    if items[0].strip() == "[execution]":
                        source = self.get_execution(items[1].strip())
                        source: CirExecution
                        for k in range(3, len(items)):
                            columns = items[k].strip().split(' ')
                            flow_type = CirExecutionFlowType.parse(columns[1].strip())
                            target = self.get_execution(columns[3].strip())
                            source.link_to(target, flow_type)
        return

    def __parse_calling__(self):
        for function in self.functions.values():
            function: CirFunction
            flow_graph = function.get_execution_flow_graph()
            for execution in flow_graph.get_executions():
                execution: CirExecution
                if execution.get_cir_type() == cirtree.CirType.call_statement:
                    call_execution = execution
                    wait_execution = flow_graph.get_execution(call_execution.get_id() + 1)
                    self.call(call_execution.ou_flows[0], wait_execution.in_flows[0])
        return


if __name__ == "__main__":
    prefix = "C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data"
    for filename in os.listdir(prefix):
        directory = os.path.join(prefix, filename)
        source_file = os.path.join(directory, filename + ".c")
        ast_tree_file = os.path.join(directory, filename + ".ast")
        cir_tree_file = os.path.join(directory, filename + ".cir")
        exe_flow_file = os.path.join(directory, filename + ".flw")
        ast_tree = astree.AstTree(source_file, ast_tree_file)
        cir_tree = cirtree.CirTree(ast_tree, cir_tree_file)
        function_call_graph = CirFunctionCallGraph(cir_tree, exe_flow_file)
        output_file = os.path.join("C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output", filename + ".ast")
        print("Open the abstract syntax tree and CIR-tree for", filename)
        with open(output_file, 'w') as writer:
            for function in function_call_graph.get_functions():
                function: CirFunction
                writer.write("Function " + function.get_name() + ":\n")
                for k in range(1, function.get_execution_flow_graph().number_of_executions() + 1):
                    execution = function.flow_graph.get_execution(k % function.flow_graph.number_of_executions())
                    execution: CirExecution
                    writer.write("\t" + str(execution) + "\t")
                    statement = execution.get_statement()
                    code = statement.generate_code(True)
                    # if statement.get_ast_source() is not None:
                    #    code = statement.ast_source.get_code(True)
                    writer.write("\"" + code + "\"\n")
                    for ou_flow in execution.get_ou_flows():
                        writer.write("\t==>\t")
                        writer.write(str(ou_flow))
                        writer.write("\n")
                writer.write("End Function\n\n")
    print("Testing end for all...")
