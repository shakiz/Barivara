package com.shakil.barivara.model.bill

data class GenerateBill(
    var tenantName: String,
    var mobileNo: String,
    var monthAndYear: String,
    var associateRoom: String,
    var rentAmount: Int,
    var gasBill: Int,
    var waterBill: Int,
    var electricityBill: Int,
    var serviceCharge: Int
)
