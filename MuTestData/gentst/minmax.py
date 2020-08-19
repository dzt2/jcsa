import os
import sys
import random


def rand_list(min_val, max_val, length):
    lst = list()
    for k in range(0, length):
        lst.append(random.randint(min_val, max_val))
    return lst


def sort_list(min_val, max_val, length, reversed):
    lst = rand_list(min_val, max_val, length)
    lst.sort(reverse=reversed)
    return lst


def write_list(writer, lst):
    for value in lst:
        writer.write(str(value) + " ")
    writer.write("\n")


if __name__ == "__main__":
    with open(sys.argv[1], "w") as writer:
        writer.write("\n")
        for length in range(1, 300):
            for k in range(0, 16):
                write_list(writer, rand_list(-16, 16, length))
            write_list(writer, sort_list(-16, 16, length, True))
            write_list(writer, sort_list(-16, 16, length, False))

