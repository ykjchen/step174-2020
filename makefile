CLANG_FORMAT=node_modules/clang-format/bin/linux_x64/clang-format --style=Google
CSS_VALIDATOR=node_modules/css-validator/bin/css-validator
ESLINT=node_modules/eslint/bin/eslint.js
HTML_VALIDATE=node_modules/html-validate/bin/html-validate.js
STYLELINT=node_modules/stylelint/bin/stylelint.js

node_modules:
	npm install clang-format css-validator html-validate eslint eslint-config-google stylelint stylelint-order stylelint-config-standard

pretty: node_modules
	find backstory/src/main -iname *.java | xargs $(CLANG_FORMAT) -i
	find backstory/src/main -iname *.js | xargs $(CLANG_FORMAT) -i
	find backstory/src/main -iname *.html -o -iname *.css | xargs $(STYLELINT) --fix

validate: node_modules
	find backstory/src/main -iname *.html | xargs $(HTML_VALIDATE)
	find backstory/src/main -iname *.css | xargs $(CSS_VALIDATOR)
	find backstory/src/main -iname *.js | xargs $(ESLINT)
