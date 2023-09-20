import expression.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class NoBrackParserTest {

    @Test
    fun simpleExpTest() {
        val nodeSpace = NodeSpace()
        val noBrackExprParser = NoBrackExprParser(
            opsNodes = TokenParser.OpsNodes(
                ops = listOf("+", unminus, "*"),
                valueAndNodes = listOf("1", "3", "5"),
                nodeSpace = nodeSpace
            )
        )
        val resultingNode = noBrackExprParser.parseAndUpdateNodeSpace()

        Assertions.assertEquals(-14.0, resultingNode.evaluate(EmptyParameterSpace))
        println(resultingNode.printTree())
    }

// -3 + -4 * 5


// op stack: unmin + unmin *
// node stack: 3 4 5

    @Test
    fun valueIndexForOpsTest() {
        val nodeSpace = NodeSpace()
        val noBrackExprParser = NoBrackExprParser(
            opsNodes = TokenParser.OpsNodes(
                ops = listOf(unminus, "+", unminus, "*"),
                valueAndNodes = listOf("3", "4", "5"),
                nodeSpace = nodeSpace
            )
        )

        Assertions.assertEquals(0, noBrackExprParser.getValueIndexForOp(0)!!.requireSingle)
        Assertions.assertEquals(0..1, noBrackExprParser.getValueIndexForOp(1)!!.requireRange)
        Assertions.assertEquals(1, noBrackExprParser.getValueIndexForOp(2)!!.requireSingle)
        Assertions.assertEquals(1..2, noBrackExprParser.getValueIndexForOp(3)!!.requireRange)
    }

    @Test
    fun nodeReduction() {
        val nodeSpace = NodeSpace()
        val noBrackExprParser = NoBrackExprParser(
            opsNodes = TokenParser.OpsNodes(
                ops = listOf(unminus, "+", unminus, "*"),
                valueAndNodes = listOf("3", "4", "5"),
                nodeSpace = nodeSpace
            )
        )

        noBrackExprParser.fullTransform()
        println(noBrackExprParser.opStack)
        println(noBrackExprParser.valueAndNodes)
        println(nodeSpace)
    }

    @Test
    fun moreReductionTest() {
        val nodeSpace = NodeSpace()
        val noBrackExprParser = NoBrackExprParser(
            opsNodes = TokenParser.OpsNodes(
                ops = listOf(unminus, "+", unminus, "*"),
                valueAndNodes = listOf("3", "4", "5"),
                nodeSpace = nodeSpace
            )
        )

        noBrackExprParser.fullTransform()
        println(noBrackExprParser.opStack)
        println(noBrackExprParser.valueAndNodes)
        println(nodeSpace)
    }

    // TODO: priorities suck
    @Test
    fun complexExpTest() {
        val nodeSpace = NodeSpace()
        val noBrackExprParser = NoBrackExprParser(
            opsNodes = TokenParser.OpsNodes(
                ops = listOf(unminus, product, minus, unminus, plus, minus, product),
                valueAndNodes = listOf("10","5", "3", "20","1","2"),
                nodeSpace
            )
        )
        val expr = noBrackExprParser.parseAndUpdateNodeSpace()
        println(expr.printTree())
    }
}