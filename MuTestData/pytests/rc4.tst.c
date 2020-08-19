#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#define MAX_KEY_SIZE (1024)
#define MAX_STR_SIZE (1024 * 1024)
static char ckey_file_name[256];
static char ctxt_file_name[256];
static unsigned char ckey[MAX_KEY_SIZE];
static unsigned char ctxt[MAX_STR_SIZE];

static void init_random()
{
    srand(time(NULL));
}

static void rand_string(unsigned char * buffer, int length)
{
    for(int k = 0; k < length; k++)
    {
        buffer[k] = (char) rand();
    }
    buffer[length] = '\0';
}

static void generate_test(char *prefix, int id, int key_length, int txt_length)
{
    sprintf(ckey_file_name, "%s/%d.key", prefix, id);
    sprintf(ctxt_file_name, "%s/%d.txt", prefix, id);
    FILE * key_fp = fopen(ckey_file_name, "wb");
    FILE * txt_fp = fopen(ctxt_file_name, "wb");
    rand_string(ckey, key_length);
    rand_string(ctxt, txt_length);
    fwrite(ckey, sizeof(char), key_length, key_fp);
    fwrite(ctxt, sizeof(char), txt_length, txt_fp);
    fclose(key_fp); fclose(txt_fp);
}

int main(int argc, char *argv[])
{
    if(argc == 3)
    {
        int tid = 0;
        FILE * suite_fp = fopen(argv[2], "w");
        fprintf(suite_fp, "\n");
        for(int key_length = 0; key_length < MAX_KEY_SIZE; key_length++)
        {
            for(int j = 0; j < 4; j++)
            {
                int txt_length = abs(rand()) % MAX_STR_SIZE;
                fprintf(suite_fp, "../inputs/%d.key ../inputs/%d.txt\n", tid, tid);
                generate_test(argv[1], tid, key_length, txt_length);
                printf("\t==> Generate %d test case.\n", tid);
                tid++;
            }
        }
        fprintf(suite_fp, "non_file\n");
        fprintf(suite_fp, "../inputs/1.key ../inputs/2.txt ../inputs.3\n");
        fprintf(suite_fp, "a b cc d s\n");
        printf("Generate %d tests.\n", tid);
        fclose(suite_fp);
    }
    return 0;
}

