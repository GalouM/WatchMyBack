package com.galou.watchmyback.utils.extension

import com.galou.watchmyback.data.entity.PointTrip
import com.galou.watchmyback.data.entity.PointTripWithData
import com.galou.watchmyback.data.entity.TypePoint
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.util.*

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

    @Test
    @Throws(Exception::class)
    fun findLatestCheckUpPointNoCheckUpPoint_returnNull(){
        val listPointWithNoCheckedUp = mutableListOf(
            PointTripWithData(pointTrip = PointTrip(typePoint = TypePoint.SCHEDULE_STAGE)),
            PointTripWithData(pointTrip = PointTrip(typePoint = TypePoint.SCHEDULE_STAGE)),
            PointTripWithData(pointTrip = PointTrip(typePoint = TypePoint.SCHEDULE_STAGE)),
            PointTripWithData(pointTrip = PointTrip(typePoint = TypePoint.END))
        )
        assertThat(listPointWithNoCheckedUp.findLatestCheckUpPoint()).isNull()
    }

    @Test
    @Throws(Exception::class)
    fun findLatestCheckUpPointOneCheckUpPoint_returnFirstPoint(){
        val listPointWithOneCheckedUp = mutableListOf(
            PointTripWithData(pointTrip = PointTrip(typePoint = TypePoint.SCHEDULE_STAGE)),
            PointTripWithData(pointTrip = PointTrip(typePoint = TypePoint.SCHEDULE_STAGE)),
            PointTripWithData(pointTrip = PointTrip(typePoint = TypePoint.SCHEDULE_STAGE)),
            PointTripWithData(pointTrip = PointTrip(typePoint = TypePoint.END)),
            PointTripWithData(pointTrip = PointTrip(typePoint = TypePoint.CHECKED_UP))
        )
        assertThat(listPointWithOneCheckedUp.findLatestCheckUpPoint()).isEqualTo(listPointWithOneCheckedUp[4])
    }

    @Test
    @Throws(Exception::class)
    fun findLatestCheckUpPointWithSeveralCheckUpPoint_returnLatestCheckUp(){
        val calendar = Calendar.getInstance()
        val today = calendar.time
        val checkUp1 = PointTripWithData(pointTrip = PointTrip(
            typePoint = TypePoint.CHECKED_UP,
            time = today))
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = calendar.time
        val checkUp2 = PointTripWithData(pointTrip = PointTrip(
            typePoint = TypePoint.CHECKED_UP,
            time = yesterday))
        calendar.add(Calendar.DAY_OF_YEAR, 2)
        val tomorrow = calendar.time
        val checkUp3 = PointTripWithData(pointTrip = PointTrip(
            typePoint = TypePoint.CHECKED_UP,
            time = tomorrow))
        val listPoint = mutableListOf(
            PointTripWithData(pointTrip = PointTrip(typePoint = TypePoint.SCHEDULE_STAGE)),
            PointTripWithData(pointTrip = PointTrip(typePoint = TypePoint.SCHEDULE_STAGE)),
            PointTripWithData(pointTrip = PointTrip(typePoint = TypePoint.SCHEDULE_STAGE)),
            PointTripWithData(pointTrip = PointTrip(typePoint = TypePoint.START)),
            PointTripWithData(pointTrip = PointTrip(typePoint = TypePoint.END)),
            checkUp1, checkUp3, checkUp2
        )
        assertThat(listPoint.findLatestCheckUpPoint()).isEqualTo(checkUp3)
    }
}