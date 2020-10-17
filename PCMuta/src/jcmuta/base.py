"""
It defines the simplified model to describe the basic element in C program, including:
	CTokenType, CToken {bool, char, int, float, str, complex, ...}
"""

import enum


class CTokenType(enum.Enum):
	boolean = 0
	character = 1
	integer = 2
	real = 3
	complex_number = 4
	keyword = 5
	operator = 6
	punctuate = 7
	ast_node_id = 8
	cir_node_id = 9
	string = 10
	nullptr = 11


class CToken:
	"""
	The token is the basic data unit in C program model, which describes a basic unit
	as well as its meta-type, denoted as (type, value)
	"""

	def __init__(self, token_type: CTokenType, value):
		"""
		:param token_type: boolean|character|integer|real|complex_number|keyword|operator|punctuate|ast_node_id
		|cir_node_id|string
		:param value: bool|char|int|float|complex|str|str|str|int|int|str
		"""
		self.token_type = token_type
		self.token_value = value
		return

	def get_token_type(self):
		"""
		:return: type of the token data unit
		"""
		return self.token_type

	def get_token_value(self):
		"""
		:return: value hold by the token unit
		"""
		return self.token_value

	def __str__(self):
		return str(self.token_value)

	@staticmethod
	def parse(word: str):
		index = word.find('@')
		prev = word[0: index].strip()
		post = word[index + 1:].strip()
		if prev == 'n':
			return CToken(CTokenType(CTokenType.nullptr), None)
		elif prev == 'b':
			return CToken(CTokenType(CTokenType.boolean), post == "True")
		elif prev == 'c':
			return CToken(CTokenType(CTokenType.character), int(post))
		elif prev == 'i':
			return CToken(CTokenType(CTokenType.integer), int(post))
		elif prev == 'f':
			return CToken(CTokenType(CTokenType.real), float(post))
		elif prev == 's':
			return CToken(CTokenType(CTokenType.string), post)
		elif prev == 'x':
			items = post.split('@')
			x_part = float(items[0])
			y_part = float(items[1])
			return CToken(CTokenType(CTokenType.complex_number), complex(x_part, y_part))
		elif prev == 'k':
			return CToken(CTokenType(CTokenType.keyword), post)
		elif prev == 'p':
			return CToken(CTokenType(CTokenType.punctuate), post)
		elif prev == 'o':
			return CToken(CTokenType(CTokenType.operator), post)
		elif prev == 'a':
			return CToken(CTokenType(CTokenType.ast_node_id), int(post))
		elif prev == 'r':
			return CToken(CTokenType(CTokenType.cir_node_id), int(post))
		else:
			return CToken(CTokenType(CTokenType.nullptr), None)

