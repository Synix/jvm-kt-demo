import io.github.synix.classfile.ClassFile
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldEqual
import io.kotlintest.specs.FunSpec
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class ClassReaderTest : FunSpec() {
    init {
        test("should parse ClassFileSample.class") {
            val classPath: Path = File("./build/classes/main/io/github/synix/test/ClassFileSample.class").toPath()
            val classData = Files.readAllBytes(classPath)
            val parsedClassFile = ClassFile.parse(classData)

            parsedClassFile.magic shouldBe 0xCAFEBABE

            parsedClassFile.majorVersion shouldBe 52
            parsedClassFile.minorVersion shouldBe 0

            parsedClassFile.constantPool.constantInfos.size shouldEqual 64

            parsedClassFile.accessFlags shouldBe 0x0021
            parsedClassFile.className shouldEqual "io/github/synix/test/ClassFileSample"
            parsedClassFile.superClassName shouldEqual "java/lang/Object"

            parsedClassFile.interfaceNames?.size shouldBe 0
            parsedClassFile.fields?.size shouldBe 8
            parsedClassFile.methods?.size shouldBe 2
            parsedClassFile.attributes?.size shouldBe 1
        }
    }
}