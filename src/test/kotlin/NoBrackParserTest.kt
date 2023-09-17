import expression.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class NoBrackParserTest {

    @Test
    fun simpleExpTest() {
        val nodeSpace = NodeSpace()
        val noBrackExprParser = NoBrackExprParser(
            opsNodes = NodeBuilder.OpsNodes(
                ops = listOf("+", unminus, "*"),
                valueAndNodes = listOf("1", "3", "5"),
                nodeSpace = nodeSpace
            )
        )
        val resultingNode = noBrackExprParser.parseAndUpdateNodeSpace()

        Assertions.assertEquals(-14.0, resultingNode.evaluate(EmptyParameterSpace))
        println(resultingNode.printTree())
    }

    // TODO: priorities suck
    @Test
    fun complexExpTest() {
        val nodeSpace = NodeSpace()
        val noBrackExprParser = NoBrackExprParser(
            opsNodes = NodeBuilder.OpsNodes(
                ops = listOf(unminus, product, minus, unminus, plus, minus, product),
                valueAndNodes = listOf("10","5", "3", "20","1","2"),
                nodeSpace
            )
        )
        val expr = noBrackExprParser.parseAndUpdateNodeSpace()
        println(expr.printTree())

    }
}