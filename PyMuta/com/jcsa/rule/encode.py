""" This file defines the data model of objects used to encode data source for pattern mining. """


import os
import scipy.sparse as sparse
import com.jcsa.libs.muta as jcmuta
import com.jcsa.libs.test as jctest


## model definition


class MerDocument:
	"""
	It denotes the memory-reduced data model of CDocument to encode data features within.
	"""

	def __init__(self, directory: str, file_name: str):
		"""
		:param directory: the directory where the encoded files are generated
		:param file_name: the name of files generated to encode data features
		"""
		## 1. generate the file paths for loading elements within
		self.name = file_name
		mut_file_path = os.path.join(directory, self.name + ".mut")
		tst_file_path = os.path.join(directory, self.name + ".tst")
		res_file_path = os.path.join(directory, self.name + ".res")
		ant_file_path = os.path.join(directory, self.name + ".ant")
		exe_file_path = os.path.join(directory, self.name + ".exc")

		## 2, load the data model of encoded elements in document
		self.test_space = MerTestCaseSpace(self, tst_file_path)
		self.muta_space = MerMutantSpace(self, mut_file_path, res_file_path)
		self.anto_space = MerAnnotationSpace(self, ant_file_path)
		self.exec_space = MerExecutionSpace(self, exe_file_path)
		return

	@staticmethod
	def __encode_tst_file__(document: jctest.CDocument, directory: str, file_name: str):
		"""
		:param document:
		:param directory:
		:param file_name:
		:return: xxx.tst
		"""
		tst_file_path = os.path.join(directory, file_name + ".tst")
		with open(tst_file_path, 'w') as writer:
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
		:return: xxx.mut
		"""
		mut_file_path = os.path.join(directory, file_name + ".mut")
		with open(mut_file_path, 'w') as writer:
			for mutant in document.get_project().muta_space.get_mutants():
				mutant: jcmuta.Mutant
				mid = mutant.get_muta_id()
				cid = mutant.get_c_mutant().get_muta_id()
				wid = mutant.get_w_mutant().get_muta_id()
				sid = mutant.get_s_mutant().get_muta_id()
				writer.write("{}\t{}\t{}\t{}\n".format(mid, cid, wid, sid))
		return

	@staticmethod
	def __encode_res_file__(document: jctest.CDocument, directory: str, file_name: str):
		"""
		:param document:
		:param directory:
		:param file_name:
		:return: xxx.res
		"""
		res_file_path = os.path.join(directory, file_name + ".res")
		with open(res_file_path, 'w') as writer:
			for mutant in document.get_project().muta_space.get_mutants():
				mutant: jcmuta.Mutant
				mid = mutant.get_muta_id()
				result = mutant.get_result()
				if result is not None:
					writer.write("{}\t{}\n".format(mid, result.result))
		return

	@staticmethod
	def __encode_ant_file__(document: jctest.CDocument, directory: str, file_name: str):
		"""
		:param document:
		:param directory:
		:param file_name:
		:return: xxx.ant
		"""
		annotation_id_dict = dict()	# CirAnnotation --> aid
		for annotation in document.annotation_tree.get_annotations():
			if not (annotation in annotation_id_dict):
				annotation: jctest.CirAnnotation
				annotation_id_dict[annotation] = len(annotation_id_dict)
		ant_file_path = os.path.join(directory, file_name + ".ant")
		with open(ant_file_path, 'w') as writer:
			for annotation, aid in annotation_id_dict.items():
				writer.write("{}\t{}\n".format(aid, str(annotation)))
		return annotation_id_dict

	@staticmethod
	def __get_features_of__(execution: jctest.SymExecution, is_extended: bool, annotation_id_dict: dict):
		"""
		:param execution: 			the symbolic execution of which annotations are encoded as features
		:param is_extended: 		whether the features are collected using the extension from children
		:param annotation_id_dict: 	mapping from the CirAnnotation to the integer ID which it is encoded
		:return: the set of integer features encoding the CirAnnotation(s) incorporated in
		"""
		features = set()
		for annotation in execution.get_annotations():
			if is_extended:
				for child in annotation.get_all_children():
					child: jctest.CirAnnotation
					aid = annotation_id_dict[child]
					aid: int
					features.add(aid)
			else:
				aid = annotation_id_dict[annotation]
				aid: int
				features.add(aid)
		return features

	@staticmethod
	def __encode_exe_file__(document: jctest.CDocument, directory: str, file_name: str,
							is_extended: bool, annotation_id_dict: dict):
		"""
		:param document:
		:param directory:
		:param file_name:
		:param is_extended: whether to incorporate all the features extended from the features given by each execution
		:param annotation_id_dict: the mapping from CirAnnotation to the integer ID it is encoded
		:return: xxx.exc
		"""
		exe_file_path = os.path.join(directory, file_name + ".exc")
		with open(exe_file_path, 'w') as writer:
			for execution in document.exec_space.get_executions():
				execution: jctest.SymExecution
				mid = execution.get_mutant().get_muta_id()
				tid = -1
				if execution.has_test():
					tid = execution.get_test().get_test_id()
				features = MerDocument.__get_features_of__(execution, is_extended, annotation_id_dict)
				writer.write("{}\t{}".format(mid, tid))
				for feature in features:
					writer.write("\t{}".format(feature))
				writer.write("\n")
		return

	@staticmethod
	def encode_c_document(document: jctest.CDocument, encode_directory: str, is_extended: bool):
		"""
		:param document:
		:param encode_directory: the directory where the encoding directory is created
		:param is_extended: whether to incorporate the features extended from root node
		:return:
		"""
		file_name = document.get_program().name
		directory = os.path.join(encode_directory, file_name)
		if not os.path.exists(directory):
			os.mkdir(directory)
		MerDocument.__encode_tst_file__(document, directory, file_name)
		MerDocument.__encode_mut_file__(document, directory, file_name)
		MerDocument.__encode_res_file__(document, directory, file_name)
		annotation_id_dict = MerDocument.__encode_ant_file__(document, directory, file_name)
		MerDocument.__encode_exe_file__(document, directory, file_name, is_extended, annotation_id_dict)
		return


class MerTestCaseSpace:
	"""
	It denotes the space of test cases encoded to represent TestCase in CDocument, and loaded from encoded file
	in directory/xxx.tst
	"""

	def __init__(self, document: MerDocument, tst_file_path: str):
		"""
		:param document: the document where the test case space is created
		:param tst_file_path: xxx.tst file to preserve test case information
		"""
		self.document = document
		self.test_cases = list()
		self.__load__(tst_file_path)
		return

	def __load__(self, tst_file_path: str):
		"""
		:param tst_file_path: xxx.tst to preserve the integer ID of TestCase in CDocument
		:return: the list of integer ID that represents the test cases from the CDocument.
		"""
		self.test_cases.clear()
		with open(tst_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					self.test_cases.append(MerTestCase(self, int(line)))
		return

	def get_document(self):
		"""
		:return: the document where this space (element) is defined
		"""
		return self.document

	def get_test_cases(self):
		"""
		:return: the test cases encoding the TestCase for CDocument
		"""
		return self.test_cases

	def get_test_case(self, tid: int):
		"""
		:param tid: the integer ID of the test case from 0 until N - 1, where N = len(MerTestSpace.obj)
		:return: the test case encoding the TestCase w.r.t. the given integer ID in the CDocument data.
		"""
		test_case = self.test_cases[tid]
		test_case: MerTestCase
		return test_case

	def __len__(self):
		"""
		:return: the number of test cases defined in this space
		"""
		return len(self.test_cases)


class MerTestCase:
	"""
	It encodes the TestCase in CDocument using its integer ID simply.
	"""

	def __init__(self, space: MerTestCaseSpace, tid: int):
		"""
		:param space: the space where the space is created
		:param tid: the integer ID of TestCase it represents
		"""
		self.space = space
		self.tid = tid
		return

	def get_space(self):
		"""
		:return: the space where this test case is defined
		"""
		return self.space

	def get_tid(self):
		"""
		:return: the integer ID of TestCase it represents
		"""
		return self.tid

	def __str__(self):
		return "tst@" + str(self.tid)

	def find_source(self, document: jctest.CDocument):
		"""
		:param document: the document that the test case's space encodes
		:return: the original test case that the encode element refers to
		"""
		return document.get_project().test_space.get_test_case(self.tid)


class MerMutantSpace:
	"""
	It models the space of mutants encoding the Mutant from CProject in source data.
	"""

	def __init__(self, document: MerDocument, mut_file_path: str, res_file_path: str):
		"""
		:param document: 		the document where this mutation space is defined
		:param mut_file_path: 	the xxx.mut file preserving information of mutant
		:param res_file_path: 	the xxx.res file preserving mutation test results
		"""
		self.document = document
		self.mutants = list()
		self.__load__(mut_file_path)
		self.__link__(mut_file_path)
		self.__done__(res_file_path)
		return

	def __load__(self, mut_file_path: str):
		"""
		:param mut_file_path:	mid cid wid sid
		:return: it loads all the mutants from xxx.mut file
		"""
		mutant_dict = dict()
		with open(mut_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					mid = int(items[0].strip())
					mutant_dict[mid] = MerMutant(self, mid)
		self.mutants.clear()
		for mid in range(0, len(mutant_dict)):
			self.mutants.append(mutant_dict[mid])
		return

	def __link__(self, mut_file_path: str):
		"""
		:param mut_file_path:
		:return: it links each mutant to the coverage, weak and strong versions.
		"""
		with open(mut_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					mutant = self.mutants[int(items[0].strip())]
					mutant.c_mutant = self.mutants[int(items[1].strip())]
					mutant.w_mutant = self.mutants[int(items[2].strip())]
					mutant.s_mutant = self.mutants[int(items[3].strip())]
		return

	def __done__(self, res_file_path: str):
		"""
		:param res_file_path: mid result
		:return: it loads the mutant along with its execution results using string
		"""
		with open(res_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					mutant = self.mutants[int(items[0].strip())]
					if len(items) < 2:
						mutant.result = ""
					else:
						mutant.result = items[1].strip()
		return

	def get_document(self):
		"""
		:return: the document where this mutation space is defined
		"""
		return self.document

	def get_mutants(self):
		"""
		:return: the set of elements encoding the Mutant in CDocument of this space
		"""
		return self.mutants

	def get_mutant(self, mid: int):
		"""
		:param mid: the integer ID referring to the Mutant in CProject.muta_space
		:return: the mutant encoding the Mutant in CDocument in this space
		"""
		mutant = self.mutants[mid]
		mutant: MerMutant
		return mutant

	def __len__(self):
		"""
		:return: the number of mutants encoded in this space
		"""
		return len(self.mutants)


class MerMutant:
	"""
	It denotes the mutant element that encodes the Mutant object in CProject.
	"""

	def __init__(self, space: MerMutantSpace, mid: int):
		"""
		:param space: the space where the encoding element is defined
		:param mid: the integer ID referring to the Mutant in CProject
		"""
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

	def is_killed_by(self, test_case):
		"""
		:param test_case: either MerTestCase or int
		:return: whether the mutant is killed by the given test
		"""
		if isinstance(test_case, MerTestCase):
			tid = test_case.get_tid()
		else:
			test_case: int
			tid = test_case
		if (tid < 0) or (tid >= len(self.result)):
			return False
		else:
			return self.result[tid] == '1'

	def is_killed_in(self, test_cases):
		"""
		:param test_cases: the set of MerTestCase or int or None for all
		:return: whether the mutant is killed by any tests in inputs set
		"""
		if test_cases is None:
			return '1' in self.result
		else:
			for test_case in test_cases:
				if self.is_killed_by(test_case):
					return True
			return False

	def find_source(self, document: jctest.CDocument):
		"""
		:param document: the original source of document
		:return: the original Mutant encoded by this element
		"""
		return document.get_project().muta_space.get_mutant(self.mid)

	def __str__(self):
		return "mut@" + str(self.mid)


class MerAnnotationSpace:
	"""
	The space to encode the elements for CirAnnotation in original CDocument.
	"""

	def __init__(self, document: MerDocument, ant_file_path: str):
		"""
		:param document: 		the document where this space is created for encoding CirAnnotationTree
		:param ant_file_path:	the xxx.ant file to preserve data elements for encoding CirAnnotation(s)
		"""
		self.document = document
		self.annotations = list()
		self.index = dict()		## str --> int
		self.__load__(ant_file_path)
		return

	def __load__(self, ant_file_path: str):
		"""
		:param ant_file_path: aid annotation_code
		:return:
		"""
		annotation_dict = dict()	# aid --> MerAnnotation
		with open(ant_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					aid = int(items[0].strip())
					code = items[1].strip()
					annotation = MerAnnotation(self, aid, code)
					annotation_dict[aid] = annotation
		self.annotations.clear()
		for aid in range(0, len(annotation_dict)):
			self.annotations.append(annotation_dict[aid])
			self.index[annotation_dict[aid].code] = aid
		return

	def get_document(self):
		"""
		:return: the document where this space is created for encoding CirAnnotationTree
		"""
		return self.document

	def get_annotations(self):
		"""
		:return: the xxx.ant file to preserve data elements for encoding CirAnnotation(s)
		"""
		return self.annotations

	def get_annotation(self, aid: int):
		"""
		:param aid: the integer ID encoding the annotation element in the space
		:return: the element encoding the CirAnnotation w.r.t. the integer ID
		"""
		annotation = self.annotations[aid]
		annotation: MerAnnotation
		return annotation

	def find_annotation(self, code: str):
		aid = self.index[code]
		return self.get_annotation(aid)

	def __len__(self):
		"""
		:return: the number of elements encoding annotations in the document
		"""
		return len(self.annotations)

	def normal(self, features):
		"""
		:param features: the set of Integer encoding the CirAnnotation in the space
		:return: the sorted sequence of unique integer IDs for encoding annotations
		"""
		feature_list = list()
		for feature in features:
			feature: int
			if (feature >= 0) and (feature < len(self.annotations)):
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
			if (feature >= 0) and (feature < len(self.annotations)):
				annotation = self.annotations[feature]
				annotation: MerAnnotation
				annotations.add(annotation)
		return annotations


class MerAnnotation:
	"""
	It refers to a CirAnnotation defined in CDocument for feature modeling.
	"""

	def __init__(self, space: MerAnnotationSpace, aid: int, code: str):
		"""
		:param space: the space where the annotation element is defined
		:param aid:   the integer ID representing the target annotation
		:param code:  the string code from the annotation being encoded
		"""
		self.space = space
		self.aid = aid
		self.code = code.strip()
		return

	def get_space(self):
		"""
		:return: the space where the annotation element is defined
		"""
		return self.space

	def get_aid(self):
		"""
		:return: the integer ID representing the target annotation
		"""
		return self.aid

	def get_code(self):
		"""
		:return: the string code from the annotation being encoded
		"""
		return self.code

	def __str__(self):
		return self.code

	def find_source(self, document: jctest.CDocument):
		"""
		:param document:
		:return: the original CirAnnotation encoded by this element
		"""
		source = document.annotation_tree.get_annotation(self.code)
		source: jctest.CirAnnotation
		return source


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
		lines, columns = len(self.exec_list), len(self.document.anto_space.get_annotations())
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


class MerExecution:
	"""
	It models the data element for encoding the SymExecution in the document
	"""

	def __init__(self, space: MerExecutionSpace, eid: int, mutant: MerMutant, test_case: MerTestCase, features):
		"""
		:param space: 		the space where the data element is defined
		:param eid: 		the integer ID to denote this element in the space
		:param mutant: 		the mutation that is executed against test in this execution
		:param test_case: 	the test case that is executed against mutant in this execution or None if it is static
		:param features:	the set of integer features encoding the CirAnnotation(s) incorporated in the execution
		"""
		self.space = space
		self.eid = eid
		self.mutant = mutant
		self.test_case = test_case
		self.features = space.get_document().anto_space.normal(features)
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
		return self.space.get_document().anto_space.decode(self.features)

	def find_source(self, document: jctest.CDocument):
		"""
		:param document:
		:return: the original mutant execution that the element refers to
		"""
		return document.exec_space.get_execution(self.eid)

	def __str__(self):
		return "exe@" + str(self.eid)


## encode-decode


def encode_c_documents(prev_path: str, post_path: str, is_extended: bool):
	"""
	:param prev_path:
	:param post_path:
	:param is_extended:
	:return:
	"""
	for file_name in os.listdir(prev_path):
		inputs_directory = os.path.join(prev_path, file_name)
		c_document = jctest.CDocument(inputs_directory, file_name)
		MerDocument.encode_c_document(c_document, post_path, is_extended)
		print("Encode project for", file_name)
	print()
	return


def decode_m_documents(post_path: str):
	"""
	:param post_path:
	:return:
	"""
	print_executions = True
	for file_name in os.listdir(post_path):
		encode_directory = os.path.join(post_path, file_name)
		m_document = MerDocument(encode_directory, file_name)
		print("Load", file_name, "using:\t",
			  len(m_document.test_space.get_test_cases()), "test cases;",
			  len(m_document.muta_space.get_mutants()), "mutants;",
			  len(m_document.exec_space.get_executions()), "executions;",
			  len(m_document.anto_space.get_annotations()), "annotations.")
		if print_executions:
			print(m_document.exec_space.new_feature_matrix().toarray())
		print()
	print()
	return


def main(prev_path: str, post_path: str, extend: bool):
	encode_c_documents(prev_path, post_path, extend)
	decode_m_documents(post_path)
	return 0


## main testing


if __name__ == "__main__":
	prev_directory = "/home/dzt2/Development/Data/zexp/features"
	post_directory = "/home/dzt2/Development/Data/zexp/encoding"
	exit_code = main(prev_directory, post_directory, True)
	exit(exit_code)

