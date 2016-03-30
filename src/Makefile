JC = javac
JFLAGS = -g -d ${classpath}

<<<<<<< HEAD
SUBDIR = exceptions scanner parser weeder ast environment static_analysis joosc utility code_generation
=======
SUBDIR = exceptions scanner parser weeder ast environment static_analysis code_generation joosc
>>>>>>> 3e26199630cfe46ef6753bebad6bc28af6edf76d
SOURCES = Joosc.java

ifndef classpath
export classpath = ${PWD}/../class
endif

.SUFFIXES: .java .class
.PHONY: all clean ${SUBDIR}

default: all

all: ${SUBDIR} 
#${SOURCES:.java=.class}

${SUBDIR}:
	${JC} ${JFLAGS} $@/*.java

.java.class:
	${JC} ${JFLAGS} $*.java

clean:
	find . -type f -name '*.class' -delete
