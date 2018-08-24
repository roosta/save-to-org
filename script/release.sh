#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

export RELEASE=1

cd "$(dirname "${BASH_SOURCE[0]}")" && cd .. || exit 1

ROOT=$(pwd)
RELEASES="$ROOT/releases"
RELEASE_BUILD="$ROOT/resources/release"
VERSION=$(cut -d'"' -f2 < "$ROOT/config.edn")
PACKAGE_DIR="$RELEASES/yank-$VERSION"

if [[ $(git diff --stat) != '' ]]; then
  echo >&2 'There are uncommitted changes, exiting.'
  exit 1
fi

clj -A:html
clj -A:manifest
clj -A:css 
clj -A:cljs -m shadow.cljs.devtools.cli release extension

if [ -d "$PACKAGE_DIR" ] ; then
  rm -rf "$PACKAGE_DIR"
fi

cp -Lr "$RELEASE_BUILD" "$PACKAGE_DIR" # this will copy actual files, not symlinks

echo "'$PACKAGE_DIR' prepared for packing"

echo "Zipping files..."
cd "$PACKAGE_DIR" && zip -r -FS "$RELEASES/yank-$VERSION.xpi" -- * && cd - || exit 1 
rm -rf "$PACKAGE_DIR"
echo "Package saved as: yank-$VERSION.xpi"
