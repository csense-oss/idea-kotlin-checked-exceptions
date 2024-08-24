# 2.2.0
- kotlin 2.0

# 2.1.3
- Fixed a bug in wrap in try catch (copping off code) https://github.com/csense-oss/idea-kotlin-checked-exceptions/issues/31
- 
# 2.1.2
- Fixes a lot of issues with respect to throw expressions (also causes a thrown exception to be interpreted as Exception https://github.com/csense-oss/idea-kotlin-checked-exceptions/issues/26 )
- QuickFixes (CSense detects Throws annotation only if lambda provides callsInPlace contract https://github.com/csense-oss/idea-kotlin-checked-exceptions/issues/27)
- Method references are now resolved (method references are not marked https://github.com/csense-oss/idea-kotlin-checked-exceptions/issues/28)

# 2.1.1
- Fixed https://github.com/csense-oss/idea-kotlin-checked-exceptions/issues/18
- Fixed https://github.com/csense-oss/idea-kotlin-checked-exceptions/issues/25

# 2.1.0

- Fixed a Stackoverflow exception (see https://github.com/csense-oss/idea-kotlin-checked-exceptions/issues/17)
- Fixed issues related to https://github.com/csense-oss/idea-kotlin-checked-exceptions/issues/21 and made plugin listen for manual changes to *.throws files
- Added capability to ignore functions marked as tests (and a setting hereof) (https://github.com/csense-oss/idea-kotlin-checked-exceptions/issues/19)
- Added capability to ignore functions marked as deprecated (and a setting hereof) (https://github.com/csense-oss/idea-kotlin-checked-exceptions/issues/20)

# 2.0.1

- fixed a regression where the main inspection was not enabled by default
- and in relation made sure that the main inspection default highlight level is warning
- fixed main inspection description
- bumped 3 party dependencies

# 2.0.0

- rewrote most of the code to fix issues
    - Most features are now working across android studio & / intellij and across various versions
    - Performance should be superb
    - Quick fixes should generally work pretty well now
    - a lot of things that previously did not work (or only partially worked) now works
        - e.g. contracts are now read / interpreted (calls in place)
    - Preliminary support for @throws kotlin doc
    - should respect imports now when generating quick fixe(s)
- began work on coding throwing functions from kotlin's std lib. (requires a lot of work)
- Renamed settings to avoid conflicts with older version(s).

# 1.2.0

- Completely fixed a lot of bugs (via testing)
- Fixed some depreciation

# 1.1.4

- Disable throws annotator in test code / modules