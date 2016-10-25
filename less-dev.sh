#!/usr/bin/env bash
lessc src/m12/styles/m12.main.less resources/public/css/compiled/m12.css --source-map && cp src/m12/styles/*.less resources/public/css/compiled
wr "lessc src/m12/styles/m12.main.less resources/public/css/compiled/m12.css --source-map && cp src/m12/styles/*.less resources/public/css/compiled" src/*/*/*.less
