fun test() {
    //TODO
    /*val p = */fun f(a: Int) {
        if (a > 0) {
            <lineMarker descr="Recursive call">f</lineMarker>(a-1)
        }
    }
}