all: float.c double.c
	gcc -o float float.c
	gcc -o double double.c
	gcc -o 32_flops flops_32.c
	gcc -o 64_flops flops_64.c
	gcc -mfma	-o	cmp_fma fma_64.c
clean:
	rm float double 32_flops 64_flops cmp_fma
