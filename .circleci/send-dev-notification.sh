#!/bin/bash

COMMIT_DESCRIPTION=$(git log --format="%h - %s\nAuthor: %cn %ce\n%b" -n 1 $CIRCLE_SHA1)

curl -H "Content-Type: application/json"\
     -X POST $DISCORD_DEV_URL\
     -d "
     {
       \"content\": \"\",
       \"embeds\": [{
           \"title\": \"A New Developer Build is Available for Use\",
           \"description\": \"$COMMIT_DESCRIPTION\",
           \"url\": \"https://artifactory.feldman.tech/artifactory/webapp/#/artifacts/browse/tree/General/minecraft/com/codingforcookies/betterrecords/BetterRecords-forge/1.12.2-SNAPSHOT/maven-metadata.xml\",
           \"color\": 2068783
       }]
     }
     "