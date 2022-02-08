"""This file defines the data model of symbolic expressions and abstract states."""


import os
import com.jcsa.pymuta.libs.base as jcbase
import com.jcsa.pymuta.libs.code as jccode
import com.jcsa.pymuta.libs.muta as jcmuta


class CDocument:
	"""
	It defines the dataset of symbolic features used to describe abstract execution states of mutation testing.
	"""

	def __init__(self, directory: str, file_name: str, middle_name: str):
		"""
		:param directory: the directory where the document and features are defined
		:param file_name: the name of the mutation project and program under test
		:param middle_name: pdg or tid
		"""
		self.project = jcmuta.CProject(directory, file_name)
		msg_file = os.path.join(directory, file_name + "." + middle_name + ".msg")
		sym_file = os.path.join(directory, file_name + "." + middle_name + ".sym")
		self.sym_tree = SymbolTree(sym_file)
		return

	def get_name(self):
		"""
		:return: the name of the program and project under test
		"""
		return self.project.program.name

	def get_program(self):
		"""
		:return: the program under test
		"""
		return self.project.program

	def get_project(self):
		"""
		:return: the mutation test project
		"""
		return self.project


class SymbolNode:
	"""
	class ID source{Token as AstNode, CirNode, Execution, Constant or Nullptr} data_type content code parent children
	"""
	def __init__(self, class_name: str, class_id: int, source: jcbase.CToken, data_type: jcbase.CToken, content: jcbase.CToken, code: str):
		"""
		:param class_name: class of symbolic node
		:param class_id: unique ID of symbolic node
		:param source: [AstNode, CirNode, Execution, Constant or None]
		:param data_type: code of data type
		:param content: Token as String, Operator, Constant or None
		:param code: simplified code to describe the symbolic node
		"""
		self.class_name = class_name
		self.class_id = class_id
		self.source = source
		self.data_type = data_type
		self.content = content
		self.code = code
		self.parent = None
		self.children = list()
		return

	def get_class_name(self):
		return self.class_name

	def get_class_id(self):
		return self.class_id

	def get_source(self):
		return self.source

	def get_data_type(self):
		return self.data_type

	def get_content(self):
		return self.content

	def get_code(self):
		return self.code

	def get_parent(self):
		return self.parent

	def get_children(self):
		return self.children

	def __str__(self):
		return self.code

	def is_root(self):
		return self.parent is None

	def is_leaf(self):
		return len(self.children) == 0

	def add_child(self, child):
		child: SymbolNode
		child.parent = self
		self.children.append(child)
		return


class SymbolTree:
	"""
	It manages all the symbolic nodes and their structure read from xxx.sym file
	"""
	def __init__(self, sym_file_path: str):
		"""
		:param sym_file_path:
		"""
		self.sym_nodes = dict()		# string --> SymNode
		self.__parse__(sym_file_path)
		return

	def get_sym_nodes(self):
		return self.sym_nodes.values()

	def get_sym_node(self, key: str):
		"""
		:param key: sym@class@id
		:return:
		"""
		node = self.sym_nodes[key]
		node: SymbolNode
		return node

	def __parse__(self, sym_file_path: str):
		"""
		:param sym_file_path:
		:return:
		"""
		self.sym_nodes.clear()
		with open(sym_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					key = items[0].strip()
					key_token = jcbase.CToken.parse(key).get_token_value()
					class_name = items[1].strip()
					class_id = key_token[1]
					source = jcbase.CToken.parse(items[2].strip())
					data_type = jcbase.CToken.parse(items[3].strip())
					content = jcbase.CToken.parse(items[4].strip())
					code = jcbase.CToken.parse(items[5].strip())
					sym_node = SymbolNode(class_name, class_id, source, data_type, content, code.get_token_value())
					self.sym_nodes[key] = sym_node
		with open(sym_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					parent = self.sym_nodes[items[0].strip()]
					parent: SymbolNode
					children_items = items[6].strip().split(' ')
					for k in range(1, len(children_items) - 1):
						child_key = children_items[k].strip()
						child = self.sym_nodes[child_key]
						parent.add_child(child)
		return


class CirAbstractState:
	"""
	It defines an abstract execution state specified at some program location.
	---	category:	the category of the state to define the semantics of its structural description;
	---	execution:	the CFG-node at which the abstract execution state is defined and evaluated for;
	---	location:	the C-intermediate representative location to preserve the values of this state;
	---	loperand:	the symbolic expression as the left-operand to describe the definition of state;
	---	roperand:	the symbolic expression as the righ-operand to describe the definition of state;
	"""

	def __init__(self, category: str, execution: jccode.CirExecution, location: jccode.CirNode, loperand: SymbolNode, roperand: SymbolNode):
		"""
		:param category: 	the category of the state to define the semantics of its structural description;
		:param execution: 	the CFG-node at which the abstract execution state is defined and evaluated for;
		:param location: 	the C-intermediate representative location to preserve the values of this state;
		:param loperand: 	the symbolic expression as the left-operand to describe the definition of state;
		:param roperand: 	the symbolic expression as the righ-operand to describe the definition of state;
		"""
		self.category = category
		self.execution = execution
		self.location = location
		self.loperand = loperand
		self.roperand = roperand
		return

	def get_category(self):
		"""
		:return: the category of the state to define the semantics of its structural description;
		"""
		return self.category

	def get_execution(self):
		"""
		:return: the CFG-node at which the abstract execution state is defined and evaluated for;
		"""
		return self.execution

	def get_location(self):
		"""
		:return: the C-intermediate representative location to preserve the values of this state;
		"""
		return self.location

	def get_loperand(self):
		"""
		:return: the symbolic expression as the left-operand to describe the definition of state;
		"""
		return self.loperand

	def get_roperand(self):
		"""
		:return: the symbolic expression as the righ-operand to describe the definition of state;
		"""
		return self.roperand

	def __str__(self):
		category = self.category
		execution = "exe@{}".format(self.execution.get_exe_id())
		location = "cir@{}".format(self.location.get_cir_id())
		loperand = "sym@{}@{}".format(self.loperand.get_class_name(), self.loperand.get_class_id())
		roperand = "sym@{}@{}".format(self.roperand.get_class_name(), self.roperand.get_class_id())
		return "{}${}${}${}${}".format(category, execution, location, loperand, roperand)


class CirAbstractNode:
	"""
	It denotes a node in abstract execution state graph with subsumption edges.
	"""

	def __init__(self, graph, state: CirAbstractState, extended_states):
		"""
		:param graph: the graph where this node is created
		:param state: the state that this node represents
		:param extended_states: the set of states extended from this node
		"""
		self.graph = graph
		self.state = state
		self.e_set = set()
		for extended_state in extended_states:
			extended_state: CirAbstractState
			self.e_set.add(extended_state)
		self.in_nodes = set()
		self.ou_nodes = set()
		return

	def get_graph(self):
		"""
		:return: the graph where this node is constructed
		"""
		return self.graph

	def get_state(self):
		"""
		:return: the state that this node specifies
		"""
		return self.state

	def get_extended_states(self):
		"""
		:return: the set of states extended from this node
		"""
		return self.e_set

	def get_in_nodes(self):
		"""
		:return: the set of nodes directly subsume this node
		"""
		return self.in_nodes

	def get_ou_nodes(self):
		"""
		:return: the set of nodes directly subsumed by this node
		"""
		return self.ou_nodes

	def get_in_degree(self):
		return len(self.in_nodes)

	def get_ou_degree(self):
		return len(self.ou_nodes)


class CirAbstractGraph:
	"""
	The state subsumption graph for feature analysis.
	"""

	def __init__(self, document: CDocument, msg_file: str):
		self.document = document
		self.__load__(msg_file)
		self.__link__(msg_file)
		self.__edge__(msg_file)
		return

	def __new_state__(self, word: str):
		"""
		:param word:
		:return:
		"""
		items = word.strip().split('$')
		category = items[0].strip()
		exec_token = jcbase.CToken.parse(items[1].strip()).get_token_value()
		execution = self.document.get_program().function_call_graph.get_execution(exec_token[0], exec_token[1])
		cir_token = jcbase.CToken.parse(items[2].strip()).get_token_value()
		location = self.document.get_program().cir_tree.get_cir_node(cir_token)
		loperand = self.document.sym_tree.get_sym_node(items[3].strip())
		roperand = self.document.sym_tree.get_sym_node(items[4].strip())
		return CirAbstractState(category, execution, location, loperand, roperand)

	def get_state(self, word: str):
		"""
		:param word: category$execution$location$loperand$roperand
		:return:
		"""
		if word in self.states:
			pass
		else:
			self.states[word] = self.__new_state__(word)
		state = self.states[word]
		state: CirAbstractState
		return state

	def __load__(self, msg_file: str):
		"""
		:param msg_file:
		:return: it loads the states constructed in the graph
		"""
		self.states = dict()
		with open(msg_file, 'r') as reader:
			for line in reader:
				items = line.strip().split('\t')
				for k in range(1, len(items)):
					item = items[k].strip()
					word = item.strip()
					if len(word) == 0:
						continue
					elif word in self.states:
						continue
					else:
						state = self.__new_state__(word)
						self.states[word] = state
		return

	def __link__(self, msg_file: str):
		"""
		:param msg_file:
		:return:
		"""
		self.nodes = dict()
		with open(msg_file, 'r') as reader:
			for line in reader:
				items = line.strip().split('\t')
				head = items[0].strip()
				if head == "[X]":
					source = self.get_state(items[1].strip())
					extended_states = set()
					for k in range(2, len(items)):
						target = self.get_state(items[k].strip())
						extended_states.add(target)
					self.nodes[source] = CirAbstractNode(self, source, extended_states)
		return

	def __edge__(self, msg_file: str):
		"""
		:param msg_file:
		:return:
		"""
		with open(msg_file, 'r') as reader:
			for line in reader:
				items = line.strip().split('\t')
				head = items[0].strip()
				if head != "[X]":
					source = self.get_state(items[1].strip())
					targets = set()
					for k in range(2, len(items)):
						targets.add(self.get_state(items[k].strip()))
					source_node = self.nodes[source]
					for target in targets:
						target_node = self.nodes[target]
						source_node.ou_nodes.add(target_node)
						target_node.in_nodes.add(source_node)
		return

	def get_document(self):
		"""
		:return: the document where the graph is defined
		"""
		return self.document

	def get_states(self):
		return self.states.values()

	def get_nodes(self):
		return self.nodes.values()

	def get_node(self, state: CirAbstractState):
		return self.nodes[state]








