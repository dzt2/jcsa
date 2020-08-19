#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>


/*------ definition part -------*/
typedef enum 
{
    Error   = -1,
	NewHash = 0,
	FreHash = 1,
	HashLen = 2,
	PutHash = 3,
	GetHash = 4,
	HasHash = 5,
	SetHash = 6,
	DelHash = 7,
	OutHash = 8
} OpType;
typedef struct 
{
	OpType type;
	char * key;
	long value;
} Operation;
static const char characters[] = "`1234567890-=~!@#$%^&*()_+qwertyuiop[]\\asdfghjkl;'zxcvbnm,./QWERTYUIOP{}|ASDFGHJKL:\"ZXCVBNM<>?\t\r\n ";
static const long MIN_VALUE = -1024, MAX_VALUE = 1024;
static const int MAX_STRING_LENGTH = 32;
static const int OPERATIONS_NUMBER = 16;
static Operation operations[OPERATIONS_NUMBER];
static const int MAX_SEQUENCE_LENGTH = 512, REPEAT_COUNT = 12;
static char file_name[1024];

/*------ random method part -------*/
static void initial_random()
{
    srand(time(NULL));
}
static char * __rand_string__(size_t length)
{
    char * string;
    int char_number;
    char_number = strlen(characters);
    string = (char *) malloc(sizeof(char) * (length + 1));
    for(int i = 0; i < length; i++)
    {
        string[i] = characters[abs(rand()) % char_number];
    }
    string[length] = '\0';
    return string;
}
static char * rand_string()
{
    int length;
    length = abs(rand()) % MAX_STRING_LENGTH;
    return __rand_string__(length);
}
static long rand_value()
{
    return MIN_VALUE + (abs(rand()) % (MAX_VALUE - MIN_VALUE));
}
static void reset_operations()
{
    for(int i = 0; i < OPERATIONS_NUMBER; i++)
    {
        operations[i].type = Error;
        if(operations[i].key != NULL)
        {
            free(operations[i].key);
        }
        operations[i].key = rand_string();
        operations[i].value = rand_value();
    }
}
static OpType rand_op_type()
{
    OpType type;
    do
    {
        type = -2 + abs(rand()) % 10;
    } while (type == NewHash || type == FreHash || type == OutHash);
    return type;
}
static Operation * rand_operation()
{
    OpType type; int index;
    type = rand_op_type();
    index = abs(rand()) % OPERATIONS_NUMBER;
    operations[index].type = type;
    return &(operations[index]);
}


/*------ test generation part -------*/
static void write_operation(FILE * fptr, Operation * operation)
{
    /* type value key_length key */
    int key_length = strlen(operation->key);
    fwrite(&(operation->type), sizeof(OpType), 1, fptr);
    fwrite(&(operation->value), sizeof(long), 1, fptr);
    fwrite(&(key_length), sizeof(int), 1, fptr);
    fwrite(operation->key, sizeof(char), key_length, fptr);
}
static void generate_input_file(const char * filepath, size_t seq_length)
{
    FILE * fptr;
    fptr = fopen(filepath, "wb");

    /* 1. initialize the random machine */
    initial_random();
    reset_operations();
    /* 2. set NewHash(?, ?) operation at first */
    operations[0].type = NewHash;
    write_operation(fptr, &(operations[0]));
    /* 3. set XXXHash(k, v) operation in medium */
    for(int i = 0; i < seq_length; i++)
    {
        Operation * operation = rand_operation();
        write_operation(fptr, operation);
    }
    /* 4. set OutHash(?, ?) and FreHash(?, ?) at last */
    operations[0].type = OutHash;
    write_operation(fptr, &(operations[0]));
    operations[0].type = FreHash;
    write_operation(fptr, &(operations[0]));

    fclose(fptr);
    printf("\tGenerate input file: %s\n", filepath);
}
static char * get_file_name(const char * directory, int tid)
{
    sprintf(file_name, "%s/%d", directory, tid);
    return file_name;
}
static int generate_input_files(const char * directory)
{
    int tid = 0;
    generate_input_file(get_file_name(directory, tid++), 0);
    for(int seq_length = 1; seq_length <= MAX_SEQUENCE_LENGTH; seq_length++)
    {
        for(int i = 0; i < REPEAT_COUNT; i++)
        {
            generate_input_file(get_file_name(directory, tid++), seq_length);
        }
    }
    return tid;
}
static void generate_suite_file(const char * suite_file, int test_number)
{
    FILE * fptr;
    fptr = fopen(suite_file, "w");
    for(int tid = 0; tid < test_number; tid++)
    {
        fprintf(fptr, "../inputs/%d\n", tid);
    }
    for(int i = 0; i < 16; i++)
    {
        for(int j = 0; j < i; j++)
        {
            fprintf(fptr, "non_file ");
        }
        fprintf(fptr, "\n");
    }
    fclose(fptr);
    return;
}


/*------ main function part -------*/
int main(int argc, char * argv[])
{
    if(argc == 3)
    {
        int test_number;
        test_number = generate_input_files(argv[1]);
        generate_suite_file(argv[2], test_number);
        return 0;
    }
    else
    {
        fprintf(stderr, "Usage: a.out directory suite_file");
        return 1;
    }
}

