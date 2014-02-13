OUT=out/

all: clean venv copyassets
	venv/bin/python render.py $(SOURCE)
	
clean:
	rm -rf $(OUT)

dirs:
	mkdir -p out

venv: dirs venv/bin/activate

venv/bin/activate: requirements.txt
	test -d $(SCRIPT)/venv || /usr/bin/virtualenv venv
	. venv/bin/activate; venv/bin/pip install -Ur requirements.txt
	touch venv/bin/activate

copyassets:
	cp assets/* $(OUT)

install:
	rm -rf $(INSTALL_DIR)
	mkdir $(INSTALL_DIR)
	cp -rf out/* $(INSTALL_DIR)


