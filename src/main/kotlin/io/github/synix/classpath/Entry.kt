package io.github.synix.classpath

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.ZipFile


data class ReadClassResult(val bytes: ByteArray?,
                           val entry: Entry)

val pathSeparator: String = File.pathSeparator


interface Entry {
    fun readClass(className: String): ReadClassResult
}

interface EntryFactory {
    fun createEntry(path: String): Entry
}

class DirEntry private constructor(val absDir: String): Entry {
    companion object : EntryFactory {
        override fun createEntry(path: String): Entry {
            val absolutePath = File(path).absolutePath
            return DirEntry(absolutePath)
        }
    }

    override fun readClass(className: String): ReadClassResult {
        val path = Paths.get(absDir, className)
        return try {
            ReadClassResult(Files.readAllBytes(path), this)
        } catch (e: IOException) {
            ReadClassResult(null, this)
        }
    }

    override fun toString(): String {
        return absDir
    }
}


class ZipEntry private constructor(val absPath: String): Entry {
    companion object : EntryFactory {
        override fun createEntry(path: String): Entry {
            val absolutePath = File(path).absolutePath
            return ZipEntry(absolutePath)
        }
    }

    override fun readClass(className: String): ReadClassResult {
        ZipFile(absPath).use { zipFile ->
            val bytes = zipFile.stream()
                    .filter{ it.name == className }
                    .findFirst()
                    .map {
                        zipFile.getInputStream(it).use {
                            it.readBytes(estimatedSize = DEFAULT_BUFFER_SIZE * 2)
                        }
                    }
            return ReadClassResult(bytes.orElse(null), this)
        }
    }

    override fun toString(): String {
        return absPath
    }
}



class CompositeEntry private constructor(val entries: Array<Entry>): Entry {
    companion object : EntryFactory {
        override fun createEntry(path: String): Entry {
            return if (path.endsWith("*")) {
                val baseDir = path.slice(0 until path.length - 1)
                val entriesArray: Array<Entry> = Files.walk(Paths.get(baseDir))
                        .filter{ it.toFile().name.endsWith(".jar") || it.toFile().name.endsWith(".JAR") }
                        .map {
                            newEntry(it.toFile().absolutePath)
                        }
                        .toArray { size -> arrayOfNulls<Entry>(size) }
                CompositeEntry(entriesArray)
            } else {
                CompositeEntry(path.split(pathSeparator).map(::newEntry).toTypedArray())
            }
        }
    }

    override fun readClass(className: String): ReadClassResult {
        val result = entries.map { it.readClass(className) }
                .filter { it.bytes != null }
                .firstOrNull()
        return ReadClassResult(result?.bytes, this)
    }

    override fun toString(): String {
        return entries.map(Entry::toString).joinToString(pathSeparator);
    }
}


fun newEntry(path: String): Entry {
    return when {
        path.contains(pathSeparator) || path.endsWith("*")
            -> CompositeEntry.createEntry(path)
        path.endsWith(".jar") || path.endsWith(".JAR") || path.endsWith(".zip") || path.endsWith(".ZIP")
            -> ZipEntry.createEntry(path)
        else
            -> DirEntry.createEntry(path)
    }
}


