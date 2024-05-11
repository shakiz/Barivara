package com.shakil.barivara.data.remote.firebasedb

import android.content.Context
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.shakil.barivara.R
import com.shakil.barivara.data.model.User
import com.shakil.barivara.data.model.meter.ElectricityBill
import com.shakil.barivara.data.model.meter.Meter
import com.shakil.barivara.data.model.note.Note
import com.shakil.barivara.data.model.notification.Notification
import com.shakil.barivara.data.model.room.Rent
import com.shakil.barivara.data.model.room.Room
import com.shakil.barivara.data.model.tenant.Tenant
import com.shakil.barivara.utils.Constants

class FirebaseCrudHelper(private val context: Context) {
    private var databaseReference: DatabaseReference? = null
    fun add(dataSet: Any?, path: String, userId: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference(path).child(userId)
        Log.i(Constants.TAG, "" + databaseReference!!.parent)
        val id = databaseReference?.push()?.key
        id?.let { key -> databaseReference?.child(key)?.setValue(dataSet) }
    }

    fun addNotification(notification: Notification?, path: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference(path)
        Log.i(Constants.TAG, "" + databaseReference!!.parent)
        val id = databaseReference?.push()?.key
        id?.let { key -> databaseReference?.child(key)?.setValue(notification) }
    }

    fun update(data: Any, firebaseId: String?, path: String?, userId: String?) {
        databaseReference = FirebaseDatabase.getInstance().getReference(path!!).child(userId!!)
        var postValues: Map<String, Any> = HashMap()
        when (path) {
            "tenant" -> {
                val tenant = data as Tenant
                postValues = tenant.toMap()
            }

            "meter" -> {
                val meter = data as Meter
                postValues = meter.toMap()
            }

            "rent" -> {
                val rent = data as Rent
                postValues = rent.toMap()
            }

            "room" -> {
                val room = data as Room
                postValues = room.toMap()
            }

            "note" -> {
                val note = data as Note
                postValues = note.toMap()
            }

            "electricityBill" -> {
                val electricityBill = data as ElectricityBill
                postValues = electricityBill.toMap()
            }

            "user" -> {
                val user = data as User
                postValues = user.toMap()
            }

            else ->                 //empty implementation
                Log.i(Constants.TAG, "Update Failed:: Not Table Found")
        }
        databaseReference!!.child(firebaseId!!).updateChildren(postValues)
    }

    fun deleteRecord(path: String, firebaseId: String, userId: String) {
        databaseReference =
            FirebaseDatabase.getInstance().getReference(path).child(userId).child(firebaseId)
        databaseReference?.removeValue()
    }

    fun fetchProfile(path: String, userId: String, onProfileFetch: onProfileFetch) {
        val objects = ArrayList<User?>()
        databaseReference = FirebaseDatabase.getInstance().getReference(path).child(userId)
        Log.i(Constants.TAG, "" + databaseReference!!.parent)
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val user = dataSnapshot.getValue(
                        User::class.java
                    )
                    user?.firebaseKey = dataSnapshot.key ?: ""
                    objects.add(user)
                    Log.i(Constants.TAG + ":fetchProfile", "" + user?.firebaseKey)
                }
                if (objects.size > 0) {
                    onProfileFetch.onFetch(objects[0])
                } else {
                    onProfileFetch.onFetch(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(Constants.TAG, "" + error.message)
            }
        })
    }

    fun fetchAllMeter(path: String, userId: String, onDataFetch: onDataFetch) {
        val objects = ArrayList<Meter?>()
        databaseReference = FirebaseDatabase.getInstance().getReference(path).child(userId)
        Log.i(Constants.TAG, "" + databaseReference!!.parent)
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val meter = dataSnapshot.getValue(Meter::class.java)
                    meter?.fireBaseKey = dataSnapshot.key ?: ""
                    objects.add(meter)
                    Log.i(Constants.TAG + ":fetchMeter", "" + meter?.meterName)
                }
                onDataFetch.onFetch(objects)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(Constants.TAG, "" + error.message)
            }
        })
    }

    fun fetchAllRoom(path: String, userId: String, onRoomDataFetch: onRoomDataFetch) {
        val objects = ArrayList<Room?>()
        databaseReference = FirebaseDatabase.getInstance().getReference(path).child(userId)
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val room = dataSnapshot.getValue(Room::class.java)
                    room?.fireBaseKey = dataSnapshot.key ?: ""
                    Log.i(Constants.TAG + ":fetchRoom", "" + room?.roomName)
                    objects.add(room)
                }
                onRoomDataFetch.onFetch(objects)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(Constants.TAG, "" + error.message)
            }
        })
    }

    fun fetchAllRent(path: String, userId: String, onRentDataFetch: onRentDataFetch) {
        val objects = ArrayList<Rent?>()
        databaseReference = FirebaseDatabase.getInstance().getReference(path).child(userId)
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val rent = dataSnapshot.getValue(Rent::class.java)
                    rent?.fireBaseKey = dataSnapshot.key ?: ""
                    Log.i(Constants.TAG + ":fetchRent", "" + rent?.monthName)
                    objects.add(rent)
                }
                onRentDataFetch.onFetch(objects)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(Constants.TAG, "" + error.message)
            }
        })
    }

    fun fetchAllElectricityBills(
        path: String,
        userId: String,
        onElectricityBillDataFetch: onElectricityBillDataFetch
    ) {
        val objects = ArrayList<ElectricityBill?>()
        databaseReference = FirebaseDatabase.getInstance().getReference(path).child(userId)
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val electricityBill = dataSnapshot.getValue(
                        ElectricityBill::class.java
                    )
                    Log.i(Constants.TAG + ":fetchBill", "" + electricityBill?.meterName)
                    electricityBill?.fireBaseKey = dataSnapshot.key ?: ""
                    objects.add(electricityBill)
                }
                onElectricityBillDataFetch.onFetch(objects)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(Constants.TAG, "" + error.message)
            }
        })
    }

    fun fetchAllTenant(path: String, userId: String, onTenantDataFetch: onTenantDataFetch) {
        val objects = ArrayList<Tenant?>()
        databaseReference = FirebaseDatabase.getInstance().getReference(path).child(userId)
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val tenant = dataSnapshot.getValue(Tenant::class.java)
                    tenant?.fireBaseKey = dataSnapshot.key ?: ""
                    Log.i(Constants.TAG + ":fetchTenant", "" + tenant?.tenantName)
                    objects.add(tenant)
                }
                onTenantDataFetch.onFetch(objects)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(Constants.TAG, "" + error.message)
            }
        })
    }

    fun fetchAllNotification(
        path: String,
        onNotificationDataFetch: onNotificationDataFetch
    ) {
        val objects = ArrayList<Notification?>()
        databaseReference = FirebaseDatabase.getInstance().getReference(path)
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val notification = dataSnapshot.getValue(
                        Notification::class.java
                    )
                    Log.i(Constants.TAG + ":fetchAllNotification", "" + notification?.title)
                    objects.add(notification)
                }
                onNotificationDataFetch.onFetch(objects)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(Constants.TAG, "" + error.message)
            }
        })
    }

    fun fetchAllNote(path: String, userId: String, onNoteDataFetch: onNoteDataFetch) {
        val objects = ArrayList<Note?>()
        databaseReference = FirebaseDatabase.getInstance().getReference(path).child(userId)
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val note = dataSnapshot.getValue(
                        Note::class.java
                    )
                    note!!.fireBaseKey = dataSnapshot.key ?: ""
                    Log.i(Constants.TAG + ":fetchNote", "" + note.title)
                    objects.add(note)
                }
                onNoteDataFetch.onFetch(objects)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(Constants.TAG, "" + error.message)
            }
        })
    }

    fun getAllName(path: String, userId: String, fieldName: String, onNameFetch: onNameFetch) {
        val roomNameList = ArrayList<String?>()
        databaseReference = FirebaseDatabase.getInstance().getReference(path).child(userId)
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                roomNameList.add(context.getString(R.string.select_data))
                for (dataSnapshot in snapshot.children) {
                    Log.i(
                        Constants.TAG, "$fieldName:" + dataSnapshot.child(fieldName).getValue(
                            String::class.java
                        )
                    )
                    roomNameList.add(
                        dataSnapshot.child(fieldName).getValue(
                            String::class.java
                        )
                    )
                }
                onNameFetch.onFetched(roomNameList)
            }

            override fun onCancelled(error: DatabaseError) {
                roomNameList.add(context.getString(R.string.no_data_message))
                onNameFetch.onFetched(roomNameList)
                Log.i(Constants.TAG, "" + error.message)
            }
        })
    }

    fun getAdditionalInfo(
        path: String,
        userId: String,
        fieldName: String,
        onAdditionalInfoFetch: onAdditionalInfoFetch
    ) {
        var data = 0.0
        databaseReference = FirebaseDatabase.getInstance().getReference(path).child(userId)
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    Log.i(
                        Constants.TAG, "$fieldName:" + dataSnapshot.child(fieldName).value
                    )
                    data += (dataSnapshot.child(fieldName).getValue<Double>() ?: 0.0)
                }
                onAdditionalInfoFetch.onFetched(data)
            }

            override fun onCancelled(error: DatabaseError) {
                onAdditionalInfoFetch.onFetched(data)
                Log.i(Constants.TAG, "" + error.message)
            }
        })
    }

    fun getSingleColumnValue(
        path: String,
        userId: String,
        fieldName: String,
        onSingleDataFetch: onSingleDataFetch
    ) {
        val singleColumnRetrieval = arrayOf<Any?>(0)
        databaseReference = FirebaseDatabase.getInstance().getReference(path).child(userId)
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                singleColumnRetrieval[0] = snapshot.child(fieldName).value
                onSingleDataFetch.onFetched(singleColumnRetrieval[0])
            }

            override fun onCancelled(error: DatabaseError) {
                onSingleDataFetch.onFetched(singleColumnRetrieval[0])
                Log.i(Constants.TAG, "" + error.message)
            }
        })
    }

    fun getRentDataByMonth(
        path: String,
        userId: String,
        queryValue: String,
        fieldName: String,
        onDataQuery: onDataQuery
    ) {
        val rentDataSetMonthly = intArrayOf(0)
        databaseReference = FirebaseDatabase.getInstance().getReference(path).child(userId)
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val rent = dataSnapshot.getValue(Rent::class.java)
                    if (rent?.monthName == queryValue || rent?.monthName?.contains(queryValue) == true) {
                        Log.i(Constants.TAG, fieldName + ":" + rent.rentAmount)
                        rentDataSetMonthly[0] = rentDataSetMonthly[0] + rent.rentAmount
                    }
                }
                Log.i(Constants.TAG, "getDataByQuery: Monthly :" + rentDataSetMonthly[0])
                onDataQuery.onQueryFinished(rentDataSetMonthly[0])
            }

            override fun onCancelled(error: DatabaseError) {
                onDataQuery.onQueryFinished(rentDataSetMonthly[0])
                Log.i(Constants.TAG, "getDataByQuery: Monthly :" + error.message)
            }
        })
    }

    fun getRentDataByYear(
        path: String,
        userId: String,
        queryValue: String,
        fieldName: String,
        onDataQueryYearlyRent: onDataQueryYearlyRent
    ) {
        val rentDataSetYearly = intArrayOf(0)
        databaseReference = FirebaseDatabase.getInstance().getReference(path).child(userId)
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val rent = dataSnapshot.getValue(Rent::class.java)
                    if (rent?.yearName == queryValue || rent?.yearName?.contains(queryValue) == true) {
                        Log.i(Constants.TAG, fieldName + ":" + rent.rentAmount)
                        rentDataSetYearly[0] = rentDataSetYearly[0] + rent.rentAmount
                    }
                }
                Log.i(Constants.TAG, "getDataByQuery: Yearly :" + rentDataSetYearly[0])
                onDataQueryYearlyRent.onQueryFinished(rentDataSetYearly[0])
            }

            override fun onCancelled(error: DatabaseError) {
                onDataQueryYearlyRent.onQueryFinished(rentDataSetYearly[0])
                Log.i(Constants.TAG, "getDataByQuery: Yearly :" + error.message)
            }
        })
    }

    interface onProfileFetch {
        fun onFetch(user: User?)
    }

    interface onDataFetch {
        fun onFetch(objects: ArrayList<Meter?>?)
    }

    interface onRoomDataFetch {
        fun onFetch(objects: ArrayList<Room?>?)
    }

    interface onRentDataFetch {
        fun onFetch(objects: ArrayList<Rent?>?)
    }

    interface onTenantDataFetch {
        fun onFetch(objects: ArrayList<Tenant?>?)
    }

    interface onElectricityBillDataFetch {
        fun onFetch(objects: ArrayList<ElectricityBill?>?)
    }

    interface onNotificationDataFetch {
        fun onFetch(objects: ArrayList<Notification?>?)
    }

    interface onNoteDataFetch {
        fun onFetch(objects: ArrayList<Note?>?)
    }

    interface onNameFetch {
        fun onFetched(nameList: ArrayList<String?>?)
    }

    interface onAdditionalInfoFetch {
        fun onFetched(data: Double)
    }

    interface onSingleDataFetch {
        fun onFetched(data: Any?)
    }

    interface onDataQuery {
        fun onQueryFinished(data: Int)
    }

    interface onDataQueryYearlyRent {
        fun onQueryFinished(data: Int)
    }
}
