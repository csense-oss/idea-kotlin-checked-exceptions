# Overall todo's:

- Improve stability (bug fixes & wrong / false positive etc.)
- Improve performance (always a top priority)

## Specifics

primary missing todos:

- tell if certain exceptions are NOT thrown but declared.
- actions to fix types thrown (e.g. make it throwable, or write the explicit types..)
- detect when there are overlaps in catch clauses (e.g. throwable followed by more specific type...)

secondary todos:

- handle "*arrayOf(exception::class, exception2::class)" ....

- Handle property setters... (and not just use the getter..)
- re-introduce custom ignore & call-though
- add interface handling from csense annotations. (regarding call-though & catches annotations)
- redo the settings page
- streamline colors for markers etc.
- re-introduce quick fixes... and add new ones for the various cases.
- cleanup the integration tests
- 
