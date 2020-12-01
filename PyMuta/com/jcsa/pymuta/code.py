"""
It implements the model to describe elements in C source code.
"""

import os
import com.jcsa.pymuta.base as base


class CProgram:
	def __init__(self, directory: str, name: str):
		cpp_file_path = os.path.join(directory, name + ".c")
		ast_file_path = os.path.join(directory, name + ".ast")
		cir_file_path = os.path.join(directory, name + ".cir")
		flw_file_path = os.path.join(directory, name + ".flw")
		self.source_code = CSourceCode(self, cpp_file_path)
		self.ast_tree = AstTree(self, ast_file_path)
		self.cir_tree = CirTree(self, cir_file_path)
		self.function_call_graph = CirFunctionCallGraph(self, flw_file_path)
		return


class CSourceCode:
	"""
	To access the source code by line index
	"""
	def __init__(self, program: CProgram, cpp_file_path: str):
		"""
		read the code in cpp file to the object
		:param cpp_file_path:
		"""
		self.program = program
		self.text = ""
		self.lines = list()
		with open(cpp_file_path, 'r') as reader:
			for line in reader:
				self.lines.append(len(self.text))
				self.text += line
			self.lines.append(len(self.text))
		return

	def get_program(self):
		return self.program

	def get_text(self):
		return self.text

	def get_code(self, beg_index: int, end_index: int):
		return self.text[beg_index: end_index]

	def number_of_lines(self):
		return len(self.lines) - 1

	def get_line(self, line: int):
		"""
		:param line: start from 0 to n - 1
		:return: the code at specified line
		"""
		beg_index = self.lines[line]
		end_index = self.lines[line + 1]
		return self.text[beg_index: end_index]

	def line_of(self, index: int):
		"""
		:param index:
		:return: the line to which the character of specified index belongs
		"""
		beg_line, end_line = 0, len(self.lines) - 1
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
		return None		# not found


class AstNode:
	"""
	Abstract syntax tree node as id class beg_index end_index type token parent children
	"""
	def __init__(self, tree, ast_id: int, class_name: str, beg_index: int, end_index: int, data_type: base.CToken, token: base.CToken):
		"""
		:param tree: abstract syntactic tree
		:param ast_id:
		:param class_name:
		:param beg_index:
		:param end_index:
		:param data_type:
		:param token:
		"""
		tree: AstTree
		self.tree = tree
		self.ast_id = ast_id
		self.class_name = class_name
		self.beg_index = beg_index
		self.end_index = end_index
		self.data_type = data_type
		self.token = token
		self.parent = None
		self.children = list()
		return

	def get_tree(self):
		return self.tree

	def get_ast_id(self):
		return self.ast_id

	def get_class_name(self):
		return self.class_name

	def get_beg_index(self):
		return self.beg_index

	def get_end_index(self):
		return self.end_index

	def get_code(self, strip: bool):
		c_program = self.tree.program
		c_program: CProgram
		code = c_program.source_code.get_code(self.beg_index, self.end_index)
		if strip:
			strip_code = ""
			for k in range(0, len(code)):
				ch = code[k]
				if ch.isspace():
					ch = ' '
				strip_code += ch
			code = strip_code
		return code

	def get_line(self):
		c_program = self.tree.program
		c_program: CProgram
		return c_program.source_code.line_of(self.beg_index)

	def get_data_type(self):
		return self.data_type

	def get_token(self):
		return self.token

	def get_parent(self):
		self.parent: AstNode
		return self.parent

	def get_children(self):
		return self.children

	def add_child(self, child):
		child: AstNode
		child.parent = self
		self.children.append(child)
		return

	def __str__(self):
		return str(self.ast_id) + "; " + self.class_name + "; " + str(self.beg_index) + " --> " + str(self.end_index) + "; " + str(self.data_type) + "; " + str(self.token) + ";"


class AstTree:
	def __init__(self, program: CProgram, ast_file_path: str):
		self.program = program
		self.ast_nodes = list()
		self.__parse__(ast_file_path)
		return

	def get_program(self):
		return self.program

	def get_ast_nodes(self):
		return self.ast_nodes

	def get_ast_node(self, ast_id: int):
		node = self.ast_nodes[ast_id]
		node: AstNode
		return node

	def __parse__(self, ast_file_path: str):
		ast_node_dict = dict()
		with open(ast_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					ast_key = base.CToken.parse(items[0].strip())
					class_name = items[1].strip()
					beg_index = int(items[2].strip())
					end_index = int(items[3].strip())
					data_type = base.CToken.parse(items[4].strip())
					token = base.CToken.parse(items[5].strip())
					ast_node = AstNode(self, ast_key.token_value, class_name, beg_index, end_index, data_type, token)
					ast_node_dict[ast_node.ast_id] = ast_node
		with open(ast_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					parent_key = base.CToken.parse(items[0].strip())
					parent = ast_node_dict[parent_key.token_value]
					children_keys = items[6].strip().split(' ')
					for k in range(1, len(children_keys) - 1):
						child_key = base.CToken.parse(children_keys[k].strip())
						child = ast_node_dict[child_key.token_value]
						parent.add_child(child)
		self.ast_nodes.clear()
		for k in range(0, len(ast_node_dict)):
			self.ast_nodes.append(ast_node_dict[k])
		return


class CirNode:
	"""
	C-intermediate representation node
	"""
	def __init__(self, tree, cir_id: int, class_name: str, ast_source: AstNode, data_type: base.CToken, token: base.CToken):
		tree: CirTree
		self.tree = tree
		self.cir_id = cir_id
		self.class_name = class_name
		self.ast_source = ast_source
		self.data_type = data_type
		self.token = token
		self.parent = None
		self.children = list()
		return

	def get_tree(self):
		return self.tree

	def get_cir_id(self):
		return self.cir_id

	def has_ast_source(self):
		return not(self.ast_source is None)

	def get_ast_source(self):
		return self.ast_source

	def get_class_name(self):
		return self.class_name

	def get_data_type(self):
		return self.data_type

	def get_token(self):
		return self.token

	def get_parent(self):
		return self.parent

	def get_children(self):
		return self.children

	def add_child(self, child):
		child: CirNode
		child.parent = self
		self.children.append(child)
		return


class CirTree:
	def __init__(self, program: CProgram, cir_file_path: str):
		self.program = program
		self.cir_nodes = list()
		self.__parse__(cir_file_path)
		return

	def get_program(self):
		return self.program

	def get_cir_nodes(self):
		return self.cir_nodes

	def get_cir_node(self, cir_id: int):
		node = self.cir_nodes[cir_id]
		node: CirNode
		return node

	def __parse__(self, cir_file_path: str):
		cir_node_dict = dict()
		ast_tree = self.program.ast_tree
		with open(cir_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					cir_key = base.CToken.parse(items[0].strip())
					class_name = items[1].strip()
					ast_key = base.CToken.parse(items[2].strip())
					data_type = base.CToken.parse(items[3].strip())
					token = base.CToken.parse(items[4].strip())
					ast_node = None
					if not(ast_key.get_token_value() is None):
						ast_node = ast_tree.get_ast_node(ast_key.get_token_value())
					cir_node = CirNode(self, cir_key.token_value, class_name, ast_node, data_type, token)
					cir_node_dict[cir_node.cir_id] = cir_node
		with open(cir_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					parent_key = base.CToken.parse(items[0].strip())
					parent = cir_node_dict[parent_key.token_value]
					children_keys = items[5].strip().split(' ')
					for k in range(1, len(children_keys) - 1):
						child_key = base.CToken.parse(children_keys[k].strip())
						child = cir_node_dict[child_key.token_value]
						parent.add_child(child)
		self.cir_nodes.clear()
		for k in range(0, len(cir_node_dict)):
			self.cir_nodes.append(cir_node_dict[k])
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
		return self.flow_type

	def get_source(self):
		return self.source

	def get_target(self):
		return self.target


class CirExecution:
	def __init__(self, function, exec_id: int, statement: CirNode):
		function: CirFunction
		self.function = function
		self.exec_id = exec_id
		self.statement = statement
		self.in_flows = list()
		self.ou_flows = list()
		return

	def get_function(self):
		return self.function

	def get_exec_id(self):
		return self.exec_id

	def get_statement(self):
		return self.statement

	def link_to(self, target, flow_type: str):
		target: CirExecution
		flow = CirExecutionFlow(flow_type, self, target)
		self.ou_flows.append(flow)
		target.in_flows.append(flow)
		return flow

	def __str__(self):
		return self.function.name + "[" + str(self.exec_id) + "]"


class CirFunctionCall:
	def __init__(self, call_execution: CirExecution, wait_execution: CirExecution):
		self.call_execution = call_execution
		self.wait_execution = wait_execution
		return

	def get_caller(self):
		return self.call_execution.function

	def get_call_execution(self):
		return self.call_execution

	def get_call_flow(self):
		flow = self.call_execution.ou_flows[0]
		flow: CirExecutionFlow
		return flow

	def get_callee_entry(self):
		return self.get_call_flow().target

	def get_callee(self):
		return self.get_callee_entry().function

	def get_wait_execution(self):
		return self.wait_execution

	def get_return_flow(self):
		flow = self.wait_execution.in_flows[0]
		flow: CirExecutionFlow
		return flow

	def get_return_execution(self):
		return self.get_return_flow().get_source()


class CirFunction:
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

	def get_execution(self, exec_id: int):
		execution = self.executions[exec_id]
		execution: CirExecution
		return execution

	def get_in_calls(self):
		return self.in_calls

	def get_ou_calls(self):
		return self.ou_calls

	@staticmethod
	def calling(call_execution: CirExecution, wait_execution: CirExecution):
		call = CirFunctionCall(call_execution, wait_execution)
		caller = call.get_caller()
		callee = call.get_callee()
		caller.ou_calls.append(call)
		callee.in_calls.append(call)
		return call


class CirFunctionCallGraph:
	def __init__(self, program: CProgram, flw_file_path: str):
		self.program = program
		self.functions = dict()
		self.__parse__(flw_file_path)
		return

	def get_program(self):
		return self.program

	def get_functions(self):
		return self.functions

	def get_function(self, name: str):
		function = self.functions[name]
		function: CirFunction
		return function

	def get_execution(self, name: str, exec_id: int):
		return self.get_function(name).get_execution(exec_id)

	def get_execution_of(self, key: base.CToken):
		name = key.token_value[0]
		exec_id = key.token_value[1]
		return self.get_execution(name, exec_id)

	def __parse__(self, flw_file_path: str):
		self.functions.clear()
		with open(flw_file_path, 'r') as reader:
			execution_dict = dict()
			for line in reader:
				line = line.strip()
				items = line.split('\t')
				if line.startswith("[func]"):
					function = CirFunction(self, items[1].strip())
					self.functions[function.get_name()] = function
				elif line.startswith("[node]"):
					exec_id = int(items[3].strip())
					cir_key = base.CToken.parse(items[4].strip())
					statement = self.program.cir_tree.get_cir_node(cir_key.token_value)
					execution = CirExecution(function, exec_id, statement)
					execution_dict[execution.exec_id] = execution
				elif line.startswith("[end_func]"):
					for k in range(0, len(execution_dict)):
						function.executions.append(execution_dict[k])
					execution_dict.clear()
		with open(flw_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				items = line.split('\t')
				if line.startswith("[edge]"):
					flow_type = items[1].strip()
					source_key = base.CToken.parse(items[2].strip())
					target_key = base.CToken.parse(items[3].strip())
					source = self.get_execution_of(source_key)
					target = self.get_execution_of(target_key)
					source.link_to(target, flow_type)
		with open(flw_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				items = line.split('\t')
				if line.startswith("[call]"):
					call_key = base.CToken.parse(items[1].strip())
					wait_key = base.CToken.parse(items[2].strip())
					call_execution = self.get_execution_of(call_key)
					wait_execution = self.get_execution_of(wait_key)
					CirFunction.calling(call_execution, wait_execution)
		return


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	for file_name in os.listdir(root_path):
		directory = os.path.join(root_path, file_name)
		program = CProgram(directory, file_name)
		print("Read", len(program.function_call_graph.get_functions()), "functions from", file_name)
		for name, c_function in program.function_call_graph.get_functions().items():
			c_function: CirFunction
			print("\tdef", c_function.name)
			for execution in c_function.get_executions():
				execution: CirExecution
				code = "[ "
				for ou_flow in execution.ou_flows:
					ou_flow: CirExecutionFlow
					code += ou_flow.flow_type + ":"
					code += str(ou_flow.target)
					code += "; "
				code += "]"
				print("\t\t", str(execution), "\t==>", code)
		print()
