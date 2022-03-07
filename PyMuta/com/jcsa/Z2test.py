import z3


def test_logic_and_to_arith_mul():
	x = z3.Int('X')
	y = z3.Int('Y')
	E1 = z3.And(x != 0, y != 0)
	E2 = (x * y) != 0
	z3.solve(E1 != E2)
	return


def test_logic_and_to_bitws_and():
	x = z3.BitVec('X', 1)
	y = z3.BitVec('Y', 1)
	E1 = z3.And(x != 0, y != 0)
	E2 = (x & y) != 0
	z3.solve(E1 != E2)
	return


def test_absolute_insert_cons():
	z3.solve(5 < 0)
	return


def test_arithmetic_mod_1():
	x = z3.Int('X')
	E1 = x % 5
	E2 = (x + 5) % 5
	z3.solve(E1 != E2)


def test_not_equal_pattern_1():
	x = z3.Int('X')
	ax = z3.If(x < 0, -x, x)
	E1 = (x != 0)
	E2 = (ax != 0)
	z3.solve(E1 != E2)
	return


if __name__ == "__main__":
	test_logic_and_to_arith_mul()
	test_logic_and_to_bitws_and()
	test_absolute_insert_cons()
	test_not_equal_pattern_1()
	test_arithmetic_mod_1()

