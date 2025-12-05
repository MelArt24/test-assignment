SRC_MAIN := src/main/java
SRC_TEST := src/test/java
RES_TEST := src/test/resources

OUT_DIR := out
OUT_MAIN := $(OUT_DIR)/main
OUT_TEST := $(OUT_DIR)/test

LIB_DIR := lib
JUNIT4 := $(LIB_DIR)/junit-4.13.2.jar
HAMCREST := $(LIB_DIR)/hamcrest-core-1.3.jar

CLASSPATH := $(OUT_MAIN);$(OUT_TEST);$(JUNIT4);$(HAMCREST)

.PHONY: all clean test compile compile-main compile-test help deps

help:
	@echo ""
	@echo "Available targets:"
	@echo "  help            Show this help message"
	@echo "  deps            Download JUnit 4 dependencies"
	@echo "  compile         Compile main and test sources"
	@echo "  compile-main    Compile only main sources"
	@echo "  compile-test    Compile only test sources"
	@echo "  test            Run all JUnit 4 tests"
	@echo "  clean           Remove build output"
	@echo ""

all: compile

compile: deps compile-main compile-test

deps: $(JUNIT4) $(HAMCREST)

$(JUNIT4): | $(LIB_DIR)
	wget -q https://repo1.maven.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar -O $(JUNIT4)
	@echo "Downloaded junit-4.13.2"

$(HAMCREST): | $(LIB_DIR)
	wget -q https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar -O $(HAMCREST)
	@echo "Downloaded hamcrest-core-1.3"

$(LIB_DIR):
	mkdir -p $(LIB_DIR)

compile-main:
	@echo "== Compiling main sources =="
	mkdir -p $(OUT_MAIN)
	javac -cp "$(JUNIT4);$(HAMCREST)" -d $(OUT_MAIN) $$(find $(SRC_MAIN) -name "*.java")

compile-test:
	@echo "== Compiling test sources =="
	mkdir -p $(OUT_TEST)
	javac -cp "$(OUT_MAIN);$(JUNIT4);$(HAMCREST)" -d $(OUT_TEST) $$(find $(SRC_TEST) -name "*.java")

	@echo "== Copying test resources =="
	@if [ -d $(RES_TEST) ]; then cp -r $(RES_TEST)/* $(OUT_TEST)/ 2>/dev/null || true; fi

test: compile
	@echo "== Running JUnit 4 tests =="

	TEST_CLASSES="$$(find $(OUT_TEST) -name '*Test.class' \
		| sed 's|$(OUT_TEST)/||' \
		| sed 's|/|.|g' \
		| sed 's|.class||')"; \
	for cls in $$TEST_CLASSES; do \
		echo "Running $$cls"; \
		java -cp "$(CLASSPATH)" org.junit.runner.JUnitCore $$cls; \
	done

clean:
	rm -rf $(OUT_DIR)
