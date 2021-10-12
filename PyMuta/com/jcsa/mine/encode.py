"""This file defines the memory-reduced model for encoding features from mutation testing project."""


import os
import scipy.sparse as sparse
import com.jcsa.libs.muta as jcmuta
import com.jcsa.libs.test as jctest


## memory-reduced data model


class MerDocument:
	"""
	It models the memory-reduced (Mer) features to encode information incorporated in a CDocument.
	"""

	def __init__(self, directory: str, file_name: str):
		"""
		:param directory: the directory where the encoded features are preserved
		:param file_name: the name of the program file to specify features files
		"""
		## 1. generate the paths of encoding feature files
		mut_file_path = os.path.join(directory, file_name + ".mut")
		tst_file_path = os.path.join(directory, file_name + ".tst")
		ant_file_path = os.path.join(directory, file_name + ".ant")
		exc_file_path = os.path.join(directory, file_name + ".exc")

		## 2. construct the spaces created to encode features
		self.file_name = file_name
		self.test_space = MerTestCaseSpace(self, tst_file_path)
		self.muta_space = MerMutantSpace(self, mut_file_path)
		self.anot_space = MerAnnotationSpace(self, ant_file_path)
		self.exec_space = MerExecutionSpace(self, exc_file_path)
		return


class MerTestCase:
	"""
	It models a memory-reduced instance to encode the TestCase object specified in CProject.
	"""

	def __init__(self, space, tid: int):
		"""
		:param space: 	the space where the memory-reduced encoding instance is created
		:param tid: 	the unique ID of the test referring to the TestCase in CProject
		"""
		space: MerTestCaseSpace
		self.space = space
		self.tid = tid
		return

	def get_space(self):
		"""
		:return: the space where the memory-reduced encoding instance is created
		"""
		return self.space

	def get_tid(self):
		"""
		:return: the unique ID of the test referring to the TestCase in CProject
		"""
		return self.tid

	def __str__(self):
		return "tst@{}".format(self.tid)

	def find_source(self, document: jctest.CDocument):
		"""
		:param document: the document of original data instance
		:return: TestCase that the memory-reduced instance will encode
		"""
		return document.get_project().test_space.get_test_case(self.tid)


class MerTestCaseSpace:
	"""
	The space of the memory-reduced instances for encoding TestCase in CProject.
	"""

	def __init__(self, document: MerDocument, file_path: str):
		"""
		:param document: 	the document where the space is created
		:param file_path: 	directory/file_name.tst	: {tid\n}+
		"""
		self.document = document
		self.__parse__(file_path)
		return

	def __parse__(self, file_path: str):
		"""
		:param file_path: directory/file_name.tst	: {tid\n}+
		:return:
		"""
		max_tid = 0
		with open(file_path, 'r') as reader:
			for line in reader:
				if len(line.strip()) > 0:
					tid = int(line.strip())
					if tid >= max_tid:
						max_tid = tid + 1
		self.test_cases = list()
		for tid in range(0, max_tid):
			self.test_cases.append(MerTestCase(self, tid))
		return

	def get_document(self):
		"""
		:return: the document where this space is created
		"""
		return self.document

	def get_test_cases(self):
		"""
		:return: the sequence of memory-reduced instances for encoding TestCase in CProject.
		"""
		return self.test_cases

	def __len__(self):
		"""
		:return: the number of test cases incorporated in this space
		"""
		return len(self.test_cases)

	def get_test_case(self, tid: int):
		"""
		:param tid: the unique ID of TestCase encoded by outcome instances
		:return: the TestCase that the memory reduced instance is encoding
		"""
		return self.test_cases[tid]


class MerMutant:
	"""
	It models a memory-reduced instance for encoding Mutant in CProject.
	"""

	def __init__(self, space, mid: int):
		"""
		:param space: 	the space where this memory-reduced instance is created
		:param mid: 	the unique ID of Mutant that is encoded by the instance
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
		:return: the space where this memory-reduced instance is created
		"""
		return self.space

	def get_mid(self):
		"""
		:return: the unique ID of Mutant that is encoded by the instance
		"""
		return self.mid

	def get_c_mutant(self):
		"""
		:return: the coverage version of the mutation
		"""
		self.c_mutant: MerMutant
		return self.c_mutant

	def get_w_mutant(self):
		"""
		:return: the weak version of the mutation
		"""
		self.w_mutant: MerMutant
		return self.w_mutant

	def get_s_mutant(self):
		"""
		:return: the strong version of the mutation
		"""
		self.s_mutant: MerMutant
		return self.s_mutant

	def get_result(self):
		"""
		:return: the string of 0-1 bits to denote which tests kill the mutant
		"""
		return self.result

	def __str__(self):
		return "mut@{}".format(self.mid)

	def is_killed_by(self, test):
		"""
		:param test: either MerTestCase or int
		:return: True if the mutant is killed by the input test or False otherwise.
		"""
		if isinstance(test, MerTestCase):
			tid = test.get_tid()
		else:
			test: int
			tid = test
		if (tid < 0) or (tid >= len(self.result)):
			return False
		return self.result[tid] == '1'

	def is_killed_in(self, tests):
		"""
		:param tests: the set of MerTestCase or int or None for all
		:return: True if the mutant is killed by any test specified in tests.
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
		:param document: the document of original data source
		:return: the Mutant that the memory-reduced instance encodes
		"""
		return document.get_project().muta_space.get_mutant(self.mid)


class MerMutantSpace:
	"""
	The space of memory-reduced instances for encoding Mutant in CProject.
	"""

	def __init__(self, document: MerDocument, file_path: str):
		"""
		:param document:  the document where this space is instantiated
		:param file_path: directory/file_name.mut: {mid cid wid sid result\n}+
		"""
		self.document = document
		self.__load__(file_path)
		self.__link__(file_path)
		return

	def __load__(self, file_path: str):
		"""
		:param file_path: directory/file_name.mut: {mid cid wid sid result\n}+
		:return:
		"""
		max_mid = 0
		with open(file_path, 'r') as reader:
			for line in reader:
				if len(line.strip()) > 0:
					items = line.strip().split('\t')
					mid = int(items[0].strip())
					if mid >= max_mid:
						max_mid = mid + 1
		self.mutants = list()
		for mid in range(0, max_mid):
			self.mutants.append(MerMutant(self, mid))
		return

	def __link__(self, file_path: str):
		"""
		:param file_path: directory/file_name.mut: {mid cid wid sid result\n}+
		:return:
		"""
		with open(file_path, 'r') as reader:
			for line in reader:
				if len(line.strip()) > 0:
					items = line.strip().split('\t')
					mid = int(items[0].strip())
					cid = int(items[1].strip())
					wid = int(items[2].strip())
					sid = int(items[3].strip())
					result = ""
					if len(items) > 4:
						result = items[4].strip()
					mutant = self.mutants[mid]
					mutant.c_mutant = self.mutants[cid]
					mutant.w_mutant = self.mutants[wid]
					mutant.s_mutant = self.mutants[sid]
					mutant.result = result
		return

	def get_document(self):
		"""
		:return: the document where this space is instantiated
		"""
		return self.document

	def get_mutants(self):
		"""
		:return: the set of memory-reduced instances for encoding Mutant in CProject.
		"""
		return self.mutants

	def get_mutant(self, mid: int):
		"""
		:param mid: the unique ID of Mutant that the output instance encodes
		:return:    the memory-reduced instance to encode Mutant with inputs mid
		"""
		return self.mutants[mid]

	def __len__(self):
		"""
		:return: the number of mutants created in the space
		"""
		return len(self.mutants)


class MerAnnotation:
	"""
	It denotes a memory-reduced instance to encode CirAnnotation in CProject.
	"""

	def __init__(self, space, aid: int, key: str):
		"""
		:param space: the space where the instance is created
		:param aid:   the integer feature encoding the annotation
		:param key:   the unique string key of the cir annotation
		"""
		space: MerAnnotationSpace
		self.space = space
		self.aid = aid
		self.key = key
		return

	def get_space(self):
		"""
		:return: the space where the instance is created
		"""
		return self.space

	def get_aid(self):
		"""
		:return: the integer feature to encode the unique CirAnnotation
		"""
		return self.aid

	def get_key(self):
		"""
		:return: the unique String key for CirAnnotation being encoded
		"""
		return self.key

	def __str__(self):
		return self.key

	def find_source(self, document: jctest.CDocument):
		"""
		:param document: the document of original data source
		:return: the CirAnnotation that this instance is encoding
		"""
		return document.annotation_tree.get_annotation(self.key)


class MerAnnotationSpace:
	"""
	The space of memory-reduced instances for encoding CirAnnotation in CProject.
	"""

	def __init__(self, document: MerDocument, file_path: str):
		"""
		:param document: the document where the space is created
		:param file_path: directory/file_name.ant: {logic_type$execution$store_unit$symb_value\n}+
		"""
		self.document = document
		self.__parse__(file_path)
		return

	def __parse__(self, file_path: str):
		"""
		:param file_path: directory/file_name.ant: {logic_type$execution$store_unit$symb_value\n}+
		:return:
		"""
		self.alist = list()	# aid --> MerAnnotation
		self.index = dict()	# key --> MerAnnotation
		with open(file_path, 'r') as reader:
			for line in reader:
				if len(line.strip()) > 0:
					aid = len(self.alist)
					key = line.strip()
					annotation = MerAnnotation(self, aid, key)
					self.alist.append(annotation)
					self.index[key] = annotation
		return

	def get_document(self):
		"""
		:return: the document in which the space is created
		"""
		return self.document

	def get_words(self):
		"""
		:return: the set of unique string keys for describing CirAnnotation
		"""
		return self.index.keys()

	def get_annotations(self):
		"""
		:return: the list of memory-reduced instances for encoding CirAnnotation
		"""
		return self.alist

	def __len__(self):
		"""
		:return: the number of annotations encoded by instances created in this space.
		"""
		return len(self.alist)

	def get_annotation(self, aid: int):
		return self.alist[aid]

	def get_annotation_of(self, key: str):
		return self.index[key]

	def normal(self, features):
		"""
		:param features: the set of integer features to encode CirAnnotation in the space
		:return: the unique sorted list of integer features to encode CirAnnotation
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
		:param annotations: the set of memory-reduced instances for encoding CirAnnotation (MerAnnotation)
		:return: the unique list of integer features to encode the annotations represented by the inputs
		"""
		features = set()
		for annotation in annotations:
			annotation: MerAnnotation
			features.add(annotation.get_aid())
		return self.normal(features)

	def decode(self, features):
		"""
		:param features: the set of integer features to encode the CirAnnotation
		:return: the memory-reduced instances for encoding CirAnnotation specified by the input features
		"""
		feature_list = self.normal(features)
		annotations = set()
		for feature in feature_list:
			annotations.add(self.alist[feature])
		return annotations


class MerExecution:
	"""
	It models the memory-reduced instance for encoding SymExecution in CProject.
	"""

	def __init__(self, space, eid: int, mutant: MerMutant, test_case: MerTestCase, features):
		"""
		:param space:
		:param eid:
		:param mutant:
		:param test_case:
		:param features:
		"""
		space: MerExecutionSpace
		self.space = space
		self.eid = eid
		self.mutant = mutant
		self.test_case = test_case
		document = space.get_document()
		document: MerDocument
		self.features = document.anot_space.normal(features)
		return

	def get_space(self):
		"""
		:return: the space where this instance is created
		"""
		return self.space

	def get_eid(self):
		"""
		:return: the integer ID of the execution defined in the space context
		"""
		return self.eid

	def get_mutant(self):
		"""
		:return: the mutant for being killed by this execution
		"""
		return self.mutant

	def has_test_case(self):
		"""
		:return: True if the execution is generated from a concrete test case
		"""
		return not (self.test_case is None)

	def get_test_case(self):
		"""
		:return: the test case for running the execution or None for static execution
		"""
		return self.test_case

	def get_features(self):
		"""
		:return: the set of integer features for encoding CirAnnotation incorporated in the execution
		"""
		return self.features

	def get_annotations(self):
		"""
		:return: the set of memory-reduced instances for encoding CirAnnotation included in the execution it refers to
		"""
		document = self.space.get_document()
		return document.anot_space.decode(self.features)

	def __str__(self):
		return "exe@{}".format(self.eid)

	def find_source(self, document: jctest.CDocument):
		"""
		:param document: the document of original data source
		:return: the CirAnnotation this instance is encoding
		"""
		execution = document.exec_space.get_execution(self.eid)
		execution: jctest.SymExecution
		return execution


class MerExecutionSpace:
	"""
	The space of memory-reduced instances for encoding SymExecution in Cproject.
	"""

	def __init__(self, document: MerDocument, file_path: str):
		"""
		:param document: 	the document where this space is created
		:param file_path: 	directory/file_name.exc: {mid tid feature+\n}+
		"""
		self.document = document
		self.__parse__(file_path)
		return

	def __read__(self, line: str, eid: int):
		"""
		:param line: mid tid feature+
		:param eid: the unique ID of the generated execution
		:return: None if the line is empty
		"""
		if len(line) > 0:
			items = line.split('\t')
			mutant = self.document.muta_space.get_mutant(int(items[0].strip()))
			tid = int(items[1].strip())
			if tid < 0:
				test_case = None
			else:
				test_case = self.document.test_space.get_test_case(tid)
			features = set()
			for k in range(2, len(items)):
				features.add(int(items[k].strip()))
			return MerExecution(self, eid, mutant, test_case, features)
		return None

	def __parse__(self, file_path: str):
		"""
		:param file_path: directory/file_name.exc: {mid tid feature+\n}+
		:return:
		"""
		self.elist = list()	# eid --> MerExecution
		self.index = dict()	# MerMutant --> MerExecution+
		with open(file_path, 'r') as reader:
			for line in reader:
				execution = self.__read__(line.strip(), len(self.elist))
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
		:return: the number of instances for encoding SymExecution
		"""
		return len(self.elist)

	def get_executions(self):
		"""
		:return: the list of memory-reduced instances for encoding SymExecution in CProject.
		"""
		return self.elist

	def get_execution(self, eid: int):
		"""
		:param eid: the unique ID of the instance referring to SymExecution in CProject
		:return: the memory-reduced instance for encoding SymExecution it represents on
		"""
		return self.elist[eid]

	def get_executions_of(self, mutant: MerMutant):
		"""
		:param mutant:
		:return: the set of instances encoding the SymExecution referred from the input mutant
		"""
		if mutant in self.index:
			return self.index[mutant]
		return list()

	def get_mutants(self):
		"""
		:return: the set of memory-reduced instances for encoding mutants defined in the execution space.
		"""
		return self.index.keys()

	def new_features_labels(self, used_tests):
		"""
		:param used_tests: the set of tests to decide of which mutants are killed
		:return: feature_matrix, label_list
		"""
		lines, columns = len(self.elist), len(self.document.anot_space.get_annotations())
		x_matrix = sparse.lil_matrix((lines, columns))
		for execution in self.elist:
			for feature in execution.get_features():
				x_matrix[execution.get_eid(), feature] = 1
		y_labels = list()
		for execution in self.elist:
			if execution.get_mutant().is_killed_in(used_tests):
				y_labels.append(1)
			else:
				y_labels.append(0)
		return x_matrix, y_labels


## encoding-decoding method


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
		x_matrix, y_labels = m_document.exec_space.new_features_labels(None)
		print(x_matrix.toarray())
		print()
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

