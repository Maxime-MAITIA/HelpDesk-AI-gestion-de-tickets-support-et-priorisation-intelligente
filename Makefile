all: compile run

compile:
	javac -d bin src/*.java

run:
	java -cp bin HelpDeskApp

clean:
	rm -rf bin/*.class
	rm -f data/tickets.csv
