package me.thorny.twoColoredRange

import me.thorny.twoColoredRange.math.BoundMath
import me.thorny.twoColoredRange.math.IntBoundMath
import me.thorny.twoColoredRange.math.LongBoundMath
import me.thorny.twoColoredRange.rangeUtils.*

/**
 * [TwoColoredRange] implementation using [ArrayList].
 */
open class TwoColoredArrayRange<
    BoundType: Comparable<BoundType>,
    LengthType: Comparable<LengthType>,
    ColorType: Enum<ColorType>,
>(
  final override val range: ClosedRange<BoundType>,
  final override val step: LengthType,
  final override val math: BoundMath<BoundType, LengthType>,
  final override val defaultColor: ColorType,
  final override val otherColor: ColorType,
  final override val rangeFactory: RangeFactory<BoundType> = ClosedRangeFactory(),
): TwoColoredRange<BoundType, LengthType, ColorType> {
  override val length = math.getLength(range.start, math.add(range.endInclusive, step))
  internal val defaultColorSubranges = ArrayList(listOf(range))

  init {
    if (range.isEmpty()) {
      throw Exception("Empty range $range")
    }

    if (defaultColor == otherColor) {
      throw Exception("Default color $defaultColor can't be equal to other color $otherColor")
    }

    // Better than nothing am I right?
    if (
      math.add(range.start, step) <= range.start ||
      math.subtract(range.endInclusive, step) >= range.endInclusive ||
      math.getLength(range.start, math.add(range.start, step)) != step
    ) {
      throw Exception("Invalid math $math")
    }
  }

  internal fun checkSubrange(subrange: ClosedRange<BoundType>) {
    if (!range.containsRange(subrange)) {
      throw Exception("Subrange $subrange is out of range bounds $range")
    }

    if (subrange.isEmpty()) {
      throw Exception("Empty subrange $range")
    }
  }

  override fun getSubrangesOfDefaultColor(): List<ClosedRange<BoundType>> {
    return defaultColorSubranges
  }

  override fun getSubrangesOfOtherColor(): List<ClosedRange<BoundType>> {
    return this
      .filter { (_, color) -> color == this.otherColor }
      .map { it.first }
  }

  override fun getSubrangesOfColor(color: ColorType): List<ClosedRange<BoundType>> {
    return when (color) {
      defaultColor -> getSubrangesOfDefaultColor()
      otherColor -> getSubrangesOfOtherColor()
      else -> throw Exception("Invalid color $color")
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

        joinedSubrange = joinedSubrange.joinRange(first, rangeFactory).joinRange(last, rangeFactory)
        replacedSubrange = first
      }

      if (touchingSubranges.isNotEmpty()) {
        if (replacedSubrange == null) {
          replacedSubrange = touchingSubranges.first()
        }

        touchingSubranges.reversed().forEachIndexed { index, it ->
          joinedSubrange = joinedSubrange.joinRange(it, rangeFactory)
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
      else -> throw Exception("Invalid color $color")
    }
  }

  override fun getColor(bound: BoundType): ColorType {
    if (!range.contains(bound)) {
      throw Exception("Bound $bound is out of range bounds $range")
    }

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
    if (maxLength < step) {
      throw Exception("Max length $maxLength can't be lesser than step $step")
    }

    if (color != defaultColor && color != otherColor) {
      throw Exception("Invalid color $color")
    }

    val fullLengthPair = this.subrangesIterator(limitByRange).asSequence().find { (subrange, subrangeColor) ->
      math.getLength(subrange.start, math.add(subrange.endInclusive, step)) >= maxLength && subrangeColor == color
    }

    if (fullLengthPair != null) {
      val (subrange) = fullLengthPair
      return rangeFactory.getRange(subrange.start, math.subtract(math.add(subrange.start, maxLength), step))
    }

    val firstMatchingPair = this.subrangesIterator(limitByRange).asSequence().find { (_, subrangeColor) ->
      subrangeColor == color
    }
    return firstMatchingPair?.first
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
>(
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

  override fun hasNext(): Boolean {
    return start <= limitByRange.endInclusive
  }

  override fun next(): Pair<ClosedRange<BoundType>, ColorType> {
    var defaultColorSubrange = parent.defaultColorSubranges.getOrNull(defaultColorSubrangeIndex)
    if (defaultColorSubrange != null) {
      if (defaultColorSubrange.start < limitByRange.start) {
        defaultColorSubrange = parent.rangeFactory.getRange(
          maxOf(defaultColorSubrange.start, limitByRange.start),
          defaultColorSubrange.endInclusive,
        )
      }
      if (defaultColorSubrange.endInclusive > limitByRange.endInclusive) {
        defaultColorSubrange = parent.rangeFactory.getRange(
          defaultColorSubrange.start,
          minOf(defaultColorSubrange.endInclusive, limitByRange.endInclusive)
        )
      }
    }

    val pair: Pair<ClosedRange<BoundType>, ColorType>

    if (color == parent.defaultColor) {
      pair = defaultColorSubrange!! to color
      color = parent.otherColor
      defaultColorSubrangeIndex += 1
    } else {
      val endInclusive = when (defaultColorSubrange) {
        null -> limitByRange.endInclusive
        else -> minOf(parent.math.subtract(defaultColorSubrange.start, parent.step), limitByRange.endInclusive)
      }
      pair = parent.rangeFactory.getRange(start, endInclusive) to color
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
