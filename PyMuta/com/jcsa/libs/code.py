"""
This file defines data model to represent SourceCode, AstTree, CirTree, CirExecution in CProgram
"""


import os
import com.jcsa.libs.base as jcbase


class CProgram:
	def __init__(self, directory: str, file_name: str):
		self.directory = directory
		self.name = file_name
		cpp_file_path = os.path.join(directory, file_name + ".c")
		ast_file_path = os.path.join(directory, file_name + ".ast")
		cir_file_path = os.path.join(directory, file_name + ".cir")
		flw_file_path = os.path.join(directory, file_name + ".flw")
		ins_file_path = os.path.join(directory, file_name + ".ins")
		dep_file_path = os.path.join(directory, file_name + ".dep")
		self.source_code = CSourceCode(self, cpp_file_path)
		self.ast_tree = AstTree(self, ast_file_path)
		self.cir_tree = CirTree(self, cir_file_path)
		self.function_call_graph = CirFunctionCallGraph(self, flw_file_path)
		self.instance_graph = CirInstanceGraph(self, ins_file_path)
		self.dependence_graph = CDependenceGraph(self, dep_file_path)
		return


class CSourceCode:
	"""
	It manages the access to source by lines
	"""
	def __init__(self, program: CProgram, cpp_file: str):
		self.program = program
		self.text = ""
		self.index = list()
		self.__parse__(cpp_file)
		return

	def __parse__(self, cpp_file: str):
		self.text = ""
		self.index.clear()
		with open(cpp_file, 'r') as reader:
			self.index.append(len(self.text))
			for line in reader:
				self.text += line
				self.index.append(len(self.text))
		return

	def get_text(self):
		"""
		:return: the entire source code read from file
		"""
		return self.text

	def get_length(self):
		"""
		:return: length of the entire source code
		"""
		return len(self.text)

	def number_of_lines(self):
		"""
		:return: number of lines in the source code
		"""
		return len(self.index) - 1

	def get_code(self, beg_index: int, end_index: int):
		"""
		:param beg_index: index to the first character in code range
		:param end_index: index next to the final char in code range
		:return:
		"""
		return self.text[beg_index: end_index]

	def get_code_at_line(self, line: int):
		"""
		:param line: start from 0 to n - 1
		:return: code at kth line
		"""
		beg_index = self.index[line]
		end_index = self.index[line + 1]
		return self.get_code(beg_index, end_index)

	def line_of(self, index: int):
		"""
		:param index:
		:return: the line at which the character w.r.t. index is located or None
		"""
		beg_line, end_line = 0, len(self.index) - 1
		while beg_line <= end_line:
			mid_line = (beg_line + end_line) // 2
			beg_index = self.index[mid_line]
			end_index = self.index[mid_line + 1]
			if index < beg_index:
				end_line = mid_line - 1
			elif index >= end_index:
				beg_line = mid_line + 1
			else:
				return mid_line
		return None

	def __str__(self):
		return self.text

	def __len__(self):
		return len(self.text)


class AstNode:
	"""
	abstract syntactic node
	"""
	def __init__(self, tree, ast_id: int, class_name: str, beg_index: int, end_index: int,
				 data_type: jcbase.CToken, content: jcbase.CToken):
		"""
		:param tree: tree where the node is created
		:param ast_id: integer ID to tag this node in the tree
		:param class_name: name of syntactic class
		:param beg_index: index to the first character in the code range of the node
		:param end_index: index next to the final char in the code range of the node
		:param data_type: data type hold by the value of the node
		:param content: token to refine the syntactic node in tree
		"""
		tree: AstTree
		self.tree = tree
		self.ast_id = ast_id
		self.class_name = class_name
		self.beg_index = beg_index
		self.end_index = end_index
		self.data_type = data_type
		self.content = content
		self.parent = None
		self.children = list()
		return

	def get_tree(self):
		"""
		:return: abstract syntax tree of the node
		"""
		return self.tree

	def get_ast_id(self):
		"""
		:return: Unique integer ID of the node in AST
		"""
		return self.ast_id

	def get_class_name(self):
		"""
		:return: the name of syntactic class
		"""
		return self.class_name

	def get_beg_index(self):
		"""
		:return: index to the first character in code range
		"""
		return self.beg_index

	def get_end_index(self):
		"""
		:return: index next to the final char in code range
		"""
		return self.end_index

	def get_data_type(self):
		"""
		:return: data type of the value hold by the node
		"""
		return self.data_type

	def get_content(self):
		"""
		:return: None, Constant, Operator, Keyword, Punctuate
		"""
		return self.content

	def get_code(self, strip: bool):
		"""
		:param strip: true to specify the simplified cod
		:return:
		"""
		source_code = self.tree.program.source_code
		source_code: CSourceCode
		code = source_code.get_code(self.beg_index, self.end_index)
		if strip:
			new_code = ""
			for k in range(0, len(code)):
				ch = code[k]
				if ch.isspace():
					ch = ' '
				new_code += ch
			code = new_code
		return code

	def line_of(self, tail: bool):
		"""
		:param tail: whether to locate at the final character
		:return: range from 0 to N - 1
		"""
		index = self.beg_index
		if tail:
			index = self.end_index - 1
		source_code = self.tree.program.source_code
		source_code: CSourceCode
		return source_code.line_of(index) + 1

	def is_root(self):
		return self.parent is None

	def is_leaf(self):
		return len(self.children) == 0

	def get_parent(self):
		"""
		:return: parent of the node or None if it is root
		"""
		self.parent: AstNode
		return self.parent

	def get_children(self):
		return self.children

	def add_child(self, child):
		"""
		:param child:
		:return:
		"""
		child: AstNode
		child.parent = self
		self.children.append(child)
		return

	def statement_of(self):
		node = self
		while not (node is None):
			if node.class_name.endswith("Statement"):
				return node
			node = node.get_parent()
		return None

	def function_definition_of(self):
		node = self
		while not (node is None):
			if node.class_name.endswith("Definition"):
				return node
			node = node.get_parent()
		return None


class AstTree:
	def __init__(self, program: CProgram, ast_file: str):
		self.program = program
		self.ast_nodes = list()
		self.__parse__(ast_file)
		return

	def get_program(self):
		return self.program

	def get_ast_nodes(self):
		return self.ast_nodes

	def get_ast_node(self, ast_id: int):
		ast_node = self.ast_nodes[ast_id]
		ast_node: AstNode
		return ast_node

	def __parse__(self, ast_file: str):
		ast_node_dict = dict()
		with open(ast_file, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					ast_id = jcbase.CToken.parse(items[0].strip()).get_token_value()
					class_name = items[1].strip()
					beg_index = int(items[2].strip())
					end_index = int(items[3].strip())
					data_type = jcbase.CToken.parse(items[4].strip())
					content = jcbase.CToken.parse(items[5].strip())
					ast_node = AstNode(self, ast_id, class_name, beg_index, end_index, data_type, content)
					ast_node_dict[ast_node.get_ast_id()] = ast_node
		with open(ast_file, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					parent_id = jcbase.CToken.parse(items[0].strip()).get_token_value()
					parent = ast_node_dict[parent_id]
					children_ids = items[6].strip().split(' ')
					for k in range(1, len(children_ids) - 1):
						child_id = jcbase.CToken.parse(children_ids[k].strip()).get_token_value()
						child = ast_node_dict[child_id]
						parent.add_child(child)
		self.ast_nodes.clear()
		for ast_id in range(0, len(ast_node_dict)):
			ast_node = ast_node_dict[ast_id]
			self.ast_nodes.append(ast_node)
		return


class CirNode:
	"""
	C-intermediate representation node
	"""
	def __init__(self, tree, cir_id: int, class_name: str, ast_source: AstNode,
				 data_type: jcbase.CToken, content: jcbase.CToken, code: str):
		"""
		:param tree: tree where the node is created
		:param class_name: the name of CIR-node class
		:param cir_id: Unique integer ID of the node in tree
		:param ast_source: source in AST to which the node corresponds
		:param data_type: data type of the value hold by the node
		:param content:
		:param code: simplified code
		"""
		tree: CirTree
		self.tree = tree
		self.class_name = class_name
		self.cir_id = cir_id
		self.ast_source = ast_source
		self.data_type = data_type
		self.content = content
		self.code = code
		self.parent = None
		self.children = list()
		return

	def get_tree(self):
		return self.tree

	def get_cir_id(self):
		return self.cir_id

	def get_class_name(self):
		return self.class_name

	def get_ast_source(self):
		return self.ast_source

	def has_ast_source(self):
		return self.ast_source is not None

	def get_data_type(self):
		return self.data_type

	def get_content(self):
		return self.content

	def get_cir_code(self):
		return self.code

	def get_ast_code(self, strip: bool):
		"""
		:param strip:
		:return: code in AST source or None
		"""
		if self.has_ast_source():
			return self.ast_source.get_code(strip)
		return None

	def get_ast_line(self, tail: bool):
		"""
		:param tail: true to select the final index
		:return: line of ast source or none
		"""
		if self.has_ast_source():
			return self.ast_source.line_of(tail)
		return None

	def get_parent(self):
		self.parent: CirNode
		return self.parent

	def is_root(self):
		return self.parent is None

	def get_children(self):
		return self.children

	def is_leaf(self):
		return len(self.children) == 0

	def add_child(self, child):
		child: CirNode
		child.parent = self
		self.children.append(child)
		return


class CirTree:
	"""
	C-intermediate representation
	"""
	def __init__(self, program: CProgram, cir_file: str):
		self.program = program
		self.cir_nodes = list()
		self.__parse__(cir_file)
		return

	def get_program(self):
		return self.program

	def get_cir_nodes(self):
		return self.cir_nodes

	def get_cir_node(self, cir_id: int):
		cir_node = self.cir_nodes[cir_id]
		cir_node: CirNode
		return cir_node

	def __parse__(self, cir_file: str):
		"""
		:param cir_file:
		:return:
		"""
		cir_node_dict = dict()
		ast_tree = self.program.ast_tree
		with open(cir_file, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					cir_id = jcbase.CToken.parse(items[0].strip()).get_token_value()
					class_name = items[1].strip()
					ast_id = jcbase.CToken.parse(items[2].strip()).get_token_value()
					ast_source = None
					if ast_id is not None:
						ast_source = ast_tree.get_ast_node(ast_id)
					data_type = jcbase.CToken.parse(items[3].strip())
					content = jcbase.CToken.parse(items[4].strip())
					code = jcbase.CToken.parse(items[6].strip()).get_token_value()
					if code is None:
						code = ""
					cir_node = CirNode(self, cir_id, class_name, ast_source, data_type, content, code)
					cir_node_dict[cir_node.get_cir_id()] = cir_node
		with open(cir_file, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					parent_id = jcbase.CToken.parse(items[0].strip()).get_token_value()
					parent = cir_node_dict[parent_id]
					children_ids = items[5].strip().split(' ')
					for k in range(1, len(children_ids) - 1):
						child_id = jcbase.CToken.parse(children_ids[k].strip()).get_token_value()
						child = cir_node_dict[child_id]
						parent.add_child(child)
		self.cir_nodes.clear()
		for cir_id in range(0, len(cir_node_dict)):
			self.cir_nodes.append(cir_node_dict[cir_id])
		return


class CirExecutionFlow:
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


class CirExecution:
	"""
	function, ID, statement
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

	def __str__(self):
		return str(self.function) + "[" + str(self.exe_id) + "]"

	def get_in_flows(self):
		return self.in_flows

	def get_ou_flows(self):
		return self.ou_flows

	def link_to(self, target, flow_type: str):
		"""
		:param target:
		:param flow_type:
		:return: flow that connects this node to the target w.r.t. given type
		"""
		flow = CirExecutionFlow(flow_type, self, target)
		target: CirExecution
		self.ou_flows.append(flow)
		target.in_flows.append(flow)
		return flow


class CirFunctionCall:
	def __init__(self, call_flow: CirExecutionFlow, retr_flow: CirExecutionFlow):
		"""
		:param call_flow:
		:param retr_flow:
		"""
		self.call_flow = call_flow
		self.retr_flow = retr_flow
		return

	def get_call_flow(self):
		return self.call_flow

	def get_call_execution(self):
		return self.call_flow.get_source()

	def get_callee_entry(self):
		return self.call_flow.get_target()

	def get_callee(self):
		return self.call_flow.get_target().get_function()

	def get_caller(self):
		return self.call_flow.get_source().get_function()

	def get_retr_flow(self):
		return self.retr_flow

	def get_callee_exit(self):
		return self.retr_flow.get_source()

	def get_wait_execution(self):
		return self.retr_flow.get_target()


class CirFunction:
	"""
	Function in C program.
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

	def __str__(self):
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


class CirFunctionCallGraph:
	"""
	function calling graph describes control flow graph
	"""
	def __init__(self, program: CProgram, flw_file: str):
		self.program = program
		self.functions = dict()
		self.__parse__(flw_file)
		return

	def get_functions(self):
		return self.functions.values()

	def get_function(self, name: str):
		"""
		:param name:
		:return: the function w.r.t. the name as given
		"""
		function = self.functions[name]
		function: CirFunction
		return function

	def get_execution(self, name: str, exe_id: int):
		function = self.get_function(name)
		return function.get_execution(exe_id)

	def __call__(self, call_flow: CirExecutionFlow, retr_flow: CirExecutionFlow):
		"""
		:param call_flow:
		:param retr_flow:
		:return:
		"""
		calling = CirFunctionCall(call_flow, retr_flow)
		calling.get_caller().ou_calls.append(calling)
		calling.get_callee().in_calls.append(calling)
		return calling

	def __parse__(self, flw_file: str):
		self.functions.clear()
		executions_dict = dict()
		cir_tree = self.program.cir_tree
		with open(flw_file, 'r') as reader:
			for line in reader:
				line = line.strip()
				items = line.split('\t')
				if line.startswith("[beg]"):
					name = items[1].strip()
					function = CirFunction(self, name)
					self.functions[function.get_name()] = function
				elif line.startswith("[end]"):
					for exe_id in range(0, len(executions_dict)):
						execution = executions_dict[exe_id]
						function.executions.append(execution)
					executions_dict.clear()
				elif line.startswith("[node]"):
					exe_token = jcbase.CToken.parse(items[1].strip()).get_token_value()
					cir_token = jcbase.CToken.parse(items[2].strip()).get_token_value()
					statement = cir_tree.get_cir_node(cir_token)
					execution = CirExecution(function, exe_token[1], statement)
					executions_dict[execution.get_exe_id()] = execution
		with open(flw_file, 'r') as reader:
			for line in reader:
				line = line.strip()
				items = line.split('\t')
				if line.startswith("[edge]"):
					flow_type = items[1].strip()
					source_token = jcbase.CToken.parse(items[2].strip()).get_token_value()
					target_token = jcbase.CToken.parse(items[3].strip()).get_token_value()
					source = self.get_execution(source_token[0], source_token[1])
					target = self.get_execution(target_token[0], target_token[1])
					source.link_to(target, flow_type)
		with open(flw_file, 'r') as reader:
			for line in reader:
				line = line.strip()
				items = line.split('\t')
				if line.startswith("[call]"):
					call_token = jcbase.CToken.parse(items[1].strip()).get_token_value()
					wait_token = jcbase.CToken.parse(items[2].strip()).get_token_value()
					call_execution = self.get_execution(call_token[0], call_token[1])
					wait_execution = self.get_execution(wait_token[0], wait_token[1])
					call_flow = call_execution.ou_flows[0]
					retr_flow = wait_execution.in_flows[0]
					self.__call__(call_flow, retr_flow)
		return


class CirInstanceEdge:
	"""
	type source target
	"""
	def __init__(self, flow_type: str, source, target):
		source: CirInstanceNode
		target: CirInstanceNode
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


class CirInstanceNode:
	"""
	graph, context, execution
	"""
	def __init__(self, graph, context: int, execution: CirExecution):
		self.graph = graph
		self.context = context
		self.execution = execution
		self.in_edges = list()
		self.ou_edges = list()
		return

	def get_graph(self):
		return self.graph

	def get_context(self):
		return self.context

	def get_execution(self):
		return self.execution

	def get_statement(self):
		return self.execution.get_statement()

	def get_in_edges(self):
		return self.in_edges

	def get_ou_edges(self):
		return self.ou_edges

	def link_to(self, target, flow_type: str):
		edge = CirInstanceEdge(flow_type, self, target)
		target: CirInstanceNode
		self.ou_edges.append(edge)
		target.in_edges.append(edge)
		return edge


class CirInstanceGraph:
	"""
	instance of execution flow graph
	"""
	def __init__(self, program: CProgram, ins_file: str):
		self.program = program
		self.context_nodes = dict()
		self.__parse__(ins_file)
		return

	def get_contexts(self):
		return self.context_nodes.keys()

	def get_instance_nodes(self, key: int):
		execution_node_dict = self.context_nodes[key]
		execution_node_dict: dict
		return execution_node_dict.values()

	def get_instance_node(self, key: int, execution: CirExecution):
		"""
		:param key:
		:param execution:
		:return:
		"""
		execution_node_dict = self.context_nodes[key]
		execution_node_dict: dict
		node = execution_node_dict[execution]
		node: CirInstanceNode
		return node

	def __parse__(self, ins_file: str):
		"""
		:param ins_file:
		:return:
		"""
		function_call_graph = self.program.function_call_graph
		with open(ins_file, 'r') as reader:
			for line in reader:
				line = line.strip()
				items = line.split('\t')
				if line.startswith("#node"):
					token = jcbase.CToken.parse(items[1].strip()).get_token_value()
					context = token[0]
					name = token[1]
					exe_id = token[2]
					execution = function_call_graph.get_execution(name, exe_id)
					if not(context in self.context_nodes):
						self.context_nodes[context] = dict()
					execution_node_dict = self.context_nodes[context]
					execution_node_dict[execution] = CirInstanceNode(self, context, execution)
		with open(ins_file, 'r') as reader:
			for line in reader:
				line = line.strip()
				items = line.split('\t')
				if line.startswith("#edge"):
					flow_type = items[1].strip()
					source_token = jcbase.CToken.parse(items[2].strip()).get_token_value()
					target_token = jcbase.CToken.parse(items[3].strip()).get_token_value()
					source = self.get_instance_node(source_token[0], function_call_graph.get_execution(source_token[1], source_token[2]))
					target = self.get_instance_node(target_token[0], function_call_graph.get_execution(target_token[1], target_token[2]))
					source.link_to(target, flow_type)
		return


class CDependenceEdge:
	"""
	depend_type, source, target, (param1, param2)
	"""
	def __init__(self, depend_type: str, source, target, parameter_0: jcbase.CToken, parameter_1: jcbase.CToken):
		self.depend_type = depend_type
		source: CDependenceNode
		target: CDependenceNode
		self.source = source
		self.target = target
		self.parameters = (parameter_0, parameter_1)
		return

	def get_depend_type(self):
		return self.depend_type

	def get_source(self):
		return self.source

	def get_target(self):
		return self.target

	def get_parameters(self):
		return self.parameters

	def get_parameter_0(self):
		return self.parameters[0]

	def get_parameter_1(self):
		return self.parameters[1]


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

	def link_to(self, target, depend_type: str, parameter_0, parameter_1):
		edge = CDependenceEdge(depend_type, self, target, parameter_0, parameter_1)
		self.ou_edges.append(edge)
		target: CDependenceNode
		target.in_edges.append(edge)
		return edge


class CDependenceGraph:
	"""
	dependence graph
	"""
	def __init__(self, program: CProgram, dep_file: str):
		self.program = program
		self.dependence_nodes = dict()
		self.__parse__(dep_file)
		return

	def get_program(self):
		return self.program

	def get_instance_nodes(self):
		return self.dependence_nodes.keys()

	def get_dependence_nodes(self):
		return self.dependence_nodes.values()

	def get_dependence_node(self, instance: CirInstanceNode):
		node = self.dependence_nodes[instance]
		node: CDependenceNode
		return node

	def __parse__(self, dep_file: str):
		function_call_graph = self.program.function_call_graph
		instance_graph = self.program.instance_graph
		self.dependence_nodes.clear()
		with open(dep_file, 'r') as reader:
			for line in reader:
				line = line.strip()
				items = line.split('\t')
				if line.startswith("#node"):
					dep_token = jcbase.CToken.parse(items[1].strip()).get_token_value()
					instance = instance_graph.get_instance_node(
						dep_token[0], function_call_graph.get_execution(dep_token[1], dep_token[2]))
					node = CDependenceNode(self, instance)
					self.dependence_nodes[instance] = node
		with open(dep_file, 'r') as reader:
			for line in reader:
				line = line.strip()
				items = line.split('\t')
				if line.startswith("#edge"):
					depend_type = items[1].strip()
					source_token = jcbase.CToken.parse(items[2].strip()).get_token_value()
					target_token = jcbase.CToken.parse(items[3].strip()).get_token_value()
					source_instance = instance_graph.get_instance_node(
						source_token[0], function_call_graph.get_execution(source_token[1], source_token[2]))
					target_instance = instance_graph.get_instance_node(
						target_token[0], function_call_graph.get_execution(target_token[1], target_token[2]))
					source = self.dependence_nodes[source_instance]
					target = self.dependence_nodes[target_instance]
					source: CDependenceNode
					parameter0 = jcbase.CToken.parse(items[4].strip())
					parameter1 = jcbase.CToken.parse(items[5].strip())
					source.link_to(target, depend_type, parameter0, parameter1)
		return


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	for fname in os.listdir(root_path):
		dir = os.path.join(root_path, fname)
		c_program = CProgram(dir, fname)
		print("Load program data from", fname)
		for c_function in c_program.function_call_graph.get_functions():
			c_function: CirFunction
			print("\tdef", c_function.get_name(), ": ")
			for c_execution in c_function.get_executions():
				c_execution: CirExecution
				print("\t\t", c_execution, ":\t", c_execution.get_statement().get_cir_code())
				for c_flow in c_execution.get_ou_flows():
					c_flow: CirExecutionFlow
					print("\t\t\t", c_flow.get_flow_type(), "==>", c_flow.get_target())
			print("\tend def\n")

