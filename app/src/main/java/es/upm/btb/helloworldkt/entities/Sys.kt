package es.upm.btb.helloworldkt.entities

import com.google.gson.annotations.SerializedName


data class Sys (

  @SerializedName("country" ) var country : String? = null

)