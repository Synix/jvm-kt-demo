package io.github.synix.classfile


object Attribute {
    fun readAttributes(reader: ClassReader, constantPool: ConstantPool): Array<AttributeInfo> {
        val attributesCount = reader.readUint16()
        return Array(attributesCount) {
            readAttributeInfo(reader, constantPool)
        }
    }

    fun readAttributeInfo(reader: ClassReader, constantPool: ConstantPool): AttributeInfo {
        with(reader) {
            val attrNameIndex = readUint16()
            val attrName = constantPool.getUtf8(attrNameIndex)
            val attrLen = readUint32()
            val attributeInfo = newAttributeInfo(attrName = attrName, attrLen = attrLen, constantPool = constantPool)
            attributeInfo.readInfo(this)
            return attributeInfo
        }
    }

    private fun newAttributeInfo(attrName: String, attrLen: Long, constantPool: ConstantPool): AttributeInfo {
        return when(attrName) {
            "Code" -> CodeAttribute(constantPool)
            "ConstantValue" -> ConstantValueAttribute()
            "Deprecated" -> DeprecatedAttribute()
            "Exceptions" -> ExceptionsAttribute()
            "LineNumberTable" -> LineNumberTableAttribute()
            "LocalVariableTable" -> LocalVariableTableAttribute()
            "SourceFile" -> SourceFileAttribute(constantPool)
            "Synthetic" -> SyntheticAttribute()
            else -> UnparsedAttribute(attrName, attrLen)
        }
    }
}


interface AttributeInfo {
    fun readInfo(reader: ClassReader)
}


class CodeAttribute(val constantPool: ConstantPool) : AttributeInfo {
    data class ExceptionTableEntry(val startPc: Int,
                                   val endPc: Int,
                                   val handlerPc: Int,
                                   val catchType: Int)

    private var maxStack: Int = 0
    private var maxLocals: Int = 0
    lateinit private var code: ByteArray
    lateinit private var exceptionTable: Array<ExceptionTableEntry>
    lateinit private var attributes: Array<AttributeInfo>

    override fun readInfo(reader: ClassReader) {
        with(reader) {
            maxStack = readUint16()
            maxLocals = readUint16()
            val codeLength = readUint32()
            code = readBytes(codeLength.toInt())
            exceptionTable = readExceptionTable(this)
            attributes = Attribute.readAttributes(reader, constantPool)
        }
    }

    private fun readExceptionTable(reader: ClassReader): Array<ExceptionTableEntry> {
        val exceptionTableLength = reader.readUint16()
        return Array<ExceptionTableEntry>(exceptionTableLength) {
            ExceptionTableEntry(startPc = reader.readUint16(),
                                endPc = reader.readUint16(),
                                handlerPc = reader.readUint16(),
                                catchType = reader.readUint16())
        }
    }
}

class ConstantValueAttribute : AttributeInfo {
    var constantValueIndex: Int = 0
    override fun readInfo(reader: ClassReader) {
        this.constantValueIndex = reader.readUint16()
    }

}



class ExceptionsAttribute : AttributeInfo {
    lateinit var exceptionIndexTable: Array<Int>
    override fun readInfo(reader: ClassReader) {
        this.exceptionIndexTable = reader.readUint16s()
    }
}

class LineNumberTableAttribute : AttributeInfo {
    data class LineNumberTableEntry(val startPc: Int,
                                    val lineNumber: Int)

    lateinit private var lineNumberTable: Array<LineNumberTableEntry>

    override fun readInfo(reader: ClassReader) {
        val lineNumberTableLength = reader.readUint16()
        this.lineNumberTable = Array<LineNumberTableEntry>(lineNumberTableLength) {
            LineNumberTableEntry(startPc = reader.readUint16(), lineNumber = reader.readUint16())
        }
    }
}

class LocalVariableTableAttribute : AttributeInfo {
    data class LocalVariableTableEntry(val startPc: Int,
                                       val indexlength: Int,
                                       val nameIndex: Int,
                                       val descriptorIndex: Int,
                                       val index: Int)

    lateinit private var localVariableTable: Array<LocalVariableTableEntry>

    override fun readInfo(reader: ClassReader) {
        val localVariableTableLength = reader.readUint16()
        this.localVariableTable = Array<LocalVariableTableEntry>(localVariableTableLength) {
            LocalVariableTableEntry(startPc = reader.readUint16(), indexlength = reader.readUint16(),
                    nameIndex = reader.readUint16(), descriptorIndex = reader.readUint16(),
                    index = reader.readUint16())
        }
    }
}

class SourceFileAttribute(val constantPool: ConstantPool) : AttributeInfo {
    private var sourceFileIndex: Int = 0
    val fileName: String
        get() = this.constantPool.getUtf8(this.sourceFileIndex)
    override fun readInfo(reader: ClassReader) {
        this.sourceFileIndex = reader.readUint16()
    }

}

open class MarkerAttribute: AttributeInfo {
    override fun readInfo(reader: ClassReader) {
        // read nothing
    }
}

class DeprecatedAttribute : MarkerAttribute()
class SyntheticAttribute : MarkerAttribute()



class UnparsedAttribute(val attrName: String, val attrLen: Long) : AttributeInfo {
    lateinit var bytes: ByteArray
    override fun readInfo(reader: ClassReader) {
        bytes = reader.readBytes(attrLen.toInt())
    }
}
