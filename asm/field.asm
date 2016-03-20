section .data
msg db 'Hello, world!', 0xa
len equ $ - msg
global x
_x: 
	dw 0

section .text

global _start
_start:
	mov	ebx,1		
	mov	eax,4

	mov dword [_x], 'h'
	mov ecx, _x

	mov	edx,1	
	int	0x80
	add esp, 4
	
	mov	ebx,0		
	mov	eax,1		
	int	0x80	