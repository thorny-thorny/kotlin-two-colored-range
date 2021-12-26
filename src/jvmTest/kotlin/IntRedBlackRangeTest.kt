import me.thorny.twoColoredRange.IntRedBlackRange
import me.thorny.twoColoredRange.RedBlackColor
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

internal class IntRedBlackRangeTest {
  @Test
  fun testLength() {
    assertEquals(IntRedBlackRange(1..2).length, 2)
  }

  @Test
  fun testConstructorExceptions() {
    assertDoesNotThrow { IntRedBlackRange(1..2) }
    assertDoesNotThrow { IntRedBlackRange(1..1) }
    assertThrows<Exception> { IntRedBlackRange(2..1) }
  }

  @Test
  fun testInitialSubranges() {
    val range = IntRedBlackRange(1..2)
    assertContentEquals(range.getRedSubranges(), arrayListOf(1..2))
    assertContentEquals(range.getBlackSubranges(), emptyList())
    assertContentEquals(range.getSubrangesOfColor(RedBlackColor.RED), arrayListOf(1..2))
    assertContentEquals(range.getSubrangesOfColor(RedBlackColor.BLACK), emptyList())
  }

  @Test
  fun testSubrangesExceptions() {
    assertDoesNotThrow { IntRedBlackRange(1..3).setSubrangeBlack(1..3) }
    assertThrows<Exception> { IntRedBlackRange(1..3).setSubrangeBlack(0..4) }
    assertThrows<Exception> { IntRedBlackRange(1..3).setSubrangeBlack(3..1) }
  }

  @Test
  fun testSubranges() {
    val range = IntRedBlackRange(1..7)
    range.setSubrangeBlack(4..4)
    assertContentEquals(range.getBlackSubranges(), arrayListOf(4..4))
    // Add same range - subranges should stay same
    range.setSubrangeBlack(4..4)
    assertContentEquals(range.getBlackSubranges(), arrayListOf(4..4))
    // Add greater range - subranges should have union
    range.setSubrangeBlack(4..5)
    assertContentEquals(range.getBlackSubranges(), arrayListOf(4..5))
    // Add far range on left - should be added before existing subrange
    range.setSubrangeBlack(1..1)
    assertContentEquals(range.getBlackSubranges(), arrayListOf(1..1, 4..5))
    // Add close range on left - should expand subrange
    range.setSubrangeBlack(3..3)
    assertContentEquals(range.getBlackSubranges(), arrayListOf(1..1, 3..5))
  }
}
