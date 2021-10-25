"""This file defines the data model of memory-reduced instances for encoding features in CProgram and CProject."""


import os
import scipy.sparse as sparse
import matplotlib.pyplot as plt
from sklearn import manifold
import com.jcsa.libs.base as jcbase
import com.jcsa.libs.muta as jcmuta
import com.jcsa.libs.test as jctest


## definition


class MerDocument:
	"""
	It defines the document to incorporate spaces of memory-reduced instances for encoding features in CDocument.
	"""

	def __init__(self, directory: str, file_name: str):
		"""
		:param directory: the project directory in which the encoding files are presented.
		:param file_name: the name of encoding files being presented in project directory.
		"""
		self.file_name = file_name
		tst_file_path = os.path.join(directory, file_name + ".tst")
		mut_file_path = os.path.join(directory, file_name + ".mut")
		ant_file_path = os.path.join(directory, file_name + ".ant")
		exc_file_path = os.path.join(directory, file_name + ".exc")
		self.test_space = MerTestCaseSpace(self, tst_file_path)
		self.muta_space = MerMutantSpace(self, mut_file_path)
		self.anot_space = MerAnnotationSpace(self, ant_file_path)
		self.exec_space = MerExecutionSpace(self, exc_file_path)
		return


class MerTestCase:
	"""
	The memory-reduced instance to encode the TestCase object in CProject using its integer ID.
	"""

	def __init__(self, space, tid: int):
		"""
		:param space: 	the space where this memory-reduced instance is created
		:param tid: 	integer ID of TestCase in CProject, encoded by this one
		"""
		space: MerTestCaseSpace
		self.space = space
		self.tid = tid
		return

	def get_space(self):
		"""
		:return: the space where this memory-reduced instance is created
		"""
		return self.space

	def get_tid(self):
		"""
		:return: integer ID of TestCase in CProject, encoded by this one
		"""
		return self.tid

	def __str__(self):
		return "tst@{}".format(self.tid)

	def find_source(self, document: jctest.CDocument):
		"""
		:param document: the document of original data source (before encoding)
		:return: the TestCase object that this instance represents in document.
		"""
		return document.get_project().test_space.get_test_case(self.tid)

	def get_kill_mutants(self, mutants):
		"""
		:param mutants:
		:return: the set of MerMutant(s) that are killed by this test
		"""
		mutant_instances = self.get_space().get_document().muta_space.decode(mutants)
		kill_mutants = set()
		for mutant in mutant_instances:
			if mutant.is_killed_by(self):
				kill_mutants.add(mutant)
		return kill_mutants

	def get_live_mutants(self, mutants):
		"""
		:param mutants:
		:return: the set of MerMutant(s) that are not killed by this
		"""
		mutant_instances = self.get_space().get_document().muta_space.decode(mutants)
		live_mutants = set()
		for mutant in mutant_instances:
			if not mutant.is_killed_by(self):
				live_mutants.add(mutant)
		return live_mutants


class MerTestCaseSpace:
	"""
	The space to incorporate memory-reduced instances for encoding TestCase in CProject.
	"""

	def __init__(self, document: MerDocument, file_path: str):
		"""
		:param document: 	the document where this space of test cases are defined
		:param file_path: 	directory/file_name.tst:	{tid\n}
		"""
		self.document = document
		self.__load__(file_path)
		return

	def __load__(self, file_path: str):
		"""
		:param file_path: 	directory/file_name.tst:	{tid\n}
		:return: 			it loads the test case instances in the space
		"""
		max_tid = -1
		with open(file_path, 'r') as reader:
			for line in reader:
				if len(line.strip()) > 0:
					tid = int(line.strip())
					if tid > max_tid:
						max_tid = tid
		self.test_cases = list()
		for tid in range(0, max_tid + 1):
			self.test_cases.append(MerTestCase(self, tid))
		return

	def get_document(self):
		"""
		:return: the document where this space of test cases are defined
		"""
		return self.document

	def __len__(self):
		"""
		:return: the number of instances created in this space
		"""
		return len(self.test_cases)

	def get_test_cases(self):
		"""
		:return: the list of memory-reduced instances for encoding TestCase in CProject in this space.
		"""
		return self.test_cases

	def get_test_case(self, tid: int):
		"""
		:param tid: the integer ID of TestCase that the output instance encodes
		:return: 	the memory-reduced instance for encoding TestCase of the id
		"""
		return self.test_cases[tid]

	def normal(self, test_case):
		"""
		:param test_case: 	MerTestCase or integer TID that the test case represents or -1 as invalid
		:return: 			the MerTestCase that the input encodes or None when input test is invalid
		"""
		if isinstance(test_case, MerTestCase):
			tid = test_case.get_tid()
		elif isinstance(test_case, int):
			tid = test_case
		else:
			tid = -1
		tid: int
		if (tid >= 0) and (tid < len(self.test_cases)):
			return self.test_cases[tid]
		return None

	def encode(self, test_cases):
		"""
		:param test_cases: 	the set of MerTestCase or integer TID for being encoded
		:return: 			the list of integer TID(s) that encodes the input tests
		"""
		if test_cases is None:
			test_cases = self.test_cases
		tid_set = set()
		for test_case in test_cases:
			test = self.normal(test_case)
			if not (test is None):
				test: MerTestCase
				tid_set.add(test.get_tid())
		tid_list = list()
		for tid in tid_set:
			tid_list.append(tid)
		tid_list.sort()
		return tid_list

	def decode(self, test_cases):
		"""
		:param test_cases: 	the set of MerTestCase or integer TID for being encoded
		:return: 			the set of MerTestCase(s) that are encoded by the input
		"""
		if test_cases is None:
			test_cases = self.test_cases
		test_instances = set()
		for test_case in test_cases:
			test = self.normal(test_case)
			if not (test is None):
				test: MerTestCase
				test_instances.add(test)
		return test_instances

	def select_random_tests(self, number: int):
		"""
		:param number: 	the number of randomly selected test cases.
		:return: 		the set of MerTestCase that are selected from the space
		"""
		if number >= len(self.test_cases):
			number = len(self.test_cases)
		test_cases = set()
		while len(test_cases) < number:
			test_case = jcbase.rand_select(self.test_cases)
			test_case: MerTestCase
			test_cases.add(test_case)
		return test_cases

	def select_killed_tests(self, targets):
		"""
		:param targets: the set of MerMutant(s) as targets for being killed
		:return: 		kill_tests, failed_targets
						(1) the set of MerTestCase(s) for killing the input targets;
						(2) the set of MerMutants that fail to be killed by outputs;
		"""
		mutants = self.get_document().muta_space.decode(targets)
		test_cases, live_mutants = set(), set()
		while len(mutants) > 0:
			## 1. randomly select a mutant for killing
			mutant = jcbase.rand_select(mutants)
			mutant: MerMutant
			mutants.remove(mutant)

			## 2. add to live if it is not killable
			if not mutant.is_killable():
				live_mutants.add(mutant)
				continue

			## 3. randomly select a killing test case
			kill_tests = mutant.get_kill_tests(None)
			kill_test = jcbase.rand_select(kill_tests)
			test_case = self.get_test_case(kill_test)
			test_cases.add(test_case)

			## 4. update the unkilled mutants from set
			killed_mutants = set()
			for mutant in mutants:
				if mutant.is_killed_by(test_case):
					killed_mutants.add(mutant)
			for mutant in killed_mutants:
				mutants.remove(mutant)
		return test_cases, live_mutants


class MerMutant:
	"""
	It denotes a memory-reduced instance for encoding Mutant in CProject.
	"""

	def __init__(self, space, mid: int):
		"""
		:param space: 	the space where this instance is created
		:param mid: 	the integer ID of Mutant that the instance encodes in the CProject.
		"""
		space: MerMutantSpace
		self.space = space
		self.mid = mid
		self.c_version = self
		self.w_version = self
		self.s_version = self
		self.result = ""
		return

	def get_space(self):
		"""
		:return: the space where this instance is created
		"""
		return self.space

	def get_mid(self):
		"""
		:return: the integer ID of Mutant that the instance encodes in the CProject.
		"""
		return self.mid

	def __str__(self):
		return "mut@{}".format(self.mid)

	def get_c_version(self):
		"""
		:return: the coverage version which is killed if the mutant is covered
		"""
		return self.c_version

	def get_w_version(self):
		"""
		:return: the weak-test version which is killed when the mutant is infected
		"""
		return self.w_version

	def get_s_version(self):
		"""
		:return: the strong version which is killed when the mutant is killed
		"""
		return self.s_version

	def get_result(self):
		"""
		:return: the 0-1 string to denote which tests kill this mutant in the project
		"""
		return self.result

	def find_source(self, document: jctest.CDocument):
		"""
		:param document: the document of original data source (before encoding)
		:return: the Mutant instance that this memory-reduced instance encodes.
		"""
		return document.get_project().muta_space.get_mutant(self.mid)

	## inference

	def is_killed_by(self, test_case):
		"""
		:param test_case: 	either MerTestCase or Integer (TID)
		:return: 			True if the mutant is killed by the given test case
		"""
		if isinstance(test_case, MerTestCase):
			index = test_case.get_tid()
		elif isinstance(test_case, int):
			index = test_case
		else:
			index = -1
		if (index >= 0) and (index < len(self.result)):
			return self.result[index] == '1'
		return False

	def is_killed_in(self, test_cases):
		"""
		:param test_cases: 	the set of MerTestCase or integer TID for killing the mutant
		:return: 			True if the mutant is killed by any tests included in inputs
		"""
		if test_cases is None:
			return '1' in self.result
		else:
			for test_case in test_cases:
				if self.is_killed_by(test_case):
					return True
			return False

	def is_killable(self):
		"""
		:return: whether the mutant can be killed by any test in the project
		"""
		return '1' in self.result

	def get_kill_tests(self, test_cases):
		"""
		:param test_cases: 	the set of MerTestCase or integer TID for killing the mutant
		:return: 			the set of integer TID of test cases for killing this mutant
		"""
		test_vector = self.get_space().get_document().test_space.encode(test_cases)
		kill_vector = list()
		for tid in test_vector:
			if self.is_killed_by(tid):
				kill_vector.append(tid)
		return kill_vector

	def get_live_tests(self, test_cases):
		"""
		:param test_cases: 	the set of MerTestCase or integer TID for killing the mutant
		:return: 			the set of integer TID of tests that cannot kill this mutant
		"""
		test_vector = self.get_space().get_document().test_space.encode(test_cases)
		live_vector = list()
		for tid in test_vector:
			if not self.is_killed_by(tid):
				live_vector.append(tid)
		return live_vector


class MerMutantSpace:
	"""
	The space to incorporate the instances for encoding Mutant in CProject.
	"""

	def __init__(self, document: MerDocument, file_path: str):
		"""
		:param document: 	the document where this space is defined
		:param file_path: 	directory/file_name.mut:	{mid cid wid sid res\n}+
		"""
		self.document = document
		self.__load__(file_path)
		self.__link__(file_path)
		return

	def __load__(self, file_path: str):
		"""
		:param file_path: 	directory/file_name.mut:	{mid cid wid sid res\n}+
		:return: 			it creates the list of instances for encoding Mutant
		"""
		max_mid = -1
		with open(file_path, 'r') as reader:
			for line in reader:
				if len(line.strip()) > 0:
					items = line.strip().split('\t')
					mid = int(items[0].strip())
					if mid > max_mid:
						max_mid = mid
		self.mutants = list()
		for mid in range(0, max_mid + 1):
			self.mutants.append(MerMutant(self, mid))
		return

	def __link__(self, file_path: str):
		"""
		:param file_path: 	directory/file_name.mut:	{mid cid wid sid res\n}+
		:return:			it connects the mutant to its versions and the result
		"""
		with open(file_path, 'r') as reader:
			for line in reader:
				if len(line.strip()) > 0:
					items = line.strip().split('\t')
					mid = int(items[0].strip())
					cid = int(items[1].strip())
					wid = int(items[2].strip())
					sid = int(items[3].strip())
					res = ""
					if len(items) > 4:
						res = items[4].strip()
					mutant = self.mutants[mid]
					mutant.c_version = self.mutants[cid]
					mutant.w_version = self.mutants[wid]
					mutant.s_version = self.mutants[sid]
					mutant.result = res
		return

	def get_document(self):
		"""
		:return: the document where this space is defined
		"""
		return self.document

	def __len__(self):
		"""
		:return: the number of memory-reduced instances for encoding Mutant in this space
		"""
		return len(self.mutants)

	def get_mutants(self):
		"""
		:return: the list of memory-reduced instances for encoding Mutant in this space
		"""
		return self.mutants

	def get_mutant(self, mid: int):
		"""
		:param mid:
		:return: the memory-reduced instance to encode Mutant w.r.t. the given mid
		"""
		return self.mutants[mid]

	def normal(self, mutant):
		"""
		:param mutant: 	either MerMutant or integer MID to encode the instance
		:return: 		MerMutant instance that is represented by the input in the space
		"""
		if isinstance(mutant, MerMutant):
			mid = mutant.get_mid()
		elif isinstance(mutant, int):
			mid = mutant
		else:
			mid = -1
		mid: int
		if (mid >= 0) and (mid < len(self.mutants)):
			return self.mutants[mid]
		return None

	def encode(self, mutants):
		"""
		:param mutants: the set of MerMutant(s) or integer MID to be encoded
		:return: 		the set of integer MID(s) that encode the input mutants
		"""
		if mutants is None:
			mutants = self.mutants
		mid_set = set()
		for mutant in mutants:
			mutant_instance = self.normal(mutant)
			if not (mutant_instance is None):
				mutant_instance: MerMutant
				mid_set.add(mutant_instance.get_mid())
		mid_list = list()
		for mid in mid_set:
			mid_list.append(mid)
		mid_list.sort()
		return mid_list

	def decode(self, mutants):
		"""
		:param mutants: the set of MerMutant or integer MID to be deocded as MerMutant in the space
		:return: 		the set of MerMutant(s) that are encoded by the input mutants or integers
		"""
		if mutants is None:
			mutants = self.mutants
		mutant_instances = set()
		for mutant in mutants:
			mutant_instance = self.normal(mutant)
			if not (mutant_instance is None):
				mutant_instance: MerMutant
				mutant_instances.add(mutant_instance)
		return mutant_instances


class MerAnnotation:
	"""
	It denotes a memory-reduced instance for encoding CirAnnotation in CDocument.
	"""

	def __init__(self, space, aid: int, key: str):
		"""
		:param space: the space where this instance is created
		:param aid: the integer ID to encode the annotation defined in CDocument.
		:param key: the unique string key to specify the annotation in CDocument.
		"""
		space: MerAnnotationSpace
		self.space = space
		self.aid = aid
		self.key = key
		return

	def get_space(self):
		"""
		:return: the space where this instance is created
		"""
		return self.space

	def get_aid(self):
		"""
		:return:  the integer ID to encode the annotation defined in CDocument.
		"""
		return self.aid

	def get_key(self):
		"""
		:return: the unique string key to specify the annotation in CDocument.
		"""
		return self.key

	def __str__(self):
		return self.key

	def find_source(self, document: jctest.CDocument):
		"""
		:param document: the document of original data source (before encoding)
		:return: CirAnnotation object that this memory-reduced instance encodes
		"""
		return document.annotation_tree.get_annotation(self.key)


class MerAnnotationSpace:
	"""
	The space of memory-reduced instances for encoding CirAnnotation in CDocument.
	"""

	def __init__(self, document: MerDocument, file_path: str):
		"""
		:param document: the document where this space is created
		:param file_path: directory/file_name.ant: {word\n}+
		"""
		self.document = document
		self.__load__(file_path)
		return

	def __load__(self, file_path: str):
		"""
		:param file_path: directory/file_name.ant: {word\n}+
		:return:
		"""
		self.alist = list()	# aid --> MerAnnotation
		self.index = dict()	# key --> MerAnnotation
		with open(file_path, 'r') as reader:
			for line in reader:
				if len(line.strip()) > 0:
					key = line.strip()
					aid = len(self.alist)
					annotation = MerAnnotation(self, aid, key)
					self.alist.append(annotation)
					self.index[key] = annotation
		return

	def get_document(self):
		"""
		:return: the document where this space is created
		"""
		return self.document

	def __len__(self):
		"""
		:return: the number of instances created for encoding CirAnnotation in this space
		"""
		return len(self.alist)

	def get_annotations(self):
		"""
		:return: the list of instances created for encoding CirAnnotation in this space
		"""
		return self.alist

	def get_annotation(self, aid: int):
		"""
		:param aid: the integer ID to encode the annotation defined in CDocument.
		:return: the memory-reduced instance to encode CirAnnotation of specified ID
		"""
		return self.alist[aid]

	def get_annotation_of(self, key: str):
		"""
		:param key: the unique string key to specify the annotation in CDocument.
		:return: the memory-reduced instance to encode CirAnnotation of specified key
		"""
		return self.index[key]

	def normal(self, features):
		"""
		:param features: the set of integer features to encode CirAnnotation.
		:return: the unique list of integer features to encode CirAnnotation.
		"""
		feature_list = list()
		for feature in features:
			feature: int
			if (feature >= 0) and (feature < len(self.alist)):
				if not (feature in feature_list):
					feature_list.append(feature)
		feature_list.sort()
		return feature_list

	def encode(self, annotations):
		"""
		:param annotations: the set of MerAnnotation to be transformed as integer feature vector
		:return: the unique sorted vector of the integer features for encoding the CirAnnotation
		"""
		features = set()
		for annotation in annotations:
			annotation: MerAnnotation
			features.add(annotation.get_aid())
		return self.normal(features)

	def decode(self, features):
		"""
		:param features: the set of integer features to encode CirAnnotation
		:return: the set of memory-reduced instances for encoding CirAnnotation
		"""
		features = self.normal(features)
		annotations = set()
		for feature in features:
			annotation = self.alist[feature]
			annotations.add(annotation)
		return annotations


class MerExecution:
	"""
	It models the memory-reduced instance for encoding SymExecution in CDocument.
	"""

	def __init__(self, space, eid: int, mutant: MerMutant, test: MerTestCase, features):
		"""
		:param space:		the space where the memory-reduced instance is created
		:param eid: 		the integer index of SymExecution in CDocument that the instance encodes
		:param mutant: 		the mutant to be killed in execution the memory-reduced instance encodes
		:param test: 		the test used for kills in execution the memory-reduced instance encodes
		:param features: 	the set of integer features encoding the CirAnnotation incorporated with
		"""
		space: MerExecutionSpace
		self.space = space
		self.eid = eid
		self.mutant = mutant
		self.test = test
		self.features = space.get_document().anot_space.normal(features)
		return

	def get_space(self):
		return self.space

	def get_eid(self):
		return self.eid

	def get_mutant(self):
		return self.mutant

	def has_test(self):
		return not (self.test is None)

	def get_test(self):
		return self.test

	def get_features(self):
		return self.features

	def get_annotations(self):
		return self.space.get_document().anot_space.decode(self.features)

	def __str__(self):
		return "exe@{}".format(self.eid)

	def find_source(self, document: jctest.CDocument):
		"""
		:param document: the document of original data source (before encoding)
		:return: the SymExecution object that this memory-reduced instance encodes
		"""
		return document.exec_space.get_execution(self.eid)


class MerExecutionSpace:
	"""
	The space of memory-reduced instances for encoding SymExecution in CDocument.
	"""

	def __init__(self, document: MerDocument, file_path: str):
		"""
		:param document: the document where this space is created
		:param file_path: directory/file_name.exc: {mid tid feature+\n}+
		"""
		self.document = document
		self.__load__(file_path)
		return

	def __read__(self, eid: int, line: str):
		"""
		:param line: mid tid features \n
		:return: MerExecution or None
		"""
		if len(line) > 0:
			items = line.strip().split('\t')
			mid = int(items[0].strip())
			tid = int(items[1].strip())
			mutant = self.document.muta_space.get_mutant(mid)
			if tid < 0:
				test = None
			else:
				test = self.document.test_space.get_test_case(tid)
			features = set()
			for k in range(2, len(items)):
				features.add(int(items[k].strip()))
			return MerExecution(self, eid, mutant, test, features)
		return None

	def __load__(self, file_path: str):
		"""
		:param file_path: directory/file_name.exc: {mid tid feature+\n}+
		:return:
		"""
		self.elist = list()
		self.index = dict()
		with open(file_path, 'r') as reader:
			for line in reader:
				execution = self.__read__(len(self.elist), line.strip())
				if not (execution is None):
					execution: MerExecution
					self.elist.append(execution)
					mutant = execution.get_mutant()
					if not (mutant in self.index):
						self.index[mutant] = list()
					self.index[mutant].append(execution)
		return

	def get_document(self):
		"""
		:return: the document where this space is created
		"""
		return self.document

	def __len__(self):
		"""
		:return: the number of memory-reduced instances for encoding SymExecution in the space
		"""
		return len(self.elist)

	def get_mutants(self):
		return self.index.keys()

	def get_executions(self):
		"""
		:return: the list of memory-reduced instances for encoding SymExecution in the space
		"""
		return self.elist

	def get_execution(self, eid: int):
		"""
		:param eid: the index of SymExecution that the memory-reduced instance encodes
		:return: the memory-reduced instance for encoding SymExecution specified by id
		"""
		return self.elist[eid]

	def get_executions_of(self, mutant: MerMutant):
		"""
		:param mutant:
		:return: the set of memory-reduced instances for encoding executions w.r.t. the mutant encoded by input mutant.
		"""
		executions = set()
		if mutant in self.index:
			for execution in self.index[mutant]:
				execution: MerExecution
				executions.add(execution)
		return executions

	def normal(self, execution):
		"""
		:param execution: 	either MerExecution or its EID integer to encode
		:return: 			the MerExecution that is encoded by the input parameter
		"""
		if isinstance(execution, MerExecution):
			eid = execution.get_eid()
		elif isinstance(execution, int):
			eid = execution
		else:
			eid = -1
		eid: int
		if (eid >= 0) and (eid < len(self.elist)):
			return self.elist[eid]
		return None

	def encode(self, executions):
		"""
		:param executions:
		:return: the sorted list of integer IDs for encoding the input executions.
		"""
		if executions is None:
			executions = self.elist
		eid_set = set()
		for execution in executions:
			exec_instance = self.normal(execution)
			if not (exec_instance is None):
				exec_instance: MerExecution
				eid_set.add(exec_instance.get_eid())
		eid_list = list()
		for eid in eid_set:
			eid_list.append(eid)
		eid_list.sort()
		return eid_list

	def decode(self, executions):
		"""
		:param executions:
		:return: the set of MerExecution that are encoded by the input executions or integers
		"""
		if executions is None:
			executions = self.elist
		exec_instances = set()
		for execution in executions:
			exec_instance = self.normal(execution)
			if not (exec_instance is None):
				exec_instance: MerExecution
				exec_instances.add(exec_instance)
		return exec_instances

	def new_feature_matrix(self):
		"""
		:return: feature matrix in which: M[eid, aid] = 1 iff. execution[eid] uses annotation[aid]
		"""
		lines, columns = len(self.elist), len(self.document.anot_space.get_annotations())
		x_matrix = sparse.lil_matrix((lines, columns))
		for execution in self.elist:
			for feature in execution.get_features():
				x_matrix[execution.get_eid(), feature] = 1
		return x_matrix

	def new_label_sequence(self, used_tests):
		"""
		:param used_tests: 	the set of test cases for killing executions in the document
		:return: 			the sequence (list) of 0-1 to denote of which executions are killed
		"""
		y_labels = list()
		for execution in self.elist:
			if execution.get_mutant().is_killed_in(used_tests):
				y_labels.append(1)
			else:
				y_labels.append(0)
		return y_labels

	def new_semantic_matrix(self, used_tests):
		"""
		:param used_tests:	the set of test cases for killing executions in the document
		:return: 			semantic_matrix, test_vector
							(1) the semantic matrix: M[eid, index] = 1 iff. execution[eid] is killed by test_vector[index]
							(2) the sequence of integer TID(s) for killing the symbolic executions in the mutation space
		"""
		test_vector = self.get_document().test_space.encode(used_tests)
		lines, columns = len(self.get_executions()), len(test_vector)
		semantic_matrix = sparse.lil_matrix((lines, columns))
		for execution in self.elist:
			for index in range(0, len(test_vector)):
				test_case = test_vector[index]
				if execution.get_mutant().is_killed_by(test_case):
					semantic_matrix[execution.get_eid(), index] = 1
		return semantic_matrix, test_vector


## parsing


class C2MDocumentParse:
	"""
	It implements the encoding of MerDocument files from CDocument features.
	"""

	@staticmethod
	def __encode_tst_file__(document: jctest.CDocument, directory: str, file_name: str):
		"""
		:param document:
		:param directory:
		:param file_name:
		:return: directory/file_name.tst: {tid\n}+
		"""
		file_path = os.path.join(directory, file_name + ".tst")
		with open(file_path, 'w') as writer:
			for test_case in document.get_project().test_space.get_test_cases():
				test_case: jcmuta.TestCase
				writer.write("{}\n".format(test_case.get_test_id()))
		return

	@staticmethod
	def __encode_mut_file__(document: jctest.CDocument, directory: str, file_name: str):
		"""
		:param document:
		:param directory:
		:param file_name:
		:return: directory/file_name.mut {mid cid wid sid result\n}+
		"""
		file_path = os.path.join(directory, file_name + ".mut")
		with open(file_path, 'w') as writer:
			for mutant in document.get_project().muta_space.get_mutants():
				mutant: jcmuta.Mutant
				mid = mutant.get_muta_id()
				cid = mutant.get_c_mutant().get_muta_id()
				wid = mutant.get_w_mutant().get_muta_id()
				sid = mutant.get_s_mutant().get_muta_id()
				res = mutant.get_result().result
				writer.write("{}\t{}\t{}\t{}\t{}\n".format(mid, cid, wid, sid, res))
		return

	@staticmethod
	def __encode_ant_file__(document: jctest.CDocument, directory: str, file_name: str):
		"""
		:param document:
		:param directory:
		:param file_name:
		:return: mapping from CirAnnotation code to integer feature
		"""
		file_path = os.path.join(directory, file_name + ".ant")
		index, counter = dict(), 0
		with open(file_path, 'w') as writer:
			for word in document.annotation_tree.get_words():
				word: str
				word = word.strip()
				index[word] = counter
				counter = counter + 1
				writer.write("{}\n".format(word))
		return index

	@staticmethod
	def __encode_features__(execution: jctest.SymExecution, index: dict):
		"""
		:param execution:
		:param index: mapping from CirAnnotation code to the integer feature
		:return: the sequence of integer features
		"""
		annotations = set()
		for annotation in execution.get_annotations():
			for child_annotation in annotation.get_all_children():
				child_annotation: jctest.CirAnnotation
				annotations.add(child_annotation)
			annotations.add(annotation)
		features = set()
		for annotation in annotations:
			aid = index[str(annotation)]
			aid: int
			features.add(aid)
		return features

	@staticmethod
	def __encode_exc_file__(document: jctest.CDocument, directory: str, file_name: str, index: dict):
		"""
		:param document:
		:param directory:
		:param file_name:
		:param index:
		:return:
		"""
		file_path = os.path.join(directory, file_name + ".exc")
		with open(file_path, 'w') as writer:
			for execution in document.exec_space.get_executions():
				execution: jctest.SymExecution
				mid = execution.get_mutant().get_muta_id()
				tid = -1
				if execution.has_test():
					tid = execution.get_test().get_test_id()
				features = C2MDocumentParse.__encode_features__(execution, index)
				writer.write("{}\t{}".format(mid, tid))
				for feature in features:
					writer.write("\t{}".format(feature))
				writer.write("\n")
		return

	@staticmethod
	def encode(document: jctest.CDocument, directory: str):
		"""
		:param document:
		:param directory:
		:return: it encodes the document of original data source to generate encoding features
		"""
		file_name = document.get_program().name
		C2MDocumentParse.__encode_mut_file__(document, directory, file_name)
		C2MDocumentParse.__encode_tst_file__(document, directory, file_name)
		index = C2MDocumentParse.__encode_ant_file__(document, directory, file_name)
		C2MDocumentParse.__encode_exc_file__(document, directory, file_name, index)
		return


def encode_c_documents(prev_path: str, post_path: str):
	"""
		:param prev_path: 	the directory where the C-document information is saved
		:param post_path: 	the directory where the M-document will be generated
		:return:
		"""
	for file_name in os.listdir(prev_path):
		inputs_directory = os.path.join(prev_path, file_name)
		encode_directory = os.path.join(post_path, file_name)
		if not os.path.exists(encode_directory):
			os.mkdir(encode_directory)
		document = jctest.CDocument(inputs_directory, file_name)
		C2MDocumentParse.encode(document, encode_directory)
		print("Encode project for", file_name)
	print()
	return


def decode_m_documents(post_path: str):
	"""
	:param post_path:
	:return:
	"""
	for file_name in os.listdir(post_path):
		encode_directory = os.path.join(post_path, file_name)
		m_document = MerDocument(encode_directory, file_name)
		print("Load", file_name, "using:\t",
			  len(m_document.test_space.get_test_cases()), "test cases;",
			  len(m_document.muta_space.get_mutants()), "mutants;",
			  len(m_document.exec_space.get_executions()), "executions;",
			  len(m_document.anot_space.get_annotations()), "annotations.")
		random_tests = m_document.test_space.select_random_tests(128)
		s_matrix, tests = m_document.exec_space.new_semantic_matrix(random_tests)
		parser = manifold.TSNE()
		s2_matrix = parser.fit_transform(s_matrix)
		plt.scatter(s2_matrix[:, 0], s2_matrix[:, 1])
		plt.title(file_name)
		plt.show()
	print()
	return


def main(prev_path: str, post_path: str):
	encode_c_documents(prev_path, post_path)
	decode_m_documents(post_path)
	return 0


if __name__ == "__main__":
	prev_directory = "/home/dzt2/Development/Data/zexp/features"
	post_directory = "/home/dzt2/Development/Data/zexp/encoding"
	exit_code = main(prev_directory, post_directory)
	exit(exit_code)


