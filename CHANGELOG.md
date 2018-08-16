# Change Log
All notable changes to this project will be documented in this file. This change
log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [Unreleased]

## [v1.0.3] - 2018-08-15
### Fix
- Fix theme icon path

## [v1.0.2] - 2018-08-15
### Added
- Icon for dark/light theme

## [v1.0.1] - 2018-07-29
### Added
- LaTeX support

## [v1.0.0] - 2018-07-25
### Added
- Add integration, background, HTML, and manifest tests
- Use travis CI

### Changed
- Upgrade dependencies
- Formatting fns are now pure and move to separte NS
- Improve reStructuredText escapes

### Fixed
- Options defaults are used and displayed when nothing is present in storage.

## [v0.3.0] - 2018-02-25
### Added
- Popup menu when clicking the toolbar icon, options to change the format on the
  fly. Shorcut option is still in preferences page.
- Add CSS to popup menu and options page

### Changed
- All HTML and manifest files are now generated from clojure src and is no
  longer tracked in repo
- License changed to MIT

## [v0.2.1] - 2017-09-24
### Fixed
- Minor issue with manifest version vs published versions

## [v0.2.0] - 2017-09-24
### Added
- Context menu, right click on a link in a page and copy its URL and description
- Added support for mac keyboards (untested)

## [v0.1.0] - 2017-09-24
### Initial release
Base feature-set in place, allows yanking using a custom keybind and supports
the following markup languages:
- Org-mode
- Markdown
- Textile
- AsciiDoc
- reStructuredText
- HTML

[Unreleased]: https://github.com/roosta/yank/compare/v1.0.3...HEAD
[v1.0.3]: https://github.com/roosta/yank/compare/v1.0.2...v1.0.3
[v1.0.2]: https://github.com/roosta/yank/compare/v1.0.1...v1.0.2
[v1.0.1]: https://github.com/roosta/yank/compare/v1.0.0...v1.0.1
[v1.0.0]: https://github.com/roosta/yank/compare/v0.3.0...v1.0.0
[v0.3.0]: https://github.com/roosta/yank/compare/v0.2.1...v0.3.0
[v0.2.1]: https://github.com/roosta/yank/compare/v0.2.0...v0.2.1
[v0.2.0]: https://github.com/roosta/yank/compare/v0.1.0...v0.2.0
[v0.1.0]: https://github.com/roosta/yank/compare/v0.1.0...master
