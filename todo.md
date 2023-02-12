# Overall todo's:

- Improve stability (bug fixes & wrong / false positive etc.)
- Improve performance (always a top priority)

## Should keep the type-alias when displaying exceptions

as of now the way the type hierarchies are resolved, any type-alias are resolved as well.
This makes the code easier to work with, but means the user will see the resolved name instead.


## throwing properties (function, eg lazy):

```kotlin

@get:Throws
val x: String by lazy {
    throw Exception()
}

fun useX() {
    //should highlight this is where an exception "might" be thrown
    val newString = x + "test"
}
```

