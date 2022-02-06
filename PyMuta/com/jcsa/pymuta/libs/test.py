import os
from collections import deque

import com.jcsa.pymuta.libs.base as jcbase
import com.jcsa.pymuta.libs.code as jccode
import com.jcsa.pymuta.libs.muta as jcmuta
import graphviz


class CDocument:
	"""
	It maintains the feature database used for pattern mining.
	"""

	def __init__(self, directory: str, file_name: str):
		"""
		:param directory: where the feature files are preserved
		:param file_name: the name of the program under test.
		"""
		self.project = jcmuta.CProject(directory, file_name)
		self.ast_msg = CirAbstractGraph(self, os.path.join(directory, file_name + ".msg"), os.path.join(directory, file_name + ".exs"))
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

	def get_state_graph(self):
		return self.ast_msg


class CirAbstractGraph:
	"""
	It defines the subsumption graph between abstract execution states.
	"""

	def __init__(self, document: CDocument, msg_path: str, exs_path: str):
		"""
		:param document: the document in which the subsumption graph of abstract execution states.
		:param msg_path: xxx.msg	{source [target]+}
		"""
		self.document = document
		self.__parse__(msg_path)
		return

	def get_document(self):
		"""
		:return: the document in which the subsumption graph of abstract execution states.
		"""
		return self.document

	def __parse__(self, msg_path: str):
		"""
		:param msg_path:	xxx.msg	{source [target]+}
		:return:
		"""
		self.states = dict()	# 	string 	-->	CirAbstractState
		self.edges = dict()		#	Mutant	-->	CirAbstractState+
		with open(msg_path, 'r') as reader:
			for line in reader:
				items = line.strip().split('\t')
				for item in items:
					item = item.strip()
					if item.startswith("mut@"):
						mid = jcbase.CToken.parse(item).get_token_value()
						mutant = self.document.get_project().muta_space.get_mutant(mid)
						self.edges[mutant] = set()
		with open(msg_path, 'r') as reader:
			for line in reader:
				items = line.strip().split('\t')
				head = items[0].strip()
				if head.startswith("mut@"):
					mid = jcbase.CToken.parse(head).get_token_value()
					mutant = self.document.get_project().muta_space.get_mutant(mid)
					targets = self.edges[mutant]
					for k in range(1, len(items)):
						target_state = self.get_state(items[k].strip())
						targets.add(target_state)
				else:
					source_state = self.get_state(head)
					for k in range(1, len(items)):
						target_state = self.get_state(items[k].strip())
						source_state.ou_states.add(target_state)
						target_state.in_states.add(source_state)
		return

	def __extends__(self, exs_path: str):
		with open(exs_path, 'r') as reader:
			for line in reader:
				items = line.strip().split('\t')
				if len(items) > 1:
					source = self.get_state(items[0].strip())
					for k in range(1, len(items)):
						target = self.get_state(items[k].strip())
						source.ex_states.add(target)
		return

	def __new_state__(self, key: str):
		"""
		:param key: execution$store_type$c_location$value_type$l_operand$r_operand
		:return: CirAbstractState
		"""
		items = key.strip().split('$')
		exec_token = jcbase.CToken.parse(items[0].strip()).get_token_value()
		execution = self.document.get_program().function_call_graph.get_execution(exec_token[0], exec_token[1])
		store_type = items[1].strip()
		cloc_token = jcbase.CToken.parse(items[2].strip()).get_token_value()
		c_location = self.document.get_program().cir_tree.get_cir_node(cloc_token)
		value_type = items[3].strip()
		l_operand = self.document.get_project().sym_tree.get_sym_node(items[4].strip())
		r_operand = self.document.get_project().sym_tree.get_sym_node(items[5].strip())
		return CirAbstractState(self, execution, store_type, c_location, value_type, l_operand, r_operand)

	def get_mutants(self):
		"""
		:return: the set of mutant defined in the graph
		"""
		return self.edges.keys()

	def get_states(self):
		"""
		:return: the set of abstract states defined in the graph
		"""
		return self.states.values()

	def get_state(self, key: str):
		if key in self.states:
			state = self.states[key]
		else:
			self.states[key] = self.__new_state__(key)
			state = self.states[key]
		state: CirAbstractState
		return state

	def get_subsumed_states(self, source):
		"""
		:param source: 	Mutant | CirAbstractState | str
		:return: 		the set of states directly subsumed by the source
		"""
		if isinstance(source, jcmuta.Mutant):
			mutant = source
			return self.edges[mutant]
		elif isinstance(source, CirAbstractState):
			return source.ou_states
		else:
			source_state = self.states[source]
			return source_state.ou_states

	def derive_subsumed_set(self, mutant: jcmuta.Mutant):
		roots = self.edges[mutant]
		queue = deque()
		nodes = set()
		for root in roots:
			queue.append(root)
		while len(queue) > 0:
			root = queue.popleft()
			root: CirAbstractState
			nodes.add(root)
			for target in root.get_ou_states():
				if target in nodes:
					continue
				else:
					queue.append(target)
		return nodes

	@staticmethod
	def __decode__(source):
		"""
		:param source: Mutant or CirAbstractState
		:return: key, label
		"""
		if isinstance(source, jcmuta.Mutant):
			mutant = source
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
			source: CirAbstractState
			state = source
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
			source_name, label = CirAbstractGraph.__decode__(mutant)
			nodes[source_name] = label
			edges[source_name] = set()
			for state_node in self.edges[mutant]:
				state_node: CirAbstractState
				target_name = str(state_node)
				edges[source_name].add(target_name)

			state_nodes = self.derive_subsumed_set(mutant)
			for state_node in state_nodes:
				target_name, label = CirAbstractGraph.__decode__(state_node)
				nodes[target_name] = label
				edges[target_name] = set()
				for child in state_node.get_ou_states():
					edges[target_name].add(str(child))

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


class CirAbstractState:
	"""
	Abstract execution state
		---	execution:	the statement point where state is defined
		---	store_type:	the class of the store unit to preserve it
		---	c_location:	the C-intermediate representative location
		---	value_type:	the class of value preserved in this state
		---	l_operand:	the left operand to describe the state
		---	r_operand:	the right operand to describe the state
	"""

	def __init__(self, graph: CirAbstractGraph, execution: jccode.CirExecution,
				 store_type: str, c_location: jccode.CirNode, value_type: str,
				 l_operand: jcbase.SymbolNode, r_operand: jcbase.SymbolNode):
		"""
		:param graph:		the subsumption graph where the state is defined
		:param execution: 	the statement point where state is defined
		:param store_type: 	the class of the store unit to preserve it
		:param c_location: 	the C-intermediate representative location
		:param value_type: 	the class of value preserved in this state
		:param l_operand: 	the left operand to describe the state
		:param r_operand: 	the right operand to describe the state
		"""
		self.graph = graph
		self.execution = execution
		self.store_type = store_type
		self.c_location = c_location
		self.value_type = value_type
		self.l_operand = l_operand
		self.r_operand = r_operand
		self.in_states = set()
		self.ou_states = set()
		self.ex_states = set()
		return

	def get_graph(self):
		"""
		:return: mutant subsumption graph where the state is described
		"""
		return self.graph

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

	def get_in_states(self):
		"""
		:return: the states that directly subsume this one
		"""
		return self.in_states

	def get_ou_states(self):
		"""
		:return: the states that this one subsumes directly
		"""
		return self.ou_states

	def get_ex_states(self):
		return self.ex_states

	def __str__(self):
		execution = "exe@{}@{}".format(self.execution.get_function().get_name(), self.execution.get_exe_id())
		store_type = self.store_type
		c_location = "cir@{}".format(self.c_location.get_cir_id())
		value_type = self.value_type
		l_operand = "sym@{}@{}".format(self.l_operand.get_class_name(), self.l_operand.get_class_id())
		r_operand = "sym@{}@{}".format(self.r_operand.get_class_name(), self.r_operand.get_class_id())
		return "{}${}${}${}${}${}".format(execution, store_type, c_location, value_type, l_operand, r_operand)


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Data/zext/features"
	o_directory = "/home/dzt2/Development/Data/zext/impacts"
	for file_name in os.listdir(root_path):
		directory = os.path.join(root_path, file_name)
		print("Testing on document of", file_name)
		c_document = CDocument(directory, file_name)
		msg = c_document.get_state_graph()
		select_mutants = set()
		while len(select_mutants) < 1:
			select_mutant = jcbase.rand_select(msg.get_mutants())
			select_mutant: jcmuta.Mutant
			if not select_mutant.get_result().is_killed_in():
				select_mutants.add(select_mutant)
		msg.visualize(o_directory, c_document.get_name(), select_mutants)
		for mutant in msg.get_mutants():
			mid = mutant.get_muta_id()
			mclass = mutant.get_mutation().get_mutation_class()
			moperator = mutant.get_mutation().get_mutation_operator()
			c_location = mutant.get_mutation().get_location()
			line = c_location.line_of(False)
			code = c_location.get_code(True)
			if len(code) > 128:
				code = code[0: 128] + "..."
			parameter = str(mutant.get_mutation().get_parameter())
			print("\tMut[{}]\t{}\t{}\t{}\t\"{}\"\t{}".format(mid, mclass, moperator, line, code, parameter))

			## subsumed infection related states
			states = msg.get_subsumed_states(mutant)
			for state in states:
				state: CirAbstractState
				execution = str(state.get_execution())
				store_type = state.get_store_type()
				value_type = state.get_value_type()
				location = state.get_c_location().get_cir_code()
				parameter = state.get_r_operand().get_code()
				print("\t==> {}\t{}\t\'{}\'\t{}\t[{}]".format(execution, store_type, location, value_type, parameter))

				for ou_state in state.get_ou_states():
					ou_state: CirAbstractState
					execution = str(ou_state.get_execution())
					store_type = ou_state.get_store_type()
					value_type = ou_state.get_value_type()
					location = ou_state.get_c_location().get_cir_code()
					parameter = ou_state.get_r_operand().get_code()
					print(
						"\t\t--> {}\t{}\t\'{}\'\t{}\t[{}]".format(execution, store_type, location, value_type, parameter))
			print()
		print()
	print("End of all...")

