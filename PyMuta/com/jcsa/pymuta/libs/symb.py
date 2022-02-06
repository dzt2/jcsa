"""This file defines the data model of symbolic execution states at mutation testing."""


import os
from collections import deque
import com.jcsa.pymuta.libs.base as jcbase
import com.jcsa.pymuta.libs.code as jccode
import com.jcsa.pymuta.libs.muta as jcmuta
import graphviz


class CDocument:
	"""
	It integrates the subsumption hierarchies and corresponding annotations from each state.
	"""

	def __init__(self, directory: str, file_name: str, middle_name: str):
		"""
		:param directory: 	the directory where the feature files are preserved
		:param file_name: 	the name of the program under test with 'c' postfix
		:param middle_name: the middle-name to select static (.pdg) or dynamics (.tid)
		"""
		self.project = jcmuta.CProject(directory, file_name)
		sym_file = os.path.join(directory, file_name + "." + middle_name + ".sym")
		msg_file = os.path.join(directory, file_name + "." + middle_name + ".msg")
		self.sym_tree = jcbase.SymbolTree(sym_file)
		self.state_graph = CirAbstractGraph(self, msg_file)
		return

	def get_name(self):
		"""
		:return: the name of the C program under test
		"""
		return self.project.program.name

	def get_program(self):
		"""
		:return: the C program model on which the document is defined
		"""
		return self.project.program

	def get_project(self):
		"""
		:return: the mutation testing project in which the document is defined
		"""
		return self.project

	def get_sym_tree(self):
		return self.sym_tree

	def get_state_graph(self):
		return self.state_graph


class CirAbstractState:
	"""
	It defines an abstract execution state at some program point.
	---	execution:	the CFG-node where the execution state is specified.
	---	store_type:	the class of store unit to preserve the description.
	---	c_location:	the C-intermediate code location to preserve values.
	---	value_type:	the class of abstract values hold within this state.
	---	l_operand:	the left-symbolic value hold within this pair state.
	---	r_operand:	the right-symbolic value hold within the pair state.
	"""

	def __init__(self, execution: jccode.CirExecution,
				 store_type: str, c_location: jccode.CirNode, value_type: str,
				 l_operand, r_operand):
		"""
		:param execution: 	the CFG-node where the execution state is specified.
		:param store_type: 	the class of store unit to preserve the description.
		:param c_location: 	the C-intermediate code location to preserve values.
		:param value_type: 	the class of abstract values hold within this state.
		:param l_operand: 	the left-symbolic value hold within this pair state.
		:param r_operand: 	the right-symbolic value hold within the pair state.
		"""
		self.execution = execution
		self.store_type = store_type
		self.c_location = c_location
		self.value_type = value_type
		l_operand: jcbase.SymbolNode
		r_operand: jcbase.SymbolNode
		self.l_operand = l_operand
		self.r_operand = r_operand
		return

	def get_execution(self):
		"""
		:return: the statement point where state is defined
		"""
		return self.execution

	def get_store_type(self):
		"""
		:return: the class of the store unit to preserve it
		"""
		return self.store_type

	def get_c_location(self):
		"""
		:return: the C-intermediate representative location
		"""
		return self.c_location

	def get_value_type(self):
		"""
		:return: the class of value preserved in this state
		"""
		return self.value_type

	def get_l_operand(self):
		"""
		:return: the left operand to describe the state
		"""
		return self.l_operand

	def get_r_operand(self):
		"""
		:return: the right operand to describe the state
		"""
		return self.r_operand

	def is_mutant_state(self):
		return self.store_type == "mutation"

	def __str__(self):
		execution = "exe@{}@{}".format(self.execution.get_function().get_name(), self.execution.get_exe_id())
		store_type = self.store_type
		c_location = "cir@{}".format(self.c_location.get_cir_id())
		value_type = self.value_type
		l_operand = "sym@{}@{}".format(self.l_operand.get_class_name(), self.l_operand.get_class_id())
		r_operand = "sym@{}@{}".format(self.r_operand.get_class_name(), self.r_operand.get_class_id())
		return "{}${}${}${}${}${}".format(execution, store_type, c_location, value_type, l_operand, r_operand)


class CirAbstractNode:
	"""
	It represents a node in state subsumption hierarchical graph.
	"""

	def __init__(self, graph, state: CirAbstractState, extensions):
		"""
		:param graph: the subsumption hierarchical graph where the node is defined
		:param state: the state to uniquely define this node in subsumption graphs
		:param extensions: the set of states extended (summarized) from this state
		"""
		graph: CirAbstractGraph
		self.graph = graph
		self.state = state
		self.extension_set = set()
		for extension in extensions:
			extension: CirAbstractState
			self.extension_set.add(extension)
		self.in_nodes = list()
		self.ou_nodes = list()
		return

	def get_graph(self):
		"""
		:return: the subsumption hierarchical graph where the node is defined
		"""
		return self.graph

	def get_state(self):
		"""
		:return: the state to uniquely define this node in subsumption graphs
		"""
		return self.state

	def get_extended_states(self):
		"""
		:return: the states summarized from the current state of this node
		"""
		return self.extension_set

	def get_in_nodes(self):
		"""
		:return: the set of edges that directly subsume this node
		"""
		return self.in_nodes

	def get_ou_nodes(self):
		"""
		:return: the set of nodes directly subsumed by this node
		"""
		return self.ou_nodes


class CirAbstractGraph:
	"""
	The subsumption hierarchical graph based on abstract execution states.
	"""

	def __init__(self, document: CDocument, msg_file: str):
		"""
		:param document: the document where the subsumption graph is defined
		:param msg_file: xxx.[pdg|tid].msg
		"""
		self.document = document
		self.states = dict()			## str --> CirAbstractState
		self.__load__(msg_file)
		self.__link__(msg_file)
		return

	def __new_state__(self, word: str):
		"""
		:param word:	execution$store_type$c_location$value_type$l_operand$r_operand
		:return:		CirAbstractState w.r.t. the key uniquely
						or Mutant which is {execution$mutation$location$mutation_ID$Rand$Rand}
		"""
		if word in self.states:
			pass
		elif word.startswith("mut@"):
			items = word.strip().split('@')
			mid = int(items[1].strip())
			execution = self.document.get_program().function_call_graph.get_function("main").get_execution(0)
			c_location = execution.get_statement()
			store_type = "mutation"
			value_type = str(mid)
			l_operand, r_operand = None, None
			for sym_node in self.document.get_sym_tree().get_sym_nodes():
				l_operand = sym_node
				r_operand = sym_node
				break
			self.states[word] = CirAbstractState(execution, store_type, c_location, value_type, l_operand, r_operand)
		else:
			items = word.strip().split('$')
			exec_token = jcbase.CToken.parse(items[0].strip()).get_token_value()
			execution = self.document.get_program().function_call_graph.get_execution(exec_token[0], exec_token[1])
			store_type = items[1].strip()
			cloc_token = jcbase.CToken.parse(items[2].strip()).get_token_value()
			c_location = self.document.get_program().cir_tree.get_cir_node(cloc_token)
			value_type = items[3].strip()
			l_operand = self.document.get_sym_tree().get_sym_node(items[4].strip())
			r_operand = self.document.get_sym_tree().get_sym_node(items[5].strip())
			self.states[word] = CirAbstractState(execution, store_type, c_location, value_type, l_operand, r_operand)
		state = self.states[word]
		state: CirAbstractState
		return state

	def __load__(self, msg_file: str):
		"""
		It loads the nodes into the graph from specified file
		:param msg_file:
		:return:
		"""
		self.nodes = dict()  ## CirAbstractState --> CirAbstractNode
		with open(msg_file, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					head = items[0].strip()
					if head == "[X]":
						source_state = self.__new_state__(items[1].strip())
						target_states = set()
						for k in range(2, len(items)):
							target_states.add(self.__new_state__(items[k].strip()))
						node = CirAbstractNode(self, source_state, target_states)
						self.nodes[source_state] = node
		return

	def __link__(self, msg_file: str):
		"""
		:param msg_file:
		:return:
		"""
		with open(msg_file, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					head = items[0].strip()
					if head == "[X]":
						continue
					elif head == "[M]":
						node = CirAbstractNode(self, self.__new_state__(items[1].strip()), set())
						self.nodes[node.get_state()] = node
					else:
						node = self.nodes[self.__new_state__(items[1].strip())]
					source = node
					for k in range(2, len(items)):
						target = self.nodes[self.__new_state__(items[k].strip())]
						source.ou_nodes.append(target)
						target.in_nodes.append(source)
		return

	def get_document(self):
		return self.document

	def get_states(self):
		"""
		:return: the set of states used to define graph nodes
		"""
		return self.nodes.keys()

	def get_mutants(self):
		mutants = set()
		for state in self.states.values():
			state: CirAbstractState
			if state.is_mutant_state():
				mid = int(state.value_type)
				mutant = self.document.get_project().muta_space.get_mutant(mid)
				mutants.add(mutant)
		return mutants

	def get_nodes(self):
		"""
		:return: the set of nodes created in this graph
		"""
		return self.nodes.values()

	def get_nodes_of(self, mutant: jcmuta.Mutant):
		"""
		:param mutant:
		:return: the set of nodes subsumed by the mutant (included)
		"""
		state = self.__new_state__("mut@{}".format(mutant.get_muta_id()))
		queue = deque()
		nodes = set()
		if state in self.nodes:
			root = self.nodes[state]
			queue.append(root)
			while len(queue) > 0:
				state_node = queue.popleft()
				state_node: CirAbstractNode
				nodes.add(state_node)
				for ou_node in state_node.get_ou_nodes():
					ou_node: CirAbstractNode
					if ou_node in nodes:
						pass
					else:
						queue.append(ou_node)
		return nodes

	def __decode__(self, state: CirAbstractState):
		"""
		:param state:
		:return: it transforms the node to a normalized string information
		"""
		if state.is_mutant_state():
			mid = int(state.get_value_type())
			mutant = self.document.get_project().muta_space.get_mutant(mid)
			cls = mutant.get_mutation().get_mutation_class()
			opr = mutant.get_mutation().get_mutation_operator()
			loc = mutant.get_mutation().get_location()
			lin = loc.line_of(False)
			cod = loc.get_code(True)
			if len(cod) > 128:
				cod = cod[0: 128].strip()
			prm = mutant.get_mutation().get_parameter()
			name = str(mutant)
			label = "Class: {}:{}\n#{}: \"{}\"\nParam: {}".format(cls, opr, lin, cod, prm)
			return name, label
		else:
			execution = state.get_execution()
			statement = execution.get_statement().get_cir_code().strip()
			store_type = state.get_store_type()
			c_location = state.get_c_location().get_cir_code()
			value_type = state.get_value_type()
			parameter = state.get_r_operand()
			name = str(state)
			label = "{}: {}\n{}: {}\n{}: {}".format(execution, statement, store_type, c_location, value_type, parameter)
			return name, label

	def visualize(self, directory: str, file_name: str, mutants):
		"""
		:param directory:
		:param file_name:
		:param mutants:
		:return:
		"""
		nodes, edges = dict(), dict()
		for mutant in mutants:
			mutant: jcmuta.Mutant
			state_nodes = self.get_nodes_of(mutant)
			for state_node in state_nodes:
				source_name, label = self.__decode__(state_node.get_state())
				nodes[source_name] = label
				edges[source_name] = set()
				for child in state_node.get_ou_nodes():
					child: CirAbstractNode
					edges[source_name].add(str(child.get_state()))

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
	for fname in os.listdir(root_path):
		i_directory = os.path.join(root_path, fname)
		print("Testing on document of", fname)
		c_document = CDocument(i_directory, fname, "pdg")
		msg = c_document.get_state_graph()
		select_mutants = set()
		while len(select_mutants) < 1:
			select_mutant = jcbase.rand_select(msg.get_mutants())
			select_mutant: jcmuta.Mutant
			if not select_mutant.get_result().is_killed_in():
				select_mutants.add(select_mutant)
		msg.visualize(o_directory, c_document.get_name(), select_mutants)
	print("End of All...")

