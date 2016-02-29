SUBDIR = src

ifndef classpath
export classpath = ${PWD}/class
endif

.PHONY: all clean

default: all

all:
	@mkdir -p ${classpath}
	${MAKE} -C ${SUBDIR}
	@echo 'java -classpath ${classpath} joosc/Joosc "$$@"' > joosc && chmod 755 joosc

clean:
	rm -rf joosc ${classpath}
