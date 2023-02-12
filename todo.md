# Overall todo's:

- Improve stability (bug fixes & wrong / false positive etc.)
- Improve performance (always a top priority)

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

