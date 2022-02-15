import me.thorny.twoColoredRange.math.BoundMath
import me.thorny.twoColoredRange.math.IntBoundMath
import me.thorny.twoColoredRange.TwoColoredArrayRange
import me.thorny.twoColoredRange.TwoColoredIntArrayRange
import me.thorny.twoColoredRange.TwoColoredLongArrayRange
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith

enum class RedGreenBlueColor {
  RED,
  GREEN,
  BLUE,
}

object BrokenIntBoundMath1: BoundMath<Int, Int> {
  override fun add(bound: Int, length: Int) = bound
  override fun subtract(bound: Int, length: Int) = IntBoundMath.subtract(bound, length)
  override fun getLength(start: Int, endExclusive: Int) = IntBoundMath.getLength(start, endExclusive)
}

object BrokenIntBoundMath2: BoundMath<Int, Int> {
  override fun add(bound: Int, length: Int) = IntBoundMath.add(bound, length)
  override fun subtract(bound: Int, length: Int) = bound
  override fun getLength(start: Int, endExclusive: Int) = IntBoundMath.getLength(start, endExclusive)
}

object BrokenIntBoundMath3: BoundMath<Int, Int> {
  override fun add(bound: Int, length: Int) = IntBoundMath.add(bound, length)
  override fun subtract(bound: Int, length: Int) = IntBoundMath.subtract(bound, length)
  override fun getLength(start: Int, endExclusive: Int) = 0
}

class WeirdBound(val value: Int): Comparable<WeirdBound> {
  override fun compareTo(other: WeirdBound): Int {
    return value.compareTo(other.value)
  }

  override fun equals(other: Any?): Boolean {
    if (other is WeirdBound) {
      return value == other.value
    }

    return false
  }

  override fun hashCode(): Int {
    return value
  }
}

object WeirdMath: BoundMath<WeirdBound, Int> {
  override fun add(bound: WeirdBound, length: Int) = WeirdBound(bound.value + length)
  override fun subtract(bound: WeirdBound, length: Int) = WeirdBound(bound.value - length)
  override fun getLength(start: WeirdBound, endExclusive: WeirdBound) = endExclusive.value - start.value
}

class ExtraTwoColoredArrayRangeTests {
  private fun rangeWithMath(math: BoundMath<Int, Int>): TwoColoredArrayRange<Int, Int, RedGreenBlueColor> {
    return TwoColoredArrayRange(1..2, 1, math, RedGreenBlueColor.RED, RedGreenBlueColor.GREEN)
  }

  @Test
  fun testExtraExceptions() {
    val range = rangeWithMath(IntBoundMath)
    assertFailsWith<Exception> { range.getSubrangesOfColor(RedGreenBlueColor.BLUE) }
    assertFailsWith<Exception> { range.setSubrangeColor(1..2, RedGreenBlueColor.BLUE) }
    assertFailsWith<Exception> { range.getSubrangeOfColor(RedGreenBlueColor.BLUE) }
    assertFailsWith<Exception> { rangeWithMath(BrokenIntBoundMath1) }
    assertFailsWith<Exception> { rangeWithMath(BrokenIntBoundMath2) }
    assertFailsWith<Exception> { rangeWithMath(BrokenIntBoundMath3) }
    assertFailsWith<Exception> {
      TwoColoredArrayRange(1..2, 1, IntBoundMath, RedGreenBlueColor.RED, RedGreenBlueColor.RED)
    }
  }

  @Test
  fun testDerivatives() {
    val intRange = TwoColoredIntArrayRange(1..2, RedGreenBlueColor.RED, RedGreenBlueColor.GREEN)
    intRange.setSubrangeOtherColor(2..2)
    assertContentEquals(listOf(1..1), intRange.getSubrangesOfDefaultColor())
    assertContentEquals(listOf(2..2), intRange.getSubrangesOfOtherColor())

    val longRange = TwoColoredLongArrayRange(1L..2L, RedGreenBlueColor.RED, RedGreenBlueColor.GREEN)
    longRange.setSubrangeOtherColor(2L..2L)
    assertContentEquals(listOf(1L..1L), longRange.getSubrangesOfDefaultColor())
    assertContentEquals(listOf(2L..2L), longRange.getSubrangesOfOtherColor())
  }

  @Test
  fun testWeirdMath() {
    val range = TwoColoredArrayRange(
      WeirdBound(1)..WeirdBound(2),
      1,
      WeirdMath,
      RedGreenBlueColor.RED,
      RedGreenBlueColor.GREEN,
    )

    // Just to cover non-standard ranges related code
    range.setSubrangeOtherColor(WeirdBound(1)..WeirdBound(1))
    range.setSubrangeOtherColor(WeirdBound(2)..WeirdBound(2))
    assertContentEquals(listOf(WeirdBound(1)..WeirdBound(2)), range.getSubrangesOfOtherColor())
  }
}
