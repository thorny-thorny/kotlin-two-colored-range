package me.thorny.twoColoredRange

import me.thorny.twoColoredRange.math.BoundMath
import me.thorny.twoColoredRange.math.IntBoundMath
import me.thorny.twoColoredRange.math.LongBoundMath
import me.thorny.twoColoredRange.rangeUtils.ClosedRangeFactory
import me.thorny.twoColoredRange.rangeUtils.IntRangeFactory
import me.thorny.twoColoredRange.rangeUtils.LongRangeFactory
import me.thorny.twoColoredRange.rangeUtils.RangeFactory

/**
 * Color type for [RedGreenArrayRange].
 */
enum class RedGreenColor {
  RED,
  GREEN,
}

/**
 * Subclass of [TwoColoredArrayRange] using [RedGreenColor] and [RedGreenColor.RED] as default color by default.
 */
open class RedGreenArrayRange<BoundType: Comparable<BoundType>, LengthType: Comparable<LengthType>>(
  range: ClosedRange<BoundType>,
  step: LengthType,
  math: BoundMath<BoundType, LengthType>,
  defaultColor: RedGreenColor = RedGreenColor.RED,
  rangeFactory: RangeFactory<BoundType> = ClosedRangeFactory(),
): TwoColoredArrayRange<BoundType, LengthType, RedGreenColor>(
  range,
  step,
  math,
  defaultColor,
  if (defaultColor == RedGreenColor.GREEN) RedGreenColor.RED else RedGreenColor.GREEN,
  rangeFactory,
) {
  /**
   * Returns subranges of [RedGreenColor.RED] color.
   */
  fun getRedSubranges() = getSubrangesOfColor(RedGreenColor.RED)

  /**
   * Returns subranges of [RedGreenColor.GREEN] color.
   */
  fun getGreenSubranges() = getSubrangesOfColor(RedGreenColor.GREEN)

  /**
   * Paints [subrange] with [RedGreenColor.RED] color.
   */
  fun setSubrangeRed(subrange: ClosedRange<BoundType>) = setSubrangeColor(subrange, RedGreenColor.RED)

  /**
   * Paints [subrange] with [RedGreenColor.GREEN] color.
   */
  fun setSubrangeGreen(subrange: ClosedRange<BoundType>) = setSubrangeColor(subrange, RedGreenColor.GREEN)

  /**
   * Requests subrange of [RedGreenColor.RED], see [TwoColoredRange.getSubrangeOfColor] for details.
   */
  fun getRedSubrange(maxLength: LengthType = step, limitByRange: ClosedRange<BoundType> = range) =
    getSubrangeOfColor(RedGreenColor.RED, maxLength, limitByRange)

  /**
   * Requests subrange of [RedGreenColor.GREEN], see [TwoColoredRange.getSubrangeOfColor] for details.
   */
  fun getGreenSubrange(maxLength: LengthType = step, limitByRange: ClosedRange<BoundType> = range) =
    getSubrangeOfColor(RedGreenColor.GREEN, maxLength, limitByRange)
}

/**
 * [RedGreenArrayRange] subclass for [IntRange].
 */
open class RedGreenIntArrayRange(
  range: ClosedRange<Int>,
  defaultColor: RedGreenColor = RedGreenColor.RED,
): RedGreenArrayRange<Int, Int>(
  range,
  1,
  IntBoundMath,
  defaultColor,
  IntRangeFactory,
)

/**
 * [RedGreenArrayRange] subclass for [LongRange].
 */
open class RedGreenLongArrayRange(
  range: ClosedRange<Long>,
  defaultColor: RedGreenColor = RedGreenColor.RED,
): RedGreenArrayRange<Long, Long>(
  range,
  1,
  LongBoundMath,
  defaultColor,
  LongRangeFactory,
)
