import io.github.synix.rtda.LocalVars
import io.github.synix.rtda.OperandStack
import io.kotlintest.matchers.plusOrMinus
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldEqual
import io.kotlintest.specs.FunSpec

class RuntimeDataAreaTest: FunSpec() {
    init {
        test("local variables table") {
            val vars = LocalVars(100)
            vars.setInt(0, 100)
            vars.setInt(1, -100)
            vars.setLong(2, 2997924580)
            vars.setLong(4, -2997924580)
            vars.setFloat(6, 3.1415926f)
            vars.setDouble(7, 2.71828182845)
            vars.setRef(9, null)

            vars.getInt(0) shouldEqual 100
            vars.getInt(1) shouldEqual -100
            vars.getLong(2) shouldEqual 2997924580
            vars.getLong(4) shouldEqual -2997924580
            vars.getFloat(6) shouldBe 3.1415926f
            vars.getDouble(7) shouldBe 2.71828182845.plusOrMinus(0.0001)
            vars.getRef(9) shouldBe null
        }

        test("operand stack") {
            val operandStack = OperandStack(100)
            operandStack.pushInt(100)
            operandStack.pushInt(-100)
            operandStack.pushLong(2997924580)
            operandStack.pushLong(-2997924580)
            operandStack.pushFloat(3.1415926f)
            operandStack.pushDouble(2.71828182845)
            operandStack.pushRef(null)

            operandStack.popRef() shouldBe null
            operandStack.popDouble() shouldBe 2.71828182845.plusOrMinus(0.0001)
            operandStack.popFloat() shouldBe 3.1415926f
            operandStack.popLong() shouldEqual -2997924580
            operandStack.popLong() shouldEqual 2997924580
            operandStack.popInt() shouldEqual -100
            operandStack.popInt() shouldEqual 100
        }
    }
}