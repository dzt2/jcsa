import os
import sys
import random


def range_list(writer, min_val, max_val):
    for value in range(min_val, max_val):
        writer.write(str(value) + " ")
    writer.write("\n")


if __name__ == "__main__":
    beg_value, end_value, counter = -128, 36000, 0
    with open(sys.argv[1], "w") as writer:
        while beg_value <= end_value:
            length = random.randint(0, 32)
            range_list(writer, beg_value, beg_value + length)
            beg_value += length
            counter += 1
    print("Generate", counter, "tests.")

