export MYDIR=`dirname $0`
export output=$MYDIR/../output
export lib=$MYDIR/../lib

#rm $output/*.o
#rm $output/main

for f in $output/*.s
do
	nasm -O1 -f elf -g -F dwarf $f
done
nasm -O1 -f elf -g -F dwarf -o $output/runtime.o $lib/runtime.s
ld -melf_i386 -o $output/main $output/*.o
$output/main
echo $?