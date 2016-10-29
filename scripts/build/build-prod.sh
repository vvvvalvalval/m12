#!/bin/bash
gulp less-dev
lein cljsbuild once prod
