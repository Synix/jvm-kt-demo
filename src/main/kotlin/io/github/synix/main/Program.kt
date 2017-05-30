package io.github.synix.main

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import com.xenomachina.argparser.mainBody
import io.github.synix.classpath.Classpath


private val HEX_CHARS = "0123456789ABCDEF".toCharArray()

internal fun ByteArray.toHex() : String{
    val result = StringBuffer()

    forEach {
        val octet = it.toInt()
        val firstIndex = (octet and 0xF0).ushr(4)
        val secondIndex = octet and 0x0F
        result.append(HEX_CHARS[firstIndex])
        result.append(HEX_CHARS[secondIndex])
    }

    return result.toString()
}

class Cmd(parser: ArgParser) {
    val cpOption by parser.storing("--classpath", help = "classpath").default(".")
    val XjreOption by parser.storing("--Xjre", help = "path to jre").default(null)   // bootstrap and ext path(jre/lib..)
    val clazz by parser.positional("CLASS", help = "source class file")
    val args by parser.positionalList("ARGS", help = "arguments", sizeRange = 1..Int.MAX_VALUE).default(null)
}


fun startJVM(cmd: Cmd) {
    val cp = Classpath.parse(cmd.XjreOption, cmd.cpOption)

    println("cp: $cp \n" +
            "class: ${cmd.clazz} \n" +
            "args: ${cmd.args}")


    val className = cmd.clazz.replace(".", "/")
    val readResult = cp.readClass(className)

    if (readResult == null || readResult.bytes == null) {
        println("Could not find or load main class ${cmd.clazz}")
    } else {
        println("class data:\n" +
                readResult.bytes.toHex())
    }
}


fun main(args: Array<String>) = mainBody("java -jar ./build/libs/jvm-kt-demo.jar") {
    Cmd(ArgParser(args)).run {
        startJVM(this)
    }
}
