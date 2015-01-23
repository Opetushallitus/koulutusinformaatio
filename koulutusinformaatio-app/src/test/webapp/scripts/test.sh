#!/bin/bash

BASE_DIR=`dirname $0`

echo ""
echo "Starting Testacular Server (http://vojtajina.github.com/testacular)"
echo "-------------------------------------------------------------------"

npm install
node_modules/karma/bin/karma start $BASE_DIR/../config/testacular.conf.js $*
