""" This file defines the memory-reduced data model for encoding instances in CProgram, CProject and CDocument. """


import os
import scipy.sparse as sparse
import com.jcsa.libs.muta as jcmuta
import com.jcsa.libs.test as jctest


## memory-reduced data model


class MerDocument:
	"""
	It models a document to incorporate memory-reduced instances for encoding objects in CProgram, CProject, CDocument.
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
	It denotes the memory-reduced instance for encoding TestCase object in CProject.
	"""

	def __init__(self, space, tid: int):
		"""
		:param space: the space where this instance is created
		:param tid: the unique ID referring to the TestCase that it refers
		"""
		space: MerTestCaseSpace
		self.space = space
		self.tid = tid
		return

	def get_space(self):
		"""
		:return: the space where this instance is created
		"""
		return self.space

	def get_tid(self):
		"""
		:return: the unique ID referring to the TestCase that it refers
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

	def get_kill_mutants(self, used_mutants):
		"""
		:param used_mutants: the set of MerMutant or None for all
		:return: the set of mutants that are killed by this tests
		"""
		kill_mutants = set()
		if used_mutants is None:
			for mutant in self.space.get_document().muta_space.get_mutants():
				if mutant.is_killed_by(self):
					kill_mutants.add(mutant)
		else:
			for mutant in used_mutants:
				mutant: MerMutant
				if mutant.is_killed_by(self):
					kill_mutants.add(mutant)
		return kill_mutants

	def get_live_mutants(self, used_mutants):
		"""
		:param used_mutants: the set of MerMutant or None for all
		:return: the set of mutants that are not killed by this one
		"""
		live_mutants = set()
		if used_mutants is None:
			for mutant in self.space.get_document().muta_space.get_mutants():
				if not mutant.is_killed_by(self):
					live_mutants.add(mutant)
		else:
			for mutant in used_mutants:
				mutant: MerMutant
				if not mutant.is_killed_by(self):
					live_mutants.add(mutant)
		return live_mutants


class MerTestCaseSpace:
	"""
	The space incorporates the memory-reduced instances for encoding TestCase in CProject.
	"""

	def __init__(self, document: MerDocument, file_path: str):
		"""
		:param document: the document where this space is created
		:param file_path: directory/file_name.tst: {tid\n}
		"""
		self.document = document
		self.__parse__(file_path)
		return

	def __parse__(self, file_path: str):
		"""
		:param file_path:
		:return: it generates the list of memory-reduced instances for encoding TestCase
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
		:return: the document where this space is created
		"""
		return self.document

	def __len__(self):
		"""
		:return: the number of memory-reduced instances in the space for encoding TestCase
		"""
		return len(self.test_cases)

	def get_test_cases(self):
		"""
		:return: the list of memory-reduced instances in the space for encoding TestCase
		"""
		return self.test_cases

	def get_test_case(self, tid: int):
		"""
		:param tid: the unique ID of TestCase that the output instance represents
		:return: the memory-reduced instance for encoding TestCase with specified ID
		"""
		return self.test_cases[tid]


class MerMutant:
	"""
	It models a memory-reduced instance for encoding Mutant in CProject.
	"""

	def __init__(self, space, mid: int):
		"""
		:param space: the space where this instance is created
		:param mid: the unique ID of the Mutant that this instance represents
		"""
		space: MerMutantSpace
		self.space = space
		self.mid = mid
		self.cmt = self
		self.wmt = self
		self.smt = self
		self.res = ""
		return

	def get_space(self):
		"""
		:return: the space where this instance is created
		"""
		return self.space

	def get_mid(self):
		"""
		:return: the unique ID of the Mutant that this instance represents
		"""
		return self.mid

	def get_c_mutant(self):
		"""
		:return: the coverage version of this mutation
		"""
		return self.cmt

	def get_w_mutant(self):
		"""
		:return: the weak version of this mutation
		"""
		return self.wmt

	def get_s_mutant(self):
		"""
		:return: the strong version of this mutation
		"""
		return self.smt

	def get_result(self):
		"""
		:return: the 0-1 bit-string to denote of which tests kill this mutant
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
			tid = test
		if (tid >= 0) and (tid < len(self.res)):
			return self.res[tid] == '1'
		return False

	def is_killed_in(self, tests):
		"""
		:param tests: the set of MerTestCase or int or None for all
		:return: True if the mutant is killed by any tests in input or False otherwise.
		"""
		if tests is None:
			return '1' in self.res
		else:
			for test in tests:
				if self.is_killed_by(test):
					return True
			return False

	def find_source(self, document: jctest.CDocument):
		"""
		:param document: the document of original data source (before encoding)
		:return: the Mutant instance that this memory-reduced instance encodes.
		"""
		return document.get_project().muta_space.get_mutant(self.mid)

	def get_kill_tests(self, used_tests):
		"""
		:param used_tests: the set of MerTestCase or int for killing this mutant or None for all
		:return: the set of tid for encoding the MerTestCase that can kill this mutant in program
		"""
		kill_tests = set()
		if used_tests is None:
			for test in self.space.get_document().test_space.get_test_cases():
				if self.is_killed_by(test):
					kill_tests.add(test.get_tid())
		else:
			for test in used_tests:
				if isinstance(test, MerTestCase):
					tid = test.get_tid()
				else:
					tid = test
				tid: int
				if self.is_killed_by(tid):
					kill_tests.add(tid)
		return kill_tests

	def get_live_tests(self, used_tests):
		"""
		:param used_tests: the set of MerTestCase or int for killing this mutant or None for all
		:return: the set of tid for encoding the MerTestCase that can not kill this mutant
		"""
		live_tests = set()
		if used_tests is None:
			for test in self.space.get_document().test_space.get_test_cases():
				if not self.is_killed_by(test):
					live_tests.add(test.get_tid())
		else:
			for test in used_tests:
				if isinstance(test, MerTestCase):
					tid = test.get_tid()
				else:
					tid = test
				tid: int
				if not self.is_killed_by(tid):
					live_tests.add(tid)
		return live_tests


class MerMutantSpace:
	"""
	The space of memory-reduced instances for encoding Mutant in CProject.
	"""

	def __init__(self, document: MerDocument, file_path: str):
		"""
		:param document: the document where this space is created
		:param file_path: directory/file_name.mut: {mid cid wid sid res\n}+
		"""
		self.document = document
		self.__load__(file_path)
		self.__link__(file_path)
		return

	def __load__(self, file_path: str):
		"""
		:param file_path: directory/file_name.mut: {mid cid wid sid res\n}+
		:return: it loads the mutant ID from lines and initializes mutants
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
		:param file_path: directory/file_name.mut: {mid cid wid sid res\n}+
		:return: it links the existing mutant to their versions and result.
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
					mutant.cmt = self.mutants[cid]
					mutant.wmt = self.mutants[wid]
					mutant.smt = self.mutants[sid]
					mutant.res = res
		return

	def get_document(self):
		"""
		:return: the document where this space is created
		"""
		return self.document

	def __len__(self):
		"""
		:return: the number of memory-reduced instances in this space for encoding Mutant
		"""
		return len(self.mutants)

	def get_mutants(self):
		"""
		:return: the list of memory-reduced instances in this space for encoding Mutant
		"""
		return self.mutants

	def get_mutant(self, mid: int):
		"""
		:param mid: the unique ID of Mutant that the output memory-reduced instance encodes
		:return: the memory-reduced instance for encoding Mutant with specified input ID
		"""
		return self.mutants[mid]


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
		self.__parse__(file_path)
		return

	def __parse__(self, file_path: str):
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
		if mutant in self.index:
			return self.index[mutant]
		return list()

	def new_feature_matrix(self):
		"""
		:return: the matrix of features for each execution in the space
		"""
		lines, columns = len(self.elist), len(self.document.anot_space.get_annotations())
		x_matrix = sparse.lil_matrix((lines, columns))
		for execution in self.elist:
			for feature in execution.get_features():
				x_matrix[execution.get_eid(), feature] = 1
		return x_matrix

	def new_label_sequence(self, used_tests):
		"""
		:param used_tests: the set of MerTestCase (or int) or None for all the tests for killing
		:return: the list of labels to denote of which executions are killed by input used_tests
		"""
		y_labels = list()
		for execution in self.elist:
			if execution.get_mutant().is_killed_in(used_tests):
				y_labels.append(1)
			else:
				y_labels.append(0)
		return y_labels


## memory-reduced encode-decode


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
		x_matrix = m_document.exec_space.new_feature_matrix()
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

