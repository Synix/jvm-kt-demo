import io.github.synix.classfile.ClassReader
import io.github.synix.classfile.ConstantMethodrefInfo
import io.github.synix.classfile.ConstantPool
import io.github.synix.classfile.ConstantUtf8Info
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldEqual
import io.kotlintest.specs.FunSpec

class ConstantPoolTest: FunSpec() {
    fun String.hexStringToByteArray(): ByteArray {
        val len = this.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(this[i], 16) shl 4) + Character.digit(this[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    init {
        test("should parse Methodref") {
            val constantInfo = ConstantPool.readConstantInfo(
                    ClassReader("0A00060031".hexStringToByteArray()), ConstantPool())

            (constantInfo is ConstantMethodrefInfo) shouldBe true

            if (constantInfo is ConstantMethodrefInfo) {
                constantInfo.classIndex shouldEqual 6
                constantInfo.nameAndTypeIndex shouldEqual 49
            }
        }

        test("should parse Utf8") {
            val constantInfo = ConstantPool.readConstantInfo(
                    ClassReader("010004464C4147".hexStringToByteArray()), ConstantPool())

            (constantInfo is ConstantUtf8Info) shouldBe true

            if (constantInfo is ConstantUtf8Info) {
                constantInfo.str shouldEqual "FLAG"
            }
        }
    }
}