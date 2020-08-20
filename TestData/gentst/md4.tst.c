#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <time.h>
#include <ctype.h>


#define MAX_STR_LENGTH (1024 * 1024 * 32)
static const char character_set[] = "`1234567890-=qwertyuiop[]\asdfghjkl;\'zxcvbnm,./ ~!@#$%^&*()_+|\\{}:\"<>?.\r\n\t";
static char buffer[MAX_STR_LENGTH];


void init_random()
{
    srand(time(NULL));
}


void rand_key(int length)
{
    int len = strlen(character_set);
    for(int k = 0; k < length; k++)
    {
        buffer[k] = character_set[abs(rand()) % len];
        if(!isalpha(buffer[k]) && !isdigit(buffer[k]) && buffer[k] != '_')
        {
            k--;
        }
    }
    buffer[length] = '\0';
}


void rand_txt(int length)
{
    int len = strlen(character_set);
    for(int k = 0; k < length; k++)
    {
        buffer[k] = character_set[abs(rand()) % len];
    }
    buffer[length] = '\0';
}


void rand_bin(int length)
{
    for(int k = 0; k < length; k++)
    {
        buffer[k] = (char) abs(rand());
    }
    buffer[length] = 0;
}


void rand_key_file(char *file, int length)
{
    FILE * fp;
    fp = fopen(file, "w");
    rand_key(length);
    fprintf(fp, "%s", buffer);
    fclose(fp);
}


void rand_txt_file(char *file, int length)
{
    FILE * fp;
    fp = fopen(file, "w");
    rand_txt(length);
    fprintf(fp, "%s", buffer);
    fclose(fp);
}


void rand_bin_file(char *file, int length)
{
    FILE * fp;
    fp = fopen(file, "wb");
    rand_bin(length);
    fwrite(buffer, sizeof(char), length, fp);
    fclose(fp);
}


static char file_name[1024];


char * get_file_name(char * prefix, int id)
{
    sprintf(file_name, "%s/%d", prefix, id);
    return file_name;
}


int generate_files(char * prefix)
{
    int id = 0; char * file_name;

    for(int length = 0; length < 1024; length++)
    {
        file_name = get_file_name(prefix, id++);
        switch(length % 3)
        {
            case 0:
            {
                rand_key_file(file_name, length);
                break;
            }
            case 1:
            {
                rand_txt_file(file_name, length);
                break;
            }
            default:
            {
                rand_bin_file(file_name, length);
                break;
            }
        }
        printf("\t==> Generate %d test input file.\n", id);
    }

    for(int length = 1024; length < 1024 * 16; length += 16)
    {
        file_name = get_file_name(prefix, id++);
        switch(abs(rand()) % 3)
        {
            case 0:
            {
                rand_key_file(file_name, length);
                break;
            }
            case 1:
            {
                rand_txt_file(file_name, length);
                break;
            }
            default:
            {
                rand_bin_file(file_name, length);
                break;
            }
        }
        printf("\t==> Generate %d test input file.\n", id);
    }

    for(int length = 16 * 1024; length < 1024 * 1024; length += 512)
    {
        file_name = get_file_name(prefix, id++);
        switch(abs(rand()) % 3)
        {
            case 0:
            {
                rand_key_file(file_name, length);
                break;
            }
            case 1:
            {
                rand_txt_file(file_name, length);
                break;
            }
            default:
            {
                rand_bin_file(file_name, length);
                break;
            }
        }
        printf("\t==> Generate %d test input file.\n", id);
    }

    return id;
}


static char command[1024 * 1024];


char * append_file(char * command, char * prefix, int fid)
{
    return command + sprintf(command, " %s", get_file_name(prefix, fid));
}


char * append_text(char *command, int length)
{
    rand_key(length);
    return command + sprintf(command, " %s", buffer);
}


void write_command(FILE * fp, int number, char *prefix, int files)
{
    char * cptr = command;
    for(int k = 0; k < number; k++)
    {
        if(abs(rand()) % 6 == 0)
        {
            cptr = append_text(cptr, abs(rand()) % 32 + 1);
        }
        else
        {
            cptr = append_file(cptr, prefix, abs(rand()) % files);
        }
    }
    *cptr = '\0';
    fprintf(fp, "%s\n", command);
}


int main(int argc, char *argv[])
{
    if(argc == 3)
    {
        int files = generate_files(argv[1]);
        FILE * fp = fopen(argv[2], "w");
        write_command(fp, 0, argv[1], files);
        for(int number = 1; number < 16; number++)
        {
            for(int i = 0; i < 512; i++)
            {
                write_command(fp, number, argv[1], files);
            }
        }
        fclose(fp);
        return 0;
    }
    else
    {
        fprintf(stderr, "Invalid usage: inputs_directory + test_suite_file\n");
        return 1;
    }
    
}

