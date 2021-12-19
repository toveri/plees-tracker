/*
 * Copyright 2019 Miklos Vajna. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 */

package hu.vmiklos.plees_tracker

import android.text.format.DateUtils
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.math.max
import kotlin.math.min

/**
 * Represents one tracked sleep.
 */
@Entity
class Sleep {
    @PrimaryKey(autoGenerate = true)
    var sid: Int = 0

    @ColumnInfo(name = "start_date")
    var start: Long = 0

    @ColumnInfo(name = "stop_date")
    var stop: Long = 0

    @ColumnInfo(name = "rating")
    var rating: Long = 0

    private val lengthMs
        get() = stop - start

    val lengthHours
        get() = lengthMs.toFloat() / DateUtils.HOUR_IN_MILLIS

    override fun equals(other: Any?): Boolean =
        other is Sleep &&
                other.start == start &&
                other.stop == stop

    override fun hashCode(): Int {
        var result = start.hashCode()
        result = 31 * result + stop.hashCode()
        return result
    }

    fun overlaps(other: Sleep): Boolean =
        this.start <= other.stop && this.stop >= other.start

    /**
     * Returns a new sleep based on two sleeps.
     * This may result in filling out time unless the sleeps overlap.
     *
     * start is the earliest of the two starts
     * stop is the latest of the two stops
     * rating is min() if both have nonzero ratings, otherwise max() which might be 0
     */
    fun merge(other: Sleep): Sleep {
        val sleep = Sleep()
        sleep.start = min(this.start, other.start)
        sleep.stop = max(this.stop, other.stop)
        sleep.rating = if (this.rating != 0L && other.rating != 0L) {
            min(this.rating, other.rating)
        } else {
            max(this.rating, other.rating)
        }
        return sleep
    }
}

/* vim:set shiftwidth=4 softtabstop=4 expandtab: */
