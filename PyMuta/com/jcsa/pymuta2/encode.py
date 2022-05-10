"""It defines the memory-reduced representation for encoding instances defined in pymuta2/libs package"""


import os
import com.jcsa.pymuta2.libs.muta as jcmuta


class MerDocument:
	"""
	It encodes all the memory-reduced instances for representing other objects.
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
		self.test_space = MerTestCaseSpace(self, tst_file)
		self.mutant_space = MerMutantSpace(self, mut_file)
		self.state_space = MerContextStateSpace(self, sta_file, zex_file)
		return

	def get_name(self):
		return self.name

	def get_test_space(self):
		return self.test_space

	def get_mutant_space(self):
		return self.mutant_space

	def get_state_space(self):
		return self.state_space


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

	def find_source(self, project: jcmuta.CProject):
		"""
		:param project:
		:return: the TestCase instance encoded by this one
		"""
		return project.test_space.get_test_case(self.tid)


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

	def get_states(self):
		"""
		:return: the set of states referred by this mutant
		"""
		mutant_exec = self.get_space().document.state_space.get_execution_of(self)
		if mutant_exec is None:
			return list()
		return mutant_exec.get_states()

	def find_source(self, project: jcmuta.CProject):
		"""
		:param project:
		:return: the Mutant object that is encoded by this one
		"""
		return project.muta_space.get_mutant(self.mid)


class MerContextState:
	"""
	It encodes the memory-reduced instance representing ContextMutationState
	"""

	def __init__(self, space, sid: int, word: str):
		"""
		:param space: 	where this state is defined
		:param sid: 	integer ID of this state
		:param word: 	category$location$loperand$roperand
		"""
		space: MerContextStateSpace
		self.space = space
		self.sid = sid
		self.word = word
		return

	def get_space(self):
		return self.space

	def get_sid(self):
		return self.sid

	def get_word(self):
		return self.word

	def __str__(self):
		return self.word

	def find_source(self, project: jcmuta.CProject):
		"""
		:param project:
		:return: the ContextMutationState encoded by this instance
		"""
		return project.get_state(self.word)


class MerContextExecution:
	"""
	Each line refers to a mutant and its corresponding states.
	"""

	def __init__(self, space, eid: int, mutant: MerMutant, features):
		"""
		:param space:
		:param mutant:
		:param features:
		"""
		space: MerContextStateSpace
		self.space = space
		self.eid = eid
		self.mutant = mutant
		self.features = space.normal(features)
		return

	def get_space(self):
		return self.space

	def get_eid(self):
		return self.eid

	def get_mutant(self):
		return self.mutant

	def get_features(self):
		return self.features

	def get_states(self):
		return self.space.decode(self.features)


class MerContextStateSpace:
	"""
	The space of memory-reduced instances of MerContextState
	"""

	def __init__(self, document: MerDocument, file_path: str, link_path: str):
		self.document = document
		self.__load_state__(file_path)
		self.__load_links__(link_path)
		return

	def get_document(self):
		return self.document

	def __load_state__(self, file_path: str):
		"""
		:param file_path:
		:return:
		"""
		self.states = list()
		with open(file_path, 'r') as reader:
			for line in reader:
				if len(line.strip()) > 0:
					word = line.strip()
					sid = len(self.states)
					self.states.append(MerContextState(self, sid, word))
		return

	def __load_links__(self, link_file: str):
		"""
		:param link_file: mid {state_id}+
		:return:
		"""
		self.lines = list()
		with open(link_file, 'r') as reader:
			for line in reader:
				if len(line.strip()) > 0:
					items = line.strip().split('\t')
					mutant = self.document.mutant_space.get_mutant(int(items[0].strip()))
					features = set()
					for k in range(1, len(items)):
						sid = int(items[k].strip())
						if (sid >= 0) and (sid < len(self.states)):
							features.add(sid)
					self.lines.append(MerContextExecution(self, len(self.lines), mutant, features))
		return

	def get_states(self):
		return self.states

	def normal(self, features):
		"""
		:param features:
		:return:
		"""
		feature_list = list()
		for feature in features:
			feature: int
			if (feature >= 0) and (feature < len(self.states)):
				if not (feature in feature_list):
					feature_list.append(feature)
		feature_list.sort()
		return feature_list

	def encode(self, states):
		"""
		:param states: 	the set of MerContextState to be encoded
		:return: 		the sorted list of their integer IDs
		"""
		features = set()
		for state in states:
			state: MerContextState
			sid = state.get_sid()
			features.add(sid)
		return self.normal(features)

	def decode(self, features):
		"""
		:param features:
		:return: the set of MerContextState encoded by the integer vector
		"""
		states = list()
		for feature in self.normal(features):
			state = self.states[feature]
			state: MerContextState
			states.append(state)
		return states

	def get_executions(self):
		return self.lines

	def get_execution(self, eid: int):
		return self.lines[eid]

	def get_execution_of(self, mutant: MerMutant):
		"""
		:param mutant:
		:return: the execution referring to the mutant
		"""
		for execution in self.get_executions():
			if execution.get_mutant() == mutant:
				return execution
		return None


class MerDocumentEncoder:
	"""
	It implements the encoding of MerDocument data from CProject
	"""

	@staticmethod
	def __encode_tst_file__(project: jcmuta.CProject, tst_file: str):
		"""
		:param project:
		:param tst_file:
		:return:
		"""
		with open(tst_file, 'w') as writer:
			for test_case in project.test_space.get_test_cases():
				test_case: jcmuta.TestCase
				writer.write("{}\n".format(test_case.get_test_id()))
		return

	@staticmethod
	def __encode_mut_file__(project: jcmuta.CProject, mut_file: str):
		"""
		:param project:
		:param mut_file: {mid cid wid sid res\n}+
		:return:
		"""
		space = project.muta_space
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
	def __encode_sta_file__(project: jcmuta.CProject, sta_file: str):
		"""
		:param project:
		:param sta_file:
		:return:
		"""
		results = dict()	#	state --> int
		with open(sta_file, 'w') as writer:
			for key, state in project.states.items():
				key: str
				state: jcmuta.ContextMutationState
				if not (state in results):
					writer.write("{}\n".format(key))
					results[state] = len(results)
		return results

	@staticmethod
	def __encode_zex_file__(project: jcmuta.CProject, zex_file: str, results: dict):
		"""
		:param project:
		:param zex_file:
		:param results: map from ContextMutationState to its integer ID
		:return:
		"""
		with open(zex_file, 'w') as writer:
			for mutant in project.context_tree.get_mutants():
				nodes = project.context_tree.get_nodes_of(mutant)
				features = set()
				for node in nodes:
					# features.add(results[node.get_state()])
					for annotation in node.get_annotations():
						features.add(results[annotation])
				writer.write("{}".format(mutant.get_muta_id()))
				for feature in features:
					writer.write("\t{}".format(feature))
				writer.write("\n")
		return

	@staticmethod
	def encode_document(project: jcmuta.CProject, directory: str, file_name: str):
		"""
		:param project:
		:param directory:
		:param file_name:
		:return:
		"""
		tst_file = os.path.join(directory, file_name + ".tst")
		mut_file = os.path.join(directory, file_name + ".mut")
		sta_file = os.path.join(directory, file_name + ".sta")
		zex_file = os.path.join(directory, file_name + ".zex")
		MerDocumentEncoder.__encode_tst_file__(project, tst_file)
		MerDocumentEncoder.__encode_mut_file__(project, mut_file)
		results = MerDocumentEncoder.__encode_sta_file__(project, sta_file)
		MerDocumentEncoder.__encode_zex_file__(project, zex_file, results)
		return


def encode_all(in_directory: str, ou_directory: str):
	"""
	:param in_directory:
	:param ou_directory:
	:return:
	"""
	for file_name in os.listdir(in_directory):
		print("Encoding CDocument of Program {}".format(file_name))
		directory = os.path.join(in_directory, file_name)
		c_project = jcmuta.CProject(directory, file_name)
		directory = os.path.join(ou_directory, file_name)
		if not os.path.exists(directory):
			os.mkdir(directory)
		MerDocumentEncoder.encode_document(c_project, directory, file_name)
		tests = len(c_project.test_space.get_test_cases())
		mutants = len(c_project.muta_space.get_mutants())
		states = len(c_project.states)
		muta_execs = len(c_project.context_tree.get_mutants())
		print("\t{} tests; {} mutants; {} executions; {} states.".format(tests, mutants, muta_execs, states))
	print()
	return


def decode_all(in_directory: str, ou_directory: str):
	"""
	:param in_directory:
	:param ou_directory:
	:return:
	"""
	for file_name in os.listdir(ou_directory):
		directory = os.path.join(in_directory, file_name)
		c_project = jcmuta.CProject(directory, file_name)
		directory = os.path.join(ou_directory, file_name)
		m_document = MerDocument(directory, file_name)
		print("Decode MerDocument of Program {}.".format(m_document.name))
		tests = len(m_document.get_test_space().get_test_cases())
		mutants = len(m_document.get_mutant_space().get_mutants())
		states = len(m_document.get_state_space().get_states())
		muta_execs = len(m_document.get_state_space().get_executions())
		print("\t{} tests; {} mutants; {} executions; {} states.".format(tests, mutants, muta_execs, states))
		out_file = os.path.join(directory, file_name + ".txt")
		with open(out_file, 'w') as writer:
			for execution in m_document.get_state_space().get_executions():
				mutant = execution.get_mutant().find_source(c_project)
				states = execution.get_states()
				result = "Alive"
				if mutant.get_result().is_killed_in(None):
					result = "Killed"
				writer.write("\tMID#{}\t{}\t{}\t{}\t#{}\t\"{}\"\t[{}]\n".format(mutant.get_muta_id(), result,
																				mutant.get_mutation().get_mutation_class(),
																				mutant.get_mutation().get_mutation_operator(),
																				mutant.get_mutation().get_location().line_of(False),
																				mutant.get_mutation().get_location().generate_code(64),
																				mutant.get_mutation().get_parameter()))
				for state in states:
					c_state = state.find_source(c_project)
					writer.write("\t==>\t{}\t#{}\t\"{}\"\t({})\t({})".format(c_state.get_category(),
																			 c_state.get_location().get_ast_source().line_of(False),
																			 c_state.get_location().get_ast_source().generate_code(64),
																			 c_state.get_loperand().get_code(),
																			 c_state.get_roperand().get_code()))
					writer.write("\n")
				writer.write("\n")
	print()
	return


if __name__ == "__main__":
	f_directory = "/home/dzt2/Development/Data/zext2/features"
	e_directory = "/home/dzt2/Development/Data/zext2/encoding"
	encode_all(f_directory, e_directory)
	decode_all(f_directory, e_directory)
	print("Testing End for All...")

