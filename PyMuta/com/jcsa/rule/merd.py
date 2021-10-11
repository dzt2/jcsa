"""This file defines the data model of memory-reduced features in CDocument and mutation testing project."""


import os
import scipy.sparse as sparse
import com.jcsa.libs.muta as jcmuta
import com.jcsa.libs.test as jctest


class MerDocument:
	"""
	It defines the memory-reduced (encoded) features in the mutation testing project.
	"""

	def __init__(self, directory: str, file_name: str):
		"""
		:param directory: the directory where the encoded feature files are generated
		:param file_name: xxx.{mut, tst, res, ant, exc}
		"""
		mut_file_path = os.path.join(directory, file_name + ".mut")
		tst_file_path = os.path.join(directory, file_name + ".tst")
		ant_file_path = os.path.join(directory, file_name + ".ant")
		exc_file_path = os.path.join(directory, file_name + ".exc")
		self.test_space = MerTestCaseSpace(self, tst_file_path)
		self.muta_space = MerMutantSpace(self, mut_file_path)
		self.anot_space = MerAnnotationSpace(self, ant_file_path)
		self.exec_space = MerExecutionSpace(self, exc_file_path)
		return

	@staticmethod
	def __encode_tst__(document: jctest.CDocument, directory: str, file_name: str):
		"""
		:param document:
		:param directory:
		:param file_name:
		:return: xxx.tst
		"""
		file_path = os.path.join(directory, file_name + ".tst")
		with open(file_path, 'w') as writer:
			for test_case in document.get_project().test_space.get_test_cases():
				test_case: jcmuta.TestCase
				writer.write("{}\n".format(test_case.get_test_id()))
		return

	@staticmethod
	def __encode_mut__(document: jctest.CDocument, directory: str, file_name: str):
		"""
		:param document:
		:param directory:
		:param file_name:
		:return: mid cid wid sid result
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
	def __encode_ant__(document: jctest.CDocument, directory: str, file_name: str):
		"""
		:param document:
		:param directory:
		:param file_name:
		:return:
		"""
		words = document.annotation_tree.get_words()
		index = dict()	# CirAnnotation --> int
		file_path = os.path.join(directory, file_name + ".ant")
		with open(file_path, 'w') as writer:
			for word in words:
				word: str
				annotation = document.annotation_tree.get_annotation(word)
				aid = len(index)
				index[annotation] = aid
				writer.write("{}\n".format(word))
		return index

	@staticmethod
	def __encode_exe__(execution: jctest.SymExecution, index: dict, extend: bool):
		"""
		:param execution:
		:param index: 		a mapping from CirAnnotation to integer features
		:param extend: 		whether to encode all the correlated features in
		:return: 			{aid as sorted}+
		"""
		features = set()
		for annotation in execution.get_annotations():
			if extend:
				for child in annotation.get_all_children():
					aid = index[child]
					aid: int
					features.add(aid)
			aid = index[annotation]
			aid: int
			features.add(aid)
		feature_list = list()
		for feature in features:
			feature_list.append(feature)
		feature_list.sort()
		return feature_list

	@staticmethod
	def __encode_exc__(document: jctest.CDocument, directory: str, file_name: str, index: dict, extend: bool):
		"""
		:param document:
		:param directory:
		:param file_name:
		:param index:
		:param extend:
		:return:
		"""
		file_path = os.path.join(directory, file_name + ".exc")
		with open(file_path, 'w') as writer:
			for execution in document.exec_space.get_executions():
				execution: jctest.SymExecution
				feature_list = MerDocument.__encode_exe__(execution, index, extend)
				mid = execution.get_mutant().get_muta_id()
				tid = -1
				if not (execution.get_test() is None):
					tid = execution.get_test().get_test_id()
				writer.write("{}\t{}".format(mid, tid))
				for feature in feature_list:
					writer.write("\t{}".format(feature))
				writer.write("\n")
		return

	@staticmethod
	def encode_c_document(document: jctest.CDocument, directory: str, extend: bool):
		"""
		:param document: 	the document of original data source
		:param directory: 	the directory where the encoded features are printed
		:param extend:
		:return:
		"""
		file_name = document.get_program().name
		MerDocument.__encode_tst__(document, directory, file_name)
		MerDocument.__encode_mut__(document, directory, file_name)
		index = MerDocument.__encode_ant__(document, directory, file_name)
		MerDocument.__encode_exc__(document, directory, file_name, index, extend)
		return


class MerTestCase:
	"""
	The memory-reduced object of TestCase as integer simply
	"""

	def __init__(self, space, tid: int):
		space: MerTestCaseSpace
		self.space = space
		self.tid = tid
		return

	def get_space(self):
		"""
		:return: the space where the test case is defined
		"""
		return self.space

	def get_tid(self):
		"""
		:return: the unique integer ID of the TestCase it refers to
		"""
		return self.tid

	def __str__(self):
		return "tst@" + str(self.tid)

	def find_source(self, document: jctest.CDocument):
		"""
		:param document: the original source of data document
		:return: TestCase that the memory-reduced instance refers to
		"""
		return document.get_project().test_space.get_test_case(self.tid)


class MerTestCaseSpace:
	"""
	The set of memory-reduced test cases to encode the TestCase instances in CDocument
	"""

	def __init__(self, document: MerDocument, file_path: str):
		"""
		:param document: the document where the test case space is created
		:param file_path: xxx.tst
		"""
		self.document = document
		self.test_cases = list()
		self.__parse__(file_path)
		return

	def get_document(self):
		"""
		:return: the document where the test case space is created
		"""
		return self.document

	def get_test_cases(self):
		"""
		:return: the set of test cases incorporated in the space
		"""
		return self.test_cases

	def get_test_case(self, tid: int):
		"""
		:param tid:
		:return: the memory-reduced instance referring to the test case of specified integer ID
		"""
		test_case = self.test_cases[tid]
		test_case: MerTestCase
		return test_case

	def __len__(self):
		"""
		:return: the number of test cases incorporated in the space
		"""
		return len(self.test_cases)

	def __parse__(self, file_path: str):
		"""
		:param file_path: xxx.tst
		:return:
		"""
		max_tid = 0
		with open(file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					tid = int(line)
					if tid > max_tid:
						max_tid = tid
		self.test_cases.clear()
		for tid in range(0, max_tid + 1):
			self.test_cases.append(MerTestCase(self, tid))
		return


class MerMutant:
	"""
	It encodes the memory-reduced mutant in CDocument.
	"""

	def __init__(self, space, mid: int):
		"""
		:param space: the space where the encoded mutant instance is defined
		:param mid: the unique integer ID referring to the Mutant in project
		"""
		space: MerMutantSpace
		self.space = space
		self.mid = mid
		self.c_mutant = None
		self.w_mutant = None
		self.s_mutant = None
		self.result = ""
		return

	def get_space(self):
		"""
		:return: the space where the encoding element is defined
		"""
		return self.space

	def get_mid(self):
		"""
		:return: the integer ID of the mutant in the space
		"""
		return self.mid

	def get_c_mutant(self):
		"""
		:return: the coverage version of mutant
		"""
		self.c_mutant: MerMutant
		return self.c_mutant

	def get_w_mutant(self):
		"""
		:return: the weak version of the mutant
		"""
		self.w_mutant: MerMutant
		return self.w_mutant

	def get_s_mutant(self):
		"""
		:return: the strong version of the mutant
		"""
		self.s_mutant: MerMutant
		return self.s_mutant

	def get_result(self):
		"""
		:return: the bit-strong of the mutation test results on every test case
		"""
		self.result: str
		return self.result

	def __str__(self):
		return "mut@" + str(self.mid)

	def is_killed_by(self, test):
		"""
		:param test: either MerTestCase, or integer of tid
		:return: True -- killed; False -- alive.
		"""
		if isinstance(test, MerTestCase):
			tid = test.get_tid()
		else:
			test: int
			tid = test
		if tid < 0 or tid >= len(self.result):
			return False
		return self.result[tid] == '1'

	def is_killed_in(self, tests):
		"""
		:param tests: the set of MerTestCase or int of tid or None to represent all the test cases in space
		:return:
		"""
		if tests is None:
			return '1' in self.result
		else:
			for test in tests:
				if self.is_killed_by(test):
					return True
			return False

	def find_source(self, document: jctest.CDocument):
		"""
		:param document: the original document of data source
		:return: the original mutant instance that the object represents
		"""
		return document.get_project().muta_space.get_mutant(self.mid)


class MerMutantSpace:
	"""
	The space of memory-reduced mutants that encode the original mutations in the project
	"""

	def __init__(self, document: MerDocument, mut_file_path: str):
		"""
		:param document: 		the document where the mutation space is created
		:param mut_file_path: 	xxx.mut
		"""
		self.document = document
		self.mutants = list()
		self.__load__(mut_file_path)
		self.__link__(mut_file_path)
		return

	def __load__(self, mut_file_path: str):
		"""
		:param mut_file_path: xxx.mut
		:return: it generates the mutants for being generated
		"""
		mutant_dict = dict()
		with open(mut_file_path, 'r') as reader:
			for line in reader:
				if len(line.strip()) > 0:
					items = line.strip().split('\t')
					mid = int(items[0].strip())
					mutant_dict[mid] = MerMutant(self, mid)
		self.mutants.clear()
		for mid in range(0, len(mutant_dict)):
			mutant = mutant_dict[mid]
			self.mutants.append(mutant)
		return

	def __link__(self, mut_file_path: str):
		"""
		:param mut_file_path: xxx.mut
		:return: it connects the mutant to its coverage, weak and strong version
		"""
		with open(mut_file_path, 'r') as reader:
			for line in reader:
				if len(line.strip()) > 0:
					items = line.strip().split('\t')
					mid = int(items[0].strip())
					cid = int(items[1].strip())
					wid = int(items[2].strip())
					sid = int(items[3].strip())
					mutant = self.get_mutant(mid)
					mutant.c_mutant = self.get_mutant(cid)
					mutant.w_mutant = self.get_mutant(wid)
					mutant.s_mutant = self.get_mutant(sid)
					if len(items) > 4:
						mutant.result = items[4].strip()
		return

	def get_document(self):
		"""
		:return: the document where the mutation space is created
		"""
		return self.document

	def get_mutants(self):
		"""
		:return: the set of mutants created in this document
		"""
		return self.mutants

	def get_mutant(self, mid: int):
		"""
		:param mid: the unique integer ID of the mutant in
		:return: the memory-reduced mutant referring to the mid
		"""
		mutant = self.mutants[mid]
		mutant: MerMutant
		return mutant

	def __len__(self):
		"""
		:return: the number of mutants in the space
		"""
		return len(self.mutants)


class MerAnnotation:
	"""
	It models the memory-reduced annotation to encode CirAnnotation.
	"""

	def __init__(self, space, aid: int, key: str):
		"""
		:param space: 	the space for MerAnnotation where it is defined
		:param aid: 	the unique integer ID of the annotation feature
		:param key: 	the string key of the code of the MerAnnotation
		"""
		space: MerAnnotationSpace
		self.space = space
		self.aid = aid
		self.key = key
		return

	def get_space(self):
		"""
		:return: the space for MerAnnotation where it is defined
		"""
		return self.space

	def get_aid(self):
		"""
		:return: the unique integer ID of the annotation feature
		"""
		return self.aid

	def get_key(self):
		"""
		:return: the string key of the code of the MerAnnotation
		"""
		return self.key

	def __str__(self):
		return self.key

	def find_source(self, document: jctest.CDocument):
		"""
		:param document: the original data source of CDocument
		:return: the original annotation this instance refers to
		"""
		return document.annotation_tree.get_annotation(self.key)


class MerAnnotationSpace:
	"""
	The space of the annotation to encode CirAnnotation in document.
	"""

	def __init__(self, document: MerDocument, ant_file_path: str):
		"""
		:param document: 		the document where the annotations space is created
		:param ant_file_path: 	xxx.ant
		"""
		self.document = document
		self.a_list = list()	# aid --> MerAnnotation
		self.a_dict = dict()	# key --> MerAnnotation
		self.__parse__(ant_file_path)
		return

	def __parse__(self, ant_file_path: str):
		"""
		:param ant_file_path: xxx.ant
		:return:
		"""
		with open(ant_file_path, 'r') as reader:
			for line in reader:
				if len(line.strip()) > 0:
					key = line.strip()
					aid = len(self.a_list)
					annotation = MerAnnotation(self, aid, key)
					self.a_list.append(annotation)
					self.a_dict[key] = annotation
		return

	def get_document(self):
		"""
		:return: the document where the annotations space is created
		"""
		return self.document

	def get_annotations(self):
		"""
		:return: the list of MerAnnotation(s) defined in the space
		"""
		return self.a_list

	def get_annotation(self, aid: int):
		"""
		:param aid: the integer feature of the annotation being encoded
		:return:
		"""
		annotation = self.a_list[aid]
		annotation: MerAnnotation
		return annotation

	def get_annotation_of(self, key: str):
		"""
		:param key: the unique string code of the annotation
		:return: the annotation referring to the given code
		"""
		annotation = self.a_dict[key]
		annotation: MerAnnotation
		return annotation

	def __len__(self):
		return len(self.a_list)

	def normal(self, features):
		"""
		:param features: the set of Integer encoding the CirAnnotation in the space
		:return: the sorted sequence of unique integer IDs for encoding annotations
		"""
		feature_list = list()
		for feature in features:
			feature: int
			if (feature >= 0) and (feature < len(self.a_list)):
				if not (feature in feature_list):
					feature_list.append(feature)
		feature_list.sort()
		return feature_list

	def encode(self, annotations):
		"""
		:param annotations: the set of MerAnnotation being encoded as integer features
		:return: the unique sequence of integers encoding the MerAnnotation from space
		"""
		features = set()
		for annotation in annotations:
			annotation: MerAnnotation
			features.add(annotation.get_aid())
		return self.normal(features)

	def decode(self, features):
		"""
		:param features: the set of integers encoding the MerAnnotation defined in this space
		:return: the set of MerAnnotation(s) encoded by the input integer features as well
		"""
		annotations = set()
		for feature in features:
			feature: int
			if (feature >= 0) and (feature < len(self.a_list)):
				annotation = self.a_list[feature]
				annotation: MerAnnotation
				annotations.add(annotation)
		return annotations


class MerExecution:
	"""
	It models the data element for encoding the SymExecution in the document
	"""

	def __init__(self, space, eid: int, mutant: MerMutant, test_case: MerTestCase, features):
		"""
		:param space: 		the space where the data element is defined
		:param eid: 		the integer ID to denote this element in the space
		:param mutant: 		the mutation that is executed against test in this execution
		:param test_case: 	the test case that is executed against mutant in this execution or None if it is static
		:param features:	the set of integer features encoding the CirAnnotation(s) incorporated in the execution
		"""
		space: MerExecutionSpace
		self.space = space
		self.eid = eid
		self.mutant = mutant
		self.test_case = test_case
		self.features = space.get_document().anot_space.normal(features)
		return

	def get_space(self):
		"""
		:return: the space where the data element is defined
		"""
		return self.space

	def get_eid(self):
		"""
		:return: the integer ID to denote this element in the space
		"""
		return self.eid

	def get_mutant(self):
		"""
		:return: the mutation that is executed against test in this execution
		"""
		return self.mutant

	def get_test_case(self):
		"""
		:return: the test case that is executed against mutant in this execution or None if it is static
		"""
		return self.test_case

	def has_test_case(self):
		"""
		:return: whether the execution is generated from some concrete testing against a test case
		"""
		if self.test_case is None:
			return False
		return True

	def get_features(self):
		"""
		:return: the set of integer features encoding the annotations incorporated in the execution
		"""
		return self.features

	def get_annotations(self):
		"""
		:return: the set of annotations being incorporated in execution encoded by this one
		"""
		return self.space.get_document().anot_space.decode(self.features)

	def find_source(self, document: jctest.CDocument):
		"""
		:param document:
		:return: the original mutant execution that the element refers to
		"""
		return document.exec_space.get_execution(self.eid)

	def __str__(self):
		return "exe@" + str(self.eid)


class MerExecutionSpace:
	"""
	It denotes the space of data elements encoding the symbolic execution in mutation testing.
	"""

	def __init__(self, document: MerDocument, exe_file_path: str):
		"""
		:param document: 		the document where the execution space is created
		:param exe_file_path: 	the xxx.exc to preserve the mutation execution set
		"""
		self.document = document
		self.exec_list = list()
		self.muta_exec = dict()
		self.__load__(exe_file_path)
		self.__link__()
		return

	def __load__(self, exe_file_path: str):
		"""
		:param exe_file_path: mid tid {feature of annotation used in}+
		:return: it loads the symbolic executions of mutants from xxx.exc
		"""
		self.exec_list.clear()
		with open(exe_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.strip().split('\t')
					mid = int(items[0].strip())
					tid = int(items[1].strip())
					mutant = self.document.muta_space.get_mutant(mid)
					if tid < 0:
						test_case = None
					else:
						test_case = self.document.test_space.get_test_case(tid)
					features = set()
					for k in range(2, len(items)):
						word = items[k].strip()
						if len(word) > 0:
							features.add(int(word))
					execution = MerExecution(self, len(self.exec_list), mutant, test_case, features)
					self.exec_list.append(execution)
		return

	def __link__(self):
		"""
		:return: it links the mutant to the executions it is executed against
		"""
		self.muta_exec.clear()
		for execution in self.exec_list:
			execution: MerExecution
			mutant = execution.get_mutant()
			if not (mutant in self.muta_exec):
				self.muta_exec[mutant] = set()
			self.muta_exec[mutant].add(execution)
		return

	def get_document(self):
		"""
		:return: the document where the execution space is created
		"""
		return self.document

	def __len__(self):
		"""
		:return: the number of mutant executions defined in this document.
		"""
		return len(self.exec_list)

	def get_executions(self):
		"""
		:return: the set of data elements encoding the SymExecution in original dataset.
		"""
		return self.exec_list

	def get_execution(self, eid: int):
		"""
		:param eid: the integer ID encoding the SymExecution in document
		:return: the data element encoding the SymExecution w.r.t. the given ID
		"""
		execution = self.exec_list[eid]
		execution: MerExecution
		return execution

	def get_mutants(self):
		"""
		:return: the set of mutants of which executions are encoded in this space
		"""
		return self.muta_exec.keys()

	def get_executions_of(self, mutant: MerMutant):
		"""
		:param mutant:
		:return: the set of data elements encoding executions of the target mutant
		"""
		if mutant in self.muta_exec:
			return self.muta_exec[mutant]
		else:
			return set()

	def new_feature_matrix(self):
		"""
		:return: sparse matrix representing the annotations incorporated by each execution in the space
		"""
		lines, columns = len(self.exec_list), len(self.document.anot_space.get_annotations())
		xmatrix = sparse.lil_matrix((lines, columns))
		for execution in self.exec_list:
			execution: MerExecution
			for feature in execution.get_features():
				xmatrix[execution.get_eid(), feature] = 1
		return xmatrix

	def new_label_list(self, used_tests):
		"""
		:param used_tests: the set of test cases to decode of which executions are killed
		:return:
		"""
		labels = list()
		for execution in self.exec_list:
			execution: MerExecution
			if execution.get_mutant().is_killed_in(used_tests):
				labels.append(1)
			else:
				labels.append(0)
		return labels


def encode_c_documents(prev_path: str, post_path: str, extend: bool):
	"""
	:param prev_path: 	the directory where the C-document information is saved
	:param post_path: 	the directory where the M-document will be generated
	:param extend: 		whether to use the extension annotation set.
	:return:
	"""
	for file_name in os.listdir(prev_path):
		inputs_directory = os.path.join(prev_path, file_name)
		encode_directory = os.path.join(post_path, file_name)
		if not os.path.exists(encode_directory):
			os.mkdir(encode_directory)
		document = jctest.CDocument(inputs_directory, file_name)
		MerDocument.encode_c_document(document, encode_directory, extend)
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
		print(m_document.exec_space.new_feature_matrix().toarray())
		print()
	print()
	return


def main(prev_path: str, post_path: str, extend: bool):
	encode_c_documents(prev_path, post_path, extend)
	decode_m_documents(post_path)
	return 0


if __name__ == "__main__":
	prev_directory = "/home/dzt2/Development/Data/zexp/features"
	post_directory = "/home/dzt2/Development/Data/zexp/encoding"
	exit_code = main(prev_directory, post_directory, True)
	exit(exit_code)

