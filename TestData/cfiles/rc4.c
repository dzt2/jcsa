/***************************************************************************
 * The program rc4 receives a text-key and text-file-name as inputs, and
 * computes the RC4-cipher-text as outputs.
 *      ./a.exe     key     text_file_path
 **************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/* type for RC4-Key-Object  */
typedef struct RC4_KEY_S {
    unsigned char S[256];
} RC4_KEY;

/*S is initialized to the identity permutation, mixes in bytes of the key.*/
void RC4_key(RC4_KEY *rc4_key, unsigned char *key, int *keylength);

/*Encrypt the plaintext and output the ciphertext.*/
void RC4(RC4_KEY *rc4_key, unsigned char *plaintext, int *plaintext_length, unsigned char *ciphertext);

/*S is initialized to the identity permutation, mixes in bytes of the key.*/
void RC4_key(RC4_KEY *rc4_key, unsigned char *key, int *keylength)
{
    int i, j, temp;

    /*Initialize S*/
    for (i = 0; i < 256; i++)
	rc4_key -> S[i] = i;

    j = 0;
    for (i = 0; i < 256; i++)
    {
        j = (j + rc4_key -> S[i] + *(key + i % *keylength)) % 256;
        /*Swap rc4_key -> S[i] and rc4_key -> S[j]*/
        temp = rc4_key -> S[i];
        rc4_key -> S[i] = rc4_key -> S[j];
        rc4_key -> S[j] = temp;
    }
}

/*Generate the key stream which length is the same as plaintext's and encrypt the plaintext and output the ciphertext.*/
void RC4(RC4_KEY *rc4_key, unsigned char *plaintext, int *plaintext_length, unsigned char *ciphertext)
{
    int i = 0, j = 0, n, temp, k;

    for (k = 0; k < *plaintext_length; k++)
    {
        i = (k + 1) % 256;
        j = (j + rc4_key -> S[i]) % 256;
        
        /*Swap rc4_key -> S[i] and rc4_key -> S[j]*/
        temp = rc4_key -> S[i];
        rc4_key -> S[i] = rc4_key -> S[j];
        rc4_key -> S[j] = temp;

        n = rc4_key -> S[(rc4_key -> S[i] + rc4_key -> S[j]) % 256];

        /*Encryption*/
        *(ciphertext + k) = *(plaintext + k) ^ n;
    }
}


/* testing part */
#define MAX_KEY_SIZE (1024)
#define MAX_STR_SIZE (1024 * 1024 * 64)
static unsigned char ckey[MAX_KEY_SIZE];
static unsigned char ctxt[MAX_STR_SIZE];
static unsigned char rtxt[MAX_STR_SIZE];


static void read_file(char * file_path, unsigned char *buffer, unsigned int max_length)
{
	FILE * fp; 
	fp = fopen(file_path, "rb");
	fread(buffer, sizeof(unsigned char), max_length - 1, fp);
	buffer[max_length] = '\0';
	fclose(fp);
	return;
}


static void print_text(unsigned char *text, size_t length)
{
	size_t i;
	for(i = 0; i < length; i++)
	{
		fprintf(stdout, "%x", text[i]);
	}
	fprintf(stdout, "\n");
	return;
}


int main(int argc, char * argv[])
{
	if(argc > 2)
	{
		/* declarations */
		RC4_KEY key; int key_length, txt_length;
		
		/* obtain key and text for testing */
		read_file(argv[1], ckey, MAX_KEY_SIZE - 1);
		read_file(argv[2], ctxt, MAX_STR_SIZE - 1);
		key_length = strlen((char *) ckey);
		txt_length = strlen((char *) ctxt);
		
		/* obtain the key instance from text */
		RC4_key(&key, ckey, &key_length);
		
		/* perform RC4 algorithm to encode text */
		RC4(&key, ctxt, &txt_length, rtxt);
		rtxt[txt_length] = '\0';
		
		/* output the result text to stdout */
		print_text(rtxt, txt_length);
		return 0;
	}
	else
	{
		fprintf(stderr, "Usage: a.out key_file text_file\n");
		return 1;
	}
}


