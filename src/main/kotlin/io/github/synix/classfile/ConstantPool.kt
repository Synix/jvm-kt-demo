package io.github.synix.classfile

import java.nio.charset.StandardCharsets


class ConstantPool {
    val constantInfos = mutableListOf<ConstantInfo?>()

    fun getConstantInfo(index: Int): ConstantInfo? {
        return constantInfos[index]
    }

    fun getNameAndType(index: Int): Pair<String, String> {
        val nameAndTypeInfo = this.getConstantInfo(index) as ConstantNameAndTypeInfo
        return (nameAndTypeInfo.name to nameAndTypeInfo.descriptor)
    }

    fun getClassName(index: Int): String {
        val classInfo = this.getConstantInfo(index) as ConstantClassInfo
        return classInfo.name
    }

    fun getUtf8(index: Int): String {
        val utf8Info = this.getConstantInfo(index) as ConstantUtf8Info
        return utf8Info.str
    }

    companion object {
        fun readConstantInfo(reader: ClassReader, constantPool: ConstantPool): ConstantInfo {
            val tag = reader.readUint8()
            val constantInfo = newConstantInfo(ConstantType.from(tag), constantPool)
            constantInfo.readInfo(reader)
            return constantInfo
        }

        private fun  newConstantInfo(tag: ConstantType, constantPool: ConstantPool): ConstantInfo {
            return when(tag) {
                ConstantType.CONSTANT_Class -> ConstantClassInfo(constantPool)
                ConstantType.CONSTANT_Fieldref -> ConstantFieldrefInfo(constantPool)
                ConstantType.CONSTANT_Methodref -> ConstantMethodrefInfo(constantPool)
                ConstantType.CONSTANT_InterfaceMethodref -> ConstantInterfaceMethodrefInfo(constantPool)
                ConstantType.CONSTANT_String -> ConstantStringInfo(constantPool)
                ConstantType.CONSTANT_Integer -> ConstantIntegerInfo(constantPool)
                ConstantType.CONSTANT_Float -> ConstantFloatInfo(constantPool)
                ConstantType.CONSTANT_Long -> ConstantLongInfo(constantPool)
                ConstantType.CONSTANT_Double -> ConstantDoubleInfo(constantPool)
                ConstantType.CONSTANT_NameAndType -> ConstantNameAndTypeInfo(constantPool)
                ConstantType.CONSTANT_Utf8 -> ConstantUtf8Info(constantPool)
                ConstantType.CONSTANT_MethodHandle -> ConstantMethodHandleInfo(constantPool)
                ConstantType.CONSTANT_MethodType -> ConstantMethodTypeInfo(constantPool)
                ConstantType.CONSTANT_InvokeDynamic -> ConstantInvokeDynamicInfo(constantPool)

            }
        }
    }
}


enum class ConstantType(val tag: Short) {
    CONSTANT_Class(7),
    CONSTANT_Fieldref(9),
    CONSTANT_Methodref(10),
    CONSTANT_InterfaceMethodref(11),
    CONSTANT_String(8),
    CONSTANT_Integer(3),
    CONSTANT_Float(4),
    CONSTANT_Long(5),
    CONSTANT_Double(6),
    CONSTANT_NameAndType(12),
    CONSTANT_Utf8(1),
    CONSTANT_MethodHandle(15),
    CONSTANT_MethodType(16),
    CONSTANT_InvokeDynamic(18);

    companion object {
        fun from(findValue: Short): ConstantType = ConstantType.values().first { it.tag == findValue }
    }
}

interface ConstantInfo {
    fun readInfo(reader: ClassReader)
}

class ConstantStringInfo(val constantPool: ConstantPool) : ConstantInfo {
    private var stringIndex: Int = 0
    val string: String
        get() = this.constantPool.getUtf8(stringIndex)

    override fun readInfo(reader: ClassReader) {
        this.stringIndex = reader.readUint16()
    }
}


class ConstantClassInfo(val constantPool: ConstantPool) : ConstantInfo {
    private var nameIndex: Int = 0
    val name: String
        get() = this.constantPool.getUtf8(nameIndex)

    override fun readInfo(reader: ClassReader) {
        this.nameIndex = reader.readUint16()
    }
}


class ConstantNameAndTypeInfo(val constantPool: ConstantPool) : ConstantInfo {
    private var nameIndex: Int = 0
    private var descriptorIndex: Int = 0
    val name: String
        get() = this.constantPool.getUtf8(nameIndex)
    val descriptor: String
        get() = this.constantPool.getUtf8(descriptorIndex)

    override fun readInfo(reader: ClassReader) {
        this.nameIndex = reader.readUint16()
        this.descriptorIndex = reader.readUint16()
    }
}


open class ConstantMemberrefInfo(val constantPool: ConstantPool) : ConstantInfo {
    var classIndex: Int = 0
    var nameAndTypeIndex: Int = 0
    val className: String
        get() = this.constantPool.getUtf8(classIndex)
    val nameAndDescriptor: String
        get() = this.constantPool.getUtf8(nameAndTypeIndex)


    override fun readInfo(reader: ClassReader) {
        this.classIndex = reader.readUint16()
        this.nameAndTypeIndex = reader.readUint16()
    }

}

class ConstantFieldrefInfo(constantPool: ConstantPool) : ConstantMemberrefInfo(constantPool)
class ConstantMethodrefInfo(constantPool: ConstantPool) : ConstantMemberrefInfo(constantPool)
class ConstantInterfaceMethodrefInfo(constantPool: ConstantPool) : ConstantMemberrefInfo(constantPool)

// TODO
class ConstantInvokeDynamicInfo(constantPool: ConstantPool) : ConstantInfo {
    var bootstrapMethodAttrIndex: Int = 0
    var nameAndTypeIndex: Int = 0
    override fun readInfo(reader: ClassReader) {
        this.bootstrapMethodAttrIndex = reader.readUint16()
        this.nameAndTypeIndex = reader.readUint16()
    }
}

class ConstantMethodTypeInfo(constantPool: ConstantPool) : ConstantInfo {
    var descriptorIndex: Int = 0
    override fun readInfo(reader: ClassReader) {
        this.descriptorIndex = reader.readUint16()
    }
}

class ConstantMethodHandleInfo(constantPool: ConstantPool) : ConstantInfo {
    var referenceKind: Short = 0
    var referenceIndex: Int = 0
    override fun readInfo(reader: ClassReader) {
        this.referenceKind = reader.readUint8()
        this.referenceIndex = reader.readUint16()
    }

}


// stand for int/short/char/byte/boolean constant
class ConstantIntegerInfo(constantPool: ConstantPool): ConstantInfo {
    var int32: Int = 0

    override fun readInfo(reader: ClassReader) {
        this.int32 = reader.readUint32().toInt()
    }
}

class ConstantDoubleInfo(constantPool: ConstantPool) : ConstantInfo {
    var double64: Double = 0.0
    override fun readInfo(reader: ClassReader) {
        this.double64 = reader.readDouble()
    }

}

class ConstantLongInfo(constantPool: ConstantPool) : ConstantInfo {
    var long64: Long = 0
    override fun readInfo(reader: ClassReader) {
        this.long64 = reader.readUint64().toLong()
    }
}

class ConstantFloatInfo(constantPool: ConstantPool) : ConstantInfo {
    var float32: Float = 0.0f
    override fun readInfo(reader: ClassReader) {
        this.float32 = reader.readFloat()
    }
}


class ConstantUtf8Info(constantPool: ConstantPool) : ConstantInfo {
    lateinit var str: String
    override fun readInfo(reader: ClassReader) {
        val length = reader.readUint16()
        val bytes = reader.readBytes(length)
        this.str = String(bytes, StandardCharsets.UTF_8)
    }

}