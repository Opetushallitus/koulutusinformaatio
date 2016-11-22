#!/bin/bash

BASE_DIR=`dirname $0`

echo ""
echo "Starting Karma Server (https://github.com/karma-runner/karma/)"
echo "-------------------------------------------------------------------"

rm -rf node_modules/
npm install
node_modules/karma/bin/karma start $BASE_DIR/../config/testacular.conf.js $*
