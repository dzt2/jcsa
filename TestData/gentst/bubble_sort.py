import os
import sys
import random


def rand_list(min_val: int, max_val: int, length: int):
    """[summary]
    It generates a list of random integers with specified number
    Args:
        min_val (int): the minimal possible value in the list
        max_val (int): the maximal possible value in the list
        length (int): the length of the generated random list
    """
    data_lst = list()
    for k in range(0, length):
        value = random.randint(min_val, max_val)
        data_lst.append(value)
    return data_lst


def sort_list(data_lst: list, reverse: bool):
    """[summary]
    It generates a sorted list of random integers with specified number
    Args:
        data_lst (list): [description]
    """
    new_lst = list()
    for value in data_lst:
        new_lst.append(value)
    new_lst.sort(reverse=reverse)
    return new_lst

def write_test(data_lst, writer):
    for value in data_lst:
        writer.write(" " + str(value))
    writer.write("\n")


def generate_tests(file_path: str):
    counter = 0
    with open(file_path, "w") as writer:
        writer.write("\n")
        counter += 1
        for length in range(1, 256):
            for k in range(-8, 8):
                write_test(rand_list(-8, 8, length), writer)
                counter += 1
            write_test(sort_list(rand_list(-8, 8, length), True), writer)
            write_test(sort_list(rand_list(-8, 8, length), False), writer)
            counter += 2
    return counter


if __name__ == "__main__":
    print("Generate", generate_tests(sys.argv[1]), "tests.")
