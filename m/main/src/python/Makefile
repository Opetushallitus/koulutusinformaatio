OUT=out/

all: clean venv
	venv/bin/python render.py

clean:
	rm -rf out/

dirs:
	mkdir -p out

venv: dirs venv/bin/activate

venv/bin/activate: requirements.txt
	test -d venv || virtualenv venv
	. venv/bin/activate; venv/bin/pip install -Ur requirements.txt
	touch venv/bin/activate
