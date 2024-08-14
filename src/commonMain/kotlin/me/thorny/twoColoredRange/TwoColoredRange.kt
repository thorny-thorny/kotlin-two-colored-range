package me.thorny.twoColoredRange

import me.thorny.twoColoredRange.math.BoundMath
import me.thorny.twoColoredRange.rangeUtils.RangeFactory

/**
 * Two colored range - range that is filled completely with subranges of one of two colors. It provides features for painting and requesting subranges.
 *
 * A newly created colored range looks like this (assuming [range] is 0..12 and [defaultColor] is c1):
 *
 * `0      4      8      12`
 *
 * `[          c1         ]`
 *
 * Example of colored range:
 *
 * `0      4      8      12`
 *
 * `[ c2 ][    c1   ][ c2 ]`
 *
 * All mutating and requesting functions are supposed to make smart subranges joining and splitting. For example, having colored range:
 *
 * `0      4      8      12`
 *
 * `[  c2  ][      c1     ]`
 *
 * and applying color 2 to subrange 5..8 should change colored range to:
 *
 * `0      4      8      12`
 *
 * `[      c2     ][  c1  ]`
 *
 * and a consequent call of [getSubrangeOfColor](c2, 2, 4..10) should return 4..5.
 *
 * @param BoundType the type of colored range bounds.
 * @param LengthType the type of length between bounds. Usually it is the same as [BoundType], but it may be different, for example: [BoundType] = LocalDate, [LengthType] = DatePeriod.
 * @param ColorType the type of range colors.
 * @property range a regular range marking bounds of colored range.
 * @property step the minimal non-zero length of a subrange and distance between two neighbour subranges. Usually it has a value of 1 for integer types of [BoundType].
 * @property math the math used for calculating lengths and distances of subranges.
 * @property defaultColor the default color, a colored range is supposed to be fully colored with it right after constructor call.
 * @property otherColor the second color.
 * @property rangeFactory the factory for ranges, it is used for custom ranges type. For example [IntRange] is more convenient than [ClosedRange]<[BoundType]> for equality checks outside the interface or class.
 * @property length the length of colored range.
 */
interface TwoColoredRange<
    BoundType: Comparable<BoundType>,
    LengthType: Comparable<LengthType>,
    ColorType: Enum<ColorType>,
>: Iterable<Pair<ClosedRange<BoundType>, ColorType>> {
  val range: ClosedRange<BoundType>
  val step: LengthType
  val math: BoundMath<BoundType, LengthType>
  val defaultColor: ColorType
  val otherColor: ColorType
  val rangeFactory: RangeFactory<BoundType>
  val length: LengthType

  /**
   * Returns list of subranges colored with [defaultColor].
   */
  fun getSubrangesOfDefaultColor(): List<ClosedRange<BoundType>>

  /**
   * Returns list of subranges colored with [otherColor].
   */
  fun getSubrangesOfOtherColor(): List<ClosedRange<BoundType>>

  /**
   * Returns list of subranges colored with [color].
   *
   * @param color the color.
   */
  fun getSubrangesOfColor(color: ColorType): List<ClosedRange<BoundType>>

  /**
   * Looks for a subrange containing [bound] and returns its color.
   *
   * @param bound the bound.
   */
  fun getColor(bound: BoundType): ColorType

  /**
   * Performs a search for a subrange with color [color] limited by [limitByRange]. If a subrange with length >= [maxLength] was found then returns a subrange from foundSubrange.start to foundSubrange.start + [maxLength] (involving [math] and [rangeFactory]). Otherwise returns first non-empty subrange with color [color] inside [limitByRange] or null.
   *
   * @param color the color.
   * @param maxLength maximum length.
   * @param limitByRange range to limit the search.
   */
  fun getSubrangeOfColor(
    color: ColorType,
    maxLength: LengthType,
    limitByRange: ClosedRange<BoundType>,
  ): ClosedRange<BoundType>?

  /**
   * [getSubrangeOfColor] called with maxLength = [TwoColoredRange.step] and limitByRange = [TwoColoredRange.range].
   *
   * @param color the color.
   */
  fun getSubrangeOfColor(color: ColorType): ClosedRange<BoundType>?

  /**
   * [getSubrangeOfColor] called with limitByRange = [TwoColoredRange.range].
   *
   * @param color the color.
   * @param maxLength maximum length.
   */
  fun getSubrangeOfColor(color: ColorType, maxLength: LengthType): ClosedRange<BoundType>?

  /**
   * [getSubrangeOfColor] called with maxLength = [TwoColoredRange.step].
   *
   * @param color the color.
   * @param limitByRange: ClosedRange<BoundType>,
   */
  fun getSubrangeOfColor(color: ColorType, limitByRange: ClosedRange<BoundType>): ClosedRange<BoundType>?

  /**
   * Iterator limited by [limitByRange].
   *
   * @param limitByRange range to limit iteration.
   */
  fun subrangesIterator(limitByRange: ClosedRange<BoundType>): Iterator<Pair<ClosedRange<BoundType>, ColorType>>
}

/**
 * Mutable [TwoColoredRange] interface.
 *
 * @param BoundType the type of range bounds.
 * @param LengthType the type of length between bounds.
 * @param ColorType the type of range colors.
 */
interface MutableTwoColoredRange<
    BoundType: Comparable<BoundType>,
    LengthType: Comparable<LengthType>,
    ColorType: Enum<ColorType>,
>: TwoColoredRange<BoundType, LengthType, ColorType> {
  /**
   * Paints [subrange] with defaultColor overriding any previous colors the [subrange] contained.
   *
   * @param subrange the subrange.
   */
  fun setSubrangeDefaultColor(subrange: ClosedRange<BoundType>)

  /**
   * Paints [subrange] with otherColor overriding any previous colors the [subrange] contained.
   *
   * @param subrange the subrange.
   */
  fun setSubrangeOtherColor(subrange: ClosedRange<BoundType>)

  /**
   * Paints [subrange] with [color] overriding any previous colors the [subrange] contained.
   *
   * @param subrange the subrange.
   * @param color the color.
   */
  fun setSubrangeColor(subrange: ClosedRange<BoundType>, color: ColorType)
}
