#include <stdio.h>
#include <stdlib.h>


static FILE * jcm_writer;

static void jcm_close() {
	if(jcm_writer != NULL) {
		fclose(jcm_writer);
		jcm_writer = NULL;
	}
}

static void jcm_open(char * filepath) {
	jcm_close();
	jcm_writer = fopen(filepath, "w");
}

static void jcm_append(int id, void * address, unsigned int length) {
	if(jcm_writer != NULL) {
		fprintf(jcm_writer, "%d", id);
		unsigned char * char_address = (unsigned char *) address;
		for(int k = 0; k < length; k++) {
			fprintf(jcm_writer, " %d", char_address[k]);
		}
		fprintf(jcm_writer, "\n");
		fflush(jcm_writer);
	}
}

#define jcm_sta(x)	jcm_open(x)

#define jcm_add(x, y)	({typeof(y) __result__ = (y); jcm_append((x), &__result__, sizeof(__result__)); __result__;})

#define jcm_prev(x)	({char __tag__ = '\0'; jcm_append((x), &__tag__, sizeof(__tag__)); __tag__;})

#define jcm_post(x)	({char __tag__ = '\1'; jcm_append((x), &__tag__, sizeof(__tag__)); __tag__;})

