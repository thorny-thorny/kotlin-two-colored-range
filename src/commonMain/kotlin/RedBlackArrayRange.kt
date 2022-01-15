package me.thorny.twoColoredRange

import me.thorny.twoColoredRange.math.BoundMath
import me.thorny.twoColoredRange.math.IntBoundMath
import me.thorny.twoColoredRange.math.LongBoundMath
import me.thorny.twoColoredRange.rangeUtils.ClosedRangeFactory
import me.thorny.twoColoredRange.rangeUtils.IntRangeFactory
import me.thorny.twoColoredRange.rangeUtils.LongRangeFactory
import me.thorny.twoColoredRange.rangeUtils.RangeFactory

/**
 * Color type for [RedBlackArrayRange].
 */
enum class RedBlackColor {
  RED,
  BLACK,
}

/**
 * Subclass of [TwoColoredArrayRange] using [RedBlackColor] and [RedBlackColor.RED] as default color by default.
 */
open class RedBlackArrayRange<BoundType: Comparable<BoundType>, LengthType: Comparable<LengthType>>(
  range: ClosedRange<BoundType>,
  step: LengthType,
  math: BoundMath<BoundType, LengthType>,
  defaultColor: RedBlackColor? = RedBlackColor.RED,
  rangeFactory: RangeFactory<BoundType> = ClosedRangeFactory(),
): TwoColoredArrayRange<BoundType, LengthType, RedBlackColor>(
  range,
  step,
  math,
  defaultColor ?: RedBlackColor.RED,
  if (defaultColor == RedBlackColor.BLACK) RedBlackColor.RED else RedBlackColor.BLACK,
  rangeFactory,
) {
  /**
   * Returns subranges of [RedBlackColor.RED] color.
   */
  fun getRedSubranges() = getSubrangesOfColor(RedBlackColor.RED)

  /**
   * Returns subranges of [RedBlackColor.BLACK] color.
   */
  fun getBlackSubranges() = getSubrangesOfColor(RedBlackColor.BLACK)

  /**
   * Paints [subrange] with [RedBlackColor.RED] color.
   */
  fun setSubrangeRed(subrange: ClosedRange<BoundType>) = setSubrangeColor(subrange, RedBlackColor.RED)

  /**
   * Paints [subrange] with [RedBlackColor.BLACK] color.
   */
  fun setSubrangeBlack(subrange: ClosedRange<BoundType>) = setSubrangeColor(subrange, RedBlackColor.BLACK)

  /**
   * Requests subrange of [RedBlackColor.RED], see [TwoColoredRange.getSubrangeOfColor] for details.
   */
  fun getRedSubrange(maxLength: LengthType = step, limitByRange: ClosedRange<BoundType> = range) =
    getSubrangeOfColor(RedBlackColor.RED, maxLength, limitByRange)

  /**
   * Requests subrange of [RedBlackColor.BLACK], see [TwoColoredRange.getSubrangeOfColor] for details.
   */
  fun getBlackSubrange(maxLength: LengthType = step, limitByRange: ClosedRange<BoundType> = range) =
    getSubrangeOfColor(RedBlackColor.BLACK, maxLength, limitByRange)
}

/**
 * [RedBlackArrayRange] subclass for [IntRange].
 */
open class RedBlackIntArrayRange(
  range: ClosedRange<Int>,
  defaultColor: RedBlackColor? = RedBlackColor.RED,
): RedBlackArrayRange<Int, Int>(
  range,
  1,
  IntBoundMath,
  defaultColor,
  IntRangeFactory,
)

/**
 * [RedBlackArrayRange] subclass for [LongRange].
 */
open class RedBlackLongArrayRange(
  range: ClosedRange<Long>,
  defaultColor: RedBlackColor? = RedBlackColor.RED,
): RedBlackArrayRange<Long, Long>(
  range,
  1,
  LongBoundMath,
  defaultColor,
  LongRangeFactory,
)
