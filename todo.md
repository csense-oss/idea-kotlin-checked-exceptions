# Overall plan

- improve performance (always a top priority)

- handle "by lazy"

```kotlin
val x: String by lazy {
    throw Exception()
}

fun useX() {
    //should highlight this is where an exception "might" be thrown
    val newString = x + "test"
}
```