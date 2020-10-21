"""
It defines the data model to describe structural feature of C programs.
"""

import os
import src.com.jclearn.base as base


class CProgram:
	"""
	It defines the structural data to describe C program and code.
	"""
	def __init__(self, directory: str, name: str):
		"""
		:param directory: where xxx.c, xxx.ast, xxx.cir, xxx.flw are created
		:param name: the name of the source code file, i.e. xxx above
		"""
		c_file_path = os.path.join(directory, name + ".c")
		ast_file_path = os.path.join(directory, name + ".ast")
		cir_file_path = os.path.join(directory, name + ".cir")
		flw_file_path = os.path.join(directory, name + ".flw")
		self.source_code = CSourceCode(self, c_file_path)
		self.ast_tree = AstTree(self, ast_file_path)
		self.cir_tree = CirTree(self, cir_file_path)
		self.function_call_graph = CirFunctionCallGraph(self, flw_file_path)
		return


class CSourceCode:
	"""
	Source code of the C source file.
	"""

	def __init__(self, program: CProgram, c_file_path: str):
		"""
		:param program: C program of the source code
		:param c_file_path: xxx.c
		"""
		self.program = program
		self.text = ""
		self.lines = list()
		self.lines.append(0)
		self.__parse__(c_file_path)
		return

	def get_program(self):
		"""
		:return: program of the source code
		"""
		return self.program

	def get_code(self):
		"""
		:return: the source code text
		"""
		return self.text

	def get_length(self):
		"""
		:return: the length of the source code text
		"""
		return len(self.text)

	def number_of_lines(self):
		"""
		:return: the number of lines in the source code
		"""
		return len(self.lines) - 1

	def get_line_of(self, index: int):
		"""
		:param index: the index to the character of location
		:return: the line in which the character of location is or -1 if the location is out of range
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
		return -1

	def __parse__(self, c_file_path: str):
		"""
		:param c_file_path: xxx.c
		:return: parse the source text in c file into code instance
		"""
		with open(c_file_path, 'r') as reader:
			index = 0
			for line in reader:
				self.text += line
				index = index + len(line)
				self.lines.append(index)
		return


class AstNode:
	"""
	abstract syntax tree node as [tree, class_name, beg_index, end_index, parent, children, data_type, token]
	"""
	def __init__(self, tree, class_name: str, beg_index: int, end_index: int, data_type: base.CToken, token: base.CToken):
		"""
		create an isolated node in abstract syntactic tree.
		:param tree: abstract syntactic tree
		:param class_name: the name of the abstract syntactic tree node
		:param beg_index: the index to the first character in the range of the code to which the node corresponds
		:param end_index: the index next to the final character in the range of the code to which the node corresponds
		:param data_type: the data type hold by the node as expression or type_name
		:param token: the token describing the value hold by the node
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
		:return: abstract syntactic tree
		"""
		return self.tree

	def get_class_name(self):
		"""
		:return: the name of the abstract syntactic tree node
		"""
		return self.class_name

	def get_code(self):
		"""
		:return: the code to which the node corresponds
		"""
		return self.tree.program.source_code.text[self.beg_index: self.end_index]

	def get_line_of(self):
		"""
		:return: the line of the start of the code range to which the node corresponds
		"""
		return self.tree.program.source_code.get_line_of(self.beg_index)

	def get_data_type(self):
		"""
		:return: the data type hold by the node as expression or type_name
		"""
		return self.data_type

	def get_token(self):
		"""
		:return: the token describing the value hold by the node
		"""
		return self.token

	def get_parent(self):
		"""
		:return: parent of the node or None if it is root
		"""
		self.parent: AstNode
		return self.parent

	def get_children(self):
		"""
		:return: children under this node
		"""
		return self.children

	def is_root(self):
		return self.parent is None

	def is_leaf(self):
		return len(self.children) == 0

	def add_child(self, child):
		"""
		:param child:
		:return: add the child in the tail of this node
		"""
		child: AstNode
		child.parent = self
		self.children.append(child)
		return


class AstTree:
	"""
	abstract syntactic tree
	"""
	def __init__(self, program: CProgram, ast_file_path: str):
		"""
		:param program: program of the abstract syntactic tree
		:param ast_file_path: xxx.ast to generate abstract syntactic tree
		"""
		self.program = program
		self.ast_nodes = list()
		self.__parse__(ast_file_path)
		return

	def get_program(self):
		"""
		:return: program of the abstract syntactic tree
		"""
		return self.program

	def get_ast_nodes(self):
		"""
		:return: abstract syntactic tree nodes in the tree
		"""
		return self.ast_nodes

	def __parse__(self, ast_file_path: str):
		"""
		:param ast_file_path: xxx.ast to generate abstract syntactic tree
		:return:
		"""
		''' 1. create the abstract syntactic nodes '''
		ast_nodes_dict = dict()
		with open(ast_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					ast_node_id = int(items[0].strip())
					class_name = items[1].strip()
					beg_index = int(items[2].strip())
					end_index = int(items[3].strip())
					data_type = base.CToken.get_data_type(items[4].strip())
					ast_token = base.CToken.parse(items[5].strip())
					ast_node = AstNode(self, class_name, beg_index, end_index, data_type, ast_token)
					ast_nodes_dict[ast_node_id] = ast_node
		''' 2. create the abstract syntactic edges '''
		for ast_node_id in range(0, len(ast_nodes_dict)):
			self.ast_nodes.append(ast_nodes_dict[ast_node_id])
		with open(ast_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					parent = self.ast_nodes[int(items[0].strip())]
					parent: AstNode
					children_items = items[6].strip().split(' ')
					for k in range(1, len(children_items) - 1):
						child = self.ast_nodes[int(children_items[k].strip())]
						parent.add_child(child)
		return


class CirNode:
	"""
	C-intermediate representation tree node as:
		[tree, class_name, ast_source, parent, children, data_type, cir_token]
	"""
	def __init__(self, tree, class_name: str, ast_source: AstNode, data_type: base.CToken, cir_token: base.CToken):
		"""
		:param tree: C-intermediate representation code tree
		:param class_name: the name of the C-intermediate representation code
		:param ast_source: the abstract syntactic tree node to which this node corresponds
		:param data_type: the data type of the value hold by the node as expression
		:param cir_token: the token of the value of the node as leaf.
		"""
		tree: CirTree
		self.tree = tree
		self.class_name = class_name
		self.ast_source = ast_source
		self.data_type = data_type
		self.cir_token = cir_token
		self.parent = None
		self.children = list()
		return

	def get_tree(self):
		"""
		:return: C-intermediate representation code tree
		"""
		return self.tree

	def get_class_name(self):
		"""
		:return: the name of the C-intermediate representation code
		"""
		return self.class_name

	def has_ast_source(self):
		"""
		:return: whether self.ast_source is not None
		"""
		return self.ast_source is not None

	def get_ast_source(self):
		"""
		:return: the abstract syntactic tree node to which this node corresponds
		"""
		return self.ast_source

	def get_data_type(self):
		"""
		:return: the data type of the value hold by the node as expression
		"""
		return self.data_type

	def get_token(self):
		"""
		:return: the token of the value of the node as leaf.
		"""
		return self.cir_token

	def get_parent(self):
		"""
		:return: parent of this node or None if it is root
		"""
		self.parent: CirNode
		return self.parent

	def get_children(self):
		"""
		:return: the children under this node
		"""
		return self.children

	def is_root(self):
		return self.parent is None

	def is_leaf(self):
		return len(self.children) == 0

	def add_child(self, child):
		child: CirNode
		child.parent = self
		self.children.append(child)
		return


class CirTree:
	"""
	C-intermediate representation tree
	"""
	def __init__(self, program: CProgram, cir_file_path: str):
		"""
		:param cir_file_path: xxx.cir to generate C-intermediate representation
		"""
		self.program = program
		self.cir_nodes = list()
		self.__parse__(cir_file_path)
		return

	def get_program(self):
		return self.program

	def get_cir_nodes(self):
		return self.cir_nodes

	def __parse__(self, cir_file_path: str):
		"""
		:param cir_file_path:
		:return: to construct the C-intermediate representation tree node
		"""
		''' 1. create the nodes in C-intermediate representation tree '''
		cir_nodes_dict = dict()
		with open(cir_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					cir_node_id = int(items[0].strip())
					class_name = items[1].strip()
					ast_node_token = base.CToken.parse(items[2].strip())
					if ast_node_token.token_value is not None:
						ast_node_id = ast_node_token.token_value
						ast_node_id: int
						ast_source = self.program.ast_tree.ast_nodes[ast_node_id]
					else:
						ast_source = None
					data_type = base.CToken.get_data_type(items[3].strip())
					cir_token = base.CToken.parse(items[4].strip())
					cir_node = CirNode(self, class_name, ast_source, data_type, cir_token)
					cir_nodes_dict[cir_node_id] = cir_node
		''' 2. create the edges in C-intermediate representation tree '''
		for cir_node_id in range(0, len(cir_nodes_dict)):
			self.cir_nodes.append(cir_nodes_dict[cir_node_id])
		with open(cir_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					parent = self.cir_nodes[int(items[0].strip())]
					parent: CirNode
					children_items = items[5].strip().split(' ')
					for k in range(1, len(children_items) - 1):
						child = self.cir_nodes[int(children_items[k].strip())]
						parent.add_child(child)
		return


class CirExecutionFlow:
	def __init__(self, flow_type: str, source, target):
		source: CirExecution
		target: CirExecution
		self.flow_type = flow_type
		self.source = source
		self.target = target
		return

	def get_flow_type(self):
		"""
		:return: the type of the execution flow
		"""
		return self.flow_type

	def get_source(self):
		"""
		:return: statement from which the flow points to another
		"""
		return self.source

	def get_target(self):
		"""
		:return: statement to which the flow points from another
		"""
		return self.target


class CirExecution:
	"""
	Each node in execution flow graph refers to a statement in C-intermediate representation.
	"""
	def __init__(self, function, exec_id: int, statement: CirNode):
		"""
		create an isolated node in execution flow graph of the function
		:param function:
		:param statement:
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
		:return: the function of the node
		"""
		return self.function

	def get_statement(self):
		"""
		:return: the statement to which the node refers
		"""
		return self.statement

	def get_in_flows(self):
		"""
		:return: flows pointing to this node
		"""
		return self.in_flows

	def get_ou_flows(self):
		"""
		:return: flows pointing from this node to others
		"""
		return self.ou_flows

	def link_to(self, flow_type: str, target):
		"""
		:param flow_type:
		:param target:
		:return: flow that links from this node to the target with specified type
		"""
		target: CirExecution
		flow = CirExecutionFlow(flow_type, self, target)
		self.ou_flows.append(flow)
		target.in_flows.append(flow)
		return flow

	def __str__(self):
		return self.function.name + "[" + str(self.exec_id) + "]"


class CirFunction:
	def __init__(self, graph, name: str):
		"""
		:param graph: function call graph of the instance
		:param name: the name of this function in program
		"""
		graph: CirFunctionCallGraph
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

	def get_execution(self, execution_id: int):
		return self.executions[execution_id]

	def get_in_calls(self):
		"""
		:return: the relations that others call this function
		"""
		return self.in_calls

	def get_ou_calls(self):
		"""
		:return: the relations that call other functions in this one
		"""
		return self.ou_calls


class CirFunctionCall:
	def __init__(self, call_execution: CirExecution, wait_execution: CirExecution):
		"""
		:param call_execution:
		:param wait_execution:
		"""
		self.call_execution = call_execution
		self.wait_execution = wait_execution
		return

	def get_call_execution(self):
		return self.call_execution

	def get_wait_execution(self):
		return self.wait_execution

	def get_enty_execution(self):
		flow = self.call_execution.ou_flows[0]
		flow: CirExecutionFlow
		return flow.get_target()

	def get_exit_execution(self):
		flow = self.wait_execution.in_flows[0]
		flow: CirExecutionFlow
		return flow.get_source()

	def get_callee(self):
		return self.get_enty_execution().get_function()

	def get_caller(self):
		return self.call_execution.get_function()


class CirFunctionCallGraph:
	"""
	C function call graph
	"""
	def __init__(self, program: CProgram, flw_file_path: str):
		"""
		:param program:
		:param flw_file_path: xxx.flw to generate execution flow graphs
		"""
		self.program = program
		self.functions = dict()
		self.__parse__(flw_file_path)
		return

	def get_program(self):
		return self.program

	def get_functions(self):
		return self.functions.values()

	def get_function(self, name: str):
		function = self.functions[name]
		function: CirFunction
		return function

	def get_execution_by_id(self, text_id: str):
		"""
		:param text_id: function_name[execution_index]
		:return:
		"""
		name, eid = CirFunctionCallGraph.__extract_function_and_id__(text_id)
		function = self.functions[name]
		function: CirFunction
		execution = function.executions[eid]
		execution: CirExecution
		return execution

	@staticmethod
	def __extract_function_and_id__(text: str):
		beg_index = text.find('[')
		end_index = text.find(']')
		return text[0: beg_index].strip(), int(text[beg_index + 1: end_index].strip())

	def __parse__(self, flw_file_path: str):
		"""
		:param flw_file_path:
		:return:
		"""
		''' 1. create functions and their execution nodes '''
		executions_dict = dict()
		with open(flw_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					head = items[0].strip()
					if head == "#BegFunc":
						function = CirFunction(self, items[1].strip())
						self.functions[function.get_name()] = function
					elif head == "#EndFunc":
						for k in range(0, len(executions_dict)):
							execution = executions_dict[k]
							execution: CirExecution
							function.executions.append(execution)
						executions_dict.clear()
					elif head == "#exec":
						name, eid = CirFunctionCallGraph.__extract_function_and_id__(items[1].strip())
						cir_node_id = base.CToken.parse(items[2].strip()).token_value
						statement = self.program.cir_tree.cir_nodes[cir_node_id]
						executions_dict[eid] = CirExecution(function, eid, statement)
		''' 2. create function calls and execution flows '''
		with open(flw_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					head = items[0].strip()
					if head == "#flow":
						flow_type = items[1].strip()
						source = self.get_execution_by_id(items[2].strip())
						target = self.get_execution_by_id(items[3].strip())
						source.link_to(flow_type, target)
					elif head == "#call":
						call_execution = self.get_execution_by_id(items[1].strip())
						wait_execution = self.get_execution_by_id(items[2].strip())
						calling = CirFunctionCall(call_execution, wait_execution)
						calling.get_caller().ou_calls.append(calling)
						calling.get_callee().in_calls.append(calling)
		return


def output_ast_tree(ast_tree: AstTree, output_directory: str, file_name: str):
	output_file_path = os.path.join(output_directory, file_name + ".ast")
	with open(output_file_path, 'w') as writer:
		for ast_node in ast_tree.get_ast_nodes():
			ast_node: AstNode
			writer.write(ast_node.get_class_name() + "\t")
			writer.write(str(ast_node.get_line_of()) + "\t")
			writer.write(str(ast_node.get_data_type().token_value) + "\t")
			writer.write(str(ast_node.get_token().token_value) + "\t")
			ast_code = ast_node.get_code()
			for ch in ast_code:
				if ch.isspace():
					writer.write(' ')
				else:
					writer.write(ch)
			writer.write("\n")
	return


if __name__ == "__main__":
	root_directory = "/home/dzt2/Development/Data/features/"
	post_directory = "/home/dzt2/Development/Code/GitProject/jcsa/JCLearn/output"
	for file_name in os.listdir(root_directory):
		directory = os.path.join(root_directory, file_name)
		program = CProgram(directory, file_name)
		output_directory = os.path.join(post_directory, file_name)
		if not os.path.exists(output_directory):
			os.mkdir(output_directory)
		output_ast_tree(program.ast_tree, output_directory, file_name)
		print("Complete parsing on program of", file_name)
