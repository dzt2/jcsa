"""This file defines the data model of symbolic expressions and abstract states."""


import os
from collections import deque
import graphviz
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
		self.state_graph = CirAbstractGraph(self, msg_file)
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

	def get_state_graph(self):
		return self.state_graph


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

	def is_conditional(self):
		"""
		:return: whether the state is conditional
		"""
		if self.category == "cov_stmt":
			return True
		elif self.category == "eva_expr":
			return True
		else:
			return False

	def is_abst_error(self):
		return self.is_path_error() or self.is_data_error()

	def is_path_error(self):
		if self.category == "mut_stmt":
			return True
		elif self.category == "mut_flow":
			return True
		elif self.category == "trp_stmt":
			return True
		else:
			return False

	def is_data_error(self):
		if self.category == "set_expr":
			return True
		elif self.category == "inc_expr":
			return True
		elif self.category == "xor_expr":
			return True
		else:
			return False

	def is_mutant_key(self):
		return self.category == "ast_muta"

	def derive_mutant_id(self):
		"""
		:return: ID of the mutation key if the state is or -1 if it is not
		"""
		if self.category == "ast_muta":
			return int(self.loperand.get_code().strip())
		return -1


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

	def is_mutant_node(self):
		"""
		:return: whether the node represents a mutation's killing state
		"""
		return self.state.is_mutant_key()

	def derive_mutant(self):
		"""
		:return: the mutant that the node represents or None if it is not
		"""
		mid = self.state.derive_mutant_id()
		if mid >= 0:
			return self.get_graph().document.get_project().muta_space.get_mutant(mid)
		return None

	def derive_subtree(self):
		"""
		:return: the set of all the nodes subsumed  by this node (including this one)
		"""
		queue = deque()
		nodes = set()
		queue.append(self)
		while len(queue) > 0:
			parent = queue.popleft()
			parent: CirAbstractNode
			nodes.add(parent)
			for child in parent.get_ou_nodes():
				if not (child in nodes):
					queue.append(child)
		return nodes


class CirAbstractGraph:
	"""
	The state subsumption hierarchical graph.
	"""

	def __init__(self, document: CDocument, msg_file: str):
		"""
		:param document:
		:param msg_file: xxx.msg {[N]|[E] s+}
		"""
		self.document = document
		self.__load__(msg_file)
		self.__node__(msg_file)
		self.__edge__(msg_file)
		self.__link__()
		return

	def __load__(self, msg_file: str):
		"""
		:param msg_file:
		:return: it loads the set of states defined in the graph
		"""
		self.states = dict()	# string --> CirAbstractState
		with open(msg_file, 'r') as reader:
			for line in reader:
				items = line.strip().split('\t')
				for k in range(1, len(items)):
					word = items[k].strip()
					if word in self.states:
						pass
					else:
						state = CirAbstractState.decode(self.document, word)
						self.states[word] = state
		return

	def __node__(self, msg_file: str):
		"""
		:param msg_file: [N] state {extended_state}+ \n
		:return: it creates the set of nodes and corresponding annotations from [N]
		"""
		self.nodes = dict()
		with open(msg_file, 'r') as reader:
			for line in reader:
				items = line.strip().split('\t')
				if len(items) > 0:
					head = items[0].strip()
					if head == "[N]":
						state = self.states[items[1].strip()]
						annotations = set()
						for k in range(2, len(items)):
							annotation = self.states[items[k].strip()]
							annotations.add(annotation)
						node = CirAbstractNode(self, state, annotations)
						self.nodes[node.get_state()] = node
		return

	def __edge__(self, msg_file: str):
		"""
		:param msg_file: [E] source {target}+
		:return: it links the nodes to corresponding nodes by subsumption
		"""
		with open(msg_file, 'r') as reader:
			for line in reader:
				items = line.strip().split('\t')
				if len(items) > 0:
					head = items[0].strip()
					if head == "[E]":
						source = self.nodes[self.states[items[1].strip()]]
						for k in range(2, len(items)):
							target = self.nodes[self.states[items[k].strip()]]
							source.ou_nodes.add(target)
							target.in_nodes.add(source)
		return

	def __link__(self):
		"""
		:return: it links the mutant to corresponding states.
		"""
		self.index = dict()		# Mutant --> CirAbstractStateNode(s)
		for node in self.nodes.values():
			mutant = node.derive_mutant()
			if mutant is None:
				continue
			else:
				mutant: jcmuta.Mutant
				if not (mutant in self.index):
					self.index[mutant] = set()
				self.index[mutant].add(node)
		return

	def get_document(self):
		return self.document

	def get_words(self):
		"""
		:return: the keys to derive abstract execution states defined in the graph
		"""
		return self.states.keys()

	def has_state(self, word: str):
		return word in self.states

	def get_state(self, word: str):
		return self.states[word]

	def get_states(self):
		return self.states.values()

	def has_node(self, state: CirAbstractState):
		return state in self.nodes

	def get_node(self, state: CirAbstractState):
		return self.nodes[state]

	def get_nodes(self):
		return self.nodes.values()

	def get_mutants(self):
		return self.index.keys()

	def has_nodes_of(self, mutant: jcmuta.Mutant):
		return mutant in self.index

	def get_nodes_of(self, mutant: jcmuta.Mutant):
		"""
		:param mutant:
		:return: the set of nodes of which states represent the mutant's killing condition.
		"""
		return self.index[mutant]

	@staticmethod
	def __string__(node: CirAbstractNode, max_code_length = 96):
		"""
		:param node: Mutant or State-Description
		:return:
		"""
		if node.is_mutant_node():
			mutant = node.derive_mutant()
			mid = mutant.get_muta_id()
			m_class = mutant.get_mutation().get_mutation_class()
			m_operator = mutant.get_mutation().get_mutation_operator()
			c_location = mutant.get_mutation().get_location()
			c_line = c_location.line_of(False)
			c_code = c_location.get_code(True)
			if len(c_code) > max_code_length:
				c_code = c_code[0: max_code_length] + "..."
			parameter = mutant.get_mutation().get_parameter()
			return "MID:\t{}\nCLS:\t{}:{}\nLIN#{}:\t\"{}\"\nPRM:\t{}".format(mid, m_class, m_operator, c_line, c_code, parameter)
		else:
			category = node.get_state().get_category()
			execution = node.get_state().get_execution()
			statement = execution.get_statement().get_cir_code()
			location = node.get_state().get_location().get_cir_code()
			loperand = node.get_state().get_loperand().get_code()
			roperand = node.get_state().get_roperand().get_code()
			return "CTG:\t{}\n{}:\t{}\nLOC:\t{}\nLOP:\t{}\nROP:\t{}".format(category, execution, statement, location, loperand, roperand)

	def visualize(self, directory: str, file_name: str, mutants):
		"""
		:param directory:	the directory where the sub-graph is visualized
		:param file_name:	the name of the pdf-file
		:param mutants:		the set of mutants of which state nodes are presented
		:return:
		"""
		## 1. capture the set of nodes subsumed by the input mutant in the graph
		state_nodes = set()
		for mutant in mutants:
			if self.has_nodes_of(mutant):
				mutant_nodes = self.get_nodes_of(mutant)
				for mutant_node in mutant_nodes:
					mutant_node: CirAbstractNode
					children = mutant_node.derive_subtree()
					for child in children:
						state_nodes.add(child)

		## 2. build the name-labels and name-name-edges to visualize in pdf file
		nodes, edges = dict(), dict()
		for state_node in state_nodes:
			nodes[str(state_node.get_state())] = CirAbstractGraph.__string__(state_node)
			edges[str(state_node.get_state())] = set()
			for child in state_node.get_ou_nodes():
				child: CirAbstractNode
				edges[str(state_node.get_state())].add(str(child.get_state()))

		## 3. generate the pdf file and dot graph for visualization
		print("\tGenerate", len(nodes), "nodes in MSG")
		graph = graphviz.Digraph()
		for name, label in nodes.items():
			graph.node(name, label)
		for parent, children in edges.items():
			for child in children:
				graph.edge(parent, child)
		if not os.path.exists(directory):
			os.mkdir(directory)
		graph.render(filename=file_name, directory=directory, format="pdf")
		file_path = os.path.join(directory, file_name)
		os.remove(file_path)
		return


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Data/zext/features"
	o_directory = "/home/dzt2/Development/Data/zext/impacts"
	selected_number = 1
	for fname in os.listdir(root_path):
		i_directory = os.path.join(root_path, fname)
		print("Testing on document of", fname)
		c_document = CDocument(i_directory, fname, "pdg")
		graph = c_document.get_state_graph()
		print("\t{} states and {} nodes in subsumption graph".format(len(graph.get_states()), len(graph.get_nodes())))
		select_mutants = set()
		for mutant in c_document.get_project().muta_space.get_mutants():
			mutant: jcmuta.Mutant
			if mutant.get_result().is_killed_in():
				continue
			else:
				select_mutants.add(mutant)
				if len(select_mutants) > selected_number:
					break
		graph.visualize(o_directory, fname, select_mutants)
		print("\tVisualize the subsumption graph for abstract states")
		print()
	print("End Testing...")

