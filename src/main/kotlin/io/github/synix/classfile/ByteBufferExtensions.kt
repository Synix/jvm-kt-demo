package io.github.synix.classfile

import java.math.BigInteger
import java.nio.ByteBuffer
import kotlin.experimental.and


fun ByteBuffer.getUnsignedByte(): Short {
    return (this.get() and 0xff.toByte()).toShort()
}

fun ByteBuffer.putUnsignedByte(value: Int) {
    this.put((value and 0xff).toByte())
}

fun ByteBuffer.getUnsignedByte(position: Int): Short {
    return (this.get(position) and 0xff.toByte()).toShort()
}

fun ByteBuffer.putUnsignedByte(position: Int, value: Int) {
    this.put(position, (value and 0xff).toByte())
}

fun ByteBuffer.getUnsignedShort(): Int {
    return (this.getShort() and 0xffff.toShort()).toInt()
}

fun ByteBuffer.putUnsignedShort(value: Int) {
    this.putShort((value and 0xffff).toShort())
}

fun ByteBuffer.getUnsignedShort(position: Int): Int {
    return (this.getShort(position) and 0xffff.toShort()).toInt()
}

fun ByteBuffer.putUnsignedShort(position: Int, value: Int) {
    this.putShort(position, (value and 0xffff).toShort())
}


fun ByteBuffer.getUnsignedInt(): Long {
    return this.getInt().toLong() and 0xffffffffL
}

fun ByteBuffer.putUnsignedInt(value: Long) {
    this.putInt((value and 0xffffffffL).toInt())
}

fun ByteBuffer.getUnsignedInt(position: Int): Long {
    return this.getInt(position).toLong() and 0xffffffffL
}

fun ByteBuffer.putUnsignedInt(position: Int, value: Long) {
    this.putInt(position, (value and 0xffffffffL).toInt())
}

fun ByteBuffer.getUnsignedLong(): BigInteger {
    val byteArray :ByteArray = ByteArray(8)
    this.get(byteArray, 0, byteArray.size)
    return BigInteger(1, byteArray)
}
