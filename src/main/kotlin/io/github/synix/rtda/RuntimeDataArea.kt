package io.github.synix.rtda

// object allocated in heap
class Object {
    // TODO
}

// thread with a runtime stack
class Thread(val maxStackSize: Int) {
    var pc: Int = 0     // point to current frame
    val stack = Stack(maxStackSize)

    fun pushFrame(frame: Frame) = stack.push(frame)
    fun popFrame(): Frame = stack.pop()
    fun currentFrame(): Frame = stack.top()
}


// stack
class Stack(val maxSize: Int) {
    var size: Int = 0
    var _top: Frame? = null

    fun push(frame: Frame): Unit {
        if (size >= maxSize) {
            throw StackOverflowError()
        }

        if (_top != null) {
            frame.lower = _top
        }

        _top = frame
        size++
    }

    fun pop(): Frame {
        if (_top == null) {
            throw Exception("jvm stack is empty!")
        }

        val top = _top
        _top = top!!.lower
        top.lower = null
        size--
        return top
    }

    fun top(): Frame {
        if (_top == null) {
            throw Exception("jvm stack is empty!")
        }
        return _top!!
    }
}


// stack frame
class Frame(maxLocals: Int, maxStack: Int) {
    val localVars = LocalVars(maxLocals)
    val operandStack = OperandStack(maxStack)
    var lower: Frame? = null
}


// local variables table
class LocalVars(maxLocals: Int) {
    var table: Array<Slot> = Array(maxLocals) {
        Slot()
    }

    fun setInt(index: Int, value: Int): Unit {
        table[index].num = value
    }

    fun getInt(index: Int): Int {
        return table[index].num
    }

    fun setFloat(index: Int, value: Float): Unit {
        table[index].num = java.lang.Float.floatToIntBits(value)
    }

    fun getFloat(index: Int): Float {
        return java.lang.Float.intBitsToFloat(table[index].num)
    }

    fun setLong(index: Int, value: Long): Unit {
        table[index].num = (value and 0xFFFFFFFF).toInt()
        table[index + 1].num = (value shr 32).toInt()
    }

    fun getLong(index: Int): Long {
        val low = table[index].num
        val high = table[index + 1].num
        return (high.toLong() shl 32) or (low.toLong() and 0xFFFFFFFF)
    }

    fun setDouble(index: Int, value: Double): Unit {
        val bits = java.lang.Double.doubleToLongBits(value)
        setLong(index, bits)
    }

    fun getDouble(index: Int): Double {
        val bits = getLong(index)
        return java.lang.Double.longBitsToDouble(bits)
    }

    fun setRef(index: Int, ref: Int?): Unit {
        table[index].ref = ref
    }

    fun getRef(index: Int): Int? {
        return table[index].ref
    }
}


// operands stack
class OperandStack(maxStack: Int) {
    var size: Int = 0
    var stack: Array<Slot> = Array(maxStack) {
        Slot()
    }

    fun pushInt(value: Int): Unit {
        stack[size].num = value
        size++
    }

    fun popInt(): Int {
        size--
        return stack[size].num
    }

    fun pushFloat(value: Float): Unit {
        stack[size].num = java.lang.Float.floatToIntBits(value)
        size++
    }

    fun popFloat(): Float {
        size--
        return java.lang.Float.intBitsToFloat(stack[size].num)
    }

    fun pushLong(value: Long): Unit {
        stack[size].num = (value and 0xFFFFFFFF).toInt()
        stack[size + 1].num = (value shr 32).toInt()
        size += 2
    }

    fun popLong(): Long {
        size -= 2
        val low = stack[size].num
        val high = stack[size + 1].num

        return (high.toLong() shl 32) or (low.toLong() and 0xFFFFFFFF)
    }

    fun pushDouble(value: Double): Unit {
        val bits = java.lang.Double.doubleToLongBits(value)
        pushLong(bits)
    }

    fun popDouble(): Double {
        val bits = popLong()
        return java.lang.Double.longBitsToDouble(bits)
    }

    fun pushRef(value: Int?): Unit {
        stack[size].ref = value
        size++
    }

    fun popRef(): Int? {
        size--
        return stack[size].ref
    }
}

// slot in local variables table/operands stack
class Slot {
    var num: Int = 0    // primitive type
    var ref: Int? = null    // reference type

}
