"""
This file defines the models for describing the structural program models, including:
	CSourceCode             --- It manages the access to characters in source code in .c file.
	AstTree                 --- It presents the structural model of abstract syntactic tree of C program.
	CirTree                 --- It describes the structural model of C-intermediate representation code.
	CirFunctionCallGraph    --- It defines the structural control flow graph for program analysis.
	TestSpace               --- It defines the test cases used for executing the program under test.
	MutantSpace             --- It defines the mutations seeded within the C program.
	CProgram                --- It provides access to these structural descriptions of C source code.
"""

import os
import src.jcmuta.base as base


class CProgram:
	def __init__(self, directory_path: str, name: str):
		self.source_code = CSourceCode(self, os.path.join(directory_path, name + ".c"))
		self.ast_tree = AstTree(self, os.path.join(directory_path, name + ".ast"))
		self.cir_tree = CirTree(self, os.path.join(directory_path, name + ".cir"))
		self.function_call_graph = CirFunctionCallGraph(self, os.path.join(directory_path, name + ".flw"))
		self.test_space = TestSpace(self, os.path.join(directory_path, name + ".tst"))
		self.mutant_space = MutantSpace(self, os.path.join(directory_path, name + ".mut"))
		return


class CSourceCode:
	"""
	It provides interfaces to access the source code of .c file
	"""

	def __init__(self, program: CProgram, c_file_path: str):
		"""
		:param c_file_path: xxx.c file of which code is accessed
		"""
		self.program = program
		self.text = ""
		self.lines = list()
		with open(c_file_path, "r") as reader:
			index = 0
			self.lines.append(index)
			for line in reader:
				self.text += line
				index = index + len(line)
				self.lines.append(index)
		return

	def get_program(self):
		"""
		:return: the program where the source code is created
		"""
		return self.program

	def get_text(self):
		"""
		:return: the text of source code of the c file
		"""
		return self.text

	def get_sub_text(self, beg_index: int, end_index: int):
		"""
		:param beg_index: index to the first character of the string in source code
		:param end_index: index next to the final character of the string in source code
		:return: the code in range of [beg_index, end_index)
		"""
		return self.text[beg_index: end_index]

	def length(self):
		"""
		:return: the length of the source code of the c file
		"""
		return len(self.text)

	def number_of_lines(self):
		"""
		:return: the number of lines within the source code
		"""
		return len(self.lines) - 1

	def text_at_line(self, line: int):
		"""
		:param line: the line of which code is extracted, start from 0
		:return: code at line specified
		"""
		beg_index = self.lines[line]
		end_index = self.lines[line + 1]
		return self.get_sub_text(beg_index, end_index)

	def line_of(self, index: int):
		"""
		:param index: the location of character where the target line is
		:return: the line in which the character of the specified index is
		"""
		beg_line, end_line = 0, len(self.lines) - 2
		while beg_line <= end_line:
			mid_line = (beg_line + end_line) // 2
			beg_index = self.lines[mid_line]
			end_index = self.lines[mid_line + 1]
			if index < beg_index:
				end_line = mid_line - 1
			elif index >= end_index:
				beg_line = mid_line + 1
			else:
				return mid_line
		return -1       # Not Found


class AstNode:
	"""
	Abstract syntactic tree node defined as:
		--- tree: AstTree
		--- class_name: str
		--- parent: AstNode
		--- children: AstNode*
		--- data_type: str
		--- token: CToken
		--- beg_index: int
		--- end_index: int
	"""
	def __init__(self, tree, class_name: str, beg_index: int, end_index: int, data_type: str, token: base.CToken):
		"""
		:param tree: abstract syntactic tree where the node is created
		:param class_name: the name of the abstract syntactic class
		:param beg_index: the index to the first character of the code range
		:param end_index: the index next to the final character of the code range
		:param data_type: the data type of the value hold by the node or None
		:param token: the token hold by the syntactic node
		"""
		tree: AstTree
		self.tree = tree
		self.class_name = class_name
		self.beg_index = beg_index
		self.end_index = end_index
		self.data_type = data_type
		self.token = token
		self.parent = None
		self.children = list()
		return

	def get_tree(self):
		"""
		:return:  abstract syntactic tree where the node is created
		"""
		return self.tree

	def get_class_name(self):
		"""
		:return: the name of the abstract syntactic class
		"""
		return self.class_name

	def get_beg_index(self):
		"""
		:return: the index to the first character of the code range
		"""
		return self.beg_index

	def get_end_index(self):
		"""
		:return: the index next to the final character of the code range
		"""
		return self.end_index

	def get_code(self):
		"""
		:return: the code in the range of the syntactic tree node
		"""
		program = self.tree.program
		program: CProgram
		return program.source_code.get_sub_text(self.beg_index, self.end_index)

	def line_of(self):
		"""
		:return: the line of the code range to which the syntactic tree belongs
		"""
		program = self.tree.program
		program: CProgram
		return program.source_code.line_of(self.beg_index)

	def has_data_type(self):
		return self.data_type is not None

	def get_data_type(self):
		"""
		:return: the data type of the value hold by the node or None
		"""
		return self.data_type

	def has_token(self):
		return self.token is not None

	def get_token(self):
		"""
		:return: the token hold by the syntactic node
		"""
		return self.token

	def get_parent(self):
		return self.parent

	def get_children(self):
		return self.children

	def is_root(self):
		return self.parent is None

	def is_leaf(self):
		return len(self.children) == 0

	def add_child(self, child):
		"""
		:param child:
		:return: add the child at the tail of the node
		"""
		child: AstNode
		self.children.append(child)
		child.parent = self
		return


class AstTree:
	"""
	Abstract syntactic tree
	"""
	def __init__(self, program: CProgram, ast_file_path: str):
		"""
		:param program:
		:param ast_file_path: xxx.ast file to generate the abstract syntactic tree
		"""
		''' create nodes in abstract syntactic tree as isolated '''
		node_dict = dict()
		with open(ast_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					ast_node_id = int(items[0].strip())
					class_name = items[1].strip()
					beg_index = int(items[2].strip())
					end_index = int(items[3].strip())
					data_type = items[4].strip()
					if len(data_type) == 0:
						data_type = None
					token = base.CToken.parse(items[5].strip())
					ast_node = AstNode(self, class_name, beg_index, end_index, data_type, token)
					node_dict[ast_node_id] = ast_node

		''' connect the abstract syntactic nodes within the tree '''
		with open(ast_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					parent = node_dict[int(items[0].strip())]
					children_items = items[6].split(' ')
					for k in range(1, len(children_items) - 1):
						child_id = int(children_items[k].strip())
						child = node_dict[child_id]
						parent.add_child(child)

		''' record the abstract syntactic tree node into the tree buffer '''
		self.nodes = list()
		self.program = program
		for k in range(0, len(node_dict)):
			self.nodes.append(node_dict[k])
		return

	def get_program(self):
		"""
		:return: the program of which code structure is defined by this tree
		"""
		return self.program

	def get_ast_nodes(self):
		"""
		:return: abstract syntactic nodes in the tree
		"""
		return self.nodes


class CirNode:
	"""
	C-intermediate representation tree node defined as following:
		--- tree: C-intermediate representation tree where the node is created
		--- class_name: the name of the C-intermediate representation class
		--- ast_source: the abstract syntactic node to which the node refers
		--- data_type: the data type of the value hold by the node as expression
		--- token: the token that describes the token of the node
		--- parent, children
	"""

	def __init__(self, tree, class_name: str, ast_source: AstNode, data_type: str, token: base.CToken):
		"""
		:param tree: C-intermediate representation tree where the node is created
		:param class_name: the name of the C-intermediate representation class
		:param ast_source: the abstract syntactic node to which the node refers
		:param data_type: the data type of the value hold by the node as expression
		:param token: the token that describes the token of the node
		"""
		self.tree = tree
		self.class_name = class_name
		self.ast_source = ast_source
		self.data_type = data_type
		self.token = token
		self.parent = None
		self.children = list()
		return

	def get_tree(self):
		"""
		:return: C-intermediate representation tree where the node is created
		"""
		return self.tree

	def get_class_name(self):
		"""
		:return: the name of the C-intermediate representation class
		"""
		return self.class_name

	def has_ast_source(self):
		"""
		:return: whether there is abstract syntactic tree node to which this node refers
		"""
		return self.ast_source is not None

	def get_ast_source(self):
		"""
		:return: the abstract syntactic node to which the node refers
		"""
		return self.ast_source

	def has_data_type(self):
		return self.data_type is not None

	def get_data_type(self):
		"""
		:return: the data type of the value hold by the node as expression
		"""
		return self.data_type

	def has_token(self):
		return self.token is not None

	def get_token(self):
		"""
		:return: the token that describes the token of the node
		"""
		return self.token

	def is_root(self):
		return self.parent is None

	def is_leaf(self):
		return len(self.children) == 0

	def get_parent(self):
		self.parent: CirNode
		return self.parent

	def get_children(self):
		return self.children

	def add_child(self, child):
		"""
		:param child:
		:return: add the child in the tail of the node
		"""
		child: CirNode
		self.children.append(child)
		child.parent = self
		return


class CirTree:
	"""
	C-intermediate representation tree
	"""
	def __init__(self, program: CProgram, cir_file_path: str):
		"""
		:param program:
		:param cir_file_path: xxx.cir to generate C-intermediate representation code
		"""
		''' 1. create isolated tree node in C-intermediate representation '''
		node_dict = dict()
		with open(cir_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					cir_node_id = int(items[0].strip())
					class_name = items[1].strip()
					ast_source_id = base.CToken.parse(items[2].strip()).token_value
					if ast_source_id is not None:
						ast_source_id: int
						ast_source = program.ast_tree.nodes[ast_source_id]
					else:
						ast_source = None
					data_type = items[3].strip()
					if len(data_type) == 0:
						data_type = None
					token = base.CToken.parse(items[4].strip())
					node_dict[cir_node_id] = CirNode(self, class_name, ast_source, data_type, token)

		''' 2. connect the nodes in C-intermediate representation tree '''
		with open(cir_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					parent = node_dict[int(items[0].strip())]
					children = items[5].strip().split(' ')
					for k in range(1, len(children) - 1):
						child = node_dict[int(children[k].strip())]
						parent.add_child(child)

		''' 3. construct the C-intermediate representation tree '''
		self.program = program
		self.nodes = list()
		for k in range(0, len(node_dict)):
			self.nodes.append(node_dict[k])
		return

	def get_program(self):
		return self.program

	def get_cir_nodes(self):
		return self.nodes


class CirExecutionFlow:
	"""
	type source target
	"""
	def __init__(self, flow_type: str, source, target):
		self.flow_type = flow_type
		source: CirExecution
		self.source = source
		target: CirExecution
		self.target = target
		return

	def get_flow_type(self):
		"""
		:return: the type of the execution from source to target
		"""
		return self.flow_type

	def get_source(self):
		"""
		:return: source node from which flows to the target
		"""
		return self.source

	def get_target(self):
		"""
		:return: target node to which flows from the source
		"""
		return self.target


class CirExecution:
	"""
	Each node in execution flow graph represents a statement in C-intermediate representation as:
		--- function: where the statement is defined
		--- statement: CirNode to which th node refers
		--- id: the unique identifier of the execution node
	"""

	def __init__(self, function, exec_id: int, statement: CirNode):
		"""
		:param function: where the statement is defined
		:param exec_id: the unique identifier of the execution node
		:param statement: CirNode to which th node refers
		"""
		function: CirFunction
		self.function = function
		self.exec_id = exec_id
		self.statement = statement
		self.in_flows = list()
		self.ou_flows = list()
		return

	def get_function(self):
		"""
		:return:  where the statement is defined
		"""
		return self.function

	def get_id(self):
		"""
		:return: the unique identifier of the execution node
		"""
		return self.exec_id

	def get_statement(self):
		"""
		:return: CirNode to which th node refers
		"""
		return self.statement

	def get_in_flows(self):
		return self.in_flows

	def get_ou_flows(self):
		return self.ou_flows

	def connect(self, flow_type: str, target):
		"""
		:param flow_type:
		:param target:
		:return: flow that connects this node to the target w.r.t. the type
		"""
		target: CirExecution
		flow = CirExecutionFlow(flow_type, self, target)
		self.ou_flows.append(flow)
		target.in_flows.append(flow)
		return flow


class CirFunctionCall:
	"""
	Function calling relationship
	"""
	def __init__(self, call_execution: CirExecution, wait_execution: CirExecution):
		"""
		:param call_execution: node that calls another function
		:param wait_execution: node that waits for the function to return
		"""
		self.call_execution = call_execution
		self.wait_execution = wait_execution
		return

	def get_call_execution(self):
		return self.call_execution

	def get_callee_entry(self):
		entry = self.call_execution.ou_flows[0].get_target()
		entry: CirExecution
		return entry

	def get_wait_execution(self):
		return self.wait_execution

	def get_callee_exits(self):
		exits = self.wait_execution.in_flows[0].get_source()
		exits: CirExecution
		return exits

	def get_caller(self):
		return self.call_execution.get_function()

	def get_callee(self):
		return self.get_callee_entry().get_function()


class CirFunction:
	"""
	A function in C program contains statements that are executed during testing.
	"""
	def __init__(self, graph, name: str):
		graph: CirFunctionCallGraph
		self.graph = graph
		self.name = name
		self.nodes = list()
		self.in_calls = list()
		self.ou_calls = list()
		return

	def get_graph(self):
		return self.graph

	def get_name(self):
		return self.name

	def get_nodes(self):
		return self.nodes

	def get_in_calls(self):
		"""
		:return: callings that raised by another function to call this function
		"""
		return self.in_calls

	def get_ou_calls(self):
		"""
		:return: callings that raised by this function
		"""
		return self.ou_calls


class CirFunctionCallGraph:
	"""
	Function call graph in C program.
	"""
	@staticmethod
	def __extract_execution_id__(text: str):
		beg_index = text.find('[')
		end_index = text.find(']')
		name = text[0: beg_index].strip()
		exec_id = int(text[beg_index + 1: end_index].strip())
		return name, exec_id

	def get_execution_of(self, text_id: str):
		"""
		:param text_id:
		:return: get the execution node w.r.t. the string identifier as function[int]
		"""
		func_name, exec_id = CirFunctionCallGraph.__extract_execution_id__(text_id)
		execution = self.functions[func_name].nodes[exec_id]
		execution: CirExecution
		return execution

	def __init__(self, program: CProgram, func_file_path: str):
		"""
		:param program:
		:param func_file_path: xxx.flw file to generate control flow graph
		"""
		''' 1. construct the functions and execution nodes within the graph '''
		self.functions, node_dict = dict(), dict()
		with open(func_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					if items[0].strip() == "#BegFunc":
						function = CirFunction(self, items[1].strip())
						self.functions[function.get_name()] = function
					elif items[0].strip() == "#EndFunc":
						function: CirFunction
						for k in range(0, len(node_dict)):
							function.nodes.append(node_dict[k])
						node_dict.clear()
					elif items[0].strip() == "#exec":
						func_name, exec_id = CirFunctionCallGraph.__extract_execution_id__(items[1].strip())
						cir_node_id = base.CToken.parse(items[2].strip()).token_value
						cir_node_id: int
						statement = program.cir_tree.nodes[cir_node_id]
						execution = CirExecution(function, exec_id, statement)
						node_dict[execution.get_id()] = execution

		''' 2. construct the calling and execution flows within the functions '''
		with open(func_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					if items[0].strip() == "#flow":
						source = self.get_execution_of(items[2].strip())
						target = self.get_execution_of(items[3].strip())
						source.connect(items[1].strip(), target)
					elif items[0].strip() == "#call":
						call_execution = self.get_execution_of(items[1].strip())
						wait_execution = self.get_execution_of(items[2].strip())
						calling = CirFunctionCall(call_execution, wait_execution)
						calling.get_caller().ou_calls.append(calling)
						calling.get_callee().in_calls.append(calling)

		''' 3. end of all '''
		self.program = program
		return

	def get_program(self):
		return self.program

	def get_functions(self):
		return self.functions.values()

	def get_function(self, name: str):
		return self.functions[name]


class Mutant:
	"""
	Each mutation seeded in source code is defined as:
	[space, id, class, operator, location, parameter]
	"""
	def __init__(self, space, mutant_id: int, mutation_class: str, mutation_operator: str, location: AstNode, parameter: base.CToken):
		"""
		:param space: space where the mutant is created
		:param mutant_id: the integer ID of which the mutant is created
		:param mutation_class: the class of mutation operator
		:param mutation_operator: the mutation operator
		:param location: the location where the mutation is injected
		:param parameter: parameter used to refine the mutation
		"""
		space: MutantSpace
		self.space = space
		self.mutant_id = mutant_id
		self.mutation_class = mutation_class
		self.mutation_operator = mutation_operator
		self.location = location
		self.parameter = parameter
		return

	def get_space(self):
		"""
		:return: space where the mutant is created
		"""
		return self.space

	def get_mutant_id(self):
		"""
		:return: the integer ID of which the mutant is created
		"""
		return self.mutant_id

	def get_mutation_class(self):
		"""
		:return: the class of mutation operator
		"""
		return self.mutation_class

	def get_mutation_operator(self):
		"""
		:return: the mutation operator
		"""
		return self.mutation_operator

	def get_location(self):
		"""
		:return: the location where the mutation is injected
		"""
		return self.location

	def get_parameter(self):
		"""
		:return: parameter used to refine the mutation
		"""
		return self.parameter


class MutantSpace:
	def __init__(self, program: CProgram, mutant_file_path: str):
		"""
		:param program:
		:param mutant_file_path: xxx.mut to generate mutations in program
		"""
		self.program = program
		self.mutants = list()
		mutants_dict = dict()
		with open(mutant_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					mutant_id = int(items[0].strip())
					mutation_class = items[1].strip()
					mutation_operator = items[2].strip()
					ast_node_id = base.CToken.parse(items[3].strip()).token_value
					ast_node_id: int
					location = program.ast_tree.nodes[ast_node_id]
					parameter = base.CToken.parse(items[4].strip())
					mutant = Mutant(self, mutant_id, mutation_class, mutation_operator, location, parameter)
					mutants_dict[mutant_id] = mutant
		for k in range(0, len(mutants_dict)):
			self.mutants.append(mutants_dict[k])
		return

	def get_program(self):
		return self.program

	def get_mutants(self):
		return self.mutants

	def get_mutant(self, mutant_id: int):
		return self.mutants[mutant_id]


class TestCase:
	def __init__(self, space, test_id: int, parameter: str):
		"""
		:param space: where the test case is created
		:param test_id: the integer ID of the test case
		:param parameter: the parameter to execute this test case
		"""
		space: TestSpace
		self.space = space
		self.test_id = test_id
		self.parameter = parameter
		return

	def get_space(self):
		return self.space

	def get_test_id(self):
		return self.test_id

	def get_parameter(self):
		return self.parameter


class TestSpace:
	def __init__(self, program: CProgram, test_file_path: str):
		"""
		:param program:
		:param test_file_path: xxx.tst to generate the test cases
		"""
		self.program = program
		self.tests = list()
		tests_dict = dict()
		with open(test_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					index = line.find(':')
					test_id = int(line[0: index].strip())
					parameter = line[index + 1:].strip()
					test_case = TestCase(self, test_id, parameter)
					tests_dict[test_id] = test_case
		for k in range(0, len(tests_dict)):
			test_case = tests_dict[k]
			self.tests.append(test_case)
		return

	def get_test_cases(self):
		return self.tests

	def get_test_case(self, test_id: int):
		return self.tests[test_id]


# TODO implement the features and labels


