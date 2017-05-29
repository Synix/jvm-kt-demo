package io.github.synix.main

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import com.xenomachina.argparser.mainBody


class Cmd(parser: ArgParser) {
    val cpOption by parser.storing("--classpath", help = "classpath").default(".")
    val XjreOption by parser.storing("--Xjre", help = "path to jre").default(null)   // bootstrap path(jre/lib..)
    val clazz by parser.positional("CLASS", help = "source class file")
    val args by parser.positionalList("ARGS", help = "arguments", sizeRange = 1..Int.MAX_VALUE).default(null)
}


fun startJVM(cmd: Cmd) {
    println("classpath: ${cmd.cpOption} \n" +
            "bootstrap path: ${cmd.XjreOption} \n" +
            "class: ${cmd.clazz} \n" +
            "args: ${cmd.args}")
}


fun main(args: Array<String>) = mainBody("java -jar ./build/libs/jvm-kt-demo.jar") {
    Cmd(ArgParser(args)).run {
        startJVM(this)
    }
}
