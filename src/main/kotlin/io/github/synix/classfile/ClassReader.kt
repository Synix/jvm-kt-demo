package io.github.synix.classfile


import java.math.BigInteger
import java.nio.ByteBuffer


class ClassReader(bytes: ByteArray) {
    val byteBuffer: ByteBuffer

    init {
        byteBuffer = ByteBuffer.wrap(bytes)
    }

    fun readUint8(): Short {
        return byteBuffer.getUnsignedByte()
    }

    fun readUint16(): Int {
        return byteBuffer.getUnsignedShort()
    }

    fun readUint32(): Long {
        return byteBuffer.getUnsignedInt()
    }

    fun readUint64(): BigInteger {
        return byteBuffer.getUnsignedLong()
    }

    fun readFloat(): Float {
        return byteBuffer.getFloat()
    }

    fun readDouble(): Double {
        return byteBuffer.getDouble()
    }

    fun readUint16s(): Array<Int> {
        val n = readUint16()
        return Array<Int>(n) {
            readUint16()
        }
    }

    fun readBytes(length: Int): ByteArray {
        return ByteArray(length) {
            byteBuffer.get()
        }
    }
}