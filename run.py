import os
import subprocess
import sys

compile = True
folder = 'test/testprogram/code_gen/'
p = folder +'Array2.java'

def getAllPath(l, path):
    for root, directories, filenames in os.walk(path):
        for f in filenames:
            fn = os.path.join(root, f)
            l.append(fn)
            
def getStdLib():
    path = 'java'
    l = list()
    getAllPath(l, path)
    return l

if __name__=='__main__':
    program = 'sh joosc'
    l = getStdLib()
    l += [p]
    program = program.split() + l
    #print(program)
        
    if (compile):
        p = subprocess.Popen(program).wait()
    
    subprocess.Popen('cp /u/cs444/pub/stdlib/5.0/runtime.s output/runtime.s'.split()).wait()

    for root,_,filenames in os.walk('output'):
        for f in filenames:
            if f[-2:] == '.s':
                fn = os.path.join(root, f)
                nasm = subprocess.Popen('/u/cs444/bin/nasm -O1 -f elf -g -F dwarf'.split() + [fn]).wait()
            
    
    ldl = list()
    for root,_,filenames in os.walk('output'):
        for f in filenames:
            if f[-2:] == '.o':
                fn = os.path.join(root, f)
                ldl.append(fn)
    
    ld = subprocess.Popen('ld -melf_i386 -o output/main'.split() + ldl).wait()
        
    main = subprocess.Popen(['./output/main']).wait()
    print(main)
    
    
