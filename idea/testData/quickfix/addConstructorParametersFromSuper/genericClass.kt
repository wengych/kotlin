// "Add constructor parameters and use them" "true"
trait I

open class Base<T1, T2>(p1: T1, p2: T2, p3: Base<T1, T2>?)

class C<T> : I, Base<T, String><caret>