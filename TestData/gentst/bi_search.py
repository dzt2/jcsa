import os
import random
import sys


def rand_list(min_val: int, max_val: int, length: int):
    """[summary]
    It generates a list of specified length with randomly generated integer
    Args:
        min_val (int): the minimal value generated in random list
        max_val (int): the maximal value generated in random list
        length (int): the length of the randomly generated list
    """
    data_lst = list()
    while len(data_lst) < length:
        data_lst.append(random.randint(min_val, max_val))
    return data_lst


def value_set(data_lst: list):
    values = set()
    for value in data_lst:
        values.add(value)
    return values


def generate_tests(file_path: str):
    """[summary]
    It writes the generated inputs to the specified file-path
    Args:
        file_path (str): test suite file where test inputs are generated
    """
    with open(file_path, "w") as writer:
        max_val, min_val = 8, -8
        writer.write("\n")
        for value in range(min_val, max_val + 1):
            writer.write(str(value) + "\n")
        for length in range(1, 256):
            data_lst = rand_list(min_val, max_val, length)
            values = value_set(data_lst)
            for value in values:
                writer.write(str(value))
                for item in data_lst:
                    writer.write(" " + str(item))
                writer.write("\n")
            for value in range(max_val + 1, max_val + 3):
                writer.write(str(value))
                for item in data_lst:
                    writer.write(" " + str(item))
                writer.write("\n")
            for value in range(min_val - 2, min_val):
                writer.write(str(value))
                for item in data_lst:
                    writer.write(" " + str(item))
                writer.write("\n")
    return


if __name__ == "__main__":
    file_path = sys.argv[1]
    generate_tests(file_path)

