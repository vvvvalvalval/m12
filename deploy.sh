#!/bin/bash
gulp build-prod
lein do clean, cljsbuild once prod
netlify deploy -e prod -t `cat resources/unversioned/netlify-token`
osascript -e 'display notification "deployed to prod." with title "M12"'