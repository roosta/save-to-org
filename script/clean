#!/usr/bin/env bash
cd "$(dirname "${BASH_SOURCE[0]}")" && cd .. || exit 1
for e in release dev
do 
  echo "Cleaning resources/$e/out..."
  rm -rf resources/$e/out
  echo "Cleaning resources/$e/css..."
  rm -rf resources/$e/css
  echo "Cleaning resources/$e/*.html..."
  rm -f resources/$e/*.html
  echo "Cleaning manifest-$e.edn..."
  rm -f resources/manifest-$e.edn
done
