#!/bin/bash
./scripts/build/build-prod.sh
netlify deploy --dir dist/ --prod
osascript -e 'display notification "deployed to prod." with title "m12"'
