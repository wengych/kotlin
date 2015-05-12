// FILE: JI.java
public interface JI {
    default String test() {
        return "123";
    }

}

// FILE: test.kt
class KCLass: JI {}

trait KTrait: JI {}

class KTraitClass: KTrait{}

fun main(args: Array<String>) {
    KClass().test()
    KTraitClass().test()
}