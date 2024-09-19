package com.shakil.barivara.utils

import com.shakil.barivara.data.model.meter.ElectricityBill
import com.shakil.barivara.data.model.meter.Meter
import com.shakil.barivara.data.model.room.NewRoom
import com.shakil.barivara.data.model.room.Rent
import com.shakil.barivara.data.model.tenant.Tenant
import java.util.Locale

class FilterManager {
    fun onFilterClick(
        searchText: String,
        sourceList: List<NewRoom>,
        onFilterClick: onFilterClick
    ) {
        val roomList = ArrayList<NewRoom>()
        for (start in sourceList.indices) {
            if (sourceList[start].name.orEmpty().lowercase(Locale.getDefault()).contains(
                    searchText.lowercase(
                        Locale.getDefault()
                    )
                )
            ) {
                roomList.add(sourceList[start])
            }
        }
        onFilterClick.onClick(roomList)
    }

    fun onFilterClick(
        searchText: String,
        sourceList: ArrayList<Rent>,
        onRentFilterClick: onRentFilterClick
    ) {
        val rentList = ArrayList<Rent>()
        for (start in sourceList.indices) {
            if (sourceList[start].monthName.lowercase(Locale.getDefault()).contains(
                    searchText.lowercase(
                        Locale.getDefault()
                    )
                )
            ) {
                rentList.add(sourceList[start])
            }
        }
        onRentFilterClick.onClick(rentList)
    }

    fun onFilterClick(
        searchText: String,
        sourceList: ArrayList<Meter>,
        onMeterFilterClick: onMeterFilterClick
    ) {
        val meterList = ArrayList<Meter>()
        for (start in sourceList.indices) {
            if (sourceList[start].meterName.lowercase(Locale.getDefault()).contains(
                    searchText.lowercase(
                        Locale.getDefault()
                    )
                )
            ) {
                meterList.add(sourceList[start])
            }
        }
        onMeterFilterClick.onClick(meterList)
    }

    fun onFilterClick(
        searchText: String,
        sourceList: ArrayList<ElectricityBill>,
        onBillFilterClick: onBillFilterClick
    ) {
        val billList = ArrayList<ElectricityBill>()
        for (start in sourceList.indices) {
            if (sourceList[start].roomName.lowercase(Locale.getDefault()).contains(
                    searchText.lowercase(
                        Locale.getDefault()
                    )
                )
            ) {
                billList.add(sourceList[start])
            }
        }
        onBillFilterClick.onClick(billList)
    }

    fun onFilterClick(
        searchText: String,
        sourceList: List<Tenant>,
        onTenantFilterClick: onTenantFilterClick?
    ) {
        val billList = ArrayList<Tenant>()
        for (start in sourceList.indices) {
            if (sourceList[start].name.lowercase(Locale.getDefault()).contains(
                    searchText.lowercase(
                        Locale.getDefault()
                    )
                )
            ) {
                billList.add(sourceList[start])
            }
        }
        onTenantFilterClick?.onClick(billList)
    }

    interface onRentFilterClick {
        fun onClick(objects: ArrayList<Rent>)
    }

    interface onFilterClick {
        fun onClick(objects: ArrayList<NewRoom>)
    }

    interface onMeterFilterClick {
        fun onClick(objects: ArrayList<Meter>)
    }

    interface onTenantFilterClick {
        fun onClick(objects: ArrayList<Tenant>)
    }

    interface onBillFilterClick {
        fun onClick(objects: ArrayList<ElectricityBill>)
    }
}
