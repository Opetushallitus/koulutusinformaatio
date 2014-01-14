OUT=out/

all: clean venv copyassets
	venv/bin/python render.py
	cp -r out/* ../../../../koulutusinformaatio-app/src/main/webapp/m/
	
clean:
	rm -rf out/

dirs:
	mkdir -p out

venv: dirs venv/bin/activate

venv/bin/activate: requirements.txt
	test -d venv || virtualenv venv
	. venv/bin/activate; venv/bin/pip install -Ur requirements.txt
	touch venv/bin/activate

copyassets:
	cp src/style.css out/style.css


