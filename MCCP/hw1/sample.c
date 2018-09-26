#include <immintrin.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

void foo_avx(const __m256d *x1,
	const __m256d *x2, const __m256d *x3, __m256d *y) {
	*y = _mm256_mul_pd(*x1, *x2);
	*y = _mm256_add_pd(*y, *x3);
}
int main(void) {
	__m256d vec1, vec2, vec3, rst; int i;
	double x = 1., y = 2., z = 3., d = 0, arr[4];
	arr[0] = x; arr[1] = y; arr[2] = z; arr[3] = 0;
	vec1 = _mm256_set_pd(1.,2.,3.,4.);
	vec2 = _mm256_set_pd(1., 2., 3., 4.);
	vec3 = _mm256_set_pd(-0., -2., -6., -12.);
//	rst = _mm256_set_pd(1., 2., 3., 4.);

	for (i = 0; i < 2; ++i) {
		printf("[0]: %lf\n[1]: %lf\n[2]: %lf\n[3]: %lf\n", rst[0], rst[1], rst[2], rst[3]);
		foo_avx(&vec1, &vec2, &vec3, &rst);
		printf("[0]: %lf\n[1]: %lf\n[2]: %lf\n[3]: %lf\n", rst[0], rst[1], rst[2], rst[3]);
	}
	printf("[0]: %lf\n[1]: %lf\n[2]: %lf\n[3]: %lf\n", rst[0],rst[1],rst[2],rst[3]);
	return 0;
}
/*void dofma(double arr[], __m256d* pvec) {
double x = arr[0], y = arr[1], z = arr[2];
vec = _mm256_set1_pd(1.0);
vec = _mm256_add_pd(vec, _mm256_set_pd(x, y, z, 0.));
return vec;
}*/
/*__m256d a, m;
__m256d zero = _mm256_set1_pd(0.0);
__m256d one = _mm256_set1_pd(1.0);
*/
/*m = _mm256_cmp_pd(a, zero, _CMP_GT_OS);
if (_mm256_movemask_pd(m) == 0x0) {
*y = zero;
return;
}*/
/*a = _mm256_blendv_pd(one, _mm256_sqrt_pd(a), m);
*y = _mm256_blendv_pd(zero, _mm256_log_pd(a), m);*/
//_mm_fmadd_pd();
//_mm256_set_pd
//vec = dofma(arr,vec);

