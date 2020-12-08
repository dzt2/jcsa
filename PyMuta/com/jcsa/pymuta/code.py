"""
This file implements the data model of source code.
"""

import os
import com.jcsa.pymuta.base as cbase


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
	It provides access to source code by line
	"""
	def __init__(self, program: CProgram, cpp_file_path: str):
		self.program = program
		self.text = ""
		self.lines = list()
		with open(cpp_file_path, 'r') as reader:
			self.lines.append(len(self.text))
			for line in reader:
				self.text += line
				self.lines.append(len(self.text))
		return

	def get_program(self):
		return self.program

	def get_source_code(self):
		"""
		:return: the entire text of source code
		"""
		return self.text

	def get_code(self, beg_index: int, end_index: int):
		"""
		:param beg_index: index to the first character in range
		:param end_index: index next to the final character in the range
		:return: the code in the specified range
		"""
		return self.text[beg_index: end_index]

	def number_of_lines(self):
		"""
		:return: number of lines
		"""
		return len(self.lines) - 1

	def get_line_code(self, line: int):
		"""
		:param line: start from 0 as the first line
		:return: the code of kth line
		"""
		beg_index = self.lines[line]
		end_index = self.lines[line + 1]
		return self.get_code(beg_index, end_index)

	def line_of(self, index: int):
		"""
		:param index:
		:return: line in which the kth character is in the source code or None if not belongs to the range
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
		return None


class AstNode:
	"""
	abstract syntactic tree node
	"""
	def __init__(self, tree, ast_id: int, class_name: str, beg_index: int, end_index: int, data_type: cbase.CToken, token: cbase.CToken):
		"""
		:param tree:
		:param ast_id:
		:param class_name:
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

	def line_of(self):
		return self.tree.program.source_code.line_of(self.beg_index) + 1

	def get_code(self, strip=False):
		"""
		:param strip: whether to strip \n\t as space
		:return:
		"""
		code = self.tree.program.source_code.get_code(self.beg_index, self.end_index)
		if strip:
			new_code = ""
			for k in range(0, len(code)):
				ch = code[k]
				if ch.isspace():
					ch = ' '
				new_code += ch
			code = new_code
		return code

	def get_data_type(self):
		return self.data_type

	def get_token(self):
		return self.token

	def is_root(self):
		return self.parent is None

	def get_parent(self):
		self.parent: AstNode
		return self.parent

	def is_leaf(self):
		return len(self.children) == 0

	def get_children(self):
		return self.children

	def get_child(self, k: int):
		child = self.children[k]
		child: AstNode
		return child

	def add_child(self, child):
		"""
		:param child:
		:return:
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
		:param program:
		:param ast_file_path:
		"""
		self.program = program
		self.ast_nodes = list()
		self.__parse__(ast_file_path)
		return

	def get_program(self):
		return self.program

	def get_ast_nodes(self):
		return self.ast_nodes

	def get_ast_node(self, ast_id: int):
		ast_node = self.ast_nodes[ast_id]
		ast_node: AstNode
		return ast_node

	def __parse__(self, ast_file_path: str):
		"""
		:param ast_file_path:
		:return:
		"""
		ast_node_dict = dict()
		with open(ast_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					ast_id = cbase.CToken.parse(items[0].strip()).get_token_value()
					class_name = items[1].strip()
					beg_index = int(items[2].strip())
					end_index = int(items[3].strip())
					data_type = cbase.CToken.parse(items[4].strip())
					token = cbase.CToken.parse(items[5].strip())
					ast_node = AstNode(self, ast_id, class_name, beg_index, end_index, data_type, token)
					ast_node_dict[ast_node.get_ast_id()] = ast_node
		with open(ast_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					parent_id = cbase.CToken.parse(items[0].strip()).get_token_value()
					parent = ast_node_dict[parent_id]
					children_ids = items[6].strip().split(' ')
					for k in range(1, len(children_ids) - 1):
						child_id = cbase.CToken.parse(children_ids[k].strip()).token_value
						child = ast_node_dict[child_id]
						parent.add_child(child)
		self.ast_nodes.clear()
		for k in range(0, len(ast_node_dict)):
			self.ast_nodes.append(ast_node_dict[k])
		return


class CirNode:
	"""
	C-intermediate representation node
	"""
	def __init__(self, tree, cir_id: int, class_name: str, ast_source: AstNode, data_type: cbase.CToken, token: cbase.CToken, cir_code: str):
		"""
		:param tree:
		:param cir_id:
		:param class_name:
		:param ast_source:
		:param data_type:
		:param token:
		:param cir_code:
		"""
		tree: CirTree
		self.tree = tree
		self.cir_id = cir_id
		self.class_name = class_name
		self.ast_source = ast_source
		self.data_type = data_type
		self.token = token
		self.cir_code = cir_code.strip()
		self.parent = None
		self.children = list()
		return

	def get_tree(self):
		return self.tree

	def get_cir_id(self):
		return self.cir_id

	def get_class_name(self):
		return self.class_name

	def get_data_type(self):
		return self.data_type

	def get_token(self):
		return self.token

	def get_ast_source(self):
		return self.ast_source

	def has_ast_source(self):
		return self.ast_source is not None

	def get_ast_code(self, strip: bool):
		if self.ast_source is None:
			return None
		return self.ast_source.get_code(strip)

	def get_cir_code(self):
		return self.cir_code

	def get_parent(self):
		self.parent: CirNode
		return self.parent

	def is_root(self):
		return self.parent is None

	def get_children(self):
		return self.children

	def get_child(self, k: int):
		child = self.children[k]
		child: CirNode
		return child

	def is_leaf(self):
		return len(self.children) == 0

	def add_child(self, child):
		"""
		:param child:
		:return:
		"""
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
		:param program:
		:param cir_file_path:
		"""
		self.program = program
		self.cir_nodes = list()
		self.__parse__(cir_file_path)
		return

	def get_program(self):
		return self.program

	def get_cir_nodes(self):
		return self.cir_nodes

	def get_cir_node(self, cir_id: int):
		"""
		:param cir_id:
		:return:
		"""
		cir_node = self.cir_nodes[cir_id]
		cir_node: CirNode
		return cir_node

	def __parse__(self, cir_file_path: str):
		"""
		:param cir_file_path:
		:return:
		"""
		cir_node_dict = dict()
		with open(cir_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					cir_id = cbase.CToken.parse(items[0].strip()).get_token_value()
					class_name = items[1].strip()
					ast_id = cbase.CToken.parse(items[2].strip()).get_token_value()
					ast_source = None
					if ast_id is not None:
						ast_source = self.program.ast_tree.get_ast_node(ast_id)
					data_type = cbase.CToken.parse(items[3].strip())
					token = cbase.CToken.parse(items[4].strip())
					cir_code = cbase.CToken.parse(items[6].strip()).get_token_value()
					if cir_code is None:
						cir_code = ""
					cir_node = CirNode(self, cir_id, class_name, ast_source, data_type, token, cir_code)
					cir_node_dict[cir_node.get_cir_id()] = cir_node
		with open(cir_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					parent_id = cbase.CToken.parse(items[0].strip()).get_token_value()
					parent = cir_node_dict[parent_id]
					children_ids = items[5].strip().split(' ')
					for k in range(1, len(children_ids) - 1):
						child_id = cbase.CToken.parse(children_ids[k].strip()).get_token_value()
						child = cir_node_dict[child_id]
						parent.add_child(child)
		self.cir_nodes.clear()
		for k in range(0, len(cir_node_dict)):
			self.cir_nodes.append(cir_node_dict[k])
		return


class CirExecutionFlow:
	"""
	flow, source, target
	"""
	def __init__(self, flow_type: str, source, target):
		"""
		:param flow_type:
		:param source:
		:param target:
		"""
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

	def __str__(self):
		return self.flow_type + "(" + str(self.source) + " --> " + str(self.target) + ")"


class CirExecution:
	"""
	function, exe_id, statement
	"""
	def __init__(self, function, exe_id: int, statement: CirNode):
		"""
		:param function:
		:param exe_id:
		:param statement:
		"""
		function: CirFunction
		self.function = function
		self.exe_id = exe_id
		self.statement = statement
		self.in_flows = list()
		self.ou_flows = list()
		return

	def get_function(self):
		return self.function

	def get_exe_id(self):
		return self.exe_id

	def get_statement(self):
		return self.statement

	def get_in_flows(self):
		return self.in_flows

	def get_ou_flows(self):
		return self.ou_flows

	def get_in_flow(self, k: int):
		flow = self.in_flows[k]
		flow: CirExecutionFlow
		return flow

	def get_ou_flow(self, k: int):
		flow = self.ou_flows[k]
		flow: CirExecutionFlow
		return flow

	def __str__(self):
		return self.function.get_name() + "[" + str(self.exe_id) + "]"

	def link(self, target, flow_type: str):
		"""
		:param target:
		:param flow_type:
		:return:
		"""
		target: CirExecution
		flow = CirExecutionFlow(flow_type, self, target)
		self.ou_flows.append(flow)
		target.in_flows.append(flow)
		return flow


class CirFunctionCall:
	"""
	call_exec, wait_exec
	"""
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

	def get_caller(self):
		return self.call_execution.get_function()

	def get_call_flow(self):
		return self.call_execution.get_ou_flow(0)

	def get_wait_execution(self):
		return self.wait_execution

	def get_retr_flow(self):
		return self.wait_execution.get_in_flow(0)

	def get_callee(self):
		return self.get_call_flow().get_target().get_function()

	def __str__(self):
		return "CALL{" + str(self.call_execution) + " --> " + str(self.wait_execution) + "}"

	@staticmethod
	def calling(call_execution: CirExecution, wait_execution: CirExecution):
		"""
		:param call_execution:
		:param wait_execution:
		:return: generate the calling relationship on functions
		"""
		call = CirFunctionCall(call_execution,wait_execution)
		call.get_caller().ou_calls.append(call)
		call.get_callee().in_calls.append(call)
		return call


class CirFunction:
	def __init__(self, graph, name: str):
		"""
		:param graph:
		:param name:
		"""
		graph: CirFunctionCallGraph
		self.graph = graph
		self.name = name.strip()
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

	def get_execution(self, exe_id: int):
		execution = self.executions[exe_id]
		execution: CirExecution
		return execution

	def get_in_calls(self):
		return self.in_calls

	def get_ou_calls(self):
		return self.ou_calls

	def get_in_call(self, k: int):
		call = self.in_calls[k]
		call: CirFunctionCall
		return call

	def get_ou_call(self, k: int):
		call = self.ou_calls[k]
		call: CirFunctionCall
		return call

	def __str__(self):
		return self.name


class CirFunctionCallGraph:
	def __init__(self, program: CProgram, flw_file_path: str):
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

	def get_execution(self, name: str, exe_id: int):
		return self.get_function(name).get_execution(exe_id)

	def __parse__(self, flw_file_path: str):
		"""
		:param flw_file_path:
		:return:
		"""
		with open(flw_file_path, 'r') as reader:
			execution_dict = dict()
			for line in reader:
				line = line.strip()
				items = line.split('\t')
				if line.startswith("[beg]"):
					function = CirFunction(self, items[1].strip())
					self.functions[function.get_name()] = function
					execution_dict.clear()
				elif line.startswith("[node]"):
					exe_token = cbase.CToken.parse(items[1].strip())
					name = exe_token.token_value[0]
					exe_id = exe_token.token_value[1]
					cir_id = cbase.CToken.parse(items[2].strip()).get_token_value()
					statement = self.program.cir_tree.get_cir_node(cir_id)
					execution = CirExecution(function, exe_id, statement)
					execution_dict[execution.get_exe_id()] = execution
				elif line.startswith("[end]"):
					function.executions.clear()
					for k in range(0, len(execution_dict)):
						function.executions.append(execution_dict[k])
		with open(flw_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				items = line.split('\t')
				if line.startswith("[edge]"):
					flow_type = items[1].strip()
					source_token = cbase.CToken.parse(items[2].strip()).get_token_value()
					target_token = cbase.CToken.parse(items[3].strip()).get_token_value()
					source = self.get_execution(source_token[0], source_token[1])
					target = self.get_execution(target_token[0], target_token[1])
					source.link(target, flow_type)
				elif line.startswith("[call]"):
					source_token = cbase.CToken.parse(items[1].strip()).get_token_value()
					target_token = cbase.CToken.parse(items[2].strip()).get_token_value()
					source = self.get_execution(source_token[0], source_token[1])
					target = self.get_execution(target_token[0], target_token[1])
					CirFunctionCall.calling(source, target)
		return


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	for file_name in os.listdir(root_path):
		directory = os.path.join(root_path, file_name)
		c_program = CProgram(directory, file_name)
		print("Testing on AST for", file_name + ".c")
		for c_function in c_program.function_call_graph.get_functions():
			c_function: CirFunction
			print("\t[beg]\t", c_function.get_name())
			for execution in c_function.get_executions():
				execution: CirExecution
				print("\t\t[exe]\t", execution, "\t\"", execution.get_statement().get_cir_code(), "\"")
				for ou_flow in execution.get_ou_flows():
					print("\t\t\t[flw]\t", ou_flow)
			print("\t[end]\t", c_function)
		print()

