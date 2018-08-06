package io.github.synix.main

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import com.xenomachina.argparser.mainBody
import io.github.synix.classfile.ClassFile
import io.github.synix.classpath.Classpath


private val HEX_CHARS = "0123456789ABCDEF".toCharArray()
private const val PROG_NAME: String = "JVM.Kt"


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
    val helpFlag by parser.flagging("-H", help = "print help message")
    val versionFlag by parser.flagging("-V", help = "print version and exit")
    val cpOption by parser.storing("--cp", "--classpath", help = "classpath").default(".")
    val XjreOption by parser.storing("--Xjre", help = "path to jre").default(null)   // bootstrap and ext path(jre/lib..)
    val clazz by parser.positional("CLASS", help = "source class file").default("")
    val args by parser.positionalList("ARGS", help = "arguments", sizeRange = 1..Int.MAX_VALUE).default(null)
}


fun startJVM(cmd: Cmd) {
    val classpath = Classpath.parse(cmd.XjreOption, cmd.cpOption)

    println("cp: $classpath \n" +
            "class: ${cmd.clazz} \n" +
            "args: ${cmd.args}")


    val className = cmd.clazz.replace(".", "/")

    val readResult = classpath.readClass(className)

    if (readResult?.bytes == null) {
        println("Could not find or load main class $className")
    } else {
        // println("class data:\n" + readResult.bytes.toHex())
        val classFile = ClassFile.parse(readResult.bytes)
        println(classFile)
    }
}


fun main(args: Array<String>) = mainBody(PROG_NAME) {
    Cmd(ArgParser(args)).run {
        when {
            this.helpFlag ->
                println("Usage: kotlin -jar build/libs/jvm.kotlin.jar [-options] class [args...]")
            this.versionFlag ->
                println("version 0.0.1")
            else -> {
                println("cp: ${this.cpOption}, class: ${this.clazz}, args: ${this.args}")
                startJVM(this)
            }
        }
    }
}

