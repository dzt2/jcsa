/*
 *    --- __jcmuta__.h ---
 *      header for JCMuta-running library
 *      used to define functions used in mutation program 
 */
//#include <math.h>
#include <limits.h>

/* used for strong mutation */
static int _JCM_trap_on_statement();
static void _JCM_trap_on_times(int);
#define	_JCM_NULL					0
#define _JCM_trap_on_true(x)        ((x) ? _JCM_trap_on_statement() : (x))
#define _JCM_trap_on_false(x)       ((x) ? (x) : _JCM_trap_on_statement())
#define _JCM_trap_on_case(x, y)     (((x) == (y))? _JCM_trap_on_statement() : (x))
#define _JCM_mut_abs(x)             (((x) < 0)? (-(x)) : (x))
#define _JCM_true_value             1
#define _JCM_false_value            0
#define _JCM_trap_on_positive(x)    (((x) > 0)? _JCM_trap_on_statement() : (x))
#define _JCM_trap_on_zero(x)        (((x) == 0)? _JCM_trap_on_statement() : (x))
#define _JCM_trap_on_nzero(x)		(((x) != 0)? _JCM_trap_on_statement() : (x))
#define _JCM_trap_on_negative(x)    (((x) < 0)? _JCM_trap_on_statement() : (x))
#define _JCM_succ(x)                ((x) + 1)
#define _JCM_pred(x)                ((x) - 1)
#define _JCM_POS_INF                INT_MAX
#define _JCM_NEG_INF                INT_MIN
extern void exit(int);
static int _JCM_trap_on_statement() {
    exit(1); return 0;
}
static void _JCM_trap_on_times(int n) {
	static int counter = 0;
	if(++counter >= n)
		_JCM_trap_on_statement();
}

/* used for weak mutation */
typedef long long (* INT_INT_INT)(long long, long long);
typedef long double (* FLT_FLT_FLT)(long double, long double);
typedef void * (* PTR_PTR_PTR)(void *, void *);
static int _JCM_count_times(int condition) {
	static int counter = 0;
	if(condition != 0) counter++;
	return counter;
}
/* integer functions */
static long long _JCM_larg_integers(long long x, long long y) { return x; }
static long long _JCM_rarg_integers(long long x, long long y) { return y; }
static long long _JCM_add_integers(long long x, long long y) { return x + y; }
static long long _JCM_sub_integers(long long x, long long y) { return x - y; }
static long long _JCM_mul_integers(long long x, long long y) { return x * y; }
static long long _JCM_div_integers(long long x, long long y) { return x / y; }
static long long _JCM_mod_integers(long long x, long long y) { return x % y; }
static long long _JCM_ban_integers(long long x, long long y) { return x & y; }
static long long _JCM_bor_integers(long long x, long long y) { return x | y; }
static long long _JCM_bxr_integers(long long x, long long y) { return x ^ y; }
static long long _JCM_lan_integers(long long x, long long y) { return x && y; }
static long long _JCM_lor_integers(long long x, long long y) { return x || y; }
static long long _JCM_eqv_integers(long long x, long long y) { return x == y; }
static long long _JCM_neq_integers(long long x, long long y) { return x != y; }
static long long _JCM_smt_integers(long long x, long long y) { return x < y; }
static long long _JCM_sme_integers(long long x, long long y) { return x <= y; }
static long long _JCM_grt_integers(long long x, long long y) { return x > y; }
static long long _JCM_gre_integers(long long x, long long y) { return x >= y; }
static long long _JCM_lsh_integers(long long x, long long y) { return x << y; }
static long long _JCM_rsh_integers(long long x, long long y) { return x >> y; }
/* real value functions */
#define _JCM_REAL_ERROR 	1e-16
static long double _JCM_larg_reals(long double x, long double y) { return x; }
static long double _JCM_rarg_reals(long double x, long double y) { return y; }
static long double _JCM_abs_reals(long double x) { if(x < 0) return -x; else return x; }
static long double _JCM_add_reals(long double x, long double y) { return x + y; }
static long double _JCM_sub_reals(long double x, long double y) { return x - y; }
static long double _JCM_mul_reals(long double x, long double y) { return x * y; }
static long double _JCM_div_reals(long double x, long double y) { return x / y; }
static long double _JCM_lan_reals(long double x, long double y) { if(x && y) return 1; else return 0; }
static long double _JCM_lor_reals(long double x, long double y) { if(x || y) return 1; else return 0; }
static long double _JCM_eqv_reals(long double x, long double y) { if(_JCM_abs_reals(x - y) < _JCM_REAL_ERROR) return 1; else return 0; }
static long double _JCM_neq_reals(long double x, long double y) { if(_JCM_abs_reals(x - y) < _JCM_REAL_ERROR) return 0; else return 1; }
static long double _JCM_smt_reals(long double x, long double y) { if(x < y)  return 1; else return 0; }
static long double _JCM_sme_reals(long double x, long double y) { if(x <= y) return 1; else return 0; }
static long double _JCM_grt_reals(long double x, long double y) { if(x > y)  return 1; else return 0; }
static long double _JCM_gre_reals(long double x, long double y) { if(x >= y) return 1; else return 0; }
/* pointer functions */
static void * _JCM_eqv_pointers(void * x, void * y) {
	if(x == y) return ((void *) 0);
	else return ((void *) 1);
}
static void * _JCM_neq_pointers(void * x, void * y) {
	if(x != y) return ((void *) 0);
	else return ((void *) 1);
}
static void * _JCM_grt_pointers(void * x, void * y) {
	if(x > y) return ((void *) 0);
	else return ((void *) 1);
}
static void * _JCM_gre_pointers(void * x, void * y) {
	if(x >= y) return ((void *) 0);
	else return ((void *) 1);
}
static void * _JCM_smt_pointers(void * x, void * y) {
	if(x < y) return ((void *) 0);
	else return ((void *) 1);
}
static void * _JCM_sme_pointers(void * x, void * y) {
	if(x <= y) return ((void *) 0);
	else return ((void *) 1);
}
static void * _JCM_lan_pointers(void * x, void * y) {
	if(x && y) return ((void *) 1);
	else return ((void *) 0);
}
static void * _JCM_lor_pointers(void * x, void * y) {
	if(x || y) return ((void *) 1);
	else return ((void *) 0);
}
/* value functions */
static char _JCM_value_char(char x, char y, INT_INT_INT f, INT_INT_INT g) {
	char ans1, ans2;
	ans1 = f(x, y) ;
	ans2 = g(x, y) ;
	if(ans1 == ans2) return ans1;
	else return _JCM_trap_on_statement(); 
}
static unsigned char _JCM_value_uchar(unsigned char x, unsigned char y, INT_INT_INT f, INT_INT_INT g) {
	unsigned char ans1, ans2;
	ans1 = f(x, y) ;
	ans2 = g(x, y) ;
	if(ans1 == ans2) return ans1;
	else return _JCM_trap_on_statement(); 
}
static short _JCM_value_short(short x, short y, INT_INT_INT f, INT_INT_INT g) {
	short ans1, ans2;
	ans1 = f(x, y) ;
	ans2 = g(x, y) ;
	if(ans1 == ans2) return ans1;
	else return _JCM_trap_on_statement(); 
}
static unsigned short _JCM_value_ushort(unsigned short x, unsigned short y, INT_INT_INT f, INT_INT_INT g) {
	unsigned short ans1, ans2;
	ans1 = f(x, y) ;
	ans2 = g(x, y) ;
	if(ans1 == ans2) return ans1;
	else return _JCM_trap_on_statement(); 
}
static int _JCM_value_int(int x, int y, INT_INT_INT f, INT_INT_INT g) {
	int ans1, ans2;
	ans1 = f(x, y);
	ans2 = g(x, y);
	if(ans1 == ans2) return ans1;
	else return _JCM_trap_on_statement(); 
}
static unsigned int _JCM_value_uint(unsigned int x, unsigned int y, INT_INT_INT f, INT_INT_INT g) {
	unsigned int ans1, ans2;
	ans1 = f(x, y);
	ans2 = g(x, y);
	if(ans1 == ans2) return ans1;
	else return _JCM_trap_on_statement(); 
}
static long _JCM_value_long(long x, long y, INT_INT_INT f, INT_INT_INT g) {
	long ans1, ans2;
	ans1 = f(x, y);
	ans2 = g(x, y);
	if(ans1 == ans2) return ans1;
	else return _JCM_trap_on_statement(); 
}
static unsigned long _JCM_value_ulong(unsigned long x, unsigned long y, INT_INT_INT f, INT_INT_INT g) {
	unsigned long ans1, ans2;
	ans1 = f(x, y);
	ans2 = g(x, y);
	if(ans1 == ans2) return ans1;
	else return _JCM_trap_on_statement(); 
}
static long long _JCM_value_llong(long long x, long long y, INT_INT_INT f, INT_INT_INT g) {
	long long ans1, ans2;
	ans1 = f(x, y);
	ans2 = g(x, y);
	if(ans1 == ans2) return ans1;
	else return _JCM_trap_on_statement(); 
}
static unsigned long long _JCM_value_ullong(unsigned long long x, unsigned long long y, INT_INT_INT f, INT_INT_INT g) {
	unsigned long long ans1, ans2;
	ans1 = f(x, y);
	ans2 = g(x, y);
	if(ans1 == ans2) return ans1;
	else return _JCM_trap_on_statement(); 
}
static float _JCM_value_float(float x, float y, FLT_FLT_FLT f, FLT_FLT_FLT g) {
	float ans1, ans2;
	ans1 = f(x, y);
	ans2 = g(x, y);
	if(_JCM_abs_reals(ans1 - ans2) < _JCM_REAL_ERROR) 
		return ans1;
	else return _JCM_trap_on_statement(); 
}
static double _JCM_value_double(double x, double y, FLT_FLT_FLT f, FLT_FLT_FLT g) {
	double ans1, ans2;
	ans1 = f(x, y);
	ans2 = g(x, y);
	if(_JCM_abs_reals(ans1 - ans2) < _JCM_REAL_ERROR) 
		return ans1;
	else return _JCM_trap_on_statement(); 
}
static long double _JCM_value_ldouble(long double x, long double y, FLT_FLT_FLT f, FLT_FLT_FLT g) {
	long double ans1, ans2;
	ans1 = f(x, y);
	ans2 = g(x, y);
	if(_JCM_abs_reals(ans1 - ans2) < _JCM_REAL_ERROR) 
		return ans1;
	else return _JCM_trap_on_statement(); 
}
static void * _JCM_value_pointers(void *x, void *y, PTR_PTR_PTR f, PTR_PTR_PTR g) {
	void * ans1, * ans2;
	ans1 = f(x, y);
	ans2 = g(x, y);
	if(ans1 == ans2) return ans1;
	else return ((void *) _JCM_trap_on_statement());
}
/* assignment functions */
static char _JCM_assign_char(char *x, char y, INT_INT_INT f, INT_INT_INT g) {
	char ans1, ans2;
	ans1 = f(*x, y);
	ans2 = g(*x, y);
	if(ans1 == ans2) { *x = ans1; return *x; }
	else return _JCM_trap_on_statement();
}
static unsigned char _JCM_assign_uchar(unsigned char *x, unsigned char y, INT_INT_INT f, INT_INT_INT g) {
	unsigned char ans1, ans2;
	ans1 = f(*x, y);
	ans2 = g(*x, y);
	if(ans1 == ans2) { *x = ans1; return *x; }
	else return _JCM_trap_on_statement();
}
static short _JCM_assign_short(short *x, short y, INT_INT_INT f, INT_INT_INT g) {
	short ans1, ans2;
	ans1 = f(*x, y);
	ans2 = g(*x, y);
	if(ans1 == ans2) { *x = ans1; return *x; }
	else return _JCM_trap_on_statement();
}
static unsigned short _JCM_assign_ushort(unsigned short *x, unsigned short y, INT_INT_INT f, INT_INT_INT g) {
	unsigned short ans1, ans2;
	ans1 = f(*x, y);
	ans2 = g(*x, y);
	if(ans1 == ans2) { *x = ans1; return *x; }
	else return _JCM_trap_on_statement();
}
static int _JCM_assign_int(int *x, int y, INT_INT_INT f, INT_INT_INT g) {
	int ans1, ans2;
	ans1 = f(*x, y);
	ans2 = g(*x, y);
	if(ans1 == ans2) { *x = ans1; return *x; }
	else return _JCM_trap_on_statement();
}
static unsigned int _JCM_assign_uint(unsigned int *x, unsigned int y, INT_INT_INT f, INT_INT_INT g) {
	unsigned int ans1, ans2;
	ans1 = f(*x, y);
	ans2 = g(*x, y);
	if(ans1 == ans2) { *x = ans1; return *x; }
	else return _JCM_trap_on_statement();
}
static long _JCM_assign_long(long *x, long y, INT_INT_INT f, INT_INT_INT g) {
	long ans1, ans2;
	ans1 = f(*x, y);
	ans2 = g(*x, y);
	if(ans1 == ans2) { *x = ans1; return *x; }
	else return _JCM_trap_on_statement();
}
static unsigned long _JCM_assign_ulong(unsigned long *x, unsigned long y, INT_INT_INT f, INT_INT_INT g) {
	unsigned long ans1, ans2;
	ans1 = f(*x, y);
	ans2 = g(*x, y);
	if(ans1 == ans2) { *x = ans1; return *x; }
	else return _JCM_trap_on_statement();
}
static long long _JCM_assign_llong(long long *x, long long y, INT_INT_INT f, INT_INT_INT g) {
	long long ans1, ans2;
	ans1 = f(*x, y);
	ans2 = g(*x, y);
	if(ans1 == ans2) { *x = ans1; return *x; }
	else return _JCM_trap_on_statement();
}
static unsigned long long _JCM_assign_ullong(unsigned long long *x, unsigned long long y, INT_INT_INT f, INT_INT_INT g) {
	unsigned long long ans1, ans2;
	ans1 = f(*x, y);
	ans2 = g(*x, y);
	if(ans1 == ans2) { *x = ans1; return *x; }
	else return _JCM_trap_on_statement();
}
static float _JCM_assign_float(float *x, float y, INT_INT_INT f, INT_INT_INT g) {
	float ans1, ans2;
	ans1 = f(*x, y);
	ans2 = g(*x, y);
	if(_JCM_abs_reals(ans1 - ans2) < _JCM_REAL_ERROR)  { 
		*x = ans1; return *x; 
	}
	else return _JCM_trap_on_statement();
}
static double _JCM_assign_double(double *x, double y, INT_INT_INT f, INT_INT_INT g) {
	double ans1, ans2;
	ans1 = f(*x, y);
	ans2 = g(*x, y);
	if(_JCM_abs_reals(ans1 - ans2) < _JCM_REAL_ERROR)  { 
		*x = ans1; return *x; 
	}
	else return _JCM_trap_on_statement();
}
static long double _JCM_assign_ldouble(long double *x, long double y, INT_INT_INT f, INT_INT_INT g) {
	long double ans1, ans2;
	ans1 = f(*x, y);
	ans2 = g(*x, y);
	if(_JCM_abs_reals(ans1 - ans2) < _JCM_REAL_ERROR)  { 
		*x = ans1; return *x; 
	}
	else return _JCM_trap_on_statement();
}
/* memory assert method */
static int _JCM_equal_objects(void *a, void *b, int n) {
	char * X = (char *) a;
	char * Y = (char *) b;
	while((n--) > 0) {
		if((*X) == (*Y)) { X++; Y++; }
		else _JCM_trap_on_statement();
	}
	return 1;
}
static void * _JCM_address_of(void *a, void *b, int n) {
	if(_JCM_equal_objects(a, b, n)) return a;
	else return ((void *) _JCM_NULL);
}
#define _JCM_assert_objects(x, y) (*( (typeof(&(x))) _JCM_address_of((void *)(&(x)), ((void *)(&(y))), sizeof(x)) ))
