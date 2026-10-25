#!/bin/bash
cd "$(dirname "$0")"
CP=target/classes:$(cat cp.txt)
for r in 1 2 3; do
  for m in single multi; do
    java -Dstdout.encoding=UTF-8 -cp $CP com.howtodoinjava.supportdesk.SupportDeskApp $m 2>/dev/null | grep '^RESULT' | sed "s/^/run$r|/" >> runs/results.txt
  done
done
echo DONE >> runs/results.txt
