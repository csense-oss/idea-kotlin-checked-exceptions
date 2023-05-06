# 2.0.0

- rewrote most of the code to fix issues
    - Most features are now working across android studio & / intellij and across various versions
    - Performance should be superb
    - Quick fixes should generally work pretty well now
    - a lot of things that previously did not work (or only partially worked) now works
        - e.g. contracts are now read / interpreted (calls in place)
    - Highlights simple Throws in kotlin doc (is not fully implemented)
    - should respect imports now when generating quick fixe(s)
- began work on coding throwing functions from kotlin's std lib. (requires a lot of work)
- Renamed settings to avoid conflicts with older version(s).

# 1.2.0

- Completely fixed a lot of bugs (via testing)
- Fixed some depreciation

# 1.1.4

- Disable throws annotator in test code / modules
