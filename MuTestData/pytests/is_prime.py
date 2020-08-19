import os
import sys
import random


def range_list(min_val, max_val):
    """[summary]
    It generates the list of values in [min_val, max_val]
    Args:
        min_val ([type]): the minimal value in list
        max_val ([type]): the maximal value in list
    """
    lst = list()
    for k in range(min_val, max_val + 1):
        lst.append(k)
    return lst


if __name__ == "__main__":
    counter = 0
    with open(sys.argv[1], "w") as writer:
        writer.write("\n")
        counter += 1
        beg_value, end_value = -16, 36000
        while beg_value <= end_value:
            length = random.randint(0, 16)
            lst = range_list(beg_value, beg_value + length)
            beg_value = beg_value + length
            for value in lst:
                writer.write(str(value) + " ")
            writer.write("\n")
            counter += 1
    print("Generate", counter, "tests.")

