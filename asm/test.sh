export MYDIR=`dirname $0`
rm $MYDIR/*.o
rm main
for f in $MYDIR/*.s
do
	nasm -O1 -f elf -g -F dwarf $f
done
ld -melf_i386 -o main $MYDIR/*.o
$MYDIR/main
echo $?