class MyClass

// Correct at compile time but wrong at run-time
fun MyClass?.inc(): MyClass? { return null }

public fun box() : MyClass? {
    var i : MyClass? 
    i = MyClass()
    var j = i++
    j.hashCode()
    // i can be null here (to be more precise it IS null)
    return i ?: j
}
