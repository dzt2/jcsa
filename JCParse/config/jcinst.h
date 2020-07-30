extern int fclose(FILE * __stream);
extern int fflush(FILE * __stream);
extern FILE * fopen(const char * __filename, const char * __modes);
extern int fprintf(FILE * __stream, const char * __format, ...);


static FILE * jcm_writer;

static void jcm_close() {
	if(jcm_writer != (void *) 0) {
		fclose(jcm_writer);
		jcm_writer = (void *) 0;
	}
}

static void jcm_open(char * filepath) {
	jcm_close();
	jcm_writer = fopen(filepath, "w");
}

static void jcm_print(char value) {
	int int_value = value;
	fprintf(jcm_writer, " %d", int_value);
}

static void jcm_append(int id, void * address, unsigned int length) {
	if(jcm_writer != (void *) 0) {
		fprintf(jcm_writer, "%d", id);
		char * char_address = (char *) address;
		for(int k = 0; k < length; k++) {
			jcm_print(char_address[k]);
		}
		fprintf(jcm_writer, "\n");
		fflush(jcm_writer);
	}
}

#define jcm_sta(x)	jcm_open(x)

#define jcm_add(x, y)	({typeof(y) __result__ = (y); jcm_append((x), &__result__, sizeof(__result__)); __result__;})

#define jcm_prev(x)	({char __tag__ = '\0'; jcm_append((x), &__tag__, sizeof(__tag__)); __tag__;})

#define jcm_post(x)	({char __tag__ = '\1'; jcm_append((x), &__tag__, sizeof(__tag__)); __tag__;})

