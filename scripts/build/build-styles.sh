#!/usr/bin/env bash

SITE_DIR=$1

echo "Compiling and minifying stylesheets..."
lessc src/m12/m12.main.less $SITE_DIR/css/compiled/m12.css
# TODO is that a bug? why is this the output?
cleancss -o $SITE_DIR/css/compiled/main.css $SITE_DIR/css/compiled/main.css

# NOTE maybe I'll want to do more post-processing (autoprefixing etc.) using pleeease http://pleeease.io
