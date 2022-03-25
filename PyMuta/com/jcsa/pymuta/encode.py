"""This file defines the data models of memory-reduced instances for encoding objects in CDocument."""


import os
import com.jcsa.pymuta.libs.muta as jcmuta
import com.jcsa.pymuta.libs.test as jctest


class MerDocument:
	"""
	It incorporates the data-instances for encoding the objects in CDocument.
	"""

	def __init__(self, directory: str, file_name: str):
		"""
		:param directory: the directory where the encoded files are preserved
		:param file_name: the name of the program and mutation under analysis
		"""
		tst_file = os.path.join(directory, file_name + ".tst")
		mut_file = os.path.join(directory, file_name + ".mut")
		sta_file = os.path.join(directory, file_name + ".sta")
		zex_file = os.path.join(directory, file_name + ".zex")
		self.name = file_name
		self.tst_space = MerTestCaseSpace(self, tst_file)
		self.mut_space = MerMutantSpace(self, mut_file)
		self.sta_space = MerAbstractStateSpace(self, sta_file)
		self.exe_space = MerExecutionSpace(self, zex_file)
		return

	def get_name(self):
		return self.name

	def get_test_space(self):
		return self.tst_space

	def get_mutant_space(self):
		return self.mut_space

	def get_state_space(self):
		return self.sta_space

	def get_execution_space(self):
		return self.exe_space


class MerTestCaseSpace:
	"""
	The space of memory-reduced instances to encode TestCase in CProject.
	"""

	def __init__(self, document: MerDocument, tst_file: str):
		"""
		:param document: the document where this space is defined
		:param tst_file: xxx.tst {tid\n}+
		"""
		self.document = document
		self.__load__(tst_file)
		return

	def __load__(self, tst_file: str):
		"""
		:param tst_file: xxx.tst {tid\n}+
		:return:
		"""
		test_number = 0
		with open(tst_file, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					tid = int(line)
					if tid >= test_number:
						test_number = tid + 1
		self.test_cases = list()
		for tid in range(0, test_number):
			self.test_cases.append(MerTestCase(self, tid))
		return

	def get_document(self):
		"""
		:return: the document where this space is defined
		"""
		return self.document

	def __len__(self):
		"""
		:return: the number of instances defined in the space
		"""
		return len(self.test_cases)

	def get_test_cases(self):
		"""
		:return: the set of instances to encode TestCase
		"""
		return self.test_cases

	def get_test_case(self, tid: int):
		"""
		:param tid:
		:return: the instance to encode TestCase of specified ID
		"""
		return self.test_cases[tid]


class MerTestCase:
	"""
	the memory-reduced instance to encode TestCase in CProject.
	"""

	def __init__(self, space: MerTestCaseSpace, tid: int):
		"""
		:param space: 	the space where the instance is created
		:param tid: 	the unique ID to specify TestCase which this instance represents
		"""
		self.space = space
		self.tid = tid
		return

	def get_space(self):
		"""
		:return: the space where the instance is created
		"""
		return self.space

	def get_tid(self):
		"""
		:return: the unique ID to specify TestCase which this instance represents
		"""
		return self.tid

	def __str__(self):
		return "tst@{}".format(self.tid)

	def find_source(self, c_document: jctest.CDocument):
		"""
		:param c_document: the document where the original data is preserved
		:return: the TestCase object that this memory-reduced instance denotes
		"""
		return c_document.get_project().test_space.get_test_case(self.tid)


class MerMutantSpace:
	"""
	The space of memory-reduced instances for encoding Mutant in CProject.
	"""

	def __init__(self, document: MerDocument, mut_file: str):
		"""
		:param document: the document where this space is defined
		:param mut_file: xxx.mut	{mid cid wid sid res\n}+
		"""
		self.document = document
		self.__load__(mut_file)
		self.__link__(mut_file)
		return

	def __load__(self, mut_file: str):
		"""
		:param mut_file: xxx.mut	{mid cid wid sid res\n}+
		:return:
		"""
		mutant_number = 0
		with open(mut_file, 'r') as reader:
			for line in reader:
				items = line.strip().split('\t')
				if len(items) > 0:
					mid = int(items[0].strip())
					if mid >= mutant_number:
						mutant_number = mid + 1
		self.mutants = list()
		for mid in range(0, mutant_number):
			self.mutants.append(MerMutant(self, mid))
		return

	def __link__(self, mut_file: str):
		"""
		:param mut_file: xxx.mut	{mid cid wid sid res\n}+
		:return:
		"""
		with open(mut_file, 'r') as reader:
			for line in reader:
				items = line.strip().split('\t')
				mutant = self.mutants[int(items[0].strip())]
				mutant.rip[0] = self.mutants[int(items[1].strip())]
				mutant.rip[1] = self.mutants[int(items[2].strip())]
				mutant.rip[2] = self.mutants[int(items[3].strip())]
				if len(items) > 4:
					mutant.res = items[4].strip()
		return

	def __len__(self):
		"""
		:return: the number of instances defined in the space
		"""
		return len(self.mutants)

	def get_mutants(self):
		"""
		:return: the set of instances to encode Mutant in CProject
		"""
		return self.mutants

	def get_mutant(self, mid: int):
		"""
		:param mid:
		:return: the instance that encodes the Mutant object
		"""
		return self.mutants[mid]


class MerMutant:
	"""
	the memory-reduced instance to encode Mutant in CProject.
	"""

	def __init__(self, space: MerMutantSpace, mid: int):
		"""
		:param space:	the space where this instance is defined
		:param mid:		the ID of Mutant that this instance encodes
		"""
		self.space = space
		self.mid = mid
		self.rip = [self, self, self]
		self.res = ""
		return

	def get_space(self):
		"""
		:return: the space where this instance is defined
		"""
		return self.space

	def get_mid(self):
		"""
		:return: the ID of Mutant that this instance encodes
		"""
		return self.mid

	def get_c_mutant(self):
		"""
		:return: the coverage-version of this mutant
		"""
		return self.rip[0]

	def get_w_mutant(self):
		"""
		:return: the weak-version of this mutant
		"""
		return self.rip[1]

	def get_s_mutant(self):
		"""
		:return: the strong-version of this mutant
		"""
		return self.rip[2]

	def get_result(self):
		"""
		:return: the bit-string of test results
		"""
		return self.res

	def __str__(self):
		return "mut@{}".format(self.mid)

	def is_killed_by(self, test):
		"""
		:param test: MerTestCase or int
		:return:
		"""
		if isinstance(test, MerTestCase):
			tid = test.get_tid()
		else:
			test: int
			tid = test
		if (tid >= 0) and (tid < len(self.res)):
			return self.res[tid] == '1'
		else:
			return False

	def is_killed_in(self, tests):
		"""
		:param tests: the set of MerTestCase or int to specify the tests for killing this mutant
		:return:
		"""
		if tests is None:
			return self.is_killable()
		else:
			for test in tests:
				if self.is_killed_by(test):
					return True
			return False

	def is_killable(self):
		"""
		:return: whether the mutant can be killed by any tests in document
		"""
		return '1' in self.res

	def find_source(self, c_document: jctest.CDocument):
		"""
		:param c_document:	the document where the original data is preserved
		:return: the Mutant object that this instance encodes in c_document
		"""
		return c_document.get_project().muta_space.get_mutant(self.mid)


class MerAbstractStateSpace:
	"""
	The space of instances to encode CirAbstractState objects in CDocument
	"""

	def __init__(self, document: MerDocument, sta_file: str):
		"""
		:param document: the document where this space is defined
		:param sta_file: xxx.sta	{word\n}+
		"""
		self.document = document
		self.__load__(sta_file)
		return

	def __load__(self, sta_file: str):
		"""
		:param sta_file: xxx.sta	{word\n}+
		:return:
		"""
		self.states = list()		# stid --> CirAbstractState
		self._index = dict()		# word --> CirAbstractState
		with open(sta_file, 'r') as reader:
			for line in reader:
				word = line.strip()
				if len(word) > 0:
					stid = len(self.states)
					self.states.append(MerAbstractState(self, stid, word))
		for state in self.states:
			self._index[state.get_word()] = state
		return

	def get_document(self):
		"""
		:return: the document where this space is defined
		"""
		return self.document

	def __len__(self):
		"""
		:return: the number of instances created in the space
		"""
		return len(self.states)

	def get_states(self):
		"""
		:return: the set of instances to encode CirAbstractState(s) in CDocument.
		"""
		return self.states

	def get_state(self, stid: int):
		"""
		:param stid:
		:return: the instance to encode CirAbstractState with respect to integer feature
		"""
		return self.states[stid]

	def get_words(self):
		"""
		:return: the set of words to encode CirAbstractState(s)
		"""
		return self._index.keys()

	def has_state_of(self, word: str):
		"""
		:param word:
		:return: whether there exists instance of CirAbstractState specified by the word
		"""
		return word in self._index

	def get_state_of(self, word: str):
		"""
		:param word:
		:return: the instance of CirAbstractState specified by the word
		"""
		return self._index[word]

	def normal(self, features):
		"""
		:param features: the set of integer features of MerAbstractState(s) encoded
		:return: the unique list of valid features
		"""
		u_features = list()
		for feature in features:
			feature: int
			if (feature >= 0) and (feature < len(self.states)):
				if not (feature in u_features):
					u_features.append(feature)
		u_features.sort()
		return u_features

	def encode(self, words):
		"""
		:param words:	the set of words or MerAbstractState(s) in space
		:return: 		the unique list of integer features of states
		"""
		features = set()
		for word in words:
			if isinstance(word, MerAbstractState):
				state = word
				features.add(state.get_stid())
			elif word in self._index:
				state = self._index[word]
				features.add(state.get_stid())
			else:
				continue
		return self.normal(features)

	def decode(self, features):
		"""
		:param features: the set of integer features to encode the CirAbstractStates
		:return:
		"""
		features = self.normal(features)
		states = list()
		for feature in features:
			states.append(self.states[feature])
		return states


class MerAbstractState:
	"""
	the memory-reduced instance to encode CirAbstractState in CDocument
	"""

	def __init__(self, space, stid: int, word: str):
		"""
		:param space: 	the space where this instance is created
		:param stid: 	the integer ID to denote the state feature
		:param word: 	the word to specify the CirAbstractState in document
		"""
		self.space = space
		self.stid = stid
		self.word = word
		return

	def get_space(self):
		"""
		:return: the space where this instance is created
		"""
		return self.space

	def get_stid(self):
		"""
		:return: the integer ID to denote the state feature
		"""
		return self.stid

	def get_word(self):
		"""
		:return: the word to specify the CirAbstractState in document
		"""
		return self.word

	def __str__(self):
		return self.word

	def find_source(self, c_document: jctest.CDocument):
		"""
		:param c_document:
		:return: the CirAbstractState that this instance encodes in c_document
		"""
		return c_document.get_state_graph().get_state(self.word)


class MerExecutionSpace:
	"""
	The space of instances to connect Mutant with corresponding CirAbstractState(s).
	"""

	def __init__(self, document: MerDocument, zex_file: str):
		"""
		:param document: the document where this space is created
		:param zex_file: {mid [aid]+\n}+
		"""
		self.document = document
		self.__load__(zex_file)
		self.__link__()
		return

	def __load__(self, zex_file: str):
		"""
		:param zex_file:
		:return:
		"""
		self.elist = list()
		with open(zex_file, 'r') as reader:
			for line in reader:
				items = line.strip().split('\t')
				if len(items) > 0:
					mutant = self.document.mut_space.get_mutant(int(items[0].strip()))
					features = set()
					for k in range(1, len(items)):
						feature = int(items[k].strip())
						features.add(feature)
					execution = MerExecution(self, len(self.elist), mutant, features)
					self.elist.append(execution)
		return

	def __link__(self):
		self.index = dict()
		for execution in self.elist:
			mutant = execution.get_mutant()
			if not (mutant in self.index):
				self.index[mutant] = set()
			self.index[mutant].add(execution)
		return

	def get_document(self):
		return self.document

	def __len__(self):
		"""
		:return: the number of instances to encode executions in the space
		"""
		return len(self.elist)

	def get_executions(self):
		"""
		:return: the set of instances to encode executions in the space
		"""
		return self.elist

	def get_execution(self, eid: int):
		"""
		:param eid:
		:return: the kth execution to connect a mutant with features of states
		"""
		return self.elist[eid]

	def get_mutants(self):
		"""
		:return: the set of mutants being connected with any states in the graph
		"""
		return self.index.keys()

	def has_executions_of(self, mutant: MerMutant):
		"""
		:param mutant:
		:return: whether there exist execution(s) referring to the mutant
		"""
		return mutant in self.index

	def get_executions_of(self, mutant: MerMutant):
		"""
		:param mutant:
		:return: the set of execution-instances referring to the mutant
		"""
		if mutant in self.index:
			return self.index[mutant]
		else:
			return set()


class MerExecution:
	"""
	the instance to connect Mutant with corresponding CirAbstractState(s)
	"""

	def __init__(self, space: MerExecutionSpace, eid: int, mutant: MerMutant, features):
		"""
		:param space: 		the space where this execution instance is created
		:param eid:			the integer ID of this instance in the space
		:param mutant: 		the mutant to be killed by features (given states)
		:param features: 	the integer IDs of the states to annotate mutation
		"""
		self.space = space
		self.eid = eid
		self.mutant = mutant
		self.features = space.get_document().get_state_space().normal(features)
		return

	def get_space(self):
		"""
		:return: the space where this execution instance is defined
		"""
		return self.space

	def get_mutant(self):
		"""
		:return: the mutant to be executed in this instance
		"""
		return self.mutant

	def get_features(self):
		"""
		:return: the set of integers to encode the CirAbstractState instances in MerAbstractStateSpace
		"""
		return self.features

	def get_states(self):
		"""
		:return: the set of instances of CirAbstractState(s) connected with mutant in the execution
		"""
		states = list()
		for feature in self.features:
			state = self.space.document.sta_space.get_state(feature)
			states.append(state)
		return states


class MerDataEncoder:
	"""
	It implements the encoding of CDocument to MerDocument dataset.
	"""

	@staticmethod
	def __encode_tst_file__(space: jcmuta.TestCaseSpace, tst_file: str):
		"""
		:param space:
		:param tst_file: xxx.tst {tid\n}+
		:return:
		"""
		with open(tst_file, 'w') as writer:
			for test_case in space.get_test_cases():
				test_case: jcmuta.TestCase
				writer.write("{}\n".format(test_case.get_test_id()))
		return

	@staticmethod
	def __encode_mut_file__(space: jcmuta.MutantSpace, mut_file: str):
		"""
		:param space:
		:param mut_file: {mid cid wid sid res\n}+
		:return:
		"""
		with open(mut_file, 'w') as writer:
			for mutant in space.get_mutants():
				mutant: jcmuta.Mutant
				mid = mutant.get_muta_id()
				cid = mutant.get_c_mutant().get_muta_id()
				wid = mutant.get_w_mutant().get_muta_id()
				sid = mutant.get_s_mutant().get_muta_id()
				res = mutant.get_result().result.strip()
				writer.write("{}\t{}\t{}\t{}\t{}\n".format(mid, cid, wid, sid, res))
		return

	@staticmethod
	def __encode_sta_file__(space: jctest.CirAbstractGraph, sta_file: str):
		"""
		:param space:
		:param sta_file: {word\n}+
		:return: mapping from word to integer stid
		"""
		words = space.get_words()
		results = dict()
		stid = 0
		with open(sta_file, 'w') as writer:
			for word in words:
				writer.write("{}\n".format(word))
				results[space.get_state(word)] = stid
				stid += 1
		return results

	@staticmethod
	def __encode_zex_file__(space: jctest.CirAbstractGraph, zex_file: str, results: dict):
		"""
		:param space:
		:param zex_file: {mid {feature+}\n}+
		:param results: mapping from CirAbstractState to integer ID
		:return:
		"""
		with open(zex_file, 'w') as writer:
			for mutant in space.get_mutants():
				## 1. collect the ast_mutant nodes
				roots = space.get_nodes_of(mutant)
				## 2. collect the children states
				states = set()
				for root in roots:
					root: jctest.CirAbstractNode
					nodes = root.derive_subtree()
					for node in nodes:
						if node.is_mutant_node():
							continue
						else:
							states.add(node.get_state())
							for annotation in node.get_annotations():
								states.add(annotation)
				## 3. generate the feature vector and mid
				mid = mutant.get_muta_id()
				features = set()
				for state in states:
					feature = results[state]
					feature: int
					features.add(feature)
				## 4. write {mid {\t feature}+ \n}
				writer.write("{}".format(mid))
				for feature in features:
					writer.write("\t{}".format(feature))
				writer.write("\n")
		return

	@staticmethod
	def encode_document(c_document: jctest.CDocument, directory: str, file_name: str):
		"""
		:param c_document:
		:param directory:
		:param file_name:
		:return:
		"""
		tst_file = os.path.join(directory, file_name + ".tst")
		mut_file = os.path.join(directory, file_name + ".mut")
		sta_file = os.path.join(directory, file_name + ".sta")
		zex_file = os.path.join(directory, file_name + ".zex")
		MerDataEncoder.__encode_tst_file__(c_document.get_project().test_space, tst_file)
		MerDataEncoder.__encode_mut_file__(c_document.get_project().muta_space, mut_file)
		res = MerDataEncoder.__encode_sta_file__(c_document.get_state_graph(), sta_file)
		MerDataEncoder.__encode_zex_file__(c_document.get_state_graph(), zex_file, res)
		return


def encode_all(in_directory: str, middle_name: str, ou_directory: str):
	"""
	:param in_directory:
	:param middle_name:
	:param ou_directory:
	:return:
	"""
	for file_name in os.listdir(in_directory):
		print("Encoding CDocument of Program {}".format(file_name))
		directory = os.path.join(in_directory, file_name)
		c_document = jctest.CDocument(directory, file_name, middle_name)
		directory = os.path.join(ou_directory, file_name)
		if not os.path.exists(directory):
			os.mkdir(directory)
		MerDataEncoder.encode_document(c_document, directory, file_name)
		tests = len(c_document.get_project().test_space.get_test_cases())
		mutants = len(c_document.get_project().muta_space.get_mutants())
		states = len(c_document.get_state_graph().get_states())
		muta_execs = len(c_document.get_state_graph().get_mutants())
		print("\t{} tests; {} mutants; {} executions; {} states.".format(tests, mutants, muta_execs, states))
	print()
	return


def decode_all(ou_directory: str):
	"""
	:param ou_directory:
	:return:
	"""
	for file_name in os.listdir(ou_directory):
		directory = os.path.join(ou_directory, file_name)
		m_document = MerDocument(directory, file_name)
		print("Decode MerDocument of Program {}.".format(m_document.get_name()))
		tests = len(m_document.get_test_space().get_test_cases())
		mutants = len(m_document.get_mutant_space().get_mutants())
		states = len(m_document.get_state_space().get_states())
		muta_execs = len(m_document.get_execution_space().get_mutants())
		print("\t{} tests; {} mutants; {} executions; {} states.".format(tests, mutants, muta_execs, states))
	print()
	return


if __name__ == "__main__":
	f_directory = "/home/dzt2/Development/Data/zext/features"
	e_directory = "/home/dzt2/Development/Data/zext/encoding"
	encode_all(f_directory, "pdg", e_directory)
	decode_all(e_directory)
	print("Testing End for All...")

