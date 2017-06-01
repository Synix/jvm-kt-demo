package io.github.synix.classfile

class ClassFile(val classReader: ClassReader) {
    companion object {
        fun parse(classData: ByteArray): ClassFile {
            val classFile = ClassFile(ClassReader(classData))
            classFile.read()
            return classFile
        }
    }

    var magic: Long? = null
    var minorVersion: Int = 0
    var majorVersion: Int = 0
    val constantPool: ConstantPool = ConstantPool()
    var accessFlags: Int = 0
    private var thisClass: Int = 0
    private var superClass: Int = 0
    private var interfaces: Array<Int>? = null
    var fields: Array<MemberInfo>? = null
    var methods: Array<MemberInfo>? = null
    var attributes: Array<AttributeInfo>? = null


    val className: String
        get() = this.constantPool.getClassName(this.thisClass)

    val superClassName: String
        get() = this.constantPool.getClassName(this.superClass)

    val interfaceNames: Array<String>?
        get() {
            if (this.interfaces != null) {
                return Array(this.interfaces!!.size) {
                    this.constantPool.getClassName(this.interfaces!![it])
                }
            } else {
                return null
            }
        }



    fun read() {
        readAndCheckMagic()
        readAndCheckVersion()

        readConstantPool()

        with(classReader) {
            accessFlags = readUint16()
            thisClass = readUint16()
            superClass = readUint16()
            interfaces = readUint16s()

            fields = readMembers()
            methods = readMembers()
            attributes = Attribute.readAttributes(classReader, constantPool)
        }
    }

    fun readAndCheckMagic() {
        this.magic = this.classReader.readUint32()
        if (this.magic != 0xCAFEBABE) {
            throw ClassFormatError("java.lang.ClassFormatError: magic!")
        }
    }

    fun readAndCheckVersion() {
        this.minorVersion = this.classReader.readUint16()
        this.majorVersion = this.classReader.readUint16()
        when(this.majorVersion) {
            45 -> return
            46, 47, 48, 49, 50, 51, 52 ->
                if (this.minorVersion == 0) {
                    return
                }
            else -> throw UnsupportedClassVersionError(
                    "Unsupport class version: ${this.majorVersion}.${this.minorVersion}")
        }
    }

    fun readConstantPool() {
        val cpCount = this.classReader.readUint16()

        this.constantPool.constantInfos.add(null)

        var i: Int = 1
        while (i < cpCount) {
            val constantInfo = ConstantPool.readConstantInfo(this.classReader, this.constantPool)
            this.constantPool.constantInfos.add(constantInfo)
            when(constantInfo) {
                is ConstantLongInfo, is ConstantDoubleInfo  -> {
                    this.constantPool.constantInfos.add(null)
                    i = i + 2 }
                else -> i++
            }
        }
    }


    private fun readMembers(): Array<MemberInfo> {
        val memberCount = this.classReader.readUint16()
        return Array(memberCount) {
            readMember()
        }
    }

    private fun readMember(): MemberInfo {
        return MemberInfo(constantPool = this.constantPool,
                accessFlags = this.classReader.readUint16(),
                nameIndex = this.classReader.readUint16(),
                descriptorIndex = this.classReader.readUint16(),
                attributes = Attribute.readAttributes(this.classReader, this.constantPool))
    }

    override fun toString(): String {
        return "ClassFile:\n" +
                "magic: ${Integer.toHexString(magic?.toInt() as Int)} \n" +
                "version: $majorVersion.$minorVersion \n" +
                "constants count: ${constantPool.constantInfos.size} \n" +
                "access flags: ${Integer.toHexString(accessFlags)} \n" +
                "this class: ${className} \n" +
                "super class: ${superClassName} \n" +
                "interfaces: ${interfaceNames?.joinToString(", ")} \n" +
                "fields count: ${fields?.size} ${fields?.map(MemberInfo::name)?.toList()?.joinToString(", ")} \n" +
                "methods count: ${methods?.size} ${methods?.map(MemberInfo::name)?.toList()?.joinToString(", ")}"
    }
}


