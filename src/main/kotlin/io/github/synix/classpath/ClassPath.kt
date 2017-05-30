package io.github.synix.classpath

import java.io.File
import java.io.IOException
import java.nio.file.Paths

class Classpath private constructor(jreOption: String?, cpOption: String) {
    companion object {
        fun parse(jreOption: String?, cpOption: String): Classpath {
            return Classpath(jreOption, cpOption)
        }
    }

    fun readClass(className: String): ReadClassResult? {
        val classNameWithExt = "${className}.class"

        return listOf(this.bootClassPath.readClass(classNameWithExt),
                this.extClassPath.readClass(classNameWithExt),
                this.userClassPath.readClass(classNameWithExt))
                .asSequence()
                .filter { it.bytes != null }
                .firstOrNull()
    }

    init {
        parseBootAndExtClasspath(jreOption)
        parseUserClasspath(cpOption)
    }

    lateinit var bootClassPath: Entry
    lateinit var extClassPath: Entry
    lateinit var userClassPath: Entry

    private fun parseBootAndExtClasspath(jreOption: String?) {
        val jreDir = getJreDir(jreOption)
        // jre/lib/*
        val jreLibPath = Paths.get(jreDir, "lib", "*").toString()
        this.bootClassPath = CompositeEntry.createEntry(jreLibPath)
        // jre/lib/ext/*
        val jreExtPath = Paths.get(jreDir, "lib", "ext", "*").toString()
        this.extClassPath = CompositeEntry.createEntry(jreExtPath)
    }

    private fun parseUserClasspath(cpOption: String) {
        this.userClassPath = newEntry(if (cpOption.isEmpty()) "." else cpOption)
    }

    private fun getJreDir(jreOption: String?): String {
        if (jreOption != null && jreOption.isNotEmpty() && File(jreOption).exists()) {
            return jreOption
        }
        if (File("./jre").exists()) {
            return "./jre"
        }

        val javaHome = System.getenv("JAVA_HOME")
        if (javaHome.isNotEmpty()) {
            return Paths.get(javaHome, "jre").toString()
        }

        throw IOException("Can not find jre folder!")
    }

    override fun toString(): String {
        return "bootstrap classpath:\n " +
                "$bootClassPath\n " +
                "extension classpath:\n " +
                "$extClassPath\n" +
                "user classpath:\n " +
                "$userClassPath"
    }
}



