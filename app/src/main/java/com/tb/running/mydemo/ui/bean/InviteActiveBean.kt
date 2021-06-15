package com.tb.running.mydemo.ui.bean


import com.google.gson.annotations.SerializedName

data class InviteActiveBean(
    @SerializedName("RSPCOD")
    var rSPCOD: String, // 000000
    @SerializedName("RSPDATA")
    var rSPDATA: RSPDATA,
    @SerializedName("RSPMSG")
    var rSPMSG: String // 查询成功
) {
    data class RSPDATA(
        @SerializedName("ACT_PROS")
        var aCTPROS: List<ACTPROS>,
        @SerializedName("BNUM")
        var bNUM: String = "4", // 6
    ) {
        data class ACTPROS(
            @SerializedName("INVITE_IMAGE")
            var iNVITEIMAGE: String = "", // https://
            @SerializedName("INVITE_NUMBER")
            var iNVITENUMBER: String = "", // 2
            var isOut: Boolean = false
        )
    }
}