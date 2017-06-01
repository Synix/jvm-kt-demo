package io.github.synix.classfile

class MemberInfo(val constantPool: ConstantPool,
                 val accessFlags: Int,
                 private val nameIndex: Int,
                 private val descriptorIndex: Int,
                 val attributes: Array<AttributeInfo>) {
    val name: String
        get() = this.constantPool.getUtf8(this.nameIndex)

    val descriptor: String
        get() = this.constantPool.getUtf8(this.descriptorIndex)
}
