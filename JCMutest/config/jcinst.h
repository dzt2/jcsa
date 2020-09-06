static FILE * jcm_writer;
extern int fclose (FILE *__stream);
extern int fflush (FILE *__stream);
extern FILE *fopen (const char *__restrict __filename, const char *__restrict __modes);
extern size_t fwrite (const void *__restrict __ptr, size_t __size, size_t __n, FILE *__restrict __s);


static void jcm_close() {
	if(jcm_writer != (void *) 0) {
		fclose(jcm_writer);
		jcm_writer = (FILE *) 0;
	}
}

static void jcm_open(char * filepath) {
	jcm_close();
	jcm_writer = fopen(filepath, "wb");
}

static void jcm_print(char value) {
	fwrite(&value, sizeof(char), 1, jcm_writer);
}

static void jcm_append(int id, void * address, int length) {
	if(jcm_writer != (void *) 0) {
		fwrite(&id, sizeof(int), 1, jcm_writer);
		fwrite(&length, sizeof(int), 1, jcm_writer);
		char * char_address = (char *) address;
		for(int k = 0; k < length; k++) {
			jcm_print(char_address[k]);
		}
		fflush(jcm_writer);
	}
}

#define jcm_sta(x)	jcm_open(x)

#define jcm_add(x, y)	({typeof(y) __result__ = (y); jcm_append((x), &__result__, sizeof(__result__)); __result__;})

#define jcm_prev(x)	({char __tag__ = '\0'; jcm_append((x), &__tag__, sizeof(__tag__)); __tag__;})

#define jcm_post(x)	({char __tag__ = '\1'; jcm_append((x), &__tag__, sizeof(__tag__)); __tag__;})

