package expression

class StringParser {
    var tokens = listOf<String>()
    fun acceptTokens(tokens: List<String>) {
        this.tokens = tokens
    }

//    fun parse(): MathNode? {
//
//    }
}


class NodeBuilder(val tokens: List<String>) {
    private val opStack: MutableList<String> = mutableListOf()
    private val valueNodeStack: MutableList<String> = mutableListOf()
    private val nodeSet: MutableList<MathNode> = mutableListOf()
    val nodeSpace = NodeSpace()

    fun reduce() {
        val closestOpOpenBrack = opStack.findLast { it == openBrack }
        val closestNodeOpenBrack = valueNodeStack.findLast { it == openBrack }


    }

    fun parseTokens() {
        var lastToken: String? = null

        for (token in tokens) {
            when {
                token in tokenOps -> {
                    val resultingToken = if (lastToken == openBrack || lastToken in tokenOps) {
                        if (token == "-") {
                            unminus
                        } else {
                            null
                        }
                    } else {
                        token
                    }
                    if (resultingToken != null) {
                        opStack.add(resultingToken)
                    }
                }

                token in brack -> {
                    valueNodeStack.add(token)
                    opStack.add(token)
                    if (token == closeBrack) {
                        reduce()
                    }
                }

                else -> {
                    val modified = if (token.toDoubleOrNull() != null) {
                        token.toDoubleOrNull()!!.toString()
                    } else if (variableMatcher.matches(token)) {
                        "var_$token"
                    } else {
                        throw IllegalStateException()
                    }
                    valueNodeStack.add(modified)
                }
            }
            lastToken = token
        }
    }

    fun buildExpression() {
        parseTokens()


    }
    class OpsNodes(
        val ops: List<String>,
        val valueAndNodes: List<String>,
        val nodeSpace: NodeSpace
    )
    data class ParsingRange(
        val opRange: IntRange,
        val valueRange: IntRange,
    )
}


//// 1 + -3 *(-3 * k) - 10
//
//// op
//+ * ( unmi * ) -
//// numb
//1 3 ( 3 k ) 10
//// nodes
//
//
//// op
//
//// numb
//
//// nodes
//
//
//a: (-3*k)
//b: 3*a
//c: 1+b
//d: c-10

const val unminus = "unminus"
const val plus = "+"
const val product = "*"
const val minus = "-"

val unaryOp = mutableSetOf("unminus")
val tokenOps = mutableSetOf("+", "-", "*")
val closeBrack = ")"
val openBrack = "("
val brack = mutableSetOf("(", ")")
val twoSideOp = mutableSetOf("+", "-", "*")
val variableMatcher = Regex("""[a-zA-Z]+[\da-zA-Z]*""")
val opPriority = mutableMapOf(
    "+" to 1,
    "-" to 1,
    "*" to 10,
    "unminus" to 100,
)