import os
import sys
import random


def uniform_range(writer, min_val, max_val, length):
    for k in range(0, length):
        writer.write(str(random.randint(min_val, max_val)) + "\n")
    return


def close_range(writer, value, distance):
    for k in range(value - distance, value + distance):
        writer.write(str(k) + "\n")
    return


def random_list(writer, min_val, max_val, length):
    for k in range(0, length):
        writer.write(str(random.randint(min_val, max_val)) + " ")
    writer.write("\n")
    return


if __name__ == "__main__":
    with open(sys.argv[1], "w") as writer:
        uniform_range(writer, -100000, 0, 1000)
        uniform_range(writer, 0, 100000, 1000)
        uniform_range(writer, 100000, 200000, 1000)
        uniform_range(writer, 200000, 400000, 1000)
        uniform_range(writer, 400000, 600000, 1000)
        uniform_range(writer, 600000, 800000, 1000)
        uniform_range(writer, 800000, 1000000, 1000)
        uniform_range(writer, 1000000, 1200000, 1000)
        close_range(writer, 0, 100)
        close_range(writer, 100000, 100)
        close_range(writer, 200000, 100)
        close_range(writer, 400000, 100)
        close_range(writer, 600000, 100)
        close_range(writer, 800000, 100)
        close_range(writer, 1000000, 100)
        for length in range(0, 128):
            random_list(writer, 0, 1000000, length)

