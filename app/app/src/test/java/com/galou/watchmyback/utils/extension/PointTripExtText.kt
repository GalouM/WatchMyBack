package com.galou.watchmyback.utils.extension

import com.galou.watchmyback.data.entity.PointTrip
import com.galou.watchmyback.data.entity.PointTripWithData
import com.galou.watchmyback.data.entity.TypePoint
import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * @author galou
 * 2019-12-01
 */
class PointTripExtText {

    private val listPoint = mutableListOf(
        PointTripWithData(pointTrip = PointTrip(typePoint = TypePoint.SCHEDULE_STAGE)),
        PointTripWithData(pointTrip = PointTrip(typePoint = TypePoint.SCHEDULE_STAGE)),
        PointTripWithData(pointTrip = PointTrip(typePoint = TypePoint.SCHEDULE_STAGE)),
        PointTripWithData(pointTrip = PointTrip(typePoint = TypePoint.START)),
        PointTripWithData(pointTrip = PointTrip(typePoint = TypePoint.END)),
        PointTripWithData(pointTrip = PointTrip(typePoint = TypePoint.CHECKED_UP)),
        PointTripWithData(pointTrip = PointTrip(typePoint = TypePoint.CHECKED_UP))
    )

    @Test
    @Throws(Exception::class)
    fun filterScheduleStage_keepOnlyPointTypeScheduleStage(){
        val listFiltered = listPoint.filterScheduleStage()
        assertThat(listFiltered).hasSize(3)
        listFiltered.forEach {
            assertThat(it.pointTrip.typePoint).isEqualTo(TypePoint.SCHEDULE_STAGE)
        }
    }

    @Test
    @Throws(Exception::class)
    fun filterStart_keepOnlyStartPoint(){
        val startPoint = listPoint.filterOrCreateMainPoint(TypePoint.START, "idTrip")
        assertThat(startPoint).isNotNull()
        assertThat(startPoint.pointTrip.typePoint).isEqualTo(TypePoint.START)
    }

    @Test
    @Throws(Exception::class)
    fun filterEnd_keepOnlyStartPoint(){
        val endPoint = listPoint.filterOrCreateMainPoint(TypePoint.END, "idTrip")
        assertThat(endPoint).isNotNull()
        assertThat(endPoint.pointTrip.typePoint).isEqualTo(TypePoint.END)
    }

    @Test
    @Throws(Exception::class)
    fun filterStartPointListDoesNotContainStartPoint_createPointAndAddToTheList(){
        val listPointWithNoStart = mutableListOf(
            PointTripWithData(pointTrip = PointTrip(typePoint = TypePoint.SCHEDULE_STAGE)),
            PointTripWithData(pointTrip = PointTrip(typePoint = TypePoint.SCHEDULE_STAGE)),
            PointTripWithData(pointTrip = PointTrip(typePoint = TypePoint.SCHEDULE_STAGE)),
            PointTripWithData(pointTrip = PointTrip(typePoint = TypePoint.END)),
            PointTripWithData(pointTrip = PointTrip(typePoint = TypePoint.CHECKED_UP)),
            PointTripWithData(pointTrip = PointTrip(typePoint = TypePoint.CHECKED_UP))
        )
        val tripId = "tripId"
        val startPoint = listPointWithNoStart.filterOrCreateMainPoint(TypePoint.START, tripId)
        assertThat(startPoint).isNotNull()
        assertThat(startPoint.pointTrip.tripId).isEqualTo(tripId)
        assertThat(startPoint.pointTrip.typePoint).isEqualTo(TypePoint.START)
        val startPointInList = listPointWithNoStart.find { it.pointTrip.typePoint == TypePoint.START }
        assertThat(startPointInList).isNotNull()

    }
}