#!/bin/bash
./scripts/build/build-prod.sh
netlify deploy -e prod -t `cat resources/unversioned/netlify-token`
osascript -e 'display notification "deployed to prod." with title "M12"'