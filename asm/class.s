section .text
global _print_method
_print_method:
	mov	edx,len
	mov	ecx,STRING_1		
	mov	ebx,1		
	mov	eax,4
	int	0x80	
	
	mov	ebx,0		
	mov	eax,1		
	int	0x80

global VTable
	VTable:
		dd _print_method	

section .data
STRING_1:
	dw 'Hello, world!'
len equ $ - STRING_1
