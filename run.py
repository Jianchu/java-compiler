import os
import subprocess

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
    program = program.split() + l
    #print(program)
    p = subprocess.Popen(program)
    p.wait()

    for root,_,filenames in os.walk('output'):
        for f in filenames:
            fn = os.path.join(root, f)
            nasm = subprocess.Popen('/u/cs444/bin/nasm -O1 -f elf -g -F dwarf'.split() + [fn])
            nasm.wait()
    ld = subprocess.Popen('ld -melf_i386 -o output/main output/*.o'.split())
    ld.wait()
    
    main = subprocess.Popen(['./output/main']).wait()
    print(main)
    
    
