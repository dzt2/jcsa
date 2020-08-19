/* header files included */
#include <stdio.h>
#include <ctype.h>
#include <stdlib.h>
#include <string.h>


/* ***************************************************************
    File name : stream.h
    PURPOSE   : This is the header file for stream.c . This inlcudes
                the type definitions which are to be exported to the
                other routines.
 * **************************************************************** */
typedef struct stream_type {
    FILE *fp;  /* File pointer to stream */
    int  stream_ind; /* buffer index */
    char stream[80];  /* buffer for the file*/
} * character_stream;
typedef char CHARACTER;
character_stream open_character_stream();
CHARACTER get_char();
int is_end_of_character_stream();


/* *******************************************************************
    File name : tokens.h
    Purpose   : This is the header file for the tokenizer.c It 
                contains the type definitions to be exported from
                the tokenizer.c.
 * ******************************************************************* */
# define TRUE               1
# define FALSE              0
# define EOTSTREAM          0
# define NUMERIC            18
# define IDENTIFIER         17
# define LAMBDA             6
# define AND                9
# define OR                 11
# define IF                 13
# define XOR                16
# define LPAREN             19
# define RPAREN             20
# define LSQUARE            21
# define RSQUARE            22
# define QUOTE              23
# define BQUOTE             24
# define COMMA              25
# define EQUALGREATER       32
# define STRING_CONSTANT    27
# define CHARACTER_CONSTANT 29
# define ERROR             -1
typedef struct token_stream_type {
    character_stream ch_stream;
} * token_stream;
typedef struct token_type {
    int token_id;
    char token_string[80];
} * token;
token_stream open_token_stream();
token get_token();
int compare_token();
int is_eof_token();
int default1[]={
    54, 17, 17, 17, 17, 17, 17, 17, 17, 17,
    17, 17, 17, 17, 17, 17, 17, 51, -2, -1,
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    -1, -1, -1, -1, -1 ,-1, -1, -1, -1, -1,
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    -1, 52, -3, -1 ,-1, -1, -1, -1, -1, -1
};
int base[]   ={
    -32, -96,-105, -93, -94, -87, -1,  -97, -86, -1,
    -99, -1,  -72, -1,  -80, -82, -1,   53,  43, -1,
    -1,  -1,  -1,  -1,  -1,  -1,  133, -1,  233, -1,
    -1,  0,   -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,
    -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,
    -1,  46,  40,  -1, 251,  -1,  -1,  -1,  -1,  -1
};
int next[] = {
    0,  2, 26, 28,  3,  4,  5, 23, 19, 20,
    6, -1, 25,  8,  9, 11, 18, 18, 18, 18,
    18, 18, 18, 18, 18, 18, -1, 30, -1, 31,
    13, 15, 16, 17, 17, 17, 17, 17, 17, 17,
    17, 17, 17, 17, 17, 17, 17, 17, 17, 17,
    17, 17, 17, 17, 17, 17, 17, 17, 17, 21,
    -1, 22, 32, -1, 24,  7, 17, 17, 17, 17,
    17, 17, 17, 12, 17, 17,  1, 17, 17, 10,
    17, 17, 17, 17, 17, 17, 17, 17, 14, 17,
    17, 18, 18, 18, 18, 18, 18, 18, 18, 18,
    18, 17, 17, 17, 17, 17, 17, 17, 17, 17,
    17, 17, 17, 17, 17, 17, 17, 17, 17, 17,
    17, 17, 17, 17, 17, 17, 17, 17, 17, 17,
    17, 17, 17, 17, 17, 17, 17, 17, 17, 17,
    17, 17, 17, 17, 17, 17, 17, 17, 17, 17,
    17, 17, 17, 17, 17, 17, 17, 17, 17, 17,
    17, 17, 17, -1, -1, 26, 26, 27, 26, 26,
    26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
    26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
    26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
    26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
    26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
    26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
    26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
    26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
    26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
    0,  0, -1, -1, -1, 29, 29, 29, 29, 29,
    29, 29, 29, 29, 29, 29, 29, 29, 29, 29,
    29, 29, 29, 29, 29, 29, 29, 29, 29, 29,
    29, 29, 29, 29, 29, 29, 29, 29, 29, 29,
    29, 29, 29, 29, 29, 29, 29, 29, 29, 29,
    29, 29, 29, 29, 29, 29, 29, 29, 29, 29,
    29, 29, 29, 29, 29, 29, 29, 29, 29, 29,
    29, 29, 29, 29, 29, 29, 29, 29, 29, 29,
    29, 29, 29, 29, 29, 29, 29, 29, 29, 29,
    29, 29, 29, 29, 29, 29, 29, 29, 29, 29
};
int check[] = {   
    0,  1,  0,  0,  2,  3,  4,  0,  0,  0,
    5, -1,  0,  7,  8, 10,  0,  0,  0,  0,
    0,  0,  0,  0,  0,  0, -1,  0, -1,  0,
    12, 14, 15,  0,  0,  0,  0,  0,  0,  0,
    0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
    0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
    -1,  0, 31, -1,  0,  0,  0,  0,  0,  0,
    0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
    0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
    0, 18, 18, 18, 18, 18, 18, 18, 18, 18,
    18, 17, 17, 17, 17, 17, 17, 17, 17, 17,
    17, 51, 51, 51, 51, 51, 51, 51, 51, 51,
    51, 51, 51, 51, 51, 51, 51, 51, 51, 51,
    51, 51, 51, 51, 51, 51, 51, 52, 52, 52,
    52, 52, 52, 52, 52, 52, 52, 52, 52, 52,
    52, 52, 52, 52, 52, 52, 52, 52, 52, 52,
    52, 52, 52, -1, -1, 26, 26, 26, 26, 26,
    26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
    26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
    26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
    26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
    26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
    26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
    26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
    26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
    26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
    54, 54, -1, -1, -1, 28, 28, 28, 28, 28,
    28, 28, 28, 28, 28, 28, 28, 28, 28, 28,
    28, 28, 28, 28, 28, 28, 28, 28, 28, 28,
    28, 28, 28, 28, 28, 28, 28, 28, 28, 28,
    28, 28, 28, 28, 28, 28, 28, 28, 28, 28,
    28, 28, 28, 28, 28, 28, 28, 28, 28, 28,
    28, 28, 28, 28, 28, 28, 28, 28, 28, 28,
    28, 28, 28, 28, 28, 28, 28, 28, 28, 28,
    28, 28, 28, 28, 28, 28, 28, 28, 28, 28,
    28, 28, 28, 28, 28, 28, 28, 28, 28, 28
};
# define START  5


/* print_tokens.c */
static token numeric_case(token_stream tstream_ptr, token token_ptr, 
                            char ch,char token_str[],int token_ind);
static token error_or_eof_case(token_stream tstream_ptr, token token_ptr, 
                int cu_state, char token_str[], int token_ind, char ch);
static int check_delimiter(char);
static int keyword(int state);
static int special(int state);
static void skip(character_stream stream_ptr);
static int constant(int state,char token_str[],int token_ind);
static int next_state(int state, char ch);
static void get_actual_token(char token_str[],int token_ind);

/* *********************************************************************
       Function name : open_character_stream
       Input         : filename 
       Output        : charactre stream.
       Exceptions    : If file name doesn't exists it will
                       exit from the program.
       Description   : The function first allocates the memory for 
                       the structure and initilizes it. The constant
                       START gives the first character available in
                       the stream. It ckecks whether the filename is
                       empty string. If it is it assigns file pointer
                       to stdin else it opens the respective file as input.                   
* ******************************************************************* */
character_stream open_character_stream(char * FILENAME)
{
    character_stream stream_ptr;
    stream_ptr=(character_stream)malloc(sizeof(struct stream_type));
    stream_ptr->stream_ind=START;
    stream_ptr->stream[START]='\0';
    if(FILENAME == NULL)
        stream_ptr->fp=stdin;
    else if((stream_ptr->fp=fopen(FILENAME,"r"))==NULL)
    {
        fprintf(stdout, "The file %s doesn't exists\n",FILENAME);
        exit(0);
    }
    return stream_ptr;
}

/* *********************************************************************
   Function name : get_char
   Input         : charcter_stream.
   Output        : character.
   Exceptions    : None.
   Description   : This function takes character_stream type variable 
                   as input and returns one character. If the stream is
                   empty then it reads the next line from the file and
                   returns the character.       
 * ****************************************************************** */
char get_char(character_stream stream_ptr)
{
    if(stream_ptr->stream[stream_ptr->stream_ind] == '\0')
    {
        if(fgets(stream_ptr->stream+START,80-START,stream_ptr->fp) == NULL)
        {
            /* Fix bug: add -START - hf*/
            stream_ptr->stream[START]=EOF;
        }
        stream_ptr->stream_ind=START;
    }
    return stream_ptr->stream[(stream_ptr->stream_ind)++];
}

/* *******************************************************************
   Function name : is_end_of_character_stream.
   Input         : character_stream.
   Output        : Boolean value.
   Description   : This function checks whether it is end of character
                   stream or not. It returns BOOLEANvariable which is 
                   true or false. The function checks whether the last 
                   read character is end file character or not and
                   returns the value according to it.
 * ****************************************************************** */
int is_end_of_character_stream(character_stream stream_ptr)
{
    return stream_ptr->stream[stream_ptr->stream_ind-1] == EOF;
}

/* *********************************************************************
   Function name : unget_char
   Input         : character,character_stream.
   Output        : void.
   Description   : This function adds the character ch to the stream. 
                   This is accomplished by decrementing the stream_ind
                   and storing it in the stream. If it is not possible
                   to unget the character then it returns
 * ******************************************************************* */
void unget_char(CHARACTER ch, character_stream stream_ptr)
{
    if(stream_ptr->stream_ind != 0) 
    {
        stream_ptr->stream[--(stream_ptr->stream_ind)] = ch;
    }
}


/* *******************************************************************
   Function name : open_token_stream
   Input         : filename
   Output        : token_stream
   Exceptions    : Exits if the file specified by filename not found.
   Description   : This function takes filename as input and opens the
                   token_stream which is nothing but the character stream.
                   This function allocates the memory for token_stream 
                   and calls open_character_stream to open the file as
                   input. This function returns the token_stream.
 * ****************************************************************** */
token_stream open_token_stream(char * FILENAME)
{
    token_stream token_ptr;
    token_ptr=(token_stream)malloc(sizeof(struct token_stream_type));
    token_ptr->ch_stream=open_character_stream(FILENAME);
    return(token_ptr);
}

/* ********************************************************************
   Function name : get_token
   Input         : token_stream
   Output        : token
   Exceptions    : none.
   Description   : This function returns the next token from the
                   token_stream.The type of token is integer and specifies 
                   only the type of the token. DFA is used for finding the
                   next token. cu_state is initialized to zero and charcter
                   are read until the the is the final state and it
                   returns the token type.
* ******************************************************************* */
token get_token(token_stream tstream_ptr)
{
    char token_str[80]; /* This buffer stores the current token */
    int token_ind;      /* Index to the token_str  */
    token token_ptr;
    CHARACTER ch;
    int cu_state, next_st, token_found;

    token_ptr = (token)(malloc(sizeof(struct token_type)));
    ch = get_char(tstream_ptr->ch_stream);
    cu_state=token_ind=token_found=0;
    while(!token_found)
    {
        if(token_ind < 80) /* ADDED ERROR CHECK - hf */
	    {
	        token_str[token_ind++]=ch;
            next_st=next_state(cu_state,ch);
	    }
	    else
	    {
            next_st = -1; /* - hf */
        }
	    if (next_st == -1) 
        { 
            /* ERROR or EOF case */
	        return error_or_eof_case(tstream_ptr, token_ptr, 
                        cu_state, token_str, token_ind, ch);
	    } 
        else if (next_st == -2) 
        {
            /* This is numeric case. */
	        return numeric_case(tstream_ptr, token_ptr, ch, token_str,token_ind);
	    } 
        else if (next_st == -3) 
        {
            /* This is the IDENTIFIER case */
	        token_ptr->token_id=IDENTIFIER;
	        unget_char(ch,tstream_ptr->ch_stream);
	        token_ind--;
	        get_actual_token(token_str,token_ind);
	        strcpy(token_ptr->token_string,token_str);
	        return token_ptr;
	    } 
	    
	    switch(next_st) 
        {
            case 6  : /* These are all KEYWORD cases. */
            case 9  :
            case 11 :
            case 13 :
            case 16 :
                ch=get_char(tstream_ptr->ch_stream);
                if(check_delimiter(ch)==TRUE)
                {
                    token_ptr->token_id=keyword(next_st);
                    unget_char(ch,tstream_ptr->ch_stream);
                    token_ptr->token_string[0]='\0';
                    return token_ptr;
                }
                unget_char(ch,tstream_ptr->ch_stream);
                break;
            case 19 : /* These are all special SPECIAL character */
            case 20 : /* cases */
            case 21 :
            case 22 :
            case 23 :
            case 24 :
            case 25 :
            case 32 :
                token_ptr->token_id=special(next_st);
                token_ptr->token_string[0]='\0';
                return(token_ptr);
            case 27 : /* These are constant cases */
            case 29 : 
                token_ptr->token_id=constant(next_st,token_str,token_ind);
                get_actual_token(token_str,token_ind);
                strcpy(token_ptr->token_string,token_str);
                return(token_ptr);
            case 30 :  
                /* This is COMMENT case */
                skip(tstream_ptr->ch_stream);
                token_ind=next_st=0;
                break;
            default : break;
        }
        cu_state=next_st;
        ch=get_char(tstream_ptr->ch_stream);
    }
    return token_ptr;
}

/* ******************************************************************
   Function name : numeric_case
   Input         : tstream_ptr,token_ptr,ch,token_str,token_ind
   Output        : token_ptr;
   Exceptions    : none 
   Description   : It checks for the delimiter, if it is then it
                   forms numeric token else forms error token.
 * ****************************************************************** */
static token numeric_case(token_stream tstream_ptr, token token_ptr, char ch,char token_str[],int token_ind)
{
    if(check_delimiter(ch)!=TRUE)
    {   /* Error case */
        token_ptr->token_id=ERROR;
        while(check_delimiter(ch)==FALSE)
	    {
		    if(token_ind >= 80) 
            {
                break; /* Added protection - hf */
            }
            ch=get_char(tstream_ptr->ch_stream);
		    token_str[token_ind++] = ch;
	    }
        unget_char(ch,tstream_ptr->ch_stream);
        token_ind--;
        get_actual_token(token_str,token_ind);
        strcpy(token_ptr->token_string,token_str);
        return(token_ptr);
    }
    token_ptr->token_id=NUMERIC; /* Numeric case */
    unget_char(ch,tstream_ptr->ch_stream);
    token_ind--;
    get_actual_token(token_str,token_ind);
    strcpy(token_ptr->token_string,token_str);
    return(token_ptr);
}

/* *****************************************************************
   Function name : error_or_eof_case 
   Input         : tstream_ptr,token_ptr,cu_state,token_str,token_ind,ch
   Output        : token_ptr 
   Exceptions    : none 
   Description   : This function checks whether it is EOF or not.
                   If it is it returns EOF token else returns ERROR 
                   token.
 * *****************************************************************/
static token error_or_eof_case(token_stream tstream_ptr, token token_ptr, 
                int cu_state, char token_str[], int token_ind, char ch)
{
    if(is_end_of_character_stream(tstream_ptr->ch_stream)) 
    {
        token_ptr->token_id = EOTSTREAM;
        token_ptr->token_string[0]='\0';
        return(token_ptr);
    }
    if(cu_state !=0)
    {
        unget_char(ch,tstream_ptr->ch_stream);
        token_ind--;
    }
    token_ptr->token_id=ERROR;
    get_actual_token(token_str,token_ind);
    strcpy(token_ptr->token_string,token_str);
    return(token_ptr);             
}

/* *********************************************************************
   Function name : check_delimiter
   Input         : character
   Output        : boolean
   Exceptions    : none.
   Description   : This function checks for the delimiter. If ch is not
                   alphabet and non numeric then it returns TRUE else 
                   it returns FALSE. 
 * ******************************************************************* */
static int check_delimiter(char ch)
{
    return !isalpha(ch) && !isdigit(ch);
}

/* ********************************************************************
   Function name : keyword
   Input         : state of the DFA
   Output        : Keyword.
   Exceptions    : If the state doesn't represent a keyword it exits.
   Description   : According to the final state specified by state the
                   respective token_id is returned.
 * ***************************************************************** */
static int keyword(int state)
{
    switch(state)
    {   /* Return the respective macro for the Keyword. */
        case 6 : return(LAMBDA);
        case 9 : return(AND);
        case 11: return(OR);
        case 13: return(IF);
        case 16: return(XOR);
        default: 
            fprintf(stdout, "error\n");
            break;
    }
    exit(0);
}

/* ********************************************************************
   Function name : special
   Input         : The state of the DFA.
   Output        : special symbol.
   Exceptions    : if the state doesn't belong to a special character
                   it exits.
   Description   : This function returns the token_id according to the
                   final state given by state.
 * ****************************************************************** */
static int special(int state)
{
    switch(state)
    {   /* return the respective macro for the special character. */
        case 19: return LPAREN;
        case 20: return RPAREN;
        case 21: return LSQUARE;
        case 22: return RSQUARE;
        case 23: return QUOTE;
        case 24: return BQUOTE;
        case 25: return COMMA;
        case 32: return EQUALGREATER;
        default: 
            fprintf(stdout, "error\n");
            break;
    }
    exit(0);
}

/* **********************************************************************
   Function name : skip
   Input         : character_stream
   Output        : void.
   Exceptions    : none.
   Description   : This function skips the comment part of the program.
                   It takes charcter_stream as input and reads character
                   until it finds new line character or
                   end_of_character_stream.                   
 * ******************************************************************* */
static void skip(character_stream stream_ptr)
{
    char c;
    while((c=get_char(stream_ptr))!='\n' && 
            !is_end_of_character_stream(stream_ptr))
    {
        /* Skip the characters until EOF or EOL found. */
    }
	if(c==EOF) 
    {
        /* Put back to leave gracefully - hf */
        unget_char(c, stream_ptr); 
    }
    return;
}

/* *********************************************************************
   Function name : constant
   Input         : state of DFA, Token string, Token id.
   Output        : constant token.
   Exceptions    : none.
   Description   : This function returns the token_id for the constatnts
                   speccified by  the final state. 
 * ****************************************************************** */
static int constant(int state, char token_str[], int token_ind)
{
    switch(state)
    {   /* Return the respective CONSTANT macro. */
        case 27 : 
            return STRING_CONSTANT;
        case 29 : 
            token_str[token_ind-2]=' '; 
            return CHARACTER_CONSTANT;
        default : 
            break;
    }
    return ERROR;
}


/* *******************************************************************
   Function name : next_state
   Input         : current state, character
   Output        : next state of the DFA
   Exceptions    : none.
   Description   : This function returns the next state in the transition
                   diagram. The next state is determined by the current
                   state state and the inpu character ch.
 * ****************************************************************** */        
static int next_state(int state, char ch)
{
    if(state < 0)
        return state;
    if(base[state]+ch >= 0)
    {
        if(check[base[state]+ch] == state) 
        {
            /* Check for the right state */
            return next[base[state]+ch];
        }
        else
        {
            return next_state(default1[state],ch);
        }
    }
    else
    {
        return next_state(default1[state],ch);
    }
}

/* *********************************************************************
   Function name : is_eof_token
   Input         : token
   Output        : Boolean
   Exceptions    : none.
   Description   : This function checks whether the token t is eof_token 
                   or not. If the integer value stored in the t is
                   EOTSTREAM then it is eof_token.
 * ***************************************************************** */
int is_eof_token(token t)
{
    return t->token_id == EOTSTREAM;
}

/* ********************************************************************
   Function name : print_token
   Input         : token
   Output        : Boolean
   Exceptions    : none.
   Description   : This function  prints the token. The token_id gives 
                   the type of token not the token itself. So, in the
                   case of identifier,numeric,  string,character it is
                   required to print the actual token  from token_str. 
                   So, precaution must be taken when printing the token.
                   This function is able to print the current token only
                   and it is the limitation of the program.
 * ******************************************************************** */
int print_token(token token_ptr)
{
    switch(token_ptr->token_id)
    {    /* Print the respective tokens. */
        case ERROR : 
            fprintf(stdout, "error,\t\"");
            fprintf(stdout, "%s",token_ptr->token_string);
            fprintf(stdout, "\".\n");
            return TRUE;
        case EOTSTREAM : 
            fprintf(stdout, "eof.\n");
            return TRUE;
        case 6 : 
            fprintf(stdout, "keyword,\t\"lambda\".\n");
            return TRUE;
        case 9 : 
            fprintf(stdout, "keyword,\t\"and\".\n");
            return TRUE;
        case 11: 
            fprintf(stdout, "keyword,\t\"or\".\n");
            return TRUE;
        case 13: 
            fprintf(stdout, "keyword,\t\"if\".\n");
            return TRUE;
        case 16: 
            fprintf(stdout, "keyword,\t\"xor\".\n");
            return TRUE;
        case 17: 
            fprintf(stdout, "identifier,\t\"");
            fprintf(stdout, "%s",token_ptr->token_string);
            fprintf(stdout, "\".\n");
            return TRUE;
        case 18: 
            fprintf(stdout, "numeric,\t");
            fprintf(stdout, "%s",token_ptr->token_string);
            fprintf(stdout, ".\n");
            return TRUE;
        case 19: 
            fprintf(stdout, "lparen.\n");
            return TRUE;
        case 20: 
            fprintf(stdout, "rparen.\n");
            return TRUE;
        case 21: 
            fprintf(stdout, "lsquare.\n");
            return TRUE;
        case 22: 
            fprintf(stdout, "rsquare.\n");
            return TRUE;
        case 23: 
            fprintf(stdout, "quote.\n");
            return TRUE;
        case 24: 
            fprintf(stdout, "bquote.\n");
            return TRUE;
        case 25: 
            fprintf(stdout, "comma.\n");
            return TRUE;
        case 27: 
            fprintf(stdout, "string,\t");
            fprintf(stdout, "%s",token_ptr->token_string);
            fprintf(stdout, ".\n");
            return TRUE;
        case 29: 
            fprintf(stdout, "character,\t\"");
            fprintf(stdout, "%s",token_ptr->token_string);
            fprintf(stdout, "\".\n");
            return TRUE;
        case 32: 
            fprintf(stdout, "keyword,\t\"=>\".\n");
            return TRUE;
        default: 
            break;
    }
    return FALSE;
}

/* **********************************************************************
   Function name : get_actual_token
   Input         : token string and token id.
   Output        : void.
   Exceptions    : none.
   Description   : This function prints the actual token in the case of
                   identifier,numeric,string and character. It removes
                   the leading and trailing  spaces and prints the token.
 * ****************************************************************** */
static void get_actual_token(char token_str[], int token_ind)
{
    int ind, start;
    for(ind=token_ind; ind>0 && isspace(token_str[ind-1]); --ind) {
        /* Skip to the next token from the character stream */
    }

    /* Delete the trailing white spaces & protect - hf */
    token_str[ind]='\0';token_ind=ind;
    for(ind=0;ind<token_ind;++ind)
    {
        if(!isspace(token_str[ind]))
            break;
    }
    
    for(start=0;ind<=token_ind;++start,++ind) 
    {
        /* Delete the leading white spaces. */
        token_str[start]=token_str[ind];
    }
    
    /* end of finding next token */    return;
}


/* USE: a.out file_path */
int main(int argc, char *argv[])
{
    token token_ptr;
    token_stream stream_ptr;
    if(argc == 2)
    {
        stream_ptr=open_token_stream(argv[1]);
        while(!is_eof_token((token_ptr=get_token(stream_ptr))))
        {
            print_token(token_ptr);
        }
        print_token(token_ptr);
        return 0;
    }
    else
    {
        fprintf(stderr, "The format is print_tokens filename(optional)\n");
        return 1;
    }
}

