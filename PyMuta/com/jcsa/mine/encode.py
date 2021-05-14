"""
This file defines the memory-reduced (Mer) model for:

	---	MerDocument:		it maps to the com.jcsa.libs.test.CDocument

	---	MerTestCase:		it maps to the com.jcsa.libs.muta.TestCase		{tid: int}
	---	MerTestCaseSpace:	it maps to the com.jcsa.libs.muta.TestSpace		{test_cases: list[MerTestCase]}

	---	MerMutantSpace:		it maps to the com.jcsa.libs.muta.MutantSpace	{mutants: list[MerMutant]}
	---	MerMutant:			it maps to the com.jcsa.libs.muta.Mutant		{cov_mutant, wek_mutant, str_mutant, result}
	---	MerMutantResult:	it maps to the com.jcsa.libs.muta.MutationResult	{mutant, result (String)}

	---	MerCondition:		it maps to the com.jcsa.libs.test.SymCondition	{cid, code (String)}
	---	MerConditions:		it maps to the com.jcsa.libs.test.SymConditionLibrary	{conditions: list[MerCondition]}

	---	MerFeatureLine:		it represents the sorted sequence of integers encoding symbolic conditions.
	---	MerExecution:		it maps to the com.jcsa.libs.test.SymExecution	{eid, mutant, features}
	---	MerExecutionSpace:	it manages the data model of {executions: list[MerExecution]; muta_executions}

"""


import os
import com.jcsa.libs.muta as jcmuta
import com.jcsa.libs.test as jctest


### document


class MerDocument:
	"""
	It provides the memory-reduced model of CDocument
	"""

	def __init__(self, directory: str, file_name: str):
		"""
		:param directory: encoding directory where the encoded features are preserved
		:param file_name:
		"""
		self.name = file_name
		tst_file_path = os.path.join(directory, file_name + ".tst")
		mut_file_path = os.path.join(directory, file_name + ".mut")
		res_file_path = os.path.join(directory, file_name + ".res")
		sym_file_path = os.path.join(directory, file_name + ".sym")
		exe_file_path = os.path.join(directory, file_name + ".exc")
		self.test_space = MerTestCaseSpace(self, tst_file_path)
		self.muta_space = MerMutantSpace(self, mut_file_path, res_file_path)
		self.cond_space = MerConditionSpace(self, sym_file_path)
		self.exec_space = MerExecutionSpace(self, exe_file_path)
		return

	@staticmethod
	def encode_mer_document(c_document: jctest.CDocument, directory: str):
		"""
		:param c_document:
		:param directory:
		:return: translate the original document to the memory-reduced in specified directory
		"""
		file_name = c_document.project.program.name
		directory = os.path.join(directory, file_name)
		if not os.path.exists(directory):
			os.mkdir(directory)

		## xxx.tst
		with open(os.path.join(directory, file_name + ".tst"), 'w') as writer:
			for test in c_document.project.test_space.get_test_cases():
				test: jcmuta.TestCase
				writer.write("{}\n".format(test.get_test_id()))

		## xxx.mut
		with open(os.path.join(directory, file_name + ".mut"), 'w') as writer:
			for mutant in c_document.project.muta_space.get_mutants():
				mutant: jcmuta.Mutant
				writer.write("{}\t{}\t{}\t{}\n".format(mutant.get_muta_id(),
													   mutant.get_c_mutant().get_muta_id(),
													   mutant.get_w_mutant().get_muta_id(),
													   mutant.get_s_mutant().get_muta_id()))

		### xxx.res
		with open(os.path.join(directory, file_name + ".res"), 'w') as writer:
			for mutant in c_document.project.muta_space.get_mutants():
				mutant: jcmuta.Mutant
				result = mutant.get_result()
				writer.write("{}\t{}\n".format(mutant.get_muta_id(), result.result))

		### xxx.sym
		cid, condition_index_dict = 0, dict()
		with open(os.path.join(directory, file_name + ".sym"), 'w') as writer:
			for condition in c_document.get_conditions_lib().get_all_conditions():
				writer.write("{}\n".format(str(condition)))
				condition_index_dict[condition] = cid
				cid += 1

		### xxx.exc
		with open(os.path.join(directory, file_name + ".exc"), 'w') as writer:
			for execution in c_document.get_executions():
				execution: jctest.SymExecution
				writer.write("{}".format(execution.get_mutant().get_muta_id()))
				for condition in execution.get_conditions():
					cid = condition_index_dict[condition]
					writer.write("\t{}".format(cid))
				writer.write("\n")
		return


### test case

class MerTestCase:
	"""
	It refers to the integer ID of the TestCase.
	"""

	def __init__(self, space, tid: int):
		"""
		:param space: the space where the test case is defined
		:param tid: integer ID of TestCase
		"""
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
		:return: integer ID of TestCase
		"""
		return self.tid

	def __str__(self):
		return str(self.tid)


class MerTestCaseSpace:
	"""
	The space of test cases
	"""

	def __init__(self, document: MerDocument, file_path: str):
		"""
		:param document:
		:param file_path: xxx.tst [each line is an integer or empty]
		"""
		self.document = document
		self.test_cases = list()
		self.__load__(file_path)
		return

	def __load__(self, file_path: str):
		"""
		:param file_path: xxx.tst [each line is an integer]
		:return:
		"""
		self.test_cases.clear()
		test_cases_dict = dict()
		with open(file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					tid = int(line)
					test_cases_dict[tid] = MerTestCase(self, tid)
		for tid in range(0, len(test_cases_dict)):
			test_case = test_cases_dict[tid]
			self.test_cases.append(test_case)
		return

	def get_document(self):
		"""
		:return: the document where the space is defined
		"""
		return self.document

	def get_test_cases(self):
		"""
		:return: the collection of test cases defined in the space
		"""
		return self.test_cases

	def get_test_case(self, tid: int):
		"""
		:param tid: the unique integer ID of the test case in the space
		:return:
		"""
		test_case = self.test_cases[tid]
		test_case: MerTestCase
		return test_case


### mutant


class MerMutant:
	"""
	The memory-reduced mutant contains [space, mid, cov_mutant, wek_mutant, str_mutant, result]
	"""

	def __init__(self, space, mid: int):
		"""
		:param space: the mutation space
		:param mid: the unique integer ID
		"""
		space: MerMutantSpace
		self.space = space
		self.mid = mid
		self.c_mutant = None
		self.w_mutant = None
		self.s_mutant = None
		self.result = MerMutantResult(self, "")
		return

	def get_space(self):
		"""
		:return: the mutation space
		"""
		return self.space

	def get_mid(self):
		"""
		:return: the unique integer ID
		"""
		return self.mid

	def get_coverage_mutant(self):
		"""
		:return: the mutant is covered if this mutant is killed
		"""
		self.c_mutant: MerMutant
		return self.c_mutant

	def get_weak_mutant(self):
		"""
		:return: the mutant is weakly killed if this mutant is killed
		"""
		self.w_mutant: MerMutant
		return self.w_mutant

	def get_strong_mutant(self):
		"""
		:return: the mutant is strongly killed if this one is killed
		"""
		self.s_mutant: MerMutant
		return self.s_mutant

	def get_result(self):
		"""
		:return: the test result of the mutation
		"""
		self.result: MerMutantResult
		return self.result


class MerMutantResult:
	"""
	It represents the result of a mutant.
	"""

	def __init__(self, mutant: MerMutant, result: str):
		"""
		:param mutant:
		:param result: the 0-1 sequence to denote whether a mutant is killed by which test in space
		"""
		self.mutant = mutant
		self.result = result
		return

	def get_mutant(self):
		"""
		:return: the mutant that the result describes
		"""
		return self.mutant

	def get_result(self):
		"""
		:return: the 0-1 sequence to denote whether a mutant is killed by which test in space
		"""
		return self.result

	def is_killed_by(self, test):
		"""
		:param test: either MerTestCase or int
		:return: None if unknown
		"""
		if isinstance(test, MerTestCase):
			tid = test.tid
		else:
			test: int
			tid = test
		if (tid >= 0) or (tid < len(self.result)):
			return self.result[tid] == '1'
		else:
			return None

	def is_killed_in(self, tests):
		"""
		:param tests: the collection of test case (or tid) used to kill the mutant
		:return:
		"""
		if tests is None:
			return '1' in self.result
		else:
			for test in tests:
				if self.is_killed_by(test):
					return True
			return False

	def __str__(self):
		return self.result

	def __len__(self):
		return len(self.result)

	def get_tests_of(self, killed: bool):
		"""
		:param killed: True to select tests killing this mutant or vice versa
		:return:
		"""
		tests = list()
		for k in range(0, len(self.result)):
			if self.result[k] == '1':
				if killed:
					tests.append(k)
				else:
					pass
			else:
				if killed:
					pass
				else:
					tests.append(k)
		return tests


class MerMutantSpace:
	"""
	The mutation space
	"""

	def __init__(self, document: MerDocument, mut_file_path: str, res_file_path: str):
		"""
		:param document:
		:param mut_file_path: mid cid wid sid
		:param res_file_path: mid result_string
		"""
		self.document = document
		self.mutants = list()
		self.__load_mut__(mut_file_path)
		self.__link_mut__(mut_file_path)
		self.__kill_mut__(res_file_path)
		return

	def get_document(self):
		return self.document

	def get_mutants(self):
		"""
		:return: the collection of mutants defined in the space
		"""
		return self.mutants

	def get_mutant(self, mid: int):
		"""
		:param mid:
		:return: the mutant w.r.t. unique ID in the space
		"""
		mutant = self.mutants[mid]
		mutant: MerMutant
		return mutant

	def __load_mut__(self, mut_file_path: str):
		"""
		:param mut_file_path: mid cid wid sid
		:return:
		"""
		mutants_dict = dict()
		with open(mut_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					mid = int(items[0].strip())
					mutant = MerMutant(self, mid)
					mutants_dict[mid] = mutant
		self.mutants.clear()
		for mid in range(0, len(mutants_dict)):
			self.mutants.append(mutants_dict[mid])
		return

	def __link_mut__(self, mut_file_path: str):
		"""
		:param mut_file_path: mid cid wid sid
		:return:
		"""
		with open(mut_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					mid = int(items[0].strip())
					cid = int(items[1].strip())
					wid = int(items[2].strip())
					sid = int(items[3].strip())
					mutant = self.mutants[mid]
					mutant: MerMutant
					mutant.c_mutant = self.mutants[cid]
					mutant.w_mutant = self.mutants[wid]
					mutant.s_mutant = self.mutants[sid]
		return

	def __kill_mut__(self, res_file_path: str):
		"""
		:param res_file_path: mid result
		:return:
		"""
		with open(res_file_path, 'r') as reader:
			for line in reader:
				if len(line.strip()) > 0:
					items = line.split('\t')
					mid = int(items[0].strip())
					res = items[1].strip()
					mutant = self.mutants[mid]
					mutant: MerMutant
					mutant.get_result().result = res
		return


### condition


class MerCondition:
	"""
	It represents memory-reduced symbolic condition.
	"""

	def __init__(self, space, cid: int, code: str):
		"""
		:param space:
		:param cid:
		:param code:
		"""
		space: MerConditionSpace
		self.space = space
		self.cid = cid
		self.code = code
		return

	def get_space(self):
		return self.space

	def get_cid(self):
		"""
		:return: integer ID of the symbolic condition
		"""
		return self.cid

	def get_code(self):
		return self.code

	def __str__(self):
		return self.code


class MerConditionSpace:
	"""
	It manages the symbolic conditions used in project
	"""

	def __init__(self, document: MerDocument, sym_file_path: str):
		self.document = document
		self.conditions = list()
		with open(sym_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					condition = MerCondition(self, len(self.conditions), line)
					self.conditions.append(condition)
		return

	def get_document(self):
		return self.document

	def get_conditions(self):
		"""
		:return: the collection of symbolic conditions defined
		"""
		return self.conditions

	def get_condition(self, cid: int):
		"""
		:param cid:
		:return: the symbolic condition w.r.t. integer ID
		"""
		return self.conditions[cid]

	def __len__(self):
		"""
		:return: the number of conditions defined in space
		"""
		return len(self.conditions)

	def encode(self, conditions):
		"""
		:param conditions: the collection of MerCondition
		:return:
		"""
		self.conditions = self.conditions
		features = list()
		for condition in conditions:
			condition: MerCondition
			feature = condition.get_cid()
			if not (feature in features):
				features.append(feature)
		features.sort()
		return features

	def decode(self, features: list):
		"""
		:param features: the sequence of integers encoding symbolic conditions
		:return:
		"""
		conditions = list()
		for feature in features:
			feature: int
			conditions.append(self.get_condition(feature))
		return conditions


### execution


class MerExecution:
	"""
	It represents the memory-reduced execution for killing mutant.
	"""

	def __init__(self, space, eid: int, mutant: MerMutant, features):
		"""
		:param space:
		:param mutant:
		:param features: the collection of integers encoding the conditions used in this execution
		"""
		space: MerExecutionSpace
		self.space = space
		self.eid = eid
		self.mutant = mutant
		self.features = list()
		for feature in features:
			feature: int
			self.features.append(feature)
		self.features.sort()
		return

	def get_space(self):
		return self.space

	def get_eid(self):
		return self.eid

	def get_mutant(self):
		return self.mutant

	def get_features(self):
		"""
		:return: feature vector of the execution line
		"""
		return self.features

	def get_conditions(self):
		"""
		:return: the set of condition instances used in the execution process.
		"""
		document = self.space.document
		document: MerDocument
		return document.cond_space.decode(self.features)


class MerExecutionSpace:
	"""
	The execution space
	"""

	def __init__(self, document: MerDocument, exe_file_path: str):
		"""
		:param document: where the execution space is created
		:param exe_file_path: mid [cid]+
		"""
		self.document = document
		self.executions = list()
		self.muta_execs = dict()
		with open(exe_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					mutant = self.document.muta_space.get_mutant(int(items[0].strip()))
					features = set()
					for k in range(1, len(items)):
						features.add(int(items[k].strip()))
					execution = MerExecution(self, len(self.executions), mutant, features)
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

	def get_executions_of(self, mutant: MerMutant):
		if mutant in self.muta_execs:
			return self.muta_execs[mutant]
		else:
			return list()


### encoding-decoding


def encode_c_documents(prev_path: str, post_path: str, postfix: str):
	"""
	:param prev_path:
	:param post_path:
	:param postfix: .sip or .sit
	:return:
	"""
	for file_name in os.listdir(prev_path):
		inputs_directory = os.path.join(prev_path, file_name)
		c_document = jctest.CDocument(inputs_directory, file_name, postfix)
		MerDocument.encode_mer_document(c_document, post_path)
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
			  len(m_document.cond_space.get_conditions()), "conditions.")
	print()
	return


def main(prev_path: str, post_path: str, postfix: str):
	encode_c_documents(prev_path, post_path, postfix)
	decode_m_documents(post_path)
	return 0


## main testing


if __name__ == "__main__":
	prev_directory = "/home/dzt2/Development/Data/zexp/features"
	post_directory = "/home/dzt2/Development/Data/zexp/encoding"
	file_postfix = ".sip"
	exit_code = main(prev_directory, post_directory, file_postfix)
	exit(exit_code)

