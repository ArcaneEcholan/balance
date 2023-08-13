package fit.wenchao.ppm_main.dbgen

class Gendb {

}

fun main() {
    var generator = fit.wenchao.db.generator.Generator()
    generator.start(Gendb::class.java)
}