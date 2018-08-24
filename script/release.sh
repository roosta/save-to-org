#!/usr/bin/env bash
export RELEASE=1
clj -A:html && clj -A:manifest && clj -A:css && clj -A:cljs -m shadow.cljs.devtools.cli release extension
