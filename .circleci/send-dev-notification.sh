#!/bin/bash

COMMITS="$(git --no-pager log --format="%h - %s\n%b" --since="24 hours ago" | sed ':a;N;$!ba;s/\n//g')"

MOD_VERSION="$(grep mod_version ../gradle.properties | cut -d "=" -f 2 | cut -d " " -f 2 | tr -d '[:space:]')"

if [[ -n "$COMMITS" ]]; then
    curl -H "Content-Type: application/json"\
     -X POST $DISCORD_DEV_URL\
     -d "
     {
       \"content\": \"\",
       \"embeds\": [{
           \"title\": \"A New Nightly Build is Available\",
           \"description\": \"$COMMITS\",
           \"url\": \"https://artifactory.feldman.tech/artifactory/webapp/#/artifacts/browse/tree/General/minecraft/com/codingforcookies/betterrecords/BetterRecords-forge/$MOD_VERSION/maven-metadata.xml\",
           \"color\": 2068783
       }]
     }
     "
fi
