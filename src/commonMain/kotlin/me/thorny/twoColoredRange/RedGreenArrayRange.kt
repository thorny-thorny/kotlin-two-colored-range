package me.thorny.twoColoredRange

import me.thorny.twoColoredRange.math.BoundMath
import me.thorny.twoColoredRange.math.IntBoundMath
import me.thorny.twoColoredRange.math.LongBoundMath
import me.thorny.twoColoredRange.rangeUtils.ClosedRangeFactory
import me.thorny.twoColoredRange.rangeUtils.IntRangeFactory
import me.thorny.twoColoredRange.rangeUtils.LongRangeFactory
import me.thorny.twoColoredRange.rangeUtils.RangeFactory
import kotlin.jvm.JvmOverloads

/**
 * Color type for [RedGreenArrayRange].
 */
enum class RedGreenColor {
  Red,
  Green,
}

/**
 * Subclass of [TwoColoredArrayRange] using [RedGreenColor] and [RedGreenColor.Red] as default color by default.
 */
open class RedGreenArrayRange<BoundType: Comparable<BoundType>, LengthType: Comparable<LengthType>> @JvmOverloads constructor(
  range: ClosedRange<BoundType>,
  step: LengthType,
  math: BoundMath<BoundType, LengthType>,
  defaultColor: RedGreenColor = RedGreenColor.Red,
  rangeFactory: RangeFactory<BoundType> = ClosedRangeFactory(),
): TwoColoredArrayRange<BoundType, LengthType, RedGreenColor>(
  range,
  step,
  math,
  defaultColor,
  if (defaultColor == RedGreenColor.Green) RedGreenColor.Red else RedGreenColor.Green,
  rangeFactory,
) {
  /**
   * Returns subranges of [RedGreenColor.Red] color.
   */
  fun getRedSubranges() = getSubrangesOfColor(RedGreenColor.Red)

  /**
   * Returns subranges of [RedGreenColor.Green] color.
   */
  fun getGreenSubranges() = getSubrangesOfColor(RedGreenColor.Green)

  /**
   * Paints [subrange] with [RedGreenColor.Red] color.
   */
  fun setSubrangeRed(subrange: ClosedRange<BoundType>) = setSubrangeColor(subrange, RedGreenColor.Red)

  /**
   * Paints [subrange] with [RedGreenColor.Green] color.
   */
  fun setSubrangeGreen(subrange: ClosedRange<BoundType>) = setSubrangeColor(subrange, RedGreenColor.Green)

  /**
   * Requests subrange of [RedGreenColor.Red] color, see [TwoColoredRange.getSubrangeOfColor] for details.
   */
  @JvmOverloads
  fun getRedSubrange(maxLength: LengthType = step, limitByRange: ClosedRange<BoundType> = range) =
    getSubrangeOfColor(RedGreenColor.Red, maxLength, limitByRange)

  /**
   * Requests subrange of [RedGreenColor.Green] color, see [TwoColoredRange.getSubrangeOfColor] for details.
   */
  @JvmOverloads
  fun getGreenSubrange(maxLength: LengthType = step, limitByRange: ClosedRange<BoundType> = range) =
    getSubrangeOfColor(RedGreenColor.Green, maxLength, limitByRange)
}

/**
 * [RedGreenArrayRange] subclass for [IntRange].
 */
open class RedGreenIntArrayRange @JvmOverloads constructor(
  range: ClosedRange<Int>,
  defaultColor: RedGreenColor = RedGreenColor.Red,
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
open class RedGreenLongArrayRange @JvmOverloads constructor(
  range: ClosedRange<Long>,
  defaultColor: RedGreenColor = RedGreenColor.Red,
): RedGreenArrayRange<Long, Long>(
  range,
  1,
  LongBoundMath,
  defaultColor,
  LongRangeFactory,
)
