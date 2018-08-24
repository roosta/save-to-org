#!/usr/bin/env bash
# source: https://github.com/binaryage/chromex-sample/blob/master/scripts/package.sh

pushd "$(dirname "${BASH_SOURCE[0]}")" > /dev/null
source "./config.sh"

pushd "$ROOT"

RELEASES="$ROOT/releases"
RELEASE_BUILD="$ROOT/resources/release"
RELEASE_BUILD_COMPILED="$RELEASE_BUILD/js"

if [ ! -d "$RELEASE_BUILD" ] ; then
  echo "'$RELEASE_BUILD' does not exist, run 'lein release' first"
  exit 1
fi

if [ ! -d "$RELEASE_BUILD_COMPILED" ] ; then
  echo "'$RELEASE_BUILD_COMPILED' does not exist, run 'lein release' to fully build the project"
  exit 2
fi

if [ ! -d "$RELEASES" ] ; then
  mkdir -p "$RELEASES"
fi

VERSION_WITH_QUOTES=$(grep "defproject" < project.clj | cut -d' ' -f3)
VERSION="${VERSION_WITH_QUOTES//\"}"

PACKAGE_DIR="$RELEASES/yank-$VERSION"

if [ -d "$PACKAGE_DIR" ] ; then
  rm -rf "$PACKAGE_DIR"
fi

cp -Lr "$RELEASE_BUILD" "$PACKAGE_DIR" # this will copy actual files, not symlinks

# prune release directory from extra files/folders
rm -rf "$PACKAGE_DIR/js/background"
rm -rf "$PACKAGE_DIR/js/content_script"
rm -rf "$PACKAGE_DIR/js/options"

echo "'$PACKAGE_DIR' prepared for packing"
echo "Zipping files..."
cd "$PACKAGE_DIR" && zip -r -FS "$RELEASES/yank-$VERSION.xpi" -- * && cd - || exit 1 
rm -rf "$PACKAGE_DIR"
echo "Package saved as: yank-$VERSION.xpi"

popd
