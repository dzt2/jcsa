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

	def get_symbol_tree(self):
		"""
		:return: the symbolic tree of expressions used
		"""
		return self.sym_tree


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

	@staticmethod
	def decode(document: CDocument, word: str):
		"""
		:param document: 	the document in which the word is decoded to an abstract execution state.
		:param word: 		{category}${execution}${location}${loperand}${roperand}
		:return: 			CirAbstractState or None if the transformation failed
		"""
		items = word.strip().split('$')
		category = items[0].strip()
		exec_token = jcbase.CToken.parse(items[1].strip()).get_token_value()
		cloc_token = jcbase.CToken.parse(items[2].strip()).get_token_value()
		execution = document.get_program().function_call_graph.get_execution(exec_token[0], exec_token[1])
		location = document.get_program().cir_tree.get_cir_node(cloc_token)
		loperand = document.get_symbol_tree().get_sym_node(items[3].strip())
		roperand = document.get_symbol_tree().get_sym_node(items[4].strip())
		return CirAbstractState(category, execution, location, loperand, roperand)


class CirAbstractNode:
	"""
	It specifies a node in state subsumption hierarchical graph.
	"""

	def __init__(self, graph, state: CirAbstractState, annotations):
		"""
		:param graph: 		the graph where this node of abstract state is defined
		:param state: 		the abstract execution state that this node represents
		:param annotations: the set of abstract states to local-describe this node
		"""
		graph: CirAbstractGraph
		self.graph = graph
		self.state = state
		self.annotations = set()
		for annotation in annotations:
			annotation: CirAbstractState
			self.annotations.add(annotation)
		self.in_nodes = set()
		self.ou_nodes = set()
		return

	def get_graph(self):
		"""
		:return: the graph where this node is defined
		"""
		return self.graph

	def get_state(self):
		"""
		:return: the state that this node represents
		"""
		return self.state

	def get_annotations(self):
		"""
		:return: the set of states to extend-describe this node's state
		"""
		return self.annotations

	def get_in_nodes(self):
		"""
		:return: the set of nodes of which states directly subsume the state of this one
		"""
		return self.in_nodes

	def get_in_degree(self):
		"""
		:return: the number of nodes of which states directly subsume the state of this one
		"""
		return len(self.in_nodes)

	def get_ou_nodes(self):
		"""
		:return: the set of nodes of which states are directly subsumed by the state of this node
		"""
		return self.ou_nodes

	def get_ou_degree(self):
		"""
		:return: the number of nodes of which states are directly subsumed by the state of this node
		"""
		return len(self.ou_nodes)


class CirAbstractGraph:
	"""
	The state subsumption hierarchical graph.
	"""

	def __init__(self, document: CDocument, msg_file: str):
		"""
		:param document:
		:param msg_file: xxx.msg {[N]|[E] s+}
		"""





