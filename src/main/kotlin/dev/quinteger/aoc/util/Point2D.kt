package dev.quinteger.aoc.util

import kotlin.math.abs

@JvmRecord
data class Point2D(
    val x: Long,
    val y: Long
) {
    
    fun areaToInclusive(other: Point2D): Long {
        return (abs(other.x - this.x) + 1) * (abs(other.y - this.y) + 1);
    }
    
    fun directionToOrthogonalPoint(other: Point2D): Direction2D {
        if (this.x == other.x) {
            if (this.y < other.y) {
                return Direction2D.RIGHT
            } else if (this.y > other.y) {
                return Direction2D.LEFT
            }
        } else if (this.y == other.y) {
            @Suppress("KotlinConstantConditions")
            if (this.x < other.x) {
                return Direction2D.UP
            } else if (this.x > other.x) {
                return Direction2D.DOWN
            }
        }
        throw IllegalArgumentException("Point $other is not orthogonal to point $this")
    }
}