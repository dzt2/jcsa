"""This file defines the memory-reduced feature models to encode the patterns being mined."""


import os
import com.jcsa.pymuta.libs.base as jcbase
import com.jcsa.pymuta.libs.code as jccode
import com.jcsa.pymuta.libs.muta as jcmuta
import com.jcsa.pymuta.libs.symb as jcsymb


class MerDocument:
	"""
	It preserves the memory-reduced instances representing the original data elements.
	"""

	def __init__(self, directory: str, file_name: str):
		"""
		:param directory: the directory where the encoded data sets are preserved
		:param file_name: the name of the mutation testing and program under test
		"""
		tst_file = os.path.join(directory, file_name + ".tst")
		mut_file = os.path.join(directory, file_name + ".mut")
		stt_file = os.path.join(directory, file_name + ".stt")
		self.test_space = MerTestCaseSpace(self, tst_file)
		self.mutant_space = MerMutantSpace(self, mut_file)
		self.state_space = MerAbstractStateSpace(self, stt_file)
		return


class MerTestCase:
	"""
	It defines the memory-reduced instance of encoding TestCase in CProject.
	"""

	def __init__(self, space, tid: int):
		"""
		:param space: 	the space in which the test case instance is defined
		:param tid: 	the integer ID referring to a TestCase it represents
		"""
		space: MerTestCaseSpace
		self.space = space
		self.tid = tid
		return

	def get_space(self):
		"""
		:return: the space in which the test case instance is defined
		"""
		return self.space

	def get_tid(self):
		"""
		:return: the unique ID that refers to the TestCase it represents
		"""
		return self.tid

	def __str__(self):
		return "tst@{}".format(self.tid)

	def find_source(self, c_document: jcsymb.CDocument):
		"""
		:param c_document: 	the original document in which the TestCase it represents will be derived
		:return: 			the original TestCase object that this memory-reduced instance represents
		"""
		return c_document.get_project().test_space.get_test_case(self.tid)


class MerTestCaseSpace:
	"""
	The space of memory-reduced instances to encode the TestCase in CProject.
	"""

	def __init__(self, document: MerDocument, tst_file: str):
		self.document = document
		self.__load__(tst_file)
		return

	def __load__(self, tst_file: str):
		"""
		:param tst_file: {tid}\n
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

	def __len__(self):
		"""
		:return: the number of instances defined in the space to represent TestCase(s) in CProject
		"""
		return len(self.test_cases)

	def __str__(self):
		return "TST[{}:{}]".format(0, len(self.test_cases) - 1)

	def get_test_cases(self):
		"""
		:return: the list of memory-reduced instances encoding TestCase(s) objects in CProject.
		"""
		return self.test_cases

	def get_test_case(self, tid: int):
		"""
		:param tid: the unique ID referring to the TestCase that the instance represents
		:return: 	the memory-reduced instance to encode the TestCase from the CProject
		"""
		return self.test_cases[tid]


class MerMutant:
	"""
	It defines the memory-reduced instance that represents the Mutant in CProject.
	"""

	def __init__(self, space, mid: int):
		"""
		:param space: 	the space in which the memory-reduced instance is preserved
		:param mid: 	the unique ID referring to Mutant that the instance represents
		"""
		space: MerMutantSpace
		self.space = space
		self.mid = mid
		self.res = ""
		self.lst = [self, self, self]
		return

	def get_space(self):
		"""
		:return: the space in which the memory-reduced instance is preserved
		"""
		return self.space

	def get_mid(self):
		"""
		:return: the unique ID referring to Mutant that the instance represents
		"""
		return self.mid

	def get_result(self):
		"""
		:return: the bit-string to encode of which tests kill this mutant
		"""
		return self.res

	def is_killed_by(self, test):
		"""
		:param test: 	either MerTestCase or int
		:return: 		whether the mutant it represents is killed by the specified test
		"""
		if isinstance(test, MerTestCase):
			tid = test.get_tid()
		else:
			test: int
			tid = test
		if (tid >= 0) and (tid < len(self.res)):
			return self.res[tid] == '1'
		return False

	def is_killed_in(self, tests):
		"""
		:param tests: 	the set of MerTestCase or int, or None for all
		:return: 		whether the mutant it represents is killed by any test specified in the inputs set
		"""
		if tests is None:
			return '1' in self.res
		else:
			for test in tests:
				if self.is_killed_by(test):
					return True
			return False

	def get_c_mutant(self):
		"""
		:return: the coverage version of this mutant
		"""
		return self.lst[0]

	def get_w_mutant(self):
		"""
		:return: the weak version of this mutant
		"""
		return self.lst[1]

	def get_s_mutant(self):
		"""
		:return: the strong version of this mutant
		"""
		return self.lst[2]

	def __str__(self):
		return "mut@{}".format(self.mid)

	def find_source(self, c_document: jcsymb.CDocument):
		"""
		:param c_document:	the original document in which the Mutant it represents will be derived
		:return: 			the original Mutant object that this memory-reduced instance represents
		"""
		return c_document.get_project().muta_space.get_mutant(self.mid)


class MerMutantSpace:
	"""
	It defines the space of memory-reduced instances for encoding Mutant in
	"""

	def __init__(self, document: MerDocument, mut_file: str):
		"""
		:param document: the document where this space is defined
		:param mut_file: xxx.mut {mid cid wid sid res\n}+
		"""
		self.document = document
		self.__load__(mut_file)
		self.__link__(mut_file)
		return

	def __load__(self, mut_file: str):
		"""
		:param mut_file: 	xxx.mut {mid cid wid sid res\n}+
		:return: 			it builds the set of instances that encode Mutant(s) in CProject.
		"""
		mid_number = 0
		with open(mut_file, 'r') as reader:
			for line in reader:
				if len(line.strip()) > 0:
					items = line.strip().split('\t')
					mid = int(items[0].strip())
					if mid >= mid_number:
						mid_number = mid + 1
		self.mutants = list()
		for mid in range(0, mid_number):
			self.mutants.append(MerMutant(self, mid))
		return

	def __link__(self, mut_file: str):
		"""
		:param mut_file: 	xxx.mut {mid cid wid sid res\n}+
		:return: 			it links each mutant with coverage, weak and strong versions.
		"""
		with open(mut_file, 'r') as reader:
			for line in reader:
				if len(line.strip()) > 0:
					items = line.strip().split('\t')
					mutant = self.mutants[int(items[0].strip())]
					mutant.lst[0] = self.mutants[int(items[1].strip())]
					mutant.lst[1] = self.mutants[int(items[2].strip())]
					mutant.lst[2] = self.mutants[int(items[3].strip())]
					if len(items) > 4:
						mutant.res = items[4].strip()
		return

	def get_document(self):
		"""
		:return: the document where the space is defined
		"""
		return self.document

	def get_mutants(self):
		"""
		:return: the set of instances encoding Mutant(s) defined in CProject.
		"""
		return self.mutants

	def get_mutant(self, mid: int):
		"""
		:param mid: the unique ID referring to the Mutant that this instance represents
		:return: 	the instance to encode the Mutant object specified by the input mid
		"""
		return self.mutants[mid]

	def __len__(self):
		"""
		:return: the number of instances encoding Mutant(s) defined in CProject.
		"""
		return len(self.mutants)

	def __str__(self):
		return "MUT[{}:{}]".format(0, len(self.mutants))


class MerAbstractState:
	"""
	It defines a memory-reduced instance that encodes the CirAbstractState in CDocument.
	"""

	def __init__(self, space, aid: int, key: str):
		"""
		:param space:	the space where the memory-reduced instance is defined
		:param aid: 	the unique ID to encode the abstract state it describe
		:param key: 	the unique string as the string of state it represents
		"""
		space: MerAbstractStateSpace
		self.space = space
		self.aid = aid
		self.key = key
		return

	def get_space(self):
		"""
		:return: the space where the memory-reduced instance is defined
		"""
		return self.space

	def get_aid(self):
		"""
		:return: the unique ID to encode the abstract state it describe
		"""
		return self.aid

	def get_key(self):
		"""
		:return: the unique string as the string of state it represents
		"""
		return self.key

	def __str__(self):
		return self.key

	def find_source(self, c_document: jcsymb.CDocument):
		"""
		:param c_document: the CDocument where the original data object (CirAbstractState) is preserved
		:return: the CirAbstractState object that this instance encodes in the specified document
		"""
		return c_document.get_state_graph().get_state(self.key)


class MerAbstractStateSpace:
	"""
	It defines the space of memory-reduced instances that encode the CirAbstractState in CDocument.
	"""

	def __init__(self, document: MerDocument, stt_file: str):
		"""
		:param document: the document where this instance space is defined
		:param stt_file: xxx.stt {key\n}+
		"""
		self.document = document
		self.__load__(stt_file)
		return

	def __load__(self, stt_file: str):
		"""
		:param stt_file: xxx.stt {key\n}+
		:return: it loads the states being encoded with specified aid and key
		"""
		self.states = list()
		with open(stt_file, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					aid = len(self.states)
					key = line
					state = MerAbstractState(self, aid, key)
					self.states.append(state)
		self._index = dict()
		for state in self.states:
			key = state.get_key()
			self._index[key] = state
		return

	def __len__(self):
		return len(self.states)

	def __str__(self):
		return "STT[{}...{}]".format(0, len(self.states))

	def get_state(self, aid: int):
		"""
		:param aid: the unique ID to encode the abstract state instance in space
		:return: 	the instance of abstract state in the space specified by aid
		"""
		return self.states[aid]

	def get_state_of(self, key: str):
		"""
		:param key: the unique string of CirAbstractState that this instance encodes
		:return: 	the instance of abstract state that the string key specifies for
		"""
		return self._index[key]

	def get_states(self):
		"""
		:return: the set of instances encoding abstract states in the CDocument
		"""
		return self.states

	def get_keys(self):
		"""
		:return: the set of string keys to represent the CirAbstractState in the space
		"""
		return self._index.keys()


class MerExecution:
	"""
	It combines both mutant and its corresponding state instances subsumed by it.
	"""

	def __init__(self, space, mutant: MerMutant, features):
		"""
		:param space: 		the space where the execution instance is specified
		:param mutant: 		the mutant bounded within the execution instance
		:param features: 	the set of integer IDs to encode CirAbstractState(s)
		"""
		self.space = space
		self.mutant = mutant
		self.features = list()
		for feature in features:
			feature: int
			if feature in self.features:
				continue
			else:
				self.features.append(feature)
		self.features.sort()
		return

	def get_space(self):
		return self.space

	def get_mutant(self):
		return self.mutant

	def get_features(self):
		return self.features

	def get_states(self):
		states = set()
		document = self.space.get_document()
		document: MerDocument
		for feature in self.features:
			state = document.state_space.get_state(feature)
			states.add(state)
		return states


















