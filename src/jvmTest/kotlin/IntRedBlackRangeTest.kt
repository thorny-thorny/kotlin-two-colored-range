import me.thorny.twoColoredRange.IntRedBlackRange
import me.thorny.twoColoredRange.RedBlackColor
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

internal class IntRedBlackRangeTest {
  fun eachColor(action: (color: RedBlackColor, otherColor: RedBlackColor) -> Unit) {
    action(RedBlackColor.BLACK, RedBlackColor.RED)
    action(RedBlackColor.RED, RedBlackColor.BLACK)
  }

  fun rangeOfColor(range: IntRange, color: RedBlackColor): IntRedBlackRange {
    val redBlackRange = IntRedBlackRange(range)
    redBlackRange.setSubrangeColor(range, color)
    return redBlackRange
  }

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
    assertContentEquals(range.getRedSubranges(), listOf(1..2))
    assertContentEquals(range.getBlackSubranges(), emptyList())
    assertContentEquals(range.getSubrangesOfColor(RedBlackColor.RED), listOf(1..2))
    assertContentEquals(range.getSubrangesOfColor(RedBlackColor.BLACK), emptyList())
    range.setSubrangeBlack(1..2)
    assertContentEquals(range.getRedSubranges(), emptyList())
    assertContentEquals(range.getBlackSubranges(), listOf(1..2))
    assertContentEquals(range.getSubrangesOfColor(RedBlackColor.RED), emptyList())
    assertContentEquals(range.getSubrangesOfColor(RedBlackColor.BLACK), listOf(1..2))
  }

  @Test
  fun testSubrangesExceptions() {
    eachColor { color, _ ->
      assertDoesNotThrow { IntRedBlackRange(1..3).setSubrangeColor(1..3, color) }
      assertThrows<Exception> { IntRedBlackRange(1..3).setSubrangeColor(0..3, color) }
      assertThrows<Exception> { IntRedBlackRange(1..3).setSubrangeColor(1..4, color) }
      assertThrows<Exception> { IntRedBlackRange(1..3).setSubrangeColor(0..4, color) }
      assertThrows<Exception> { IntRedBlackRange(1..3).setSubrangeColor(3..1, color) }
    }
  }

  @Test
  fun testBasicSubranges() {
    eachColor { color, otherColor ->
      val range = rangeOfColor(1..11, otherColor)
      // Add subrange
      range.setSubrangeColor(6..6, color)
      assertContentEquals(range.getSubrangesOfColor(color), listOf(6..6))
      assertContentEquals(range.getSubrangesOfColor(otherColor), listOf(1..5, 7..11))
      // Add far range on left - should be added before existing subrange
      range.setSubrangeColor(1..1, color)
      assertContentEquals(range.getSubrangesOfColor(color), listOf(1..1, 6..6))
      assertContentEquals(range.getSubrangesOfColor(otherColor), listOf(2..5, 7..11))
      // Add far range on right - should be added after existing subrange
      range.setSubrangeColor(11..11, color)
      assertContentEquals(range.getSubrangesOfColor(color), listOf(1..1, 6..6, 11..11))
      assertContentEquals(range.getSubrangesOfColor(otherColor), listOf(2..5, 7..10))
      // Add range in middle between other ranges
      range.setSubrangeColor(3..3, color)
      range.setSubrangeColor(9..9, color)
      assertContentEquals(range.getSubrangesOfColor(color), listOf(1..1, 3..3, 6..6, 9..9, 11..11))
      assertContentEquals(range.getSubrangesOfColor(otherColor), listOf(2..2, 4..5, 7..8, 10..10))
    }
  }

  @Test
  fun testSingleIntersectingSubranges() {
    eachColor { color, otherColor ->
      val range = rangeOfColor(1..11, otherColor)
      // Add same ranges - subranges should have one copy
      range.setSubrangeColor(6..6, color)
      range.setSubrangeColor(6..6, color)
      assertContentEquals(range.getSubrangesOfColor(color), listOf(6..6))
      assertContentEquals(range.getSubrangesOfColor(otherColor), listOf(1..5, 7..11))
      // Add greater-right range - subranges should have union
      range.setSubrangeColor(6..7, color)
      assertContentEquals(range.getSubrangesOfColor(color), listOf(6..7))
      assertContentEquals(range.getSubrangesOfColor(otherColor), listOf(1..5, 8..11))
      // Add greater-left range - subranges should have union
      range.setSubrangeColor(5..7, color)
      assertContentEquals(range.getSubrangesOfColor(color), listOf(5..7))
      assertContentEquals(range.getSubrangesOfColor(otherColor), listOf(1..4, 8..11))
      // Add range intersecting on left
      range.setSubrangeColor(4..6, color)
      assertContentEquals(range.getSubrangesOfColor(color), listOf(4..7))
      assertContentEquals(range.getSubrangesOfColor(otherColor), listOf(1..3, 8..11))
      // Add range intersecting on right
      range.setSubrangeColor(6..8, color)
      assertContentEquals(range.getSubrangesOfColor(color), listOf(4..8))
      assertContentEquals(range.getSubrangesOfColor(otherColor), listOf(1..3, 9..11))
      // Add greater-both range
      range.setSubrangeColor(3..9, color)
      assertContentEquals(range.getSubrangesOfColor(color), listOf(3..9))
      assertContentEquals(range.getSubrangesOfColor(otherColor), listOf(1..2, 10..11))
    }
  }

  @Test
  fun testMultipleIntersectingSubranges() {
    eachColor { color, otherColor ->
      var range = rangeOfColor(1..11, otherColor)
      range.setSubrangeColor(2..3, color)
      range.setSubrangeColor(5..6, color)
      // Add two-intersection subrange
      range.setSubrangeColor(3..5, color)
      assertContentEquals(range.getSubrangesOfColor(color), listOf(2..6))
      assertContentEquals(range.getSubrangesOfColor(otherColor), listOf(1..1, 7..11))

      range = rangeOfColor(1..11, otherColor)
      range.setSubrangeColor(2..3, color)
      range.setSubrangeColor(5..6, color)
      // Add two-intersection subrange touching ends
      range.setSubrangeColor(2..6, color)
      assertContentEquals(range.getSubrangesOfColor(color), listOf(2..6))
      assertContentEquals(range.getSubrangesOfColor(otherColor), listOf(1..1, 7..11))

      range = rangeOfColor(1..11, otherColor)
      range.setSubrangeColor(2..3, color)
      range.setSubrangeColor(5..6, color)
      // Add two-intersection greater subrange
      range.setSubrangeColor(1..7, color)
      assertContentEquals(range.getSubrangesOfColor(color), listOf(1..7))
      assertContentEquals(range.getSubrangesOfColor(otherColor), listOf(8..11))

      range = rangeOfColor(1..11, otherColor)
      range.setSubrangeColor(2..3, color)
      range.setSubrangeColor(5..6, color)
      range.setSubrangeColor(8..9, color)
      // Add three-intersection subrange
      range.setSubrangeColor(3..8, color)
      assertContentEquals(range.getSubrangesOfColor(color), listOf(2..9))
      assertContentEquals(range.getSubrangesOfColor(otherColor), listOf(1..1, 10..11))
    }
  }

  @Test
  fun testTouchingSubranges() {
    val range = IntRedBlackRange(1..11)
    range.setSubrangeBlack(6..6)
    // Add close range on left - should expand subrange
    range.setSubrangeBlack(4..5)
    assertContentEquals(range.getBlackSubranges(), listOf(4..6))
    // Add close range on right - should expand subrange
    range.setSubrangeBlack(7..8)
    assertContentEquals(range.getBlackSubranges(), listOf(4..8))
    // Add close range between ranges
    range.setSubrangeBlack(1..2)
    range.setSubrangeBlack(3..3)
    assertContentEquals(range.getBlackSubranges(), listOf(1..8))
  }

  @Test
  fun testTouchingIntersectingSubranges() {
    var range = IntRedBlackRange(1..11)
    range.setSubrangeBlack(2..3)
    range.setSubrangeBlack(5..6)
    // Add intersecting-left touching-right range
    range.setSubrangeBlack(3..4)
    assertContentEquals(range.getBlackSubranges(), listOf(2..6))

    range = IntRedBlackRange(1..11)
    range.setSubrangeBlack(2..3)
    range.setSubrangeBlack(5..6)
    // Add touching-left intersecting-right range
    range.setSubrangeBlack(4..5)
    assertContentEquals(range.getBlackSubranges(), listOf(2..6))

    range = IntRedBlackRange(1..11)
    range.setSubrangeBlack(2..3)
    range.setSubrangeBlack(5..6)
    // Add containing-left touching-right range
    range.setSubrangeBlack(1..4)
    assertContentEquals(range.getBlackSubranges(), listOf(1..6))

    range = IntRedBlackRange(1..11)
    range.setSubrangeBlack(2..3)
    range.setSubrangeBlack(5..6)
    // Add touching-left containing-right range
    range.setSubrangeBlack(3..7)
    assertContentEquals(range.getBlackSubranges(), listOf(2..7))

    range = IntRedBlackRange(1..11)
    range.setSubrangeBlack(2..3)
    range.setSubrangeBlack(5..6)
    range.setSubrangeBlack(8..9)
    // Add intersecting-left containing-middle touching-right range
    range.setSubrangeBlack(3..7)
    assertContentEquals(range.getBlackSubranges(), listOf(2..9))

    range = IntRedBlackRange(1..11)
    range.setSubrangeBlack(2..3)
    range.setSubrangeBlack(5..6)
    range.setSubrangeBlack(8..9)
    // Add touching-left containing-middle intersecting-right range
    range.setSubrangeBlack(4..8)
    assertContentEquals(range.getBlackSubranges(), listOf(2..9))
  }
}
