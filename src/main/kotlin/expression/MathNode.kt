package expression

interface ParameterSpace {
    operator fun get(parameterName : String) : Double
}

object EmptyParameterSpace : ParameterSpace {
    override fun get(parameterName: String): Double {
        throw NotImplementedError()
    }
}

class VariableParameterSpace(vararg pairs : Pair<String, Double>) : ParameterSpace {
    val map = mapOf(*pairs)
    override fun get(parameterName: String): Double {
        return map[parameterName]!!
    }
}

interface MathNode {
    fun evaluate(parameterSpace: ParameterSpace) : Double

    fun printTree() : String
    fun printExpr() : String
}

class ConstantNode(val value: Double) : MathNode {
    override fun evaluate(parameterSpace: ParameterSpace): Double {
        return value
    }

    override fun toString(): String {
        return printExpr()
    }

    override fun printTree(): String {
        return value.toString()
    }

    override fun printExpr(): String {
        return value.toString()
    }
}

class VariableNode(val name : String) : MathNode {
    override fun evaluate(parameterSpace: ParameterSpace): Double {
        return parameterSpace[name]
    }

    override fun toString(): String {
        return printExpr()
    }

    override fun printTree(): String {
        return name
    }

    override fun printExpr(): String {
        return name
    }
}

val Number.m : MathNode
    get() = ConstantNode(this.toDouble())
val String.m : MathNode
    get() = VariableNode(this)


fun MathNode.addToTwoSideMathNode(newOpKey: String, opFun: TwoSideFun, mathNode: MathNode): MathNode {
    val children = if (this is TwoSideOperatorNode && opKey == newOpKey) {
        children.toMutableList().apply {
            add(mathNode)
        }
    } else {
        mutableListOf(this, mathNode)
    }

    return TwoSideOperatorNode(
        children = children,
        opKey = newOpKey,
        foldingFunction = opFun,
    )
}

operator fun MathNode.plus(mathNode: MathNode) : MathNode {
    val newOpKey = "+"
    val newOpFun = Maths.plusFun

    return addToTwoSideMathNode(newOpKey, newOpFun, mathNode)
}

operator fun MathNode.minus(mathNode: MathNode) : MathNode {
    val newOpKey = "-"
    val newOpFun = Maths.minusFun

    return addToTwoSideMathNode(newOpKey, newOpFun, mathNode)
}

operator fun MathNode.times(mathNode: MathNode) : MathNode {
    val newOpKey = "*"
    val newOpFun = Maths.productFun

    return addToTwoSideMathNode(newOpKey, newOpFun, mathNode)
}

operator fun MathNode.unaryMinus() : MathNode {
    return UniOperatorNode(
        childNode = this,
        opKey = "-",
        operation = Maths.uniMinusFun
    )
}

operator fun MathNode.unaryPlus() : MathNode {
    return UniOperatorNode(
        childNode = this,
        opKey = "+",
        operation = Maths.uniPlusFun
    )
}

interface OperatorNode : MathNode {
    val children : List<MathNode>
    val opKey : String
}

class UniOperatorNode(private val childNode : MathNode,  override val opKey: String, private val operation : (value : Double) -> Double,

): OperatorNode {
    override val children: List<MathNode> = listOf(childNode)
    override fun evaluate(parameterSpace: ParameterSpace): Double {
        val value = childNode.evaluate(parameterSpace)
        return operation(value)
    }

    override fun toString(): String {
        return printExpr()
    }

    override fun printTree(): String {
        return """uni $opKey: 
            |${childNode.printTree().prependIndent("\t")}
        """.trimMargin()
    }

    override fun printExpr(): String {
        return "$opKey(${childNode.printExpr()})"
    }
}

class TwoSideOperatorNode(
    override val children: List<MathNode>,
    override val opKey: String,
    val foldingFunction : TwoSideFun,
) : OperatorNode {

    override fun toString(): String {
        return printExpr()
    }

    override fun evaluate(parameterSpace: ParameterSpace): Double {
        return children.subList(1, children.size).fold(children.first().evaluate(parameterSpace)) { accum, child ->
            foldingFunction(accum, child.evaluate(parameterSpace))
        }
    }

    override fun printTree(): String {
        return """multi $opKey:
            |${children.joinToString("\n") { it.printTree() }.prependIndent("\t")}
        """.trimMargin()
    }

    override fun printExpr(): String {
        return children.joinToString(separator = " $opKey ") { "(" + it.printExpr() + ")" }
    }
}

typealias TwoSideFun = (Double, Double) -> Double
typealias UniSideFun = (Double) -> Double

object Maths {
    fun minus(nodes : List<MathNode>): TwoSideOperatorNode {
        return TwoSideOperatorNode(nodes, "-", minusFun)
    }

    fun plus(nodes : List<MathNode>) : TwoSideOperatorNode {
        return TwoSideOperatorNode(nodes, "+", plusFun)
    }

    fun product(nodes : List<MathNode>) : TwoSideOperatorNode {
        return TwoSideOperatorNode(nodes, "*", productFun)
    }

    val uniMinusFun : UniSideFun = { value : Double ->
        -value
    }
    val uniPlusFun : UniSideFun = { value : Double ->
        +value
    }

    val minusFun: TwoSideFun = { left : Double, right : Double ->
        left - right
    }

    val plusFun : TwoSideFun = { left : Double, right : Double ->
        left + right
    }

    val productFun : TwoSideFun = { left : Double, right : Double ->
        left * right
    }

    val foldingFunctions = mutableMapOf(
        "+" to plusFun,
        "-" to minusFun,
        "*" to productFun
    )
}