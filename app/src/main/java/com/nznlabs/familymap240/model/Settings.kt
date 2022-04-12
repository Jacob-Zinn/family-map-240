package com.nznlabs.familymap240.model

data class Settings(
    var showLifeStoryLines: Boolean = true,
    var showTreeLines: Boolean = true,
    var showSpouseLines: Boolean = true,
    var fatherSide: Boolean = true,
    var motherSide: Boolean = true,
    var maleEvents: Boolean = true,
    var femaleEvents: Boolean = true
)