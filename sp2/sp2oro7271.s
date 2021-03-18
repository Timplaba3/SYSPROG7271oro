.intel_syntax noprefix
.data

    array: .byte 85, 29, 17, 189, 206, 74, 251, 230, 219, 207
    .set  n, . - array - 1
    tor: .byte 107
    tcmp: .byte 128
    count: .byte 0x30
.text
.globl  main
.type   main, @function
main:

    mov ebx, 0 

    one_iter:
        cmp ebx, n
        je _close
        xor eax, eax
        mov al, [array+ebx]
        or al, tor
        mov [array+ebx], al
        cmp al, tcmp
        jb lower
        jae continue

    lower:
        mov al, count
        inc al
        mov count, al
        jmp continue

    continue:
        xor eax, eax
        inc ebx
        jmp one_iter

    _close:
        mov ebp, esp
        mov edx, 2
        lea ecx, count
        mov ebx, 1
        mov eax, 4
        int 0x80
        mov eax, 0
        ret
        .size main, . - main
