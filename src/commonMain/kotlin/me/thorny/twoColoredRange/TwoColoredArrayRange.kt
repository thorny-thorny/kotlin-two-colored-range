package me.thorny.twoColoredRange

import me.thorny.twoColoredRange.math.BoundMath
import me.thorny.twoColoredRange.math.IntBoundMath
import me.thorny.twoColoredRange.math.LongBoundMath
import me.thorny.twoColoredRange.rangeUtils.*
import kotlin.jvm.JvmOverloads

/**
 * [TwoColoredRange] implementation using [ArrayList]. It keeps list of subranges painted with [defaultColor] and calculates subranges of [otherColor] when required so requests related to [defaultColor] are more efficient than ones related to [otherColor].
 */
open class TwoColoredArrayRange<
    BoundType: Comparable<BoundType>,
    LengthType: Comparable<LengthType>,
    ColorType: Enum<ColorType>,
> @JvmOverloads constructor(
  final override val range: ClosedRange<BoundType>,
  final override val step: LengthType,
  final override val math: BoundMath<BoundType, LengthType>,
  final override val defaultColor: ColorType,
  final override val otherColor: ColorType,
  final override val rangeFactory: RangeFactory<BoundType> = ClosedRangeFactory(),
): MutableTwoColoredRange<BoundType, LengthType, ColorType> {
  override val length = math.getLength(range.start, math.add(range.endInclusive, step))
  internal val defaultColorSubranges = ArrayList(listOf(rangeFactory.makeRange(range.start, range.endInclusive)))

  init {
    require(!range.isEmpty()) { "Empty range $range" }
    require(defaultColor != otherColor) { "Default color $defaultColor can't be equal to other color $otherColor" }
    // Better than nothing am I right?
    require(
      math.add(range.start, step) > range.start &&
      math.subtract(range.endInclusive, step) < range.endInclusive &&
      math.getLength(range.start, math.add(range.start, step)) == step
    ) { "Invalid math $math" }
  }

  internal fun checkSubrange(subrange: ClosedRange<BoundType>) {
    require(range.containsRange(subrange)) { "Subrange $subrange is out of range bounds $range" }
    require(!subrange.isEmpty()) { "Empty subrange $range" }
  }

  override fun getSubrangesOfDefaultColor(): List<ClosedRange<BoundType>> {
    return defaultColorSubranges
  }

  override fun getSubrangesOfOtherColor(): List<ClosedRange<BoundType>> {
    return filter { (_, color) -> color == otherColor }.map { it.first }
  }

  override fun getSubrangesOfColor(color: ColorType): List<ClosedRange<BoundType>> {
    return when (color) {
      defaultColor -> getSubrangesOfDefaultColor()
      otherColor -> getSubrangesOfOtherColor()
      else -> throw IllegalArgumentException("Invalid color $color")
    }
  }

  override fun setSubrangeDefaultColor(subrange: ClosedRange<BoundType>) {
    checkSubrange(subrange)

    val intersectingSubranges = defaultColorSubranges.filter { it.intersectsRange(subrange) }
    val touchingSubranges = defaultColorSubranges.filter { it.touchesRange(subrange, step, math) }

    if (intersectingSubranges.isEmpty() && touchingSubranges.isEmpty()) {
      val index = defaultColorSubranges.indexOfLast { it.start < subrange.start }
      defaultColorSubranges.add(index + 1, subrange)
    } else {
      var joinedSubrange = subrange
      var replacedSubrange: ClosedRange<BoundType>? = null

      if (intersectingSubranges.isNotEmpty()) {
        val first = intersectingSubranges.first()
        val last = intersectingSubranges.last()
        for (index in defaultColorSubranges.indexOf(last) downTo defaultColorSubranges.indexOf(first) + 1) {
          defaultColorSubranges.removeAt(index)
        }

        joinedSubrange = joinedSubrange.joinedByRange(first, rangeFactory).joinedByRange(last, rangeFactory)
        replacedSubrange = first
      }

      if (touchingSubranges.isNotEmpty()) {
        if (replacedSubrange == null) {
          replacedSubrange = touchingSubranges.first()
        }

        touchingSubranges.reversed().forEachIndexed { index, it ->
          joinedSubrange = joinedSubrange.joinedByRange(it, rangeFactory)
          if (index != touchingSubranges.lastIndex || intersectingSubranges.isNotEmpty()) {
            defaultColorSubranges.remove(it)
          }
        }
      }

      defaultColorSubranges[defaultColorSubranges.indexOf(replacedSubrange)] = joinedSubrange
    }
  }

  override fun setSubrangeOtherColor(subrange: ClosedRange<BoundType>) {
    checkSubrange(subrange)

    val intersectingSubranges = defaultColorSubranges.filter { it.intersectsRange(subrange) }
    intersectingSubranges.forEach {
      if (subrange.containsRange(it)) {
        defaultColorSubranges.remove(it)
      } else {
        val split = it.splitByRange(subrange, step, math, rangeFactory)
        val itIndex = defaultColorSubranges.indexOf(it)
        defaultColorSubranges[itIndex] = split.first()

        val second = split.getOrNull(1)
        if (second != null) {
          defaultColorSubranges.add(itIndex + 1, second)
        }
      }
    }
  }

  override fun setSubrangeColor(subrange: ClosedRange<BoundType>, color: ColorType) {
    when (color) {
      defaultColor -> setSubrangeDefaultColor(subrange)
      otherColor -> setSubrangeOtherColor(subrange)
      else -> throw IllegalArgumentException("Invalid color $color")
    }
  }

  override fun getColor(bound: BoundType): ColorType {
    require(range.contains(bound)) { "Bound $bound is out of range bounds $range" }

    return when (defaultColorSubranges.find { it.start <= bound && it.endInclusive >= bound }) {
      null -> otherColor
      else -> defaultColor
    }
  }

  override fun getSubrangeOfColor(
    color: ColorType,
    maxLength: LengthType,
    limitByRange: ClosedRange<BoundType>,
  ): ClosedRange<BoundType>? {
    require(maxLength >= step) { "Max length $maxLength can't be less than step $step" }
    require(color == defaultColor || color == otherColor) { "Invalid color $color" }

    val fullLengthPair = subrangesIterator(limitByRange).asSequence().find { (subrange, subrangeColor) ->
      math.getLength(subrange.start, math.add(subrange.endInclusive, step)) >= maxLength && subrangeColor == color
    }

    if (fullLengthPair != null) {
      val (subrange) = fullLengthPair
      return rangeFactory.makeRange(subrange.start, math.subtract(math.add(subrange.start, maxLength), step))
    }

    val firstMatchingPair = subrangesIterator(limitByRange).asSequence().find { (_, subrangeColor) ->
      subrangeColor == color
    }
    return firstMatchingPair?.first
  }

  override fun getSubrangeOfColor(color: ColorType): ClosedRange<BoundType>? {
    return getSubrangeOfColor(color, step, range)
  }

  override fun getSubrangeOfColor(color: ColorType, maxLength: LengthType): ClosedRange<BoundType>? {
    return getSubrangeOfColor(color, maxLength, range)
  }

  override fun getSubrangeOfColor(color: ColorType, limitByRange: ClosedRange<BoundType>): ClosedRange<BoundType>? {
    return getSubrangeOfColor(color, step, limitByRange)
  }

  override fun iterator(): Iterator<Pair<ClosedRange<BoundType>, ColorType>> {
    return TwoColoredArrayRangeIterator(this)
  }

  override fun subrangesIterator(limitByRange: ClosedRange<BoundType>): Iterator<Pair<ClosedRange<BoundType>, ColorType>> {
    return TwoColoredArrayRangeIterator(this, limitByRange)
  }
}

/**
 * [TwoColoredArrayRange] iterator.
 */
open class TwoColoredArrayRangeIterator<
    BoundType: Comparable<BoundType>,
    LengthType: Comparable<LengthType>,
    ColorType: Enum<ColorType>,
> @JvmOverloads constructor(
  private val parent: TwoColoredArrayRange<BoundType, LengthType, ColorType>,
  private val limitByRange: ClosedRange<BoundType> = parent.range,
): Iterator<Pair<ClosedRange<BoundType>, ColorType>> {
  private var start = limitByRange.start
  private var color = parent.getColor(start)
  private var defaultColorSubrangeIndex = parent.defaultColorSubranges.indexOfFirst {
    when (color) {
      parent.defaultColor -> it.contains(start)
      else -> it.start >= start
    }
  }

  init {
    parent.checkSubrange(limitByRange)
  }

  private fun trimSubrange(subrange: ClosedRange<BoundType>): ClosedRange<BoundType> {
    return when (limitByRange.containsRange(subrange)) {
      true -> subrange
      else -> subrange.trimmedByRange(limitByRange, parent.rangeFactory)
    }
  }

  override fun hasNext(): Boolean {
    return start <= limitByRange.endInclusive
  }

  override fun next(): Pair<ClosedRange<BoundType>, ColorType> {
    val pair: Pair<ClosedRange<BoundType>, ColorType>

    if (color == parent.defaultColor) {
      val subrange = trimSubrange(parent.defaultColorSubranges[defaultColorSubrangeIndex])
      pair = subrange to color
      color = parent.otherColor
      defaultColorSubrangeIndex += 1
    } else {
      val endInclusive = when (val defaultColorSubrange = parent.defaultColorSubranges.getOrNull(defaultColorSubrangeIndex)) {
        null -> limitByRange.endInclusive
        else -> minOf(parent.math.subtract(defaultColorSubrange.start, parent.step), limitByRange.endInclusive)
      }
      pair = parent.rangeFactory.makeRange(start, endInclusive) to color
      color = parent.defaultColor
    }
    start = parent.math.add(pair.first.endInclusive, parent.step)

    return pair
  }
}

/**
 * [TwoColoredArrayRange] for [IntRange].
 */
open class TwoColoredIntArrayRange<ColorType: Enum<ColorType>>(
  range: ClosedRange<Int>,
  defaultColor: ColorType,
  otherColor: ColorType,
): TwoColoredArrayRange<Int, Int, ColorType>(range, 1, IntBoundMath, defaultColor, otherColor, IntRangeFactory)

/**
 * [TwoColoredArrayRange] for [LongRange].
 */
open class TwoColoredLongArrayRange<ColorType: Enum<ColorType>>(
  range: ClosedRange<Long>,
  defaultColor: ColorType,
  otherColor: ColorType,
): TwoColoredArrayRange<Long, Long, ColorType>(range, 1, LongBoundMath, defaultColor, otherColor, LongRangeFactory)
