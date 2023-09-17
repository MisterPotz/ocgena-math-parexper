import expression.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ArithmeticTest {

    @Test
    fun opprecedence() {
        val m5 = (-5).m
        val p10 = 10.m

        val expr = m5 + p10 * p10

        Assertions.assertTrue(expr.evaluate(EmptyParameterSpace) == 95.0)
    }

    @Test
    fun associativity() {
        val m5 = (-5).m
        val p10 = 10.m

        val expr = (m5 + p10) * p10

        Assertions.assertEquals(50.0 , expr.evaluate(EmptyParameterSpace))
    }

    @Test
    fun commutativity() {
        val m5 = (-5).m
        val p10 = 10.m

        Assertions.assertEquals(5.0, (m5 + p10).evaluate(EmptyParameterSpace))
        Assertions.assertEquals(5.0 , (p10 + m5).evaluate(EmptyParameterSpace))
    }

    @Test
    fun unary() {
        val m5 = -(5.m)
        val p10 = 10.m

        Assertions.assertEquals(5.0, (m5 + p10).evaluate(EmptyParameterSpace))
        Assertions.assertEquals(5.0 , (p10 + m5).evaluate(EmptyParameterSpace))
    }

    @Test
    fun complexExpr() {
        val m5 = -(5.m)
        val p10 = 10.m

        val expr = 3.m-10.m*-(5.m)+7.m

        println(expr.printTree())
        println(expr.printExpr())

        val value =  expr.evaluate(EmptyParameterSpace)
        Assertions.assertEquals(60.0, value)
    }
}