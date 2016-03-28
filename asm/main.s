extern VTable
extern __debexit
section .text
global _start

_start:
	;call [VTable]	
	mov eax, 12
	call __debexit
