#!/bin/bash
rm -r dist/*
lein clean
cp -R resources/public/* dist
gulp build-prod
lein cljsbuild once prod
