"""This file defines the encoded model to represent mutant, test and symbolic condition etc."""


import os
import com.jcsa.libs.muta as jcmuta
import com.jcsa.libs.test as jctest


class EncDocument:
	"""
	The document preserves the information encoded from CDocument in a predefined project
	"""

	def __init__(self, directory: str, file_name: str):
		"""
		:param directory: where the encoded feature files are directly preserved
		:param file_name: the file name of the program name
		"""
		self.name = file_name				# program name
		self.test_space = EncTestSpace(self, os.path.join(directory, file_name + ".tst"))
		self.muta_space = EncMutantSpace(self, os.path.join(directory, file_name + ".mut"))
		self.conditions = EncConditionSpace(self, os.path.join(directory, file_name + ".sym"))
		self.exec_space = EncExecutionSpace(self, os.path.join(directory, file_name + ".lin"))
		return


class EncMutant:
	"""
	The encoded mutation is a pair [id, result]
	"""

	def __init__(self, space, mut_id: int, result: str):
		"""
		:param space:  the space of encoded mutants used in
		:param mut_id: the unique ID of mutant being encoded
		:param result: the 01 string representing test result
		"""
		space: EncMutantSpace
		self.space = space
		self.mut_id = mut_id
		self.result = result
		return

	def get_space(self):
		"""
		:return: the space of encoded mutants used in
		"""
		return self.space

	def get_mut_id(self):
		"""
		:return: the unique ID of mutant being encoded
		"""
		return self.mut_id

	def get_result(self):
		"""
		:return: the 01 string representing test result
		"""
		return self.result

	def is_killed_by(self, test):
		"""
		:param test: the integer ID of test case encoded in the project
		:return: whether the mutant is killed by the test or None if unknown
		"""
		if isinstance(test, EncTestCase):
			tid = test.get_test_id()
		else:
			test: int
			tid = test
		if (tid < 0) or (tid >= len(self.result)):
			return None
		else:
			return self.result[tid] == '1'

	def is_killed_in(self, tests):
		"""
		:param tests: the collection of encoded test cases (as integer) to kill
		:return: whether the mutant is killed by any test within the collection
		"""
		if tests is None:
			return '1' in self.result
		else:
			for test in tests:
				if self.is_killed_by(test):
					return True
			return False


class EncMutantSpace:
	"""
	The space where encoded mutations are preserved
	"""

	def __init__(self, document: EncDocument, file_path: str):
		"""
		:param document:
		:param file_path: xxx.mut to preserve encoded mutants and their results
		"""
		self.document = document
		self.mutants = list()
		mutants_dict = dict()
		with open(file_path, 'r') as reader:
			for line in reader:
				if len(line.strip()) > 0:
					items = line.split('\t')
					mid = int(items[0].strip())
					result = items[1].strip()
					mutants_dict[mid] = EncMutant(self, mid, result)
		for mid in range(0, len(mutants_dict)):
			self.mutants.append(mutants_dict[mid])
		return

	def get_document(self):
		"""
		:return: the document where the mutant space is defined
		"""
		return self.document

	def get_mutants(self):
		"""
		:return: the collection of encoded mutants within the space
		"""
		return self.mutants

	def get_mutant(self, mut_id: int):
		"""
		:param mut_id:
		:return: the encoded mutant w.r.t. given integer ID
		"""
		return self.mutants[mut_id]


class EncTestCase:
	"""
	The encoded test case is only an integer
	"""

	def __init__(self, space, test_id: int):
		space: EncTestSpace
		self.test_id = test_id
		self.space = space
		return

	def get_space(self):
		"""
		:return: space where the test case is created
		"""
		return self.space

	def get_test_id(self):
		"""
		:return: the integer ID of the test in space
		"""
		return self.test_id


class EncTestSpace:
	"""
	The space records the encoded test case (only integer)
	"""

	def __init__(self, document: EncDocument, file_path: str):
		self.document = document
		self.test_cases = list()
		tests_dict = dict()
		with open(file_path, 'r') as reader:
			for line in reader:
				if len(line.strip()) > 0:
					test_id = int(line.strip())
					tests_dict[test_id] = EncTestCase(self, test_id)
		for test_id in range(0, len(tests_dict)):
			self.test_cases.append(tests_dict[test_id])
		return

	def get_document(self):
		"""
		:return: the document where the space is defined
		"""
		return self.document

	def get_test_cases(self):
		return self.test_cases

	def get_test_case(self, test_id: int):
		return self.test_cases[test_id]


class EncCondition:
	"""
	Encoded symbolic condition is a pair [id, word]
	"""

	def __init__(self, space, cid: int, word: str):
		space: EncConditionSpace
		self.space = space
		self.cid = cid
		self.word = word
		return

	def get_space(self):
		"""
		:return: space where the condition is created
		"""
		return self.space

	def get_cid(self):
		"""
		:return: the index of the condition in the space
		"""
		return self.cid

	def get_word(self):
		"""
		:return: string word encoding the condition used
		"""
		return self.word

	def __str__(self):
		return self.word


class EncConditionSpace:
	"""
	The space of symbolic conditions being encoded
	"""

	def __init__(self, document: EncDocument, file_path: str):
		"""
		:param document: where the space is created
		:param file_path: xxx.sym that preserves the symbolic conditions
		"""
		self.document = document
		self.conditions = list()
		with open(file_path, 'r') as reader:
			for word in reader:
				word = word.strip()
				if len(word) > 0:
					condition = EncCondition(self, len(self.conditions), word)
					self.conditions.append(condition)
		return

	def get_document(self):
		return self.document

	def get_conditions(self):
		return self.conditions

	def get_condition(self, cid: int):
		return self.conditions[cid]


class EncExecution:
	"""
	It encodes the symbolic execution in the project
	"""

	def __init__(self, space, eid: int, mutant: EncMutant, conditions):
		"""
		:param space:
		:param mutant: the mutant to be killed
		:param conditions: the collection of symbolic conditions required
		"""
		space: EncExecutionSpace
		self.space = space
		self.eid = eid
		self.mutant = mutant
		self.conditions = list()
		for condition in conditions:
			condition: EncCondition
			self.conditions.append(condition)
		return

	def get_space(self):
		return self.space

	def get_exec_id(self):
		return self.eid

	def get_mutant(self):
		return self.mutant

	def get_conditions(self):
		return self.conditions

	def get_condition(self, k: int):
		return self.conditions[k]


class EncExecutionSpace:
	"""
	It preserves the symbolic executions as feature inputs
	"""

	def __init__(self, document: EncDocument, file_path: str):
		"""
		:param document:
		:param file_path: xxx.lin
		"""
		self.document = document
		self.executions = list()
		self.muta_execs = dict()
		with open(file_path, 'r') as reader:
			for line in reader:
				if len(line.strip()) > 0:
					items = line.strip().split('\t')
					mid = int(items[0].strip())
					mutant = self.document.muta_space.get_mutant(mid)
					conditions = list()
					for k in range(1, len(items)):
						condition = self.document.conditions.get_condition(int(items[k].strip()))
						conditions.append(condition)
					execution = EncExecution(self, len(self.executions), mutant, conditions)
					self.executions.append(execution)
					if not (mutant in self.muta_execs):
						self.muta_execs[mutant] = list()
					self.muta_execs[mutant].append(execution)
		return

	def get_document(self):
		return self.document

	def get_mutants(self):
		return self.muta_execs.keys()

	def get_executions(self):
		return self.executions

	def get_execution(self, eid: int):
		return self.executions[eid]

	def get_executions_of(self, mutant: EncMutant):
		if not (mutant in self.muta_execs):
			return list()
		else:
			return self.muta_execs[mutant]


def encoding_c_document(c_document: jctest.CDocument, encode_directory: str):
	"""
	:param c_document:
	:param encode_directory:
	:return: xxx.mut xxx.tst xxx.sym xxx.lin
	"""
	output_directory = os.path.join(encode_directory, c_document.project.program.name)
	if not os.path.exists(output_directory):
		os.mkdir(output_directory)
	file_name = c_document.project.program.name
	# 1. xxx.mut
	with open(os.path.join(output_directory, file_name + ".mut"), 'w') as writer:
		for mutant in c_document.project.muta_space.get_mutants():
			mutant: jcmuta.Mutant
			if mutant.get_result() is None:
				result = ""
			else:
				result = mutant.get_result().result
			writer.write("{}\t{}\n".format(mutant.get_muta_id(), result))
	# 2. xxx.tst
	with open(os.path.join(output_directory, file_name + ".tst"), 'w') as writer:
		for test in c_document.project.test_space.get_test_cases():
			test: jcmuta.TestCase
			writer.write("{}\n".format(test.get_test_id()))
	# 3. xxx.sym
	index, cid = dict(), 0
	with open(os.path.join(output_directory, file_name + ".sym"), 'w') as writer:
		for condition in c_document.get_conditions_lib().get_all_conditions():
			writer.write("{}\n".format(str(condition)))
			index[str(condition)] = cid
			cid += 1
	# 4. xxx.lin
	with open(os.path.join(output_directory, file_name + ".lin"), 'w') as writer:
		for execution in c_document.get_executions():
			writer.write(str(execution.get_mutant().get_muta_id()))
			for condition in execution.get_conditions():
				cid = index[str(condition)]
				writer.write("\t{}".format(cid))
			writer.write("\n")
	return


def encoding_c_documents(inputs_directory: str, output_directory: str, file_postfix: str):
	"""
	:param inputs_directory: original project directory
	:param output_directory: output directory to encode
	:param file_postfix:
	:return:
	"""
	for file_name in os.listdir(inputs_directory):
		c_document = jctest.CDocument(os.path.join(inputs_directory, file_name), file_name, file_postfix)
		encoding_c_document(c_document, output_directory)
		print("Encoding", file_name, "to output directory.")
	return


def testing_e_documents(encode_directory: str):
	for file_name in os.listdir(encode_directory):
		directory = os.path.join(encode_directory, file_name)
		e_document = EncDocument(directory, file_name)
		print("Load", len(e_document.muta_space.get_mutants()), "mutants and",
			  len(e_document.test_space.get_test_cases()), "test cases and",
			  len(e_document.exec_space.get_executions()), "executions with",
			  len(e_document.conditions.get_conditions()), "conditions.")
	return



if __name__ == "__main__":
	prev_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	post_path = "/home/dzt2/Development/Data/encodes"
	# encoding_c_documents(prev_path, post_path, ".sip")
	testing_e_documents(post_path)

