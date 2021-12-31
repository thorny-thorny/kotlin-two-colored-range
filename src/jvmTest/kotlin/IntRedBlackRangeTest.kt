import me.thorny.twoColoredRange.IntRedBlackRange
import me.thorny.twoColoredRange.RedBlackColor
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

internal class IntRedBlackRangeTest {
  @Test
  fun testConstructorExceptions() {
    assertDoesNotThrow { IntRedBlackRange(1..2) }
    assertDoesNotThrow { IntRedBlackRange(1..1) }
    assertThrows<Exception> { IntRedBlackRange(1..0) }
  }

  @Test
  fun testLengths() {
    assertEquals(IntRedBlackRange(1..2).length, 2)
  }

  @Test
  fun testBasicSubrangesGetters() {
    val range = IntRedBlackRange(1..2)
    assertContentEquals(range.getRedSubranges(), arrayListOf(1..2))
    assertContentEquals(range.getBlackSubranges(), emptyList())
    assertContentEquals(range.getSubrangesOfColor(RedBlackColor.RED), arrayListOf(1..2))
    assertContentEquals(range.getSubrangesOfColor(RedBlackColor.BLACK), emptyList())
  }

  @Test
  fun testSubrangesExceptions() {
    assertDoesNotThrow { IntRedBlackRange(1..3).setSubrangeBlack(1..3) }
    assertThrows<Exception> { IntRedBlackRange(1..3).setSubrangeBlack(0..3) }
    assertThrows<Exception> { IntRedBlackRange(1..3).setSubrangeBlack(1..4) }
    assertThrows<Exception> { IntRedBlackRange(1..3).setSubrangeBlack(0..4) }
    assertThrows<Exception> { IntRedBlackRange(1..3).setSubrangeBlack(3..1) }
  }

  @Test
  fun testBasicSubranges() {
    val range = IntRedBlackRange(1..11)
    // Add subrange
    range.setSubrangeBlack(6..6)
    assertContentEquals(range.getBlackSubranges(), arrayListOf(6..6))
    // Add far range on left - should be added before existing subrange
    range.setSubrangeBlack(1..1)
    assertContentEquals(range.getBlackSubranges(), arrayListOf(1..1, 6..6))
    // Add far range on right - should be added after existing subrange
    range.setSubrangeBlack(11..11)
    assertContentEquals(range.getBlackSubranges(), arrayListOf(1..1, 6..6, 11..11))
    // Add range in middle between other ranges
    range.setSubrangeBlack(3..3)
    range.setSubrangeBlack(9..9)
    assertContentEquals(range.getBlackSubranges(), arrayListOf(1..1, 3..3, 6..6, 9..9, 11..11))
  }

  @Test
  fun testSingleIntersectingSubranges() {
    val range = IntRedBlackRange(1..11)
    // Add same ranges - subranges should have one copy
    range.setSubrangeBlack(6..6)
    range.setSubrangeBlack(6..6)
    assertContentEquals(range.getBlackSubranges(), arrayListOf(6..6))
    // Add greater-right range - subranges should have union
    range.setSubrangeBlack(6..7)
    assertContentEquals(range.getBlackSubranges(), arrayListOf(6..7))
    // Add greater-left range - subranges should have union
    range.setSubrangeBlack(5..7)
    assertContentEquals(range.getBlackSubranges(), arrayListOf(5..7))
    // Add range intersecting on left
    range.setSubrangeBlack(4..6)
    assertContentEquals(range.getBlackSubranges(), arrayListOf(4..7))
    // Add range intersecting on right
    range.setSubrangeBlack(6..8)
    assertContentEquals(range.getBlackSubranges(), arrayListOf(4..8))
    // Add greater-both range
    range.setSubrangeBlack(3..9)
    assertContentEquals(range.getBlackSubranges(), arrayListOf(3..9))
  }

  @Test
  fun testMultipleIntersectingSubranges() {
    var range = IntRedBlackRange(1..11)
    range.setSubrangeBlack(2..3)
    range.setSubrangeBlack(5..6)
    // Add two-intersection subrange
    range.setSubrangeBlack(3..5)
    assertContentEquals(range.getBlackSubranges(), arrayListOf(2..6))

    range = IntRedBlackRange(1..11)
    range.setSubrangeBlack(2..3)
    range.setSubrangeBlack(5..6)
    // Add two-intersection subrange touching ends
    range.setSubrangeBlack(2..6)
    assertContentEquals(range.getBlackSubranges(), arrayListOf(2..6))

    range = IntRedBlackRange(1..11)
    range.setSubrangeBlack(2..3)
    range.setSubrangeBlack(5..6)
    // Add two-intersection greater subrange
    range.setSubrangeBlack(1..7)
    assertContentEquals(range.getBlackSubranges(), arrayListOf(1..7))

    range = IntRedBlackRange(1..11)
    range.setSubrangeBlack(2..3)
    range.setSubrangeBlack(5..6)
    range.setSubrangeBlack(8..9)
    // Add three-intersection subrange
    range.setSubrangeBlack(3..8)
    assertContentEquals(range.getBlackSubranges(), arrayListOf(2..9))
  }

  @Test
  fun testTouchingSubranges() {
    val range = IntRedBlackRange(1..11)
    range.setSubrangeBlack(6..6)
    // Add close range on left - should expand subrange
    range.setSubrangeBlack(4..5)
    assertContentEquals(range.getBlackSubranges(), arrayListOf(4..6))
    // Add close range on right - should expand subrange
    range.setSubrangeBlack(7..8)
    assertContentEquals(range.getBlackSubranges(), arrayListOf(4..8))
    // Add close range between ranges
    range.setSubrangeBlack(1..2)
    range.setSubrangeBlack(3..3)
    assertContentEquals(range.getBlackSubranges(), arrayListOf(1..8))
  }

  @Test
  fun testTouchingIntersectingSubranges() {
    var range = IntRedBlackRange(1..11)
    range.setSubrangeBlack(2..3)
    range.setSubrangeBlack(5..6)
    // Add intersecting-left touching-right range
    range.setSubrangeBlack(3..4)
    assertContentEquals(range.getBlackSubranges(), arrayListOf(2..6))

    range = IntRedBlackRange(1..11)
    range.setSubrangeBlack(2..3)
    range.setSubrangeBlack(5..6)
    // Add touching-left intersecting-right range
    range.setSubrangeBlack(4..5)
    assertContentEquals(range.getBlackSubranges(), arrayListOf(2..6))

    range = IntRedBlackRange(1..11)
    range.setSubrangeBlack(2..3)
    range.setSubrangeBlack(5..6)
    // Add containing-left touching-right range
    range.setSubrangeBlack(1..4)
    assertContentEquals(range.getBlackSubranges(), arrayListOf(1..6))

    range = IntRedBlackRange(1..11)
    range.setSubrangeBlack(2..3)
    range.setSubrangeBlack(5..6)
    // Add touching-left containing-right range
    range.setSubrangeBlack(3..7)
    assertContentEquals(range.getBlackSubranges(), arrayListOf(2..7))

    range = IntRedBlackRange(1..11)
    range.setSubrangeBlack(2..3)
    range.setSubrangeBlack(5..6)
    range.setSubrangeBlack(8..9)
    // Add intersecting-left containing-middle touching-right range
    range.setSubrangeBlack(3..7)
    assertContentEquals(range.getBlackSubranges(), arrayListOf(2..9))

    range = IntRedBlackRange(1..11)
    range.setSubrangeBlack(2..3)
    range.setSubrangeBlack(5..6)
    range.setSubrangeBlack(8..9)
    // Add touching-left containing-middle intersecting-right range
    range.setSubrangeBlack(4..8)
    assertContentEquals(range.getBlackSubranges(), arrayListOf(2..9))
  }
}
